/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.util.DecimalFormatUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.Version;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

class PdfXrefTable implements Serializable {

    private static final long serialVersionUID = 4171655392492002944L;

    private static final int INITIAL_CAPACITY = 32;
    private static final int MAX_GENERATION = 65535;

    private static final byte[] freeXRefEntry = ByteUtils.getIsoBytes("f \n");
    private static final byte[] inUseXRefEntry = ByteUtils.getIsoBytes("n \n");

    private PdfIndirectReference[] xref;
    private int count = 0;

    private final TreeSet<Integer> freeReferences;

    public PdfXrefTable() {
        this(INITIAL_CAPACITY);
    }

    public PdfXrefTable(int capacity) {
        if (capacity < 1) {
            capacity = INITIAL_CAPACITY;
        }
        xref = new PdfIndirectReference[capacity];
        freeReferences = new TreeSet<>();
        add(new PdfIndirectReference(null, 0, MAX_GENERATION, 0).setState(PdfObject.FREE));
    }

    /**
     * Adds indirect reference to list of indirect objects.
     *
     * @param reference indirect reference to add.
     */
    public PdfIndirectReference add(PdfIndirectReference reference) {
        if (reference == null) {
            return null;
        }
        int objNr = reference.getObjNumber();
        this.count = Math.max(this.count, objNr);
        ensureCount(objNr);
        xref[objNr] = reference;
        return reference;
    }

    public int size() {
        return count + 1;
    }

    public PdfIndirectReference get(int index) {
        if (index > count) {
            return null;
        }
        return xref[index];
    }

    /**
     * Creates next available indirect reference.
     *
     * @return created indirect reference.
     */
    protected PdfIndirectReference createNextIndirectReference(PdfDocument document) {
        PdfIndirectReference reference;
        if (freeReferences.size() > 0) {
            int num = (int) freeReferences.pollFirst();
            reference = xref[num];
            if (reference == null) {
                reference = new PdfIndirectReference(document, num);
                xref[num] = reference;
            }
            reference.setOffset(0);
            reference.clearState(PdfObject.FREE);
        } else {
            reference = new PdfIndirectReference(document, ++count);
            add(reference);
        }
        return reference.setState(PdfObject.MODIFIED);
    }

    protected void freeReference(PdfIndirectReference reference) {
        reference.setOffset(0);
        reference.setState(PdfObject.FREE);
        if (!reference.checkState(PdfObject.FLUSHED)) {
            if (reference.refersTo != null) {
                reference.refersTo.setIndirectReference(null).setState(PdfObject.MUST_BE_INDIRECT);
                reference.refersTo = null;
            }
            if (reference.getGenNumber() < MAX_GENERATION) {
                freeReferences.add(reference.getObjNumber());
                xref[reference.getObjNumber()] = null;
            }

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
     * @throws IOException
     * @throws PdfException
     */
    protected void writeXrefTableAndTrailer(PdfDocument document, PdfObject fileId, PdfObject crypto) throws IOException {
        PdfWriter writer = document.getWriter();
        if (document.isAppendMode()) {
            // Increment generation number for all freed references.
            for (Integer objNr : freeReferences) {
                xref[(int) objNr].genNr++;
            }
        } else {
            for (Integer objNr : freeReferences) {
                xref[(int) objNr] = null;
            }
        }
        freeReferences.clear();


        for (int i = count; i > 0; --i) {
            PdfIndirectReference lastRef = xref[i];
            if (lastRef == null
                    || (lastRef.isFree() && lastRef.getGenNumber() == 0)
                    || (!lastRef.checkState(PdfObject.FLUSHED)
                            && !(document.properties.appendMode && !lastRef.checkState(PdfObject.MODIFIED)))) {
                --count;
            } else {
                break;
            }
        }

        List<Integer> sections = new ArrayList<>();
        int first = 0;
        int len = 1;
        if (document.isAppendMode()) {
            first = 1;
            len = 0;
        }
        for (int i = 1; i < size(); i++) {
            PdfIndirectReference reference = xref[i];
            if (reference != null) {
                if ((document.properties.appendMode && !reference.checkState(PdfObject.MODIFIED)) ||
                        (reference.isFree() && reference.getGenNumber() == 0) ||
                        (!reference.checkState(PdfObject.FLUSHED))) {
                    reference = null;
                }
            }

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
        if (document.properties.appendMode && sections.size() == 0) { // no modifications.
            xref = null;
            return;
        }

        long startxref = writer.getCurrentPos();
        if (writer.isFullCompression()) {
            PdfStream xrefStream = new PdfStream().makeIndirect(document);
            xrefStream.makeIndirect(document);
            xrefStream.put(PdfName.Type, PdfName.XRef);
            xrefStream.put(PdfName.ID, fileId);
            if (crypto != null)
                xrefStream.put(PdfName.Encrypt, crypto);
            xrefStream.put(PdfName.Size, new PdfNumber(this.size()));
            ArrayList<PdfObject> tmpArray = new ArrayList<PdfObject>(3);
            tmpArray.add(new PdfNumber(1));
            tmpArray.add(new PdfNumber(4));
            tmpArray.add(new PdfNumber(2));
            xrefStream.put(PdfName.W, new PdfArray(tmpArray));
            xrefStream.put(PdfName.Info, document.getDocumentInfo().getPdfObject());
            xrefStream.put(PdfName.Root, document.getCatalog().getPdfObject());
            PdfArray index = new PdfArray();
            for (Integer section : sections) {
                index.add(new PdfNumber((int) section));
            }
            if (document.properties.appendMode) {
                PdfNumber lastXref = new PdfNumber(document.reader.getLastXref());
                xrefStream.put(PdfName.Prev, lastXref);
            }
            xrefStream.put(PdfName.Index, index);
            PdfXrefTable xrefTable = document.getXref();
            for (int k = 0; k < sections.size(); k += 2) {
                first = (int) sections.get(k);
                len = (int) sections.get(k + 1);
                for (int i = first; i < first + len; i++) {
                    PdfIndirectReference reference = xrefTable.get(i);
                    if (reference == null) {
                        continue;
                    }
                    if (reference.isFree()) {
                        xrefStream.getOutputStream().write(0);
                        //NOTE The object number of the next free object should be at this position due to spec.
                        xrefStream.getOutputStream().write(intToBytes(0));
                        xrefStream.getOutputStream().write(shortToBytes(reference.getGenNumber()));
                    } else if (reference.getObjStreamNumber() == 0) {
                        xrefStream.getOutputStream().write(1);
                        assert reference.getOffset() < Integer.MAX_VALUE;
                        xrefStream.getOutputStream().write(intToBytes((int) reference.getOffset()));
                        xrefStream.getOutputStream().write(shortToBytes(reference.getGenNumber()));
                    } else {
                        xrefStream.getOutputStream().write(2);
                        xrefStream.getOutputStream().write(intToBytes(reference.getObjStreamNumber()));
                        xrefStream.getOutputStream().write(shortToBytes(reference.getIndex()));
                    }
                }
            }
            xrefStream.flush();
        } else {
            writer.writeString("xref\n");
            PdfXrefTable xrefTable = document.getXref();
            for (int k = 0; k < sections.size(); k += 2) {
                first = (int) sections.get(k);
                len = (int) sections.get(k + 1);
                writer.writeInteger(first).writeSpace().writeInteger(len).writeByte((byte) '\n');
                for (int i = first; i < first + len; i++) {
                    PdfIndirectReference reference = xrefTable.get(i);

                    StringBuilder off = new StringBuilder("0000000000").append(reference.getOffset());
                    StringBuilder gen = new StringBuilder("00000").append(reference.getGenNumber());
                    writer.writeString(off.substring(off.length() - 10, off.length())).writeSpace().
                            writeString(gen.substring(gen.length() - 5, gen.length())).writeSpace();
                    if (reference.isFree()) {
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
            trailer.put(PdfName.Size, new PdfNumber(this.size()));
            trailer.put(PdfName.ID, fileId);
            if (crypto != null)
                trailer.put(PdfName.Encrypt, crypto);
            writer.writeString("trailer\n");
            if (document.properties.appendMode) {
                PdfNumber lastXref = new PdfNumber(document.reader.getLastXref());
                trailer.put(PdfName.Prev, lastXref);
            }
            writer.write(document.getTrailer());
            writer.write('\n');
        }
        writeKeyInfo(writer);
        writer.writeString("startxref\n").
                writeLong(startxref).
                writeString("\n%%EOF\n");
        xref = null;
    }

    void clear() {
        for (int i = 1; i <= count; i++) {
            if (xref[i] != null && xref[i].isFree()) {
                continue;
            }
            xref[i] = null;
        }
        count = 1;
    }

    protected static void writeKeyInfo(PdfWriter writer) throws IOException {
        String platform = "";
        Version version = Version.getInstance();
        String k = version.getKey();
        if (k == null) {
            k = "iText";
        }
        writer.writeString(MessageFormat.format("%{0}-{1}{2}\n", k, version.getRelease(), platform));
    }

    private void ensureCount(int count) {
        if (count >= xref.length) {
            extendXref(count << 1);
        }
    }

    private void extendXref(int capacity) {
        PdfIndirectReference[] newXref = new PdfIndirectReference[capacity];
        System.arraycopy(xref, 0, newXref, 0, xref.length);
        xref = newXref;
    }

    private static byte[] shortToBytes(int n) {
        return new byte[]{(byte) ((n >> 8) & 0xFF), (byte) (n & 0xFF)};
    }

    private static byte[] intToBytes(int n) {
        return new byte[]{(byte) ((n >> 24) & 0xFF), (byte) ((n >> 16) & 0xFF), (byte) ((n >> 8) & 0xFF), (byte) (n & 0xFF)};
    }
}
