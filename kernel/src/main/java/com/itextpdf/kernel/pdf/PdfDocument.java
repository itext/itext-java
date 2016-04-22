/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.Version;
import com.itextpdf.kernel.crypto.BadPasswordException;
import com.itextpdf.kernel.events.EventDispatcher;
import com.itextpdf.kernel.events.IEventDispatcher;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.numbering.EnglishAlphabetNumbering;
import com.itextpdf.kernel.numbering.RomanNumbering;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLinkAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.kernel.pdf.navigation.PdfStringDestination;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.xmp.*;
import com.itextpdf.kernel.xmp.options.PropertyOptions;
import com.itextpdf.kernel.xmp.options.SerializeOptions;

import java.io.*;
import java.util.*;

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
     * Document version. 1.7 by default.
     */
    protected PdfVersion pdfVersion = PdfVersion.PDF_1_7;

    /**
     * List of indirect objects used in the document.
     */
    final PdfXrefTable xref = new PdfXrefTable();

    /**
     * Indicate incremental updates mode of stamping mode.
     */
    protected final boolean appendMode;

    protected PdfStructTreeRoot structTreeRoot;

    protected int structParentIndex = -1;
    protected boolean userProperties;

    protected boolean closeReader = true;
    protected boolean closeWriter = true;

    protected boolean isClosing = false;

    protected boolean closed = false;


    /**
    * flag determines whether to write unused objects to result document
    */
    protected boolean flushUnusedObjects = false;

    protected Set<PdfFont> documentFonts = new HashSet<>();

    protected TagStructureContext tagStructureContext;

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
        this.reader = reader;
        this.appendMode = false;
        open(null);
    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     *
     * @param writer PDF writer
     */
    public PdfDocument(PdfWriter writer) {
        this(writer, PdfVersion.PDF_1_7);
    }

    /**
     * Open PDF document in writing mode.
     * Document has no pages when initialized.
     *
     * @param writer     PDF writer
     * @param pdfVersion pdf version of the resultant document
     */
    public PdfDocument(PdfWriter writer, PdfVersion pdfVersion) {
        if (writer == null) {
            throw new NullPointerException("writer");
        }
        this.writer = writer;
        this.appendMode = false;
        this.pdfVersion = pdfVersion;
        open(pdfVersion);
    }

    /**
     * Open PDF document in stamping mode.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     * @param append if true, incremental updates will
     */
    public PdfDocument(PdfReader reader, PdfWriter writer, boolean append) {
        this(reader, writer, append, null);
    }

    /**
     * Opens PDF document in the stamping mode.
     * <br/>
     * Note: to enable append mode use {@link #PdfDocument(PdfReader, PdfWriter, boolean)} instead.
     *
     * @param reader PDF reader.
     * @param writer PDF writer.
     */
    public PdfDocument(PdfReader reader, PdfWriter writer) {
        this(reader, writer, false);
    }

    /**
     * Opens PDF document in the stamping mode.
     * <br/>
     * Note: to enable append mode use {@link #PdfDocument(PdfReader, PdfWriter, boolean)} instead.
     *
     * @param reader        PDF reader.
     * @param writer        PDF writer.
     * @param newPdfVersion the pdf version of the resultant file.
     */
    public PdfDocument(PdfReader reader, PdfWriter writer, PdfVersion newPdfVersion) {
        this(reader, writer, false, newPdfVersion);
    }

    /**
     * Open PDF document in stamping mode.
     *
     * @param reader        PDF reader.
     * @param writer        PDF writer.
     * @param append        if true, incremental updates will
     * @param newPdfVersion the pdf version of the resultant file, or null to leave it as is.
     */
    public PdfDocument(PdfReader reader, PdfWriter writer, boolean append, PdfVersion newPdfVersion) {
        if (reader == null) {
            throw new NullPointerException("reader");
        }
        if (writer == null) {
            throw new NullPointerException("writer");
        }
        this.reader = reader;
        this.writer = writer;
        this.appendMode = append;

        open(newPdfVersion);
    }

    /**
     * Use this method to set the XMP Metadata.
     *
     * @param xmpMetadata The xmpMetadata to set.
     */
    protected void setXmpMetadata(final byte[] xmpMetadata) {
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

    public void createXmpMetadata() throws XMPException {
        checkClosingStatus();
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
        if (isTagged()) {
            xmpMeta.setPropertyInteger(XMPConst.NS_PDFUA_ID, XMPConst.PART, 1, new PropertyOptions(PropertyOptions.SEPARATE_NODE));
        }
        setXmpMetadata(xmpMeta);
    }

    public byte[] getXmpMetadata() {
        return xmpMetadata;
    }

    public PdfObject getPdfObject(final int objNum) {
        checkClosingStatus();
        PdfIndirectReference reference = xref.get(objNum);
        if (reference == null) {
            return null;
        } else {
            return reference.getRefersTo();
        }
    }

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
        return catalog.getPage(pageNum);
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
        catalog.addPage(page);
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
        catalog.addPage(index, page);
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
        catalog.addPage(page);
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
        catalog.addPage(index, page);
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
        return catalog.getNumberOfPages();
    }

    /**
     * Gets page number by page.
     *
     * @param page the page.
     * @return page number.
     */
    public int getPageNumber(PdfPage page) {
        checkClosingStatus();
        return catalog.getPageNumber(page);
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
        if (pageNum < 1)
            return false;
        return removePage(pageNum) != null;
    }

    /**
     * Removes page from the document by page number.
     *
     * @param pageNum the one-based index of the PdfPage to be removed
     * @return the page that was removed from the list
     */
    public PdfPage removePage(int pageNum) {
        checkClosingStatus();
        PdfPage removedPage = catalog.removePage(pageNum);

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

    @Override
    public void addEventHandler(String type, IEventHandler handler) {
        eventDispatcher.addEventHandler(type, handler);
    }

    @Override
    public void dispatchEvent(com.itextpdf.kernel.events.Event event) {
        eventDispatcher.dispatchEvent(event);
    }

    @Override
    public void dispatchEvent(com.itextpdf.kernel.events.Event event, boolean delayed) {
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
        return appendMode;
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
    public void close() {
        if (closed) {
            return;
        }
        isClosing = true;
        try {
            if (writer != null) {
                if (catalog.isFlushed())
                    throw new PdfException(PdfException.CannotCloseDocumentWithAlreadyFlushedPdfCatalog);
                if (xmpMetadata != null) {
                    PdfStream xmp = new PdfStream().makeIndirect(this);
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
                checkIsoConformance();
                PdfObject crypto = null;
                if (appendMode) {
                    if (structTreeRoot != null && structTreeRoot.getPdfObject().isModified()) {
                        getTagStructureContext().removeAllConnectionsToTags();
                        structTreeRoot.flush();
                    }
                    if (catalog.isOCPropertiesMayHaveChanged() && catalog.getOCProperties(false).getPdfObject().isModified()) {
                        catalog.getOCProperties(false).flush();
                    }
                    if (catalog.pageLabels != null) {
                        catalog.put(PdfName.PageLabels, catalog.pageLabels.buildTree());
                    }

                    PdfObject pageRoot = catalog.pageTree.generateTree();
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
                        assert reader.getCryptoDict() == writer.crypto.getPdfObject() : "Conflict with source encryption";
                        crypto = reader.getCryptoDict();
                    }
                } else {
                    if (structTreeRoot != null) {
                        getTagStructureContext().removeAllConnectionsToTags();
                        structTreeRoot.flush();
                    }
                    if (catalog.isOCPropertiesMayHaveChanged()) {
                        catalog.getPdfObject().put(PdfName.OCProperties, catalog.getOCProperties(false).getPdfObject());
                        catalog.getOCProperties(false).flush();
                    }
                    if (catalog.pageLabels != null) {
                        catalog.put(PdfName.PageLabels, catalog.pageLabels.buildTree());
                    }

                    catalog.getPdfObject().put(PdfName.Pages, catalog.pageTree.generateTree());

                    for (Map.Entry<PdfName, PdfNameTree> entry : catalog.nameTrees.entrySet()) {
                        PdfNameTree tree = entry.getValue();
                        if (tree.isModified()) {
                            ensureTreeRootAddedToNames(tree.buildTree().makeIndirect(this), entry.getKey());
                        }
                    }

                    for(int pageNum = 1; pageNum <= getNumberOfPages(); pageNum++) {
                        getPage(pageNum).flush();
                    }
                    catalog.getPdfObject().flush(false);
                    info.flush();
                    flushFonts();
                    writer.flushWaitingObjects();
                    // flush unused objects
                    if (flushUnusedObjects) {
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
                // if originalFIleID comes from crypto, it means that no need in checking modified state.
                // For crypto purposes new documentId always generated.
                fileId = PdfEncryption.createInfoId(originalFileID, isModified);

                // The following two operators prevents the possible inconsistency between root and info
                // entries existing in the trailer object and corresponding fields. This inconsistency
                // may appear when user gets trailer and explicitly sets new root or info dictionaries.
                trailer.put(PdfName.Root, catalog.getPdfObject());
                trailer.put(PdfName.Info, info.getPdfObject());

                xref.writeXrefTableAndTrailer(this, fileId, crypto);
                writer.flush();
                if (isCloseWriter()) {
                    writer.close();
                }
            }
            catalog.pageTree.clearPageRefs();
            removeAllHandlers();
            if (reader != null && isCloseReader()) {
                reader.close();
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotCloseDocument, e, this);
        }
        closed = true;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isTagged() {
        return structTreeRoot != null;
    }

    public void setTagged() {
        checkClosingStatus();
        if (structTreeRoot == null) {
            structTreeRoot = new PdfStructTreeRoot(this);
            catalog.getPdfObject().put(PdfName.StructTreeRoot, structTreeRoot.getPdfObject());
            catalog.getPdfObject().put(PdfName.MarkInfo, new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
                put(PdfName.Marked, PdfBoolean.TRUE);
                if (userProperties) {
                    put(PdfName.UserProperties, new PdfBoolean(true));
                }
            }}));
            structParentIndex = 0;
        }
    }

    public PdfStructTreeRoot getStructTreeRoot() {
        return structTreeRoot;
    }

    public Integer getNextStructParentIndex() {
        return structParentIndex++;
    }

    /**
     * Gets document {@code TagStructureContext}.
     * The document must be tagged, otherwise an exception will be thrown.
     * @return document {@code TagStructureContext}.
     */
    public TagStructureContext getTagStructureContext() {
        checkClosingStatus();
        if (tagStructureContext != null) {
            return tagStructureContext;
        }

        if (!isTagged()) {
            throw new PdfException(PdfException.MustBeATaggedDocument);
        }

        tagStructureContext = new TagStructureContext(this);
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
     * @throws PdfException
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument, int insertBeforePage) {
        return copyPagesTo(pageFrom, pageTo, toDocument, insertBeforePage, null);
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
     * @param copier           a copier which bears a special copy logic. May be NULL
     * @return list of copied pages
     * @throws PdfException
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
     * @param pageFrom
     * @param pageTo
     * @param toDocument
     * @return list of copied pages
     * @throws PdfException
     */
    public List<PdfPage> copyPagesTo(int pageFrom, int pageTo, PdfDocument toDocument) {
        return copyPagesTo(pageFrom, pageTo, toDocument, null);
    }

    /**
     * Copies a range of pages from current document to {@code toDocument} appending copied pages to the end.
     * Use this method if you want to copy pages across tagged documents.
     * This will keep resultant PDF structure consistent.
     *
     * @param pageFrom
     * @param pageTo
     * @param toDocument
     * @param copier a copier which bears a special copy logic. May be NULL
     * @return list of copied pages
     * @throws PdfException
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
     * @return list of copied pages
     * @throws PdfException
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
     * @return list of copied pages
     * @throws PdfException
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument, int insertBeforePage, IPdfPageExtraCopier copier) {
        if (pagesToCopy.isEmpty()) {
            return Collections.emptyList();
        }

        checkClosingStatus();
        List<PdfPage> copiedPages = new ArrayList<>();
        Map<PdfPage, PdfPage> page2page = new LinkedHashMap<>();
        Set<PdfOutline> outlinesToCopy = new HashSet<>();

        List<Map<PdfPage, PdfPage>> rangesOfPagesWithIncreasingNumbers = new ArrayList<>();
        int lastCopiedPageNum = pagesToCopy.get(0);

        int pageInsertIndex = insertBeforePage;
        boolean insertInBetween = insertBeforePage < toDocument.getNumberOfPages() + 1;
        for (Integer pageNum : pagesToCopy) {
            PdfPage page = getPage(pageNum);
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
            if (toDocument.getCatalog().isOutlineMode()) {
                List<PdfOutline> pageOutlines = page.getOutlines(false);
                if (pageOutlines != null)
                    outlinesToCopy.addAll(pageOutlines);
            }
            lastCopiedPageNum = pageNum;
        }

        copyLinkAnnotations(toDocument, page2page);

        // It's important to copy tag structure after link annotations were copied, because object content items in tag
        // structure are not copied in case if their's OBJ key is annotation and doesn't contain /P entry.
        if (toDocument.isTagged()) {
            if (tagStructureContext != null) {
                tagStructureContext.actualizeTagsProperties();
            }
            for (Map<PdfPage, PdfPage> increasingPagesRange : rangesOfPagesWithIncreasingNumbers) {
                if (insertInBetween) {
                    getStructTreeRoot().copyTo(toDocument, insertBeforePage, increasingPagesRange);
                } else {
                    getStructTreeRoot().copyTo(toDocument, increasingPagesRange);
                }
                insertBeforePage += increasingPagesRange.size();
            }
            toDocument.getTagStructureContext().normalizeDocumentRootTag();
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
     * @throws PdfException
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
     * @throws PdfException
     */
    public List<PdfPage> copyPagesTo(List<Integer> pagesToCopy, PdfDocument toDocument, IPdfPageExtraCopier copier) {
        return copyPagesTo(pagesToCopy, toDocument, toDocument.getNumberOfPages() + 1, copier);
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

    public boolean isFlushUnusedObjects() {
        return flushUnusedObjects;
    }

    public void setFlushUnusedObjects(boolean flushUnusedObjects) {
        checkClosingStatus();
        this.flushUnusedObjects = flushUnusedObjects;
    }

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
     *              See PdfSpec 12.3.2.3 for more info.
     * @throws PdfException
     */
    public void addNameDestination(String key, PdfObject value) {
        checkClosingStatus();
        catalog.addNamedDestination(key, value);
    }

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

    public void checkIsoConformance(Object obj, IsoKey key) {
    }

    public void checkIsoConformance(Object obj, IsoKey key, PdfResources resources) {
    }

    public void checkShowTextIsoConformance(Object gState, PdfResources resources) {
    }

    public void addFileAttachment(String description, byte[] fileStore, String fileDisplay, PdfName mimeType, PdfDictionary fileParameter, PdfName afRelationshipValue) {
        addFileAttachment(description, PdfFileSpec.createEmbeddedFileSpec(this, fileStore, description, fileDisplay, mimeType, fileParameter, afRelationshipValue, true));
    }

    public void addFileAttachment(String description, String file, String fileDisplay, PdfName mimeType, PdfName afRelationshipValue) throws FileNotFoundException {
        addFileAttachment(description, PdfFileSpec.createEmbeddedFileSpec(this, file, description, fileDisplay, mimeType, afRelationshipValue, true));
    }

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
     * @return {@code true}, if there are outlines and {@code false} otherwise.
     */
    public boolean hasOutlines() {
        return catalog.hasOutlines();
    }

    /**
     * Sets the flag indicating the presence of structure elements that contain user properties attributes.
     * @param userProperties the user properties flag
     */
    public void setUserProperties(boolean userProperties) {
        this.userProperties = userProperties;
    }

    protected void storeLinkAnnotation(PdfPage page, PdfLinkAnnotation annotation) {
        List<PdfLinkAnnotation> pageAnnotations = linkAnnotations.get(page);
        if (pageAnnotations == null) {
            pageAnnotations = new ArrayList<>();
            linkAnnotations.put(page, pageAnnotations);
        }
        pageAnnotations.add(annotation);
    }

    protected void checkIsoConformance() {
    }

    protected void markObjectAsMustBeFlushed(PdfObject pdfObject){
        if (pdfObject.isIndirect()) {
            pdfObject.getIndirectReference().setState(PdfObject.MUST_BE_FLUSHED);
        }
    }

    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        writer.flushObject(pdfObject, canBeInObjStm);
    }

    /**
     * Initializes document.
     *
     * @param newPdfVersion new pdf version of the resultant file if stamper is used and the version needs to be changed,
     *                      or {@code null} otherwise
     * @throws PdfException
     */
    protected void open(PdfVersion newPdfVersion) {
        try {
            if (reader != null) {
                reader.pdfDocument = this;
                reader.readPdf();
                pdfVersion = reader.pdfVersion;
                trailer = new PdfDictionary(reader.trailer);
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
                    } catch (XMPException ignored) {}
                }
                PdfObject infoDict = trailer.get(PdfName.Info, true);
                info = new PdfDocumentInfo(infoDict instanceof PdfDictionary ?
                        (PdfDictionary) infoDict : new PdfDictionary(), this);

                PdfDictionary str = catalog.getPdfObject().getAsDictionary(PdfName.StructTreeRoot);
                if (str != null) {
                    structTreeRoot = new PdfStructTreeRoot(str);
                    structParentIndex = getStructTreeRoot().getParentTreeNextKey();
                }
                if (appendMode && (reader.hasRebuiltXref() || reader.hasFixedXref()))
                    throw new PdfException(PdfException.AppendModeRequiresADocumentWithoutErrorsEvenIfRecoveryWasPossible);
            }
            if (writer != null) {
                if (reader != null && reader.xrefStm && writer.fullCompression == null) {
                    writer.setFullCompression(true);
                }
                if (reader != null && !reader.isOpenedWithFullPermission()) {
                    throw new BadPasswordException(BadPasswordException.PdfReaderNotOpenedWithOwnerPassword);
                }
                writer.document = this;
                if (reader == null) {
                    catalog = new PdfCatalog(this);
                    info = new PdfDocumentInfo(this).addCreationDate();
                    info.addModDate();
                    info.setProducer(Version.getInstance().getVersion());
                } else {
                    info.addModDate();
                }
                trailer = new PdfDictionary();
                trailer.put(PdfName.Root, catalog.getPdfObject().getIndirectReference());
                trailer.put(PdfName.Info, info.getPdfObject().getIndirectReference());
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

                if (writer.crypto != null) {
                    // TODO log that writer crypto will be ignored
                }
                writer.crypto = reader.decrypt;

                if (newPdfVersion != null) {
                    // In PDF 1.4, a PDF version can also be specified in the Version entry of the document catalog,
                    // essentially updating the version associated with the file by overriding the one specified in the file header
                    if (pdfVersion.compareTo(PdfVersion.PDF_1_4) >= 0) {
                        // If the header specifies a later version, or if this entry is absent, the document conforms to the
                        // version specified in the header.

                        // So only update the version if it is older than the one in the header
                        if (newPdfVersion.compareTo(reader.pdfVersion) > 0) {
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
     * Gets list of indirect references.
     *
     * @return list of indirect references.
     */
    PdfXrefTable getXref() {
        return xref;
    }

    /**
     * List all newly added or loaded fonts
     *
     * @return List of {@see PdfFonts}.
     */
    protected Set<PdfFont> getDocumentFonts() {
        return documentFonts;
    }

    protected void flushFonts() {
        if (appendMode) {
            for (PdfFont font : getDocumentFonts()) {
                if (font.getPdfObject().getIndirectReference().checkState(PdfObject.MODIFIED)) {
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
     * checks whether a method is invoked at the closed document
     * @throws PdfException
     */
    protected void checkClosingStatus() {
        if (closed) {
            throw new PdfException(PdfException.DocumentClosedImpossibleExecuteAction);
        }
    }

    /**
     * This method removes all annotation entries from form fields associated with a given page.
     * @param page
     */
    private void removeUnusedWidgetsFromFields(PdfPage page){
        if (page.isFlushed()) {
            return;
        }
        List<PdfAnnotation> annots = page.getAnnotations();
        for (PdfAnnotation annot : annots) {
            if (annot.getSubtype().equals(PdfName.Widget)) {
                ((PdfWidgetAnnotation)annot).releaseFormFieldFromWidgetAnnotation();
            }
        }
    }

    private void copyLinkAnnotations(PdfDocument toDocument, Map<PdfPage, PdfPage> page2page) {
        List<PdfName> excludedKeys = new ArrayList<>();
        excludedKeys.add(PdfName.Dest);
        excludedKeys.add(PdfName.A);
        for (Map.Entry<PdfPage, List<PdfLinkAnnotation>> entry : linkAnnotations.entrySet()) {
            // We don't want to copy those link annotations, which reference to not copied pages.
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
                    PdfLinkAnnotation newAnnot = (PdfLinkAnnotation) PdfAnnotation.makeAnnotation(annot.getPdfObject().copyTo(toDocument, excludedKeys, false));
                    if (copiedDest != null) {
                        newAnnot.setDestination(copiedDest);
                    }
                    if (copiedAction != null) {
                        newAnnot.setAction(copiedAction);
                    }
                    page2page.get(entry.getKey()).addAnnotation(-1, newAnnot, false);
                }
            }
        }
        linkAnnotations.clear();
    }

    /**
     * This method copies all given outlines
     *
     * @param outlines      outlines to be copied
     * @param toDocument    document where outlines should be copied
     * @throws PdfException
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
        if (parent.getTitle().equals("Outlines") || !outlinesToCopy.add(parent))
            return;
        getAllOutlinesToCopy(parent, outlinesToCopy);
    }

    /**
     * This method copies create new outlines in the Document to copy.
     *
     * @param outlinesToCopy - Set of outlines to be copied
     * @param newParent      - new parent outline
     * @param oldParent      - old parent outline
     * @throws PdfException
     */
    private void cloneOutlines(Set<PdfOutline> outlinesToCopy, PdfOutline newParent, PdfOutline oldParent, Map<PdfPage, PdfPage> page2page, PdfDocument toDocument) {
        if (null == oldParent) {
            return;
        }
        Set<String> names = toDocument.getCatalog().getNameTree(PdfName.Dests).getNames().keySet();
        Map<String, PdfObject> srcNamedDestinations = catalog.getNameTree(PdfName.Dests).getNames();
        for (PdfOutline outline : oldParent.getAllChildren()) {
            if (outlinesToCopy.contains(outline)) {
                PdfDestination dest = outline.getDestination();
                if (dest instanceof PdfStringDestination) {
                    String name = ((PdfString)dest.getPdfObject()).toUnicodeString();
                    if (!names.contains(name)) {
                        PdfArray array = new PdfArray();
                        array.addAll((PdfArray) srcNamedDestinations.get(name));
                        PdfObject pageObject = array.get(0);
                        if (!pageObject.isNumber()) {
                            PdfPage oldPage = catalog.getPage((PdfDictionary)pageObject);
                            PdfPage newPage = page2page.get(oldPage);
                            if (oldPage == null || newPage == null) {
                                dest = null;
                            } else {
                                array.set(0, newPage.getPdfObject());
                            }
                        }
                        if (dest != null) {
                            toDocument.addNameDestination(name, array.makeIndirect(toDocument));
                        }
                    }
                } else {
                    PdfArray destArray = new PdfArray();
                    destArray.addAll((PdfArray) dest.getPdfObject());
                    PdfObject pageObject = destArray.get(0);
                    if (!pageObject.isNumber()) {
                        PdfPage oldPage = catalog.getPage((PdfDictionary)pageObject);
                        PdfPage newPage = page2page.get(oldPage);
                        if (oldPage == null || newPage == null) {
                            dest = null;
                        } else {
                            destArray.set(0, newPage.getPdfObject());
                            dest = new PdfExplicitDestination(destArray);
                        }
                    }
                }
                PdfOutline child = newParent.addOutline(outline.getTitle());
                if (dest != null) {
                    child.addDestination(dest);
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
            e.printStackTrace();
            return null;
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
