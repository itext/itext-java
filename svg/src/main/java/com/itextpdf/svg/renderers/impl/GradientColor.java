/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

/**
 * Represents a gradient color with an optional opacity mask color.
 * It's a container for 2 colors and uses inheritance only to be "compatible" with {@link Color} class.
 */
final class GradientColor extends Color {
    private static final String UNSUPPORTED_OPERATION =
            "This class is a container for 2 colors, do not use the methods from a parent class";
    private final Color color;
    private final Color opacityColor;

    GradientColor(Color color, Color opacityColor) {
        super(color.getColorSpace(), color.getColorValue());
        this.color = color;
        this.opacityColor = opacityColor;
    }

    @Override
    public int getNumberOfComponents() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public PdfColorSpace getColorSpace() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public float[] getColorValue() {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    @Override
    public void setColorValue(float[] value) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION);
    }

    Color getColor() {
        return color;
    }

    Color getOpacityColor() {
        return opacityColor;
    }

    @Override
    public int hashCode() {
        return 31 * color.hashCode() + opacityColor.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GradientColor that = (GradientColor) o;
        return color.equals(that.color) && opacityColor.equals(that.opacityColor);
    }
}

