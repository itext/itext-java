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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.Property;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class CollapsedTableBorders extends TableBorders {
    /**
     * Horizontal borders to be collapsed with
     * the first-on-the-area row's cell top borders of this TableRenderer instance.
     */
    private List<Border> topBorderCollapseWith = new ArrayList<Border>();

    /**
     * Horizontal borders to be collapsed with
     * the last-on-the-area row's cell bottom borders of this TableRenderer instance.
     */
    private List<Border> bottomBorderCollapseWith = new ArrayList<Border>();

    // NOTE: Currently body's top border is written at header level and footer's top border is written
    //  at body's level, hence there is no need in the same array for vertical top borders.
    /**
     * Vertical borders to be collapsed with
     * the last-on-the-area row's cell bottom borders of this TableRenderer instance.
     */
    private List<Border> verticalBottomBorderCollapseWith = null;

    private static Comparator<Border> borderComparator = new BorderComparator();

    // region constructors
    public CollapsedTableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders) {
        super(rows, numberOfColumns, tableBoundingBorders);
    }

    public CollapsedTableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders, int largeTableIndexOffset) {
        super(rows, numberOfColumns, tableBoundingBorders, largeTableIndexOffset);
    }
    // endregion

    // region getters
    public List<Border> getTopBorderCollapseWith() {
        return topBorderCollapseWith;
    }

    public List<Border> getBottomBorderCollapseWith() {
        return bottomBorderCollapseWith;
    }

    @Override
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
        for (int i = startRow - largeTableIndexOffset + row - rowspan + 1; i < startRow - largeTableIndexOffset + row + 1; i++) {
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
        for (int i = startRow - largeTableIndexOffset + row - rowspan + 1; i < startRow - largeTableIndexOffset + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[3]) {
                indents[3] = border.getWidth();
            }
        }
        return indents;
    }

    /**
     * Gets vertical borders which cross the top horizontal border.
     *
     * @return vertical borders which cross the top horizontal border
     */
    public List<Border> getVerticalBordersCrossingTopHorizontalBorder() {
        List<Border> borders = new ArrayList<>(numberOfColumns + 1);
        for (int i = 0; i <= numberOfColumns; i++) {
            final List<Border> verticalBorder = getVerticalBorder(i);
            // the passed index indicates the index of the border on the page, not in the entire document
            Border borderToAdd = startRow - largeTableIndexOffset < verticalBorder.size()
                    ? verticalBorder.get(startRow - largeTableIndexOffset) : null;
            borders.add(borderToAdd);
        }
        return borders;
    }

    @Override
    public List<Border> getVerticalBorder(int index) {
        if (index == 0) {
            List<Border> borderList = TableBorderUtil
                    .createAndFillBorderList(null, tableBoundingBorders[3], verticalBorders.get(0).size());
            return getCollapsedList(verticalBorders.get(0), borderList);
        } else if (index == numberOfColumns) {
            List<Border> borderList = TableBorderUtil.createAndFillBorderList(null, tableBoundingBorders[1],
                    verticalBorders.get(verticalBorders.size() - 1).size());
            return getCollapsedList(verticalBorders.get(verticalBorders.size() - 1), borderList);
        } else {
            return verticalBorders.get(index);
        }
    }


    public List<Border> getHorizontalBorder(int index) {
        if (index == startRow) {
            List<Border> firstBorderOnCurrentPage = TableBorderUtil.createAndFillBorderList(topBorderCollapseWith, tableBoundingBorders[0], numberOfColumns);
            if (index == largeTableIndexOffset) {
                return getCollapsedList(horizontalBorders.get(index - largeTableIndexOffset), firstBorderOnCurrentPage);
            }
            if (0 != rows.size()) {
                int col = 0;
                int row = index;
                while (col < numberOfColumns) {
                    if (null != rows.get(row - largeTableIndexOffset)[col] &&
                            row - index + 1 <= (int) ((Cell) rows.get(row - largeTableIndexOffset)[col].getModelElement()).getRowspan()) {
                        CellRenderer cell = rows.get(row - largeTableIndexOffset)[col];
                        Border cellModelTopBorder = TableBorderUtil.getCellSideBorder(((Cell) cell.getModelElement()), Property.BORDER_TOP);
                        int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
                        if (null == firstBorderOnCurrentPage.get(col) || (null != cellModelTopBorder && cellModelTopBorder.getWidth() > firstBorderOnCurrentPage.get(col).getWidth())) {
                            for (int i = col; i < col + colspan; i++) {
                                firstBorderOnCurrentPage.set(i, cellModelTopBorder);
                            }
                        }
                        col += colspan;
                        row = index;
                    } else {
                        row++;
                        if (row == rows.size()) {
                            break;
                        }
                    }
                }
            }
            return firstBorderOnCurrentPage;

        } else if ((index == finishRow + 1)) {
            List<Border> lastBorderOnCurrentPage = TableBorderUtil.createAndFillBorderList(bottomBorderCollapseWith, tableBoundingBorders[2], numberOfColumns);
            if (index - largeTableIndexOffset == horizontalBorders.size() - 1) {
                return getCollapsedList(horizontalBorders.get(index - largeTableIndexOffset), lastBorderOnCurrentPage);
            }
            if (0 != rows.size()) {
                int col = 0;
                int row = index - 1;
                while (col < numberOfColumns) {
                    if (null != rows.get(row - largeTableIndexOffset)[col]) {
                        CellRenderer cell = rows.get(row - largeTableIndexOffset)[col];
                        Border cellModelBottomBorder = TableBorderUtil.getCellSideBorder(((Cell) cell.getModelElement()), Property.BORDER_BOTTOM);
                        int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
                        if (null == lastBorderOnCurrentPage.get(col) || (null != cellModelBottomBorder && cellModelBottomBorder.getWidth() > lastBorderOnCurrentPage.get(col).getWidth())) {
                            for (int i = col; i < col + colspan; i++) {
                                lastBorderOnCurrentPage.set(i, cellModelBottomBorder);
                            }
                        }
                        col += colspan;
                        row = index - 1;
                    } else {
                        row++;
                        if (row == rows.size()) {
                            break;
                        }
                    }
                }
            }
            return lastBorderOnCurrentPage;
        } else {
            return horizontalBorders.get(index - largeTableIndexOffset);
        }
    }
    // endregion

    // region setters
    public CollapsedTableBorders setTopBorderCollapseWith(List<Border> topBorderCollapseWith) {
        this.topBorderCollapseWith = new ArrayList<Border>();
        if (null != topBorderCollapseWith) {
            this.topBorderCollapseWith.addAll(topBorderCollapseWith);
        }
        return this;
    }

    public CollapsedTableBorders setBottomBorderCollapseWith(List<Border> bottomBorderCollapseWith,
            List<Border> verticalBordersCrossingBottomBorder) {
        this.bottomBorderCollapseWith = new ArrayList<Border>();
        if (null != bottomBorderCollapseWith) {
            this.bottomBorderCollapseWith.addAll(bottomBorderCollapseWith);
        }
        this.verticalBottomBorderCollapseWith = null;
        if (null != verticalBordersCrossingBottomBorder) {
            this.verticalBottomBorderCollapseWith = new ArrayList<Border>(verticalBordersCrossingBottomBorder);
        }
        return this;
    }
    //endregion

    protected void buildBordersArrays(CellRenderer cell, int row, int col, int[] rowspansToDeduct) {
        // We should check if the row number is less than horizontal borders array size. It can happen if the cell with
        // big rowspan doesn't fit current area and is going to be placed partial.
        if (row > horizontalBorders.size()) {
            row--;
        }
        int currCellColspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);

        int nextCellRow;
        int j;

        // consider the cell on the left side of the current one
        if (col != 0 && null == rows.get(row)[col - 1]) {
            j = col;
            do {
                j--;
                nextCellRow = row;
                while (rows.size() != nextCellRow && null == rows.get(nextCellRow)[j]) {
                    nextCellRow++;
                }

            } while (j > 0 && rows.size() != nextCellRow &&
                    (j + (int) rows.get(nextCellRow)[j].getPropertyAsInteger(Property.COLSPAN) != col ||
                            (int) nextCellRow - rows.get((int) nextCellRow)[j].getPropertyAsInteger(Property.ROWSPAN) + 1 != row));
            // process only valid cells which hasn't been processed yet
            if (j >= 0 && nextCellRow != rows.size() && nextCellRow > row) {
                CellRenderer nextCell = rows.get(nextCellRow)[j];
                buildBordersArrays(nextCell, nextCellRow, true);
            }

        }
        // consider cells under the current one
        j = 0;
        while (j < currCellColspan) {
            nextCellRow = row + 1;
            while (nextCellRow < rows.size() && null == rows.get(nextCellRow)[col + j]) {
                nextCellRow++;
            }
            if (nextCellRow == rows.size()) {
                break;
            }
            CellRenderer nextCell = rows.get(nextCellRow)[col + j];
            // otherwise the border was considered previously
            if (row == nextCellRow - (int) nextCell.getPropertyAsInteger(Property.ROWSPAN)) {
                buildBordersArrays(nextCell, nextCellRow, true);
            }
            j += (int) nextCell.getPropertyAsInteger(Property.COLSPAN);
        }

        // consider cells on the right side of the current one
        if (col + currCellColspan < rows.get(row).length) {
            nextCellRow = row;
            while (nextCellRow < rows.size() && null == rows.get(nextCellRow)[col + currCellColspan]) {
                nextCellRow++;
            }
            if (nextCellRow != rows.size()) {
                CellRenderer nextCell = rows.get(nextCellRow)[col + currCellColspan];
                buildBordersArrays(nextCell, nextCellRow, true);
            }
        }
        // consider current cell
        buildBordersArrays(cell, row, false);

    }

    protected void buildBordersArrays(CellRenderer cell, int row, boolean isNeighbourCell) {
        int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
        int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
        int colN = ((Cell) cell.getModelElement()).getCol();
        Border[] cellBorders = cell.getBorders();

        // cell with big rowspan was splitted
        if (row + 1 - rowspan < 0) {
            rowspan = row + 1;
        }

        // consider top border
        for (int i = 0; i < colspan; i++) {
            checkAndReplaceBorderInArray(horizontalBorders, row + 1 - rowspan, colN + i, cellBorders[0], false);
        }
        // consider bottom border
        for (int i = 0; i < colspan; i++) {
            checkAndReplaceBorderInArray(horizontalBorders, row + 1, colN + i, cellBorders[2], true);
        }
        // consider left border
        for (int j = row - rowspan + 1; j <= row; j++) {
            checkAndReplaceBorderInArray(verticalBorders, colN, j, cellBorders[3], false);
        }
        // consider right border
        for (int i = row - rowspan + 1; i <= row; i++) {
            checkAndReplaceBorderInArray(verticalBorders, colN + colspan, i, cellBorders[1], true);
        }
    }
    // endregion

    // region lowlevel
    protected boolean checkAndReplaceBorderInArray(List<List<Border>> borderArray, int i, int j, Border borderToAdd, boolean hasPriority) {
        List<Border> borders = borderArray.get(i);
        Border neighbour = borders.get(j);
        if (neighbour == null) {
            borders.set(j, borderToAdd);
            return true;
        } else {
            if (neighbour != borderToAdd) {
                if (borderToAdd != null && neighbour.getWidth() <= borderToAdd.getWidth()) {
                    if (!hasPriority && neighbour.getWidth() == borderToAdd.getWidth()) {
                        return false;
                    }
                    borders.set(j, borderToAdd);
                    return true;
                }
            }
        }

        return false;
    }
    // endregion

    // region draw
    protected TableBorders drawHorizontalBorder(PdfCanvas canvas, TableBorderDescriptor borderDescriptor) {
        int i = borderDescriptor.getBorderIndex();
        float startX = borderDescriptor.getMainCoordinateStart();
        float y1 = borderDescriptor.getCrossCoordinate();
        float[] countedColumnWidth = borderDescriptor.getMainCoordinateWidths();

        List<Border> horizontalBorder = getHorizontalBorder(startRow + i);
        float x1 = startX;
        float x2 = x1 + countedColumnWidth[0];

        for (int j = 1; j <= horizontalBorder.size(); j++) {
            Border currentBorder = horizontalBorder.get(j - 1);
            Border nextBorder = j < horizontalBorder.size() ? horizontalBorder.get(j) : null;
            if (currentBorder != null) {
                List<Border> crossingBordersAtStart = getCrossingBorders(i, j - 1);
                float startCornerWidth = getWidestBorderWidth(crossingBordersAtStart.get(1),
                        crossingBordersAtStart.get(3));
                List<Border> crossingBordersAtEnd = getCrossingBorders(i, j);
                float endCornerWidth = getWidestBorderWidth(crossingBordersAtEnd.get(1), crossingBordersAtEnd.get(3));

                // TODO DEVSIX-5962 Once the ticket is done, remove this workaround, which allows
                //  horizontal borders to win at vertical-0. Bear in mind that this workaround helps
                //  in standard cases, when borders are of the same width. If they are not then
                //  this workaround doesn't help to improve corner collapsing
                if (1 == j) {
                    crossingBordersAtStart.add(0, currentBorder);
                }
                if (0 == i) {
                    if (1 != j) {
                        crossingBordersAtStart.add(0, crossingBordersAtStart.get(3));
                    }
                    crossingBordersAtEnd.add(0, crossingBordersAtEnd.get(3));
                }

                Collections.sort(crossingBordersAtStart, borderComparator);
                Collections.sort(crossingBordersAtEnd, borderComparator);

                float x1Offset = currentBorder.equals(crossingBordersAtStart.get(0))
                        ? -startCornerWidth / 2
                        : startCornerWidth / 2;
                float x2Offset = currentBorder.equals(crossingBordersAtEnd.get(0))
                        ? endCornerWidth / 2
                        : -endCornerWidth / 2;
                currentBorder.drawCellBorder(canvas, x1 + x1Offset, y1, x2 + x2Offset, y1, Border.Side.NONE);
                x1 = x2;
            } else {
                // if current border is null, then just skip it's processing.
                // Border corners will be processed by borders which are not null.
                x1 += countedColumnWidth[j - 1];
                x2 = x1;
            }
            if (nextBorder != null && j != horizontalBorder.size()) {
                x2 += countedColumnWidth[j];
            }
        }
        return this;
    }

    protected TableBorders drawVerticalBorder(PdfCanvas canvas, TableBorderDescriptor borderDescriptor) {
        int i = borderDescriptor.getBorderIndex();
        float startY = borderDescriptor.getMainCoordinateStart();
        float x1 = borderDescriptor.getCrossCoordinate();
        float[] heights = borderDescriptor.getMainCoordinateWidths();

        List<Border> borders = getVerticalBorder(i);
        float y1 = startY;
        float y2 = y1;
        if (0 != heights.length) {
            y2 = y1 - heights[0];
        }
        Float y1Offset = null;
        for (int j = 1; j <= heights.length; j++) {
            Border currentBorder = borders.get(startRow - largeTableIndexOffset + j - 1);
            Border nextBorder = j < heights.length ? borders.get(startRow - largeTableIndexOffset + j) : null;
            if (currentBorder != null) {
                List<Border> crossingBordersAtStart = getCrossingBorders(j - 1, i);
                float startCornerWidth = getWidestBorderWidth(crossingBordersAtStart.get(0),
                        crossingBordersAtStart.get(2));
                // TODO DEVSIX-5962 Once the ticket is done, remove this workaround, which allows
                //  vertical borders to win at horizontal-0. Bear in mind that this workaround helps
                //  in standard cases, when borders are of the same width. If they are not then
                //  this workaround doesn't help to improve corner collapsing
                if (1 == j) {
                    crossingBordersAtStart.add(0, currentBorder);
                }
                Collections.sort(crossingBordersAtStart, borderComparator);

                List<Border> crossingBordersAtEnd = getCrossingBorders(j, i);
                float endCornerWidth = getWidestBorderWidth(crossingBordersAtEnd.get(0), crossingBordersAtEnd.get(2));
                Collections.sort(crossingBordersAtEnd, borderComparator);

                // if all the borders are equal, we need to draw them at the end
                if (!currentBorder.equals(nextBorder)) {
                    if (null == y1Offset) {
                        y1Offset = currentBorder.equals(crossingBordersAtStart.get(0))
                                ? startCornerWidth / 2
                                : -startCornerWidth / 2;
                    }
                    float y2Offset = currentBorder.equals(crossingBordersAtEnd.get(0))
                            ? -endCornerWidth / 2
                            : endCornerWidth / 2;

                    currentBorder
                            .drawCellBorder(canvas, x1, y1 + (float) y1Offset, x1, y2 + y2Offset, Border.Side.NONE);
                    y1 = y2;
                    y1Offset = null;
                } else {
                    // if current border equal the next one, we apply an optimization here, which allows us
                    // to draw equal borders at once and not by part. Therefore for the first of such borders
                    // we store its start offset
                    if (null == y1Offset) {
                        y1Offset = currentBorder.equals(crossingBordersAtStart.get(0))
                                ? startCornerWidth / 2
                                : -startCornerWidth / 2;
                    }
                }
            } else {
                // if current border is null, then just skip it's processing.
                // Border corners will be processed by borders which are not null.
                y1 -= heights[j - 1];
                y2 = y1;
            }
            if (nextBorder != null) {
                y2 -= heights[j];
            }
        }
        return this;
    }
    // endregion

    // region static

    /**
     * Returns the collapsed border. We process collapse
     * if the table border width is strictly greater than cell border width.
     *
     * @param cellBorder  cell border
     * @param tableBorder table border
     * @return the collapsed border
     */
    public static Border getCollapsedBorder(Border cellBorder, Border tableBorder) {
        if (null != tableBorder) {
            if (null == cellBorder || cellBorder.getWidth() < tableBorder.getWidth()) {
                return tableBorder;
            }
        }
        if (null != cellBorder) {
            return cellBorder;
        } else {
            return Border.NO_BORDER;
        }
    }

    public static List<Border> getCollapsedList(List<Border> innerList, List<Border> outerList) {
        int size = Math.min(null == innerList ? 0 : innerList.size(), null == outerList ? 0 : outerList.size());
        List<Border> collapsedList = new ArrayList<Border>(size);
        for (int i = 0; i < size; i++) {
            collapsedList.add(getCollapsedBorder(innerList.get(i), outerList.get(i)));
        }
        return collapsedList;
    }
    // endregion

    // region occupation

    protected TableBorders applyLeftAndRightTableBorder(Rectangle layoutBox, boolean reverse) {
        if (null != layoutBox) {
            layoutBox.applyMargins(0, rightBorderMaxWidth / 2, 0, leftBorderMaxWidth / 2, reverse);
        }

        return this;
    }

    protected TableBorders applyTopTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean force, boolean reverse) {
        if (!isEmpty) {
            return applyTopTableBorder(occupiedBox, layoutBox, reverse);
        } else if (force) {
            // process empty table
            applyTopTableBorder(occupiedBox, layoutBox, reverse);
            return applyTopTableBorder(occupiedBox, layoutBox, reverse);
        }
        return this;
    }

    protected TableBorders applyBottomTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean force, boolean reverse) {
        if (!isEmpty) {
            return applyBottomTableBorder(occupiedBox, layoutBox, reverse);
        } else if (force) {
            // process empty table
            applyBottomTableBorder(occupiedBox, layoutBox, reverse);
            return applyBottomTableBorder(occupiedBox, layoutBox, reverse);
        }
        return this;
    }

    protected TableBorders applyTopTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean reverse) {
        float topIndent = (reverse ? -1 : 1) * getMaxTopWidth();
        layoutBox.decreaseHeight(topIndent / 2);
        occupiedBox.moveDown(topIndent / 2).increaseHeight(topIndent / 2);
        return this;
    }

    protected TableBorders applyBottomTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean reverse) {
        float bottomTableBorderWidth = (reverse ? -1 : 1) * getMaxBottomWidth();
        layoutBox.decreaseHeight(bottomTableBorderWidth / 2);
        occupiedBox.moveDown(bottomTableBorderWidth / 2).increaseHeight(bottomTableBorderWidth / 2);
        return this;
    }


    protected TableBorders applyCellIndents(Rectangle box, float topIndent, float rightIndent, float bottomIndent, float leftIndent, boolean reverse) {
        box.applyMargins(topIndent / 2, rightIndent / 2, bottomIndent / 2, leftIndent / 2, false);
        return this;
    }

    protected float getCellVerticalAddition(float[] indents) {
        return indents[0] / 2 + indents[2] / 2;
    }
    // endregion

    // region update, footer/header
    protected TableBorders updateBordersOnNewPage(boolean isOriginalNonSplitRenderer, boolean isFooterOrHeader, TableRenderer currentRenderer, TableRenderer headerRenderer, TableRenderer footerRenderer) {
        if (!isFooterOrHeader) {
            // collapse all cell borders
            if (isOriginalNonSplitRenderer) {
                if (null != rows) {
                    processAllBordersAndEmptyRows();
                    rightBorderMaxWidth = getMaxRightWidth();
                    leftBorderMaxWidth = getMaxLeftWidth();
                }
                // in case of large table and no content (Table#complete is called right after Table#flush)
                setTopBorderCollapseWith(((Table) currentRenderer.getModelElement()).getLastRowBottomBorder());
            } else {
                setTopBorderCollapseWith(null);
                setBottomBorderCollapseWith(null, null);
            }
        }
        if (null != footerRenderer) {
            float rightFooterBorderWidth = footerRenderer.bordersHandler.getMaxRightWidth();
            float leftFooterBorderWidth = footerRenderer.bordersHandler.getMaxLeftWidth();

            leftBorderMaxWidth = Math.max(leftBorderMaxWidth, leftFooterBorderWidth);
            rightBorderMaxWidth = Math.max(rightBorderMaxWidth, rightFooterBorderWidth);
        }

        if (null != headerRenderer) {
            float rightHeaderBorderWidth = headerRenderer.bordersHandler.getMaxRightWidth();
            float leftHeaderBorderWidth = headerRenderer.bordersHandler.getMaxLeftWidth();

            leftBorderMaxWidth = Math.max(leftBorderMaxWidth, leftHeaderBorderWidth);
            rightBorderMaxWidth = Math.max(rightBorderMaxWidth, rightHeaderBorderWidth);
        }

        return this;
    }

    protected TableBorders skipFooter(Border[] borders) {
        setTableBoundingBorders(borders);
        setBottomBorderCollapseWith(null, null);
        return this;
    }

    protected TableBorders skipHeader(Border[] borders) {
        setTableBoundingBorders(borders);
        setTopBorderCollapseWith(null);
        return this;
    }

    protected TableBorders collapseTableWithFooter(TableBorders footerBordersHandler, boolean hasContent) {
        ((CollapsedTableBorders) footerBordersHandler).setTopBorderCollapseWith(
                hasContent ? getLastHorizontalBorder() : getTopBorderCollapseWith());
        setBottomBorderCollapseWith(footerBordersHandler.getHorizontalBorder(0),
                ((CollapsedTableBorders) footerBordersHandler).getVerticalBordersCrossingTopHorizontalBorder());
        return this;
    }

    protected TableBorders collapseTableWithHeader(TableBorders headerBordersHandler, boolean updateBordersHandler) {
        ((CollapsedTableBorders) headerBordersHandler).setBottomBorderCollapseWith(getHorizontalBorder(startRow),
                getVerticalBordersCrossingTopHorizontalBorder());
        if (updateBordersHandler) {
            setTopBorderCollapseWith(
                    headerBordersHandler.getLastHorizontalBorder());
        }
        return this;
    }

    protected TableBorders fixHeaderOccupiedArea(Rectangle occupiedBox, Rectangle layoutBox) {
        float topBorderMaxWidth = getMaxTopWidth();
        layoutBox.increaseHeight(topBorderMaxWidth);
        occupiedBox.moveUp(topBorderMaxWidth).decreaseHeight(topBorderMaxWidth);
        return this;
    }
    // endregion

    /**
     * Returns the {@link Border} instances, which intersect in the specified point.
     *
     * <p>
     * The order of the borders: first the left one, then the top, the right and the bottom ones.
     *
     * @param horizontalIndex index of horizontal border
     * @param verticalIndex   index of vertical border
     * @return a list of {@link Border} instances, which intersect in the specified point
     */
    List<Border> getCrossingBorders(int horizontalIndex, int verticalIndex) {
        List<Border> horizontalBorder = getHorizontalBorder(startRow + horizontalIndex);
        List<Border> verticalBorder = getVerticalBorder(verticalIndex);

        List<Border> crossingBorders = new ArrayList<>(4);
        crossingBorders.add(verticalIndex > 0 ? horizontalBorder.get(verticalIndex - 1) : null);
        crossingBorders.add(horizontalIndex > 0
                ? verticalBorder.get(startRow - largeTableIndexOffset + horizontalIndex - 1) : null);
        crossingBorders.add(verticalIndex < numberOfColumns ? horizontalBorder.get(verticalIndex) : null);
        crossingBorders.add(horizontalIndex <= finishRow - startRow
                ? verticalBorder.get(startRow - largeTableIndexOffset + horizontalIndex) : null);

        // In case the last horizontal border on the page is specified,
        // we need to consider a vertical border of the table's bottom part
        // (f.e., for header it is table's body).
        if (horizontalIndex == finishRow - startRow + 1 && null != verticalBottomBorderCollapseWith) {
            if (isBorderWider(verticalBottomBorderCollapseWith.get(verticalIndex), crossingBorders.get(3))) {
                crossingBorders.set(3, verticalBottomBorderCollapseWith.get(verticalIndex));
            }
        }
        return crossingBorders;
    }

    /**
     * A comparison function to compare two {@link Border} instances.
     */
    private static class BorderComparator implements Comparator<Border> {

        @Override
        /**
         * Compares its two {@link Border} instances for order. Returns a negative integer,
         *      zero, or a positive integer as the first argument is wider than, of equal width,
         *      or more narrow than the second.
         */
        public int compare(Border o1, Border o2) {
            if (o1 == o2) {
                return 0;
            } else if (null == o1) {
                return 1;
            } else if (null == o2) {
                return -1;
            } else {
                return Float.compare(o2.getWidth(), o1.getWidth());
            }
        }
    }

    /**
     * Gets the width of the widest border in the specified list.
     *
     * @param borders the borders which widths should be considered
     * @return the width of the widest border in the specified list
     */
    private float getWidestBorderWidth(Border... borders) {
        float maxWidth = 0;
        for (Border border : borders) {
            if (null != border && maxWidth < border.getWidth()) {
                maxWidth = border.getWidth();
            }
        }
        return maxWidth;
    }

    /**
     * Compares borders and defines whether this border is wider than the other.
     *
     * <p>
     * Note that by default the comparison will be strict, e.g. if this border
     * is of the same width as the other border, then false will be returned.
     *
     * @param thisBorder  this border
     * @param otherBorder the other border to be compared with
     * @return whether this border is wider than the other
     */
    private static boolean isBorderWider(Border thisBorder, Border otherBorder) {
        return isBorderWider(thisBorder, otherBorder, true);
    }

    /**
     * Compares borders and defines whether this border is wider than the other.
     *
     * @param thisBorder  this border
     * @param otherBorder the other border to be compared with
     * @param strict      if true, then in case this border is of the same width as the other border,
     *                    true will be returned. If false, it will be checked whether the width
     *                    of this border is strictly greater than the other border's width
     * @return whether this border is wider than the other
     */
    private static boolean isBorderWider(Border thisBorder, Border otherBorder, boolean strict) {
        if (null == thisBorder) {
            return false;
        }
        if (null == otherBorder) {
            return true;
        }
        int comparisonResult = Float.compare(thisBorder.getWidth(), otherBorder.getWidth());
        return strict ? comparisonResult > 0 : comparisonResult >= 0;
    }
}
