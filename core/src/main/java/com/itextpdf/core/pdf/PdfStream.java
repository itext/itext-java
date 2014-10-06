package com.itextpdf.core.pdf;

import java.io.ByteArrayOutputStream;

public class PdfStream extends PdfDictionary {

    /**
     * Output stream associated with PDF stream.
     */
    protected PdfOutputStream outputStream = new PdfOutputStream(new ByteArrayOutputStream());

    public PdfStream(PdfDocument doc) {
        super();
        makeIndirect(doc);
    }

    private PdfStream() {
        super();
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

    @Override
    protected PdfStream newInstance() {
        return new PdfStream();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);

    }
}
