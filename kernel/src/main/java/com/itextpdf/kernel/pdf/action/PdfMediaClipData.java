package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfMediaClipData extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -7030377585169961523L;
	private static final PdfString TEMPACCESS = new PdfString("TEMPACCESS");

    public PdfMediaClipData(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfMediaClipData(String file, PdfFileSpec fs, String mimeType) {
        this(new PdfDictionary());
        PdfDictionary dic = new PdfDictionary();
        markObjectAsIndirect(dic);
        dic.put(PdfName.TF, TEMPACCESS);
        put(PdfName.Type, PdfName.MediaClip).put(PdfName.S, PdfName.MCD).
                put(PdfName.N, new PdfString(String.format("Media clip for %s", file))).
                put(PdfName.CT, new PdfString(mimeType)).
                put(PdfName.P, dic).
                put(PdfName.D, fs.getPdfObject());
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

}
