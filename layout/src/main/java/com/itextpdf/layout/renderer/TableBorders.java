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


import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.properties.Property;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class TableBorders {
    /**
     * Horizontal borders of the table.
     *
     * It consists of a list, each item of which represents
     * a horizontal border of a row, each of them is a list of borders of the cells.
     * The amount of the lists is the number of rows + 1, the size of each of these lists
     * corresponds to the number of columns.
     */
    protected List<List<Border>> horizontalBorders = new ArrayList<>();

    /**
     * Vertical borders of the table.
     *
     * It consists of a list, each item of which represents
     * a vertical border of a row, each of them is a list of borders of the cells.
     * The amount of the lists is the number of columns + 1, the size of each of these lists
     * corresponds to the number of rows.
     */
    protected List<List<Border>> verticalBorders = new ArrayList<>();

    /**
     * The number of the table's columns.
     */
    protected final int numberOfColumns;

    /**
     * The outer borders of the table (as body).
     */
    protected Border[] tableBoundingBorders = new Border[4];

    /**
     * All the cells of the table.
     *
     * Each item of the list represents a row and consists of its cells.
     */
    protected List<CellRenderer[]> rows;

    /**
     * The first row, which should be processed on this area.
     *
     * The value of this field varies from area to area.
     * It's zero-based and inclusive.
     */
    protected int startRow;
    /**
     * The last row, which should be processed on this area.
     *
     * The value of this field varies from area to area.
     * It's zero-based and inclusive. The last border will have index (finishRow+1) because
     * the number of borders is greater by one than the number of rows
     */
    protected int finishRow;

    /**
     * The width of the widest left border.
     */
    protected float leftBorderMaxWidth;

    /**
     * The width of the widest right border.
     */
    protected float rightBorderMaxWidth;

    /**
     * The number of rows flushed to the table.
     *
     * Its value is zero for regular tables. The field makes sense only for large tables.
     */
    protected int largeTableIndexOffset = 0;

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders) {
        this.rows = rows;
        this.numberOfColumns = numberOfColumns;
        setTableBoundingBorders(tableBoundingBorders);
    }

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders, int largeTableIndexOffset) {
        this(rows, numberOfColumns, tableBoundingBorders);
        this.largeTableIndexOffset = largeTableIndexOffset;
    }
    // region abstract

    // region draw
    protected abstract TableBorders drawHorizontalBorder(PdfCanvas canvas, TableBorderDescriptor borderDescriptor);

    protected abstract TableBorders drawVerticalBorder(PdfCanvas canvas, TableBorderDescriptor borderDescriptor);
    // endregion

    // region area occupation
    protected abstract TableBorders applyTopTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean force, boolean reverse);

    protected abstract TableBorders applyTopTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean reverse);

    protected abstract TableBorders applyBottomTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean force, boolean reverse);

    protected abstract TableBorders applyBottomTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean reverse);

    protected abstract TableBorders applyLeftAndRightTableBorder(Rectangle layoutBox, boolean reverse);

    protected abstract TableBorders skipFooter(Border[] borders);

    protected abstract TableBorders skipHeader(Border[] borders);

    protected abstract TableBorders collapseTableWithFooter(TableBorders footerBordersHandler, boolean hasContent);

    protected abstract TableBorders collapseTableWithHeader(TableBorders headerBordersHandler, boolean updateBordersHandler);

    protected abstract TableBorders fixHeaderOccupiedArea(Rectangle occupiedBox, Rectangle layoutBox);

    protected abstract TableBorders applyCellIndents(Rectangle box, float topIndent, float rightIndent, float bottomIndent, float leftIndent, boolean reverse);
    // endregion

    // region getters
    abstract public List<Border> getVerticalBorder(int index);

    abstract public List<Border> getHorizontalBorder(int index);

    protected abstract float getCellVerticalAddition(float[] indents);
    // endregion

    protected abstract void buildBordersArrays(CellRenderer cell, int row, int col);

    protected abstract TableBorders updateBordersOnNewPage(boolean isOriginalNonSplitRenderer, boolean isFooterOrHeader, TableRenderer currentRenderer, TableRenderer headerRenderer, TableRenderer footerRenderer);
    // endregion

    protected TableBorders processAllBordersAndEmptyRows() {
        CellRenderer[] currentRow;
        int numOfRowsToRemove = 0;
        if (!rows.isEmpty()) {
            for (int row = startRow - largeTableIndexOffset; row <= finishRow - largeTableIndexOffset; row++) {
                currentRow = rows.get(row);
                boolean hasCells = false;
                for (int col = 0; col < numberOfColumns; col++) {
                    if (null != currentRow[col]) {
                        if (0 != numOfRowsToRemove) {
                            // Decrease rowspans if necessary
                            updateRowspanForNextNonEmptyCellInEachColumn(numOfRowsToRemove, row);

                            // Remove empty rows
                            removeRows(row - numOfRowsToRemove, numOfRowsToRemove);
                            row -= numOfRowsToRemove;
                            numOfRowsToRemove = 0;
                        }

                        buildBordersArrays(currentRow[col], row, col);
                        hasCells = true;
                        int colspan = (int) currentRow[col].getPropertyAsInteger(Property.COLSPAN);
                        col += colspan - 1;
                    } else {
                        if (horizontalBorders.get(row).size() <= col) {
                            horizontalBorders.get(row).add(null);
                        }
                    }
                }
                
                if (!hasCells) {
                    if (row == rows.size() - 1) {
                        removeRows(row - numOfRowsToRemove, numOfRowsToRemove);
                        // delete current row
                        rows.remove(row - numOfRowsToRemove);
                        setFinishRow(finishRow - 1);

                        Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                        logger.warn(IoLogMessageConstant.LAST_ROW_IS_NOT_COMPLETE);
                    } else {
                        numOfRowsToRemove++;
                    }
                }
            }
        }
        if (finishRow < startRow) {
            setFinishRow(startRow);
        }
        return this;
    }

    // region init
    protected TableBorders initializeBorders() {
        List<Border> tempBorders;
        // initialize vertical borders
        while (numberOfColumns + 1 > verticalBorders.size()) {
            tempBorders = new ArrayList<Border>();
            while ((int) Math.max(rows.size(), 1) > tempBorders.size()) {
                tempBorders.add(null);
            }
            verticalBorders.add(tempBorders);
        }
        // initialize horizontal borders
        while ((int) Math.max(rows.size(), 1) + 1 > horizontalBorders.size()) {
            tempBorders = new ArrayList<Border>();
            while (numberOfColumns > tempBorders.size()) {
                tempBorders.add(null);
            }
            horizontalBorders.add(tempBorders);
        }
        return this;
    }
    // endregion

    // region setters
    protected TableBorders setTableBoundingBorders(Border[] borders) {
        tableBoundingBorders = new Border[4];
        if (null != borders) {
            for (int i = 0; i < borders.length; i++) {
                tableBoundingBorders[i] = borders[i];
            }
        }
        return this;
    }

    protected TableBorders setRowRange(int startRow, int finishRow) {
        this.startRow = startRow;
        this.finishRow = finishRow;
        return this;
    }

    protected TableBorders setStartRow(int row) {
        this.startRow = row;
        return this;
    }

    protected TableBorders setFinishRow(int row) {
        this.finishRow = row;
        return this;
    }
    // endregion

    // region getters
    public float getLeftBorderMaxWidth() {
        return leftBorderMaxWidth;
    }

    public float getRightBorderMaxWidth() {
        return rightBorderMaxWidth;
    }

    public float getMaxTopWidth() {
        float width = 0;
        Border widestBorder = getWidestHorizontalBorder(startRow);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public float getMaxBottomWidth() {
        float width = 0;
        Border widestBorder = getWidestHorizontalBorder(finishRow + 1);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public float getMaxRightWidth() {
        float width = 0;
        Border widestBorder = getWidestVerticalBorder(verticalBorders.size() - 1);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public float getMaxLeftWidth() {
        float width = 0;
        Border widestBorder = getWidestVerticalBorder(0);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public Border getWidestVerticalBorder(int col) {
        return TableBorderUtil.getWidestBorder(getVerticalBorder(col));
    }

    public Border getWidestVerticalBorder(int col, int start, int end) {
        return TableBorderUtil.getWidestBorder(getVerticalBorder(col), start, end);
    }

    public Border getWidestHorizontalBorder(int row) {
        return TableBorderUtil.getWidestBorder(getHorizontalBorder(row));
    }

    public Border getWidestHorizontalBorder(int row, int start, int end) {
        return TableBorderUtil.getWidestBorder(getHorizontalBorder(row), start, end);
    }

    public List<Border> getFirstHorizontalBorder() {
        return getHorizontalBorder(startRow);
    }

    public List<Border> getLastHorizontalBorder() {
        return getHorizontalBorder(finishRow + 1);
    }

    public List<Border> getFirstVerticalBorder() {
        return getVerticalBorder(0);
    }

    public List<Border> getLastVerticalBorder() {
        return getVerticalBorder(verticalBorders.size() - 1);
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getFinishRow() {
        return finishRow;
    }

    public Border[] getTableBoundingBorders() {
        return tableBoundingBorders;
    }

    public float[] getCellBorderIndents(int row, int col, int rowspan, int colspan) {
        float[] indents = new float[4];
        List<Border> borderList;
        Border border;
        // process top border
        borderList = getHorizontalBorder(startRow + row - rowspan + 1);
        for (int i = col; i < col + colspan; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[0]) {
                indents[0] = border.getWidth();
            }
        }
        // process right border
        borderList = getVerticalBorder(col + colspan);
        for (int i = startRow + row - rowspan + 1; i < startRow + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[1]) {
                indents[1] = border.getWidth();
            }
        }
        // process bottom border
        borderList = getHorizontalBorder(startRow + row + 1);
        for (int i = col; i < col + colspan; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[2]) {
                indents[2] = border.getWidth();
            }
        }
        // process left border
        borderList = getVerticalBorder(col);
        for (int i = startRow + row - rowspan + 1; i < startRow + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[3]) {
                indents[3] = border.getWidth();
            }
        }
        return indents;
    }
    // endregion

    private void removeRows(int startRow, int numOfRows) {
        for (int row = startRow; row < startRow + numOfRows; row++) {
            rows.remove(startRow);
            horizontalBorders.remove(startRow + 1);
            for (int j = 0; j <= numberOfColumns; j++) {
                verticalBorders.get(j).remove(startRow + 1);
            }
        }
        setFinishRow(finishRow - numOfRows);
    }

    private void updateRowspanForNextNonEmptyCellInEachColumn(int numOfRowsToRemove, int row) {
        // We go by columns in a current row which is not empty. For each column we look for
        // a non-empty cell going up by rows (going down in a table). For each such cell we
        // collect data to be able to analyze its rowspan.

        // Iterate by columns
        int c = 0;
        while (c < numberOfColumns) {
            int r = row;
            CellRenderer[] cr = null;
            // Look for non-empty cell in a column
            while (r < rows.size() && (cr == null || cr[c] == null)) {
                cr = rows.get(r);
                ++r;
            }

            // Found a cell
            if (cr != null && cr[c] != null) {
                CellRenderer cell = cr[c];
                final int origRowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
                int spansToRestore = 0;
                // Here we analyze whether current cell's rowspan touches a non-empty row before
                // numOfRowsToRemove. If it doesn't touch it we will need to 'restore' a few
                // rowspans which is a difference between the current (non-empty) row and the row
                // where we found non-empty cell for this column
                if (row - numOfRowsToRemove < r - origRowspan) {
                    spansToRestore = r - row - 1;
                }

                int rowspan = origRowspan;
                rowspan = rowspan - numOfRowsToRemove;
                if (rowspan < 1) {
                    rowspan = 1;
                }
                rowspan += spansToRestore;
                rowspan = Math.min(rowspan, origRowspan);

                cell.setProperty(Property.ROWSPAN, rowspan);

                final int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
                c += colspan;
            } else {
                ++c;
            }
        }
    }
}
