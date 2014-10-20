package com.itextpdf.core.pdf;

import com.itextpdf.io.PdfException;

import java.util.Arrays;

abstract class PdfPrimitiveObject extends PdfObject {

    protected byte[] content = null;
    protected boolean directOnly;

    public PdfPrimitiveObject() {
        super();
    }

    public PdfPrimitiveObject(boolean directOnly) {
        super();
        this.directOnly = directOnly;
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
    public <T extends PdfObject> T makeIndirect(PdfDocument document) {
        //TODO log makingIndirect for directObjects
        if (directOnly) return null;
        return super.makeIndirect(document);
    }

    @Override
    public <T extends PdfObject> T setIndirectReference(PdfIndirectReference indirectReference) {
        //TODO log setIndirect for directObjects
        if (!directOnly) {
            super.setIndirectReference(indirectReference);
        }
        return (T) this;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        PdfPrimitiveObject object = (PdfPrimitiveObject)from;
        if (object.content != null)
            content = Arrays.copyOf(object.content, object.content.length);
    }


}
