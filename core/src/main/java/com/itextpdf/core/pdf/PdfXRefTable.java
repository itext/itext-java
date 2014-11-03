package com.itextpdf.core.pdf;

import java.util.TreeSet;

public class PdfXRefTable {

    private static final int InitialCapacity = 32;

    PdfIndirectReference[] xref;
    int count = 0;
    int nextNumber = 0;

    public PdfXRefTable() {
        xref = new PdfIndirectReference[InitialCapacity];
    }

    public PdfXRefTable(int capacity) {
        if (capacity < 1)
            capacity = InitialCapacity;
        xref = new PdfIndirectReference[capacity];
    }

    public TreeSet<PdfIndirectReference> toSet() {
        TreeSet<PdfIndirectReference> indirects = new TreeSet<PdfIndirectReference>();
        for (int i = 0; i < xref.length; i++) {
            if (xref[i] != null)
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

    /**
     * Creates next available indirect reference.
     *
     * @param object an object for which indirect reference should be created.
     * @return created indirect reference.
     */
    protected PdfIndirectReference createNextIndirectReference(PdfDocument document, PdfObject object) {
        return add(new PdfIndirectReference(document, ++nextNumber, object));
    }

    protected void setCapacity(int capacity) {
        if (capacity > xref.length) {
            extendXref(capacity);
        }
    }

    protected void setNextObjectNumber(int nextNumber) {
        this.nextNumber = Math.max(this.nextNumber, nextNumber);
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
