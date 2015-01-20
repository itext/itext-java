package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

import java.io.IOException;

abstract public class PdfObject {

    static public final byte Array = 1;
    static public final byte Boolean = 2;
    static public final byte Dictionary = 3;
    static public final byte Literal = 4;
    static public final byte IndirectReference = 5;
    static public final byte Name = 6;
    static public final byte Null = 7;
    static public final byte Number = 8;
    static public final byte Stream = 9;
    static public final byte String = 10;

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
     * @throws PdfException
     */
    final public void flush() throws PdfException {
        flush(true);
    }

    /**
     * Flushes the object to the document.
     *
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     * @throws PdfException
     */
    final public void flush(boolean canBeInObjStm) throws PdfException {
        if (isFlushed() || getIndirectReference() == null) {
            //TODO log meaningless call of flush: object is direct or released
            return;
        }
        try {
            PdfWriter writer = getWriter();
            if (writer != null) {
                writer.flushObject(this, canBeInObjStm && getType() != Stream
                        && getType() != IndirectReference && getIndirectReference().getGenNr() == 0);
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotFlushObject, e, this);
        }
    }

    /**
     * Copied object to a specified document.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    public <T extends PdfObject> T copy(PdfDocument document) throws PdfException {
        return copy(document, true);
    }

    /**
     * Copies object.
     *
     * @return copied object.
     */
    public <T extends PdfObject> T copy() throws PdfException {
        return copy(getDocument());
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
    public <T extends PdfObject> T makeIndirect(PdfDocument document, PdfIndirectReference reference) throws PdfException {
        if (document == null || indirectReference != null) return (T) this;
        if (document.getWriter() == null) {
            throw new PdfException(PdfException.ThereIsNoAssociatePdfWriterForMakingIndirects);
        }
        if (reference == null) {
            indirectReference = document.createNextIndirectReference(this);
        } else {
            indirectReference = reference;
        }
        return (T) this;
    }

     /**
     * Marks object to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    public <T extends PdfObject> T makeIndirect(PdfDocument document) throws PdfException {
        return makeIndirect(document, null);
    }

    /**
     * Indicates is the object has been flushed or not.
     *
     * @return true is object has been flushed, otherwise false.
     */
    public boolean isFlushed() {
        PdfIndirectReference indirectReference = getIndirectReference();
        return (indirectReference != null && indirectReference.checkState(PdfIndirectReference.Flushed));
    }

    /**
     * Indicates is the object has been set as modified or not. Useful for incremental updates (e.g. appendMode).
     * @return true is object has been set as modified, otherwise false.
     */
    public boolean isModified() {
        PdfIndirectReference indirectReference = getIndirectReference();
        return (indirectReference != null && indirectReference.checkState(PdfIndirectReference.Modified));
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

    protected  <T extends PdfObject> T setIndirectReference(PdfIndirectReference indirectReference) {
        this.indirectReference = indirectReference;
        return (T) this;
    }

    /**
     * Copied object to a specified document.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     * @throws PdfException
     */
    protected <T extends PdfObject> T copy(PdfDocument document, boolean allowDuplicating) throws PdfException {
        PdfWriter writer = null;
        if (document == null)
            document = getDocument();
        if (document != null)
            writer = document.getWriter();
        if (writer != null)
            return (T) writer.copyObject(this, document, allowDuplicating);
        T newObject = newInstance();
        newObject.copyContent(this, document);
        return newObject;
    }

    /**
     * Gets a PdfWriter associated with the document object belongs to.
     *
     * @return PdfWriter.
     */
    protected PdfWriter getWriter() {
        PdfDocument doc = getDocument();
        if (doc != null)
            return doc.getWriter();
        return null;
    }

    /**
     * Gets a PdfReader associated with the document object belongs to.
     *
     * @return PdfReader.
     */
    protected PdfReader getReader() {
        PdfDocument doc = getDocument();
        if (doc != null)
            return doc.getReader();
        return null;
    }

    //TODO comment! Add note about flush, modified flag and xref.
    public void setModified() {
        if (indirectReference != null)
            indirectReference.setState(PdfIndirectReference.Modified);
    }

    public void release() {
        if (getReader() != null && indirectReference != null
                && !indirectReference.checkState(PdfIndirectReference.Flushed)) {
            indirectReference.refersTo = null;
            indirectReference = null;
        }
        //TODO log reasonless call of method
    }

    /**
     * Creates new instance of object.
     *
     * @return new instance of object.
     */
    abstract protected <T extends PdfObject> T newInstance();

    /**
     * Copies object content from object 'from'.
     *
     * @param from     object to copy content from.
     * @param document document to copy object to.
     */
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {
        if (isFlushed())
            throw new PdfException(PdfException.CannotCopyFlushedObject, this);
    }


}
