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
package com.itextpdf.kernel.font;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
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

    /**
     * The upper bound value for char code. As for simple fonts char codes are a single byte values,
     * it may vary from 0 to 255.
     */
    public static final int SIMPLE_FONT_MAX_CHAR_CODE_VALUE = 255;

    protected FontProgram fontProgram;

    protected static final byte[] EMPTY_BYTES = new byte[0];

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
     *
     * @return {@link Glyph} if it exists or .NOTDEF if supported, otherwise {@code null}.
     */
    public abstract Glyph getGlyph(int unicode);

    /**
     * Check whether font contains glyph with specified unicode.
     *
     * @param unicode a unicode code point
     *
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
     *
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
     *
     * @return number of processed chars: 2 in case surrogate pair, otherwise 1
     */
    public abstract int appendAnyGlyph(String text, int from, List<Glyph> glyphs);

    /**
     * Converts the text into bytes to be placed in the document.
     * The conversion is done according to the font and the encoding and the characters
     * used are stored.
     *
     * @param text the text to convert
     *
     * @return the conversion
     */
    public abstract byte[] convertToBytes(String text);

    public abstract byte[] convertToBytes(GlyphLine glyphLine);

    public abstract String decode(PdfString content);

    /**
     * Decodes sequence of character codes (e.g. from content stream) into a {@link GlyphLine}
     *
     * @param characterCodes the string which is interpreted as a sequence of character codes. Note, that {@link
     *                       PdfString} acts as a storage for char code values specific to given font, therefore
     *                       individual character codes must not be interpreted as code units of the UTF-16 encoding
     *
     * @return the {@link GlyphLine} containing the glyphs encoded by the passed string
     */
    public abstract GlyphLine decodeIntoGlyphLine(PdfString characterCodes);

    /**
     * Decodes sequence of character codes (e.g. from content stream) to sequence of glyphs
     * and appends them to the passed list.
     *
     * @param list           the list to the end of which decoded glyphs are to be added
     * @param characterCodes the string which is interpreted as a sequence of character codes. Note, that {@link
     *                       PdfString} acts as a storage for char code values specific to given font, therefore
     *                       individual character codes must not be interpreted as code units of the UTF-16 encoding
     *
     * @return true if all codes where successfully decoded, false otherwise
     */
    public boolean appendDecodedCodesToGlyphsList(List<Glyph> list, PdfString characterCodes) {
        return false;
    }

    public abstract float getContentWidth(PdfString content);

    public abstract byte[] convertToBytes(Glyph glyph);

    public abstract void writeText(GlyphLine text, int from, int to, PdfOutputStream stream);

    public abstract void writeText(String text, PdfOutputStream stream);

    /**
     * Returns the width of a certain character of this font in 1000 normalized units.
     *
     * @param unicode a certain character.
     *
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
     *
     * @return a width in points.
     */
    public float getWidth(int unicode, float fontSize) {
        return FontProgram.convertTextSpaceToGlyphSpace(getWidth(unicode) * fontSize);
    }

    /**
     * Returns the width of a string of this font in 1000 normalized units.
     *
     * @param text a string content.
     *
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
     *
     * @return the width in points
     */
    public float getWidth(String text, float fontSize) {
        return FontProgram.convertTextSpaceToGlyphSpace(getWidth(text) * fontSize);
    }

    /**
     * Gets the descent of a {@code String} in points. The descent will always be
     * less than or equal to zero even if all the characters have a higher descent.
     *
     * @param text     the {@code String} to get the descent of
     * @param fontSize the font size
     *
     * @return the descent in points
     */
    public float getDescent(String text, float fontSize) {
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
        return FontProgram.convertTextSpaceToGlyphSpace(min * fontSize);
    }

    /**
     * Gets the descent of a char code in points. The descent will always be
     * less than or equal to zero even if all the characters have a higher descent.
     *
     * @param unicode  the char code to get the descent of
     * @param fontSize the font size
     *
     * @return the descent in points
     */
    public float getDescent(int unicode, float fontSize) {
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

        return FontProgram.convertTextSpaceToGlyphSpace(min * fontSize);
    }

    /**
     * Gets the ascent of a {@code String} in points. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param text     the {@code String} to get the ascent of
     * @param fontSize the font size
     *
     * @return the ascent in points
     */
    public float getAscent(String text, float fontSize) {
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

        return FontProgram.convertTextSpaceToGlyphSpace(max * fontSize);
    }

    /**
     * Gets the ascent of a char code in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param unicode  the char code to get the ascent of
     * @param fontSize the font size
     *
     * @return the ascent in points
     */
    public float getAscent(int unicode, float fontSize) {
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

        return FontProgram.convertTextSpaceToGlyphSpace(max * fontSize);
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
     * encoding should be included in the document. When set to {@code true}
     * only the glyphs used will be included in the font. When set to {@code false}
     * the full font will be included and all subset ranges will be removed.
     *
     * @param subset new value of property subset
     *
     * @see #addSubsetRange(int[])
     */
    public void setSubset(boolean subset) {
        this.subset = subset;
    }

    /**
     * Adds a character range when subsetting. The range is an <CODE>int</CODE> array
     * where the first element is the start range inclusive and the second element is the
     * end range inclusive. Several ranges are allowed in the same array.
     * Note, #setSubset(true) will be called implicitly
     * therefore this range is an addition to the used glyphs.
     *
     * @param range the character range
     */
    public void addSubsetRange(int[] range) {
        if (subsetRanges == null) {
            subsetRanges = new ArrayList<>();
        }
        subsetRanges.add(range);
        setSubset(true);
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
     *
     * @return true, if the PdfFont was built with the fontProgram and encoding. Otherwise false.
     *
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
     * Adds a unique subset prefix to be added to the font name when the font is embedded and subsetted.
     *
     * @param fontName   the original font name.
     * @param isSubset   denotes whether font in question is subsetted, i.e. only used symbols are kept in it.
     * @param isEmbedded denotes whether font in question is embedded into the PDF document.
     *
     * @return the font name prefixed with subset if isSubset and isEmbedded are true,
     * otherwise original font name is returned intact.
     */
    protected static String updateSubsetPrefix(String fontName, boolean isSubset, boolean isEmbedded) {
        if (isSubset && isEmbedded) {
            return FontUtil.addRandomSubsetPrefixForFontName(fontName);
        }
        return fontName;
    }

    /**
     * Create {@code PdfStream} based on {@code fontStreamBytes}.
     *
     * @param fontStreamBytes   original font data, must be not null.
     * @param fontStreamLengths array to generate {@code Length*} keys, must be not null.
     *
     * @return the PdfStream containing the font or {@code null}, if there is an error reading the font.
     *
     * @throws PdfException Method will throw exception if {@code fontStreamBytes} is {@code null}.
     */
    protected PdfStream getPdfFontStream(byte[] fontStreamBytes, int[] fontStreamLengths) {
        if (fontStreamBytes == null || fontStreamLengths == null) {
            throw new PdfException(KernelExceptionMessageConstant.FONT_EMBEDDING_ISSUE);
        }
        PdfStream fontStream = new PdfStream(fontStreamBytes);
        makeObjectIndirect(fontStream);
        for (int k = 0; k < fontStreamLengths.length; ++k) {
            fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
        }
        return fontStream;
    }

    /**
     * Helper method for making an object indirect, if the object already is indirect.
     * Useful for FontDescriptor and FontFile to make possible immediate flushing.
     * If there is no PdfDocument, mark the object as {@code MUST_BE_INDIRECT}.
     *
     * @param obj an object to make indirect.
     *
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
