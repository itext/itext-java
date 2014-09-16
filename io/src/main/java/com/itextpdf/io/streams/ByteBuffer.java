package com.itextpdf.io.streams;

public class ByteBuffer {

    protected int count;
    private byte buffer[];

    public ByteBuffer(int size) {
        buffer = new byte[size];
    }

    public ByteBuffer append(byte b[]) {
        System.arraycopy(b, 0, buffer, count, b.length);
        count += b.length;
        return this;
    }

    public ByteBuffer append(byte b) {
        buffer[count] = b;
        count++;
        return this;
    }

    public ByteBuffer append(int b) {
        return append((byte)b);
    }

    public ByteBuffer append(String str) {
        return append(OutputStream.getIsoBytes(str));
    }


    public ByteBuffer reset() {
        count = 0; return this;
    }

    public byte[] toByteArray() {
        byte newBuf[] = new byte[count];
        System.arraycopy(buffer, startPos(), newBuf, 0, count);
        return newBuf;
    }

    public int size() {
        return count;
    }

    int startPos() {
        return 0;
    }
}