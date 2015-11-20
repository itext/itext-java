package com.itextpdf.model.renderer;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.Property;
import com.itextpdf.model.border.Border;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Table;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableRenderer extends AbstractRenderer {

    protected List<CellRenderer[]> rows = new ArrayList<>();
    // Row range of the current renderer. For large tables it may contain only a few rows.
    protected Table.RowRange rowRange;
    protected TableRenderer headerRenderer;
    protected TableRenderer footerRenderer;
    /** True for newly created renderer. For split renderers this is set to false. Used for tricky layout. **/
    protected boolean isOriginalNonSplitRenderer = true;
    private Border[][] horizontalBorders;
    private Border[][] verticalBorders;
    private float[] columnWidths = null;
    private ArrayList<Float> heights = new ArrayList<>();

    public TableRenderer(Table modelElement, Table.RowRange rowRange) {
        super(modelElement);
        this.rowRange = rowRange;
        for (int row = rowRange.getStartRow(); row <= rowRange.getFinishRow(); row++) {
            rows.add(new CellRenderer[modelElement.getNumberOfColumns()]);
        }
    }

    @Override
    public void addChild(IRenderer renderer) {
        if (renderer instanceof CellRenderer) {
            renderer.setParent(this);
            // In case rowspan or colspan save cell into bottom left corner.
            // In in this case it will be easier handle row heights in case rowspan.
            Cell cell = (Cell) renderer.getModelElement();
            rows.get(cell.getRow() - rowRange.getStartRow() + cell.getRowspan() - 1)[cell.getCol()] = (CellRenderer) renderer;
        } else {
            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
            logger.error("Only BlockRenderer with Cell model element could be added");
        }
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox().clone();
        if (!((Table)modelElement).isComplete()) {
            setProperty(Property.MARGIN_BOTTOM, 0);
        }
        if (rowRange.getStartRow() != 0) {
            setProperty(Property.MARGIN_TOP, 0);
        }
        applyMargins(layoutBox, false);
        Table tableModel = (Table) getModelElement();

        Float tableWidth = retrieveWidth(layoutBox.getWidth());
        if (tableWidth == null || tableWidth == 0) {
            tableWidth = layoutBox.getWidth();
        }
        occupiedArea = new LayoutArea(area.getPageNumber(),
                new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), tableWidth, 0));

        int numberOfColumns = ((Table)getModelElement()).getNumberOfColumns();
        int numberOfRows = rows.size();
        horizontalBorders = new Border[numberOfRows + 1][numberOfColumns];
        verticalBorders = new Border[numberOfColumns + 1][numberOfRows];

        Table headerElement = tableModel.getHeader();
        boolean isFirstHeader = rowRange.getStartRow() == 0 && isOriginalNonSplitRenderer;
        boolean headerShouldBeApplied = !rows.isEmpty() && (!isOriginalNonSplitRenderer || isFirstHeader && !tableModel.isSkipFirstHeader());
        if (headerElement != null && headerShouldBeApplied) {
            headerRenderer = (TableRenderer) headerElement.createRendererSubTree().setParent(this);
            LayoutResult result = headerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            if (result.getStatus() != LayoutResult.FULL) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this);
            }
            float headerHeight = result.getOccupiedArea().getBBox().getHeight();
            layoutBox.decreaseHeight(headerHeight);
            occupiedArea.getBBox().moveDown(headerHeight).increaseHeight(headerHeight);
        }
        Table footerElement = tableModel.getFooter();
        if (footerElement != null) {
            footerRenderer = (TableRenderer) footerElement.createRendererSubTree().setParent(this);
            LayoutResult result = footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            if (result.getStatus() != LayoutResult.FULL) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this);
            }
            float footerHeight = result.getOccupiedArea().getBBox().getHeight();
            footerRenderer.move(0, - (layoutBox.getHeight() - footerHeight));
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);
        }

        columnWidths = calculateScaledColumnWidths(tableModel, tableWidth);
        LayoutResult[] splits = new LayoutResult[tableModel.getNumberOfColumns()];
        // This represents the target row index for the overflow renderer to be placed to.
        // Usually this is just the current row id of a cell, but it has valuable meaning when a cell has rowspan.
        int[] targetOverflowRowIndex = new int[tableModel.getNumberOfColumns()];

        horizontalBorders[0] = tableModel.getLastRowBottomBorder();
        for (int row = 0; row < rows.size(); row++) {
            CellRenderer[] currentRow = rows.get(row);
            for (int col = 0; col < currentRow.length; col++) {
                CellRenderer cell = currentRow[col];
                if (cell != null) {
                    buildBordersArrays(cell, row, cell.getModelElement().getRowspan(), cell.getModelElement().getColspan(), true);
                }
            }
        }

        for (int row = 0; row < rows.size(); row++) {
            CellRenderer[] currentRow = rows.get(row);
            float rowHeight = 0;
            boolean split = false;
            // Indicates that all the cells fit (at least partially after splitting if not forbidden by keepTogether) in the current row.
            boolean hasContent = true;
            // Indicates that we have added a cell from the future, i.e. a cell which has a big rowspan and we shouldn't have
            // added it yet, because we add a cell with rowspan only during the processing of the very last row this cell occupied,
            // but now we have area break and we had to force that cell addition.
            boolean cellWithBigRowspanAdded = false;
            ArrayList<CellRenderer> currChildRenderers = new ArrayList<>();
            // Process in a queue, because we might need to add a cell from the future, i.e. having big rowspan in case of split.
            Queue<CellRendererInfo> cellProcessingQueue = new LinkedList<>();
            for (int col = 0; col < currentRow.length; col++) {
                if (currentRow[col] != null) {
                    cellProcessingQueue.add(new CellRendererInfo(currentRow[col], col, row));
                }
            }
            while (!cellProcessingQueue.isEmpty()) {
                CellRendererInfo currentCellInfo = cellProcessingQueue.poll();
                int col = currentCellInfo.column;
                CellRenderer cell = currentCellInfo.cellRenderer;
                targetOverflowRowIndex[col] = currentCellInfo.finishRowInd;
                // This cell came from the future (split occurred and we need to place cell with big rowpsan into the current area)
                boolean currentCellHasBigRowspan = (row != currentCellInfo.finishRowInd);

                int colspan = cell.getPropertyAsInteger(Property.COLSPAN);
                int rowspan = cell.getPropertyAsInteger(Property.ROWSPAN);
                float cellWidth = 0, colOffset = 0;
                for (int i = col; i < col + colspan; i++) {
                    cellWidth += columnWidths[i];
                }
                for (int i = 0; i < col; i++) {
                    colOffset += columnWidths[i];
                }
                float rowspanOffset = 0;
                for (int i = row - 1; i > currentCellInfo.finishRowInd - rowspan && i >= 0; i--) {
                    rowspanOffset += heights.get(i);
                }
                float cellLayoutBoxHeight = rowspanOffset + (!currentCellHasBigRowspan || hasContent ? layoutBox.getHeight() : 0);
                float cellLayoutBoxBottom = layoutBox.getY() + (!currentCellHasBigRowspan || hasContent ? 0 : layoutBox.getHeight());
                Rectangle cellLayoutBox = new Rectangle(layoutBox.getX() + colOffset, cellLayoutBoxBottom, cellWidth, cellLayoutBoxHeight);
                LayoutArea cellArea = new LayoutArea(layoutContext.getArea().getPageNumber(), cellLayoutBox);
                LayoutResult cellResult = cell.layout(new LayoutContext(cellArea));
                //width of BlockRenderer depends on child areas, while in cell case it is hardly define.
                cell.getOccupiedArea().getBBox().setWidth(cellWidth);

                if (currentCellHasBigRowspan) {
                    // cell from the future
                    if (cellResult.getStatus() == LayoutResult.PARTIAL) {
                        splits[col] = cellResult;
                        currentRow[col] = (CellRenderer) cellResult.getSplitRenderer();
                    } else {
                        rows.get(currentCellInfo.finishRowInd)[col] = null;
                        currentRow[col] = cell;
                    }
                } else {
                    if (cellResult.getStatus() != LayoutResult.FULL) {
                        // first time split occurs
                        if (!split) {
                            // This is a case when last footer should be skipped and we might face an end of the table.
                            // We check if we can fit all the rows right now and the split occurred only because we reserved
                            // space for footer before, and if yes we skip footer and write all the content right now.
                            if (footerRenderer != null && tableModel.isSkipLastFooter() && tableModel.isComplete()) {
                                LayoutArea potentialArea = new LayoutArea(area.getPageNumber(), layoutBox.clone());
                                float footerHeight = footerRenderer.getOccupiedArea().getBBox().getHeight();
                                potentialArea.getBBox().moveDown(footerHeight).increaseHeight(footerHeight);
                                if (canFitRowsInGivenArea(potentialArea, row, columnWidths, heights)) {
                                    layoutBox.increaseHeight(footerHeight).moveDown(footerHeight);
                                    cellProcessingQueue.clear();
                                    for (int addCol = 0; addCol < currentRow.length; addCol++) {
                                        if (currentRow[addCol] != null) {
                                            cellProcessingQueue.add(new CellRendererInfo(currentRow[addCol], addCol, row));
                                        }
                                    }
                                    footerRenderer = null;
                                    continue;
                                }
                            }

                            // Here we look for a cell with big rowpsan (i.e. one which would not be normally processed in
                            // the scope of this row), and we add such cells to the queue, because we need to write them
                            // at least partially into the available area we have.
                            for (int addCol = 0; addCol < currentRow.length; addCol++) {
                                if (currentRow[addCol] == null) {
                                    // Search for the next cell including rowspan.
                                    for (int addRow = row + 1; addRow < rows.size(); addRow++) {
                                        if (rows.get(addRow)[addCol] != null) {
                                            CellRenderer addRenderer = rows.get(addRow)[addCol];
                                            if (row + addRenderer.getPropertyAsInteger(Property.ROWSPAN) - 1 >= addRow) {
                                                cellProcessingQueue.add(new CellRendererInfo(addRenderer, addCol, addRow));
                                                cellWithBigRowspanAdded = true;
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        split = true;
                        if (cellResult.getStatus() == LayoutResult.NOTHING) {
                            hasContent = false;
                        }

                        splits[col] = cellResult;
                        currentRow[col] = (CellRenderer) cellResult.getSplitRenderer();
                    }
                }

                currChildRenderers.add(cell);

                if (!currentCellHasBigRowspan && cellResult.getStatus() != LayoutResult.NOTHING) {
                    rowHeight = Math.max(rowHeight, cell.getOccupiedArea().getBBox().getHeight() - rowspanOffset);
                }
            }

            if (hasContent || cellWithBigRowspanAdded) {
                heights.add(rowHeight);
                for (int col = 0; col < currentRow.length; col++) {
                    CellRenderer cell = currentRow[col];
                    if (cell == null) {
                        continue;
                    }
                    float height = 0;
                    int rowspan = cell.getPropertyAsInteger(Property.ROWSPAN);
                    for (int i = row; i > targetOverflowRowIndex[col] - rowspan && i >= 0; i--) {
                        height += heights.get(i);
                    }
                    int rowN = row + 1;
                    if (!hasContent) {
                        rowN--;
                    }
                    if (horizontalBorders[rowN][col] == null) {
                        horizontalBorders[rowN][col] = cell.getBorders()[2];
                    }


                    // Coorrection of cell bbox only. We don't need #move() here.
                    // This is because of BlockRenderer's specificity regarding occupied area.
                    float shift = height - cell.getOccupiedArea().getBBox().getHeight();
                    Rectangle bBox = cell.getOccupiedArea().getBBox();
                    bBox.moveDown(shift);
                    bBox.setHeight(height);
                }

                occupiedArea.getBBox().moveDown(rowHeight);
                occupiedArea.getBBox().increaseHeight(rowHeight);
            }

            if (split) {
                TableRenderer splitResult[] = split(row, hasContent);
                for (int col = 0; col < currentRow.length; col++) {
                    if (splits[col] != null) {
                        BlockRenderer cellSplit = currentRow[col];
                        if (splits[col].getStatus() != LayoutResult.NOTHING) {
                            childRenderers.add(cellSplit);
                        }
                        currentRow[col] = null;
                        rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) splits[col].getOverflowRenderer();
                    } else if (hasContent && currentRow[col] != null) {
                        Cell overflowCell = currentRow[col].getModelElement().clone(false);
                        childRenderers.add(currentRow[col]);
                        currentRow[col] = null;
                        rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) overflowCell.makeRenderer().setParent(this);
                    }
                }
                adjustFooterAndFixOccupiedArea(layoutBox);
                int status = childRenderers.isEmpty() && footerRenderer == null ? LayoutResult.NOTHING : LayoutResult.PARTIAL;
                return new LayoutResult(status, occupiedArea, splitResult[0], splitResult[1]);
            } else {
                childRenderers.addAll(currChildRenderers);
                currChildRenderers.clear();
            }

            layoutBox.decreaseHeight(rowHeight);
        }

        applyMargins(occupiedArea.getBBox(), true);
        if (tableModel.isSkipLastFooter() || !tableModel.isComplete()) {
            footerRenderer = null;
        }
        adjustFooterAndFixOccupiedArea(layoutBox);
        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
    }

    @Override
    public void drawChildren(PdfDocument document, PdfCanvas canvas) {
        if (headerRenderer != null) {
            headerRenderer.draw(document, canvas);
        }
        super.drawChildren(document, canvas);
        drawBorders(canvas);

        if (footerRenderer != null) {
            footerRenderer.draw(document, canvas);
        }
    }

    protected float[] calculateScaledColumnWidths(Table tableModel, float tableWidth) {
        float[] columnWidths = new float[tableModel.getNumberOfColumns()];
        float widthSum = 0;
        for (int i = 0; i < tableModel.getNumberOfColumns(); i++) {
            columnWidths[i] = tableModel.getColumnWidth(i);
            widthSum += columnWidths[i];
        }
        for (int i = 0; i < tableModel.getNumberOfColumns(); i++) {
            columnWidths[i] *= tableWidth / widthSum;
        }

        return columnWidths;
    }

    protected TableRenderer[] split (int row) {
        return split(row, false);
    }

    protected TableRenderer[] split(int row, boolean hasContent) {
        TableRenderer splitRenderer = createSplitRenderer(new Table.RowRange(rowRange.getStartRow(), rowRange.getStartRow() + row));
        splitRenderer.rows = rows.subList(0, row);
        int rowN = row;
        if (hasContent) {
            rowN++;
        }
        splitRenderer.horizontalBorders = new Border[rowN + 1][];
        for (int i = 0; i <= rowN; i++) {
            splitRenderer.horizontalBorders[i] = horizontalBorders[i];
        }
        splitRenderer.verticalBorders = new Border[verticalBorders.length][];
        for (int i = 0; i < verticalBorders.length; i++) {
            splitRenderer.verticalBorders[i] = new Border[rowN];
            for (int j = 0; j < rowN; j++) {
                splitRenderer.verticalBorders[i][j] = verticalBorders[i][j];
            }
        }
        splitRenderer.heights = heights;
        splitRenderer.columnWidths = columnWidths;
        TableRenderer overflowRenderer = createOverflowRenderer(new Table.RowRange(rowRange.getStartRow() + row,rowRange.getFinishRow()));
        overflowRenderer.rows = rows.subList(row, rows.size());
        splitRenderer.occupiedArea = occupiedArea;

        return new TableRenderer[] {splitRenderer, overflowRenderer};
    }

    protected <T extends TableRenderer> T makeSplitRenderer(Table.RowRange rowRange) {
        return (T) new TableRenderer((Table) modelElement, rowRange);
    }

    protected <T extends TableRenderer> T createSplitRenderer(Table.RowRange rowRange) {
        TableRenderer splitRenderer = makeSplitRenderer(rowRange);
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        // TODO childRenderers will be populated twice during the relayout.
        // We should probably clean them before #layout().
        splitRenderer.childRenderers = childRenderers;
        splitRenderer.addAllProperties(getOwnProperties());
        splitRenderer.headerRenderer = headerRenderer;
        splitRenderer.footerRenderer = footerRenderer;
        return (T) splitRenderer;
    }

    protected <T extends TableRenderer> T makeOverflowRenderer(Table.RowRange rowRange) {
        return (T) new TableRenderer((Table) modelElement, rowRange);
    }

    protected <T extends TableRenderer> T createOverflowRenderer(Table.RowRange rowRange) {
        TableRenderer overflowRenderer = makeOverflowRenderer(rowRange);
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        overflowRenderer.isOriginalNonSplitRenderer = false;
        return (T) overflowRenderer;
    }

    protected void drawBorders(PdfCanvas canvas) {
        float startX = getOccupiedArea().getBBox().getX();
        float startY = getOccupiedArea().getBBox().getY() + getOccupiedArea().getBBox().getHeight();

        for (IRenderer child : childRenderers) {
            CellRenderer cell = (CellRenderer) child;
            if (cell.getModelElement().getCol() == 0) {
                startX = cell.getOccupiedArea().getBBox().getX();
                startY = cell.getOccupiedArea().getBBox().getY() + cell.getOccupiedArea().getBBox().getHeight();
                break;
            }
        }

        float y1 = startY;
        for (int i = 0; i < horizontalBorders.length; i++) {
            Border[] borders = horizontalBorders[i];
            float x1 = startX;
            float x2 = x1 + columnWidths[0];
            if (i == 0) {
                if (verticalBorders != null && verticalBorders.length > 0 && verticalBorders[0].length > 0 && verticalBorders[verticalBorders.length - 1].length > 0) {
                    Border firstBorder = verticalBorders[0][0];
                    if (firstBorder != null) {
                        x1 -= firstBorder.getWidth() / 2;
                    }
                }
            } else if(i == horizontalBorders.length - 1) {
                if (verticalBorders != null && verticalBorders.length > 0 && verticalBorders[0].length > 0 && verticalBorders[verticalBorders.length - 1] != null && verticalBorders[verticalBorders.length - 1].length > 0
                        && verticalBorders[0] != null) {
                    Border firstBorder = verticalBorders[0][verticalBorders[verticalBorders.length - 1].length - 1];
                    if (firstBorder != null) {
                        x1 -= firstBorder.getWidth() / 2;
                    }
                }
            }

            int j;
            for (j = 1; j < borders.length; j++) {
                Border prevBorder = borders[j - 1];
                Border curBorder = borders[j];
                if (prevBorder != null) {
                    if (!prevBorder.equals(curBorder)) {
                        prevBorder.drawCellBorder(canvas, x1, y1, x2, y1);
                        x1 = x2;
                    }
                } else {
                    x1 += columnWidths[j - 1];
                    x2 = x1;
                }
                if (curBorder != null) {
                    x2 += columnWidths[j];
                }
            }

            Border lastBorder = borders[j - 1];
            if (lastBorder != null) {
                if (i == 0 || i == horizontalBorders.length - 1) {
                    x2 += lastBorder.getWidth() / 2;
                }
                lastBorder.drawCellBorder(canvas, x1, y1, x2, y1);
            }
            if (i < heights.size()) {
                y1 -= heights.get(i);
            }
        }
        float x1 = startX;
        for (int i = 0; i < verticalBorders.length; i++) {
            Border[] borders = verticalBorders[i];
            y1 = startY;
            float y2 = y1;
            if (!heights.isEmpty()) {
                y2 = y1 - heights.get(0);
            }
            int j;
            for (j = 1; j < borders.length; j++) {
                Border prevBorder = borders[j - 1];
                Border curBorder = borders[j];
                if (prevBorder != null) {
                    if (!prevBorder.equals(curBorder)) {
                        prevBorder.drawCellBorder(canvas, x1, y1, x1, y2);
                        y1 = y2;
                    }
                } else {
                    y1 -= heights.get(j - 1);
                    y2 = y1;
                }
                if (curBorder != null) {
                    y2 -= heights.get(j);
                }
            }
            if (borders.length == 0) {
                continue;
            }
            Border lastBorder = borders[j - 1];
            if (lastBorder != null) {
                lastBorder.drawCellBorder(canvas, x1, y1, x1, y2);
            }
            if (i < columnWidths.length) {
                x1 += columnWidths[i];
            }
        }
    }

    /**
     * If there is some space left, we move footer up, because initially footer will be at the very bottom of the area.
     * We also adjust occupied area by footer size if it is present.
     * @param layoutBox the layout box which represents the area which is left free.
     */
    private void adjustFooterAndFixOccupiedArea(Rectangle layoutBox) {
        if (footerRenderer != null) {
            footerRenderer.move(0, layoutBox.getHeight());
            float footerHeight = footerRenderer.getOccupiedArea().getBBox().getHeight();
            occupiedArea.getBBox().moveDown(footerHeight).increaseHeight(footerHeight);
        }
    }

    /**
     * This method checks if we can completely fit the rows in the given area, staring from the startRow.
     */
    private boolean canFitRowsInGivenArea(LayoutArea layoutArea, int startRow, float[] columnWidths, List<Float> heights) {
        layoutArea = layoutArea.clone();
        heights = new ArrayList<>(heights);
        for (int row = startRow; row < rows.size(); row++) {
            CellRenderer[] rowCells = rows.get(row);
            float rowHeight = 0;
            for (int col = 0; col < rowCells.length; col++) {
                CellRenderer cell = rowCells[col];
                if (cell == null) {
                    continue;
                }

                int colspan = cell.getPropertyAsInteger(Property.COLSPAN);
                int rowspan = cell.getPropertyAsInteger(Property.ROWSPAN);
                float cellWidth = 0, colOffset = 0;
                for (int i = col; i < col + colspan; i++) {
                    cellWidth += columnWidths[i];
                }
                for (int i = 0; i < col; i++) {
                    colOffset += columnWidths[i];
                }
                float rowspanOffset = 0;
                for (int i = row - 1; i > row - rowspan && i >= 0; i--) {
                    rowspanOffset += heights.get(i);
                }
                float cellLayoutBoxHeight = rowspanOffset + layoutArea.getBBox().getHeight();
                Rectangle cellLayoutBox = new Rectangle(layoutArea.getBBox().getX() + colOffset, layoutArea.getBBox().getY(), cellWidth, cellLayoutBoxHeight);
                LayoutArea cellArea = new LayoutArea(layoutArea.getPageNumber(), cellLayoutBox);
                LayoutResult cellResult = cell.layout(new LayoutContext(cellArea));

                if (cellResult.getStatus() != LayoutResult.FULL) {
                    return false;
                }
                rowHeight = Math.max(rowHeight, cellResult.getOccupiedArea().getBBox().getHeight());
            }
            heights.add(rowHeight);
            layoutArea.getBBox().moveUp(rowHeight).decreaseHeight(rowHeight);
        }
        return true;
    }

    private void buildBordersArrays(CellRenderer cell, int row, int rowspan, int colspan, boolean hasContent) {
        int colN = cell.getModelElement().getCol();
        Border[] cellBorders = cell.getBorders();
        if (row + 1 - rowspan < 0) {
            rowspan = row + 1;
        }

        if (row + 1 - rowspan != 0) {
            for (int i = 0; i < colspan; i++) {
                if (checkAndReplaceBorderInArray(horizontalBorders, row + 1 - rowspan, colN + i, cellBorders[0])) {
                    CellRenderer rend = rows.get(row - rowspan)[colN];
                    if (rend != null) {
                        rend.setBorders(cellBorders[0], 2);
                    }
                } else {
                    if (cell != null) {
                        cell.setBorders(horizontalBorders[row + 1 - rowspan][i], 0);
                    }
                }
            }
        } else {
            for (int i = 0; i < colspan; i++ ) {
                if (!checkAndReplaceBorderInArray(horizontalBorders, 0, colN + i, cellBorders[0])) {
                    cell.setBorders(horizontalBorders[0][i], 0);
                }
            }
        }
        for (int i = 0; i < colspan; i++) {
            if (hasContent) {
                horizontalBorders[row + 1][colN + i] = cellBorders[2];
            } else {
                horizontalBorders[row][colN + i] = cellBorders[2];
            }
        }
        if (colN != 0) {
            for (int i = row - rowspan + 1; i <= row; i++) {
                if (checkAndReplaceBorderInArray(verticalBorders, colN, i, cellBorders[3])) {
                    if (colN != 0) {
                        CellRenderer rend = rows.get(i)[colN - 1];
                        if (rend != null) {
                            rend.setBorders(cellBorders[3], 1);
                        }
                    }
                } else {
                    CellRenderer rend = rows.get(i)[colN];
                    if (rend != null) {
                        rend.setBorders(verticalBorders[colN][row], 3);
                    }
                }
            }
        } else {
            for (int i = row - rowspan + 1; i <= row; i++) {
                verticalBorders[0][i] = cellBorders[3];
            }
        }

        for (int i = row - rowspan + 1; i <= row; i++) {
            checkAndReplaceBorderInArray(verticalBorders, colN + colspan, i, cellBorders[1]);
        }
    }

    private boolean checkAndReplaceBorderInArray(Border[][] borderArray, int i, int j, Border borderToAdd) {
        Border neighbour = borderArray[i][j];
        if (neighbour == null) {
            borderArray[i][j] = borderToAdd;
            return true;
        } else {
            if (neighbour != borderToAdd) {
                if (borderToAdd != null && neighbour.getWidth() < borderToAdd.getWidth()) {
                    borderArray[i][j] = borderToAdd;
                    return true;
                }
            }
        }

        return false;
    }



    /**
     * This is a struct used for convenience in layout.
     */
    private static class CellRendererInfo {
        public CellRenderer cellRenderer;
        public int column;
        public int finishRowInd;

        public CellRendererInfo(CellRenderer cellRenderer, int column, int finishRow) {
            this.cellRenderer = cellRenderer;
            this.column = column;
            // When a cell has a rowspan, this is the index of the finish row of the cell.
            // Otherwise, this is simply the index of the row of the cell in the {@link #rows} array.
            this.finishRowInd = finishRow;
        }
    }
}
