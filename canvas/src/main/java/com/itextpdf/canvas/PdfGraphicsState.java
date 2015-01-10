package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.canvas.color.DeviceGray;
import com.itextpdf.core.fonts.PdfFont;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;

public class PdfGraphicsState {

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
    private Float flatnessTolerance;
    private Float smoothnessTolerance;
    private Boolean automaticStrokeAdjustment;
    private PdfObject blendMode;
    private PdfObject softMask;
    private Float strokeAlpha;
    private Float fillAlpha;
    private Boolean alphaIsShape;
    private Boolean textKnockout;
    private Color fillColor = DeviceGray.Black;
    private Color strokeColor = DeviceGray.Black;
    private Integer textRenderingMode;

    public PdfGraphicsState() {

    }

    public PdfGraphicsState(final PdfGraphicsState source) {

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

    public void updateFromExtGState(PdfDictionary extGState) throws PdfException {
        Float lw = extGState.getAsFloat(PdfName.LW);
        if (lw != null)
            lineWidth = lw;
        Integer lc = extGState.getAsInt(PdfName.LC);
        if (lc != null)
            lineCapStyle = lc;
        Integer lj = extGState.getAsInt(PdfName.LJ);
        if (lj != null)
            lineJoinStyle = lj;
        Float ml = extGState.getAsFloat(PdfName.ML);
        if (ml != null)
            miterLimit = ml;
        PdfArray d = extGState.getAsArray(PdfName.D);
        if (d != null)
            dashPattern = d;
        PdfName ri = extGState.getAsName(PdfName.RI);
        if (ri != null)
            renderingIntent = ri;
        Boolean op = extGState.getAsBool(PdfName.OP);
        if (op != null)
            strokeOverprint = op;
        op = extGState.getAsBool(PdfName.op);
        if (op != null)
            fillOverprint = op;
        Integer opm = extGState.getAsInt(PdfName.OPM);
        if (opm != null)
            overprintMode = opm;
        PdfArray font = extGState.getAsArray(PdfName.Font);
        if (font != null) {
            PdfDictionary fontDictionary = font.getAsDictionary(0);
            if (this.font != null && this.font.getPdfObject() == fontDictionary) {

            } else {
                this.font = new PdfFont(fontDictionary, extGState.getDocument());
            }
            Float fontSize = font.getAsFloat(1);
            if (fontSize != null)
                this.fontSize = fontSize;
        }
        PdfObject bg = extGState.get(PdfName.BG);
        if (bg != null)
            blackGenerationFunction = bg;
        PdfObject bg2 = extGState.get(PdfName.BG2);
        if (bg2 != null)
            blackGenerationFunction2 = bg2;
        PdfObject ucr = extGState.get(PdfName.UCR);
        if (ucr != null)
            underColorRemovalFunction = ucr;
        PdfObject ucr2 = extGState.get(PdfName.UCR2);
        if (ucr2 != null)
            underColorRemovalFunction2 = ucr2;
        PdfObject tr = extGState.get(PdfName.TR);
        if (tr != null)
            transferFunction = tr;
        PdfObject tr2 = extGState.get(PdfName.TR2);
        if (tr2 != null)
            transferFunction2 = tr2;
        PdfObject ht = extGState.get(PdfName.HT);
        if (ht != null)
            halftone = ht;
        Float fl = extGState.getAsFloat(PdfName.FL);
        if (fl != null)
            flatnessTolerance = fl;
        Float sm = extGState.getAsFloat(PdfName.SM);
        if (sm != null)
            smoothnessTolerance = sm;
        Boolean sa = extGState.getAsBool(PdfName.SA);
        if (sa != null)
            automaticStrokeAdjustment = sa;
        PdfObject bm = extGState.get(PdfName.BM);
        if (bm != null)
            blendMode = bm;
        PdfObject sMask = extGState.get(PdfName.SMask);
        if (sMask != null)
            softMask = sMask;
        Float ca = extGState.getAsFloat(PdfName.CA);
        if (ca != null)
            strokeAlpha = ca;
        ca = extGState.getAsFloat(PdfName.ca);
        if (ca != null)
            fillAlpha = ca;
        Boolean ais = extGState.getAsBool(PdfName.AIS);
        if (ais != null)
            alphaIsShape = ais;
        Boolean tk = extGState.getAsBool(PdfName.TK);
        if (tk != null)
            textKnockout = tk;
    }

    public void updateFromExtGState(PdfExtGState extGState) throws PdfException {
        updateFromExtGState(extGState.getPdfObject());
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

    public Float getFlatnessTolerance() {
        return flatnessTolerance;
    }

    public void setFlatnessTolerance(Float flatnessTolerance) {
        this.flatnessTolerance = flatnessTolerance;
    }
}
