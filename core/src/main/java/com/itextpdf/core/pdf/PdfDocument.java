package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.RandomAccessFileOrArray;
import com.itextpdf.core.Version;
import com.itextpdf.core.events.EventDispatcher;
import com.itextpdf.core.events.IEventDispatcher;
import com.itextpdf.core.events.IEventHandler;
import com.itextpdf.core.events.PdfDocumentEvent;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.core.xmp.*;
import com.itextpdf.core.xmp.options.PropertyOptions;
import com.itextpdf.core.xmp.options.SerializeOptions;

import java.io.IOException;
import java.util.*;

public class PdfDocument implements IEventDispatcher {

    /**
     * Currently active page.
     */
    protected PdfPage currentPage = null;

    /**
     * Default page size.
     * New page by default will be created with this size.
     */
    protected PageSize defaultPageSize = PageSize.Default;

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
     * XMP Metadata for the document.
     */
    protected byte[] xmpMetadata = null;

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
    protected final PdfXrefTable xref = new PdfXrefTable();

    /**
     * Indicate incremental updates mode of stamping mode.
     */
    protected final boolean appendMode;

    protected PdfStructTreeRoot structTreeRoot;

    protected Integer structParentIndex = null;

    protected boolean closeReader = true;
    protected boolean closeWriter = true;

    /**
     * Open PDF document in reading mode.
     *
     * @param reader PDF reader.
     */
    public PdfDocument(PdfReader reader) throws PdfException {
        if (reader == null) {
            throw new NullPointerException("reader");
        }
        this.reader = reader;
        this.appendMode = false;
        open();
    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     *
     * @param writer PDF writer
     */
    public PdfDocument(PdfWriter writer) throws PdfException {
        if (writer == null) {
            throw new NullPointerException("writer");
        }
        this.writer = writer;
        this.appendMode = false;
        open();
    }

    /**
     * Open PDF document in stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     * @param append if true, incremental updates will
     */
    public PdfDocument(PdfReader reader, PdfWriter writer, boolean append) throws PdfException {
        if (reader == null) {
            throw new NullPointerException("reader");
        }
        if (writer == null) {
            throw new NullPointerException("writer");
        }
        this.reader = reader;
        this.writer = writer;
        this.appendMode = append;
        open();
    }

    /**
     * Open PDF document in stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     */
    public PdfDocument(PdfReader reader, PdfWriter writer) throws PdfException {
        this(reader, writer, false);
    }


    /**
     * Use this method to set the XMP Metadata.
     *
     * @param xmpMetadata The xmpMetadata to set.
     */
    public void setXmpMetadata(final byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }

    public void setXmpMetadata(final XMPMeta xmpMeta, final SerializeOptions serializeOptions) throws XMPException {
        setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    public void setXmpMetadata(final XMPMeta xmpMeta) throws XMPException {
        SerializeOptions serializeOptions = new SerializeOptions();
        serializeOptions.setPadding(2000);
        setXmpMetadata(xmpMeta, serializeOptions);
    }

    public void setXmpMetadata() throws XMPException, PdfException {
        XMPMeta xmpMeta = XMPMetaFactory.create();
        xmpMeta.setObjectName(XMPConst.TAG_XMPMETA);
        xmpMeta.setObjectName("");
        try {
            xmpMeta.setProperty(XMPConst.NS_DC, PdfConst.Format, "application/pdf");
            xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Producer, Version.getInstance().getVersion());
        } catch (XMPException ignored) {
        }
        PdfDictionary docInfo = info.getPdfObject();
        if (docInfo != null) {
            PdfName key;
            PdfObject obj;
            String value;
            for (PdfName pdfName : docInfo.keySet()) {
                key = pdfName;
                obj = docInfo.get(key);
                if (obj == null)
                    continue;
                if (obj.getType() != PdfObject.String)
                    continue;
                value = ((PdfString) obj).toUnicodeString();
                if (PdfName.Title.equals(key)) {
                    xmpMeta.setLocalizedText(XMPConst.NS_DC, PdfConst.Title, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
                } else if (PdfName.Author.equals(key)) {
                    xmpMeta.appendArrayItem(XMPConst.NS_DC, PdfConst.Creator, new PropertyOptions(PropertyOptions.ARRAY_ORDERED), value, null);
                } else if (PdfName.Subject.equals(key)) {
                    xmpMeta.setLocalizedText(XMPConst.NS_DC, PdfConst.Description, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
                } else if (PdfName.Keywords.equals(key)) {
                    for (String v : value.split(",|;"))
                        if (v.trim().length() > 0)
                            xmpMeta.appendArrayItem(XMPConst.NS_DC, PdfConst.Subject, new PropertyOptions(PropertyOptions.ARRAY), v.trim(), null);
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Keywords, value);
                } else if (PdfName.Producer.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Producer, value);
                } else if (PdfName.Creator.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.CreatorTool, value);
                } else if (PdfName.CreationDate.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.CreateDate, PdfDate.getW3CDate(value));
                } else if (PdfName.ModDate.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.ModifyDate, PdfDate.getW3CDate(value));
                }
            }
        }
        setXmpMetadata(xmpMeta);
    }

    public PdfStream getXmpMetadata() throws XMPException, PdfException {
        return getCatalog().getPdfObject().getAsStream(PdfName.Metadata);
    }

    public PdfObject getPdfObject(final int objNum) throws PdfException {
        PdfIndirectReference reference = xref.get(objNum);
        if (reference == null) {
            return null;
        } else {
            return reference.getRefersTo();
        }
    }

    /**
     * Gets the page by page number.
     *
     * @param pageNum page number.
     * @return page by page number.
     */
    public PdfPage getPage(int pageNum) throws PdfException {
        return catalog.getPage(pageNum);
    }

    /**
     * Get the first page of the document.
     *
     * @return first page of the document.
     */
    public PdfPage getFirstPage() throws PdfException {
        return getPage(PdfPage.FirstPage);
    }

    /**
     * Gets the last page of the document.
     *
     * @return last page.
     */
    public PdfPage getLastPage() throws PdfException {
        return getPage(PdfPage.LastPage);
    }

    /**
     * Creates and adds new page to the end of document.
     *
     * @return added page
     */
    public PdfPage addNewPage() throws PdfException {
        return addNewPage(getDefaultPageSize());
    }

    /**
     * Creates and adds new page with the specified page size.
     *
     * @param pageSize page size of the new page
     * @return added page
     */
    public PdfPage addNewPage(PageSize pageSize) throws PdfException {
        PdfPage page = new PdfPage(this, pageSize);
        catalog.addPage(page);
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.InsertPage, page));
        return page;
    }

    /**
     * Creates and inserts new page to the document.
     *
     * @param index position to addPage page to
     * @return inserted page
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addNewPage(int index) throws PdfException {
        return addNewPage(index, getDefaultPageSize());
    }

    /**
     * Creates and inserts new page to the document.
     *
     * @param index    position to addPage page to
     * @param pageSize page size of the new page
     * @return inserted page
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addNewPage(int index, PageSize pageSize) throws PdfException {
        PdfPage page = new PdfPage(this, pageSize);
        catalog.addPage(index, page);
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
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.InsertPage, page));
        return page;
    }

    /**
     * Inserts page to the document.
     *
     * @param index position to addPage page to
     * @param page  page to addPage
     * @return inserted page
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addPage(int index, PdfPage page) throws PdfException {
        catalog.addPage(index, page);
        currentPage = page;
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.InsertPage, page));
        return currentPage;
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
     * Gets {@code PdfWriter} associated with the document.
     *
     * @return PdfWriter associated with the document.
     */
    public PdfWriter getWriter() {
        return writer;
    }

    /**
     * Gets {@code PdfReader} associated with the document.
     *
     * @return PdfReader associated with the document.
     */
    public PdfReader getReader() {
        return reader;
    }

    /**
     * Returns true if the document is opened in append mode, and false otherwise.
     *
     * @return
     */
    public boolean isAppendMode() {
        return appendMode;
    }

    /**
     * Creates next available indirect reference.
     *
     * @param object an object for which indirect reference should be created.
     * @return created indirect reference.
     */
    public PdfIndirectReference createNextIndirectReference(PdfObject object) {
        return xref.createNextIndirectReference(this, object);
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
     * Gets PDF document info.
     *
     * @return PDF document info.
     */
    public PdfDocumentInfo getDocumentInfo() {
        return info;
    }

    /**
     * Close PDF document.
     */
    public void close() throws PdfException {
        try {
            removeAllHandlers();
            if (writer != null) {
                if (catalog.isFlushed())
                    throw new PdfException(PdfException.CannotCloseDocumentWithAlreadyFlushedPdfCatalog);
                if (xmpMetadata != null) {
                    PdfStream xmp = new PdfStream(this);
                    xmp.getOutputStream().write(xmpMetadata);
                    xmp.put(PdfName.Type, PdfName.Metadata);
                    xmp.put(PdfName.Subtype, PdfName.XML);
                    PdfEncryption crypto = writer.getEncryption();
                    if (crypto != null && !crypto.isMetadataEncrypted()) {
                        PdfArray ar = new PdfArray();
                        ar.add(PdfName.Crypt);
                        xmp.put(PdfName.Filter, ar);
                    }
                    catalog.getPdfObject().put(PdfName.Metadata, xmp);
                }
                PdfObject crypto = null;
                if (appendMode) {
                    if (structTreeRoot != null && structTreeRoot.getPdfObject().isModified())
                        structTreeRoot.flush();
                    if (catalog.isOCPropertiesMayHaveChanged() && catalog.getOCProperties(false).getPdfObject().isModified()) {
                        catalog.getOCProperties(false).flush();
                    }
                    PdfObject pageRoot = catalog.pageTree.generateTree();
                    if (catalog.getPdfObject().isModified() || pageRoot.isModified()) {
                        catalog.getPdfObject().put(PdfName.Pages, pageRoot);
                        catalog.getPdfObject().flush(false);
                    }
                    if (info.getPdfObject().isModified()) {
                        info.flush();
                    }
                    writer.flushModifiedWaitingObjects();
                    if (writer.crypto != null) {
                        assert reader.getCryptoRef() != null : "Conflict with source encryption";
                        crypto = reader.getCryptoRef();
                    }
                } else {
                    if (structTreeRoot != null)
                        structTreeRoot.flush();
                    if (catalog.isOCPropertiesMayHaveChanged()) {
                        catalog.getPdfObject().put(PdfName.OCProperties, catalog.getOCProperties(false).getPdfObject());
                        catalog.getOCProperties(false).flush();
                    }
                    catalog.getPdfObject().put(PdfName.Pages, catalog.pageTree.generateTree());
                    catalog.getPdfObject().flush(false);
                    info.flush();
                    writer.flushWaitingObjects();
                }

                byte[] originalFileID = null;
                if (crypto == null && writer.crypto != null) {
                    originalFileID = writer.crypto.documentID;
                    crypto = writer.crypto.getEncryptionDictionary();
                    crypto.makeIndirect(this);
                    // To avoid encryption of XrefStream and Encryption dictionary remove crypto.
                    // NOTE. No need in reverting, because it is the last operation with the document.
                    writer.crypto = null;
                    crypto.flush(false);
                }

                PdfObject fileId;
                boolean isModified = false;
                if (originalFileID == null) {
                    if (getReader() != null) {
                        originalFileID = getReader().getOriginalFileId();
                        isModified = true;
                    }
                    if (originalFileID == null) {
                        originalFileID = PdfEncryption.createDocumentId();
                    }
                }
                // if originalFIleID comes from crypto, it means that no need in checking modified state.
                // For crypto purposes new documentId always generated.
                fileId = PdfEncryption.createInfoId(originalFileID, isModified);
                xref.writeXrefTableAndTrailer(this, fileId, crypto);
                if (isCloseWriter())
                    writer.close();
            }
            if (reader != null && isCloseReader())
                reader.close();
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotCloseDocument, e, this);
        }
    }

    public boolean isTagged() {
        return structTreeRoot != null;
    }

    public void setTagged() throws PdfException {
        if (structTreeRoot == null) {
            structTreeRoot = new PdfStructTreeRoot(this);
            catalog.getPdfObject().put(PdfName.StructTreeRoot, structTreeRoot.getPdfObject());
            catalog.getPdfObject().put(PdfName.MarkInfo, new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
                put(PdfName.Marked, PdfBoolean.PdfTrue);
            }}));
            structParentIndex = new Integer(0);
        }
    }

    public PdfStructTreeRoot getStructTreeRoot() {
        return structTreeRoot;
    }

    public Integer getNextStructParentIndex() {
        return structParentIndex++;
    }

    /**
     * Copies a range of pages from current document to {@code toDocument}.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pageFrom         start of the range of pages to be copied.
     * @param pageTo           end of the range of pages to be copied.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     * @return list of copied pages
     * @throws PdfException
     */
    public List<PdfPage> copyPages(int pageFrom, int pageTo, PdfDocument toDocument, int insertBeforePage) throws PdfException {
        TreeSet<Integer> pages = new TreeSet<Integer>();
        for (int i = pageFrom; i <= pageTo; i++) {
            pages.add(i);
        }
        return copyPages(pages, toDocument, insertBeforePage);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pageFrom
     * @param pageTo
     * @param toDocument
     * @return list of copied pages
     * @throws PdfException
     */
    public List<PdfPage> copyPages(int pageFrom, int pageTo, PdfDocument toDocument) throws PdfException {
        return copyPages(pageFrom, pageTo, toDocument, toDocument.getNumOfPages() + 1);
    }


    /**
     * Copies a range of pages from current document to {@code toDocument}.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pagesToCopy      list of pages to be copied.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     * @return list of copied pages
     * @throws PdfException
     */
    public List<PdfPage> copyPages(TreeSet<Integer> pagesToCopy, PdfDocument toDocument, int insertBeforePage) throws PdfException {
        List<PdfPage> copiedPages = new ArrayList<PdfPage>();
        LinkedHashMap<PdfPage, PdfPage> page2page = new LinkedHashMap<PdfPage, PdfPage>();
        for (Integer pageNum : pagesToCopy) {
            PdfPage page = getPage(pageNum);
            PdfPage newPage = page.copy(toDocument);
            copiedPages.add(newPage);
            page2page.put(page, newPage);
            if (insertBeforePage < toDocument.getNumOfPages() + 1) {
                toDocument.addPage(insertBeforePage, newPage);
            } else {
                toDocument.addPage(newPage);
            }
            insertBeforePage++;
        }
        if (toDocument.isTagged()) {
            if (insertBeforePage > toDocument.getNumOfPages())
                getStructTreeRoot().copyToDocument(toDocument, page2page);
            else
                getStructTreeRoot().copyToDocument(toDocument, insertBeforePage, page2page);
        }
        return copiedPages;
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pagesToCopy list of pages to be copied.
     * @param toDocument  a document to copy pages to.
     * @return list of copied pages
     * @throws PdfException
     */
    public List<PdfPage> copyPages(TreeSet<Integer> pagesToCopy, PdfDocument toDocument) throws PdfException {
        return copyPages(pagesToCopy, toDocument, toDocument.getNumOfPages() + 1);
    }

    public boolean isCloseReader() {
        return closeReader;
    }

    public void setCloseReader(boolean closeReader) {
        this.closeReader = closeReader;
    }

    public boolean isCloseWriter() {
        return closeWriter;
    }

    public void setCloseWriter(boolean closeWriter) {
        this.closeWriter = closeWriter;
    }

    /**
     * Initializes document.
     *
     * @throws PdfException
     */
    protected void open() throws PdfException {
        try {
            if (reader != null) {
                reader.pdfDocument = this;
                reader.readPdf();
                trailer = new PdfTrailer(reader.trailer);
                catalog = new PdfCatalog((PdfDictionary) trailer.getPdfObject().get(PdfName.Root, true), this);
                info = new PdfDocumentInfo((PdfDictionary) trailer.getPdfObject().get(PdfName.Info, true), this);
                PdfDictionary str = catalog.getPdfObject().getAsDictionary(PdfName.StructTreeRoot);
                if (str != null) {
                    structTreeRoot = new PdfStructTreeRoot(str, this);
                    structParentIndex = getStructTreeRoot().getStructParentIndex() + 1;
                }
                if (appendMode && (reader.hasRebuiltXref() || reader.hasFixedXref()))
                    throw new PdfException(PdfException.AppendModeRequiresADocumentWithoutErrorsEvenIfRecoveryWasPossible);
            }
            if (writer != null) {
                writer.document = this;
                if (reader == null) {
                    catalog = new PdfCatalog(this);
                    info = new PdfDocumentInfo(this).addCreationDate();
                    trailer = new PdfTrailer();
                    trailer.setCatalog(catalog);
                    trailer.setInfo(info);
                } else {
                    info.addModDate();
                }
            }
            if (appendMode) {       // Due to constructor reader and writer not null.
                assert reader != null;
                RandomAccessFileOrArray file = reader.tokens.getSafeFile();
                int n;
                byte[] buffer = new byte[8192];
                while ((n = file.read(buffer)) > 0) {
                    writer.write(buffer, 0, n);
                }
                file.close();
                writer.write((byte) '\n');
                //TODO log if full compression differs
                writer.setFullCompression(reader.hasXrefStm());
            } else if (writer != null) {
                writer.writeHeader();
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotOpenDocument, e, this);
        }
    }

    /**
     * Gets list of indirect references.
     *
     * @return list of indirect references.
     */
    protected PdfXrefTable getXref() {
        return xref;
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
