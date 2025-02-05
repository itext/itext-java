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
package com.itextpdf.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;

/**
 * This class is used for convenient multi-column Document Layouting
 */
public class ColumnDocumentRenderer extends DocumentRenderer {

    protected Rectangle[] columns;
    protected int nextAreaNumber;

    /**
     * Creates a ColumnDocumentRenderer. Sets {@link #immediateFlush} to true.
     * 
     * @param document the {@link Document} on which this Renderer will calculate
     * and execute element placements
     * @param columns an array of {@link Rectangle} specifying the acceptable
     * positions for elements on a page
     */
    public ColumnDocumentRenderer(Document document, Rectangle[] columns) {
        super(document);
        this.columns = columns;
    }

    /**
     * Creates a ColumnDocumentRenderer whose elements need not be flushed
     * immediately.
     * 
     * @param document the {@link Document} on which this Renderer will calculate
     * and execute element placements
     * @param immediateFlush whether or not to flush contents as soon as possible
     * @param columns an array of {@link Rectangle} specifying the acceptable
     * positions for elements on a page
     */
    public ColumnDocumentRenderer(Document document, boolean immediateFlush, Rectangle[] columns) {
        super(document, immediateFlush);
        this.columns = columns;
    }

    /**
     * Gets the array index of the next area that will be written on after the
     * current one is full (overflowed).
     * @return the array index of the next area that will be written on
     */
    public int getNextAreaNumber() {
        return nextAreaNumber;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new ColumnDocumentRenderer(document, immediateFlush, columns);
    }

    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        if (overflowResult != null && overflowResult.getAreaBreak() != null && overflowResult.getAreaBreak().getType() != AreaBreakType.NEXT_AREA) {
            nextAreaNumber = 0;
        }
        if (nextAreaNumber % columns.length == 0) {
            super.updateCurrentArea(overflowResult);
        }
        return (currentArea = new RootLayoutArea(currentArea.getPageNumber(), columns[nextAreaNumber++ % columns.length].clone()));
    }
}
