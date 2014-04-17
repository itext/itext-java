package com.itextpdf.core.pdf;

import com.itextpdf.core.geom.PageSize;

import java.util.ArrayList;
import java.util.List;

public class PdfDocument {

    protected List<PdfPage> pages = new ArrayList<PdfPage>();
    protected PdfPage currentPage = null;
    protected PageSize defaultPageSize = PageSize.A4;


    /**
     * Open PDF document in reading mode.
     * @param reader
     */
    public PdfDocument(PdfReader reader) {

    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     * @param writer
     */
    public PdfDocument(PdfWriter writer) {

    }

    /**
     * Open PDF document in stamping mode.
     * @param reader
     * @param writer
     */
    public PdfDocument(PdfReader reader, PdfWriter writer) {

    }

    /**
     * Closes the document, all open PdfPages and associated PdfWriter and PdfReader.
     */
    public void close() {

    }

    /**
     * Gets the page by page number.
     * @param pageNum
     * @return
     */
    public PdfPage getPage(int pageNum) {
        if (pageNum == PdfPage.FirstPage)
            return pages.get(0);
        if (pageNum == PdfPage.LastPage)
            return pages.get(pages.size() - 1);
        if (pageNum == PdfPage.CurrentPage)
            return currentPage;
        return pages.get(pageNum - 1);
    }

    /**
     * Get the first page of the document.
     * @return
     */
    public PdfPage getFirstPage() {
        return getPage(PdfPage.FirstPage);
    }

    /**
     * Gets the last page of the document.
     * @return
     */
    public PdfPage getLastPage() {
        return getPage(PdfPage.LastPage);
    }

    /**
     * Gets the current page.
     * @return
     */
    public PdfPage getCurrentPage() {
        return getPage(PdfPage.CurrentPage);
    }

    /**
     *
     * @param page
     * @param position
     * @return added page.
     */
    public PdfPage insertPage(PdfPage page, int position) {
        currentPage = page;
        if (position == PdfPage.LastPage) {
            pages.add(currentPage);
        } else {
            pages.add(position - 1, currentPage);
        }
        return currentPage;
    }

    /**
     *
     * @param page
     * @return added page.
     */
    public PdfPage addPage(PdfPage page) {
        return insertPage(page, PdfPage.LastPage);
    }

    public int getNumOfPages() {
        return pages.size();
    }

    public int getPageNum(PdfPage page) {
        return pages.indexOf(page) + 1;
    }

    public PdfDocumentInfo getInfo() {
        return new PdfDocumentInfo(this);
    }

    public PageSize getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(PageSize pageSize) {
        defaultPageSize = pageSize;
    }

}
