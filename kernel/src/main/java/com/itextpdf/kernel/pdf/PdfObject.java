package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
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

    // Indicates if the object has been flushed.
    protected static final byte Flushed = 1;
    // Indicates that the indirect reference of the object could be reused or have to be marked as free.
    protected static final byte Free = 2;
    // Indicates that definition of the indirect reference of the object still not found (e.g. keys in XRefStm).
    protected static final byte Reading = 4;
    // Indicates that object changed (using in stamp mode).
    protected static final byte Modified = 8;
    // Indicates that the indirect reference of the object represents ObjectStream from original document.
    // When PdfReader read ObjectStream reference marked as OriginalObjectStream
    // to avoid further reusing.
    protected static final byte OriginalObjectStream = 16;
    // For internal usage only. Marks objects that shall be written to the output document.
    // Option is needed to build the correct PDF objects tree when closing the document.
    // As a result it avoids writing unused (removed) objects.
    protected static final byte MustBeFlushed = 32;
    // Indicates that the object shall be indirect when it is written to the document.
    // It is used to postpone the creation of indirect reference for the objects that shall be indirect,
    // so it is possible to create such objects without PdfDocument instance.
    protected static final byte MustBeIndirect = 64;

    /**
     * Indicate same special states of PdfIndirectObject or PdfObject like @see Free, @see Reading, @see Modified.
     */
    private byte state;

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
    final public void flush() {
        flush(true);
    }

    /**
     * Flushes the object to the document.
     *
     * @param canBeInObjStm indicates whether object can be placed into object stream.
     * @throws PdfException
     */
    final public void flush(boolean canBeInObjStm) {
        if (isFlushed() || getIndirectReference() == null) {
            //TODO log meaningless call of flush: object is direct or released
            //TODO also if object is mustBeIndirect log that flush call is premature
            return;
        }
        try {
            PdfDocument document = getIndirectReference().getDocument();
            if (document != null) {
                document.checkIsoConformance(this, IsoKey.PDF_OBJECT);
                document.flushObject(this, canBeInObjStm && getType() != Stream
                        && getType() != IndirectReference && getIndirectReference().getGenNumber() == 0);
            }
        } catch (IOException e) {
            throw new PdfException(PdfException.CannotFlushObject, e, this);
        }
    }

    /**
     * Gets the indirect reference associated with the object.
     * The indirect reference is used when flushing object to the document.
     *
     * @return indirect reference.
     */
    public PdfIndirectReference getIndirectReference() {
        return indirectReference;
    }

    /**
     * Checks if object is indirect.
     * <br>
     * Note:
     * Return value {@true} doesn't necessarily mean that indirect reference of this object
     * is not null at the moment. Object could be marked as indirect and
     * be transformed to indirect on flushing.
     * <br>
     * E.g. all PdfStreams are transformed to indirect objects when they are written, but they don't always
     * have indirect references at any given moment.
     * @return returns {@code true} if object is indirect or is to be indirect in the resultant document.
     */
    public boolean isIndirect() {
        return indirectReference != null || checkState(PdfObject.MustBeIndirect);
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
            throw new PdfException(PdfException.ThereIsNoAssociatePdfWriterForMakingIndirects);
        }
        if (reference == null) {
            indirectReference = document.createNextIndirectReference();
            indirectReference.setRefersTo(this);
        } else {
            indirectReference = reference;
            indirectReference.setRefersTo(this);
        }
        clearState(MustBeIndirect);
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
     * Checks state of the flag of current object.
     * @param state special flag to check
     * @return true if the state was set.
     */
    protected boolean checkState(byte state) {
        return (this.state & state) == state;
    }

    /**
     * Sets special states of current object.
     * @param state special flag of current object
     */
    protected <T extends PdfObject> T setState(byte state) {
        this.state |= state;
        return (T) this;
    }

    /**
     * Clear state of the flag of current object.
     * @param state special flag state to clear
     */
    protected <T extends PdfObject> T clearState(byte state) {
        this.state &= 0xFF^state;
        return (T) this;
    }

    /**
     * Indicates is the object has been flushed or not.
     *
     * @return true is object has been flushed, otherwise false.
     */
    public boolean isFlushed() {
        PdfIndirectReference indirectReference = getIndirectReference();
        return (indirectReference != null && indirectReference.checkState(Flushed));
    }

    /**
     * Indicates is the object has been set as modified or not. Useful for incremental updates (e.g. appendMode).
     *
     * @return true is object has been set as modified, otherwise false.
     */
    public boolean isModified() {
        PdfIndirectReference indirectReference = getIndirectReference();
        return (indirectReference != null && indirectReference.checkState(Modified));
    }

    /**
     * Creates clone of the object which belongs to the same document as original object.
     * New object shall not be used in other documents.
     *
     * @return cloned object.
     */
    @Override
    public PdfObject clone() {
        PdfObject newObject = newInstance();
        if (indirectReference != null || checkState(MustBeIndirect)) {
            newObject.setState(MustBeIndirect);
        }
        newObject.copyContent(this, null);
        return newObject;
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document document to copy object to.
     * @return copied object.
     */
    public <T extends PdfObject> T copyTo(PdfDocument document) {
        return copyTo(document, true);
    }

    /**
     * Copies object to a specified document.
     * Works only for objects that are read from existing document, otherwise an exception is thrown.
     *
     * @param document         document to copy object to.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    public  <T extends PdfObject> T copyTo(PdfDocument document, boolean allowDuplicating) {
        if (document == null)
            throw new PdfException(PdfException.DocumentToCopyToCannotBeNull);

        if ((indirectReference != null && indirectReference.getWriter() != null) || checkState(MustBeIndirect)) {
            throw new PdfException(PdfException.CannotCopyIndirectObjectFromTheDocumentThatIsBeingWritten);
        }

        return processCopying(document, allowDuplicating);
    }

    /**
     * Processes two cases of object copying:
     * <ol>
     * <li>copying to the other document</li>
     * <li>cloning inside of the current document</li>
     * </ol>
     *
     * This two cases are distinguished by the state of <code>document</code> parameter:
     * the second case is processed if <code>document</code> is <code>null</code>.
     *
     * @param document if not null: document to copy object to; otherwise indicates that object is to be cloned.
     * @param allowDuplicating indicates if to allow copy objects which already have been copied.
     *                         If object is associated with any indirect reference and allowDuplicating is false then already existing reference will be returned instead of copying object.
     *                         If allowDuplicating is true then object will be copied and new indirect reference will be assigned.
     * @return copied object.
     */
    protected <T extends PdfObject> T processCopying(PdfDocument document, boolean allowDuplicating) {
        if (document != null) {
            //copyTo case
            PdfWriter writer = document.getWriter();
            if (writer == null)
                throw new PdfException(PdfException.CannotCopyToDocumentOpenedInReadingMode);
            return (T) writer.copyObject(this, document, allowDuplicating);

        } else {
            //clone case
            PdfObject obj = this;
            if (obj.isIndirectReference()) {
                PdfObject refTo = ((PdfIndirectReference) obj).getRefersTo();
                obj = refTo != null ? refTo : obj;
            }
            if (obj.isIndirect() && !allowDuplicating) {
                return (T) obj;
            }
            return (T) obj.clone();
        }
    }

    protected <T extends PdfObject> T setIndirectReference(PdfIndirectReference indirectReference) {
        this.indirectReference = indirectReference;
        return (T) this;
    }

    //TODO comment! Add note about flush, modified flag and xref.
    public void setModified() {
        if (indirectReference != null)
            indirectReference.setState(Modified);
    }

    public void release() {
        if (indirectReference != null && indirectReference.getReader() != null
                && !indirectReference.checkState(Flushed)) {
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
            throw new PdfException(PdfException.CannotCopyFlushedObject, this);
    }


}
