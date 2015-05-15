package com.itextpdf.canvas.font;


import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.font.AdobeGlyphList;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

import java.util.*;


public class PdfType3Font extends PdfFont {

    private boolean[] usedSlot = new boolean[256];

    private IntHashtable widths = new IntHashtable();

    private HashMap<Integer, Type3Glyph> charGlyphs = new HashMap<Integer, Type3Glyph>();

    private float wx;

    private float llx = Float.NaN;

    private float lly;

    private float urx;

    private float ury;

    private boolean isColor = false;

    /**
     * array of six numbers specifying the font matrix, mapping glyph space to text space
     */
    private double[] fontMatrix = {0.001, 0, 0, 0.001, 0, 0};
    PdfArray differences = new PdfArray();
    private PdfDictionary fontDictionary;

    public static final int standartEncoding[] = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            32, 33, 34, 35, 36, 37, 38, 8217, 40, 41, 42, 43, 44, 45, 46, 47,
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63,
            64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
            80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95,
            8216, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111,
            112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 161, 162, 163, 8260, 165, 402, 167, 164, 39, 8220, 171, 8249, 8250, 64257, 64258,
            0, 8211, 8224, 8225, 183, 0, 182, 8226, 8218, 8222, 8221, 187, 8230, 8240, 0, 191,
            0, 96, 180, 710, 732, 175, 728, 729, 168, 0, 730, 184, 0, 733, 731, 711,
            8212, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 198, 0, 170, 0, 0, 0, 0, 321, 216, 338, 186, 0, 0, 0, 0,
            0, 230, 0, 0, 0, 305, 0, 0, 322, 248, 339, 223, 0, 0, 0, 0
    };

    /**
     * Creates a Type3 font.
     *
     * @param pdfDocument pdfDocument and only images as masks can be used
     */
    public PdfType3Font(PdfDocument pdfDocument, boolean isColor) throws PdfException {
        super(pdfDocument);
        this.isColor = isColor;
    }

    /**
     * Creates a Type3 font on based exist font dictionary.
     *
     * @param pdfDocument pdfDocument and only images as masks can be used
     */
    public PdfType3Font(PdfDocument pdfDocument, PdfDictionary fontDictionary) throws PdfException {
        super(pdfDocument);
        this.fontDictionary = fontDictionary;
        checkFontDictionary();
        init();
    }

    public PdfType3Font(PdfDocument pdfDocument, PdfIndirectReference indirectReference) throws PdfException {
        this(pdfDocument, (PdfDictionary) indirectReference.getRefersTo());
    }

    public HashMap<Integer, Type3Glyph> getCharGlyphs() {
        return charGlyphs;
    }

    public float getLlx() {
        return llx;
    }

    public void setLlx(float llx) {
        this.llx = llx;
    }

    public float getLly() {
        return lly;
    }

    public void setLly(float lly) {
        this.lly = lly;
    }

    public float getUrx() {
        return urx;
    }

    public void setUrx(float urx) {
        this.urx = urx;
    }

    public float getUry() {
        return ury;
    }

    public void setUry(float ury) {
        this.ury = ury;
    }

    public float getWx() {
        return wx;
    }

    public void setWx(float wx) {
        this.wx = wx;
    }


    /**
     * Defines a List glyphs, must set this.wx, this.llx, this.lly, this.urx, this.ury parameters
     *
     * @param charArray the array of characters to match this glyph.
     * @return glyphs array
     */
    public List<Type3Glyph> createGlyphs(char charArray[]) throws PdfException {
        if (charArray == null || charArray.length == 0) {
            return Collections.emptyList();
        }
        List<Type3Glyph> listGlyph = new ArrayList<Type3Glyph>();
        for (char c : charArray) {
            listGlyph.add(createGlyph(c, this.wx, this.llx, this.lly, this.urx, this.ury));
        }
        return listGlyph;
    }

    public Type3Glyph createGlyph(char c) throws PdfException {
        return createGlyph(c, this.wx, this.llx, this.lly, this.urx, this.ury);
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
     * @return a content where the glyph can be defined
     */
    public Type3Glyph createGlyph(char c, float wx, float llx, float lly, float urx, float ury) throws PdfException {
        usedSlot[c] = true;
        Integer ck = Integer.valueOf(c);
        Type3Glyph glyph = charGlyphs.get(ck);

        if (glyph != null) {
            return glyph;
        }
        widths.put(c, (int) wx);
        if (!isColor) {
            if (Float.isNaN(this.llx)) {
                this.llx = llx;
                this.lly = lly;
                this.urx = urx;
                this.ury = ury;
            } else {
                this.llx = Math.min(this.llx, llx);
                this.lly = Math.min(this.lly, lly);
                this.urx = Math.max(this.urx, urx);
                this.ury = Math.max(this.ury, ury);
            }
        }

        glyph = new Type3Glyph(getDocument());
        glyph.writeMetrics(wx, llx, lly, urx, ury, isColor);
        charGlyphs.put(ck, glyph);
        differences.add(new PdfNumber(ck));
        String s = AdobeGlyphList.unicodeToName(ck);

        if (s == null) {
            s = "a" + ck;
        }

        differences.add(new PdfName(s));

        return glyph;
    }

    public double[] getFontMatrix() {
        return fontMatrix;
    }

    public void setFontMatrix(double[] fontMatrix) {
        this.fontMatrix = fontMatrix;
    }


    @Override
    public float getWidth(int char1) {
        return widths.get(char1);
    }


    @Override
    public float getWidth(String text) {
        char[] c = text.toCharArray();
        int total = 0;
        for (int k = 0; k < c.length; ++k)
            total += getWidth(c[k]);
        return total;
    }

    @Override
    public byte[] convertToBytes(String text) {
        char[] cc = text.toCharArray();
        byte[] b = new byte[cc.length];
        int p = 0;
        for (int k = 0; k < cc.length; ++k) {
            char c = cc[k];
            if (charExists(c))
                b[p++] = (byte) c;
        }
        if (b.length == p)
            return b;
        byte[] b2 = new byte[p];
        System.arraycopy(b, 0, b2, 0, p);
        return b2;
    }

    public boolean charExists(int c) {
        if (c > 0 && c < 256) {
            return usedSlot[c];
        } else {
            return false;
        }
    }


    @Override
    public void flush() throws PdfException {

        int firstChar = 0;
        int lastChar = 0;
        int[] wd;
        PdfDictionary charProcs = new PdfDictionary();

        while (firstChar < usedSlot.length && !usedSlot[firstChar])
            firstChar++;

        if (firstChar == usedSlot.length) {
            throw new PdfException("no.glyphs.defined.for.type3.font");
        }

        lastChar = usedSlot.length - 1;

        while (lastChar >= firstChar && !usedSlot[lastChar])
            lastChar--;

        wd = new int[lastChar - firstChar + 1];

        int w = 0;
        for (int u = firstChar; u <= lastChar; u++, w++) {
            if (usedSlot[u]) {
                wd[w] = widths.get(u);
            }
        }

        for (Map.Entry<Integer, Type3Glyph> glyphEntry : charGlyphs.entrySet()) {
            String s = AdobeGlyphList.unicodeToName(glyphEntry.getKey().intValue());
            if (s != null) {
                charProcs.put(new PdfName(s), glyphEntry.getValue().getContentStream());
            }
        }

        fillFontParams(firstChar, lastChar, wd, differences, charProcs);
        super.flush();
    }

    private void checkFontDictionary() throws PdfException {
        if (this.fontDictionary == null || this.fontDictionary.get(PdfName.Subtype) == null
                || !this.fontDictionary.get(PdfName.Subtype).equals(PdfName.Type3)) {
            throw new PdfRuntimeException("Dictionary doesn't contain font data");
        }
    }


    private void init() throws PdfException {
        Rectangle fontBBoxRec = fontDictionary.getAsArray(PdfName.FontBBox).toRectangle();
        PdfDictionary charProcsDic = fontDictionary.getAsDictionary(PdfName.CharProcs);
        PdfArray fontMatrixArray = fontDictionary.getAsArray(PdfName.FontMatrix);
        differences = fontDictionary.getAsDictionary(PdfName.Encoding).getAsArray(PdfName.Differences);
        if (differences == null) {
            differences = new PdfArray();
        }
        this.llx = fontBBoxRec.getX();
        this.lly = fontBBoxRec.getY();
        this.urx = fontBBoxRec.getWidth();
        this.ury = fontBBoxRec.getHeight();
        int width[] = getWidths();

        for (int i = 0; i < fontMatrixArray.size(); i++) {
            fontMatrix[i] = ((PdfNumber) fontMatrixArray.get(i)).getValue();
        }

        for (int i = 0; i < width.length; i++) {
            if (width[i] != 0) {
                widths.put(i, width[i]);
                usedSlot[i] = true;
            }
        }

        Iterator<Map.Entry<PdfName, PdfObject>> it = charProcsDic.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PdfName, PdfObject> procsEntry = it.next();
            int[] val = AdobeGlyphList.nameToUnicode(procsEntry.getKey().getValue());
            if (val != null) {
                charGlyphs.put(val[0], new Type3Glyph(getDocument(), (((PdfStream) ((PdfIndirectReference) procsEntry.getValue()).getRefersTo())).getBytes()));
            }

        }
    }

    private void fillFontParams(int firstChar, int lastChar, int[] wd, PdfArray diffs, PdfDictionary charProcs) {
        getPdfObject().put(PdfName.Subtype, PdfName.Type3);

        if (isColor) {
            getPdfObject().put(PdfName.FontBBox, new PdfArray(new Rectangle(0, 0, 0, 0)));
        } else {
            getPdfObject().put(PdfName.FontBBox, new PdfArray(new Rectangle(this.llx, this.lly, this.urx, this.ury)));
        }

        getPdfObject().put(PdfName.FontMatrix, new PdfArray(fontMatrix));
        getPdfObject().put(PdfName.CharProcs, charProcs);
        PdfDictionary encoding = new PdfDictionary();
        encoding.put(PdfName.Type, PdfName.Encoding);
        encoding.put(PdfName.Differences, diffs);
        getPdfObject().put(PdfName.Encoding, encoding);
        getPdfObject().put(PdfName.FirstChar, new PdfNumber(firstChar));
        getPdfObject().put(PdfName.LastChar, new PdfNumber(lastChar));
        getPdfObject().put(PdfName.Widths, new PdfArray(wd));
        /*if (pdfPage.getResources()!=null) {
            getPdfObject().put(PdfName.Resources, pdfPage.getPdfObject());
        }*/
    }

    private int[] getWidths() throws PdfException {
        PdfArray newWidths = fontDictionary.getAsArray(PdfName.Widths);
        PdfNumber first = fontDictionary.getAsNumber(PdfName.FirstChar);
        PdfNumber last = fontDictionary.getAsNumber(PdfName.LastChar);
        int f = first.getIntValue();
        int nSize = f + newWidths.size();
        int[] tmp = new int[nSize];
        for (int k = 0; k < newWidths.size(); ++k) {
            tmp[f + k] = newWidths.getAsInt(k);
        }
        return tmp;
    }


}
