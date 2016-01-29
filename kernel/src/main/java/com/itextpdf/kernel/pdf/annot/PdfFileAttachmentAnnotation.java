package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;

public class PdfFileAttachmentAnnotation extends PdfMarkupAnnotation {

    public PdfFileAttachmentAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfFileAttachmentAnnotation(Rectangle rect, PdfFileSpec file) {
        this(rect);
        put(PdfName.FS, file);
    }

    public PdfFileAttachmentAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.FileAttachment;
    }

    public PdfObject getFileSpecObject() {
        return getPdfObject().get(PdfName.FS);
    }

}
