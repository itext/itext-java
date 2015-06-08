package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.FontProgram;
import com.itextpdf.basics.font.PdfEncodings;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Nothing here...
 * We do not yet know how the font class should look like.
 */
public class PdfFont extends PdfObjectWrapper<PdfDictionary> {

    protected PdfDictionary fontDictionary;

    protected boolean isCopy = false;


    /** true if the font is to be embedded in the PDF. */
    protected boolean embedded = false;
    protected boolean subset = true;
    protected ArrayList<int[]> subsetRanges;

    public PdfFont(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    protected PdfFont(PdfDocument pdfDocument) {
        this(new PdfDictionary(), pdfDocument);
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    public static PdfFont getDefaultFont(PdfDocument pdfDocument) throws IOException {
        return new PdfType1Font(pdfDocument, new Type1Font(FontConstants.HELVETICA, ""));
    }

    /**
     * Converts the text into bytes to be placed in the document.
     * The conversion is done according to the font and the encoding and the characters
     * used are stored.
     *
     * @param text the text to convert
     * @return the conversion
     */
    public byte[] convertToBytes(String text) {
        //TODO when implement document fonts, throw exception
        //throw new IllegalStateException();
        return PdfEncodings.convertToBytes(text, "");
    }

    /**
     * Returns the width of a certain character of this font.
     *
     * @param ch a certain character.
     * @return a width in Text Space.
     */
    public float getWidth(int ch) {
        throw new IllegalStateException();
    }

    /**
     * Returns the width of a string of this font.
     *
     * @param s a string content.
     * @return a width of string in Text Space.
     */
    public float getWidth(String s) {
        throw new IllegalStateException();
    }

    public FontProgram getFontProgram() {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public boolean isEmbedded() {
        return embedded;
    }

    /** Indicates if all the glyphs and widths for that particular
     * encoding should be included in the document.
     * @return <CODE>false</CODE> to include all the glyphs and widths.
     */
    public boolean isSubset() {
        return subset;
    }

    /** Indicates if all the glyphs and widths for that particular
     * encoding should be included in the document. When set to <CODE>true</CODE>
     * only the glyphs used will be included in the font. When set to <CODE>false</CODE>
     * and {@link #addSubsetRange(int[])} was not called the full font will be included
     * otherwise just the characters ranges will be included.
     * @param subset new value of property subset
     */
    public void setSubset(boolean subset) {
        this.subset = subset;
    }

    /**
     * Adds a character range when subsetting. The range is an <CODE>int</CODE> array
     * where the first element is the start range inclusive and the second element is the
     * end range inclusive. Several ranges are allowed in the same array.
     * @param range the character range
     */
    //TODO
    public void addSubsetRange(int[] range) {
        if (subsetRanges == null) {
            subsetRanges = new ArrayList<int[]>();
        }
        subsetRanges.add(range);
    }

    @Override
    public PdfFont copy(PdfDocument document) {
        return new PdfFont((PdfDictionary) getPdfObject().copy(document), document);
    }

    protected void checkFontDictionary(PdfName fontType) {
        if (this.fontDictionary == null || this.fontDictionary.get(PdfName.Subtype) == null
                || !this.fontDictionary.get(PdfName.Subtype).equals(fontType)) {
            throw new PdfException(PdfException.DictionaryNotContainFontData).setMessageParams(fontType.getValue());
        }
    }

    protected void checkTrueTypeFontDictionary() {
        if (this.fontDictionary == null || this.fontDictionary.get(PdfName.Subtype) == null
                || !(this.fontDictionary.get(PdfName.Subtype).equals(PdfName.TrueType) || this.fontDictionary.get(PdfName.Subtype).equals(PdfName.Type1))) {
            throw new PdfException(PdfException.DictionaryNotContainFontData).setMessageParams(PdfName.TrueType.getValue());
        }
    }

    protected PdfStream copyFontFileStream(PdfStream fileStream) {
        PdfStream newFileStream = new PdfStream(getDocument(), fileStream.getBytes());
        for (Map.Entry<PdfName, PdfObject> entry : fileStream.entrySet()) {
            newFileStream.put(entry.getKey(), entry.getValue());
        }
        return newFileStream;
    }

    protected boolean isSymbolic() {
        PdfDictionary fontDescriptor = fontDictionary.getAsDictionary(PdfName.FontDescriptor);
        if (fontDescriptor == null)
            return false;
        PdfNumber flags = fontDescriptor.getAsNumber(PdfName.Flags);
        if (flags == null)
            return false;
        return (flags.getIntValue() & 0x04) != 0;
    }

    protected int[] getFillWidths(PdfArray widths, PdfNumber firstObj, PdfNumber lastObj) {
        int wd[] = new int[256];
        if (firstObj != null && lastObj != null && widths != null) {
            int first = firstObj.getIntValue();
            int nSize = first + widths.size();
            int[] tmp = new int[nSize];
            System.arraycopy(wd, 0, tmp, 0, first);
            wd = tmp;
            for (int k = 0; k < widths.size(); ++k) {
                wd[first + k] = widths.getAsNumber(k).getIntValue();
            }
        }
        return wd;

    }

    /** Creates a unique subset prefix to be added to the font name when the font is embedded and subset.
     * @return the subset prefix
     */
    protected static String createSubsetPrefix() {
        StringBuilder s = new StringBuilder("");
        for (int k = 0; k < 6; ++k)
            s.append((char)(Math.random() * 26 + 'A'));
        return s + "+";
    }

    /**
     * If the embedded flag is {@code false} or if the font is one of the 14 built in types, it returns {@code null},
     * otherwise the font is read and output in a PdfStream object.
     * @return the PdfStream containing the font or {@code null}.
     * @if there is an error reading the font.
     */
    protected PdfStream getFontStream(byte[] fontStreamBytes, int[] fontStreamLengths) {
        if (fontStreamBytes == null) {
            return null;
        }
        PdfStream fontStream = new PdfStream(getDocument(), fontStreamBytes);
        for (int k = 0; k < fontStreamLengths.length; ++k) {
            fontStream.put(new PdfName("Length" + (k + 1)), new PdfNumber(fontStreamLengths[k]));
        }
        return fontStream;
    }

    protected static int[] compactRanges(ArrayList<int[]> ranges) {
        ArrayList<int[]> simp = new ArrayList<int[]>();
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
