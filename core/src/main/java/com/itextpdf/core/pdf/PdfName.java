package com.itextpdf.core.pdf;

public class PdfName extends PdfObject implements Comparable<PdfName> {

    protected String value = "";

    public static final PdfName Action = new PdfName("Action");
    public static final PdfName Author = new PdfName("Author");
    public static final PdfName Catalog = new PdfName("Catalog");
    public static final PdfName Contents = new PdfName("Contents");
    public static final PdfName Count = new PdfName("Count");
    public static final PdfName Creator = new PdfName("Creator");
    public static final PdfName D = new PdfName("D");
    public static final PdfName Extends = new PdfName("Extends");
    public static final PdfName F = new PdfName("F");
    public static final PdfName First = new PdfName("First");
    public static final PdfName Fit = new PdfName("Fit");
    public static final PdfName FitB = new PdfName("FitB");
    public static final PdfName FitBH = new PdfName("FitBH");
    public static final PdfName FitBV = new PdfName("FitBV");
    public static final PdfName FitH = new PdfName("FitH");
    public static final PdfName FitR = new PdfName("FitR");
    public static final PdfName FitV = new PdfName("FitV");
    public static final PdfName GoTo = new PdfName("GoTo");
    public static final PdfName GoToR = new PdfName("GoToR");
    public static final PdfName ID = new PdfName("ID");
    public static final PdfName Index = new PdfName("Index");
    public static final PdfName Info = new PdfName("Info");
    public static final PdfName IsMap = new PdfName("IsMap");
    public static final PdfName Keywords = new PdfName("Keywords");
    public static final PdfName Kids = new PdfName("Kids");
    public static final PdfName Length = new PdfName("Length");
    public static final PdfName MediaBox = new PdfName("MediaBox");
    public static final PdfName N = new PdfName("N");
    public static final PdfName NewWindow = new PdfName("NewWindow");
    public static final PdfName Next = new PdfName("Next");
    public static final PdfName ObjStm = new PdfName("ObjStm");
    public static final PdfName Page = new PdfName("Page");
    public static final PdfName Pages = new PdfName("Pages");
    public static final PdfName Parent = new PdfName("Parent");
    public static final PdfName Resources = new PdfName("Resources");
    public static final PdfName Root = new PdfName("Root");
    public static final PdfName S = new PdfName("S");
    public static final PdfName Size = new PdfName("Size");
    public static final PdfName Subject = new PdfName("Subject");
    public static final PdfName Title = new PdfName("Title");
    public static final PdfName Type = new PdfName("Type");
    public static final PdfName URI = new PdfName("URI");
    public static final PdfName W = new PdfName("W");
    public static final PdfName XRef = new PdfName("XRef");
    public static final PdfName XYZ = new PdfName("XYZ");

    public PdfName(String value) {
        super(PdfObject.Name);
        this.value = value;
    }

    public PdfName(PdfDocument doc, String value) {
        super(doc, PdfObject.Name);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(PdfName o) {
        return value.compareTo(o.value);
    }
}
