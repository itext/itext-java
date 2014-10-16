package com.itextpdf.core.exceptions;

public class PdfException extends Exception {

    public static final String CannotAddObjectToObjectstream = "cannot.add.object.to.objectstream";
    public static final String CannotCloseDocument = "cannot.close.document";
    public static final String CannotCopyFlushedObject = "cannot.copy.flushed.object";
    public static final String CannotCopyObjectContent = "cannot.copy.object.content";
    public static final String CannotFlushObject = "cannot.flush.object";
    public static final String CannotOpenDocument = "cannot.open.document";
    public static final String DocumentHasNoPages = "document.has.no.pages";
    public static final String Error1AtFilePointer2 = "1.at.file.pointer.2";
    public static final String FdfStartxrefNotFound = "fdf.startxref.not.found";
    public static final String FlushedPageCannotBeAddedOrInserted = "flushed.page.cannot.be.added.or.inserted";
    public static final String FontAndSizeMustBeSetBeforeWritingAnyText = "font.and.size.must.be.set.before.writing.any.text";
    public static final String FontSizeTooSmall = "font.size.too.small";
    public static final String GreaterthanNotExpected = "greaterthan.not.expected";
    public static final String InfiniteIndirectReferenceChain = "infinite.indirect.reference.chain";
    public static final String ObjectCannotBeAddedToObjectStream = "object.cannot.be.added.to.object.stream";
    public static final String PdfHeaderNotFound = "pdf.header.not.found";
    public static final String PdfStartxrefNotFound = "pdf.startxref.not.found";

    protected Object object;
    protected String composedMessage;

    public PdfException(String message) {
        super(message);
    }

    public PdfException(String message, Object object) {
        this(message);
        this.object = object;
    }

    public PdfException(String message, Throwable cause) {
        super(message, cause);
    }

    public PdfException(String message, Throwable cause, Object object) {
        this(message, cause);
        this.object = object;
    }

    public PdfException setMessageParams(Object... messageParams) {
        return this;
    }

    public String getComposedMessage() {
        return composedMessage;
    }

}
