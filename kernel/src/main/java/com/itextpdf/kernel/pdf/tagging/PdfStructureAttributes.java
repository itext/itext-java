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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfStructureAttributes extends PdfObjectWrapper<PdfDictionary> {


    public PdfStructureAttributes(PdfDictionary attributesDict) {
        super(attributesDict);
    }

    public PdfStructureAttributes(String owner) {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.O, PdfStructTreeRoot.convertRoleToPdfName(owner));
    }

    /**
     * Method to get owner of current pdf object.
     *
     * @return Pdf owner
     */
    public String getPdfOwner() {
        PdfName pdfName = (PdfName) getPdfObject().get(PdfName.O);
        return pdfName == null ? null : pdfName.getValue();
    }

    public PdfStructureAttributes(PdfNamespace namespace) {
        super(new PdfDictionary());
        getPdfObject().put(PdfName.O, PdfName.NSO);
        getPdfObject().put(PdfName.NS, namespace.getPdfObject());
    }

    public PdfStructureAttributes addEnumAttribute(String attributeName, String attributeValue) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().put(name, new PdfName(attributeValue));
        setModified();
        return this;
    }

    public PdfStructureAttributes addTextAttribute(String attributeName, String attributeValue) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().put(name, new PdfString(attributeValue, PdfEncodings.UNICODE_BIG));
        setModified();
        return this;
    }

    public PdfStructureAttributes addIntAttribute(String attributeName, int attributeValue) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().put(name, new PdfNumber(attributeValue));
        setModified();
        return this;
    }

    public PdfStructureAttributes addFloatAttribute(String attributeName, float attributeValue) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().put(name, new PdfNumber(attributeValue));
        setModified();
        return this;
    }

    public String getAttributeAsEnum(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        PdfName attrVal = getPdfObject().getAsName(name);
        return attrVal != null ? attrVal.getValue() : null;
    }

    public String getAttributeAsText(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        PdfString attrVal = getPdfObject().getAsString(name);
        return attrVal != null ? attrVal.toUnicodeString() : null;
    }

    public Integer getAttributeAsInt(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        PdfNumber attrVal = getPdfObject().getAsNumber(name);
        return attrVal != null ? (Integer) attrVal.intValue() : (Integer) null;
    }

    public Float getAttributeAsFloat(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        PdfNumber attrVal = getPdfObject().getAsNumber(name);
        return attrVal != null ? (Float) attrVal.floatValue() : (Float) null;
    }

    public PdfStructureAttributes removeAttribute(String attributeName) {
        PdfName name = PdfStructTreeRoot.convertRoleToPdfName(attributeName);
        getPdfObject().remove(name);
        setModified();
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
