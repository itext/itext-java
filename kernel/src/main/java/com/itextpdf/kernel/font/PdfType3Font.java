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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.constants.FontDescriptorFlags;
import com.itextpdf.io.font.constants.FontStretches;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Low-level API class for Type 3 fonts.
 * <p>
 * In Type 3 fonts, glyphs are defined by streams of PDF graphics operators.
 * These streams are associated with character names. A separate encoding entry
 * maps character codes to the appropriate character names for the glyphs.
 * <p>
 * <br><br>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfType3Font extends PdfSimpleFont<Type3Font> {

    private static final long serialVersionUID = 4940119184993066859L;
    private double[] fontMatrix = DEFAULT_FONT_MATRIX;

    /**
     * Creates a Type 3 font.
     *
     * @param colorized defines whether the glyph color is specified in the glyph descriptions in the font.
     */
    PdfType3Font(PdfDocument document, boolean colorized) {
        super();
        makeIndirect(document);
        subset = true;
        embedded = true;
        fontProgram = new Type3Font(colorized);
        fontEncoding = FontEncoding.createEmptyFontEncoding();
    }

    /**
     * Creates a Type 3 font.
     *
     * @param document   the target document of the new font.
     * @param fontName   the PostScript name of the font, shall not be null or empty.
     * @param fontFamily a preferred font family name.
     * @param colorized  indicates whether the font will be colorized
     */
    PdfType3Font(PdfDocument document, String fontName, String fontFamily, boolean colorized) {
        this(document, colorized);
        ((Type3Font) fontProgram).setFontName(fontName);
        ((Type3Font) fontProgram).setFontFamily(fontFamily);
    }

    /**
     * Creates a Type 3 font based on an existing font dictionary, which must be an indirect object.
     *
     * @param fontDictionary a dictionary of type <code>/Font</code>, must have an indirect reference.
     */
    PdfType3Font(PdfDictionary fontDictionary) {
        super(fontDictionary);
        subset = true;
        embedded = true;
        fontProgram = new Type3Font(false);
        fontEncoding = DocFontEncoding.createDocFontEncoding(fontDictionary.get(PdfName.Encoding), toUnicode);
        PdfDictionary charProcsDic = getPdfObject().getAsDictionary(PdfName.CharProcs);
        PdfArray fontMatrixArray = getPdfObject().getAsArray(PdfName.FontMatrix);
        if (getPdfObject().containsKey(PdfName.FontBBox)) {
            PdfArray fontBBox = getPdfObject().getAsArray(PdfName.FontBBox);
            fontProgram.getFontMetrics().setBbox(fontBBox.getAsNumber(0).intValue(), fontBBox.getAsNumber(1).intValue(),
                    fontBBox.getAsNumber(2).intValue(), fontBBox.getAsNumber(3).intValue());
        } else {
            fontProgram.getFontMetrics().setBbox(0, 0, 0, 0);
        }
        int firstChar = normalizeFirstLastChar(fontDictionary.getAsNumber(PdfName.FirstChar), 0);
        int lastChar = normalizeFirstLastChar(fontDictionary.getAsNumber(PdfName.LastChar), 255);
        for (int i = firstChar; i <= lastChar; i++) {
            shortTag[i] = 1;
        }
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
                ((Type3Font) getFontProgram()).addGlyph(code, unicode, widths[code], null, new Type3Glyph(charProcsDic.getAsStream(glyphName), getDocument()));
            }
        }
        fillFontDescriptor(fontDictionary.getAsDictionary(PdfName.FontDescriptor));
    }

    /**
     * Sets the PostScript name of the font.
     *
     * @param fontName the PostScript name of the font, shall not be null or empty.
     */
    public void setFontName(String fontName) {
        ((Type3Font) fontProgram).setFontName(fontName);
    }

    /**
     * Sets a preferred font family name.
     *
     * @param fontFamily a preferred font family name.
     */
    public void setFontFamily(String fontFamily) {
        ((Type3Font) fontProgram).setFontFamily(fontFamily);
    }

    /**
     * Sets font weight.
     *
     * @param fontWeight integer form 100 to 900. See {@link FontWeights}.
     */
    public void setFontWeight(int fontWeight) {
        ((Type3Font) fontProgram).setFontWeight(fontWeight);
    }

    /**
     * Sets the PostScript italic angle.
     * <br/>
     * Italic angle in counter-clockwise degrees from the vertical. Zero for upright text, negative for text that leans to the right (forward).
     *
     * @param italicAngle in counter-clockwise degrees from the vertical
     */
    public void setItalicAngle(int italicAngle) {
        ((Type3Font) fontProgram).setItalicAngle(italicAngle);
    }

    /**
     * Sets font width in css notation (font-stretch property)
     *
     * @param fontWidth {@link FontStretches}.
     */
    public void setFontStretch(String fontWidth) {
        ((Type3Font) fontProgram).setFontStretch(fontWidth);
    }

    /**
     * Sets Font descriptor flags.
     *
     * @param flags font descriptor flags.
     * @see FontDescriptorFlags
     */
    public void setPdfFontFlags(int flags) {
        ((Type3Font) fontProgram).setPdfFontFlags(flags);
    }


    public Type3Glyph getType3Glyph(int unicode) {
        return ((Type3Font) getFontProgram()).getType3Glyph(unicode);
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
     * Gets count of glyphs in Type 3 font.
     *
     * @return number of glyphs.
     */
    public int getNumberOfGlyphs() {
        return ((Type3Font) getFontProgram()).getNumberOfGlyphs();
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
        glyph = new Type3Glyph(getDocument(), wx, llx, lly, urx, ury, ((Type3Font) getFontProgram()).isColorized());
        ((Type3Font) getFontProgram()).addGlyph(code, c, wx, new int[]{llx, lly, urx, ury}, glyph);
        fontEncoding.addSymbol((byte) code, c);

        if (!((Type3Font) getFontProgram()).isColorized()) {
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
    public boolean containsGlyph(int unicode) {
        return (fontEncoding.canEncode(unicode) || unicode < 33)
                && getFontProgram().getGlyph(fontEncoding.getUnicodeDifference(unicode)) != null;
    }

    @Override
    public void flush() {
        if (isFlushed()) return;
        ensureUnderlyingObjectHasIndirectReference();
        if (((Type3Font) getFontProgram()).getNumberOfGlyphs() < 1) {
            throw new PdfException("No glyphs defined for type3 font.");
        }

        PdfDictionary charProcs = new PdfDictionary();
        for (int i = 0; i < 256; i++) {
            if (fontEncoding.canDecode(i)) {
                Type3Glyph glyph = getType3Glyph(fontEncoding.getUnicode(i));
                if (glyph != null) {
                    charProcs.put(new PdfName(fontEncoding.getDifference(i)), glyph.getContentStream());
                    glyph.getContentStream().flush();
                }
            }
        }

        getPdfObject().put(PdfName.CharProcs, charProcs);
        getPdfObject().put(PdfName.FontMatrix, new PdfArray(getFontMatrix()));
        getPdfObject().put(PdfName.FontBBox, new PdfArray(fontProgram.getFontMetrics().getBbox()));

        String fontName = fontProgram.getFontNames().getFontName();
        super.flushFontData(fontName, PdfName.Type3);
        //BaseFont is not listed as key in Type 3 font specification.
        getPdfObject().remove(PdfName.BaseFont);
        super.flush();
    }

    @Override
    protected PdfDictionary getFontDescriptor(String fontName) {
        if (fontName != null && fontName.length() > 0) {
            PdfDictionary fontDescriptor = new PdfDictionary();
            makeObjectIndirect(fontDescriptor);
            fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);

            FontMetrics fontMetrics = fontProgram.getFontMetrics();
            fontDescriptor.put(PdfName.CapHeight, new PdfNumber(fontMetrics.getCapHeight()));
            fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(fontMetrics.getItalicAngle()));

            FontNames fontNames = fontProgram.getFontNames();
            fontDescriptor.put(PdfName.FontWeight, new PdfNumber(fontNames.getFontWeight()));
            fontDescriptor.put(PdfName.FontName, new PdfName(fontName));
            if (fontNames.getFamilyName() != null && fontNames.getFamilyName().length > 0 && fontNames.getFamilyName()[0].length >= 4) {
                fontDescriptor.put(PdfName.FontFamily, new PdfString(fontNames.getFamilyName()[0][3]));
            }

            int flags = fontProgram.getPdfFontFlags();
            flags &= ~(FontDescriptorFlags.Symbolic | FontDescriptorFlags.Nonsymbolic); // reset both flags
            flags |= fontEncoding.isFontSpecific() ? // set based on font encoding
                    FontDescriptorFlags.Symbolic : FontDescriptorFlags.Nonsymbolic;

            fontDescriptor.put(PdfName.Flags, new PdfNumber(flags));
            return fontDescriptor;
        } else if (getPdfObject().getIndirectReference() != null
                && getPdfObject().getIndirectReference().getDocument().isTagged()) {
            Logger logger = LoggerFactory.getLogger(PdfType3Font.class);
            logger.warn(LogMessageConstant.TYPE3_FONT_ISSUE_TAGGED_PDF);
        }
        return null;
    }

    @Override
    protected void addFontStream(PdfDictionary fontDescriptor) {
    }

    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    /**
     * Gets first empty code, that could use with {@see addSymbol()}
     *
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

    private void fillFontDescriptor(PdfDictionary fontDesc) {
        if (fontDesc == null) {
            return;
        }
        PdfNumber v = fontDesc.getAsNumber(PdfName.ItalicAngle);
        if (v != null) {
            setItalicAngle(v.intValue());
        }
        v = fontDesc.getAsNumber(PdfName.FontWeight);
        if (v != null) {
            setFontWeight(v.intValue());
        }

        PdfName fontStretch = fontDesc.getAsName(PdfName.FontStretch);
        if (fontStretch != null) {
            setFontStretch(fontStretch.getValue());
        }

        PdfName fontName = fontDesc.getAsName(PdfName.FontName);
        if (fontName != null) {
            setFontName(fontName.getValue());
        }

        PdfString fontFamily = fontDesc.getAsString(PdfName.FontFamily);
        if (fontFamily != null) {
            setFontFamily(fontFamily.getValue());
        }
    }

    private int normalizeFirstLastChar(PdfNumber firstLast, int defaultValue) {
        if (firstLast == null) return defaultValue;
        int result = firstLast.intValue();
        return result < 0 || result > 255 ? defaultValue : result;
    }
}
