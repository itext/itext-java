package com.itextpdf.canvas.font;


import com.itextpdf.basics.IntHashtable;
import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.AdobeGlyphList;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class PdfType3Font extends PdfFont {

    private boolean[] usedSlot = new boolean[256];

    private IntHashtable widths = new IntHashtable();

    private HashMap<Integer, Type3Glyph> charGlyph = new HashMap<Integer, Type3Glyph>();

    private float wx;

    private float llx = Float.NaN;

    private float lly;

    private float urx;

    private float ury;

    private boolean isColor = false;

    /** array of six numbers specifying the font matrix, mapping glyph space to text space */
    private double[] fontMatrix = {0.001, 0, 0, 0.001, 0, 0};

    /**
     * Creates a Type3 font.
     * @param pdfDocument pdfDocument and only images as masks can be used
     */
    public PdfType3Font(PdfDocument pdfDocument) throws PdfException {
        super(pdfDocument);
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
            listGlyph.add(createGlyph(c, this.wx, this.llx, this.lly, this.urx, this.ury, this.isColor));
        }
        return listGlyph;
    }

    public Type3Glyph createGlyph(char c) throws PdfException {
        return createGlyph(c, this.wx, this.llx, this.lly, this.urx, this.ury, this.isColor);
    }

    /**
     * Defines a glyph. If the character was already defined it will return the same content
     *
     * @param c       the character to match this glyph.
     * @param wx      the advance this character will have
     * @param llx     the X lower left corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *                <CODE>true</CODE> the value is ignored
     * @param lly     the Y lower left corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *                <CODE>true</CODE> the value is ignored
     * @param urx     the X upper right corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *                <CODE>true</CODE> the value is ignored
     * @param ury     the Y upper right corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     * @param isColor <CODE>true</CODE> the value is ignored
     * @return a content where the glyph can be defined
     */
    public Type3Glyph createGlyph(char c, float wx, float llx, float lly, float urx, float ury, boolean isColor) throws PdfException {
        usedSlot[c] = true;
        Integer ck = Integer.valueOf(c);
        Type3Glyph glyph = charGlyph.get(ck);
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
        charGlyph.put(ck, glyph);
        return glyph;
    }

    public double[] getFontMatrix() {
        return fontMatrix;
    }

    public void setFontMatrix(double[] fontMatrix) {
        this.fontMatrix = fontMatrix;
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
        int[] wd = null;
        PdfArray diffs = new PdfArray();
        PdfDictionary charprocs = new PdfDictionary();

        while (firstChar < usedSlot.length && !usedSlot[firstChar])
            firstChar++;

        if (firstChar == usedSlot.length) {
            throw new PdfException("no.glyphs.defined.for.type3.font");
        }

        lastChar = usedSlot.length - 1;

        while (lastChar >= firstChar && !usedSlot[lastChar])
            lastChar--;

        wd = new int[lastChar - firstChar + 1];
        int[] invOrd = new int[lastChar - firstChar + 1];

        int invOrdIndx = 0, w = 0;
        for (int u = firstChar; u <= lastChar; u++, w++) {
            if (usedSlot[u]) {
                invOrd[invOrdIndx++] = u;
                wd[w] = widths.get(u);
            }
        }
        int last = -1;
        for (int k = 0; k < invOrdIndx; ++k) {
            int c = invOrd[k];

            if (c > last) {
                last = c;
                diffs.add(new PdfNumber(last));
            }

            ++last;
            int c2 = invOrd[k];
            String s = AdobeGlyphList.unicodeToName(c2);

            if (s == null) {
                s = "a" + c2;
            }

            PdfName n = new PdfName(s);
            diffs.add(n);
            Type3Glyph glyph = charGlyph.get(Integer.valueOf(c2));
            charprocs.put(n, glyph.getContentStream());
        }

        setFontParams(firstChar, lastChar, wd, diffs, charprocs);

        super.flush();
    }

    private void setFontParams(int firstChar, int lastChar, int[] wd, PdfArray diffs, PdfDictionary charprocs) {
        getPdfObject().put(PdfName.Subtype, PdfName.Type3);
        if (isColor) {
            getPdfObject().put(PdfName.FontBBox, new PdfArray(new Rectangle(0, 0, 0, 0)));
        } else {
            getPdfObject().put(PdfName.FontBBox, new PdfArray(new Rectangle(llx, lly, urx, ury)));
        }
        getPdfObject().put(PdfName.FontMatrix, new PdfArray(fontMatrix));
        getPdfObject().put(PdfName.CharProcs, charprocs);
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


}
