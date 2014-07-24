package com.itextpdf.io.streams;

import java.io.IOException;

public class OutputStream extends java.io.OutputStream {

    protected java.io.OutputStream outputStream = null;
    protected int currentPos = 0;

    public static final byte[] getIsoBytes(String text) {
        if (text == null)
            return null;
        int len = text.length();
        byte b[] = new byte[len];
        for (int k = 0; k < len; ++k)
            b[k] = (byte) text.charAt(k);
        return b;
    }

    public OutputStream(java.io.OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        currentPos++;
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
        currentPos += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        currentPos += len;
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    public OutputStream writeInteger(int value) throws IOException {
        write(getIsoBytes(String.valueOf(value)));
        return this;
    }

    public OutputStream writeFloat(float value) throws IOException {
        write(getIsoBytes(String.valueOf(value)));
        return this;
    }

    public OutputStream writeDouble(double value) throws IOException {
        write(getIsoBytes(String.valueOf(value)));
        return this;
    }

    public OutputStream writeByte(byte value) throws IOException {
        write(value);
        return this;
    }

    public OutputStream writeChar(char value) throws IOException {
        write(getIsoBytes(String.valueOf(value)));
        return this;
    }

    public OutputStream writeSpace() throws IOException {
        write((byte)' ');
        return this;
    }

    public OutputStream writeString(String value) throws IOException {
        write(getIsoBytes(value));
        return this;
    }

    public OutputStream writeBoolean(boolean value) throws IOException {
        write(getIsoBytes(String.valueOf(value)));
        return this;
    }

    public OutputStream writeBytes(byte[] value) throws IOException {
        write(value);
        return this;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public java.io.OutputStream getOutputStream() {
        return outputStream;
    }


}
