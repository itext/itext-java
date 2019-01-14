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

import com.itextpdf.io.font.PdfEncodings;

import java.util.Arrays;

public class PdfLiteral extends PdfPrimitiveObject {

    private static final long serialVersionUID = -770215611509192403L;
	
    private long position;

    public PdfLiteral(byte[] content) {
        super(true);
        this.content = content;
    }

    public PdfLiteral(int size) {
        this(new byte[size]);
        Arrays.fill(content, (byte) 32);
    }

    public PdfLiteral(String content) {
        this(PdfEncodings.convertToBytes(content, null));
    }

    private PdfLiteral() {
        this((byte[]) null);
    }

    @Override
    public byte getType() {
        return LITERAL;
    }

    @Override
    public String toString() {
        if (content != null) {
            return new String(content);
        } else {
            return "";
        }
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public int getBytesCount() {
        return content.length;
    }

    @Override
    protected void generateContent() {

    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                o != null && getClass() == o.getClass() && Arrays.equals(content, ((PdfLiteral) o).content);
    }

    @Override
    public int hashCode() {
        return content == null ? 0 : Arrays.hashCode(content);
    }

    @Override
    protected PdfObject newInstance() {
        return new PdfLiteral();
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfLiteral literal = (PdfLiteral) from;
        this.content = literal.getInternalContent();
    }
}
