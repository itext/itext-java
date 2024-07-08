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
import com.itextpdf.layout.properties.grid.GridFlow;

import java.util.ArrayList;
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
    //Using array list instead of array for .NET portability
    private final List<Collection<GridCell>> uniqueCells = new ArrayList<>(2);
    private final List<GridCell> itemsWithoutPlace = new ArrayList<>();

    /**
     * Creates a new grid instance.
     *
     * @param initialRowsCount initial number of row for the grid
     * @param initialColumnsCount initial number of columns for the grid
     */
    Grid(int initialRowsCount, int initialColumnsCount) {
        ensureGridSize(initialRowsCount, initialColumnsCount);
        //Add GridOrder.ROW and GridOrder.COLUMN cache for unique cells, which is null initially
        uniqueCells.add(null);
        uniqueCells.add(null);
    }

    /**
     * Get internal matrix of cells.
     *
     * @return matrix of cells.
     */
    final GridCell[][] getRows() {
        return rows;
    }

    /**
     * Gets the current number of rows of grid.
     *
     * @return the number of rows
     */
    final int getNumberOfRows() {
        return rows.length;
    }

    /**
     * Gets the current number of rows of grid.
     *
     * @return the number of columns
     */
    final int getNumberOfColumns() {
        return rows.length > 0 ? rows[0].length : 0;
    }

    /**
     * Gets unique cells in the specified row or column depends on passed {@link GridOrder}.
     *
     * @param order the order which will be used to extract cells
     * @param trackIndex the track index from which cells will be extracted
     *
     * @return collection of unique cells in a row or column
     */
    Collection<GridCell> getUniqueCellsInTrack(GridOrder order, int trackIndex) {
        List<GridCell> result = new ArrayList<>(order == GridOrder.ROW ? getNumberOfRows() : getNumberOfColumns());
        GridCell previous = null;
        if (GridOrder.COLUMN == order) {
            for (GridCell[] row : rows) {
                final GridCell current = row[trackIndex];
                if (current != null && current != previous) {
                    previous = current;
                    result.add(current);
                }
            }
        } else {
            for (GridCell current : rows[trackIndex]) {
                if (current != null && current != previous) {
                    previous = current;
                    result.add(current);
                }
            }
        }
        return result;
    }

    /**
     * Get all unique cells in the grid.
     * Internally big cells (height * width > 1) are stored in multiple quantities
     * For example, cell with height = 2 and width = 2 will have 4 instances on a grid (width * height) to simplify
     * internal grid processing. This method counts such cells as one and returns a list of unique cells.
     * The result is cached since grid can't be changed after creation.
     *
     * @param iterationOrder if {GridOrder.ROW} the order of cells is from left to right, top to bottom
     *                       if {GridOrder.COLUMN} the order of cells is from top to bottom, left to right
     *
     * @return collection of unique grid cells.
     */
    Collection<GridCell> getUniqueGridCells(GridOrder iterationOrder) {
        Collection<GridCell> result = new LinkedHashSet<>();
        if (uniqueCells.get(iterationOrder.ordinal()) != null) {
            return uniqueCells.get(iterationOrder.ordinal());
        }
        if (GridOrder.COLUMN.equals(iterationOrder)) {
            for (int j = 0; j < getNumberOfColumns(); ++j) {
                for (int i = 0; i < getNumberOfRows(); ++i) {
                    if (rows[i][j] != null) {
                        result.add(rows[i][j]);
                    }
                }
            }
            result.addAll(itemsWithoutPlace);
            uniqueCells.set(iterationOrder.ordinal(), result);
            return result;
        }
        // GridOrder.ROW
        for (GridCell[] cellsRow : rows) {
            for (GridCell cell : cellsRow) {
                if (cell != null) {
                    result.add(cell);
                }
            }
        }
        result.addAll(itemsWithoutPlace);
        uniqueCells.set(iterationOrder.ordinal(), result);
        return result;
    }

    /**
     * Resize grid if needed, so it would have given number of rows/columns.
     *
     * @param height new grid height
     * @param width new grid width
     */
    void ensureGridSize(int height, int width) {
        if (height <= getNumberOfRows() && width <= getNumberOfColumns()) {
            return;
        }
        GridCell[][] resizedRows = height > getNumberOfRows() ? new GridCell[height][] : rows;
        int gridWidth = Math.max(width, getNumberOfColumns());
        for (int i = 0; i < resizedRows.length; ++i) {
            if (i < getNumberOfRows()) {
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

    /**
     * Deletes all null rows/columns depending on the given values.
     * If resulting grid size is less than provided minSize than some null lines will be preserved.
     *
     * @param order which null lines to remove - {@link GridOrder#ROW} to remove rows
     *                                           {@link GridOrder#COLUMN} to remove columns
     * @param minSize minimal size of the resulting grid
     *
     * @return the number of left lines in given order
     */
    int collapseNullLines(GridOrder order, int minSize) {
        int nullLinesStart = determineNullLinesStart(order);
        if (nullLinesStart == -1) {
            return GridOrder.ROW.equals(order) ? getNumberOfRows() : getNumberOfColumns();
        } else {
            nullLinesStart = Math.max(minSize, nullLinesStart);
        }
        int rowsNumber = GridOrder.ROW.equals(order) ? nullLinesStart : getNumberOfRows();
        int colsNumber = GridOrder.COLUMN.equals(order) ? nullLinesStart : getNumberOfColumns();
        GridCell[][] shrankGrid = new GridCell[rowsNumber][];
        for (int i = 0; i < shrankGrid.length; ++i) {
            shrankGrid[i] = new GridCell[colsNumber];
        }
        for (int i = 0; i < shrankGrid.length; ++i) {
            System.arraycopy(rows[i], 0, shrankGrid[i], 0, shrankGrid[0].length);
        }
        rows = shrankGrid;
        return GridOrder.ROW.equals(order) ? getNumberOfRows() : getNumberOfColumns();
    }

    /**
     * Add cell in the grid, checking that it would fit and initializing it bottom left corner (x, y).
     *
     * @param cell cell to and in the grid
     */
    private void addCell(GridCell cell) {
        boolean placeFound = false;
        for (int i = cell.getRowStart(); i < cell.getRowEnd(); ++i) {
            for(int j = cell.getColumnStart(); j < cell.getColumnEnd(); ++j) {
                if (rows[i][j] == null) {
                    rows[i][j] = cell;
                    placeFound = true;
                }
            }
        }

        if (!placeFound) {
            itemsWithoutPlace.add(cell);
        }
    }

    private int determineNullLinesStart(GridOrder order) {
        if (GridOrder.ROW.equals(order)) {
            for (int i = 0; i < getNumberOfRows(); ++i) {
                boolean isNull = true;
                for (int j = 0; j < getNumberOfColumns(); ++j) {
                    if (getRows()[i][j] != null) {
                        isNull = false;
                        break;
                    }
                }
                if (isNull) {
                    return i;
                }
            }
            return -1;
        } else if (GridOrder.COLUMN.equals(order)) {
            for (int j = 0; j < getNumberOfColumns(); ++j) {
                boolean isNull = true;
                for (int i = 0; i < getNumberOfRows(); ++i) {
                    if (getRows()[i][j] != null) {
                        isNull = false;
                        break;
                    }
                }
                if (isNull) {
                    return j;
                }
            }
            return -1;
        }
        return -1;
    }

    enum GridOrder {
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
            final Grid grid = new Grid(rowCount, columnCount);
            CellPlacementHelper cellPlacementHelper = new CellPlacementHelper(grid, flow);
            for (GridCell cell : cells) {
                cellPlacementHelper.fit(cell);
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
                // If cell restricts both x and y position grow and can't be fitted on a grid,
                // exit occupying fixed position
                if (view.isFixed()) {
                    break;
                }
                //If cell was not fitted while iterating grid, then there is not enough space to fit it, and grid
                //has to be resized
                view.increaseDefaultAxis();
            }
        }
    }
}