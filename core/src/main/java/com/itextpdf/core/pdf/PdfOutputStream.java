package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.basics.io.OutputStream;

import java.io.IOException;
import java.util.Map;

public class PdfOutputStream extends OutputStream {

    static private final byte[] stream = getIsoBytes("stream\n");
    static private final byte[] endstream = getIsoBytes("\nendstream\n");
    static private final byte[] openDict = getIsoBytes("<<");
    static private final byte[] closeDict = getIsoBytes(">>");
    static private final byte[] endIndirect = getIsoBytes(" R");
    static private final byte[] endIndirectWithZeroGenNr = getIsoBytes(" 0 R");

    /**
     * Document associated with PdfOutputStream.
     */
    protected PdfDocument document = null;

    public PdfOutputStream(java.io.OutputStream outputStream) {
        super(outputStream);
    }

    public PdfOutputStream write(PdfObject object) throws PdfException {
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
                write((PdfName)object);
                break;
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

    protected void write(PdfArray array) throws PdfException {
        writeByte((byte)'[');
        for (int i = 0; i < array.size(); i++) {
            PdfObject value = array.get(i, false);
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

    protected void write(PdfDictionary dictionary) throws PdfException {
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

    protected void write(PdfIndirectReference indirectReference) throws PdfException {
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

    protected void write(PdfPrimitiveObject primitive) throws PdfException {
        writeBytes(primitive.getContent());
    }

    protected void write(PdfName name) throws PdfException {
        writeByte((byte)'/');
        writeBytes(name.getContent());
    }

    protected void write(PdfNumber number) throws PdfException {
        if (number.hasContent()) {
            writeBytes(number.getContent());
        } else if(number.getValueType() == PdfNumber.Int) {
            writeInteger(number.getIntValue());
        } else {
            writeDouble(number.getValue());
        }
    }

    protected void write(PdfStream stream) throws PdfException {
        try {
            //When document is opened in stamping mode the output stream can be uninitialized.
            //We shave to initialize it and write all data from streams input to streams output.
            if (stream.getOutputStream() == null && stream.getReader() != null) {
                byte[] bytes = stream.getBytes(false);
                stream.initOutputStream();
                stream.getOutputStream().write(bytes);
            }
            assert stream.getOutputStream() != null : "PdfStream lost OutputStream";
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
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotWritePdfStream, e, stream);
        }
    }
}
