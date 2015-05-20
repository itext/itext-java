package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.io.OutputStream;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

    private final TreeSet<Integer> freeReferences;

    public PdfXrefTable() {
        this(InitialCapacity);
    }

    public PdfXrefTable(int capacity) {
        if (capacity < 1)
            capacity = InitialCapacity;
        xref = new PdfIndirectReference[capacity];
        freeReferences = new TreeSet<Integer>();
        add(new PdfIndirectReference(null, 0, MaxGeneration, 0).setState(PdfIndirectReference.Free));
    }

    /**
     * Adds indirect reference to list of indirect objects.
     *
     * @param indirectReference indirect reference to add.
     */
    public PdfIndirectReference add(PdfIndirectReference indirectReference) {
        if (indirectReference == null)
            return null;
        int objNr = indirectReference.getObjNumber();
        this.count = Math.max(this.count, objNr);
        ensureCount(objNr);
        xref[objNr] = indirectReference;
        return indirectReference;
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
        PdfIndirectReference indirectReference;
        if (freeReferences.size() > 0) {
            indirectReference = xref[freeReferences.pollFirst()];
            assert indirectReference.isFree();
            indirectReference.setOffset(0);
            indirectReference.setRefersTo(object);
            indirectReference.clearState(PdfIndirectReference.Free);
        } else {
            indirectReference = new PdfIndirectReference(document, ++count, object);
            add(indirectReference);
        }
        return indirectReference.setState(PdfIndirectReference.Modified);
    }

    protected void freeReference(PdfIndirectReference indirectReference) {
        indirectReference.setOffset(0);
        indirectReference.setState(PdfIndirectReference.Free);
        if (!indirectReference.checkState(PdfIndirectReference.Flushed)) {
            if (indirectReference.refersTo != null) {
                indirectReference.refersTo.setIndirectReference(null);
                indirectReference.refersTo = null;
            }
            if (indirectReference.getGenNumber() < MaxGeneration)
                freeReferences.add(indirectReference.getObjNumber());
        }
    }

    protected void setCapacity(int capacity) {
        if (capacity > xref.length) {
            extendXref(capacity);
        }
    }

    /**
     * Writes cross reference table and trailer to PDF.
     *
     * @throws java.io.IOException
     * @throws com.itextpdf.basics.PdfException
     */
    protected void writeXrefTableAndTrailer(PdfDocument document, PdfObject fileId, PdfObject crypto) throws IOException, PdfException {
        PdfWriter writer = document.getWriter();
        if (document.appendMode) {
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
        if (document.appendMode) {
            first = 1;
            len = 0;
        }
        for (int i = 1; i < size(); i++) {
            PdfIndirectReference indirectReference = xref[i];
            if (indirectReference != null) {
                if ((document.appendMode && !indirectReference.checkState(PdfIndirectReference.Modified)) ||
                        (indirectReference.isFree() && indirectReference.getGenNumber() == 0) ||
                        (!indirectReference.checkState(PdfIndirectReference.Flushed))) {
                    indirectReference = null;
                }
            }

            if (indirectReference == null) {
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
        if (document.appendMode && sections.size() == 0) { // no modifications.
            xref = null;
            return;
        }

        int size = sections.get(sections.size() - 2) + sections.get(sections.size() - 1);
        long startxref = writer.getCurrentPos();
        if (writer.isFullCompression()) {
            PdfStream xrefStream = new PdfStream(document);
            xrefStream.put(PdfName.Type, PdfName.XRef);
            xrefStream.put(PdfName.ID, fileId);
            if (crypto != null)
                xrefStream.put(PdfName.Encrypt, crypto);
            xrefStream.put(PdfName.Size, new PdfNumber(size));
            xrefStream.put(PdfName.W, new PdfArray(new ArrayList<PdfObject>() {{
                add(new PdfNumber(1));
                add(new PdfNumber(4));
                add(new PdfNumber(2));
            }}));
            xrefStream.put(PdfName.Info, document.getDocumentInfo().getPdfObject());
            xrefStream.put(PdfName.Root, document.getCatalog().getPdfObject());
            PdfArray index = new PdfArray();
            for (Integer section : sections) {
                index.add(new PdfNumber(section.intValue()));
            }
            if (document.appendMode) {
                PdfNumber lastXref = new PdfNumber(document.reader.getLastXref());
                xrefStream.put(PdfName.Prev, lastXref);
            }
            xrefStream.put(PdfName.Index, index);
            PdfXrefTable xref = document.getXref();
            for (int k = 0; k < sections.size(); k += 2) {
                first = sections.get(k);
                len = sections.get(k + 1);
                for (int i = first; i < first + len; i++) {
                    PdfIndirectReference indirectReference = xref.get(i);
                    if (indirectReference == null)
                        continue;
                    if (indirectReference.isFree()) {
                        xrefStream.getOutputStream().write(0);
                        //NOTE The object number of the next free object should be at this position due to spec.
                        xrefStream.getOutputStream().write(intToBytes(0));
                        xrefStream.getOutputStream().write(shortToBytes(indirectReference.getGenNumber()));
                    } else if (indirectReference.getObjStreamNumber() == 0) {
                        xrefStream.getOutputStream().write(1);
                        assert indirectReference.getOffset() < Integer.MAX_VALUE;
                        xrefStream.getOutputStream().write(intToBytes((int) indirectReference.getOffset()));
                        xrefStream.getOutputStream().write(shortToBytes(indirectReference.getGenNumber()));
                    } else {
                        xrefStream.getOutputStream().write(2);
                        xrefStream.getOutputStream().write(intToBytes(indirectReference.getObjStreamNumber()));
                        xrefStream.getOutputStream().write(shortToBytes(indirectReference.getIndex()));
                    }
                }
            }
            xrefStream.flush();
        } else {
            writer.writeString("xref\n");
            PdfXrefTable xref = document.getXref();
            for (int k = 0; k < sections.size(); k += 2) {
                first = sections.get(k);
                len = sections.get(k + 1);
                writer.writeInteger(first).writeSpace().writeInteger(len).writeByte((byte) '\n');
                for (int i = first; i < first + len; i++) {
                    PdfIndirectReference indirectReference = xref.get(i);
                    writer.writeString(objectOffsetFormatter.format(indirectReference.getOffset())).writeSpace().
                            writeString(objectGenerationFormatter.format(indirectReference.getGenNumber())).writeSpace();
                    if (indirectReference.isFree()) {
                        writer.writeBytes(freeXRefEntry);
                    } else {
                        writer.writeBytes(inUseXRefEntry);
                    }
                }
            }
            PdfDictionary trailer = document.getTrailer();
            // Remove all unused keys in case stamp mode in case original file has full compression, but destination file has not.
            trailer.remove(PdfName.W);
            trailer.remove(PdfName.Index);
            trailer.remove(PdfName.Type);
            trailer.remove(PdfName.Length);
            trailer.put(PdfName.Size, new PdfNumber(size));
            trailer.put(PdfName.ID, fileId);
            if (crypto != null)
                trailer.put(PdfName.Encrypt, crypto);
            writer.writeString("trailer\n");
            if (document.appendMode) {
                PdfNumber lastXref = new PdfNumber(document.reader.getLastXref());
                trailer.put(PdfName.Prev, lastXref);
            }
            writer.write(document.getTrailer());

        }

        writer.writeString("\nstartxref\n").
                writeLong(startxref).
                writeString("\n%%EOF\n");
        xref = null;
    }

    void clear() {
        for (int i = 1; i <= count; i++) {
            if (xref[i] != null && xref[i].isFree())
                continue;
            xref[i] = null;
        }
        count = 1;
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
