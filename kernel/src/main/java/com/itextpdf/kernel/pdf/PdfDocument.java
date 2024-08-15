/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.confirmations.EventConfirmationType;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.utils.DIContainer;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.actions.events.FlushPdfDocumentEvent;
import com.itextpdf.kernel.actions.events.ITextCoreProductEvent;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.events.EventDispatcher;
import com.itextpdf.kernel.events.IEventDispatcher;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.numbering.EnglishAlphabetNumbering;
import com.itextpdf.kernel.numbering.RomanNumbering;
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.kernel.pdf.filespec.PdfEncryptedPayloadFileSpecFactory;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.statistics.NumberOfPagesStatisticsEvent;
import com.itextpdf.kernel.pdf.statistics.SizeOfPdfStatisticsEvent;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.utils.ValidationContainer;
import com.itextpdf.kernel.utils.ValidationContext;
import com.itextpdf.kernel.xmp.PdfConst;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.kernel.xmp.options.SerializeOptions;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main enter point to work with PDF document.
 */
public class PdfDocument implements IEventDispatcher, Closeable {
    //
    private static final PdfName[] PDF_NAMES_TO_REMOVE_FROM_ORIGINAL_TRAILER = new PdfName[] {
        PdfName.Encrypt,
        PdfName.Size,
        PdfName.Prev,
        PdfName.Root,
        PdfName.Info,
        PdfName.ID,
        PdfName.XRefStm,
    };

    private static final IPdfPageFactory pdfPageFactory = new PdfPageFactory();
    protected final StampingProperties properties;
    /**
     * List of indirect objects used in the document.
     */
    final PdfXrefTable xref = new PdfXrefTable();
    private final Map<PdfIndirectReference, PdfFont> documentFonts = new HashMap<>();
    private final SequenceId documentId;
    /**
     * To be adjusted destinations.
     * Key - originating page on the source document
     * Value - a hashmap of Parent pdf objects and destinations to be updated
     */
    private final List<DestinationMutationInfo> pendingDestinationMutations =
            new ArrayList<DestinationMutationInfo>();
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
    protected PdfDictionary trailer = null;
    /**
     * Document info.
     */
    protected PdfDocumentInfo info = null;
    /**
     * Document version.
     */
    protected PdfVersion pdfVersion = PdfVersion.PDF_1_7;
    protected FingerPrint fingerPrint;
    protected SerializeOptions serializeOptions = new SerializeOptions();
    protected PdfStructTreeRoot structTreeRoot;
    protected int structParentIndex = -1;
    protected boolean closeReader = true;
    protected boolean closeWriter = true;
    protected boolean isClosing = false;
    protected boolean closed = false;
    /**
     * flag determines whether to write unused objects to result document
     */
    protected boolean flushUnusedObjects = false;
    protected TagStructureContext tagStructureContext;
    /**
     * Cache of already serialized objects from this document for smart mode.
     */
    Map<PdfIndirectReference, byte[]> serializedObjectsCache = new HashMap<>();
    /**
     * Handler which will be used for decompression of pdf streams.
     */
    MemoryLimitsAwareHandler memoryLimitsAwareHandler = null;
    /**
     * Default page size.
     * New page by default will be created with this size.
     */
    private PageSize defaultPageSize = PageSize.DEFAULT;
    /**
     * The original (first) id when the document is read initially.
     */
    private PdfString originalDocumentId;
    /**
     * The original modified (second) id when the document is read initially.
     */
    private PdfString modifiedDocumentId;
    private PdfFont defaultFont = null;
    private EncryptedEmbeddedStreamsHandler encryptedEmbeddedStreamsHandler;

    private final DIContainer diContainer = new DIContainer();

    /**
     * Open PDF document in reading mode.
     *
     * @param reader PDF reader.
     */
    public PdfDocument(PdfReader reader) {
        this(reader, new DocumentProperties());
    }

    /**
     * Open PDF document in reading mode.
     *
     * @param reader     PDF reader.
     * @param properties document properties
     */
    public PdfDocument(PdfReader reader, DocumentProperties properties) {
        if (reader == null) {
            throw new IllegalArgumentException("The reader in PdfDocument constructor can not be null.");
        }
        documentId = new SequenceId();
        this.reader = reader;
        // default values of the StampingProperties doesn't affect anything
        this.properties = new StampingProperties();
        this.properties.setEventCountingMetaInfo(properties.metaInfo);
        open(null);
    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     *
     * @param writer PDF writer
     */
    public PdfDocument(PdfWriter writer) {
        this(writer, new DocumentProperties());
    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     *
     * @param writer     PDF writer
     * @param properties document properties
     */
    public PdfDocument(PdfWriter writer, DocumentProperties properties) {
        if (writer == null) {
            throw new IllegalArgumentException("The writer in PdfDocument constructor can not be null.");
        }
        documentId = new SequenceId();
        this.writer = writer;
        // default values of the StampingProperties doesn't affect anything
        this.properties = new StampingProperties();
        this.properties.setEventCountingMetaInfo(properties.metaInfo);
        open(writer.properties.pdfVersion);
    }

    /**
     * Opens PDF document in the stamping mode.
     * <br>
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     */
    public PdfDocument(PdfReader reader, PdfWriter writer) {
        this(reader, writer, new StampingProperties());
    }

    /**
     * Open PDF document in stamping mode.
     *
     * @param reader     PDF reader.
     * @param writer     PDF writer.
     * @param properties properties of the stamping process
     */
    public PdfDocument(PdfReader reader, PdfWriter writer, StampingProperties properties) {
        if (reader == null) {
            throw new IllegalArgumentException("The reader in PdfDocument constructor can not be null.");
        }
        if (writer == null) {
            throw new IllegalArgumentException("The writer in PdfDocument constructor can not be null.");
        }
        documentId = new SequenceId();
        this.reader = reader;
        this.writer = writer;
        this.properties = properties;

        boolean writerHasEncryption = writerHasEncryption();
        if (properties.appendMode && writerHasEncryption) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.warn(IoLogMessageConstant.WRITER_ENCRYPTION_IS_IGNORED_APPEND);
        }
        if (properties.preserveEncryption && writerHasEncryption) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.warn(IoLogMessageConstant.WRITER_ENCRYPTION_IS_IGNORED_PRESERVE);
        }

        open(writer.properties.pdfVersion);
    }

    /**
     * Sets the XMP Metadata.
     *
     * @param xmpMeta          the xmpMetadata to set
     * @param serializeOptions serialization options
     *
     * @throws XMPException on serialization errors
     */
    public void setXmpMetadata(XMPMeta xmpMeta, SerializeOptions serializeOptions) throws XMPException {
        this.serializeOptions = serializeOptions;
        setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    /**
     * Use this method to set the XMP Metadata.
     *
     * @param xmpMetadata The xmpMetadata to set.
     */
    protected void setXmpMetadata(byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }

    /**
     * Sets the XMP Metadata.
     *
     * @param xmpMeta the xmpMetadata to set
     *
     * @throws XMPException on serialization errors
     */
    public void setXmpMetadata(XMPMeta xmpMeta) throws XMPException {
        serializeOptions.setPadding(2000);
        setXmpMetadata(xmpMeta, serializeOptions);
    }

    /**
     * Gets XMPMetadata.
     *
     * @return the XMPMetadata
     */
    public byte[] getXmpMetadata() {
        return getXmpMetadata(false);
    }

    /**
     * Gets XMPMetadata or create a new one.
     *
     * @param createNew if true, create a new empty XMPMetadata if it did not present.
     *
     * @return existed or newly created XMPMetadata byte array.
     */
    public byte[] getXmpMetadata(boolean createNew) {
        if (xmpMetadata == null && createNew) {
            XMPMeta xmpMeta = XMPMetaFactory.create();
            xmpMeta.setObjectName(XMPConst.TAG_XMPMETA);
            xmpMeta.setObjectName("");
            addCustomMetadataExtensions(xmpMeta);
            try {
                xmpMeta.setProperty(XMPConst.NS_DC, PdfConst.Format, "application/pdf");
                setXmpMetadata(xmpMeta);
            } catch (XMPException ignored) {
            }
        }
        return xmpMetadata;
    }

    /**
     * Gets PdfObject by object number.
     *
     * @param objNum object number.
     *
     * @return {@link PdfObject} or {@code null}, if object not found.
     */
    public PdfObject getPdfObject(int objNum) {
        checkClosingStatus();
        PdfIndirectReference reference = xref.get(objNum);
        if (reference == null) {
            return null;
        } else {
            return reference.getRefersTo();
        }
    }


    /**
     * Get number of indirect objects in the document.
     *
     * @return number of indirect objects.
     */
    public int getNumberOfPdfObjects() {
        return xref.size();
    }

    /**
     * Gets the page by page number.
     *
     * @param pageNum page number.
     *
     * @return page by page number.
     *
     * @throws PdfException in case the page tree is broken
     */
    public PdfPage getPage(int pageNum) {
        checkClosingStatus();
        return catalog.getPageTree().getPage(pageNum);
    }

    /**
     * Gets the {@link PdfPage} instance by {@link PdfDictionary}.
     *
     * @param pageDictionary {@link PdfDictionary} that present page.
     *
     * @return page by {@link PdfDictionary}.
     */
    public PdfPage getPage(PdfDictionary pageDictionary) {
        checkClosingStatus();
        return catalog.getPageTree().getPage(pageDictionary);
    }

    /**
     * Get the first page of the document.
     *
     * @return first page of the document.
     */
    public PdfPage getFirstPage() {
        checkClosingStatus();
        return getPage(1);
    }

    /**
     * Gets the last page of the document.
     *
     * @return last page.
     */
    public PdfPage getLastPage() {
        return getPage(getNumberOfPages());
    }

    /**
     * Gets current memory limits handler
     *
     * @return {@code MemoryLimitsAwareHandler} instance
     */
    public MemoryLimitsAwareHandler getMemoryLimitsAwareHandler() {
        return memoryLimitsAwareHandler;
    }

    /**
     * Marks {@link PdfStream} object as embedded file stream. Note that this method is for internal usage.
     * To add an embedded file to the PDF document please use specialized API for file attachments.
     * (e.g. {@link PdfDocument#addFileAttachment(String, PdfFileSpec)}, {@link PdfPage#addAnnotation(PdfAnnotation)})
     *
     * @param stream to be marked as embedded file stream
     */
    public void markStreamAsEmbeddedFile(PdfStream stream) {
        encryptedEmbeddedStreamsHandler.storeEmbeddedStream(stream);
    }

    /**
     * Creates and adds new page to the end of document.
     *
     * @return added page
     */
    public PdfPage addNewPage() {
        return addNewPage(getDefaultPageSize());
    }

    /**
     * Creates and adds new page with the specified page size.
     *
     * @param pageSize page size of the new page
     *
     * @return added page
     */
    public PdfPage addNewPage(PageSize pageSize) {
        checkClosingStatus();
        PdfPage page = getPageFactory().createPdfPage(this, pageSize);
        checkAndAddPage(page);
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.START_PAGE, page));
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.INSERT_PAGE, page));
        return page;
    }

    /**
     * Creates and inserts new page to the document.
     *
     * @param index position to addPage page to
     *
     * @return inserted page
     *
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addNewPage(int index) {
        return addNewPage(index, getDefaultPageSize());
    }

    /**
     * Creates and inserts new page to the document.
     *
     * @param index    position to addPage page to
     * @param pageSize page size of the new page
     *
     * @return inserted page
     *
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addNewPage(int index, PageSize pageSize) {
        checkClosingStatus();
        PdfPage page = getPageFactory().createPdfPage(this, pageSize);
        checkAndAddPage(index, page);
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.START_PAGE, page));
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.INSERT_PAGE, page));
        return page;
    }

    /**
     * Adds page to the end of document.
     *
     * @param page page to add.
     *
     * @return added page.
     *
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addPage(PdfPage page) {
        checkClosingStatus();
        checkAndAddPage(page);
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.INSERT_PAGE, page));
        return page;
    }

    /**
     * Inserts page to the document.
     *
     * @param index position to addPage page to
     * @param page  page to addPage
     *
     * @return inserted page
     *
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addPage(int index, PdfPage page) {
        checkClosingStatus();
        checkAndAddPage(index, page);
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.INSERT_PAGE, page));
        return page;
    }

    /**
     * Gets number of pages of the document.
     *
     * @return number of pages.
     */
    public int getNumberOfPages() {
        checkClosingStatus();
        return catalog.getPageTree().getNumberOfPages();
    }

    /**
     * Gets page number by page.
     *
     * @param page the page.
     *
     * @return page number.
     */
    public int getPageNumber(PdfPage page) {
        checkClosingStatus();
        return catalog.getPageTree().getPageNumber(page);
    }

    /**
     * Gets page number by {@link PdfDictionary}.
     *
     * @param pageDictionary {@link PdfDictionary} that present page.
     *
     * @return page number by {@link PdfDictionary}.
     */
    public int getPageNumber(PdfDictionary pageDictionary) {
        return catalog.getPageTree().getPageNumber(pageDictionary);
    }

    /**
     * Moves page to new place in same document with all it tag structure
     *
     * @param page         page to be moved in document if present
     * @param insertBefore indicates before which page new one will be inserted to
     *
     * @return <tt>true</tt> if this document contained the specified page
     */
    public boolean movePage(PdfPage page, int insertBefore) {
        checkClosingStatus();
        int pageNum = getPageNumber(page);
        if (pageNum > 0) {
            movePage(pageNum, insertBefore);
            return true;
        }
        return false;
    }

    /**
     * Moves page to new place in same document with all it tag structure
     *
     * @param pageNumber   number of Page that will be moved
     * @param insertBefore indicates before which page new one will be inserted to
     */
    public void movePage(int pageNumber, int insertBefore) {
        checkClosingStatus();
        if (insertBefore < 1 || insertBefore > getNumberOfPages() + 1) {
            throw new IndexOutOfBoundsException(
                    MessageFormatUtil.format(KernelExceptionMessageConstant.REQUESTED_PAGE_NUMBER_IS_OUT_OF_BOUNDS,
                            insertBefore));
        }
        PdfPage page = getPage(pageNumber);
        if (isTagged()) {
            getStructTreeRoot().move(page, insertBefore);
            getTagStructureContext().normalizeDocumentRootTag();
        }
        PdfPage removedPage = catalog.getPageTree().removePage(pageNumber);
        if (insertBefore > pageNumber) {
            --insertBefore;
        }
        catalog.getPageTree().addPage(insertBefore, removedPage);
    }

    /**
     * Removes the first occurrence of the specified page from this document,
     * if it is present. Returns <tt>true</tt> if this document
     * contained the specified element (or equivalently, if this document
     * changed as a result of the call).
     *
     * @param page page to be removed from this document, if present
     *
     * @return <tt>true</tt> if this document contained the specified page
     */
    public boolean removePage(PdfPage page) {
        checkClosingStatus();
        int pageNum = getPageNumber(page);
        if (pageNum >= 1) {
            removePage(pageNum);
            return true;
        }
        return false;
    }

    /**
     * Removes page from the document by page number.
     *
     * @param pageNum the one-based index of the PdfPage to be removed
     */
    public void removePage(int pageNum) {
        checkClosingStatus();

        PdfPage removedPage = getPage(pageNum);
        if (removedPage != null && removedPage.isFlushed() && (isTagged() || hasAcroForm())) {
            throw new PdfException(KernelExceptionMessageConstant.FLUSHED_PAGE_CANNOT_BE_REMOVED);
        }

        if (removedPage != null) {
            catalog.removeOutlines(removedPage);
            removeUnusedWidgetsFromFields(removedPage);
            if (isTagged()) {
                getTagStructureContext().removePageTags(removedPage);
            }
            if (!removedPage.isFlushed()) {
                removedPage.getPdfObject().remove(PdfName.Parent);
                removedPage.getPdfObject().getIndirectReference().setFree();
            }

            dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.REMOVE_PAGE, removedPage));
        }

        catalog.getPageTree().removePage(pageNum);
    }


    /**
     * Gets the container containing all available dependencies.
     *
     * @return the container containing all available dependencies.
     */
    public DIContainer getDiContainer() {
        return diContainer;
    }

    /**
     * Gets document information dictionary.
     * {@link PdfDocument#info} is lazy initialized. It will be initialized during the first call of this method.
     * The information dictionary values are synchronized with document XMP Metadata.
     *
     * @return document information dictionary.
     */
    public PdfDocumentInfo getDocumentInfo() {
        checkClosingStatus();
        if (info == null) {
            PdfObject infoDict = trailer.get(PdfName.Info);
            info = new PdfDocumentInfo(
                    infoDict instanceof PdfDictionary ? (PdfDictionary) infoDict : new PdfDictionary(), this);
            XmpMetaInfoConverter.appendMetadataToInfo(xmpMetadata, info);
        }
        return info;
    }

    /**
     * Gets original document id
     * <p>
     * In order to set originalDocumentId  {@link WriterProperties#setInitialDocumentId} should be used
     *
     * @return original dccument id
     */
    public PdfString getOriginalDocumentId() {
        return originalDocumentId;
    }


    /**
     * Gets modified document id
     * <p>
     * In order to set modifiedDocumentId {@link WriterProperties#setModifiedDocumentId} should be used
     *
     * @return modified document id
     */
    public PdfString getModifiedDocumentId() {
        return modifiedDocumentId;
    }

    /**
     * Gets default page size.
     * New pages by default are created with this size.
     *
     * @return default page size
     */
    public PageSize getDefaultPageSize() {
        return defaultPageSize;
    }

    /**
     * Sets default page size.
     * New pages by default will be created with this size.
     *
     * @param pageSize page size to be set as default
     */
    public void setDefaultPageSize(PageSize pageSize) {
        defaultPageSize = pageSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventHandler(String type, com.itextpdf.kernel.events.IEventHandler handler) {
        eventDispatcher.addEventHandler(type, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchEvent(com.itextpdf.kernel.events.Event event) {
        eventDispatcher.dispatchEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispatchEvent(com.itextpdf.kernel.events.Event event, boolean delayed) {
        eventDispatcher.dispatchEvent(event, delayed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEventHandler(String type) {
        return eventDispatcher.hasEventHandler(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventHandler(String type, com.itextpdf.kernel.events.IEventHandler handler) {
        eventDispatcher.removeEventHandler(type, handler);
    }

    /**
     * {@inheritDoc}
     */
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
        checkClosingStatus();
        return writer;
    }

    /**
     * Gets {@code PdfReader} associated with the document.
     *
     * @return PdfReader associated with the document.
     */
    public PdfReader getReader() {
        checkClosingStatus();
        return reader;
    }

    /**
     * Returns {@code true} if the document is opened in append mode, and {@code false} otherwise.
     *
     * @return {@code true} if the document is opened in append mode, and {@code false} otherwise.
     */
    public boolean isAppendMode() {
        checkClosingStatus();
        return properties.appendMode;
    }

    /**
     * Creates next available indirect reference.
     *
     * @return created indirect reference.
     */
    public PdfIndirectReference createNextIndirectReference() {
        checkClosingStatus();
        return xref.createNextIndirectReference(this);
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
        checkClosingStatus();
        return catalog;
    }

    /**
     * Close PDF document.
     */
    @Override
    public void close() {
        if (closed) {
            return;
        }
        isClosing = true;
        try {
            if (writer != null) {
                if (catalog.isFlushed()) {
                    throw new PdfException(
                            KernelExceptionMessageConstant.CANNOT_CLOSE_DOCUMENT_WITH_ALREADY_FLUSHED_PDF_CATALOG);
                }

                EventManager manager = EventManager.getInstance();
                manager.onEvent(new NumberOfPagesStatisticsEvent(catalog.getPageTree().getNumberOfPages(),
                        ITextCoreProductData.getInstance()));
                // The event will prepare document for flushing, i.e. will set an appropriate producer line
                manager.onEvent(new FlushPdfDocumentEvent(this));
                dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.START_DOCUMENT_CLOSING, this));

                updateXmpMetadata();
                // In PDF 2.0, all the values except CreationDate and ModDate are deprecated. Remove them now
                if (pdfVersion.compareTo(PdfVersion.PDF_2_0) >= 0) {
                    for (PdfName deprecatedKey : PdfDocumentInfo.PDF20_DEPRECATED_KEYS) {
                        getDocumentInfo().getPdfObject().remove(deprecatedKey);
                    }
                }
                if (getXmpMetadata() != null) {
                    PdfStream xmp = catalog.getPdfObject().getAsStream(PdfName.Metadata);
                    if (isAppendMode() && xmp != null && !xmp.isFlushed() && xmp.getIndirectReference() != null) {
                        // Use existing object for append mode
                        xmp.setData(xmpMetadata);
                        xmp.setModified();
                    } else {
                        // Create new object
                        xmp = (PdfStream) new PdfStream().makeIndirect(this);
                        xmp.getOutputStream().write(xmpMetadata);
                        catalog.getPdfObject().put(PdfName.Metadata, xmp);
                        catalog.setModified();
                    }
                    xmp.put(PdfName.Type, PdfName.Metadata);
                    xmp.put(PdfName.Subtype, PdfName.XML);
                    if (writer.crypto != null && !writer.crypto.isMetadataEncrypted()) {
                        PdfArray ar = new PdfArray();
                        ar.add(PdfName.Crypt);
                        xmp.put(PdfName.Filter, ar);
                    }
                }

                if (!properties.appendMode && catalog.isOCPropertiesMayHaveChanged()) {
                    catalog.getPdfObject().put(PdfName.OCProperties, catalog.getOCProperties(false).getPdfObject());
                }

                checkIsoConformance();

                if (getNumberOfPages() == 0) {
                    // Add new page here, not in PdfPagesTree#generateTree method, so that any page
                    // operations are available when handling the START_PAGE and INSERT_PAGE events
                    addNewPage();
                }

                PdfObject crypto = null;
                final Set<PdfIndirectReference> forbiddenToFlush = new HashSet<>();
                if (properties.appendMode) {
                    if (structTreeRoot != null) {
                        tryFlushTagStructure(true);
                    }
                    if (catalog.isOCPropertiesMayHaveChanged() && catalog.getOCProperties(false).getPdfObject()
                            .isModified()) {
                        catalog.getOCProperties(false).flush();
                    }
                    if (catalog.pageLabels != null) {
                        catalog.put(PdfName.PageLabels, catalog.pageLabels.buildTree());
                    }

                    for (Map.Entry<PdfName, PdfNameTree> entry : catalog.nameTrees.entrySet()) {
                        PdfNameTree tree = entry.getValue();
                        if (tree.isModified()) {
                            ensureTreeRootAddedToNames(tree.buildTree().makeIndirect(this), entry.getKey());
                        }
                    }

                    PdfObject pageRoot = catalog.getPageTree().generateTree();
                    flushInfoDictionary(properties.appendMode);
                    if (catalog.getPdfObject().isModified() || pageRoot.isModified()) {
                        catalog.put(PdfName.Pages, pageRoot);
                        catalog.getPdfObject().flush(false);
                    }
                    flushFonts();

                    if (writer.crypto != null) {
                        assert reader.decrypt.getPdfObject()
                                == writer.crypto.getPdfObject() : "Conflict with source encryption";
                        crypto = reader.decrypt.getPdfObject();
                        if (crypto.getIndirectReference() != null) {
                            // Checking just for extra safety, encryption dictionary shall never be direct.
                            forbiddenToFlush.add(crypto.getIndirectReference());
                        }
                    }

                    writer.flushModifiedWaitingObjects(forbiddenToFlush);
                    for (int i = 0; i < xref.size(); i++) {
                        PdfIndirectReference indirectReference = xref.get(i);
                        if (indirectReference != null && !indirectReference.isFree() && indirectReference.checkState(
                                PdfObject.MODIFIED) && !indirectReference.checkState(PdfObject.FLUSHED)
                                && !forbiddenToFlush.contains(indirectReference)) {
                            indirectReference.setFree();
                        }
                    }
                } else {
                    if (catalog.isOCPropertiesMayHaveChanged()) {
                        catalog.getOCProperties(false).flush();
                    }
                    if (catalog.pageLabels != null) {
                        catalog.put(PdfName.PageLabels, catalog.pageLabels.buildTree());
                    }

                    catalog.getPdfObject().put(PdfName.Pages, catalog.getPageTree().generateTree());

                    for (Map.Entry<PdfName, PdfNameTree> entry : catalog.nameTrees.entrySet()) {
                        PdfNameTree tree = entry.getValue();
                        if (tree.isModified()) {
                            ensureTreeRootAddedToNames(tree.buildTree().makeIndirect(this), entry.getKey());
                        }
                    }

                    for (int pageNum = 1; pageNum <= getNumberOfPages(); pageNum++) {
                        PdfPage page = getPage(pageNum);
                        if (page != null) {
                            page.flush();
                        }
                    }
                    if (structTreeRoot != null) {
                        tryFlushTagStructure(false);
                    }
                    flushInfoDictionary(properties.appendMode);
                    catalog.getPdfObject().flush(false);
                    flushFonts();

                    if (writer.crypto != null) {
                        crypto = writer.crypto.getPdfObject();
                        crypto.makeIndirect(this);
                        forbiddenToFlush.add(crypto.getIndirectReference());
                    }

                    writer.flushWaitingObjects(forbiddenToFlush);
                    for (int i = 0; i < xref.size(); i++) {
                        PdfIndirectReference indirectReference = xref.get(i);
                        if (indirectReference != null && !indirectReference.isFree() && !indirectReference.checkState(
                                PdfObject.FLUSHED) && !forbiddenToFlush.contains(indirectReference)) {
                            PdfObject object;
                            if (isFlushUnusedObjects() && !indirectReference.checkState(
                                    PdfObject.ORIGINAL_OBJECT_STREAM)
                                    && (object = indirectReference.getRefersTo(false)) != null) {
                                object.flush();
                            } else {
                                indirectReference.setFree();
                            }
                        }
                    }
                }

                // To avoid encryption of XrefStream and Encryption dictionary remove crypto.
                // NOTE. No need in reverting, because it is the last operation with the document.
                writer.crypto = null;
                checkIsoConformance(crypto, IsoKey.CRYPTO);

                if (!properties.appendMode && crypto != null) {
                    // no need to flush crypto in append mode, it shall not have changed in this case
                    crypto.flush(false);
                }

                // The following operator prevents the possible inconsistency between root and info
                // entries existing in the trailer object and corresponding fields. This inconsistency
                // may appear when user gets trailer and explicitly sets new root or info dictionaries.
                trailer.put(PdfName.Root, catalog.getPdfObject());

                //By this time original and modified document ids should always be not null due to initializing in
                // either writer properties, or in the writer init section on document open or from pdfreader. So we
                // shouldn't worry about it being null next
                PdfObject fileId = PdfEncryption.createInfoId(ByteUtils.getIsoBytes(originalDocumentId.getValue()),
                        ByteUtils.getIsoBytes(modifiedDocumentId.getValue()), this.properties.preserveEncryption);
                xref.writeXrefTableAndTrailer(this, fileId, crypto);
                writer.flush();
                if (writer.getOutputStream() instanceof CountOutputStream) {
                    long amountOfBytes = ((CountOutputStream) writer.getOutputStream()).getAmountOfWrittenBytes();
                    manager.onEvent(new SizeOfPdfStatisticsEvent(amountOfBytes, ITextCoreProductData.getInstance()));
                }
            }
            catalog.getPageTree().clearPageRefs();
            removeAllHandlers();
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_CLOSE_DOCUMENT, e, this);
        } finally {
            if (writer != null && isCloseWriter()) {
                try {
                    writer.close();
                } catch (Exception e) {
                    Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                    logger.error(IoLogMessageConstant.PDF_WRITER_CLOSING_FAILED, e);
                }
            }

            if (reader != null && isCloseReader()) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                    logger.error(IoLogMessageConstant.PDF_READER_CLOSING_FAILED, e);
                }
            }

        }
        closed = true;
    }

    /**
     * Gets close status of the document.
     *
     * @return true, if the document has already been closed, otherwise false.
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Gets tagged status of the document.
     *
     * @return true, if the document has tag structure, otherwise false.
     */
    public boolean isTagged() {
        return structTreeRoot != null;
    }

    /**
     * Specifies that document shall contain tag structure.
     * See ISO 32000-1, section 14.8 "Tagged PDF"
     *
     * @return this {@link PdfDocument} instance
     */
    public PdfDocument setTagged() {
        checkClosingStatus();
        if (structTreeRoot == null) {
            structTreeRoot = new PdfStructTreeRoot(this);
            catalog.getPdfObject().put(PdfName.StructTreeRoot, structTreeRoot.getPdfObject());
            updateValueInMarkInfoDict(PdfName.Marked, PdfBoolean.TRUE);

            structParentIndex = 0;
        }
        return this;
    }

    /**
     * Gets {@link PdfStructTreeRoot} of tagged document.
     *
     * @return {@link PdfStructTreeRoot} in case document is tagged, otherwise it returns null.
     *
     * @see #isTagged()
     * @see #getNextStructParentIndex()
     */
    public PdfStructTreeRoot getStructTreeRoot() {
        return structTreeRoot;
    }

    /**
     * Gets next parent index of tagged document.
     *
     * @return -1 if document is not tagged, or &gt;= 0 if tagged.
     *
     * @see #isTagged()
     * @see #getNextStructParentIndex()
     */
    public int getNextStructParentIndex() {
        return structParentIndex < 0 ? -1 : structParentIndex++;
    }

    /**
     * Gets document {@code TagStructureContext}.
     * The document must be tagged, otherwise an exception will be thrown.
     *
     * @return document {@code TagStructureContext}.
     */
    public TagStructureContext getTagStructureContext() {
        checkClosingStatus();
        if (tagStructureContext == null) {
            if (!isTagged()) {
                throw new PdfException(KernelExceptionMessageConstant.MUST_BE_A_TAGGED_DOCUMENT);
            }

            initTagStructureContext();
        }

        return tagStructureContext;
    }

    /**
     * Copies a range of pages from current document to {@code toDocument}.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     * <p>
     * If outlines destination names are the same in different documents, all
     * such outlines will lead to a single location in the resultant document.
     * In this case iText will log a warning. This can be avoided by renaming
     * destinations names in the source document.
     *
     * @param pageFrom         start of the range of pages to be copied.
     * @param pageTo           end of the range of pages to be copied.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     *
     * @return list of copied pages
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument, int insertBeforePage) {
        return copyPagesTo(pageFrom, pageTo, toDocument, insertBeforePage, null);
    }


    /**
     * Get the {@link IConformanceLevel}
     *
     * @return the {@link IConformanceLevel}  will be null if the document does not have a conformance level specified
     */
    public IConformanceLevel getConformanceLevel() {
        return null;
    }

    /**
     * Copies a range of pages from current document to {@code toDocument}. This range is inclusive, both {@code page}
     * and {@code pageTo} are included in list of copied pages.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     * <p>
     * If outlines destination names are the same in different documents, all
     * such outlines will lead to a single location in the resultant document.
     * In this case iText will log a warning. This can be avoided by renaming
     * destinations names in the source document.
     *
     * @param pageFrom         1-based start of the range of pages to be copied.
     * @param pageTo           1-based end (inclusive) of the range of pages to be copied. This page is included in list
     *                         of copied pages.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     * @param copier           a copier which bears a special copy logic. May be null.
     *                         It is recommended to use the same instance of {@link IPdfPageExtraCopier}
     *                         for the same output document.
     *
     * @return list of new copied pages
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument, int insertBeforePage,
            IPdfPageExtraCopier copier) {
        List<Integer> pages = new ArrayList<>();
        for (int i = pageFrom; i <= pageTo; i++) {
            pages.add(i);
        }
        return copyPagesTo(pages, toDocument, insertBeforePage, copier);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end. This range
     * is inclusive, both {@code page} and {@code pageTo} are included in list of copied pages.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     * <p>
     * If outlines destination names are the same in different documents, all
     * such outlines will lead to a single location in the resultant document.
     * In this case iText will log a warning. This can be avoided by renaming
     * destinations names in the source document.
     *
     * @param pageFrom   1-based start of the range of pages to be copied.
     * @param pageTo     1-based end (inclusive) of the range of pages to be copied. This page is included in list of
     *                   copied pages.
     * @param toDocument a document to copy pages to.
     *
     * @return list of new copied pages
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument) {
        return copyPagesTo(pageFrom, pageTo, toDocument, null);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end. This range
     * is inclusive, both {@code page} and {@code pageTo} are included in list of copied pages.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     * <p>
     * If outlines destination names are the same in different documents, all
     * such outlines will lead to a single location in the resultant document.
     * In this case iText will log a warning. This can be avoided by renaming
     * destinations names in the source document.
     *
     * @param pageFrom   1-based start of the range of pages to be copied.
     * @param pageTo     1-based end (inclusive) of the range of pages to be copied. This page is included in list of
     *                   copied pages.
     * @param toDocument a document to copy pages to.
     * @param copier     a copier which bears a special copy logic. May be null.
     *                   It is recommended to use the same instance of {@link IPdfPageExtraCopier}
     *                   for the same output document.
     *
     * @return list of new copied pages.
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument, IPdfPageExtraCopier copier) {
        return copyPagesTo(pageFrom, pageTo, toDocument, toDocument.getNumberOfPages() + 1, copier);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument}.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     * <p>
     * If outlines destination names are the same in different documents, all
     * such outlines will lead to a single location in the resultant document.
     * In this case iText will log a warning. This can be avoided by renaming
     * destinations names in the source document.
     *
     * @param pagesToCopy      list of pages to be copied.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     *
     * @return list of new copied pages
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument, int insertBeforePage) {
        return copyPagesTo(pagesToCopy, toDocument, insertBeforePage, null);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument}.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     * <p>
     * If outlines destination names are the same in different documents, all
     * such outlines will lead to a single location in the resultant document.
     * In this case iText will log a warning. This can be avoided by renaming
     * destinations names in the source document.
     *
     * @param pagesToCopy      list of pages to be copied.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     * @param copier           a copier which bears a special copy logic. May be null.
     *                         It is recommended to use the same instance of {@link IPdfPageExtraCopier}
     *                         for the same output document.
     *
     * @return list of new copied pages
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument, int insertBeforePage,
            IPdfPageExtraCopier copier) {
        if (pagesToCopy.isEmpty()) {
            return Collections.<PdfPage>emptyList();
        }
        pendingDestinationMutations.clear();
        checkClosingStatus();
        List<PdfPage> copiedPages = new ArrayList<>();
        Map<PdfPage, PdfPage> page2page = new LinkedHashMap<>();
        Set<PdfOutline> outlinesToCopy = new HashSet<>();

        List<Map<PdfPage, PdfPage>> rangesOfPagesWithIncreasingNumbers = new ArrayList<>();
        int lastCopiedPageNum = (int) pagesToCopy.get(0);

        int pageInsertIndex = insertBeforePage;
        boolean insertInBetween = insertBeforePage < toDocument.getNumberOfPages() + 1;

        for (Integer pageNum : pagesToCopy) {
            PdfPage page = getPage((int) pageNum);
            PdfPage newPage = page.copyTo(toDocument, copier, true,
                    insertInBetween ? pageInsertIndex : -1);
            copiedPages.add(newPage);
            page2page.put(page, newPage);

            if (lastCopiedPageNum >= pageNum) {
                rangesOfPagesWithIncreasingNumbers.add(new HashMap<PdfPage, PdfPage>());
            }
            int lastRangeInd = rangesOfPagesWithIncreasingNumbers.size() - 1;
            rangesOfPagesWithIncreasingNumbers.get(lastRangeInd).put(page, newPage);

            pageInsertIndex++;
            if (toDocument.hasOutlines()) {
                List<PdfOutline> pageOutlines = page.getOutlines(false);
                if (pageOutlines != null) {
                    outlinesToCopy.addAll(pageOutlines);
                }
            }
            lastCopiedPageNum = (int) pageNum;
        }

        resolveDestinations(toDocument, page2page);

        // Copying OCGs should go after copying LinkAnnotations
        if (getCatalog() != null && getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties) != null) {
            OcgPropertiesCopier.copyOCGProperties(this, toDocument, page2page);
            if(toDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.OCProperties) != null){
                toDocument.getCatalog().setOcgCopied(true);
            }
        }

        // It's important to copy tag structure after link annotations were copied, because object content items in tag
        // structure are not copied in case if their's OBJ key is annotation and doesn't contain /P entry.
        if (toDocument.isTagged()) {
            if (isTagged()) {
                try {
                    for (Map<PdfPage, PdfPage> increasingPagesRange : rangesOfPagesWithIncreasingNumbers) {
                        if (insertInBetween) {
                            getStructTreeRoot().copyTo(toDocument, insertBeforePage, increasingPagesRange);
                        } else {
                            getStructTreeRoot().copyTo(toDocument, increasingPagesRange);
                        }
                        insertBeforePage += increasingPagesRange.size();
                    }
                    toDocument.getTagStructureContext().normalizeDocumentRootTag();
                } catch (Exception e) {
                    throw new PdfException(
                            KernelExceptionMessageConstant.
                                    TAG_STRUCTURE_COPYING_FAILED_IT_MIGHT_BE_CORRUPTED_IN_ONE_OF_THE_DOCUMENTS,
                            e);
                }
                if (copier instanceof IPdfPageFormCopier) {
                    ((IPdfPageFormCopier) copier).recreateAcroformToProcessCopiedFields(toDocument);
                }
            } else {
                Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                logger.warn(IoLogMessageConstant.NOT_TAGGED_PAGES_IN_TAGGED_DOCUMENT);
            }
        }
        if (catalog.isOutlineMode()) {
            copyOutlines(outlinesToCopy, toDocument, page2page);
        }
        return copiedPages;
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     * <p>
     * If outlines destination names are the same in different documents, all
     * such outlines will lead to a single location in the resultant document.
     * In this case iText will log a warning. This can be avoided by renaming
     * destinations names in the source document.
     *
     * @param pagesToCopy list of pages to be copied.
     * @param toDocument  a document to copy pages to.
     *
     * @return list of copied pages
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument) {
        return copyPagesTo(pagesToCopy, toDocument, null);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     * <p>
     * If outlines destination names are the same in different documents, all
     * such outlines will lead to a single location in the resultant document.
     * In this case iText will log a warning. This can be avoided by renaming
     * destinations names in the source document.
     *
     * @param pagesToCopy list of pages to be copied.
     * @param toDocument  a document to copy pages to.
     * @param copier      a copier which bears a special copy logic. May be null.
     *                    It is recommended to use the same instance of {@link IPdfPageExtraCopier}
     *                    for the same output document.
     *
     * @return list of copied pages
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument, IPdfPageExtraCopier copier) {
        return copyPagesTo(pagesToCopy, toDocument, toDocument.getNumberOfPages() + 1, copier);
    }

    /**
     * Flush all copied objects and remove them from copied cache.
     * <p>
     * Note, if you will copy objects from the same document, duplicated objects will be created.
     * That's why usually this method is meant to be used when all copying from source document is finished.
     * For other cases one can also consider other flushing mechanisms, e.g. pages-based flushing.
     *
     * @param sourceDoc source document
     */
    public void flushCopiedObjects(PdfDocument sourceDoc) {
        if (getWriter() != null) {
            getWriter().flushCopiedObjects(sourceDoc.getDocumentId());
        }
    }

    /**
     * Checks, whether {@link #close()} method will close associated PdfReader.
     *
     * @return true, {@link #close()} method is going to close associated PdfReader, otherwise false.
     */
    public boolean isCloseReader() {
        return closeReader;
    }

    /**
     * Sets, whether {@link #close()} method shall close associated PdfReader.
     *
     * @param closeReader true, {@link #close()} method shall close associated PdfReader, otherwise false.
     */
    public void setCloseReader(boolean closeReader) {
        checkClosingStatus();
        this.closeReader = closeReader;
    }

    /**
     * Checks, whether {@link #close()} method will close associated PdfWriter.
     *
     * @return true, {@link #close()} method is going to close associated PdfWriter, otherwise false.
     */
    public boolean isCloseWriter() {
        return closeWriter;
    }

    /**
     * Sets, whether {@link #close()} method shall close associated PdfWriter.
     *
     * @param closeWriter true, {@link #close()} method shall close associated PdfWriter, otherwise false.
     */
    public void setCloseWriter(boolean closeWriter) {
        checkClosingStatus();
        this.closeWriter = closeWriter;
    }

    /**
     * Checks, whether {@link #close()} will flush unused objects,
     * e.g. unreachable from PDF Catalog. By default - false.
     *
     * @return false, if {@link #close()} shall not flush unused objects, otherwise true.
     */
    public boolean isFlushUnusedObjects() {
        return flushUnusedObjects;
    }

    /**
     * Sets, whether {@link #close()} shall flush unused objects,
     * e.g. unreachable from PDF Catalog.
     *
     * @param flushUnusedObjects false, if {@link #close()} shall not flush unused objects, otherwise true.
     */
    public void setFlushUnusedObjects(boolean flushUnusedObjects) {
        checkClosingStatus();
        this.flushUnusedObjects = flushUnusedObjects;
    }

    /**
     * This method returns a complete outline tree of the whole document.
     *
     * @param updateOutlines if the flag is {@code true}, the method reads the whole document and creates outline tree.
     *                       If the flag is {@code false}, the method gets cached outline tree
     *                       (if it was cached via calling getOutlines method before).
     *
     * @return fully initialize {@link PdfOutline} object.
     */
    public PdfOutline getOutlines(boolean updateOutlines) {
        checkClosingStatus();
        return catalog.getOutlines(updateOutlines);
    }

    /**
     * This method initializes an outline tree of the document and sets outline mode to true.
     */
    public void initializeOutlines() {
        checkClosingStatus();
        getOutlines(false);
    }

    /**
     * This methods adds new name in the Dests NameTree. It throws an exception, if the name already exists.
     *
     * @param key   Name of the destination.
     * @param value An object destination refers to. Must be an array or a dictionary with key /D and array.
     *              See ISO 32000-1 12.3.2.3 for more info.
     */
    public void addNamedDestination(String key, PdfObject value) {
        addNamedDestination(new PdfString(key), value);
    }

    /**
     * This methods adds new name in the Dests NameTree. It throws an exception, if the name already exists.
     *
     * @param key   Name of the destination.
     * @param value An object destination refers to. Must be an array or a dictionary with key /D and array.
     *              See ISO 32000-1 12.3.2.3 for more info.
     */
    public void addNamedDestination(PdfString key, PdfObject value) {
        checkClosingStatus();
        if (value.isArray() && ((PdfArray) value).get(0).isNumber()) {
            LoggerFactory.getLogger(PdfDocument.class).warn(IoLogMessageConstant.INVALID_DESTINATION_TYPE);
        }
        catalog.addNamedDestination(key, value);
    }

    /**
     * Gets static copy of cross reference table.
     *
     * @return a static copy of cross reference table
     */
    public List<PdfIndirectReference> listIndirectReferences() {
        checkClosingStatus();
        List<PdfIndirectReference> indRefs = new ArrayList<>(xref.size());
        for (int i = 0; i < xref.size(); ++i) {
            PdfIndirectReference indref = xref.get(i);
            if (indref != null) {
                indRefs.add(indref);
            }
        }
        return indRefs;
    }

    /**
     * Gets document trailer.
     *
     * @return document trailer.
     */
    public PdfDictionary getTrailer() {
        checkClosingStatus();
        return trailer;
    }

    /**
     * Adds {@link PdfOutputIntent} that shall specify the colour characteristics of output devices
     * on which the document might be rendered.
     *
     * @param outputIntent {@link PdfOutputIntent} to add.
     *
     * @see PdfOutputIntent
     */
    public void addOutputIntent(PdfOutputIntent outputIntent) {
        checkClosingStatus();
        if (outputIntent == null) {
            return;
        }

        PdfArray outputIntents = catalog.getPdfObject().getAsArray(PdfName.OutputIntents);
        if (outputIntents == null) {
            outputIntents = new PdfArray();
            catalog.put(PdfName.OutputIntents, outputIntents);
        }
        outputIntents.add(outputIntent.getPdfObject());
    }

    /**
     * Checks whether PDF document conforms a specific standard.
     *
     * @param obj An object to conform.
     * @param key type of object to conform.
     */
    public void checkIsoConformance(Object obj, IsoKey key) {
        checkIsoConformance(obj, key, null, null);
    }

    /**
     * Checks whether PDF document conforms a specific standard.
     *
     * @param obj           an object to conform.
     * @param key           type of object to conform.
     * @param resources     {@link PdfResources} associated with an object to check.
     * @param contentStream current content stream
     */
    public void checkIsoConformance(Object obj, IsoKey key, PdfResources resources, PdfStream contentStream) {
        checkIsoConformance(obj, key, resources, contentStream, null);
    }

    /**
     * Checks whether PDF document conforms a specific standard.
     *
     * @param obj           an object to conform.
     * @param key           type of object to conform.
     * @param resources     {@link PdfResources} associated with an object to check.
     * @param contentStream current content stream.
     * @param extra         extra data required for the check.
     */
    public void checkIsoConformance(Object obj, IsoKey key, PdfResources resources, PdfStream contentStream,
            Object extra) {
        if (!this.getDiContainer().isRegistered(ValidationContainer.class)) {
            return;
        }
        ValidationContainer container = this.getDiContainer().getInstance(ValidationContainer.class);
        if (container == null) {
            return;
        }
        container.validate(obj, key, resources, contentStream, extra);
    }

    /**
     * Checks whether PDF document conforms a specific standard.
     * Shall be overridden.
     *
     * @param gState    a {@link CanvasGraphicsState} object to conform.
     * @param resources {@link PdfResources} associated with an object to check.
     */
    public void checkShowTextIsoConformance(CanvasGraphicsState gState, PdfResources resources) {
    }

    /**
     * Adds file attachment at document level.
     *
     * @param key name of the destination.
     * @param fs  {@link PdfFileSpec} object.
     */
    public void addFileAttachment(String key, PdfFileSpec fs) {
        checkClosingStatus();
        catalog.addNameToNameTree(new PdfString(key), fs.getPdfObject(), PdfName.EmbeddedFiles);
    }

    /**
     * Adds file associated with PDF document as a whole and identifies the relationship between them.
     * <p>
     * Associated files may be used in Pdf/A-3 and Pdf 2.0 documents.
     * The method is very similar to {@link PdfDocument#addFileAttachment(String, PdfFileSpec)}.
     * However, besides adding file description to Names tree, it adds file to array value of the AF key in the document
     * catalog.
     * <p>
     * For associated files their associated file specification dictionaries shall include the AFRelationship key
     *
     * @param description the file description
     * @param fs          file specification dictionary of associated file
     *
     * @see PdfDocument#addFileAttachment(String, PdfFileSpec)
     */
    public void addAssociatedFile(String description, PdfFileSpec fs) {
        if (null == ((PdfDictionary) fs.getPdfObject()).get(PdfName.AFRelationship)) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.error(IoLogMessageConstant.ASSOCIATED_FILE_SPEC_SHALL_INCLUDE_AFRELATIONSHIP);
        }

        PdfArray afArray = catalog.getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null) {
            afArray = (PdfArray) new PdfArray().makeIndirect(this);
            catalog.put(PdfName.AF, afArray);
        }
        afArray.add(fs.getPdfObject());

        addFileAttachment(description, fs);
    }

    /**
     * Returns files associated with PDF document.
     *
     * @return associated files array.
     */
    public PdfArray getAssociatedFiles() {
        checkClosingStatus();
        return catalog.getPdfObject().getAsArray(PdfName.AF);
    }

    /**
     * Gets the encrypted payload of this document,
     * or returns {@code null} if this document isn't an unencrypted wrapper document.
     *
     * @return encrypted payload of this document.
     */
    public PdfEncryptedPayloadDocument getEncryptedPayloadDocument() {
        if (getReader() != null && getReader().isEncrypted()) {
            return null;
        }
        PdfCollection collection = getCatalog().getCollection();
        if (collection != null && collection.isViewHidden()) {
            PdfString documentName = collection.getInitialDocument();
            PdfNameTree embeddedFiles = getCatalog().getNameTree(PdfName.EmbeddedFiles);
            PdfObject fileSpecObject = embeddedFiles.getNames().get(documentName);
            if (fileSpecObject != null && fileSpecObject.isDictionary()) {
                try {
                    PdfFileSpec fileSpec = PdfEncryptedPayloadFileSpecFactory.wrap((PdfDictionary) fileSpecObject);
                    if (fileSpec != null) {
                        final PdfDictionary embeddedDictionary = ((PdfDictionary) fileSpec.getPdfObject()).
                                getAsDictionary(
                                        PdfName.EF);
                        PdfStream stream = embeddedDictionary.getAsStream(PdfName.UF);
                        if (stream == null) {
                            stream = embeddedDictionary.getAsStream(PdfName.F);
                        }
                        if (stream != null) {
                            String documentNameUnicode = documentName.toUnicodeString();
                            return new PdfEncryptedPayloadDocument(stream, fileSpec, documentNameUnicode);
                        }
                    }
                } catch (PdfException e) {
                    LoggerFactory.getLogger(getClass()).error(e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Sets an encrypted payload, making this document an unencrypted wrapper document.
     * The file spec shall include the AFRelationship key with a value of EncryptedPayload,
     * and shall include an encrypted payload dictionary.
     *
     * @param fs encrypted payload file spec. {@link PdfEncryptedPayloadFileSpecFactory} can produce one.
     */
    public void setEncryptedPayload(PdfFileSpec fs) {
        if (getWriter() == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.CANNOT_SET_ENCRYPTED_PAYLOAD_TO_DOCUMENT_OPENED_IN_READING_MODE);
        }
        if (writerHasEncryption()) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_SET_ENCRYPTED_PAYLOAD_TO_ENCRYPTED_DOCUMENT);
        }
        if (!PdfName.EncryptedPayload.equals(((PdfDictionary) fs.getPdfObject()).get(PdfName.AFRelationship))) {
            LoggerFactory.getLogger(getClass())
                    .error(IoLogMessageConstant.ENCRYPTED_PAYLOAD_FILE_SPEC_SHALL_HAVE_AFRELATIONSHIP_FILED_EQUAL_TO_ENCRYPTED_PAYLOAD);
        }
        PdfEncryptedPayload encryptedPayload = PdfEncryptedPayload.extractFrom(fs);
        if (encryptedPayload == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.ENCRYPTED_PAYLOAD_FILE_SPEC_DOES_NOT_HAVE_ENCRYPTED_PAYLOAD_DICTIONARY);
        }
        PdfCollection collection = getCatalog().getCollection();
        if (collection != null) {
            LoggerFactory.getLogger(getClass())
                    .warn(IoLogMessageConstant.COLLECTION_DICTIONARY_ALREADY_EXISTS_IT_WILL_BE_MODIFIED);
        } else {
            collection = new PdfCollection();
            getCatalog().setCollection(collection);
        }
        collection.setView(PdfCollection.HIDDEN);
        String displayName = PdfEncryptedPayloadFileSpecFactory.generateFileDisplay(encryptedPayload);
        collection.setInitialDocument(displayName);
        addAssociatedFile(displayName, fs);
    }

    /**
     * This method retrieves the page labels from a document as an array of String objects.
     *
     * @return {@link String} list of page labels if they were found, or {@code null} otherwise
     */
    public String[] getPageLabels() {
        if (catalog.getPageLabelsTree(false) == null) {
            return null;
        }
        Map<Integer, PdfObject> pageLabels = catalog.getPageLabelsTree(false).getNumbers();
        if (pageLabels.size() == 0) {
            return null;
        }
        String[] labelStrings = new String[getNumberOfPages()];
        int pageCount = 1;
        String prefix = "";
        String type = "D";
        for (int i = 0; i < getNumberOfPages(); i++) {
            if (pageLabels.containsKey(i)) {
                PdfDictionary labelDictionary = (PdfDictionary) pageLabels.get(i);
                PdfNumber pageRange = labelDictionary.getAsNumber(PdfName.St);
                if (pageRange != null) {
                    pageCount = pageRange.intValue();
                } else {
                    pageCount = 1;
                }
                PdfString p = labelDictionary.getAsString(PdfName.P);
                if (p != null) {
                    prefix = p.toUnicodeString();
                } else {
                    prefix = "";
                }
                PdfName t = labelDictionary.getAsName(PdfName.S);
                if (t != null) {
                    type = t.getValue();
                } else {
                    type = "e";
                }
            }
            switch (type) {
                case "R":
                    labelStrings[i] = prefix + RomanNumbering.toRomanUpperCase(pageCount);
                    break;
                case "r":
                    labelStrings[i] = prefix + RomanNumbering.toRomanLowerCase(pageCount);
                    break;
                case "A":
                    labelStrings[i] = prefix + EnglishAlphabetNumbering.toLatinAlphabetNumberUpperCase(pageCount);
                    break;
                case "a":
                    labelStrings[i] = prefix + EnglishAlphabetNumbering.toLatinAlphabetNumberLowerCase(pageCount);
                    break;
                case "e":
                    labelStrings[i] = prefix;
                    break;
                default:
                    labelStrings[i] = prefix + pageCount;
                    break;
            }
            pageCount++;
        }
        return labelStrings;
    }

    /**
     * Indicates if the document has any outlines
     *
     * @return {@code true}, if there are outlines and {@code false} otherwise.
     */
    public boolean hasOutlines() {
        return catalog.hasOutlines();
    }

    /**
     * Sets the flag indicating the presence of structure elements that contain user properties attributes.
     *
     * @param userProperties the user properties flag
     */
    public void setUserProperties(boolean userProperties) {
        PdfBoolean userPropsVal = userProperties ? PdfBoolean.TRUE : PdfBoolean.FALSE;
        updateValueInMarkInfoDict(PdfName.UserProperties, userPropsVal);
    }

    /**
     * Create a new instance of {@link PdfFont} or load already created one.
     *
     * @param dictionary {@link PdfDictionary} that presents {@link PdfFont}.
     *
     * @return instance of {@link PdfFont}
     * <p>
     * Note, PdfFont which created with {@link PdfFontFactory#createFont(PdfDictionary)} won't be cached
     * until it will be added to {@link com.itextpdf.kernel.pdf.canvas.PdfCanvas} or {@link PdfResources}.
     */
    public PdfFont getFont(PdfDictionary dictionary) {
        PdfIndirectReference indirectReference = dictionary.getIndirectReference();
        if (indirectReference != null && documentFonts.containsKey(indirectReference)) {
            return documentFonts.get(indirectReference);
        } else {
            return addFont(PdfFontFactory.createFont(dictionary));
        }
    }

    /**
     * Gets default font for the document: Helvetica, WinAnsi.
     * One instance per document.
     *
     * @return instance of {@link PdfFont} or {@code null} on error.
     */
    public PdfFont getDefaultFont() {
        if (defaultFont == null) {
            try {
                defaultFont = PdfFontFactory.createFont();
                if (writer != null) {
                    defaultFont.makeIndirect(this);
                }
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                logger.error(IoLogMessageConstant.EXCEPTION_WHILE_CREATING_DEFAULT_FONT, e);
                defaultFont = null;
            }
        }
        return defaultFont;
    }

    /**
     * Adds a {@link PdfFont} instance to this document so that this font is flushed automatically
     * on document close. As a side effect, the underlying font dictionary is made indirect if it wasn't the case yet
     *
     * @param font a {@link PdfFont} instance to add
     *
     * @return the same PdfFont instance.
     */
    public PdfFont addFont(PdfFont font) {
        font.makeIndirect(this);
        // forbid release for font dictionaries that are stored in #documentFonts collection
        font.setForbidRelease();
        documentFonts.put(font.getPdfObject().getIndirectReference(), font);
        return font;
    }

    /**
     * Registers a product for debugging purposes.
     *
     * @param productData product to be registered.
     *
     * @return true if the product hadn't been registered before.
     */
    public boolean registerProduct(final ProductData productData) {
        return this.fingerPrint.registerProduct(productData);
    }

    /**
     * Returns the object containing the registered products.
     *
     * @return fingerprint object
     */
    public FingerPrint getFingerPrint() {
        return fingerPrint;
    }

    /**
     * Find {@link PdfFont} from loaded fonts with corresponding fontProgram and encoding or CMAP.
     *
     * @param fontProgram a font name or path to a font program
     * @param encoding    an encoding or CMAP
     *
     * @return the font instance, or null if font wasn't found
     */
    public PdfFont findFont(String fontProgram, String encoding) {
        for (PdfFont font : documentFonts.values()) {
            if (!font.isFlushed() && font.isBuiltWith(fontProgram, encoding)) {
                return font;
            }
        }
        return null;
    }

    /**
     * Obtains numeric document id.
     *
     * @return document id
     */
    public long getDocumentId() {
        return documentId.getId();
    }

    /**
     * Obtains document id as a {@link SequenceId}.
     *
     * @return document id
     */
    public SequenceId getDocumentIdWrapper() {
        return documentId;
    }

    /**
     * Gets a persistent XMP metadata serialization options.
     *
     * @return serialize options
     */
    public SerializeOptions getSerializeOptions() {
        return this.serializeOptions;
    }

    /**
     * Sets a persistent XMP metadata serialization options.
     *
     * @param serializeOptions serialize options
     */
    public void setSerializeOptions(SerializeOptions serializeOptions) {
        this.serializeOptions = serializeOptions;
    }

    /**
     * Initialize {@link TagStructureContext}.
     */
    protected void initTagStructureContext() {
        tagStructureContext = new TagStructureContext(this);
    }

    /**
     * Save destinations in a temporary storage for further copying.
     *
     * @param destination        the {@link PdfDestination} to be updated itself.
     * @param onPageAvailable    a destination consumer that will handle the copying when the
     *                           destination still resolves, it gets the new destination as input
     * @param onPageNotAvailable a destination consumer that will handle the copying when the
     *                           destination is not available, it gets the original destination
     *                           as input
     */
    protected void storeDestinationToReaddress(PdfDestination destination,
            Consumer<PdfDestination> onPageAvailable, Consumer<PdfDestination> onPageNotAvailable) {
        pendingDestinationMutations.add(new DestinationMutationInfo(destination, onPageAvailable, onPageNotAvailable));
    }

    /**
     * Checks whether PDF document conforms to a specific standard.
     */
    protected void checkIsoConformance() {
        if (!this.getDiContainer().isRegistered(ValidationContainer.class)) {
            return;
        }
        ValidationContainer container = this.getDiContainer().getInstance(ValidationContainer.class);
        if (container == null) {
            return;
        }
        ValidationContext context = new ValidationContext()
                .withPdfDocument(this)
                .withFonts(getDocumentFonts());
        container.validate(context);
    }

    /**
     * Mark an object with {@link PdfObject#MUST_BE_FLUSHED}.
     *
     * @param pdfObject an object to mark.
     */
    protected void markObjectAsMustBeFlushed(PdfObject pdfObject) {
        if (pdfObject.getIndirectReference() != null) {
            pdfObject.getIndirectReference().setState(PdfObject.MUST_BE_FLUSHED);
        }
    }

    /**
     * Flush an object.
     *
     * @param pdfObject     object to flush.
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     *
     * @throws IOException on error.
     */
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        writer.flushObject(pdfObject, canBeInObjStm);
    }

    /**
     * Initializes document.
     *
     * @param newPdfVersion new pdf version of the resultant file if stamper is used and the version needs to be
     *                      changed,
     *                      or {@code null} otherwise
     */
    protected void open(PdfVersion newPdfVersion) {
        this.fingerPrint = new FingerPrint();
        this.encryptedEmbeddedStreamsHandler = new EncryptedEmbeddedStreamsHandler(this);

        try {
            final ITextCoreProductEvent event = ITextCoreProductEvent.createProcessPdfEvent(this.getDocumentIdWrapper(),
                    properties.metaInfo,
                    writer == null ? EventConfirmationType.ON_DEMAND : EventConfirmationType.ON_CLOSE);
            EventManager.getInstance().onEvent(event);
            boolean embeddedStreamsSavedOnReading = false;
            if (reader != null) {
                if (reader.pdfDocument != null) {
                    throw new PdfException(KernelExceptionMessageConstant.PDF_READER_HAS_BEEN_ALREADY_UTILIZED);
                }
                reader.pdfDocument = this;
                memoryLimitsAwareHandler = reader.properties.memoryLimitsAwareHandler;
                if (null == memoryLimitsAwareHandler) {
                    memoryLimitsAwareHandler = new MemoryLimitsAwareHandler(reader.tokens.getSafeFile().length());
                }
                xref.setMemoryLimitsAwareHandler(memoryLimitsAwareHandler);
                reader.readPdf();
                if (reader.decrypt != null && reader.decrypt.isEmbeddedFilesOnly()) {
                    encryptedEmbeddedStreamsHandler.storeAllEmbeddedStreams();
                    embeddedStreamsSavedOnReading = true;
                }
                pdfVersion = reader.headerPdfVersion;
                trailer = new PdfDictionary(reader.trailer);

                readDocumentIds();

                PdfDictionary catalogDictionary = (PdfDictionary) trailer.get(PdfName.Root, true);
                if (null == catalogDictionary) {
                    throw new PdfException(KernelExceptionMessageConstant.CORRUPTED_ROOT_ENTRY_IN_TRAILER);
                }
                catalog = new PdfCatalog(catalogDictionary);
                updatePdfVersionFromCatalog();
                PdfStream xmpMetadataStream = catalog.getPdfObject().getAsStream(PdfName.Metadata);
                if (xmpMetadataStream != null) {
                    xmpMetadata = xmpMetadataStream.getBytes();
                    if (!this.getClass().equals(PdfDocument.class)) {
                        // TODO DEVSIX-5292 If somebody extends PdfDocument we have to initialize document info
                        //  and conformance level to provide compatibility. This code block shall be removed
                        reader.getPdfAConformanceLevel();
                        getDocumentInfo();
                    }
                }

                PdfDictionary str = catalog.getPdfObject().getAsDictionary(PdfName.StructTreeRoot);
                if (str != null) {
                    tryInitTagStructure(str);
                }
                if (properties.appendMode && (reader.hasRebuiltXref() || reader.hasFixedXref())) {
                    throw new PdfException(
                            KernelExceptionMessageConstant.APPEND_MODE_REQUIRES_A_DOCUMENT_WITHOUT_ERRORS_EVEN_IF_RECOVERY_IS_POSSIBLE);
                }
            }
            xref.initFreeReferencesList(this);
            if (writer != null) {
                if (reader != null && reader.hasXrefStm() && writer.properties.isFullCompression == null) {
                    writer.properties.isFullCompression = Boolean.TRUE;
                }
                if (reader != null && !reader.isOpenedWithFullPermission()) {
                    throw new BadPasswordException(BadPasswordException.PdfReaderNotOpenedWithOwnerPassword);
                }
                if (reader != null && properties.preserveEncryption) {
                    writer.crypto = reader.decrypt;
                }
                writer.document = this;
                if (reader == null) {
                    catalog = new PdfCatalog(this);
                    info = new PdfDocumentInfo(this).addCreationDate();
                }
                getDocumentInfo().addModDate();

                if (trailer == null ) {
                    trailer = new PdfDictionary();
                }
                // We keep the original trailer of the document to preserve the original document keys,
                // but we have to remove all standard keys that can occur in the trailer to avoid invalid pdfs
                if (trailer.size() > 0) {
                    for (final PdfName key : PdfDocument.PDF_NAMES_TO_REMOVE_FROM_ORIGINAL_TRAILER) {
                        trailer.remove(key);
                    }
                }

                trailer.put(PdfName.Root, catalog.getPdfObject().getIndirectReference());

                if (reader != null) {
                    // If the reader's trailer contains an ID entry, let's copy it over to the new trailer
                    if (reader.trailer.containsKey(PdfName.ID)) {
                        trailer.put(PdfName.ID, reader.trailer.get(PdfName.ID));
                    }
                }

                if (writer.properties != null) {
                    PdfString readerModifiedId = modifiedDocumentId;
                    if (writer.properties.initialDocumentId != null && !(reader != null && reader.decrypt != null && (
                            properties.appendMode || properties.preserveEncryption))) {
                        originalDocumentId = writer.properties.initialDocumentId;
                    }
                    if (writer.properties.modifiedDocumentId != null) {
                        modifiedDocumentId = writer.properties.modifiedDocumentId;
                    }
                    if (originalDocumentId == null && modifiedDocumentId != null) {
                        originalDocumentId = modifiedDocumentId;
                    }
                    if (modifiedDocumentId == null) {
                        if (originalDocumentId == null) {
                            originalDocumentId = new PdfString(PdfEncryption.generateNewDocumentId());
                        }
                        modifiedDocumentId = originalDocumentId;
                    }
                    if (writer.properties.modifiedDocumentId == null && modifiedDocumentId.equals(readerModifiedId)) {
                        modifiedDocumentId = new PdfString(PdfEncryption.generateNewDocumentId());
                    }
                }

                assert originalDocumentId != null;
                assert modifiedDocumentId != null;
            }
            if (properties.appendMode) {
                // Due to constructor reader and writer not null.
                assert reader != null;
                RandomAccessFileOrArray file = reader.tokens.getSafeFile();
                int n;
                byte[] buffer = new byte[8192];
                while ((n = file.read(buffer)) > 0) {
                    writer.write(buffer, 0, n);
                }
                file.close();
                writer.write((byte) '\n');

                overrideFullCompressionInWriterProperties(writer.properties, reader.hasXrefStm());

                writer.crypto = reader.decrypt;

                if (newPdfVersion != null) {
                    // In PDF 1.4, a PDF version can also be specified in the Version entry of the document catalog,
                    // essentially updating the version associated with the file by overriding the one specified in
                    // the file header
                    if (pdfVersion.compareTo(PdfVersion.PDF_1_4) >= 0) {
                        // If the header specifies a later version, or if this entry is absent, the document conforms
                        // to the
                        // version specified in the header.

                        // So only update the version if it is older than the one in the header
                        if (newPdfVersion.compareTo(reader.headerPdfVersion) > 0) {
                            catalog.put(PdfName.Version, newPdfVersion.toPdfName());
                            catalog.setModified();
                            pdfVersion = newPdfVersion;
                        }
                    } else {
                        // Formally we cannot update version in the catalog as it is not supported for the
                        // PDF version of the original document
                    }
                }
            } else if (writer != null) {
                if (newPdfVersion != null) {
                    pdfVersion = newPdfVersion;
                }
                writer.writeHeader();

                if (writer.crypto == null) {
                    writer.initCryptoIfSpecified(pdfVersion);
                }
                if (writer.crypto != null) {
                    if (!embeddedStreamsSavedOnReading && writer.crypto.isEmbeddedFilesOnly()) {
                        encryptedEmbeddedStreamsHandler.storeAllEmbeddedStreams();
                    }
                    writer.crypto.checkEncryptionRequirements(this);
                    writer.crypto.configureEncryptionParameters(this);
                }
            }
            if (EventConfirmationType.ON_DEMAND == event.getConfirmationType()) {
                // Event confirmation: opening has passed successfully
                EventManager.getInstance().onEvent(new ConfirmEvent(event));
            }
        } catch (IOException e) {
            throw new PdfException(KernelExceptionMessageConstant.CANNOT_OPEN_DOCUMENT, e, this);
        }
    }

    /**
     * Adds custom XMP metadata extension. Useful for PDF/UA, ZUGFeRD, etc.
     *
     * @param xmpMeta {@link XMPMeta} to add custom metadata to.
     */
    protected void addCustomMetadataExtensions(XMPMeta xmpMeta) {
    }

    /**
     * Flush info dictionary if needed.
     *
     * @param appendMode <code>true</code> if the document is edited in append mode.
     */
    protected void flushInfoDictionary(boolean appendMode) {
        PdfObject infoDictObj = getDocumentInfo().getPdfObject();
        if (!appendMode || infoDictObj.isModified()) {
            infoDictObj.flush(false);
        }

        // The following operator prevents the possible inconsistency between root and info
        // entries existing in the trailer object and corresponding fields. This inconsistency
        // may appear when user gets trailer and explicitly sets new root or info dictionaries.
        trailer.put(PdfName.Info, infoDictObj);
    }

    /**
     * Updates XMP metadata.
     * Shall be overridden.
     */
    protected void updateXmpMetadata() {
        try {
            // We add PDF producer info in any case, and the valid way to do it for PDF 2.0 in only in metadata, not
            // in the info dictionary.
            if (xmpMetadata != null || writer.properties.addXmpMetadata
                    || pdfVersion.compareTo(PdfVersion.PDF_2_0) >= 0) {
                setXmpMetadata(updateDefaultXmpMetadata());
            }
        } catch (XMPException e) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.error(IoLogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, e);
        }
    }

    /**
     * Update XMP metadata values from {@link PdfDocumentInfo}.
     *
     * @return the XMPMetadata
     *
     * @throws XMPException if the file is not well-formed XML or if parsing fails.
     */
    protected XMPMeta updateDefaultXmpMetadata() throws XMPException {
        XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(getXmpMetadata(true));
        XmpMetaInfoConverter.appendDocumentInfoToMetadata(getDocumentInfo(), xmpMeta);

        if (isTagged() && writer.properties.addUAXmpMetadata && !isXmpMetaHasProperty(xmpMeta, XMPConst.NS_PDFUA_ID,
                XMPConst.PART)) {
            xmpMeta.setPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART, 1,
                    new PropertyOptions(PropertyOptions.SEPARATE_NODE));
        }

        return xmpMeta;
    }

    /**
     * List all newly added or loaded fonts
     *
     * @return List of {@link PdfFont}.
     */
    protected Collection<PdfFont> getDocumentFonts() {
        return documentFonts.values();
    }

    /**
     * Flushes all newly added or loaded fonts.
     */
    protected void flushFonts() {
        if (properties.appendMode) {
            for (PdfFont font : getDocumentFonts()) {
                if (font.getPdfObject().checkState(PdfObject.MUST_BE_INDIRECT) || font.getPdfObject()
                        .getIndirectReference().checkState(PdfObject.MODIFIED)) {
                    font.flush();
                }
            }
        } else {
            for (PdfFont font : getDocumentFonts()) {
                font.flush();
            }
        }
    }

    /**
     * Checks page before adding and add.
     *
     * @param index one-base index of the page.
     * @param page  {@link PdfPage} to add.
     */
    protected void checkAndAddPage(int index, PdfPage page) {
        if (page.isFlushed()) {
            throw new PdfException(KernelExceptionMessageConstant.FLUSHED_PAGE_CANNOT_BE_ADDED_OR_INSERTED, page);
        }
        if (page.getDocument() != null && this != page.getDocument()) {
            throw new PdfException(
                    KernelExceptionMessageConstant.
                            PAGE_CANNOT_BE_ADDED_TO_DOCUMENT_BECAUSE_IT_BELONGS_TO_ANOTHER_DOCUMENT).setMessageParams(
                    page.getDocument(), page.getDocument().getPageNumber(page), this);
        }
        catalog.getPageTree().addPage(index, page);
    }

    /**
     * Checks page before adding.
     *
     * @param page {@link PdfPage} to add.
     */
    protected void checkAndAddPage(PdfPage page) {
        if (page.isFlushed()) {
            throw new PdfException(KernelExceptionMessageConstant.FLUSHED_PAGE_CANNOT_BE_ADDED_OR_INSERTED, page);
        }
        if (page.getDocument() != null && this != page.getDocument()) {
            throw new PdfException(
                    KernelExceptionMessageConstant.
                            PAGE_CANNOT_BE_ADDED_TO_DOCUMENT_BECAUSE_IT_BELONGS_TO_ANOTHER_DOCUMENT).setMessageParams(
                    page.getDocument(), page.getDocument().getPageNumber(page), this);
        }
        catalog.getPageTree().addPage(page);
    }

    /**
     * checks whether a method is invoked at the closed document
     */
    protected void checkClosingStatus() {
        if (closed) {
            throw new PdfException(KernelExceptionMessageConstant.DOCUMENT_CLOSED_IT_IS_IMPOSSIBLE_TO_EXECUTE_ACTION);
        }
    }

    /**
     * Returns the factory for creating page instances.
     *
     * @return implementation of {@link IPdfPageFactory} for current document
     */
    protected IPdfPageFactory getPageFactory() {
        return pdfPageFactory;
    }

    /**
     * Initializes the new instance of document's structure tree root {@link PdfStructTreeRoot}.
     * See ISO 32000-1, section 14.7.2 Structure Hierarchy.
     *
     * @param str dictionary to create structure tree root
     */
    protected void tryInitTagStructure(PdfDictionary str) {
        try {
            structTreeRoot = new PdfStructTreeRoot(str, this);
            structParentIndex = getStructTreeRoot().getParentTreeNextKey();
        } catch (MemoryLimitsAwareException e){
            throw e;
        } catch (Exception e) {
            structTreeRoot = null;
            structParentIndex = -1;
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.error(IoLogMessageConstant.TAG_STRUCTURE_INIT_FAILED, e);
        }
    }

    /**
     * Gets list of indirect references.
     *
     * @return list of indirect references.
     */
    PdfXrefTable getXref() {
        return xref;
    }

    boolean isDocumentFont(PdfIndirectReference indRef) {
        return indRef != null && documentFonts.containsKey(indRef);
    }

    boolean doesStreamBelongToEmbeddedFile(PdfStream stream) {
        return encryptedEmbeddedStreamsHandler.isStreamStoredAsEmbedded(stream);
    }

    boolean hasAcroForm() {
        return getCatalog().getPdfObject().containsKey(PdfName.AcroForm);
    }

    private void tryFlushTagStructure(boolean isAppendMode) {
        try {
            if (tagStructureContext != null) {
                tagStructureContext.prepareToDocumentClosing();
            }
            if (!isAppendMode || structTreeRoot.getPdfObject().isModified()) {
                structTreeRoot.flush();
            }
        } catch (MemoryLimitsAwareException e){
            throw e;
        } catch (Exception e) { 
            throw new PdfException(KernelExceptionMessageConstant.TAG_STRUCTURE_FLUSHING_FAILED_IT_MIGHT_BE_CORRUPTED,
                    e);
        }
    }

    private void updateValueInMarkInfoDict(PdfName key, PdfObject value) {
        PdfDictionary markInfo = catalog.getPdfObject().getAsDictionary(PdfName.MarkInfo);
        if (markInfo == null) {
            markInfo = new PdfDictionary();
            catalog.getPdfObject().put(PdfName.MarkInfo, markInfo);
        }
        markInfo.put(key, value);
    }

    /**
     * Removes all widgets associated with a given page from AcroForm structure. Widgets can be either pure or merged.
     *
     * @param page to remove from.
     */
    private void removeUnusedWidgetsFromFields(PdfPage page) {
        if (page.isFlushed()) {
            return;
        }

        final PdfDictionary acroForm = this.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
        final PdfArray fields = acroForm == null ? null : acroForm.getAsArray(PdfName.Fields);

        List<PdfAnnotation> annots = page.getAnnotations();
        for (PdfAnnotation annot : annots) {
            if (annot.getSubtype().equals(PdfName.Widget)) {
                ((PdfWidgetAnnotation) annot).releaseFormFieldFromWidgetAnnotation();
                if (fields != null) {
                    fields.remove(annot.getPdfObject());
                }
            }
        }
    }

    private void resolveDestinations(PdfDocument toDocument, Map<PdfPage, PdfPage> page2page) {
        for (int i = 0; i < pendingDestinationMutations.size(); ++i) {
            PdfDocument.DestinationMutationInfo mutation = pendingDestinationMutations.get(i);
            PdfDestination copiedDest = null;
            copiedDest = getCatalog().copyDestination(mutation.getOriginalDestination().getPdfObject(), page2page,
                    toDocument);
            if (copiedDest == null) {
                mutation.handleDestinationUnavailable();
            } else {
                mutation.handleDestinationAvailable(copiedDest);
            }
        }
    }

    /**
     * This method copies all given outlines
     *
     * @param outlines   outlines to be copied
     * @param toDocument document where outlines should be copied
     */
    private void copyOutlines(Set<PdfOutline> outlines, PdfDocument toDocument, Map<PdfPage, PdfPage> page2page) {

        final Set<PdfOutline> outlinesToCopy = new HashSet<>();
        outlinesToCopy.addAll(outlines);

        for (PdfOutline outline : outlines) {
            getAllOutlinesToCopy(outline, outlinesToCopy);
        }

        PdfOutline rootOutline = toDocument.getOutlines(false);
        if (rootOutline == null) {
            rootOutline = new PdfOutline(toDocument);
            rootOutline.setTitle("Outlines");
        }

        cloneOutlines(outlinesToCopy, rootOutline, getOutlines(false), page2page, toDocument);
    }

    /**
     * This method gets all outlines to be copied including parent outlines
     *
     * @param outline        current outline
     * @param outlinesToCopy a Set of outlines to be copied
     */
    private void getAllOutlinesToCopy(PdfOutline outline, Set<PdfOutline> outlinesToCopy) {
        final PdfOutline parent = outline.getParent();
        //note there's no need to continue recursion if the current outline parent is root (first condition) or
        // if it is already in the Set of outlines to be copied (second condition)
        if ("Outlines".equals(parent.getTitle()) || outlinesToCopy.contains(parent)) {
            return;
        }
        outlinesToCopy.add(parent);
        getAllOutlinesToCopy(parent, outlinesToCopy);
    }

    /**
     * This method copies create new outlines in the Document to copy.
     *
     * @param outlinesToCopy - Set of outlines to be copied
     * @param newParent      - new parent outline
     * @param oldParent      - old parent outline
     */
    private void cloneOutlines(Set<PdfOutline> outlinesToCopy, PdfOutline newParent, PdfOutline oldParent,
            Map<PdfPage, PdfPage> page2page, PdfDocument toDocument) {
        if (null == oldParent) {
            return;
        }
        for (PdfOutline outline : oldParent.getAllChildren()) {
            if (outlinesToCopy.contains(outline)) {
                PdfDestination copiedDest = null;
                if (null != outline.getDestination()) {
                    PdfObject destObjToCopy = outline.getDestination().getPdfObject();
                    copiedDest = getCatalog().copyDestination(destObjToCopy, page2page, toDocument);
                }
                PdfOutline child = newParent.addOutline(outline.getTitle());
                if (copiedDest != null) {
                    child.addDestination(copiedDest);
                }
                Integer copiedStyle = outline.getStyle();
                if (copiedStyle != null) {
                    child.setStyle(copiedStyle.intValue());
                }
                Color copiedColor = outline.getColor();
                if (copiedColor != null) {
                    child.setColor(copiedColor);
                }
                child.setOpen(outline.isOpen());

                cloneOutlines(outlinesToCopy, child, outline, page2page, toDocument);
            }
        }
    }

    private void ensureTreeRootAddedToNames(PdfObject treeRoot, PdfName treeType) {
        PdfDictionary names = catalog.getPdfObject().getAsDictionary(PdfName.Names);
        if (names == null) {
            names = new PdfDictionary();
            catalog.put(PdfName.Names, names);
            names.makeIndirect(this);
        }
        names.put(treeType, treeRoot);
        names.setModified();
    }

    private boolean writerHasEncryption() {
        return writer.properties.isStandardEncryptionUsed() || writer.properties.isPublicKeyEncryptionUsed();
    }

    private void updatePdfVersionFromCatalog() {
        if (catalog.getPdfObject().containsKey(PdfName.Version)) {
            // The version of the PDF specification to which the document conforms (for example, 1.4)
            // if later than the version specified in the file's header
            try {
                PdfVersion catalogVersion = PdfVersion.fromPdfName(catalog.getPdfObject().getAsName(PdfName.Version));
                if (catalogVersion.compareTo(pdfVersion) > 0) {
                    pdfVersion = catalogVersion;
                }
            } catch (IllegalArgumentException e) {
                processReadingError(IoLogMessageConstant.DOCUMENT_VERSION_IN_CATALOG_CORRUPTED);
            }
        }
    }

    private void readDocumentIds() {
        final PdfArray id = reader.trailer.getAsArray(PdfName.ID);

        if (id != null) {
            if (id.size() == 2) {
                originalDocumentId = id.getAsString(0);
                modifiedDocumentId = id.getAsString(1);
            }

            if (originalDocumentId == null || modifiedDocumentId == null) {
                processReadingError(IoLogMessageConstant.DOCUMENT_IDS_ARE_CORRUPTED);
            }
        }
    }

    private void processReadingError(String errorMessage) {
        if (StrictnessLevel.CONSERVATIVE.isStricter(reader.getStrictnessLevel())) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.error(errorMessage);
        } else {
            throw new PdfException(errorMessage);
        }
    }

    private static void overrideFullCompressionInWriterProperties(WriterProperties properties,
            boolean readerHasXrefStream) {
        if (Boolean.TRUE == properties.isFullCompression && !readerHasXrefStream) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.warn(KernelLogMessageConstant.FULL_COMPRESSION_APPEND_MODE_XREF_TABLE_INCONSISTENCY);
        } else if (Boolean.FALSE == properties.isFullCompression && readerHasXrefStream) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.warn(KernelLogMessageConstant.FULL_COMPRESSION_APPEND_MODE_XREF_STREAM_INCONSISTENCY);
        }
        properties.isFullCompression = readerHasXrefStream;
    }

    private static boolean isXmpMetaHasProperty(XMPMeta xmpMeta, String schemaNS, String propName) throws XMPException {
        return xmpMeta.getProperty(schemaNS, propName) != null;
    }

    private static class DestinationMutationInfo {
        private final PdfDestination originalDestination;
        private final Consumer<PdfDestination> onDestinationAvailable;
        private final Consumer<PdfDestination> onDestinationNotAvailable;

        public DestinationMutationInfo(PdfDestination originalDestination,
                Consumer<PdfDestination> onDestinationAvailable, Consumer<PdfDestination> onDestinationNotAvailable) {
            this.originalDestination = originalDestination;
            this.onDestinationAvailable = onDestinationAvailable;
            this.onDestinationNotAvailable = onDestinationNotAvailable;
        }

        public void handleDestinationAvailable(PdfDestination newDestination) {
            onDestinationAvailable.accept(newDestination);
        }

        public void handleDestinationUnavailable() {
            onDestinationNotAvailable.accept(originalDestination);
        }

        public PdfDestination getOriginalDestination() {
            return originalDestination;
        }
    }
}
