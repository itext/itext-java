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
