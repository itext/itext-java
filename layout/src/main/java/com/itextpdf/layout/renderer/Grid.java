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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * This class represents a grid of elements.
 */
class Grid {
    private final List<List<GridCell>> rows = new ArrayList<>();
    private final CellPacker cellPacker;
    private float minHeight = 0.0f;
    private float minWidth = 0.0f;
    static final int ROW_ORDER = 1;
    static final int COLUMN_ORDER = 2;

    /**
     * Creates a new grid instance.
     *
     * @param initialRowsCount initial number of row for the grid
     * @param initialColumnsCount initial number of columns for the grid
     * @param densePacking if true, dense packing will be used, otherwise sparse packing will be used
     */
    Grid(int initialRowsCount, int initialColumnsCount, boolean densePacking) {
        ensureGridSize(initialRowsCount, initialColumnsCount);
        cellPacker = new CellPacker(densePacking);
    }

    /**
     * Get resulting layout height of the grid, if it's less than explicit (minimal) height of the grid
     * return the explicit one.
     *
     * @return resulting layout height of a grid.
     */
    float getHeight() {
        for (int i = rows.size() - 1; i >= 0; --i) {
            for (int j = 0; j < rows.get(0).size(); ++j) {
                if (rows.get(i).get(j) != null) {
                    return Math.max(rows.get(i).get(j).getLayoutArea().getTop(), minHeight);
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
    List<List<GridCell>> getRows() {
        return rows;
    }

    /**
     * Get any cell adjacent to the left of a given cell.
     * If there is no a direct neighbor to the left, and other adjacent cells are big cells and their column end
     * is bigger than the column start of a given cell, method will still return such a neighbor, though it's not
     * actually a neighbor to the left.
     *
     * @param value cell for which to find the neighbor
     *
     * @return adjacent cell to the left if found one, null otherwise
     */
    GridCell getClosestLeftNeighbor(GridCell value) {
        int x = value.getColumnStart();
        GridCell bigNeighbor = null;
        for (int i = 1; i <= x; ++i) {
            for (int j = 0; j < rows.size(); ++j) {
                if (rows.get(j).get(x - i) != null) {
                    if (rows.get(j).get(x - i).getColumnEnd() > x) {
                        bigNeighbor = rows.get(j).get(x - i);
                        continue;
                    }
                    return rows.get(j).get(x - i);
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
     * If there is no a direct neighbor to the top, and other adjacent cells are big cells and their row end
     * is bigger than the row start of a given cell, method will still return such a neighbor, though it's not
     * actually a neighbor to the top.
     *
     * @param value cell for which to find the neighbor
     *
     * @return adjacent cell to the top if found one, null otherwise
     */
    GridCell getClosestTopNeighbor(GridCell value) {
        int y = value.getRowStart();
        GridCell bigNeighbor = null;
        for (int i = 1; i <= y; ++i) {
            for (int j = 0; j < rows.get(0).size(); ++j) {
                if (rows.get(y - i).get(j) != null) {
                    if (rows.get(y - i).get(j).getRowEnd() > y) {
                        bigNeighbor = rows.get(y - i).get(j);
                        continue;
                    }
                    return rows.get(y - i).get(j);
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
     * @param iterationOrder if <code>Grid.ROW</code> the order of cells is from left to right, top to bottom
     *                       if <code>Grid.COLUMN</code> the order of cells is from top to bottom, left to right
     * @return collection of unique grid cells.
     */
    Collection<GridCell> getUniqueGridCells(int iterationOrder) {
        Collection<GridCell> result = new LinkedHashSet<>();
        if (iterationOrder == ROW_ORDER) {
            for (List<GridCell> cellsRow : rows) {
                for (GridCell cell : cellsRow) {
                    if (cell != null) {
                        result.add(cell);
                    }
                }
            }
        }
        if (iterationOrder == COLUMN_ORDER) {
            for (int j = 0; j < rows.get(0).size(); ++j) {
                for (int i = 0; i < rows.size(); ++i) {
                    if (rows.get(i).get(j) != null) {
                        result.add(rows.get(i).get(j));
                    }
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
        for (GridCell cell : rows.get(row)) {
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
        for (int i = 0; i < rows.size(); ++i) {
            GridCell cell = rows.get(i).get(column);
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

    float getMaxRowTop(int y, int x) {
        List<GridCell> row = rows.get(y);
        float maxTop = 0.0f;
        for (int i = 0; i < x; ++i) {
            if (row.get(i) == null || row.get(i).getLayoutArea() == null) {
                continue;
            }
            //process cells which end at the same row
            if (row.get(i).getLayoutArea().getTop() > maxTop && row.get(i).getRowEnd() == y + 1) {
                maxTop = row.get(i).getLayoutArea().getTop();
            }
        }
        return maxTop;
    }

    float getMaxColumnRight(int y, int x) {
        float maxRight = 0.0f;
        for (int i = 0; i < y; ++i) {
            GridCell cell = rows.get(i).get(x);
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
     * Add cell in the grid, checking that it would fit and initializing it upper left corner (x, y).
     *
     * @param cell cell to and in the grid
     */
    void addCell(GridCell cell) {
        ensureGridSize(cell.getRowEnd(), cell.getColumnEnd());
        setStartingRowAndColumn(cell);
        for (int i = cell.getRowStart(); i < cell.getRowEnd(); ++i) {
            for(int j = cell.getColumnStart(); j < cell.getColumnEnd(); ++j) {
                rows.get(i).set(j, cell);
            }
        }
    }

    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    private void setStartingRowAndColumn(GridCell cell) {
        if (cell.getColumnStart() != -1 && cell.getRowStart() != -1) {
            // cells which take more than 1 grid cells vertically and horizontally can't be moved
            for (int i = cell.getRowStart(); i < cell.getRowEnd(); ++i) {
                for(int j = cell.getColumnStart(); j < cell.getColumnEnd(); ++j) {
                    if (rows.get(i).get(j) != null) {
                        throw new IllegalArgumentException(LayoutExceptionMessageConstant.INVALID_CELL_INDEXES);
                    }
                }
            }
        } else if (cell.getColumnStart() != -1) {
            cellPacker.fitHorizontalCell(cell);
        } else if (cell.getRowStart() != -1) {
            cellPacker.fitVerticalCell(cell);
        } else {
            cellPacker.fitSimpleCell(cell);
        }
    }

    /**
     * Resize grid to ensure that right bottom corner of a cell will fit into the grid.
     *
     * @param rowEnd end row pos of a cell on a grid
     * @param columnEnd end column pos of a cell on a grid
     */
    private void ensureGridSize(int rowEnd, int columnEnd) {
        int maxRowSize = -1;
        for (int i = 0; i < Math.max(rowEnd, rows.size()); i++) {
            List<GridCell> row;
            if (i >= rows.size()) {
                row = new ArrayList<>();
                rows.add(row);
            } else {
                row = rows.get(i);
            }
            maxRowSize = Math.max(maxRowSize, row.size());

            for (int j = row.size(); j < Math.max(columnEnd, maxRowSize); j++) {
                row.add(null);
            }
        }
    }

    //TODO DEVSIX-8323 Right now "row sparse" and "row dense" algorithms are implemented
    // implement "column sparse" and "column dense" the only thing which changes is winding order of a grid.
    // One will need to create a "view" on cellRows which is rotated 90 degrees to the right and also swap parameters
    // for the ensureGridSize in such a case
    private class CellPacker {
        //Determines whether to use "dense" or "sparse" packing algorithm
        private final boolean densePacking;
        private int placementCursorX = 0;
        private int placementCursorY = 0;

        CellPacker(boolean densePacking) {
            this.densePacking = densePacking;
        }

        //TODO DEVSIX-8323 double check with https://drafts.csswg.org/css-grid/#grid-item-placement-algorithm
        // they have 2 cases for such cells however I could not find a case for “sparse” and “dense” packing to
        // be different

        /**
         * init vertical (<code>GridCell#getGridHeight() > 1</code>)
         * <code>GridCell</code> upper left corner to fit it in the grid
         *
         * @param cell cell to fit
         */
        void fitVerticalCell(GridCell cell) {
            for(int j = 0; j < rows.get(0).size(); ++j) {
                boolean found = true;
                for (int i = cell.getRowStart(); i < cell.getRowEnd(); ++i) {
                    if (rows.get(i).get(j) != null) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    cell.setStartingRowAndColumn(cell.getRowStart(), j);
                    return;
                }
            }
            cell.setStartingRowAndColumn(cell.getRowStart(), rows.get(0).size());
            ensureGridSize(-1, rows.get(0).size() + 1);
        }

        /**
         * init horizontal (<code>GridCell#getColumnEnd() - GridCell#getColumnStart() > 1</code>)
         * <code>GridCell</code> upper left corner to fit it in the grid
         *
         * @param cell cell to fit
         */
        void fitHorizontalCell(GridCell cell) {
            // All comments bellow are for the "sparse" packing, dense packing is much simpler and is achieved by
            // disabling placement cursor

            //Increment the cursor’s row position until a value is found where the grid item
            // does not overlap any occupied grid cells (creating new rows in the implicit grid as necessary).
            for (int i = getPlacementCursorY(); i < rows.size(); ++i, ++placementCursorY) {
                //Set the column position of the cursor to the grid item’s column-start line.
                // If this is less than the previous column position of the cursor, increment the row position by 1.
                if (cell.getColumnStart() < getPlacementCursorX()) {
                    placementCursorX = cell.getColumnStart();
                    continue;
                }
                placementCursorX = cell.getColumnStart();
                boolean found = true;
                for(int j = cell.getColumnStart(); j < cell.getColumnEnd(); ++j, ++placementCursorX) {
                    if (rows.get(i).get(j) != null) {
                        found = false;
                        break;
                    }
                }
                if (found) {
                    //Set the item’s row-start line to the cursor’s row position
                    cell.setStartingRowAndColumn(i, cell.getColumnStart());
                    return;
                }
            }
            cell.setStartingRowAndColumn(rows.size(), cell.getColumnStart());
            ensureGridSize(rows.size() + 1, -1);
        }

        /**
         * init simple (cell height = width = 1)
         * <code>GridCell</code> upper left corner to fit it in the grid
         *
         * @param cell cell to fit
         */
        void fitSimpleCell(GridCell cell) {
            //Algorithm the same as for horizontal cells except we're not checking for overlapping
            //and just placing the cell to the first space place
            for (int i = getPlacementCursorY(); i < rows.size(); ++i, ++placementCursorY) {
                for(int j = getPlacementCursorX(); j < rows.get(i).size(); ++j, ++placementCursorX) {
                    if (rows.get(i).get(j) == null) {
                        cell.setStartingRowAndColumn(i, j);
                        return;
                    }
                }
                placementCursorX = 0;
            }
            cell.setStartingRowAndColumn(rows.size(), 0);
            ensureGridSize(rows.size() + 1, -1);
        }

        //If it's dense packing, then it's enough to just disable placement cursors
        // and search for a spare place for the cell from the start of the grid.
        int getPlacementCursorX() {
            return densePacking ? 0 : placementCursorX;
        }

        int getPlacementCursorY() {
            return densePacking ? 0 : placementCursorY;
        }
    }
}