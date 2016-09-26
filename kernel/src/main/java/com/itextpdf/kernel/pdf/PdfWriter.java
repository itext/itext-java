/*

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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.kernel.PdfException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    protected Map<PdfDocument.IndirectRefDescription, PdfIndirectReference> copiedObjects = new HashMap<>();

    /**
     * Is used in smart mode to store serialized objects content.
     */
    private HashMap<SerializedPdfObject, PdfIndirectReference> serializedContentToObjectRef = new HashMap<>();
    private HashMap<Integer, byte[]> objectRefToSerializedContent = new HashMap<>();

    //forewarned is forearmed
    protected boolean isUserWarnedAboutAcroFormCopying;

    public PdfWriter(java.io.OutputStream os) {
        this(os, new WriterProperties());
    }

    public PdfWriter(java.io.OutputStream os, WriterProperties properties) {
        super(FileUtil.wrapWithBufferedOutputStream(os));
        this.properties = properties;
        EncryptionProperties encryptProps = properties.encryptionProperties;
        if (properties.isStandardEncryptionUsed()) {
            crypto = new PdfEncryption(encryptProps.userPassword, encryptProps.ownerPassword, encryptProps.standardEncryptPermissions,
                    encryptProps.encryptionAlgorithm, PdfEncryption.generateNewDocumentId());
        } else if (properties.isPublicKeyEncryptionUsed()) {
            crypto = new PdfEncryption(encryptProps.publicCertificates,
                    encryptProps.publicKeyEncryptPermissions, encryptProps.encryptionAlgorithm);
        }
        if (properties.debugMode) {
            setDebugMode();
        }
    }

    public PdfWriter(String filename) throws FileNotFoundException {
        this(filename, new WriterProperties());
    }

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
     * <p/>
     * In smart mode when resources (such as fonts, images,...) are
     * encountered, a reference to these resources is saved
     * in a cache, so that they can be reused.
     * This requires more memory, but reduces the file size
     * of the resulting PDF document.
     */
    public PdfWriter setSmartMode(boolean smartMode) {
        this.properties.smartMode = smartMode;
        return this;
    }

    @Override
    public void write(int b) throws java.io.IOException {
        super.write(b);
        if (duplicateStream != null) {
            duplicateStream.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws java.io.IOException {
        super.write(b);
        if (duplicateStream != null) {
            duplicateStream.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws java.io.IOException {
        super.write(b, off, len);
        if (duplicateStream != null) {
            duplicateStream.write(b, off, len);
        }
    }

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

    /**
     * Flushes the object. Override this method if you want to define custom behaviour for object flushing.
     *
     * @param pdfObject     object to flush.
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     * @throws IOException
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

    protected PdfObject copyObject(PdfObject obj, PdfDocument document, boolean allowDuplicating) {
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
            copiedObjectKey = new PdfDocument.IndirectRefDescription(indirectReference.getDocument().getDocumentId(),
                    indirectReference.getObjNumber(), indirectReference.getGenNumber());

            PdfIndirectReference copiedIndirectReference = copiedObjects.get(copiedObjectKey);
            if (copiedIndirectReference != null)
                return copiedIndirectReference.getRefersTo();
        }

        if (obj.isDictionary()) {
            PdfName subtype = ((PdfDictionary)obj).getAsName(PdfName.Subtype);
            if (subtype != null && subtype.equals(PdfName.Widget)) {
                tryToFindDuplicate = false;
            }

        }
        if (properties.smartMode && tryToFindDuplicate && !checkTypeOfPdfDictionary(obj, PdfName.Page)) {
            PdfIndirectReference copiedObjectRef = tryToFindPreviouslyCopiedEqualObject(obj);
            if (copiedObjectRef != null) {
                PdfIndirectReference copiedIndirectReference = copiedObjects.get(new PdfDocument.IndirectRefDescription(copiedObjectRef.getDocument().getDocumentId(),
                        copiedObjectRef.getObjNumber(), copiedObjectRef.getGenNumber()));
                copiedObjects.put(copiedObjectKey, copiedIndirectReference);
                return copiedIndirectReference.getRefersTo();
            }
        }

        PdfObject newObject = obj.newInstance();
        if (indirectReference != null) {
            if (copiedObjectKey == null)
                copiedObjectKey = new PdfDocument.IndirectRefDescription(indirectReference.getDocument().getDocumentId(),
                        indirectReference.getObjNumber(), indirectReference.getGenNumber());
            PdfIndirectReference indRef = newObject.makeIndirect(document).getIndirectReference();
            copiedObjects.put(copiedObjectKey, indRef);
        }
        newObject.copyContent(obj, document);

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
     */
    protected void flushWaitingObjects() {
        PdfXrefTable xref = document.getXref();
        boolean needFlush = true;
        while (needFlush) {
            needFlush = false;
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference indirectReference = xref.get(i);
                if (indirectReference != null
                        && indirectReference.checkState(PdfObject.MUST_BE_FLUSHED)) {
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
     */
    protected void flushModifiedWaitingObjects() {
        PdfXrefTable xref = document.getXref();
        for (int i = 1; i < xref.size(); i++) {
            PdfIndirectReference indirectReference = xref.get(i);
            if (null != indirectReference) {
                PdfObject obj = indirectReference.getRefersTo(false);
                if (obj != null && !obj.equals(objectStream) && obj.isModified()) {
                    obj.flush();
                }
            }
        }
        if (objectStream != null && objectStream.getSize() > 0) {
            objectStream.flush();
            objectStream = null;
        }
    }

    /**
     * Calculates hash code for the indirect reference taking into account the document it belongs to.
     *
     * @param indRef object to be hashed.
     * @return calculated hash code.
     */
    protected static int calculateIndRefKey(PdfIndirectReference indRef) {
        int result = indRef.hashCode();
        result = 31 * result + indRef.getDocument().hashCode();
        return result;
    }

    /**
     * Calculates hash code for object to be copied.
     * The hash code and the copied object is the stored in @{link copiedObjects} hash map to avoid duplications.
     *
     * @param obj object to be copied.
     * @return calculated hash code.
     * @deprecated Functionality will be removed.
     */
    @Deprecated
    protected int getCopyObjectKey(PdfObject obj) {
        return calculateIndRefKey(obj.getIndirectReference());
    }

    /**
     * Used in the smart mode.
     * It serializes given object content and tries to find previously copied object with the same content.
     * If already copied object is not found, it saves current object serialized content into the map.
     *
     * @param object an object to check if some other object with the same content was already copied.
     * @return indirect reference of the object with the same content, which already has a copy in the new document.
     */
    private PdfIndirectReference tryToFindPreviouslyCopiedEqualObject(PdfObject object) {
        SerializedPdfObject objectKey;
        if (object.isStream() || object.isDictionary()) {
            objectKey = new SerializedPdfObject(object, objectRefToSerializedContent);
            PdfIndirectReference objectRef = serializedContentToObjectRef.get(objectKey);
            if (objectRef != null) {
                return objectRef;
            }
            serializedContentToObjectRef.put(objectKey, object.getIndirectReference());
        }

        return null;
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

    static class SerializedPdfObject {
        private final byte[] serializedContent;
        private final int hash;

        private MessageDigest md5;
        private HashMap<Integer, byte[]> objToSerializedContent;

        SerializedPdfObject(PdfObject obj, HashMap<Integer, byte[]> objToSerializedContent) {
            assert obj.isDictionary() || obj.isStream();

            this.objToSerializedContent = objToSerializedContent;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                throw new PdfException(e);
            }
            ByteBufferOutputStream bb = new ByteBufferOutputStream();
            int level = 100;
            serObject(obj, level, bb);
            this.serializedContent = bb.toByteArray();
            hash = calculateHash(this.serializedContent);
            md5 = null;
        }

        // TODO 2: object is not checked if it was already serialized on start, double work could be done
        // TODO 3: indirect objects often stored multiple times as parts of the other objects
        private void serObject(PdfObject obj, int level, ByteBufferOutputStream bb) {
            if (level <= 0)
                return;
            if (obj == null) {
                bb.append("$Lnull");
                return;
            }
            PdfIndirectReference reference = null;
            ByteBufferOutputStream savedBb = null;
            int indRefKey = -1;

            if (obj.isIndirectReference()) {
                reference = (PdfIndirectReference) obj;
                indRefKey = calculateIndRefKey(reference);
                byte[] cached = objToSerializedContent.get(indRefKey);
                if (cached != null) {
                    bb.append(cached);
                    return;
                } else {
                    savedBb = bb;
                    bb = new ByteBufferOutputStream();
                    obj = reference.getRefersTo();
                }
            }

            if (obj.isStream()) {
                bb.append("$B");
                serDic((PdfDictionary) obj, level - 1, bb);
                if (level > 0) {
                    md5.reset();
                    bb.append(md5.digest(((PdfStream) obj).getBytes(false)));
                }
            } else if (obj.isDictionary()) {
                serDic((PdfDictionary) obj, level - 1, bb);
            } else if (obj.isArray()) {
                serArray((PdfArray) obj, level - 1, bb);
            } else if (obj.isString()) {
                bb.append("$S").append(obj.toString());
            } else if (obj.isName()) {
                bb.append("$N").append(obj.toString());
            } else
                bb.append("$L").append(obj.toString()); // PdfNull case is also here

            if (savedBb != null) {
                objToSerializedContent.put(indRefKey, bb.getBuffer());
                savedBb.append(bb);
            }
        }

        private void serDic(PdfDictionary dic, int level, ByteBufferOutputStream bb) {
            bb.append("$D");
            if (level <= 0)
                return;
            PdfName[] keys = new PdfName[dic.keySet().size()];
            keys = dic.keySet().toArray(keys);
            Arrays.sort(keys);
            for (Object key : keys) {
                if (key.equals(PdfName.P) && (dic.get((PdfName) key).isIndirectReference() || dic.get((PdfName) key).isDictionary()) || key.equals(PdfName.Parent)) // ignore recursive call
                    continue;
                serObject((PdfObject) key, level, bb);
                serObject(dic.get((PdfName) key, false), level, bb);

            }
        }

        private void serArray(PdfArray array, int level, ByteBufferOutputStream bb) {
            bb.append("$A");
            if (level <= 0)
                return;
            for (int k = 0; k < array.size(); ++k) {
                serObject(array.get(k, false), level, bb);
            }
        }

        private static int calculateHash(byte[] b) {
            int hash = 0;
            int len = b.length;
            for (int k = 0; k < len; ++k) {
                hash = hash * 31 + (b[k] & 0xff);
            }
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SerializedPdfObject && hashCode() == obj.hashCode() && Arrays.equals(serializedContent, ((SerializedPdfObject) obj).serializedContent);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
