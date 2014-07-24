package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.ByteArrayOutputStream;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.io.streams.OutputStream;

import java.io.IOException;

public class PdfStream extends PdfDictionary {

    protected OutputStream outputStream = new ByteArrayOutputStream();

    private PdfStream() {
        super();
        type = PdfObject.Stream;
        initOutputStream();
    }

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

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public byte[] getBytes() {
        return ((ByteArrayOutputStream)outputStream).getBytes();
    }

    @Override
    public boolean canBeInObjStm() {
        return false;
    }

    protected void initOutputStream() {
        outputStream = new ByteArrayOutputStream();
    }

}
