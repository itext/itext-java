package com.itextpdf.core.pdf.objects;

import com.itextpdf.core.pdf.PdfDocument;

public class PdfName extends PdfObject {

    protected String name = "";

    public PdfName(String name) {
        super();
        this.name = name;
    }

    public PdfName(PdfDocument doc, String name) {
        super(doc);
        this.name = name;
    }

    public static final PdfName Action = new PdfName("Action");
    public static final PdfName D = new PdfName("D");
    public static final PdfName F = new PdfName("F");
    public static final PdfName Fit = new PdfName("Fit");
    public static final PdfName FitB = new PdfName("FitB");
    public static final PdfName FitBH = new PdfName("FitBH");
    public static final PdfName FitBV = new PdfName("FitBV");
    public static final PdfName FitH = new PdfName("FitH");
    public static final PdfName FitR = new PdfName("FitR");
    public static final PdfName FitV = new PdfName("FitV");
    public static final PdfName GoTo = new PdfName("GoTo");
    public static final PdfName GoToR = new PdfName("GoToR");
    public static final PdfName IsMap = new PdfName("IsMap");
    public static final PdfName NewWindow = new PdfName("NewWindow");
    public static final PdfName S = new PdfName("S");
    public static final PdfName Type = new PdfName("Type");
    public static final PdfName URI = new PdfName("URI");
    public static final PdfName XYZ = new PdfName("XYZ");

}
