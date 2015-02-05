package com.itextpdf.core.pdf.xobject;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.layer.PdfOCG;

public class PdfXObject extends PdfObjectWrapper<PdfStream> {

    public PdfXObject(PdfDocument document) throws PdfException {
        this(new PdfStream(document), document);
    }

    public PdfXObject(PdfStream pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    static public PdfXObject makeXObject(PdfStream stream, PdfDocument document) throws PdfException {
        if (PdfName.Form.equals(stream.getAsName(PdfName.Subtype)) || stream.containsKey(PdfName.BBox))
            return new PdfFormXObject(stream, document);
        else
            return new PdfImageXObject(stream, document);
    }

    /**
     * Sets the layer this XObject belongs to.
     * @param layer the layer this XObject belongs to
     */
    public void setLayer(PdfOCG layer) throws PdfException {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

}
