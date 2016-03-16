package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

public class PdfSquareAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = 5577194318058336359L;

	public PdfSquareAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfSquareAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Square;
    }
}
