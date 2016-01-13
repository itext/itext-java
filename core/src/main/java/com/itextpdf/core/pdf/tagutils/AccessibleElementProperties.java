package com.itextpdf.core.pdf.tagutils;

import com.itextpdf.core.pdf.PdfDictionary;
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
}
