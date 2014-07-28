package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    public PdfPage getPage(int pageNum) {
        return pages.getPage(pageNum);
    }

    public int getNumOfPages() {
        return pages.getNumOfPages();
    }

    public int getPageNum(PdfPage page) {
        return pages.getPageNum(page);
    }

    public void removePage(PdfPage page) {
        pages.removePage(page);
    }

    public void removePage(int pageNum) {
        pages.removePage(pageNum);
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
            throw new NotImplementedException();
        }

        public PdfPage getPage(int pageNum) {
            throw new NotImplementedException();
        }

        public int getNumOfPages() {
            throw new NotImplementedException();
        }

        public int getPageNum(PdfPage page) {
            throw new NotImplementedException();
        }

        public void removePage(PdfPage page) {
            throw new NotImplementedException();
        }

        public void removePage(int pageNum) {
            throw new NotImplementedException();
        }

        @Override
        public void flush() throws IOException, PdfException {
            put(PdfName.Count, new PdfNumber(pageCount));
            super.flush();
        }
    }

}
