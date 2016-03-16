package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

public class PdfPrinterMarkAnnotation extends PdfAnnotation {

    private static final long serialVersionUID = -7709626622860134020L;

	public PdfPrinterMarkAnnotation(Rectangle rect, PdfFormXObject appearanceStream) {
        super(rect);
        setNormalAppearance(appearanceStream.getPdfObject());
        setFlags(PdfAnnotation.Print | PdfAnnotation.ReadOnly);
    }

    public PdfPrinterMarkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfName getSubtype() {
        return PdfName.PrinterMark;
    }

    public PdfMarkupAnnotation setArbitraryTypeName(PdfName arbitraryTypeName) {
        return put(PdfName.MN, arbitraryTypeName);
    }

    public PdfName getArbitraryTypeName() {
        return getPdfObject().getAsName(PdfName.MN);
    }
}
