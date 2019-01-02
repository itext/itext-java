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
package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import java.util.List;

final class AccessibilityPropertiesToStructElem {

    static void apply(AccessibilityProperties properties, PdfStructElem elem) {
        if (properties.getActualText() != null) {
            elem.setActualText(new PdfString(properties.getActualText(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getAlternateDescription() != null) {
            elem.setAlt(new PdfString(properties.getAlternateDescription(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getExpansion() != null) {
            elem.setE(new PdfString(properties.getExpansion(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getLanguage() != null) {
            elem.setLang(new PdfString(properties.getLanguage(), PdfEncodings.UNICODE_BIG));
        }

        List<PdfStructureAttributes> newAttributesList = properties.getAttributesList();
        if (newAttributesList.size() > 0) {
            PdfObject attributesObject = elem.getAttributes(false);

            PdfObject combinedAttributes = combineAttributesList(attributesObject, -1, newAttributesList, elem.getPdfObject().getAsNumber(PdfName.R));
            elem.setAttributes(combinedAttributes);
        }

        if (properties.getPhoneme() != null) {
            elem.setPhoneme(new PdfString(properties.getPhoneme(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getPhoneticAlphabet() != null) {
            elem.setPhoneticAlphabet(new PdfName(properties.getPhoneticAlphabet()));
        }
        if (properties.getNamespace() != null) {
            elem.setNamespace(properties.getNamespace());
        }
        for (TagTreePointer ref : properties.getRefsList()) {
            elem.addRef(ref.getCurrentStructElem());
        }
    }

    static PdfObject combineAttributesList(PdfObject attributesObject, int insertIndex, List<PdfStructureAttributes> newAttributesList, PdfNumber revision) {
        PdfObject combinedAttributes;

        if (attributesObject instanceof PdfDictionary) {
            PdfArray combinedAttributesArray = new PdfArray();
            combinedAttributesArray.add(attributesObject);
            addNewAttributesToAttributesArray(insertIndex, newAttributesList, revision, combinedAttributesArray);
            combinedAttributes = combinedAttributesArray;
        } else if (attributesObject instanceof PdfArray) {
            PdfArray combinedAttributesArray = (PdfArray) attributesObject;
            addNewAttributesToAttributesArray(insertIndex, newAttributesList, revision, combinedAttributesArray);
            combinedAttributes = combinedAttributesArray;
        } else {
            if (newAttributesList.size() == 1) {
                if (insertIndex > 0) {
                    throw new IndexOutOfBoundsException();
                }
                combinedAttributes = newAttributesList.get(0).getPdfObject();
            } else {
                combinedAttributes = new PdfArray();
                addNewAttributesToAttributesArray(insertIndex, newAttributesList, revision, (PdfArray) combinedAttributes);
            }
        }

        return combinedAttributes;
    }

    private static void addNewAttributesToAttributesArray(int insertIndex, List<PdfStructureAttributes> newAttributesList, PdfNumber revision, PdfArray attributesArray) {
        if (insertIndex < 0) {
            insertIndex = attributesArray.size();
        }
        if (revision != null) {
            for (PdfStructureAttributes attributes : newAttributesList) {
                attributesArray.add(insertIndex++, attributes.getPdfObject());
                attributesArray.add(insertIndex++, revision);
            }
        } else {
            for (PdfStructureAttributes newAttribute : newAttributesList) {
                attributesArray.add(insertIndex++, newAttribute.getPdfObject());
            }
        }
    }
}
