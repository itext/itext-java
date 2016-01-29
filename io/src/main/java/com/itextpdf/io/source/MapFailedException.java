package com.itextpdf.io.source;

@SuppressWarnings("serial")
public class MapFailedException extends java.io.IOException {
    public MapFailedException(java.io.IOException e) {
        super(e.getMessage());
        initCause(e);
    }
}
