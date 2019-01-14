/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.borders;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Represents a border that is displayed using a 3D effect.
 */
public abstract class Border3D extends Border {

    /**
     * Predefined gray {@link DeviceRgb RGB-color}
     */
    private static final DeviceRgb GRAY = new DeviceRgb(212, 208, 200);

    /**
     * Creates a Border3D instance with the specified width. Also sets the color to gray.
     *
     * @param width with of the border
     */
    protected Border3D(float width) {
        this(GRAY, width);
    }

    /**
     * Creates a Border3D instance with the specified width and color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    protected Border3D(DeviceRgb color, float width) {
        super(color, width);
    }

    /**
     * Creates a Border3D instance with the specified width and color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    protected Border3D(DeviceCmyk color, float width) {
        super(color, width);
    }

    /**
     * Creates a Border3D instance with the specified width and color.
     *
     * @param color color of the border
     * @param width width of the border
     */
    protected Border3D(DeviceGray color, float width) {
        super(color, width);
    }

    /**
     * Creates a Border3D instance with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity opacity of the border
     */
    protected Border3D(DeviceRgb color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * Creates a Border3D instance with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity opacity of the border
     */
    protected Border3D(DeviceCmyk color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * Creates a Border3D instance with the specified width, color and opacity.
     *
     * @param color   color of the border
     * @param width   width of the border
     * @param opacity opacity of the border
     */
    protected Border3D(DeviceGray color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;
        float widthHalf = width / 2;
        float halfOfWidthBefore = borderWidthBefore / 2;
        float halfOfWidthAfter = borderWidthAfter / 2;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2, defaultSide);
        switch (borderSide) {
            case TOP:
                x3 = x2 + halfOfWidthAfter;
                y3 = y2 + widthHalf;
                x4 = x1 - halfOfWidthBefore;
                y4 = y1 + widthHalf;
                break;
            case RIGHT:
                x3 = x2 + widthHalf;
                y3 = y2 - halfOfWidthAfter;
                x4 = x1 + widthHalf;
                y4 = y1 + halfOfWidthBefore;
                break;
            case BOTTOM:
                x3 = x2 - halfOfWidthAfter;
                y3 = y2 - widthHalf;
                x4 = x1 + halfOfWidthBefore;
                y4 = y1 - widthHalf;
                break;
            case LEFT:
                x3 = x2 - widthHalf;
                y3 = y2 + halfOfWidthAfter;
                x4 = x1 - widthHalf;
                y4 = y1 - halfOfWidthBefore;
                break;
        }

        canvas.saveState();
        transparentColor.applyFillTransparency(canvas);
        setInnerHalfColor(canvas, borderSide);
        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();

        switch (borderSide) {
            case TOP:
                x2 += borderWidthAfter;
                y2 += width;
                x1 -= borderWidthBefore;
                y1 += width;
                break;
            case RIGHT:
                x2 += width;
                y2 -= borderWidthAfter;
                x1 += width;
                y1 += borderWidthBefore;
                break;
            case BOTTOM:
                x2 -= borderWidthAfter;
                y2 -= width;
                x1 += borderWidthBefore;
                y1 -= width;
                break;
            case LEFT:
                x2 -= width;
                y2 += borderWidthAfter;
                x1 -= width;
                y1 -= borderWidthBefore;
                break;
        }

        setOuterHalfColor(canvas, borderSide);
        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();
        canvas.restoreState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide) {
        canvas.
                saveState().
                setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas.
                setLineWidth(width).
                moveTo(x1, y1).
                lineTo(x2, y2).
                stroke().
                restoreState();
    }

    /**
     * Makes the {@link Border#transparentColor} color of the border darker and returns the result
     * @return The darker color
     */
    protected Color getDarkerColor() {
        Color color = this.transparentColor.getColor();
        if (color instanceof DeviceRgb)
            return DeviceRgb.makeDarker((DeviceRgb) color);
        else if (color instanceof DeviceCmyk)
            return DeviceCmyk.makeDarker((DeviceCmyk) color);
        else if (color instanceof DeviceGray)
            return DeviceGray.makeDarker((DeviceGray) color);

        return color;
    }

    /**
     * Sets the fill color for the inner half of {@link Border3D 3D Border}
     *
     * @param canvas PdfCanvas the color will be applied on
     * @param side   the {@link Border.Side side} the color will be applied on
     */
    protected abstract void setInnerHalfColor(PdfCanvas canvas, Side side);

    /**
     * Sets the fill color for the outer half of {@link Border3D 3D Border}
     *
     * @param canvas PdfCanvas the color will be applied on
     * @param side   the {@link Border.Side side} the color will be applied on
     */
    protected abstract void setOuterHalfColor(PdfCanvas canvas, Side side);
}
