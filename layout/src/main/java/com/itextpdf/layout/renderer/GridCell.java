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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.renderer.Grid.GridOrder;

/**
 * This class represents a cell in a grid.
 */
class GridCell {
    private final IRenderer value;
    private int columnStart;
    private int rowStart;
    private final int columnSpan;
    private final int rowSpan;
    private final Rectangle layoutArea = new Rectangle(0.0f, 0.0f, 0.0f,0.0f);

    /**
     * Cached track sizes for rows to use them during split.
     */
    private float[] rowSizes;

    /**
     * Create a grid cell and init value renderer position on a grid based on its properties.
     * @param value item renderer
     * @param x column number at which this cell starts (column numbers start from 0)
     * @param y row number at which this cell starts (row numbers from 0)
     * @param width number of columns spanned by this cell.
     * @param height number of rows spanned by this cell.
     */
    GridCell(IRenderer value, int x, int y, int width, int height) {
        this.value = value;
        this.columnStart = x;
        this.rowStart = y;
        this.columnSpan = width;
        this.rowSpan = height;
    }

    int getColumnStart() {
        return columnStart;
    }

    int getColumnEnd() {
        return columnStart + columnSpan;
    }

    int getRowStart() {
        return rowStart;
    }

    int getRowEnd() {
        return rowStart + rowSpan;
    }

    int getStart(GridOrder order) {
        if (GridOrder.COLUMN == order) {
            return getColumnStart();
        } else {
            return getRowStart();
        }
    }

    int getEnd(GridOrder order) {
        if (GridOrder.COLUMN == order) {
            return getColumnEnd();
        } else {
            return getRowEnd();
        }
    }

    int getGridHeight() {
        return rowSpan;
    }

    int getGridWidth() {
        return columnSpan;
    }

    int getGridSpan(GridOrder order) {
        if (GridOrder.COLUMN == order) {
            return getGridWidth();
        } else {
            return getGridHeight();
        }
    }

    IRenderer getValue() {
        return value;
    }

    Rectangle getLayoutArea() {
        return layoutArea;
    }

    void setPos(int y, int x) {
        this.rowStart = y;
        this.columnStart = x;
    }

    float[] getRowSizes() {
        return this.rowSizes;
    }

    void setRowSizes(float[] rowSizes) {
        this.rowSizes = rowSizes;
    }
}