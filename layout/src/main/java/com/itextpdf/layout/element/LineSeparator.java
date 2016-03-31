package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.canvas.draw.LineDrawer;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.renderer.BlockRenderer;
import com.itextpdf.layout.renderer.LineSeparatorRenderer;

/**
 * This is a line separator element which is basically just a horizontal line with
 * a style specified by {@link LineDrawer} custom drawing interface instance.
 * This might be thought of as an HTML's <hr> element alternative.
 */
public class LineSeparator extends BlockElement<LineSeparator> {

    protected PdfName role = PdfName.Artifact;
    protected AccessibilityProperties tagProperties;

    /**
     * Creates a custom line separator with line style defined by custom {@link LineDrawer} interface instance
     * @param lineDrawer line drawer instance
     */
    public LineSeparator(LineDrawer lineDrawer) {
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
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibilityProperties();
        }
        return tagProperties;
    }

}
