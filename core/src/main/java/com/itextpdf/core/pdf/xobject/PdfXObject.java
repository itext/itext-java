package com.itextpdf.core.pdf.xobject;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.layer.PdfOCG;

public class PdfXObject extends PdfObjectWrapper<PdfStream> {

    public PdfXObject() {
        this(new PdfStream());
    }

    public PdfXObject(PdfStream pdfObject) {
        super(pdfObject);
    }

    static public PdfXObject makeXObject(PdfStream stream) {
        if (PdfName.Form.equals(stream.getAsName(PdfName.Subtype)) || stream.containsKey(PdfName.BBox))
            return new PdfFormXObject(stream);
        else
            return new PdfImageXObject(stream);
    }

    /**
     * Sets the layer this XObject belongs to.
     * @param layer the layer this XObject belongs to
     */
    public void setLayer(PdfOCG layer) {
        getPdfObject().put(PdfName.OC, layer.getIndirectReference());
    }

}
