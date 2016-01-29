package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

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
