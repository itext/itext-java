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

import com.itextpdf.layout.properties.grid.GridFlow;

/**
 * This class represents a view on a grid, which returns cells one by one in a specified order.
 * Also it allows to place a cell on a grid in this specific order and resizes the grid if needed.
 */
class GridView {
    private final Grid grid;
    private final Grid.GridOrder iterationOrder;
    private final Cursor cursor;
    private boolean restrictYGrow = false;
    private boolean restrictXGrow = false;
    private boolean hasNext = true;
    private int rightMargin;
    private int bottomMargin;

    GridView(Grid grid, GridFlow iterationOrder) {
        this.iterationOrder = (GridFlow.COLUMN.equals(iterationOrder) || GridFlow.COLUMN_DENSE.equals(iterationOrder))
                ? Grid.GridOrder.COLUMN : Grid.GridOrder.ROW;
        this.cursor = new Cursor(GridFlow.ROW_DENSE.equals(iterationOrder)
                || GridFlow.COLUMN_DENSE.equals(iterationOrder));
        this.grid = grid;
    }

    public boolean hasNext() {
        return cursor.y < grid.getRows().length - bottomMargin
                && cursor.x < grid.getRows()[0].length - rightMargin
                && hasNext;
    }

    public Pos next() {
        //If cell has fixed both x and y, then no need to iterate over the grid.
        if (isFixed()) {
            hasNext = false;
        } else if (restrictXGrow) {
            //If cell has fixed x position, then only view's 'y' can be moved.
            ++cursor.y;
            return new Pos(cursor);
        } else if (restrictYGrow) {
            //If cell has fixed y position, then only view's 'x' can be moved.
            ++cursor.x;
            return new Pos(cursor);
        }
        //If current flow is row, then grid should be iterated row by row, so iterate 'x' first, then 'y'
        //If current flow is column, then grid should be iterated column by column, so iterate 'y' first, then 'x'
        Pos boundaries = new Pos(grid.getRows().length - bottomMargin, grid.getRows()[0].length - rightMargin);
        cursor.increment(iterationOrder, boundaries);
        return new Pos(cursor);
    }

    /**
     * Resets grid view and sets it up for processing new element
     * If sparse algorithm is used then x and y positions on a grid are not reset.
     *
     * @param y left upper corner y position of an element on the grid
     * @param x left upper corner x position of an element on the grid
     * @param rightMargin specifies how many cells not to process at the right end of a grid
     * @param bottomMargin specifies how many cells not to process at the bottom end of a grid
     * @return first element position
     */
    Pos reset(int y, int x, int rightMargin, int bottomMargin) {
        this.cursor.setY(y);
        this.cursor.setX(x);
        this.rightMargin = rightMargin - 1;
        this.bottomMargin = bottomMargin - 1;
        this.restrictXGrow = x != -1;
        this.restrictYGrow = y != -1;
        this.hasNext = true;
        return new Pos(cursor);
    }

    /**
     * Try to fit cell at the current position.
     * If cell is fit, then update current flow cursor axis by width/height of a laid out cell.
     *
     * @param width width of the cell
     * @param height height of the cell
     * @return true if cell is fit, false otherwise
     */
    boolean fit(int width, int height) {
        final GridCell[][] rows = grid.getRows();
        for (int i = cursor.x; i < cursor.x + width; ++i) {
            for (int j = cursor.y; j < cursor.y + height; ++j) {
                if (rows[j][i] != null) {
                    return false;
                }
            }
        }
        increaseDefaultCursor(new Pos(height, width));
        resetCursorIfIntersectingCellIsPlaced();
        return true;
    }

    /**
     * Reset cursor to the start of a grid if placed a cell, which intersects current flow axis.
     * This is needed because such cells should be placed out of order and it's expected that
     * they are go first while constructing the grid.
     */
    void resetCursorIfIntersectingCellIsPlaced() {
        if ((Grid.GridOrder.ROW.equals(iterationOrder) && restrictYGrow)
        || (Grid.GridOrder.COLUMN.equals(iterationOrder) && restrictXGrow)) {
            this.cursor.reset();
        }
    }

    /**
     * Increase cursor in current flow axis
     *
     * @param cellSizes cell width and height values
     */
    void increaseDefaultCursor(Pos cellSizes) {
        if (Grid.GridOrder.ROW.equals(iterationOrder)) {
            cursor.x += cellSizes.x - 1;
        } else if (Grid.GridOrder.COLUMN.equals(iterationOrder)) {
            cursor.y += cellSizes.y - 1;
        }

    }

    void increaseDefaultAxis() {
        if (restrictYGrow) {
            grid.ensureGridSize(-1, grid.getRows()[0].length + 1);
        } else if (restrictXGrow) {
            grid.ensureGridSize(grid.getRows().length + 1, -1);
        } else if (Grid.GridOrder.ROW.equals(iterationOrder)) {
            grid.ensureGridSize(grid.getRows().length + 1, -1);
        } else if (Grid.GridOrder.COLUMN.equals(iterationOrder)) {
            grid.ensureGridSize(-1, grid.getRows()[0].length + 1);
        }
        hasNext = true;
    }

    /**
     * Determines if current grid view can be iterated.
     *
     * @return {@code true} if fixed {@code false} otherwise
     */
    boolean isFixed() {
        return restrictXGrow && restrictYGrow;
    }

    /**
     * Represents position on a grid.
     */
    static class Pos {
        /**
         * column index.
         */
        protected int x;
        /**
         * row index.
         */
        protected int y;

        /**
         * Creates a position from two integers.
         *
         * @param y row index.
         * @param x column index.
         */
        public Pos(int y, int x) {
            this.y = y;
            this.x = x;
        }

        /**
         * Creates a position from other position instance.
         *
         * @param other other position
         */
        public Pos(Pos other) {
            this.y = other.y;
            this.x = other.x;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    /**
     * Represents a placement cursor.
     */
    static class Cursor extends Pos {
        //Determines whether to use "dense" or "sparse" packing algorithm
        private final boolean densePacking;

        /**
         * Create new placement cursor with either sparse or dense placement algorithm.
         *
         * @param densePacking true to use "dense", false to use "sparse" placement algorithm
         */
        public Cursor(boolean densePacking) {
            super(0, 0);
            this.densePacking = densePacking;
        }

        public void setX(int x) {
            if (densePacking) {
                this.x = Math.max(x, 0);
            } else if (this.x > x && x != -1) {
                //Special case for sparse algorithm
                //if provided 'x' less than cursor 'x' then increase cursor's 'y'
                this.x = x;
                ++this.y;
            } else {
                this.x = Math.max(x, this.x);
            }
        }

        public void setY(int y) {
            if (densePacking) {
                this.y = Math.max(y, 0);
            } else if (this.y > y && y != -1) {
                //Special case for sparse algorithm
                //if provided 'y' less than cursor 'y' then increase cursor's 'x'
                this.y = y;
                ++this.x;
            } else {
                this.y = Math.max(y, this.y);
            }
        }

        /**
         * Increment cursor in specified flow axis and if it exceeds the boundary in that axis
         * make a carriage return.
         *
         * @param flow flow which determines in which axis cursor will be increased
         * @param boundaries grid view boundaries
         */
        public void increment(Grid.GridOrder flow, Pos boundaries) {
            if (Grid.GridOrder.ROW.equals(flow)) {
                ++x;
                if (x == boundaries.x) {
                    x = 0;
                    ++y;
                }
            } else if (Grid.GridOrder.COLUMN.equals(flow)) {
                ++y;
                if (y == boundaries.y) {
                    y = 0;
                    ++x;
                }
            }
        }

        public void reset() {
            this.x = 0;
            this.y = 0;
        }
    }
}