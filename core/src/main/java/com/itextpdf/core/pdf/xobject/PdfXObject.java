package com.itextpdf.core.pdf.xobject;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.layer.PdfOCG;

public class PdfXObject extends PdfObjectWrapper<PdfStream> {

    public PdfXObject(PdfDocument document) {
        this(new PdfStream(document), document);
    }

    public PdfXObject(PdfStream pdfObject, PdfDocument pdfDocument) {
        super(pdfObject, pdfDocument);
    }

    static public PdfXObject makeXObject(PdfStream stream, PdfDocument document) {
        if (PdfName.Form.equals(stream.getAsName(PdfName.Subtype)) || stream.containsKey(PdfName.BBox))
            return new PdfFormXObject(stream, document);
        else
            return new PdfImageXObject(stream, document);
    }

    /**
     * Sets the layer this XObject belongs to.
     * @param layer the layer this XObject belongs to
     */
    public void setLayer(PdfOCG layer) {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

}
