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
package com.itextpdf.barcodes;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * Base class for the barcode types that have 1D representation.
 * This means all data is encoded in the width of the bars. And the height of the bars is constant.
 */
public abstract class Barcode1D {

    /**
     * Constant that defines left alignment.
     */
    public static final int ALIGN_LEFT = 1;
    /**
     * Constant that defines right alignment.
     */
    public static final int ALIGN_RIGHT = 2;
    /**
     * Constant that defines center alignment.
     */
    public static final int ALIGN_CENTER = 3;

    // Android-Conversion-Skip-Block-Start (java.awt library isn't available on Android)
    /**
     * The default color to draw if a bar is present.
     */
    protected final java.awt.Color DEFAULT_BAR_FOREGROUND_COLOR = java.awt.Color.BLACK;
    /**
     * The default color to draw if a bar is not present.
     */
    protected final java.awt.Color DEFAULT_BAR_BACKGROUND_COLOR = java.awt.Color.WHITE;
    // Android-Conversion-Skip-Block-End
	
    protected PdfDocument document;

    /**
     * The minimum bar width.
     */
    protected float x;

    /**
     * The bar multiplier for wide bars or the distance between
     * bars for Postnet and Planet.
     */
    protected float n;

    /**
     * The text font. <CODE>null</CODE> if no text.
     */
    protected PdfFont font;

    /**
     * The size of the text or the height of the shorter bar
     * in Postnet.
     */
    protected float size;

    /**
     * If positive, the text distance under the bars. If zero or negative,
     * the text distance above the bars.
     */
    protected float baseline;

    /**
     * The height of the bars.
     */
    protected float barHeight;

    /**
     * The text alignment.
     */
    protected int textAlignment;

    /**
     * The optional checksum generation.
     */
    protected boolean generateChecksum;

    /**
     * Shows the generated checksum in the the text.
     */
    protected boolean checksumText;

    /**
     * Show the start and stop character '*' in the text for
     * the barcode 39 or 'ABCD' for codabar.
     */
    protected boolean startStopText;

    /**
     * Generates extended barcode 39.
     */
    protected boolean extended;

    /**
     * The code to generate.
     */
    protected String code = "";

    /**
     * Show the guard bars for barcode EAN.
     */
    protected boolean guardBars;

    /**
     * The code type.
     */
    protected int codeType;

    /**
     * The ink spreading.
     */
    protected float inkSpreading = 0;

    /**
     * The alternate text to be used, if present.
     */
    protected String altText;

    /**
     * Creates new {@link Barcode1D} instance.
     *
     * @param document The document
     */
    protected Barcode1D(PdfDocument document) {
        this.document = document;
    }

    /**
     * Gets the minimum bar width.
     *
     * @return the minimum bar width
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the minimum bar width.
     *
     * @param x the minimum bar width
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets the bar multiplier for wide bars.
     *
     * @return the bar multiplier for wide bars
     */
    public float getN() {
        return n;
    }

    /**
     * Sets the bar multiplier for wide bars.
     *
     * @param n the bar multiplier for wide bars
     */
    public void setN(float n) {
        this.n = n;
    }

    /**
     * Gets the text font. <CODE>null</CODE> if no text.
     *
     * @return the text font. <CODE>null</CODE> if no text
     */
    public PdfFont getFont() {
        return font;
    }

    /**
     * Sets the text font.
     *
     * @param font the text font. Set to <CODE>null</CODE> to suppress any text
     */
    public void setFont(PdfFont font) {
        this.font = font;
    }

    /**
     * Gets the size of the text.
     *
     * @return the size
     */
    public float getSize() {
        return size;
    }

    /**
     * Sets the size of the text.
     *
     * @param size the size of the text
     */
    public void setSize(float size) {
        this.size = size;
    }

    /**
     * Gets the text baseline.
     * If positive, the text distance under the bars. If zero or negative,
     * the text distance above the bars.
     *
     * @return the baseline.
     */
    public float getBaseline() {
        return baseline;
    }

    /**
     * Sets the text baseline.
     * If positive, the text distance under the bars. If zero or negative,
     * the text distance above the bars.
     *
     * @param baseline the baseline.
     */
    public void setBaseline(float baseline) {
        this.baseline = baseline;
    }

    /**
     * Gets the height of the bars.
     *
     * @return the height of the bars
     */
    public float getBarHeight() {
        return barHeight;
    }

    /**
     * Sets the height of the bars.
     *
     * @param barHeight the height of the bars
     */
    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
    }

    /**
     * Gets the text alignment.
     *
     * @return the text alignment
     */
    public int getTextAlignment() {
        return textAlignment;
    }

    /**
     * Sets the text alignment.
     *
     * @param textAlignment the text alignment
     */
    public void setTextAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
    }

    /**
     * Gets the optional checksum generation.
     *
     * @return the optional checksum generation
     */
    public boolean isGenerateChecksum() {
        return generateChecksum;
    }

    /**
     * Setter for property generateChecksum.
     *
     * @param generateChecksum New value of property generateChecksum.
     */
    public void setGenerateChecksum(boolean generateChecksum) {
        this.generateChecksum = generateChecksum;
    }

    /**
     * Gets the property to show the generated checksum in the the text.
     *
     * @return value of property checksumText
     */
    public boolean isChecksumText() {
        return checksumText;
    }

    /**
     * Sets the property to show the generated checksum in the the text.
     *
     * @param checksumText new value of property checksumText
     */
    public void setChecksumText(boolean checksumText) {
        this.checksumText = checksumText;
    }

    /**
     * Sets the property to show the start and stop character '*' in the text for
     * the barcode 39.
     *
     * @return value of property startStopText
     */
    public boolean isStartStopText() {
        return startStopText;
    }

    /**
     * Gets the property to show the start and stop character '*' in the text for
     * the barcode 39.
     *
     * @param startStopText new value of property startStopText
     */
    public void setStartStopText(boolean startStopText) {
        this.startStopText = startStopText;
    }

    /**
     * Gets the property to generate extended barcode 39.
     *
     * @return value of property extended.
     */
    public boolean isExtended() {
        return extended;
    }

    /**
     * Sets the property to generate extended barcode 39.
     *
     * @param extended new value of property extended
     */
    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    /**
     * Gets the code to generate.
     *
     * @return the code to generate
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code to generate.
     *
     * @param code the code to generate
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the property to show the guard bars for barcode EAN.
     *
     * @return value of property guardBars
     */
    public boolean isGuardBars() {
        return guardBars;
    }

    /**
     * Sets the property to show the guard bars for barcode EAN.
     *
     * @param guardBars new value of property guardBars
     */
    public void setGuardBars(boolean guardBars) {
        this.guardBars = guardBars;
    }

    /**
     * Gets the code type.
     *
     * @return the code type
     */
    public int getCodeType() {
        return codeType;
    }

    /**
     * Sets the code type.
     *
     * @param codeType the code type
     */
    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    /**
     * Gets the maximum area that the barcode and the text, if
     * any, will occupy. The lower left corner is always (0, 0).
     *
     * @return the size the barcode occupies.
     */
    public abstract Rectangle getBarcodeSize();

    /**
     * Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.<p>
     * The bars and text are written in the following colors:
     * <br>
     * <TABLE BORDER="1" SUMMARY="barcode properties">
     * <TR>
     * <TH><CODE>barColor</CODE></TH>
     * <TH><CODE>textColor</CODE></TH>
     * <TH>Result</TH>
     * </TR>
     * <TR>
     * <TD><CODE>null</CODE></TD>
     * <TD><CODE>null</CODE></TD>
     * <TD>bars and text painted with current fill color</TD>
     * </TR>
     * <TR>
     * <TD><CODE>barColor</CODE></TD>
     * <TD><CODE>null</CODE></TD>
     * <TD>bars and text painted with <CODE>barColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><CODE>null</CODE></TD>
     * <TD><CODE>textColor</CODE></TD>
     * <TD>bars painted with current color<br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><CODE>barColor</CODE></TD>
     * <TD><CODE>textColor</CODE></TD>
     * <TD>bars painted with <CODE>barColor</CODE><br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * </TABLE>
     *
     * @param canvas    the <CODE>PdfCanvas</CODE> where the barcode will be placed
     * @param barColor  the color of the bars. It can be <CODE>null</CODE>
     * @param textColor the color of the text. It can be <CODE>null</CODE>
     * @return the dimensions the barcode occupies
     */
    public abstract Rectangle placeBarcode(PdfCanvas canvas, Color barColor, Color textColor);

    /**
     * Gets the amount of ink spreading.
     *
     * @return the ink spreading
     */
    public float getInkSpreading() {
        return this.inkSpreading;
    }

    /**
     * Sets the amount of ink spreading. This value will be subtracted
     * to the width of each bar. The actual value will depend on the ink
     * and the printing medium.
     *
     * @param inkSpreading the ink spreading
     */
    public void setInkSpreading(float inkSpreading) {
        this.inkSpreading = inkSpreading;
    }

    /**
     * Gets the alternate text.
     *
     * @return the alternate text
     */
    public String getAltText() {
        return this.altText;
    }

    /**
     * Sets the alternate text. If present, this text will be used instead of the
     * text derived from the supplied code.
     *
     * @param altText the alternate text
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }

    // Android-Conversion-Skip-Block-Start (java.awt library isn't available on Android)
    /**
     * Creates a <CODE>java.awt.Image</CODE>. This image only
     * contains the bars without any text.
     *
     * @param foreground the color of the bars. If <CODE>null</CODE> defaults to {@link Barcode1D#DEFAULT_BAR_FOREGROUND_COLOR}
     * @param background the color of the background. If <CODE>null</CODE> defaults to {@link Barcode1D#DEFAULT_BAR_BACKGROUND_COLOR}
     * @return the image
     */
    public abstract java.awt.Image createAwtImage(java.awt.Color foreground, java.awt.Color background);
    // Android-Conversion-Skip-Block-End

    /**
     * Creates a PdfFormXObject with the barcode. Default bar color and text color will be used.
     * @param document  The document
     * @return          The XObject
     * @see #createFormXObject(Color, Color, PdfDocument)
     */
    public PdfFormXObject createFormXObject(PdfDocument document) {
        return createFormXObject(null, null, document);
    }

    /**
     * Creates a PdfFormXObject with the barcode.
     *
     * @param barColor  The color of the bars. It can be <CODE>null</CODE>
     * @param textColor The color of the text. It can be <CODE>null</CODE>
     * @param document  The document
     * @return the XObject
     * @see #placeBarcode(PdfCanvas canvas, Color barColor, Color textColor)
     */
    public PdfFormXObject createFormXObject(Color barColor, Color textColor, PdfDocument document) {
        PdfFormXObject xObject = new PdfFormXObject((Rectangle) null);
        Rectangle rect = placeBarcode(new PdfCanvas(xObject, document), barColor, textColor);
        xObject.setBBox(new PdfArray(rect));

        return xObject;
    }

    /**
     * Make the barcode occupy the specified width.
     * Usually this is achieved by adjusting bar widths.
     * @param width The width
     */
    public void fitWidth(float width) {
        setX(x * width / getBarcodeSize().getWidth());
    }

    /**
     * Gets the descender value of the font.
     *
     * @return the descender value of the font
     */
    protected float getDescender() {
        final float sizeCoefficient = FontProgram.convertTextSpaceToGlyphSpace(size);
        return font.getFontProgram().getFontMetrics().getTypoDescender() * sizeCoefficient;
    }
}
