package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

abstract class PdfPrimitiveObject extends PdfObject {

    protected byte[] content = null;

    public PdfPrimitiveObject(byte type) {
        super(type);
    }

    public PdfPrimitiveObject(PdfDocument doc, byte type) {
        super(doc, type);
    }

    PdfPrimitiveObject(byte[] content, byte type) {
        super(type);
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
