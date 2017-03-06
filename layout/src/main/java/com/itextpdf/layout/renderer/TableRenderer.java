/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.tagutils.IAccessibleElement;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This class represents the {@link IRenderer renderer} object for a {@link Table}
 * object. It will delegate its drawing operations on to the {@link CellRenderer}
 * instances associated with the {@link Cell table cells}.
 */
public class TableRenderer extends AbstractRenderer {

    protected List<CellRenderer[]> rows = new ArrayList<>();
    // Row range of the current renderer. For large tables it may contain only a few rows.
    protected Table.RowRange rowRange;
    protected TableRenderer headerRenderer;
    protected TableRenderer footerRenderer;
    /**
     * True for newly created renderer. For split renderers this is set to false. Used for tricky layout.
     */
    protected boolean isOriginalNonSplitRenderer = true;
    private float[] columnWidths = null;
    private List<Float> heights = new ArrayList<>();

    private float[] countedColumnWidth = null;
    private float totalWidthForColumns;

    private float topBorderMaxWidth;

    TableBorders bordersHandler;

    private TableRenderer() {
    }

    /**
     * Creates a TableRenderer from a {@link Table} which will partially render
     * the table.
     *
     * @param modelElement the table to be rendered by this renderer
     * @param rowRange     the table rows to be rendered
     */
    public TableRenderer(Table modelElement, Table.RowRange rowRange) {
        super(modelElement);
        setRowRange(rowRange);
    }

    /**
     * Creates a TableRenderer from a {@link Table}.
     *
     * @param modelElement the table to be rendered by this renderer
     */
    public TableRenderer(Table modelElement) {
        this(modelElement, new Table.RowRange(0, modelElement.getNumberOfRows() - 1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild(IRenderer renderer) {
        if (renderer instanceof CellRenderer) {
            // In case rowspan or colspan save cell into bottom left corner.
            // In in this case it will be easier handle row heights in case rowspan.
            Cell cell = (Cell) renderer.getModelElement();
            rows.get(cell.getRow() - rowRange.getStartRow() + cell.getRowspan() - 1)[cell.getCol()] = (CellRenderer) renderer;
        } else {
            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
            logger.error("Only CellRenderer could be added");
        }
    }

    @Override
    protected Rectangle applyBorderBox(Rectangle rect, Border[] borders, boolean reverse) {
        // Do nothing here. Applying border box for tables is indeed difficult operation and is done on #layout()
        return rect;
    }

    private Table getTable() {
        return (Table) getModelElement();
    }

    private void initializeHeaderAndFooter(boolean isFirstOnThePage) {
        Table table = (Table) getModelElement();
        Border[] tableBorder = getBorders();

        Table footerElement = table.getFooter();
        // footer can be skipped, but after the table content will be layouted
        boolean footerShouldBeApplied = !(table.isComplete() && 0 != table.getLastRowBottomBorder().size() && table.isSkipLastFooter())
                && !Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.IGNORE_FOOTER));
        if (footerElement != null && footerShouldBeApplied) {
            footerRenderer = initFooterOrHeaderRenderer(true, tableBorder);
        }

        Table headerElement = table.getHeader();
        boolean isFirstHeader = rowRange.getStartRow() == 0 && isOriginalNonSplitRenderer;
        boolean headerShouldBeApplied = (table.isComplete() || !rows.isEmpty()) && (isFirstOnThePage && (!table.isSkipFirstHeader() || !isFirstHeader))
                && !Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.IGNORE_HEADER));
        if (headerElement != null && headerShouldBeApplied) {
            headerRenderer = initFooterOrHeaderRenderer(false, tableBorder);
        }
    }

    private boolean isOriginalRenderer() {
        return isOriginalNonSplitRenderer && !isFooterRenderer() && !isHeaderRenderer();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        overrideHeightProperties();
        Float blockMinHeight = retrieveMinHeight();
        Float blockMaxHeight = retrieveMaxHeight();

        boolean wasHeightClipped = false;
        LayoutArea area = layoutContext.getArea();
        Rectangle layoutBox = area.getBBox().clone();

        Table tableModel = (Table) getModelElement();
        if (!tableModel.isComplete()) {
            setProperty(Property.MARGIN_BOTTOM, 0);
        }
        if (rowRange.getStartRow() != 0) {
            setProperty(Property.MARGIN_TOP, 0);
        }

        // we can invoke #layout() twice (processing KEEP_TOGETHER for instance)
        // so we need to clear the results of previous #layout() invocation
        heights.clear();
        childRenderers.clear();

        // Cells' up moves occured while split processing
        // key is column number (there can be only one move during one split)
        // value is the previous row number of the cell
        Map<Integer, Integer> rowMoves = new HashMap<>();

        MarginsCollapseHandler marginsCollapseHandler = null;
        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler = new MarginsCollapseHandler(this, layoutContext.getMarginsCollapseInfo());
            marginsCollapseHandler.startMarginsCollapse(layoutBox);
        }
        applyMargins(layoutBox, false);

        int row, col;

        if (isPositioned()) {
            if (isFixedLayout()) {
                float x = (float) this.getPropertyAsFloat(Property.X);
                float relativeX = isFixedLayout() ? 0 : layoutBox.getX();
                layoutBox.setX(relativeX + x);
            }
        }

        if (null != blockMaxHeight && blockMaxHeight < layoutBox.getHeight()
                && !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
            layoutBox.moveUp(layoutBox.getHeight() - (float) blockMaxHeight).setHeight((float) blockMaxHeight);
            wasHeightClipped = true;
        }

        List<Rectangle> floatRendererAreas = layoutContext.getFloatRendererAreas();
        FloatPropertyValue floatPropertyValue = getProperty(Property.FLOAT);
        for (Rectangle floatRendererArea : floatRendererAreas) {
            if (floatRendererArea != null) {
                if (layoutBox.getX() >= floatRendererArea.getX() && layoutBox.getX() < floatRendererArea.getX() + floatRendererArea.getWidth()) {
                    layoutBox.moveRight(floatRendererArea.getWidth());
                    layoutBox.setWidth(layoutBox.getWidth() - floatRendererArea.getWidth());
                }
            }
        }
        if (floatPropertyValue != null) {
            if (floatPropertyValue.equals(FloatPropertyValue.LEFT)) {
                setProperty(Property.HORIZONTAL_ALIGNMENT, HorizontalAlignment.LEFT);
            } else if (floatPropertyValue.equals(FloatPropertyValue.RIGHT)) {
                setProperty(Property.HORIZONTAL_ALIGNMENT, HorizontalAlignment.RIGHT);
            }
        }

        int numberOfColumns = ((Table) getModelElement()).getNumberOfColumns();

        // The last flushed row. Empty list if the table hasn't been set incomplete
        List<Border> lastFlushedRowBottomBorder = tableModel.getLastRowBottomBorder();
        boolean isAndWasComplete = tableModel.isComplete() && 0 == lastFlushedRowBottomBorder.size();

        if (!isFooterRenderer() && !isHeaderRenderer()) {
            if (isOriginalNonSplitRenderer) {
                bordersHandler = new CollapsedTableBorders(rows, numberOfColumns, getBorders(), !isAndWasComplete ? rowRange.getStartRow() : 0);
                bordersHandler.initializeBorders();
            }
        }
        bordersHandler.setRowRange(rowRange.getStartRow(), rowRange.getFinishRow());
        initializeHeaderAndFooter(0 == rowRange.getStartRow() || area.isEmptyArea());

        // update
        bordersHandler.updateBordersOnNewPage(isOriginalNonSplitRenderer, isFooterRenderer() || isHeaderRenderer(), this, headerRenderer, footerRenderer);
        if (isOriginalNonSplitRenderer) {
            correctRowRange();
        }

        if (isOriginalRenderer()) {
            calculateColumnWidths(layoutBox.getWidth());
        }
        float tableWidth = getTableWidth();

        if (layoutBox.getWidth() > tableWidth) {
            layoutBox.setWidth((float) tableWidth + bordersHandler.getRightBorderMaxWidth() / 2 + bordersHandler.getLeftBorderMaxWidth() / 2);
        }

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), (float) tableWidth, 0));

        if (footerRenderer != null) {
            // apply the difference to set footer and table left/right margins identical
            prepareFooterOrHeaderRendererForLayout(footerRenderer, layoutBox.getWidth());

            // collapse with top footer border
            if (0 != rows.size() || !isAndWasComplete) {
                bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, false);
            } else if (null != headerRenderer) {
                headerRenderer.bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, false);
            }

            LayoutResult result = footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            if (result.getStatus() != LayoutResult.FULL) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, result.getCauseOfNothing());
            }
            float footerHeight = result.getOccupiedArea().getBBox().getHeight();
            footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);

            if (!tableModel.isEmpty()) {
                float maxFooterTopBorderWidth = footerRenderer.bordersHandler.getMaxTopWidth();
                footerRenderer.occupiedArea.getBBox().decreaseHeight(maxFooterTopBorderWidth);
                layoutBox.moveDown(maxFooterTopBorderWidth).increaseHeight(maxFooterTopBorderWidth);
            }
            // we will delete FORCED_PLACEMENT property after adding one row
            // but the footer should be forced placed once more (since we renderer footer twice)
            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                footerRenderer.setProperty(Property.FORCED_PLACEMENT, true);
            }
        }

        if (headerRenderer != null) {
            prepareFooterOrHeaderRendererForLayout(headerRenderer, layoutBox.getWidth());
            if (0 != rows.size()) {
                bordersHandler.collapseTableWithHeader(headerRenderer.bordersHandler, !tableModel.isEmpty());
            } else if (null != footerRenderer){
                footerRenderer.bordersHandler.collapseTableWithHeader(headerRenderer.bordersHandler, true);
            }
            topBorderMaxWidth = bordersHandler.getMaxTopWidth(); // first row own top border. We will use it while header processing
            LayoutResult result = headerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            if (result.getStatus() != LayoutResult.FULL) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, result.getCauseOfNothing());
            }
            float headerHeight = result.getOccupiedArea().getBBox().getHeight();
            layoutBox.decreaseHeight(headerHeight);
            occupiedArea.getBBox().moveDown(headerHeight).increaseHeight(headerHeight);
            bordersHandler.fixHeaderOccupiedArea(occupiedArea.getBBox(), layoutBox);

        }


        bordersHandler.setTableBoundingBorders(getBorders());
        topBorderMaxWidth = bordersHandler.getMaxTopWidth();
        bordersHandler.applyLeftAndRightTableBorder(layoutBox, false);
        // Table should have a row and some child elements in order to be considered non empty
        bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox,
                tableModel.isEmpty() || 0 == rows.size(), isAndWasComplete, false);

        LayoutResult[] splits = new LayoutResult[numberOfColumns];
        // This represents the target row index for the overflow renderer to be placed to.
        // Usually this is just the current row id of a cell, but it has valuable meaning when a cell has rowspan.
        int[] targetOverflowRowIndex = new int[numberOfColumns];
        // if this is the last renderer, we will use that information to enlarge rows proportionally
        List<Boolean> rowsHasCellWithSetHeight = new ArrayList<>();

        for (row = 0; row < rows.size(); row++) {
            // if forced placement was earlier set, this means the element did not fit into the area, and in this case
            // we only want to place the first row in a forced way, not the next ones, otherwise they will be invisible
            if (row == 1 && Boolean.TRUE.equals(this.<Boolean>getProperty(Property.FORCED_PLACEMENT))) {
                if (Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.FORCED_PLACEMENT))) {
                    deleteOwnProperty(Property.FORCED_PLACEMENT);
                } else {
                    setProperty(Property.FORCED_PLACEMENT, false);
                }
            }

            CellRenderer[] currentRow = rows.get(row);
            float rowHeight = 0;
            boolean split = false;
            // Indicates that all the cells fit (at least partially after splitting if not forbidden by keepTogether) in the current row.
            boolean hasContent = true;
            // Indicates that we have added a cell from the future, i.e. a cell which has a big rowspan and we shouldn't have
            // added it yet, because we add a cell with rowspan only during the processing of the very last row this cell occupied,
            // but now we have area break and we had to force that cell addition.
            boolean cellWithBigRowspanAdded = false;
            List<CellRenderer> currChildRenderers = new ArrayList<>();
            // Process in a queue, because we might need to add a cell from the future, i.e. having big rowspan in case of split.
            Deque<CellRendererInfo> cellProcessingQueue = new ArrayDeque<CellRendererInfo>();
            for (col = 0; col < currentRow.length; col++) {
                if (currentRow[col] != null) {
                    cellProcessingQueue.addLast(new CellRendererInfo(currentRow[col], col, row));
                }
            }
            boolean rowHasCellWithSetHeight = false;
            // the element which was the first to cause Layout.Nothing
            IRenderer firstCauseOfNothing = null;

            // the width of the widest bottom border of the row
            bordersHandler.setFinishRow(rowRange.getStartRow() + row);
            Border widestRowBottomBorder = bordersHandler.getWidestHorizontalBorder(rowRange.getStartRow() + row + 1);
            bordersHandler.setFinishRow(rowRange.getFinishRow());
            float widestRowBottomBorderWidth = null == widestRowBottomBorder ? 0 : widestRowBottomBorder.getWidth();

            // if cell is in the last row on the page, its borders shouldn't collapse with the next row borders
            while (cellProcessingQueue.size() > 0) {
                CellRendererInfo currentCellInfo = cellProcessingQueue.pop();
                col = currentCellInfo.column;
                CellRenderer cell = currentCellInfo.cellRenderer;
                int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
                int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
                if (1 != rowspan) {
                    cellWithBigRowspanAdded = true;
                }
                targetOverflowRowIndex[col] = currentCellInfo.finishRowInd;
                // This cell came from the future (split occurred and we need to place cell with big rowpsan into the current area)
                boolean currentCellHasBigRowspan = (row != currentCellInfo.finishRowInd);
                if (cell.hasOwnOrModelProperty(Property.HEIGHT)) {
                    rowHasCellWithSetHeight = true;
                }
                float cellWidth = 0, colOffset = 0;
                for (int k = col; k < col + colspan; k++) {
                    cellWidth += countedColumnWidth[k];
                }
                for (int l = 0; l < col; l++) {
                    colOffset += countedColumnWidth[l];
                }
                float rowspanOffset = 0;
                for (int m = row - 1; m > currentCellInfo.finishRowInd - rowspan && m >= 0; m--) {
                    rowspanOffset += (float) heights.get(m);
                }
                float cellLayoutBoxHeight = rowspanOffset + (!currentCellHasBigRowspan || hasContent ? layoutBox.getHeight() : 0);
                float cellLayoutBoxBottom = layoutBox.getY() + (!currentCellHasBigRowspan || hasContent ? 0 : layoutBox.getHeight());
                Rectangle cellLayoutBox = new Rectangle(layoutBox.getX() + colOffset, cellLayoutBoxBottom, cellWidth, cellLayoutBoxHeight);
                LayoutArea cellArea = new LayoutArea(layoutContext.getArea().getPageNumber(), cellLayoutBox);
                VerticalAlignment verticalAlignment = cell.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT);
                cell.setProperty(Property.VERTICAL_ALIGNMENT, null);
                UnitValue cellWidthProperty = cell.<UnitValue>getProperty(Property.WIDTH);
                if (cellWidthProperty != null && cellWidthProperty.isPercentValue()) {
                    cell.setProperty(Property.WIDTH, UnitValue.createPointValue(cellWidth));
                }
                // Apply cell borders
                float[] cellIndents = bordersHandler.getCellBorderIndents(currentCellInfo.finishRowInd, col, rowspan, colspan);
                bordersHandler.applyCellIndents(cellArea.getBBox(), cellIndents[0], cellIndents[1], cellIndents[2] + widestRowBottomBorderWidth, cellIndents[3], false);
                // update cell width
                cellWidth = cellArea.getBBox().getWidth();
                LayoutResult cellResult = cell.setParent(this).layout(new LayoutContext(cellArea, null, floatRendererAreas));

                cell.setProperty(Property.VERTICAL_ALIGNMENT, verticalAlignment);
                // width of BlockRenderer depends on child areas, while in cell case it is hardly define.
                if (cellResult.getStatus() != LayoutResult.NOTHING) {
                    cell.getOccupiedArea().getBBox().setWidth(cellWidth);
                } else if (null == firstCauseOfNothing) {
                    firstCauseOfNothing = cellResult.getCauseOfNothing();
                }

                if (currentCellHasBigRowspan) {
                    // cell from the future
                    if (cellResult.getStatus() != LayoutResult.FULL) {
                        splits[col] = cellResult;
                        if (cellResult.getStatus() != LayoutResult.NOTHING) {
                            // one should disable cell alignment if it was split
                            splits[col].getOverflowRenderer().setProperty(Property.VERTICAL_ALIGNMENT, VerticalAlignment.TOP);
                        }
                    }
                    if (cellResult.getStatus() == LayoutResult.PARTIAL) {
                        currentRow[col] = (CellRenderer) cellResult.getSplitRenderer();
                    } else {
                        rows.get(currentCellInfo.finishRowInd)[col] = null;
                        currentRow[col] = cell;
                        rowMoves.put(col, currentCellInfo.finishRowInd);
                    }
                } else {
                    if (cellResult.getStatus() != LayoutResult.FULL) {
                        // first time split occurs
                        if (!split) {
                            int addCol;
                            // This is a case when last footer should be skipped and we might face an end of the table.
                            // We check if we can fit all the rows right now and the split occurred only because we reserved
                            // space for footer before, and if yes we skip footer and write all the content right now.
                            boolean skipLastFooter = null != footerRenderer && tableModel.isSkipLastFooter() && tableModel.isComplete();
                            if (skipLastFooter) {
                                LayoutArea potentialArea = new LayoutArea(area.getPageNumber(), layoutBox.clone());
                                // Fix layout area
                                Border widestRowTopBorder = bordersHandler.getWidestHorizontalBorder(rowRange.getStartRow() + row);
                                if (null != widestRowTopBorder) {
                                    potentialArea.getBBox().moveDown(widestRowTopBorder.getWidth() / 2).increaseHeight(widestRowTopBorder.getWidth() / 2);
                                }
                                float footerHeight = footerRenderer.getOccupiedArea().getBBox().getHeight();
                                potentialArea.getBBox().moveDown(footerHeight).increaseHeight(footerHeight);

                                TableRenderer overflowRenderer = createOverflowRenderer(new Table.RowRange(rowRange.getStartRow() + row, rowRange.getFinishRow()));
                                overflowRenderer.rows = rows.subList(row, rows.size());
                                overflowRenderer.setProperty(Property.IGNORE_HEADER, true);
                                overflowRenderer.setProperty(Property.IGNORE_FOOTER, true);
                                overflowRenderer.setProperty(Property.MARGIN_TOP, 0);
                                overflowRenderer.setProperty(Property.MARGIN_BOTTOM, 0);
                                overflowRenderer.setProperty(Property.MARGIN_LEFT, 0);
                                overflowRenderer.setProperty(Property.MARGIN_RIGHT, 0);
                                overflowRenderer.deleteOwnProperty(Property.BORDER_BOTTOM);

                                overflowRenderer.rowRange = new Table.RowRange(0, rows.size() - row - 1);
                                overflowRenderer.bordersHandler = bordersHandler;
                                // save old bordersHandler properties
                                bordersHandler.skipFooter(overflowRenderer.getBorders());
                                if (null != headerRenderer) {
                                    bordersHandler.skipHeader(overflowRenderer.getBorders());
                                }
                                int savedStartRow = overflowRenderer.bordersHandler.startRow;
                                overflowRenderer.bordersHandler.setStartRow(row);
                                prepareFooterOrHeaderRendererForLayout(overflowRenderer, layoutBox.getWidth());
                                LayoutResult res = overflowRenderer.layout(new LayoutContext(potentialArea));
                                bordersHandler.setStartRow(savedStartRow);
                                if (LayoutResult.FULL == res.getStatus()) {
                                    footerRenderer = null;
                                    // fix layout area and table bottom border
                                    layoutBox.increaseHeight(footerHeight).moveDown(footerHeight);
                                    deleteOwnProperty(Property.BORDER_BOTTOM);

                                    bordersHandler.setFinishRow(rowRange.getStartRow() + row);
                                    widestRowBottomBorder = bordersHandler.getWidestHorizontalBorder(rowRange.getStartRow() + row + 1);
                                    bordersHandler.setFinishRow(rowRange.getFinishRow());
                                    widestRowBottomBorderWidth = null == widestRowBottomBorder ? 0 : widestRowBottomBorder.getWidth();

                                    cellProcessingQueue.clear();
                                    currChildRenderers.clear();
                                    for (addCol = 0; addCol < currentRow.length; addCol++) {
                                        if (currentRow[addCol] != null) {
                                            cellProcessingQueue.addLast(new CellRendererInfo(currentRow[addCol], addCol, row));
                                        }
                                    }
                                    continue;
                                } else {
                                    int reusedRows = 0;
                                    if (null != res.getSplitRenderer()) {
                                        reusedRows = ((TableRenderer) res.getSplitRenderer()).rows.size();
                                    }
                                    for (int i = 0; i < numberOfColumns; i++) {
                                        if (null != rows.get(row + reusedRows)[i]) {
                                            rows.get(row + reusedRows)[i] = (CellRenderer) rows.get(row + reusedRows)[i].getModelElement().createRendererSubTree();
                                        }
                                    }
                                    if (null != headerRenderer) {
                                        bordersHandler.collapseTableWithHeader(headerRenderer.bordersHandler, true);
                                    }
                                    bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, true);
                                }
                            }

                            // Here we look for a cell with big rowspan (i.e. one which would not be normally processed in
                            // the scope of this row), and we add such cells to the queue, because we need to write them
                            // at least partially into the available area we have.
                            for (addCol = 0; addCol < currentRow.length; addCol++) {
                                if (currentRow[addCol] == null) {
                                    // Search for the next cell including rowspan.
                                    for (int addRow = row + 1; addRow < rows.size(); addRow++) {
                                        if (rows.get(addRow)[addCol] != null) {
                                            CellRenderer addRenderer = rows.get(addRow)[addCol];
                                            if (row + (int) addRenderer.getPropertyAsInteger(Property.ROWSPAN) - 1 >= addRow) {
                                                cellProcessingQueue.addLast(new CellRendererInfo(addRenderer, addCol, addRow));
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        split = true;
                        splits[col] = cellResult;
                        if (cellResult.getStatus() == LayoutResult.NOTHING) {
                            hasContent = false;
                            splits[col].getOverflowRenderer().setProperty(Property.VERTICAL_ALIGNMENT, verticalAlignment);
                        }
                    }
                }
                currChildRenderers.add(cell);
                if (cellResult.getStatus() != LayoutResult.NOTHING) {
                    rowHeight = Math.max(rowHeight, cellResult.getOccupiedArea().getBBox().getHeight() + bordersHandler.getCellVerticalAddition(cellIndents) - rowspanOffset);
                }
            }
            rowHeight = fixRowHeightIfFloatRendererPresented(rowHeight, floatRendererAreas);
            if (hasContent) {
                heights.add(rowHeight);
                rowsHasCellWithSetHeight.add(rowHasCellWithSetHeight);
                occupiedArea.getBBox().moveDown(rowHeight);
                occupiedArea.getBBox().increaseHeight(rowHeight);
                layoutBox.decreaseHeight(rowHeight);
            }

            if (split || row == rows.size() - 1) {
                bordersHandler.setFinishRow(bordersHandler.getStartRow() + row);
                if (!hasContent && bordersHandler.getFinishRow() != bordersHandler.getStartRow()) {
                    bordersHandler.setFinishRow(bordersHandler.getFinishRow() - 1);
                }
                boolean skip = false;
                if (null != footerRenderer && tableModel.isComplete() && tableModel.isSkipLastFooter() && !split) {
                    footerRenderer = null;
                    // delete #layout() related properties
                    deleteOwnProperty(Property.BORDER_BOTTOM);
                    if (tableModel.isEmpty()) {
                        this.deleteOwnProperty(Property.BORDER_TOP);
                    }
                    skip = true;
                }
                // Correct occupied areas of all added cells
                correctLayoutedCellsOccupiedAreas(splits, row, targetOverflowRowIndex, blockMinHeight, layoutBox, rowsHasCellWithSetHeight, !split, !hasContent && cellWithBigRowspanAdded, skip);
            }
            // process footer with collapsed borders
            if ((split || row == rows.size() - 1) && null != footerRenderer) {
                // maybe the table was incomplete and we can process the footer
                if (!isAndWasComplete && !hasContent && 0 == childRenderers.size()) {
                    bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox, true);
                } else {
                    bordersHandler.applyBottomTableBorder(occupiedArea.getBBox(), layoutBox, tableModel.isEmpty(), false, true);
                }
                layoutBox.moveDown(footerRenderer.occupiedArea.getBBox().getHeight()).increaseHeight(footerRenderer.occupiedArea.getBBox().getHeight());
                // apply the difference to set footer and table left/right margins identical
                bordersHandler.applyLeftAndRightTableBorder(layoutBox, true);
                prepareFooterOrHeaderRendererForLayout(footerRenderer, layoutBox.getWidth());

                bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, hasContent || 0 != childRenderers.size());
                footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
                bordersHandler.applyLeftAndRightTableBorder(layoutBox, false);
                float footerHeight = footerRenderer.getOccupiedAreaBBox().getHeight();
                footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
                layoutBox.setY(footerRenderer.occupiedArea.getBBox().getTop()).setHeight(occupiedArea.getBBox().getBottom() - layoutBox.getBottom());
            }
            if (!split) {
                childRenderers.addAll(currChildRenderers);
                currChildRenderers.clear();
            }
            if (split) {
                if (marginsCollapsingEnabled) {
                    marginsCollapseHandler.endMarginsCollapse(layoutBox);
                }
                TableRenderer[] splitResult = !split ? split(row + 1, false, cellWithBigRowspanAdded) : split(row, hasContent, cellWithBigRowspanAdded);
                // delete #layout() related properties
                if (null != headerRenderer || null != footerRenderer) {
                    if (null != headerRenderer || tableModel.isEmpty()) {
                        splitResult[1].deleteOwnProperty(Property.BORDER_TOP);
                    }
                    if (null != footerRenderer || tableModel.isEmpty()) {
                        splitResult[1].deleteOwnProperty(Property.BORDER_BOTTOM);
                    }
                }
                if (split) {
                    int[] rowspans = new int[currentRow.length];
                    boolean[] columnsWithCellToBeEnlarged = new boolean[currentRow.length];
                    for (col = 0; col < currentRow.length; col++) {
                        if (splits[col] != null) {
                            CellRenderer cellSplit = (CellRenderer) splits[col].getSplitRenderer();
                            if (null != cellSplit) {
                                rowspans[col] = cellSplit.getModelElement().getRowspan();
                            }
                            if (splits[col].getStatus() != LayoutResult.NOTHING && (hasContent || cellWithBigRowspanAdded)) {
                                childRenderers.add(cellSplit);
                            }
                            LayoutArea cellOccupiedArea = currentRow[col].getOccupiedArea();
                            if (hasContent || cellWithBigRowspanAdded || splits[col].getStatus() == LayoutResult.NOTHING) {
                                CellRenderer cellOverflow = (CellRenderer) splits[col].getOverflowRenderer();
                                currentRow[col] = null;
                                rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) cellOverflow.setParent(splitResult[1]);
                            } else {
                                rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) currentRow[col].setParent(splitResult[1]);
                            }
                            rows.get(targetOverflowRowIndex[col])[col].occupiedArea = cellOccupiedArea;
                        } else if (currentRow[col] != null) {
                            if (hasContent) {
                                rowspans[col] = currentRow[col].getModelElement().getRowspan();
                            }
                            boolean isBigRowspannedCell = 1 != currentRow[col].getModelElement().getRowspan();
                            if (hasContent || isBigRowspannedCell) {
                                columnsWithCellToBeEnlarged[col] = true;
                            }
                        }
                    }

                    int minRowspan = Integer.MAX_VALUE;
                    for (col = 0; col < rowspans.length; col++) {
                        if (0 != rowspans[col]) {
                            minRowspan = Math.min(minRowspan, rowspans[col]);
                        }
                    }

                    for (col = 0; col < numberOfColumns; col++) {
                        if (columnsWithCellToBeEnlarged[col]) {
                            LayoutArea cellOccupiedArea = currentRow[col].getOccupiedArea();
                            if (1 == minRowspan) {
                                // Here we use the same cell, but create a new renderer which doesn't have any children,
                                // therefore it won't have any content.
                                Cell overflowCell = currentRow[col].getModelElement().clone(true); // we will change properties
                                currentRow[col].isLastRendererForModelElement = false;
                                childRenderers.add(currentRow[col]);
                                currentRow[col] = null;
                                rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) overflowCell.getRenderer().setParent(this);
                                rows.get(targetOverflowRowIndex[col])[col].deleteProperty(Property.HEIGHT);
                                rows.get(targetOverflowRowIndex[col])[col].deleteProperty(Property.MIN_HEIGHT);
                                rows.get(targetOverflowRowIndex[col])[col].deleteProperty(Property.MAX_HEIGHT);
                            } else {
                                childRenderers.add(currentRow[col]);
                                // shift all cells in the column up
                                int i = row;
                                for (; i < row + minRowspan && i + 1 < rows.size() && rows.get(i + 1)[col] != null; i++) {
                                    rows.get(i)[col] = rows.get(i + 1)[col];
                                    rows.get(i + 1)[col] = null;
                                }
                                // the number of cells behind is less then minRowspan-1
                                // so we should process the last cell in the column as in the case 1 == minRowspan
                                if (i != row + minRowspan - 1 && null != rows.get(i)[col]) {
                                    Cell overflowCell = rows.get(i)[col].getModelElement();
                                    rows.get(i)[col].isLastRendererForModelElement = false;
                                    rows.get(i)[col] = null;
                                    rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) overflowCell.getRenderer().setParent(this);
                                }
                            }
                            rows.get(targetOverflowRowIndex[col])[col].occupiedArea = cellOccupiedArea;
                        }
                    }
                }
                // Apply borders if there is no footer
                if (null == footerRenderer) {
                    if (0 != this.childRenderers.size()) {
                        bordersHandler.applyBottomTableBorder(occupiedArea.getBBox(), layoutBox, false);
                    } else {
                        bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox, true);
                        // process bottom border of the last added row if there is no footer
                        if (!isAndWasComplete) {
                            bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox, 0 == childRenderers.size(), true, false);
                        }
                    }
                }
                if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))
                        || Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA_ON_SPLIT))) {
                    extendLastRow(currentRow, layoutBox);
                }
                adjustFooterAndFixOccupiedArea(layoutBox);

                // On the next page we need to process rows without any changes except moves connected to actual cell splitting
                for (Map.Entry<Integer, Integer> entry : rowMoves.entrySet()) {
                    // Move the cell back to its row if there was no actual split
                    if (null == splitResult[1].rows.get((int) entry.getValue() - splitResult[0].rows.size())[entry.getKey()]) {
                        splitResult[1].rows.get((int) entry.getValue() - splitResult[0].rows.size())[entry.getKey()] = splitResult[1].rows.get(row - splitResult[0].rows.size())[entry.getKey()];
                        splitResult[1].rows.get(row - splitResult[0].rows.size())[entry.getKey()] = null;
                    }
                }

                if ((isKeepTogether() && 0 == lastFlushedRowBottomBorder.size()) && !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                    return new LayoutResult(LayoutResult.NOTHING, null, null, this, null == firstCauseOfNothing ? this : firstCauseOfNothing);
                } else {
                    int status = ((occupiedArea.getBBox().getHeight() - (null == footerRenderer ? 0 : footerRenderer.getOccupiedArea().getBBox().getHeight()) == 0)
                            && isAndWasComplete)
                            ? LayoutResult.NOTHING
                            : LayoutResult.PARTIAL;
                    if ((status == LayoutResult.NOTHING && Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)))
                            || wasHeightClipped) {
                        if (wasHeightClipped) {
                            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                            logger.warn(LogMessageConstant.CLIP_ELEMENT);
                            // Process borders
                            if (status == LayoutResult.NOTHING) {
                                bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox, 0 == childRenderers.size(), true, false);
                                bordersHandler.applyBottomTableBorder(occupiedArea.getBBox(), layoutBox, 0 == childRenderers.size(), true, false);
                            }
                            // Notice that we extend the table only on the current page
                            if (null != blockMinHeight && blockMinHeight > occupiedArea.getBBox().getHeight()) {
                                float blockBottom = Math.max(occupiedArea.getBBox().getBottom() - ((float) blockMinHeight - occupiedArea.getBBox().getHeight()), layoutBox.getBottom());
                                if (0 == heights.size()) {
                                    heights.add(((float) blockMinHeight) - occupiedArea.getBBox().getHeight() / 2);
                                } else {
                                    heights.set(heights.size() - 1, heights.get(heights.size() - 1) + ((float) blockMinHeight) - occupiedArea.getBBox().getHeight());
                                }
                                occupiedArea.getBBox()
                                        .increaseHeight(occupiedArea.getBBox().getBottom() - blockBottom)
                                        .setY(blockBottom);
                            }
                        }
                        applyMargins(occupiedArea.getBBox(), true);
                        return new LayoutResult(LayoutResult.FULL, occupiedArea, splitResult[0], null);
                    } else {
                        applyMargins(occupiedArea.getBBox(), true);
                        if (hasProperty(Property.HEIGHT)) {
                            splitResult[1].setProperty(Property.HEIGHT, retrieveHeight() - occupiedArea.getBBox().getHeight());
                        }
                        if (hasProperty(Property.MAX_HEIGHT)) {
                            splitResult[1].setProperty(Property.MAX_HEIGHT, retrieveMaxHeight() - occupiedArea.getBBox().getHeight());
                        }
                        if (hasProperty(Property.MAX_HEIGHT)) {
                            splitResult[1].setProperty(Property.MAX_HEIGHT, retrieveMaxHeight() - occupiedArea.getBBox().getHeight());
                        }
                        return new LayoutResult(status, status != LayoutResult.NOTHING ? occupiedArea : null, splitResult[0], splitResult[1], null == firstCauseOfNothing ? this : firstCauseOfNothing);
                    }
                }
            }
        }
        // check if the last row is incomplete
        if (tableModel.isComplete() && !tableModel.isEmpty()) {
            CellRenderer[] lastRow = rows.get(rows.size() - 1);
            int lastInRow = lastRow.length - 1;
            while (lastInRow >= 0 && null == lastRow[lastInRow]) {
                lastInRow--;
            }
            if (lastInRow < 0 || lastRow.length != lastInRow + (int) lastRow[lastInRow].getPropertyAsInteger(Property.COLSPAN)) {
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.warn(LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE);
            }
        }

        // process footer renderer with collapsed borders
        if (tableModel.isComplete() && (0 != lastFlushedRowBottomBorder.size() || tableModel.isEmpty()) && null != footerRenderer) {
            layoutBox.moveDown(footerRenderer.occupiedArea.getBBox().getHeight()).increaseHeight(footerRenderer.occupiedArea.getBBox().getHeight());
            // apply the difference to set footer and table left/right margins identical
            bordersHandler.applyLeftAndRightTableBorder(layoutBox, true);
            prepareFooterOrHeaderRendererForLayout(footerRenderer, layoutBox.getWidth());
            if (0 != rows.size() || !isAndWasComplete) {
                bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, true);
            } else if (null != headerRenderer) {
                headerRenderer.bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, true);
            }

            footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            bordersHandler.applyLeftAndRightTableBorder(layoutBox, false);

            float footerHeight = footerRenderer.getOccupiedAreaBBox().getHeight();
            footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);
        }


        float bottomTableBorderWidth = bordersHandler.getMaxBottomWidth();
        // Apply bottom and top border
        if (tableModel.isComplete()) {
            if (null == footerRenderer) {
                if (0 != childRenderers.size()) {
                    bordersHandler.applyBottomTableBorder(occupiedArea.getBBox(), layoutBox, false);
                } else {
                    if (0 != lastFlushedRowBottomBorder.size()) {
                        bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox, 0 == childRenderers.size(), true, false);
                    } else {
                        bordersHandler.applyBottomTableBorder(occupiedArea.getBBox(), layoutBox, 0 == childRenderers.size(), true, false);
                    }
                }
            } else {
                if (tableModel.isEmpty() && null != headerRenderer) {
                    float headerBottomBorderWidth = headerRenderer.bordersHandler.getMaxBottomWidth();
                    headerRenderer.bordersHandler.applyBottomTableBorder(headerRenderer.occupiedArea.getBBox(), layoutBox, true, true, true);
                    occupiedArea.getBBox().moveUp(headerBottomBorderWidth).decreaseHeight(headerBottomBorderWidth);
                }
            }
        } else {
            // the bottom border should be processed and placed lately
            if (0 != heights.size()) {
                heights.set(heights.size() - 1, heights.get(heights.size() - 1) - bottomTableBorderWidth / 2);
            }
            if (null == footerRenderer) {
                if (0 != childRenderers.size()) {
                    bordersHandler.applyBottomTableBorder(occupiedArea.getBBox(), layoutBox, 0 == childRenderers.size(), false, true);
                }
            } else {
                // occupied area is right here
                layoutBox.increaseHeight(bottomTableBorderWidth);
            }
        }


        if (0 != rows.size()) {
            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))) {
                extendLastRow(rows.get(rows.size() - 1), layoutBox);
            }
        } else {
            if (null != blockMinHeight && blockMinHeight > occupiedArea.getBBox().getHeight()) {
                float blockBottom = Math.max(occupiedArea.getBBox().getBottom() - ((float) blockMinHeight - occupiedArea.getBBox().getHeight()), layoutBox.getBottom());
                if (0 != heights.size()) {
                    heights.set(heights.size() - 1, heights.get(heights.size() - 1) + occupiedArea.getBBox().getBottom() - blockBottom);
                } else {
                    heights.add((occupiedArea.getBBox().getBottom() - blockBottom) + occupiedArea.getBBox().getHeight() / 2);
                }

                occupiedArea.getBBox()
                        .increaseHeight(occupiedArea.getBBox().getBottom() - blockBottom)
                        .setY(blockBottom);
            }
        }

        if (isPositioned()) {
            if (isFixedLayout()) {
                float y = (float) this.getPropertyAsFloat(Property.Y);
                float relativeY = isFixedLayout() ? 0 : layoutBox.getY();
                move(0, relativeY + y - occupiedArea.getBBox().getY());
            }
        }

        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.endMarginsCollapse(layoutBox);
        }

        applyMargins(occupiedArea.getBBox(), true);
        // we should process incomplete table's footer only dureing splitting
        if (!tableModel.isComplete() && null != footerRenderer) {
            footerRenderer = null;
            bordersHandler.skipFooter(getBorders());
        }
        adjustFooterAndFixOccupiedArea(layoutBox);
        removeUnnecessaryFloatRendererAreas(floatRendererAreas);

        LayoutArea editedArea = applyFloatPropertyOnCurrentArea(floatRendererAreas, layoutContext.getArea().getBBox().getWidth());

        return new LayoutResult(LayoutResult.FULL, editedArea, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        PdfDocument document = drawContext.getDocument();
        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        boolean ignoreTag = false;
        PdfName role = null;
        if (isTagged) {
            role = ((IAccessibleElement) getModelElement()).getRole();
            boolean isHeaderOrFooter = PdfName.THead.equals(role) || PdfName.TFoot.equals(role);
            boolean ignoreHeaderFooterTag =
                    document.getTagStructureContext().getTagStructureTargetVersion().compareTo(PdfVersion.PDF_1_5) < 0;
            ignoreTag = isHeaderOrFooter && ignoreHeaderFooterTag;
        }
        if (role != null
                && !role.equals(PdfName.Artifact)
                && !ignoreTag) {
            TagStructureContext tagStructureContext = document.getTagStructureContext();
            TagTreePointer tagPointer = tagStructureContext.getAutoTaggingPointer();

            IAccessibleElement accessibleElement = (IAccessibleElement) getModelElement();
            if (!tagStructureContext.isElementConnectedToTag(accessibleElement)) {
                AccessibleAttributesApplier.applyLayoutAttributes(role, this, document);
            }

            Table modelElement = (Table) getModelElement();
            tagPointer.addTag(accessibleElement, true);

            super.draw(drawContext);

            tagPointer.moveToParent();

            boolean toRemoveConnectionsWithTag = isLastRendererForModelElement && modelElement.isComplete();
            if (toRemoveConnectionsWithTag) {
                tagPointer.removeElementConnectionToTag(accessibleElement);
            }
        } else {
            super.draw(drawContext);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawChildren(DrawContext drawContext) {
        Table modelElement = (Table) getModelElement();
        if (headerRenderer != null) {
            boolean firstHeader = rowRange.getStartRow() == 0 && isOriginalNonSplitRenderer && !modelElement.isSkipFirstHeader();
            boolean notToTagHeader = drawContext.isTaggingEnabled() && !firstHeader;
            if (notToTagHeader) {
                drawContext.setTaggingEnabled(false);
                drawContext.getCanvas().openTag(new CanvasArtifact());
            }
            headerRenderer.draw(drawContext);
            if (notToTagHeader) {
                drawContext.getCanvas().closeTag();
                drawContext.setTaggingEnabled(true);
            }
        }

        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement && !childRenderers.isEmpty();
        TagTreePointer tagPointer = null;
        boolean shouldHaveFooterOrHeaderTag = modelElement.getHeader() != null || modelElement.getFooter() != null;
        if (isTagged) {
            PdfName role = modelElement.getRole();
            if (role != null && !PdfName.Artifact.equals(role)) {
                tagPointer = drawContext.getDocument().getTagStructureContext().getAutoTaggingPointer();

                boolean ignoreHeaderFooterTag = drawContext.getDocument().getTagStructureContext()
                        .getTagStructureTargetVersion().compareTo(PdfVersion.PDF_1_5) < 0;
                shouldHaveFooterOrHeaderTag = shouldHaveFooterOrHeaderTag && !ignoreHeaderFooterTag
                        && (!modelElement.isSkipFirstHeader() || !modelElement.isSkipLastFooter());
                if (shouldHaveFooterOrHeaderTag) {
                    if (tagPointer.getKidsRoles().contains(PdfName.TBody)) {
                        tagPointer.moveToKid(PdfName.TBody);
                    } else {
                        tagPointer.addTag(PdfName.TBody);
                    }
                }
            } else {
                isTagged = false;
            }
        }

        for (IRenderer child : childRenderers) {
            if (isTagged) {
                int adjustByHeaderRowsNum = 0;
                if (modelElement.getHeader() != null && !modelElement.isSkipFirstHeader() && !shouldHaveFooterOrHeaderTag) {
                    adjustByHeaderRowsNum = modelElement.getHeader().getNumberOfRows();
                }
                int cellRow = ((Cell) child.getModelElement()).getRow() + adjustByHeaderRowsNum;
                int rowsNum = tagPointer.getKidsRoles().size();
                if (cellRow < rowsNum) {
                    tagPointer.moveToKid(cellRow);
                } else {
                    tagPointer.addTag(PdfName.TR);
                }
            }

            child.draw(drawContext);

            if (isTagged) {
                tagPointer.moveToParent();
            }
        }

        if (isTagged) {
            if (shouldHaveFooterOrHeaderTag) {
                tagPointer.moveToParent();
            }
        }

        drawBorders(drawContext);

        if (footerRenderer != null) {
            boolean lastFooter = isLastRendererForModelElement && modelElement.isComplete() && !modelElement.isSkipLastFooter();
            boolean notToTagFooter = drawContext.isTaggingEnabled() && !lastFooter;
            if (notToTagFooter) {
                drawContext.setTaggingEnabled(false);
                drawContext.getCanvas().openTag(new CanvasArtifact());
            }
            footerRenderer.draw(drawContext);
            if (notToTagFooter) {
                drawContext.getCanvas().closeTag();
                drawContext.setTaggingEnabled(true);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        TableRenderer nextTable = new TableRenderer();
        nextTable.modelElement = modelElement;
        return nextTable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void move(float dxRight, float dyUp) {
        super.move(dxRight, dyUp);
        if (headerRenderer != null) {
            headerRenderer.move(dxRight, dyUp);
        }
        if (footerRenderer != null) {
            footerRenderer.move(dxRight, dyUp);
        }
    }

    /**
     * @deprecated Method will be removed in 7.1.
     */
    @Deprecated
    protected float[] calculateScaledColumnWidths(Table tableModel, float tableWidth) {
        return countedColumnWidth;
    }

    protected TableRenderer[] split(int row) {
        return split(row, false);
    }

    protected TableRenderer[] split(int row, boolean hasContent) {
        return split(row, false, false);
    }


    protected TableRenderer[] split(int row, boolean hasContent, boolean cellWithBigRowspanAdded) {
        TableRenderer splitRenderer = createSplitRenderer(new Table.RowRange(rowRange.getStartRow(), rowRange.getStartRow() + row));
        splitRenderer.rows = rows.subList(0, row);

        splitRenderer.bordersHandler = bordersHandler;

        splitRenderer.heights = heights;
        splitRenderer.columnWidths = columnWidths;
        splitRenderer.countedColumnWidth = countedColumnWidth;
        splitRenderer.totalWidthForColumns = totalWidthForColumns;
        TableRenderer overflowRenderer = createOverflowRenderer(new Table.RowRange(rowRange.getStartRow() + row, rowRange.getFinishRow()));
        if (0 == row && !(hasContent || cellWithBigRowspanAdded) && 0 == rowRange.getStartRow()) {
            overflowRenderer.isOriginalNonSplitRenderer = true;
        }
        overflowRenderer.rows = rows.subList(row, rows.size());
        splitRenderer.occupiedArea = occupiedArea;

        overflowRenderer.bordersHandler = bordersHandler;

        return new TableRenderer[]{splitRenderer, overflowRenderer};
    }

    protected TableRenderer createSplitRenderer(Table.RowRange rowRange) {
        TableRenderer splitRenderer = (TableRenderer) getNextRenderer();
        splitRenderer.rowRange = rowRange;
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        // TODO childRenderers will be populated twice during the relayout.
        // We should probably clean them before #layout().
        splitRenderer.childRenderers = childRenderers;
        splitRenderer.addAllProperties(getOwnProperties());
        splitRenderer.headerRenderer = headerRenderer;
        splitRenderer.footerRenderer = footerRenderer;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.topBorderMaxWidth = topBorderMaxWidth;

        return splitRenderer;
    }

    protected TableRenderer createOverflowRenderer(Table.RowRange rowRange) {
        TableRenderer overflowRenderer = (TableRenderer) getNextRenderer();
        overflowRenderer.setRowRange(rowRange);
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        overflowRenderer.isOriginalNonSplitRenderer = false;
        overflowRenderer.countedColumnWidth = this.countedColumnWidth;
        return overflowRenderer;
    }

    @Override
    protected Float retrieveWidth(float parentBoxWidth) {
        Float tableWidth = super.retrieveWidth(parentBoxWidth);
        Table tableModel = (Table) getModelElement();
        if (tableWidth == null || tableWidth == 0) {
            float totalColumnWidthInPercent = 0;
            for (int col = 0; col < tableModel.getNumberOfColumns(); col++) {
                UnitValue columnWidth = tableModel.getColumnWidth(col);
                if (columnWidth.isPercentValue()) {
                    totalColumnWidthInPercent += columnWidth.getValue();
                }
            }
            tableWidth = parentBoxWidth;
            if (totalColumnWidthInPercent > 0) {
                tableWidth = parentBoxWidth * totalColumnWidthInPercent / 100;
            }
        }
        return tableWidth;
    }

    @Override
    MinMaxWidth getMinMaxWidth(float availableWidth) {
        initializeTableLayoutBorders();

        TableWidths tableWidths = new TableWidths(this, availableWidth, true, bordersHandler.getRightBorderMaxWidth(), bordersHandler.getLeftBorderMaxWidth());
        float minWidth;
        float[] columns;
        if (tableWidths.hasFixedLayout()) {
            columns = tableWidths.fixedLayout();
            minWidth = tableWidths.getMinWidth();
        } else {
            ColumnMinMaxWidth minMax = countTableMinMaxWidth(false);
            columns = tableWidths.autoLayout(minMax.getMinWidths(), minMax.getMaxWidths());
            minWidth = tableWidths.getMinWidth();
        }

        cleanTableLayoutBorders();

        float maxColTotalWidth = 0;
        for (float column : columns) {
            maxColTotalWidth += column;
        }
        float additionalWidth = (float) this.getPropertyAsFloat(Property.MARGIN_RIGHT) + (float) this.getPropertyAsFloat(Property.MARGIN_LEFT);
        return new MinMaxWidth(additionalWidth, availableWidth, minWidth, maxColTotalWidth);
    }

    private ColumnMinMaxWidth countTableMinMaxWidth(boolean initializeBorders) {
        if (initializeBorders) {
            initializeTableLayoutBorders();
        }

        ColumnMinMaxWidth footerColWidth = null;
        if (footerRenderer != null) {
            footerColWidth = footerRenderer.countRegionMinMaxWidth(null, null);
        }

        ColumnMinMaxWidth headerColWidth = null;
        if (headerRenderer != null) {
            headerColWidth = headerRenderer.countRegionMinMaxWidth(null, null);
        }

        ColumnMinMaxWidth tableColWidth = countRegionMinMaxWidth(headerColWidth, footerColWidth);

        if (initializeBorders) {
            cleanTableLayoutBorders();
        }

        return tableColWidth;
    }

    private ColumnMinMaxWidth countRegionMinMaxWidth(ColumnMinMaxWidth headerWidth, ColumnMinMaxWidth footerWidth) {
        Table tableModel = (Table) getModelElement();
        int nrow = rows.size();
        int ncol = tableModel.getNumberOfColumns();
        MinMaxWidth[][] cellsMinMaxWidth = new MinMaxWidth[nrow][];
        int[][] cellsColspan = new int[nrow][];
        for (int i = 0; i < cellsMinMaxWidth.length; i++) {
            cellsMinMaxWidth[i] = new MinMaxWidth[ncol];
            cellsColspan[i] = new int[ncol];
        }
        ColumnMinMaxWidth result = new ColumnMinMaxWidth(ncol);

        for (int row = 0; row < nrow; ++row) {
            for (int col = 0; col < ncol; ++col) {
                CellRenderer cell = rows.get(row)[col];
                if (cell != null) {
                    cell.setParent(this);
                    int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
                    int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
                    //We place the width of big cells in each row of in last column its occupied place and save it's colspan for convenience.
                    int finishCol = col + colspan - 1;
                    cellsMinMaxWidth[row][finishCol] = cell.getMinMaxWidth(MinMaxWidthUtils.getMax());
                    float[] indents = bordersHandler.getCellBorderIndents(row, col, rowspan, colspan);
                    cellsMinMaxWidth[row][finishCol].setAdditionalWidth(cellsMinMaxWidth[row][finishCol].getAdditionalWidth() + indents[1] / 2 + indents[3] / 2);
                    cellsColspan[row][finishCol] = colspan;
                    for (int i = 1; i < rowspan; ++i) {
                        cellsMinMaxWidth[row - i][finishCol] = cellsMinMaxWidth[row][finishCol];
                        cellsColspan[row - i][finishCol] = colspan;
                    }
                }
            }
        }

        //The DP is used to count each column width.
        //In next two arrays at the index 'i' will be the sum of corresponding widths of first 'i' columns.
        float[] maxColumnsWidth = new float[ncol + 1];
        float[] minColumnsWidth = new float[ncol + 1];
        minColumnsWidth[0] = 0;
        maxColumnsWidth[0] = 0;
        int curColspan;
        for (int col = 0; col < ncol; ++col) {
            for (int row = 0; row < nrow; ++row) {
                if (cellsMinMaxWidth[row][col] != null) {
                    curColspan = cellsColspan[row][col];
                    maxColumnsWidth[col + 1] = Math.max(maxColumnsWidth[col + 1], cellsMinMaxWidth[row][col].getMaxWidth() + maxColumnsWidth[col - curColspan + 1]);
                    minColumnsWidth[col + 1] = Math.max(minColumnsWidth[col + 1], cellsMinMaxWidth[row][col].getMinWidth() + minColumnsWidth[col - curColspan + 1]);
                } else {
                    maxColumnsWidth[col + 1] = Math.max(maxColumnsWidth[col + 1], maxColumnsWidth[col]);
                    minColumnsWidth[col + 1] = Math.max(minColumnsWidth[col + 1], minColumnsWidth[col]);
                }
            }
        }
        for (int col = 0; col < ncol; ++col) {
            result.minWidth[col] = minColumnsWidth[col + 1] - minColumnsWidth[col];
            result.maxWidth[col] = maxColumnsWidth[col + 1] - maxColumnsWidth[col];
        }
        if (headerWidth != null) {
            result.mergeWith(headerWidth);
        }
        if (footerWidth != null) {
            result.mergeWith(footerWidth);
        }
        return result;
    }

    private void initializeTableLayoutBorders() {
        bordersHandler = new CollapsedTableBorders(rows, ((Table) getModelElement()).getNumberOfColumns(), getBorders());
        bordersHandler.initializeBorders();
        bordersHandler.setTableBoundingBorders(getBorders());
        bordersHandler.setRowRange(rowRange.getStartRow(), rowRange.getFinishRow());
        initializeHeaderAndFooter(true);
        bordersHandler.updateBordersOnNewPage(isOriginalNonSplitRenderer, isFooterRenderer() || isHeaderRenderer(), this, headerRenderer, footerRenderer);
        correctRowRange();
    }

    private void cleanTableLayoutBorders() {
        footerRenderer = null;
        headerRenderer = null;
        // we may have deleted empty rows and now need to update table's rowrange
        this.rowRange = new Table.RowRange(rowRange.getStartRow(), bordersHandler.getFinishRow());
        //TODO do we need it?
        // delete set properties
        deleteOwnProperty(Property.BORDER_BOTTOM);
        deleteOwnProperty(Property.BORDER_TOP);
    }

    private void correctRowRange() {
        if (rows.size() < rowRange.getFinishRow() - rowRange.getStartRow() + 1) {
            rowRange = new Table.RowRange(rowRange.getStartRow(), rowRange.getStartRow() + rows.size() - 1);
        }
    }

    @Override
    public void drawBorder(DrawContext drawContext) {
        // Do nothing here. Itext7 handles cell and table borders collapse and draws result borders during #drawBorders()
    }

    protected void drawBorders(DrawContext drawContext) {
        drawBorders(drawContext, null != headerRenderer, null != footerRenderer);
    }

    @Deprecated
    protected void drawBorders(DrawContext drawContext, boolean hasHeader, boolean hasFooter) {
        float height = occupiedArea.getBBox().getHeight();
        if (null != footerRenderer) {
            height -= footerRenderer.occupiedArea.getBBox().getHeight();
        }
        if (null != headerRenderer) {
            height -= headerRenderer.occupiedArea.getBBox().getHeight();
        }
        if (height < EPS) {
            return;
        }

        float startX = getOccupiedArea().getBBox().getX() + bordersHandler.getLeftBorderMaxWidth() / 2;
        float startY = getOccupiedArea().getBBox().getY() + getOccupiedArea().getBBox().getHeight();
        if (null != headerRenderer) {
            startY -= headerRenderer.occupiedArea.getBBox().getHeight();
            startY += topBorderMaxWidth / 2;
        } else {
            startY -= topBorderMaxWidth / 2;
        }
        if (hasProperty(Property.MARGIN_TOP)) {
            Float topMargin = this.getPropertyAsFloat(Property.MARGIN_TOP);
            startY -= null == topMargin ? 0 : (float) topMargin;
        }
        if (hasProperty(Property.MARGIN_LEFT)) {
            Float leftMargin = this.getPropertyAsFloat(Property.MARGIN_LEFT);
            startX += +(null == leftMargin ? 0 : (float) leftMargin);
        }


        // process halves of the borders here
        if (childRenderers.size() == 0) {
            Border[] borders = this.getBorders();
            if (null != borders[0]) {
                if (null != borders[2]) {
                    if (0 == heights.size()) {
                        heights.add(0, borders[0].getWidth() / 2 + borders[2].getWidth() / 2);
                    }
                }
            } else if (null != borders[2]) {
                startY -= borders[2].getWidth() / 2;
            }
            if (0 == heights.size()) {
                heights.add(0f);
            }
        }

        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        if (isTagged) {
            drawContext.getCanvas().openTag(new CanvasArtifact());
        }

        // considering these values itext will draw table borders correctly
        boolean isTopTablePart = isTopTablePart();
        boolean isBottomTablePart = isBottomTablePart();
        boolean isComplete = getTable().isComplete();
        boolean isFooterRendererOfLargeTable = isFooterRendererOfLargeTable();


        float y1 = startY;
        if (isFooterRendererOfLargeTable) {
            bordersHandler.drawHorizontalBorder(0, startX, y1, drawContext.getCanvas(), countedColumnWidth);
        }
        if (0 != heights.size()) {
            y1 -= (float) heights.get(0);
        }
        for (int i = 1; i < heights.size(); i++) {
            bordersHandler.drawHorizontalBorder(i, startX, y1, drawContext.getCanvas(), countedColumnWidth);
            if (i < heights.size()) {
                y1 -= (float) heights.get(i);
            }
        }
        if (!isBottomTablePart && isComplete) {
            bordersHandler.drawHorizontalBorder(heights.size(), startX, y1, drawContext.getCanvas(), countedColumnWidth);
        }

        float x1 = startX;
        if (countedColumnWidth.length > 0) {
            x1 += countedColumnWidth[0];
        }
        for (int i = 1; i < bordersHandler.getNumberOfColumns(); i++) {
            bordersHandler.drawVerticalBorder(i, startY, x1, drawContext.getCanvas(), heights);
            if (i < countedColumnWidth.length) {
                x1 += countedColumnWidth[i];
            }
        }

        // Draw bounding borders. Vertical borders are the last to draw in order to collapse with header / footer
        if (isTopTablePart) {
            bordersHandler.drawHorizontalBorder(0, startX, startY, drawContext.getCanvas(), countedColumnWidth);
        }
        if (isBottomTablePart && isComplete) {
            bordersHandler.drawHorizontalBorder(heights.size(), startX, y1, drawContext.getCanvas(), countedColumnWidth);
        }
        // draw left
        bordersHandler.drawVerticalBorder(0, startY, startX, drawContext.getCanvas(), heights);
        // draw right
        bordersHandler.drawVerticalBorder(bordersHandler.getNumberOfColumns(), startY, x1, drawContext.getCanvas(), heights);

        if (isTagged) {
            drawContext.getCanvas().closeTag();
        }
    }

    float fixRowHeightIfFloatRendererPresented(float rowHeight, List<Rectangle> floatRenderers) {
        float maxHeight = 0;
        for (Rectangle floatRenderer: floatRenderers) {
            float floatRendererHeight = floatRenderer.getHeight();
            if (floatRendererHeight > maxHeight) {
                maxHeight = floatRendererHeight;
            }
        }
        return rowHeight + maxHeight;
    }
    /**
     * If there is some space left, we move footer up, because initially footer will be at the very bottom of the area.
     * We also adjust occupied area by footer size if it is present.
     *
     * @param layoutBox the layout box which represents the area which is left free.
     */
    private void adjustFooterAndFixOccupiedArea(Rectangle layoutBox) {
        if (footerRenderer != null) {
            footerRenderer.move(0, layoutBox.getHeight());
            float footerHeight = footerRenderer.getOccupiedArea().getBBox().getHeight();
            occupiedArea.getBBox().moveDown(footerHeight).increaseHeight(footerHeight);
        }
    }

    private void correctLayoutedCellsOccupiedAreas(LayoutResult[] splits, int row, int[] targetOverflowRowIndex,
                                                   Float blockMinHeight, Rectangle layoutBox,
                                                   List<Boolean> rowsHasCellWithSetHeight, boolean isLastRenderer,
                                                   boolean processBigRowspan, boolean skip) {
        // correct last height
        int finish = bordersHandler.getFinishRow();
        bordersHandler.setFinishRow(rowRange.getFinishRow());
        Border currentBorder = bordersHandler.getWidestHorizontalBorder(finish + 1);
        bordersHandler.setFinishRow(finish);
        if (skip) {
            // Update bordersHandler
            bordersHandler.skipFooter(getBorders());
        }
        float currentBottomIndent = null == currentBorder ? 0 : currentBorder.getWidth();
        float realBottomIndent = bordersHandler.getMaxBottomWidth();
        if (0 != heights.size()) {
            heights.set(heights.size() - 1, heights.get(heights.size() - 1) + (realBottomIndent - currentBottomIndent) / 2);
            // correct occupied area and layoutbox
            occupiedArea.getBBox().increaseHeight((realBottomIndent - currentBottomIndent) / 2).moveDown((realBottomIndent - currentBottomIndent) / 2);
            layoutBox.decreaseHeight((realBottomIndent - currentBottomIndent) / 2);
            if (processBigRowspan) {
                // process the last row and correct its height
                CellRenderer[] currentRow = rows.get(heights.size());
                for (int col = 0; col < currentRow.length; col++) {
                    CellRenderer cell = null == splits[col] ? currentRow[col] : (CellRenderer) splits[col].getSplitRenderer();
                    if (cell == null) {
                        continue;
                    }
                    float height = 0;
                    int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
                    int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
                    float[] indents = bordersHandler.getCellBorderIndents(targetOverflowRowIndex[col], col, rowspan, colspan);
                    for (int l = heights.size() - 1 - 1; l > targetOverflowRowIndex[col] - rowspan && l >= 0; l--) {
                        height += (float) heights.get(l);
                    }
                    float cellHeightInLastRow = cell.getOccupiedArea().getBBox().getHeight() + indents[0] / 2 + indents[2] / 2 - height;
                    if (heights.get(heights.size() - 1) < cellHeightInLastRow) {
                        heights.set(heights.size() - 1, cellHeightInLastRow);
                    }
                }
            }
        }
        float additionalCellHeight = 0;
        int numOfRowsWithFloatHeight = 0;
        if (isLastRenderer) {
            float additionalHeight = 0;
            if (null != blockMinHeight && blockMinHeight > occupiedArea.getBBox().getHeight() + realBottomIndent / 2) {
                additionalHeight = Math.min(layoutBox.getHeight() - realBottomIndent / 2, (float) blockMinHeight - occupiedArea.getBBox().getHeight() - realBottomIndent / 2);
                for (int k = 0; k < rowsHasCellWithSetHeight.size(); k++) {
                    if (Boolean.FALSE.equals(rowsHasCellWithSetHeight.get(k))) {
                        numOfRowsWithFloatHeight++;
                    }
                }
            }
            additionalCellHeight = additionalHeight / (0 == numOfRowsWithFloatHeight ? heights.size() : numOfRowsWithFloatHeight);
            for (int k = 0; k < heights.size(); k++) {
                if (0 == numOfRowsWithFloatHeight || Boolean.FALSE.equals(rowsHasCellWithSetHeight.get(k))) {
                    heights.set(k, (float) heights.get(k) + additionalCellHeight);
                }
            }
        }
        float cumulativeShift = 0;
        // Correct occupied areas of all added cells
        for (int k = 0; k < heights.size(); k++) {
            correctRowCellsOccupiedAreas(splits, row, targetOverflowRowIndex, k, rowsHasCellWithSetHeight, cumulativeShift, additionalCellHeight);
            if (isLastRenderer) {
                if (0 == numOfRowsWithFloatHeight || Boolean.FALSE.equals(rowsHasCellWithSetHeight.get(k))) {
                    cumulativeShift += additionalCellHeight;
                }
            }
        }
        // extend occupied area, if some rows have been extended
        occupiedArea.getBBox().moveDown(cumulativeShift).increaseHeight(cumulativeShift);
        layoutBox.decreaseHeight(cumulativeShift);
    }

    private void correctRowCellsOccupiedAreas(LayoutResult[] splits, int row, int[] targetOverflowRowIndex, int currentRowIndex,
                                              List<Boolean> rowsHasCellWithSetHeight, float cumulativeShift, float additionalCellHeight) {
        CellRenderer[] currentRow = rows.get(currentRowIndex);
        for (int col = 0; col < currentRow.length; col++) {
            CellRenderer cell = (currentRowIndex < row || null == splits[col]) ? currentRow[col] : (CellRenderer) splits[col].getSplitRenderer();
            if (cell == null) {
                continue;
            }
            float height = 0;
            int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
            int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
            float rowspanOffset = 0;
            float[] indents = bordersHandler.getCellBorderIndents(currentRowIndex < row ? currentRowIndex : targetOverflowRowIndex[col], col, rowspan, colspan);
            // process rowspan
            for (int l = (currentRowIndex < row ? currentRowIndex : heights.size() - 1) - 1; l > (currentRowIndex < row ? currentRowIndex : targetOverflowRowIndex[col]) - rowspan && l >= 0; l--) {
                height += (float) heights.get(l);
                if (Boolean.FALSE.equals(rowsHasCellWithSetHeight.get(l))) {
                    rowspanOffset += additionalCellHeight;
                }
            }
            height += (float) heights.get(currentRowIndex < row ? currentRowIndex : heights.size() - 1);
            height -= indents[0] / 2 + indents[2] / 2;
            // Correcting cell bbox only. We don't need #move() here.
            // This is because of BlockRenderer's specificity regarding occupied area.
            float shift = height - cell.getOccupiedArea().getBBox().getHeight();
            Rectangle bBox = cell.getOccupiedArea().getBBox();
            bBox.moveDown(shift);
            cell.move(0, -(cumulativeShift - rowspanOffset));
            bBox.setHeight(height);
            cell.applyVerticalAlignment();
        }
    }

    protected void extendLastRow(CellRenderer[] lastRow, Rectangle freeBox) {
        if (null != lastRow && 0 != heights.size()) {
            heights.set(heights.size() - 1, heights.get(heights.size() - 1) + freeBox.getHeight());
            occupiedArea.getBBox().moveDown(freeBox.getHeight()).increaseHeight(freeBox.getHeight());
            for (CellRenderer cell : lastRow) {
                if (null != cell) {
                    cell.occupiedArea.getBBox().moveDown(freeBox.getHeight()).increaseHeight(freeBox.getHeight());
                }
            }
            freeBox.moveUp(freeBox.getHeight()).setHeight(0);
        }
    }

    /**
     * This method is used to set row range for table renderer during creating a new renderer.
     * The purpose to use this method is to remove input argument RowRange from createOverflowRenderer
     * and createSplitRenderer methods.
     */
    private void setRowRange(Table.RowRange rowRange) {
        this.rowRange = rowRange;
        for (int row = rowRange.getStartRow(); row <= rowRange.getFinishRow(); row++) {
            rows.add(new CellRenderer[((Table) modelElement).getNumberOfColumns()]);
        }
    }

    private TableRenderer initFooterOrHeaderRenderer(boolean footer, Border[] tableBorders) {
        Table table = (Table) getModelElement();
        Table footerOrHeader = footer ? table.getFooter() : table.getHeader();
        int innerBorder = footer ? 0 : 2;
        int outerBorder = footer ? 2 : 0;
        TableRenderer renderer = (TableRenderer) footerOrHeader.createRendererSubTree().setParent(this);
        Border[] borders = renderer.getBorders();
        if (table.isEmpty()) {
            renderer.setBorders(CollapsedTableBorders.getCollapsedBorder(borders[innerBorder], tableBorders[innerBorder]), innerBorder);
            setBorders(Border.NO_BORDER, innerBorder);
        }
        renderer.setBorders(CollapsedTableBorders.getCollapsedBorder(borders[1], tableBorders[1]), 1);
        renderer.setBorders(CollapsedTableBorders.getCollapsedBorder(borders[3], tableBorders[3]), 3);
        renderer.setBorders(CollapsedTableBorders.getCollapsedBorder(borders[outerBorder], tableBorders[outerBorder]), outerBorder);
        setBorders(Border.NO_BORDER, outerBorder);

        renderer.bordersHandler = new CollapsedTableBorders(renderer.rows, ((Table) renderer.getModelElement()).getNumberOfColumns(), renderer.getBorders());
        renderer.bordersHandler.initializeBorders();
        renderer.bordersHandler.setRowRange(renderer.rowRange.getStartRow(), renderer.rowRange.getFinishRow());
        ((CollapsedTableBorders) renderer.bordersHandler).collapseAllBordersAndEmptyRows();
        renderer.correctRowRange();
        // update bounding borders
        bordersHandler.setTableBoundingBorders(getBorders());
        return renderer;
    }

    private TableRenderer prepareFooterOrHeaderRendererForLayout(TableRenderer renderer, float layoutBoxWidth) {
        renderer.countedColumnWidth = countedColumnWidth;
        renderer.bordersHandler.leftBorderMaxWidth = bordersHandler.getLeftBorderMaxWidth();
        renderer.bordersHandler.rightBorderMaxWidth = bordersHandler.getRightBorderMaxWidth();
        if (hasProperty(Property.WIDTH)) {
            renderer.setProperty(Property.WIDTH, UnitValue.createPointValue(layoutBoxWidth));
        }
        return this;
    }


    private boolean isHeaderRenderer() {
        return parent instanceof TableRenderer && ((TableRenderer) parent).headerRenderer == this;
    }

    private boolean isFooterRenderer() {
        return parent instanceof TableRenderer && ((TableRenderer) parent).footerRenderer == this;
    }

    private boolean isFooterRendererOfLargeTable() {
        return isFooterRenderer() && (!getTable().isComplete() || 0 != ((TableRenderer) parent).getTable().getLastRowBottomBorder().size());
    }

    private boolean isTopTablePart() {
        return null == headerRenderer
                && (!isFooterRenderer() || (0 == ((TableRenderer) parent).rows.size() && null == ((TableRenderer) parent).headerRenderer));
    }

    private boolean isBottomTablePart() {
        return null == footerRenderer
                && (!isHeaderRenderer() || (0 == ((TableRenderer) parent).rows.size() && null == ((TableRenderer) parent).footerRenderer));
    }
        /**
     * Returns minWidth
     */
    private float calculateColumnWidths(float availableWidth) {
        if (countedColumnWidth == null || totalWidthForColumns != availableWidth) {
            TableWidths tableWidths = new TableWidths(this, availableWidth, false, bordersHandler.rightBorderMaxWidth, bordersHandler.leftBorderMaxWidth);
            if (tableWidths.hasFixedLayout()) {
                countedColumnWidth = tableWidths.fixedLayout();
                return tableWidths.getMinWidth();
            } else {
                ColumnMinMaxWidth minMax = countTableMinMaxWidth(false);
                countedColumnWidth = tableWidths.autoLayout(minMax.getMinWidths(), minMax.getMaxWidths());
                return tableWidths.getMinWidth();
            }
        }
        return -1;
    }

    private float getTableWidth() {
        float sum = 0;
        for (float column : countedColumnWidth) {
            sum += column;
        }
        return sum + bordersHandler.getRightBorderMaxWidth() / 2 + bordersHandler.getLeftBorderMaxWidth() / 2;
    }

    /**
     * This are a structs used for convenience in layout.
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

    private static class ColumnMinMaxWidth {
        float[] minWidth;
        float[] maxWidth;
        private float layoutBoxWidth;

        float[] getMinWidths() {
            return minWidth;
        }

        float[] getMaxWidths() {
            return maxWidth;
        }

        ColumnMinMaxWidth(int ncol) {
            minWidth = new float[ncol];
            maxWidth = new float[ncol];
        }

        void mergeWith(ColumnMinMaxWidth other) {
            int n = Math.min(minWidth.length, other.minWidth.length);
            for (int i = 0; i < n; ++i) {
                minWidth[i] = Math.max(minWidth[i], other.minWidth[i]);
                maxWidth[i] = Math.max(maxWidth[i], other.maxWidth[i]);
            }
        }

        MinMaxWidth toTableMinMaxWidth(float availableWidth) {
            float additionalWidth = availableWidth - layoutBoxWidth;
            float minColTotalWidth = 0;
            float maxColTotalWidth = 0;
            for (int i = 0; i < minWidth.length; ++i) {
                minColTotalWidth += minWidth[i];
                maxColTotalWidth += maxWidth[i];
            }
            return new MinMaxWidth(additionalWidth, availableWidth, minColTotalWidth, maxColTotalWidth);
        }

        ColumnMinMaxWidth setLayoutBoxWidth(float width) {
            this.layoutBoxWidth = width;
            return this;
        }
    }
}
