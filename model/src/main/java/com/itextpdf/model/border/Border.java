package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;

public abstract class Border {


    protected Color color;
    protected float width;

    public Border(Color color, float width) {
        this.color = color;
        this.width = width;
    }

    /**
     * <p>
     * All borders are supposed to be drawn in such way, that inner content of the element is on the right from the
     * drawing direction.
     * </p>
     * <p>
     * Given points specify the line which lies on the border of the content area,
     * therefore the border itself should be drawn to the left from the drawing direction.
     * </p>
     * <p>
     * <code>joinAreaBefore</code> and <code>joinAreaAfter</code> parameters are used to
     * define the length of the areas where border is colliding other borders.
     * </p>
     * @param canvas PdfCanvas to be written to
     * @param x1 x coordinate of the beginning point of the element side, that should be bordered
     * @param y1 y coordinate of the beginning point of the element side, that should be bordered
     * @param x2 x coordinate of the ending point of the element side, that should be bordered
     * @param y2 y coordinate of the ending point of the element side, that should be bordered
     * @param joinAreaBefore defines length of the borders colliding area before the border
     * @param joinAreaAfter defines length of the borders colliding area after the border
     */
    public abstract void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float joinAreaBefore, float joinAreaAfter);

    public Color getColor() {
        return color;
    }

    public float getWidth() {
        return width;
    }

    protected Side getBorderSide(float x1, float y1, float x2, float y2) {
        boolean isTop = x2 - x1 > 0;
        boolean isRight = y2 - y1 < 0;
        boolean isBottom = x2 - x1 < 0;
        boolean isLeft = y2 - y1 > 0;

        if (isTop) {
            return Side.TOP;
        } else if (isRight) {
            return Side.RIGHT;
        } else if (isBottom) {
            return Side.BOTTOM;
        } else if (isLeft) {
            return Side.LEFT;
        }

        return Side.NONE;
    }

    protected enum Side {NONE, TOP, RIGHT, BOTTOM, LEFT}
}
