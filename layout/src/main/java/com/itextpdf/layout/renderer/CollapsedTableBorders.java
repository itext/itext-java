package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class CollapsedTableBorders extends TableBorders {
    private List<Border> topBorderCollapseWith = new ArrayList<Border>();
    private List<Border> bottomBorderCollapseWith = new ArrayList<Border>();

    // region constructors
    public CollapsedTableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders) {
        super(rows, numberOfColumns, tableBoundingBorders);
    }

    public CollapsedTableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders, int largeTableIndexOffset) {
        super(rows, numberOfColumns, tableBoundingBorders, largeTableIndexOffset);
    }
    // endregion

    // region collapse
    protected CollapsedTableBorders collapseAllBordersAndEmptyRows() {
        CellRenderer[] currentRow;
        int[] rowsToDelete = new int[numberOfColumns];
        for (int row = startRow - largeTableIndexOffset; row <= finishRow - largeTableIndexOffset; row++) {
            currentRow = rows.get(row);
            boolean hasCells = false;
            for (int col = 0; col < numberOfColumns; col++) {
                if (null != currentRow[col]) {
                    int colspan = (int) currentRow[col].getPropertyAsInteger(Property.COLSPAN);
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
                setFinishRow(finishRow - 1);
                rows.remove(currentRow);
                row--;
                for (int i = 0; i < numberOfColumns; i++) {
                    rowsToDelete[i]++;
                }
                if (row == rows.size() - 1) {
                    Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                    logger.warn(LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE);
                }
            }
        }
        if (finishRow < startRow) {
            setFinishRow(startRow);
        }
        return this;
    }
    // endregion

    // region getters
    public List<Border> getTopBorderCollapseWith() {
        return topBorderCollapseWith;
    }

    public List<Border> getBottomBorderCollapseWith() {
        return bottomBorderCollapseWith;
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

    public List<Border> getVerticalBorder(int index) {
        if (index == 0) {
            List<Border> borderList = BorderUtil.createAndFillBorderList(null, tableBoundingBorders[3], verticalBorders.get(0).size());
            List<Border> leftVerticalBorder = verticalBorders.get(0);
            for (int i = 0; i < leftVerticalBorder.size(); i++) {
                if (null == borderList.get(i) || (null != leftVerticalBorder.get(i) && leftVerticalBorder.get(i).getWidth() > borderList.get(i).getWidth())) {
                    borderList.set(i, leftVerticalBorder.get(i));
                }
            }
            return borderList;
        } else if (index == numberOfColumns) {
            List<Border> borderList = BorderUtil.createAndFillBorderList(null, tableBoundingBorders[1], verticalBorders.get(0).size());
            List<Border> rightVerticalBorder = verticalBorders.get(verticalBorders.size() - 1);
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
        if (index == startRow) {
            List<Border> firstBorderOnCurrentPage = BorderUtil.createAndFillBorderList(topBorderCollapseWith, tableBoundingBorders[0], numberOfColumns);
            if (index == largeTableIndexOffset) {
                return getCollapsedList(horizontalBorders.get(index - largeTableIndexOffset), firstBorderOnCurrentPage);
            }
            if (0 != rows.size()) {
                int col = 0;
                int row = index;
                while (col < numberOfColumns) {
                    if (null != rows.get(row - largeTableIndexOffset)[col] &&
                            row - index + 1 <= (int) rows.get(row - largeTableIndexOffset)[col].getModelElement().getRowspan()) {
                        CellRenderer cell = rows.get(row - largeTableIndexOffset)[col];
                        Border cellModelTopBorder = BorderUtil.getCellSideBorder(cell.getModelElement(), Property.BORDER_TOP);
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
            List<Border> lastBorderOnCurrentPage = BorderUtil.createAndFillBorderList(bottomBorderCollapseWith, tableBoundingBorders[2], numberOfColumns);
            if (index - largeTableIndexOffset == horizontalBorders.size() - 1) {
                return getCollapsedList(horizontalBorders.get(index - largeTableIndexOffset), lastBorderOnCurrentPage);
            }
            if (0 != rows.size()) {
                int col = 0;
                int row = index - 1;
                while (col < numberOfColumns) {
                    if (null != rows.get(row - largeTableIndexOffset)[col]) { // TODO
                        CellRenderer cell = rows.get(row - largeTableIndexOffset)[col];
                        Border cellModelBottomBorder = BorderUtil.getCellSideBorder(cell.getModelElement(), Property.BORDER_BOTTOM);
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
        if (null != this.topBorderCollapseWith) {
            this.topBorderCollapseWith.clear();
        } else {
            this.topBorderCollapseWith = new ArrayList<Border>();
        }
        if (null != topBorderCollapseWith) {
            this.topBorderCollapseWith.addAll(topBorderCollapseWith);
        }
        return this;
    }

    public CollapsedTableBorders setBottomBorderCollapseWith(List<Border> bottomBorderCollapseWith) {
        if (null != this.bottomBorderCollapseWith) {
            this.bottomBorderCollapseWith.clear();
        } else {
            this.bottomBorderCollapseWith = new ArrayList<Border>();
        }
        if (null != bottomBorderCollapseWith) {
            this.bottomBorderCollapseWith.addAll(bottomBorderCollapseWith);
        }
        return this;
    }
    //endregion

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

    // region draw
    protected TableBorders drawHorizontalBorder(int i, float startX, float y1, PdfCanvas canvas, float[] countedColumnWidth) {
        List<Border> borders = getHorizontalBorder(startRow /*- largeTableIndexOffset*/ + i);
        float x1 = startX;
        float x2 = x1 + countedColumnWidth[0];
        if (i == 0) {
            Border firstBorder = getFirstVerticalBorder().get(startRow - largeTableIndexOffset);
            if (firstBorder != null) {
                x1 -= firstBorder.getWidth() / 2;
            }
        } else if (i == finishRow - startRow + 1) {
            Border firstBorder = getFirstVerticalBorder().get(startRow - largeTableIndexOffset + finishRow - startRow + 1 - 1);
            if (firstBorder != null) {
                x1 -= firstBorder.getWidth() / 2;
            }
        }

        int j;
        for (j = 1; j < borders.size(); j++) {
            Border prevBorder = borders.get(j - 1);
            Border curBorder = borders.get(j);
            if (prevBorder != null) {
                if (!prevBorder.equals(curBorder)) {
                    prevBorder.drawCellBorder(canvas, x1, y1, x2, y1);
                    prevBorder.drawCellBorder(canvas, x1, y1, x2, y1);
                    x1 = x2;
                }
            } else {
                x1 += countedColumnWidth[j - 1];
                x2 = x1;
            }
            if (curBorder != null) {
                x2 += countedColumnWidth[j];
            }
        }

        Border lastBorder = borders.size() > j - 1 ? borders.get(j - 1) : null;
        if (lastBorder != null) {
            if (i == 0) {
                if (getVerticalBorder(j).get(startRow - largeTableIndexOffset + i) != null)
                    x2 += getVerticalBorder(j).get(startRow - largeTableIndexOffset + i).getWidth() / 2;
            } else if (i == finishRow - startRow + 1 && getVerticalBorder(j).size() >= startRow - largeTableIndexOffset + i - 1 && getVerticalBorder(j).get(startRow - largeTableIndexOffset + i - 1) != null) {
                x2 += getVerticalBorder(j).get(startRow - largeTableIndexOffset + i - 1).getWidth() / 2;
            }

            lastBorder.drawCellBorder(canvas, x1, y1, x2, y1);
        }
        return this;
    }

    protected TableBorders drawVerticalBorder(int i, float startY, float x1, PdfCanvas canvas, List<Float> heights) {
        List<Border> borders = getVerticalBorder(i);
        float y1 = startY;
        float y2 = y1;
        if (!heights.isEmpty()) {
            y2 = y1 - (float) heights.get(0);
        }
        int j;
        for (j = 1; j < heights.size(); j++) {
            Border prevBorder = borders.get(startRow - largeTableIndexOffset + j - 1);
            Border curBorder = borders.get(startRow - largeTableIndexOffset + j);
            if (prevBorder != null) {
                if (!prevBorder.equals(curBorder)) {
                    prevBorder.drawCellBorder(canvas, x1, y1, x1, y2);
                    y1 = y2;
                }
            } else {
                y1 -= (float) heights.get(j - 1);
                y2 = y1;
            }
            if (curBorder != null) {
                y2 -= (float) heights.get(j);
            }
        }
        if (borders.size() == 0) {
            return this;
        }
        Border lastBorder = borders.get(startRow - largeTableIndexOffset + j - 1);
        if (lastBorder != null) {
            lastBorder.drawCellBorder(canvas, x1, y1, x1, y2);
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
        List<Border> collapsedList = new ArrayList<Border>();
        for (int i = 0; i < size; i++) {
            collapsedList.add(getCollapsedBorder(innerList.get(i), outerList.get(i)));
        }
        return collapsedList;
    }
    // endregion

    // region occupation

    protected TableBorders applyLeftAndRightTableBorder(Rectangle layoutBox, boolean reverse) {
        if (null != layoutBox) {
            layoutBox.<Rectangle>applyMargins(0, rightBorderMaxWidth / 2, 0, leftBorderMaxWidth / 2, reverse);
        }

        return this;
    }

    protected TableBorders applyTopTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean force, boolean reverse) {
        if (!isEmpty) {
            return applyTopTableBorder(occupiedBox, layoutBox, reverse);
        } else if (force) { // process empty table
            applyTopTableBorder(occupiedBox, layoutBox, reverse);
            return applyTopTableBorder(occupiedBox, layoutBox, reverse);
        }
        return this;
    }

    protected TableBorders applyBottomTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean force, boolean reverse) {
        if (!isEmpty) {
            return applyBottomTableBorder(occupiedBox, layoutBox, reverse);
        } else if (force) { // process empty table
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
                    collapseAllBordersAndEmptyRows();
                    rightBorderMaxWidth = getMaxRightWidth();
                    leftBorderMaxWidth = getMaxLeftWidth();
                }
                setTopBorderCollapseWith(((Table) currentRenderer.getModelElement()).getLastRowBottomBorder());
            } else {
                setTopBorderCollapseWith(null);
                setBottomBorderCollapseWith(null);
            }
        }
        if (null != footerRenderer) {
            processRendererBorders(footerRenderer);
            float rightFooterBorderWidth = footerRenderer.bordersHandler.getMaxRightWidth();
            float leftFooterBorderWidth = footerRenderer.bordersHandler.getMaxLeftWidth();

            leftBorderMaxWidth = Math.max(leftBorderMaxWidth, leftFooterBorderWidth);
            rightBorderMaxWidth = Math.max(rightBorderMaxWidth, rightFooterBorderWidth);
        }

        if (null != headerRenderer) {
            processRendererBorders(headerRenderer);
            float rightHeaderBorderWidth = headerRenderer.bordersHandler.getMaxRightWidth();
            float leftHeaderBorderWidth = headerRenderer.bordersHandler.getMaxLeftWidth();

            leftBorderMaxWidth = Math.max(leftBorderMaxWidth, leftHeaderBorderWidth);
            rightBorderMaxWidth = Math.max(rightBorderMaxWidth, rightHeaderBorderWidth);
        }

        return this;
    }

    protected TableBorders skipFooter(Border[] borders) {
        setTableBoundingBorders(borders);
        setBottomBorderCollapseWith(null);
        return this;
    }

    protected TableBorders collapseTableWithFooter(TableBorders footerBordersHandler, boolean hasContent) {
        ((CollapsedTableBorders) footerBordersHandler).setTopBorderCollapseWith(hasContent ? getLastHorizontalBorder() : getTopBorderCollapseWith());
        setBottomBorderCollapseWith(footerBordersHandler.getHorizontalBorder(0));
        return this;
    }

    protected TableBorders collapseTableWithHeader(TableBorders headerBordersHandler, boolean changeThis) {
        ((CollapsedTableBorders) headerBordersHandler).setBottomBorderCollapseWith(getHorizontalBorder(startRow));
        if (changeThis) {
            setTopBorderCollapseWith(headerBordersHandler.getLastHorizontalBorder());
        }
        return this;
    }

    protected TableBorders fixHeaderOccupiedArea(Rectangle occupiedBox, Rectangle layoutBox) {
        float topBorderMaxWidth = getMaxTopWidth();
        layoutBox.increaseHeight(topBorderMaxWidth);
        occupiedBox.moveUp(topBorderMaxWidth).decreaseHeight(topBorderMaxWidth);
        return this;
    }


    protected static TableBorders processRendererBorders(TableRenderer renderer) {
        renderer.bordersHandler = new CollapsedTableBorders(renderer.rows, ((Table) renderer.getModelElement()).getNumberOfColumns(), renderer.getBorders());
        renderer.bordersHandler.initializeBorders();
        renderer.bordersHandler.setRowRange(renderer.rowRange.getStartRow(), renderer.rowRange.getFinishRow());
        ((CollapsedTableBorders) renderer.bordersHandler).collapseAllBordersAndEmptyRows();
        return renderer.bordersHandler;
    }
    // endregion
}
