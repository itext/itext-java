package com.itextpdf.core.pdf.annots;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.layer.PdfOCG;

public class PdfAnnotation extends PdfObjectWrapper<PdfDictionary> {

    public PdfAnnotation() {
        super(new PdfDictionary());
    }

    /**
     * Sets the layer this annotation belongs to.
     * @param layer the layer this annotation belongs to
     */
    public void setLayer(final PdfOCG layer) throws PdfException {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

}
