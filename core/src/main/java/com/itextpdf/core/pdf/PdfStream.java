package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

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
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        super.copyContent(from, document);
        PdfStream stream = (PdfStream) from;
        if (stream.outputStream != null && stream.outputStream.getOutputStream() != null && stream.outputStream.getOutputStream() instanceof ByteArrayOutputStream) {
            try {
                stream.outputStream.getOutputStream().flush();
                byte[] bytes = ((ByteArrayOutputStream) stream.outputStream.getOutputStream()).toByteArray();
                outputStream.write(bytes);
            } catch (IOException ioe) {
                throw new PdfException(PdfException.CannotCopyObjectContent, ioe);
            }
        }
    }
}
