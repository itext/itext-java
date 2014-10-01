package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfObjectStream extends PdfStream {

    /**
     * Max number of objects in object stream.
     */
    public static int maxObjStreamSize = 200;

    /**
     * Current object stream size (number of objects inside).
     */
    protected int size = 0;

    /**
     * Stream containing object indices, a heading part og object stream.
     */
    protected PdfOutputStream indexStream = new PdfOutputStream(new ByteArrayOutputStream());

    public PdfObjectStream(PdfDocument doc) {
        super(doc);
        outputStream.pdfDocument = doc;
        put(PdfName.Type, PdfName.ObjStm);
        put(PdfName.N, new PdfNumber(size));
        put(PdfName.First, new PdfNumber(indexStream.getCurrentPos()));
    }

    /**
     * Adds object to the object stream.
     *
     * @param object object to add.
     * @throws PdfException
     * @throws IOException
     */
    public void addObject(PdfObject object) throws PdfException, IOException {
        if (size == maxObjStreamSize)
            throw new PdfException(PdfException.ObjectCannotBeAddedToObjectStream);
        indexStream.writeInteger(object.getIndirectReference().getObjNr()).
                writeSpace().
                writeInteger(outputStream.getCurrentPos()).
                writeSpace();
        outputStream.write(object);
        object.getIndirectReference().setObjectStreamNumber(getIndirectReference().getObjNr());
        object.getIndirectReference().setIndex(size);
        outputStream.writeSpace();
        ((PdfNumber)get(PdfName.N)).setValue(++size);
        ((PdfNumber)get(PdfName.First)).setValue(indexStream.getCurrentPos());
    }

    /**
     * Gets object stream size (number of objects inside).
     *
     * @return object stream size.
     */
    public int getSize() {
        return size;
    }

    public PdfOutputStream getIndexStream() {
        return indexStream;
    }

}
