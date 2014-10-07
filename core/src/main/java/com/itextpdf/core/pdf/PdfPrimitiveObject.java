package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.util.Arrays;

abstract class PdfPrimitiveObject extends PdfObject {

    protected byte[] content = null;

    public PdfPrimitiveObject() {
        super();
    }

    public PdfPrimitiveObject(byte[] content) {
        this();
        this.content = content;
    }

    final public byte[] getContent() {
        if (content == null)
            generateContent();
        return content;
    }

    protected boolean hasContent() {
        return content != null;
    }

    protected abstract void generateContent();

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        PdfPrimitiveObject object = (PdfPrimitiveObject)from;
        if (object.content != null)
            content = Arrays.copyOf(object.content, object.content.length);
    }


}
