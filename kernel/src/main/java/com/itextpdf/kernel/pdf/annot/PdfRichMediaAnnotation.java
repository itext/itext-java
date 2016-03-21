package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

public class PdfRichMediaAnnotation extends PdfAnnotation {

    public PdfRichMediaAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfRichMediaAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.RichMedia;
    }
}
