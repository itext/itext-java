package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfStream extends PdfDictionary {

    /**
     * Output stream associated with PDF stream.
     */
    protected PdfOutputStream outputStream = new PdfOutputStream(new ByteArrayOutputStream());

    public PdfStream(PdfDocument doc) {
        super(doc);
    }

    @Override
    protected void flush(PdfWriter writer) throws IOException, PdfException {
        if (isFlushed())
            return;
        super.flush(writer);
        if (isFlushed() && outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
    }

    /**
     * Gets output stream.
     *
     * @return output stream.
     */
    public PdfOutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public boolean canBeInObjStm() {
        return false;
    }

    @Override
    public byte getType() {
        return Stream;
    }
}
