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

import java.nio.charset.StandardCharsets;

/**
 * A {@code PdfNumber}-class is the PDF-equivalent of a {@code Double}-object.
 * 
 * <p>
 * PDF provides two types of numeric objects: integer and real. Integer objects represent mathematical integers. Real
 * objects represent mathematical real numbers. The range and precision of numbers may be limited by the internal
 * representations used in the computer on which the PDF processor is running.
 * An integer shall be written as one or more decimal digits optionally preceded by a sign. The value shall be
 * interpreted as a signed decimal integer and shall be converted to an integer object.
 * A real value shall be written as one or more decimal digits with an optional sign and a leading, trailing, or
 * embedded period (decimal point).
 */
public class PdfNumber extends PdfPrimitiveObject {


    private double value;
    private boolean isDouble;

    /**
     * Creates an instance of {@link PdfNumber} and sets value.
     *
     * @param value double value to set
     */
    public PdfNumber(double value) {
        super();
        setValue(value);
    }

    /**
     * Creates an instance of {@link PdfNumber} and sets value.
     *
     * @param value int value to set
     */
    public PdfNumber(int value) {
        super();
        setValue(value);
    }

    /**
     * Creates an instance of {@link PdfNumber} with provided content.
     *
     * @param content byte array content to set
     */
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

    /**
     * Returns value of current instance of {@link PdfNumber}.
     *
     * @return value of {@link PdfNumber} instance
     */
    public double getValue() {
        if (java.lang.Double.isNaN(value))
            generateValue();
        return value;
    }

    /**
     * Returns double value of current instance of {@link PdfNumber}.
     *
     * @return double value of {@link PdfNumber} instance
     */
    public double doubleValue() {
        return getValue();
    }

    /**
     * Returns value and converts it to float.
     *
     * @return value converted to float
     */
    public float floatValue() {
        return (float) getValue();
    }

    /**
     * Returns value and converts it to long.
     *
     * @return value converted to long
     */
    public long longValue() {
        return (long) getValue();
    }

    /**
     * Returns value and converts it to an int. If value surpasses {@link Integer#MAX_VALUE}, {@link Integer#MAX_VALUE}
     * would be return.
     *
     * @return value converted to int
     */
    public int intValue() {
        if (getValue() > (double) Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) getValue();
        }
    }

    /**
     * Sets value and convert it to double.
     *
     * @param value to set
     */
    public void setValue(int value) {
        this.value = value;
        this.isDouble = false;
        this.content = null;
    }

    /**
     * Sets value.
     *
     * @param value to set
     */
    public void setValue(double value) {
        this.value = value;
        this.isDouble = true;
        this.content = null;
    }

    /**
     * Increments current value.
     */
    public void increment() {
        setValue(++value);
    }

    /**
     * Decrements current value.
     */
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
     *
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
    protected void copyContent(PdfObject from, PdfDocument document, ICopyFilter copyFilter) {
        super.copyContent(from, document, copyFilter);
        PdfNumber number = (PdfNumber) from;
        value = number.value;
        isDouble = number.isDouble;
    }
}
