package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.colors.Color;
import com.itextpdf.canvas.colors.DeviceGray;
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
    protected Float lineWidth;
    protected Integer lineCapStyle;
    protected Integer lineJoinStyle;
    protected Float miterLimit;
    protected PdfArray dashPattern;
    protected PdfName renderingIntent;
    protected Boolean strokeOverprint;
    protected Boolean fillOverprint;
    protected Integer overprintMode;
    protected Float fontSize;
    protected PdfFont font;
    protected PdfObject blackGenerationFunction;
    protected PdfObject blackGenerationFunction2;
    protected PdfObject underColorRemovalFunction;
    protected PdfObject underColorRemovalFunction2;
    protected PdfObject transferFunction;
    protected PdfObject transferFunction2;
    protected PdfObject halftone;
    protected Float flatnessTolerance;
    protected Float smoothnessTolerance;
    protected Boolean automaticStrokeAdjustment;
    protected PdfObject blendMode;
    protected PdfObject softMask;
    protected Float strokeAlpha;
    protected Float fillAlpha;
    protected Boolean alphaIsShape;
    protected Boolean textKnockout;

    protected Color fillColor = DeviceGray.Black;
    protected Color strokeColor = DeviceGray.Black;

    protected Integer textRenderingMode;

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


}
