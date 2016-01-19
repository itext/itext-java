package com.itextpdf.canvas.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.AdobeGlyphList;
import com.itextpdf.basics.font.Type3Font;
import com.itextpdf.basics.font.otf.Glyph;
import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.font.PdfSimpleFont;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfNumber;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.PdfStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Low-level API class for Type 3 fonts.
 * 
 * In Type 3 fonts, glyphs are defined by streams of PDF graphics operators.
 * These streams are associated with character names. A separate encoding entry
 * maps character codes to the appropriate character names for the glyphs.
 */
public class PdfType3Font extends PdfSimpleFont<Type3Font> {

    private final boolean[] usedSlot = new boolean[256];

    private final Map<Integer, Type3Glyph> charGlyphs = new HashMap<>();

    private boolean isColor = false;

    /**
     * array of six numbers specifying the font matrix, mapping glyph space to text space
     */
    PdfArray differences = new PdfArray();

    /**
     * Creates a Type3 font.
     *
     * @param pdfDocument pdfDocument and only images as masks can be used
     * @param isColor defines whether the glyph color is specified in the glyph descriptions in the font.
     */
    public PdfType3Font(PdfDocument pdfDocument, boolean isColor) {
        super();
        makeIndirect(pdfDocument);
        this.isColor = isColor;
        fontProgram = new Type3Font();
    }

    /**
     * Creates a Type3 font based on an existing font dictionary.
     *
     * @param fontDictionary a dictionary of type <code>/Font</code>
     */
    public PdfType3Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        fontProgram = new Type3Font();
        checkFontDictionary(fontDictionary,PdfName.Type3);
        init();
    }

    public Map<Integer, Type3Glyph> getCharGlyphs() {
        return charGlyphs;
    }

    @Override
    public double[] getFontMatrix() {
        return fontProgram.getFontMatrix();
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
    public Type3Glyph createGlyph(char c, int wx, int llx, int lly, int urx, int ury) {
        usedSlot[c] = true;
        Integer ck = (int) c;
        Type3Glyph glyph = charGlyphs.get(ck);

        if (glyph != null) {
            return glyph;
        }

        if (!isColor) {
            Rectangle bbox = fontProgram.getFontMetrics().getBbox();
            if (fontProgram.countOfGlyphs() == 0) {
                bbox.setBbox(llx, lly, urx, ury);
            } else {
                float newLlx = Math.min(bbox.getLeft(), llx);
                float newLly = Math.min(bbox.getBottom(), lly);
                float newUrx = Math.max(bbox.getRight(), urx);
                float newUry = Math.max(bbox.getTop(), ury);
                bbox.setBbox(newLlx, newLly, newUrx, newUry);
            }
        }

        fontProgram.addGlyph(c, wx, new int[]{llx, lly, urx, ury});


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
    public Glyph getGlyph(int ch) {
        //TODO simplify!
        if (fontEncoding.canEncode(ch)) {
            Glyph glyph;
            if (fontEncoding.isFontSpecific()) {
                glyph = getFontProgram().getGlyphByCode(ch);
            } else {
                glyph = getFontProgram().getGlyph(ch);
            }
            return glyph;
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

    /**
     * Looks up existence of a character in this font
     * 
     * @param c a local glyph ID; should be in the range [0,256[
     * @return whether or not the character exists in the font
     */
    public boolean charExists(int c) {
        if (c >= 0 && c < 256) {
            return usedSlot[c];
        } else {
            return false;
        }
    }


    @Override
    public void flush() {

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
                wd[w] = fontProgram.getWidth(u);
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


    @Override
    protected Type3Font initializeTypeFontForCopy(String encodingName) {
        return new Type3Font();
    }

    @Override
    protected void init() {
        Rectangle fontBBoxRec = getPdfObject().getAsArray(PdfName.FontBBox).toRectangle();
        PdfDictionary charProcsDic = getPdfObject().getAsDictionary(PdfName.CharProcs);
        PdfArray fontMatrixArray = getPdfObject().getAsArray(PdfName.FontMatrix);
        differences = getPdfObject().getAsDictionary(PdfName.Encoding).getAsArray(PdfName.Differences);
        if (differences == null) {
            differences = new PdfArray();
        }
        fontProgram.getFontMetrics().getBbox()
                .setBbox(fontBBoxRec.getX(), fontBBoxRec.getY(), fontBBoxRec.getWidth(), fontBBoxRec.getHeight());
        int width[] = getWidths();
        double[] fontMatrix = new double[6];
        for (int i = 0; i < fontMatrixArray.size(); i++) {
            fontMatrix[i] = ((PdfNumber) fontMatrixArray.get(i)).getValue();
        }
        fontProgram.setFontMatrix(fontMatrix);

        for (int i = 0; i < width.length; i++) {
            if (width[i] != 0) {
                fontProgram.addGlyph(i, width[i], null);
                usedSlot[i] = true;
            }
        }

        Iterator<Map.Entry<PdfName, PdfObject>> it = charProcsDic.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<PdfName, PdfObject> procsEntry = it.next();
            Integer val = AdobeGlyphList.nameToUnicode(procsEntry.getKey().getValue());
            if (val != null) {
                charGlyphs.put(val, new Type3Glyph(getDocument(), (((PdfStream) ((PdfIndirectReference) procsEntry.getValue()).getRefersTo())).getBytes()));
            }

        }
    }

    private void fillFontParams(int firstChar, int lastChar, int[] wd, PdfArray diffs, PdfDictionary charProcs) {
        getPdfObject().put(PdfName.Subtype, PdfName.Type3);

        if (isColor) {
            getPdfObject().put(PdfName.FontBBox, new PdfArray(new Rectangle(0, 0, 0, 0)));
        } else {
            getPdfObject().put(PdfName.FontBBox, new PdfArray(new Rectangle(fontProgram.getFontMetrics().getBbox().clone())));
        }

        getPdfObject().put(PdfName.FontMatrix, new PdfArray(fontProgram.getFontMatrix()));
        getPdfObject().put(PdfName.CharProcs, charProcs);
        PdfDictionary encoding = new PdfDictionary();
        encoding.put(PdfName.Type, PdfName.Encoding);
        encoding.put(PdfName.Differences, diffs);
        getPdfObject().put(PdfName.Encoding, encoding);
        getPdfObject().put(PdfName.FirstChar, new PdfNumber(firstChar));
        getPdfObject().put(PdfName.LastChar, new PdfNumber(lastChar));
        getPdfObject().put(PdfName.Widths, new PdfArray(wd));

        if(getPdfObject() != null) {
            PdfObject toUnicode = getPdfObject().get(PdfName.ToUnicode);
            if (toUnicode != null) {
                if (toUnicode instanceof PdfStream) {
                    PdfStream newStream = (PdfStream) toUnicode.clone();
                    getPdfObject().put(PdfName.ToUnicode, newStream);
                    newStream.flush();
                }
            }

            PdfDictionary fromDescriptorDictionary = getPdfObject().getAsDictionary(PdfName.FontDescriptor);
            if (fromDescriptorDictionary != null) {
                PdfDictionary toDescriptorDictionary = getNewFontDescriptor(fromDescriptorDictionary);
                getPdfObject().put(PdfName.FontDescriptor, toDescriptorDictionary);
                toDescriptorDictionary.flush();
            }
        }
    }

    private int[] getWidths() {
        PdfArray newWidths = getPdfObject().getAsArray(PdfName.Widths);
        PdfNumber first = getPdfObject().getAsNumber(PdfName.FirstChar);
        PdfNumber last = getPdfObject().getAsNumber(PdfName.LastChar);
        int f = first.getIntValue();
        int nSize = f + newWidths.size();
        int[] tmp = new int[nSize];
        for (int k = 0; k < newWidths.size(); ++k) {
            tmp[f + k] = newWidths.getAsInt(k);
        }
        return tmp;
    }


}
