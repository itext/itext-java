package com.itextpdf.kernel.pdf;

public class PdfIndirectReference extends PdfObject implements Comparable<PdfIndirectReference> {

    private static final int LengthOfIndirectsChain = 31;

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
     * PdfDocument object belongs to. For direct objects it is null.
     */
    protected PdfDocument pdfDocument = null;

    protected PdfIndirectReference(PdfDocument doc, int objNr) {
        this(doc, objNr, 0);
    }

    protected PdfIndirectReference(PdfDocument doc, int objNr, int genNr) {
        super();
        this.pdfDocument = doc;
        this.objNr = objNr;
        this.genNr = genNr;
    }

    protected PdfIndirectReference(PdfDocument doc, int objNr, int genNr, long offset) {
        super();
        this.pdfDocument = doc;
        this.objNr = objNr;
        this.genNr = genNr;
        this.offsetOrIndex = offset;
        assert offset >= 0;
    }

    public int getObjNumber() {
        return objNr;
    }

    public int getGenNumber() {
        return genNr;
    }

    public PdfObject getRefersTo() {
        return getRefersTo(true);
    }

    // Gets direct object and try to resolve indirects chain.
    // <p>
    // Note: If chain of references has length of more than 32,
    // this method return 31st reference in chain.
    // </p>
    public PdfObject getRefersTo(boolean recursively) {
        if (!recursively) {
            if (refersTo == null && !checkState(Flushed) && !checkState(Modified) && getReader() != null) {
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

    protected void setRefersTo(PdfObject refersTo) {
        this.refersTo = refersTo;
    }

    public int getObjStreamNumber() {
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
        StringBuilder states = new StringBuilder(" ");
        if (checkState(Free)) {
            states.append("Free; ");
        }
        if (checkState(Modified)) {
            states.append("Modified; ");
        }
        if (checkState(MustBeFlushed)) {
            states.append("MustBeFlushed; ");
        }
        if (checkState(Reading)) {
            states.append("Reading; ");
        }
        if (checkState(Flushed)) {
            states.append("Flushed; ");
        }
        if (checkState(OriginalObjectStream)) {
            states.append("OriginalObjectStream; ");
        }
        return java.lang.String.format("%d %d R%s", getObjNumber(), getGenNumber(), states.substring(0, states.length() - 1));
    }

    protected void setObjStreamNumber(int objectStreamNumber) {
        this.objectStreamNumber = objectStreamNumber;
    }

    protected void setIndex(long index) {
        this.offsetOrIndex = index;
    }

    protected void setOffset(long offset) {
        this.offsetOrIndex = offset;
        this.objectStreamNumber = 0;
    }

    protected void fixOffset(long offset){
        //TODO log invalid offsets
        if (!isFree()) {
            this.offsetOrIndex = offset;
        }
    }

    // NOTE In append mode object could be OriginalObjectStream, but not Modified,
    // so information about this reference would not be added to the new Cross-Reference table.
    // In stamp mode without append the reference will be free.
    protected boolean isFree() {
        return checkState(Free) || checkState(OriginalObjectStream);
    }

    /**
    * Releases indirect reference from the document. Remove link to the referenced indirect object.
    * <p>
    * Note: Be careful when using this method. Do not use this method for wrapper objects,
    * it can be cause of errors.
    * Free indirect reference could be reused for a new indirect object.
    * </p>
    */
    public void setFree() {
        getDocument().getXref().freeReference(this);
    }

    @Override
    protected PdfObject newInstance() {
        return PdfNull.PdfNull;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {

    }
}
