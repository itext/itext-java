package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.AccessibleElementProperties;

/**
 * A {@link Div} is a container object that defines a section in a document,
 * which will have some shared layout properties. Like all {@link BlockElement}
 * types, it will try to take up as much horizontal space as possible.
 * 
 * The concept is very similar to that of the div tag in HTML.
 */
public class Div extends BlockElement<Div> {

    protected PdfName role = PdfName.Div;
    protected AccessibleElementProperties tagProperties;

    /**
     * Adds any block element to the div's contents.
     * 
     * @param <T> the type of this object
     * @param element a {@link BlockElement}
     * @return this Element
     */
    public <T extends Div> T add(BlockElement element) {
        childElements.add(element);
        return (T) this;
    }
    
    /**
     * Adds an image to the div's contents.
     * 
     * @param <T> the type of this object
     * @param element an {@link Image}
     * @return this Element
     */
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
