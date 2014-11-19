package com.itextpdf.core.pdf;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

public class PdfXRefTable {

    private static final int InitialCapacity = 32;
    private static final int MaxGeneration = 65535;

    private PdfIndirectReference[] xref;
    private int count = 0;
    private int nextNumber = 0;

    private final Queue<Integer> freeReferences;
    protected boolean isXRefStm;

    public PdfXRefTable() {
        this(InitialCapacity);
    }

    public PdfXRefTable(int capacity) {
        if (capacity < 1)
            capacity = InitialCapacity;
        xref = new PdfIndirectReference[capacity];
        freeReferences = new LinkedList<Integer>();
    }

    public TreeSet<PdfIndirectReference> toSet() {
        TreeSet<PdfIndirectReference> indirects = new TreeSet<PdfIndirectReference>();
        for (int i = 0; i < xref.length; i++) {
            if (xref[i] != null && xref[i].isInUse())
                indirects.add(xref[i]);
        }
        return indirects;
    }

    /**
     * Adds indirect reference to list of indirect objects.
     *
     * @param indirectReference indirect reference to add.
     */
    public PdfIndirectReference add(PdfIndirectReference indirectReference) {
        if (indirectReference == null)
            return null;
        int objNr = indirectReference.getObjNr();
        this.count = Math.max(this.count, objNr);
        ensureCount(objNr);
        xref[objNr] = indirectReference;
        return indirectReference;
    }

    public void addAll(Iterable<PdfIndirectReference> indirectReferences) {
        if (indirectReferences == null) return;
        for (PdfIndirectReference indirectReference : indirectReferences) {
            add(indirectReference);
        }
    }

    public void clear() {
        for (int i = 0; i <= count; i++) {
            if (xref[i] != null && !xref[i].isInUse())
                continue;
            xref[i] = null;
        }
        count = 0;
    }

    public int size() {
        return count + 1;
    }

    public PdfIndirectReference get(final int index) {
        if (index > count) {
            return null;
        }
        return xref[index];
    }

    protected boolean isXRefStm() {
        return isXRefStm;
    }

    protected void setXRefStm(boolean isXRefStm) {
        this.isXRefStm = isXRefStm;
    }

    /**
     * Creates next available indirect reference.
     *
     * @param object an object for which indirect reference should be created.
     * @return created indirect reference.
     */
    protected PdfIndirectReference createNextIndirectReference(PdfDocument document, PdfObject object) {
        if (freeReferences.size() > 0) {
            PdfIndirectReference reference = xref[freeReferences.poll()];
            assert !reference.isInUse();
            reference.setOffsetOrIndex(0);
            reference.setRefersTo(object);
            return reference;
        }
        return add(new PdfIndirectReference(document, ++nextNumber, object));
    }

    protected void freeReference(PdfIndirectReference reference) {
        reference.setOffsetOrIndex(PdfIndirectReference.Free);
        reference.setObjectStreamNumber(0);
        if (reference.refersTo != null) {
            reference.refersTo.setIndirectReference(null);
            reference.refersTo = null;
        }
        if (reference.getObjNr() != 0 && reference.getGenNr() < MaxGeneration)
            freeReferences.add(reference.getObjNr());
    }

    protected void setCapacity(int capacity) {
        if (capacity > xref.length) {
            extendXref(capacity);
        }
    }

    protected void updateNextObjectNumber() {
        this.nextNumber = size();
    }

    private void ensureCount(final int count) {
        if (count >= xref.length) {
            extendXref(count << 1);
        }
    }

    private void extendXref(final int capacity) {
        PdfIndirectReference newXref[] = new PdfIndirectReference[capacity];
        System.arraycopy(xref, 0, newXref, 0, xref.length);
        xref = newXref;
    }
}
