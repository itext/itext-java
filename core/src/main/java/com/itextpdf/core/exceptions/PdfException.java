package com.itextpdf.core.exceptions;

public class PdfException extends Exception {

    public static final String indirectReferenceAlreadyAssigned = "indirect.reference.already.assigned";
    public static final String objectCannotBeAddedToObjectStream = "object.cannot.be.added.to.object.stream";
    public static final String fontAndSizeMustBeSetBeforeWritingAnyText = "font.and.size.must.be.set.before.writing.any.text";
    public static final String fontSizeTooSmall = "font.size.too.small";
    public static final String flushedPageCannotBeAddedOrInserted = "flushed.page.cannot.be.added.or.inserted";
    public static final String theDocumentHasNoPages = "the.document.has.no.pages";

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
