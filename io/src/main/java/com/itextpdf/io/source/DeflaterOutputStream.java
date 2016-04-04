package com.itextpdf.io.source;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

public class DeflaterOutputStream extends java.util.zip.DeflaterOutputStream {

    public DeflaterOutputStream(OutputStream out, int level, int size) {
        super(out, new Deflater(level), size);
    }

    public DeflaterOutputStream(OutputStream out, int level) {
        this(out, level, 512);
    }

    public DeflaterOutputStream(OutputStream out) {
        this(out, -1);
    }

    @Override
    public void close() throws IOException {
        finish();
        super.close();
    }

    @Override
    public void finish() throws IOException {
        super.finish();
        def.end();
    }
}
