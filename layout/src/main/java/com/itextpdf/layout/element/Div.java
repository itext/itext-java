package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.AccessibleElementProperties;

public class Div extends BlockElement<Div> {

    protected PdfName role = PdfName.Div;
    protected AccessibleElementProperties tagProperties;

    public <T extends Div> T add(BlockElement element) {
        childElements.add(element);
        return (T) this;
    }

    public <T extends Div> T add(Image element) {
        childElements.add(element);
        return (T) this;
    }

    @Override
    public PdfName getRole() {
        return role;
    }

    @Override
    public void setRole(PdfName role) {
        this.role = role;
        if (PdfName.Artifact.equals(role)) {
            propagateArtifactRoleToChildElements();
        }
    }

    @Override
    public AccessibleElementProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibleElementProperties();
        }
        return tagProperties;
    }
}
