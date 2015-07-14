package com.itextpdf.barcodes;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

public abstract class Barcode1D {

    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    public static final int ALIGN_CENTER = 3;

    /** A type of barcode */
    public static final int POSTNET = 1;
    /** A type of barcode */
    public static final int PLANET = 2;
    /** A type of barcode */
    public static final int CODABAR = 3;

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

    public Barcode1D(PdfDocument document){
        this.document = document;
    }

    /**
     * Gets the minimum bar width.
     * @return the minimum bar width
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the minimum bar width.
     * @param x the minimum bar width
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets the bar multiplier for wide bars.
     * @return the bar multiplier for wide bars
     */
    public float getN() {
        return n;
    }

    /**
     * Sets the bar multiplier for wide bars.
     * @param n the bar multiplier for wide bars
     */
    public void setN(float n) {
        this.n = n;
    }

    /**
     * Gets the text font. <CODE>null</CODE> if no text.
     * @return the text font. <CODE>null</CODE> if no text
     */
    public PdfFont getFont() {
        return font;
    }

    /**
     * Sets the text font.
     * @param font the text font. Set to <CODE>null</CODE> to suppress any text
     */
    public void setFont(PdfFont font) {
        this.font = font;
    }

    public float getSize() {
        return size;
    }

    /**
     * Sets the size of the text.
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
     * @return the height of the bars
     */
    public float getBarHeight() {
        return barHeight;
    }

    /**
     * Sets the height of the bars.
     * @param barHeight the height of the bars
     */
    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
    }

    /**
     * Gets the text alignment.
     * @return the text alignment
     */
    public int getTextAlignment() {
        return textAlignment;
    }

    /**
     * Sets the text alignment.
     * @param textAlignment the text alignment
     */
    public void setTextAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
    }

    /**
     * Gets the optional checksum generation.
     * @return the optional checksum generation
     */
    public boolean isGenerateChecksum() {
        return generateChecksum;
    }

    /**
     * Setter for property generateChecksum.
     * @param generateChecksum New value of property generateChecksum.
     */
    public void setGenerateChecksum(boolean generateChecksum) {
        this.generateChecksum = generateChecksum;
    }

    /**
     * Gets the property to show the generated checksum in the the text.
     * @return value of property checksumText
     */
    public boolean isChecksumText() {
        return checksumText;
    }

    /**
     * Sets the property to show the generated checksum in the the text.
     * @param checksumText new value of property checksumText
     */
    public void setChecksumText(boolean checksumText) {
        this.checksumText = checksumText;
    }

    /**
     * Sets the property to show the start and stop character '*' in the text for
     * the barcode 39.
     * @return value of property startStopText
     */
    public boolean isStartStopText() {
        return startStopText;
    }

    /**
     * Gets the property to show the start and stop character '*' in the text for
     * the barcode 39.
     * @param startStopText new value of property startStopText
     */
    public void setStartStopText(boolean startStopText) {
        this.startStopText = startStopText;
    }

    /**
     * Gets the property to generate extended barcode 39.
     * @return value of property extended.
     */
    public boolean isExtended() {
        return extended;
    }

    /**
     * Sets the property to generate extended barcode 39.
     * @param extended new value of property extended
     */
    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    /**
     * Gets the code to generate.
     * @return the code to generate
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code to generate.
     * @param code the code to generate
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the property to show the guard bars for barcode EAN.
     * @return value of property guardBars
     */
    public boolean isGuardBars() {
        return guardBars;
    }

    /**
     * Sets the property to show the guard bars for barcode EAN.
     * @param guardBars new value of property guardBars
     */
    public void setGuardBars(boolean guardBars) {
        this.guardBars = guardBars;
    }

    /**
     * Gets the code type.
     * @return the code type
     */
    public int getCodeType() {
        return codeType;
    }

    /**
     * Sets the code type.
     * @param codeType the code type
     */
    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    /**
     * Gets the maximum area that the barcode and the text, if
     * any, will occupy. The lower left corner is always (0, 0).
     * @return the size the barcode occupies.
     */
    public abstract Rectangle getBarcodeSize();

    /**
     * Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.<p>
     * The bars and text are written in the following colors:<p>
     * <P><TABLE BORDER=1>
     * <TR>
     * <TH><P><CODE>barColor</CODE></TH>
     * <TH><P><CODE>textColor</CODE></TH>
     * <TH><P>Result</TH>
     * </TR>
     * <TR>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P>bars and text painted with current fill color</TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>barColor</CODE></TD>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P>bars and text painted with <CODE>barColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P><CODE>textColor</CODE></TD>
     * <TD><P>bars painted with current color<br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>barColor</CODE></TD>
     * <TD><P><CODE>textColor</CODE></TD>
     * <TD><P>bars painted with <CODE>barColor</CODE><br>text painted with <CODE>textColor</CODE></TD>
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
     * @return the ink spreading
     */
    public float getInkSpreading() {
        return this.inkSpreading;
    }

    /**
     * Sets the amount of ink spreading. This value will be subtracted
     * to the width of each bar. The actual value will depend on the ink
     * and the printing medium.
     * @param inkSpreading the ink spreading
     */
    public void setInkSpreading(float inkSpreading) {
        this.inkSpreading = inkSpreading;
    }

    /**
     * Gets the alternate text.
     * @return the alternate text
     */
    public String getAltText() {
        return this.altText;
    }

    /**
     * Sets the alternate text. If present, this text will be used instead of the
     * text derived from the supplied code.
     * @param altText the alternate text
     */
    public void setAltText(String altText) {
        this.altText = altText;
    }

    /** Creates a <CODE>java.awt.Image</CODE>. This image only
     * contains the bars without any text.
     * @param foreground the color of the bars
     * @param background the color of the background
     * @return the image
     */
    public abstract java.awt.Image createAwtImage(java.awt.Color foreground, java.awt.Color background);

    /** Creates a PdfFormXObject with the barcode.
     * @param barColor the color of the bars. It can be <CODE>null</CODE>
     * @param textColor the color of the text. It can be <CODE>null</CODE>
     * @return the XObject
     * @see #placeBarcode(PdfCanvas canvas, Color barColor, Color textColor)
     */
    public PdfFormXObject createFormXObjectWithBarcode(Color barColor, Color textColor) {
        PdfStream stream = new PdfStream(document);
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources());
        Rectangle rect = placeBarcode(canvas, barColor, textColor);

        PdfFormXObject xObject = new PdfFormXObject(document, rect);
        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());

        return xObject;
    }
}
