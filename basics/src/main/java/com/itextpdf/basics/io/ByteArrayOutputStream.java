package com.itextpdf.basics.io;

public class ByteArrayOutputStream extends java.io.ByteArrayOutputStream {

    public ByteArrayOutputStream() {
        super();
    }

    public ByteArrayOutputStream(int size) {
        super(size);
    }

    public void assignBytes(byte[] bytes, int count) {
        buf = bytes;
        this.count = count;
    }

}
