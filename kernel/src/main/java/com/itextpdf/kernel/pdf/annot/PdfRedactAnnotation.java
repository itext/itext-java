package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfRedactAnnotation extends PdfMarkupAnnotation {
    public PdfRedactAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfRedactAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Redact;
    }

    public PdfRedactAnnotation setOverlayText(PdfString text){
        return put(PdfName.OverlayText, text);
    }

    public PdfString getOverlayText() {
        return getPdfObject().getAsString(PdfName.OverlayText);
    }

    public PdfRedactAnnotation setRedactRolloverAppearance(PdfStream stream) {
        return put(PdfName.RO, stream);
    }

    public PdfStream getRedactRolloverAppearance() {
        return getPdfObject().getAsStream(PdfName.RO);
    }

    public PdfRedactAnnotation setRepeat(PdfBoolean repeat) {
        return put(PdfName.Repeat, repeat);
    }

    public PdfBoolean getRepeat() {
        return getPdfObject().getAsBoolean(PdfName.Repeat);
    }
}
