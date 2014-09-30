package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.IOException;

public class PdfCatalog extends PdfDictionary {

    protected PdfPagesTree pageTree = null;

    public PdfCatalog(PdfDocument doc) {
        super();
        makeIndirect(doc);
        pageTree = new PdfPagesTree(doc);
        put(PdfName.Type, PdfName.Catalog);
    }

    public void addPage(PdfPage page) throws PdfException {
        if (page.isFlushed())
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted);
        pageTree.addPage(page);
    }

    protected void addNewPage(PdfPage page) {
        pageTree.addPage(page);
    }

    public void insertPage(int index, PdfPage page) throws PdfException {
        pageTree.insertPage(index, page);
    }

    public PdfPage getPage(int pageNum) {
        return pageTree.getPage(pageNum);
    }

    public int getNumOfPages() {
        return pageTree.getNumOfPages();
    }

    public int getPageNum(PdfPage page) {
        return pageTree.getPageNum(page);
    }

    public boolean removePage(PdfPage page) throws PdfException {
        return pageTree.removePage(page);
    }

    public PdfPage removePage(int pageNum) throws PdfException {
        return pageTree.removePage(pageNum);
    }

    @Override
    public void flush(boolean canBeInObjStm) throws IOException, PdfException {
        if (isFlushed())
            return;
        put(PdfName.Pages, pageTree.generateTree());
        super.flush(canBeInObjStm);
    }

}
