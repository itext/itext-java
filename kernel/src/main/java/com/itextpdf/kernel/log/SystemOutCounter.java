package com.itextpdf.kernel.log;

import java.text.MessageFormat;

/**
 * A {@link Counter} implementation that outputs information about read and written documents to {@link System#out}
 */
public class SystemOutCounter implements Counter {

    /**
     * The name of the class for which the Counter was created
     * (or iText if no name is available)
     */
    protected String name;

    public SystemOutCounter(String name) {
        this.name = name;
    }

    public SystemOutCounter() {
        this("iText");
    }

    public SystemOutCounter(Class<?> cls) {
        this(cls.getName());
    }

    @Override
    public Counter getCounter(Class<?> cls) {
        return new SystemOutCounter(cls);
    }

    @Override
    public void onDocumentRead(long size) {
        System.out.println(MessageFormat.format("[{0}] {1} bytes read", name, size));
    }

    @Override
    public void onDocumentWritten(long size) {
        System.out.println(MessageFormat.format("[{0}] {1} bytes written", name, size));
    }
}
