package com.itextpdf.io.source;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

public class DeflaterOutputStream extends java.util.zip.DeflaterOutputStream {

    public DeflaterOutputStream(java.io.OutputStream out, int level, int size, boolean syncFlush) {
        super(out, new Deflater(level), size, syncFlush);
    }

    public DeflaterOutputStream(OutputStream out, int level, int size) {
        this(out, level, size, false);
    }

    public DeflaterOutputStream(OutputStream out, int level, boolean syncFlush) {
        this(out, level, 512, syncFlush);
    }

    public DeflaterOutputStream(OutputStream out, int level) {
        this(out, level, false);
    }

    public DeflaterOutputStream(OutputStream out, boolean syncFlush) {
        this(out, -1, syncFlush);
    }

    public DeflaterOutputStream(OutputStream out) {
        this(out, false);
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
