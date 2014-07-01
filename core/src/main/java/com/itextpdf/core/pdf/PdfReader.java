package com.itextpdf.core.pdf;

import java.io.InputStream;

public class PdfReader {

    /**
     * Streams are closed automatically.
     */
    protected boolean closeStream = true;


    public PdfReader(InputStream is) {

    }

    public void close() {

    }

    public boolean isCloseStream() {
        return closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }
}
