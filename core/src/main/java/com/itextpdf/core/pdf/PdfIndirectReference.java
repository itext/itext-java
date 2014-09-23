package com.itextpdf.core.pdf;

public class PdfIndirectReference extends PdfObject implements Comparable<PdfIndirectReference> {

    protected int objNr = 0;
    protected int genNr = 0;

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
     * Offset in a document of the <code>refersTo</code> object.
     * If the object placed into object stream then it is an object index inside object stream.
     */
    protected int offsetOrIndex = 0;

    /**
     * Indicates if the refersTo object has been flushed.
     */
    protected boolean flushed = false;

    /**
     * PdfDocument object belongs to. For direct objects it is null.
     */
    protected PdfDocument pdfDocument = null;

    private PdfIndirectReference() {
        super();
    }

    public PdfIndirectReference(PdfDocument doc, int objNr, PdfObject refersTo) {
        this(doc, objNr, 0, refersTo);
    }

    public PdfIndirectReference(PdfDocument doc, int objNr, int genNr, PdfObject refersTo) {
        super();
        this.pdfDocument = doc;
        this.objNr = objNr;
        this.genNr = genNr;
        this.refersTo = refersTo;
    }

    public int getObjNr() {
        return objNr;
    }

    public int getGenNr() {
        return genNr;
    }

    public PdfObject getRefersTo() {
        return refersTo;
    }

    public void setRefersTo(PdfObject refersTo) {
        this.refersTo = refersTo;
    }

    public int getObjectStreamNumber() {
        return objectStreamNumber;
    }

    public void setObjectStreamNumber(int objectStreamNumber) {
        this.objectStreamNumber = objectStreamNumber;
    }

    /**
     * Gets refersTo object offset in a document.
     *
     * @return object offset in a document. If refersTo object is in object stream then 0.
     */
    public int getOffset() {
        return objectStreamNumber == 0 ? offsetOrIndex : 0;
    }

    public void setOffset(int offset) {
        this.offsetOrIndex = objectStreamNumber == 0 ? offset : 0;
    }

    /**
     * Gets refersTo object index in the object stream.
     *
     * @return object index in a document. If refersTo object is not in object stream then 0.
     */
    public int getIndex() {
        return objectStreamNumber == 0 ? 0 : offsetOrIndex;
    }

    public void setIndex(int index) {
        this.offsetOrIndex = objectStreamNumber == 0 ? 0 : index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdfIndirectReference that = (PdfIndirectReference) o;

        if (objNr != that.objNr) return false;
        if (genNr != that.genNr) return false;

        return true;
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
    public boolean canBeInObjStm() {
        return false;
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
        return Integer.toString(getObjNr()) + " " + Integer.toString(getGenNr()) + " R";
    }
}
