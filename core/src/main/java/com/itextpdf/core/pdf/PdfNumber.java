package com.itextpdf.core.pdf;

public class PdfNumber extends PdfObject {

    private static final byte Int = 1;
    private static final byte Float = 2;

    float value;
    byte intOrFloat = 0;
    byte[] content = null;

    public PdfNumber(int value) {
        super(PdfObject.Number);
        this.value = value;
        this.intOrFloat = Int;
    }

    public PdfNumber(float value) {
        super(PdfObject.Number);
        this.value = value;
        this.intOrFloat = Float;
    }

    public byte[] getContent() {
        if (content == null) {
            switch (intOrFloat) {
                case Int:
                    content = PdfWriter.getIsoBytes(java.lang.String.valueOf((int)value));
                    break;
                case Float:
                    content = PdfWriter.getIsoBytes(java.lang.String.valueOf(value));
                    break;
                default:
                    content = new byte[0];
            }
        }
        return content;
    }


}
