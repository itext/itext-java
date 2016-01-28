package com.itextpdf.basics.io;

import java.io.IOException;

/**
 * A RandomAccessSource that is based on a set of underlying sources,
 * treating the sources as if they were a contiguous block of data.
 */
class GroupedRandomAccessSource implements RandomAccessSource {
    /**
     * The underlying sources (along with some meta data to quickly determine where each source begins and ends)
     */
    private final SourceEntry[] sources;

    /**
     * Cached value to make multiple reads from the same underlying source more efficient
     */
    private SourceEntry currentSourceEntry;

    /**
     * Cached size of the underlying channel
     */
    private final long size;

    /**
     * Constructs a new {@link GroupedRandomAccessSource} based on the specified set of sources
     * @param sources the sources used to build this group
     */
    public GroupedRandomAccessSource(RandomAccessSource[] sources) throws IOException {
        this.sources = new SourceEntry[sources.length];

        long totalSize = 0;
        for(int i = 0; i < sources.length; i++){
            this.sources[i] = new SourceEntry(i, sources[i], totalSize);
            totalSize += sources[i].length();
        }
        size = totalSize;
        currentSourceEntry = this.sources[sources.length-1];
        sourceInUse(currentSourceEntry.source);
    }

    /**
     * For a given offset, return the index of the source that contains the specified offset.
     * This is an optimization feature to help optimize the access of the correct source without having to iterate
     * through every single source each time.  It is safe to always return 0, in which case the full set of sources
     * will be searched.
     * Subclasses should override this method if they are able to compute the source index more efficiently
     * (for example {@link FileChannelRandomAccessSource} takes advantage of fixed size page buffers to compute the index)
     * @param offset the offset
     * @return the index of the input source that contains the specified offset, or 0 if unknown
     */
    protected int getStartingSourceIndex(long offset){
        if (offset >= currentSourceEntry.firstByte)
            return currentSourceEntry.index;

        return 0;
    }

    /**
     * Returns the SourceEntry that contains the byte at the specified offset
     * sourceReleased is called as a notification callback so subclasses can take care of cleanup
     * when the source is no longer the active source
     * @param offset the offset of the byte to look for
     * @return the SourceEntry that contains the byte at the specified offset
     * @throws IOException if there is a problem with IO (usually the result of the sourceReleased() call)
     */
    private SourceEntry getSourceEntryForOffset(long offset) throws IOException {
        if (offset >= size)
            return null;

        if (offset >= currentSourceEntry.firstByte && offset <= currentSourceEntry.lastByte)
            return currentSourceEntry;

        // hook to allow subclasses to release resources if necessary
        sourceReleased(currentSourceEntry.source);

        int startAt = getStartingSourceIndex(offset);
        for(int i = startAt; i < sources.length; i++){
            if (offset >= sources[i].firstByte && offset <= sources[i].lastByte){
                currentSourceEntry = sources[i];
                sourceInUse(currentSourceEntry.source);
                return currentSourceEntry;
            }
        }
        return null;
    }

    /**
     * Called when a given source is no longer the active source.  This gives subclasses the abilty to release resources, if appropriate.
     * @param source the source that is no longer the active source
     * @throws IOException if there are any problems
     */
    protected void sourceReleased(RandomAccessSource source) throws IOException{
        // by default, do nothing
    }

    /**
     * Called when a given source is about to become the active source.  This gives subclasses the abilty to retrieve resources, if appropriate.
     * @param source the source that is about to become the active source
     * @throws IOException if there are any problems
     */
    protected void sourceInUse(RandomAccessSource source) throws IOException{
        // by default, do nothing
    }

    /**
     * {@inheritDoc}
     * The source that contains the byte at position is retrieved, the correct offset into that source computed, then the value
     * from that offset in the underlying source is returned.
     */
    public int get(long position) throws IOException {
        SourceEntry entry = getSourceEntryForOffset(position);

        if (entry == null) // we have run out of data to read from
            return -1;

        return entry.source.get(entry.offsetN(position));
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position, byte[] bytes, int off, int len) throws IOException {
        SourceEntry entry = getSourceEntryForOffset(position);

        if (entry == null) // we have run out of data to read from
            return -1;

        long offN = entry.offsetN(position);

        int remaining = len;

        while(remaining > 0){
            if (entry == null) // we have run out of data to read from
                break;
            if (offN > entry.source.length())
                break;

            int count = entry.source.get(offN, bytes, off, remaining);
            if (count == -1)
                break;

            off += count;
            position += count;
            remaining -= count;

            offN = 0;
            entry = getSourceEntryForOffset(position);
        }
        return remaining == len ? -1 : len - remaining;
    }


    /**
     * {@inheritDoc}
     */
    public long length() {
        return size;
    }

    /**
     * {@inheritDoc}
     * Closes all of the underlying sources
     */
    public void close() throws IOException {
        for (SourceEntry entry : sources) {
            entry.source.close();
        }
    }

    /**
     * Used to track each source, along with useful meta data
     */
    private static class SourceEntry{
        /**
         * The underlying source
         */
        final RandomAccessSource source;
        /**
         * The first byte (in the coordinates of the GroupedRandomAccessSource) that this source contains
         */
        final long firstByte;
        /**
         * The last byte (in the coordinates of the GroupedRandomAccessSource) that this source contains
         */
        final long lastByte;
        /**
         * The index of this source in the GroupedRandomAccessSource
         */
        final int index;

        /**
         * Standard constructor
         * @param index the index
         * @param source the source
         * @param offset the offset of the source in the GroupedRandomAccessSource
         */
        public SourceEntry(int index, RandomAccessSource source, long offset) {
            this.index = index;
            this.source = source;
            this.firstByte = offset;
            this.lastByte = offset + source.length() - 1;
        }

        /**
         * Given an absolute offset (in the GroupedRandomAccessSource coordinates), calculate the effective offset in the underlying source
         * @param absoluteOffset the offset in the parent GroupedRandomAccessSource
         * @return the effective offset in the underlying source
         */
        public long offsetN(long absoluteOffset){
            return absoluteOffset - firstByte;
        }
    }
}

