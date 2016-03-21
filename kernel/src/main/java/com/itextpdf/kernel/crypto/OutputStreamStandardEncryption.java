package com.itextpdf.kernel.crypto;

import java.io.IOException;

public class OutputStreamStandardEncryption extends OutputStreamEncryption {
    protected ARCFOUREncryption arcfour;

    /**
     * Creates a new instance of OutputStreamStandardEncryption
     */
    public OutputStreamStandardEncryption(java.io.OutputStream out, byte key[], int off, int len) {
        super(out);
        arcfour = new ARCFOUREncryption();
        arcfour.prepareARCFOURKey(key, off, len);
    }

    public OutputStreamStandardEncryption(java.io.OutputStream out, byte key[]) {
        this(out, key, 0, key.length);
    }

    /**
     * Writes {@code len} bytes from the specified byte array
     * starting at offset {@code off} to this output stream.
     * The general contract for {@code write(b, off, len)} is that
     * some of the bytes in the array {@code b} are written to the
     * output stream in order; element {@code b[off]} is the first
     * byte written and {@code b[off+len-1]} is the last byte written
     * by this operation.
     * <p/>
     * The {@code write} method of {@code OutputStream} calls
     * the write method of one argument on each of the bytes to be
     * written out. Subclasses are encouraged to override this method and
     * provide a more efficient implementation.
     * <p/>
     * If {@code b} is {@code null}, a
     * {@code NullPointerException} is thrown.
     * <p/>
     * If {@code off} is negative, or {@code len} is negative, or
     * {@code off+len} is greater than the length of the array
     * {@code b}, then an <tt>IndexOutOfBoundsException</tt> is thrown.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException if an I/O error occurs. In particular,
     *                     an {@code IOException} is thrown if the output
     *                     stream is closed.
     */
    public void write(byte[] b, int off, int len) throws IOException {
        byte[] b2 = new byte[Math.min(len, 4192)];
        while (len > 0) {
            int sz = Math.min(len, b2.length);
            arcfour.encryptARCFOUR(b, off, sz, b2, 0);
            out.write(b2, 0, sz);
            len -= sz;
            off += sz;
        }
    }

    public void finish() { }
}
