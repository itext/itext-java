package com.itextpdf.core.exceptions;

public class PdfException extends Exception {

    public static final String CannotCopyObject = "cannot.copy.object";
    public static final String DocumentHasNoPages = "document.has.no.pages";
    public static final String IndirectReferenceAlreadyAssigned = "indirect.reference.already.assigned";
    public static final String FlushedPageCannotBeAddedOrInserted = "flushed.page.cannot.be.added.or.inserted";
    public static final String FontAndSizeMustBeSetBeforeWritingAnyText = "font.and.size.must.be.set.before.writing.any.text";
    public static final String FontSizeTooSmall = "font.size.too.small";
    public static final String ObjectCannotBeAddedToObjectStream = "object.cannot.be.added.to.object.stream";
    public static final String error1AtFilePointer2 = "1.at.file.pointer.2";
    public static final String pdfHeaderNotFound = "pdf.header.not.found";
    public static final String pdfStartxrefNotFound = "pdf.startxref.not.found";
    public static final String fdfStartxrefNotFound = "fdf.startxref.not.found";
    public static final String greaterthanNotExpected = "greaterthan.not.expected";

    public PdfException() {
        super();
    }

    public PdfException(String message) {
        super(message);
    }

    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfException(Throwable cause) {
        super(cause);
    }
}
