package com.itextpdf.core.pdf.objects;

import com.itextpdf.io.streams.ByteArrayOutputStream;
import com.itextpdf.core.pdf.PdfDocument;

import java.io.IOException;

public class PdfStream extends PdfDictionary {

    protected ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private PdfStream() {
        super();
        type = PdfObject.Stream;
    }

    public PdfStream(PdfDocument doc) {
        super(doc);
        type = PdfObject.Stream;
    }

    @Override
    public boolean flush() throws IOException {
        super.flush();
        if (flushed && outputStream != null) {
            outputStream.close();
            outputStream = null;
        }
        return flushed;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

}
