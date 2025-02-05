/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.actions.events.AddFingerPrintEvent;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.validation.context.XrefTableValidationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of a cross-referenced table of a PDF document.
 */
public class PdfXrefTable {


    private static final int INITIAL_CAPACITY = 32;
    private static final int MAX_GENERATION = 65535;

    /**
     * The maximum offset in a cross-reference stream. This is a limitation of the PDF specification.
     * SPEC1.7: 7.5.4 Cross reference trailer
     * <p>
     *
     * It states that the offset should be a 10-digit byte, so the maximum value is 9999999999.
     * This is the max value that can be represented in 10 bytes.
     */
    private static final long MAX_OFFSET_IN_CROSS_REFERENCE_STREAM = 9_999_999_999L;
    private static final byte[] freeXRefEntry = ByteUtils.getIsoBytes("f \n");
    private static final byte[] inUseXRefEntry = ByteUtils.getIsoBytes("n \n");

    private PdfIndirectReference[] xref;
    private int count = 0;
    private boolean readingCompleted;
    private MemoryLimitsAwareHandler memoryLimitsAwareHandler;

    /**
     * Free references linked list is stored in a form of a map, where:
     * key - free reference obj number;
     * value - previous item in the linked list of free references for the object denoted by the key.
     */
    private final TreeMap<Integer, PdfIndirectReference> freeReferencesLinkedList;

    /**
     * Creates a {@link PdfXrefTable} which will be used to store xref structure of the pdf document.
     * Capacity and {@link MemoryLimitsAwareHandler} instance would be set by default values.
     */
    public PdfXrefTable() {
        this(INITIAL_CAPACITY);
    }

    /**
     * Creates a {@link PdfXrefTable} which will be used to store xref structure of the pdf document.
     *
     * @param capacity initial capacity of xref table.
     */
    public PdfXrefTable(int capacity) {
        this(capacity, null);
    }

    /**
     * Creates a {@link PdfXrefTable} which will be used to store xref structure of the pdf document.
     *
     * @param memoryLimitsAwareHandler custom {@link MemoryLimitsAwareHandler} to set.
     */
    public PdfXrefTable(MemoryLimitsAwareHandler memoryLimitsAwareHandler) {
        this(INITIAL_CAPACITY, memoryLimitsAwareHandler);
    }

    /**
     * Creates a {@link PdfXrefTable} which will be used to store xref structure of the pdf document.
     *
     * @param capacity initial capacity of xref table.
     * @param memoryLimitsAwareHandler memoryLimitsAwareHandler custom {@link MemoryLimitsAwareHandler} to set.
     */
    public PdfXrefTable(int capacity, MemoryLimitsAwareHandler memoryLimitsAwareHandler) {
        if (capacity < 1) {
            capacity = memoryLimitsAwareHandler == null ? INITIAL_CAPACITY
                    : Math.min(INITIAL_CAPACITY, memoryLimitsAwareHandler.getMaxNumberOfElementsInXrefStructure());
        }
        this.memoryLimitsAwareHandler = memoryLimitsAwareHandler;
        if (this.memoryLimitsAwareHandler != null) {
            this.memoryLimitsAwareHandler.checkIfXrefStructureExceedsTheLimit(capacity);
        }
        this.xref = new PdfIndirectReference[capacity];
        this.freeReferencesLinkedList = new TreeMap<>();
        add((PdfIndirectReference) new PdfIndirectReference(null, 0, MAX_GENERATION, 0).setState(PdfObject.FREE));
    }

    /**
     * Sets custom {@link MemoryLimitsAwareHandler}.
     *
     * @param memoryLimitsAwareHandler instance to set.
     */
    public void setMemoryLimitsAwareHandler(MemoryLimitsAwareHandler memoryLimitsAwareHandler) {
        this.memoryLimitsAwareHandler = memoryLimitsAwareHandler;
    }

    /**
     * Adds indirect reference to list of indirect objects.
     *
     * @param reference indirect reference to add.
     * @return reference from param
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

    /**
     * Get size of cross-reference table.
     *
     * @return amount of lines including zero-object
     */
    public int size() {
        return count + 1;
    }

    /**
     * Calculates a number of stored references to indirect objects.
     *
     * @return number of indirect objects
     */
    public int getCountOfIndirectObjects() {
        int countOfIndirectObjects = 0;

        for (final PdfIndirectReference ref: xref) {
            if (ref != null && ! ref.isFree()) {
                countOfIndirectObjects++;
            }
        }

        return countOfIndirectObjects;
    }

    /**
     * Get appropriate reference to indirect object.
     *
     * @param index is the index of required object
     * @return reference to object with the provided index
     */
    public PdfIndirectReference get(int index) {
        if (index > count) {
            return null;
        }
        return xref[index];
    }

    /**
     * Creates next available indirect reference.
     *
     * @param document is the current {@link PdfDocument document}
     * @return created indirect reference.
     */
    protected PdfIndirectReference createNextIndirectReference(PdfDocument document) {
        PdfIndirectReference reference = new PdfIndirectReference(document, ++count);
        add(reference);
        return (PdfIndirectReference) reference.setState(PdfObject.MODIFIED);
    }

    /**
     * Set the reference to free state.
     *
     * @param reference is a reference to be updated.
     */
    protected void freeReference(PdfIndirectReference reference) {
        if (reference.isFree()) {
            return;
        }
        if (reference.checkState(PdfObject.MUST_BE_FLUSHED)) {
            Logger logger = LoggerFactory.getLogger(PdfXrefTable.class);
            logger.error(IoLogMessageConstant.INDIRECT_REFERENCE_USED_IN_FLUSHED_OBJECT_MADE_FREE);
            return;
        }
        if (reference.checkState(PdfObject.FLUSHED)) {
            Logger logger = LoggerFactory.getLogger(PdfXrefTable.class);
            logger.error(IoLogMessageConstant.ALREADY_FLUSHED_INDIRECT_OBJECT_MADE_FREE);
            return;
        }

        reference.setState(PdfObject.FREE).setState(PdfObject.MODIFIED);

        appendNewRefToFreeList(reference);

        if (reference.getGenNumber() < MAX_GENERATION) {
            reference.genNr++;
        }

    }

    /**
     * Gets the capacity of xref stream.
     *
     * @return the capacity of xref stream.
     */
    protected int getCapacity() {
        return xref.length;
    }

    /**
     * Increase capacity of the array of indirect references.
     *
     * @param capacity is a new capacity to set
     */
    protected void setCapacity(int capacity) {
        if (capacity > xref.length) {
            extendXref(capacity);
        }
    }

    /**
     * Writes cross reference table and trailer to PDF.
     *
     * @param document is the current {@link PdfDocument document}
     * @param fileId   field id
     * @param crypto   pdf encryption
     * @throws IOException if any I/O error occurs
     */
    protected void writeXrefTableAndTrailer(PdfDocument document, PdfObject fileId, PdfObject crypto) throws IOException {
        PdfWriter writer = document.getWriter();

        if (!document.properties.appendMode) {
            for (int i = count; i > 0; --i) {
                PdfIndirectReference lastRef = xref[i];
                if (lastRef == null || lastRef.isFree()) {
                    removeFreeRefFromList(i);
                    --count;
                } else {
                    break;
                }
            }
        }

        PdfStream xrefStream = null;
        if (writer.isFullCompression()) {
            xrefStream = new PdfStream();
            xrefStream.makeIndirect(document);
        }
        List<Integer> sections = createSections(document, false);
        boolean noModifiedObjects = (sections.size() == 0) ||
                (xrefStream != null && sections.size() == 2 && sections.get(0) == count && sections.get(1) == 1);
        if (document.properties.appendMode && noModifiedObjects) {
            // No modifications in document
            xref = null;
            return;
        }

        document.checkIsoConformance(new XrefTableValidationContext(this));

        long startxref = writer.getCurrentPos();
        long xRefStmPos = -1;
        if (xrefStream != null) {
            xrefStream.put(PdfName.Type, PdfName.XRef);
            xrefStream.put(PdfName.ID, fileId);
            if (crypto != null)
                xrefStream.put(PdfName.Encrypt, crypto);
            xrefStream.put(PdfName.Size, new PdfNumber(this.size()));

            int offsetSize = getOffsetSize(Math.max(startxref, size()));
            xrefStream.put(PdfName.W, new PdfArray(
                    Arrays.asList((PdfObject) new PdfNumber(1), new PdfNumber(offsetSize), new PdfNumber(2))));
            if (document.getTrailer().get(PdfName.Info) != null) {
                xrefStream.put(PdfName.Info, document.getTrailer().get(PdfName.Info));
            }
            xrefStream.put(PdfName.Root, document.getCatalog().getPdfObject());
            PdfArray index = new PdfArray();
            for (Integer section : sections) {
                index.add(new PdfNumber((int) section));
            }
            if (document.properties.appendMode && !document.reader.hybridXref) {
                // "not meaningful in hybrid-reference files"
                PdfNumber lastXref = new PdfNumber(document.reader.getLastXref());
                xrefStream.put(PdfName.Prev, lastXref);
            }
            xrefStream.put(PdfName.Index, index);
            xrefStream.getIndirectReference().setOffset(startxref);
            PdfXrefTable xrefTable = document.getXref();
            for (int k = 0; k < sections.size(); k += 2) {
                int first = (int) sections.get(k);
                int len = (int) sections.get(k + 1);
                for (int i = first; i < first + len; i++) {
                    PdfIndirectReference reference = xrefTable.get(i);
                    if (reference.isFree()) {
                        xrefStream.getOutputStream().write(0);
                        xrefStream.getOutputStream().write(reference.getOffset(), offsetSize);
                        xrefStream.getOutputStream().write(reference.getGenNumber(), 2);
                    } else if (reference.getObjStreamNumber() == 0) {
                        xrefStream.getOutputStream().write(1);
                        xrefStream.getOutputStream().write(reference.getOffset(), offsetSize);
                        xrefStream.getOutputStream().write(reference.getGenNumber(), 2);
                    } else {
                        xrefStream.getOutputStream().write(2);
                        xrefStream.getOutputStream().write(reference.getObjStreamNumber(), offsetSize);
                        xrefStream.getOutputStream().write(reference.getIndex(), 2);
                    }
                }
            }
            xrefStream.flush();
            xRefStmPos = startxref;
        }

        // For documents with hybrid cross-reference table, i.e. containing xref streams as well as regular xref sections,
        // we write additional regular xref section at the end of the document because the /Prev reference from
        // xref stream to a regular xref section doesn't seem to be valid
        boolean needsRegularXref = !writer.isFullCompression() || (document.properties.appendMode && document.reader.hybridXref);

        if (needsRegularXref) {
            startxref = writer.getCurrentPos();
            writer.writeString("xref\n");
            PdfXrefTable xrefTable = document.getXref();
            if (xRefStmPos != -1) {
                // Get rid of all objects from object stream. This is done for hybrid documents
                sections = createSections(document, true);
            }
            for (int k = 0; k < sections.size(); k += 2) {
                int first = (int) sections.get(k);
                int len = (int) sections.get(k + 1);
                writer.writeInteger(first).writeSpace().writeInteger(len).writeByte((byte) '\n');
                for (int i = first; i < first + len; i++) {
                    PdfIndirectReference reference = xrefTable.get(i);
                    if (reference.getOffset() > MAX_OFFSET_IN_CROSS_REFERENCE_STREAM) {
                        throw new PdfException(KernelExceptionMessageConstant.XREF_HAS_AN_ENTRY_WITH_TOO_BIG_OFFSET);
                    }
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
            if (xRefStmPos != -1) {
                trailer.put(PdfName.XRefStm, new PdfNumber(xRefStmPos));
            }
            if (crypto != null) {
                trailer.put(PdfName.Encrypt, crypto);
            }

            writer.writeString("trailer\n");
            if (document.properties.appendMode) {
                PdfNumber lastXref = new PdfNumber(document.reader.getLastXref());
                trailer.put(PdfName.Prev, lastXref);
            }
            writer.write(document.getTrailer());
            writer.write('\n');
        }
        EventManager.getInstance().onEvent(new AddFingerPrintEvent(document));
        writer.writeString("startxref\n").
                writeLong(startxref).
                writeString("\n%%EOF\n");
        xref = null;
        freeReferencesLinkedList.clear();
    }

    /**
     * Change the state of the cross-reference table to mark that reading of the document
     * was completed.
     */
    void markReadingCompleted() {
        readingCompleted = true;
    }

    /**
     * Change the state of the cross-reference table to unmark that reading of the document
     * was completed.
     */
    void unmarkReadingCompleted() {
        readingCompleted = false;
    }

    /**
     * Check if reading of the document was completed.
     *
     * @return true if reading was completed and false otherwise
     */
    boolean isReadingCompleted() {
        return readingCompleted;
    }

    /**
     * Set up appropriate state for the free references list.
     *
     * @param pdfDocument is the current {@link PdfDocument document}
     */
    void initFreeReferencesList(PdfDocument pdfDocument) {
        freeReferencesLinkedList.clear();

        // ensure zero object is free
        xref[0].setState(PdfObject.FREE);
        TreeSet<Integer> freeReferences = new TreeSet<>();
        for (int i = 1; i < size() && i < xref.length; ++i) {
            PdfIndirectReference ref = xref[i];
            if (ref == null || ref.isFree()) {
                freeReferences.add(i);
            }
        }

        PdfIndirectReference prevFreeRef = xref[0];
        while (!freeReferences.<Integer>isEmpty()) {
            int currFreeRefObjNr = -1;
            if (prevFreeRef.getOffset() <= Integer.MAX_VALUE) {
                currFreeRefObjNr = (int) prevFreeRef.getOffset();
            }
            if (!freeReferences.contains(currFreeRefObjNr) || xref[currFreeRefObjNr] == null) {
                break;
            }

            freeReferencesLinkedList.put(currFreeRefObjNr, prevFreeRef);
            prevFreeRef = xref[currFreeRefObjNr];
            freeReferences.remove(currFreeRefObjNr);
        }

        while (!freeReferences.<Integer>isEmpty()) {
            int next = freeReferences.pollFirst();
            if (xref[next] == null) {
                if (pdfDocument.properties.appendMode) {
                    continue;
                }
                xref[next] = (PdfIndirectReference) new PdfIndirectReference(pdfDocument, next, 0).setState(PdfObject.FREE).setState(PdfObject.MODIFIED);
            } else if (xref[next].getGenNumber() == MAX_GENERATION && xref[next].getOffset() == 0) {
                continue;
            }
            if (prevFreeRef.getOffset() != (long)next) {
                ((PdfIndirectReference) prevFreeRef.setState(PdfObject.MODIFIED)).setOffset(next);
            }
            freeReferencesLinkedList.put(next, prevFreeRef);
            prevFreeRef = xref[next];
        }

        if (prevFreeRef.getOffset() != 0) {
            ((PdfIndirectReference) prevFreeRef.setState(PdfObject.MODIFIED)).setOffset(0);
        }
        freeReferencesLinkedList.put(0, prevFreeRef);
    }

    /**
     * Method is used for object streams to avoid reuse existed references.
     *
     * @param document is the current {@link PdfDocument document}
     * @return created indirect reference to the object stream
     */
    PdfIndirectReference createNewIndirectReference(PdfDocument document) {
        PdfIndirectReference reference = new PdfIndirectReference(document, ++count);
        add(reference);
        return (PdfIndirectReference) reference.setState(PdfObject.MODIFIED);
    }

    /**
     * Clear the state of the cross-reference table without free references removal.
     */
    void clear() {
        for (int i = 1; i <= count; i++) {
            if (xref[i] != null && xref[i].isFree()) {
                continue;
            }
            xref[i] = null;
        }
        count = 1;
    }

    /**
     * Clear the state of the cross-reference table including free references.
     */
    void clearAllReferences() {
        for (int i = 1; i <= count; i++) {
            xref[i] = null;
        }
        count = 1;
    }

    private List<Integer> createSections(PdfDocument document, boolean dropObjectsFromObjectStream) {
        List<Integer> sections = new ArrayList<>();
        int first = 0;
        int len = 0;
        for (int i = 0; i < size(); i++) {
            PdfIndirectReference reference = xref[i];
            if (document.properties.appendMode && reference != null &&
                    (!reference.checkState(PdfObject.MODIFIED) || (dropObjectsFromObjectStream && reference.getObjStreamNumber() != 0))) {
                reference = null;
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

        return sections;
    }

    /**
     * Gets size of the offset. Max size is 2^40, i.e. 1 Tb.
     */
    private int getOffsetSize(long startxref) {
        assert startxref >= 0 && startxref < (1L << 40);
        //initial size = 5 bytes. It is 1 Tb. Shall be enough.
        int size = 5;
        long mask = 0xff00000000L;
        for (; size > 1; size--) {
            if ((mask & startxref) != 0)
                break;
            // there is no need to use >>> because mask is positive
            mask >>= 8;
        }
        return size;
    }

    private void appendNewRefToFreeList(PdfIndirectReference reference) {
        reference.setOffset(0);
        if (freeReferencesLinkedList.<Integer, PdfIndirectReference>isEmpty()) {
            assert false;
            // free references list is not initialized yet
            return;
        }
        PdfIndirectReference lastFreeRef = freeReferencesLinkedList.get(0);
        ((PdfIndirectReference) lastFreeRef.setState(PdfObject.MODIFIED)).setOffset(reference.getObjNumber());
        freeReferencesLinkedList.put(reference.getObjNumber(), lastFreeRef);
        freeReferencesLinkedList.put(0, reference);
    }

    /**
     * Removes indirect reference from free references linked list.
     * It does not removes it from xref table and affects only the linked list formed by offset values of free references.
     * @param freeRefObjNr object number of the reference to be removed.
     *                     Removes the free reference with the least object number if this parameter is less than zero:
     *                     this could be used for finding the next free reference for reusing.
     * @return {@link PdfIndirectReference} instance of the removed free reference corresponding to the object number
     * passed as parameter. {@code null} - if given object number doesn't correspond to free reference or equals to zero.
     */
    private PdfIndirectReference removeFreeRefFromList(int freeRefObjNr) {
        if (freeReferencesLinkedList.<Integer, PdfIndirectReference>isEmpty()) {
            assert false;
            // free references list is not initialized yet
            return null;
        }
        if (freeRefObjNr == 0) {
            return null;
        }
        if (freeRefObjNr < 0) {
            Integer leastFreeRefObjNum = null;
            for (Map.Entry<Integer, PdfIndirectReference> entry : freeReferencesLinkedList.entrySet()) {
                if (entry.getKey() <= 0 || xref[entry.getKey()].getGenNumber() >= MAX_GENERATION) {
                    continue;
                }
                leastFreeRefObjNum = entry.getKey();
                break;
            }
            if (leastFreeRefObjNum == null) {
                return null;
            }
            freeRefObjNr = (int)leastFreeRefObjNum;
        }

        PdfIndirectReference freeRef = xref[freeRefObjNr];
        if (!freeRef.isFree()) {
            return null;
        }

        PdfIndirectReference prevFreeRef = freeReferencesLinkedList.remove(freeRef.getObjNumber());
        if (prevFreeRef != null) {
            freeReferencesLinkedList.put((int) freeRef.getOffset(), prevFreeRef);
            ((PdfIndirectReference) prevFreeRef.setState(PdfObject.MODIFIED)).setOffset(freeRef.getOffset());
        }

        return freeRef;
    }

    private void ensureCount(int count) {
        if (count >= xref.length) {
            extendXref(count << 1);
        }
    }

    private void extendXref(int capacity) {
        if (this.memoryLimitsAwareHandler != null) {
            this.memoryLimitsAwareHandler.checkIfXrefStructureExceedsTheLimit(capacity);
        }
        PdfIndirectReference[] newXref = new PdfIndirectReference[capacity];
        System.arraycopy(this.xref, 0, newXref, 0, this.xref.length);
        this.xref = newXref;
    }
}
