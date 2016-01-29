package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;

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
