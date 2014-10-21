package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
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

    @Override
    protected PdfNull newInstance() {
        return PdfNull;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {

    }
}