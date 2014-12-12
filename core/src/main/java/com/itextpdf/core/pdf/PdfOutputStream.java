package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.basics.io.OutputStream;

import java.io.IOException;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class PdfOutputStream extends OutputStream {

    // A possible compression level.
    public static final int DEFAULT_COMPRESSION = Deflater.DEFAULT_COMPRESSION;
    // A possible compression level.
    public static final int NO_COMPRESSION = Deflater.NO_COMPRESSION;
    // A possible compression level.
    public static final int BEST_SPEED = Deflater.BEST_SPEED;
    // A possible compression level.
    public static final int BEST_COMPRESSION = Deflater.BEST_COMPRESSION;

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

    protected void write(PdfStream pdfStream) throws PdfException {
        try {
            //When document is opened in stamping mode the output stream can be uninitialized.
            //We shave to initialize it and write all data from streams input to streams output.
            if (pdfStream.getOutputStream() == null && pdfStream.getReader() != null) {
                byte[] bytes = pdfStream.getBytes(false);
                pdfStream.initOutputStream(null);
                pdfStream.getOutputStream().write(bytes);
            }
            assert pdfStream.getOutputStream() != null : "PdfStream lost OutputStream";
            pdfStream.getOutputStream().flush();
            ByteArrayOutputStream byteStream = getFinalStream(pdfStream);
            pdfStream.put(PdfName.Length, new PdfNumber(byteStream.size()));
            write((PdfDictionary) pdfStream);
            writeBytes(PdfOutputStream.stream);
            byteStream.writeTo(this);
            writeBytes(PdfOutputStream.endstream);
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotWritePdfStream, e, pdfStream);
        }
    }

    /**
     * Compresses the stream.
     */
    protected static ByteArrayOutputStream getFinalStream(PdfStream pdfStream) throws PdfException {
        boolean compress = true;
        if (pdfStream.getCompressionLevel() == NO_COMPRESSION) {
            compress = false;
        }
        // check if a filter already exists
        PdfObject filter = pdfStream.get(PdfName.Filter);
        if (filter != null) {
            if (filter.getType() == PdfObject.Name) {
                if (PdfName.FlateDecode.equals(filter))
                    compress = false;
            }
            else if (filter.getType() == PdfObject.Array) {
                if (((PdfArray) filter).contains(PdfName.FlateDecode))
                    compress = false;
            }
            else {
                throw new PdfException(PdfException.StreamCouldNotBeCompressedFilterIsNotANameOrArray);
            }
        }
        try {
            ByteArrayOutputStream stream;
            if (!compress) {
                if (pdfStream instanceof PdfObjectStream) {
                    PdfObjectStream objectStream = (PdfObjectStream)pdfStream;
                    stream = new ByteArrayOutputStream();
                    ((ByteArrayOutputStream) objectStream.getIndexStream().getOutputStream()).writeTo(stream);
                    ((ByteArrayOutputStream) objectStream.getOutputStream().getOutputStream()).writeTo(stream);
                } else {
                    if (pdfStream.getOutputStream() == null && pdfStream.getReader() != null) {
                        byte[] bytes = pdfStream.getBytes(false);
                        stream = new ByteArrayOutputStream();
                        stream.write(bytes);
                    } else {
                        assert pdfStream.getOutputStream() != null : "Error in outputStream";
                        stream = (ByteArrayOutputStream) pdfStream.getOutputStream().getOutputStream();
                    }
                }
            } else {
                // compress
                stream = new ByteArrayOutputStream();
                Deflater deflater = new Deflater(pdfStream.getCompressionLevel());
                DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
                if (pdfStream instanceof PdfObjectStream) {
                    PdfObjectStream objectStream = (PdfObjectStream) pdfStream;
                    ((ByteArrayOutputStream) objectStream.getIndexStream().getOutputStream()).writeTo(zip);
                    ((ByteArrayOutputStream) objectStream.getOutputStream().getOutputStream()).writeTo(zip);
                } else {
                    if (pdfStream.getOutputStream() == null && pdfStream.getReader() != null) {
                        byte[] bytes = pdfStream.getBytes(false);
                        zip.write(bytes);
                    } else {
                        assert pdfStream.getOutputStream() != null : "Error in outputStream";
                        ((ByteArrayOutputStream) pdfStream.getOutputStream().getOutputStream()).writeTo(zip);
                    }
                }

                zip.close();
                deflater.end();
                if (filter == null) {
                    pdfStream.put(PdfName.Filter, PdfName.FlateDecode);
                } else {
                    PdfArray filters = new PdfArray();
                    filters.add(PdfName.FlateDecode);
                    filters.add(filter);
                    pdfStream.put(PdfName.Filter, filters);
                }
            }
            return stream;
        }
        catch(IOException ioe) {
            throw new PdfException(PdfException.FlateCompressException, ioe);
        }
    }
}
