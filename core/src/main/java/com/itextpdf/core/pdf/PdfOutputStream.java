package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
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

    /**
     * Document associated with PdfOutputStream.
     */
    protected PdfDocument pdfDocument = null;

    public PdfOutputStream(java.io.OutputStream outputStream) {
        super(outputStream);
    }

    public PdfOutputStream write(PdfObject object) throws IOException, PdfException {
        switch (object.getType()) {
            case PdfObject.Array:
                write((PdfArray) object);
                break;
            case PdfObject.Dictionary:
                write((PdfDictionary) object);
                break;
            case PdfObject.IndirectReference:
                write((PdfIndirectReference) object);
                break;
            case PdfObject.Name:
            case PdfObject.Null:
            case PdfObject.String:
            case PdfObject.Boolean:
                write((PdfPrimitiveObject)object);
                break;
            case PdfObject.Number:
                write((PdfNumber)object);
                break;
            case PdfObject.Stream:
                write((PdfStream) object);
                break;
            default:
                break;
        }
        return this;
    }

    protected void write(PdfArray array) throws IOException, PdfException {
        writeByte((byte)'[');
        for (int i = 0; i < array.size(); i++) {
            PdfObject value = array.get(i);
            PdfIndirectReference indirectReference;
            if ((indirectReference = value.getIndirectReference()) != null) {
                write(indirectReference);
            } else {
                write(value);
            }
            if (i < array.size() - 1)
                writeSpace();
        }
        writeByte((byte)']');
    }

    protected void write(PdfDictionary dictionary) throws IOException, PdfException {
        writeBytes(openDict);
        for (Map.Entry<PdfName, PdfObject> entry : dictionary.entrySet()) {
            write(entry.getKey());
            writeSpace();
            PdfObject value = entry.getValue();
            PdfIndirectReference indirectReference;
            if ((indirectReference = value.getIndirectReference()) != null) {
                write(indirectReference);
            } else {
                write(value);
            }
        }
        writeBytes(closeDict);
    }

    protected void write(PdfIndirectReference indirectReference) throws IOException, PdfException {
//        if (indirectReference.getRefersTo() != null)
//            pdfDocument.addIndirectReference(indirectReference);
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

    protected void write(PdfPrimitiveObject primitive) throws IOException {
        write(primitive.getContent());
    }

    protected void write(PdfNumber primitive) throws IOException, PdfException {
        if (primitive.hasContent()) {
            write(primitive.getContent());
        } else if(primitive.getValueType() == PdfNumber.Int) {
            writeInteger(primitive.getIntValue());
        } else {
            writeDouble(primitive.getValue());
        }
    }

    protected void write(PdfStream stream) throws IOException, PdfException {
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
}
