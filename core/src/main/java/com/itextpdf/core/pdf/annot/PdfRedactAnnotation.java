package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfRedactAnnotation extends PdfMarkupAnnotation {
    public PdfRedactAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
    }

    public PdfRedactAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() throws PdfException {
        return PdfName.Redact;
    }

    public PdfRedactAnnotation setOverlayText(PdfString text){
        return put(PdfName.OverlayText, text);
    }

    public PdfString getOverlayText() throws PdfException {
        return getPdfObject().getAsString(PdfName.OverlayText);
    }

    public PdfRedactAnnotation setRedactRolloverAppearance(PdfDictionary dictionary) {
        return put(PdfName.RO, dictionary);
    }

    public PdfDictionary getRedactRolloverAppearance() throws PdfException {
        return getPdfObject().getAsDictionary(PdfName.RO);
    }

    public PdfRedactAnnotation setRepeat(PdfBoolean repeat) {
        return put(PdfName.Repeat, repeat);
    }

    public PdfBoolean getRepeat() throws PdfException {
        return getPdfObject().getAsBoolean(PdfName.Repeat);
    }
}
