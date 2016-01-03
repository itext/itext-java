package com.itextpdf.core.pdf.tagutils;

//TODO add attributes field and rename class
public class AccessibleAttributes {
    protected String language;
    protected String actualText;
    protected String alternateDescription;
    protected String expansion;

    public String getLanguage() {
        return language;
    }

    public AccessibleAttributes setLanguage(String language) {
        this.language = language;
        return this;
    }

    public String getActualText() {
        return actualText;
    }

    public AccessibleAttributes setActualText(String actualText) {
        this.actualText = actualText;
        return this;
    }

    public String getAlternateDescription() {
        return alternateDescription;
    }

    public AccessibleAttributes setAlternateDescription(String alternateDescription) {
        this.alternateDescription = alternateDescription;
        return this;
    }

    public String getExpansion() {
        return expansion;
    }

    public AccessibleAttributes setExpansion(String expansion) {
        this.expansion = expansion;
        return this;
    }

}
