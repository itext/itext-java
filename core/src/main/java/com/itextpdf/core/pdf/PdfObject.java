package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.IOException;

public class PdfObject {

    static public final int Array = 1;
    static public final int Boolean = 2;
    static public final int Dictionary = 3;
    static public final int IndirectReference = 4;
    static public final int Name = 5;
    static public final int Number = 6;
    static public final int Stream = 7;
    static public final int String = 8;

    /**
     * Object type: Array, Boolean, Dictionary, ...
     */
    protected int type = 0;

    /**
     * PdfDocument object belongs to. For direct objects it can be null.
     */
    protected PdfDocument pdfDocument = null;

    /**
     * Indicates if the object has been flushed or not.
     */
    protected boolean flushed = false;

    /**
     * Object offset in a document.
     * If the object placed into object stream then it is an object index inside object stream.
     */
    protected int offset = 0;

    /**
     * If object is flushed the indirect reference is kept here.
     */
    protected PdfIndirectReference indirectReference = null;

    /**
     * Object stream reference containing current object.
     * If object is not placed into object stream - objectStream = null.
     */
    protected PdfObjectStream objectStream = null;

    public PdfObject(int type) {
        this(null, type);
    }

    public PdfObject(PdfDocument doc, int type) {
        pdfDocument = doc;
        this.type = type;
    }

    /**
     * Gets object type.
     *
     * @return object type.
     */
    public int getType() {
        return type;
    }

    /**
     * Flushes the object to the document.
     *
     * @throws IOException
     */
    public void flush() throws IOException, PdfException {
        if (flushed)
            return;
        getIndirectReference();
        PdfWriter writer = pdfDocument.getWriter();
        pdfDocument.add(indirectReference);
        if (writer.isFullCompression() && canBeInObjStm()) {
            PdfObjectStream objectStream = writer.getObjectStream();
            objectStream.addObject(this);
        } else {
            offset = writer.getCurrentPos();
            writer.writeToBody(this);
        }
        indirectReference.refersTo = null;
        indirectReference.objectStream = objectStream;
        indirectReference.offset = offset;
        flushed = true;
    }

    /**
     * Gets the pdf document object belongs to.
     *
     * @return pdf document associated object belongs to or null for direct objects.
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * Gets the indirect reference associated with the object.
     * The indirect reference is used when flushing object to the document.
     * If no reference is associated - create a new one.
     *
     * @return indirect reference.
     */
    public PdfIndirectReference getIndirectReference() {
        if (indirectReference == null) {
            if (pdfDocument != null) {
                indirectReference = pdfDocument.getNextIndirectReference(this);
            }
        }
        return indirectReference;
    }

    public PdfObject setIndirectReference(PdfIndirectReference indirectReference) throws PdfException {
        if (this.indirectReference != null)
            throw new PdfException(PdfException.indirectReferenceAlreadyAssigned);
        this.indirectReference = indirectReference;
        return this;
    }

    /**
     * Gets object offset in a document.
     * If object placed into object stream then method returns object index in ovbject stream.
     *
     * @return object offset in a document.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the object stream which contains current object.
     *
     * @return object stream if object s in object stream, null otherwise.
     */
    public PdfObjectStream getObjectStream() {
        return objectStream;
    }

    /**
     * Indicates if the object can be placed to object stream.
     *
     * @return true if object can be placed to object stream, false otherwise.
     */
    public boolean canBeInObjStm() {
        return true;
    }

}
