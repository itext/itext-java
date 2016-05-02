/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.layout.ElementPropertyContainer;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.property.Property;
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
public abstract class AbstractElement<T extends IElement> extends ElementPropertyContainer<T> implements IElement {

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
        Object result = super.getProperty(property);
        if (styles != null && styles.size() > 0 && result == null && !super.hasProperty(property)) {
            for (Style style : styles) {
                result = style.getProperty(property);
                if (result != null || super.hasProperty(property)) {
                    break;
                }
            }
        }
        return (T1) result;
    }

    /**
     * Add a new style to this element. A style can be used as an effective way
     * to define multiple equal properties to several elements.
     * @param style the style to be added
     * @return this element
     */
    public T addStyle(Style style) {
        if (styles == null) {
            styles = new LinkedHashSet<>();
        }
        styles.add(style);
        return (T)this;
    }

    protected abstract IRenderer makeNewRenderer();

    /**
     * Marks all child elements as artifacts recursively.
     */
    protected void propagateArtifactRoleToChildElements() {
        for (IElement child : childElements) {
            if (child instanceof IAccessibleElement) {
                ((IAccessibleElement) child).setRole(PdfName.Artifact);
            }
        }
    }

    public boolean isEmpty() {
        return 0 == childElements.size();
    }

    public T setAction(PdfAction action) {
        setProperty(Property.ACTION, action);
        return (T) this;
    }

    public T setPageNumber(int pageNumber) {
        setProperty(Property.PAGE_NUMBER, pageNumber);
        return (T) this;
    }
}
