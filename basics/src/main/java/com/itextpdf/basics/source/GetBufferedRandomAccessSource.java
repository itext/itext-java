package com.itextpdf.basics.source;

public class GetBufferedRandomAccessSource implements RandomAccessSource {

    private final RandomAccessSource source;

    private final byte[] getBuffer;
    private long getBufferStart = -1;
    private long getBufferEnd = -1;

    /**
     * Constructs a new OffsetRandomAccessSource
     * @param source the source
     */
    public GetBufferedRandomAccessSource(RandomAccessSource source) {
        this.source = source;
        this.getBuffer = new byte[(int)Math.min(Math.max(source.length()/4, 1), (long)4096)];
        this.getBufferStart = -1;
        this.getBufferEnd = -1;
    }

    /**
     * {@inheritDoc}
     */
    public int get(long position) throws java.io.IOException {
        if (position < getBufferStart || position > getBufferEnd){
            int count = source.get(position, getBuffer, 0, getBuffer.length);
            if (count == -1)
                return -1;
            getBufferStart = position;
            getBufferEnd = position + count - 1;
        }
        int bufPos = (int)(position-getBufferStart);
        return 0xff & getBuffer[bufPos];
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
        source.close();
        getBufferStart = -1;
        getBufferEnd = -1;
    }
}
