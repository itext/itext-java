package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.filespec.PdfFileSpec;

public class PdfFileAttachmentAnnotation extends PdfMarkupAnnotation {

    public PdfFileAttachmentAnnotation(PdfDocument document, Rectangle rect) throws PdfException {
        super(document, rect);
    }

    public PdfFileAttachmentAnnotation(PdfDocument document, Rectangle rect, PdfFileSpec file) throws PdfException {
        this(document, rect);
        put(PdfName.FS, file);
    }

    public PdfFileAttachmentAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.FileAttachment;
    }

    public PdfObject getFileSpecObject() throws PdfException {
        return getPdfObject().get(PdfName.FS);
    }

}
