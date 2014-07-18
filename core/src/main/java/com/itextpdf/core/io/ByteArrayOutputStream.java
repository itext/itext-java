package com.itextpdf.core.io;

public class ByteArrayOutputStream extends OutputStream {

    public ByteArrayOutputStream() {
        super(new java.io.ByteArrayOutputStream());
    }

    public byte[] getBytes() {
        return ((java.io.ByteArrayOutputStream)outputStream).toByteArray();
    }

}
