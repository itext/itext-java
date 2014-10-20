package com.itextpdf.io.streams.ras;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A RandomAccessSource that uses a {@link java.io.RandomAccessFile} as it's source
 * Note: Unlike most of the RandomAccessSource implementations, this class is not thread safe
 */
class RAFRandomAccessSource implements RandomAccessSource {
    /**
     * The source
     */
    private final RandomAccessFile raf;

    /**
     * The length of the underling RAF.  Note that the length is cached at construction time to avoid the possibility
     * of IOExceptions when reading the length.
     */
    private final long length;

    /**
     * Creates this object
     * @param raf the source for this RandomAccessSource
     * @throws java.io.IOException if the RAF can't be read
     */
    public RAFRandomAccessSource(RandomAccessFile raf) throws IOException {
        this.raf = raf;
        length = raf.length();
    }

    /**
     * {@inheritDoc}
     */
    // TODO: test to make sure we are handling the length properly (i.e. is raf.length() the last byte in the file, or one past the last byte?)
    public int get(long position) throws IOException {
        if (position > raf.length())
            return -1;

        // Not thread safe!
        raf.seek(position);

        return raf.read();
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position, byte[] bytes, int off, int len) throws IOException {
        if (position > length)
            return -1;

        // Not thread safe!
        raf.seek(position);

        return raf.read(bytes, off, len);
    }

    /**
     * {@inheritDoc}
     * Note: the length is determined when the {@link RAFRandomAccessSource} is constructed.  If the file length changes
     * after construction, that change will not be reflected in this call.
     */
    public long length() {
        return length;
    }

    /**
     * Closes the underlying RandomAccessFile
     */
    public void close() throws IOException {
        raf.close();
    }
}
