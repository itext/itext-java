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
        PdfDictionary dic = new PdfDictionary().makeIndirect(pdfDocument);
        dic.put(PdfName.TF, TEMPACCESS);
        put(PdfName.Type, PdfName.MediaClip).put(PdfName.S, PdfName.MCD).
                put(PdfName.N, new PdfString(String.format("Media clip for %s", file))).
                put(PdfName.CT, new PdfString(mimeType)).
                put(PdfName.P, dic).
                put(PdfName.D, fs.getPdfObject());
    }

}
