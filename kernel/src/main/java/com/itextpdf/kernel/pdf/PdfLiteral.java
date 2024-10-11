/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.utils.ICopyFilter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PdfLiteral extends PdfPrimitiveObject {
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
            return new String(content, StandardCharsets.ISO_8859_1);
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
    protected void copyContent(PdfObject from, PdfDocument document, ICopyFilter copyFilter) {
        super.copyContent(from, document, copyFilter);
        PdfLiteral literal = (PdfLiteral) from;
        this.content = literal.getInternalContent();
    }
}
