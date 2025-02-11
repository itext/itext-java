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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.utils.ICopyFilter;

public class PdfIndirectReference extends PdfObject implements Comparable<PdfIndirectReference> {


    private static final int LENGTH_OF_INDIRECTS_CHAIN = 31;

    /**
     * Object number.
     */
    protected final int objNr;
    /**
     * Object generation.
     */
    protected int genNr;

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
     * Offset in a document of the {@code refersTo} object.
     * If the object placed into object stream then it is an object index inside object stream.
     */
    protected long offsetOrIndex = 0;

    /**
     * PdfDocument object belongs to. For direct objects it is null.
     */
    protected PdfDocument pdfDocument = null;

    protected PdfIndirectReference(PdfDocument doc, int objNr) {
        this(doc, objNr, 0);
    }

    protected PdfIndirectReference(PdfDocument doc, int objNr, int genNr) {
        super();
        this.pdfDocument = doc;
        this.objNr = objNr;
        this.genNr = genNr;
    }

    protected PdfIndirectReference(PdfDocument doc, int objNr, int genNr, long offset) {
        super();
        this.pdfDocument = doc;
        this.objNr = objNr;
        this.genNr = genNr;
        this.offsetOrIndex = offset;
        assert offset >= 0;
    }

    public int getObjNumber() {
        return objNr;
    }

    public int getGenNumber() {
        return genNr;
    }

    public PdfObject getRefersTo() {
        return getRefersTo(true);
    }

    /**
     * Gets direct object and try to resolve indirects chain.
     * <p>
     * Note: If chain of references has length of more than 32,
     * this method return 31st reference in chain.
     *
     * @param recursively {@code true} to resolve indirects chain
     * @return the {@link PdfObject} result of indirect reference resolving
     */
    public PdfObject getRefersTo(boolean recursively) {
        if (!recursively) {
            if (refersTo == null && !checkState(FLUSHED) && !checkState(MODIFIED) && !checkState(FREE)
                    && getReader() != null) {
                refersTo = getReader().readObject(this);
            }
            return refersTo;
        } else {
            PdfObject currentRefersTo = getRefersTo(false);
            for (int i = 0; i < LENGTH_OF_INDIRECTS_CHAIN; i++) {
                if (currentRefersTo instanceof PdfIndirectReference) {
                    currentRefersTo = ((PdfIndirectReference) currentRefersTo).getRefersTo(false);
                } else {
                    break;
                }
            }
            return currentRefersTo;
        }
    }

    protected void setRefersTo(PdfObject refersTo) {
        this.refersTo = refersTo;
    }

    public int getObjStreamNumber() {
        return objectStreamNumber;
    }

    /**
     * Gets refersTo object offset in a document.
     *
     * @return object offset in a document. If refersTo object is in object stream then -1.
     */
    public long getOffset() {
        return objectStreamNumber == 0 ? offsetOrIndex : -1;
    }

    /**
     * Gets refersTo object index in the object stream.
     *
     * @return object index in a document. If refersTo object is not in object stream then -1.
     */
    public int getIndex() {
        return objectStreamNumber == 0 ? -1 : (int) offsetOrIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PdfIndirectReference that = (PdfIndirectReference) o;
        boolean documentsEquals = pdfDocument == that.pdfDocument;
        if (!documentsEquals) {
            documentsEquals = pdfDocument != null
                    && that.pdfDocument != null
                    && pdfDocument.getDocumentId() == that.pdfDocument.getDocumentId();
        }

        return objNr == that.objNr && genNr == that.genNr && documentsEquals;
    }

    @Override
    public int hashCode() {
        int result = objNr;
        result = 31 * result + genNr;
        if (pdfDocument != null) {
            result = 31 * result + (int) pdfDocument.getDocumentId();
        }
        return result;
    }

    @Override
    public int compareTo(PdfIndirectReference o) {
        if (objNr == o.objNr) {
            if (genNr == o.genNr) {
                return comparePdfDocumentLinks(o);
            }
            return (genNr > o.genNr) ? 1 : -1;
        }
        return (objNr > o.objNr) ? 1 : -1;
    }

    @Override
    public byte getType() {
        return INDIRECT_REFERENCE;
    }

    public PdfDocument getDocument() {
        return pdfDocument;
    }

    /**
     * Marks indirect reference as free in the document. This doesn't "remove" indirect objects from the document,
     * it only ensures that corresponding xref entry is free and indirect object referred by this reference is no longer
     * linked to it. Actual object still might be written to the resultant document (and would get a new corresponding
     * indirect reference in this case) if it is still contained in some other object.
     * <p>
     * This method will not give any result if the corresponding indirect object or another object
     * that contains a reference to this object is already flushed.
     * <p>
     * Note: in some cases, removing a link of indirect object to it's indirect reference while
     * leaving the actual object in the document structure might lead to errors, because some objects are expected
     * to always have such explicit link (e.g. Catalog object, page objects, etc).
     */
    public void setFree() {
        getDocument().getXref().freeReference(this);
    }

    /**
     * Checks if this {@link PdfIndirectReference} instance corresponds to free indirect reference.
     * Indirect reference might be in a free state either because it was read as such from the opened existing
     * PDF document or because it was set free via {@link PdfIndirectReference#setFree()} method.
     *
     * @return {@code true} if this {@link PdfIndirectReference} is free, {@code false} otherwise.
     */
    public boolean isFree() {
        return checkState(FREE);
    }

    @Override
    public String toString() {
        StringBuilder states = new StringBuilder(" ");
        if (checkState(FREE)) {
            states.append("Free; ");
        }
        if (checkState(MODIFIED)) {
            states.append("Modified; ");
        }
        if (checkState(MUST_BE_FLUSHED)) {
            states.append("MustBeFlushed; ");
        }
        if (checkState(READING)) {
            states.append("Reading; ");
        }
        if (checkState(FLUSHED)) {
            states.append("Flushed; ");
        }
        if (checkState(ORIGINAL_OBJECT_STREAM)) {
            states.append("OriginalObjectStream; ");
        }
        if (checkState(FORBID_RELEASE)) {
            states.append("ForbidRelease; ");
        }
        if (checkState(READ_ONLY)) {
            states.append("ReadOnly; ");
        }
        return MessageFormatUtil.format("{0} {1} R{2}", Integer.toString(getObjNumber()),
                Integer.toString(getGenNumber()), states.substring(0, states.length() - 1));
    }

    /**
     * Gets a PdfWriter associated with the document object belongs to.
     *
     * @return PdfWriter.
     */
    protected PdfWriter getWriter() {
        if (getDocument() != null) {
            return getDocument().getWriter();
        }
        return null;
    }

    /**
     * Gets a PdfReader associated with the document object belongs to.
     *
     * @return PdfReader.
     */
    protected PdfReader getReader() {
        if (getDocument() != null) {
            return getDocument().getReader();
        }
        return null;
    }

    @Override
    protected PdfObject newInstance() {
        return PdfNull.PDF_NULL;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document, ICopyFilter copyFilter) {

    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {

    }

    /**
     * Sets special states of current object.
     *
     * @param state special flag of current object
     */
    protected PdfObject setState(short state) {
        return super.setState(state);
    }

    void setObjStreamNumber(int objectStreamNumber) {
        this.objectStreamNumber = objectStreamNumber;
    }

    void setIndex(long index) {
        this.offsetOrIndex = index;
    }

    void setOffset(long offset) {
        this.offsetOrIndex = offset;
        this.objectStreamNumber = 0;
    }

    void fixOffset(long offset) {
        if (!isFree()) {
            this.offsetOrIndex = offset;
        }
    }

    private int comparePdfDocumentLinks(PdfIndirectReference toCompare) {
        if (pdfDocument == toCompare.pdfDocument) {
            return 0;
        } else if (pdfDocument == null) {
            return -1;
        } else if (toCompare.pdfDocument == null) {
            return 1;
        } else {
            long thisDocumentId = pdfDocument.getDocumentId();
            long documentIdToCompare = toCompare.pdfDocument.getDocumentId();
            if (thisDocumentId == documentIdToCompare) {
                return 0;
            }
            return (thisDocumentId > documentIdToCompare) ? 1 : -1;
        }
    }
}
