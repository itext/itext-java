package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructureAttributes;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public abstract class AccessibilityProperties implements Serializable {
    public String getRole() {
        return null;
    }

    public AccessibilityProperties setRole(String role) {
        return this;
    }

    public String getLanguage() {
        return null;
    }

    public AccessibilityProperties setLanguage(String language) {
        return this;
    }

    public String getActualText() {
        return null;
    }

    public AccessibilityProperties setActualText(String actualText) {
        return this;
    }

    public String getAlternateDescription() {
        return null;
    }

    public AccessibilityProperties setAlternateDescription(String alternateDescription) {
        return this;
    }

    public String getExpansion() {
        return null;
    }

    public AccessibilityProperties setExpansion(String expansion) {
        return this;
    }

    public String getPhoneme() {
        return null;
    }

    public AccessibilityProperties setPhoneme(String phoneme) {
        return this;
    }

    public String getPhoneticAlphabet() {
        return null;
    }

    public AccessibilityProperties setPhoneticAlphabet(String phoneticAlphabet) {
        return this;
    }

    public PdfNamespace getNamespace() {
        return null;
    }

    public AccessibilityProperties setNamespace(PdfNamespace namespace) {
        return this;
    }

    public AccessibilityProperties addRef(TagTreePointer treePointer) {
        return this;
    }

    public List<TagTreePointer> getRefsList() {
        return Collections.<TagTreePointer>emptyList();
    }

    public AccessibilityProperties clearRefs() {
        return this;
    }

    public AccessibilityProperties addAttributes(PdfStructureAttributes attributes) {
        return this;
    }

    public AccessibilityProperties addAttributes(int index, PdfStructureAttributes attributes) {
        return this;
    }

    public AccessibilityProperties clearAttributes() {
        return this;
    }

    public List<PdfStructureAttributes> getAttributesList() {
        return Collections.<PdfStructureAttributes>emptyList();
    }
}
