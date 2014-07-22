package com.itextpdf.core.pdf;

import com.itextpdf.core.pdf.objects.*;
import com.itextpdf.io.streams.OutputStream;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

public class PdfWriter extends OutputStream {

    protected int indirectReferenceNumber = 0;
    protected TreeSet<PdfIndirectReference> indirects = new TreeSet<PdfIndirectReference>();
    static final DecimalFormat objectOffsetFormatter = new DecimalFormat("0000000000");

    public PdfWriter(java.io.OutputStream os) {
        super(os);
    }

    public void add(PdfIndirectReference indirectReference) throws IOException {
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

    public void write(PdfObject object) throws IOException {
        switch (object.getType()) {
            case PdfObject.Array:
                write((PdfArray) object);
                break;
            case PdfObject.Boolean:
                write((PdfBoolean) object);
                break;
            case PdfObject.Dictionary:
                write((PdfDictionary) object);
                break;
            case PdfObject.IndirectReference:
                write((PdfIndirectReference) object);
                break;
            case PdfObject.Name:
                write((PdfName) object);
                break;
            case PdfObject.Number:
                write((PdfNumber) object);
                break;
            case PdfObject.Stream:
                write((PdfStream) object);
                break;
            case PdfObject.String:
                write((PdfString) object);
                break;
            default:
                break;
        }
    }

    protected void write(PdfArray array) throws IOException {
        writeChar('[');
        for (int i = 0; i < array.size(); i++) {
            PdfObject value = array.get(i);
            PdfIndirectReference indirectReference = value.getIndirectReference();
            if (indirectReference != null) {
                indirects.add(indirectReference);
                write(indirectReference);
            } else {
                write(value);
            }
            if (i < array.size() - 1)
                writeChar(' ');
        }
        writeChar(']');
    }

    protected void write(PdfBoolean bool) throws IOException {
        writeBoolean(bool.getValue());
    }

    protected void write(PdfDictionary dictionary) throws IOException {
        writeString("<<");
        for (Map.Entry<PdfName, PdfObject> entry : dictionary.entrySet()) {
            write(entry.getKey());
            writeChar(' ');
            PdfObject value = entry.getValue();
            PdfIndirectReference indirectReference = value.getIndirectReference();
            if (indirectReference != null) {
                indirects.add(indirectReference);
                write(indirectReference);
            } else {
                write(value);
            }
        }
        writeString(">>");
    }

    protected void write(PdfIndirectReference indirectReference) throws IOException {
        writeInteger(indirectReference.getObjNr()).
                writeChar(' ').
                writeInteger(indirectReference.getGenNr()).
                writeString(" R");
    }

    protected void write(PdfName name) throws IOException {
        writeChar('/').writeString(name.getValue());
    }

    protected void write(PdfNumber number) throws IOException {
        write(number.getContent());
    }

    protected void write(PdfStream stream) throws IOException {
        byte[] bytes = stream.getOutputStream().getBytes();
        stream.put(PdfName.Length, new PdfNumber(bytes.length));
        write((PdfDictionary) stream);
        writeString("stream\n").
                writeBytes(bytes).
                writeString("\nendstream");
    }

    protected void write(PdfString string) throws IOException {
        writeChar('(').
                writeString(string.getValue()).
                writeChar(')');
    }

    protected void writeHeader() throws IOException {
        writeString("%PDF-1.7").
                writeString("\n%\u00e2\u00e3\u00cf\u00d3\n");
    }

    protected void flushWaitingObjects() throws IOException {
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
    }

    protected int writeXRefTable() throws IOException {
        int strtxref = currentPos;
        writeString("xref\n").
                writeString("0 ").
                writeInteger(indirects.size() + 1).
                writeString("\n0000000000 65535 f \n");
        for (PdfIndirectReference indirect : indirects) {
            writeString(objectOffsetFormatter.format(indirect.getRefersTo().getOffset())).
                    writeString(" 00000 n \n");
        }
        return strtxref;
    }

    protected void writeTrailer(PdfTrailer trailer, int startxref) throws IOException {
        trailer.setSize(indirects.size() + 1);
        writeString("trailer\n");
        write(trailer);
        writeString("\nstartxref\n").
                writeInteger(startxref).
                writeString("\n%%EOF\n");
    }

}
