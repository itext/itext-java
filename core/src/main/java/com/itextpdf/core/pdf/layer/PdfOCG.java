package com.itextpdf.core.pdf.layer;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfIndirectReference;
import com.itextpdf.core.pdf.PdfObject;

/**
 * The interface generalizing the layer types (PdfLayer, PdfLayerMembership).
 */
public interface PdfOCG {

    /**
     * Gets the object representing the layer.
     * @return the object representing the layer
     */
    PdfObject getPdfObject();
    /**
     * Gets the <CODE>PdfIndirectReference</CODE> that represents this layer.
     * @return the <CODE>PdfIndirectReference</CODE> that represents this layer
     */
    PdfIndirectReference getIndirectReference() throws PdfException;

}
