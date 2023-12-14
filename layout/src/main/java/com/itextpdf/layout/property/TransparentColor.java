/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.layout.property;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;

public class TransparentColor {
    private Color color;
    private float opacity;
    
    public TransparentColor(Color color) {
        this.color = color;
        this.opacity = 1f;
    }
    
    public TransparentColor(Color color, float opacity) {
        this.color = color;
        this.opacity = opacity;
    }

    public Color getColor() {
        return color;
    }

    public float getOpacity() {
        return opacity;
    }

    public void applyFillTransparency(PdfCanvas canvas) {
        applyTransparency(canvas, false);

    }

    public void applyStrokeTransparency(PdfCanvas canvas) {
        applyTransparency(canvas, true);        
    }

    private void applyTransparency(PdfCanvas canvas, boolean isStroke) {
        if (isTransparent()) {
            PdfExtGState extGState = new PdfExtGState();
            if (isStroke) {
                extGState.setStrokeOpacity(opacity);
            } else {
                extGState.setFillOpacity(opacity);
            }
            canvas.setExtGState(extGState);
        }
    }

    private boolean isTransparent() {
        return opacity < 1f;
    }

}
