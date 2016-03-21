package com.itextpdf.kernel.font;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.util.Utilities;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class PdfFont extends PdfObjectWrapper<PdfDictionary> {

    protected static final byte[] emptyBytes = new byte[0];

    Map<Integer, Glyph> notdefGlyphs = new HashMap<>();

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
        markObjectAsIndirect(getPdfObject());
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    public abstract Glyph getGlyph(int unicode);

    public boolean containsGlyph(char unicode) {
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

    public abstract float getContentWidth(PdfString content);

    public abstract byte[] convertToBytes(Glyph glyph);

    public abstract void writeText(GlyphLine text, int from, int to, PdfOutputStream stream);

    public abstract void writeText(String text, PdfOutputStream stream);

    public void writeText(GlyphLine text, PdfOutputStream stream) {
        writeText(text, 0, text.size() - 1, stream);
    }

    public double[] getFontMatrix() {
        return FontConstants.DefaultFontMatrix;
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
     * @param unicode a certain character.
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
            if (Utilities.isSurrogatePair(text, i)) {
                ch = Utilities.convertToUtf32(text, i);
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
     * @param text the {@code String} to get the descent of
     * @param fontSize the font size
     * @return the descent in points
     */
    public int getDescent(String text, float fontSize) {
        int min = 0;
        for (int k = 0; k < text.length(); ++k) {
            int ch;
            if (Utilities.isSurrogatePair(text, k)) {
                ch = Utilities.convertToUtf32(text, k);
                k++;
            } else {
                ch = text.charAt(k);
            }
            int[] bbox = getGlyph(ch).getBbox();
            if (bbox != null && bbox[1] < min) {
                min = bbox[1];
            } else if (bbox == null && getFontProgram().getFontMetrics().getTypoDescender() < min) {
                min = getFontProgram().getFontMetrics().getTypoDescender();
            }
        }
        return (int) (min * fontSize / FontProgram.UNITS_NORMALIZATION);
    }

    /**
     * Gets the descent of a char code in points. The descent will always be
     * less than or equal to zero even if all the characters have an higher descent.
     *
     * @param unicode the char code to get the descent of
     * @param fontSize the font size
     * @return the descent in points
     */
    public int getDescent(int unicode, float fontSize) {
        int min = 0;
        int[] bbox = getGlyph(unicode).getBbox();
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
     * @param text the {@code String} to get the ascent of
     * @param fontSize the font size
     * @return the ascent in points
     */
    public int getAscent(String text, float fontSize) {
        int max = 0;
        for (int k = 0; k < text.length(); ++k) {
            int ch;
            if (Utilities.isSurrogatePair(text, k)) {
                ch = Utilities.convertToUtf32(text, k);
                k++;
            } else {
                ch = text.charAt(k);
            }
            int[] bbox = getGlyph(ch).getBbox();
            if (bbox != null && bbox[3] > max) {
                max = bbox[3];
            } else if (bbox == null && getFontProgram().getFontMetrics().getTypoAscender() > max) {
                max = getFontProgram().getFontMetrics().getTypoAscender();
            }
        }

        return (int) (max * fontSize / FontProgram.UNITS_NORMALIZATION);
    }

    /**
     * Gets the ascent of a char code in normalized 1000 units. The ascent will always be
     * greater than or equal to zero even if all the characters have a lower ascent.
     *
     * @param unicode the char code to get the ascent of
     * @param fontSize the font size
     * @return the ascent in points
     */
    public int getAscent(int unicode, float fontSize) {
        int max = 0;
        int[] bbox = getGlyph(unicode).getBbox();
        if (bbox != null && bbox[3] > max) {
            max = bbox[3];
        } else if (bbox == null && getFontProgram().getFontMetrics().getTypoAscender() > max) {
            max = getFontProgram().getFontMetrics().getTypoAscender();
        }

        return (int) (max * fontSize / FontProgram.UNITS_NORMALIZATION);
    }

    public abstract FontProgram getFontProgram();

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
    //TODO
    public void addSubsetRange(int[] range) {
        if (subsetRanges == null) {
            subsetRanges = new ArrayList<>();
        }
        subsetRanges.add(range);
    }

    public List<String> splitString(String text, int fontSize, float maxWidth) {
        List<String> resultString = new ArrayList<>();
        int lastWhiteSpace = 0;
        int startPos = 0;

        float tokenLength = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isWhitespace(ch)) {
                lastWhiteSpace = i;
            }
            tokenLength += getWidth(ch, fontSize);
            if (tokenLength >= maxWidth || ch == '\n') {
                if(startPos < lastWhiteSpace) {
                    resultString.add(text.substring(startPos, lastWhiteSpace));
                    startPos = lastWhiteSpace + 1;
                    tokenLength = 0;
                    i = lastWhiteSpace;
                }else{
                    resultString.add(text.substring(startPos, i+1));
                    startPos = i+1;
                    tokenLength = 0;
                    i=i+1;
                }
            }
        }

        resultString.add(text.substring(startPos));
        return resultString;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    protected boolean checkFontDictionary(PdfDictionary fontDic, PdfName fontType) {
        return PdfFontFactory.checkFontDictionary(fontDic, fontType, true);
    }

    protected boolean checkTrueTypeFontDictionary(PdfDictionary fontDic) {
        return checkTrueTypeFontDictionary(fontDic, true);
    }

    protected boolean checkTrueTypeFontDictionary(PdfDictionary fontDic, boolean isException) {
        if (fontDic == null || fontDic.get(PdfName.Subtype) == null
                || !(fontDic.get(PdfName.Subtype).equals(PdfName.TrueType) || fontDic.get(PdfName.Subtype).equals(PdfName.Type1))) {
            if (isException) {
                throw new PdfException(PdfException.DictionaryNotContainFontData).setMessageParams(PdfName.TrueType.getValue());
            }
            return false;
        }
        return true;
    }

    /**
     * Creates a unique subset prefix to be added to the font name when the font is embedded and subset.
     *
     * @return the subset prefix
     */
    protected static String createSubsetPrefix() {
        StringBuilder s = new StringBuilder("");
        for (int k = 0; k < 6; ++k) {
            s.append((char) (Math.random() * 26 + 'A'));
        }
        return s + "+";
    }

    /**
     * TODO strange comments
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     *
     * @return the PdfStream containing the font or {@code null}, if there is an error reading the font.
     * @exception PdfException Method will throw exception if {@code fontStreamBytes} is {@code null}.
     */
    protected PdfStream getPdfFontStream(byte[] fontStreamBytes, int[] fontStreamLengths) {
        if (fontStreamBytes == null) {
            throw new PdfException(PdfException.FontEmbeddingIssue);
        }
        PdfStream fontStream = new PdfStream(fontStreamBytes);
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
}
