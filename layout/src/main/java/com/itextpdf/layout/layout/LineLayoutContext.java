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
package com.itextpdf.layout.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import java.util.List;

/**
 * Represents the context for content of a line {@link com.itextpdf.layout.renderer.IRenderer#layout(LayoutContext) layouting}.
 */
public class LineLayoutContext extends LayoutContext {
    private boolean floatOverflowedToNextPageWithNothing = false;
    private float textIndent;

    /**
     * Creates the context for content of a line.
     *
     * @param area for the content to be placed on
     * @param marginsCollapseInfo the info about margins collapsing
     * @param floatedRendererAreas list of {@link Rectangle} objects
     * @param clippedHeight indicates whether the height is clipped or not
     */
    public LineLayoutContext(LayoutArea area, MarginsCollapseInfo marginsCollapseInfo, List<Rectangle> floatedRendererAreas, boolean clippedHeight) {
        super(area, marginsCollapseInfo, floatedRendererAreas, clippedHeight);
    }

    /**
     * Creates the context for content of a line.
     *
     * @param layoutContext the context for content layouting
     */
    public LineLayoutContext(LayoutContext layoutContext) {
        super(layoutContext.area, layoutContext.marginsCollapseInfo, layoutContext.floatRendererAreas, layoutContext.clippedHeight);
    }

    /**
     * Specifies whether some floating element within the same paragraph has already completely overflowed to the next
     * page.
     * @return true if floating element has already overflowed to the next page, false otherwise.
     */
    public boolean isFloatOverflowedToNextPageWithNothing() {
        return floatOverflowedToNextPageWithNothing;
    }

    /**
     * Changes the value of property specified by {@link #isFloatOverflowedToNextPageWithNothing()}.
     * @param floatOverflowedToNextPageWithNothing true if some floating element already completely overflowed.
     * @return this {@link LineLayoutContext} instance.
     */
    public LineLayoutContext setFloatOverflowedToNextPageWithNothing(boolean floatOverflowedToNextPageWithNothing) {
        this.floatOverflowedToNextPageWithNothing = floatOverflowedToNextPageWithNothing;
        return this;
    }

    /**
     * Gets the indent of text in the beginning of the current line.
     * @return the indent of text in this line.
     */
    public float getTextIndent() {
        return textIndent;
    }

    /**
     * Sets the indent of text in the beginning of the current line.
     * @param textIndent the indent of text in this line.
     * @return this {@link LineLayoutContext} instance.
     */
    public LineLayoutContext setTextIndent(float textIndent) {
        this.textIndent = textIndent;
        return this;
    }
}
