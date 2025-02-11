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

import com.itextpdf.layout.renderer.IRenderer;
import java.util.List;

/**
 * Represents the result of a line {@link com.itextpdf.layout.renderer.LineRenderer#layout(LayoutContext) layouting}.
 */
public class LineLayoutResult extends MinMaxWidthLayoutResult {

    /**
     * Indicates whether split was forced by new line symbol or not.
     */
    protected boolean splitForcedByNewline;
    private List<IRenderer> floatsOverflowedToNextPage;

    /**
     * Creates the {@link LayoutResult result of {@link com.itextpdf.layout.renderer.LineRenderer#layout(LayoutContext) layouting}}.
     * The {@link LayoutResult#causeOfNothing} will be set as null.
     *
     * @param status the status of {@link com.itextpdf.layout.renderer.LineRenderer#layout(LayoutContext)}
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the splitted part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     */
    public LineLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer) {
        super(status, occupiedArea, splitRenderer, overflowRenderer);
    }

    /**
     * Creates the {@link LayoutResult result of {@link com.itextpdf.layout.renderer.LineRenderer#layout(LayoutContext) layouting}}.
     *
     * @param status the status of {@link com.itextpdf.layout.renderer.LineRenderer#layout(LayoutContext)}
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the splitted part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     * @param cause the first renderer to produce {@link LayoutResult#NOTHING}
     */
    public LineLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer, IRenderer cause) {
        super(status, occupiedArea, splitRenderer, overflowRenderer, cause);
    }

    /**
     * Indicates whether split was forced by new line symbol in rendered text.
     * The value will be set as true if, for example,
     * the rendered text of one of the child renderers contains '\n' symbol.
     *
     * @return whether split was forced by new line or not
     */
    public boolean isSplitForcedByNewline() {
        return splitForcedByNewline;
    }

    /**
     * Sets a flat that split was forced by new line symbol in rendered text.
     *
     * @param isSplitForcedByNewline indicates that split was forced by new line symbol in rendered text.
     * @return {@link com.itextpdf.layout.layout.LineLayoutResult this layout result} the setting was applied on.
     */
    public LineLayoutResult setSplitForcedByNewline(boolean isSplitForcedByNewline) {
        this.splitForcedByNewline = isSplitForcedByNewline;
        return this;
    }

    /**
     * Gets the list of floats overflowed to next page.
     *
     * @return list of floats overflowed to next page
     */
    public List<IRenderer> getFloatsOverflowedToNextPage() {
        return floatsOverflowedToNextPage;
    }

    /**
     * Sets the list of floats overflowed to next page.
     *
     * @param floatsOverflowedToNextPage the floats overflowed to next page
     */
    public void setFloatsOverflowedToNextPage(List<IRenderer> floatsOverflowedToNextPage) {
        this.floatsOverflowedToNextPage = floatsOverflowedToNextPage;
    }
}
