package com.itextpdf.model.border;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;

public abstract class Border {

    public static final Border NO_BORDER = null;
    public static final int SOLID = 0;
    public static final int DASHED = 1;
    public static final int DOTTED = 2;
    public static final int DOUBLE = 3;
    public static final int ROUND_DOTS = 4;
    public static final int _3D = 5;

    protected Color color;
    protected float width;
    protected int type;
    private int hash;

    public Border(float width) {
        this(Color.BLACK, width);
    }

    public Border(Color color, float width) {
        this.color = color;
        this.width = width;
    }

    /**
     * <p>
     * All borders are supposed to be drawn in such way, that inner content of the element is on the right from the
     * drawing direction. Borders are drawn in this order: top, right, bottom, left.
     * </p>
     * <p>
     * Given points specify the line which lies on the border of the content area,
     * therefore the border itself should be drawn to the left from the drawing direction.
     * </p>
     * <p>
     * <code>borderWidthBefore</code> and <code>borderWidthAfter</code> parameters are used to
     * define the widths of the borders that are before and after the current border, e.g. for
     * the bottom border, <code>borderWidthBefore</code> specifies width of the right border and
     * <code>borderWidthAfter</code> - width of the left border. Those width are used to handle areas
     * of border joins.
     * </p>
     * @param canvas PdfCanvas to be written to
     * @param x1 x coordinate of the beginning point of the element side, that should be bordered
     * @param y1 y coordinate of the beginning point of the element side, that should be bordered
     * @param x2 x coordinate of the ending point of the element side, that should be bordered
     * @param y2 y coordinate of the ending point of the element side, that should be bordered
     * @param borderWidthBefore defines width of the border that is before the current one
     * @param borderWidthAfter defines width of the border that is after the current one
     */
    public abstract void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, float borderWidthBefore, float borderWidthAfter);

    public abstract void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2);

    public abstract int getType();

    public Color getColor() {
        return color;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Border) {
            Border anotherBorder = (Border) anObject;
            if (anotherBorder.getType() != getType() || anotherBorder.getColor() != getColor() || anotherBorder.getWidth() != getWidth()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = hash;

        if (h == 0) {
            h = (int) getWidth() * 31 + getColor().hashCode();
            hash = h;
        }

        return h;
    }

    protected Side getBorderSide(float x1, float y1, float x2, float y2) {
        boolean isLeft = false;
        boolean isRight = false;
        if (Math.abs(y2 - y1) > 0.0005f) {
            isLeft = y2 - y1 > 0;
            isRight = y2 - y1 < 0;
        }

        boolean isTop = false;
        boolean isBottom = false;
        if (Math.abs(x2-x1) > 0.0005f) {
            isTop = x2 - x1 > 0;
            isBottom = x2 - x1 < 0;
        }

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
