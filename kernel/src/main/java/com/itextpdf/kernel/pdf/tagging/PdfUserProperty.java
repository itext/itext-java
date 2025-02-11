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
