package com.itextpdf.core.pdf;

import com.itextpdf.core.events.EventDispatcher;
import com.itextpdf.core.events.IEventDispatcher;
import com.itextpdf.core.events.IEventHandler;
import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.geom.PageSize;

import java.io.IOException;
import java.util.TreeSet;

public class PdfDocument implements IEventDispatcher {

    /**
     * Currently active page.
     */
    protected PdfPage currentPage = null;

    /**
     * Default page size.
     * New page by default will be created with this size.
     */
    protected PageSize defaultPageSize = PageSize.DEFAULT;

    protected EventDispatcher eventDispatcher = new EventDispatcher();

    /**
     * PdfWriter associated with the document.
     * Not null if document opened either in writing or stamping mode.
     */
    protected PdfWriter writer = null;

    /**
     * PdfReader associated with the document.
     * Not null if document is opened either in reading or stamping mode.
     */
    protected PdfReader reader = null;

    /**
     * Document catalog.
     */
    protected PdfCatalog catalog = null;

    /**
     * Document trailed.
     */
    protected PdfTrailer trailer = null;

    /**
     * Document info.
     */
    protected PdfDocumentInfo info = null;

    /**
     * Document version. 1.7 by default.
     */
    protected PdfVersion pdfVersion = PdfVersion.PDF_1_7;

    /**
     * List of indirect objects used in the document.
     */
    protected TreeSet<PdfIndirectReference> indirects = new TreeSet<PdfIndirectReference>();

    /**
     * Current indirect reference number.
     */
    protected int indirectReferenceNumber = 0;


    /**
     * Open PDF document in reading mode.
     *
     * @param reader PDF reader.
     */
    public PdfDocument(PdfReader reader) throws IOException {
        this.reader = reader;
        initialize();
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.OpenDocument, this), true);
    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     *
     * @param writer PDF writer
     */
    public PdfDocument(PdfWriter writer) throws IOException {
        this.writer = writer;
        initialize();
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.OpenDocument, this), true);
    }

    /**
     * Open PDF document in stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     */
    public PdfDocument(PdfReader reader, PdfWriter writer) throws IOException {
        this.reader = reader;
        this.writer = writer;
        initialize();
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.OpenDocument, this), true);
    }

    /**
     * Close PDF document.
     *
     * @throws IOException
     * @throws PdfException
     */
    public void close() throws IOException, PdfException {
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.CloseDocument, this));
        removeAllHandlers();
        if (writer != null) {
            catalog.flush();
            info.flush();
            writer.flushWaitingObjects();
            int startxref = writer.writeXRefTable();
            writer.writeTrailer(startxref);
            writer.close();
        }
        if (reader != null)
            reader.close();
    }

    /**
     * Gets the page by page number.
     *
     * @param pageNum page number.
     * @return page by page number.
     */
    public PdfPage getPage(int pageNum) {
        return catalog.getPage(pageNum);
    }

    /**
     * Get the first page of the document.
     *
     * @return first page of the document.
     */
    public PdfPage getFirstPage() {
        return getPage(PdfPage.FirstPage);
    }

    /**
     * Gets the last page of the document.
     *
     * @return last page.
     */
    public PdfPage getLastPage() {
        return getPage(PdfPage.LastPage);
    }

    /**
     * Inserts page to the document.
     *
     * @param index position to insert page to
     * @param page     page to insert
     * @return inserted page
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage insertPage(int index, PdfPage page) throws PdfException {
        catalog.insertPage(index, page);
        currentPage = page;
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.InsertPage, page));
        return currentPage;
    }

    /**
     * Adds page to the end of document.
     *
     * @param page page to add.
     * @return added page.
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addPage(PdfPage page) throws PdfException {
        catalog.addPage(page);
        return page;
    }

    /**
     * Creates and adds new page to the end of document.
     *
     * @return added page
     */
    public PdfPage addNewPage() {
        PdfPage page = new PdfPage(this, getDefaultPageSize());
        catalog.addNewPage(page);
        return page;
    }

    /**
     * Creates and adds new page wit hthe specified page size.
     *
     * @param pageSize page size of the new page
     * @return added page
     */
    public PdfPage addNewPage(PageSize pageSize) {
        PdfPage page = new PdfPage(this, pageSize);
        catalog.addNewPage(page);
        return page;
    }

    /**
     * Gets number of pages of the document.
     *
     * @return number of pages.
     */
    public int getNumOfPages() {
        return catalog.getNumOfPages();
    }

    /**
     * Gets page number by page.
     *
     * @param page the page.
     * @return page number.
     */
    public int getPageNum(PdfPage page) {
        return catalog.getPageNum(page);
    }

    /**
     * Removes page from the document.
     *
     * @param page a page to remove.
     */
    public boolean removePage(PdfPage page) throws PdfException {
        boolean result = catalog.removePage(page);
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.RemovePage, page));
        return result;
    }

    /**
     * Removes page from the document by page number.
     *
     * @param pageNum a number of page to remove.
     */
    public PdfPage removePage(int pageNum) throws PdfException {
        return catalog.removePage(pageNum);
    }

    /**
     * Gets document information dictionary.
     *
     * @return document information dictionary.
     */
    public PdfDocumentInfo getInfo() {
        return info;
    }

    /**
     * Gets default page size.
     *
     * @return default page size.
     */
    public PageSize getDefaultPageSize() {
        return defaultPageSize;
    }

    /**
     * Sets default page size.
     *
     * @param pageSize page size to be set as default.
     */
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

    /**
     * Gets PdfWriter associated with the document.
     *
     * @return PdfWriter associated with the document.
     */
    public PdfWriter getWriter() {
        return writer;
    }

    /**
     * Creates next available indirect reference.
     *
     * @param object an object for which indirect reference should be created.
     * @return created indirect reference.
     */
    public PdfIndirectReference getNextIndirectReference(PdfObject object) {
        return new PdfIndirectReference(this, ++indirectReferenceNumber, object);
    }

    /**
     * Sets PDF version.
     *
     * @param pdfVersion PDF version to set.
     * @return self.
     */
    public PdfDocument setVersion(PdfVersion pdfVersion) {
        this.pdfVersion = pdfVersion;
        return this;
    }

    /**
     * Gets PDF version.
     *
     * @return PDF version.
     */
    public PdfVersion getPdfVersion() {
        return pdfVersion;
    }

    /**
     * Gets PDF catalog.
     *
     * @return PDF catalog.
     */
    public PdfCatalog getCatalog() {
        return catalog;
    }

    /**
     * Initializes document.
     *
     * @throws IOException
     */
    protected void initialize() throws IOException {
        if (writer != null) {
            writer.pdfDocument = this;
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

    /**
     * Adds indirect reference to list of indirect objects.
     *
     * @param indirectReference indirect reference to add.
     * @throws IOException
     * @throws PdfException
     */
    protected void add(PdfIndirectReference indirectReference) throws IOException, PdfException {
        getIndirects().add(indirectReference);
    }

    /**
     * Gets list of indirect references.
     *
     * @return list of indirect references.
     */
    protected TreeSet<PdfIndirectReference> getIndirects() {
        return indirects;
    }

    protected void setIndirects(TreeSet<PdfIndirectReference> indirects) {
        this.indirects = indirects;
    }

    /**
     * Gets document trailer.
     *
     * @return document trailer.
     */
    protected PdfTrailer getTrailer() {
        return trailer;
    }
}
