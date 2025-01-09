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
package com.itextpdf.layout.properties;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;

import java.util.Objects;

/**
 * Represents a color with the specified opacity.
 */
public class TransparentColor {
    private Color color;
    private float opacity;

    /**
     * Creates a new {@link TransparentColor} instance of certain fully opaque color.
     *
     * @param color the {@link Color} of the created {@link TransparentColor} object
     */
    public TransparentColor(Color color) {
        this.color = color;
        this.opacity = 1f;
    }

    /**
     * Creates a new {@link TransparentColor}.
     *
     * @param color the {@link Color} of the created {@link TransparentColor} object
     * @param opacity a float defining the opacity of the color; a float between 0 and 1,
     *                where 1 stands for fully opaque color and 0 - for fully transparent
     */
    public TransparentColor(Color color, float opacity) {
        this.color = color;
        this.opacity = opacity;
    }

    /**
     * Gets the color.
     * @return a {@link Color}
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the opacity of color.
     * @return a float between 0 and 1, where 1 stands for fully opaque color and 0 - for fully transparent
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * Sets the opacity value for <b>non-stroking</b> operations in the transparent imaging model.
     * @param canvas the {@link PdfCanvas} to be written to
     */
    public void applyFillTransparency(PdfCanvas canvas) {
        applyTransparency(canvas, false);
    }

    /**
     * Sets the opacity value for <b>stroking</b> operations in the transparent imaging model.
     * @param canvas the {@link PdfCanvas} to be written to
     */
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
