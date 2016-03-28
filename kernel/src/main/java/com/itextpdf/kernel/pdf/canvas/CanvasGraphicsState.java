package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import java.util.Arrays;

/**
 * This class is designed for internal usage. <br>
 * Use <code>PdfExtGState</code> class and <code>PdfCanvas#setExtGState()</code> method for setting extended graphics properties.
 */
public class CanvasGraphicsState {

    /**
     * The current transformation matrix, which maps positions from user coordinates to device coordinates.
     *
     * We use an identity matrix as a default value, but in spec a default value is:
     * "a matrix that transforms default user coordinates to device coordinates".
     */
    private Matrix ctm = new Matrix();

    /**
     * Those two fields are currently used only in {@code PdfContentStreamProcessor}, which in it's turn doesn't work with
     * other than device color spaces (RGB, CMYK, GRAY). Therefore for simplicity, if current color space is not a device color space
     * it will have a {@code null} value.
     */
    private PdfColorSpace strokeColorSpace = new PdfDeviceCs.Gray();
    private PdfColorSpace fillColorSpace = new PdfDeviceCs.Gray();

    // color
    private Color strokeColor = DeviceGray.BLACK;
    private Color fillColor = DeviceGray.BLACK;

    // text state
    private float charSpacing = 0f;
    private float wordSpacing = 0f;
    private float scale = 100f; // horizontal scaling
    private float leading = 0f;
    private PdfFont font;
    private float fontSize;
    private int textRenderingMode = PdfCanvasConstants.TextRenderingMode.FILL;
    private float textRise = 0f;
    private boolean textKnockout = true;

    private float lineWidth = 1f;
    private int lineCapStyle = PdfCanvasConstants.LineCapStyle.BUTT;
    private int lineJoinStyle = PdfCanvasConstants.LineJoinStyle.MITER;
    private float miterLimit = 10f;

    /**
     * A description of the dash pattern to be used when paths are stroked. Default value is solid line.
     *
     * The line dash pattern is expressed as an array of the form [ dashArray dashPhase ],
     * where dashArray is itself an array and dashPhase is an integer.
     *
     * An empty dash array (first element in the array) and zero phase (second element in the array)
     * can be used to restore the dash pattern to a solid line.
     */
    private PdfArray dashPattern = new PdfArray(Arrays.asList(new PdfArray(), new PdfNumber(0)));

    private PdfName renderingIntent = PdfName.RelativeColorimetric;
    private boolean automaticStrokeAdjustment = false;
    private PdfObject blendMode = PdfName.Normal;
    private PdfObject softMask = PdfName.None;

    // alpha constant
    private float strokeAlpha = 1f;
    private float fillAlpha = 1f;
    // alpha source
    private boolean alphaIsShape = false;

    private boolean strokeOverprint = false;
    private boolean fillOverprint = false;
    private int overprintMode = 0;
    private PdfObject blackGenerationFunction;
    private PdfObject blackGenerationFunction2;
    private PdfObject underColorRemovalFunction;
    private PdfObject underColorRemovalFunction2;
    private PdfObject transferFunction;
    private PdfObject transferFunction2;
    private PdfObject halftone;
    private float flatnessTolerance = 1f;
    private Float smoothnessTolerance;
    private PdfObject htp;

    /**
     * Internal empty & default constructor.
     */
    protected CanvasGraphicsState() {

    }

    /**
     * Copy constructor.
     * @param source the Graphics State to copy from
     */
    protected CanvasGraphicsState(final CanvasGraphicsState source) {
        copyFrom(source);
    }

    /**
     * Updates this object with the values from a dictionary.
     *
     * @param extGState the dictionary containing source parameters
     */
    public void updateFromExtGState(PdfDictionary extGState) {
        updateFromExtGState(new PdfExtGState(extGState));
    }

    /**
     * @return current transformation matrix.
     */
    public Matrix getCtm() {
        return ctm;
    }

    /**
     * Updates current transformation matrix.
     */
    public void updateCtm(float a, float b, float c, float d, float e, float f) {
        updateCtm(new Matrix(a, b, c, d, e, f));
    }

    /**
     * Updates current transformation matrix.
     * @param newCtm new current transformation matrix.
     */
    public void updateCtm(Matrix newCtm) {
        ctm = newCtm.multiply(ctm);
    }

    public PdfColorSpace getStrokeColorSpace() {
        return strokeColorSpace;
    }

    public void setStrokeColorSpace(PdfColorSpace strokeColorSpace) {
        this.strokeColorSpace = strokeColorSpace;
    }

    public PdfColorSpace getFillColorSpace() {
        return fillColorSpace;
    }

    public void setFillColorSpace(PdfColorSpace fillColorSpace) {
        this.fillColorSpace = fillColorSpace;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getLineCapStyle() {
        return lineCapStyle;
    }

    public void setLineCapStyle(int lineCapStyle) {
        this.lineCapStyle = lineCapStyle;
    }

    public int getLineJoinStyle() {
        return lineJoinStyle;
    }

    public void setLineJoinStyle(int lineJoinStyle) {
        this.lineJoinStyle = lineJoinStyle;
    }

    public float getMiterLimit() {
        return miterLimit;
    }

    public void setMiterLimit(float miterLimit) {
        this.miterLimit = miterLimit;
    }

    public PdfArray getDashPattern() {
        return dashPattern;
    }

    public void setDashPattern(PdfArray dashPattern) {
        this.dashPattern = dashPattern;
    }

    public PdfName getRenderingIntent() {
        return renderingIntent;
    }

    public void setRenderingIntent(PdfName renderingIntent) {
        this.renderingIntent = renderingIntent;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public PdfFont getFont() {
        return font;
    }

    public void setFont(PdfFont font) {
        this.font = font;
    }

    public int getTextRenderingMode() {
        return textRenderingMode;
    }

    public void setTextRenderingMode(int textRenderingMode) {
        this.textRenderingMode = textRenderingMode;
    }

    public float getTextRise() {
        return textRise;
    }

    public void setTextRise(float textRise) {
        this.textRise = textRise;
    }

    public float getFlatnessTolerance() {
        return flatnessTolerance;
    }

    public void setFlatnessTolerance(float flatnessTolerance) {
        this.flatnessTolerance = flatnessTolerance;
    }

    public void setWordSpacing(float wordSpacing) {
        this.wordSpacing = wordSpacing;
    }

    public Float getWordSpacing() {
        return wordSpacing;
    }

    public void setCharSpacing(float characterSpacing) {
        this.charSpacing = characterSpacing;
    }

    public float getCharSpacing() {
        return charSpacing;
    }

    public float getLeading() {
        return leading;
    }

    public void setLeading(float leading) {
        this.leading = leading;
    }

    public float getHorizontalScaling() {
        return scale;
    }

    public void setHorizontalScaling(float scale) {
        this.scale = scale;
    }

    public boolean getStrokeOverprint() {
        return strokeOverprint;
    }

    public boolean getFillOverprint() {
        return fillOverprint;
    }

    public int getOverprintMode() {
        return overprintMode;
    }

    public PdfObject getBlackGenerationFunction() {
        return blackGenerationFunction;
    }

    public PdfObject getBlackGenerationFunction2() {
        return blackGenerationFunction2;
    }

    public PdfObject getUnderColorRemovalFunction() {
        return underColorRemovalFunction;
    }

    public PdfObject getUnderColorRemovalFunction2() {
        return underColorRemovalFunction2;
    }

    public PdfObject getTransferFunction() {
        return transferFunction;
    }

    public PdfObject getTransferFunction2() {
        return transferFunction2;
    }

    public PdfObject getHalftone() {
        return halftone;
    }

    public Float getSmoothnessTolerance() {
        return smoothnessTolerance;
    }

    public boolean getAutomaticStrokeAdjustment() {
        return automaticStrokeAdjustment;
    }

    public PdfObject getBlendMode() {
        return blendMode;
    }

    public PdfObject getSoftMask() {
        return softMask;
    }

    public float getStrokeOpacity() {
        return strokeAlpha;
    }

    public float getFillOpacity() {
        return fillAlpha;
    }

    public boolean getAlphaIsShape() {
        return alphaIsShape;
    }

    public boolean getTextKnockout() {
        return textKnockout;
    }

    public PdfObject getHTP() {
        return htp;
    }

    /**
     * Updates current graphic state with values from extended graphic state dictionary.
     * @param extGState the wrapper around the extended graphic state dictionary
     */
    public void updateFromExtGState(PdfExtGState extGState) {
        Float lw = extGState.getLineWidth();
        if (lw != null)
            lineWidth = lw;
        Integer lc = extGState.getLineCapStyle();
        if (lc != null)
            lineCapStyle = lc;
        Integer lj = extGState.getLineJoinStyle();
        if (lj != null)
            lineJoinStyle = lj;
        Float ml = extGState.getMiterLimit();
        if (ml != null)
            miterLimit = ml;
        PdfArray d = extGState.getDashPattern();
        if (d != null)
            dashPattern = d;
        PdfName ri = extGState.getRenderingIntent();
        if (ri != null)
            renderingIntent = ri;
        Boolean op = extGState.getStrokeOverprintFlag();
        if (op != null)
            strokeOverprint = op;
        op = extGState.getFillOverprintFlag();
        if (op != null)
            fillOverprint = op;
        Integer opm = extGState.getOverprintMode();
        if (opm != null)
            overprintMode = opm;
        PdfArray fnt = extGState.getFont();
        if (fnt != null) {
            PdfDictionary fontDictionary = fnt.getAsDictionary(0);
            if (this.font == null || this.font.getPdfObject() != fontDictionary) {
                this.font = PdfFontFactory.createFont(fontDictionary);
            }
            Float fntSz = fnt.getAsFloat(1);
            if (fntSz != null)
                this.fontSize = fntSz;
        }
        PdfObject bg = extGState.getBlackGenerationFunction();
        if (bg != null)
            blackGenerationFunction = bg;
        PdfObject bg2 = extGState.getBlackGenerationFunction2();
        if (bg2 != null)
            blackGenerationFunction2 = bg2;
        PdfObject ucr = extGState.getUndercolorRemovalFunction();
        if (ucr != null)
            underColorRemovalFunction = ucr;
        PdfObject ucr2 = extGState.getUndercolorRemovalFunction2();
        if (ucr2 != null)
            underColorRemovalFunction2 = ucr2;
        PdfObject tr = extGState.getTransferFunction();
        if (tr != null)
            transferFunction = tr;
        PdfObject tr2 = extGState.getTransferFunction2();
        if (tr2 != null)
            transferFunction2 = tr2;
        PdfObject ht = extGState.getHalftone();
        if (ht != null)
            halftone = ht;
        PdfObject local_htp = extGState.getHTP();
        if (local_htp != null)
            this.htp = local_htp;
        Float fl = extGState.getFlatnessTolerance();
        if (fl != null)
            flatnessTolerance = fl;
        Float sm = extGState.getSmothnessTolerance();
        if (sm != null)
            smoothnessTolerance = sm;
        Boolean sa = extGState.getAutomaticStrokeAdjustmentFlag();
        if (sa != null)
            automaticStrokeAdjustment = sa;
        PdfObject bm = extGState.getBlendMode();
        if (bm != null)
            blendMode = bm;
        PdfObject sMask = extGState.getSoftMask();
        if (sMask != null)
            softMask = sMask;
        Float ca = extGState.getStrokeOpacity();
        if (ca != null)
            strokeAlpha = ca;
        ca = extGState.getFillOpacity();
        if (ca != null)
            fillAlpha = ca;
        Boolean ais = extGState.getAlphaSourceFlag();
        if (ais != null)
            alphaIsShape = ais;
        Boolean tk = extGState.getTextKnockoutFlag();
        if (tk != null)
            textKnockout = tk;
    }

    private void copyFrom(CanvasGraphicsState source) {
        this.ctm = source.ctm;
        this.strokeColorSpace = source.strokeColorSpace;
        this.fillColorSpace = source.fillColorSpace;
        this.strokeColor = source.strokeColor;
        this.fillColor = source.fillColor;
        this.charSpacing = source.charSpacing;
        this.wordSpacing = source.wordSpacing;
        this.scale = source.scale;
        this.leading = source.leading;
        this.font = source.font;
        this.fontSize = source.fontSize;
        this.textRenderingMode = source.textRenderingMode;
        this.textRise = source.textRise;
        this.textKnockout = source.textKnockout;
        this.lineWidth = source.lineWidth;
        this.lineCapStyle = source.lineCapStyle;
        this.lineJoinStyle = source.lineJoinStyle;
        this.miterLimit = source.miterLimit;
        this.dashPattern = source.dashPattern;
        this.renderingIntent = source.renderingIntent;
        this.automaticStrokeAdjustment = source.automaticStrokeAdjustment;
        this.blendMode = source.blendMode;
        this.softMask = source.softMask;
        this.strokeAlpha = source.strokeAlpha;
        this.fillAlpha = source.fillAlpha;
        this.alphaIsShape = source.alphaIsShape;
        this.strokeOverprint = source.strokeOverprint;
        this.fillOverprint = source.fillOverprint;
        this.overprintMode = source.overprintMode;
        this.blackGenerationFunction = source.blackGenerationFunction;
        this.blackGenerationFunction2 = source.blackGenerationFunction2;
        this.underColorRemovalFunction = source.underColorRemovalFunction;
        this.underColorRemovalFunction2 = source.underColorRemovalFunction2;
        this.transferFunction = source.transferFunction;
        this.transferFunction2 = source.transferFunction2;
        this.halftone = source.halftone;
        this.flatnessTolerance = source.flatnessTolerance;
        this.smoothnessTolerance = source.smoothnessTolerance;
        this.htp = source.htp;
    }
}
