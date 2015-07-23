package com.itextpdf.core.pdf.action;

import com.itextpdf.core.pdf.*;

/**
* Created by chin on 2/23/2015.
*/
public class PdfTargetDictionary extends PdfObjectWrapper<PdfDictionary> {

    public PdfTargetDictionary(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfTargetDictionary(PdfName r) {
        this(new PdfDictionary());
        put(PdfName.R, r);
    }

    public PdfTargetDictionary(PdfName r, PdfString n, PdfObject p, PdfObject a, PdfTargetDictionary t) {
        this(new PdfDictionary());
        put(PdfName.R, r).put(PdfName.N, n).
                put(PdfName.P, p).
                put(PdfName.A, a).put(PdfName.T, t);
    }

}
