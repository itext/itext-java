package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfRendition extends PdfObjectWrapper<PdfDictionary> {

    public PdfRendition(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfRendition(String file, PdfFileSpec fs, String mimeType) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.S, PdfName.MR);
        getPdfObject().put(PdfName.N, new PdfString(String.format("Rendition for %s", file)));
        getPdfObject().put(PdfName.C, new PdfMediaClipData(file, fs, mimeType).getPdfObject());
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

}
