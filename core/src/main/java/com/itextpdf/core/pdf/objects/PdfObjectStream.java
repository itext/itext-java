package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfOutputStream;
import com.itextpdf.io.streams.ByteArrayOutputStream;

import java.io.IOException;

public class PdfObjectStream extends PdfStream {

    /**
     * Max number of objects in object stream.
     */
    public static int maxObjStreamSize = 200;

    /**
     * Current object stream size.
     */
    protected int size = 0;

    protected ByteArrayOutputStream indexStream = new ByteArrayOutputStream();

    public PdfObjectStream(PdfDocument doc) {
        super(doc);
    }

    public void addObject(PdfObject object) throws PdfException, IOException {
        if (!object.canBeInObjStm() || size == maxObjStreamSize)
            throw new PdfException(PdfException.objectCannotBeAddedToObjectStream);
        indexStream.writeInteger(object.getIndirectReference().getObjNr()).
                writeChar(' ').
                writeInteger(outputStream.getCurrentPos()).
                writeChar(' ');
        ((PdfOutputStream) outputStream).write(object);
        object.offset = size;
        object.objectStream = this;
        outputStream.writeChar(' ');
        size++;
    }

    public int getSize() {
        return size;
    }

    @Override
    public byte[] getBytes() {
        byte[] indexStreamBytes = indexStream.getBytes();
        byte[] outputStreamBytes = ((ByteArrayOutputStream) outputStream.getOutputStream()).getBytes();
        byte[] bytes = new byte[indexStreamBytes.length + outputStreamBytes.length];
        System.arraycopy(indexStreamBytes, 0, bytes, 0, indexStreamBytes.length);
        System.arraycopy(outputStreamBytes, 0, bytes, indexStreamBytes.length, outputStreamBytes.length);
        return bytes;
    }

    @Override
    public boolean flush() throws IOException, PdfException {
        if (flushed)
            return true;
        put(PdfName.Type, PdfName.ObjStm);
        put(PdfName.N, new PdfNumber(size));
        put(PdfName.First, new PdfNumber(indexStream.getCurrentPos()));
        super.flush();
        if (flushed && indexStream != null) {
            indexStream.close();
            indexStream = null;
        }
        return flushed;
    }

    @Override
    protected void initOutputStream() {
        outputStream = new PdfOutputStream(new ByteArrayOutputStream());
    }
}
