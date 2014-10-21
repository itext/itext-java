package com.itextpdf.basics.io;

import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * A RandomAccessSource that represents a memory mapped section of an underlying FileChannel.
 * This source can be closed and will automatically re-open as needed.
 * This class is an internal implementation detail of the {@link FileChannelRandomAccessSource} class and
 * shouldn't be used by general iText users.
 */
class MappedChannelRandomAccessSource implements RandomAccessSource {
    /**
     * The underlying channel
     */
    private final FileChannel channel;
    /**
     * The offset into the channel that this source maps to
     */
    private final long offset;
    /**
     * The number of bytes this source maps to
     */
    private final long length;

    /**
     * If the map is active, the actual map.  null other wise.
     */
    private ByteBufferRandomAccessSource source;

    /**
     * Create a new source based on the channel.  Mapping will not occur until data is actually read.
     * @param channel the underlying channel
     * @param offset the offset of the map
     * @param length the length of the map
     */
    public MappedChannelRandomAccessSource(FileChannel channel, long offset, long length) {
        if (offset < 0)
            throw new IllegalArgumentException(offset + " is negative");
        if (length <= 0)
            throw new IllegalArgumentException(length + " is zero or negative");

        this.channel = channel;
        this.offset = offset;
        this.length = length;
        this.source = null;
    }

    /**
     * Map the region of the channel
     * @throws java.io.IOException if there is a problem with creating the map
     */
    void open() throws IOException {
        if (source != null)
            return;

        if (!channel.isOpen())
            throw new IllegalStateException("Channel is closed");

        try{
            source = new ByteBufferRandomAccessSource(channel.map(FileChannel.MapMode.READ_ONLY, offset, length));
        } catch (IOException e){
            if (exceptionIsMapFailureException(e))
                throw new MapFailedException(e);
            throw e;
        }
    }

    /**
     * Utility method that determines whether a given IOException is the result
     * of a failure to map a memory mapped file.  It would be better if the runtime
     * provided a special exception for this case, but it doesn't, so we have to rely
     * on parsing the exception message.
     * @param e the exception to check
     * @return true if the exception was the result of a failure to map a memory mapped file
     */
    private static boolean exceptionIsMapFailureException(IOException e){
        if (e.getMessage() != null && e.getMessage().contains("Map failed"))
            return true;
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position) throws IOException {
        if (source == null)
            throw new IOException("RandomAccessSource not opened");
        return source.get(position);
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position, byte[] bytes, int off, int len) throws IOException {
        if (source == null)
            throw new IOException("RandomAccessSource not opened");
        return source.get(position, bytes, off, len);
    }

    /**
     * {@inheritDoc}
     */
    public long length() {
        return length;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException {
        if (source == null)
            return;
        source.close();
        source = null;
    }

    @Override
    public String toString() {
        return getClass().getName() + " (" + offset + ", " + length + ")";
    }
}
