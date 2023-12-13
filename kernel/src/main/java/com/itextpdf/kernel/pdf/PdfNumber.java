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

import java.nio.charset.StandardCharsets;

public class PdfNumber extends PdfPrimitiveObject {

    private static final long serialVersionUID = -250799718574024246L;

    private double value;
    private boolean isDouble;

    public PdfNumber(double value) {
        super();
        setValue(value);
    }

    public PdfNumber(int value) {
        super();
        setValue(value);
    }

    public PdfNumber(byte[] content) {
        super(content);
        this.isDouble = true;
        this.value = java.lang.Double.NaN;
    }

    private PdfNumber() {
        super();
    }

    @Override
    public byte getType() {
        return NUMBER;
    }

    public double getValue() {
        if (java.lang.Double.isNaN(value))
            generateValue();
        return value;
    }

    public double doubleValue() {
        return getValue();
    }

    public float floatValue() {
        return (float) getValue();
    }

    public long longValue() {
        return (long) getValue();
    }

    public int intValue() {
        return (int) getValue();
    }

    public void setValue(int value) {
        this.value = value;
        this.isDouble = false;
        this.content = null;
    }

    public void setValue(double value) {
        this.value = value;
        this.isDouble = true;
        this.content = null;
    }

    public void increment() {
        setValue(++value);
    }

    public void decrement() {
        setValue(--value);
    }

    @Override
    public String toString() {
        if (content != null) {
            return new String(content, StandardCharsets.ISO_8859_1);
        } else if (isDouble) {
            return new String(ByteUtils.getIsoBytes(getValue()), StandardCharsets.ISO_8859_1);
        } else {
            return new String(ByteUtils.getIsoBytes(intValue()), StandardCharsets.ISO_8859_1);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Double.compare(((PdfNumber) o).getValue(), getValue()) == 0;
    }

    /**
     * Checks if string representation of the value contains decimal point.
     * @return true if contains so the number must be real not integer
     */
    public boolean hasDecimalPoint() {
        return this.toString().contains(".");
    }

    @Override
    public int hashCode() {
        long hash = Double.doubleToLongBits(getValue());
        return (int) (hash ^ (hash >>> 32));
    }

    @Override
    protected PdfObject newInstance() {
        return new PdfNumber();
    }

    protected boolean isDoubleNumber() {
        return isDouble;
    }

    @Override
    protected void generateContent() {
        if (isDouble) {
            content = ByteUtils.getIsoBytes(value);
        } else {
            content = ByteUtils.getIsoBytes((int) value);
        }
    }

    protected void generateValue() {
        try {
            value = java.lang.Double.parseDouble(new String(content, StandardCharsets.ISO_8859_1));
        } catch (NumberFormatException e) {
            value = java.lang.Double.NaN;
        }
        isDouble = true;
    }

    @Override
    protected void copyContent(PdfObject from, PdfDocument document) {
        super.copyContent(from, document);
        PdfNumber number = (PdfNumber) from;
        value = number.value;
        isDouble = number.isDouble;
    }
}
