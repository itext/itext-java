package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.OutputStream;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

class PdfXrefTable {

    private static final int InitialCapacity = 32;
    private static final int MaxGeneration = 65535;

    static private final DecimalFormat objectOffsetFormatter = new DecimalFormat("0000000000");
    static private final DecimalFormat objectGenerationFormatter = new DecimalFormat("00000");
    static private final byte[] freeXRefEntry = OutputStream.getIsoBytes("f \n");
    static private final byte[] inUseXRefEntry = OutputStream.getIsoBytes("n \n");

    private PdfIndirectReference[] xref;
    private int count = 0;
    private int nextNumber = 0;

    private final Queue<Integer> freeReferences;
    protected boolean isXRefStm;

    public PdfXrefTable() {
        this(InitialCapacity);
    }

    public PdfXrefTable(int capacity) {
        if (capacity < 1)
            capacity = InitialCapacity;
        xref = new PdfIndirectReference[capacity];
        freeReferences = new LinkedList<Integer>();
        add(new PdfIndirectReference(null, 0, MaxGeneration, 0));
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
        for (int i = 1; i <= count; i++) {
            if (xref[i] != null && !xref[i].isInUse())
                continue;
            xref[i] = null;
        }
        count = 1;
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
        PdfIndirectReference reference;
        if (freeReferences.size() > 0) {
            reference = xref[freeReferences.poll()];
            assert !reference.isInUse();
            reference.setOffsetOrIndex(0);
            reference.setRefersTo(object);
            reference.clearState(PdfIndirectReference.Free);
        } else {
            reference = new PdfIndirectReference(document, ++nextNumber, object);
            add(reference);
        }
        return reference.setState(PdfIndirectReference.Modified);
    }

    protected void freeReference(PdfIndirectReference reference) {
        reference.setOffsetOrIndex(0);
        reference.setObjectStreamNumber(0);
        reference.setState(PdfIndirectReference.Free);
        if (!reference.checkState(PdfIndirectReference.Flushed)) {
            if (reference.refersTo != null) {
                reference.refersTo.setIndirectReference(null);
                reference.refersTo = null;
            }
            if (reference.getGenNr() < MaxGeneration)
                freeReferences.add(reference.getObjNr());
        }
    }

    protected void setCapacity(int capacity) {
        if (capacity > xref.length) {
            extendXref(capacity);
        }
    }

    protected void updateNextObjectNumber() {
        this.nextNumber = size();
    }

    /**
     * Writes cross reference table to PDF.
     *
     * @return start of cross reference table.
     * @throws java.io.IOException
     * @throws com.itextpdf.basics.PdfException
     */
    protected int writeXrefTable(PdfDocument doc) throws IOException, PdfException {
        PdfWriter writer = doc.getWriter();

        if (doc.getReader() != null) {
            // Increment generation number for all freed references.
            for (Integer objNr : freeReferences) {
                xref[objNr].genNr++;
            }
        } else {
            for (Integer objNr : freeReferences) {
                xref[objNr] = null;
            }
        }
        freeReferences.clear();

        ArrayList<Integer> sections = new ArrayList<Integer>();
        int first = 0;
        int len = 1;
        for (int i = 1; i < size(); i++) {
            PdfIndirectReference reference = xref[i];
            if (reference == null) {
                if (len > 0) {
                    sections.add(first);
                    sections.add(len);
                }
                len = 0;
            } else {
                if (len > 0) {
                    len++;
                } else {
                    first = i;
                    len = 1;
                }
            }
        }
        if (len > 0) {
            sections.add(first);
            sections.add(len);
        }

        int startxref = writer.getCurrentPos();
        PdfDocument pdfDocument = writer.pdfDocument;
        if (writer.isFullCompression()) {
            PdfStream stream = new PdfStream(pdfDocument);
            stream.put(PdfName.Type, PdfName.XRef);
            stream.put(PdfName.Size, new PdfNumber(pdfDocument.getXref().size()));
            stream.put(PdfName.W, new PdfArray(new ArrayList<PdfObject>() {{
                add(new PdfNumber(1));
                add(new PdfNumber(4));
                add(new PdfNumber(2));
            }}));
            stream.put(PdfName.Info, pdfDocument.getDocumentInfo().getPdfObject());
            stream.put(PdfName.Root, pdfDocument.getCatalog().getPdfObject());
            PdfArray index = new PdfArray();
            for (int k = 0; k < sections.size(); k++) {
                index.add(new PdfNumber(sections.get(k).intValue()));
            }
            stream.put(PdfName.Index, index);
            stream.getOutputStream().write(0);
            stream.getOutputStream().write(intToBytes(0));
            stream.getOutputStream().write(shortToBytes(0xFFFF));
            PdfXrefTable xref = pdfDocument.getXref();
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference reference = xref.get(i);
                if (reference == null)
                    continue;
                if (!reference.isInUse()) {
                    stream.getOutputStream().write(0);
                    //NOTE The object number of the next free object should be at this position due to spec.
                    stream.getOutputStream().write(intToBytes(0));
                    stream.getOutputStream().write(shortToBytes(reference.getGenNr()));
                } else if (reference.getObjectStreamNumber() == 0) {
                    stream.getOutputStream().write(1);
                    assert reference.getOffset() < Integer.MAX_VALUE;
                    stream.getOutputStream().write(intToBytes((int) reference.getOffset()));
                    stream.getOutputStream().write(shortToBytes(reference.getGenNr()));
                } else {
                    stream.getOutputStream().write(2);
                    stream.getOutputStream().write(intToBytes(reference.getObjectStreamNumber()));
                    stream.getOutputStream().write(shortToBytes(reference.getIndex()));
                }
            }
            stream.flush();
        } else {
            writer.writeString("xref\n");
            PdfXrefTable xref = pdfDocument.getXref();
            for (int k = 0; k < sections.size(); k += 2) {
                first = sections.get(k);
                len = sections.get(k + 1);
                writer.writeInteger(first).writeSpace().writeInteger(len).writeByte((byte) '\n');
                for (int i = first; i < first + len; i++) {
                    PdfIndirectReference reference = xref.get(i);
                    writer.writeString(objectOffsetFormatter.format(reference.getOffset())).writeSpace().
                            writeString(objectGenerationFormatter.format(reference.getGenNr())).writeSpace();
                    if (reference.getOffset() > 0) {
                        writer.writeBytes(inUseXRefEntry);
                    } else {
                        writer.writeBytes(freeXRefEntry);
                    }
                }
            }
        }
        return startxref;
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

    private byte[] shortToBytes(int n) {
        return new byte[]{(byte) ((n >> 8) & 0xFF), (byte) (n & 0xFF)};
    }

    private byte[] intToBytes(int n) {
        return new byte[]{(byte) ((n >> 24) & 0xFF), (byte) ((n >> 16) & 0xFF), (byte) ((n >> 8) & 0xFF), (byte) (n & 0xFF)};
    }
}
