package com.itextpdf.kernel.log;

/**
 * Implementation of the Counter interface that doesn't do anything.
 */
public class NoOpCounter implements Counter {

    @Override
    public Counter getCounter(Class<?> cls) {
        return this;
    }

    @Override
    public void onDocumentRead(long size) {

    }

    @Override
    public void onDocumentWritten(long size) {

    }
}
