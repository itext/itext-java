package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

public class PdfIndirectReference extends PdfObject implements Comparable<PdfIndirectReference> {

    // Indicates that the reference could be reused or have to be marked as free.
    protected static final byte Free = 1;
    // Indicates that definition of the reference still not found (e.g. keys in XRefStm).
    protected static final byte Reading = 2;
    // Indicates that @see refersTo changed (using in stamp mode).
    protected static final byte Dirty = 4;

    private static final int LengthOfIndirectsChain = 31;

    /**
     * Indicate same special states of PdfIndirectObject like @see Free, @see Reading, @see Dirty.
     */
    private byte state;

    /**
     * Object number.
     */
    protected final int objNr;
    /**
     * Object generation.
     */
    protected int genNr;

    /**
     * PdfObject that current PdfIndirectReference instance refers to.
     */
    protected PdfObject refersTo = null;

    /**
     * Indirect reference number of object stream containing refersTo object.
     * If refersTo is not placed into object stream - objectStreamNumber = 0.
     */
    protected int objectStreamNumber = 0;

    /**
     * Offset in a document of the {@code refersTo} object.
     * If the object placed into object stream then it is an object index inside object stream.
     */
    protected long offsetOrIndex = 0;

    /**
     * Indicates if the refersTo object has been flushed.
     */
    protected boolean flushed = false;

    /**
     * PdfDocument object belongs to. For direct objects it is null.
     */
    protected PdfDocument pdfDocument = null;

    protected PdfIndirectReference(PdfDocument doc, int objNr, PdfObject refersTo) {
        this(doc, objNr, 0, refersTo);
    }

    protected PdfIndirectReference(PdfDocument doc, int objNr, int genNr, PdfObject refersTo) {
        super();
        this.pdfDocument = doc;
        this.objNr = objNr;
        this.genNr = genNr;
        this.refersTo = refersTo;
    }

    // NOTE
    // If offset = -1 -> object have to initialize.
    // Usually this means that PdfReader found this indirect before reading it in xref table.
    // If offset = 0 -> it means that object is not in use, marked as 'f' in xref table.
    protected PdfIndirectReference(PdfDocument doc, int objNr, int genNr, long offset) {
        super();
        this.pdfDocument = doc;
        this.objNr = objNr;
        this.genNr = genNr;
        this.offsetOrIndex = offset;
        assert offset >= 0;
    }

    public int getObjNr() {
        return objNr;
    }

    public int getGenNr() {
        return genNr;
    }

    public PdfObject getRefersTo() throws PdfException {
        return getRefersTo(true);
    }

    // NOTE
    // This method return direct object and try to resolve indirects chain.
    // But if chain of references has length of more than 32,
    // this method return 31st reference in chain.
    public PdfObject getRefersTo(boolean recursively) throws PdfException {
        if (!recursively) {
            if (refersTo == null && !flushed && getReader() != null) {
                refersTo = getReader().readObject(this);
            }
            return refersTo;
        } else {
            PdfObject currentRefersTo = getRefersTo(false);
            for (int i = 0; i < LengthOfIndirectsChain; i++) {
                if (currentRefersTo instanceof PdfIndirectReference)
                    currentRefersTo = ((PdfIndirectReference) currentRefersTo).getRefersTo(false);
                else
                    break;
            }
            return currentRefersTo;
        }
    }

    public void setRefersTo(PdfObject refersTo) {
        this.refersTo = refersTo;
    }

    public int getObjectStreamNumber() {
        return objectStreamNumber;
    }

    /**
     * Gets refersTo object offset in a document.
     *
     * @return object offset in a document. If refersTo object is in object stream then 0.
     */
    public long getOffset() {
        return objectStreamNumber == 0 ? offsetOrIndex : 0;
    }

    /**
     * Gets refersTo object index in the object stream.
     *
     * @return object index in a document. If refersTo object is not in object stream then 0.
     */
    public int getIndex() {
        return objectStreamNumber == 0 ? 0 : (int)offsetOrIndex;
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
    protected PdfIndirectReference setState(byte state) {
        this.state |= state;
        return this;
    }

    /**
     * Clear state of the flag of current object.
     * @param state special flag state to clear
     */
    protected PdfIndirectReference clearState(byte state) {
        this.state &= 0xFF^state;
        return this;
    }

    protected void releaseObject() {
        refersTo = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdfIndirectReference that = (PdfIndirectReference) o;

        return objNr == that.objNr && genNr == that.genNr;
    }

    @Override
    public int hashCode() {
        int result = objNr;
        result = 31 * result + genNr;
        return result;
    }

    @Override
    public int compareTo(PdfIndirectReference o) {
        if (objNr == o.objNr) {
            if (genNr == o.genNr)
                return 0;
            return (genNr > o.genNr) ? 1 : -1;
        }
        return (objNr > o.objNr) ? 1 : -1;
    }

    @Override
    public byte getType() {
        return IndirectReference;
    }

    @Override
    public PdfDocument getDocument() {
        return pdfDocument;
    }

    @Override
    public String toString() {
        return java.lang.String.format("%d %d R", getObjNr(), getGenNr());
    }

    protected void setObjectStreamNumber(int objectStreamNumber) {
        this.objectStreamNumber = objectStreamNumber;
    }

    protected void setOffsetOrIndex(long offsetOrIndex) {
        this.offsetOrIndex = offsetOrIndex;
    }

    protected void fixOffset(long offset){
        //TODO log invalid offsets
        if (isInUse()) {
            this.offsetOrIndex = offset;
        }
    }

    protected boolean isInUse() {
        return !checkState(Free);
    }

    // NOTE After this operation this indirect reference could be reused for new indirect objects.
    protected void setFree() {
        getDocument().getXref().freeReference(this);
    }

    @Override
    protected PdfIndirectReference newInstance() {
        return pdfDocument.createNextIndirectReference(refersTo);
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) throws PdfException {

    }
}
