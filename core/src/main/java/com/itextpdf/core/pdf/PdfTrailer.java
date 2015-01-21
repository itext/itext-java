package com.itextpdf.core.pdf;

public class PdfTrailer extends PdfObjectWrapper<PdfDictionary> {



    public PdfTrailer(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfTrailer() {
        this(new PdfDictionary());
    }

    public void setCatalog(PdfCatalog catalog) {
        getPdfObject().put(PdfName.Root, catalog.getPdfObject());
    }

    public void setInfo(PdfDocumentInfo info) {
        getPdfObject().put(PdfName.Info, info.getPdfObject());
    }

    public void setSize(int size) {
        getPdfObject().put(PdfName.Size, new PdfNumber(size));
    }

}
