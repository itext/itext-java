package com.itextpdf.core.pdf;

import com.itextpdf.core.events.EventDispatcher;
import com.itextpdf.core.events.IEventDispatcher;
import com.itextpdf.core.events.IEventHandler;
import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.objects.PdfDictionary;
import com.itextpdf.core.pdf.objects.PdfIndirectReference;
import com.itextpdf.core.pdf.objects.PdfObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfDocument implements IEventDispatcher {

    protected List<PdfPage> pages = new ArrayList<PdfPage>();
    protected PdfPage currentPage = null;
    protected PageSize defaultPageSize = PageSize.DEFAULT;
    protected EventDispatcher eventDispatcher = new EventDispatcher();
    protected PdfWriter writer = null;
    protected PdfReader reader = null;
    protected PdfCatalog catalog = null;
    protected PdfTrailer trailer = null;
    protected PdfDocumentInfo info = null;
    protected boolean closing = false;

    /**
     * Open PDF document in reading mode.
     *
     * @param reader
     */
    public PdfDocument(PdfReader reader) throws IOException {
        this.reader = reader;
        initialize();
        dispatchEvent(new PdfDocumentEvent(this, PdfDocumentEvent.OpenDocument), true);
    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     *
     * @param writer
     */
    public PdfDocument(PdfWriter writer) throws IOException {
        this.writer = writer;
        initialize();
        dispatchEvent(new PdfDocumentEvent(this, PdfDocumentEvent.OpenDocument), true);
    }

    /**
     * Open PDF document in stamping mode.
     *
     * @param reader
     * @param writer
     */
    public PdfDocument(PdfReader reader, PdfWriter writer) throws IOException {
        this.reader = reader;
        this.writer = writer;
        initialize();
        dispatchEvent(new PdfDocumentEvent(this, PdfDocumentEvent.OpenDocument), true);
    }

    public void close() throws IOException {
        closing = true;
        dispatchEvent(new PdfDocumentEvent(this, PdfDocumentEvent.CloseDocument));
        removeAllHandlers();
        if (writer != null) {
            catalog.addPages(pages);
            catalog.flush();
            info.flush();
            writer.flushWaitingObjects();
            int startxref = writer.writeXRefTable();
            writer.writeTrailer(trailer, startxref);
            writer.close();
        }
        if (reader != null)
            reader.close();
        closing = false;
    }

    /**
     * Gets the page by page number.
     *
     * @param pageNum
     * @return
     */
    public PdfPage getPage(int pageNum) {
        if (pageNum == PdfPage.FirstPage)
            return pages.get(0);
        if (pageNum == PdfPage.LastPage)
            return pages.get(pages.size() - 1);
        return pages.get(pageNum - 1);
    }

    /**
     * Get the first page of the document.
     *
     * @return
     */
    public PdfPage getFirstPage() {
        return getPage(PdfPage.FirstPage);
    }

    /**
     * Gets the last page of the document.
     *
     * @return
     */
    public PdfPage getLastPage() {
        return getPage(PdfPage.LastPage);
    }

    /**
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
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.InsertPage, page));
        return currentPage;
    }

    /**
     * @param page
     * @return added page.
     */
    public PdfPage addPage(PdfPage page) {
        return insertPage(page, PdfPage.LastPage);
    }

    public PdfPage addNewPage() {
        return addPage(new PdfPage(this, getDefaultPageSize()));
    }

    public PdfPage addNewPage(PageSize pageSize) {
        return addPage(new PdfPage(this, pageSize));
    }

    public int getNumOfPages() {
        return pages.size();
    }

    public int getPageNum(PdfPage page) {
        return pages.indexOf(page) + 1;
    }

    public void removePage(PdfPage page) {
        pages.remove(page);
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.RemovePage, page));
    }

    public void removePage(int pageNum) {
        removePage(pages.get(pageNum - 1));
    }

    public PdfDocumentInfo getInfo() {
        return info;
    }

    public PageSize getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(PageSize pageSize) {
        defaultPageSize = pageSize;
    }

    @Override
    public void addEventHandler(String type, IEventHandler handler) {
        eventDispatcher.addEventHandler(type, handler);
    }

    @Override
    public void dispatchEvent(com.itextpdf.core.events.Event event) {
        eventDispatcher.dispatchEvent(event);
    }

    @Override
    public void dispatchEvent(com.itextpdf.core.events.Event event, boolean delayed) {
        eventDispatcher.dispatchEvent(event, delayed);
    }

    @Override
    public boolean hasEventHandler(String type) {
        return eventDispatcher.hasEventHandler(type);
    }

    @Override
    public void removeEventHandler(String type, IEventHandler handler) {
        eventDispatcher.removeEventHandler(type, handler);
    }

    @Override
    public void removeAllHandlers() {
        eventDispatcher.removeAllHandlers();
    }

    public PdfWriter getWriter() {
        return writer;
    }

    public PdfIndirectReference getNextIndirectReference(PdfObject object) {
        return new PdfIndirectReference(this, writer.getNextIndirectReferenceNumber(), object);
    }

    protected void initialize() throws IOException {
        if (writer != null) {
            if (reader == null) {
                catalog = new PdfCatalog(this);
                info = new PdfDocumentInfo(this);
                trailer = new PdfTrailer(this);
                trailer.setCatalog(catalog);
                trailer.setInfo(info);
            }
            writer.writeHeader();
        }
    }

    public boolean isClosing() {
        return closing;
    }
}
