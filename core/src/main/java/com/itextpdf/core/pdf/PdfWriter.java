package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class PdfWriter extends PdfOutputStream {

    private static final byte[] obj = getIsoBytes(" obj\n");
    private static final byte[] endobj = getIsoBytes("\nendobj\n");

    /**
     * Indicates if to use full compression (using object streams).
     */
    protected boolean fullCompression = false;

    protected int compressionLevel = DEFAULT_COMPRESSION;

    /**
     * Currently active object stream.
     * Objects are written to the object stream if fullCompression set to true.
     */
    protected PdfObjectStream objectStream = null;

    protected Hashtable<Integer, PdfIndirectReference> copiedObjects = new Hashtable<Integer, PdfIndirectReference>();

    //forewarned is forearmed
    protected boolean isUserWarnedAboutAcroFormCopying;

    public PdfWriter(java.io.OutputStream os) {
        super(new BufferedOutputStream(os));
    }

    public PdfWriter(String filename) throws FileNotFoundException {
        this(new FileOutputStream(filename));
    }

    /**
     * Indicates if to use full compression mode.
     *
     * @return true if to use full compression, false otherwise.
     */
    public boolean isFullCompression() {
        return fullCompression;
    }

    /**
     * Sets full compression mode.
     *
     * @param fullCompression true if to use full compression, false otherwise.
     */
    public PdfWriter setFullCompression(boolean fullCompression) {
        this.fullCompression = fullCompression;
        return this;
    }

    /**
     * Gets default compression level for @see PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     *
     * @return compression level.
     */
    public int getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * Sets default compression level for @see PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     *
     * @param compressionLevel compression level.
     */
    public PdfWriter setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
        return this;
    }

    /**
     * Gets the current object stream.
     *
     * @return object stream.
     * @throws IOException
     * @throws PdfException
     */
    protected PdfObjectStream getObjectStream() throws IOException {
        if (!fullCompression)
            return null;
        if (objectStream == null) {
            objectStream = new PdfObjectStream(document);
        } else if (objectStream.getSize() == PdfObjectStream.maxObjStreamSize) {
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
     * @throws PdfException
     */
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException {
        PdfIndirectReference indirectReference = pdfObject.getIndirectReference();
        if (fullCompression && canBeInObjStm) {
            PdfObjectStream objectStream = getObjectStream();
            objectStream.addObject(pdfObject);
        } else {
            indirectReference.setOffset(getCurrentPos());
            writeToBody(pdfObject);
        }
        indirectReference.setState(PdfObject.Flushed);
        indirectReference.clearState(PdfObject.MustBeFlushed);
        switch (pdfObject.getType()) {
            case PdfObject.Boolean:
            case PdfObject.Name:
            case PdfObject.Null:
            case PdfObject.Number:
            case PdfObject.String:
                ((PdfPrimitiveObject) pdfObject).content = null;
                break;
            case PdfObject.Array:
                PdfArray array = ((PdfArray) pdfObject);
                markArrayContentToFlush(array);
                array.releaseContent();
                break;
            case PdfObject.Stream:
            case PdfObject.Dictionary:
                PdfDictionary dictionary = ((PdfDictionary) pdfObject);
                markDictionaryContentToFlush(dictionary);
                dictionary.releaseContent();
                break;
            case PdfObject.IndirectReference:
                markObjectToFlush(((PdfIndirectReference) pdfObject).getRefersTo(false));
        }
    }

    private void markArrayContentToFlush(PdfArray array) {
        for (PdfObject item : array) {
            markObjectToFlush(item);
        }
    }

    private void markDictionaryContentToFlush(PdfDictionary dictionary) {
        for (PdfObject item : dictionary.values()) {
            markObjectToFlush(item);
        }
    }

    private void markObjectToFlush(PdfObject pdfObject) {
        if (pdfObject != null) {
            PdfIndirectReference indirectReference = pdfObject.getIndirectReference();
            if (indirectReference != null) {
                if (!indirectReference.checkState(PdfObject.Flushed)) {
                    indirectReference.setState(PdfObject.MustBeFlushed);
                }
            } else {
                if (pdfObject.getType() == PdfObject.IndirectReference) {
                    if (!pdfObject.checkState(PdfObject.Flushed)) {
                        pdfObject.setState(PdfObject.MustBeFlushed);
                    }
                } else if (pdfObject.getType() == PdfObject.Array) {
                    markArrayContentToFlush((PdfArray) pdfObject);
                } else if (pdfObject.getType() == PdfObject.Dictionary) {
                    markDictionaryContentToFlush((PdfDictionary) pdfObject);
                }
            }
        }
    }

    protected PdfObject copyObject(PdfObject object, PdfDocument document, boolean allowDuplicating) {
        if (object instanceof PdfIndirectReference)
            object = ((PdfIndirectReference) object).getRefersTo();
        if (object == null) {
            object = PdfNull.PdfNull;
        }
        PdfIndirectReference indirectReference = object.getIndirectReference();
        PdfIndirectReference copiedIndirectReference;

        int copyObjectKey = 0;
        if (!allowDuplicating && indirectReference != null) {
            copyObjectKey = getCopyObjectKey(object);
            copiedIndirectReference = copiedObjects.get(copyObjectKey);
            if (copiedIndirectReference != null)
                return copiedIndirectReference.getRefersTo();
        }

        PdfObject newObject = object.newInstance();
        if (indirectReference != null) {
            if (copyObjectKey == 0)
                copyObjectKey = getCopyObjectKey(object);
            copiedObjects.put(copyObjectKey, newObject.makeIndirect(document).getIndirectReference());
        }
        newObject.copyContent(object, document);
        return newObject;
    }

    /**
     * Writes object to body of PDF document.
     *
     * @param object object to write.
     * @throws IOException
     * @throws PdfException
     */
    protected void writeToBody(PdfObject object) throws IOException {
        if (crypto != null) {
            crypto.setHashKey(object.getIndirectReference().getObjNumber(), object.getIndirectReference().getGenNumber());
        }
        writeInteger(object.getIndirectReference().getObjNumber()).
                writeSpace().
                writeInteger(object.getIndirectReference().getGenNumber()).writeBytes(obj);
        write(object);
        writeBytes(endobj);
    }

    /**
     * Writes PDF header.
     *
     * @throws PdfException
     */
    protected void writeHeader() {
        writeByte((byte) '%').
                writeString(document.getPdfVersion().toString()).
                writeString("\n%\u00e2\u00e3\u00cf\u00d3\n");
    }

    /**
     * Flushes all objects which have not been flushed yet.
     *
     * @throws PdfException
     */
    protected void flushWaitingObjects() {
        PdfXrefTable xref = document.getXref();
        boolean needFlush = true;
        while (needFlush) {
            needFlush = false;
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference indirectReference = xref.get(i);
                if (indirectReference != null
                        && indirectReference.checkState(PdfObject.MustBeFlushed)) {
                    PdfObject object = indirectReference.getRefersTo(false);
                    if (object != null) {
                        object.flush();
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
     * @throws PdfException
     */
    protected void flushModifiedWaitingObjects() {
        PdfXrefTable xref = document.getXref();
        for (int i = 1; i < xref.size(); i++) {
            PdfIndirectReference indirectReference = xref.get(i);
            PdfObject object = indirectReference.getRefersTo(false);
            if (object != null && !object.equals(objectStream) && object.isModified()) {
                object.flush();
            }
        }
        if (objectStream != null && objectStream.getSize() > 0) {
            objectStream.flush();
            objectStream = null;
        }
    }

    /**
     * Calculates hash code for object to be copied.
     * The hash code and the copied object is the stored in @{link copiedObjects} hash map to avoid duplications.
     *
     * @param object object to be copied.
     * @return calculated hash code.
     */
    protected int getCopyObjectKey(PdfObject object) {
        int result = object.getIndirectReference().hashCode();
        result = 31 * result + object.getDocument().hashCode();
        return result;
    }
}
