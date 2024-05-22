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

import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.properties.GridFlow;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a grid of elements.
 * Complex elements (which span over few cells of a grid) are stored as duplicates.
 * For example if element with width = 2, height = 3 added to a grid, grid will store it as 6 elements each having
 * width = height = 1.
 */
class Grid {
    private GridCell[][] rows = new GridCell[1][1];
    private final CellPlacementHelper cellPlacementHelper;
    private float minHeight = 0.0f;
    private float minWidth = 0.0f;

    /**
     * Creates a new grid instance.
     *
     * @param initialRowsCount initial number of row for the grid
     * @param initialColumnsCount initial number of columns for the grid
     * @param flow) see {@link com.itextpdf.layout.properties.GridFlow}
     */
    Grid(int initialRowsCount, int initialColumnsCount, GridFlow flow) {
        cellPlacementHelper = new CellPlacementHelper(this, flow);
        ensureGridSize(initialRowsCount, initialColumnsCount);
    }

    /**
     * Get resulting layout height of the grid, if it's less than explicit (minimal) height of the grid
     * return the explicit one.
     *
     * @return resulting layout height of a grid.
     */
    float getHeight() {
        for (int i = rows.length - 1; i >= 0; --i) {
            for (int j = 0; j < rows[0].length; ++j) {
                if (rows[i][j] != null) {
                    return Math.max(rows[i][j].getLayoutArea().getTop(), minHeight);
                }
            }
        }
        return minHeight;
    }

    /**
     * Get min width of the grid, which is size of the grid covered by absolute template values.
     *
     * @return min width of a grid.
     */
    float getMinWidth() {
        return minWidth;
    }

    /**
     * Get internal matrix of cells.
     *
     * @return matrix of cells.
     */
    GridCell[][] getRows() {
        return rows;
    }

    /**
     * Get any cell adjacent to the left of a given cell.
     * If there is no a direct neighbor to the left, and other adjacent cells are wide cells and their column end
     * is bigger than the column start of a given cell, method will still return such a neighbor, though it's not
     * actually a neighbor to the left. But if there will be a neighbor before column start of such a cell method will
     * return such a neighbor.
     *
     * @param value cell for which to find a neighbor
     *
     * @return adjacent cell to the left if found one, null otherwise
     */
    GridCell getClosestLeftNeighbor(GridCell value) {
        int x = value.getColumnStart();
        GridCell bigNeighbor = null;
        for (int i = 1; i <= x; ++i) {
            for (int j = 0; j < rows.length; ++j) {
                if (rows[j][x - i] != null) {
                    if (rows[j][x - i].getColumnEnd() > x) {
                        bigNeighbor = rows[j][x - i];
                        continue;
                    }
                    return rows[j][x - i];
                }
            }
            if (bigNeighbor != null && bigNeighbor.getColumnStart() == x - i) {
                return bigNeighbor;
            }
        }
        return null;
    }

    /**
     * Get any cell adjacent to the top of a given cell
     * If there is no a direct neighbor to the top, and other adjacent cells are tall cells and their row end
     * is bigger than the row start of a given cell, method will still return such a neighbor, though it's not
     * actually a neighbor to the top. But if there will be a neighbor before row start of such a cell method will
     * return such a neighbor.
     *
     * @param value cell for which to find a neighbor
     *
     * @return adjacent cell to the top if found one, null otherwise
     */
    GridCell getClosestTopNeighbor(GridCell value) {
        int y = value.getRowStart();
        GridCell bigNeighbor = null;
        for (int i = 1; i <= y; ++i) {
            for (int j = 0; j < rows[0].length; ++j) {
                if (rows[y - i][j] != null) {
                    if (rows[y - i][j].getRowEnd() > y) {
                        bigNeighbor = rows[y - i][j];
                        continue;
                    }
                    return rows[y - i][j];
                }
            }
            if (bigNeighbor != null && bigNeighbor.getRowStart() == y - i) {
                return bigNeighbor;
            }
        }
        return null;
    }

    /**
     * Get all unique cells in the grid.
     * Internally big cells (height * width > 1) are stored in multiple quantities
     * For example, cell with height = 2 and width = 2 will have 4 instances on a grid (width * height) to simplify
     * internal grid processing. This method counts such cells as one and returns a list of unique cells.
     *
     * @param iterationOrder if {GridOrder.ROW} the order of cells is from left to right, top to bottom
     *                       if {GridOrder.COLUMN} the order of cells is from top to bottom, left to right
     *
     * @return collection of unique grid cells.
     */
    Collection<GridCell> getUniqueGridCells(GridOrder iterationOrder) {
        Collection<GridCell> result = new LinkedHashSet<>();
        if (GridOrder.COLUMN.equals(iterationOrder)) {
            for (int j = 0; j < rows[0].length; ++j) {
                for (int i = 0; i < rows.length; ++i) {
                    if (rows[i][j] != null) {
                        result.add(rows[i][j]);
                    }
                }
            }
            return result;
        }
        for (GridCell[] cellsRow : rows) {
            for (GridCell cell : cellsRow) {
                if (cell != null) {
                    result.add(cell);
                }
            }
        }
        return result;
    }

    /**
     * align all cells in the specified row.
     *
     * @param row row to iterate
     * @param value new pos on a grid at which row should end
     */
    void alignRow(int row, float value) {
        GridCell previousCell = null;
        for (GridCell cell : rows[row]) {
            if (cell == null) {
                continue;
            }
            // previousCell is used to avoid multiple area updating for items which spread through few cells
            if (previousCell != cell && cell.getLayoutArea().getTop() < value) {
                cell.getLayoutArea().setHeight(value - cell.getLayoutArea().getY());
            }
            previousCell = cell;
        }
    }

    /**
     * align all cells in the specified column.
     *
     * @param column column to iterate
     * @param value new pos on a grid at which column should end
     */
    void alignColumn(int column, float value) {
        GridCell previousCell = null;
        for (int i = 0; i < rows.length; ++i) {
            GridCell cell = rows[i][column];
            if (cell == null) {
                continue;
            }
            // previousCell is used to avoid multiple area updating for items which spread through few cells
            if (previousCell != cell && cell.getLayoutArea().getRight() < value) {
                cell.getLayoutArea().setWidth(value - cell.getLayoutArea().getX());
            }
            previousCell = cell;
        }
    }

    /**
     * Get max top (layout area y + height of a cell) in a row with index = y, for all elements in a row before given x.
     *
     * @param y index of a row to find max top value
     * @param x index of element in a row before which to search for max top value
     *
     * @return max top value, all cells which do not end in the given row are not counted.
     */
    float getMaxRowTop(int y, int x) {
        GridCell[] row = rows[y];
        float maxTop = 0.0f;
        for (int i = 0; i < x; ++i) {
            if (row[i] == null || row[i].getLayoutArea() == null) {
                continue;
            }
            //process cells which end at the same row
            if (row[i].getLayoutArea().getTop() > maxTop && row[i].getRowEnd() == y + 1) {
                maxTop = row[i].getLayoutArea().getTop();
            }
        }
        return maxTop;
    }

    /**
     * Get max right (layout area x + width of a cell) in a column with index = x,
     * for all elements in a column before given y.
     *
     * @param y index of element in a column before which to search for max right value
     * @param x index of a column to find max right value
     *
     * @return max right value, all cells which do not end in the given column are not counted.
     */
    float getMaxColumnRight(int y, int x) {
        float maxRight = 0.0f;
        for (int i = 0; i < y; ++i) {
            GridCell cell = rows[i][x];
            if (cell == null || cell.getLayoutArea() == null) {
                continue;
            }
            //process cells which ends in the same column
            if (cell.getLayoutArea().getRight() > maxRight && cell.getColumnEnd() == x + 1) {
                maxRight = cell.getLayoutArea().getRight();
            }
        }
        return maxRight;
    }

    /**
     * Add cell in the grid, checking that it would fit and initializing it bottom left corner (x, y).
     *
     * @param cell cell to and in the grid
     */
    void addCell(GridCell cell) {
        cellPlacementHelper.fit(cell);
        for (int i = cell.getRowStart(); i < cell.getRowEnd(); ++i) {
            for(int j = cell.getColumnStart(); j < cell.getColumnEnd(); ++j) {
                rows[i][j] = cell;
            }
        }
    }

    void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    /**
     * Resize grid if needed, so it would have given number of rows/columns.
     *
     * @param height new grid height
     * @param width new grid width
     */
    void ensureGridSize(int height, int width) {
        if (height <= rows.length && width <= rows[0].length) {
            return;
        }
        GridCell[][] resizedRows = height > rows.length ? new GridCell[height][] : rows;
        int gridWidth = Math.max(width, rows[0].length);
        for (int i = 0; i < resizedRows.length; ++i) {
            if (i < rows.length) {
                if (width <= rows[i].length) {
                    resizedRows[i] = rows[i];
                } else {
                    GridCell[] row = new GridCell[width];
                    System.arraycopy(rows[i], 0, row, 0, rows[i].length);
                    resizedRows[i] = row;
                }
            } else {
                GridCell[] row = new GridCell[gridWidth];
                resizedRows[i] = row;
            }
        }
        rows = resizedRows;
    }

    static enum GridOrder {
        ROW,
        COLUMN
    }

    /**
     * This class is used to properly initialize starting values for grid.
     */
    static final class Builder {
        private int columnCount;
        private int rowCount;
        private GridFlow flow;
        private List<GridCell> cells;

        private Builder() {
        }

        /**
         * Get grid builder for list of values.
         *
         * @param values values to layout on grid
         * @return new grid builder instance
         */
        static Builder forItems(List<IRenderer> values) {
            Builder builder = new Builder();
            builder.cells = values.stream().map(val -> new GridCell(val)).collect(Collectors.toList());
            return builder;
        }

        /**
         * Set number of columns for a grid, the result will be either a provided one or if some elements
         * have a property defining more columns on a grid than provided value it will be set instead.
         *
         * @param minColumnCount min column count of a grid
         * @return current builder instance
         */
        public Builder columns(int minColumnCount) {
            columnCount = Math.max(
                    minColumnCount,
                    calculateInitialColumnsCount(cells)
            );
            return this;
        }

        /**
         * Set number of rows for a grid, the result will be either a provided one or if some elements
         * have a property defining more rows on a grid than provided value it will be set instead.
         *
         * @param minRowCount min height of a grid
         * @return current builder instance
         */
        public Builder rows(int minRowCount) {
            rowCount = Math.max(
                    minRowCount,
                    calculateInitialRowsCount(cells)
            );
            return this;
        }

        /**
         * Set iteration flow for a grid.
         *
         * @param flow iteration flow
         * @return current builder instance
         */
        public Builder flow(GridFlow flow) {
            this.flow = flow;
            Collections.sort(cells, getOrderingFunctionForFlow(flow));
            return this;
        }

        /**
         * Build a grid with provided properties.
         *
         * @return new {@code Grid} instance.
         */
        public Grid build() {
            final Grid grid = new Grid(rowCount, columnCount, flow);
            for (GridCell cell : cells) {
                grid.addCell(cell);
            }
            return grid;
        }

        private static int calculateInitialColumnsCount(Collection<GridCell> cells) {
            int initialColumnsCount = 1;
            for (GridCell cell : cells) {
                if (cell != null) {
                    initialColumnsCount = Math.max(cell.getGridWidth(), Math.max(initialColumnsCount, cell.getColumnEnd()));
                }
            }
            return initialColumnsCount;
        }

        private static int calculateInitialRowsCount(Collection<GridCell> cells) {
            int initialRowsCount = 1;
            for (GridCell cell : cells) {
                if (cell != null) {
                    initialRowsCount = Math.max(cell.getGridHeight(), Math.max(initialRowsCount, cell.getRowEnd()));
                }
            }
            return initialRowsCount;
        }

        static Comparator<GridCell> getOrderingFunctionForFlow(GridFlow flow) {
            if (GridFlow.COLUMN.equals(flow) || GridFlow.COLUMN_DENSE.equals(flow)) {
                return new ColumnCellComparator();
            }
            return new RowCellComparator();
        }
    }

    /**
     * This comparator sorts cells so ones with both fixed row and column positions would go first,
     * then cells with fixed row and then all other cells.
     */
    private final static class RowCellComparator implements Comparator<GridCell> {
        @Override
        public int compare(GridCell lhs, GridCell rhs) {
            int lhsModifiers = 0;
            if (lhs.getColumnStart() != -1 && lhs.getRowStart() != -1) {
                lhsModifiers = 2;
            } else if (lhs.getRowStart() != -1) {
                lhsModifiers = 1;
            }

            int rhsModifiers = 0;
            if (rhs.getColumnStart() != -1 && rhs.getRowStart() != -1) {
                rhsModifiers = 2;
            } else if (rhs.getRowStart() != -1) {
                rhsModifiers = 1;
            }
            //passing parameters in reversed order so ones with properties would come first
            return Integer.compare(rhsModifiers, lhsModifiers);
        }
    }

    /**
     * This comparator sorts cells so ones with both fixed row and column positions would go first,
     * then cells with fixed column and then all other cells.
     */
    private final static class ColumnCellComparator implements Comparator<GridCell> {
        @Override
        public int compare(GridCell lhs, GridCell rhs) {
            int lhsModifiers = 0;
            if (lhs.getColumnStart() != -1 && lhs.getRowStart() != -1) {
                lhsModifiers = 2;
            } else if (lhs.getColumnStart() != -1) {
                lhsModifiers = 1;
            }

            int rhsModifiers = 0;
            if (rhs.getColumnStart() != -1 && rhs.getRowStart() != -1) {
                rhsModifiers = 2;
            } else if (rhs.getColumnStart() != -1) {
                rhsModifiers = 1;
            }
            //passing parameters in reversed order so ones with properties would come first
            return Integer.compare(rhsModifiers, lhsModifiers);
        }
    }

    /**
     * This class is used to place cells on grid.
     */
    private static class CellPlacementHelper {
        private final GridView view;
        private final Grid grid;

        CellPlacementHelper(Grid grid, GridFlow flow) {
            this.view = new GridView(grid, flow);
            this.grid = grid;
        }

        /**
         * Place cell on grid and resize grid if needed.
         *
         * @param cell cell to place on a grid.
         */
        void fit(GridCell cell) {
            //resize the grid if needed to fit a cell into it
            grid.ensureGridSize(cell.getRowEnd(), cell.getColumnEnd());
            boolean result;
            //reset grid view to process new cell
            GridView.Pos pos = view.reset(cell.getRowStart(), cell.getColumnStart(),
                    cell.getGridWidth(), cell.getGridHeight());
            //Can use while(true) here, but since it's not expected to do more placement iteration as described with
            //max statement, to be on a safe side and prevent algorithm from hanging in unexpected situations doing
            //a finite number of iterations here.
            for(int i = 0; i < Math.max(cell.getGridHeight(), cell.getGridWidth()) + 1; ++i) {
                while (view.hasNext()) {
                    //Try to place the cell
                    result = view.fit(cell.getGridWidth(), cell.getGridHeight());
                    //If fit, init cell's left corner position
                    if (result) {
                        cell.setPos(pos.getY(), pos.getX());
                        return;
                    }
                    //Move grid view cursor
                    pos = view.next();
                }
                //If cell restricts both x and y position grow and can't be fitted on a grid, throw an excpetion
                if (view.isFixed()) {
                    throw new IllegalArgumentException(LayoutExceptionMessageConstant.INVALID_CELL_INDEXES);
                }
                //If cell was not fitted while iterating grid, then there is not enough space to fit it, and grid
                //has to be resized
                view.increaseDefaultAxis();
            }
        }
    }
}