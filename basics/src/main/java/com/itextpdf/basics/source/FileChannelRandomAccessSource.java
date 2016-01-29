package com.itextpdf.basics.source;

import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * A RandomAccessSource that is based on an underlying {@link java.nio.channels.FileChannel}.
 * The entire channel will be mapped into memory for efficient reads.
 */
public class FileChannelRandomAccessSource implements RandomAccessSource {

    /**
     * The channel this source is based on
     */
    private final FileChannel channel;

    /**
     * Tracks the actual mapping
     */
    private final MappedChannelRandomAccessSource source;

    /**
     * Constructs a new {@link FileChannelRandomAccessSource} based on the specified FileChannel.  The entire source channel will be mapped into memory.
     * @param channel the channel to use as the backing store
     * @throws java.io.IOException if the channel cannot be opened or mapped
     */
    public FileChannelRandomAccessSource(FileChannel channel) throws IOException {
        this.channel = channel;
        if(channel.size() == 0)
            throw new IOException("File size is 0 bytes");
        source = new MappedChannelRandomAccessSource(channel, 0, channel.size());
        source.open();
    }


    /**
     * {@inheritDoc}
     * Cleans the mapped byte buffers and closes the channel
     */
    public void close() throws IOException {
        source.close();
        channel.close();
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position) throws IOException {
        return source.get(position);
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position, byte[] bytes, int off, int len) throws IOException {
        return source.get(position, bytes, off, len);
    }

    /**
     * {@inheritDoc}
     */
    public long length() {
        return source.length();
    }
}
