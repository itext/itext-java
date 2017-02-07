package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TableBorders {
    protected List<List<Border>> horizontalBorders;
    protected List<List<Border>> verticalBorders;

    protected Border[] tableBoundingBorders = null;
    protected Border[] headerBoundingBorders = null;
    protected Border[] footerBoundingBorders = null;

    protected List<CellRenderer[]> rows;

    protected int numberOfColumns;

    protected int horizontalBordersIndexOffset = 0;
    protected int verticalBordersIndexOffset = 0;

//    protected LayoutResult[] splits;
//
//    protected boolean hasContent;
//
//    protected boolean cellWithBigRowspanAdded;
//
//    protected int splitRow;

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns) {
        this.rows = rows;
        this.numberOfColumns = numberOfColumns;
    }


    // region collapse methods

    protected TableBorders collapseAllBordersAndEmptyRows(List<CellRenderer[]> rows, Border[] tableBorders, int startRow, int finishRow, int colN) {
        CellRenderer[] currentRow;
        int[] rowsToDelete = new int[colN];
        for (int row = startRow; row <= finishRow; row++) {
            currentRow = rows.get(row);
            boolean hasCells = false;
            for (int col = 0; col < colN; col++) {
                if (null != currentRow[col]) {
                    int colspan = (int) currentRow[col].getPropertyAsInteger(Property.COLSPAN);
                    prepareBuildingBordersArrays(currentRow[col], tableBorders, colN, row, col);
                    buildBordersArrays(currentRow[col], row, col);
                    hasCells = true;
                    if (rowsToDelete[col] > 0) {
                        int rowspan = (int) currentRow[col].getPropertyAsInteger(Property.ROWSPAN) - rowsToDelete[col];
                        if (rowspan < 1) {
                            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                            logger.warn(LogMessageConstant.UNEXPECTED_BEHAVIOUR_DURING_TABLE_ROW_COLLAPSING);
                            rowspan = 1;
                        }
                        currentRow[col].setProperty(Property.ROWSPAN, rowspan);
                    }
                    for (int i = 0; i < colspan; i++) {
                        rowsToDelete[col + i] = 0;
                    }
                    col += colspan - 1;
                } else {
                    if (horizontalBorders.get(row).size() <= col) {
                        horizontalBorders.get(row).add(null);
                    }
                }
            }
            if (!hasCells) {
                rows.remove(currentRow);
                row--;
                finishRow--;
                for (int i = 0; i < colN; i++) {
                    rowsToDelete[i]++;
                }
                if (row == finishRow) {
                    Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                    logger.warn(LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE);
                }
            }
        }
        return this;
    }

    protected TableBorders correctTopBorder() {
        return correctTopBorder(null);
    }

    protected TableBorders correctTopBorder(TableBorders headerTableBorders) {
        List<Border> topBorders;
        if (null != headerTableBorders) {
            topBorders = headerTableBorders.horizontalBorders.get(headerTableBorders.horizontalBorders.size() - 1);
        } else {
            topBorders = new ArrayList<Border>();
            for (int col = 0; col < numberOfColumns; col++) {
                topBorders.add(tableBoundingBorders[0]);
            }
        }

        for (int col = 0; col < numberOfColumns; col++) {
            topBorders.set(col, getCollapsedBorder(horizontalBorders.get(horizontalBordersIndexOffset).get(col), topBorders.get(col)));
        }
        updateTopBorder(topBorders, new boolean[numberOfColumns]);
        if (null != headerTableBorders) {
            headerTableBorders.updateBottomBorder(horizontalBorders.get(horizontalBordersIndexOffset), new boolean[numberOfColumns]);
        }
        return this;
    }

    protected TableBorders processEmptyTable(List<Border> lastFlushedBorder) {
        List<Border> topHorizontalBorders = new ArrayList<Border>();
        List<Border> bottomHorizontalBorders = new ArrayList<Border>();
        if (null != lastFlushedBorder && 0 != lastFlushedBorder.size()) {
            bottomHorizontalBorders = lastFlushedBorder;
        } else {
            for (int i = 0; i < numberOfColumns; i++) {
                bottomHorizontalBorders.add(Border.NO_BORDER);
            }
        }
        List<Border> leftVerticalBorders = new ArrayList<Border>();
        List<Border> rightVerticalBorders = new ArrayList<Border>();

        // collapse with table bottom border
        for (int i = 0; i < bottomHorizontalBorders.size(); i++) {
            Border border = bottomHorizontalBorders.get(i);
            if (null == border || (null != tableBoundingBorders[2] && border.getWidth() < tableBoundingBorders[2].getWidth())) {
                bottomHorizontalBorders.set(i, tableBoundingBorders[2]);
            }
            topHorizontalBorders.add(tableBoundingBorders[0]);
        }
        horizontalBorders.set(0, topHorizontalBorders);
        horizontalBorders.add(bottomHorizontalBorders);
        leftVerticalBorders.add(tableBoundingBorders[3]);
        rightVerticalBorders.add(tableBoundingBorders[1]);
        verticalBorders = new ArrayList<>();
        verticalBorders.add(leftVerticalBorders);
        for (int i = 0; i < numberOfColumns - 1; i++) {
            verticalBorders.add(new ArrayList<Border>());
        }
        verticalBorders.add(rightVerticalBorders);

        return this;
    }

    protected TableBorders processSplit(int splitRow, boolean split, boolean hasContent, boolean cellWithBigRowspanAdded) {
        return processSplit(splitRow, split, hasContent, cellWithBigRowspanAdded, null);
    }

    protected TableBorders processSplit(int splitRow, boolean split, boolean hasContent, boolean cellWithBigRowspanAdded, TableBorders footerTableBorders) {
        CellRenderer[] currentRow = rows.get(splitRow);
        CellRenderer[] lastRowOnCurrentPage = new CellRenderer[numberOfColumns];
        CellRenderer[] firstRowOnTheNextPage = new CellRenderer[numberOfColumns];

        int curPageIndex = 0;
        int nextPageIndex = 0;
        int row;
        for (int col = 0; col < numberOfColumns; col++) {
            if (hasContent || (cellWithBigRowspanAdded && null == rows.get(splitRow - 1)[col])) {
                if (null != currentRow[col]) {
                    if (0 >= curPageIndex) {
                        lastRowOnCurrentPage[col] = currentRow[col];
                        curPageIndex = lastRowOnCurrentPage[col].getPropertyAsInteger(Property.COLSPAN);
                    }
                    if (0 >= nextPageIndex) {
                        firstRowOnTheNextPage[col] = currentRow[col];
                        ;
                        nextPageIndex = firstRowOnTheNextPage[col].getPropertyAsInteger(Property.COLSPAN);
                    }
                }
            } else {
                if (0 >= curPageIndex) {
                    lastRowOnCurrentPage[col] = rows.get(splitRow - 1)[col];
                    curPageIndex = lastRowOnCurrentPage[col].getPropertyAsInteger(Property.COLSPAN);
                }
                if (0 >= nextPageIndex) {
                    row = splitRow;
                    while (row < rows.size() && null == rows.get(row)[col]) {
                        row++;
                    }
                    if (row == rows.size()) {
                        nextPageIndex = 1;
                    } else {
                        firstRowOnTheNextPage[col] = rows.get(row)[col];
                        nextPageIndex = firstRowOnTheNextPage[col].getPropertyAsInteger(Property.COLSPAN);
                    }
                }
            }
            curPageIndex--;
            nextPageIndex--;
        }
        //verticalBordersIndexOffset += splitRow;
        // splitRow += horizontalBordersIndexOffset;
        if (hasContent) {
            if (split) {
                addNewHorizontalBorder(horizontalBordersIndexOffset + splitRow + 1, true); // the last row on current page
                addNewVerticalBorder(verticalBordersIndexOffset + splitRow, true);
                //verticalBordersIndexOffset++;
            }
            splitRow++;
        }
        if (split) {
            addNewHorizontalBorder(horizontalBordersIndexOffset + splitRow + 1, false); // the first row on the next page
        }

        // here splitRow is the last horizontal border index on current page
        // and splitRow + 1 is the first horizontal border index on the next page

        List<Border> lastBorderOnCurrentPage = horizontalBorders.get(horizontalBordersIndexOffset + splitRow);

        for (int col = 0; col < numberOfColumns; col++) {
            if (null != lastRowOnCurrentPage[col]) {
                CellRenderer cell = lastRowOnCurrentPage[col];

                Border cellModelBottomBorder = getCellSideBorder(cell.getModelElement(), Property.BORDER_BOTTOM);
                Border cellCollapsedBottomBorder = getCollapsedBorder(cellModelBottomBorder, tableBoundingBorders[2]);

                // fix the last border on the page
                for (int i = col; i < col + cell.getPropertyAsInteger(Property.COLSPAN); i++) {
                    lastBorderOnCurrentPage.set(i, cellCollapsedBottomBorder);
                }

                col += lastRowOnCurrentPage[col].getPropertyAsInteger(Property.COLSPAN) - 1;
            }
        }


        if (horizontalBordersIndexOffset + splitRow != horizontalBorders.size() - 1) {
            List<Border> firstBorderOnTheNextPage = horizontalBorders.get(horizontalBordersIndexOffset + splitRow + 1);

            for (int col = 0; col < numberOfColumns; col++) {
                if (null != firstRowOnTheNextPage[col]) {
                    CellRenderer cell = firstRowOnTheNextPage[col];
                    Border cellModelTopBorder = getCellSideBorder(cell.getModelElement(), Property.BORDER_TOP);
                    Border cellCollapsedTopBorder = getCollapsedBorder(cellModelTopBorder, tableBoundingBorders[0]);

                    // fix the last border on the page
                    for (int i = col; i < col + cell.getPropertyAsInteger(Property.COLSPAN); i++) {
                        firstBorderOnTheNextPage.set(i, cellCollapsedTopBorder);
                    }
                    col += lastRowOnCurrentPage[col].getPropertyAsInteger(Property.COLSPAN) - 1;
                }
            }
        }

        // update row offest
        if (split) {
            horizontalBordersIndexOffset += splitRow + 1;
            verticalBordersIndexOffset += splitRow;
        }

        return this;
    }

    // important to invoke on each new page
    private void updateFirstRowBorders(int colN) {
        int col = 0;
        int row = 0;
        List<Border> topBorders = horizontalBorders.get(0);
        topBorders.clear();
        while (col < colN) {
            if (null != rows.get(row)[col]) {
                // we may have deleted collapsed border property trying to process the row as last on the page
                Border collapsedBottomBorder = null;
                int colspan = (int) rows.get(row)[col].getPropertyAsInteger(Property.COLSPAN);
                for (int i = col; i < col + colspan; i++) {
                    topBorders.add(rows.get(row)[col].getBorders()[0]);
                    collapsedBottomBorder = getCollapsedBorder(collapsedBottomBorder, horizontalBorders.get(row + 1).get(i));
                }
                rows.get(row)[col].setBorders(collapsedBottomBorder, 2);
                col += colspan;
                row = 0;
            } else {
                if (0 == row) {
                    horizontalBorders.get(1).set(col, Border.NO_BORDER);
                }
                row++;
                if (row == rows.size()) {
                    break;
                }
            }
        }
    }

    // collapse with table border or header bottom borders
    protected void correctFirstRowTopBorders(Border tableBorder, int colN) {
//        int col = 0;
//        int row = 0;
//        List<Border> topBorders = horizontalBorders.get(0);
//        List<Border> bordersToBeCollapsedWith = null != headerRenderer
//                ? headerRenderer.horizontalBorders.get(headerRenderer.horizontalBorders.size() - 1)
//                : new ArrayList<Border>();
//        if (null == headerRenderer) {
//            for (col = 0; col < colN; col++) {
//                bordersToBeCollapsedWith.add(tableBorder);
//            }
//        }
//        col = 0;
//        while (col < colN) {
//            if (null != rows.get(row)[col] && row + 1 == (int) rows.get(row)[col].getPropertyAsInteger(Property.ROWSPAN)) {
//                Border oldTopBorder = rows.get(row)[col].getBorders()[0];
//                Border resultCellTopBorder = null;
//                Border collapsedBorder = null;
//                int colspan = (int) rows.get(row)[col].getPropertyAsInteger(Property.COLSPAN);
//                for (int i = col; i < col + colspan; i++) {
//                    collapsedBorder = getCollapsedBorder(oldTopBorder, bordersToBeCollapsedWith.get(i));
//                    if (null == topBorders.get(i) || (null != collapsedBorder && topBorders.get(i).getWidth() < collapsedBorder.getWidth())) {
//                        topBorders.set(i, collapsedBorder);
//                    }
//                    if (null == resultCellTopBorder || (null != collapsedBorder && resultCellTopBorder.getWidth() < collapsedBorder.getWidth())) {
//                        resultCellTopBorder = collapsedBorder;
//                    }
//                }
//                rows.get(row)[col].setBorders(resultCellTopBorder, 0);
//                col += colspan;
//                row = 0;
//            } else {
//                row++;
//                if (row == rows.size()) {
//                    break;
//                }
//            }
//        }
//        if (null != headerRenderer) {
//            headerRenderer.horizontalBorders.set(headerRenderer.horizontalBorders.size() - 1, topBorders);
//        }
    }

    // endregion

    // region intialisers
    protected void initializeBorders(List<Border> lastFlushedRowBottomBorder, boolean isFirstOnPage) {
        // initialize borders
        if (null == horizontalBorders) {
            horizontalBorders = new ArrayList<>();
            horizontalBorders.add(new ArrayList<Border>(lastFlushedRowBottomBorder));
            verticalBorders = new ArrayList<>();
        }
        // The first row on the page shouldn't collapse with the last on the previous one
        if (0 != lastFlushedRowBottomBorder.size() && isFirstOnPage) {
            horizontalBorders.get(0).clear();
        }
    }

    //endregion

    // region getters

    protected Border getWidestHorizontalBorder(int row) {
        Border theWidestBorder = null;
        if (row >= 0 && row < horizontalBorders.size()) {
            theWidestBorder = getWidestBorder(horizontalBorders.get(row));
        }
        return theWidestBorder;
    }

    protected Border getWidestVerticalBorder(int col) {
        Border theWidestBorder = null;
        if (col >= 0 && col < verticalBorders.size()) {
            theWidestBorder = getWidestBorder(verticalBorders.get(col));
        }
        return theWidestBorder;
    }

    protected float getMaxTopWidth(Border tableBorder) {
        float width = null == tableBorder ? 0 : tableBorder.getWidth();
        Border widestBorder = getWidestHorizontalBorder(horizontalBordersIndexOffset);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    protected float getMaxRightWidth(Border tableBorder) {
        float width = null == tableBorder ? 0 : tableBorder.getWidth();
        Border widestBorder = getWidestVerticalBorder(verticalBorders.size() - 1);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    protected float getMaxLeftWidth(Border tableBorder) {
        float width = null == tableBorder ? 0 : tableBorder.getWidth();
        Border widestBorder = getWidestVerticalBorder(0);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    // TODO Do not use it =)
    protected float getMaxBottomWidth(Border tableBorder) {
        float width = null == tableBorder ? 0 : tableBorder.getWidth();
        Border widestBorder = getWidestHorizontalBorder(horizontalBorders.size() - 1);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }


    protected int getCurrentHorizontalBordersIndexOffset() {
        return horizontalBordersIndexOffset;
    }

    protected int getCurrentVerticalBordersIndexOffset() {
        return verticalBordersIndexOffset;
    }


//    protected List<List<Border>> getRendererHorizontalBorders(int startIndex, int numOfRows) {
//        return horizontalBorders.subList(startIndex, startIndex + numOfRows + 1);
//    }
//
//    protected List<List<Border>> getRendererVerticalBorders(int startIndex, int numOfRows) {
//        // return horizontalBorders.subList(startIndex, startIndex + numOfRows + 1);
//    }

    // endregion

    // region setters

    protected TableBorders setRows(List<CellRenderer[]> rows) {
        this.rows = rows;
        return this;
    }

    protected TableBorders setTableBoundingBorders(Border[] borders) {
        if (null == tableBoundingBorders) {
            tableBoundingBorders = new Border[borders.length];
        }
        for (int i = 0; i < borders.length; i++) {
            tableBoundingBorders[i] = borders[i];
        }
        return this;
    }

    protected TableBorders setHeaderBoundingBorders(Border[] borders) {
        if (null == headerBoundingBorders) {
            headerBoundingBorders = new Border[borders.length];
        }
        for (int i = 0; i < borders.length; i++) {
            headerBoundingBorders[i] = borders[i];
        }
        return this;
    }

    protected TableBorders setFooterBoundingBorders(Border[] borders) {
        if (null == footerBoundingBorders) {
            footerBoundingBorders = new Border[borders.length];
        }
        for (int i = 0; i < borders.length; i++) {
            footerBoundingBorders[i] = borders[i];
        }
        return this;
    }


    //endregion

    //region building border arrays

    protected void prepareBuildingBordersArrays(CellRenderer cell, Border[] tableBorders, int colNum, int row, int col) {
        Border[] cellBorders = cell.getBorders();
        int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
        if (0 == col) {
            cell.setProperty(Property.BORDER_LEFT, getCollapsedBorder(cellBorders[3], tableBorders[3]));
        }
        if (colNum == col + colspan) {
            cell.setProperty(Property.BORDER_RIGHT, getCollapsedBorder(cellBorders[1], tableBorders[1]));
        }
    }

    protected void buildBordersArrays(CellRenderer cell, int row, int col) {
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
                    (j + rows.get(nextCellRow)[j].getPropertyAsInteger(Property.COLSPAN) != col ||
                            nextCellRow - rows.get(nextCellRow)[j].getPropertyAsInteger(Property.ROWSPAN) + 1 != row));
            if (j >= 0 && nextCellRow != rows.size()) {
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
            if (row == nextCellRow - nextCell.getPropertyAsInteger(Property.ROWSPAN)) {
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
        int colN = cell.getModelElement().getCol();
        Border[] cellBorders = cell.getBorders();

        // cell with big rowspan was splitted
        if (row + 1 - rowspan < 0) {
            rowspan = row + 1;
        }

        // consider top border
        for (int i = 0; i < colspan; i++) {
            if (!checkAndReplaceBorderInArray(horizontalBorders, row + 1 - rowspan, colN + i, cellBorders[0], false) && !isNeighbourCell) {
                cell.setBorders(horizontalBorders.get(row + 1 - rowspan).get(colN + i), 0);
            }
        }
        // consider bottom border
        for (int i = 0; i < colspan; i++) {
            if (!checkAndReplaceBorderInArray(horizontalBorders, row + 1, colN + i, cellBorders[2], true) && !isNeighbourCell) {
                cell.setBorders(horizontalBorders.get(row + 1).get(colN + i), 2);
            }
        }
        // process big rowspan
        if (rowspan > 1) {
            int numOfColumns = numberOfColumns;
            for (int k = row - rowspan + 1; k <= row; k++) {
                List<Border> borders = horizontalBorders.get(k);
                if (borders.size() < numOfColumns) {
                    for (int j = borders.size(); j < numOfColumns; j++) {
                        borders.add(null);
                    }
                }
            }
        }
        // consider left border
        for (int j = row - rowspan + 1; j <= row; j++) {
            if (!checkAndReplaceBorderInArray(verticalBorders, colN, j, cellBorders[3], false) && !isNeighbourCell) {
                cell.setBorders(verticalBorders.get(colN).get(j), 3);
            }
        }
        // consider right border
        for (int i = row - rowspan + 1; i <= row; i++) {
            if (!checkAndReplaceBorderInArray(verticalBorders, colN + colspan, i, cellBorders[1], true) && !isNeighbourCell) {
                cell.setBorders(verticalBorders.get(colN + colspan).get(i), 1);
            }
        }
        // process big colspan
        if (colspan > 1) {
            for (int k = colN; k <= colspan + colN; k++) {
                List<Border> borders = verticalBorders.get(k);
                if (borders.size() < row + rowspan) {
                    for (int l = borders.size(); l < row + rowspan; l++) {
                        borders.add(null);
                    }
                }
            }
        }
    }

    //endregion

    //region static methods

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

    private static Border getCellSideBorder(Cell cellModel, int borderType) {
        Border cellModelSideBorder = cellModel.getProperty(borderType);
        if (null == cellModelSideBorder && !cellModel.hasProperty(borderType)) {
            cellModelSideBorder = cellModel.getProperty(Property.BORDER);
            if (null == cellModelSideBorder && !cellModel.hasProperty(Property.BORDER)) {
//                cellModelSideBorder = cellModel.getDefaultProperty(borderType); // TODO
//                if (null == cellModelSideBorder && !cellModel.hasDefaultProperty(borderType)) {
                cellModelSideBorder = cellModel.getDefaultProperty(Property.BORDER);
//                }
            }
        }
        return cellModelSideBorder;
    }

    private static Border getWidestBorder(List<Border> borderList) {
        Border theWidestBorder = null;
        if (0 != borderList.size()) {
            for (Border border : borderList) {
                if (null != border && (null == theWidestBorder || border.getWidth() > theWidestBorder.getWidth())) {
                    theWidestBorder = border;
                }
            }
        }
        return theWidestBorder;
    }

    // endregion

    // region lowlevel logic
    protected boolean checkAndReplaceBorderInArray(List<List<Border>> borderArray, int i, int j, Border borderToAdd, boolean hasPriority) {
        if (borderArray.size() <= i) {
            for (int count = borderArray.size(); count <= i; count++) {
                borderArray.add(new ArrayList<Border>());
            }
        }
        List<Border> borders = borderArray.get(i);
        if (borders.isEmpty()) {
            for (int count = 0; count < j; count++) {
                borders.add(null);
            }
            borders.add(borderToAdd);
            return true;
        }
        if (borders.size() == j) {
            borders.add(borderToAdd);
            return true;
        }
        if (borders.size() < j) {
            for (int count = borders.size(); count <= j; count++) {
                borders.add(count, null);
            }
        }
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

    // TODO
    protected TableBorders addNewHorizontalBorder(int index, boolean usePrevious) {
        List<Border> newBorder;
        if (usePrevious) {
            newBorder = (List<Border>) ((ArrayList<Border>) horizontalBorders.get(index)).clone();
        } else {
            newBorder = new ArrayList<Border>();
            for (int i = 0; i < numberOfColumns; i++) {
                newBorder.add(Border.NO_BORDER);
            }
        }
        horizontalBorders.add(index, newBorder);
        return this;
    }

    // TODO
    protected TableBorders addNewVerticalBorder(int index, boolean usePrevious) {
        for (int i = 0; i < numberOfColumns + 1; i++) {
            verticalBorders.get(i).add(index, usePrevious ? verticalBorders.get(i).get(index) : Border.NO_BORDER);
        }
        return this;
    }

    // endregion

    // region footer collapsing methods


    protected TableBorders updateTopBorder(List<Border> newBorder, boolean[] useOldBorders) {
        updateBorder(horizontalBorders.get(horizontalBordersIndexOffset), newBorder, useOldBorders);
        return this;
    }

    protected TableBorders updateBottomBorder(List<Border> newBorder, boolean[] useOldBorders) {
        updateBorder(horizontalBorders.get(horizontalBorders.size() - 1), newBorder, useOldBorders);
        return this;
    }

    protected TableBorders updateBorder(List<Border> oldBorder, List<Border> newBorders, boolean[] isOldBorder) {
        for (int i = 0; i < oldBorder.size(); i++) {
            if (!isOldBorder[i]) {
                oldBorder.set(i, newBorders.get(i));
            }
        }
        return this;
    }


// endregion

}
