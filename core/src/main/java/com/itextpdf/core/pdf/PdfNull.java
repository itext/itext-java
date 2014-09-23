package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.io.streams.OutputStream;

public class PdfNull extends PdfPrimitiveObject {

    public static final PdfNull PdfNull = new PdfNull();
    private static final byte[] NullContent = OutputStream.getIsoBytes("null");

    public PdfNull() {
        super();
    }

    @Override
    public byte getType() {
        return Null;
    }

    @Override
    protected void generateValue() throws PdfException {
    }

    @Override
    protected void generateContent() {
        content = NullContent;
    }

    @Override
    public String toString() {
        return "null";
    }
}