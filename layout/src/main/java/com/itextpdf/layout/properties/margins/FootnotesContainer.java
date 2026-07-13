/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.layout.properties.margins;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.tagging.TaggingHintKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Class representing container to store {@link Footnote} instances.
 */
class FootnotesContainer extends BlockElement<FootnotesContainer> {
    private final int pageNumber;
    private final Map<IPropertyContainer, TaggingHintKey> footnoteTaggingHints = new HashMap<>();

    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates new {@link FootnotesContainer} instance.
     *
     * @param pageNum number of the page to which this container will be added
     */
    public FootnotesContainer(int pageNum) {
        this.pageNumber = pageNum;
        this.setNeutralRole();
    }

    /**
     * Adds {@link Footnote} to this container.
     *
     * @param footnote {@link Footnote} to add
     *
     * @return this same {@link FootnotesContainer} instance
     */
    public FootnotesContainer add(Footnote footnote, TaggingHintKey taggingHint) {
        this.childElements.add(footnote);
        footnoteTaggingHints.put(footnote, taggingHint);
        return this;
    }

    /**
     * Adds footnoted from another FootnotesContainer.
     *
     * @param otherContainer the other FootnotesContainer to add the footnotes from
     */
    public void addFootnotesFromOtherContainer(FootnotesContainer otherContainer) {
        for (IElement childElement : otherContainer.childElements) {
            if (childElement instanceof Footnote) {
                add((Footnote) childElement, otherContainer.footnoteTaggingHints.get(childElement));
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IRenderer createRendererSubTree() {

        IRenderer rendererRoot = getRenderer();
        for (IElement child : childElements) {
            if (child instanceof Footnote) {
                Footnote footnote = (Footnote) child;
                footnote.applyFootnoteAnchor(this.pageNumber);
            }
            IRenderer childRenderer = child.createRendererSubTree();
            if (footnoteTaggingHints.containsKey(child)) {
                childRenderer.setProperty(Property.TAGGING_HINT_KEY, footnoteTaggingHints.get(child));
            }
            rendererRoot.addChild(childRenderer);
        }
        return rendererRoot;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.P);
        }
        return tagProperties;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    protected IRenderer makeNewRenderer() {
        return new FootnotesContainerRenderer(this);
    }
}
