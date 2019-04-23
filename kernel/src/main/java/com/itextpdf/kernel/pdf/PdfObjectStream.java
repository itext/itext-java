/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.source.ByteArrayOutputStream;


class PdfObjectStream extends PdfStream {

    private static final long serialVersionUID = -3513488307665597642L;

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
            throw new PdfException(PdfException.PdfObjectStreamReachMaxSize);
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
