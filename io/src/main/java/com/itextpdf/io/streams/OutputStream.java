package com.itextpdf.io.streams;

import java.io.IOException;

public class OutputStream extends java.io.OutputStream {

    static private final byte[] booleanTrue = getIsoBytes("true");
    static private final byte[] booleanFalse = getIsoBytes("false");

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
        writeInteger(value, 8);
        return this;
    }

    public OutputStream writeInteger(int value, int maxLen) throws IOException {
        byte buf[] = new byte[maxLen];
        int i = maxLen - 1;
        for (; ; ) {
            buf[i] = (byte) (value % 10 + 0x30);
            value = value / 10;
            if (value == 0)
                break;
            i--;
        }
        write(buf, i, maxLen - i);
        return this;
    }

    public OutputStream writeFloat(float value) throws IOException {
        int intPart = (int) value;
        int fractalPart = (int) (value - (int) value) * 1000;
        writeInteger(intPart, 8).writeByte((byte) '.').writeInteger(fractalPart, 4);
        return this;
    }

    public OutputStream writeDouble(double value) throws IOException {
        return writeFloat((float)value);
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
        write((byte) ' ');
        return this;
    }

    public OutputStream writeString(String value) throws IOException {
        write(getIsoBytes(value));
        return this;
    }

    public OutputStream writeBoolean(boolean value) throws IOException {
        write(value ? booleanTrue : booleanFalse);
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
