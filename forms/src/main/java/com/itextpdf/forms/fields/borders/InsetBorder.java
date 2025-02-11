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
package com.itextpdf.forms.fields.borders;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.SolidBorder;

class InsetBorder extends AbstractFormBorder {

    public InsetBorder(Color color, float width) {
        super(color, width);
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
        if (Side.BOTTOM.equals(defaultSide)) {
            solidBorder = new SolidBorder(ColorConstants.LIGHT_GRAY, borderWidth);
            solidBorder.draw(canvas, x1 - borderWidth, borderWidthX2, borderWidthX2, borderWidthX2,
                    Side.BOTTOM, borderWidth, borderWidth);
        } else if (Side.TOP.equals(defaultSide)) {
            solidBorder = new SolidBorder(ColorConstants.DARK_GRAY, borderWidth);
            solidBorder.draw(canvas, borderWidthX2, y1 - borderWidth, x2 - borderWidth,
                    y2 - borderWidth, Side.TOP, borderWidth, borderWidth);
        } else if (Side.LEFT.equals(defaultSide)) {
            solidBorder = new SolidBorder(ColorConstants.DARK_GRAY, borderWidth);
            solidBorder.draw(canvas, borderWidthX2, borderWidthX2, borderWidthX2, y2 - borderWidth,
                    Side.LEFT, borderWidth, borderWidth);
        } else if (Side.RIGHT.equals(defaultSide)) {
            solidBorder = new SolidBorder(ColorConstants.LIGHT_GRAY, borderWidth);
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
        return AbstractFormBorder.FORM_INSET;
    }
}
