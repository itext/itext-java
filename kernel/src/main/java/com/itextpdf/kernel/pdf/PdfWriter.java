/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.mac.AbstractMacIntegrityProtector;
import com.itextpdf.kernel.mac.IMacContainerLocator;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.utils.ICopyFilter;
import com.itextpdf.kernel.utils.NullCopyFilter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes the PDF to the specified output. Writing can be customized using {@link WriterProperties}.
 */
public class PdfWriter extends PdfOutputStream {
    private static final byte[] OBJ = ByteUtils.getIsoBytes(" obj\n");
    private static final byte[] ENDOBJ = ByteUtils.getIsoBytes("\nendobj\n");

    protected WriterProperties properties;
    //forewarned is forearmed
    protected boolean isUserWarnedAboutAcroFormCopying;
    /**
     * Currently active object stream.
     * Objects are written to the object stream if fullCompression set to true.
     */
    PdfObjectStream objectStream = null;
    /**
     * Is used to avoid duplications on object copying.
     * It stores hashes of the indirect reference from the source document and the corresponding
     * indirect references of the copied objects from the new document.
     */
    private final Map<PdfIndirectReference, PdfIndirectReference> copiedObjects = new LinkedHashMap<>();
    /**
     * Is used in smart mode to serialize and store serialized objects content.
     */
    private final SmartModePdfObjectsSerializer smartModeSerializer = new SmartModePdfObjectsSerializer();
    private OutputStream originalOutputStream;

    /**
     * Create a PdfWriter writing to the passed File and with default writer properties.
     *
     * @param file File to write to.
     *
     * @throws FileNotFoundException if the file exists but is a directory
     *                               rather than a regular file, does not exist but cannot
     *                               be created, or cannot be opened for any other reason
     */
    public PdfWriter(java.io.File file) throws IOException {
        this(file.getAbsolutePath());
    }

    /**
     * Create a PdfWriter writing to the passed outputstream and with default writer properties.
     *
     * @param os Outputstream to write to.
     */
    public PdfWriter(java.io.OutputStream os) {
        this(os, new WriterProperties());
    }

    /**
     * Creates {@link PdfWriter} instance, which writes to the passed {@link OutputStream},
     * using provided {@link WriterProperties}.
     *
     * @param os {@link OutputStream} in which writing should happen
     * @param properties {@link WriterProperties} to be used during the writing
     */
    public PdfWriter(java.io.OutputStream os, WriterProperties properties) {
        super(new CountOutputStream(FileUtil.wrapWithBufferedOutputStream(os)));
        this.properties = properties;
    }

    /**
     * Create a PdfWriter writing to the passed filename and with default writer properties.
     *
     * @param filename filename of the resulting pdf.
     *
     * @throws FileNotFoundException if the file exists but is a directory
     *                               rather than a regular file, does not exist but cannot
     *                               be created, or cannot be opened for any other reason
     */
    public PdfWriter(String filename) throws IOException {
        this(filename, new WriterProperties());
    }

    /**
     * Create a PdfWriter writing to the passed filename and using the passed writer properties.
     *
     * @param filename   filename of the resulting pdf.
     * @param properties writerproperties to use.
     *
     * @throws FileNotFoundException if the file exists but is a directory
     *                               rather than a regular file, does not exist but cannot
     *                               be created, or cannot be opened for any other reason
     */
    public PdfWriter(String filename, WriterProperties properties) throws IOException {
        this(FileUtil.getBufferedOutputStream(filename), properties);
    }

    /**
     * Indicates if to use full compression mode.
     *
     * @return true if to use full compression, false otherwise.
     */
    public boolean isFullCompression() {
        return properties.isFullCompression != null && (boolean) properties.isFullCompression;
    }

    /**
     * Gets default compression level for @see PdfStream.
     * For more details @see {@link com.itextpdf.io.source.DeflaterOutputStream}.
     *
     * @return compression level.
     */
    public int getCompressionLevel() {
        return properties.compressionLevel;
    }

    /**
     * Sets default compression level for @see PdfStream.
     * For more details @see {@link com.itextpdf.io.source.DeflaterOutputStream}.
     *
     * @param compressionLevel compression level.
     *
     * @return this {@link PdfWriter} instance
     */
    public PdfWriter setCompressionLevel(int compressionLevel) {
        this.properties.setCompressionLevel(compressionLevel);
        return this;
    }

    /**
     * Gets defined pdf version for the document.
     *
     * @return version for the document
     */
    public PdfVersion getPdfVersion() {
        return properties.pdfVersion;
    }

    /**
     * Gets the writer properties.
     *
     * @return The {@link WriterProperties} of the current PdfWriter.
     */
    public WriterProperties getProperties() {
        return properties;
    }

    /**
     * Sets the smart mode.
     * <br>
     * In smart mode when resources (such as fonts, images,...) are
     * encountered, a reference to these resources is saved
     * in a cache, so that they can be reused.
     * This requires more memory, but reduces the file size
     * of the resulting PDF document.
     *
     * @param smartMode True for enabling smart mode.
     *
     * @return this {@link PdfWriter} instance
     */
    public PdfWriter setSmartMode(boolean smartMode) {
        this.properties.smartMode = smartMode;
        return this;
    }

    /**
     * Initializes {@link PdfEncryption} object if any encryption is specified in {@link WriterProperties}.
     *
     * @param version {@link PdfVersion} version of the document in question
     */
    protected void initCryptoIfSpecified(PdfVersion version) {
        EncryptionProperties encryptProps = properties.encryptionProperties;
        // Suppress MAC properties for PDF version < 2.0 and old deprecated encryption algorithms
        // if default ones have been passed to WriterProperties
        final int encryptionAlgorithm = crypto == null ?
                (encryptProps.encryptionAlgorithm & EncryptionConstants.ENCRYPTION_MASK) :
                crypto.getEncryptionAlgorithm();
        if (document.properties.disableMac) {
            encryptProps.macProperties = null;
        }
        if (encryptProps.macProperties == EncryptionProperties.DEFAULT_MAC_PROPERTIES) {
            if (version == null || version.compareTo(PdfVersion.PDF_2_0) < 0 ||
                    encryptionAlgorithm < EncryptionConstants.ENCRYPTION_AES_256) {
                encryptProps.macProperties = null;
            }
        }

        AbstractMacIntegrityProtector mac = encryptProps.macProperties == null ? null : document.getDiContainer()
                .getInstance(IMacContainerLocator.class)
                .createMacIntegrityProtector(document, encryptProps.macProperties);
        if (properties.isStandardEncryptionUsed()) {
            crypto = new PdfEncryption(encryptProps.userPassword, encryptProps.ownerPassword,
                    encryptProps.standardEncryptPermissions,
                    encryptProps.encryptionAlgorithm,
                    ByteUtils.getIsoBytes(this.document.getOriginalDocumentId().getValue()),
                    version, mac);
        } else if (properties.isPublicKeyEncryptionUsed()) {
            crypto = new PdfEncryption(encryptProps.publicCertificates, encryptProps.publicKeyEncryptPermissions,
                    encryptProps.encryptionAlgorithm, version, mac);
        }
    }

    /**
     * Flushes the object. Override this method if you want to define custom behaviour for object flushing.
     *
     * @param pdfObject     object to flush.
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     */
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) {
        PdfIndirectReference indirectReference = pdfObject.getIndirectReference();
        if (isFullCompression() && canBeInObjStm) {
            PdfObjectStream objectStream = getObjectStream();
            objectStream.addObject(pdfObject);
        } else {
            indirectReference.setOffset(getCurrentPos());
            writeToBody(pdfObject);
        }
        indirectReference.setState(PdfObject.FLUSHED).clearState(PdfObject.MUST_BE_FLUSHED);
        switch (pdfObject.getType()) {
            case PdfObject.BOOLEAN:
            case PdfObject.NAME:
            case PdfObject.NULL:
            case PdfObject.NUMBER:
            case PdfObject.STRING:
                ((PdfPrimitiveObject) pdfObject).content = null;
                break;
            case PdfObject.ARRAY:
                PdfArray array = ((PdfArray) pdfObject);
                markArrayContentToFlush(array);
                array.releaseContent();
                break;
            case PdfObject.STREAM:
            case PdfObject.DICTIONARY:
                PdfDictionary dictionary = ((PdfDictionary) pdfObject);
                markDictionaryContentToFlush(dictionary);
                dictionary.releaseContent();
                break;
            case PdfObject.INDIRECT_REFERENCE:
                markObjectToFlush(((PdfIndirectReference) pdfObject).getRefersTo(false));
        }
    }

    /**
     * Copies a PdfObject either stand alone or as part of the PdfDocument passed as documentTo.
     *
     * @param obj              object to copy
     * @param documentTo       optional target document
     * @param allowDuplicating allow that some objects will become duplicated by this action
     *
     * @return the copies object
     */
    protected PdfObject copyObject(PdfObject obj, PdfDocument documentTo, boolean allowDuplicating) {
        return copyObject(obj, documentTo, allowDuplicating, NullCopyFilter.getInstance());
    }

    /**
     * Copies a PdfObject either stand alone or as part of the PdfDocument passed as documentTo.
     *
     * @param obj              object to copy
     * @param documentTo       optional target document
     * @param allowDuplicating allow that some objects will become duplicated by this action
     * @param copyFilter       {@link  ICopyFilter} a filter to apply while copying arrays and dictionaries
     *                         *             Use {@link NullCopyFilter} for no filtering
     *
     * @return the copies object
     */
    protected PdfObject copyObject(PdfObject obj, PdfDocument documentTo, boolean allowDuplicating,
            ICopyFilter copyFilter) {
        if (obj instanceof PdfIndirectReference) {
            obj = ((PdfIndirectReference) obj).getRefersTo();
        }
        if (obj == null) {
            obj = PdfNull.PDF_NULL;
        }
        if (checkTypeOfPdfDictionary(obj, PdfName.Catalog)) {
            Logger logger = LoggerFactory.getLogger(PdfReader.class);
            logger.warn(IoLogMessageConstant.MAKE_COPY_OF_CATALOG_DICTIONARY_IS_FORBIDDEN);
            obj = PdfNull.PDF_NULL;
        }

        PdfIndirectReference indirectReference = obj.getIndirectReference();
        boolean tryToFindDuplicate = !allowDuplicating && indirectReference != null;

        if (tryToFindDuplicate) {
            PdfIndirectReference copiedIndirectReference = copiedObjects.get(indirectReference);
            if (copiedIndirectReference != null) {
                return copiedIndirectReference.getRefersTo();
            }
        }

        SerializedObjectContent serializedContent = null;
        if (properties.smartMode && tryToFindDuplicate && !checkTypeOfPdfDictionary(obj, PdfName.Page) &&
                !checkTypeOfPdfDictionary(obj, PdfName.OCG) && !checkTypeOfPdfDictionary(obj, PdfName.OCMD)) {
            serializedContent = smartModeSerializer.serializeObject(obj);
            PdfIndirectReference objectRef = smartModeSerializer.getSavedSerializedObject(serializedContent);
            if (objectRef != null) {
                copiedObjects.put(indirectReference, objectRef);
                return objectRef.refersTo;
            }
        }

        PdfObject newObject = obj.newInstance();
        if (indirectReference != null) {
            PdfIndirectReference indRef = newObject.makeIndirect(documentTo).getIndirectReference();
            if (serializedContent != null) {
                smartModeSerializer.saveSerializedObject(serializedContent, indRef);
            }
            copiedObjects.put(indirectReference, indRef);
        }
        newObject.copyContent(obj, documentTo, copyFilter);

        return newObject;
    }

    /**
     * Writes object to body of PDF document.
     *
     * @param pdfObj object to write.
     */
    protected void writeToBody(PdfObject pdfObj) {
        if (crypto != null) {
            crypto.setHashKeyForNextObject(pdfObj.getIndirectReference().getObjNumber(),
                    pdfObj.getIndirectReference().getGenNumber());
        }
        writeInteger(pdfObj.getIndirectReference().getObjNumber()).
                writeSpace().
                writeInteger(pdfObj.getIndirectReference().getGenNumber()).writeBytes(OBJ);
        write(pdfObj);
        writeBytes(ENDOBJ);
    }

    /**
     * Writes PDF header.
     */
    protected void writeHeader() {
        writeByte('%').
                writeString(document.getPdfVersion().toString()).
                writeString("\n%\u00e2\u00e3\u00cf\u00d3\n");
    }

    /**
     * Flushes all objects which have not been flushed yet.
     *
     * @param forbiddenToFlush a {@link Set} of {@link PdfIndirectReference references} that are forbidden to be flushed
     *                         automatically.
     */
    protected void flushWaitingObjects(Set<PdfIndirectReference> forbiddenToFlush) {
        PdfXrefTable xref = document.getXref();
        boolean needFlush = true;
        while (needFlush) {
            needFlush = false;
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference indirectReference = xref.get(i);
                if (indirectReference != null && !indirectReference.isFree()
                        && indirectReference.checkState(PdfObject.MUST_BE_FLUSHED)
                        && !forbiddenToFlush.contains(indirectReference)) {
                    PdfObject obj = indirectReference.getRefersTo(false);
                    if (obj != null) {
                        obj.flush();
                        needFlush = true;
                    }
                }
            }
        }
        if (objectStream != null && objectStream.getSize() > 0) {
            objectStream.flush();
            objectStream = null;
        }
    }

    /**
     * Flushes all modified objects which have not been flushed yet. Used in case incremental updates.
     *
     * @param forbiddenToFlush a {@link Set} of {@link PdfIndirectReference references} that are forbidden to be flushed
     *                         automatically.
     */
    protected void flushModifiedWaitingObjects(Set<PdfIndirectReference> forbiddenToFlush) {
        PdfXrefTable xref = document.getXref();
        for (int i = 1; i < xref.size(); i++) {
            PdfIndirectReference indirectReference = xref.get(i);
            if (null != indirectReference && !indirectReference.isFree() && !forbiddenToFlush.contains(
                    indirectReference)) {
                boolean isModified = indirectReference.checkState(PdfObject.MODIFIED);
                if (isModified) {
                    PdfObject obj = indirectReference.getRefersTo(false);
                    if (obj != null) {
                        if (!obj.equals(objectStream)) {
                            obj.flush();
                        }
                    }
                }
            }
        }
        if (objectStream != null && objectStream.getSize() > 0) {
            objectStream.flush();
            objectStream = null;
        }
    }

    void finish() throws IOException {
        if (document != null && !document.isClosed()) {
            // Writer is always closed as part of document closing
            document.dispatchEvent(new PdfDocumentEvent(PdfDocumentEvent.START_WRITER_CLOSING));

            if (isByteArrayWritingMode()) {
                completeByteArrayWritingMode();
            }
        }

        close();
    }

    /**
     * Gets the current object stream.
     *
     * @return object stream.
     */
    PdfObjectStream getObjectStream() {
        if (!isFullCompression()) {
            return null;
        }
        if (objectStream == null) {
            objectStream = new PdfObjectStream(document);
        } else if (objectStream.getSize() == PdfObjectStream.MAX_OBJ_STREAM_SIZE) {
            objectStream.flush();
            objectStream = new PdfObjectStream(objectStream);
        }
        return objectStream;
    }

    /**
     * Flush all copied objects.
     *
     * @param docId id of the source document
     */
    void flushCopiedObjects(long docId) {
        List<PdfIndirectReference> remove = new ArrayList<>();
        for (Map.Entry<PdfIndirectReference, PdfIndirectReference> copiedObject : copiedObjects.entrySet()) {
            PdfDocument document = copiedObject.getKey().getDocument();
            if (document != null && document.getDocumentId() == docId) {
                if (copiedObject.getValue().refersTo != null) {
                    copiedObject.getValue().refersTo.flush();
                    remove.add(copiedObject.getKey());
                }
            }
        }
        for (PdfIndirectReference ird : remove) {
            copiedObjects.remove(ird);
        }
    }

    void enableByteArrayWritingMode() {
        if (isByteArrayWritingMode()) {
            throw new PdfException("Byte array writing mode is already enabled");
        } else {
            this.originalOutputStream = this.outputStream;
            this.outputStream = new ByteArrayOutputStream();
        }
    }

    private void completeByteArrayWritingMode() throws IOException {
        byte[] baos = ((ByteArrayOutputStream) getOutputStream()).toByteArray();
        originalOutputStream.write(baos, 0, baos.length);
        originalOutputStream.close();
    }

    private boolean isByteArrayWritingMode() {
        return originalOutputStream != null;
    }

    private void markArrayContentToFlush(PdfArray array) {
        for (int i = 0; i < array.size(); i++) {
            markObjectToFlush(array.get(i, false));
        }
    }

    private void markDictionaryContentToFlush(PdfDictionary dictionary) {
        for (PdfObject item : dictionary.values(false)) {
            markObjectToFlush(item);
        }
    }

    private void markObjectToFlush(PdfObject pdfObject) {
        if (pdfObject != null) {
            PdfIndirectReference indirectReference = pdfObject.getIndirectReference();
            if (indirectReference != null) {
                if (!indirectReference.checkState(PdfObject.FLUSHED)) {
                    indirectReference.setState(PdfObject.MUST_BE_FLUSHED);
                }
            } else {
                if (pdfObject.getType() == PdfObject.INDIRECT_REFERENCE) {
                    if (!pdfObject.checkState(PdfObject.FLUSHED)) {
                        pdfObject.setState(PdfObject.MUST_BE_FLUSHED);
                    }
                } else if (pdfObject.getType() == PdfObject.ARRAY) {
                    markArrayContentToFlush((PdfArray) pdfObject);
                } else if (pdfObject.getType() == PdfObject.DICTIONARY) {
                    markDictionaryContentToFlush((PdfDictionary) pdfObject);
                }
            }
        }
    }

    private static boolean checkTypeOfPdfDictionary(PdfObject dictionary, PdfName expectedType) {
        return dictionary.isDictionary() && expectedType.equals(((PdfDictionary) dictionary).getAsName(PdfName.Type));
    }
}
