package com.itextpdf.core.pdf.xobject;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfFormXObject extends PdfXObject {

    protected PdfResources resources = null;

    public PdfFormXObject(PdfDocument document, Rectangle bBox) throws PdfException {
        super(new PdfStream(document), document);
        pdfObject.put(PdfName.Resources, (resources = new PdfResources()).getPdfObject());
        pdfObject.put(PdfName.Type, PdfName.XObject);
        pdfObject.put(PdfName.Subtype, PdfName.Form);
        pdfObject.put(PdfName.BBox, new PdfArray(bBox));
    }

    public PdfFormXObject(PdfStream pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
        PdfDictionary resources = pdfObject.getAsDictionary(PdfName.Resources);
        if (resources != null)
            this.resources = new PdfResources(resources);
        else {
            this.resources = new PdfResources();
            pdfObject.put(PdfName.Resources, resources);
        }
    }

    /**
     * Creates form XObject from page content.
     *
     * @param page
     */
    public PdfFormXObject(PdfPage page) throws PdfException {
        this(page.getDocument(), page.getCropBox());
        pdfObject.getOutputStream().writeBytes(page.getContentBytes());
        resources = new PdfResources((PdfDictionary)page.getResources().getPdfObject().copy());
        pdfObject.put(PdfName.Resources, resources.getPdfObject());

    }

    public PdfResources getResources() {
        return resources;
    }

    @Override
    public PdfFormXObject copy(PdfDocument document) throws PdfException {
        return new PdfFormXObject((PdfStream)getPdfObject().copy(document), document);
    }
}
