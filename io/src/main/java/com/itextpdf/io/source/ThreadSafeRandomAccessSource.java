package com.itextpdf.io.source;

import java.io.IOException;

public class ThreadSafeRandomAccessSource implements IRandomAccessSource {
    private final IRandomAccessSource source;
    private final Object lockObj = new Object();
    
    public ThreadSafeRandomAccessSource(IRandomAccessSource source) {
        this.source = source;
    }

    @Override
    public int get(long position) throws IOException {
        synchronized (lockObj) {
            return source.get(position);
        }
    }

    @Override
    public int get(long position, byte[] bytes, int off, int len) throws IOException {
        synchronized (lockObj) {
            return source.get(position, bytes, off, len);
        }
    }

    @Override
    public long length() {
        synchronized (lockObj) {
            return source.length();
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lockObj) {
            source.close();
        }
    }
}
