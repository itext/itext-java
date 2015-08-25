package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;

public class PdfFileAttachmentAnnotation extends PdfMarkupAnnotation {

    public PdfFileAttachmentAnnotation(PdfDocument document, Rectangle rect) {
        super(document, rect);
    }

    public PdfFileAttachmentAnnotation(PdfDocument document, Rectangle rect, PdfFileSpec file) {
        this(document, rect);
        put(PdfName.FS, file);
    }

    public PdfFileAttachmentAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.FileAttachment;
    }

    public PdfObject getFileSpecObject() {
        return getPdfObject().get(PdfName.FS);
    }

}
