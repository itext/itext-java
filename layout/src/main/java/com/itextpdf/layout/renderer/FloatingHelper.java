/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class FloatingHelper {
    private FloatingHelper() { }

    static void adjustLineAreaAccordingToFloats(List<Rectangle> floatRendererAreas, Rectangle layoutBox) {
        adjustLayoutBoxAccordingToFloats(floatRendererAreas, layoutBox, null, 0, null);
    }

    static float adjustLayoutBoxAccordingToFloats(List<Rectangle> floatRendererAreas, Rectangle layoutBox, Float boxWidth,
                                                  float clearHeightCorrection, MarginsCollapseHandler marginsCollapseHandler) {

        float topShift = clearHeightCorrection;
        float left;
        float right;
        Rectangle[] lastLeftAndRightBoxes = null;
        do {
            if (lastLeftAndRightBoxes != null) {
                float bottomLeft = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getBottom() : Float.MAX_VALUE;
                float bottomRight = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getBottom() : Float.MAX_VALUE;
                float updatedHeight = Math.min(bottomLeft, bottomRight) - layoutBox.getY();
                topShift = layoutBox.getHeight() - updatedHeight;
            }
            List<Rectangle> boxesAtYLevel = getBoxesAtYLevel(floatRendererAreas, layoutBox.getTop() - topShift);
            if (boxesAtYLevel.isEmpty()) {
                applyClearance(layoutBox, marginsCollapseHandler, topShift, false);
                return topShift;
            }

            lastLeftAndRightBoxes = findLastLeftAndRightBoxes(layoutBox, boxesAtYLevel);
            left = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getRight() : Float.MIN_VALUE;
            right = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getLeft() : Float.MAX_VALUE;

            if (left > right || left > layoutBox.getRight() || right < layoutBox.getLeft()) {
                left = layoutBox.getLeft();
                right = left;
            } else {
                if (right > layoutBox.getRight()) {
                    right = layoutBox.getRight();
                }
                if (left < layoutBox.getLeft()) {
                    left = layoutBox.getLeft();
                }
            }
        } while (boxWidth != null && boxWidth > right - left);

        if (layoutBox.getWidth() > right - left) {
            layoutBox.setX(left).setWidth(right - left);
        }

        applyClearance(layoutBox, marginsCollapseHandler, topShift, false);
        return topShift;
    }

    static Float calculateLineShiftUnderFloats(List<Rectangle> floatRendererAreas, Rectangle layoutBox) {
        List<Rectangle> boxesAtYLevel = getBoxesAtYLevel(floatRendererAreas, layoutBox.getTop());
        if (boxesAtYLevel.isEmpty()) {
            return null;
        }

        Rectangle[] lastLeftAndRightBoxes = findLastLeftAndRightBoxes(layoutBox, boxesAtYLevel);
        float left = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getRight() : layoutBox.getLeft();
        float right = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getLeft() : layoutBox.getRight();
        if (layoutBox.getLeft() < left || layoutBox.getRight() > right) {
            float maxLastFloatBottom;
            if (lastLeftAndRightBoxes[0] != null && lastLeftAndRightBoxes[1] != null) {
                maxLastFloatBottom = Math.max(lastLeftAndRightBoxes[0].getBottom(), lastLeftAndRightBoxes[1].getBottom());
            } else if (lastLeftAndRightBoxes[0] != null) {
                maxLastFloatBottom = lastLeftAndRightBoxes[0].getBottom();
            } else {
                maxLastFloatBottom = lastLeftAndRightBoxes[1].getBottom();
            }

            return layoutBox.getTop() - maxLastFloatBottom + AbstractRenderer.EPS;
        }
        return null;
    }

    static void adjustFloatedTableLayoutBox(TableRenderer tableRenderer, Rectangle layoutBox, float tableWidth, List<Rectangle> floatRendererAreas, FloatPropertyValue floatPropertyValue) {
        tableRenderer.setProperty(Property.HORIZONTAL_ALIGNMENT, null);
        UnitValue[] margins = tableRenderer.getMargins();
        if (!margins[1].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(FloatingHelper.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_RIGHT));
        }
        if (!margins[3].isPointValue()) {
            Logger logger = LoggerFactory.getLogger(FloatingHelper.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_LEFT));
        }
        adjustBlockAreaAccordingToFloatRenderers(floatRendererAreas, layoutBox, tableWidth + margins[1].getValue() + margins[3].getValue(), FloatPropertyValue.LEFT.equals(floatPropertyValue));
    }

    static Float adjustFloatedBlockLayoutBox(AbstractRenderer renderer, Rectangle parentBBox, Float blockWidth, List<Rectangle> floatRendererAreas, FloatPropertyValue floatPropertyValue, OverflowPropertyValue overflowX) {
        renderer.setProperty(Property.HORIZONTAL_ALIGNMENT, null);

        float floatElemWidth;
        boolean overflowFit = AbstractRenderer.isOverflowFit(overflowX);
        if (blockWidth != null) {
            floatElemWidth = (float)blockWidth + AbstractRenderer.calculateAdditionalWidth(renderer);
            if (overflowFit && floatElemWidth > parentBBox.getWidth()) {
                floatElemWidth = parentBBox.getWidth();
            }
        } else {
            MinMaxWidth minMaxWidth = calculateMinMaxWidthForFloat(renderer, floatPropertyValue);

            float maxWidth = minMaxWidth.getMaxWidth();
            if (maxWidth > parentBBox.getWidth()) {
                maxWidth = parentBBox.getWidth();
            }
            if (!overflowFit && minMaxWidth.getMinWidth() > parentBBox.getWidth()) {
                maxWidth = minMaxWidth.getMinWidth();
            }
            floatElemWidth = maxWidth + AbstractRenderer.EPS;
            blockWidth = maxWidth - minMaxWidth.getAdditionalWidth() + AbstractRenderer.EPS;
        }

        adjustBlockAreaAccordingToFloatRenderers(floatRendererAreas, parentBBox, floatElemWidth, FloatPropertyValue.LEFT.equals(floatPropertyValue));
        return blockWidth;
    }

    // Float boxes shall be ordered by addition; No zero-width boxes shall be in the list.
    private static void adjustBlockAreaAccordingToFloatRenderers(List<Rectangle> floatRendererAreas, Rectangle layoutBox, float blockWidth, boolean isFloatLeft) {
        if (floatRendererAreas.isEmpty()) {
            if (!isFloatLeft) {
                adjustBoxForFloatRight(layoutBox, blockWidth);
            }
            return;
        }

        float currY;
        if (floatRendererAreas.get(floatRendererAreas.size() - 1).getTop() < layoutBox.getTop()) {
            currY = floatRendererAreas.get(floatRendererAreas.size() - 1).getTop();
        } else {
            // e.g. if clear was applied on float and current top of layoutBox is lower than last float renderer
            currY = layoutBox.getTop();
        }
        Rectangle[] lastLeftAndRightBoxes = null;
        float left = 0;
        float right = 0;
        while (lastLeftAndRightBoxes == null || right - left < blockWidth) {
            if (lastLeftAndRightBoxes != null) {
                if (isFloatLeft) {
                    currY = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getBottom() : lastLeftAndRightBoxes[1].getBottom();
                } else {
                    currY = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getBottom() : lastLeftAndRightBoxes[0].getBottom();
                }
            }
            layoutBox.setHeight(currY - layoutBox.getY());
            List<Rectangle> yLevelBoxes = getBoxesAtYLevel(floatRendererAreas, currY);
            if (yLevelBoxes.isEmpty()) {
                if (!isFloatLeft) {
                    adjustBoxForFloatRight(layoutBox, blockWidth);
                }
                return;
            }
            lastLeftAndRightBoxes = findLastLeftAndRightBoxes(layoutBox, yLevelBoxes);
            left = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getRight() : layoutBox.getLeft();
            right = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getLeft() : layoutBox.getRight();
        }

        layoutBox.setX(left);
        layoutBox.setWidth(right - left);

        if (!isFloatLeft) {
            adjustBoxForFloatRight(layoutBox, blockWidth);
        }
    }

    static void removeFloatsAboveRendererBottom(List<Rectangle> floatRendererAreas, IRenderer renderer) {
        if (!isRendererFloating(renderer)) {
            float bottom = renderer.getOccupiedArea().getBBox().getBottom();
            for (int i = floatRendererAreas.size() - 1; i >= 0; i--) {
                if (floatRendererAreas.get(i).getBottom() >= bottom) {
                    floatRendererAreas.remove(i);
                }
            }
        }
    }

    static LayoutArea adjustResultOccupiedAreaForFloatAndClear(IRenderer renderer, List<Rectangle> floatRendererAreas,
                                                               Rectangle parentBBox, float clearHeightCorrection, boolean marginsCollapsingEnabled) {
        LayoutArea occupiedArea = renderer.getOccupiedArea();
        LayoutArea editedArea = occupiedArea;
        if (isRendererFloating(renderer)) {
            editedArea = occupiedArea.clone();
            if (occupiedArea.getBBox().getWidth() > 0) {
                floatRendererAreas.add(occupiedArea.getBBox());
            }
            editedArea.getBBox().setY(parentBBox.getTop());
            editedArea.getBBox().setHeight(0);
        } else if (clearHeightCorrection > 0 && !marginsCollapsingEnabled) {
            editedArea = occupiedArea.clone();
            editedArea.getBBox().increaseHeight(clearHeightCorrection);
        }

        return editedArea;
    }

    static void includeChildFloatsInOccupiedArea(List<Rectangle> floatRendererAreas, IRenderer renderer, Set<Rectangle> nonChildFloatingRendererAreas) {
        Rectangle commonRectangle = includeChildFloatsInOccupiedArea(floatRendererAreas, renderer.getOccupiedArea().getBBox(), nonChildFloatingRendererAreas);
        renderer.getOccupiedArea().setBBox(commonRectangle);
    }

    static Rectangle includeChildFloatsInOccupiedArea(List<Rectangle> floatRendererAreas, Rectangle occupiedAreaBbox, Set<Rectangle> nonChildFloatingRendererAreas) {
        for (Rectangle floatBox : floatRendererAreas) {
            if (nonChildFloatingRendererAreas.contains(floatBox)) {
                // Currently there is no other way to distinguish floats that are not descendants of this renderer
                // except by preserving a set of such.
                continue;
            }
            occupiedAreaBbox = Rectangle.getCommonRectangle(occupiedAreaBbox, floatBox);
        }
        return occupiedAreaBbox;
    }

    static MinMaxWidth calculateMinMaxWidthForFloat(AbstractRenderer renderer, FloatPropertyValue floatPropertyVal) {
        boolean floatPropIsRendererOwn = renderer.hasOwnProperty(Property.FLOAT);
        renderer.setProperty(Property.FLOAT, FloatPropertyValue.NONE);
        MinMaxWidth kidMinMaxWidth = renderer.getMinMaxWidth();
        if (floatPropIsRendererOwn) {
            renderer.setProperty(Property.FLOAT, floatPropertyVal);
        } else {
            renderer.deleteOwnProperty(Property.FLOAT);
        }
        return kidMinMaxWidth;
    }

    static float calculateClearHeightCorrection(IRenderer renderer, List<Rectangle> floatRendererAreas, Rectangle parentBBox) {
        ClearPropertyValue clearPropertyValue = renderer.<ClearPropertyValue>getProperty(Property.CLEAR);
        float clearHeightCorrection = 0;
        if (clearPropertyValue == null || floatRendererAreas.isEmpty()) {
            return clearHeightCorrection;
        }

        float currY;
        if (floatRendererAreas.get(floatRendererAreas.size() - 1).getTop() < parentBBox.getTop()) {
            currY = floatRendererAreas.get(floatRendererAreas.size() - 1).getTop();
        } else {
            currY = parentBBox.getTop();
        }

        List<Rectangle> boxesAtYLevel = getBoxesAtYLevel(floatRendererAreas, currY);
        Rectangle[] lastLeftAndRightBoxes = findLastLeftAndRightBoxes(parentBBox, boxesAtYLevel);
        float lowestFloatBottom = Float.MAX_VALUE;
        boolean isBoth = clearPropertyValue.equals(ClearPropertyValue.BOTH);
        if ((clearPropertyValue.equals(ClearPropertyValue.LEFT) || isBoth) && lastLeftAndRightBoxes[0] != null) {
            for (Rectangle floatBox : floatRendererAreas) {
                if (floatBox.getBottom() < lowestFloatBottom && floatBox.getLeft() <= lastLeftAndRightBoxes[0].getLeft()) {
                    lowestFloatBottom = floatBox.getBottom();
                }
            }
        }
        if ((clearPropertyValue.equals(ClearPropertyValue.RIGHT) || isBoth) && lastLeftAndRightBoxes[1] != null) {
            for (Rectangle floatBox : floatRendererAreas) {
                if (floatBox.getBottom() < lowestFloatBottom && floatBox.getRight() >= lastLeftAndRightBoxes[1].getRight()) {
                    lowestFloatBottom = floatBox.getBottom();
                }
            }
        }
        if (lowestFloatBottom < Float.MAX_VALUE) {
            clearHeightCorrection = parentBBox.getTop() - lowestFloatBottom + AbstractRenderer.EPS;
        }

        return clearHeightCorrection;
    }

    static void applyClearance(Rectangle layoutBox, MarginsCollapseHandler marginsCollapseHandler, float clearHeightAdjustment, boolean isFloat) {
        if (clearHeightAdjustment <= 0) {
            return;
        }

        if (marginsCollapseHandler == null || isFloat) {
            layoutBox.decreaseHeight(clearHeightAdjustment);
        } else {
            marginsCollapseHandler.applyClearance(clearHeightAdjustment);
        }
    }

    static boolean isRendererFloating(IRenderer renderer) {
        return isRendererFloating(renderer, renderer.<FloatPropertyValue>getProperty(Property.FLOAT));
    }

    static boolean isRendererFloating(IRenderer renderer, FloatPropertyValue kidFloatPropertyVal) {
        Integer position = renderer.<Integer>getProperty(Property.POSITION);
        boolean notAbsolutePos = position == null || position != LayoutPosition.ABSOLUTE;
        return notAbsolutePos && kidFloatPropertyVal != null && !kidFloatPropertyVal.equals(FloatPropertyValue.NONE);
    }

    static boolean isClearanceApplied(List<IRenderer> floatingRenderers, ClearPropertyValue clearPropertyValue) {
        if (clearPropertyValue == null || clearPropertyValue.equals(ClearPropertyValue.NONE)) {
            return false;
        }
        for (IRenderer floatingRenderer : floatingRenderers) {
            FloatPropertyValue floatPropertyValue = floatingRenderer.<FloatPropertyValue>getProperty(Property.FLOAT);

            if (clearPropertyValue.equals(ClearPropertyValue.BOTH)
                    || (floatPropertyValue.equals(FloatPropertyValue.LEFT) && clearPropertyValue.equals(ClearPropertyValue.LEFT))
                    || (floatPropertyValue.equals(FloatPropertyValue.RIGHT) && clearPropertyValue.equals(ClearPropertyValue.RIGHT))) {
                return true;
            }
        }
        return false;
    }

    static void removeParentArtifactsOnPageSplitIfOnlyFloatsOverflow(IRenderer overflowRenderer) {
        overflowRenderer.setProperty(Property.BACKGROUND, null);
        overflowRenderer.setProperty(Property.BACKGROUND_IMAGE, null);
        overflowRenderer.setProperty(Property.OUTLINE, null);

        Border[] borders = AbstractRenderer.getBorders(overflowRenderer);
        overflowRenderer.setProperty(Property.BORDER_TOP, null);
        overflowRenderer.setProperty(Property.BORDER_BOTTOM, null);
        if (borders[1] != null) {
            overflowRenderer.setProperty(Property.BORDER_RIGHT, new SolidBorder(ColorConstants.BLACK, borders[1].getWidth(), 0));
        }
        if (borders[3] != null) {
            overflowRenderer.setProperty(Property.BORDER_LEFT, new SolidBorder(ColorConstants.BLACK, borders[3].getWidth(), 0));
        }

        overflowRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(0));
        overflowRenderer.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(0));
        overflowRenderer.setProperty(Property.PADDING_TOP, UnitValue.createPointValue(0));
        overflowRenderer.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(0));

    }

    private static void adjustBoxForFloatRight(Rectangle layoutBox, float blockWidth) {
        layoutBox.setX(layoutBox.getRight() - blockWidth);
        layoutBox.setWidth(blockWidth);
    }

    private static Rectangle[] findLastLeftAndRightBoxes(Rectangle layoutBox, List<Rectangle> yLevelBoxes) {
        Rectangle lastLeftFloatAtY = null;
        Rectangle lastRightFloatAtY = null;
        float left = layoutBox.getLeft();
        for (Rectangle box : yLevelBoxes) {
            if (box.getLeft() < left) {
                left = box.getLeft();
            }
        }
        for (Rectangle box : yLevelBoxes) {
            if (left >= box.getLeft() && left < box.getRight()) {
                lastLeftFloatAtY = box;
                left = box.getRight();
            } else {
                lastRightFloatAtY = box;
            }
        }

        return new Rectangle[] {lastLeftFloatAtY, lastRightFloatAtY};
    }

    private static List<Rectangle> getBoxesAtYLevel(List<Rectangle> floatRendererAreas, float currY) {
        List<Rectangle> yLevelBoxes = new ArrayList<>();
        for (Rectangle box : floatRendererAreas) {
            if (box.getBottom() + AbstractRenderer.EPS < currY && box.getTop() + AbstractRenderer.EPS >= currY) {
                yLevelBoxes.add(box);
            }
        }
        return yLevelBoxes;
    }
}
