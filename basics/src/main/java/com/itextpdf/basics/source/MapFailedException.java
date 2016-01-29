package com.itextpdf.basics.source;

import java.io.IOException;

@SuppressWarnings("serial")
public class MapFailedException extends IOException {
    public MapFailedException(IOException e) {
        super(e.getMessage());
        initCause(e);
    }
}
