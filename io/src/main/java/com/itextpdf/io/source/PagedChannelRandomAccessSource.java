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
import java.util.Iterator;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RandomAccessSource that is based on an underlying {@link java.nio.channels.FileChannel}.  The channel is mapped into memory using a paging scheme to allow for efficient reads of very large files.
 * As an implementation detail, we use {@link GroupedRandomAccessSource} functionality, but override to make determination of the underlying
 * mapped page more efficient - and to close each page as another is opened
 */
class PagedChannelRandomAccessSource extends GroupedRandomAccessSource implements IRandomAccessSource {
    // these values were selected based on parametric testing with extracting text content from a 2.3GB file.  These settings resulted in the best improvement over
    // the single size MRU case (24% speed improvement)
    public static final int DEFAULT_TOTAL_BUFSIZE = 1 << 26;
    public static final int DEFAULT_MAX_OPEN_BUFFERS = 16;

    /**
     * The size of each of the buffers to use when mapping files into memory.  This must be greater than 0 and less than {@link Integer#MAX_VALUE}
     */
    private final int bufferSize;

    /**
     * The channel this source is based on
     */
    private final FileChannel channel;

    /**
     * Most recently used list used to hold a number of mapped pages open at a time
     */
    private final MRU<IRandomAccessSource> mru;

    /**
     * Constructs a new {@link PagedChannelRandomAccessSource} based on the specified FileChannel, with a default buffer configuration.
     * The default buffer configuration is currently 2^26 total paged bytes, spread across a maximum of 16 active buffers. This arrangement
     * resulted in a 24% speed improvement over the single buffer case in parametric tests extracting text from a 2.3 GB file.
     * @param channel the channel to use as the backing store
     * @throws java.io.IOException if the channel cannot be opened or mapped
     */
    public PagedChannelRandomAccessSource(FileChannel channel) throws java.io.IOException {
        this(channel, DEFAULT_TOTAL_BUFSIZE, DEFAULT_MAX_OPEN_BUFFERS);
    }

    /**
     * Constructs a new {@link PagedChannelRandomAccessSource} based on the specified FileChannel, with a specific buffer size
     * @param channel the channel to use as the backing store
     * @param totalBufferSize total buffer size
     * @param maxOpenBuffers open buffers
     * @throws java.io.IOException if the channel cannot be opened or mapped
     */
    public PagedChannelRandomAccessSource(FileChannel channel, int totalBufferSize, int maxOpenBuffers) throws java.io.IOException {
        super(buildSources(channel, totalBufferSize/maxOpenBuffers));
        this.channel = channel;
        this.bufferSize = totalBufferSize/maxOpenBuffers;
        this.mru = new MRU<IRandomAccessSource>(maxOpenBuffers);
    }

    /**
     * Constructs a set of {@link MappedChannelRandomAccessSource}s for each page (of size bufferSize) of the underlying channel
     * @param channel the underlying channel
     * @param bufferSize the size of each page (the last page may be shorter)
     * @return a list of sources that represent the pages of the channel
     * @throws java.io.IOException if IO fails for any reason
     */
    private static IRandomAccessSource[] buildSources(FileChannel channel, int bufferSize) throws java.io.IOException{
        long size = channel.size();
        if (size <= 0)
            throw new java.io.IOException("File size must be greater than zero");

        int bufferCount = (int)(size/bufferSize) + (size % bufferSize == 0 ? 0 : 1);

        MappedChannelRandomAccessSource[] sources = new MappedChannelRandomAccessSource[bufferCount];
        for (int i = 0; i < bufferCount; i++){
            long pageOffset = (long)i*bufferSize;
            long pageLength = Math.min(size - pageOffset, bufferSize);
            sources[i] = new MappedChannelRandomAccessSource(channel, pageOffset, pageLength);
        }
        return sources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getStartingSourceIndex(long offset) {
        return (int) (offset / bufferSize);
    }

    /**
     * {@inheritDoc}
     * For now, close the source that is no longer being used.  In the future, we may implement an MRU that allows multiple pages to be opened at a time
     */
    @Override
    protected void sourceReleased(IRandomAccessSource source) throws java.io.IOException {
        IRandomAccessSource old = mru.enqueue(source);
        if (old != null)
            old.close();
    }

    /**
     * {@inheritDoc}
     * Ensure that the source is mapped.  In the future, we may implement an MRU that allows multiple pages to be opened at a time
     */
    @Override
    protected void sourceInUse(IRandomAccessSource source) throws java.io.IOException {
        ((MappedChannelRandomAccessSource)source).open();
    }

    /**
     * {@inheritDoc}
     * Cleans the mapped bytebuffers and closes the channel
     */
    @Override
    public void close() throws java.io.IOException {
        try {
            super.close();
        } finally {
            try {
                channel.close();
            } catch (Exception ex) {
                Logger logger = LoggerFactory.getLogger(PagedChannelRandomAccessSource.class);
                logger.error(IoLogMessageConstant.FILE_CHANNEL_CLOSING_FAILED, ex);
            }
        }
    }

    private static class MRU<E> {
        /**
         * The maximum number of entries held by this MRU
         */
        private final int limit;

        /**
         * Backing list for managing the MRU
         */
        private LinkedList<E> queue = new LinkedList<>();

        /**
         * Constructs an MRU with the specified size
         * @param limit the limit
         */
        public MRU(int limit) {
            this.limit = limit;
        }

        /**
         * Adds an element to the MRU.  If the element is already in the MRU, it is moved to the top.
         * @param newElement the element to add
         * @return the element that was removed from the MRU to make room for the new element, or null if no element needed to be removed
         */
        public E enqueue(E newElement){
            if (!queue.isEmpty() && queue.getFirst() == newElement) {
                return null;
            }

            for (Iterator<E> it = queue.iterator(); it.hasNext();) {
                E element = it.next();
                if (newElement == element) {
                    it.remove();
                    queue.addFirst(newElement);
                    return null;
                }
            }
            queue.addFirst(newElement);

            if (queue.size() > limit) {
                return queue.removeLast();
            }

            return null;
        }
    }
}

