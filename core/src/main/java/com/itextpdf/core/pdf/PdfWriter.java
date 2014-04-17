package com.itextpdf.core.pdf;

import java.io.OutputStream;

public class PdfWriter {

    protected boolean closeStream = true;

    public PdfWriter(OutputStream os) {

    }

    public void close() {
    }

    public boolean getCloseStream() {
        return closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }


}
