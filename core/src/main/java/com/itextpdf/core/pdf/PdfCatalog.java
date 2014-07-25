package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.IOException;

public class PdfCatalog extends PdfDictionary {

    protected PdfPages pages = null;

    public PdfCatalog(PdfDocument doc) {
        super(doc);
        pages = new PdfPages(doc);
        put(PdfName.Type, PdfName.Catalog);
        put(PdfName.Pages, pages);
    }

    @Override
    public boolean canBeInObjStm() {
        return false;
    }

    public void addPage(PdfPage page) {
        pages.addPage(page);
    }

    public void insertPage(PdfPage page, int index) {
        pages.insertPage(page, index);
    }

    static class PdfPages extends PdfDictionary {

        protected PdfArray kids = null;
        protected int pageCount = 0;

        public PdfPages(PdfDocument doc) {
            super(doc);
            kids = new PdfArray(doc);
            put(PdfName.Type, PdfName.Pages);
            put(PdfName.Kids, kids);
        }

        public void addPage(PdfPage page) {
            page.put(PdfName.Parent, this);
            kids.add(page);
            pageCount++;
        }

        public void insertPage(PdfPage page, int index) {
            page.put(PdfName.Parent, this);
            kids.add(index, page);
            pageCount++;
        }

        @Override
        public boolean flush() throws IOException, PdfException {
            put(PdfName.Count, new PdfNumber(pageCount));
            return super.flush();
        }
    }

}
