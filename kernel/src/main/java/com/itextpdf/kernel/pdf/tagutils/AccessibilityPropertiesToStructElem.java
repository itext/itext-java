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
        setTextualAids(properties, elem);
        setAttributes(properties.getAttributesList(), elem);

        if (properties.getNamespace() != null) {
            elem.setNamespace(properties.getNamespace());
        }
        if(properties.getStructureElementId() != null) {
            elem.setStructureElementId(new PdfString(properties.getStructureElementId()));
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

    private static void setAttributes(
            List<PdfStructureAttributes> newAttributesList, PdfStructElem elem) {
        if (newAttributesList.size() > 0) {
            PdfObject attributesObject = elem.getAttributes(false);

            PdfObject combinedAttributes = combineAttributesList(
                    attributesObject,
                    -1,
                    newAttributesList,
                    elem.getPdfObject().getAsNumber(PdfName.R));
            elem.setAttributes(combinedAttributes);
        }
    }

    private static void setTextualAids(AccessibilityProperties properties, PdfStructElem elem) {
        if (properties.getLanguage() != null) {
            elem.setLang(new PdfString(properties.getLanguage(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getActualText() != null) {
            elem.setActualText(new PdfString(properties.getActualText(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getAlternateDescription() != null) {
            elem.setAlt(new PdfString(properties.getAlternateDescription(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getExpansion() != null) {
            elem.setE(new PdfString(properties.getExpansion(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getPhoneme() != null) {
            elem.setPhoneme(new PdfString(properties.getPhoneme(), PdfEncodings.UNICODE_BIG));
        }
        if (properties.getPhoneticAlphabet() != null) {
            elem.setPhoneticAlphabet(new PdfName(properties.getPhoneticAlphabet()));
        }
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
