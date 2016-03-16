package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;

public class PdfScreenAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = 1334399136151450493L;

	public PdfScreenAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfScreenAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.Screen;
    }
}
