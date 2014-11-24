package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.TreeSet;

public class PdfWriter extends PdfOutputStream {

    static private final byte[] obj = getIsoBytes(" obj\n");
    static private final byte[] endobj = getIsoBytes("\nendobj\n");
    static public final int GenerationMax = 65535;

    /**
     * Indicates if to use full compression (using object streams).
     */
    protected boolean fullCompression = false;

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
            objectStream = new PdfObjectStream(pdfDocument);
        } else if (objectStream.getSize() == PdfObjectStream.maxObjStreamSize) {
            objectStream.flush();
            objectStream = new PdfObjectStream(pdfDocument);
        }
        return objectStream;
    }

    /**
     * Flushes the object. Override this method if you want to define custom behaviour for object flushing.
     *
     * @param object        object to flush.
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     * @throws IOException
     * @throws PdfException
     */
    protected void flushObject(PdfObject object, boolean canBeInObjStm) throws IOException, PdfException {
        PdfIndirectReference indirectReference;
        if (object.isFlushed() || (indirectReference = object.getIndirectReference()) == null)
            return;
        if (isFullCompression() && canBeInObjStm) {
            PdfObjectStream objectStream = getObjectStream();
            objectStream.addObject(object);
        } else {
            indirectReference.setOffsetOrIndex(getCurrentPos());
            writeToBody(object);
        }
        indirectReference.flushed = true;
        indirectReference.setRefersTo(null);
        switch (object.getType()) {
            case PdfObject.Boolean:
            case PdfObject.Name:
            case PdfObject.Null:
            case PdfObject.Number:
            case PdfObject.String:
                flushObject((PdfPrimitiveObject) object);
                break;
            case PdfObject.Array:
                flushObject((PdfArray) object);
                break;
            case PdfObject.Stream:
                flushObject((PdfStream) object);
                break;
            case PdfObject.Dictionary:
                flushObject((PdfDictionary) object);
                break;
        }
    }

    protected void flushObject(PdfPrimitiveObject object) {
        object.content = null;
    }

    protected void flushObject(PdfArray array) {
        array.release();
    }

    protected void flushObject(PdfDictionary dictionary) {
        dictionary.release();
    }

    protected void flushObject(PdfStream stream) throws IOException {
        flushObject((PdfDictionary) stream);
        stream.releaseOutputStream();
        if (stream instanceof PdfObjectStream) {
            ((PdfObjectStream) stream).indexStream.close();
            ((PdfObjectStream) stream).indexStream = null;
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
                writeString(pdfDocument.getPdfVersion().getPdfVersion()).
                writeString("\n%\u00e2\u00e3\u00cf\u00d3\n");
    }

    /**
     * Flushes all objects which have not been flushed yet.
     *
     * @throws PdfException
     */
    protected void flushWaitingObjects() throws PdfException {
        TreeSet<PdfIndirectReference> indirects = pdfDocument.getXref().toSet();
        pdfDocument.getXref().clear();
        for (PdfIndirectReference indirectReference : indirects) {
            PdfObject object = indirectReference.getRefersTo(false);
            if (object != null && !object.equals(objectStream)) {
                object.flush();
            }
        }
        if (objectStream != null && objectStream.getSize() > 0) {
            objectStream.flush();
            objectStream = null;
        }
        pdfDocument.getXref().addAll(indirects);
    }

    /**
     * Writes trailer to PDF.
     *
     * @param startxref start of cross reference table.
     * @throws IOException
     */
    protected void writeTrailer(int startxref) throws IOException, PdfException {
        if (!fullCompression) {
            pdfDocument.getTrailer().setSize(pdfDocument.getXref().size());
            writeString("trailer\n");
            write(pdfDocument.getTrailer().getPdfObject());
        }
        writeString("\nstartxref\n").
                writeInteger(startxref).
                writeString("\n%%EOF\n");
        pdfDocument.getXref().clear();
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
