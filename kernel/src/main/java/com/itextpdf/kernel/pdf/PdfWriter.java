/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.io.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.itextpdf.io.source.ByteUtils.getIsoBytes;

public class PdfWriter extends PdfOutputStream implements Serializable {

    private static final long serialVersionUID = -6875544505477707103L;

    private static final byte[] obj = getIsoBytes(" obj\n");
    private static final byte[] endobj = getIsoBytes("\nendobj\n");

    // For internal usage only
    private PdfOutputStream duplicateStream = null;

    protected WriterProperties properties;

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
    private Map<PdfDocument.IndirectRefDescription, PdfIndirectReference> copiedObjects = new LinkedHashMap<>();

    /**
     * Is used in smart mode to serialize and store serialized objects content.
     */
    private SmartModePdfObjectsSerializer smartModeSerializer = new SmartModePdfObjectsSerializer();

    //forewarned is forearmed
    protected boolean isUserWarnedAboutAcroFormCopying;

    /**
     * Create a PdfWriter writing to the passed File and with default writer properties.
     *
     * @param file File to write to.
     */
    public PdfWriter(java.io.File file) throws FileNotFoundException {
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

    public PdfWriter(java.io.OutputStream os, WriterProperties properties) {
        super(FileUtil.wrapWithBufferedOutputStream(os));
        this.properties = properties;
        if (properties.debugMode) {
            setDebugMode();
        }
    }

    /**
     * Create a PdfWriter writing to the passed filename and with default writer properties.
     *
     * @param filename filename of the resulting pdf.
     * @throws FileNotFoundException
     */
    public PdfWriter(String filename) throws FileNotFoundException {
        this(filename, new WriterProperties());
    }

    /**
     * Create a PdfWriter writing to the passed filename and using the passed writer properties.
     *
     * @param filename   filename of the resulting pdf.
     * @param properties writerproperties to use.
     * @throws FileNotFoundException
     */
    public PdfWriter(String filename, WriterProperties properties) throws FileNotFoundException {
        this(FileUtil.getBufferedOutputStream(filename), properties);
    }

    /**
     * Indicates if to use full compression mode.
     *
     * @return true if to use full compression, false otherwise.
     */
    public boolean isFullCompression() {
        return properties.isFullCompression != null ? (boolean) properties.isFullCompression : false;
    }

    /**
     * Gets default compression level for @see PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     *
     * @return compression level.
     */
    public int getCompressionLevel() {
        return properties.compressionLevel;
    }

    /**
     * Sets default compression level for @see PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     *
     * @param compressionLevel compression level.
     */
    public PdfWriter setCompressionLevel(int compressionLevel) {
        this.properties.setCompressionLevel(compressionLevel);
        return this;
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
     */
    public PdfWriter setSmartMode(boolean smartMode) {
        this.properties.smartMode = smartMode;
        return this;
    }

    /**
     * Write an integer to the underlying stream
     *
     * @param b integer to write
     * @throws java.io.IOException
     */
    @Override
    public void write(int b) throws java.io.IOException {
        super.write(b);
        if (duplicateStream != null) {
            duplicateStream.write(b);
        }
    }

    /**
     * Write a byte array to the underlying stream
     *
     * @param b byte array to write
     * @throws java.io.IOException
     */
    @Override
    public void write(byte[] b) throws java.io.IOException {
        super.write(b);
        if (duplicateStream != null) {
            duplicateStream.write(b);
        }
    }

    /**
     * Write a slice of the passed byte array to the underlying stream
     *
     * @param b   byte array to slice and write.
     * @param off starting index of the slice.
     * @param len length of the slice.
     * @throws java.io.IOException
     */
    @Override
    public void write(byte[] b, int off, int len) throws java.io.IOException {
        super.write(b, off, len);
        if (duplicateStream != null) {
            duplicateStream.write(b, off, len);
        }
    }


    /**
     * Close the writer and underlying streams.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            try {
                if (duplicateStream != null) {
                    duplicateStream.close();
                }
            } catch (Exception ex) {
                Logger logger = LoggerFactory.getLogger(PdfWriter.class);
                logger.error("Closing of the duplicatedStream failed.", ex);
            }
        }
    }

    /**
     * Gets the current object stream.
     *
     * @return object stream.
     * @throws IOException
     */
    PdfObjectStream getObjectStream() throws IOException {
        if (!isFullCompression())
            return null;
        if (objectStream == null) {
            objectStream = new PdfObjectStream(document);
        } else if (objectStream.getSize() == PdfObjectStream.MAX_OBJ_STREAM_SIZE) {
            objectStream.flush();
            objectStream = new PdfObjectStream(objectStream);
        }
        return objectStream;
    }

    protected void initCryptoIfSpecified(PdfVersion version) {
        EncryptionProperties encryptProps = properties.encryptionProperties;
        if (properties.isStandardEncryptionUsed()) {
            crypto = new PdfEncryption(encryptProps.userPassword, encryptProps.ownerPassword, encryptProps.standardEncryptPermissions,
                    encryptProps.encryptionAlgorithm, PdfEncryption.generateNewDocumentId(), version);
        } else if (properties.isPublicKeyEncryptionUsed()) {
            crypto = new PdfEncryption(encryptProps.publicCertificates,
                    encryptProps.publicKeyEncryptPermissions, encryptProps.encryptionAlgorithm, version);
        }
    }

    /**
     * Flushes the object. Override this method if you want to define custom behaviour for object flushing.
     *
     * @param pdfObject     object to flush.
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     * @throws IOException on error.
     */
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
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


    protected PdfObject copyObject(PdfObject obj, PdfDocument documentTo, boolean allowDuplicating) {
        if (obj instanceof PdfIndirectReference)
            obj = ((PdfIndirectReference) obj).getRefersTo();
        if (obj == null) {
            obj = PdfNull.PDF_NULL;
        }
        if (checkTypeOfPdfDictionary(obj, PdfName.Catalog)) {
            Logger logger = LoggerFactory.getLogger(PdfReader.class);
            logger.warn(LogMessageConstant.MAKE_COPY_OF_CATALOG_DICTIONARY_IS_FORBIDDEN);
            obj = PdfNull.PDF_NULL;
        }

        PdfIndirectReference indirectReference = obj.getIndirectReference();

        PdfDocument.IndirectRefDescription copiedObjectKey = null;
        boolean tryToFindDuplicate = !allowDuplicating && indirectReference != null;

        if (tryToFindDuplicate) {
            copiedObjectKey = new PdfDocument.IndirectRefDescription(indirectReference);

            PdfIndirectReference copiedIndirectReference = copiedObjects.get(copiedObjectKey);
            if (copiedIndirectReference != null)
                return copiedIndirectReference.getRefersTo();
        }

        SerializedObjectContent serializedContent = null;
        if (properties.smartMode && tryToFindDuplicate && !checkTypeOfPdfDictionary(obj, PdfName.Page)) {
            serializedContent = smartModeSerializer.serializeObject(obj);
            PdfIndirectReference objectRef = smartModeSerializer.getSavedSerializedObject(serializedContent);
            if (objectRef != null) {
                copiedObjects.put(copiedObjectKey, objectRef);
                return objectRef.refersTo;
            }
        }

        PdfObject newObject = obj.newInstance();
        if (indirectReference != null) {
            if (copiedObjectKey == null) {
                copiedObjectKey = new PdfDocument.IndirectRefDescription(indirectReference);
            }
            PdfIndirectReference indRef = newObject.makeIndirect(documentTo).getIndirectReference();
            if (serializedContent != null) {
                smartModeSerializer.saveSerializedObject(serializedContent, indRef);
            }
            copiedObjects.put(copiedObjectKey, indRef);
        }
        newObject.copyContent(obj, documentTo);

        return newObject;
    }

    /**
     * Writes object to body of PDF document.
     *
     * @param pdfObj object to write.
     * @throws IOException
     */
    protected void writeToBody(PdfObject pdfObj) throws IOException {
        if (crypto != null) {
            crypto.setHashKeyForNextObject(pdfObj.getIndirectReference().getObjNumber(), pdfObj.getIndirectReference().getGenNumber());
        }
        writeInteger(pdfObj.getIndirectReference().getObjNumber()).
                writeSpace().
                writeInteger(pdfObj.getIndirectReference().getGenNumber()).writeBytes(obj);
        write(pdfObj);
        writeBytes(endobj);
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
     * @param forbiddenToFlush {@link Set<PdfIndirectReference>} of references that are forbidden to be flushed automatically.
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
     * @param forbiddenToFlush {@link Set<PdfIndirectReference>} of references that are forbidden to be flushed automatically.
     */
    protected void flushModifiedWaitingObjects(Set<PdfIndirectReference> forbiddenToFlush) {
        PdfXrefTable xref = document.getXref();
        for (int i = 1; i < xref.size(); i++) {
            PdfIndirectReference indirectReference = xref.get(i);
            if (null != indirectReference && !indirectReference.isFree() && !forbiddenToFlush.contains(indirectReference)) {
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

    /**
     * Flush all copied objects.
     *
     * @param docId id of the source document
     */
    void flushCopiedObjects(long docId) {
        List<PdfDocument.IndirectRefDescription> remove = new ArrayList<>();
        for (Map.Entry<PdfDocument.IndirectRefDescription, PdfIndirectReference> copiedObject : copiedObjects.entrySet()) {
            if (copiedObject.getKey().docId == docId) {
                if (copiedObject.getValue().refersTo != null) {
                    copiedObject.getValue().refersTo.flush();
                    remove.add(copiedObject.getKey());
                }
            }
        }
        for (PdfDocument.IndirectRefDescription ird : remove) {
            copiedObjects.remove(ird);
        }
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

    private PdfWriter setDebugMode() {
        duplicateStream = new PdfOutputStream(new ByteArrayOutputStream());
        return this;
    }

    private byte[] getDebugBytes() throws IOException {
        if (duplicateStream != null) {
            duplicateStream.flush();
            return ((ByteArrayOutputStream) (duplicateStream.getOutputStream())).toByteArray();
        } else {
            return null;
        }
    }

    private static boolean checkTypeOfPdfDictionary(PdfObject dictionary, PdfName expectedType) {
        return dictionary.isDictionary() && expectedType.equals(((PdfDictionary) dictionary).getAsName(PdfName.Type));
    }

    /**
     * This method is invoked while deserialization
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (outputStream == null) {
            outputStream = new ByteArrayOutputStream().assignBytes(getDebugBytes());
        }
    }

    /**
     * This method is invoked while serialization
     */
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        if (duplicateStream == null) {
            throw new NotSerializableException(this.getClass().getName() + ": debug mode is disabled!");
        }
        OutputStream tempOutputStream = outputStream;
        outputStream = null;
        out.defaultWriteObject();
        outputStream = tempOutputStream;
    }

}
