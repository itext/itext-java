package com.itextpdf.core.pdf.canvas;

import com.itextpdf.core.color.Color;
import com.itextpdf.core.color.DeviceGray;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfFontFactory;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;

/**
 * This class is designed for internal usage. <br>
 * Use <code>PdfExtGState</code> class and <code>PdfCanvas#setExtGState()</code> method for setting extended graphics properties.
 */
public class CanvasGraphicsState {

    /**
     * ExtGState parameters.
     */
    private Float lineWidth;
    private Integer lineCapStyle;
    private Integer lineJoinStyle;
    private Float miterLimit;
    private PdfArray dashPattern;
    private PdfName renderingIntent;
    private Boolean strokeOverprint;
    private Boolean fillOverprint;
    private Integer overprintMode;
    private Float fontSize;
    private PdfFont font;
    private PdfObject blackGenerationFunction;
    private PdfObject blackGenerationFunction2;
    private PdfObject underColorRemovalFunction;
    private PdfObject underColorRemovalFunction2;
    private PdfObject transferFunction;
    private PdfObject transferFunction2;
    private PdfObject halftone;
    private PdfObject htp;
    private Float flatnessTolerance;
    private Float smoothnessTolerance;
    private Boolean automaticStrokeAdjustment;
    private PdfObject blendMode;
    private PdfObject softMask;
    private Float strokeAlpha;
    private Float fillAlpha;
    private Boolean alphaIsShape;
    private Boolean textKnockout;
    private Color fillColor = DeviceGray.BLACK;
    private Color strokeColor = DeviceGray.BLACK;
    private Integer textRenderingMode;
    private Float textRise;
    private Float wordSpacing;
    private Float charSpacing;
    private Float scale = 100f;
    private Float leading;

    /**
     * Creates a CanvasGraphicsState object from a PdfExtGState. Essentially
     * a copy constructor from a PdfDictionary object.
     * @param extGStateToUpdateFrom the dictionary wrapper containing source parameters
     */
    public CanvasGraphicsState(PdfExtGState extGStateToUpdateFrom) {
        copyFrom(extGStateToUpdateFrom);
    }

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
        // TODO implement
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

    /**
     * Updates this object with the values from a dictionary-like public object.
     * 
     * @param extGState the dictionary wrapper containing source parameters
     */
    public void updateFromExtGState(PdfExtGState extGState) {
        copyFrom(extGState);
    }
    
    /**
     * Updates this object with the values from a dictionary.
     * 
     * @param extGState the dictionary containing source parameters
     */
    public void updateFromExtGState(PdfDictionary extGState) {
        updateFromExtGState(new PdfExtGState(extGState));
    }
    
    private void copyFrom(PdfExtGState extGState) {
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
            if (this.font != null && this.font.getPdfObject() == fontDictionary) {

            } else {
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

    public Float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(Float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public Integer getLineCapStyle() {
        return lineCapStyle;
    }

    public void setLineCapStyle(Integer lineCapStyle) {
        this.lineCapStyle = lineCapStyle;
    }

    public Integer getLineJoinStyle() {
        return lineJoinStyle;
    }

    public void setLineJoinStyle(Integer lineJoinStyle) {
        this.lineJoinStyle = lineJoinStyle;
    }

    public Float getMiterLimit() {
        return miterLimit;
    }

    public void setMiterLimit(Float miterLimit) {
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

    public Float getFontSize() {
        return fontSize;
    }

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
    }

    public PdfFont getFont() {
        return font;
    }

    public void setFont(PdfFont font) {
        this.font = font;
    }

    public Integer getTextRenderingMode() {
        return textRenderingMode;
    }

    public void setTextRenderingMode(Integer textRenderingMode) {
        this.textRenderingMode = textRenderingMode;
    }

    public Float getTextRise() {
        return textRise;
    }

    public void setTextRise(Float textRise) {
        this.textRise = textRise;
    }

    public Float getFlatnessTolerance() {
        return flatnessTolerance;
    }

    public void setFlatnessTolerance(Float flatnessTolerance) {
        this.flatnessTolerance = flatnessTolerance;
    }

    public void setWordSpacing(Float wordSpacing) {
        this.wordSpacing = wordSpacing;
    }

    public Float getWordSpacing() {
        return wordSpacing;
    }

    public void setCharSpacing(Float characterSpacing) {
        this.charSpacing = characterSpacing;
    }

    public Float getCharSpacing() {
        return charSpacing;
    }

    public Float getLeading() {
        return leading;
    }

    public void setLeading(Float leading) {
        this.leading = leading;
    }

    public Float getHorizontalScaling() {
        return scale;
    }

    public void setHorizontalScaling(Float scale) {
        this.scale = scale;
    }

    public Boolean getStrokeOverprint() {
        return strokeOverprint;
    }

    public Boolean getFillOverprint() {
        return fillOverprint;
    }

    public Integer getOverprintMode() {
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

    public Boolean getAutomaticStrokeAdjustment() {
        return automaticStrokeAdjustment;
    }

    public PdfObject getBlendMode() {
        return blendMode;
    }

    public PdfObject getSoftMask() {
        return softMask;
    }

    public Float getStrokeOpacity() {
        return strokeAlpha;
    }

    public Float getFillOpacity() {
        return fillAlpha;
    }

    public Boolean getAlphaIsShape() {
        return alphaIsShape;
    }

    public Boolean getTextKnockout() {
        return textKnockout;
    }

    public PdfObject getHTP() {
        return htp;
    }
}
