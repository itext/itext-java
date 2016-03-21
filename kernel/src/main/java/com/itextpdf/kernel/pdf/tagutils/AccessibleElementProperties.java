package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import java.util.ArrayList;
import java.util.List;

public class AccessibleElementProperties {
    protected String language;
    protected String actualText;
    protected String alternateDescription;
    protected String expansion;
    protected List<PdfDictionary> attributesList = new ArrayList<>();

    public String getLanguage() {
        return language;
    }

    public AccessibleElementProperties setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getActualText() {
        return actualText;
    }

    public AccessibleElementProperties setActualText(String actualText) {
        this.actualText = actualText;
        return this;
    }

    public String getAlternateDescription() {
        return alternateDescription;
    }

    public AccessibleElementProperties setAlternateDescription(String alternateDescription) {
        this.alternateDescription = alternateDescription;
        return this;
    }

    public String getExpansion() {
        return expansion;
    }

    public AccessibleElementProperties setExpansion(String expansion) {
        this.expansion = expansion;
        return this;
    }

    public AccessibleElementProperties addAttributes(PdfDictionary attributes) {
        attributesList.add(attributes);

        return this;
    }

    public AccessibleElementProperties clearAttributes() {
        attributesList.clear();

        return this;
    }

    public List<PdfDictionary> getAttributesList() {
        return attributesList;
    }

    void setToStructElem(PdfStructElem elem) {
        if (getActualText() != null) {
            elem.setActualText(new PdfString(getActualText()));
        }
        if (getAlternateDescription() != null) {
            elem.setAlt(new PdfString(getAlternateDescription()));
        }
        if (getExpansion() != null) {
            elem.setE(new PdfString(getExpansion()));
        }
        if (getLanguage() != null) {
            elem.setLang(new PdfString(getLanguage()));
        }

        List<PdfDictionary> newAttributesList = getAttributesList();
        if (!newAttributesList.isEmpty()) {
            PdfObject attributesObject = elem.getAttributes(false);

            PdfObject combinedAttributes = combineAttributesList(attributesObject, newAttributesList, elem.getPdfObject().getAsNumber(PdfName.R));
            elem.setAttributes(combinedAttributes);
        }
    }

    private PdfObject combineAttributesList(PdfObject attributesObject, List<PdfDictionary> newAttributesList, PdfNumber revision) {
        PdfObject combinedAttributes;

        if (attributesObject instanceof PdfDictionary) {
            PdfArray combinedAttributesArray = new PdfArray();
            combinedAttributesArray.add(attributesObject);
            addNewAttributesToAttributesArray(newAttributesList, revision, combinedAttributesArray);
            combinedAttributes = combinedAttributesArray;
        } else if (attributesObject instanceof PdfArray) {
            PdfArray combinedAttributesArray = (PdfArray) attributesObject;
            addNewAttributesToAttributesArray(newAttributesList, revision, combinedAttributesArray);
            combinedAttributes = combinedAttributesArray;
        } else {
            if (newAttributesList.size() == 1) {
                combinedAttributes = newAttributesList.get(0);
            } else {
                combinedAttributes = new PdfArray();
                addNewAttributesToAttributesArray(newAttributesList, revision, (PdfArray) combinedAttributes);
            }
        }

        return combinedAttributes;
    }

    private void addNewAttributesToAttributesArray(List<PdfDictionary> newAttributesList, PdfNumber revision, PdfArray attributesArray) {
        if (revision != null) {
            for (PdfDictionary attributes : newAttributesList) {
                attributesArray.add(attributes);
                attributesArray.add(revision);
            }
        } else {
            attributesArray.addAll(newAttributesList);
        }
    }
}
