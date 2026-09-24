/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.commons.logs.LazyLogger;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.minmaxwidth.RotationMinMaxWidth;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class RotationUtils {
    private static final LazyLogger LOGGER = new LazyLogger(RotationUtils.class);

    private RotationUtils() {
    }

    /**
     * Calculates a rotated {@link MinMaxWidth} for a renderer that has {@link Property#ROTATION_ANGLE} set.
     *
     * <p>
     * This method lays out the renderer using the original (non-rotated) min and max widths,
     * then converts both resulting occupied area bounding boxes to rotated widths.
     * The smaller rotated value becomes the returned min width and the larger one becomes
     * the returned max width.
     *
     * <p>
     * This is an approximation, not an exact rotated min/max width calculation.
     * Only two sample points are measured (original min and original max width),
     * so it does not account for cases where the rotated extrema occur at an intermediate width.
     *
     * @param minMaxWidth min/max width calculated for the non-rotated renderer
     * @param renderer the renderer
     *
     * @return rotated min/max width
     */
    static MinMaxWidth calculateRotationMinMaxWidth(MinMaxWidth minMaxWidth, AbstractRenderer renderer) {
        PropertiesBackup backup = new PropertiesBackup(renderer);
        Float rotation = backup.storeFloatProperty(Property.ROTATION_ANGLE);
        if (rotation != null) {
            float angle = (float) rotation;
            // Measure rotated widths at min and max widths
            Float rotatedAtMinWidth = getLayoutRotatedWidth(renderer, minMaxWidth.getMinWidth(), angle);
            Float rotatedAtMaxWidth = getLayoutRotatedWidth(renderer, minMaxWidth.getMaxWidth(), angle);
            if (rotatedAtMinWidth != null && rotatedAtMaxWidth != null) {
                backup.restoreProperty(Property.ROTATION_ANGLE);
                return new MinMaxWidth(Math.min(rotatedAtMinWidth.floatValue(), rotatedAtMaxWidth.floatValue()),
                        Math.max(rotatedAtMinWidth.floatValue(), rotatedAtMaxWidth.floatValue()), 0);
            }
        }

        backup.restoreProperty(Property.ROTATION_ANGLE);
        return minMaxWidth;
    }

    /**
     * This method tries to calculate width of not rotated renderer, so after rotation it fits availableWidth.
     * This method uses heuristics of {@link RotationMinMaxWidth#calculate(double, double, MinMaxWidth, double)}
     * as a fallback if we could not find an appropriate rotated width by laying out element without rotation.
     * The minMaxWidth calculations and initial layout may take long time.
     *
     * @param availableWidth the width of layoutArea
     * @param availableHeight the height of layoutArea
     * @param renderer       the actual renderer
     *
     * @return the width that should be set as width of layout area to properly layout element, or fallback to
     * {@link AbstractRenderer#retrieveWidth(float)} in case it can not be calculated, or renderer isn't rotated.
     */
    static Float retrieveRotatedLayoutWidth(float availableWidth, float availableHeight,
            AbstractRenderer renderer) {
        PropertiesBackup backup = new PropertiesBackup(renderer);
        Float rotation = backup.storeFloatProperty(Property.ROTATION_ANGLE);
        try {
            UnitValue widthProperty = renderer.<UnitValue>getProperty(Property.WIDTH);
            Float resolvedWidth = renderer.retrieveWidth(availableWidth);
            // Some renderers (like CellRenderer) may have WIDTH property set, but retrieveWidth() still returns null
            // because width depends on a parent context (table column width). In that case optimization is needed.
            if (rotation != null && (widthProperty == null || resolvedWidth == null)) {
                final float angle = (float) rotation;
                // Backup FORCED_PLACEMENT property to avoid successful layout of rotated element
                // in case it doesn't fit. It also prevents mutating the renderer's state during layout.
                backup.storeBoolProperty(Property.FORCED_PLACEMENT);
                MinMaxWidth minMaxWidth = renderer.getMinMaxWidth();
                final Float heuristicsWidth = fallbackHeuristicsAlgo(renderer, minMaxWidth, angle, availableWidth);

                Rectangle additions = new Rectangle(0, 0);
                renderer.applyPaddings(additions, true);
                renderer.applyBorderBox(additions, true);
                renderer.applyMargins(additions, true);

                RotatedMetrics bestFit = findBestFittingMetrics(renderer, minMaxWidth, heuristicsWidth,
                        availableWidth, availableHeight, angle);
                if (bestFit != null) {
                    return bestFit.originalWidth - additions.getWidth() + MinMaxWidthUtils.getEps();
                } else if (heuristicsWidth != null) {
                    return heuristicsWidth.floatValue() - additions.getWidth() + MinMaxWidthUtils.getEps();
                }
            }

            return renderer.retrieveWidth(availableWidth);
        } finally {
            backup.restoreProperty(Property.ROTATION_ANGLE);
            backup.restoreProperty(Property.FORCED_PLACEMENT);
        }
    }

    private static Float fallbackHeuristicsAlgo(AbstractRenderer renderer, MinMaxWidth minMaxWidth, float angle,
            float availableWidth) {
        Float width = null;

        PropertiesBackup backup = new PropertiesBackup(renderer);
        backup.<UnitValue>storeProperty(Property.HEIGHT);
        backup.<UnitValue>storeProperty(Property.MIN_HEIGHT);
        backup.<UnitValue>storeProperty(Property.MAX_HEIGHT);

        final float length = (minMaxWidth.getMaxWidth() + minMaxWidth.getMinWidth()) / 2 + MinMaxWidthUtils.getEps();
        // Using this width for initial layout helps in case of small elements. They may have more free spaces,
        // but it's more likely they fit.
        LayoutResult layoutResult = renderer.layout(
                new LayoutContext(new LayoutArea(1, new Rectangle(length, AbstractRenderer.INF))));
        if (layoutResult.getOccupiedArea() != null) {
            final double area = layoutResult.getOccupiedArea().getBBox().getWidth() *
                    layoutResult.getOccupiedArea().getBBox().getHeight();
            RotationMinMaxWidth result = RotationMinMaxWidth.calculate(angle, area, minMaxWidth, availableWidth);
            if (result != null) {
                width = result.getMaxWidthHeight() > result.getMinWidthHeight()
                        ? (float) result.getMinWidthOrigin()
                        : (float) result.getMaxWidthOrigin();
            }
        } else {
            LOGGER.warn(() -> LayoutLogMessageConstant.ROTATED_LAYOUT_ELEMENT_DOES_NOT_FIT_AREA);
        }

        backup.restoreProperty(Property.HEIGHT);
        backup.restoreProperty(Property.MIN_HEIGHT);
        backup.restoreProperty(Property.MAX_HEIGHT);

        return width;
    }

    // Get actual width of element based on its layout
    private static Float getLayoutRotatedWidth(AbstractRenderer renderer, float layoutWidth, double angle) {
        RotatedMetrics rotatedMetrics = getLayoutRotatedMetrics(renderer, layoutWidth, angle);
        if (rotatedMetrics != null) {
            return rotatedMetrics.rotatedWidth;
        } else {
            return null;
        }
    }

    private static RotatedMetrics getLayoutRotatedMetrics(AbstractRenderer renderer, float layoutWidth, double angle) {
        LayoutResult result = renderer.layout(new LayoutContext(
                new LayoutArea(1, new Rectangle(layoutWidth + MinMaxWidthUtils.getEps(), AbstractRenderer.INF))));
        if (result.getOccupiedArea() == null || result.getStatus() != LayoutResult.FULL) {
            if (result.getOccupiedArea() == null) {
                LOGGER.warn(() -> LayoutLogMessageConstant.ROTATED_LAYOUT_ELEMENT_DOES_NOT_FIT_AREA);
            }

            return null;
        }

        Rectangle bbox = result.getOccupiedArea().getBBox();
        float rotatedWidth = (float) RotationMinMaxWidth.calculateRotatedWidth(bbox, angle);
        float rotatedHeight = (float) RotationMinMaxWidth.calculateRotatedHeight(bbox, angle);
        return new RotatedMetrics(layoutWidth, rotatedWidth, rotatedHeight);
    }

    private static RotatedMetrics findBestFittingMetrics(AbstractRenderer renderer, MinMaxWidth minMaxWidth,
            Float heuristicsWidth, float availableWidth, float availableHeight, double angle) {
        final float eps = MinMaxWidthUtils.getEps();
        final float minOriginWidth = minMaxWidth.getMinWidth() + eps;
        final float maxOriginWidth = minMaxWidth.getMaxWidth() + eps;
        RotatedMetrics bestFit = null;

        AbstractRenderer r = renderer;
        if (renderer instanceof CellRenderer) {
            // Use a special renderer for cells
            Cell cellModel = (Cell) renderer.getModelElement();
            AbstractRenderer subTree = (AbstractRenderer) cellModel.createRendererSubTree();
            r = new RotatedCellRenderer(cellModel);

            r.setParent(renderer.getParent());
            r.addAllChildRenderers(subTree.getChildRenderers());
            r.addAllProperties(renderer.getOwnProperties());
        }

        if (heuristicsWidth != null) {
            RotatedMetrics metricsAtHeuristics = getLayoutRotatedMetrics(r, heuristicsWidth.floatValue(), angle);
            bestFit = chooseLowerHeightFit(bestFit, metricsAtHeuristics, availableWidth, availableHeight);
        }

        // Try different options
        for (float currentWidth : getCandidateWidths(minOriginWidth, maxOriginWidth, bestFit)) {
            RotatedMetrics candidate = getLayoutRotatedMetrics(r, currentWidth, angle);
            bestFit = chooseLowerHeightFit(bestFit, candidate, availableWidth + eps, availableHeight + eps);
        }

        return bestFit;
    }

    private static RotatedMetrics chooseLowerHeightFit(RotatedMetrics currentBest, RotatedMetrics candidate,
                                                        float availableWidth, float availableHeight) {
        if (candidate == null || candidate.rotatedWidth > availableWidth ||
                candidate.rotatedHeight > availableHeight) {
            return currentBest;
        }
        if (currentBest == null || candidate.rotatedHeight < currentBest.rotatedHeight) {
            return candidate;
        }
        return currentBest;
    }

    private static float[] getCandidateWidths(float minOriginWidth, float maxOriginWidth,
            RotatedMetrics metricsAtHeuristics) {
        // If we already found something suitable using heuristics, let's not try hard to improve it further
        // If heuristics failed completely, let's try to find some result by using smaller steps and more iterations
        final int AMOUNT_OF_STEPS = metricsAtHeuristics == null ? 10 : 4;
        float[] widths = new float[AMOUNT_OF_STEPS];
        for (int i = 0; i < AMOUNT_OF_STEPS; i++) {
            widths[i] = minOriginWidth + (maxOriginWidth - minOriginWidth) / (AMOUNT_OF_STEPS - 1) * i;
        }

        return widths;
    }

    private static final class RotatedMetrics {
        public final float originalWidth;
        public final float rotatedWidth;
        public final float rotatedHeight;

        public RotatedMetrics(float originalWidth, float rotatedWidth, float rotatedHeight) {
            this.originalWidth = originalWidth;
            this.rotatedWidth = rotatedWidth;
            this.rotatedHeight = rotatedHeight;
        }
    }

    private static final class PropertiesBackup {

        private AbstractRenderer renderer;
        private HashMap<Integer, PropertyBackup> propertiesBackup = new HashMap<>();

        public PropertiesBackup(AbstractRenderer renderer) {
            this.renderer = renderer;
        }

        //workaround for autoport
        public Float storeFloatProperty(int property) {
            Float value = renderer.getPropertyAsFloat(property);
            if (value != null) {
                propertiesBackup.put(property, new PropertyBackup(value, renderer.hasOwnProperty(property)));
                renderer.setProperty(property, null);
            }
            return value;
        }

        public Boolean storeBoolProperty(int property) {
            Boolean value = renderer.getPropertyAsBoolean(property);
            if (value != null) {
                propertiesBackup.put(property, new PropertyBackup(value, renderer.hasOwnProperty(property)));
                renderer.setProperty(property, null);
            }
            return value;
        }

        public <T> T storeProperty(int property) {
            T value = renderer.<T>getProperty(property);
            if (value != null) {
                propertiesBackup.put(property, new PropertyBackup(value, renderer.hasOwnProperty(property)));
                renderer.setProperty(property, null);
            }
            return value;
        }

        public void restoreProperty(int property) {
            PropertyBackup backup = propertiesBackup.remove(property);
            if (backup != null) {
                if (backup.isOwnedByRender()) {
                    renderer.setProperty(property, backup.getValue());
                } else {
                    renderer.deleteOwnProperty(property);
                }
            }
        }

        private static class PropertyBackup {
            private Object propertyValue;
            private boolean isOwnedByRender;

            public PropertyBackup(Object propertyValue, boolean isOwnedByRender) {
                this.propertyValue = propertyValue;
                this.isOwnedByRender = isOwnedByRender;
            }

            public Object getValue() {
                return propertyValue;
            }

            public boolean isOwnedByRender() {
                return isOwnedByRender;
            }
        }
    }

    /**
     * This renderer is used for calculations of rotated area.
     * processNotFullChildResult switches off wasHeightClipped parameter because it allows LayoutResult.FULL
     * even if the element doesn't fit by height.
     */
    private static final class RotatedCellRenderer extends CellRenderer {
        public RotatedCellRenderer(Cell modelElement) {
            super(modelElement);
        }

        @Override
        public IRenderer getNextRenderer() {
            return new RotatedCellRenderer((Cell) modelElement);
        }

        @Override
        LayoutResult processNotFullChildResult(LayoutContext layoutContext,
                Map<Integer, IRenderer> waitingFloatsSplitRenderers,
                List<IRenderer> waitingOverflowFloatRenderers, boolean wasHeightClipped,
                List<Rectangle> floatRendererAreas, boolean marginsCollapsingEnabled,
                float clearHeightCorrection, Border[] borders, UnitValue[] paddings,
                List<Rectangle> areas, int currentAreaPos, Rectangle layoutBox,
                Set<Rectangle> nonChildFloatingRendererAreas, IRenderer causeOfNothing,
                boolean anythingPlaced, int childPos, LayoutResult result) {
            return super.processNotFullChildResult(layoutContext, waitingFloatsSplitRenderers,
                    waitingOverflowFloatRenderers, false, floatRendererAreas, marginsCollapsingEnabled,
                    clearHeightCorrection, borders, paddings, areas, currentAreaPos, layoutBox,
                    nonChildFloatingRendererAreas, causeOfNothing, anythingPlaced, childPos, result);
        }
    }
}
