/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.io.source;

import com.itextpdf.io.logs.IoLogMessageConstant;

import java.nio.channels.FileChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RandomAccessSource that is based on an underlying {@link java.nio.channels.FileChannel}.
 * The entire channel will be mapped into memory for efficient reads.
 */
public class FileChannelRandomAccessSource implements IRandomAccessSource {

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
    public FileChannelRandomAccessSource(FileChannel channel) throws java.io.IOException {
        this.channel = channel;
        if(channel.size() == 0)
            throw new java.io.IOException("File size is 0 bytes");
        source = new MappedChannelRandomAccessSource(channel, 0, channel.size());
        source.open();
    }


    /**
     * {@inheritDoc}
     * Cleans the mapped byte buffers and closes the channel
     */
    public void close() throws java.io.IOException {
        try {
            source.close();
        } finally {
            try {
                channel.close();
            } catch (Exception ex) {
                Logger logger = LoggerFactory.getLogger(FileChannelRandomAccessSource.class);
                logger.error(IoLogMessageConstant.FILE_CHANNEL_CLOSING_FAILED, ex);
            }
        }
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
}
