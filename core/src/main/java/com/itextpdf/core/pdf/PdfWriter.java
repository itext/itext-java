package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

public class PdfWriter extends PdfOutputStream {

    static final DecimalFormat objectOffsetFormatter = new DecimalFormat("0000000000");
    static private final byte[] obj = getIsoBytes(" obj\n");
    static private final byte[] endobj = getIsoBytes("\nendobj\n");
    static private final byte[] endXRefEntry = getIsoBytes(" 00000 n \n");

    /**
     * Indicates if to use full compression (using object streams).
     */
    protected boolean fullCompression = false;

    /**
     * Currently active object stream.
     * Objects are written to the object stream if fullCompression set to true.
     */
    protected PdfObjectStream objectStream = null;

    private byte[] intToBytes(int n) {
        byte[] bytes = new byte[]{(byte) ((n >> 24) & 0xFF), (byte) ((n >> 16) & 0xFF), (byte) ((n >> 8) & 0xFF), (byte) (n & 0xFF)};
        return bytes;
    }

    private byte[] shortToBytes(int n) {
        byte[] bytes = new byte[]{(byte) ((n >> 8) & 0xFF), (byte) (n & 0xFF)};
        return bytes;
    }

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
        }
        if (objectStream.getSize() == PdfObjectStream.maxObjStreamSize) {
            objectStream.flush();
            objectStream = new PdfObjectStream(pdfDocument);
        }
        return objectStream;
    }

    /**
     * Flushes the object. Override this method if you want to define cusomt behaviour for object flushing.
     *
     * @param object object to flush.
     * @throws IOException
     * @throws PdfException
     */
    protected void flushObject(PdfObject object) throws IOException, PdfException {
        object.flush(this);
    }

    /**
     * Writes object to body of PDF document.
     *
     * @param object object to write.
     * @throws IOException
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
     * @throws IOException
     * @throws PdfException
     */
    protected void flushWaitingObjects() throws IOException, PdfException {
        TreeSet<PdfIndirectReference> indirectsCopy = new TreeSet<PdfIndirectReference>();
        PdfIndirectReference indirectReference;
        for (; ; ) {
            NavigableSet<PdfIndirectReference> indirects = pdfDocument.getIndirects();
            if (indirects.isEmpty())
                break;
            Object[] indirectsArray = indirects.toArray();
            for (Object newIndirectReference : indirectsArray) {
                indirectReference = (PdfIndirectReference) newIndirectReference;
                indirectsCopy.add(indirectReference);
                PdfObject object = indirectReference.getRefersTo();
                if (object != null) {
                    object.flush();
                }
            }
            pdfDocument.getIndirects().removeAll(Arrays.asList(indirectsArray));
        }
        pdfDocument.setIndirects(indirectsCopy);
        if (objectStream != null && objectStream.getSize() > 0) {
            objectStream.flush();
            objectStream = null;
        }
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
            stream.put(PdfName.Size, new PdfNumber(pdfDocument.getIndirects().size() + 1));
            stream.put(PdfName.W, new PdfArray(new ArrayList<PdfObject>() {{
                add(new PdfNumber(1));
                add(new PdfNumber(4));
                add(new PdfNumber(2));
            }}));
            stream.put(PdfName.Info, pdfDocument.trailer.getDocumentInfo());
            stream.put(PdfName.Root, pdfDocument.trailer.getCatalog());
            stream.getOutputStream().write(0);
            stream.getOutputStream().write(intToBytes(0));
            stream.getOutputStream().write(shortToBytes(0xFFFF));
            for (PdfIndirectReference indirect : pdfDocument.getIndirects()) {
                if (indirect.getObjectStream() != null) {
                    stream.getOutputStream().write(2);
                    stream.getOutputStream().write(intToBytes(indirect.getObjectStream().getIndirectReference().getObjNr()));
                    stream.getOutputStream().write(shortToBytes(indirect.getOffset()));
                } else {
                    stream.getOutputStream().write(1);
                    stream.getOutputStream().write(intToBytes(indirect.getOffset()));
                    stream.getOutputStream().write(shortToBytes(0));
                }
            }
            stream.flush();
        } else {
            writeString("xref\n").
                    writeString("0 ").
                    writeInteger(pdfDocument.getIndirects().size() + 1).
                    writeString("\n0000000000 65535 f \n");
            for (PdfIndirectReference indirect : pdfDocument.getIndirects()) {
                writeString(objectOffsetFormatter.format(indirect.getOffset())).
                        writeBytes(endXRefEntry);
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
            pdfDocument.getTrailer().setSize(pdfDocument.getIndirects().size() + 1);
            writeString("trailer\n");
            write(pdfDocument.getTrailer());
        }
        writeString("\nstartxref\n").
                writeInteger(startxref).
                writeString("\n%%EOF\n");
        pdfDocument.getIndirects().clear();
    }
}
