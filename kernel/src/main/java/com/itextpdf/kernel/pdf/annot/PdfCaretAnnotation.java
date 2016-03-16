package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfCaretAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = 1542932123958535397L;

	public PdfCaretAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfCaretAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Caret;
    }

    public PdfCaretAnnotation setSymbol(PdfString symbol) {
        return put(PdfName.Sy, symbol);
    }

    public PdfString getSymbol() {
        return getPdfObject().getAsString(PdfName.Sy);
    }
}
