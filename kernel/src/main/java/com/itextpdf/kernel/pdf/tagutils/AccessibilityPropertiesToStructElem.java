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
