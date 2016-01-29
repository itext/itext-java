package com.itextpdf.basics.source;

/**
 * A RandomAccessSource that is based on an underlying byte array
 */
class ArrayRandomAccessSource implements RandomAccessSource {
    private byte[] array;

    public ArrayRandomAccessSource(byte[] array) {
        if(array == null) throw new NullPointerException();
        this.array = array;
    }

    public int get(long offset) {
        if (offset >= array.length) return -1;
        return 0xff & array[(int)offset];
    }

    public int get(long offset, byte[] bytes, int off, int len) {
        if (array == null) throw new IllegalStateException("Already closed");

        if (offset >= array.length)
            return -1;

        if (offset + len > array.length)
            len = (int)(array.length - offset);

        System.arraycopy(array, (int)offset, bytes, off, len);

        return len;
    }

    public long length() {
        return array.length;
    }

    public void close() throws java.io.IOException {
        array = null;
    }
}
