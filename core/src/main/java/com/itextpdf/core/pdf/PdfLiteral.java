package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.PdfEncodings;

public class PdfLiteral extends PdfPrimitiveObject {

    public PdfLiteral(byte[] content) {
        super(true);
        this.content = content;
    }

    public PdfLiteral(String content) {
        this(PdfEncodings.convertToBytes(content, null));
    }

    private PdfLiteral() {
        this((byte[]) null);
    }

    @Override
    public byte getType() {
        return Literal;
    }

    @Override
    public String toString() {
        if (content != null) {
            return new String(content);
        } else {
            return "";
        }
    }

    @Override
    protected void generateContent() {

    }

    @Override
    protected PdfLiteral newInstance() {
        return new PdfLiteral();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        super.copyContent(from, document);
        PdfLiteral literal = (PdfLiteral) from;
        this.content = literal.getInternalContent();
    }
}