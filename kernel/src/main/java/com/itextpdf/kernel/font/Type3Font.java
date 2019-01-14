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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.constants.FontDescriptorFlags;
import com.itextpdf.io.font.constants.FontStretches;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.otf.Glyph;

import java.util.HashMap;
import java.util.Map;

/**
 * FontProgram class for Type 3 font. Contains map of {@Link Type3Glyph}.
 * Type3Glyphs belong to a particular pdf document.
 * Note, an instance of Type3Font can not be reused for multiple pdf documents.
 */
public class Type3Font extends FontProgram {

	private static final long serialVersionUID = 1027076515537536993L;
	
	private final Map<Integer, Type3Glyph> type3Glyphs = new HashMap<>();
    private boolean colorized = false;
    private int flags = 0;

    /**
     * Creates a Type 3 font program.
     *
     * @param colorized defines whether the glyph color is specified in the glyph descriptions in the font.
     */
    Type3Font(boolean colorized) {
        this.colorized = colorized;
        this.fontNames = new FontNames();
        getFontMetrics().setBbox(0, 0, 0, 0);
    }

    public Type3Glyph getType3Glyph(int unicode) {
        return type3Glyphs.get(unicode);
    }

    @Override
    public int getPdfFontFlags() {
        return flags;
    }

    @Override
    public boolean isFontSpecific() {
        return false;
    }

    public boolean isColorized() {
        return colorized;
    }

    @Override
    public int getKerning(Glyph glyph1, Glyph glyph2) {
        return 0;
    }

    public int getNumberOfGlyphs() {
        return type3Glyphs.size();
    }

    /**
     * Sets the PostScript name of the font.
     * <br />
     * If full name is null, it will be set as well.
     * @param fontName the PostScript name of the font, shall not be null or empty.
     */
    @Override   //This dummy override allows PdfType3Font to set font name because of different modules.
    protected void setFontName(String fontName) {
        super.setFontName(fontName);
    }

    /**
     * Sets a preferred font family name.
     *
     * @param fontFamily a preferred font family name.
     */
    @Override   //This dummy override allows PdfType3Font to set font family because of different modules.
    protected void setFontFamily(String fontFamily) {
        super.setFontFamily(fontFamily);
    }

    /**
     * Sets font weight.
     *
     * @param fontWeight integer form 100 to 900. See {@link FontWeights}.
     */
    @Override   //This dummy override allows PdfType3Font to set font weight because of different modules.
    protected void setFontWeight(int fontWeight) {
        super.setFontWeight(fontWeight);
    }

    /**
     * Sets font width in css notation (font-stretch property)
     *
     * @param fontWidth {@link FontStretches}.
     */
    @Override   //This dummy override allows PdfType3Font to set font stretch because of different modules.
    protected void setFontStretch(String fontWidth) {
        super.setFontStretch(fontWidth);
    }

    /**
     * Sets the PostScript italic angel.
     * <br/>
     * Italic angle in counter-clockwise degrees from the vertical. Zero for upright text, negative for text that leans to the right (forward).
     *
     * @param italicAngle in counter-clockwise degrees from the vertical
     */
    @Override   //This dummy override allows PdfType3Font to set the PostScript italicAngel because of different modules.
    protected void setItalicAngle(int italicAngle) {
        super.setItalicAngle(italicAngle);
    }

    /**
     * Sets Font descriptor flags.
     * @see FontDescriptorFlags
     *
     * @param flags {@link FontDescriptorFlags}.
     */
    void setPdfFontFlags(int flags) {
        this.flags = flags;
    }

    void addGlyph(int code, int unicode, int width, int[] bbox, Type3Glyph type3Glyph) {
        Glyph glyph = new Glyph(code, width, unicode, bbox);
        codeToGlyph.put(code, glyph);
        unicodeToGlyph.put(unicode, glyph);
        type3Glyphs.put(unicode, type3Glyph);
    }
}
