/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.layout.properties.InlineVerticalAlignment;
import com.itextpdf.layout.properties.InlineVerticalAlignmentType;
import com.itextpdf.layout.properties.LineHeight;
import com.itextpdf.layout.properties.Property;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class InlineVerticalAlignmentHelper {

    private static final float ADJUSTMENT_THRESHOLD = 0.001F;

    private static final float SUPER_OFFSET = 0.3F;

    private static final float SUB_OFFSET = -0.2F;

    private InlineVerticalAlignmentHelper() {
    }

    public static void adjustChildrenYLineHtmlMode(LineRenderer lineRenderer) {
        float actualYLine = lineRenderer.occupiedArea.getBBox().getY() +
                lineRenderer.occupiedArea.getBBox().getHeight() - lineRenderer.maxDescent;

        // first round, all text based alignments
        processRenderers(lineRenderer, lineRenderer.getChildRenderers(), actualYLine,
                alignment -> !isBoxOrientedVerticalAlignment(alignment),
                alignment -> !isBoxOrientedVerticalAlignment(alignment));

        // next round, box oriented alignments
        List<IRenderer> sortedRenderers = lineRenderer.getChildRenderers().stream().
                sorted((r1, r2) -> (int) Math.round((r2.getOccupiedArea().getBBox().getHeight() -
                        r1.getOccupiedArea().getBBox().getHeight()) * 1000)).collect(Collectors.toList());
        processRenderers(lineRenderer, sortedRenderers, actualYLine,
                alignment -> isBoxOrientedVerticalAlignment(alignment),
                alignment -> true);
    }

    private static boolean isBoxOrientedVerticalAlignment(InlineVerticalAlignment alignment) {
        return alignment.getType() == InlineVerticalAlignmentType.TOP ||
                alignment.getType() == InlineVerticalAlignmentType.BOTTOM;
    }

    private static void processRenderers(LineRenderer lineRenderer, List<IRenderer> renderers, float actualYLine,
            Predicate<InlineVerticalAlignment> needProcess,
            Predicate<InlineVerticalAlignment> needRecalculateSizes) {
        float[] fontInfo = LineHeightHelper.getActualFontInfo(lineRenderer);
        float textTop = actualYLine + fontInfo[LineHeightHelper.ASCENDER_INDEX] -
                fontInfo[LineHeightHelper.LEADING_INDEX] / 2;
        float textBottom = actualYLine + fontInfo[LineHeightHelper.DESCENDER_INDEX] -
                fontInfo[LineHeightHelper.LEADING_INDEX] / 2;
        float leading = fontInfo[LineHeightHelper.LEADING_INDEX];
        float xHeight = fontInfo[LineHeightHelper.XHEIGHT_INDEX];

        float maxTop = Float.MIN_VALUE;
        float minBottom = Float.MAX_VALUE;
        float maxHeight = Float.MIN_VALUE;
        boolean maxminValuesChanged = false;
        for (final IRenderer renderer : renderers) {
            if (FloatingHelper.isRendererFloating(renderer)) {
                continue;
            }
            InlineVerticalAlignment alignment = renderer.<InlineVerticalAlignment>getProperty(

                    Property.INLINE_VERTICAL_ALIGNMENT);
            if (alignment == null) {
                alignment = new InlineVerticalAlignment();
            }

            if (needProcess.test(alignment)) {
                Rectangle cBbox = getAdjustedArea(renderer);
                // Take into account new size but not apply it yet to the parent renderer
                Rectangle pBbox = new Rectangle(lineRenderer.occupiedArea.getBBox().getX(),
                        Math.min(minBottom, lineRenderer.occupiedArea.getBBox().getY()),
                        lineRenderer.occupiedArea.getBBox().getWidth(),
                        Math.max(maxHeight, lineRenderer.occupiedArea.getBBox().getHeight()));
                float offset = calculateOffset(renderer, cBbox, alignment,
                        actualYLine, textTop, textBottom, leading, xHeight, pBbox);
                if (Math.abs(offset) > ADJUSTMENT_THRESHOLD) {
                    renderer.move(0, offset);
                }
            }
            if (needRecalculateSizes.test(alignment)) {
                Rectangle cBbox = getAdjustedArea(renderer);
                maxTop = Math.max(maxTop, cBbox.getTop());
                minBottom = Math.min(minBottom, cBbox.getBottom());
                maxHeight = Math.max(maxHeight, cBbox.getHeight());
                maxminValuesChanged = true;
            }
        }

        // Adjust this and move children down as needed
        if (maxminValuesChanged) {
            adjustBBox(lineRenderer, maxHeight, maxTop, minBottom);
        }
    }

    private static Rectangle getAdjustedArea(IRenderer renderer) {
        Rectangle rect = renderer.getOccupiedArea().getBBox().clone();
        if (renderer instanceof AbstractRenderer && !(renderer instanceof BlockRenderer) &&
                !renderer.hasProperty(Property.INLINE_VERTICAL_ALIGNMENT)) {
            AbstractRenderer ar = (AbstractRenderer) renderer;
            ar.applyBorderBox(rect, false);
            ar.applyPaddings(rect, false);
        }

        return rect;
    }


    private static void adjustBBox(LineRenderer lineRenderer, float maxHeight, float maxTop, float minBottom) {
        LineHeight lineHeight = lineRenderer.<LineHeight>getProperty(Property.LINE_HEIGHT);
        float actualHeight = maxHeight;
        if (lineHeight != null) {
            actualHeight = Math.max(actualHeight, LineHeightHelper.calculateLineHeight(lineRenderer));
        }

        maxTop += (actualHeight - maxHeight) / 2;
        minBottom -= (actualHeight - maxHeight) / 2;
        maxHeight = actualHeight;
        maxHeight = Math.max(maxHeight, maxTop - minBottom);

        float originalTop = lineRenderer.occupiedArea.getBBox().getTop();
        lineRenderer.occupiedArea.getBBox().setHeight(maxHeight);
        float delta = originalTop - lineRenderer.occupiedArea.getBBox().getTop();
        lineRenderer.occupiedArea.getBBox().moveUp(delta);

        float childDelta = originalTop - maxTop;
        for (final IRenderer renderer : lineRenderer.getChildRenderers()) {
            renderer.move(0, childDelta);
        }
    }

    private static float calculateOffset(IRenderer renderer, Rectangle cBBox, InlineVerticalAlignment alignment,
            float baseline, float textTop, float textBottom, float leading, float xHeight, Rectangle pBBox) {
        switch (alignment.getType()) {
            case BASELINE:
                return baseline - getChildBaseline(renderer, leading);
            case TEXT_TOP:
                return textTop - cBBox.getTop();
            case TEXT_BOTTOM:
                return textBottom - cBBox.getBottom();
            case FIXED:
                float offsetFixed = 0;
                offsetFixed = alignment.getValue();
                return baseline + offsetFixed - getChildBaseline(renderer, leading);
            case SUPER:
            case SUB:
            case FRACTION:
                float offsetFraction = 0;
                if (alignment.getType() == InlineVerticalAlignmentType.SUPER) {
                    offsetFraction = SUPER_OFFSET;
                } else if (alignment.getType() == InlineVerticalAlignmentType.SUB) {
                    offsetFraction = SUB_OFFSET;
                } else {
                    offsetFraction = alignment.getValue();
                }

                float target = baseline + (textTop - textBottom) * offsetFraction;
                return target - getChildBaseline(renderer, leading);
            case MIDDLE:
                return (baseline + xHeight / 2) - (cBBox.getBottom() + cBBox.getHeight() / 2);
            case BOTTOM:
                return pBBox.getBottom() - cBBox.getBottom();
            case TOP:
                return pBBox.getTop() - cBBox.getTop();
            default:
                return 0;
        }
    }

    private static float getChildBaseline(IRenderer renderer, float leading) {
        if (renderer instanceof ILeafElementRenderer) {
            float descent = ((ILeafElementRenderer) renderer).getDescent();
            return renderer.getOccupiedArea().getBBox().getBottom() - descent;
        } else {
            Float yLine = LineRenderer.isInlineBlockChild(renderer) && renderer instanceof AbstractRenderer ?
                    ((AbstractRenderer) renderer).getLastYLineRecursively() : null;
            return (yLine == null ?
                    renderer.getOccupiedArea().getBBox().getBottom() : (float) yLine - (leading / 2));
        }
    }
}
