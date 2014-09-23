package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

abstract class PdfPrimitiveObject extends PdfObject {

    protected byte[] content = null;

    public PdfPrimitiveObject() {
        super();
    }

    PdfPrimitiveObject(byte[] content) {
        this();
        this.content = content;
    }

    protected boolean hasContent() {
        return content != null;
    }

    protected abstract void generateValue() throws PdfException;

    protected abstract void generateContent();

    final public byte[] getContent() {
        if (content == null)
            generateContent();
        return content;
    }

}
