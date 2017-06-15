package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutPosition;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.ClearPropertyValue;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.Property;
import java.util.ArrayList;
import java.util.List;

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
                applyClearance(layoutBox, marginsCollapseHandler, topShift ,false);
                return topShift;
            }

            lastLeftAndRightBoxes = findLastLeftAndRightBoxes(layoutBox, boxesAtYLevel);
            left = lastLeftAndRightBoxes[0] != null ? lastLeftAndRightBoxes[0].getRight() : layoutBox.getLeft();
            right = lastLeftAndRightBoxes[1] != null ? lastLeftAndRightBoxes[1].getLeft() : layoutBox.getRight();

        } while (boxWidth != null && boxWidth > right - left);

        if (layoutBox.getLeft() < left) {
            layoutBox.setX(left);
        }
        if (layoutBox.getRight() > right && layoutBox.getLeft() <= right) {
            layoutBox.setWidth(right - layoutBox.getLeft());
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

            return layoutBox.getTop() - maxLastFloatBottom;
        }
        return null;
    }

    static void adjustFloatedTableLayoutBox(TableRenderer tableRenderer, Rectangle layoutBox, Float tableWidth, List<Rectangle> floatRendererAreas, FloatPropertyValue floatPropertyValue) {
        tableRenderer.setProperty(Property.HORIZONTAL_ALIGNMENT, null);
        float[] margins = tableRenderer.getMargins();
        adjustBlockAreaAccordingToFloatRenderers(floatRendererAreas, layoutBox, tableWidth + margins[1] + margins[3], FloatPropertyValue.LEFT.equals(floatPropertyValue));
    }

    static Float adjustFloatedBlockLayoutBox(AbstractRenderer renderer, Rectangle parentBBox, Float blockWidth, List<Rectangle> floatRendererAreas, FloatPropertyValue floatPropertyValue) {
        renderer.setProperty(Property.HORIZONTAL_ALIGNMENT, null);
        float floatElemWidth;
        if (blockWidth != null) {
            float[] margins = renderer.getMargins();
            Border[] borders = renderer.getBorders();
            float[] paddings = renderer.getPaddings();
            float bordersWidth = (borders[1] != null ? borders[1].getWidth() : 0) + (borders[3] != null ? borders[3].getWidth() : 0);
            float additionalWidth = margins[1] + margins[3] + bordersWidth + paddings[1] + paddings[3];
            floatElemWidth = blockWidth + additionalWidth;
        } else {
            MinMaxWidth minMaxWidth = calculateMinMaxWidthForFloat(renderer, floatPropertyValue);

            float childrenMaxWidthWithEps = minMaxWidth.getChildrenMaxWidth() + AbstractRenderer.EPS;
            if (childrenMaxWidthWithEps > parentBBox.getWidth()) {
                childrenMaxWidthWithEps = parentBBox.getWidth() - minMaxWidth.getAdditionalWidth() + AbstractRenderer.EPS;
            }
            floatElemWidth = childrenMaxWidthWithEps + minMaxWidth.getAdditionalWidth();
            blockWidth = childrenMaxWidthWithEps;
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

    static void includeChildFloatsInOccupiedArea(List<Rectangle> floatRendererAreas, IRenderer renderer) {
        Rectangle bBox = renderer.getOccupiedArea().getBBox();
        float lowestFloatBottom = bBox.getBottom();
        for (Rectangle floatBox : floatRendererAreas) {
            if (floatBox.getBottom() < lowestFloatBottom) {
                lowestFloatBottom = floatBox.getBottom();
            }
        }
        bBox.setHeight(bBox.getTop() - lowestFloatBottom)
                .setY(lowestFloatBottom);
    }

    static MinMaxWidth calculateMinMaxWidthForFloat(AbstractRenderer renderer, FloatPropertyValue floatPropertyVal) {
        boolean floatPropIsRendererOwn = renderer.hasOwnProperty(Property.FLOAT);
        renderer.setProperty(Property.FLOAT, FloatPropertyValue.NONE);
        MinMaxWidth kidMinMaxWidth = renderer.getMinMaxWidth(MinMaxWidthUtils.getMax());
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
            clearHeightCorrection = parentBBox.getTop() - lowestFloatBottom;
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
            if (box.getBottom() < currY && box.getTop() >= currY) {
                yLevelBoxes.add(box);
            }
        }
        return yLevelBoxes;
    }
}
