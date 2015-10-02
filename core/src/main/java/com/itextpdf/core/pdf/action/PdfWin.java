package com.itextpdf.core.pdf.action;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObjectWrapper;
import com.itextpdf.core.pdf.PdfString;

/**
* Created by chin on 2/23/2015.
*/
public class PdfWin extends PdfObjectWrapper<PdfDictionary> {

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


}
