package com.itextpdf.kernel.pdf.tagutils;

import com.itextpdf.kernel.pdf.PdfName;

public interface IAccessibleElement {
    PdfName getRole();
    void setRole(PdfName role);
    AccessibleElementProperties getAccessibilityProperties();
}
