package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfStream extends PdfDictionary {

    /**
     * Output stream associated with PDF stream.
     */
    protected PdfOutputStream outputStream = new PdfOutputStream(new ByteArrayOutputStream());

    public PdfStream(PdfDocument doc) {
        super();
        makeIndirect(doc);
    }

    @Override
    public void flush(boolean canBeInObjStm) throws IOException, PdfException {
        if (isFlushed())
            return;
        super.flush(canBeInObjStm);
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
    public byte getType() {
        return Stream;
    }

}
