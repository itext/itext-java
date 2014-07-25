package com.itextpdf.core.pdf;

import com.itextpdf.io.streams.OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class PdfOutputStream extends OutputStream {

    static private final byte[] stream = getIsoBytes("stream\n");
    static private final byte[] endstream = getIsoBytes("endstream\n");
    static private final byte[] openDict = getIsoBytes("<<");
    static private final byte[] closeDict = getIsoBytes(">>");
    static private final byte[] endIndirect = getIsoBytes(" R");
    static private final byte[] endIndirectWithZeroGenNr = getIsoBytes(" 0 R");


    public PdfOutputStream(java.io.OutputStream outputStream) {
        super(outputStream);
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
        writeByte((byte)'[');
        for (int i = 0; i < array.size(); i++) {
            PdfObject value = array.get(i);
            PdfIndirectReference indirectReference = value.getIndirectReference();
            if (indirectReference != null) {
                write(indirectReference);
            } else {
                write(value);
            }
            if (i < array.size() - 1)
                writeSpace();
        }
        writeByte((byte)']');
    }

    protected void write(PdfBoolean bool) throws IOException {
        writeBoolean(bool.getValue());
    }

    protected void write(PdfDictionary dictionary) throws IOException {
        writeBytes(openDict);
        for (Map.Entry<PdfName, PdfObject> entry : dictionary.entrySet()) {
            write(entry.getKey());
            writeSpace();
            PdfObject value = entry.getValue();
            PdfIndirectReference indirectReference = value.getIndirectReference();
            if (indirectReference != null) {
                write(indirectReference);
            } else {
                write(value);
            }
        }
        writeBytes(closeDict);
    }

    protected void write(PdfIndirectReference indirectReference) throws IOException {
        if (indirectReference.getGenNr() == 0) {
            writeInteger(indirectReference.getObjNr()).
                    writeBytes(endIndirectWithZeroGenNr);
        } else {
            writeInteger(indirectReference.getObjNr()).
                    writeSpace().
                    writeInteger(indirectReference.getGenNr()).
                    writeBytes(endIndirect);
        }
    }

    protected void write(PdfName name) throws IOException {
        writeByte((byte)'/').writeString(name.getValue());
    }

    protected void write(PdfNumber number) throws IOException {
        write(number.getContent());
    }

    protected void write(PdfStream stream) throws IOException {
        ByteArrayOutputStream byteStream = (ByteArrayOutputStream)stream.getOutputStream().getOutputStream();
        if (stream instanceof PdfObjectStream) {
            ByteArrayOutputStream indexStream = (ByteArrayOutputStream)((PdfObjectStream)stream).getIndexStream().getOutputStream();
            stream.put(PdfName.Length, new PdfNumber(byteStream.size() + indexStream.size()));
            write((PdfDictionary) stream);
            writeBytes(PdfOutputStream.stream);
            indexStream.writeTo(this);
            byteStream.writeTo(this);
            writeBytes(PdfOutputStream.endstream);
        } else {
            stream.put(PdfName.Length, new PdfNumber(byteStream.size()));
            write((PdfDictionary) stream);
            writeBytes(PdfOutputStream.stream);
            byteStream.writeTo(this);
            writeBytes(PdfOutputStream.endstream);
        }
    }

    protected void write(PdfString string) throws IOException {
        writeByte((byte)'(').
                writeString(string.getValue()).
                writeByte((byte)')');
    }

}
