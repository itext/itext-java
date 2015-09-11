package com.itextpdf.core.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.*;
import com.itextpdf.core.pdf.*;

import java.io.IOException;
import java.util.ArrayList;


public class PdfFont extends PdfObjectWrapper<PdfDictionary> {

    protected PdfDictionary fontDictionary;

    protected boolean isCopy = false;


    /** true if the font is to be embedded in the PDF. */
    //TODO mark as final
    protected boolean embedded = false;
    protected boolean subset = true;
    protected ArrayList<int[]> subsetRanges;

    public static PdfFont getDefaultFont(PdfDocument pdfDocument) throws IOException {
        return createStandardFont(pdfDocument, FontConstants.HELVETICA, PdfEncodings.WINANSI);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, PdfDictionary fontDictionary) throws IOException {
        if (checkFontDictionary(fontDictionary, PdfName.Type1, false)) {
            return new PdfType1Font(pdfDocument, fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.Type0, false)) {
            return new PdfType0Font(pdfDocument, fontDictionary);
        } else if (checkFontDictionary(fontDictionary, PdfName.TrueType, false)) {
            return new PdfTrueTypeFont(pdfDocument, fontDictionary);
        } else {
            throw new PdfException(PdfException.DictionaryNotContainFontData);
        }
    }

    public static PdfFont createFont(PdfDocument pdfDocument, String path, String encoding) throws IOException {
        return createFont(pdfDocument, path, encoding, false);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, String path, String encoding, boolean embedded) throws IOException {
        FontProgram fontProgram = FontFactory.createFont(path, encoding);
        if (fontProgram == null) {
            return null;
        } else if (fontProgram instanceof Type1Font) {
            return new PdfType1Font(pdfDocument, (Type1Font)fontProgram, embedded);
        } else if (fontProgram instanceof TrueTypeFont) {
            if (PdfEncodings.IDENTITY_H.equals(encoding) || PdfEncodings.IDENTITY_V.equals(encoding)) {
                return new PdfType0Font(pdfDocument, (TrueTypeFont)fontProgram, encoding);
            } else {
                return new PdfTrueTypeFont(pdfDocument, (TrueTypeFont)fontProgram, embedded);
            }
        } else if (fontProgram instanceof CidFont) {
            return new PdfType0Font(pdfDocument, (CidFont)fontProgram, encoding);
        } else {
            return null;
        }
    }

    public static PdfFont createFont(PdfDocument pdfDocument, byte[] font, String encoding) throws IOException {
        return createFont(pdfDocument, font, encoding, false);
    }

    public static PdfFont createFont(PdfDocument pdfDocument, byte[] font, String encoding, boolean embedded) throws IOException {
        FontProgram fontProgram = FontFactory.createFont(null, encoding, false, font, null, true);
        if (fontProgram == null) {
            return null;
        } else if (fontProgram instanceof Type1Font) {
            return new PdfType1Font(pdfDocument, (Type1Font)fontProgram, embedded);
        } else if (fontProgram instanceof TrueTypeFont) {
            if (PdfEncodings.IDENTITY_H.equals(encoding) || PdfEncodings.IDENTITY_V.equals(encoding)) {
                return new PdfType0Font(pdfDocument, (TrueTypeFont)fontProgram, encoding);
            } else {
                return new PdfTrueTypeFont(pdfDocument, (TrueTypeFont)fontProgram, embedded);
            }
        } else {
            return null;
        }
    }

    public static PdfFont createStandardFont(PdfDocument pdfDocument, String name) throws IOException {
        return createStandardFont(pdfDocument, name, PdfEncodings.WINANSI);
    }

    public static PdfFont createStandardFont(PdfDocument pdfDocument, String name, String encoding) throws IOException {
        return new PdfType1Font(pdfDocument, Type1Font.createStandardFont(name, encoding));
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics) throws IOException {
        return createType1Font(pdfDocument, metrics, null, PdfEncodings.WINANSI, false);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics, String encoding) throws IOException {
        return createType1Font(pdfDocument, metrics, null, encoding, false);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics, String binary, String encoding) throws IOException {
        return createType1Font(pdfDocument, metrics, binary, encoding, false);
    }

    /**
     * Create {@see PdfType1Font}.
     * @param metrics path to .afm or .pfm metrics file.
     * @param binary .pfb binary file
     */
    public static PdfFont createType1Font(PdfDocument pdfDocument, String metrics, String binary, String encoding, boolean embedded) throws IOException {
        return new PdfType1Font(pdfDocument, Type1Font.createFont(metrics, binary, encoding), embedded);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics) throws IOException {
        return createType1Font(pdfDocument, metrics, null, PdfEncodings.WINANSI, false);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics, String encoding) throws IOException {
        return createType1Font(pdfDocument, metrics, null, encoding, false);
    }

    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics, byte[] binary, String encoding) throws IOException {
        return createType1Font(pdfDocument, metrics, binary, encoding, false);
    }

    /**
     * Create {@see PdfType1Font}.
     * @param metrics .afm or .pfm metrics file.
     * @param binary .pfb binary file
     */
    public static PdfFont createType1Font(PdfDocument pdfDocument, byte[] metrics, byte[] binary, String encoding, boolean embedded) throws IOException {
        return new PdfType1Font(pdfDocument, Type1Font.createFont(metrics, binary, encoding), embedded);
    }

    public PdfFont(PdfDocument pdfDocument, PdfDictionary pdfObject) {
        super(pdfObject);
        makeIndirect(pdfDocument);
        getPdfObject().put(PdfName.Type, PdfName.Font);
    }

    protected PdfFont(PdfDocument document, PdfDictionary pdfDictionary, boolean isCopy){
        super(pdfDictionary);
        makeIndirect(document);
        getPdfObject().put(PdfName.Type, PdfName.Font);
        this.fontDictionary = pdfDictionary;
        this.isCopy = isCopy;
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
     * Returns the width of a certain character of this font in 1000 normalized units.
     *
     * @param ch a certain character.
     * @return a width in Text Space.
     */
    public float getWidth(int ch) {
        // TODO abstract method
        throw new IllegalStateException();
    }

    /**
     * Returns the width of a string of this font in 1000 normalized units.
     *
     * @param s a string content.
     * @return a width of string in Text Space.
     */
    public float getWidth(String s) {
        // TODO abstract method
        throw new IllegalStateException();
    }

    /**
     * Gets the width of a {@code String} in points.
     *
     * @param text the {@code String} to get the width of
     * @param fontSize the font size
     * @return the width in points
     */
    public float getWidthPoint(String text, float fontSize) {
        return getWidth(text) * fontSize / FontProgram.UNITS_NORMALIZATION;
    }

    /**
     * Gets the width of a {@code char} in points.
     *
     * @param ch the {@code char} to get the width of
     * @param fontSize the font size
     * @return the width in points
     */
    public float getWidthPoint(int ch, float fontSize) {
        return getWidth(ch) * fontSize / FontProgram.UNITS_NORMALIZATION;
    }


    public boolean hasKernPairs() {
        FontProgram fontProgram = getFontProgram();
        return fontProgram != null && fontProgram.hasKernPairs();
    }

    public int getKerning(int char1, int char2) {
        FontProgram fontProgram = getFontProgram();
        if (fontProgram != null) {
            return fontProgram.getKerning(char1, char2);
        } else {
            return 0;
        }
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
        return new PdfFont(document, (PdfDictionary) getPdfObject().copyToDocument(document));
    }

    protected static boolean checkFontDictionary(PdfDictionary fontDic, PdfName fontType,boolean isException) {
        if (fontDic == null || fontDic.get(PdfName.Subtype) == null
                || !fontDic.get(PdfName.Subtype).equals(fontType)) {
            if(isException) {
                throw new PdfException(PdfException.DictionaryNotContainFontData).setMessageParams(fontType.getValue());
            }
            return false;
        }
        return true;
    }

    protected  boolean checkFontDictionary(PdfDictionary fontDic, PdfName fontType) {
        return checkFontDictionary(fontDic,fontType,true);
    }


    protected  boolean checkTrueTypeFontDictionary(PdfDictionary fontDic) {
        return  checkTrueTypeFontDictionary(fontDic,true);
    }

    protected  boolean checkTrueTypeFontDictionary(PdfDictionary fontDic,boolean isException) {
        if (fontDic == null || fontDic.get(PdfName.Subtype) == null
                || !(fontDic.get(PdfName.Subtype).equals(PdfName.TrueType) || fontDic.get(PdfName.Subtype).equals(PdfName.Type1))) {
            if(isException) {
                throw new PdfException(PdfException.DictionaryNotContainFontData).setMessageParams(PdfName.TrueType.getValue());
            }
            return false;
        }
        return true;
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
            if (wd.length < nSize) {
                int[] tmp = new int[nSize];
                System.arraycopy(wd, 0, tmp, 0, first);
                wd = tmp;
            }
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
     * @return the PdfStream containing the font or {@code null}, if there is an error reading the font.
     */
    protected PdfStream getFontStream(byte[] fontStreamBytes, int[] fontStreamLengths) {
        if (fontStreamBytes == null) {
            return null;
        }
        PdfStream fontStream = new PdfStream(fontStreamBytes).makeIndirect(getDocument());
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
