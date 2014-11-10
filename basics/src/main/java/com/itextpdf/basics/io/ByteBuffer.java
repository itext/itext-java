package com.itextpdf.basics.io;

public class ByteBuffer {

    private static final byte[] bytes = new byte[] {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    protected int count;
    private byte[] buffer;

    public ByteBuffer() {
        this(128);
    }

    public ByteBuffer(int size) {
        if (size < 1)
            size = 128;
        buffer = new byte[size];
    }

    public static int getHex(int v) {
        if (v >= '0' && v <= '9')
            return v - '0';
        if (v >= 'A' && v <= 'F')
            return v - 'A' + 10;
        if (v >= 'a' && v <= 'f')
            return v - 'a' + 10;
        return -1;
    }

    public ByteBuffer append(byte b) {
        int newCount = count + 1;
        if (newCount > buffer.length) {
            byte[] newBuffer = new byte[Math.max(buffer.length << 1, newCount)];
            System.arraycopy(buffer, 0, newBuffer, 0, count);
            buffer = newBuffer;
        }
        buffer[count] = b;
        count = newCount;
        return this;
    }

    public ByteBuffer append(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0) || len == 0)
            return this;
        int newCount = count + len;
        if (newCount > buffer.length) {
            byte[] newBuffer = new byte[Math.max(buffer.length << 1, newCount)];
            System.arraycopy(buffer, 0, newBuffer, 0, count);
            buffer = newBuffer;
        }
        System.arraycopy(b, off, buffer, count, len);
        count = newCount;
        return this;
    }

    public ByteBuffer append(byte[] b) {
        return append(b, 0, b.length);
    }

    public ByteBuffer append(int b) {
        return append((byte)b);
    }

    public ByteBuffer append(String str) {
        return append(OutputStream.getIsoBytes(str));
    }

    public ByteBuffer appendHex(byte b) {
        append(bytes[(b >> 4) & 0x0f]);
        return append(bytes[b & 0x0f]);
    }

    public byte get(int index) {
        if (index >= count) {
            throw new IndexOutOfBoundsException(String.format("Index: %d, Size: %d", index, count));
        }
        return buffer[index];
    }



    public ByteBuffer reset() {
        count = 0;
        return this;
    }

    public byte[] toByteArray() {
        byte newBuf[] = new byte[count];
        System.arraycopy(buffer, 0, newBuf, 0, count);
        return newBuf;
    }

    public byte[] getInternalBuffer() {
        return buffer;
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int capacity() {
        return buffer.length;
    }
}