/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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

/**
 * Representation of the null object in the PDF specification.
 */
public class PdfNull extends PdfPrimitiveObject {

    private static final long serialVersionUID = 7789114018630038033L;

	public static final PdfNull PDF_NULL = new PdfNull(true);
    private static final byte[] NullContent = ByteUtils.getIsoBytes("null");

    /**
     * Creates a PdfNull instance.
     */
    public PdfNull() {
        super();
    }

    private PdfNull(boolean directOnly) {
        super(directOnly);
    }

    @Override
    public byte getType() {
        return NULL;
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    protected void generateContent() {
        content = NullContent;
    }

    //Here we create new object, because if we use static object it can cause unpredictable behavior during copy objects
    @Override
    protected PdfObject newInstance() {
        return new PdfNull();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {

    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj != null && getClass() == obj.getClass();
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
