package com.itextpdf.core.pdf;

import java.util.TreeSet;

public class PdfXRefTable {

    private static final int InitialCapacity = 32;

    PdfIndirectReference[] xref;
    int count;

    public PdfXRefTable() {
        xref = new PdfIndirectReference[InitialCapacity];
    }

    public PdfXRefTable(int capacity) {
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
    public void add(PdfIndirectReference indirectReference) {
        if (indirectReference == null)
            return;
        ensureSize(indirectReference.getObjNr());
        xref[indirectReference.getObjNr()] = indirectReference;
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
            throw new IndexOutOfBoundsException();
        }
        return xref[index];
    }

    private void ensureSize(final int size) {
        this.count = Math.max(this.count, size);
        if (xref.length <= size) {
            PdfIndirectReference newXref[] = new PdfIndirectReference[size*2];
            System.arraycopy(xref, 0, newXref, 0, xref.length);
            xref = newXref;
        }
    }
}
