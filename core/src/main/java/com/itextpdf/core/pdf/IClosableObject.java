package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.objects.PdfObject;

/**
 * Describes the PDF object which can be closed.
 * The object is flushed to output document when closed.
 */
public interface IClosableObject {

    /**
     * Closes the object.
     * @return the object representation in the PDF document. It can be either direct or indirect object. Closing PdfStream always returns PdfIndirectReference.
     */
    public PdfObject close();

}
