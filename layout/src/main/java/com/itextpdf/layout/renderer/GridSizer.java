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
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;

import java.util.List;

/**
 * This class is responsible for sizing grid elements and calculating their layout area for future layout process.
 */
class GridSizer {
    private final Grid grid;
    //TODO DEVSIX-8326 since templates will have not only absoulute values, we're probably need to create
    // separate fields, something like rowsHeights, columnsWidths which will store absolute/calculated values.
    // replace all absolute value logic using this new fields
    private final List<Float> templateRows;
    private final List<Float> templateColumns;
    private final Float rowAutoHeight;
    private final Float columnAutoWidth;
    //TODO DEVSIX-8326 here should be a list/map of different resolvers
    private final SizeResolver sizeResolver;
    private final float columnGap;
    private final float rowGap;

    GridSizer(Grid grid, List<Float> templateRows, List<Float> templateColumns,
                     Float rowAutoHeight, Float columnAutoWidth, Float columnGap, Float rowGap) {
        this.grid = grid;
        this.templateRows = templateRows;
        this.templateColumns = templateColumns;
        this.rowAutoHeight = rowAutoHeight;
        this.columnAutoWidth = columnAutoWidth;
        this.sizeResolver = new MinContentResolver(grid);
        this.columnGap = columnGap == null ? 0.0f : (float) columnGap;
        this.rowGap = rowGap == null ? 0.0f : (float) rowGap;
    }

    //Grid Sizing Algorithm
    /**
     * This method simulates positioning of cells on the grid, by calculating their occupied area.
     * If there was enough place to fit the renderer value of a cell on the grid, then such cell will be marked as
     * non-fit and will be added to nothing result during actual layout.
     */
    void sizeCells() {
        //TODO DEVSIX-8326 if rowsAutoSize/columnsAutoSize == auto/fr/min-content/max-content we need to track the
        // corresponding values of the elements and update auto height/width after calculating each cell layout area
        // and if its changed than re-layout
        //Grid Sizing Algorithm
        for (GridCell cell : grid.getUniqueGridCells(Grid.COLUMN_ORDER)) {
            cell.getLayoutArea().setX(calculateCellX(cell));
            cell.getLayoutArea().setWidth(calculateCellWidth(cell));
        }
        for (GridCell cell : grid.getUniqueGridCells(Grid.ROW_ORDER)) {
            cell.getLayoutArea().setY(calculateCellY(cell));
            cell.getLayoutArea().setHeight(calculateCellHeight(cell));
        }
    }

    /**
     * Calculate cell left upper corner y position.
     *
     * @param cell cell to calculate starting position
     * @return left upper corner y value
     */
    private float calculateCellY(GridCell cell) {
        //For y we always know that there is a top neighbor at least in one column (because if not it means there
        //is a null row) and all cells in a row above have the same top.
        GridCell topNeighbor = grid.getClosestTopNeighbor(cell);
        if (topNeighbor != null) {
            return topNeighbor.getLayoutArea().getTop() + rowGap;
        }
        return 0.0f;
    }

    /**
     * Calculate cell left upper corner x position.
     *
     * @param cell cell to calculate starting position
     * @return left upper corner x value
     */
    private float calculateCellX(GridCell cell) {
        float x = 0.0f;
        int currentColumn = 0;
        if (templateColumns != null) {
            for (; currentColumn < Math.min(templateColumns.size(), cell.getColumnStart()); ++currentColumn) {
                x += (float) templateColumns.get(currentColumn);
                x += columnGap;
            }
            if (currentColumn == cell.getColumnStart()) {
                return x;
            }
        }
        if (columnAutoWidth != null) {
            for (; currentColumn < cell.getColumnStart(); ++currentColumn) {
                x += (float) columnAutoWidth;
                x += columnGap;
            }
            return x;
        }

        GridCell leftNeighbor = grid.getClosestLeftNeighbor(cell);
        if (leftNeighbor != null) {
            if (leftNeighbor.getColumnEnd() > cell.getColumnStart()) {
                x = leftNeighbor.getLayoutArea().getX() + leftNeighbor.getLayoutArea().getWidth()
                        *((float)(cell.getColumnStart() - leftNeighbor.getColumnStart()))/leftNeighbor.getGridWidth();
            } else {
                x = leftNeighbor.getLayoutArea().getRight();
            }
            x += columnGap;
        }
        return x;
    }

    //TODO DEVSIX-8327 Calculate fr units of rowsAutoSize here
    //TODO DEVSIX-8327 currently the default behaviour when there is no templateRows or rowAutoHeight is
    // css grid-auto-columns: min-content analogue, the default one should be 1fr
    // (for rows they are somewhat similar, if there where no fr values in templateRows, then the
    // size of all subsequent rows is the size of the highest row after templateRows
    //TODO DEVSIX-8327 add a comment for this method and how it works (not done in the scope of previous ticket
    // because logic will be changed after fr value implementation)
    private float calculateCellHeight(GridCell cell) {
        float cellHeight = 0.0f;
        //process cells with grid height > 1;
        if (cell.getGridHeight() > 1) {
            int counter = 0;
            for (int i = cell.getRowStart(); i < cell.getRowEnd(); ++i) {
                if (templateRows != null && i < templateRows.size()) {
                    ++counter;
                    cellHeight += (float) templateRows.get(i);
                } else if (rowAutoHeight != null) {
                    ++counter;
                    cellHeight += (float) rowAutoHeight;
                }
            }
            if (counter > 1) {
                cellHeight += rowGap * (counter - 1);
            }
            if (counter == cell.getGridHeight()) {
                return cellHeight;
            }
        }

        if (templateRows == null || cell.getRowStart() >= templateRows.size()) {
            //TODO DEVSIX-8324 if row auto height value is fr or min-content do not return here
            if (rowAutoHeight != null) {
                return (float) rowAutoHeight;
            }
            cellHeight = sizeResolver.resolveHeight(cell, cellHeight);
        } else {
            cellHeight = templateRows.get(cell.getRowStart());
        }
        return cellHeight;
    }

    //TODO DEVSIX-8327 currently the default behaviour when there is no templateColumns or columnAutoWidth is
    // css grid-auto-columns: min-content analogue, the default one should be 1fr
    private float calculateCellWidth(GridCell cell) {
        float cellWidth = 0.0f;
        //process absolute values for wide cells
        if (cell.getGridWidth() > 1) {
            int counter = 0;
            for (int i = cell.getColumnStart(); i < cell.getColumnEnd(); ++i) {
                if (templateColumns != null && i < templateColumns.size()) {
                    ++counter;
                    cellWidth += templateColumns.get(i);
                } else if (columnAutoWidth != null) {
                    ++counter;
                    cellWidth += (float) columnAutoWidth;
                }
            }
            if (counter > 1) {
                cellWidth += columnGap * (counter - 1);
            }
            if (counter == cell.getGridWidth()) {
                return cellWidth;
            }
        }
        if (templateColumns == null || cell.getColumnEnd() > templateColumns.size()) {
            //TODO DEVSIX-8324 if row auto width value is fr or min-content do not return here
            if (columnAutoWidth != null) {
                return (float) columnAutoWidth;
            }
            cellWidth = sizeResolver.resolveWidth(cell, cellWidth);
        } else {
            //process absolute template values
            cellWidth = templateColumns.get(cell.getColumnStart());
        }
        return cellWidth;
    }

    /**
     * The {@code SizeResolver} is used to calculate cell width and height on layout area.
     */
    protected abstract static class SizeResolver {
        protected Grid grid;

        /**
         * Create a new {@code SizeResolver} instance for the given {@code Grid} instance.
         *
         * @param grid grid which cells sizes will be resolved
         */
        public SizeResolver(Grid grid) {
            this.grid = grid;
        }
        public abstract float resolveHeight(GridCell cell, float explicitCellHeight);
        public abstract float resolveWidth(GridCell cell, float explicitCellWidth);

        //TODO DEVSIX-8326 If we're getting LayoutResult = NOTHING (PARTIAL ? if it is even possible) / null occupied area
        // from this method, we need to return current default sizing for a grid.
        // For min-content - that's should be practically impossible but as a safe guard we can return height of any adjacent
        // item in a row
        // For fr (flex) unit - currently calculated flex value
        // For other relative unit this should be investigated
        // Basically this method will only be called for relative values of rowsAutoHeight and we need to carefully think
        // what to return if we failed to layout a cell item in a given space
        /**
         * Calculate minimal cell height required for cell value to be laid out.
         *
         * @param cell cell container in which cell value has to be laid out
         * @return required height for cell to be laid out
         */
        protected float calculateImplicitCellHeight(GridCell cell) {
            cell.getValue().setProperty(Property.FILL_AVAILABLE_AREA, Boolean.FALSE);
            LayoutResult inifiniteHeighLayoutResult = cell.getValue().layout(
                    new LayoutContext(new LayoutArea(1,
                            new Rectangle(cell.getLayoutArea().getWidth(), AbstractRenderer.INF))));
            if (inifiniteHeighLayoutResult.getStatus() == LayoutResult.NOTHING
                    || inifiniteHeighLayoutResult.getStatus() == LayoutResult.PARTIAL) {
                cell.setValueFitOnCellArea(false);
                return -1;
            }
            return inifiniteHeighLayoutResult.getOccupiedArea().getBBox().getHeight();
        }

        /**
         * Calculate minimal cell width required for cell value to be laid out.
         *
         * @param cell cell container in which cell value has to be laid out
         * @return required width for cell to be laid out
         */
        protected float calculateMinRequiredCellWidth(GridCell cell) {
            cell.getValue().setProperty(Property.FILL_AVAILABLE_AREA, Boolean.FALSE);
            if (cell.getValue() instanceof AbstractRenderer) {
                AbstractRenderer abstractRenderer = (AbstractRenderer) cell.getValue();
                return abstractRenderer.getMinMaxWidth().getMinWidth();
            }
            return -1;
        }

    }

    /**
     * The {@code MinContentResolver} is used to calculate cell width and height on layout area by calculating their
     * min required size.
     */
    protected static class MinContentResolver extends SizeResolver {
        /**
         * {@inheritDoc}
         */
        public MinContentResolver(Grid grid) {
            super(grid);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public float resolveHeight(GridCell cell, float cellHeight) {
            float maxRowTop = grid.getMaxRowTop(cell.getRowStart(), cell.getColumnStart());
            cellHeight = Math.max(cellHeight, calculateImplicitCellHeight(cell));
            if (maxRowTop >= cellHeight + cell.getLayoutArea().getY()) {
                cellHeight = maxRowTop - cell.getLayoutArea().getY();
            } else {
                grid.alignRow(cell.getRowEnd() - 1, cellHeight + cell.getLayoutArea().getY());
            }
            return cellHeight;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public float resolveWidth(GridCell cell, float cellWidth) {
            float maxColumnRight = grid.getMaxColumnRight(cell.getRowStart(), cell.getColumnStart());
            cellWidth = Math.max(cellWidth, calculateMinRequiredCellWidth(cell));
            if (maxColumnRight >= cellWidth + cell.getLayoutArea().getX()) {
                cellWidth = maxColumnRight - cell.getLayoutArea().getX();
            } else {
                grid.alignColumn(cell.getColumnEnd() - 1, cellWidth + cell.getLayoutArea().getX());
            }
            return cellWidth;
        }
    }
}
