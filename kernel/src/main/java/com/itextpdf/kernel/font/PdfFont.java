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

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class PdfFont extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -7661159455613720321L;

    protected FontProgram fontProgram;

    protected static final byte[] EMPTY_BYTES = new byte[0];
    protected static final double[] DEFAULT_FONT_MATRIX = {0.001, 0, 0, 0.001, 0, 0};

    protected Map<Integer, Glyph> notdefGlyphs = new HashMap<>();

    /**
     * false, if the font comes from PdfDocument.
     */
    protected boolean newFont = true;

    /**
     * true if the font is to be embedded in the PDF.
     */
    protected boolean embedded = false;
    /**
     * Indicates if all the glyphs and widths for that particular encoding should be included in the document.
     */
    protected boolean subset = true;
    protected List<int[]> subsetRanges;

    protected PdfFont(PdfDictionary fontDictionary) {
        super(fontDictionary);
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    protected PdfFont() {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    /**
     * Get glyph by unicode
     *
     * @param unicode a unicode code point
     * @return {@link Glyph} if it exists or .NOTDEF if supported, otherwise {@code null}.
     */
    public abstract Glyph getGlyph(int unicode);

    /**
     * Check whether font contains glyph with specified unicode.
     *
     * @param unicode a unicode code point
     * @return true if font contains glyph, represented with the unicode code point,
     * otherwise false.
     */
    public boolean containsGlyph(int unicode) {
        Glyph glyph = getGlyph(unicode);
        if (glyph != null) {
            if (getFontProgram() != null && getFontProgram().isFontSpecific()) {
                //if current is symbolic, zero code is valid value
                return glyph.getCode() > -1;
            } else {
                return glyph.getCode() > 0;
            }
        } else {
            return false;
        }
    }

    public abstract GlyphLine createGlyphLine(String content);

    /**
     * Append all supported glyphs and return number of processed chars.
     * Composite font supports surrogate pairs.
     *
     * @param text   String to convert to glyphs.
     * @param from   from index of the text.
     * @param to     to index of the text.
     * @param glyphs array for a new glyphs, shall not be null.
     * @return number of processed chars from text.
     */
    public abstract int appendGlyphs(String text, int from, int to, List<Glyph> glyphs);

    /**
     * Append any single glyph, even notdef.
     * Returns number of processed chars: 2 in case surrogate pair, otherwise 1.
     *
     * @param text   String to convert to glyphs.
     * @param from   from index of the text.
     * @param glyphs array for a new glyph, shall not be null.
     * @return number of processed chars: 2 in case surrogate pair, otherwise 1
     */
    public abstract int appendAnyGlyph(String text, int from, List<Glyph> glyphs);

    /**
     * Converts the text into bytes to be placed in the document.
     * The conversion is done according to the font and the encoding and the characters
     * used are stored.
     *
     * @param text the text to convert
     * @return the conversion
     */
    public abstract byte[] convertToBytes(String text);

    public abstract byte[] convertToBytes(GlyphLine glyphLine);

    public abstract String decode(PdfString content);

    /**
     * Decodes a given {@link PdfString} containing encoded string (e.g. from content stream) into a {@link GlyphLine}
     *
     * @param content the encoded string
     * @return the {@link GlyphLine} containing the glyphs encoded by the passed string
     */
    public abstract GlyphLine decodeIntoGlyphLine(PdfString content);

    public abstract float getContentWidth(PdfString content);

    public abstract byte[] convertToBytes(Glyph glyph);

    public abstract void writeText(GlyphLine text, int from, int to, PdfOutputStream stream);

    public abstract void writeText(String text, PdfOutputStream stream);

    public double[] getFontMatrix() {
        return DEFAULT_FONT_MATRIX;
    }

    /**
     * Returns the width of a certain character of this font in 1000 normalized units.
     *
     * @param unicode a certain character.
     * @return a width in Text Space.
     */
    public int getWidth(int unicode) {
        Glyph glyph = getGlyph(unicode);
        return glyph != null ? glyph.getWidth() : 0;
    }

    /**
     * Returns the width of a certain character of this font in points.
     *
     * @param unicode  a certain character.
     * @param fontSize the font size.
     * @return a width in points.
     */
    public float getWidth(int unicode, float fontSize) {
        return getWidth(unicode) * fontSize / FontProgram.UNITS_NORMALIZATION;
    }

    /**
     * Returns the width of a string of this font in 1000 normalized units.
     *
     * @param text a string content.
     * @return a width of string in Text Space.
     */
    public int getWidth(String text) {
        int total = 0;
        for (int i = 0; i < text.length(); i++) {
            int ch;
            if (TextUtil.isSurrogatePair(text, i)) {
                ch = TextUtil.convertToUtf32(text, i);
                i++;
            } else {
                ch = text.charAt(i);
            }
            Glyph glyph = getGlyph(ch);
            if (glyph != null) {
                total += glyph.getWidth();
            }
        }
        return total;
    }

    /**
     * Gets the width of a {@code String} in points.
     *
     * @param text     the {@code String} to get the width of
     * @param fontSize the font size
     * @return the width in points
     */
    public float getWidth(String text, float fontSize) {
        return getWidth(text) * fontSize / FontProgram.UNITS_NORMALIZATION;
    }

    /**
     * Gets the descent of a {@code String} in points. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     *
     * @param text     the {@code String} to get the descent of
     * @param fontSize the font size
     * @return the descent in points
     */
    public int getDescent(String text, float fontSize) {
        int min = 0;
        for (int k = 0; k < text.length(); ++k) {
            int ch;
            if (TextUtil.isSurrogatePair(text, k)) {
                ch = TextUtil.convertToUtf32(text, k);
                k++;
            } else {
                ch = text.charAt(k);
            }
            Glyph glyph = getGlyph(ch);
            if (glyph != null) {
                int[] bbox = glyph.getBbox();
                if (bbox != null && bbox[1] < min) {
                    min = bbox[1];
                } else if (bbox == null && getFontProgram().getFontMetrics().getTypoDescender() < min) {
                    min = getFontProgram().getFontMetrics().getTypoDescender();
                }
            }
        }
        return (int) (min * fontSize / FontProgram.UNITS_NORMALIZATION);
    }

    /**
     * Gets the descent of a char code in points. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     *
     * @param unicode  the char code to get the descent of
     * @param fontSize the font size
     * @return the descent in points
     */
    public int getDescent(int unicode, float fontSize) {
        int min = 0;
        Glyph glyph = getGlyph(unicode);
        if (glyph == null) {
            return 0;
        }
        int[] bbox = glyph.getBbox();
        if (bbox != null && bbox[1] < min) {
            min = bbox[1];
        } else if (bbox == null && getFontProgram().getFontMetrics().getTypoDescender() < min) {
            min = getFontProgram().getFontMetrics().getTypoDescender();
        }

        return (int) (min * fontSize / FontProgram.UNITS_NORMALIZATION);
    }

    /**
     * Gets the ascent of a {@code String} in points. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param text     the {@code String} to get the ascent of
     * @param fontSize the font size
     * @return the ascent in points
     */
    public int getAscent(String text, float fontSize) {
        int max = 0;
        for (int k = 0; k < text.length(); ++k) {
            int ch;
            if (TextUtil.isSurrogatePair(text, k)) {
                ch = TextUtil.convertToUtf32(text, k);
                k++;
            } else {
                ch = text.charAt(k);
            }
            Glyph glyph = getGlyph(ch);
            if (glyph != null) {
                int[] bbox = glyph.getBbox();
                if (bbox != null && bbox[3] > max) {
                    max = bbox[3];
                } else if (bbox == null && getFontProgram().getFontMetrics().getTypoAscender() > max) {
                    max = getFontProgram().getFontMetrics().getTypoAscender();
                }
            }
        }

        return (int) (max * fontSize / FontProgram.UNITS_NORMALIZATION);
    }

    /**
     * Gets the ascent of a char code in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param unicode  the char code to get the ascent of
     * @param fontSize the font size
     * @return the ascent in points
     */
    public int getAscent(int unicode, float fontSize) {
        int max = 0;
        Glyph glyph = getGlyph(unicode);
        if (glyph == null) {
            return 0;
        }
        int[] bbox = glyph.getBbox();
        if (bbox != null && bbox[3] > max) {
            max = bbox[3];
        } else if (bbox == null && getFontProgram().getFontMetrics().getTypoAscender() > max) {
            max = getFontProgram().getFontMetrics().getTypoAscender();
        }

        return (int) (max * fontSize / FontProgram.UNITS_NORMALIZATION);
    }

    public FontProgram getFontProgram() {
        return fontProgram;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    /**
     * Indicates if all the glyphs and widths for that particular
     * encoding should be included in the document.
     *
     * @return <CODE>false</CODE> to include all the glyphs and widths.
     */
    public boolean isSubset() {
        return subset;
    }

    /**
     * Indicates if all the glyphs and widths for that particular
     * encoding should be included in the document. When set to <CODE>true</CODE>
     * only the glyphs used will be included in the font. When set to <CODE>false</CODE>
     * and {@link #addSubsetRange(int[])} was not called the full font will be included
     * otherwise just the characters ranges will be included.
     *
     * @param subset new value of property subset
     */
    public void setSubset(boolean subset) {
        this.subset = subset;
    }

    /**
     * Adds a character range when subsetting. The range is an <CODE>int</CODE> array
     * where the first element is the start range inclusive and the second element is the
     * end range inclusive. Several ranges are allowed in the same array.
     *
     * @param range the character range
     */
    public void addSubsetRange(int[] range) {
        if (subsetRanges == null) {
            subsetRanges = new ArrayList<>();
        }
        subsetRanges.add(range);
    }

    public List<String> splitString(String text, float fontSize, float maxWidth) {
        List<String> resultString = new ArrayList<>();
        int lastWhiteSpace = 0;
        int startPos = 0;

        float tokenLength = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isWhitespace(ch)) {
                lastWhiteSpace = i;
            }
            float currentCharWidth = getWidth(ch, fontSize);
            if (tokenLength + currentCharWidth >= maxWidth || ch == '\n') {
                if (startPos < lastWhiteSpace) {
                    resultString.add(text.substring(startPos, lastWhiteSpace));
                    startPos = lastWhiteSpace + 1;
                    tokenLength = 0;
                    i = lastWhiteSpace;
                } else if (startPos != i) {
                    resultString.add(text.substring(startPos, i));
                    startPos = i;
                    tokenLength = currentCharWidth;
                } else {
                    resultString.add(text.substring(startPos, startPos + 1));
                    startPos = i + 1;
                    tokenLength = 0;
                }
            } else {
                tokenLength += currentCharWidth;
            }
        }

        resultString.add(text.substring(startPos));
        return resultString;
    }

    /**
     * Checks whether the {@link PdfFont} was built with corresponding fontProgram and encoding or CMAP.
     * Default value is false unless overridden.
     *
     * @param fontProgram a font name or path to a font program
     * @param encoding    an encoding or CMAP
     * @return true, if the PdfFont was built with the fontProgram and encoding. Otherwise false.
     * @see PdfDocument#findFont(String, String)
     * @see FontProgram#isBuiltWith(String)
     * @see com.itextpdf.io.font.FontEncoding#isBuiltWith(String)
     * @see com.itextpdf.io.font.CMapEncoding#isBuiltWith(String)
     */
    public boolean isBuiltWith(String fontProgram, String encoding) {
        return false;
    }

    /**
     * To manually flush a {@code PdfObject} behind this wrapper, you have to ensure
     * that this object is added to the document, i.e. it has an indirect reference.
     * Basically this means that before flushing you need to explicitly call {@link #makeIndirect(PdfDocument)}.
     * For example: wrapperInstance.makeIndirect(document).flush();
     * Note that not every wrapper require this, only those that have such warning in documentation.
     */
    @Override
    public void flush() {
        super.flush();
    }

    protected abstract PdfDictionary getFontDescriptor(String fontName);

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * Adds a unique subset prefix to be added to the font name when the font is embedded and subset.
     *
     * @return the font name with subset prefix if isSubset and isEmbedded are true.s
     */
    protected static String updateSubsetPrefix(String fontName, boolean isSubset, boolean isEmbedded) {
        if (isSubset && isEmbedded) {
            StringBuilder s = new StringBuilder(fontName.length() + 7);
            for (int k = 0; k < 6; ++k) {
                s.append((char) (Math.random() * 26 + 'A'));
            }
            return s.append('+').append(fontName).toString();
        }
        return fontName;
    }

    /**
     * Create {@code PdfStream} based on {@code fontStreamBytes}.
     *
     * @param fontStreamBytes   original font data, must be not null.
     * @param fontStreamLengths array to generate {@code Length*} keys, must be not null.
     * @return the PdfStream containing the font or {@code null}, if there is an error reading the font.
     * @throws PdfException Method will throw exception if {@code fontStreamBytes} is {@code null}.
     */
    protected PdfStream getPdfFontStream(byte[] fontStreamBytes, int[] fontStreamLengths) {
        if (fontStreamBytes == null) {
            throw new PdfException(PdfException.FontEmbeddingIssue);
        }
        PdfStream fontStream = new PdfStream(fontStreamBytes);
        makeObjectIndirect(fontStream);
        for (int k = 0; k < fontStreamLengths.length; ++k) {
            fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
        }
        return fontStream;
    }

    protected static int[] compactRanges(List<int[]> ranges) {
        List<int[]> simp = new ArrayList<>();
        for (int[] range : ranges) {
            for (int j = 0; j < range.length; j += 2) {
                simp.add(new int[]{Math.max(0, Math.min(range[j], range[j + 1])), Math.min(0xffff, Math.max(range[j], range[j + 1]))});
            }
        }
        for (int k1 = 0; k1 < simp.size() - 1; ++k1) {
            for (int k2 = k1 + 1; k2 < simp.size(); ++k2) {
                int[] r1 = simp.get(k1);
                int[] r2 = simp.get(k2);
                if (r1[0] >= r2[0] && r1[0] <= r2[1] || r1[1] >= r2[0] && r1[0] <= r2[1]) {
                    r1[0] = Math.min(r1[0], r2[0]);
                    r1[1] = Math.max(r1[1], r2[1]);
                    simp.remove(k2);
                    --k2;
                }
            }
        }
        int[] s = new int[simp.size() * 2];
        for (int k = 0; k < simp.size(); ++k) {
            int[] r = simp.get(k);
            s[k * 2] = r[0];
            s[k * 2 + 1] = r[1];
        }
        return s;
    }

    /**
     * Helper method for making an object indirect, if the object already is indirect.
     * Useful for FontDescriptor and FontFile to make possible immediate flushing.
     * If there is no PdfDocument, mark the object as {@code MUST_BE_INDIRECT}.
     *
     * @param obj an object to make indirect.
     * @return if current object isn't indirect, returns {@code false}, otherwise {@code tree}
     */
    boolean makeObjectIndirect(PdfObject obj) {
        if (getPdfObject().getIndirectReference() != null) {
            obj.makeIndirect(getPdfObject().getIndirectReference().getDocument());
            return true;
        } else {
            markObjectAsIndirect(obj);
            return false;
        }
    }

    @Override
    public String toString() {
        return "PdfFont{" +
                "fontProgram=" + fontProgram +
                '}';
    }
}
