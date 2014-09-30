package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;

import java.io.IOException;

abstract public class PdfObject {

    static public final byte Array = 1;
    static public final byte Boolean = 2;
    static public final byte Dictionary = 3;
    static public final byte IndirectReference = 4;
    static public final byte Name = 5;
    static public final byte Null = 6;
    static public final byte Number = 7;
    static public final byte Stream = 8;
    static public final byte String = 9;


    /**
     * If object is flushed the indirect reference is kept here.
     */
    protected PdfIndirectReference indirectReference = null;

    public PdfObject() {

    }

    /**
     * Gets object type.
     *
     * @return object type.
     */
    abstract public byte getType();

    /**
     * Flushes the object to the document.
     *
     * @throws IOException
     * @throws PdfException
     */
    public void flush() throws IOException, PdfException {
        PdfWriter writer = getWriter();
        if (writer != null)
            writer.flushObject(this);
    }

    /**
     * Gets the indirect reference associated with the object.
     * The indirect reference is used when flushing object to the document.
     * If no reference is associated - create a new one.
     *
     * @return indirect reference.
     */
    public PdfIndirectReference getIndirectReference() {
        return indirectReference;
    }

    /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    public PdfObject makeIndirect(PdfDocument document) {
        setDocument(document);
        return this;
    }

    /**
     * Indicates if the object can be placed to object stream.
     *
     * @return true if object can be placed to object stream, false otherwise.
     */
    public boolean canBeInObjStm() {
        return true;
    }

    /**
     * Indicates is the object has been flushed or not.
     *
     * @return true is object has been flushed, otherwise false.
     */
    public boolean isFlushed() {
        PdfIndirectReference indirectReference = getIndirectReference();
        return (indirectReference != null && indirectReference.flushed);
    }

    /**
     * Gets the document the object belongs to.
     *
     * @return a document the object belongs to. If object is direct return null.
     */
    public PdfDocument getDocument() {
        if (indirectReference != null)
            return indirectReference.getDocument();
        return null;
    }

    /**
     * Sets PdfDocument for the object.
     *
     * @param document a dPdfDocument to set.
     */
    public void setDocument(PdfDocument document) {
        if (document != null && indirectReference == null) {
            indirectReference = document.getNextIndirectReference(this);
            document.addIndirectReference(indirectReference);
        }
    }

    protected PdfWriter getWriter() {
        PdfDocument doc = getDocument();
        if (doc != null)
            return doc.getWriter();
        return null;
    }



}
