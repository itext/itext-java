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
import com.itextpdf.layout.properties.GridValue;
import com.itextpdf.layout.renderer.Grid.GridOrder;

import java.util.ArrayList;
import java.util.List;

// 12.1. Grid Sizing Algorithm
class GridSizer {
    private final Grid grid;
    private final List<GridValue> templateColumns;
    private final List<GridValue> templateRows;
    private final GridValue columnAutoWidth;
    private final GridValue rowAutoHeight;
    private final float columnGap;
    private final float rowGap;
    private final Rectangle actualBBox;

    GridSizer(Grid grid, List<GridValue> templateColumns, List<GridValue> templateRows,
            GridValue columnAutoWidth, GridValue rowAutoHeight, float columnGap, float rowGap, Rectangle actualBBox) {
        this.grid = grid;
        this.templateColumns = templateColumns;
        this.templateRows = templateRows;
        this.columnAutoWidth = columnAutoWidth;
        this.rowAutoHeight = rowAutoHeight;
        this.columnGap = columnGap;
        this.rowGap = rowGap;
        this.actualBBox = actualBBox;
    }

    public void sizeGrid() {
        // 1. First, the track sizing algorithm is used to resolve the sizes of the grid columns.
        resolveGridColumns();
        // 2. Next, the track sizing algorithm resolves the sizes of the grid rows.
        resolveGridRows();
    }

    private void resolveGridRows() {
        List<GridValue> rowsValues = new ArrayList<>();
        for (int i = 0; i < grid.getNumberOfRows(); i++) {
            if (templateRows != null && i < templateRows.size()) {
                rowsValues.add(templateRows.get(i));
            } else if (rowAutoHeight != null) {
                rowsValues.add(rowAutoHeight);
            } else {
                rowsValues.add(GridValue.createAutoValue());
            }
        }

        // TODO DEVSIX-8384 during grid sizing algorithm take into account grid container constraints
        GridTrackSizer gridTrackSizer = new GridTrackSizer(grid, rowsValues, rowGap, -1, GridOrder.ROW);
        List<Float> rows = gridTrackSizer.sizeTracks();
        for (GridCell cell : grid.getUniqueGridCells(GridOrder.ROW)) {
            float y = 0.0f;
            for (int currentRow = 0; currentRow < cell.getRowStart(); ++currentRow) {
                y += (float) rows.get(currentRow);
                y += rowGap;
            }
            cell.getLayoutArea().setY(y);

            float cellHeight = 0.0f;
            float[] rowSizes = new float[cell.getRowEnd() - cell.getRowStart()];
            int rowSizesIdx = 0;
            for (int i = cell.getRowStart(); i < cell.getRowEnd(); ++i) {
                rowSizes[rowSizesIdx] = (float) rows.get(i);
                if (rowSizesIdx != 0) {
                    // We take into account only top gap and not bottom one
                    rowSizes[rowSizesIdx] += rowGap;
                }
                ++rowSizesIdx;
                cellHeight += (float) rows.get(i);
            }
            // Preserve row sizes for split
            cell.setRowSizes(rowSizes);
            cellHeight += (cell.getGridHeight() - 1) * rowGap;
            cell.getLayoutArea().setHeight(cellHeight);
        }

        // calculating explicit height to ensure that even empty rows which covered by template would be considered
        float minHeight = 0.0f;
        for (Float row : rows) {
            minHeight += (float) row;
        }
        grid.setMinHeight(minHeight);
    }

    private void resolveGridColumns() {
        List<GridValue> colsValues = new ArrayList<>();
        for (int i = 0; i < grid.getNumberOfColumns(); i++) {
            if (templateColumns != null && i < templateColumns.size()) {
                colsValues.add(templateColumns.get(i));
            } else if (columnAutoWidth != null) {
                colsValues.add(columnAutoWidth);
            } else {
                colsValues.add(GridValue.createFlexValue(1f));
            }
        }
        GridTrackSizer gridTrackSizer = new GridTrackSizer(grid, colsValues, columnGap, actualBBox.getWidth(),
                GridOrder.COLUMN);
        List<Float> columns = gridTrackSizer.sizeTracks();

        for (GridCell cell : grid.getUniqueGridCells(GridOrder.COLUMN)) {
            float x = 0.0f;
            for (int currentColumn = 0; currentColumn < cell.getColumnStart(); ++currentColumn) {
                x += (float) columns.get(currentColumn);
                x += columnGap;
            }
            cell.getLayoutArea().setX(x);

            float cellWidth = 0.0f;
            for (int i = cell.getColumnStart(); i < cell.getColumnEnd(); ++i) {
                cellWidth += (float) columns.get(i);
            }
            cellWidth += (cell.getGridWidth() - 1) * columnGap;
            cell.getLayoutArea().setWidth(cellWidth);
        }
    }
}
