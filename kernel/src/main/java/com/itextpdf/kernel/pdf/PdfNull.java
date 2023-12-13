/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
