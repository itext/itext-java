/*
    Copyright 2015 The Chromium Authors

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are
    met:

    * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
    copyright notice, this list of conditions and the following disclaimer
    in the documentation and/or other materials provided with the
    distribution.
    * Neither the name of Google LLC nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
    "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
    LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
    A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
    OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
    SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
    LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.grid.AutoValue;
import com.itextpdf.layout.properties.grid.BreadthValue;
import com.itextpdf.layout.properties.grid.FitContentValue;
import com.itextpdf.layout.properties.grid.FlexValue;
import com.itextpdf.layout.properties.grid.GridValue;
import com.itextpdf.layout.properties.grid.LengthValue;
import com.itextpdf.layout.properties.grid.MinMaxValue;
import com.itextpdf.layout.properties.grid.PercentValue;
import com.itextpdf.layout.properties.grid.TemplateValue.ValueType;
import com.itextpdf.layout.renderer.Grid.GridOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 12.3. Track Sizing Algorithm https://drafts.csswg.org/css-grid-2/#algo-track-sizing
// More than half of the code in that class was ported from chromium code on C++
// See https://source.chromium.org/chromium/chromium/src/+/main:third_party/blink/renderer/core/layout/grid/grid_layout_algorithm.cc;l=1858
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
    // iText's method
    GridTrackSizer(Grid grid, List<GridValue> values, float gap, float availableSpace, GridOrder order) {
        this.grid = grid;
        this.availableSpace = availableSpace;
        this.gap = gap;
        tracks = new ArrayList<>(values.size());
        for (GridValue value : values) {
            final Track track = new Track();
            track.value = value;
            tracks.add(track);
            if (track.value.getType() == ValueType.FLEX) {
                track.value = new MinMaxValue(AutoValue.VALUE, (FlexValue) track.value);
            }
        }
        if (Float.compare(availableSpace, -1f) == 0) {
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
    // iText's method
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
        stretchAutoTracks();

        return new TrackSizingResult(tracks, gap, percentValueIndexes);
    }

    // chromium's method
    private void stretchAutoTracks() {
        // iText's comment: for now consider that we always have content-distribution property equals to `normal`

        // Expand tracks that have an 'auto' max track sizing function by dividing any
        // remaining positive, definite free space equally amongst them.
        List<Track> tracksToGrow = new ArrayList<>();
        for (Track track : tracks) {
            if (track.hasAutoMax()) {
                tracksToGrow.add(track);
            }
        }

        if (tracksToGrow.isEmpty()) {
            return;
        }

        float freeSpace = determineFreeSpace();
        // iText's comment: the case when grid container has min-width\height is processed in the GridContainerRenderer
        if (Float.compare(freeSpace, -1f) == 0) {
            return;
        }

        distributeExtraSpaceToTracks(freeSpace, 0, GridItemContributionType.FOR_FREE_SPACE, tracksToGrow, tracksToGrow, true);
        for (Track track : tracksToGrow) {
            track.baseSize += track.incurredIncrease;
            track.ensureGrowthLimitIsNotLessThanBaseSize();
        }
    }

    // chromium's method
    private void maximizeTracks() {
        float freeSpace = determineFreeSpace();
        if (Float.compare(freeSpace, 0f) == 0) {
            return;
        }
        List<Track> tracksToGrow = new ArrayList<>(tracks);
        distributeExtraSpaceToTracks(freeSpace, 0f, GridItemContributionType.FOR_FREE_SPACE, tracksToGrow, null, true);
        for (Track track : tracksToGrow) {
            track.baseSize += track.incurredIncrease;
            track.ensureGrowthLimitIsNotLessThanBaseSize();
        }
    }

    // chromium's method
    private void expandFlexibleTracks() {
        boolean thereIsFlexibleTrack = false;
        for (Track track : tracks) {
            if (track.isFlexibleTrack()) {
                thereIsFlexibleTrack = true;
                break;
            }
        }
        // iText's comment: this check is performed in GridTrackSizer.sizeTracks method in chromium
        if (!thereIsFlexibleTrack) {
            return;
        }

        float freeSpace = determineFreeSpace();
        // If the free space is zero or if sizing the grid container under a
        // min-content constraint, the used flex fraction is zero.
        if (Float.compare(freeSpace, 0f) == 0) {
            return;
        }
        float frSize = 0;
        if (Float.compare(freeSpace, -1f) != 0) {
            // Otherwise, if the free space is a definite length, the used flex fraction
            // is the result of finding the size of an fr using all of the grid tracks
            // and a space to fill of the available grid space.
            frSize = findFrSize(tracks, availableSpace);
        } else {
            // Otherwise, if the free space is an indefinite length, the used flex
            // fraction is the maximum of:
            //   - For each grid item that crosses a flexible track, the result of
            //   finding the size of an fr using all the grid tracks that the item
            //   crosses and a space to fill of the item's max-content contribution.
            for (GridCell cell : grid.getUniqueGridCells(order)) {
                List<Track> flexSpannedTracks = new ArrayList<>();
                List<Track> spannedTracks = getSpannedTracks(cell);
                for (Track track : spannedTracks) {
                    if (track.isFlexibleTrack()) {
                        flexSpannedTracks.add(track);
                    }
                }
                // iText's comment: grid_item.IsConsideredForSizing(track_direction) check was skipped, because it isn't clear how it works
                if (flexSpannedTracks.isEmpty()) {
                    continue;
                }
                float gridItemFrSize = findFrSize(spannedTracks, calculateMinMaxContribution(cell, false));
                frSize = Math.max(gridItemFrSize, frSize);
            }

            //   - For each flexible track, if the flexible track's flex factor is
            //   greater than one, the result of dividing the track's base size by its
            //   flex factor; otherwise, the track's base size.
            for (Track track : tracks) {
                if (!track.isFlexibleTrack()) {
                    continue;
                }
                float trackFlexFactor = Math.max(track.getFlexFactor(), 1);
                frSize = Math.max(track.baseSize / trackFlexFactor, frSize);
            }
        }

        // iText's comment: logic with leftover_size and expanded_size skipped because it isn't needed for java
        for (Track track : tracks) {
            if (!track.isFlexibleTrack()) {
                continue;
            }
            float frShare = frSize * track.getFlexFactor();
            if (frShare >= track.baseSize) {
                track.baseSize = frShare;
                track.ensureGrowthLimitIsNotLessThanBaseSize();
            }
        }
    }

    // iText's method
    private List<Track> getSpannedTracks(GridCell cell) {
        List<Track> affectedTracks = new ArrayList<>();
        for (int i = cell.getStart(order); i < cell.getEnd(order); i++) {
            affectedTracks.add(tracks.get(i));
        }
        return affectedTracks;
    }

    // chromium's method
    private float determineFreeSpace() {
        // iText's comment: method was simplified, because we don't support different sizing constraint
        float freeSpace = availableSpace;
        if (Float.compare(freeSpace, -1f) != 0) {
            for (Track track : tracks) {
                freeSpace -= track.baseSize;
            }
            freeSpace -= (tracks.size() - 1) * gap;
            // If tracks consume more space than the grid container has available,
            // clamp the free space to zero as there's no more room left to grow.
            return Math.max(freeSpace, 0);
        }
        return -1;
    }

    // iText's method
    private float findFrSize(List<Track> affectedTracks, float leftoverSpace) {
        // iText's comment: initially was implemented method from chromium but it worked worse in some cases than our implementation
        // 12.7.1. Find the Size of an 'fr'
        float frSize = 0;
        boolean allFlexTracksSatisfied = false;

        boolean[] ignoreTracks = new boolean[affectedTracks.size()];
        while (!allFlexTracksSatisfied) {
            float currentLeftoverSpace = leftoverSpace;
            int totalTrackCount = 0;
            float flexFactorSum = 0;
            for (int i = 0; i < affectedTracks.size(); i++) {
                Track track = affectedTracks.get(i);
                totalTrackCount++;
                if (track.isFlexibleTrack() && !ignoreTracks[i]) {
                    flexFactorSum += track.getFlexFactor();
                } else {
                    currentLeftoverSpace -= track.baseSize;
                }
            }
            currentLeftoverSpace -= (totalTrackCount - 1) * gap;
            flexFactorSum = flexFactorSum < 1 ? 1 : flexFactorSum;
            float hyphFrSize = currentLeftoverSpace / flexFactorSum;

            allFlexTracksSatisfied = true;
            for (int i = 0; i < affectedTracks.size(); i++) {
                Track track = affectedTracks.get(i);
                if (track.isFlexibleTrack()
                        && !ignoreTracks[i]
                        && hyphFrSize * track.getFlexFactor() < track.baseSize) {
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

    // chromium's method
    private void resolveIntrinsicTrackSizes() {
        // iText's comment: part for subgrid is skipped
        // itext's comment: reordering grid items is skipped, grid items groups are created through `for` cycle

        // iText's comment: 2 - Size tracks to fit non-spanning items
        // iText's comment: 3 - Increase sizes to accommodate spanning items crossing content-sized tracks
        int maxSpanCell = 0;
        for (GridCell cell : grid.getUniqueGridCells(order)) {
            maxSpanCell = Math.max(maxSpanCell, cell.getGridSpan(order));
        }
        // First, process the items that don't span a flexible track.
        for (int span = 1; span <= maxSpanCell; span++) {
            List<GridCell> group = new ArrayList<>();
            for (GridCell cell : grid.getUniqueGridCells(order)) {
                // Each iteration considers all items with the same span size.
                if (cell.getGridSpan(order) == span) {
                    boolean flexTracksExist = false;
                    List<Track> spannedTracks = getSpannedTracks(cell);
                    for (Track track : spannedTracks) {
                        if (track.isFlexibleTrack()) {
                            flexTracksExist = true;
                            break;
                        }
                    }
                    if (flexTracksExist) {
                        continue;
                    }
                    group.add(cell);
                }
            }

            increaseTrackSizesToAccommodateGridItems(group, false, GridItemContributionType.FOR_INTRINSIC_MINIMUMS);
            increaseTrackSizesToAccommodateGridItems(group, false, GridItemContributionType.FOR_CONTENT_BASED_MINIMUMS);
            increaseTrackSizesToAccommodateGridItems(group, false, GridItemContributionType.FOR_MAX_CONTENT_MINIMUMS);
            increaseTrackSizesToAccommodateGridItems(group, false, GridItemContributionType.FOR_INTRINSIC_MAXIMUMS);
            increaseTrackSizesToAccommodateGridItems(group, false, GridItemContributionType.FOR_MAX_CONTENT_MAXIMUMS);
        }

        // iText's comment: 4 - Increase sizes to accommodate spanning items crossing flexible tracks
        // Now, process items spanning flexible tracks (if any).
        List<GridCell> group = new ArrayList<>();
        for (GridCell cell : grid.getUniqueGridCells(order)) {
            List<Track> flexSpannedTracks = new ArrayList<>();
            List<Track> spannedTracks = getSpannedTracks(cell);
            for (Track track : spannedTracks) {
                if (track.isFlexibleTrack()) {
                    flexSpannedTracks.add(track);
                }
            }
            if (flexSpannedTracks.isEmpty()) {
                continue;
            }

            group.add(cell);
        }
        if (!group.isEmpty()) {
            // We can safely skip contributions for maximums since a <flex> definition
            // does not have an intrinsic max track sizing function.
            increaseTrackSizesToAccommodateGridItems(group, true, GridItemContributionType.FOR_INTRINSIC_MINIMUMS);
            increaseTrackSizesToAccommodateGridItems(group, true, GridItemContributionType.FOR_CONTENT_BASED_MINIMUMS);
            increaseTrackSizesToAccommodateGridItems(group, true, GridItemContributionType.FOR_MAX_CONTENT_MINIMUMS);
        }

        // iText's comment: 5 - If any track still has an infinite growth limit
        // iText's comment: in chromium this part in GridTrackSizer.sizeTracks method
        for (Track track : tracks) {
            if (Float.compare(track.growthLimit, -1f) == 0) {
                track.growthLimit = track.baseSize;
            }
        }
    }

    // chromium's method
    private void increaseTrackSizesToAccommodateGridItems(List<GridCell> group, boolean isGroupSpanningFlexTrack,
            GridItemContributionType contributionType) {

        for (Track track : tracks) {
            track.plannedIncrease = -1;
        }

        for (GridCell cell : group) {
            List<Track> tracksToGrow = new ArrayList<>();
            List<Track> tracksToGrowBeyondLimit = new ArrayList<>();
            float flexFactorSum = 0;
            float spannedTrackSize = gap * (cell.getGridSpan(order) - 1);

            for (Track track : getSpannedTracks(cell)) {
                spannedTrackSize += affectedSizeForContribution(track, contributionType);

                if (isGroupSpanningFlexTrack && !track.isFlexibleTrack()) {
                    // From https://drafts.csswg.org/css-grid-2/#algo-spanning-flex-items:
                    //   Distributing space only to flexible tracks (i.e. treating all other
                    //   tracks as having a fixed sizing function).
                    continue;
                }

                if (isContributionAppliedToTrack(track, contributionType)) {
                     if (Float.compare(track.plannedIncrease, -1f) == 0) {
                         track.plannedIncrease = 0;
                     }
                     if (isGroupSpanningFlexTrack) {
                         flexFactorSum += track.getFlexFactor();
                     }

                    tracksToGrow.add(track);
                    if (shouldUsedSizeGrowBeyondLimit(track, contributionType)) {
                        tracksToGrowBeyondLimit.add(track);
                    }
                }
            }

            if (tracksToGrow.isEmpty()) {
                continue;
            }


            // iText's comment: extraSpace calculation was simplified in comparison how it works in chromium,
            // iText's comment: it is possible place for difference with browser
            final boolean minTypeContribution = contributionType == GridItemContributionType.FOR_INTRINSIC_MINIMUMS
                    || contributionType == GridItemContributionType.FOR_CONTENT_BASED_MINIMUMS
                    || contributionType == GridItemContributionType.FOR_INTRINSIC_MAXIMUMS;
            // Subtract the corresponding size (base size or growth limit) of every
            // spanned track from the grid item's size contribution to find the item's
            // remaining size contribution. For infinite growth limits, substitute with
            // the track's base size. This is the space to distribute, floor it at zero.
            float extraSpace = calculateMinMaxContribution(cell, minTypeContribution);
            extraSpace = Math.max(extraSpace - spannedTrackSize, 0);
            if (Float.compare(extraSpace, 0) == 0) {
                continue;
            }

            // From https://drafts.csswg.org/css-grid-2/#algo-spanning-flex-items:
            //   If the sum of the flexible sizing functions of all flexible tracks
            //   spanned by the item is greater than zero, distributing space to such
            //   tracks according to the ratios of their flexible sizing functions
            //   rather than distributing space equally.
            if (!isGroupSpanningFlexTrack || Float.compare(flexFactorSum, 0f) == 0) {
                distributeExtraSpaceToTracks(extraSpace, 0, contributionType, tracksToGrow,
                        tracksToGrowBeyondLimit.isEmpty() ? tracksToGrow : tracksToGrowBeyondLimit, true);
            } else {
                // 'fr' units are only allowed as a maximum in track definitions, meaning
                // that no track has an intrinsic max track sizing function that would allow
                // it to grow beyond limits (see |ShouldUsedSizeGrowBeyondLimit|).
                distributeExtraSpaceToTracks(extraSpace, flexFactorSum, contributionType, tracksToGrow, null, false);
            }

            // For each affected track, if the track's item-incurred increase is larger
            // than its planned increase, set the planned increase to that value.
            for (Track track : tracksToGrow) {
                track.plannedIncrease = Math.max(track.incurredIncrease, track.plannedIncrease);
            }
        }

        for (Track track : tracks) {
            growAffectedSizeByPlannedIncrease(track, contributionType);
        }
    }

    // chromium's method
    private static void growAffectedSizeByPlannedIncrease(Track track, GridItemContributionType contributionType) {
        track.isInfinityGrowable = false;
        float plannedIncrease = track.plannedIncrease;

        // Only grow sets that accommodated a grid item.
        if (Float.compare(plannedIncrease, -1f) == 0) {
            return;
        }

        switch (contributionType) {
            case FOR_INTRINSIC_MINIMUMS:
            case FOR_CONTENT_BASED_MINIMUMS:
            case FOR_MAX_CONTENT_MINIMUMS:
                track.baseSize += plannedIncrease;
                track.ensureGrowthLimitIsNotLessThanBaseSize();
                return;
            case FOR_INTRINSIC_MAXIMUMS:
                // Mark any tracks whose growth limit changed from infinite to finite in
                // this step as infinitely growable for the next step.
                track.isInfinityGrowable = Float.compare(track.growthLimit, -1f) == 0;
                track.growthLimit = track.definiteGrowthLimit() + plannedIncrease;
                return;
            case FOR_MAX_CONTENT_MAXIMUMS:
                track.growthLimit = track.definiteGrowthLimit() + plannedIncrease;
                return;
            // FOR_FREE_SPACE not reachable here
        }
    }

    // Follow the definitions from https://drafts.csswg.org/css-grid-2/#extra-space;
    // notice that this method replaces the notion of "tracks" with "sets".

    // chromium's method
    private void distributeExtraSpaceToTracks(float extraSpace, float flexFactorSum,
            GridItemContributionType contributionType, List<Track> tracksToGrow, List<Track> tracksToGrowBeyondLimits,
            boolean isEqualDistribution) {

        if (Float.compare(extraSpace, -1f) == 0) {
            // Infinite extra space should only happen when distributing free space at
            // the maximize tracks step; in such case, we can simplify this method by
            // "filling" every track base size up to their growth limit.
            for (Track track : tracksToGrow) {
                track.incurredIncrease = growthPotentialForSet(track, contributionType, false);
            }
            return;
        }

        int growableTrackCount = 0;
        for (Track track : tracksToGrow) {
            track.incurredIncrease = 0;

            // From the first note in https://drafts.csswg.org/css-grid-2/#extra-space:
            //   If the affected size was a growth limit and the track is not marked
            //   "infinitely growable", then each item-incurred increase will be zero.
            //
            // When distributing space to growth limits, we need to increase each track
            // up to its 'fit-content' limit. However, because of the note above, first
            // we should only grow tracks marked as "infinitely growable" up to limits
            // and then grow all affected tracks beyond limits.
            //
            // We can correctly resolve every scenario by doing a single sort of
            // |sets_to_grow|, purposely ignoring the "infinitely growable" flag, then
            // filtering out sets that won't take a share of the extra space at each
            // step; for base sizes this is not required, but if there are no tracks
            // with growth potential > 0, we can optimize by not sorting the sets.
            if (Float.compare(growthPotentialForSet(track, contributionType, false), 0) != 0) {
                growableTrackCount++;
            }
        }

        float shareRatioSum = isEqualDistribution ? growableTrackCount : flexFactorSum;

        // We will sort the tracks by growth potential in non-decreasing order to
        // distribute space up to limits; notice that if we start distributing space
        // equally among all tracks we will eventually reach the limit of a track or
        // run out of space to distribute. If the former scenario happens, it should
        // be easy to see that the group of tracks that will reach its limit first
        // will be that with the least growth potential. Otherwise, if tracks in such
        // group does not reach their limit, every upcoming track with greater growth
        // potential must be able to increase its size by the same amount.
        if (growableTrackCount != 0 || isDistributionForGrowthLimits(contributionType)) {
            // iText's comment: in chromium CompareTracksByGrowthPotential is lambda,
            // iText's comment: but for porting purpose lambda was extracted to a class
            
            // Only sort for equal distributions; since the growth potential of any
            // flexible set is infinite, they don't require comparing.
            if (Float.compare(flexFactorSum, 0) == 0) {
                Collections.sort(tracksToGrow, new CompareTracksByGrowthPotential(this, contributionType));
            }
        }

        // iText's comment: ExtraSpaceShare lambda was replaced with static method extraSpaceShare to resolve issue with working on java and porting

        // Distribute space up to limits:
        //   - For base sizes, grow the base size up to the growth limit.
        //   - For growth limits, the only case where a growth limit should grow at
        //   this step is when its set has already been marked "infinitely growable".
        //   Increase the growth limit up to the 'fit-content' argument (if any); note
        //   that these arguments could prevent this step to fulfill the entirety of
        //   the extra space and further distribution would be needed.
        for (Track track : tracksToGrow) {
            // Break early if there are no further tracks to grow.
            if (growableTrackCount == 0) {
                break;
            }
            // iText's comment: java doesn't allow change variables inside lambda, it is why ExtraSpaceShareFunctionParams was introduced
            ExtraSpaceShareFunctionParams changedParams = new ExtraSpaceShareFunctionParams(growableTrackCount, shareRatioSum, extraSpace);
            track.incurredIncrease = extraSpaceShare(track, growthPotentialForSet(track, contributionType, false),
                    isEqualDistribution, changedParams);
            growableTrackCount = changedParams.growableTrackCount;
            shareRatioSum = changedParams.shareRatioSum;
            extraSpace = changedParams.extraSpace;
        }

        // Distribute space beyond limits:
        //   - For base sizes, every affected track can grow indefinitely.
        //   - For growth limits, grow tracks up to their 'fit-content' argument.
        if (tracksToGrowBeyondLimits != null && Float.compare(extraSpace, 0f) != 0) {
            for (Track track : tracksToGrowBeyondLimits) {
                // iText's comment: BeyondLimitsGrowthPotential function was replaced by 1 line of code
                // For growth limits, ignore the "infinitely growable" flag and grow all
                // affected tracks up to their 'fit-content' argument (note that
                // |GrowthPotentialForSet| already accounts for it).
                float beyondLimitsGrowthPotential = isDistributionForGrowthLimits(contributionType)
                        ? growthPotentialForSet(track, contributionType, true) : -1;

                if (Float.compare(beyondLimitsGrowthPotential, 0) != 0) {
                    growableTrackCount++;
                }
            }

            shareRatioSum = growableTrackCount;

            for (Track track : tracksToGrowBeyondLimits) {
                // Break early if there are no further tracks to grow.
                if (growableTrackCount == 0) {
                    break;
                }
                // iText's comment: BeyondLimitsGrowthPotential function was replaced by 1 line of code
                // For growth limits, ignore the "infinitely growable" flag and grow all
                // affected tracks up to their 'fit-content' argument (note that
                // |GrowthPotentialForSet| already accounts for it).
                float beyondLimitsGrowthPotential = isDistributionForGrowthLimits(contributionType)
                        ? growthPotentialForSet(track, contributionType, true) : -1;

                // iText's comment: java doesn't allow change variables inside lambda, it is why ExtraSpaceShareFunctionParams was introduced
                final ExtraSpaceShareFunctionParams changedParams = new ExtraSpaceShareFunctionParams(growableTrackCount, shareRatioSum, extraSpace);
                track.incurredIncrease = extraSpaceShare(track, beyondLimitsGrowthPotential, isEqualDistribution, changedParams);
                growableTrackCount = changedParams.growableTrackCount;
                shareRatioSum = changedParams.shareRatioSum;
                extraSpace = changedParams.extraSpace;
            }
        }
    }

    // chromium's method
    private static float extraSpaceShare(Track track, float growthPotential, boolean isEqualDistribution,
            ExtraSpaceShareFunctionParams changedParams) {

        // If this set won't take a share of the extra space, e.g. has zero growth
        // potential, exit so that this set is filtered out of |share_ratio_sum|.
        if (Float.compare(growthPotential, 0.0f) == 0) {
            return 0;
        }
        int trackCount = 1;
        float trackShareRatio = isEqualDistribution ? 1 : track.getFlexFactor();
        // Since |share_ratio_sum| can be greater than the wtf_size_t limit, cap the
        // value of |set_share_ratio| to prevent overflows.
        if (trackShareRatio > changedParams.shareRatioSum) {
            trackShareRatio = changedParams.shareRatioSum;
        }
        float extraSpaceShare;
        if (Float.compare(trackShareRatio, changedParams.shareRatioSum) == 0) {
            // If this set's share ratio and the remaining ratio sum are the same, it
            // means that this set will receive all of the remaining space. Hence, we
            // can optimize a little by directly using the extra space as this set's
            // share and break early by decreasing the remaining growable track count
            // to 0 (even if there are further growable tracks, since the share ratio
            // sum will be reduced to 0, their space share will also be 0).
            trackCount = changedParams.growableTrackCount;
            extraSpaceShare = changedParams.extraSpace;
        } else {
            extraSpaceShare = changedParams.extraSpace * trackShareRatio / changedParams.shareRatioSum;
        }

        if (Float.compare(growthPotential, -1f) != 0) {
            extraSpaceShare = Math.min(extraSpaceShare, growthPotential);
        }

        changedParams.growableTrackCount -= trackCount;
        changedParams.shareRatioSum -= trackShareRatio;
        changedParams.extraSpace -= extraSpaceShare;
        return extraSpaceShare;
    }

    // chromium class
    // iText's comment: in chromium this class is lambda, but for porting purpose lambda was extracted to a class
    private final static class CompareTracksByGrowthPotential implements Comparator<Track> {
        private final GridTrackSizer gridTrackSizer;
        private final GridItemContributionType contributionType;

        public CompareTracksByGrowthPotential(GridTrackSizer gridTrackSizer,
                GridItemContributionType contributionType) {
            this.gridTrackSizer = gridTrackSizer;
            this.contributionType = contributionType;
        }

        @Override
        public int compare(Track lhs, Track rhs) {
            float growthPotentialLhs = gridTrackSizer.growthPotentialForSet(lhs, contributionType, true);
            float growthPotentialRhs = gridTrackSizer.growthPotentialForSet(rhs, contributionType, true);
            if (Float.compare(growthPotentialLhs, -1f) == 0 || Float.compare(growthPotentialRhs, -1f) == 0) {
                // At this point we know that there is at least one set with
                // infinite growth potential; if |a| has a definite value, then |b|
                // must have infinite growth potential, and thus, |a| < |b|.
                return Float.compare(growthPotentialLhs, -1f) == 0 ? 1 : -1;
            }
            // Straightforward comparison of definite growth potentials.
            return Float.compare(growthPotentialLhs, growthPotentialRhs);
        }
    }

    // chromium's method
    private static boolean isDistributionForGrowthLimits(GridItemContributionType contributionType) {
        switch (contributionType) {
            case FOR_INTRINSIC_MINIMUMS:
            case FOR_CONTENT_BASED_MINIMUMS:
            case FOR_MAX_CONTENT_MINIMUMS:
            case FOR_FREE_SPACE:
                return false;
            case FOR_INTRINSIC_MAXIMUMS:
            case FOR_MAX_CONTENT_MAXIMUMS:
                return true;
            default:
                return false;
        }
    }

    // iText's class which is workaround to port c++ code to java
    private static class ExtraSpaceShareFunctionParams {
        int growableTrackCount;
        float shareRatioSum;
        float extraSpace;

        public ExtraSpaceShareFunctionParams(int growableTrackCount, float shareRatioSum, float extraSpace) {
            this.growableTrackCount = growableTrackCount;
            this.shareRatioSum = shareRatioSum;
            this.extraSpace = extraSpace;
        }
    }

    // We define growth potential = limit - affected size; for base sizes, the limit
    // is its growth limit. For growth limits, the limit is infinity if it is marked
    // as "infinitely growable", and equal to the growth limit otherwise.

    // chromium's method
    // iText's comment: InfinitelyGrowableBehavior enum was replaced by ignoreInfinitelyGrowable boolean
    private float growthPotentialForSet(Track track, GridItemContributionType contributionType, boolean ignoreInfinitelyGrowable) {
        switch (contributionType) {
            case FOR_INTRINSIC_MINIMUMS:
            case FOR_CONTENT_BASED_MINIMUMS:
            case FOR_MAX_CONTENT_MINIMUMS:
                if (Float.compare(track.growthLimit, -1f) == 0) {
                    return -1f;
                }
                float increasedBaseSize = track.baseSize + track.incurredIncrease;
                return track.growthLimit - increasedBaseSize;
            case FOR_INTRINSIC_MAXIMUMS:
            case FOR_MAX_CONTENT_MAXIMUMS:
                if (!ignoreInfinitelyGrowable && Float.compare(track.growthLimit, -1f) != 0 && !track.isInfinityGrowable) {
                    // For growth limits, the potential is infinite if its value is infinite
                    // too or if the set is marked as infinitely growable; otherwise, zero.
                    return 0f;
                }
                // The max track sizing function of a 'fit-content' track is treated as
                // 'max-content' until it reaches the limit specified as the 'fit-content'
                // argument, after which it is treated as having a fixed sizing function
                // of that argument (with a growth potential of zero).

                // iText's comment: Case when availableSpace is indefinite and fit-content uses percent is handled in GridTrackSizer constructor
                if (track.value.getType() == ValueType.FIT_CONTENT) {
                    FitContentValue fitContentValue = (FitContentValue) track.value;
                    float growthPotential = fitContentValue.getMaxSizeForSpace(availableSpace) -
                            track.definiteGrowthLimit() - track.incurredIncrease;
                    return Math.max(growthPotential, 0);
                }

                // Otherwise, this set has infinite growth potential.
                return -1f;
            case FOR_FREE_SPACE:
                return track.growthLimit - track.baseSize;
            default:
                return 0;
        }
    }

    // https://drafts.csswg.org/css-grid-2/#extra-space
    // Returns true if a track's used size should be consider to grow beyond its limit
    // (see the "Distribute space beyond limits" section); otherwise, false.
    // Note that we will deliberately return false in cases where we don't have a
    // collection of tracks different than "all affected tracks".

    // chromium's method
    private static boolean shouldUsedSizeGrowBeyondLimit(Track track, GridItemContributionType contributionType) {
        GridValue maxTrack = track.value;
        if (track.value.getType() == GridValue.ValueType.MINMAX) {
            maxTrack = ((MinMaxValue) track.value).getMax();
        }
        switch (contributionType) {
            case FOR_INTRINSIC_MINIMUMS:
            case FOR_CONTENT_BASED_MINIMUMS:
                // intrinsic max track sizing function
                return maxTrack.getType() == ValueType.MIN_CONTENT || maxTrack.getType() == ValueType.MAX_CONTENT ||
                        maxTrack.getType() == ValueType.AUTO || maxTrack.getType() == ValueType.FIT_CONTENT;
            case FOR_MAX_CONTENT_MINIMUMS:
                // max-content max track sizing function
                return maxTrack.getType() == ValueType.MAX_CONTENT || maxTrack.getType() == ValueType.AUTO;
            case FOR_INTRINSIC_MAXIMUMS:
            case FOR_MAX_CONTENT_MAXIMUMS:
            case FOR_FREE_SPACE:
                return false;
            default:
                return false;
        }
    }


    // Returns true if a track should increase its used size according to the steps in
    // https://drafts.csswg.org/css-grid-2/#algo-spanning-items; false otherwise.
    //
    // chromium's method
    private static boolean isContributionAppliedToTrack(Track track, GridItemContributionType contributionType) {
        GridValue minTrack = track.value;
        GridValue maxTrack = track.value;
        if (track.value.getType() == GridValue.ValueType.MINMAX) {
            minTrack = ((MinMaxValue) track.value).getMin();
            maxTrack = ((MinMaxValue) track.value).getMax();
        }
        switch (contributionType) {
            case FOR_INTRINSIC_MINIMUMS:
                // intrinsic min track sizing function
                return minTrack.getType() == ValueType.MIN_CONTENT || minTrack.getType() == ValueType.MAX_CONTENT ||
                        minTrack.getType() == ValueType.AUTO || minTrack.getType() == ValueType.FIT_CONTENT;
            case FOR_CONTENT_BASED_MINIMUMS:
                // min\max-content min track sizing function
                return minTrack.getType() == ValueType.MIN_CONTENT || minTrack.getType() == ValueType.MAX_CONTENT;
            case FOR_MAX_CONTENT_MINIMUMS:
                // max-content min track sizing function
                return minTrack.getType() == ValueType.MAX_CONTENT;
            case FOR_INTRINSIC_MAXIMUMS:
                // intrinsic max track sizing function
                return maxTrack.getType() == ValueType.MIN_CONTENT || maxTrack.getType() == ValueType.MAX_CONTENT ||
                        maxTrack.getType() == ValueType.AUTO || maxTrack.getType() == ValueType.FIT_CONTENT;
            case FOR_MAX_CONTENT_MAXIMUMS:
                // max-content max track sizing function
                return maxTrack.getType() == ValueType.MAX_CONTENT || maxTrack.getType() == ValueType.AUTO || maxTrack.getType() == ValueType.FIT_CONTENT;
            case FOR_FREE_SPACE:
                return true;
            default:
                return false;
        }
    }

    // Returns the corresponding size to be increased by accommodating a grid item's
    // contribution; for intrinsic min track sizing functions, return the base size.
    // For intrinsic max track sizing functions, return the growth limit.

    // chromium's method
    private static float affectedSizeForContribution(Track track, GridItemContributionType contributionType) {
        switch (contributionType) {
            case FOR_INTRINSIC_MINIMUMS:
            case FOR_CONTENT_BASED_MINIMUMS:
            case FOR_MAX_CONTENT_MINIMUMS:
                return track.baseSize;
            case FOR_INTRINSIC_MAXIMUMS:
            case FOR_MAX_CONTENT_MAXIMUMS:
                return track.definiteGrowthLimit();
            // FOR_FREE_SPACE not reachable here
            default:
                return 0;
        }
    }

    // This enum corresponds to each step used to accommodate grid items across
    // intrinsic tracks according to their min and max track sizing functions

    // chromium's enum
    private enum GridItemContributionType {
        FOR_INTRINSIC_MINIMUMS,
        FOR_CONTENT_BASED_MINIMUMS,
        FOR_MAX_CONTENT_MINIMUMS,
        FOR_INTRINSIC_MAXIMUMS,
        FOR_MAX_CONTENT_MAXIMUMS,
        FOR_FREE_SPACE
    }

    // iText's method
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
                    track.baseSize = ((LengthValue) minTrackSizingValue).getValue();
                } else {
                    track.baseSize = ((LengthValue) minTrackSizingValue).getValue() / 100 * availableSpace;
                }
            } else {
                track.baseSize = 0;
            }

            // A fixed sizing function
            if (maxTrackSizingValue.getType() == GridValue.ValueType.POINT
                    || maxTrackSizingValue.getType() == GridValue.ValueType.PERCENT) {

                if (maxTrackSizingValue.getType() == GridValue.ValueType.POINT) {
                    track.growthLimit = ((LengthValue) maxTrackSizingValue).getValue();
                } else {
                    track.growthLimit = ((LengthValue) maxTrackSizingValue).getValue() / 100 * availableSpace;
                }
            } else {
                track.growthLimit = -1;
            }
        }
    }

    /**
     * Calculate min or max contribution of a cell.
     *
     * @param cell cell to calculate contribution
     * @param minTypeContribution type of contribution: min if true, max otherwise
     * @return contribution value
     */
    // iText's method
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

    // iText's class
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

    // iText's class
    static class Track {
        float baseSize;
        // consider -1 as an infinity value
        float growthLimit;
        GridValue value;

        float plannedIncrease;
        float incurredIncrease;
        boolean isInfinityGrowable = false;

        public float definiteGrowthLimit() {
            // For infinite growth limits, substitute the track's base size.
            return Float.compare(growthLimit, -1f) == 0 ? baseSize : growthLimit;
        }

        public boolean isFlexibleTrack() {
            // Flex is replaced by minmax in GridTrackSizer constructor
            if (value.getType() == ValueType.MINMAX) {
                return ((MinMaxValue) value).getMax().getType() == ValueType.FLEX;
            }
            return false;
        }

        public boolean hasAutoMax() {
            if (value.getType() == ValueType.MINMAX) {
                return ((MinMaxValue) value).getMax().getType() == ValueType.AUTO;
            }
            return value.getType() == ValueType.AUTO;
        }

        public float getFlexFactor() {
            // Flex is replaced by minmax in GridTrackSizer constructor
            if (value.getType() == ValueType.MINMAX) {
                final BreadthValue max = ((MinMaxValue) value).getMax();
                return max.getType() == ValueType.FLEX ? ((FlexValue) max).getFlex() : 0f;
            }
            return 0f;
        }

        public void ensureGrowthLimitIsNotLessThanBaseSize() {
            if (Float.compare(growthLimit, -1) != 0 && growthLimit < baseSize) {
                growthLimit = baseSize;
            }
        }
    }
}
