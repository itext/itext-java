package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfWin extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -3057526285278565800L;

	public PdfWin(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfWin(PdfString f) {
        this(new PdfDictionary());
        put(PdfName.F, f);
    }

    public PdfWin(PdfString f, PdfString d, PdfString o, PdfString p) {
        this(new PdfDictionary());
        put(PdfName.F, f).put(PdfName.D, d).put(PdfName.O, o).put(PdfName.P, p);
    }


    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }

}
