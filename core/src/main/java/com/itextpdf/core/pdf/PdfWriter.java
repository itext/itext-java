package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class PdfWriter extends PdfOutputStream {

    static private final byte[] obj = getIsoBytes(" obj\n");
    static private final byte[] endobj = getIsoBytes("\nendobj\n");

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

    public PdfWriter(java.io.OutputStream os) {
        super(new BufferedOutputStream(os));
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
    public void setFullCompression(boolean fullCompression) {
        this.fullCompression = fullCompression;
    }

    /**
     * Gets default compression level for @see PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     * @return compression level.
     */
    public int getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * Sets default compression level for @see PdfStream.
     * For more details @see {@link java.util.zip.Deflater}.
     * @param compressionLevel compression level.
     */
    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    /**
     * Gets the current object stream.
     *
     * @return object stream.
     * @throws IOException
     * @throws PdfException
     */
    protected PdfObjectStream getObjectStream() throws IOException, PdfException {
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
     * @param pdfObject object to flush.
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     * @throws IOException
     * @throws PdfException
     */
    protected void flushObject(PdfObject pdfObject, boolean canBeInObjStm) throws IOException, PdfException {
        PdfIndirectReference indirectReference = pdfObject.getIndirectReference();
        if (fullCompression && canBeInObjStm) {
            PdfObjectStream objectStream = getObjectStream();
            objectStream.addObject(pdfObject);
        } else {
            indirectReference.setOffset(getCurrentPos());
            writeToBody(pdfObject);
        }
        indirectReference.setState(PdfIndirectReference.Flushed);
        indirectReference.clearState(PdfIndirectReference.MustBeFlushed);
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
                markObjectToFlush(((PdfIndirectReference)pdfObject).getRefersTo(false));
        }
    }

    private void markArrayContentToFlush(PdfArray array) {
        for (PdfObject item: array) {
            markObjectToFlush(item);
        }
    }

    private void markDictionaryContentToFlush(PdfDictionary dictionary) {
        for (PdfObject item: dictionary.values()) {
            markObjectToFlush(item);
        }
    }

    private void markObjectToFlush(PdfObject pdfObject) {
        if (pdfObject != null) {
            PdfIndirectReference indirectReference = pdfObject.getIndirectReference();
            if (indirectReference != null) {
                if (!indirectReference.checkState(PdfIndirectReference.Flushed)) {
                    indirectReference.setState(PdfIndirectReference.MustBeFlushed);
                }
            } else {
                if (pdfObject.getType() == PdfObject.IndirectReference) {
                    if (!((PdfIndirectReference)pdfObject).checkState(PdfIndirectReference.Flushed)) {
                        ((PdfIndirectReference) pdfObject).setState(PdfIndirectReference.MustBeFlushed);
                    }
                } else if (pdfObject.getType() == PdfObject.Array) {
                    markArrayContentToFlush((PdfArray)pdfObject);
                } else if (pdfObject.getType() == PdfObject.Dictionary) {
                    markDictionaryContentToFlush((PdfDictionary) pdfObject);
                }
            }
        }
    }

    protected PdfObject copyObject(PdfObject object, PdfDocument document, boolean allowDuplicating) throws PdfException {
        if (object instanceof PdfIndirectReference)
            object = ((PdfIndirectReference)object).getRefersTo();
        PdfIndirectReference indirectReference = object.getIndirectReference();
        PdfIndirectReference copiedIndirectReference;
        int copyObjectKey = 0;
        if (!allowDuplicating && indirectReference != null) {
            if (indirectReference.getDocument().hashCode() == document.hashCode()) {
                return indirectReference;
            } else {
                copyObjectKey = getCopyObjectKey(object);
                copiedIndirectReference = copiedObjects.get(copyObjectKey);
                if (copiedIndirectReference != null)
                    return copiedIndirectReference;
            }
        }
        if (!allowDuplicating && indirectReference != null && (copiedIndirectReference = copiedObjects.get(copyObjectKey = getCopyObjectKey(object))) != null) {
            return copiedIndirectReference;
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
    protected void writeToBody(PdfObject object) throws IOException, PdfException {
        writeInteger(object.getIndirectReference().getObjNr()).
                writeSpace().
                writeInteger(object.getIndirectReference().getGenNr()).writeBytes(obj);
        write(object);
        writeBytes(endobj);
    }

    /**
     * Writes PDF header.
     *
     * @throws PdfException
     */
    protected void writeHeader() throws PdfException {
        writeByte((byte) '%').
                writeString(document.getPdfVersion().getPdfVersion()).
                writeString("\n%\u00e2\u00e3\u00cf\u00d3\n");
    }

    /**
     * Flushes all objects which have not been flushed yet.
     *
     * @throws PdfException
     */
    protected void flushWaitingObjects() throws PdfException {
        PdfXrefTable xref = document.getXref();
        boolean needFlush = true;
        while (needFlush) {
            needFlush = false;
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference indirectReference = xref.get(i);
                if (indirectReference != null
                        && indirectReference.checkState(PdfIndirectReference.MustBeFlushed)) {
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
    protected void flushModifiedWaitingObjects() throws PdfException {
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
