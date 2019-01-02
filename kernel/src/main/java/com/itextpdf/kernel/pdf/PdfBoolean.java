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

import com.itextpdf.io.source.ByteUtils;

public class PdfBoolean extends PdfPrimitiveObject {

    private static final long serialVersionUID = -1363839858135046832L;

    public static final PdfBoolean TRUE = new PdfBoolean(true, true);
    public static final PdfBoolean FALSE = new PdfBoolean(false, true);

    private static final byte[] True = ByteUtils.getIsoBytes("true");
    private static final byte[] False = ByteUtils.getIsoBytes("false");

    private boolean value;

    /**
     * Store a boolean value
     *
     * @param value value to store
     */
    public PdfBoolean(boolean value) {
        this(value, false);
    }

    private PdfBoolean(boolean value, boolean directOnly) {
        super(directOnly);
        this.value = value;
    }

    private PdfBoolean() {
        super();
    }

    public boolean getValue() {
        return value;
    }

    public byte getType() {
        return BOOLEAN;
    }

    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    @Override
    protected void generateContent() {
        content = value ? True : False;
    }

    @Override
    protected PdfObject newInstance() {
        return new PdfBoolean();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfBoolean bool = (PdfBoolean)from;
        value = bool.value;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj ||
                obj != null && getClass() == obj.getClass() && value == ((PdfBoolean) obj).value;
    }

    @Override
    public int hashCode() {
        return (value ? 1 : 0);
    }

    /**
     * Gets PdfBoolean existing static class variable equivalent for given boolean value.
     *
     * Note, returned object will be direct only, which means it is impossible to make in indirect.
     * If required PdfBoolean has to be indirect,
     * use {@link #PdfBoolean(boolean)} constructor instead.
     * @param value boolean variable defining value of PdfBoolean to return.
     * @return existing static PdfBoolean class variable.
     */
    public static PdfBoolean valueOf(boolean value) { return value ? TRUE : FALSE; }
}
