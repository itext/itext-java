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
import com.itextpdf.layout.properties.grid.AutoValue;
import com.itextpdf.layout.properties.grid.GridValue;
import com.itextpdf.layout.renderer.Grid.GridOrder;
import com.itextpdf.layout.renderer.GridTrackSizer.TrackSizingResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// 12.1. Grid Sizing Algorithm
/**
 * Class representing grid sizing algorithm.
 */
class GridSizer {
    private final Grid grid;
    private final List<GridValue> templateColumns;
    private final List<GridValue> templateRows;
    private final GridValue columnAutoWidth;
    private final GridValue rowAutoHeight;
    private final float columnGap;
    private final float rowGap;
    private final Rectangle actualBBox;
    private float containerHeight;

    /**
     * Creates new grid sizer instance.
     *
     * @param grid grid to size
     * @param templateColumns template values for columns
     * @param templateRows template values for rows
     * @param columnAutoWidth value which used to size columns out of template range
     * @param rowAutoHeight value which used to size rows out of template range
     * @param columnGap gap size between columns
     * @param rowGap gap size between rows
     * @param actualBBox actual bbox which restricts sizing algorithm
     */
    GridSizer(Grid grid, List<GridValue> templateColumns, List<GridValue> templateRows,
              GridValue columnAutoWidth, GridValue rowAutoHeight, float columnGap, float rowGap,
              Rectangle actualBBox) {
        this.grid = grid;
        this.templateColumns = templateColumns;
        this.templateRows = templateRows;
        this.columnAutoWidth = columnAutoWidth;
        this.rowAutoHeight = rowAutoHeight;
        this.columnGap = columnGap;
        this.rowGap = rowGap;
        this.actualBBox = actualBBox;
    }

    /**
     * Resolves grid track sizes.
     */
    public void sizeGrid() {
        // 1. First, the track sizing algorithm is used to resolve the sizes of the grid columns.
        resolveGridColumns();
        // 2. Next, the track sizing algorithm resolves the sizes of the grid rows.
        resolveGridRows();
    }

    /**
     * Gets grid container height.
     * Use this method only after calling {@link GridSizer#sizeGrid()}.
     *
     * @return grid container height covered by row template
     */
    public float getContainerHeight() {
        return containerHeight;
    }

    private void resolveGridRows() {
        List<GridValue> rowsValues = new ArrayList<>();
        for (int i = 0; i < grid.getNumberOfRows(); i++) {
            if (templateRows != null && i < templateRows.size()) {
                rowsValues.add(templateRows.get(i));
            } else if (rowAutoHeight != null) {
                rowsValues.add(rowAutoHeight);
            } else {
                rowsValues.add(AutoValue.VALUE);
            }
        }

        GridTrackSizer gridTrackSizer = new GridTrackSizer(grid, rowsValues, rowGap,
                actualBBox.getHeight(), GridOrder.ROW);
        TrackSizingResult result = gridTrackSizer.sizeTracks();
        List<Float> rows = result.getTrackSizesAndExpandPercents(rowsValues);
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
            //Preserve row sizes for split
            cell.setRowSizes(rowSizes);
            cellHeight += (cell.getGridHeight() - 1) * rowGap;
            cell.getLayoutArea().setHeight(cellHeight);
        }

        containerHeight = calculateGridOccupiedHeight(result.getTrackSizes());
    }

    /**
     * Calculate grid container occupied area based on original (non-expanded percentages) track sizes.
     *
     * @param originalSizes original track sizes
     * @return grid container occupied area
     */
    private float calculateGridOccupiedHeight(List<Float> originalSizes) {
        // Calculate explicit height to ensure that even empty rows which covered by template would be considered
        float minHeight = 0.0f;
        for (int i = 0; i < (templateRows == null ? 0 : Math.min(templateRows.size(), originalSizes.size())); ++i) {
            minHeight += (float) originalSizes.get(i);
        }
        float maxHeight = sum(originalSizes);
        // Add gaps
        minHeight += (grid.getNumberOfRows() - 1) * rowGap;
        maxHeight += (grid.getNumberOfRows() - 1) * rowGap;
        float occupiedHeight = 0.0f;
        Collection<GridCell> cells = grid.getUniqueGridCells(GridOrder.ROW);
        for (GridCell cell : cells) {
            occupiedHeight = Math.max(occupiedHeight, cell.getLayoutArea().getTop());
        }
        return Math.max(Math.min(maxHeight, occupiedHeight), minHeight);
    }

    private float sum(List<Float> trackSizes) {
        float sum = 0.0f;
        for (Float size : trackSizes) {
            sum += (float) size;
        }
        return sum;
    }

    private void resolveGridColumns() {
        List<GridValue> colsValues = new ArrayList<>();
        for (int i = 0; i < grid.getNumberOfColumns(); i++) {
            if (templateColumns != null && i < templateColumns.size()) {
                colsValues.add(templateColumns.get(i));
            } else if (columnAutoWidth != null) {
                colsValues.add(columnAutoWidth);
            } else {
                colsValues.add(AutoValue.VALUE);
            }
        }
        GridTrackSizer gridTrackSizer = new GridTrackSizer(grid, colsValues, columnGap,
                actualBBox.getWidth(), GridOrder.COLUMN);
        List<Float> columns = gridTrackSizer.sizeTracks().getTrackSizesAndExpandPercents(colsValues);

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
