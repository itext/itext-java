package com.itextpdf.model.element;

import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.tagutils.AccessibleAttributes;

public class Div extends BlockElement<Div> {

    protected PdfName role = PdfName.Div;

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
    }

    @Override
    public AccessibleAttributes getAccessibleAttributes() {
        return null;
    }
}
