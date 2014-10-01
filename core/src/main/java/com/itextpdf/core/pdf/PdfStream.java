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
