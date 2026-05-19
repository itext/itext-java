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
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Footnote;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Class representing container to store {@link Footnote} instances.
 */
class FootnotesContainer extends BlockElement<FootnotesContainer> {

    protected DefaultAccessibilityProperties tagProperties;

    /**
     * Creates new {@link FootnotesContainer} instance.
     */
    public FootnotesContainer() {
        // Empty constructor.
    }

    /**
     * Adds {@link Footnote} to this container.
     *
     * @param footnote {@link Footnote} to add
     *
     * @return this same {@link FootnotesContainer} instance
     */
    public FootnotesContainer add(Footnote footnote) {
        this.childElements.add(footnote);
        return this;
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
