package com.itextpdf.core.pdf.action;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;

public class PdfMediaClipData extends PdfObjectWrapper<PdfDictionary> {

    private static final PdfString TEMPACCESS = new PdfString("TEMPACCESS");

    public PdfMediaClipData(PdfDictionary pdfObject, PdfDocument pdfDocument) throws PdfException {
        super(pdfObject, pdfDocument);
    }

    public PdfMediaClipData(PdfDocument pdfDocument, String file, PdfFileSpec fs, String mimeType) throws PdfException {
        this(new PdfDictionary(), pdfDocument);
        getPdfObject().put(PdfName.Type, PdfName.MediaClip);
        getPdfObject().put(PdfName.S, PdfName.MCD);
        getPdfObject().put(PdfName.N, new PdfString(String.format("Media clip for %s", file)));
        getPdfObject().put(PdfName.CT, new PdfString(mimeType));
        PdfDictionary dic = new PdfDictionary().makeIndirect(pdfDocument);
        dic.put(PdfName.TF, TEMPACCESS);
        getPdfObject().put(PdfName.P, dic);
        getPdfObject().put(PdfName.D, dic);
    }

}
