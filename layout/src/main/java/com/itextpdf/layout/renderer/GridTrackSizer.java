/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.grid.AutoValue;
import com.itextpdf.layout.properties.grid.FitContentValue;
import com.itextpdf.layout.properties.grid.FlexValue;
import com.itextpdf.layout.properties.grid.GridValue;
import com.itextpdf.layout.properties.grid.LengthValue;
import com.itextpdf.layout.properties.grid.MinMaxValue;
import com.itextpdf.layout.properties.grid.PercentValue;
import com.itextpdf.layout.renderer.Grid.GridOrder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 12.3. Track Sizing Algorithm
/**
 * Class representing a track sizing algorithm.
 */
class GridTrackSizer {
    private final Grid grid;
    private final List<Track> tracks;
    private final float gap;
    private final float availableSpace;
    private final GridOrder order;
    private final Set<Integer> percentValueIndexes = new HashSet<>();

    /**
     * Create a track sizing algorithm for given template.
     *
     * @param grid grid to process
     * @param values template values
     * @param gap gap between tracks
     * @param availableSpace space to fit tracks on
     * @param order grid order
     */
    GridTrackSizer(Grid grid, List<GridValue> values, float gap, float availableSpace, GridOrder order) {
        this.grid = grid;
        this.availableSpace = availableSpace;
        this.gap = gap;
        tracks = new ArrayList<>(values.size());
        for (GridValue value : values) {
            final Track track = new Track();
            track.value = value;
            tracks.add(track);
        }
        if (availableSpace < 0) {
            for (int i = 0; i < tracks.size(); ++i) {
                final Track track = tracks.get(i);
                if (track.value.getType() == GridValue.ValueType.PERCENT) {
                    // 7.2.1. Track Sizes: If the size of the grid container depends on the
                    // size of its tracks, then the <percentage> must be treated as auto
                    percentValueIndexes.add(i);
                    track.value = AutoValue.VALUE;
                }
                if (track.value.getType() == GridValue.ValueType.FIT_CONTENT
                        && ((FitContentValue) track.value).getLength().getType() == GridValue.ValueType.PERCENT) {
                    // "7.2.1. Track Sizes: If the size of the grid container depends on the
                    // size of its tracks, then the <percentage> must be treated as auto"
                    // for fit content this means, that formula becomes max(auto-minimum, auto-maximum) = auto
                    track.value = AutoValue.VALUE;
                }
            }
        }
        this.order = order;
    }

    /**
     * Resolves template values and auto-values to point values.
     *
     * @return list of points, representing track sizes with expanded percentages in case of inline calculation.
     */
    TrackSizingResult sizeTracks() {
        // First step (12.4. Initialize Track Sizes)
        initializeTrackSizes();
        // Second step (12.5. Resolve Intrinsic Track Sizes)
        resolveIntrinsicTrackSizes();
        // Third step (12.6. Maximize Tracks)
        maximizeTracks();
        // Fourth step (12.7. Expand Flexible Tracks)
        expandFlexibleTracks();
        // Fifth step (12.8. Stretch auto Tracks)
        // Skip for now

        return new TrackSizingResult(tracks, gap, percentValueIndexes);
    }

    private void maximizeTracks() {
        float freeSpace = getFreeSpace();
        if (availableSpace > 0) {
            float leftSpace = (float) freeSpace;
            while (leftSpace > 0.0f) {
                int unfrozenTracks = 0;
                for (Track track : tracks) {
                    if (Float.compare(track.baseSize, track.growthLimit) < 0) {
                        unfrozenTracks++;
                    }
                }
                if (unfrozenTracks == 0) {
                    break;
                }
                float diff = leftSpace / unfrozenTracks;
                for (Track track : tracks) {
                    if (Float.compare(track.baseSize, track.growthLimit) < 0) {
                        float trackDiff = Math.min(track.growthLimit, track.baseSize + diff) - track.baseSize;
                        track.baseSize += trackDiff;
                        leftSpace -= trackDiff;
                    }
                }
            }
        } else {
            for (Track track : tracks) {
                if (Float.compare(track.baseSize, track.growthLimit) < 0) {
                    track.baseSize = track.growthLimit;
                }
            }
        }
    }


    private void expandFlexibleTracks() {
        boolean thereIsFlexibleTrack = false;
        for (Track track : tracks) {
            if (track.value.getType() == GridValue.ValueType.FLEX) {
                thereIsFlexibleTrack = true;
                break;
            }
        }
        if (!thereIsFlexibleTrack) {
            return;
        }

        float frSize = 0;

        if (availableSpace > 0.0f) {
            // If the free space is zero or if sizing the grid container under a min-content constraint:
            float freeSpace = (float) getFreeSpace();
            if (freeSpace < 0.0f) {
                return;
            }
            // Otherwise, if the free space is a definite length:
            frSize = findFrSize(tracks, getAvailableSpaceForSizing());
        } else {
            // Otherwise, if the free space is an indefinite length:
            for (Track track : tracks) {
                if (track.value.getType() == GridValue.ValueType.FLEX) {
                    frSize = Math.max(frSize, track.baseSize / ((FlexValue)track.value).getFlex());
                }
            }
            for (GridCell cell : grid.getUniqueGridCells(order)) {
                boolean atLeastOneFlexTrack = false;
                List<Track> affectedTracks = getAffectedTracks(cell);
                for (Track track : affectedTracks) {
                    if (track.value.getType() == GridValue.ValueType.FLEX) {
                        atLeastOneFlexTrack = true;
                        break;
                    }
                }
                if (!atLeastOneFlexTrack) {
                    continue;
                }
                float maxContribution = calculateMinMaxContribution(cell, false);
                frSize = Math.max(frSize, findFrSize(affectedTracks, maxContribution));
            }
        }
        for (Track track : tracks) {
            if (track.value.getType() == GridValue.ValueType.FLEX) {
                float newBaseSize = frSize * ((FlexValue)track.value).getFlex();
                if (newBaseSize > track.baseSize) {
                    track.baseSize = newBaseSize;
                }
            }
        }
    }

    private List<Track> getAffectedTracks(GridCell cell) {
        List<Track> affectedTracks = new ArrayList<>();
        for (int i = cell.getStart(order); i < cell.getEnd(order); i++) {
            affectedTracks.add(tracks.get(i));
        }
        return affectedTracks;
    }

    private float getAvailableSpaceForSizing() {
        // Grid sizing algorithm says "Gutters are treated as empty fixed-size tracks for the purpose of the algorithm."
        // But relative gaps haven't supported yet, it is why to make algorithm simpler, available space just reduced by gaps.
        return availableSpace - ((tracks.size() - 1) * gap);
    }

    private float getFreeSpace() {
        float freeSpace = getAvailableSpaceForSizing();
        for (Track track : tracks) {
            freeSpace -= track.baseSize;
        }
        return freeSpace;
    }

    private static float findFrSize(List<Track> affectedTracks, float spaceToFill) {
        // 12.7.1. Find the Size of an 'fr'
        float frSize = 0;
        boolean allFlexTracksSatisfied = false;
        boolean[] ignoreTracks = new boolean[affectedTracks.size()];
        while (!allFlexTracksSatisfied) {
            float leftoverSpace = spaceToFill;
            float flexFactorSum = 0;
            for (int i = 0; i < affectedTracks.size(); i++) {
                Track track = affectedTracks.get(i);
                if (track.value.getType() == GridValue.ValueType.FLEX && !ignoreTracks[i]) {
                    flexFactorSum += ((FlexValue)track.value).getFlex();
                } else {
                    leftoverSpace -= track.baseSize;
                }
            }
            flexFactorSum = flexFactorSum < 1 ? 1 : flexFactorSum;
            float hyphFrSize = leftoverSpace / flexFactorSum;

            allFlexTracksSatisfied = true;
            for (int i = 0; i < affectedTracks.size(); i++) {
                Track track = affectedTracks.get(i);
                if (track.value.getType() == GridValue.ValueType.FLEX
                        && !ignoreTracks[i]
                        && hyphFrSize * ((FlexValue)track.value).getFlex() < track.baseSize) {
                    ignoreTracks[i] = true;
                    allFlexTracksSatisfied = false;
                }
            }
            if (allFlexTracksSatisfied) {
                frSize = hyphFrSize;
            }
        }
        return frSize;
    }

    private void resolveIntrinsicTrackSizes() {
        // 1. Shim baseline-aligned items so their intrinsic size contributions reflect their baseline alignment.
        // Not sure whether we need to do anything in first point

        // 2. Size tracks to fit non-spanning items.
        for (int i = 0; i < tracks.size(); i++) {
            Track track = tracks.get(i);
            GridValue minTrackSizingValue = track.value;
            GridValue maxTrackSizingValue = track.value;
            if (track.value.getType() == GridValue.ValueType.MINMAX) {
                minTrackSizingValue = ((MinMaxValue) track.value).getMin();
                maxTrackSizingValue = ((MinMaxValue) track.value).getMax();
            }

            Collection<GridCell> cells = grid.getUniqueCellsInTrack(order, i);
            // -> For max-content minimums:
            if (minTrackSizingValue.getType() == GridValue.ValueType.MAX_CONTENT) {
                float maxContribution = 0;
                for (GridCell cell : cells) {
                    // non-spanning items only
                    if (cell.getGridSpan(order) == 1) {
                        float contribution = calculateMinMaxContribution(cell, false);
                        maxContribution = Math.max(maxContribution, contribution);
                    }
                }
                track.baseSize = maxContribution;
            }

            // -> For min-content minimums:
            // -> For auto minimums: (also the case if track specified by fr value)
            if (minTrackSizingValue.getType() == GridValue.ValueType.AUTO
                    || minTrackSizingValue.getType() == GridValue.ValueType.FLEX
                    || minTrackSizingValue.getType() == GridValue.ValueType.MIN_CONTENT
                    || minTrackSizingValue.getType() == GridValue.ValueType.FIT_CONTENT) {
                float maxContribution = 0;
                for (GridCell cell : cells) {
                    // non-spanning items only
                    if (cell.getGridSpan(order) == 1) {
                        float contribution = calculateMinMaxContribution(cell, true);
                        maxContribution = Math.max(maxContribution, contribution);
                    }
                }
                track.baseSize = maxContribution;
            }
            // -> For min-content maximums:
            if (maxTrackSizingValue.getType() == GridValue.ValueType.MIN_CONTENT && track.baseSize > 0.0f) {
                track.growthLimit = track.baseSize;
            }
            // -> For max-content maximums:
            // Treat auto as max-content for max track sizing function
            if (maxTrackSizingValue.getType() == GridValue.ValueType.AUTO
                    || maxTrackSizingValue.getType() == GridValue.ValueType.MAX_CONTENT
                    || maxTrackSizingValue.getType() == GridValue.ValueType.FIT_CONTENT) {
                float maxContribution = 0;
                for (GridCell cell : cells) {
                    // non-spanning items only
                    if (cell.getGridSpan(order) == 1) {
                        float contribution = calculateMinMaxContribution(cell, false);
                        maxContribution = Math.max(maxContribution, contribution);
                    }
                }
                if (maxContribution > 0.0f) {
                    track.growthLimit = maxContribution;
                    if (maxTrackSizingValue.getType() == GridValue.ValueType.FIT_CONTENT) {
                        //For fit-content() maximums, furthermore clamp this growth limit by the fit-content() argument.
                        float maxSize = ((FitContentValue) maxTrackSizingValue).getMaxSizeForSpace(availableSpace);
                        track.growthLimit = Math.min(track.growthLimit, maxSize);
                    }
                }
            }

            // if a track’s growth limit is now less than its base size
            if (track.growthLimit > 0.0f && track.baseSize > 0.0f && track.baseSize > track.growthLimit) {
                track.growthLimit = track.baseSize;
            }
        }

        // 3. Increase sizes to accommodate spanning items crossing content-sized tracks.
        int maxSpanCell = 0;
        for (GridCell cell : grid.getUniqueGridCells(order)) {
            maxSpanCell = Math.max(maxSpanCell, cell.getGridSpan(order));
        }
        for (int span = 2; span <= maxSpanCell; span++) {
            for (GridCell cell : grid.getUniqueGridCells(order)) {
                if (cell.getGridSpan(order) == span) {
                    boolean flexTracksExist = false;
                    List<Track> affectedTracks = getAffectedTracks(cell);
                    for (Track track : affectedTracks) {
                        if (track.value.getType() == GridValue.ValueType.FLEX) {
                            flexTracksExist = true;
                        }
                    }
                    if (flexTracksExist) {
                        continue;
                    }
                    float contribution = calculateMinMaxContribution(cell, true);
                    // 3.1 For intrinsic minimums:
                    // 3.2 For content-based minimums:
                    // 3.3 For max-content minimums:
                    distributeExtraSpace(affectedTracks, true, contribution);
                }
            }
            // 3.4 If at this point any track’s growth limit is now less than its base size:
            // 3.5 For intrinsic maximums:
            // 3.6 For max-content maximums:
        }

        // 4. Increase sizes to accommodate spanning items crossing flexible tracks:
        for (GridCell cell : grid.getUniqueGridCells(order)) {
            List<Track> affectedTracks = new ArrayList<>();
            for (int i = cell.getStart(order); i < cell.getEnd(order); i++) {
                if (tracks.get(i).value.getType() == GridValue.ValueType.FLEX) {
                    affectedTracks.add(tracks.get(i));
                }
            }
            if (affectedTracks.isEmpty()) {
                continue;
            }
            float contribution = calculateMinMaxContribution(cell, true);
            distributeExtraSpaceWithFlexTracks(affectedTracks, contribution);
        }

        // 5. If any track still has an infinite growth limit
        for (Track track : tracks) {
            if (track.growthLimit < 0) {
                track.growthLimit = track.baseSize;
            }
        }
    }

    private void distributeExtraSpaceWithFlexTracks(List<Track> tracks, float sizeContribution) {
        // 1. Find the space to distribute:
        float trackSizes = 0.0f;
        float sumFraction = 0.0f;
        for (Track track : tracks) {
            trackSizes += track.baseSize;
            sumFraction += ((FlexValue)track.value).getFlex();
        }
        if (sumFraction < 1.0f) {
            sumFraction = 1.0f;
        }
        float space = Math.max(0.0f, sizeContribution - trackSizes);
        float spacePerFraction = space / sumFraction;

        // 2. Distribute space up to limits:
        // For flex values we know that they're can't be frozen so we can distribute all available space at once
        for (Track track : tracks) {
            distributeSpaceToTrack(track, spacePerFraction * ((FlexValue)track.value).getFlex());
        }
        // 3. Distribute space to non-affected tracks: skipped
        // 4. Distribute space beyond limits: skipped
    }

    private void distributeExtraSpace(List<Track> tracks, boolean affectsBase, float sizeContribution) {
        // 1. Find the space to distribute:
        float trackSizes = 0;
        int numberOfAffectedTracks = 0;
        for (Track track : tracks) {
            GridValue value = track.value;
            if (track.value.getType() == GridValue.ValueType.MINMAX) {
                value = affectsBase ? ((MinMaxValue)track.value).getMin()
                                    : ((MinMaxValue)track.value).getMax();
            }
            trackSizes += affectsBase ? track.baseSize : track.growthLimit;
            if (value.getType() != GridValue.ValueType.POINT && value.getType() != GridValue.ValueType.PERCENT) {
                numberOfAffectedTracks++;
            }
        }
        float space = Math.max(0, sizeContribution - trackSizes);
        // 2. Distribute space up to limits:
        while (space > 0.0f) {
            float distributedSpace = space / numberOfAffectedTracks;
            boolean allFrozen = true;
            for (Track track : tracks) {
                GridValue value = track.value;
                if (track.value.getType() == GridValue.ValueType.MINMAX) {
                    value = affectsBase ? ((MinMaxValue)track.value).getMin()
                            : ((MinMaxValue)track.value).getMax();
                }
                if (value.getType() != GridValue.ValueType.POINT
                        && value.getType() != GridValue.ValueType.PERCENT) {
                    float added = distributeSpaceToTrack(track, distributedSpace);
                    if (added > 0) {
                        space -= (float) added;
                        allFrozen = false;
                    }
                }
            }
            if (allFrozen) {
                break;
            }
        }
        // 3. Distribute space to non-affected tracks: skipped
        // 4. Distribute space beyond limits: skipped
    }

    private void  initializeTrackSizes() {
        for (Track track : tracks) {
            GridValue minTrackSizingValue = track.value;
            GridValue maxTrackSizingValue = track.value;
            if (track.value.getType() == GridValue.ValueType.MINMAX) {
                minTrackSizingValue = ((MinMaxValue) track.value).getMin();
                maxTrackSizingValue = ((MinMaxValue) track.value).getMax();
            }

            // A fixed sizing function
            if (minTrackSizingValue.getType() == GridValue.ValueType.POINT
                    || minTrackSizingValue.getType() == GridValue.ValueType.PERCENT) {
                if (minTrackSizingValue.getType() == GridValue.ValueType.POINT ) {
                    track.baseSize = ((LengthValue)minTrackSizingValue).getValue();
                } else {
                    track.baseSize = ((LengthValue)minTrackSizingValue).getValue() / 100 * availableSpace;
                }
            } else {
                track.baseSize = 0;
            }

            // A fixed sizing function
            if (maxTrackSizingValue.getType() == GridValue.ValueType.POINT
                    || maxTrackSizingValue.getType() == GridValue.ValueType.PERCENT) {
                if (maxTrackSizingValue.getType() == GridValue.ValueType.POINT) {
                    track.growthLimit = ((LengthValue)maxTrackSizingValue).getValue();
                } else {
                    track.growthLimit = ((LengthValue)maxTrackSizingValue).getValue() / 100 * availableSpace;
                }
            } else {
                track.growthLimit = -1;
            }
        }
    }

    /**
     * Distributes given space to track, if given space can't be fully distributed returns
     * as many space as was distributed.
     *
     * @param track track to which distribute space
     * @param distributedSpace how much space to distribute
     * @return how much space was distributed
     */
    private static float distributeSpaceToTrack(Track track, float distributedSpace) {
        if (track.growthLimit < 0 || distributedSpace + track.baseSize <= track.growthLimit) {
            track.baseSize += distributedSpace;
            return distributedSpace;
        } else if (Float.compare(track.growthLimit, track.baseSize) != 0) {
            float addedToLimit = track.growthLimit - track.baseSize;
            track.baseSize += addedToLimit;
            return addedToLimit;
        }
        return -1.0f;
    }

    /**
     * Calculate min or max contribution of a cell.
     *
     * @param cell cell to calculate contribution
     * @param minTypeContribution type of contribution: min if true, max otherwise
     * @return contribution value
     */
    private float calculateMinMaxContribution(GridCell cell, boolean minTypeContribution) {
        if (GridOrder.COLUMN == order) {
            if (cell.getValue() instanceof AbstractRenderer) {
                AbstractRenderer abstractRenderer = (AbstractRenderer) cell.getValue();
                return minTypeContribution ? abstractRenderer.getMinMaxWidth().getMinWidth()
                                           : abstractRenderer.getMinMaxWidth().getMaxWidth();
            }
        } else {
            // https://drafts.csswg.org/css-sizing-3/#auto-box-sizes:
            // min-content block size - For block containers, tables, and
            // inline boxes, this is equivalent to the max-content block size.
            cell.getValue().setProperty(Property.FILL_AVAILABLE_AREA, Boolean.FALSE);
            final LayoutContext layoutContext = new LayoutContext(
                    new LayoutArea(1, new Rectangle(cell.getLayoutArea().getWidth(), AbstractRenderer.INF)));
            LayoutResult inifiniteHeighLayoutResult = cell.getValue().layout(layoutContext);
            if (inifiniteHeighLayoutResult.getStatus() == LayoutResult.NOTHING
                    || inifiniteHeighLayoutResult.getStatus() == LayoutResult.PARTIAL) {
                return 0;
            }
            return inifiniteHeighLayoutResult.getOccupiedArea().getBBox().getHeight();
        }
        return 0;
    }

    static class TrackSizingResult {
        private final List<Track> tracks;
        private final Set<Integer> percentValueIndexes;
        private final float gap;

        TrackSizingResult(List<Track> tracks, float gap, Set<Integer> percentValueIndexes) {
            this.tracks = tracks;
            this.percentValueIndexes = percentValueIndexes;
            this.gap = gap;
        }

        /**
         * Get original track sizes which are were resolved during track sizing algorithm.
         * If result contains inline percentages those are not expanded/reduced and have a size equivalent
         * of AUTO.
         *
         * @return original track sizes list
         */
        List<Float> getTrackSizes() {
            List<Float> result = new ArrayList<>(tracks.size());
            for (Track track : tracks) {
                result.add(track.baseSize);
            }
            return result;
        }

        /**
         * Get expanded track sizes where inline percents are resolved against calculated grid area.
         *
         * @param template grid value template
         * @return expanded track sizes list
         */
        List<Float> getTrackSizesAndExpandPercents(List<GridValue> template) {
            if (percentValueIndexes.isEmpty()) {
                return getTrackSizes();
            }
            // Resolve inline percentage values (7.2.1. Track Sizes)
            float total = 0.0f;
            for (Track track : tracks) {
                total += track.baseSize;
            }
            total += ((tracks.size() - 1) * gap);

            List<Float> expandedTrackSizes = new ArrayList<>(tracks.size());
            for (int i = 0; i < tracks.size(); ++i) {
                if (percentValueIndexes.contains(i)) {
                    expandedTrackSizes.add(((PercentValue)template.get(i)).getValue() / 100 * total);
                } else {
                    expandedTrackSizes.add(tracks.get(i).baseSize);
                }
            }
            return expandedTrackSizes;
        }
    }

    static class Track {
        float baseSize;
        // consider -1 as an infinity value
        float growthLimit;
        GridValue value;
    }
}
