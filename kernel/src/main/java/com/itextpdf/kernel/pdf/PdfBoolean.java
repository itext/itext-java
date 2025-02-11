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

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.utils.ICopyFilter;

public class PdfBoolean extends PdfPrimitiveObject {


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
    protected void copyContent(PdfObject from, PdfDocument document, ICopyFilter copyFilter) {
        super.copyContent(from, document, copyFilter);
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
