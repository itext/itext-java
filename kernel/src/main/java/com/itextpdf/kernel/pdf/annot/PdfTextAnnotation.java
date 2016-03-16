package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfTextAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = -2061119066076464569L;

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
