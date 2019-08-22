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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;

public class PatternColor extends Color {

    private static final long serialVersionUID = -2405470180325720440L;
    private PdfPattern pattern;
    // The underlying color for uncolored patterns. Will be null for colored ones.
    private Color underlyingColor;

    public PatternColor(PdfPattern coloredPattern) {
        super(new PdfSpecialCs.Pattern(), null);
        this.pattern = coloredPattern;
    }

    public PatternColor(PdfPattern.Tiling uncoloredPattern, Color color) {
        this(uncoloredPattern, color.getColorSpace(), color.getColorValue());
    }

    public PatternColor(PdfPattern.Tiling uncoloredPattern, PdfColorSpace underlyingCS, float[] colorValue) {
        this(uncoloredPattern, new PdfSpecialCs.UncoloredTilingPattern(ensureNotPatternCs(underlyingCS)), colorValue);
    }

    public PatternColor(PdfPattern.Tiling uncoloredPattern, PdfSpecialCs.UncoloredTilingPattern uncoloredTilingCS, float[] colorValue) {
        super(uncoloredTilingCS, colorValue);
        this.pattern = uncoloredPattern;
        this.underlyingColor = makeColor(uncoloredTilingCS.getUnderlyingColorSpace(), colorValue);
    }

    public PdfPattern getPattern() {
        return pattern;
    }

    @Override
    public void setColorValue(float[] value) {
        super.setColorValue(value);
        underlyingColor.setColorValue(value);
    }

    /**
     * Changes pattern for {@link PatternColor}. Be sure to only set uncolored patterns for uncolored {@link PatternColor},
     * and vice versa, only set colored patterns for colored {@link PatternColor}.
     * @param pattern a pattern to be set for this instance.
     * @deprecated To be removed in iText 7.2. In order to change pattern one shall create a new {@link PatternColor}.
     */
    @Deprecated
    public void setPattern(PdfPattern pattern) {
        this.pattern = pattern;
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
