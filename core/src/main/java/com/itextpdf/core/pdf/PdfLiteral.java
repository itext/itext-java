package com.itextpdf.core.pdf;

import com.itextpdf.basics.font.PdfEncodings;

import java.util.Arrays;

public class PdfLiteral extends PdfPrimitiveObject {

    private long position;

    public PdfLiteral(byte[] content) {
        super(true);
        this.content = content;
    }

    public PdfLiteral(int size) {
        this(new byte[size]);
        Arrays.fill(content, (byte) 32);
    }

    public PdfLiteral(String content) {
        this(PdfEncodings.convertToBytes(content, null));
    }

    private PdfLiteral() {
        this((byte[]) null);
    }

    @Override
    public int getType() {
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

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public int getBytesCount() {
        return content.length;
    }

    @Override
    protected void generateContent() {

    }

    @Override
    protected PdfLiteral newInstance() {
        return new PdfLiteral();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfLiteral literal = (PdfLiteral) from;
        this.content = literal.getInternalContent();
    }
}