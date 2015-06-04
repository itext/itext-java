package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfRuntimeException;

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
     * @throws PdfRuntimeException
     */
    final public void flush() {
        flush(true);
    }

    /**
     * Flushes the object to the document.
     *
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     * @throws PdfRuntimeException
     */
    final public void flush(boolean canBeInObjStm) {
        if (isFlushed() || getIndirectReference() == null) {
            //TODO log meaningless call of flush: object is direct or released
            return;
        }
        try {
            PdfWriter writer = getWriter();
            if (writer != null) {
                writer.flushObject(this, canBeInObjStm && getType() != Stream
                        && getType() != IndirectReference && getIndirectReference().getGenNumber() == 0);
            }
        } catch (IOException e) {
            throw new PdfRuntimeException(PdfRuntimeException.CannotFlushObject, e, this);
        }
    }

    /**
     * Copied object to a specified document.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    public <T extends PdfObject> T copy(PdfDocument document) {
        return copy(document, true);
    }

    /**
     * Copies object.
     *
     * @return copied object.
     */
    public <T extends PdfObject> T copy() {
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
    public <T extends PdfObject> T makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        if (document == null || indirectReference != null) return (T) this;
        if (document.getWriter() == null) {
            throw new PdfRuntimeException(PdfRuntimeException.ThereIsNoAssociatePdfWriterForMakingIndirects);
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
    public <T extends PdfObject> T makeIndirect(PdfDocument document) {
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
     *
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

    /**
     * Copied object to a specified document.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     * @throws PdfRuntimeException
     */
    public <T extends PdfObject> T copy(PdfDocument document, boolean allowDuplicating) {
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

    protected <T extends PdfObject> T setIndirectReference(PdfIndirectReference indirectReference) {
        this.indirectReference = indirectReference;
        return (T) this;
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
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfNull</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isNull() {
        return getType() == Null;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfBoolean</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isBoolean() {
        return getType() == Boolean;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfNumber</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isNumber() {
        return getType() == Number;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfString</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isString() {
        return getType() == String;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfName</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isName() {
        return getType() == Name;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfArray</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isArray() {
        return getType() == Array;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfDictionary</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isDictionary() {
        return getType() == Dictionary;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfStream</CODE>.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean isStream() {
        return getType() == Stream;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfIndirectReference</CODE>.
     *
     * @return <CODE>true</CODE> if this is an indirect reference,
     *   otherwise <CODE>false</CODE>
     */
    public boolean isIndirectReference() {
        return getType() == IndirectReference;
    }

    /**
     * Checks if this <CODE>PdfObject</CODE> is of the type
     * <CODE>PdfLiteral</CODE>.
     *
     * @return <CODE>true</CODE> if this is a literal,
     *   otherwise <CODE>false</CODE>
     */
    public boolean isLiteral() {
        return getType() == Literal;
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
    protected void copyContent(PdfObject from, PdfDocument document) {
        if (isFlushed())
            throw new PdfRuntimeException(PdfRuntimeException.CannotCopyFlushedObject, this);
    }


}
