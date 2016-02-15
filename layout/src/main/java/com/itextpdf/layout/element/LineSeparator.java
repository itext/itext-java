package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.draw.Drawable;
import com.itextpdf.kernel.pdf.tagutils.AccessibleElementProperties;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.LineSeparatorRenderer;

public class LineSeparator extends BlockElement<LineSeparator> {

    protected PdfName role = PdfName.Artifact;
    protected AccessibleElementProperties tagProperties;

    public LineSeparator(Drawable lineDrawer) {
        setProperty(Property.LINE_DRAWER, lineDrawer);
    }

    @Override
    protected BlockRenderer makeNewRenderer() {
        return new LineSeparatorRenderer(this);
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
    public AccessibleElementProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibleElementProperties();
        }
        return tagProperties;
    }
}
