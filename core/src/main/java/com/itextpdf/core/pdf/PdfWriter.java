package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.TreeSet;

public class PdfWriter extends PdfOutputStream {

    static private final DecimalFormat objectOffsetFormatter = new DecimalFormat("0000000000");
    static private final DecimalFormat objectGenerationFormatter = new DecimalFormat("00000");
    static private final byte[] obj = getIsoBytes(" obj\n");
    static private final byte[] endobj = getIsoBytes("\nendobj\n");
    static private final byte[] freeXRefEntry = getIsoBytes(" f \n");
    static private final byte[] inUseXRefEntry = getIsoBytes(" n \n");
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
        array.list.clear();
        array.list = null;
    }

    protected void flushObject(PdfDictionary dictionary) {
        dictionary.map.clear();
        dictionary.map = null;
    }

    protected void flushObject(PdfStream stream) throws IOException {
        flushObject((PdfDictionary) stream);
        stream.outputStream.close();
        stream.outputStream = null;
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
     * @throws IOException
     */
    protected void writeHeader() throws IOException {
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
        TreeSet<PdfIndirectReference> indirects = pdfDocument.getXRef().toSet();
        pdfDocument.getXRef().clear();
        for (PdfIndirectReference indirectReference : indirects) {
            PdfObject object = indirectReference.getRefersTo(false);
            if (object != null) {
                object.flush();
            }
        }
        if (objectStream != null && objectStream.getSize() > 0) {
            objectStream.flush();
            objectStream = null;
        }
        pdfDocument.getXRef().addAll(indirects);
    }

    /**
     * Writes cross reference table to PDF.
     *
     * @return start of cross reference table.
     * @throws IOException
     * @throws PdfException
     */
    protected int writeXRefTable() throws IOException, PdfException {
        int strtxref = currentPos;
        if (fullCompression) {
            PdfStream stream = new PdfStream(pdfDocument);
            stream.put(PdfName.Type, PdfName.XRef);
            stream.put(PdfName.Size, new PdfNumber(pdfDocument.getXRef().size()));
            stream.put(PdfName.W, new PdfArray(new ArrayList<PdfObject>() {{
                add(new PdfNumber(1));
                add(new PdfNumber(4));
                add(new PdfNumber(2));
            }}));
            stream.put(PdfName.Info, pdfDocument.getDocumentInfo().getPdfObject());
            stream.put(PdfName.Root, pdfDocument.getCatalog().getPdfObject());
            stream.getOutputStream().write(0);
            stream.getOutputStream().write(intToBytes(0));
            stream.getOutputStream().write(shortToBytes(0xFFFF));
            PdfXRefTable xref = pdfDocument.getXRef();
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference indirect = xref.get(i);
                if (indirect.getObjectStreamNumber() == 0) {
                    stream.getOutputStream().write(1);
                    //TODO check object stream writing in case long offsets
                    stream.getOutputStream().write(intToBytes((int) indirect.getOffset()));
                    stream.getOutputStream().write(shortToBytes(0));
                } else {
                    stream.getOutputStream().write(2);
                    stream.getOutputStream().write(intToBytes(indirect.getObjectStreamNumber()));
                    stream.getOutputStream().write(shortToBytes(indirect.getIndex()));
                }
            }
            stream.flush();
        } else {
            writeString("xref\n").
                    writeString("0 ").
                    writeInteger(pdfDocument.getXRef().size()).
                    writeString("\n0000000000 65535 f \n");
            PdfXRefTable xref = pdfDocument.getXRef();
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference indirect = xref.get(i);
                writeString(objectOffsetFormatter.format(indirect.getOffset())).
                        writeSpace().
                        writeString(objectGenerationFormatter.format(indirect.getGenNr()));
                if (indirect.getOffset() > 0) {
                    writeBytes(inUseXRefEntry);
                } else {
                    writeBytes(freeXRefEntry);
                }
            }
        }
        return strtxref;
    }

    /**
     * Writes trailer to PDF.
     *
     * @param startxref start of cross reference table.
     * @throws IOException
     */
    protected void writeTrailer(int startxref) throws IOException, PdfException {
        if (!fullCompression) {
            pdfDocument.getTrailer().setSize(pdfDocument.getXRef().size());
            writeString("trailer\n");
            write(pdfDocument.getTrailer().getPdfObject());
        }
        writeString("\nstartxref\n").
                writeInteger(startxref).
                writeString("\n%%EOF\n");
        pdfDocument.getXRef().clear();
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

    private byte[] shortToBytes(int n) {
        return new byte[]{(byte) ((n >> 8) & 0xFF), (byte) (n & 0xFF)};
    }

    private byte[] intToBytes(int n) {
        return new byte[]{(byte) ((n >> 24) & 0xFF), (byte) ((n >> 16) & 0xFF), (byte) ((n >> 8) & 0xFF), (byte) (n & 0xFF)};
    }
}
