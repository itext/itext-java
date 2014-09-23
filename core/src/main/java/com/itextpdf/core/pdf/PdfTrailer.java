package com.itextpdf.core.pdf;

public class PdfTrailer extends PdfDictionary {

    public PdfTrailer() {
        super();
    }

    public PdfCatalog getCatalog() {
        return (PdfCatalog) get(PdfName.Root);
    }

    public void setCatalog(PdfCatalog catalog) {
        put(PdfName.Root, catalog);
    }

    public PdfDocumentInfo getDocumentInfo() {
        return (PdfDocumentInfo) get(PdfName.Info);
    }

    public void setInfo(PdfDocumentInfo info) {
        put(PdfName.Info, info);
    }


    public void setSize(int size) {
        put(PdfName.Size, new PdfNumber(size));
    }

}
