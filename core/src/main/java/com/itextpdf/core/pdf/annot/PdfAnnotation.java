package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.layer.PdfOCG;

public class PdfAnnotation extends PdfObjectWrapper<PdfDictionary> {

    public PdfAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        this(new PdfDictionary(), document);
        put(PdfName.Rect, new PdfArray(rect));
    }

    public PdfAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }


    /**
     * Sets the layer this annotation belongs to.
     *
     * @param layer the layer this annotation belongs to
     */
    public void setLayer(final PdfOCG layer) throws PdfException {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

    public <T extends PdfAnnotation> T setAction(PdfAction action) {
        return put(PdfName.A, action);
    }

    public <T extends PdfAnnotation> T setAdditionalAction(PdfName key, PdfAction action) throws PdfException {
        PdfAction.setAdditionalAction(this, key, action);
        return (T) this;
    }

    static public PdfAnnotation makeAnnotation(PdfObject pdfObject, PdfDocument document) throws PdfException {
        if (pdfObject.isIndirectReference())
            pdfObject = ((PdfIndirectReference) pdfObject).getRefersTo();
        if (pdfObject.isDictionary()) {
            PdfDictionary dictionary = (PdfDictionary) pdfObject;
            PdfName subtype = dictionary.getAsName(PdfName.Subtype);
            if (PdfName.Link.equals(subtype))
                return new PdfLinkAnnotation((PdfDictionary) pdfObject, document);
        }
        return null;
    }

}
