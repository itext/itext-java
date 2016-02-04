package com.itextpdf.kernel.font;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;

/**
 * Low-level API class for Type 3 fonts.
 * <p/>
 * In Type 3 fonts, glyphs are defined by streams of PDF graphics operators.
 * These streams are associated with character names. A separate encoding entry
 * maps character codes to the appropriate character names for the glyphs.
 */
public class PdfType3Font extends PdfSimpleFont<Type3FontProgram> {

    private double[] fontMatrix = {0.001, 0, 0, 0.001, 0, 0};

    /**
     * Creates a Type3 font.
     *
     * @param colorized defines whether the glyph color is specified in the glyph descriptions in the font.
     */
    PdfType3Font(PdfDocument document, boolean colorized) {
        super();
        makeIndirect(document);
        subset = true;
        embedded = true;
        fontProgram = new Type3FontProgram(colorized);
        fontEncoding = FontEncoding.createEmptyFontEncoding();
    }

    /**
     * Creates a Type3 font based on an existing font dictionary.
     *
     * @param fontDictionary a dictionary of type <code>/Font</code>
     */
    PdfType3Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        checkFontDictionary(fontDictionary, PdfName.Type3);
        subset = true;
        embedded = true;
        fontProgram = new Type3FontProgram(false);
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), null, false);
        PdfDictionary charProcsDic = getPdfObject().getAsDictionary(PdfName.CharProcs);
        PdfArray fontMatrixArray = getPdfObject().getAsArray(PdfName.FontMatrix);
        if (getPdfObject().containsKey(PdfName.FontBBox)) {
            PdfArray fontBBox = getPdfObject().getAsArray(PdfName.FontBBox);
            fontProgram.getFontMetrics().setBbox(fontBBox.getAsInt(0), fontBBox.getAsInt(1),
                    fontBBox.getAsInt(2), fontBBox.getAsInt(3));
        } else {
            fontProgram.getFontMetrics().setBbox(0, 0, 0, 0);
        }
        PdfNumber firstCharNumber = fontDictionary.getAsNumber(PdfName.FirstChar);
        int firstChar = firstCharNumber != null ? Math.max(firstCharNumber.getIntValue(), 0) : 0;
        int[] widths = FontUtils.convertSimpleWidthsArray(fontDictionary.getAsArray(PdfName.Widths), firstChar);
        double[] fontMatrix = new double[6];
        for (int i = 0; i < fontMatrixArray.size(); i++) {
            fontMatrix[i] = ((PdfNumber) fontMatrixArray.get(i)).getValue();
        }
        setFontMatrix(fontMatrix);

        for (PdfName glyphName : charProcsDic.keySet()) {
            Integer unicode = AdobeGlyphList.nameToUnicode(glyphName.getValue());
            if (unicode != null && fontEncoding.canEncode(unicode)) {
                int code = fontEncoding.convertToByte(unicode);
                fontProgram.addGlyph(code, unicode, widths[code], null, new Type3Glyph(charProcsDic.getAsStream(glyphName)));
            }
        }
    }

    public Type3Glyph getType3Glyph(int unicode) {
        return fontProgram.getType3Glyph(unicode);
    }

    @Override
    public boolean isSubset() {
        return true;
    }

    @Override
    public boolean isEmbedded() {
        return true;
    }

    @Override
    public double[] getFontMatrix() {
        return this.fontMatrix;
    }

    public void setFontMatrix(double[] fontMatrix) {
        this.fontMatrix = fontMatrix;
    }

    /**
     * Defines a glyph. If the character was already defined it will return the same content
     *
     * @param c   the character to match this glyph.
     * @param wx  the advance this character will have
     * @param llx the X lower left corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *            <CODE>true</CODE> the value is ignored
     * @param lly the Y lower left corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *            <CODE>true</CODE> the value is ignored
     * @param urx the X upper right corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *            <CODE>true</CODE> the value is ignored
     * @param ury the Y upper right corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *            <CODE>true</CODE> the value is ignored
     * @return a content where the glyph can be defined
     */
    public Type3Glyph addGlyph(char c, int wx, int llx, int lly, int urx, int ury) {
        Type3Glyph glyph = getType3Glyph(c);
        if (glyph != null) {
            return glyph;
        }
        int code = getFirstEmptyCode();
        glyph = new Type3Glyph(getDocument(), wx, llx, lly, urx, ury, fontProgram.isColorized());
        fontProgram.addGlyph(code, c, wx, new int[]{llx, lly, urx, ury}, glyph);
        fontEncoding.addSymbol((byte) code, c);

        if (!fontProgram.isColorized()) {
            if (fontProgram.countOfGlyphs() == 0) {
                fontProgram.getFontMetrics().setBbox(llx, lly, urx, ury);
            } else {
                int[] bbox = fontProgram.getFontMetrics().getBbox();
                int newLlx = Math.min(bbox[0], llx);
                int newLly = Math.min(bbox[1], lly);
                int newUrx = Math.max(bbox[2], urx);
                int newUry = Math.max(bbox[3], ury);
                fontProgram.getFontMetrics().setBbox(newLlx, newLly, newUrx, newUry);
            }
        }
        return glyph;
    }

    @Override
    public Glyph getGlyph(int unicode) {
        if (fontEncoding.canEncode(unicode)) {
            return getFontProgram().getGlyph(unicode);
        }
        return null;
    }

    @Override
    protected PdfDictionary getFontDescriptor(String fontName) {
        return null;
    }

    @Override
    protected void addFontStream(PdfDictionary fontDescriptor) {
    }

    @Override
    public void flush() {
        if (fontProgram.getGlyphsCount() < 1) {
            throw new PdfException("no.glyphs.defined.fo r.type3.font");
        }
        PdfDictionary charProcs = new PdfDictionary();
        for (int i = 0; i < 256; i++) {
            if (fontEncoding.canDecode(i)) {
                Type3Glyph glyph = getType3Glyph(fontEncoding.getUnicode(i));
                charProcs.put(new PdfName(fontEncoding.getDifference(i)), glyph.getContentStream());
            }
        }
        getPdfObject().put(PdfName.CharProcs, charProcs);
        getPdfObject().put(PdfName.FontMatrix, new PdfArray(getFontMatrix()));
        getPdfObject().put(PdfName.FontBBox, new PdfArray(fontProgram.getFontMetrics().getBbox()));
        super.flushFontData(null, PdfName.Type3);
        super.flush();
    }

    /**
     * Gets first empty code, that could use with {@see addSymbol()}
     * @return code from 1 to 255 or -1 if all slots are busy.
     */
    private int getFirstEmptyCode() {
        final int startFrom = 1;
        for (int i = startFrom; i < 256; i++) {
            if (!fontEncoding.canDecode(i)) {
                return i;
            }
        }
        return -1;
    }
}
