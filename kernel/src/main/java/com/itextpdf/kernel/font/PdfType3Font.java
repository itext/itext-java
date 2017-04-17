/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

/**
 * Low-level API class for Type 3 fonts.
 * <p/>
 * In Type 3 fonts, glyphs are defined by streams of PDF graphics operators.
 * These streams are associated with character names. A separate encoding entry
 * maps character codes to the appropriate character names for the glyphs.
 *
 * <br><br>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfType3Font extends PdfSimpleFont<Type3FontProgram> {

    private static final long serialVersionUID = 4940119184993066859L;
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
     * Creates a Type3 font based on an existing font dictionary, which must be an indirect object.
     *
     * @param fontDictionary a dictionary of type <code>/Font</code>, must have an indirect reference.
     */
    PdfType3Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        ensureObjectIsAddedToDocument(fontDictionary);
        subset = true;
        embedded = true;
        fontProgram = new Type3FontProgram(false);
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), null, false);
        PdfDictionary charProcsDic = getPdfObject().getAsDictionary(PdfName.CharProcs);
        PdfArray fontMatrixArray = getPdfObject().getAsArray(PdfName.FontMatrix);
        if (getPdfObject().containsKey(PdfName.FontBBox)) {
            PdfArray fontBBox = getPdfObject().getAsArray(PdfName.FontBBox);
            fontProgram.getFontMetrics().setBbox(fontBBox.getAsNumber(0).intValue(), fontBBox.getAsNumber(1).intValue(),
                    fontBBox.getAsNumber(2).intValue(), fontBBox.getAsNumber(3).intValue());
        } else {
            fontProgram.getFontMetrics().setBbox(0, 0, 0, 0);
        }
        PdfNumber firstCharNumber = fontDictionary.getAsNumber(PdfName.FirstChar);
        int firstChar = firstCharNumber != null ? Math.max(firstCharNumber.intValue(), 0) : 0;
        int[] widths = FontUtil.convertSimpleWidthsArray(fontDictionary.getAsArray(PdfName.Widths), firstChar, 0);
        double[] fontMatrix = new double[6];
        for (int i = 0; i < fontMatrixArray.size(); i++) {
            fontMatrix[i] = ((PdfNumber) fontMatrixArray.get(i)).getValue();
        }
        setFontMatrix(fontMatrix);

        for (PdfName glyphName : charProcsDic.keySet()) {
            int unicode = AdobeGlyphList.nameToUnicode(glyphName.getValue());
            if (unicode != -1 && fontEncoding.canEncode(unicode)) {
                int code = fontEncoding.convertToByte(unicode);
                getFontProgram().addGlyph(code, unicode, widths[code], null, new Type3Glyph(charProcsDic.getAsStream(glyphName), getDocument()));
            }
        }
    }

    public Type3Glyph getType3Glyph(int unicode) {
        return getFontProgram().getType3Glyph(unicode);
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
        glyph = new Type3Glyph(getDocument(), wx, llx, lly, urx, ury, getFontProgram().isColorized());
        getFontProgram().addGlyph(code, c, wx, new int[]{llx, lly, urx, ury}, glyph);
        fontEncoding.addSymbol((byte) code, c);

        if (!getFontProgram().isColorized()) {
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
        if (fontEncoding.canEncode(unicode) || unicode < 33) {
            Glyph glyph = getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode));
                if (glyph == null && (glyph = notdefGlyphs.get(unicode)) == null) {
                    // Handle special layout characters like sfthyphen (00AD).
                    // This glyphs will be skipped while converting to bytes
                    glyph = new Glyph(-1, 0, unicode);
                    notdefGlyphs.put(unicode, glyph);
                }
            return glyph;
        }
        return null;
    }

    @Override
    public boolean containsGlyph(String text, int from) {
        return containsGlyph((int) text.charAt(from));
    }

    @Override
    public boolean containsGlyph(int unicode) {
        return (fontEncoding.canEncode(unicode) || unicode < 33)
                && getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode)) != null;
    }

    @Override
    protected PdfDictionary getFontDescriptor(String fontName) {
        return null;
    }

    @Override
    protected void addFontStream(PdfDictionary fontDescriptor) {
    }

    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    @Override
    public void flush() {
        ensureUnderlyingObjectHasIndirectReference();
        if (getFontProgram().getGlyphsCount() < 1) {
            throw new PdfException("no.glyphs.defined.fo r.type3.font");
        }
        PdfDictionary charProcs = new PdfDictionary();
        for (int i = 0; i < 256; i++) {
            if (fontEncoding.canDecode(i)) {
                Type3Glyph glyph = getType3Glyph(fontEncoding.getUnicode(i));
                charProcs.put(new PdfName(fontEncoding.getDifference(i)), glyph.getContentStream());
                glyph.getContentStream().flush();
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
