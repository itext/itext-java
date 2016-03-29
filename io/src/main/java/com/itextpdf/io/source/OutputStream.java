package com.itextpdf.io.source;

import com.itextpdf.io.IOException;

public class OutputStream<T extends java.io.OutputStream> extends java.io.OutputStream {

    //long=19 + max frac=6 => 26 => round to 32.
    private final ByteBuffer numBuffer = new ByteBuffer(32);

    protected java.io.OutputStream outputStream = null;
    protected long currentPos = 0;
    protected boolean closeStream = true;

    public static boolean getHighPrecision() {
        return ByteUtils.HighPrecision;
    }

    public static void setHighPrecision(boolean value) {
        ByteUtils.HighPrecision = value;
    }

    public OutputStream(java.io.OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws java.io.IOException {
        outputStream.write(b);
        currentPos++;
    }

    @Override
    public void write(byte[] b) throws java.io.IOException {
        outputStream.write(b);
        currentPos += b.length;
    }

    @Override
    public void write(byte[] b, int off, int len) throws java.io.IOException {
        outputStream.write(b, off, len);
        currentPos += len;
    }

    public void writeByte(byte value) {
        try {
            write(value);
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteByte, e);
        }
    }

    @Override
    public void flush() throws java.io.IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws java.io.IOException {
        if (closeStream)
            outputStream.close();
    }

    public T writeLong(long value) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteIntNumber, e);
        }
    }

    public T writeInteger(int value) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset());
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteIntNumber, e);
        }
    }

    public T writeFloat(float value) {
        return writeFloat(value, ByteUtils.HighPrecision);
    }

    public T writeFloat(float value, boolean highPrecision) {
        return writeDouble(value, highPrecision);
    }

    public T writeFloats(float[] value) {
        for (int i = 0; i < value.length; i++) {
            writeFloat(value[i]);
            if (i < value.length - 1)
                writeSpace();
        }
        return (T) this;
    }

    public T writeDouble(double value) {
        return writeDouble(value, ByteUtils.HighPrecision);
    }

    public T writeDouble(double value, boolean highPrecision) {
        try {
            ByteUtils.getIsoBytes(value, numBuffer.reset(), highPrecision);
            write(numBuffer.getInternalBuffer(), numBuffer.capacity() - numBuffer.size(), numBuffer.size());
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteFloatNumber, e);
        }
    }

    public T writeByte(int value) {
        try {
            write(value);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteByte, e);
        }
    }

    public T writeSpace() {
        return writeByte(' ');
    }

    public T writeNewLine() {
        return writeByte('\n');
    }

    public T writeString(String value) {
        return writeBytes(ByteUtils.getIsoBytes(value));
    }

    public T writeBytes(byte[] b) {
        try {
            write(b);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteBytes, e);
        }
    }

    public T writeBytes(byte[] b, int off, int len) {
        try {
            write(b, off, len);
            return (T) this;
        } catch (java.io.IOException e) {
            throw new IOException(IOException.CannotWriteBytes, e);
        }
    }

    public long getCurrentPos() {
        return currentPos;
    }

    public java.io.OutputStream getOutputStream() {
        return outputStream;
    }

    public boolean isCloseStream() {
        return closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }

    public void assignBytes(byte[] bytes, int count) {
        if (outputStream instanceof ByteArrayOutputStream) {
            ((ByteArrayOutputStream) outputStream).assignBytes(bytes, count);
            currentPos = count;
        } else
            throw new IOException(IOException.BytesCanBeAssignedToByteArrayOutputStreamOnly);
    }

    public void reset() {
        if (outputStream instanceof ByteArrayOutputStream) {
            ((ByteArrayOutputStream) outputStream).reset();
            currentPos = 0;
        } else
            throw new IOException(IOException.BytesCanBeResetInByteArrayOutputStreamOnly);
    }
}
