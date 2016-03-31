package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfName;

class DummyAccessibleElement implements IAccessibleElement {
    private PdfName role;
    private AccessibilityProperties properties;

    DummyAccessibleElement(PdfName role, AccessibilityProperties properties) {
        this.role = role;
        this.properties = properties;
    }

    @Override
    public PdfName getRole() {
        return role;
    }

    @Override
    public void setRole(PdfName role) {
        this.role = role;
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        return properties;
    }
}
