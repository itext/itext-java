package com.itextpdf.io.streams.ras;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A RandomAccessSource that is based on an underlying {@link java.nio.channels.FileChannel}.  The channel is mapped into memory using a paging scheme to allow for efficient reads of very large files.
 * As an implementation detail, we use {@link GroupedRandomAccessSource} functionality, but override to make determination of the underlying
 * mapped page more efficient - and to close each page as another is opened
 */
class PagedChannelRandomAccessSource extends GroupedRandomAccessSource implements RandomAccessSource {
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
    private final MRU<RandomAccessSource> mru;

    /**
     * Constructs a new {@link PagedChannelRandomAccessSource} based on the specified FileChannel, with a default buffer configuration.
     * The default buffer configuration is currently 2^26 total paged bytes, spread across a maximum of 16 active buffers. This arrangement
     * resulted in a 24% speed improvement over the single buffer case in parametric tests extracting text from a 2.3 GB file.
     * @param channel the channel to use as the backing store
     * @throws java.io.IOException if the channel cannot be opened or mapped
     */
    public PagedChannelRandomAccessSource(FileChannel channel) throws IOException {
        this(channel, DEFAULT_TOTAL_BUFSIZE, DEFAULT_MAX_OPEN_BUFFERS);
    }

    /**
     * Constructs a new {@link PagedChannelRandomAccessSource} based on the specified FileChannel, with a specific buffer size
     * @param channel the channel to use as the backing store
     * @param totalBufferSize total buffer size
     * @param maxOpenBuffers open buffers
     * @throws IOException if the channel cannot be opened or mapped
     */
    public PagedChannelRandomAccessSource(final FileChannel channel, final int totalBufferSize, final int maxOpenBuffers) throws IOException {
        super(buildSources(channel, totalBufferSize/maxOpenBuffers));
        this.channel = channel;
        this.bufferSize = totalBufferSize/maxOpenBuffers;
        this.mru = new MRU<RandomAccessSource>(maxOpenBuffers);
    }

    /**
     * Constructs a set of {@link MappedChannelRandomAccessSource}s for each page (of size bufferSize) of the underlying channel
     * @param channel the underlying channel
     * @param bufferSize the size of each page (the last page may be shorter)
     * @return a list of sources that represent the pages of the channel
     * @throws IOException if IO fails for any reason
     */
    private static RandomAccessSource[] buildSources(final FileChannel channel, final int bufferSize) throws IOException{
        long size = channel.size();
        if (size <= 0)
            throw new IOException("File size must be greater than zero");

        int bufferCount = (int)(size/bufferSize) + (size % bufferSize == 0 ? 0 : 1);

        MappedChannelRandomAccessSource[] sources = new MappedChannelRandomAccessSource[bufferCount];
        for (int i = 0; i < bufferCount; i++){
            long pageOffset = (long)i*bufferSize;
            long pageLength = Math.min(size - pageOffset, bufferSize);
            sources[i] = new MappedChannelRandomAccessSource(channel, pageOffset, pageLength);
        }
        return sources;
    }

    @Override
    /**
     * {@inheritDoc}
     */
    protected int getStartingSourceIndex(long offset) {
        return (int) (offset / bufferSize);
    }

    @Override
    /**
     * {@inheritDoc}
     * For now, close the source that is no longer being used.  In the future, we may implement an MRU that allows multiple pages to be opened at a time
     */
    protected void sourceReleased(RandomAccessSource source) throws IOException {
        RandomAccessSource old = mru.enqueue(source);
        if (old != null)
            old.close();
    }

    @Override
    /**
     * {@inheritDoc}
     * Ensure that the source is mapped.  In the future, we may implement an MRU that allows multiple pages to be opened at a time
     */
    protected void sourceInUse(RandomAccessSource source) throws IOException {
        ((MappedChannelRandomAccessSource)source).open();
    }

    @Override
    /**
     * {@inheritDoc}
     * Cleans the mapped bytebuffers and closes the channel
     */
    public void close() throws IOException {
        super.close();
        channel.close();
    }

    private static class MRU<E>{
        /**
         * The maximum number of entries held by this MRU
         */
        private final int limit;

        /**
         * Backing list for managing the MRU
         */
        private LinkedList<E> queue = new LinkedList<E>();

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
            // TODO: this check may not be an effective optimization - the GroupedRandomAccessSource already tracks the 'current' source, so it seems unlikely that we would ever hit this code branch
            if (queue.size() > 0 && queue.getFirst() == newElement)
                return null;

            for(Iterator<E> it = queue.iterator(); it.hasNext();){
                E element = it.next();
                if (newElement == element){
                    it.remove();
                    queue.addFirst(newElement);
                    return null;
                }
            }
            queue.addFirst(newElement);

            if (queue.size() > limit)
                return queue.removeLast();

            return null;
        }
    }
}

