package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfTextAnnotation extends PdfMarkupAnnotation {

    public PdfTextAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfTextAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Text;
    }

    public PdfString getState() {
        return getPdfObject().getAsString(PdfName.State);
    }

    public PdfTextAnnotation setState(PdfString state) {
        return put(PdfName.State, state);
    }

    public PdfString getStateModel() {
        return getPdfObject().getAsString(PdfName.StateModel);
    }

    public PdfTextAnnotation setStateModel(PdfString stateModel) {
        return put(PdfName.StateModel, stateModel);
    }


}
