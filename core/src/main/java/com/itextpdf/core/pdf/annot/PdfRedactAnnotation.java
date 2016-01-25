package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfBoolean;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfString;

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

    public PdfRedactAnnotation setRedactRolloverAppearance(PdfDictionary dictionary) {
        return put(PdfName.RO, dictionary);
    }

    public PdfDictionary getRedactRolloverAppearance() {
        return getPdfObject().getAsDictionary(PdfName.RO);
    }

    public PdfRedactAnnotation setRepeat(PdfBoolean repeat) {
        return put(PdfName.Repeat, repeat);
    }

    public PdfBoolean getRepeat() {
        return getPdfObject().getAsBoolean(PdfName.Repeat);
    }
}
