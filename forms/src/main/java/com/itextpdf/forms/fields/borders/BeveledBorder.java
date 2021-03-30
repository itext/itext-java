/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.forms.fields.borders;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.SolidBorder;

class BeveledBorder extends AbstractFormBorder {

    private final Color backgroundColor;

    public BeveledBorder(Color color, float width, Color backgroundColor) {
        super(color, width);
        this.backgroundColor = backgroundColor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide,
            float borderWidthBefore, float borderWidthAfter) {
        SolidBorder solidBorder = new SolidBorder(getColor(), width);
        solidBorder.draw(canvas, x1, y1, x2, y2, defaultSide, borderWidthBefore, borderWidthAfter);
        float borderWidth = getWidth();
        float borderWidthX2 = borderWidth + borderWidth;
        Color darkerBackground = backgroundColor != null ? getDarkerColor(backgroundColor) : ColorConstants.LIGHT_GRAY;
        if (Side.TOP.equals(defaultSide)) {
            solidBorder = new SolidBorder(ColorConstants.WHITE, borderWidth);
            solidBorder.draw(canvas, borderWidthX2, y1 - borderWidth, x2 - borderWidth,
                    y2 - borderWidth, Side.TOP, borderWidth, borderWidth);
        } else if (Side.BOTTOM.equals(defaultSide)) {
            solidBorder = new SolidBorder(darkerBackground, borderWidth);
            solidBorder.draw(canvas, x1 - borderWidth, borderWidthX2, borderWidthX2, borderWidthX2,
                    Side.BOTTOM, borderWidth, borderWidth);
        } else if (Side.LEFT.equals(defaultSide)) {
            solidBorder = new SolidBorder(ColorConstants.WHITE, borderWidth);
            solidBorder.draw(canvas, borderWidthX2, borderWidthX2, borderWidthX2, y2 - borderWidth,
                    Side.LEFT, borderWidth, borderWidth);
        } else if (Side.RIGHT.equals(defaultSide)) {
            solidBorder = new SolidBorder(darkerBackground, borderWidth);
            solidBorder.draw(canvas, x1 - borderWidth, y1 - borderWidth,
                    x2 - borderWidth, borderWidthX2, Side.RIGHT, borderWidth, borderWidth);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawCellBorder(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getType() {
        return AbstractFormBorder.FORM_BEVELED;
    }

    private Color getDarkerColor(Color color) {
        if (color instanceof DeviceRgb) {
            return DeviceRgb.makeDarker((DeviceRgb) color);
        } else if (color instanceof DeviceCmyk) {
            return DeviceCmyk.makeDarker((DeviceCmyk) color);
        } else if (color instanceof DeviceGray) {
            return DeviceGray.makeDarker((DeviceGray) color);
        } else {
            return color;
        }
    }
}
