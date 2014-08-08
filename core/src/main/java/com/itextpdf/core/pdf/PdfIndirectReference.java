package com.itextpdf.core.pdf;

public class PdfIndirectReference extends PdfObject implements Comparable<PdfIndirectReference> {

    protected int objNr = 0;
    protected int genNr = 0;
    protected PdfObject refersTo = null;

    private PdfIndirectReference() {
        super(IndirectReference);
    }

    public PdfIndirectReference(PdfDocument doc, int objNr, PdfObject refersTo) {
        this(doc, objNr, 0, refersTo);
    }

    public PdfIndirectReference(PdfDocument doc, int objNr, int genNr, PdfObject refersTo) {
        super(doc, PdfObject.IndirectReference);
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

    @Override
    public PdfIndirectReference getIndirectReference() {
        return this;
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
}
