package com.itextpdf.io.source;

/**
 * A RandomAccessSource that is wraps another RandomAccessSource but does not propagate close().  This is useful when
 * passing a RandomAccessSource to a method that would normally close the source.
 */
public class IndependentRandomAccessSource implements RandomAccessSource {
    /**
     * The source
     */
    private final RandomAccessSource source;

    /**
     * Constructs a new OffsetRandomAccessSource
     * @param source the source
     */
    public IndependentRandomAccessSource(RandomAccessSource source) {
        this.source = source;
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position) throws java.io.IOException {
        return source.get(position);
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position, byte[] bytes, int off, int len) throws java.io.IOException {
        return source.get(position, bytes, off, len);
    }

    /**
     * {@inheritDoc}
     */
    public long length() {
        return source.length();
    }

    /**
     * Does nothing - the underlying source is not closed
     */
    public void close() throws java.io.IOException {
        // do not close the source
    }
}
