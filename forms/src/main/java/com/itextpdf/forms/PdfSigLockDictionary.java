package com.itextpdf.forms;

import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfObjectWrapper;

public class PdfSigLockDictionary extends PdfObjectWrapper<PdfDictionary> {

    public PdfSigLockDictionary(PdfDictionary pdfObject) {
        super(pdfObject);
    }
}
