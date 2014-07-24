package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.pdf.objects.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

public class PdfWriter extends PdfOutputStream {

    static final DecimalFormat objectOffsetFormatter = new DecimalFormat("0000000000");

    protected int indirectReferenceNumber = 0;
    protected TreeSet<PdfIndirectReference> indirects = new TreeSet<PdfIndirectReference>();
    protected boolean fullCompression = false;
    protected PdfObjectStream objectStream = null;
    protected PdfDocument pdfDocument = null;

    public PdfWriter(java.io.OutputStream os) {
        super(os);
    }

    public void add(PdfIndirectReference indirectReference) throws IOException, PdfException {
        indirects.add(indirectReference);
    }

    public void writeToBody(PdfObject object) throws IOException {
        writeInteger(object.getIndirectReference().getObjNr()).
                writeChar(' ').
                writeInteger(object.getIndirectReference().getGenNr()).writeString(" obj\n");
        write(object);
        writeString("\nendobj\n");
    }

    public int getNextIndirectReferenceNumber() {
        return ++indirectReferenceNumber;
    }

    public boolean isFullCompression() {
        return fullCompression;
    }

    public void setFullCompression(boolean fullCompression) {
        this.fullCompression = fullCompression;
    }

    public PdfObjectStream getObjectStream() throws IOException, PdfException {
        if (objectStream == null) {
            objectStream = new PdfObjectStream(pdfDocument);
        }
        if (objectStream.getSize() == PdfObjectStream.maxObjStreamSize) {
            objectStream.flush();
            objectStream = new PdfObjectStream(pdfDocument);
        }
        return objectStream;
    }

    protected void writeHeader(PdfVersion pdfVersion) throws IOException {
        writeChar('%').
                writeString(pdfVersion.getPdfVersion()).
                writeString("\n%\u00e2\u00e3\u00cf\u00d3\n");
    }

    protected void flushWaitingObjects() throws IOException, PdfException {
        Iterator<PdfIndirectReference> it = indirects.iterator();
        PdfIndirectReference indirectReference = null;
        if (it.hasNext()) {
            indirectReference = it.next();
            for (; ; ) {
                NavigableSet<PdfIndirectReference> newIndirects = indirects.subSet(indirectReference, false, indirects.last(), true);
                if (newIndirects.isEmpty())
                    break;
                Object[] newIndirectsArray = newIndirects.toArray();
                for (int i = 0; i < newIndirectsArray.length; i++) {
                    indirectReference = (PdfIndirectReference) newIndirectsArray[i];
                    PdfObject object = indirectReference.getRefersTo();
                    object.flush();
                }
            }
        }
        if (objectStream != null && objectStream.getSize() > 0) {
            objectStream.flush();
            objectStream = null;
        }
    }

    protected int writeXRefTable() throws IOException, PdfException {
        int strtxref = currentPos;
        if (fullCompression) {
            PdfStream stream = new PdfStream(pdfDocument);
            stream.put(PdfName.Type, PdfName.XRef);
            stream.put(PdfName.Size, new PdfNumber(indirects.size() + 1));
            stream.put(PdfName.W, new PdfArray(new ArrayList() {{
                add(new PdfNumber(1));
                add(new PdfNumber(2));
                add(new PdfNumber(2));
            }}));
            stream.put(PdfName.Info, pdfDocument.trailer.getDocumentInfo());
            stream.put(PdfName.Root, pdfDocument.trailer.getCatalog());
            stream.getOutputStream().write((byte) 0);
            stream.getOutputStream().write((short) 0x0000);
            stream.getOutputStream().write((short) 0xFFFF);
            for (PdfIndirectReference indirect : indirects) {
                PdfObject refersTo = indirect.getRefersTo();
                if (refersTo.getObjectStream() != null) {
                    stream.getOutputStream().write((byte) 2);
                    stream.getOutputStream().write((short) refersTo.getObjectStream().getIndirectReference().getObjNr());
                    stream.getOutputStream().write((short) refersTo.getOffset());
                } else {
                    stream.getOutputStream().write((byte) 1);
                    stream.getOutputStream().write((short) refersTo.getOffset());
                    stream.getOutputStream().write((short) 0);
                }
            }
            stream.flush();
        } else {
            writeString("xref\n").
                    writeString("0 ").
                    writeInteger(indirects.size() + 1).
                    writeString("\n0000000000 65535 f \n");
            for (PdfIndirectReference indirect : indirects) {
                writeString(objectOffsetFormatter.format(indirect.getRefersTo().getOffset())).
                        writeString(" 00000 n \n");
            }
        }
        return strtxref;
    }

    protected void writeTrailer(PdfTrailer trailer, int startxref) throws IOException {
        if (fullCompression) {

        } else {
            trailer.setSize(indirects.size() + 1);
            writeString("trailer\n");
            write(trailer);
        }
        writeString("\nstartxref\n").
                writeInteger(startxref).
                writeString("\n%%EOF\n");
        indirects.clear();
    }

}
