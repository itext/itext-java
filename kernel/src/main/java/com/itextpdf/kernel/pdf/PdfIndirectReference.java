/*
    $Id$

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

import java.text.MessageFormat;

public class PdfIndirectReference extends PdfObject implements Comparable<PdfIndirectReference> {

    private static final long serialVersionUID = -8293603068792908601L;

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
     * </p>
     */
    public PdfObject getRefersTo(boolean recursively) {
        if (!recursively) {
            if (refersTo == null && !checkState(FLUSHED) && !checkState(MODIFIED) && getReader() != null) {
                refersTo = getReader().readObject(this);
            }
            return refersTo;
        } else {
            PdfObject currentRefersTo = getRefersTo(false);
            for (int i = 0; i < LENGTH_OF_INDIRECTS_CHAIN; i++) {
                if (currentRefersTo instanceof PdfIndirectReference)
                    currentRefersTo = ((PdfIndirectReference) currentRefersTo).getRefersTo(false);
                else
                    break;
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
     * @return object offset in a document. If refersTo object is in object stream then 0.
     */
    public long getOffset() {
        return objectStreamNumber == 0 ? offsetOrIndex : 0;
    }

    /**
     * Gets refersTo object index in the object stream.
     *
     * @return object index in a document. If refersTo object is not in object stream then 0.
     */
    public int getIndex() {
        return objectStreamNumber == 0 ? 0 : (int)offsetOrIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdfIndirectReference that = (PdfIndirectReference) o;

        return objNr == that.objNr && genNr == that.genNr;
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
    public byte getType() {
        return INDIRECT_REFERENCE;
    }

    public PdfDocument getDocument() {
        return pdfDocument;
    }

    /**
     * Gets a PdfWriter associated with the document object belongs to.
     *
     * @return PdfWriter.
     */
    protected PdfWriter getWriter() {
        if (getDocument() != null)
            return getDocument().getWriter();
        return null;
    }

    /**
     * Gets a PdfReader associated with the document object belongs to.
     *
     * @return PdfReader.
     */
    protected PdfReader getReader() {
        if (getDocument() != null)
            return getDocument().getReader();
        return null;
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
        return MessageFormat.format("{0} {1} R{2}", getObjNumber(), getGenNumber(), states.substring(0, states.length() - 1));
    }

    protected void setObjStreamNumber(int objectStreamNumber) {
        this.objectStreamNumber = objectStreamNumber;
    }

    protected void setIndex(long index) {
        this.offsetOrIndex = index;
    }

    protected void setOffset(long offset) {
        this.offsetOrIndex = offset;
        this.objectStreamNumber = 0;
    }

    protected void fixOffset(long offset){
        //TODO log invalid offsets
        if (!isFree()) {
            this.offsetOrIndex = offset;
        }
    }

    // NOTE In append mode object could be OriginalObjectStream, but not Modified,
    // so information about this reference would not be added to the new Cross-Reference table.
    // In stamp mode without append the reference will be free.
    protected boolean isFree() {
        return checkState(FREE) || checkState(ORIGINAL_OBJECT_STREAM);
    }

    /**
    * Releases indirect reference from the document. Remove link to the referenced indirect object.
    * <p>
    * Note: Be careful when using this method. Do not use this method for wrapper objects,
    * it can be cause of errors.
    * Free indirect reference could be reused for a new indirect object.
    * </p>
    */
    public void setFree() {
        getDocument().getXref().freeReference(this);
    }

    @Override
    protected PdfObject newInstance() {
        return PdfNull.PDF_NULL;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {

    }
}
