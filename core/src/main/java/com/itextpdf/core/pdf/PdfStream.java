package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.ByteArrayOutputStream;
import com.itextpdf.io.streams.OutputStream;

import java.io.IOException;

public class PdfStream extends PdfDictionary {

    /**
     * Output stream associated with PDF stream.
     */
    protected OutputStream outputStream = null;

    public PdfStream(PdfDocument doc) {
        super(doc);
        type = PdfObject.Stream;
        initOutputStream();
    }

    @Override
    public boolean flush() throws IOException, PdfException {
        if (flushed)
            return true;
        super.flush();
        if (flushed && outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
        return flushed;
    }

    /**
     * Gets output stream.
     *
     * @return output stream.
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Gets stream bytes.
     *
     * @return stream bytes.
     */
    public byte[] getBytes() {
        return ((ByteArrayOutputStream) outputStream).getBytes();
    }

    @Override
    public boolean canBeInObjStm() {
        return false;
    }

    /**
     * Initializes output stream.
     */
    protected void initOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

}
