package com.itextpdf.kernel.crypto;

import com.itextpdf.kernel.PdfException;
import java.io.IOException;

public class OutputStreamAesEncryption extends OutputStreamEncryption {
    protected AESCipher cipher;
    private boolean finished;

    /**
     * Creates a new instance of OutputStreamCounter
     */
    public OutputStreamAesEncryption(java.io.OutputStream out, byte key[], int off, int len) {
        super(out);
        byte[] iv = IVGenerator.getIV();
        byte[] nkey = new byte[len];
        System.arraycopy(key, off, nkey, 0, len);
        cipher = new AESCipher(true, nkey, iv);
        try {
            write(iv);
        } catch (IOException e) {
            throw new PdfException(PdfException.PdfEncryption, e);
        }
    }

    public OutputStreamAesEncryption(java.io.OutputStream out, byte key[]) {
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
        byte[] b2 = cipher.update(b, off, len);
        if (b2 == null || b2.length == 0)
            return;
        out.write(b2, 0, b2.length);
    }

    public void finish() {
        if (!finished) {
            finished = true;

            byte[] b = cipher.doFinal();
            try {
                out.write(b, 0, b.length);
            } catch (IOException e) {
                throw new PdfException(PdfException.PdfEncryption, e);
            }
        }
    }
}
