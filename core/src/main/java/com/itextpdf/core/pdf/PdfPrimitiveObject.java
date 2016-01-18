package com.itextpdf.core.pdf;

import java.util.Arrays;

abstract class PdfPrimitiveObject extends PdfObject {

    protected byte[] content = null;
    protected boolean directOnly;

    protected PdfPrimitiveObject() {
        super();
    }

    public PdfPrimitiveObject(boolean directOnly) {
        super();
        this.directOnly = directOnly;
    }

    protected PdfPrimitiveObject(byte[] content) {
        this();
        this.content = content;
    }

    protected final byte[] getInternalContent() {
        if (content == null)
            generateContent();
        return content;
    }

    protected boolean hasContent() {
        return content != null;
    }

    protected abstract void generateContent();

    @Override
    public <T extends PdfObject> T makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        //TODO log makingIndirect directOnly Objects
        if (!directOnly) {
            return super.makeIndirect(document, reference);
        }
        return (T) this;
    }

    @Override
    public <T extends PdfObject> T setIndirectReference(PdfIndirectReference indirectReference) {
        //TODO log setIndirect for directOnly Objects
        if (!directOnly) {
            super.setIndirectReference(indirectReference);
        }
        return (T) this;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfPrimitiveObject object = (PdfPrimitiveObject) from;
        if (object.content != null)
            content = Arrays.copyOf(object.content, object.content.length);
    }

    protected int compareContent(PdfPrimitiveObject o) {
        for (int i = 0; i < Math.min(content.length, o.content.length); i++) {
            if (content[i] > o.content[i])
                return 1;
            if (content[i] < o.content[i])
                return -1;
        }
        if (content.length > o.content.length)
            return 1;
        if (content.length < o.content.length)
            return -1;
        return 0;
    }
}
