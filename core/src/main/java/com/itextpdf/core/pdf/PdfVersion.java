package com.itextpdf.core.pdf;

public class PdfVersion {

    static public final PdfVersion PDF_1_0 = new PdfVersion("PDF-1.0");
    static public final PdfVersion PDF_1_4 = new PdfVersion("PDF-1.4");
    static public final PdfVersion PDF_1_5 = new PdfVersion("PDF-1.5");
    static public final PdfVersion PDF_1_7 = new PdfVersion("PDF-1.7");
    static public final PdfVersion PDF_2_0 = new PdfVersion("PDF-2.0");

    private String pdfVersion = "";

    private PdfVersion(String pdfVersion) {
        this.pdfVersion = pdfVersion;
    }

    public String getPdfVersion() {
        return pdfVersion;
    }
}
