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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.source.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdfNumber extends PdfPrimitiveObject {

    private static final long serialVersionUID = -250799718574024246L;

    private double value;
    private boolean isDouble;
    private boolean changed = false;

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
        this.changed = true;
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
            return new String(content);
        } else if (isDouble) {
            return new String(ByteUtils.getIsoBytes(getValue()));
        } else {
            return new String(ByteUtils.getIsoBytes(intValue()));
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o ||
                o != null && getClass() == o.getClass() && Double.compare(((PdfNumber) o).value, value) == 0;
    }

    @Override
    public int hashCode() {
        if (changed) {
            //if the instance was modified, hashCode also will be changed, it may cause inconsistency.
            Logger logger = LoggerFactory.getLogger(PdfReader.class);
            logger.warn(LogMessageConstant.CALCULATE_HASHCODE_FOR_MODIFIED_PDFNUMBER);
            changed = false;
        }
        long hash = Double.doubleToLongBits(value);
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
            value = java.lang.Double.parseDouble(new String(content));
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
