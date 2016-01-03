package com.itextpdf.core.pdf.tagutils;

import com.itextpdf.core.pdf.PdfName;

public interface IAccessibleElement {
    PdfName getRole();
    void setRole(PdfName role);
    AccessibleAttributes getAccessibleAttributes();
}
