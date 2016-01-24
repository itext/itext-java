package com.itextpdf.model.element;

import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.tagutils.IAccessibleElement;
import com.itextpdf.model.ElementPropertyContainer;
import com.itextpdf.model.Property;
import com.itextpdf.model.Style;
import com.itextpdf.model.renderer.IRenderer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractElement<Type extends AbstractElement> extends ElementPropertyContainer<Type> implements IElement<Type> {

    protected IRenderer nextRenderer;
    protected List<IElement> childElements = new ArrayList<>();
    protected Set<Style> styles;

    @Override
    public IRenderer getRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = nextRenderer.getNextRenderer();
            return renderer;
        }
        return makeNewRenderer();
    }

    @Override
    public void setNextRenderer(IRenderer renderer) {
        this.nextRenderer = renderer;
    }

    @Override
    public IRenderer createRendererSubTree() {
        IRenderer rendererRoot = getRenderer();
        for (IElement child : childElements) {
            rendererRoot.addChild(child.createRendererSubTree());
        }
        return rendererRoot;
    }

    @Override
    public boolean hasProperty(Property property) {
        boolean hasProperty = super.hasProperty(property);
        if (styles != null && styles.size() > 0 && !hasProperty) {
            for (Style style : styles) {
                if (style.hasProperty(property)) {
                    hasProperty = true;
                    break;
                }
            }
        }
        return hasProperty;
    }

    @Override
    public <T> T getProperty(Property property) {
        Object result = super.getProperty(property);
        if (styles != null && styles.size() > 0 && result == null && !super.hasProperty(property)) {
            for (Style style : styles) {
                result = style.getProperty(property);
                if (result != null || super.hasProperty(property)) {
                    break;
                }
            }
        }
        return (T) result;
    }

    /**
     * Add a new style to this element. A style can be used as an effective way
     * to define multiple equal properties to several elements.
     * @param style the style to be added
     * @return this element
     */
    public Type addStyle(Style style) {
        if (styles == null) {
            styles = new LinkedHashSet<>();
        }
        styles.add(style);
        return (Type)this;
    }

    protected abstract IRenderer makeNewRenderer();

    /**
     * Marks all child elements as artifacts recursively.
     */
    protected void propagateArtifactRoleToChildElements() {
        for (IElement child : childElements) {
            if (child instanceof AbstractElement) {
                if (child instanceof IAccessibleElement) {
                    ((IAccessibleElement) child).setRole(PdfName.Artifact);
                }
            }
        }
    }

}
