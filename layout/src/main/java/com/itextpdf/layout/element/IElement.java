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

import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * This class represents a layout element, i.e. a piece of content that will
 * take up 'physical' space on a canvas or document. Its presence and positioning
 * may influence the position of other {@link IElement}s on the layout surface.
 *
 */
public interface IElement extends IPropertyContainer {

    /**
     * Overrides the {@link IRenderer} instance which will be returned by the next call to the {@link #getRenderer()}.
     * @param renderer the renderer instance
     */
    void setNextRenderer(IRenderer renderer);

    /**
     * Gets a renderer for this element. Note that this method can be called more than once.
     * By default each element should define its own renderer, but the renderer can be overridden by
     * {@link #setNextRenderer(IRenderer)} method call.
     * @return a renderer for this element
     */
    IRenderer getRenderer();

    /**
     * Creates a renderer subtree with root in the current element.
     * Compared to {@link #getRenderer()}, the renderer returned by this method should contain all the child
     * renderers for children of the current element.
     * @return a renderer subtree for this element
     */
    IRenderer createRendererSubTree();
}
