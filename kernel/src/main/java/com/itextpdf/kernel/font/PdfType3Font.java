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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.font.AdobeGlyphList;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontMetrics;
import com.itextpdf.io.font.FontNames;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.constants.FontDescriptorFlags;
import com.itextpdf.io.font.constants.FontStretches;
import com.itextpdf.io.font.constants.FontWeights;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
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
import java.util.Map;

/**
 * Low-level API class for Type 3 fonts.
 * <p>
 * In Type 3 fonts, glyphs are defined by streams of PDF graphics operators.
 * These streams are associated with character names. A separate encoding entry
 * maps character codes to the appropriate character names for the glyphs.
 *
 * <p>
 * Note, that this class operates in a special way with glyph space units.
 * In the code when working with fonts, iText expects that 1000 units of glyph-space correspond
 * to 1 unit of text space. For Type3 fonts this is not always the case and depends on FontMatrix.
 * That's why in {@link PdfType3Font} the font matrix and all font metrics in glyph space units
 * are "normalized" in such way, that 1 to 1000 relation is preserved. This is done on
 * Type3 font initialization, and is reverted back on font flushing, because the actual content
 * streams of type3 font glyphs are left with original coordinates based on original font matrix.
 * See also ISO-32000-2, 9.2.4 "Glyph positioning and metrics":
 *
 * <p>
 * "The glyph coordinate system is the space in which an individual character’s glyph is defined. All path
 * coordinates and metrics shall be interpreted in glyph space. For all font types except Type 3, the units
 * of glyph space are one-thousandth of a unit of text space; for a Type 3 font, the transformation from
 * glyph space to text space shall be defined by a font matrix specified in an explicit FontMatrix entry in
 * the font."
 *
 * <p>
 * Note, that because of this when processing Type3 glyphs content streams either process them completely independent
 * from this class or take this normalization into account.
 *
 * <p>
 * To be able to be wrapped with this {@link PdfObjectWrapper} the {@link PdfObject}
 * must be indirect.
 */
public class PdfType3Font extends PdfSimpleFont<Type3Font> {


    private static final int FONT_BBOX_LLX = 0;
    private static final int FONT_BBOX_LLY = 1;
    private static final int FONT_BBOX_URX = 2;
    private static final int FONT_BBOX_URY = 3;
    private static final double[] DEFAULT_FONT_MATRIX = {0.001, 0, 0, 0.001, 0, 0};

    private double[] fontMatrix = DEFAULT_FONT_MATRIX;

    /**
     * Used to normalize font metrics expressed in glyph space units. See {@link PdfType3Font}.
     */
    private double glyphSpaceNormalizationFactor;

    /**
     * Gets the transformation matrix that defines relation between text and glyph spaces.
     *
     * @return the font matrix
     */
    private double[] getFontMatrix() {
        return this.fontMatrix;
    }

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
        setGlyphSpaceNormalizationFactor(1.0f);
    }

    /**
     * Creates a Type 3 font.
     *
     * @param document the target document of the new font.
     * @param fontName the PostScript name of the font, shall not be null or empty.
     * @param fontFamily a preferred font family name.
     * @param colorized indicates whether the font will be colorized
     */
    PdfType3Font(PdfDocument document, String fontName, String fontFamily, boolean colorized) {
        this(document, colorized);
        ((Type3Font) fontProgram).setFontName(fontName);
        ((Type3Font) fontProgram).setFontFamily(fontFamily);
        setGlyphSpaceNormalizationFactor(1.0f);
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

        double[] fontMatrixArray = readFontMatrix();
        double[] fontBBoxRect = readFontBBox();
        double[] widthsArray = readWidths(fontDictionary);

        setGlyphSpaceNormalizationFactor(FontProgram.convertGlyphSpaceToTextSpace(fontMatrixArray[0]));

        PdfDictionary charProcsDic = fontDictionary.getAsDictionary(PdfName.CharProcs);
        PdfDictionary encoding = fontDictionary.getAsDictionary(PdfName.Encoding);
        PdfArray differences = encoding != null ? encoding.getAsArray(PdfName.Differences) : null;
        if (charProcsDic == null || differences == null) {
            LoggerFactory.getLogger(getClass()).warn(IoLogMessageConstant.TYPE3_FONT_INITIALIZATION_ISSUE);
        }
        fillFontDescriptor(fontDictionary.getAsDictionary(PdfName.FontDescriptor));

        normalize1000UnitsToGlyphSpaceUnits(fontMatrixArray);
        normalizeGlyphSpaceUnitsTo1000Units(fontBBoxRect);
        normalizeGlyphSpaceUnitsTo1000Units(widthsArray);

        int firstChar = initializeUsedGlyphs(fontDictionary);
        fontMatrix = fontMatrixArray;
        initializeFontBBox(fontBBoxRect);
        initializeTypoAscenderDescender(fontBBoxRect);

        int[] widths = new int[256];
        for (int i = 0; i < widthsArray.length && firstChar + i < 256; i++) {
            widths[firstChar + i] = (int) (widthsArray[i]);
        }
        addGlyphsFromDifferences(differences, charProcsDic, widths);
        addGlyphsFromCharProcs(charProcsDic, widths);
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
     * Sets cap height.
     *
     * @param capHeight integer in glyph-space 1000-units
     */
    public void setCapHeight(int capHeight) {
        ((Type3Font) fontProgram).setCapHeight(capHeight);
    }

    /**
     * Sets the PostScript italic angle.
     * <p>
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

    /**
     * Returns a {@link Type3Glyph} by unicode.
     *
     * @param unicode glyph unicode
     *
     * @return {@link Type3Glyph} glyph, or {@code null} if this font does not contain glyph for the unicode
     */
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
     * @param c the character to match this glyph.
     * @param wx the advance this character will have
     * @param llx the X lower left corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *            <CODE>true</CODE> the value is ignored
     * @param lly the Y lower left corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *            <CODE>true</CODE> the value is ignored
     * @param urx the X upper right corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *            <CODE>true</CODE> the value is ignored
     * @param ury the Y upper right corner of the glyph bounding box. If the <CODE>colorize</CODE> option is
     *            <CODE>true</CODE> the value is ignored
     *
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
        fontEncoding.addSymbol(code, c);

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
        flushFontData();
        super.flush();
    }

    @Override
    protected PdfDictionary getFontDescriptor(String fontName) {
        if (fontName != null && fontName.length() > 0) {
            PdfDictionary fontDescriptor = new PdfDictionary();
            makeObjectIndirect(fontDescriptor);
            fontDescriptor.put(PdfName.Type, PdfName.FontDescriptor);

            FontMetrics fontMetrics = fontProgram.getFontMetrics();

            int capHeight = fontMetrics.getCapHeight();
            fontDescriptor.put(PdfName.CapHeight, new PdfNumber(normalize1000UnitsToGlyphSpaceUnits(capHeight)));
            fontDescriptor.put(PdfName.ItalicAngle, new PdfNumber(fontMetrics.getItalicAngle()));

            FontNames fontNames = fontProgram.getFontNames();
            fontDescriptor.put(PdfName.FontWeight, new PdfNumber(fontNames.getFontWeight()));
            fontDescriptor.put(PdfName.FontName, new PdfName(fontName));
            if (fontNames.getFamilyName() != null && fontNames.getFamilyName().length > 0 && fontNames.getFamilyName()[0].length >= 4) {
                fontDescriptor.put(PdfName.FontFamily, new PdfString(fontNames.getFamilyName()[0][3]));
            }

            int flags = fontProgram.getPdfFontFlags();
            // reset both flags
            flags &= ~(FontDescriptorFlags.SYMBOLIC | FontDescriptorFlags.NONSYMBOLIC);
            // set fontSpecific based on font encoding
            flags |= fontEncoding.isFontSpecific() ?
                    FontDescriptorFlags.SYMBOLIC : FontDescriptorFlags.NONSYMBOLIC;

            fontDescriptor.put(PdfName.Flags, new PdfNumber(flags));
            return fontDescriptor;
        } else if (getPdfObject().getIndirectReference() != null
                && getPdfObject().getIndirectReference().getDocument().isTagged()) {
            Logger logger = LoggerFactory.getLogger(PdfType3Font.class);
            logger.warn(IoLogMessageConstant.TYPE3_FONT_ISSUE_TAGGED_PDF);
        }
        return null;
    }

    @Override
    protected PdfArray buildWidthsArray(int firstChar, int lastChar) {
        double[] widths = new double[lastChar - firstChar + 1];
        for (int k = firstChar; k <= lastChar; ++k) {
            int i = k - firstChar;
            if (usedGlyphs[k] == 0) {
                widths[i] = 0;
            } else {
                int uni = getFontEncoding().getUnicode(k);
                Glyph glyph = uni > -1 ? getGlyph(uni) : getFontProgram().getGlyphByCode(k);
                widths[i] = glyph != null ? glyph.getWidth() : 0;
            }
        }
        normalize1000UnitsToGlyphSpaceUnits(widths);
        return new PdfArray(widths);
    }

    @Override
    protected void addFontStream(PdfDictionary fontDescriptor) {
    }

    protected PdfDocument getDocument() {
        return getPdfObject().getIndirectReference().getDocument();
    }

    final double getGlyphSpaceNormalizationFactor() {
        return glyphSpaceNormalizationFactor;
    }

    final void setGlyphSpaceNormalizationFactor(double glyphSpaceNormalizationFactor) {
        this.glyphSpaceNormalizationFactor = glyphSpaceNormalizationFactor;
    }

    private void addGlyphsFromDifferences(PdfArray differences, PdfDictionary charProcsDic, int[] widths) {
        if (differences == null || charProcsDic == null) {
            return;
        }

        int currentNumber = 0;
        for (int k = 0; k < differences.size(); ++k) {
            PdfObject obj = differences.get(k);
            if (obj.isNumber()) {
                currentNumber = ((PdfNumber) obj).intValue();
            } else if (currentNumber > SIMPLE_FONT_MAX_CHAR_CODE_VALUE) {
                // Skip glyphs with id greater than 255
            } else {
                String glyphName = ((PdfName) obj).getValue();
                int unicode = fontEncoding.getUnicode(currentNumber);
                if (getFontProgram().getGlyphByCode(currentNumber) == null
                        && charProcsDic.containsKey(new PdfName(glyphName))) {
                    fontEncoding.setDifference(currentNumber, glyphName);
                    ((Type3Font) getFontProgram()).addGlyph(currentNumber, unicode, widths[currentNumber], null,
                            new Type3Glyph(charProcsDic.getAsStream(new PdfName(glyphName)), getDocument()));
                }
                currentNumber++;
            }
        }
    }

    /**
     * Gets the first empty code that could be passed to {@link FontEncoding#addSymbol(int, int)}
     *
     * @return code from 1 to 255 or -1 if all slots are busy.
     */
    private int getFirstEmptyCode() {
        final int startFrom = 1;
        for (int i = startFrom; i <= PdfFont.SIMPLE_FONT_MAX_CHAR_CODE_VALUE; i++) {
            if (!fontEncoding.canDecode(i) && fontProgram.getGlyphByCode(i) == null) {
                return i;
            }
        }
        return -1;
    }

    private void addGlyphsFromCharProcs(PdfDictionary charProcsDic, int[] widths) {
        if (charProcsDic == null) {
            return;
        }
        Map<Integer, Integer> unicodeToCode = null;
        if (getToUnicode() != null) {
            try { unicodeToCode = getToUnicode().createReverseMapping(); } catch (Exception e) {/*ignored*/}
        }

        for (PdfName glyphName : charProcsDic.keySet()) {
            int unicode = AdobeGlyphList.nameToUnicode(glyphName.getValue());
            int code = -1;
            if (fontEncoding.canEncode(unicode)) {
                code = fontEncoding.convertToByte(unicode);
            } else if (unicodeToCode != null && unicodeToCode.containsKey(unicode)) {
                code = (int) unicodeToCode.get(unicode);
            }
            if (code != -1 && getFontProgram().getGlyphByCode(code) == null) {
                ((Type3Font) getFontProgram()).addGlyph(code, unicode, widths[code],
                        null, new Type3Glyph(charProcsDic.getAsStream(glyphName), getDocument()));
            }
        }
    }

    private void flushFontData() {
        if (((Type3Font) getFontProgram()).getNumberOfGlyphs() < 1) {
            throw new PdfException(KernelExceptionMessageConstant.NO_GLYPHS_DEFINED_FOR_TYPE_3_FONT);
        }
        PdfDictionary charProcs = new PdfDictionary();
        for (int i = 0; i <= PdfFont.SIMPLE_FONT_MAX_CHAR_CODE_VALUE; i++) {
            Type3Glyph glyph = null;
            if (fontEncoding.canDecode(i)) {
                glyph = getType3Glyph(fontEncoding.getUnicode(i));
            }
            if (glyph == null) {
                glyph = ((Type3Font) getFontProgram()).getType3GlyphByCode(i);
            }
            if (glyph != null) {
                charProcs.put(new PdfName(fontEncoding.getDifference(i)), glyph.getContentStream());
                glyph.getContentStream().flush();
            }
        }
        getPdfObject().put(PdfName.CharProcs, charProcs);

        double[] fontMatrixDouble = getFontMatrix();
        int[] fontBBoxInt = getFontProgram().getFontMetrics().getBbox();
        double[] fontBBoxDouble = new double[] {
                fontBBoxInt[FONT_BBOX_LLX], fontBBoxInt[FONT_BBOX_LLY],
                fontBBoxInt[FONT_BBOX_URX], fontBBoxInt[FONT_BBOX_URY]};

        normalizeGlyphSpaceUnitsTo1000Units(fontMatrixDouble);
        normalize1000UnitsToGlyphSpaceUnits(fontBBoxDouble);

        getPdfObject().put(PdfName.FontMatrix, new PdfArray(fontMatrixDouble));
        getPdfObject().put(PdfName.FontBBox, new PdfArray(fontBBoxDouble));
        String fontName = fontProgram.getFontNames().getFontName();
        super.flushFontData(fontName, PdfName.Type3);
        makeObjectIndirect(getPdfObject().get(PdfName.Widths));
        //BaseFont is not listed as key in Type 3 font specification.
        getPdfObject().remove(PdfName.BaseFont);
    }

    private double[] readWidths(PdfDictionary fontDictionary) {
        PdfArray pdfWidths = fontDictionary.getAsArray(PdfName.Widths);
        if (pdfWidths == null) {
            throw new PdfException(KernelExceptionMessageConstant.MISSING_REQUIRED_FIELD_IN_FONT_DICTIONARY)
                    .setMessageParams(PdfName.Widths);
        }

        double[] widths = new double[pdfWidths.size()];
        for (int i = 0; i < pdfWidths.size(); i++) {
            PdfNumber n = pdfWidths.getAsNumber(i);
            widths[i] = n != null ? n.doubleValue() : 0;
        }

        return widths;
    }

    private int initializeUsedGlyphs(PdfDictionary fontDictionary) {
        int firstChar = normalizeFirstLastChar(fontDictionary.getAsNumber(PdfName.FirstChar), 0);
        int lastChar = normalizeFirstLastChar(fontDictionary.getAsNumber(PdfName.LastChar),
                PdfFont.SIMPLE_FONT_MAX_CHAR_CODE_VALUE);
        for (int i = firstChar; i <= lastChar; i++) {
            usedGlyphs[i] = 1;
        }
        return firstChar;
    }

    private double[] readFontBBox() {
        PdfArray fontBBox = getPdfObject().getAsArray(PdfName.FontBBox);
        if (fontBBox != null) {
            double llx = fontBBox.getAsNumber(FONT_BBOX_LLX).doubleValue();
            double lly = fontBBox.getAsNumber(FONT_BBOX_LLY).doubleValue();
            double urx = fontBBox.getAsNumber(FONT_BBOX_URX).doubleValue();
            double ury = fontBBox.getAsNumber(FONT_BBOX_URY).doubleValue();

            return new double[] {llx, lly, urx, ury};
        }

        return new double[] {0, 0, 0, 0};
    }

    private double[] readFontMatrix() {
        PdfArray fontMatrixArray = getPdfObject().getAsArray(PdfName.FontMatrix);
        if (fontMatrixArray == null) {
            throw new PdfException(KernelExceptionMessageConstant.MISSING_REQUIRED_FIELD_IN_FONT_DICTIONARY)
                    .setMessageParams(PdfName.FontMatrix);
        }
        double[] fontMatrix = new double[6];
        for (int i = 0; i < fontMatrixArray.size(); i++) {
            fontMatrix[i] = ((PdfNumber) fontMatrixArray.get(i)).getValue();
        }
        return fontMatrix;
    }

    private void initializeTypoAscenderDescender(double[] fontBBoxRect) {
        // iText uses typo ascender/descender for text extraction, that's why we need to set
        // them here to values relative to actual glyph metrics values.
        ((Type3Font) fontProgram).setTypoAscender((int) fontBBoxRect[FONT_BBOX_URY]);
        ((Type3Font) fontProgram).setTypoDescender((int) fontBBoxRect[FONT_BBOX_LLY]);
    }

    private void initializeFontBBox(double[] fontBBoxRect) {
        fontProgram.getFontMetrics().setBbox(
                (int) fontBBoxRect[FONT_BBOX_LLX],
                (int) fontBBoxRect[FONT_BBOX_LLY],
                (int) fontBBoxRect[FONT_BBOX_URX],
                (int) fontBBoxRect[FONT_BBOX_URY]
        );
    }

    private void normalizeGlyphSpaceUnitsTo1000Units(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = normalizeGlyphSpaceUnitsTo1000Units(array[i]);;
        }
    }

    private double normalizeGlyphSpaceUnitsTo1000Units(double value) {
        return value * getGlyphSpaceNormalizationFactor();
    }

    private void normalize1000UnitsToGlyphSpaceUnits(double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = normalize1000UnitsToGlyphSpaceUnits(array[i]);
        }
    }

    private double normalize1000UnitsToGlyphSpaceUnits(double value) {
        return value / getGlyphSpaceNormalizationFactor();
    }

    private void fillFontDescriptor(PdfDictionary fontDesc) {
        if (fontDesc == null) {
            return;
        }
        PdfNumber v = fontDesc.getAsNumber(PdfName.CapHeight);
        if (v != null) {
            double capHeight = v.doubleValue();
            setCapHeight((int) normalizeGlyphSpaceUnitsTo1000Units(capHeight));
        }
        v = fontDesc.getAsNumber(PdfName.ItalicAngle);
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
        return result < 0 || result > PdfFont.SIMPLE_FONT_MAX_CHAR_CODE_VALUE ? defaultValue : result;
    }
}
