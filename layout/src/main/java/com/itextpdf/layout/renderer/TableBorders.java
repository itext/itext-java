package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TableBorders {
    private List<List<Border>> horizontalBorders;
    private List<List<Border>> verticalBorders;

    private final int numberOfColumns;

    private Border[] tableBoundingBorders;

    private List<CellRenderer[]> rows;

    private Table.RowRange rowRange;

    private List<Border> topBorderCollapseWith;
    private List<Border> bottomBorderCollapseWith;


    // region constructors

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns) {
        this.rows = rows;
        this.numberOfColumns = numberOfColumns;
        verticalBorders = new ArrayList<>();
        horizontalBorders = new ArrayList<>();
        tableBoundingBorders = null;
        topBorderCollapseWith = new ArrayList<Border>();
        bottomBorderCollapseWith = new ArrayList<Border>();
    }

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders) {
        this(rows, numberOfColumns);
        setTableBoundingBorders(tableBoundingBorders);
    }

    // endregion

    // region collapsing and correction

    protected TableBorders collapseAllBordersAndEmptyRows(List<CellRenderer[]> rows, Border[] tableBorders, int startRow, int finishRow) {
        CellRenderer[] currentRow;
        int[] rowsToDelete = new int[numberOfColumns];
        for (int row = startRow; row <= finishRow; row++) {
            currentRow = rows.get(row);
            boolean hasCells = false;
            for (int col = 0; col < numberOfColumns; col++) {
                if (null != currentRow[col]) {
                    int colspan = (int) currentRow[col].getPropertyAsInteger(Property.COLSPAN);
                    prepareBuildingBordersArrays(currentRow[col], tableBorders, numberOfColumns, row, col);
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
                for (int i = 0; i < numberOfColumns; i++) {
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

//    protected TableBorders processEmptyTable(List<Border> lastFlushedBorder) { // FIXNE
////        List<Border> topHorizontalBorders = new ArrayList<Border>();
////        List<Border> bottomHorizontalBorders = new ArrayList<Border>();
////        if (null != lastFlushedBorder && 0 != lastFlushedBorder.size()) {
////            topHorizontalBorders = lastFlushedBorder;
////        } else {
////            for (int i = 0; i < numberOfColumns; i++) {
////                topHorizontalBorders.add(null);
////            }
////        }
////
////        // collapse with table bottom border
////        for (int i = 0; i < topHorizontalBorders.size(); i++) {
////            Border border = topHorizontalBorders.get(i);
////            if (null == border || (null != tableBoundingBorders[0] && border.getWidth() < tableBoundingBorders[0].getWidth())) {
////                topHorizontalBorders.set(i, tableBoundingBorders[0]);
////            }
////            bottomHorizontalBorders.add(tableBoundingBorders[2]);
////        }
////        horizontalBorders.set(horizontalBordersIndexOffset, topHorizontalBorders);
////        if (horizontalBorders.size() == horizontalBordersIndexOffset + 1) {
////            horizontalBorders.add(bottomHorizontalBorders);
////        } else {
////            horizontalBorders.set(horizontalBordersIndexOffset + 1, bottomHorizontalBorders);
////        }
////
////        if (0 != verticalBorders.size()) {
////            verticalBorders.get(0).set(verticalBordersIndexOffset, (tableBoundingBorders[3]));
////            for (int i = 1; i < numberOfColumns; i++) {
////                verticalBorders.get(i).set(verticalBordersIndexOffset, null);
////            }
////            verticalBorders.get(verticalBorders.size() - 1).set(verticalBordersIndexOffset, (tableBoundingBorders[1]));
////        } else {
////            List<Border> tempBorders;
////            for (int i = 0; i < numberOfColumns + 1; i++) {
////                tempBorders = new ArrayList<Border>();
////                tempBorders.add(null);
////                verticalBorders.add(tempBorders);
////            }
////            verticalBorders.get(0).set(0, tableBoundingBorders[3]);
////            verticalBorders.get(numberOfColumns).set(0, tableBoundingBorders[1]);
////
////        }
////
//        return this;
//    }

    // endregion

    // region intializers

    protected void initializeBorders(List<Border> lastFlushedRowBottomBorder, boolean isFirstOnPage) {
        List<Border> tempBorders;
        // initialize vertical borders
        if (0 != rows.size()) {
            while (numberOfColumns + 1 > verticalBorders.size()) {
                tempBorders = new ArrayList<Border>();
                while (rows.size() > tempBorders.size()) {
                    tempBorders.add(null);
                }
                verticalBorders.add(tempBorders);
            }
        }
        // initialize horizontal borders
        while (rows.size() + 1 > horizontalBorders.size()) {
            tempBorders = new ArrayList<Border>();
            while (numberOfColumns > tempBorders.size()) {
                tempBorders.add(null);
            }
            horizontalBorders.add(tempBorders);
        }
        // Notice that the first row on the page shouldn't collapse with the last on the previous one
        if (null != lastFlushedRowBottomBorder && 0 < lastFlushedRowBottomBorder.size() && !isFirstOnPage) { // TODO
            tempBorders = new ArrayList<Border>();
            for (Border border : lastFlushedRowBottomBorder) {
                tempBorders.add(border);
            }
            horizontalBorders.set(0, tempBorders);
        }
    }

    //endregion

    // region getters

    public float[] getCellBorderIndents(int row, int col, int rowspan, int colspan, boolean forceNotToProcessAsLast) {
        float[] indents = new float[4];
        List<Border> borderList;
        Border border;
        // process top border
        borderList = getHorizontalBorder(rowRange.getStartRow() + row - rowspan + 1);
        for (int i = col; i < col+colspan; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[0]) {
                indents[0] = border.getWidth();
            }
        }
        // process right border
        borderList = getVerticalBorder(col + colspan);
        for (int i = rowRange.getStartRow() + row - rowspan + 1; i < rowRange.getStartRow() + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[1]) {
                indents[1] = border.getWidth();
            }
        }
        // process bottom border
        borderList = getHorizontalBorder(rowRange.getStartRow() + row + 1, false, forceNotToProcessAsLast);
        for (int i = col; i < col+colspan; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[2]) {
                indents[2] = border.getWidth();
            }
        }
        // process left border
        borderList = getVerticalBorder(col);
        for (int i = rowRange.getStartRow() + row - rowspan + 1; i < rowRange.getStartRow() + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[3]) {
                indents[3] = border.getWidth();
            }
        }
        return indents;
    }


    public float[] getCellBorderIndents(int row, int col, int rowspan, int colspan) {
        return getCellBorderIndents(row, col, rowspan, colspan, false);
    }

    public Border getWidestHorizontalBorder(int row, boolean forceNotToProcessAsFirst, boolean forceNotToProcessWithLast) {
        Border theWidestBorder = null;
        if (row >= 0 && row < horizontalBorders.size()) {
            theWidestBorder = getWidestBorder(getHorizontalBorder(row, forceNotToProcessAsFirst, forceNotToProcessWithLast));
        }
        return theWidestBorder;
    }

    public Border getWidestHorizontalBorder(int row) {
        return getWidestHorizontalBorder(row, false, false);
    }

    public Border getWidestHorizontalBorder(int row, int start, int end, boolean forceNotToProcessAsFirst, boolean forceNotToProcessAsLast) {
        Border theWidestBorder = null;
        if (row >= 0 && row < horizontalBorders.size()) {
            theWidestBorder = getWidestBorder(getHorizontalBorder(row, forceNotToProcessAsFirst, forceNotToProcessAsLast), start, end);
        }
        return theWidestBorder;
    }

    public Border getWidestHorizontalBorder(int row, int start, int end) {
        return getWidestHorizontalBorder(row, start, end, false, false);
    }

    public Border getWidestVerticalBorder(int col) {
        Border theWidestBorder = null;
        if (col >= 0 && col < verticalBorders.size()) {
            theWidestBorder = getWidestBorder(getVerticalBorder(col));
        }
        return theWidestBorder;
    }

    public Border getWidestVerticalBorder(int col, int start, int end) {
        Border theWidestBorder = null;
        if (col >= 0 && col < verticalBorders.size()) {
            theWidestBorder = getWidestBorder(getVerticalBorder(col), start, end);
        }
        return theWidestBorder;
    }

    public float getMaxTopWidth(boolean forceNoToProcessAsFirst) {
        float width = 0;
        Border widestBorder = getWidestHorizontalBorder(rowRange.getStartRow(), forceNoToProcessAsFirst, false);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public float getMaxBottomWidth(boolean forceNoToProcessAsLast) {
        float width = 0;
        Border widestBorder = getWidestHorizontalBorder(rowRange.getFinishRow() + 1, false, forceNoToProcessAsLast); // TODO
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

    public List<Border> getVerticalBorder(int index) { // TODO REFACTOR
        if (index == 0) {
            List<Border> borderList = getBorderList(null, tableBoundingBorders[3], verticalBorders.get(0).size());
            List<Border> leftVerticalBorder = verticalBorders.get(0);
            for (int i = 0; i < leftVerticalBorder.size(); i++) {
                if (null == borderList.get(i) || (null != leftVerticalBorder.get(i) && leftVerticalBorder.get(i).getWidth() > borderList.get(i).getWidth())) {
                    borderList.set(i, leftVerticalBorder.get(i));
                }
            }
            return borderList;
        } else if (index == numberOfColumns) {
            List<Border> borderList = getBorderList(null, tableBoundingBorders[1], verticalBorders.get(0).size());
            List<Border> rightVerticalBorder = verticalBorders.get(verticalBorders.size()-1);
            for (int i = 0; i < rightVerticalBorder.size(); i++) {
                if (null == borderList.get(i) || (null != rightVerticalBorder.get(i) && rightVerticalBorder.get(i).getWidth() > borderList.get(i).getWidth())) {
                    borderList.set(i, rightVerticalBorder.get(i));
                }
            }
            return borderList;
        } else {
            return verticalBorders.get(index);
        }
    }

    public List<Border> getHorizontalBorder(int index) {
        return getHorizontalBorder(index, false, false);
    }


        public List<Border> getHorizontalBorder(int index, boolean forceNotToProcessAsFirst, boolean forceNotToProcessAsLast) {
        if (index == rowRange.getStartRow() && !forceNotToProcessAsFirst) {
            List<Border> firstBorderOnCurrentPage = getBorderList(topBorderCollapseWith, tableBoundingBorders[0], numberOfColumns);
            if (0 != rows.size()) {
                int col = 0;
                int row = index;
                while (col < numberOfColumns) {
                    if (null != rows.get(row)[col] &&
                            row == (int) rows.get(row)[col].getPropertyAsInteger(Property.ROWSPAN) + (int) rows.get(row)[col].getModelElement().getRow() - 1) {
                        CellRenderer cell = rows.get(row)[col];
                        Border cellModelTopBorder = getCellSideBorder(cell.getModelElement(), Property.BORDER_TOP);
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

        } else if (((index == rowRange.getFinishRow() + 1 && !forceNotToProcessAsLast) || index == horizontalBorders.size() - 1)) {
            List<Border> lastBorderOnCurrentPage = getBorderList(bottomBorderCollapseWith, tableBoundingBorders[2], numberOfColumns);
            if (0 != rows.size()) {
                int col = 0;
                int row = index - 1;
                while (col < numberOfColumns) {
                    if (null != rows.get(row)[col]) { // TODO
                        CellRenderer cell = rows.get(row)[col];
                        Border cellModelBottomBorder = getCellSideBorder(cell.getModelElement(), Property.BORDER_BOTTOM);
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
            return horizontalBorders.get(index);
        }
    }

    public List<Border> getFirstHorizontalBorder() {
        return getHorizontalBorder(rowRange.getStartRow());
    }

    public List<Border> getLastHorizontalBorder() {
        return getHorizontalBorder(rowRange.getFinishRow()+1);
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

    public Border[] getTableBoundingBorders() {
        return tableBoundingBorders;
    }

    public int getVerticalBordersSize() {
        return verticalBorders.size();
    }

    public int getHorizontalBordersSize() {
        return verticalBorders.size();
    }

    // endregion

    // region setters

    protected TableBorders setTableBoundingBorders(Border[] borders) {
        if (null == tableBoundingBorders) {
            tableBoundingBorders = new Border[borders.length];
        }
        for (int i = 0; i < borders.length; i++) {
            tableBoundingBorders[i] = borders[i];
        }
        return this;
    }

    protected TableBorders setRowRange(Table.RowRange rowRange) {
        this.rowRange = rowRange;
        return this;
    }

    protected TableBorders setStartRow(int row) {
        this.rowRange = new Table.RowRange(row, rowRange.getFinishRow());
        return this;
    }

    protected TableBorders setFinishRow(int row) {
        this.rowRange = new Table.RowRange(rowRange.getStartRow(), row);
        return this;
    }

    public TableBorders setTopBorderCollapseWith(List<Border> topBorderCollapseWith) {
        this.topBorderCollapseWith.clear();
        if (null != topBorderCollapseWith) {
            this.topBorderCollapseWith.addAll(topBorderCollapseWith);
        }
        return this;
    }

    public TableBorders setBottomBorderCollapseWith(List<Border> bottomBorderCollapseWith) {
        this.bottomBorderCollapseWith.clear();
        if (null != bottomBorderCollapseWith) {
            this.bottomBorderCollapseWith.addAll(bottomBorderCollapseWith);
        }
        return this;
    }

    //endregion

    // region building border arrays

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
            checkAndReplaceBorderInArray(horizontalBorders, row + 1 - rowspan, colN + i, cellBorders[0], false);
        }
        // consider bottom border
        for (int i = 0; i < colspan; i++) {
            checkAndReplaceBorderInArray(horizontalBorders, row + 1, colN + i, cellBorders[2], true);
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
            checkAndReplaceBorderInArray(verticalBorders, colN, j, cellBorders[3], false);
        }
        // consider right border
        for (int i = row - rowspan + 1; i <= row; i++) {
            checkAndReplaceBorderInArray(verticalBorders, colN + colspan, i, cellBorders[1], true);
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

    // endregion

    //region static

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

    public static Border getCellSideBorder(Cell cellModel, int borderType) {
        Border cellModelSideBorder = cellModel.getProperty(borderType);
        if (null == cellModelSideBorder && !cellModel.hasProperty(borderType)) {
            cellModelSideBorder = cellModel.getProperty(Property.BORDER);
            if (null == cellModelSideBorder && !cellModel.hasProperty(Property.BORDER)) {
                cellModelSideBorder = cellModel.getDefaultProperty(Property.BORDER); // TODO Mayvb we need to foresee the possibility of default side border property
            }
        }
        return cellModelSideBorder;
    }

    public static Border getWidestBorder(List<Border> borderList) {
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

    public static Border getWidestBorder(List<Border> borderList, int start, int end) {
        Border theWidestBorder = null;
        if (0 != borderList.size()) {
            for (Border border : borderList.subList(start, end)) {
                if (null != border && (null == theWidestBorder || border.getWidth() > theWidestBorder.getWidth())) {
                    theWidestBorder = border;
                }
            }
        }
        return theWidestBorder;
    }

    private static List<Border> getBorderList(Border border, int n) {
        List<Border> borderList = new ArrayList<Border>();
        for (int i = 0; i < n; i++) {
            borderList.add(border);
        }
        return borderList;
    }

    private static List<Border> getBorderList(List<Border> originalList, Border borderToCollapse, int size) {
        List<Border> borderList = new ArrayList<Border>();
        if (null != originalList) {
            borderList.addAll(originalList);
        }
        while (borderList.size() < size) {
            borderList.add(borderToCollapse);
        }
        int end = null == originalList ? size : Math.min(originalList.size(), size);
        for (int i = 0; i < end; i++) {
            if (null == borderList.get(i) || (null != borderToCollapse && borderList.get(i).getWidth() <= borderToCollapse.getWidth())) {
                borderList.set(i, borderToCollapse);
            }
        }
        return borderList;
    }

    // endregion

    // region lowlevel logic

    protected boolean checkAndReplaceBorderInArray(List<List<Border>> borderArray, int i, int j, Border borderToAdd, boolean hasPriority) {
//        if (borderArray.size() <= i) {
//            for (int count = borderArray.size(); count <= i; count++) {
//                borderArray.add(new ArrayList<Border>());
//            }
//        }
        List<Border> borders = borderArray.get(i);
//        if (borders.isEmpty()) {
//            for (int count = 0; count < j; count++) {
//                borders.add(null);
//            }
//            borders.add(borderToAdd);
//            return true;
//        }
//        if (borders.size() == j) {
//            borders.add(borderToAdd);
//            return true;
//        }
//        if (borders.size() < j) {
//            for (int count = borders.size(); count <= j; count++) {
//                borders.add(count, null);
//            }
//        }
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

    // region update

//    protected TableBorders updateTopBorder(List<Border> newBorder, boolean[] useOldBorders) {
////        updateBorder(horizontalBorders.get(horizontalBordersIndexOffset), newBorder, useOldBorders);
//        return this;
//    }
//
//    protected TableBorders updateBottomBorder(List<Border> newBorder, boolean[] useOldBorders) {
//        updateBorder(horizontalBorders.get(horizontalBorders.size() - 1), newBorder, useOldBorders);
//        return this;
//    }
//
//    protected TableBorders updateBorder(List<Border> oldBorder, List<Border> newBorders, boolean[] isOldBorder) {
//        for (int i = 0; i < oldBorder.size(); i++) {
//            if (!isOldBorder[i]) {
//                oldBorder.set(i, newBorders.get(i));
//            }
//        }
//        return this;
//    }


// endregion
}
