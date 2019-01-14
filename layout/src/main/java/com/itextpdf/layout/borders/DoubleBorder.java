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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Creates a double border around the element it's set to. The space between the two border lines has
 * the same width as the two borders. If a background has been set on the element the color will show in
 * between the two borders.
 */
public class DoubleBorder extends Border {

    /**
     * Creates a DoubleBorder with the specified width for both the two borders as the space in between them.
     * The color is set to the default: black.
     *
     * @param width width of the borders and the space between them
     */
    public DoubleBorder(float width) {
        super(width);
    }

    /**
     * Creates a DoubleBorder with the specified width for both the two borders as the space in between them and
     * the specified color for the two borders. The space in between the two borders is either colorless or will
     * be filled with the background color of the element, if a color has been set.
     *
     * @param color The color of the borders
     * @param width The width of the borders and the space between them
     */
    public DoubleBorder(Color color, float width) {
        super(color, width);
    }

    /**
     * Creates a DoubleBorder with the specified width for both the two borders as the space in between them and
     * the specified color for the two borders. The space in between the two borders is either colorless or will
     * be filled with the background color of the element, if a color has been set.
     *
     * @param color     The color of the borders
     * @param width     The width of the borders and the space between them
     * @param opacity   The opacity
     */
    public DoubleBorder(Color color, float width, float opacity) {
        super(color, width, opacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return Border.DOUBLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide, float borderWidthBefore, float borderWidthAfter) {
        float x3 = 0, y3 = 0;
        float x4 = 0, y4 = 0;
        float thirdOfWidth = width / 3;
        float thirdOfWidthBefore = borderWidthBefore / 3;
        float thirdOfWidthAfter = borderWidthAfter / 3;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2, defaultSide);

        switch (borderSide) {
            case TOP:
                x3 = x2 + thirdOfWidthAfter;
                y3 = y2 + thirdOfWidth;
                x4 = x1 - thirdOfWidthBefore;
                y4 = y1 + thirdOfWidth;
                break;
            case RIGHT:
                x3 = x2 + thirdOfWidth;
                y3 = y2 - thirdOfWidthAfter;
                x4 = x1 + thirdOfWidth;
                y4 = y1 + thirdOfWidthBefore;
                break;
            case BOTTOM:
                x3 = x2 - thirdOfWidthAfter;
                y3 = y2 - thirdOfWidth;
                x4 = x1 + thirdOfWidthBefore;
                y4 = y1 - thirdOfWidth;
                break;
            case LEFT:
                x3 = x2 - thirdOfWidth;
                y3 = y2 + thirdOfWidthAfter;
                x4 = x1 - thirdOfWidth;
                y4 = y1 - thirdOfWidthBefore;
                break;
        }

        canvas.saveState()
                .setFillColor(transparentColor.getColor());
        transparentColor.applyFillTransparency(canvas);
        canvas
                .moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill();

        switch (borderSide) {
            case TOP:
                x2 += 2 * thirdOfWidthAfter;
                y2 += 2 * thirdOfWidth;
                x3 += 2 * thirdOfWidthAfter;
                y3 += 2 * thirdOfWidth;
                x4 -= 2 * thirdOfWidthBefore;
                y4 += 2 * thirdOfWidth;
                x1 -= 2 * thirdOfWidthBefore;
                y1 += 2 * thirdOfWidth;
                break;
            case RIGHT:
                x2 += 2 * thirdOfWidth;
                y2 -= 2 * thirdOfWidthAfter;
                x3 += 2 * thirdOfWidth;
                y3 -= 2 * thirdOfWidthAfter;
                x4 += 2 * thirdOfWidth;
                y4 += 2 * thirdOfWidthBefore;
                x1 += 2 * thirdOfWidth;
                y1 += 2 * thirdOfWidthBefore;
                break;
            case BOTTOM:
                x2 -= 2 * thirdOfWidthAfter;
                y2 -= 2 * thirdOfWidth;
                x3 -= 2 * thirdOfWidthAfter;
                y3 -= 2 * thirdOfWidth;
                x4 += 2 * thirdOfWidthBefore;
                y4 -= 2 * thirdOfWidth;
                x1 += 2 * thirdOfWidthBefore;
                y1 -= 2 * thirdOfWidth;
                break;
            case LEFT:
                x2 -= 2 * thirdOfWidth;
                y2 += 2 * thirdOfWidthAfter;
                x3 -= 2 * thirdOfWidth;
                y3 += 2 * thirdOfWidthAfter;
                x4 -= 2 * thirdOfWidth;
                y4 -= 2 * thirdOfWidthBefore;
                x1 -= 2 * thirdOfWidth;
                y1 -= 2 * thirdOfWidthBefore;
                break;
        }

        canvas.moveTo(x1, y1).lineTo(x2, y2).lineTo(x3, y3).lineTo(x4, y4).lineTo(x1, y1).fill()
                .restoreState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide) {
        float thirdOfWidth = width / 3;

        Border.Side borderSide = getBorderSide(x1, y1, x2, y2, defaultSide);

        switch (borderSide) {
            case TOP:
                y1 -= thirdOfWidth;
                y2 = y1;
                break;
            case RIGHT:
                x1 -= thirdOfWidth;
                x2 -= thirdOfWidth;
                y1 += thirdOfWidth;
                y2 -= thirdOfWidth;
                break;
            case BOTTOM:
                break;
            case LEFT:
                break;
        }

        canvas.
                saveState().
                setLineWidth(thirdOfWidth).
                setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas.
                moveTo(x1, y1).
                lineTo(x2, y2).
                stroke().
                restoreState();

        switch (borderSide) {
            case TOP:
//                x1 -= 2*thirdOfWidth;
                y2 += 2 * thirdOfWidth;
                y1 += 2 * thirdOfWidth;
                break;
            case RIGHT:
                x2 += 2 * thirdOfWidth;
                x1 += 2 * thirdOfWidth;
//                y1 -= 2*thirdOfWidth;
                break;
            case BOTTOM:
                x2 -= 2 * thirdOfWidth;
                y2 -= 2 * thirdOfWidth;
                x1 += 2 * thirdOfWidth;
                y1 -= 2 * thirdOfWidth;
                break;
            case LEFT:
                y2 += 2 * thirdOfWidth;
                x1 -= 2 * thirdOfWidth;
                y1 -= 2 * thirdOfWidth;
                break;
        }

        canvas.
                saveState().
                setLineWidth(thirdOfWidth).
                setStrokeColor(transparentColor.getColor());
        transparentColor.applyStrokeTransparency(canvas);
        canvas.
                moveTo(x1, y1).
                lineTo(x2, y2).
                stroke().
                restoreState();
    }
}
