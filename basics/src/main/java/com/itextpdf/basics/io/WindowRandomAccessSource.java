package com.itextpdf.basics.io;

import java.io.IOException;

/**
 * A RandomAccessSource that wraps another RandomAccessSource and provides a window of it at a specific offset and over
 * a specific length.  Position 0 becomes the offset position in the underlying source.
 */
public class WindowRandomAccessSource implements RandomAccessSource {
    /**
     * The source
     */
    private final RandomAccessSource source;

    /**
     * The amount to offset the source by
     */
    private final long offset;

    /**
     * The length
     */
    private final long length;

    /**
     * Constructs a new OffsetRandomAccessSource that extends to the end of the underlying source
     * @param source the source
     * @param offset the amount of the offset to use
     */
    public WindowRandomAccessSource(RandomAccessSource source, long offset) {
        this(source, offset, source.length() - offset);
    }

    /**
     * Constructs a new OffsetRandomAccessSource with an explicit length
     * @param source the source
     * @param offset the amount of the offset to use
     * @param length the number of bytes to be included in this RAS
     */
    public WindowRandomAccessSource(RandomAccessSource source, long offset, long length) {
        this.source = source;
        this.offset = offset;
        this.length = length;
    }

    /**
     * {@inheritDoc}
     * Note that the position will be adjusted to read from the corrected location in the underlying source
     */
    public int get(long position) throws IOException {
        if (position >= length) return -1;
        return source.get(offset + position);
    }

    /**
     * {@inheritDoc}
     * Note that the position will be adjusted to read from the corrected location in the underlying source
     */
    public int get(long position, byte[] bytes, int off, int len) throws IOException {
        if (position >= length)
            return -1;

        long toRead = Math.min(len, length - position);
        return source.get(offset + position, bytes, off, (int)toRead);
    }

    /**
     * {@inheritDoc}
     * Note that the length will be adjusted to read from the corrected location in the underlying source
     */
    public long length() {
        return length;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        source.close();
    }
}
