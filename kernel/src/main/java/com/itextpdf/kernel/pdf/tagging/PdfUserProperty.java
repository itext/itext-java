/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfUserProperty extends PdfObjectWrapper<PdfDictionary> {
    private static final long serialVersionUID = -347021704725128837L;

    public enum ValueType {
        UNKNOWN,
        TEXT,
        NUMBER,
        BOOLEAN,
    }

    public PdfUserProperty(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfUserProperty(String name, String value) {
        super(new PdfDictionary());
        setName(name);
        setValue(value);
    }

    public PdfUserProperty(String name, int value) {
        super(new PdfDictionary());
        setName(name);
        setValue(value);
    }

    public PdfUserProperty(String name, float value) {
        super(new PdfDictionary());
        setName(name);
        setValue(value);
    }

    public PdfUserProperty(String name, boolean value) {
        super(new PdfDictionary());
        setName(name);
        setValue(value);
    }

    public String getName() {
        return getPdfObject().getAsString(PdfName.N).toUnicodeString();
    }

    public PdfUserProperty setName(String name) {
        getPdfObject().put(PdfName.N, new PdfString(name, PdfEncodings.UNICODE_BIG));
        return this;
    }

    public ValueType getValueType() {
        PdfObject valObj = getPdfObject().get(PdfName.V);
        if (valObj == null) {
            return ValueType.UNKNOWN;
        }
        switch (valObj.getType()) {
            case PdfObject.BOOLEAN:
                return ValueType.BOOLEAN;
            case PdfObject.NUMBER:
                return ValueType.NUMBER;
            case PdfObject.STRING:
                return ValueType.TEXT;
            default:
                return ValueType.UNKNOWN;
        }
    }

    public PdfUserProperty setValue(String value) {
        getPdfObject().put(PdfName.V, new PdfString(value, PdfEncodings.UNICODE_BIG));
        return this;
    }

    public PdfUserProperty setValue(int value) {
        getPdfObject().put(PdfName.V, new PdfNumber(value));
        return this;
    }

    public PdfUserProperty setValue(float value) {
        getPdfObject().put(PdfName.V, new PdfNumber(value));
        return this;
    }

    public PdfUserProperty setValue(boolean value) {
        getPdfObject().put(PdfName.V, new PdfBoolean(value));
        return this;
    }

    public String getValueAsText() {
        PdfString str = getPdfObject().getAsString(PdfName.V);
        return str != null ? str.toUnicodeString() : null;
    }

    public Float getValueAsFloat() {
        PdfNumber num = getPdfObject().getAsNumber(PdfName.V);
        return num != null ? (Float)num.floatValue() : (Float)null;
    }

    public Boolean getValueAsBool() {
        return getPdfObject().getAsBool(PdfName.V);
    }


    public String getValueFormattedRepresentation() {
        PdfString f = getPdfObject().getAsString(PdfName.F);
        return f != null ? f.toUnicodeString() : null;
    }

    public PdfUserProperty setValueFormattedRepresentation(String formattedRepresentation) {
        getPdfObject().put(PdfName.F, new PdfString(formattedRepresentation, PdfEncodings.UNICODE_BIG));
        return this;
    }

    public Boolean isHidden() {
        return getPdfObject().getAsBool(PdfName.H);
    }

    public PdfUserProperty setHidden(boolean isHidden) {
        getPdfObject().put(PdfName.H, new PdfBoolean(isHidden));
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
