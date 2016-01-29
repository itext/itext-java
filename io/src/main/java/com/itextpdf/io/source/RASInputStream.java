package com.itextpdf.io.source;

import java.io.InputStream;

/**
 * An input stream that uses a {@link RandomAccessSource} as
 * its underlying source.
 */
public class RASInputStream extends InputStream {

    /**
     * The source.
     */
    private final RandomAccessSource source;

    /**
     * The current position in the source.
     */
    private long position = 0;

    /**
     * Creates an input stream based on the source.
     * @param source The source.
     */
    public RASInputStream(RandomAccessSource source){
        this.source = source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read(byte[] b, int off, int len) throws java.io.IOException {
        int count = source.get(position, b, off, len);
        position += count;
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int read() throws java.io.IOException {
        return source.get(position++);
    }
}
