/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.Version;
import com.itextpdf.kernel.crypto.BadPasswordException;
import com.itextpdf.kernel.events.EventDispatcher;
import com.itextpdf.kernel.events.IEventDispatcher;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.log.Counter;
import com.itextpdf.kernel.log.CounterFactory;
import com.itextpdf.kernel.numbering.EnglishAlphabetNumbering;
import com.itextpdf.kernel.numbering.RomanNumbering;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.xmp.PdfConst;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.kernel.xmp.options.SerializeOptions;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main enter point to work with PDF document.
 */
public class PdfDocument implements IEventDispatcher, Closeable, Serializable {

    private static final long serialVersionUID = -7041578979319799646L;

    /**
     * Currently active page.
     */
    protected PdfPage currentPage = null;

    /**
     * Default page size.
     * New page by default will be created with this size.
     */
    protected PageSize defaultPageSize = PageSize.Default;

    protected transient EventDispatcher eventDispatcher = new EventDispatcher();

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

    /**
     * The ID entry that represents a change in a document.
     */
    protected PdfString modifiedDocumentId;

    /**
     * The original second id when the document is read initially.
     */
    private PdfString originalModifiedDocumentId;

    /**
     * List of indirect objects used in the document.
     */
    final PdfXrefTable xref = new PdfXrefTable();

    protected final StampingProperties properties;

    protected PdfStructTreeRoot structTreeRoot;

    protected int structParentIndex = -1;
    @Deprecated
    protected boolean userProperties;

    protected boolean closeReader = true;
    protected boolean closeWriter = true;

    protected boolean isClosing = false;

    protected boolean closed = false;


    /**
     * flag determines whether to write unused objects to result document
     */
    protected boolean flushUnusedObjects = false;

    private Map<PdfIndirectReference, PdfFont> documentFonts = new HashMap<>();
    private PdfFont defaultFont = null;

    protected transient TagStructureContext tagStructureContext;

    private static AtomicLong lastDocumentId = new AtomicLong();

    private long documentId;

    /**
     * Yet not copied link annotations from the other documents.
     * Key - page from the source document, which contains this annotation.
     * Value - link annotation from the source document.
     */
    private LinkedHashMap<PdfPage, List<PdfLinkAnnotation>> linkAnnotations = new LinkedHashMap<>();

    /**
     * Open PDF document in reading mode.
     *
     * @param reader PDF reader.
     */
    public PdfDocument(PdfReader reader) {
        if (reader == null) {
            throw new NullPointerException("reader");
        }
        documentId = incrementDocumentId();
        this.reader = reader;
        this.properties = new StampingProperties(); // default values of the StampingProperties doesn't affect anything
        open(null);
    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     *
     * @param writer PDF writer
     */
    public PdfDocument(PdfWriter writer) {
        if (writer == null) {
            throw new NullPointerException("writer");
        }
        documentId = incrementDocumentId();
        this.writer = writer;
        this.properties = new StampingProperties(); // default values of the StampingProperties doesn't affect anything
        open(writer.properties.pdfVersion);
    }

    /**
     * Opens PDF document in the stamping mode.
     * <br/>
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
            throw new NullPointerException("reader");
        }
        if (writer == null) {
            throw new NullPointerException("writer");
        }
        documentId = incrementDocumentId();
        this.reader = reader;
        this.writer = writer;
        this.properties = properties;

        boolean writerHasEncryption = writer.properties.isStandardEncryptionUsed() || writer.properties.isPublicKeyEncryptionUsed();
        if (properties.appendMode && writerHasEncryption) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.warn(LogMessageConstant.WRITER_ENCRYPTION_IS_IGNORED_APPEND);
        }
        if (properties.preserveEncryption && writerHasEncryption) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.warn(LogMessageConstant.WRITER_ENCRYPTION_IS_IGNORED_PRESERVE);
        }

        open(writer.properties.pdfVersion);
    }

    /**
     * Use this method to set the XMP Metadata.
     *
     * @param xmpMetadata The xmpMetadata to set.
     */
    protected void setXmpMetadata(byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }

    public void setXmpMetadata(XMPMeta xmpMeta, SerializeOptions serializeOptions) throws XMPException {
        setXmpMetadata(XMPMetaFactory.serializeToBuffer(xmpMeta, serializeOptions));
    }

    public void setXmpMetadata(XMPMeta xmpMeta) throws XMPException {
        SerializeOptions serializeOptions = new SerializeOptions();
        serializeOptions.setPadding(2000);
        setXmpMetadata(xmpMeta, serializeOptions);
    }

    /**
     * Gets XMPMetadata.
     */
    public byte[] getXmpMetadata() {
        return getXmpMetadata(false);
    }

    /**
     * Gets XMPMetadata or create a new one.
     *
     * @param createNew if true, create a new empty XMPMetadata if it did not present.
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
                xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Producer, Version.getInstance().getVersion());
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
     * @return page by page number.
     */
    public PdfPage getPage(int pageNum) {
        checkClosingStatus();
        return catalog.getPageTree().getPage(pageNum);
    }

    /**
     * Gets the {@link PdfPage} instance by {@link PdfDictionary}.
     *
     * @param pageDictionary {@link PdfDictionary} that present page.
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
     * @return added page
     */
    public PdfPage addNewPage(PageSize pageSize) {
        checkClosingStatus();
        PdfPage page = new PdfPage(this, pageSize);
        checkAndAddPage(page);
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.START_PAGE, page));
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.INSERT_PAGE, page));
        return page;
    }

    /**
     * Creates and inserts new page to the document.
     *
     * @param index position to addPage page to
     * @return inserted page
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
     * @return inserted page
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addNewPage(int index, PageSize pageSize) {
        checkClosingStatus();
        PdfPage page = new PdfPage(this, pageSize);
        checkAndAddPage(index, page);
        currentPage = page;
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.START_PAGE, page));
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.INSERT_PAGE, page));
        return currentPage;
    }

    /**
     * Adds page to the end of document.
     *
     * @param page page to add.
     * @return added page.
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
     * @return inserted page
     * @throws PdfException in case {@code page} is flushed
     */
    public PdfPage addPage(int index, PdfPage page) {
        checkClosingStatus();
        checkAndAddPage(index, page);
        currentPage = page;
        dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.INSERT_PAGE, page));
        return currentPage;
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
     * @return page number by {@link PdfDictionary}.
     */
    public int getPageNumber(PdfDictionary pageDictionary) {
        return catalog.getPageTree().getPageNumber(pageDictionary);
    }

    /**
     * Removes the first occurrence of the specified page from this document,
     * if it is present. Returns <tt>true</tt> if this document
     * contained the specified element (or equivalently, if this document
     * changed as a result of the call).
     *
     * @param page page to be removed from this document, if present
     * @return <tt>true</tt> if this document contained the specified page
     */
    public boolean removePage(PdfPage page) {
        checkClosingStatus();
        int pageNum = getPageNumber(page);
        return pageNum >= 1 && removePage(pageNum) != null;
    }

    /**
     * Removes page from the document by page number.
     *
     * @param pageNum the one-based index of the PdfPage to be removed
     * @return the page that was removed from the list
     */
    public PdfPage removePage(int pageNum) {
        checkClosingStatus();
        PdfPage removedPage = catalog.getPageTree().removePage(pageNum);

        if (removedPage != null) {
            catalog.removeOutlines(removedPage);
            removeUnusedWidgetsFromFields(removedPage);
            if (isTagged()) {
                getTagStructureContext().removePageTags(removedPage);
            }

            if (!removedPage.getPdfObject().isFlushed()) {
                removedPage.getPdfObject().remove(PdfName.Parent);
            }
            removedPage.getPdfObject().getIndirectReference().setFree();

            dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.REMOVE_PAGE, removedPage));
        }
        return removedPage;
    }

    /**
     * Gets document information dictionary.
     *
     * @return document information dictionary.
     */
    public PdfDocumentInfo getDocumentInfo() {
        checkClosingStatus();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventHandler(String type, IEventHandler handler) {
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
    public void removeEventHandler(String type, IEventHandler handler) {
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
                    throw new PdfException(PdfException.CannotCloseDocumentWithAlreadyFlushedPdfCatalog);
                }
                updateXmpMetadata();
                if (getXmpMetadata() != null) {
                    PdfStream xmp = new PdfStream().makeIndirect(this);
                    xmp.getOutputStream().write(xmpMetadata);
                    xmp.put(PdfName.Type, PdfName.Metadata);
                    xmp.put(PdfName.Subtype, PdfName.XML);
                    PdfEncryption crypto = writer.crypto;
                    if (crypto != null && !crypto.isMetadataEncrypted()) {
                        PdfArray ar = new PdfArray();
                        ar.add(PdfName.Crypt);
                        xmp.put(PdfName.Filter, ar);
                    }
                    catalog.getPdfObject().put(PdfName.Metadata, xmp);
                }
                String producer = null;
                if (reader == null) {
                    producer = Version.getInstance().getVersion();
                } else {
                    if (info.getPdfObject().containsKey(PdfName.Producer)) {
                        producer = info.getPdfObject().getAsString(PdfName.Producer).toUnicodeString();
                    }
                    producer = addModifiedPostfix(producer);
                }
                info.getPdfObject().put(PdfName.Producer, new PdfString(producer));
                checkIsoConformance();
                PdfObject crypto = null;
                if (properties.appendMode) {
                    if (structTreeRoot != null) {
                        tryFlushTagStructure(true);
                    }
                    if (catalog.isOCPropertiesMayHaveChanged() && catalog.getOCProperties(false).getPdfObject().isModified()) {
                        catalog.getOCProperties(false).flush();
                    }
                    if (catalog.pageLabels != null) {
                        catalog.put(PdfName.PageLabels, catalog.pageLabels.buildTree());
                    }

                    PdfObject pageRoot = catalog.getPageTree().generateTree();
                    if (catalog.getPdfObject().isModified() || pageRoot.isModified()) {
                        catalog.getPdfObject().put(PdfName.Pages, pageRoot);
                        catalog.getPdfObject().flush(false);
                    }

                    for (Map.Entry<PdfName, PdfNameTree> entry : catalog.nameTrees.entrySet()) {
                        PdfNameTree tree = entry.getValue();
                        if (tree.isModified()) {
                            ensureTreeRootAddedToNames(tree.buildTree().makeIndirect(this), entry.getKey());
                        }
                    }

                    if (info.getPdfObject().isModified()) {
                        info.flush();
                    }
                    flushFonts();

                    writer.flushModifiedWaitingObjects();
                    if (writer.crypto != null) {
                        assert reader.decrypt.getPdfObject() == writer.crypto.getPdfObject() : "Conflict with source encryption";
                        crypto = reader.decrypt.getPdfObject();
                    }
                } else {

                    if (catalog.isOCPropertiesMayHaveChanged()) {
                        catalog.getPdfObject().put(PdfName.OCProperties, catalog.getOCProperties(false).getPdfObject());
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
                        getPage(pageNum).flush();
                    }
                    if (structTreeRoot != null) {
                        tryFlushTagStructure(false);
                    }
                    catalog.getPdfObject().flush(false);
                    info.flush();
                    flushFonts();
                    writer.flushWaitingObjects();
                    // flush unused objects
                    if (isFlushUnusedObjects()) {
                        for (int i = 0; i < xref.size(); i++) {
                            PdfIndirectReference indirectReference = xref.get(i);
                            if (!indirectReference.isFree() && !indirectReference.checkState(PdfObject.FLUSHED)) {
                                PdfObject object = indirectReference.getRefersTo();
                                object.flush();
                            }
                        }
                    }

                }

                byte[] originalFileID = null;
                if (crypto == null && writer.crypto != null) {
                    originalFileID = writer.crypto.getDocumentId();
                    crypto = writer.crypto.getPdfObject();
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
                        originalFileID = PdfEncryption.generateNewDocumentId();
                    }
                }
                byte[] secondId;

                if ( modifiedDocumentId != null ) {
                    secondId = ByteUtils.getIsoBytes(modifiedDocumentId.getValue());
                } else {
                    if ( originalModifiedDocumentId != null ) {
                        PdfString newModifiedId = reader.trailer.getAsArray(PdfName.ID).getAsString(1);

                        if (!originalModifiedDocumentId.equals(newModifiedId)) {
                            secondId = ByteUtils.getIsoBytes(newModifiedId.getValue());
                        } else {
                            secondId = PdfEncryption.generateNewDocumentId();
                        }
                    } else {
                        if (isModified) {
                            secondId = PdfEncryption.generateNewDocumentId();
                        } else {
                            secondId = originalFileID;
                        }
                    }
                }
                // if originalFIleID comes from crypto, it means that no need in checking modified state.
                // For crypto purposes new documentId always generated.
                fileId = PdfEncryption.createInfoId(originalFileID, secondId);

                // The following two operators prevents the possible inconsistency between root and info
                // entries existing in the trailer object and corresponding fields. This inconsistency
                // may appear when user gets trailer and explicitly sets new root or info dictionaries.
                trailer.put(PdfName.Root, catalog.getPdfObject());
                trailer.put(PdfName.Info, info.getPdfObject());

                xref.writeXrefTableAndTrailer(this, fileId, crypto);
                writer.flush();
                Counter counter = getCounter();
                if (counter != null) {
                    counter.onDocumentWritten(writer.getCurrentPos());
                }
            }
            catalog.getPageTree().clearPageRefs();
            removeAllHandlers();
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotCloseDocument, e, this);
        } finally {

            if (writer != null && isCloseWriter()) {
                try {
                    writer.close();
                } catch (Exception e) {
                    Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                    logger.error(LogMessageConstant.PDF_WRITER_CLOSING_FAILED, e);
                }
            }

            if (reader != null && isCloseReader()) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                    logger.error(LogMessageConstant.PDF_READER_CLOSING_FAILED, e);
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

    public void setTagged() {
        checkClosingStatus();
        if (structTreeRoot == null) {
            structTreeRoot = new PdfStructTreeRoot(this);
            catalog.getPdfObject().put(PdfName.StructTreeRoot, structTreeRoot.getPdfObject());
            updateValueInMarkInfoDict(PdfName.Marked, PdfBoolean.TRUE);

            structParentIndex = 0;
        }
    }

    /**
     * Gets {@link PdfStructTreeRoot} of tagged document.
     *
     * @return {@link PdfStructTreeRoot} in case tagged document, otherwise false.
     * @see #isTagged()
     * @see #getNextStructParentIndex()
     */
    public PdfStructTreeRoot getStructTreeRoot() {
        return structTreeRoot;
    }

    /**
     * Gets next parent index of tagged document.
     *
     * @return -1 if document is not tagged, or >= 0 if tagged.
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
                throw new PdfException(PdfException.MustBeATaggedDocument);
            }

            initTagStructureContext();
        }

        return tagStructureContext;
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
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument, int insertBeforePage) {
        return copyPagesTo(pageFrom, pageTo, toDocument, insertBeforePage, null);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument}.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pageFrom         1-based start of the range of pages to be copied.
     * @param pageTo           1-based end of the range of pages to be copied.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     * @param copier           a copier which bears a special copy logic. May be NULL
     * @return list of new copied pages
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument, int insertBeforePage, IPdfPageExtraCopier copier) {
        List<Integer> pages = new ArrayList<>();
        for (int i = pageFrom; i <= pageTo; i++) {
            pages.add(i);
        }
        return copyPagesTo(pages, toDocument, insertBeforePage, copier);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pageFrom   1-based start of the range of pages to be copied.
     * @param pageTo     1-based end of the range of pages to be copied.
     * @param toDocument a document to copy pages to.
     * @return list of new copied pages
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument) {
        return copyPagesTo(pageFrom, pageTo, toDocument, null);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pageFrom   1-based start of the range of pages to be copied.
     * @param pageTo     1-based end of the range of pages to be copied.
     * @param toDocument a document to copy pages to.
     * @param copier     a copier which bears a special copy logic. May be null.
     * @return list of new copied pages.
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument, IPdfPageExtraCopier copier) {
        return copyPagesTo(pageFrom, pageTo, toDocument, toDocument.getNumberOfPages() + 1, copier);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument}.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pagesToCopy      list of pages to be copied. TreeSet for the order of the pages to be natural.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     * @return list of new copied pages
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument, int insertBeforePage) {
        return copyPagesTo(pagesToCopy, toDocument, insertBeforePage, null);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument}.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pagesToCopy      list of pages to be copied. TreeSet for the order of the pages to be natural.
     * @param toDocument       a document to copy pages to.
     * @param insertBeforePage a position where to insert copied pages.
     * @param copier           a copier which bears a special copy logic. May be NULL
     * @return list of new copied pages
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument, int insertBeforePage, IPdfPageExtraCopier copier) {
        if (pagesToCopy.isEmpty()) {
            return Collections.<PdfPage>emptyList();
        }

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
            PdfPage newPage = page.copyTo(toDocument, copier);
            copiedPages.add(newPage);
            if (!page2page.containsKey(page)) {
                page2page.put(page, newPage);
            }

            if (lastCopiedPageNum >= pageNum) {
                rangesOfPagesWithIncreasingNumbers.add(new HashMap<PdfPage, PdfPage>());
            }
            int lastRangeInd = rangesOfPagesWithIncreasingNumbers.size() - 1;
            rangesOfPagesWithIncreasingNumbers.get(lastRangeInd).put(page, newPage);

            if (insertInBetween) {
                toDocument.addPage(pageInsertIndex, newPage);
            } else {
                toDocument.addPage(newPage);
            }
            pageInsertIndex++;
            if (toDocument.hasOutlines()) {
                List<PdfOutline> pageOutlines = page.getOutlines(false);
                if (pageOutlines != null)
                    outlinesToCopy.addAll(pageOutlines);
            }
            lastCopiedPageNum = (int) pageNum;
        }

        copyLinkAnnotations(toDocument, page2page);

        // It's important to copy tag structure after link annotations were copied, because object content items in tag
        // structure are not copied in case if their's OBJ key is annotation and doesn't contain /P entry.
        if (toDocument.isTagged()) {
            if (isTagged()) {
                if (tagStructureContext != null) {
                    tagStructureContext.actualizeTagsProperties();
                }
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
                } catch (Exception ex) {
                    throw new PdfException(PdfException.TagStructureCopyingFailedItMightBeCorruptedInOneOfTheDocuments, ex);
                }
            } else {
                Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                logger.warn(LogMessageConstant.NOT_TAGGED_PAGES_IN_TAGGED_DOCUMENT);
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
     *
     * @param pagesToCopy list of pages to be copied. TreeSet for the order of the pages to be natural.
     * @param toDocument  a document to copy pages to.
     * @return list of copied pages
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument) {
        return copyPagesTo(pagesToCopy, toDocument, null);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pagesToCopy list of pages to be copied. TreeSet for the order of the pages to be natural.
     * @param toDocument  a document to copy pages to.
     * @param copier      a copier which bears a special copy logic
     * @return list of copied pages
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument, IPdfPageExtraCopier copier) {
        return copyPagesTo(pagesToCopy, toDocument, toDocument.getNumberOfPages() + 1, copier);
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
     * @param updateOutlines if the flag is true, the method read the whole document and creates outline tree.
     *                       If false the method gets cached outline tree (if it was cached via calling getOutlines method before).
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
        checkClosingStatus();
        catalog.addNamedDestination(key, value);
    }

    /**
     * Gets static copy of cross reference table.
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
     * @see PdfOutputIntent
     */
    public void addOutputIntent(PdfOutputIntent outputIntent) {
        checkClosingStatus();
        if (outputIntent == null)
            return;

        PdfArray outputIntents = catalog.getPdfObject().getAsArray(PdfName.OutputIntents);
        if (outputIntents == null) {
            outputIntents = new PdfArray();
            catalog.put(PdfName.OutputIntents, outputIntents);
        }
        outputIntents.add(outputIntent.getPdfObject());
    }

    /**
     * Checks whether PDF document conforms a specific standard.
     * Shall be override.
     *
     * @param obj An object to conform.
     * @param key type of object to conform.
     */
    public void checkIsoConformance(Object obj, IsoKey key) {
    }

    /**
     * Checks whether PDF document conforms a specific standard.
     * Shall be override.
     *
     * @param obj       an object to conform.
     * @param key       type of object to conform.
     * @param resources {@link PdfResources} associated with an object to check.
     */
    public void checkIsoConformance(Object obj, IsoKey key, PdfResources resources) {
    }

    /**
     * Checks whether PDF document conforms a specific standard.
     * Shall be override.
     *
     * @param gState    a {@link CanvasGraphicsState} object to conform.
     * @param resources {@link PdfResources} associated with an object to check.
     */
    public void checkShowTextIsoConformance(Object gState, PdfResources resources) {
    }

    /**
     * Adds file attachment at document level.
     *
     * @param description         the file description
     * @param fileStore           an array with the file.
     * @param fileDisplay         the actual file name stored in the pdf
     * @param mimeType            mime type of the file
     * @param fileParameter       the optional extra file parameters such as the creation or modification date
     * @param afRelationshipValue if {@code null}, {@link PdfName#Unspecified} will be added. Shall be one of:
     *                            {@link PdfName#Source}, {@link PdfName#Data}, {@link PdfName#Alternative},
     *                            {@link PdfName#Supplement} or {@link PdfName#Unspecified}.
     */
    public void addFileAttachment(String description, byte[] fileStore, String fileDisplay, PdfName mimeType, PdfDictionary fileParameter, PdfName afRelationshipValue) {
        addFileAttachment(description, PdfFileSpec.createEmbeddedFileSpec(this, fileStore, description, fileDisplay, mimeType, fileParameter, afRelationshipValue, true));
    }

    /**
     * Adds file attachment at document level.
     *
     * @param description         the file description
     * @param file                the path to the file.
     * @param fileDisplay         the actual file name stored in the pdf
     * @param mimeType            mime type of the file
     * @param afRelationshipValue if {@code null}, {@link PdfName#Unspecified} will be added. Shall be one of:
     *                            {@link PdfName#Source}, {@link PdfName#Data}, {@link PdfName#Alternative},
     *                            {@link PdfName#Supplement} or {@link PdfName#Unspecified}.
     */
    public void addFileAttachment(String description, String file, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue) throws IOException {
        addFileAttachment(description, PdfFileSpec.createEmbeddedFileSpec(this, file, description, fileDisplay, mimeType, afRelationshipValue, true));
    }

    /**
     * Adds file attachment at document level.
     *
     * @param description the file description
     * @param fs          {@link PdfFileSpec} object.
     */
    public void addFileAttachment(String description, PdfFileSpec fs) {
        checkClosingStatus();
        catalog.addNameToNameTree(description, fs.getPdfObject(), PdfName.EmbeddedFiles);

        PdfArray afArray = catalog.getPdfObject().getAsArray(PdfName.AF);
        if (afArray == null) {
            afArray = new PdfArray().makeIndirect(this);
            catalog.put(PdfName.AF, afArray);
        }
        afArray.add(fs.getPdfObject());
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
        this.userProperties = userProperties;
        PdfBoolean userPropsVal = userProperties ? PdfBoolean.TRUE : PdfBoolean.FALSE;
        updateValueInMarkInfoDict(PdfName.UserProperties, userPropsVal);
    }

    /**
     * The /ID entry of a document contains an array with two entries. The first one represents the initial document id.
     * The second one should be the same entry, unless the document has been modified. iText will by default generate
     * a modified id. But if you'd like you can set this id yourself using this setter.
     *
     * @param modifiedDocumentId the new modified document id
     */
    public void setModifiedDocumentId(PdfString modifiedDocumentId) {
        this.modifiedDocumentId = modifiedDocumentId;
    }

    /**
     * Create a new instance of {@link PdfFont} or load already created one.
     *
     * Note, PdfFont which created with {@link PdfFontFactory#createFont(PdfDictionary)} won't be cached
     * until it will be added to {@link com.itextpdf.kernel.pdf.canvas.PdfCanvas} or {@link PdfResources}.
     */
    public PdfFont getFont(PdfDictionary dictionary) {
        assert dictionary.getIndirectReference() != null;
        if (documentFonts.containsKey(dictionary.getIndirectReference())) {
            return documentFonts.get(dictionary.getIndirectReference());
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
                defaultFont.makeIndirect(this);
                addFont(defaultFont);
            } catch (IOException e) {
                Logger logger = LoggerFactory.getLogger(PdfDocument.class);
                logger.error(LogMessageConstant.EXCEPTION_WHILE_CREATING_DEFAULT_FONT, e);
                defaultFont = null;
            }
        }
        return defaultFont;
    }
    /**
     * Gets list of indirect references.
     *
     * @return list of indirect references.
     */
    PdfXrefTable getXref() {
        return xref;
    }

    /**
     * Adds PdfFont without an checks
     * @return the same PdfFont instance.
     */
    PdfFont addFont(PdfFont font) {
        documentFonts.put(font.getPdfObject().getIndirectReference(), font);
        return font;
    }

    /**
     * Initialize {@link TagStructureContext}.
     */
    protected void initTagStructureContext() {
        tagStructureContext = new TagStructureContext(this);
    }

    /**
     * Save the link annotation in a temporary storage for further copying.
     *
     * @param page       just copied {@link PdfPage} link annotation belongs to.
     * @param annotation {@link PdfLinkAnnotation} itself.
     */
    protected void storeLinkAnnotation(PdfPage page, PdfLinkAnnotation annotation) {
        List<PdfLinkAnnotation> pageAnnotations = linkAnnotations.get(page);
        if (pageAnnotations == null) {
            pageAnnotations = new ArrayList<>();
            linkAnnotations.put(page, pageAnnotations);
        }
        pageAnnotations.add(annotation);
    }

    /**
     * Checks whether PDF document conforms a specific standard.
     * Shall be override.
     */
    protected void checkIsoConformance() {
    }

    /**
     * Mark an object with {@link PdfObject#MUST_BE_FLUSHED}.
     *
     * @param pdfObject an object to mark.
     */
    protected void markObjectAsMustBeFlushed(PdfObject pdfObject) {
        if (pdfObject.isIndirect()) {
            pdfObject.getIndirectReference().setState(PdfObject.MUST_BE_FLUSHED);
        }
    }

    /**
     * Flush an object.
     *
     * @param pdfObject     object to flush.
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     * @throws IOException on error.
     */
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        writer.flushObject(pdfObject, canBeInObjStm);
    }

    /**
     * Initializes document.
     *
     * @param newPdfVersion new pdf version of the resultant file if stamper is used and the version needs to be changed,
     *                      or {@code null} otherwise
     */
    protected void open(PdfVersion newPdfVersion) {
        try {
            if (reader != null) {
                reader.pdfDocument = this;
                reader.readPdf();
                Counter counter = getCounter();
                if (counter != null) {
                    counter.onDocumentRead(reader.getFileLength());
                }
                pdfVersion = reader.headerPdfVersion;
                trailer = new PdfDictionary(reader.trailer);

                PdfArray id = reader.trailer.getAsArray(PdfName.ID);

                if (id != null) {
                     originalModifiedDocumentId = id.getAsString(1);
                }

                catalog = new PdfCatalog((PdfDictionary) trailer.get(PdfName.Root, true));
                if (catalog.getPdfObject().containsKey(PdfName.Version)) {
                    // The version of the PDF specification to which the document conforms (for example, 1.4)
                    // if later than the version specified in the file's header
                    PdfVersion catalogVersion = PdfVersion.fromPdfName(catalog.getPdfObject().getAsName(PdfName.Version));
                    if (catalogVersion.compareTo(pdfVersion) > 0) {
                        pdfVersion = catalogVersion;
                    }
                }
                if (catalog.getPdfObject().containsKey(PdfName.Metadata) && null != catalog.getPdfObject().get(PdfName.Metadata)) {
                    xmpMetadata = catalog.getPdfObject().getAsStream(PdfName.Metadata).getBytes();
                    try {
                        reader.pdfAConformanceLevel = PdfAConformanceLevel.getConformanceLevel(XMPMetaFactory.parseFromBuffer(xmpMetadata));
                    } catch (XMPException ignored) {
                    }
                }
                PdfObject infoDict = trailer.get(PdfName.Info, true);
                info = new PdfDocumentInfo(infoDict instanceof PdfDictionary ?
                        (PdfDictionary) infoDict : new PdfDictionary(), this);

                PdfDictionary str = catalog.getPdfObject().getAsDictionary(PdfName.StructTreeRoot);
                if (str != null) {
                    tryInitTagStructure(str);
                }
                if (properties.appendMode && (reader.hasRebuiltXref() || reader.hasFixedXref()))
                    throw new PdfException(PdfException.AppendModeRequiresADocumentWithoutErrorsEvenIfRecoveryWasPossible);
            }
            if (writer != null) {
                if (reader != null && reader.hasXrefStm() && writer.properties.isFullCompression == null) {
                    writer.properties.isFullCompression = true;
                }
                if (reader != null && !reader.isOpenedWithFullPermission()) {
                    throw new BadPasswordException(BadPasswordException.PdfReaderNotOpenedWithOwnerPassword);
                }
                if (reader != null && properties.preserveEncryption) {
                    writer.crypto = reader.decrypt;
                }
                writer.document = this;
                String producer = null;
                if (reader == null) {
                    catalog = new PdfCatalog(this);
                    info = new PdfDocumentInfo(this).addCreationDate();
                    producer = Version.getInstance().getVersion();
                } else {
                    if (info.getPdfObject().containsKey(PdfName.Producer)) {
                        producer = info.getPdfObject().getAsString(PdfName.Producer).toUnicodeString();
                    }
                    producer = addModifiedPostfix(producer);
                }
                info.addModDate();
                info.getPdfObject().put(PdfName.Producer, new PdfString(producer));
                trailer = new PdfDictionary();
                trailer.put(PdfName.Root, catalog.getPdfObject().getIndirectReference());
                trailer.put(PdfName.Info, info.getPdfObject().getIndirectReference());

                if ( reader !=  null ) {
                    // If the reader's trailer contains an ID entry, let's copy it over to the new trailer
                    if ( reader.trailer.containsKey(PdfName.ID)) {
                        trailer.put(PdfName.ID, reader.trailer.getAsArray(PdfName.ID));
                    }
                }
            }
            if (properties.appendMode) {       // Due to constructor reader and writer not null.
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
                writer.properties.isFullCompression = reader.hasXrefStm();

                writer.crypto = reader.decrypt;

                if (newPdfVersion != null) {
                    // In PDF 1.4, a PDF version can also be specified in the Version entry of the document catalog,
                    // essentially updating the version associated with the file by overriding the one specified in the file header
                    if (pdfVersion.compareTo(PdfVersion.PDF_1_4) >= 0) {
                        // If the header specifies a later version, or if this entry is absent, the document conforms to the
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
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotOpenDocument, e, this);
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
     * Updates XMP metadata.
     * Shall be override.
     */
    protected void updateXmpMetadata() {
        try {
            if (writer.properties.addXmpMetadata) {
                setXmpMetadata(updateDefaultXmpMetadata());
            }
        } catch (XMPException e) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.error(LogMessageConstant.EXCEPTION_WHILE_UPDATING_XMPMETADATA, e);
        }
    }

    /**
     * Update XMP metadata values from {@link PdfDocumentInfo}.
     */
    protected XMPMeta updateDefaultXmpMetadata() throws XMPException {
        XMPMeta xmpMeta = XMPMetaFactory.parseFromBuffer(getXmpMetadata(true));
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
                if (obj.getType() != PdfObject.STRING)
                    continue;
                value = ((PdfString) obj).toUnicodeString();
                if (PdfName.Title.equals(key)) {
                    xmpMeta.setLocalizedText(XMPConst.NS_DC, PdfConst.Title, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
                } else if (PdfName.Author.equals(key)) {
                    xmpMeta.appendArrayItem(XMPConst.NS_DC, PdfConst.Creator, new PropertyOptions(PropertyOptions.ARRAY_ORDERED), value, null);
                } else if (PdfName.Subject.equals(key)) {
                    xmpMeta.setLocalizedText(XMPConst.NS_DC, PdfConst.Description, XMPConst.X_DEFAULT, XMPConst.X_DEFAULT, value);
                } else if (PdfName.Keywords.equals(key)) {
                    for (String v : value.split(",|;")) {
                        if (v.trim().length() > 0) {
                            xmpMeta.appendArrayItem(XMPConst.NS_DC, PdfConst.Subject, new PropertyOptions(PropertyOptions.ARRAY), v.trim(), null);
                        }
                    }
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Keywords, value);
                } else if (PdfName.Creator.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.CreatorTool, value);
                } else if (PdfName.Producer.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_PDF, PdfConst.Producer, value);
                } else if (PdfName.CreationDate.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.CreateDate, PdfDate.getW3CDate(value));
                } else if (PdfName.ModDate.equals(key)) {
                    xmpMeta.setProperty(XMPConst.NS_XMP, PdfConst.ModifyDate, PdfDate.getW3CDate(value));
                }
            }
        }
        if (isTagged() && !isXmpMetaHasProperty(xmpMeta, XMPConst.NS_PDFUA_ID, XMPConst.PART)) {
            xmpMeta.setPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART, 1, new PropertyOptions(PropertyOptions.SEPARATE_NODE));
        }
        return xmpMeta;
    }

    /**
     * List all newly added or loaded fonts
     *
     * @return List of {@see PdfFonts}.
     */
    protected Collection<PdfFont> getDocumentFonts() {
        return documentFonts.values();
    }

    protected void flushFonts() {
        if (properties.appendMode) {
            for (PdfFont font : getDocumentFonts()) {
                if (font.getPdfObject().checkState(PdfObject.MUST_BE_INDIRECT) || font.getPdfObject().getIndirectReference().checkState(PdfObject.MODIFIED)) {
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
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted, page);
        }
        if (page.getDocument() != null && this != page.getDocument()) {
            throw new PdfException(PdfException.Page1CannotBeAddedToDocument2BecauseItBelongsToDocument3).setMessageParams(page, this, page.getDocument());
        }
        catalog.getPageTree().addPage(index, page);
    }

    /**
     * Checks page before adding.
     *
     * @param page {@link PdfPage} to add.
     */
    protected void checkAndAddPage(PdfPage page) {
        if (page.isFlushed())
            throw new PdfException(PdfException.FlushedPageCannotBeAddedOrInserted, page);
        if (page.getDocument() != null && this != page.getDocument())
            throw new PdfException(PdfException.Page1CannotBeAddedToDocument2BecauseItBelongsToDocument3).setMessageParams(page, this, page.getDocument());
        catalog.getPageTree().addPage(page);
    }

    /**
     * checks whether a method is invoked at the closed document
     */
    protected void checkClosingStatus() {
        if (closed) {
            throw new PdfException(PdfException.DocumentClosedItIsImpossibleToExecuteAction);
        }
    }

    /**
     * Gets {@link Counter} instance.
     *
     * @return {@link Counter} instance.
     */
    protected Counter getCounter() {
        return CounterFactory.getCounter(PdfDocument.class);
    }

    private void tryInitTagStructure(PdfDictionary str) {
        try {
            structTreeRoot = new PdfStructTreeRoot(str);
            structParentIndex = getStructTreeRoot().getParentTreeNextKey();
        } catch (Exception ex) {
            structTreeRoot = null;
            structParentIndex = -1;
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.error(LogMessageConstant.TAG_STRUCTURE_INIT_FAILED, ex);
        }
    }

    private void tryFlushTagStructure(boolean isAppendMode) {
        try {
            if (tagStructureContext != null) {
                tagStructureContext.prepareToDocumentClosing();
            }
            if (!isAppendMode || structTreeRoot.getPdfObject().isModified()) {
                structTreeRoot.flush();
            }
        } catch (Exception ex) {
            throw new PdfException(PdfException.TagStructureFlushingFailedItMightBeCorrupted, ex);
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
     * This method removes all annotation entries from form fields associated with a given page.
     *
     * @param page to remove from.
     */
    private void removeUnusedWidgetsFromFields(PdfPage page) {
        if (page.isFlushed()) {
            return;
        }
        List<PdfAnnotation> annots = page.getAnnotations();
        for (PdfAnnotation annot : annots) {
            if (annot.getSubtype().equals(PdfName.Widget)) {
                ((PdfWidgetAnnotation) annot).releaseFormFieldFromWidgetAnnotation();
            }
        }
    }

    private void copyLinkAnnotations(PdfDocument toDocument, Map<PdfPage, PdfPage> page2page) {
        List<PdfName> excludedKeys = new ArrayList<>();
        excludedKeys.add(PdfName.Dest);
        excludedKeys.add(PdfName.A);
        for (Map.Entry<PdfPage, List<PdfLinkAnnotation>> entry : linkAnnotations.entrySet()) {
            // We don't want to copy those link annotations, which reference to pages which weren't copied.
            for (PdfLinkAnnotation annot : entry.getValue()) {
                boolean toCopyAnnot = true;
                PdfDestination copiedDest = null;
                PdfDictionary copiedAction = null;

                PdfObject dest = annot.getDestinationObject();
                if (dest != null) {
                    // If link annotation has destination object, we try to copy this destination.
                    // Destination is not copied if it points to the not copied page, and therefore the whole
                    // link annotation is not copied.
                    copiedDest = getCatalog().copyDestination(dest, page2page, toDocument);
                    toCopyAnnot = copiedDest != null;
                } else {
                    // Link annotation may have associated action. If it is GoTo type, we try to copy it's destination.
                    // GoToR and GoToE also contain destinations, but they point not to pages of the current document,
                    // so we just copy them as is. If it is action of any other type, it is also just copied as is.
                    PdfDictionary action = annot.getAction();
                    if (action != null) {
                        if (PdfName.GoTo.equals(action.get(PdfName.S))) {
                            copiedAction = action.copyTo(toDocument, Arrays.asList(PdfName.D), false);
                            PdfDestination goToDest = getCatalog().copyDestination(action.get(PdfName.D), page2page, toDocument);
                            if (goToDest != null) {
                                copiedAction.put(PdfName.D, goToDest.getPdfObject());
                            } else {
                                toCopyAnnot = false;
                            }
                        } else {
                            copiedAction = action.copyTo(toDocument, false);
                        }
                    }
                }

                if (toCopyAnnot) {
                    PdfLinkAnnotation newAnnot = (PdfLinkAnnotation) PdfAnnotation.makeAnnotation(annot.getPdfObject().copyTo(toDocument, excludedKeys, true));
                    if (copiedDest != null) {
                        newAnnot.setDestination(copiedDest);
                    }
                    if (copiedAction != null) {
                        newAnnot.setAction(copiedAction);
                    }
                    entry.getKey().addAnnotation(-1, newAnnot, false);
                }
            }
        }
        linkAnnotations.clear();
    }

    /**
     * This method copies all given outlines
     *
     * @param outlines   outlines to be copied
     * @param toDocument document where outlines should be copied
     */
    private void copyOutlines(Set<PdfOutline> outlines, PdfDocument toDocument, Map<PdfPage, PdfPage> page2page) {

        Set<PdfOutline> outlinesToCopy = new HashSet<>();
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
        PdfOutline parent = outline.getParent();
        //note there's no need to continue recursion if the current outline parent is root (first condition) or
        // if it is already in the Set of outlines to be copied (second condition)
        if (parent.getTitle().equals("Outlines") || outlinesToCopy.contains(parent)) {
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
    private void cloneOutlines(Set<PdfOutline> outlinesToCopy, PdfOutline newParent, PdfOutline oldParent, Map<PdfPage, PdfPage> page2page, PdfDocument toDocument) {
        if (null == oldParent) {
            return;
        }
        for (PdfOutline outline : oldParent.getAllChildren()) {
            if (outlinesToCopy.contains(outline)) {
                PdfObject destObjToCopy = outline.getDestination().getPdfObject();
                PdfDestination copiedDest = getCatalog().copyDestination(destObjToCopy, page2page, toDocument);
                PdfOutline child = newParent.addOutline(outline.getTitle());
                if (copiedDest != null) {
                    child.addDestination(copiedDest);
                }

                cloneOutlines(outlinesToCopy, child, outline, page2page, toDocument);
            }
        }
    }

    private void ensureTreeRootAddedToNames(PdfObject treeRoot, PdfName treeType) {
        PdfDictionary names = catalog.getPdfObject().getAsDictionary(PdfName.Names);
        if (names == null) {
            names = new PdfDictionary();
            catalog.getPdfObject().put(PdfName.Names, names);
            names.makeIndirect(this);
        }
        names.put(treeType, treeRoot);
    }

    private static boolean isXmpMetaHasProperty(XMPMeta xmpMeta, String schemaNS, String propName) throws XMPException {
        return xmpMeta.getProperty(schemaNS, propName) != null;
    }

    @SuppressWarnings("unused")
    private byte[] getSerializedBytes() {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            Logger logger = LoggerFactory.getLogger(PdfDocument.class);
            logger.warn(LogMessageConstant.DOCUMENT_SERIALIZATION_EXCEPTION_RAISED, e);
            return null;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ignored) {
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private long incrementDocumentId() {
        return lastDocumentId.incrementAndGet();
    }

    private long getDocumentId() {
        return documentId;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (tagStructureContext != null) {
            LoggerFactory.getLogger(getClass()).warn(LogMessageConstant.TAG_STRUCTURE_CONTEXT_WILL_BE_REINITIALIZED_ON_SERIALIZATION);
        }
        out.defaultWriteObject();
    }

    /**
     * A structure storing documentId, object number and generation number. This structure is using to calculate
     * an unique object key during the copy process.
     */
    static class IndirectRefDescription {
        private long docId;
        private int objNr;
        private int genNr;

        IndirectRefDescription(PdfIndirectReference reference) {
            this.docId = reference.getDocument().getDocumentId();
            this.objNr = reference.getObjNumber();
            this.genNr = reference.getGenNumber();
        }

        @Override
        public int hashCode() {
            int result = (int) docId;
            result *= 31;
            result += objNr;
            result *= 31;
            result += genNr;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IndirectRefDescription that = (IndirectRefDescription) o;

            return docId == that.docId && objNr == that.objNr && genNr == that.genNr;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        eventDispatcher = new EventDispatcher();
    }

    private String addModifiedPostfix(String producer) {
        Version version = Version.getInstance();
        if (producer == null || !version.getVersion().contains(version.getProduct())) {
            return version.getVersion();
        } else {
            int idx = producer.indexOf("; modified using");
            StringBuilder buf;
            if (idx == -1) {
                buf = new StringBuilder(producer);
            } else {
                buf = new StringBuilder(producer.substring(0, idx));
            }
            buf.append("; modified using ");
            buf.append(version.getVersion());
            return buf.toString();
        }
    }
}
