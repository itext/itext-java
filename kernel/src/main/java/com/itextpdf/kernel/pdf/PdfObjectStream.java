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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;


class PdfObjectStream extends PdfStream {


    /**
     * Max number of objects in object stream.
     */
    public static final int MAX_OBJ_STREAM_SIZE = 200;

    /**
     * Current object stream size (number of objects inside).
     */
    protected PdfNumber size = new PdfNumber(0);

    /**
     * Stream containing object indices, a heading part of object stream.
     */
    protected PdfOutputStream indexStream;

    public PdfObjectStream(PdfDocument doc) {
        this(doc, new ByteArrayOutputStream());
        indexStream = new PdfOutputStream(new ByteArrayOutputStream());
    }

    /**
     * This constructor is for reusing ByteArrayOutputStreams of indexStream and outputStream.
     * NOTE Only for internal use in PdfWriter!
     * @param prev previous PdfObjectStream.
     */
    PdfObjectStream(PdfObjectStream prev) {
        this(prev.getIndirectReference().getDocument(), prev.getOutputStream().getOutputStream());
        indexStream = new PdfOutputStream(prev.indexStream.getOutputStream());
        ((ByteArrayOutputStream)outputStream.getOutputStream()).reset();
        ((ByteArrayOutputStream)indexStream.getOutputStream()).reset();

        prev.releaseContent(true);
    }

    private PdfObjectStream(PdfDocument doc, java.io.OutputStream outputStream) {
        super(outputStream);
        //avoid reuse existed references, create new, opposite to get next reference
        makeIndirect(doc, doc.getXref().createNewIndirectReference(doc));
        getOutputStream().document = doc;
        put(PdfName.Type, PdfName.ObjStm);
        put(PdfName.N, size);
        put(PdfName.First, new PdfNumber(0));
    }

    /**
     * Adds object to the object stream.
     *
     * @param object object to add.
     */
    public void addObject(PdfObject object) {
        if (size.intValue() == MAX_OBJ_STREAM_SIZE) {
            throw new PdfException(KernelExceptionMessageConstant.PDF_OBJECT_STREAM_REACH_MAX_SIZE);
        }
        PdfOutputStream outputStream = getOutputStream();
        indexStream.writeInteger(object.getIndirectReference().getObjNumber()).
                writeSpace().
                writeLong(outputStream.getCurrentPos()).
                writeSpace();
        outputStream.write(object);
        object.getIndirectReference().setObjStreamNumber(getIndirectReference().getObjNumber());
        object.getIndirectReference().setIndex(size.intValue());
        outputStream.writeSpace();
        size.increment();
        getAsNumber(PdfName.First).setValue(indexStream.getCurrentPos());
    }

    /**
     * Gets object stream size (number of objects inside).
     *
     * @return object stream size.
     */
    public int getSize() {
        return size.intValue();
    }

    public PdfOutputStream getIndexStream() {
        return indexStream;
    }

    @Override
    protected void releaseContent() {
        releaseContent(false);
    }

    private void releaseContent(boolean close) {
        if (close) {
            outputStream = null;
            indexStream = null;
            super.releaseContent();
        }
    }
}
