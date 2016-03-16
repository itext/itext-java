package com.itextpdf.kernel.pdf.xobject;

import com.itextpdf.io.NotImplementedException;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.layer.PdfOCG;

public class PdfXObject extends PdfObjectWrapper<PdfStream> {

    private static final long serialVersionUID = -480702872582809954L;

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

    public Float getWidth() {throw new NotImplementedException(); }

    public Float getHeight() { throw new NotImplementedException(); }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
