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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.SolidBorder;

class UnderlineBorder extends AbstractFormBorder {

    public UnderlineBorder(Color color, float width) {
        super(color, width);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(PdfCanvas canvas, float x1, float y1, float x2, float y2, Side defaultSide,
            float borderWidthBefore, float borderWidthAfter) {
        if (Side.BOTTOM.equals(defaultSide)) {
            if (x1 < x2) {
                x1 = x1 - width;
                x2 = x2 + width;
            } else {
                x1 = x1 + width;
                x2 = x2 - width;
            }
            SolidBorder solidBorder = new SolidBorder(getColor(), width);
            solidBorder.draw(canvas, x1, y1, x2, y2, defaultSide, 0, 0);
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
        return AbstractFormBorder.FORM_UNDERLINE;
    }
}
