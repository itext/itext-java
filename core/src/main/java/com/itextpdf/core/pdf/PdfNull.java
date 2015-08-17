package com.itextpdf.core.pdf;

import com.itextpdf.basics.io.OutputStream;

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
    public String toString() {
        return "null";
    }

    @Override
    protected void generateContent() {
        content = NullContent;
    }

    //Here we create new object, because if we use static object it can cause unpredictable behavior during copy objects
    @Override
    protected PdfNull newInstance() {
        return new PdfNull();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {

    }
}