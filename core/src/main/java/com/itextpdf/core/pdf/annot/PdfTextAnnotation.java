package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfString;

public class PdfTextAnnotation extends PdfMarkupAnnotation {

    public PdfTextAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfTextAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
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
