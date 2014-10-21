package com.itextpdf.basics;

public class PdfException extends Exception {

    public static final String CannotAddObjectToObjectstream = "cannot.add.object.to.objectstream";
    public static final String CannotCloseDocument = "cannot.close.document";
    public static final String CannotCopyFlushedObject = "cannot.copy.flushed.object";
    public static final String CannotCopyObjectContent = "cannot.copy.object.content";
    public static final String CannotFlushObject = "cannot.flush.object";
    public static final String CannotOpenDocument = "cannot.open.document";
    public static final String CannotReadPdfObject = "cannot.read.pdf.object";
    public static final String DictionaryKey1IsNotAName = "dictionary.key.1.is.not.a.name";
    public static final String DocumentHasNoPages = "document.has.no.pages";
    public static final String ErrorAtFilePointer1 = "error.at.file.pointer.1";
    public static final String ErrorReadingString = "error.reading.string";
    public static final String FdfStartxrefNotFound = "fdf.startxref.not.found";
    public static final String FilePosition0CrossReferenceEntryInThisXrefSubsection = "file.position.0.cross.reference.entry.in.this.xref.subsection";
    public static final String FlushedPageCannotBeAddedOrInserted = "flushed.page.cannot.be.added.or.inserted";
    public static final String FontAndSizeMustBeSetBeforeWritingAnyText = "font.and.size.must.be.set.before.writing.any.text";
    public static final String FontSizeTooSmall = "font.size.too.small";
    public static final String ImageFormatCannotBeRecognized = "image.format.cannot.be.recognized";
    public static final String GtNotExpected = "gt.not.expected";
    public static final String InfiniteIndirectReferenceChain = "infinite.indirect.reference.chain";
    public static final String InvalidCrossReferenceEntryInThisXrefSubsection = "invalid.cross.reference.entry.in.this.xref.subsection";
    public static final String InvalidOffsetForObject1 = "invalid.offset.for.object.1";
    public static final String NumberOfEntriesInThisXrefSubsectionNotFound = "number.of.entries.in.this.xref.subsection.not.found";
    public static final String ObjectCannotBeAddedToObjectStream = "object.cannot.be.added.to.object.stream";
    public static final String ObjectNumberOfTheFirstObjectInThisXrefSubsectionNotFound = "object.number.of.the.first.object.in.this.xref.subsection.not.found";
    public static final String PdfHeaderNotFound = "pdf.header.not.found";
    public static final String PdfStartxrefIsNotFollowedByANumber = "pdf.startxref.is.not.followed.by.a.number";
    public static final String PdfStartxrefNotFound = "pdf.startxref.not.found";
    public static final String UnexpectedCloseBracket = "unexpected.close.bracket";
    public static final String UnexpectedEndOfFile = "unexpected.end.of.file";
    public static final String UnexpectedGtGt = "unexpected.gt.gt";
    public static final String XrefSubsectionNotFound = "xref.subsection.not.found";
    public static final String XrefTableDoesntHaveSuitableItemForObject1 = "xref.table.doesn't.have.suitable.item.for.object.1";

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
