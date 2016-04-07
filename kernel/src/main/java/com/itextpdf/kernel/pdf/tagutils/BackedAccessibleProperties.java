/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BackedAccessibleProperties extends AccessibilityProperties {

    private static final long serialVersionUID = 4080083623525383278L;

    private PdfStructElem backingElem;

    BackedAccessibleProperties(PdfStructElem backingElem) {
        this.backingElem = backingElem;
    }

    @Override
    public String getLanguage() {
        return backingElem.getLang().getValue();
    }

    @Override
    public AccessibilityProperties setLanguage(String language) {
        backingElem.setLang(new PdfString(language));
        return this;
    }

    @Override
    public String getActualText() {
        return backingElem.getActualText().getValue();
    }

    @Override
    public AccessibilityProperties setActualText(String actualText) {
        backingElem.setActualText(new PdfString(actualText));
        return this;
    }

    @Override
    public String getAlternateDescription() {
        return backingElem.getAlt().getValue();
    }

    @Override
    public AccessibilityProperties setAlternateDescription(String alternateDescription) {
        backingElem.setAlt(new PdfString(alternateDescription));
        return this;
    }

    @Override
    public String getExpansion() {
        return backingElem.getE().getValue();
    }

    @Override
    public AccessibilityProperties setExpansion(String expansion) {
        backingElem.setE(new PdfString(expansion));
        return this;
    }

    @Override
    public AccessibilityProperties addAttributes(PdfDictionary attributes) {
        PdfObject attributesObject = backingElem.getAttributes(false);

        PdfObject combinedAttributes = combineAttributesList(attributesObject, Collections.singletonList(attributes),
                backingElem.getPdfObject().getAsNumber(PdfName.R));
        backingElem.setAttributes(combinedAttributes);
        return this;
    }

    @Override
    public AccessibilityProperties clearAttributes() {
        backingElem.remove(PdfName.A);
        return this;
    }

    @Override
    public List<PdfDictionary> getAttributesList() {
        ArrayList<PdfDictionary> attributesList = new ArrayList<>();
        PdfObject elemAttributesObj = backingElem.getAttributes(false);
        if (elemAttributesObj != null) {
            if (elemAttributesObj.isDictionary()) {
                attributesList.add((PdfDictionary) elemAttributesObj);
            } else if (elemAttributesObj.isArray()) {
                PdfArray attributesArray = (PdfArray) elemAttributesObj;
                for (PdfObject attributeObj : attributesArray) {
                    if (attributeObj.isDictionary()) {
                        attributesList.add((PdfDictionary) attributeObj);
                    }
                }
            }
        }
        return attributesList;
    }

    @Override
    void setToStructElem(PdfStructElem elem) {
        // ignore, because all attributes are directly set to the structElem
    }
}
