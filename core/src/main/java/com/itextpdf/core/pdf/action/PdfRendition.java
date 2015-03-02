package com.itextpdf.core.pdf.action;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;

public class PdfRendition extends PdfObjectWrapper<PdfDictionary> {

    public PdfRendition(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    public PdfRendition(PdfDocument pdfDocument, String file, PdfFileSpec fs, String mimeType) throws PdfException {
        this(new PdfDictionary(), pdfDocument);
        getPdfObject().put(PdfName.S, PdfName.MR);
        getPdfObject().put(PdfName.N, new PdfString(String.format("Rendition for %s", file)));
        getPdfObject().put(PdfName.C, new PdfMediaClipData(pdfDocument, file, fs, mimeType).getPdfObject());
    }


}
