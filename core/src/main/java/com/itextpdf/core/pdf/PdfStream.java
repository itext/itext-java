package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfStream extends PdfDictionary {

    /**
     * Output stream associated with PDF stream.
     */
    protected OutputStream outputStream = new PdfOutputStream(new ByteArrayOutputStream());

    public PdfStream(PdfDocument doc) {
        super(doc);
        type = PdfObject.Stream;
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

    @Override
    public boolean canBeInObjStm() {
        return false;
    }

}
