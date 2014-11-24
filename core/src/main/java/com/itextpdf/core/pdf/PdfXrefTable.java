package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.OutputStream;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

public class PdfXrefTable {

    private static final int InitialCapacity = 32;
    private static final int MaxGeneration = 65535;

    static private final DecimalFormat objectOffsetFormatter = new DecimalFormat("0000000000");
    static private final DecimalFormat objectGenerationFormatter = new DecimalFormat("00000");
    static private final byte[] freeXRefEntry = OutputStream.getIsoBytes(" f \n");
    static private final byte[] inUseXRefEntry = OutputStream.getIsoBytes(" n \n");

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
        reference.setOffsetOrIndex(0);
        reference.setObjectStreamNumber(0);
        reference.setState(PdfIndirectReference.Free);
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

    /**
     * Writes cross reference table to PDF.
     *
     * @return start of cross reference table.
     * @throws java.io.IOException
     * @throws com.itextpdf.basics.PdfException
     */
    protected int writeXrefTable(PdfWriter writer) throws IOException, PdfException {
        // Increment generation number for all freed references.
        for(Integer objNr: freeReferences){
            xref[objNr].genNr++;
        }

        int startxref = writer.getCurrentPos();
        PdfDocument pdfDocument = writer.pdfDocument;
        if (writer.isFullCompression()) {
            PdfStream stream = new PdfStream(pdfDocument);
            stream.put(PdfName.Type, PdfName.XRef);
            stream.put(PdfName.Size, new PdfNumber(pdfDocument.getXRef().size()));
            stream.put(PdfName.W, new PdfArray(new ArrayList<PdfObject>() {{
                add(new PdfNumber(1));
                add(new PdfNumber(4));
                add(new PdfNumber(2));
            }}));
            stream.put(PdfName.Info, pdfDocument.getDocumentInfo().getPdfObject());
            stream.put(PdfName.Root, pdfDocument.getCatalog().getPdfObject());
            stream.getOutputStream().write(0);
            stream.getOutputStream().write(intToBytes(0));
            stream.getOutputStream().write(shortToBytes(0xFFFF));
            PdfXrefTable xref = pdfDocument.getXRef();
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference indirect = xref.get(i);
                if (indirect.getObjectStreamNumber() == 0) {
                    stream.getOutputStream().write(1);
                    assert indirect.getOffset() < Integer.MAX_VALUE;
                    stream.getOutputStream().write(intToBytes((int) indirect.getOffset()));
                    stream.getOutputStream().write(shortToBytes(0));
                } else {
                    stream.getOutputStream().write(2);
                    stream.getOutputStream().write(intToBytes(indirect.getObjectStreamNumber()));
                    stream.getOutputStream().write(shortToBytes(indirect.getIndex()));
                }
            }
            stream.flush();
        } else {
            writer.writeString("xref\n").
                    writeString("0 ").
                    writeInteger(pdfDocument.getXRef().size()).
                    writeString("\n0000000000 65535 f \n");
            PdfXrefTable xref = pdfDocument.getXRef();
            for (int i = 1; i < xref.size(); i++) {
                PdfIndirectReference indirect = xref.get(i);
                if (indirect == null) {
                    writer.writeString(objectOffsetFormatter.format(0)).
                            writeSpace().
                            writeString(objectGenerationFormatter.format(0)).writeBytes(freeXRefEntry);
                } else {
                    writer.writeString(objectOffsetFormatter.format(indirect.getOffset())).
                            writeSpace().
                            writeString(objectGenerationFormatter.format(indirect.getGenNr()));
                    if (indirect.getOffset() > 0) {
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
