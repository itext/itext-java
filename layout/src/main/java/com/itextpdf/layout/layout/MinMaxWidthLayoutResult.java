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

import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * Represents the result of content {@link IRenderer#layout(LayoutContext) layouting}.
 */
public class MinMaxWidthLayoutResult extends LayoutResult {

    /**
     * The {@link MinMaxWidth} value of min and max width.
     */
    protected MinMaxWidth minMaxWidth;

    /**
     * Creates min and max width.
     *
     * @param status the status which indicates the content
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the splitted part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     */
    public MinMaxWidthLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer) {
        super(status, occupiedArea, splitRenderer, overflowRenderer);
        minMaxWidth = new MinMaxWidth();
    }

    /**
     * Creates min and max width.
     *
     * @param status the status which indicates the content
     * @param occupiedArea the area occupied by the content
     * @param splitRenderer the renderer to draw the splitted part of the content
     * @param overflowRenderer the renderer to draw the overflowed part of the content
     * @param cause the first renderer to produce {@link LayoutResult#NOTHING}
     */
    public MinMaxWidthLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer, IRenderer cause) {
        super(status, occupiedArea, splitRenderer, overflowRenderer, cause);
        minMaxWidth = new MinMaxWidth();
    }

    /**
     * Gets min and max width.
     *
     * @return min and max width
     */
    public MinMaxWidth getMinMaxWidth() {
        return minMaxWidth;
    }

    /**
     * Sets min and max width.
     *
     * @param minMaxWidth min and max width
     * @return min and max width
     */
    public MinMaxWidthLayoutResult setMinMaxWidth(MinMaxWidth minMaxWidth) {
        this.minMaxWidth = minMaxWidth;
        return this;
    }
}
