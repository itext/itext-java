package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfRendition extends PdfObjectWrapper<PdfDictionary> {

    public PdfRendition(PdfDictionary pdfObject, PdfDocument pdfDocument) {
        super(pdfObject);
        makeIndirect(pdfDocument);
    }

    public PdfRendition(PdfDocument pdfDocument, String file, PdfFileSpec fs, String mimeType) {
        this(new PdfDictionary(), pdfDocument);
        getPdfObject().put(PdfName.S, PdfName.MR);
        getPdfObject().put(PdfName.N, new PdfString(String.format("Rendition for %s", file)));
        getPdfObject().put(PdfName.C, new PdfMediaClipData(pdfDocument, file, fs, mimeType).getPdfObject());
    }


}
