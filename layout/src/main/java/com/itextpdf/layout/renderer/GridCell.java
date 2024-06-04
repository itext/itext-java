/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
    private int gridX;
    private int gridY;
    private final int spanColumn;
    private final int spanRow;
    private final Rectangle layoutArea = new Rectangle(0.0f, 0.0f, 0.0f,0.0f);

    /**
     * Create a grid cell and init value renderer position on a grid based on its properties.
     *
     * @param value item renderer
     */
    GridCell(IRenderer value) {
        this.value = value;
        final int[] rowPlacement = initAxisPlacement(value.<Integer>getProperty(Property.GRID_ROW_START),
                value.<Integer>getProperty(Property.GRID_ROW_END), value.<Integer>getProperty(Property.GRID_ROW_SPAN));
        gridY = rowPlacement[0];
        spanRow = rowPlacement[1];

        final int[] columnPlacement = initAxisPlacement(value.<Integer>getProperty(Property.GRID_COLUMN_START),
                value.<Integer>getProperty(Property.GRID_COLUMN_END), value.<Integer>getProperty(Property.GRID_COLUMN_SPAN));
        gridX = columnPlacement[0];
        spanColumn = columnPlacement[1];
    }

    int getColumnStart() {
        return gridX;
    }

    int getColumnEnd() {
        return gridX + spanColumn;
    }

    int getRowStart() {
        return gridY;
    }

    int getRowEnd() {
        return gridY + spanRow;
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
        return spanRow;
    }

    int getGridWidth() {
        return spanColumn;
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
        this.gridY = y;
        this.gridX = x;
    }

    /**
     * Init axis placement values
     * if start > end values are swapped
     *
     * @param start x/y pos of cell on a grid
     * @param end x/y + width/height pos of cell on a grid
     * @param span vertical or horizontal span of the cell on a grid
     * @return row/column start + vertical/horizontal span values as a pair, where first value is start, second is span
     */
    private int[] initAxisPlacement(Integer start, Integer end, Integer span) {
        int[] result = new int[] {0, 1};
        if (start != null && end != null) {
            int intStart = (int) start;
            int intEnd = (int) end;
            if (intStart < intEnd) {
                result[0] = intStart;
                result[1] = intEnd - intStart;
            } else {
                result[0] = intEnd;
                result[1] = intStart - intEnd;
            }
        } else if (start != null) {
            result[0] = (int) start;
            if (span != null) {
                result[1] = (int) span;
            }
            // span default value 1 was set up on the result array initialization
        } else if (end != null) {
            int intEnd = (int) end;
            if (span == null) {
                result[0] = end <= 1 ? 1 : ((int) end) - 1;
                // span default value 1 was set up on the result array initialization
            } else {
                int intSpan = (int) span;
                result[1] = intSpan;
                result[0] = Math.max(intEnd - intSpan, 1);
            }
        } else if (span != null) {
            result[1] = (int) span;
        }
        result[0] -= 1;
        return result;
    }
}