package com.itextpdf.kernel.pdf;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An <code>OutputStream</code> that counts the written bytes.
 * You should not use same instance of this class in different threads as far as it's not thread safe.
 */
public class CountOutputStream extends OutputStream {

    private final OutputStream outputStream;
    private long amountOfWrittenBytes = 0;

    /**
     * Creates an instance of output stream which counts written bytes.
     *
     * @param outputStream inner {@link OutputStream}
     */
    public CountOutputStream(OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
        amountOfWrittenBytes += b.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        amountOfWrittenBytes += len;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        ++amountOfWrittenBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        outputStream.close();
    }

    /**
     * Gets amount of bytes written to the inner output stream.
     *
     * @return amount of bytes
     */
    public long getAmountOfWrittenBytes() {
        return amountOfWrittenBytes;
    }
}
