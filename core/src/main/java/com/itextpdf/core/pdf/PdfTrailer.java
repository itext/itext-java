package com.itextpdf.core.pdf;

public class PdfTrailer extends PdfObjectWrapper<PdfDictionary> {



    public PdfTrailer(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfTrailer() {
        this(new PdfDictionary());
    }

    public void setCatalog(PdfCatalog catalog) {
        pdfObject.put(PdfName.Root, catalog.getPdfObject());
    }

    public void setInfo(PdfDocumentInfo info) {
        pdfObject.put(PdfName.Info, info.getPdfObject());
    }

    public void setSize(int size) {
        pdfObject.put(PdfName.Size, new PdfNumber(size));
    }

}
