/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;

/**
 * Representation of a Pattern Color.
 */
public class PatternColor extends Color {

    private PdfPattern pattern;
    // The underlying color for uncolored patterns. Will be null for colored ones.
    private Color underlyingColor;

    /**
     * Creates a pattern color using the given color pattern object.
     *
     * @param coloredPattern Color space that uses pattern objects
     */
    public PatternColor(PdfPattern coloredPattern) {
        super(new PdfSpecialCs.Pattern(), null);
        this.pattern = coloredPattern;
    }

    /**
     * Creates a pattern color using the given uncolored pattern object and color.
     *
     * @param uncoloredPattern Tiling pattern object of the color space
     * @param color            Color object
     */
    public PatternColor(PdfPattern.Tiling uncoloredPattern, Color color) {
        this(uncoloredPattern, color.getColorSpace(), color.getColorValue());
    }

    /**
     * Creates a pattern color using the given uncolored pattern object, an underlying color space and color values.
     *
     * @param uncoloredPattern Tiling pattern object of the color space
     * @param underlyingCS     Underlying color space object
     * @param colorValue       Color values
     */
    public PatternColor(PdfPattern.Tiling uncoloredPattern, PdfColorSpace underlyingCS, float[] colorValue) {
        this(uncoloredPattern, new PdfSpecialCs.UncoloredTilingPattern(ensureNotPatternCs(underlyingCS)), colorValue);
    }

    /**
     * Creates a pattern color using the given uncolored pattern object, uncolored tiling pattern and color values.
     *
     * @param uncoloredPattern  Tiling pattern object of the color space
     * @param uncoloredTilingCS Tiling pattern color space
     * @param colorValue        Color values
     */
    public PatternColor(PdfPattern.Tiling uncoloredPattern, PdfSpecialCs.UncoloredTilingPattern uncoloredTilingCS, float[] colorValue) {
        super(uncoloredTilingCS, colorValue);
        this.pattern = uncoloredPattern;
        this.underlyingColor = makeColor(uncoloredTilingCS.getUnderlyingColorSpace(), colorValue);
    }

    /**
     * Returns the pattern of the color space.
     *
     * @return PdfPattern object
     */
    public PdfPattern getPattern() {
        return pattern;
    }

    @Override
    public void setColorValue(float[] value) {
        super.setColorValue(value);
        underlyingColor.setColorValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        PatternColor color = (PatternColor)o;
        return pattern.equals(color.pattern) &&
                (underlyingColor != null ? underlyingColor.equals(color.underlyingColor) : color.underlyingColor == null);
    }

    private static PdfColorSpace ensureNotPatternCs(PdfColorSpace underlyingCS) {
        if (underlyingCS instanceof PdfSpecialCs.Pattern)
            throw new IllegalArgumentException("underlyingCS");
        return underlyingCS;
    }
}
