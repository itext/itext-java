package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

import java.text.MessageFormat;

public class PdfRendition extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -726500192326824100L;

	public PdfRendition(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfRendition(String file, PdfFileSpec fs, String mimeType) {
        this(new PdfDictionary());
        getPdfObject().put(PdfName.S, PdfName.MR);
        getPdfObject().put(PdfName.N, new PdfString(MessageFormat.format("Rendition for {0}", file)));
        getPdfObject().put(PdfName.C, new PdfMediaClipData(file, fs, mimeType).getPdfObject());
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

}
