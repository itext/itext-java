/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.layout.ElementPropertyContainer;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Defines the most common properties that most {@link IElement} implementations
 * share.
 *
 * @param <T> the type of the implementation
 */
public abstract class AbstractElement<T extends IElement>
        extends ElementPropertyContainer<T> implements IAbstractElement {

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
    public boolean hasProperty(int property) {
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
    public <T1> T1 getProperty(int property) {
        Object result = super.<T1>getProperty(property);
        if (styles != null && styles.size() > 0 && result == null && !super.hasProperty(property)) {
            for (Style style : styles) {
                T1 foundInStyle = style.<T1>getProperty(property);
                if (foundInStyle != null || style.hasProperty(property)) {
                    result = foundInStyle;
                }
            }
        }
        return (T1) result;
    }

    /**
     * Add a new style to this element. A style can be used as an effective way
     * to define multiple equal properties to several elements, however its properties have
     * lower priority than properties, directly set on {@link ElementPropertyContainer}
     *
     * Note that if several Style objects are added, iText checks them one by one
     * in the order in which they were added and returns the property's value from
     * the last Style object, which contains this property. So, if there are two Style
     * objects added: the first has set width of 100 points and the second of 200 points,
     * iText will get 200 points as width value.
     *
     * @param style the style to be added
     * @return this element
     */
    public T addStyle(Style style) {
        if (style == null) {
            throw new IllegalArgumentException("Style can not be null.");
        }
        if (styles == null) {
            styles = new LinkedHashSet<>();
        }
        styles.add(style);
        return (T) (Object) this;
    }

    /**
     * Gets the child elements of this elements
     *
     * @return a list of children
     */
    @Override
    public List<IElement> getChildren() {
        return childElements;
    }

    /**
     * Returns <code>true</code> if this list contains no elements.
     *
     * @return <code>true</code> if this list contains no elements
     */
    public boolean isEmpty() {
        return 0 == childElements.size();
    }

    /**
     * Sets an action on this Element. An action is a general PDF concept that
     * signifies anything that makes the document interactive, e.g. a hyperlink
     * or a button.
     *
     * @param action the {@link PdfAction} that should be performed
     * @return this Element
     */
    public T setAction(PdfAction action) {
        setProperty(Property.ACTION, action);
        return (T) (Object) this;
    }

    /**
     * Explicitly sets the page number this element should be put on. The location
     * on the page will be the same as if it were added at the end of the document,
     * but it will be located on the specified page.
     * <p>
     * This method should be used very carefully in client code.
     *
     * @param pageNumber the page number of the page this element should be placed on
     * @return this Element
     */
    public T setPageNumber(int pageNumber) {
        setProperty(Property.PAGE_NUMBER, pageNumber);
        return (T) (Object) this;
    }

    /**
     * Creates new renderer instance.
     *
     * @return new {@link IRenderer}
     */
    protected abstract IRenderer makeNewRenderer();
}
