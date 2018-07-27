package com.itextpdf.svg.dummy.sdk;

import java.io.IOException;
import java.io.InputStream;

public class ExceptionInputStream extends InputStream {

    @Override
    public int read() throws IOException {
        throw new IOException();
    }
}