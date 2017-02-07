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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
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
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //TODO remove
    private float[] countedMinColumnWidth;
    private float[] countedMaxColumnWidth;

    private float[] countedColumnWidth = null;
    private float totalWidthForColumns;

    private float leftBorderMaxWidth;
    private float rightBorderMaxWidth;

    private TableBorders bordersHandler;

    protected int horizontalBordersIndexOffset;
    protected int verticalBordersIndexOffset;

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
        boolean headerShouldBeApplied = !rows.isEmpty() && (isFirstOnThePage && (!table.isSkipFirstHeader() || !isFirstHeader))
                && !Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.IGNORE_HEADER));
        if (headerElement != null && headerShouldBeApplied) {
            headerRenderer = initFooterOrHeaderRenderer(false, tableBorder);
        }
    }

    private void collapseAllBorders() {
        // TODO
        Border[] tableBorder = getBorders();
        int numberOfColumns = getTable().getNumberOfColumns();
        // collapse all cell borders
        if (null != rows && !isFooterRenderer() && !isHeaderRenderer()) {
            if (isOriginalNonSplitRenderer) {
                bordersHandler.collapseAllBordersAndEmptyRows(rows, getBorders(), 0, rows.size() - 1, numberOfColumns);
            } else {
                updateFirstRowBorders(numberOfColumns);
            }
        }

        if (isOriginalNonSplitRenderer && !isFooterRenderer() && !isHeaderRenderer()) {
            rightBorderMaxWidth = bordersHandler.getMaxRightWidth(tableBorder[1]);
            leftBorderMaxWidth = bordersHandler.getMaxLeftWidth(tableBorder[3]);
        }
        // TODO process header / footer
        if (footerRenderer != null) {
            footerRenderer.processRendererBorders(numberOfColumns);
            float rightFooterBorderWidth = footerRenderer.bordersHandler.getMaxRightWidth(footerRenderer.getBorders()[1]);
            float leftFooterBorderWidth = footerRenderer.bordersHandler.getMaxLeftWidth(footerRenderer.getBorders()[3]);

            if (isOriginalNonSplitRenderer && !isFooterRenderer() && !isHeaderRenderer()) {
                leftBorderMaxWidth = Math.max(leftBorderMaxWidth, leftFooterBorderWidth);
                rightBorderMaxWidth = Math.max(rightBorderMaxWidth, rightFooterBorderWidth);
            }
        }

        if (headerRenderer != null) {
            headerRenderer.processRendererBorders(numberOfColumns);
            float rightHeaderBorderWidth = headerRenderer.bordersHandler.getMaxRightWidth(headerRenderer.getBorders()[1]);
            float leftHeaderBorderWidth = headerRenderer.bordersHandler.getMaxLeftWidth(headerRenderer.getBorders()[3]);

            if (isOriginalNonSplitRenderer && !isHeaderRenderer() && !isFooterRenderer()) {
                leftBorderMaxWidth = Math.max(leftBorderMaxWidth, leftHeaderBorderWidth);
                rightBorderMaxWidth = Math.max(rightBorderMaxWidth, rightHeaderBorderWidth);
            }
        }
    }

    // important to invoke on each new page
    private void updateFirstRowBorders(int colN) {
        int col = 0;
        int row = 0;
        while (col < colN) {
            if (null != rows.get(row)[col]) {
                // we may have deleted collapsed border property trying to process the row as last on the page
                Border collapsedBottomBorder = null;
                int colspan = (int) rows.get(row)[col].getPropertyAsInteger(Property.COLSPAN);
                for (int i = col; i < col + colspan; i++) {
                    collapsedBottomBorder = TableBorders.getCollapsedBorder(collapsedBottomBorder, bordersHandler.horizontalBorders.get(horizontalBordersIndexOffset + row + 1).get(i));
                }
                rows.get(row)[col].setBorders(collapsedBottomBorder, 2);
                col += colspan;
                row = 0;
            } else {
                row++;
                if (row == rows.size()) {
                    break;
                }
            }
        }
    }

    // collapse with table border or header bottom borders
    protected void correctFirstRowTopBorders(Border tableBorder, int colN) {
        int row = 0;
        List<Border> bordersToBeCollapsedWith = null != headerRenderer
                ? headerRenderer.bordersHandler.horizontalBorders.get(headerRenderer.bordersHandler.horizontalBorders.size() - 1)
                : new ArrayList<Border>();
        if (null == headerRenderer) {
            for (int col = 0; col < colN; col++) {
                bordersToBeCollapsedWith.add(tableBorder);
            }
        }
        int col = 0;
        while (col < colN) {
            if (null != rows.get(row)[col] && row + 1 == (int) rows.get(row)[col].getPropertyAsInteger(Property.ROWSPAN)) {
                Border oldTopBorder = rows.get(row)[col].getBorders()[0];
                Border resultCellTopBorder = null;
                Border collapsedBorder = null;
                int colspan = (int) rows.get(row)[col].getPropertyAsInteger(Property.COLSPAN);
                for (int i = col; i < col + colspan; i++) {
                    collapsedBorder = TableBorders.getCollapsedBorder(oldTopBorder, bordersToBeCollapsedWith.get(i));
                    if (null == resultCellTopBorder || (null != collapsedBorder && resultCellTopBorder.getWidth() < collapsedBorder.getWidth())) {
                        resultCellTopBorder = collapsedBorder;
                    }
                }
                rows.get(row)[col].setBorders(resultCellTopBorder, 0);
                col += colspan;
                row = 0;
            } else {
                row++;
                if (row == rows.size()) {
                    break;
                }
            }
        }
    }

    protected boolean[] collapseFooterBorders(List<Border> tableBottomBorders, int colNum, int rowNum) {
        boolean[] useFooterBorders = new boolean[colNum];
        int row = 0;
        int col = 0;
        while (col < colNum) {
            if (null != footerRenderer.rows.get(row)[col]) {
                Border oldBorder = footerRenderer.rows.get(row)[col].getBorders()[0];
                Border maxBorder = oldBorder;
                for (int k = col; k < col + footerRenderer.rows.get(row)[col].getModelElement().getColspan(); k++) {
                    Border collapsedBorder = tableBottomBorders.get(k);
                    if (null != collapsedBorder && (null == oldBorder || collapsedBorder.getWidth() >= oldBorder.getWidth())) {
                        if (null == maxBorder || maxBorder.getWidth() < collapsedBorder.getWidth()) {
                            maxBorder = collapsedBorder;
                        }
                    } else {
                        useFooterBorders[k] = true;
                    }
                }
                footerRenderer.rows.get(row)[col].setBorders(maxBorder, 0);
                col += footerRenderer.rows.get(row)[col].getModelElement().getColspan();
                row = 0;
            } else {
                row++;
                if (row == rowNum) {
                    break;
                }
            }
        }
        return useFooterBorders;
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
        if (!((Table) modelElement).isComplete()) {
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
        Map<Integer, Integer> rowMoves = new HashMap<Integer, Integer>();

        MarginsCollapseHandler marginsCollapseHandler = null;
        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler = new MarginsCollapseHandler(this, layoutContext.getMarginsCollapseInfo());
            marginsCollapseHandler.startMarginsCollapse(layoutBox);
        }
        applyMargins(layoutBox, false);

        int row, col;

        if (isPositioned()) {
            float x = (float) this.getPropertyAsFloat(Property.X);
            float relativeX = isFixedLayout() ? 0 : layoutBox.getX();
            layoutBox.setX(relativeX + x);
        }

        Table tableModel = (Table) getModelElement();

        if (null != blockMaxHeight && blockMaxHeight < layoutBox.getHeight()
                && !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
            layoutBox.moveUp(layoutBox.getHeight() - (float) blockMaxHeight).setHeight((float) blockMaxHeight);
            wasHeightClipped = true;
        }

        int numberOfColumns = ((Table) getModelElement()).getNumberOfColumns();

        // The last flushed row. Empty list if the table hasn't been set incomplete
        List<Border> lastFlushedRowBottomBorder = tableModel.getLastRowBottomBorder();
        Border widestLustFlushedBorder = null;
        for (Border border : lastFlushedRowBottomBorder) {
            if (null != border && (null == widestLustFlushedBorder || widestLustFlushedBorder.getWidth() < border.getWidth())) {
                widestLustFlushedBorder = border;
            }
        }

        if (isOriginalRenderer()) {
            if (!isFooterRenderer() && !isHeaderRenderer()) {
                bordersHandler = new TableBorders(rows, numberOfColumns);
                bordersHandler.setTableBoundingBorders(getBorders());
            }
            bordersHandler.initializeBorders(lastFlushedRowBottomBorder, area.isEmptyArea());
        } else {
            if (!isFooterRenderer() && !isHeaderRenderer()) {
                bordersHandler.setRows(rows);
            }
        }

        initializeHeaderAndFooter(0 == rowRange.getStartRow() || area.isEmptyArea());
        collapseAllBorders();

        if (isOriginalRenderer()) {
            calculateColumnWidths(layoutBox.getWidth(), false);
        }
        float tableWidth = getTableWidth();

        if (layoutBox.getWidth() > tableWidth) {
            layoutBox.setWidth((float) tableWidth + rightBorderMaxWidth / 2 + leftBorderMaxWidth / 2);
        }

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), (float) tableWidth, 0));

        if (footerRenderer != null) {
            // apply the difference to set footer and table left/right margins identical
            prepareFooterOrHeaderRendererForLayout(footerRenderer, layoutBox.getWidth());
            LayoutResult result = footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            if (result.getStatus() != LayoutResult.FULL) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, result.getCauseOfNothing());
            }
            float footerHeight = result.getOccupiedArea().getBBox().getHeight();
            footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);

            if (!tableModel.isEmpty()) {
                float maxFooterTopBorderWidth = footerRenderer.bordersHandler.getMaxTopWidth(footerRenderer.bordersHandler.tableBoundingBorders[0]);
                footerRenderer.occupiedArea.getBBox().decreaseHeight(maxFooterTopBorderWidth);
                layoutBox.moveDown(maxFooterTopBorderWidth).increaseHeight(maxFooterTopBorderWidth);
            }
            // we will delete FORCED_PLACEMENT property after adding one row
            // but the footer should be forced placed once more (since we renderer footer twice)
            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                footerRenderer.setProperty(Property.FORCED_PLACEMENT, true);
            }
        }

        float topTableBorderWidth = bordersHandler.getMaxTopWidth(null); // first row own top border. We will use it in header processing

        if (headerRenderer != null) {
            prepareFooterOrHeaderRendererForLayout(headerRenderer, layoutBox.getWidth());
            LayoutResult result = headerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            if (result.getStatus() != LayoutResult.FULL) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, result.getCauseOfNothing());
            }
            float headerHeight = result.getOccupiedArea().getBBox().getHeight();
            layoutBox.decreaseHeight(headerHeight);
            occupiedArea.getBBox().moveDown(headerHeight).increaseHeight(headerHeight);

            float maxHeaderBottomBorderWidth = headerRenderer.bordersHandler.getMaxBottomWidth(headerRenderer.bordersHandler.tableBoundingBorders[2]);
            if (!tableModel.isEmpty()) {
                if (maxHeaderBottomBorderWidth < topTableBorderWidth) {
                    // fix
                    headerRenderer.heights.set(headerRenderer.heights.size() - 1,
                            headerRenderer.heights.get(headerRenderer.heights.size() - 1) + (topTableBorderWidth - maxHeaderBottomBorderWidth) / 2);
                    headerRenderer.occupiedArea.getBBox()
                            .moveDown(topTableBorderWidth - maxHeaderBottomBorderWidth)
                            .increaseHeight(topTableBorderWidth - maxHeaderBottomBorderWidth);
                    occupiedArea.getBBox()
                            .moveDown(topTableBorderWidth - maxHeaderBottomBorderWidth)
                            .increaseHeight(topTableBorderWidth - maxHeaderBottomBorderWidth);
                    layoutBox.decreaseHeight(topTableBorderWidth - maxHeaderBottomBorderWidth);
                } else {
                    topTableBorderWidth = maxHeaderBottomBorderWidth;
                }
                // correct in a view of table handling
                layoutBox.increaseHeight(topTableBorderWidth);
                occupiedArea.getBBox().moveUp(topTableBorderWidth).decreaseHeight(topTableBorderWidth);
            }
        }

        Border[] borders = getBorders();
        if (null != rows && 0 != rows.size()) {
            correctFirstRowTopBorders(borders[0], numberOfColumns);
            bordersHandler.correctTopBorder(null == headerRenderer ? null : headerRenderer.bordersHandler);
        }
        float bottomTableBorderWidth = null == borders[2] ? 0 : borders[2].getWidth();

        topTableBorderWidth = bordersHandler.getMaxTopWidth(borders[0]);

        // Apply halves of the borders. The other halves are applied on a Cell level
        layoutBox.<Rectangle>applyMargins(0, rightBorderMaxWidth / 2, 0, leftBorderMaxWidth / 2, false);
        // Table should have a row and some child elements in order to be considered non empty
        if (!tableModel.isEmpty() && 0 != rows.size()) {
            layoutBox.decreaseHeight(topTableBorderWidth / 2);
            occupiedArea.getBBox().moveDown(topTableBorderWidth / 2).increaseHeight(topTableBorderWidth / 2);
        } else if (tableModel.isComplete() && 0 == lastFlushedRowBottomBorder.size()) { // process empty table
            layoutBox.decreaseHeight(topTableBorderWidth);
            occupiedArea.getBBox().moveDown(topTableBorderWidth).increaseHeight(topTableBorderWidth);
        }

        LayoutResult[] splits = new LayoutResult[numberOfColumns];
        // This represents the target row index for the overflow renderer to be placed to.
        // Usually this is just the current row id of a cell, but it has valuable meaning when a cell has rowspan.
        int[] targetOverflowRowIndex = new int[numberOfColumns];

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
            // the element which was the first to cause Layout.Nothing
            IRenderer firstCauseOfNothing = null;

            // the width of the widest bottom border of the row
            bottomTableBorderWidth = 0;

            Border widestRowBottomBorder = bordersHandler.getWidestHorizontalBorder(row + 1);

            // if cell is in the last row on the page, its borders shouldn't collapse with the next row borders
            boolean processAsLast = false;
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
                Border oldBottomBorder = cell.getBorders()[2];
                Border collapsedBottomBorder = TableBorders.getCollapsedBorder(oldBottomBorder, footerRenderer != null ? footerRenderer.bordersHandler.horizontalBorders.get(0).get(col) : borders[2]);
                if (collapsedBottomBorder != null) {
                    float diff = Math.max(collapsedBottomBorder.getWidth(), null != widestRowBottomBorder ? widestRowBottomBorder.getWidth() : 0);
                    cellArea.getBBox().moveUp(diff / 2).decreaseHeight(diff / 2);
                    cell.setProperty(Property.BORDER_BOTTOM, collapsedBottomBorder);
                }
                LayoutResult cellResult = cell.setParent(this).layout(new LayoutContext(cellArea));
                // we need to disable collapsing with the next row
                if (!processAsLast && LayoutResult.NOTHING == cellResult.getStatus()) {
                    processAsLast = true;
                    // undo collapsing with the next row
                    widestRowBottomBorder = null;
                    for (int tempCol = 0; tempCol < currentRow.length; tempCol++) {
                        if (null != currentRow[tempCol]) {
                            currentRow[tempCol].deleteOwnProperty(Property.BORDER_BOTTOM);
                            oldBottomBorder = currentRow[tempCol].getBorders()[2];
                            if (null != oldBottomBorder && (null == widestRowBottomBorder || widestRowBottomBorder.getWidth() > oldBottomBorder.getWidth())) {
                                widestRowBottomBorder = oldBottomBorder;
                            }
                        }
                    }
                    cellProcessingQueue.clear();
                    for (int addCol = 0; addCol < currentRow.length; addCol++) {
                        if (currentRow[addCol] != null) {
                            cellProcessingQueue.addLast(new CellRendererInfo(currentRow[addCol], addCol, row));
                        }
                    }
                    continue;
                }
                if (collapsedBottomBorder != null && null != cellResult.getOccupiedArea()) {
                    // apply the difference between collapsed table border and own cell border
                    cellResult.getOccupiedArea().getBBox()
                            .moveUp((collapsedBottomBorder.getWidth() - (oldBottomBorder == null ? 0 : oldBottomBorder.getWidth())) / 2)
                            .decreaseHeight((collapsedBottomBorder.getWidth() - (oldBottomBorder == null ? 0 : oldBottomBorder.getWidth())) / 2);
                    cell.setProperty(Property.BORDER_BOTTOM, oldBottomBorder);
                }
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
                            if (null != footerRenderer && tableModel.isSkipLastFooter() && tableModel.isComplete()) {
//                                LayoutArea potentialArea = new LayoutArea(area.getPageNumber(), layoutBox.clone());
//                                // Fix layout area
//                                Border widestRowTopBorder = bordersHandler.getWidestHorizontalBorder(row);
//                                if (null != widestRowTopBorder) {
//                                    potentialArea.getBBox().moveDown(widestRowTopBorder.getWidth() / 2).increaseHeight(widestRowTopBorder.getWidth() / 2);
//                                }
//                                float footerHeight = footerRenderer.getOccupiedArea().getBBox().getHeight();
//                                potentialArea.getBBox().moveDown(footerHeight).increaseHeight(footerHeight);
//
//                                TableRenderer overflowRenderer = createOverflowRenderer(new Table.RowRange(rowRange.getStartRow() + row, rowRange.getFinishRow()));
//                                overflowRenderer.rows = rows.subList(row, rows.size());
//                                overflowRenderer.setProperty(Property.IGNORE_HEADER, true);
//                                overflowRenderer.setProperty(Property.IGNORE_FOOTER, true);
//                                overflowRenderer.setProperty(Property.MARGIN_TOP, 0);
//                                overflowRenderer.setProperty(Property.MARGIN_BOTTOM, 0);
//                                overflowRenderer.setProperty(Property.MARGIN_LEFT, 0);
//                                overflowRenderer.setProperty(Property.MARGIN_RIGHT, 0);
//                                // init borders
//                                overflowRenderer.initializeBorders(new ArrayList<Border>(), true);
//                                overflowRenderer.collapseAllBordersAndEmptyRows(overflowRenderer.getBorders(), 0, rowRange.getFinishRow() - rowRange.getStartRow() - row, numberOfColumns);
//                                prepareFooterOrHeaderRendererForLayout(overflowRenderer, layoutBox.getWidth());
//                                if (LayoutResult.FULL == overflowRenderer.layout(new LayoutContext(potentialArea)).getStatus()) {
//                                    footerRenderer = null;
//                                    // fix layout area and table bottom border
//                                    layoutBox.increaseHeight(footerHeight).moveDown(footerHeight);
//                                    deleteOwnProperty(Property.BORDER_BOTTOM);
//                                    borders = getBorders();
//                                    processAsLast = false;
//                                    cellProcessingQueue.clear();
//                                    for (addCol = 0; addCol < currentRow.length; addCol++) {
//                                        if (currentRow[addCol] != null) {
//                                            cellProcessingQueue.addLast(new CellRendererInfo(currentRow[addCol], addCol, row));
//                                        }
//                                    }
//                                    // build borders of current row cells and find the widest one
//                                    for (CellRendererInfo curCellInfo : cellProcessingQueue) {
//                                        col = curCellInfo.column;
//                                        cell = curCellInfo.cellRenderer;
//                                        prepareBuildingBordersArrays(cell, borders, tableModel.getNumberOfColumns(), row, col);
//                                        buildBordersArrays(cell, curCellInfo.finishRowInd, col);
//                                    }
//                                    continue;
//                                }
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
                                            // TODO DEVSIX-1060
                                            verticalAlignment = addRenderer.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT);
//                                            if (verticalAlignment != null && verticalAlignment.equals(VerticalAlignment.BOTTOM)) {
//                                                if (row + addRenderer.getPropertyAsInteger(Property.ROWSPAN) - 1 < addRow) {
//                                                    cellProcessingQueue.addLast(new CellRendererInfo(addRenderer, addCol, addRow));
//                                                } else {
//                                                    horizontalBorders.get(row + 1).set(addCol, addRenderer.getBorders()[2]);
//                                                    if (addCol == 0) {
//                                                        for (int i = row; i >= 0; i--) {
//                                                            if (!checkAndReplaceBorderInArray(verticalBorders, addCol, i, addRenderer.getBorders()[3], false)) {
//                                                                break;
//                                                            }
//                                                        }
//                                                    } else if (addCol == numberOfColumns - 1) {
//                                                        for (int i = row; i >= 0; i--) {
//                                                            if (!checkAndReplaceBorderInArray(verticalBorders, addCol + 1, i, addRenderer.getBorders()[1], true)) {
//                                                                break;
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            } else
                                            if (row + addRenderer.getPropertyAsInteger(Property.ROWSPAN) - 1 >= addRow) {
                                                cellProcessingQueue.addLast(new CellRendererInfo(addRenderer, addCol, addRow));
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
                    }
                }
                currChildRenderers.add(cell);
                if (cellResult.getStatus() != LayoutResult.NOTHING) {
                    rowHeight = Math.max(rowHeight, cell.getOccupiedArea().getBBox().getHeight() - rowspanOffset);
                }
            }

            // maybe the table was incomplete and we can process the footer
            if (null != footerRenderer && (0 != lastFlushedRowBottomBorder.size() || !tableModel.isComplete()) && !hasContent && 0 == childRenderers.size()) {
                layoutBox.increaseHeight(occupiedArea.getBBox().getHeight());
                occupiedArea.getBBox().moveUp(occupiedArea.getBBox().getHeight()).setHeight(0);
            }

            if (hasContent) {
                heights.add(rowHeight);
                occupiedArea.getBBox().moveDown(rowHeight);
                occupiedArea.getBBox().increaseHeight(rowHeight);
                layoutBox.decreaseHeight(rowHeight);
            }
            if (row == rows.size() - 1 && null != footerRenderer && tableModel.isComplete() && tableModel.isSkipLastFooter() && !split && !processAsLast) {
                footerRenderer = null;
                // delete #layout() related properties
                deleteOwnProperty(Property.BORDER_BOTTOM);
                if (tableModel.isEmpty()) {
                    this.deleteOwnProperty(Property.BORDER_TOP);
                }
                borders = getBorders();
            }

            if (split || processAsLast || row == rows.size() - 1) {
                if (hasContent || 0 != row) {
                    horizontalBordersIndexOffset = bordersHandler.getCurrentHorizontalBordersIndexOffset();
                    verticalBordersIndexOffset = bordersHandler.getCurrentVerticalBordersIndexOffset();
                    bordersHandler.processSplit(row, split, hasContent, cellWithBigRowspanAdded);
                }
                // Correct layout area of the last row rendered on the page
                if (heights.size() != 0) {
                    rowHeight = 0;
                    for (col = 0; col < currentRow.length; col++) {
                        if (hasContent || (cellWithBigRowspanAdded && null == rows.get(row - 1)[col])) {
                            if (null != currentRow[col]) {
                                cellProcessingQueue.addLast(new CellRendererInfo(currentRow[col], col, row));
                            }
                        } else if (null != rows.get(row - 1)[col]) {
                            cellProcessingQueue.addLast(new CellRendererInfo(rows.get(row - 1)[col], col, row - 1));
                        }
                    }
                    while (0 != cellProcessingQueue.size()) {
                        CellRendererInfo cellInfo = cellProcessingQueue.pop();
                        col = cellInfo.column;
                        int rowN = cellInfo.finishRowInd;
                        CellRenderer cell = cellInfo.cellRenderer;
                        float collapsedWithNextRowBorderWidth = null == cell.getBorders()[2] ? 0 : cell.getBorders()[2].getWidth();
                        cell.deleteOwnProperty(Property.BORDER_BOTTOM);
                        Border cellOwnBottomBorder = cell.getBorders()[2];
                        Border collapsedWithTableBorder = TableBorders.getCollapsedBorder(cellOwnBottomBorder,
                                footerRenderer != null ? footerRenderer.bordersHandler.horizontalBorders.get(0).get(col) : borders[2]);
                        if (null != collapsedWithTableBorder && bottomTableBorderWidth < collapsedWithTableBorder.getWidth()) {
                            bottomTableBorderWidth = collapsedWithTableBorder.getWidth();
                        }
                        float collapsedBorderWidth = null == collapsedWithTableBorder ? 0 : collapsedWithTableBorder.getWidth();
                        if (collapsedWithNextRowBorderWidth != collapsedBorderWidth) {
                            cell.setBorders(collapsedWithTableBorder, 2);
                            // TODO
//                            for (int i = col; i < col + cell.getPropertyAsInteger(Property.COLSPAN); i++) {
//                                bordersHandler.horizontalBorders.get(row + (hasContent ? 1 : 0)).set(i, collapsedWithTableBorder);
//                            }
                            // apply the difference between collapsed table border and own cell border
                            cell.occupiedArea.getBBox()
                                    .moveDown((collapsedBorderWidth - collapsedWithNextRowBorderWidth) / 2)
                                    .increaseHeight((collapsedBorderWidth - collapsedWithNextRowBorderWidth) / 2);
                        }
                        // fix row height
                        int cellRowStartIndex = cell.getModelElement().getRow();
                        float rowspanOffset = 0;
                        for (int l = cellRowStartIndex; l < heights.size() - 1; l++) {
                            rowspanOffset += heights.get(l);
                        }
                        if (cell.occupiedArea.getBBox().getHeight() > rowHeight + rowspanOffset) {
                            rowHeight = cell.occupiedArea.getBBox().getHeight() - rowspanOffset;
                        }
                    }
                    if (rowHeight != heights.get(heights.size() - 1)) {
                        float heightDiff = rowHeight - heights.get(heights.size() - 1);
                        heights.set(heights.size() - 1, rowHeight);
                        occupiedArea.getBBox().moveDown(heightDiff).increaseHeight(heightDiff);
                        layoutBox.decreaseHeight(heightDiff);
                    }
                } else {
                    if (null != borders[2]) {
                        // TODO
//                        for (col = 0; col < numberOfColumns; col++) {
//                            if (null == bordersHandler.horizontalBorders.get(1).get(col) || bordersHandler.horizontalBorders.get(1).get(col).getWidth() < borders[2].getWidth()) {
//                                bordersHandler.horizontalBorders.get(1).set(col, borders[2]);
//                            }
//                        }
                    }
                }
                // Correct occupied areas of all added cells
                correctCellsOccupiedAreas(splits, row, targetOverflowRowIndex);
            }
            // process footer with collapsed borders
            if ((split || processAsLast || row == rows.size() - 1) && null != footerRenderer) {
                int lastRow;
                if (hasContent || cellWithBigRowspanAdded) {
                    lastRow = row + 1;
                } else {
                    lastRow = row;
                }
                boolean[] useFooterBorders = new boolean[numberOfColumns];
                if (!tableModel.isEmpty()) {
                    useFooterBorders = collapseFooterBorders(
                            0 != lastFlushedRowBottomBorder.size() && 0 == row
                                    ? lastFlushedRowBottomBorder
                                    : bordersHandler.horizontalBorders.get(horizontalBordersIndexOffset + lastRow),
                            numberOfColumns,
                            rows.size());
                    layoutBox.increaseHeight(bottomTableBorderWidth / 2);
                    occupiedArea.getBBox().moveUp(bottomTableBorderWidth / 2).decreaseHeight(bottomTableBorderWidth / 2);
                }
                footerRenderer.processRendererBorders(numberOfColumns);

                layoutBox.moveDown(footerRenderer.occupiedArea.getBBox().getHeight()).increaseHeight(footerRenderer.occupiedArea.getBBox().getHeight());
                // apply the difference to set footer and table left/right margins identical
                layoutBox.<Rectangle>applyMargins(0, -rightBorderMaxWidth / 2, 0, -leftBorderMaxWidth / 2, false);
                prepareFooterOrHeaderRendererForLayout(footerRenderer, layoutBox.getWidth());
                LayoutResult res = footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
                layoutBox.<Rectangle>applyMargins(0, -rightBorderMaxWidth / 2, 0, -leftBorderMaxWidth / 2, true);
                float footerHeight = footerRenderer.getOccupiedAreaBBox().getHeight();
                footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
                layoutBox.setY(footerRenderer.occupiedArea.getBBox().getTop()).setHeight(occupiedArea.getBBox().getBottom() - layoutBox.getBottom());
                // fix footer borders
                if (!tableModel.isEmpty()) {
                    footerRenderer.bordersHandler.updateTopBorder(
                            0 != lastFlushedRowBottomBorder.size() && 0 == row
                                    ? lastFlushedRowBottomBorder
                                    : bordersHandler.horizontalBorders.get(horizontalBordersIndexOffset + lastRow),
                            useFooterBorders);
                }
            }
            if (!split) {
                childRenderers.addAll(currChildRenderers);
                currChildRenderers.clear();
            }
            if (split || processAsLast) {
                if (marginsCollapsingEnabled) {
                    marginsCollapseHandler.endMarginsCollapse(layoutBox);
                }
                TableRenderer[] splitResult = !split && processAsLast ? split(row + 1, false, cellWithBigRowspanAdded) : split(row, hasContent, cellWithBigRowspanAdded);
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
                                cellOverflow.deleteOwnProperty(Property.BORDER_BOTTOM);
                                cellOverflow.deleteOwnProperty(Property.BORDER_TOP);
                                // TODO
//                                if (null != cellSplit) {
//                                    for (int j = col; j < col + cellOverflow.getPropertyAsInteger(Property.COLSPAN); j++) {
//                                        splitResult[0].horizontalBorders.get(row + (hasContent ? 1 : 0)).set(j, getCollapsedBorder(currentRow[col].getBorders()[2], borders[2]));
//                                    }
//                                }
                                currentRow[col] = null;
                                rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) cellOverflow.setParent(splitResult[1]);
                            } else {
                                rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) currentRow[col].setParent(splitResult[1]);
                                rows.get(targetOverflowRowIndex[col])[col].deleteOwnProperty(Property.BORDER_TOP);
                            }
                            rows.get(targetOverflowRowIndex[col])[col].occupiedArea = cellOccupiedArea;
                        } else if (currentRow[col] != null) {
                            if (hasContent) {
                                rowspans[col] = currentRow[col].getModelElement().getRowspan();
                            }
                            boolean isBigRowspannedCell = 1 != currentRow[col].getModelElement().getRowspan();
                            if (hasContent || isBigRowspannedCell) {
                                columnsWithCellToBeEnlarged[col] = true;
                            } else {
                                if (Border.NO_BORDER != currentRow[col].<Border>getProperty(Property.BORDER_TOP)) {
                                    splitResult[1].rows.get(0)[col].deleteOwnProperty(Property.BORDER_TOP);
                                }
                            }
                            // TODO
//                            for (int j = col; j < col + currentRow[col].getPropertyAsInteger(Property.COLSPAN); j++) {
//                                horizontalBorders.get(row + (hasContent ? 1 : 0)).set(j, getCollapsedBorder(currentRow[col].getBorders()[2], borders[2]));
//                            }
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
                        occupiedArea.getBBox().moveDown(bottomTableBorderWidth / 2).increaseHeight(bottomTableBorderWidth / 2);
                        layoutBox.decreaseHeight(bottomTableBorderWidth / 2);
                    } else {
                        occupiedArea.getBBox().moveUp(topTableBorderWidth / 2).decreaseHeight(topTableBorderWidth / 2);
                        layoutBox.increaseHeight(topTableBorderWidth / 2);
                        // process bottom border of the last added row if there is no footer
                        if (!tableModel.isComplete() || 0 != lastFlushedRowBottomBorder.size()) {
                            bottomTableBorderWidth = null == widestLustFlushedBorder ? 0f : widestLustFlushedBorder.getWidth();
                            occupiedArea.getBBox().moveDown(bottomTableBorderWidth).increaseHeight(bottomTableBorderWidth);

                            // TODO
//                            splitResult[0].horizontalBorders.clear();
//                            splitResult[0].horizontalBorders.add(lastFlushedRowBottomBorder);

                            // hack to process 'margins'
                            splitResult[0].setBorders(widestLustFlushedBorder, 2);
                            splitResult[0].setBorders(Border.NO_BORDER, 0);
                            // TODO
//                            if (0 != splitResult[0].verticalBorders.size()) {
//                                splitResult[0].setBorders(splitResult[0].verticalBorders.get(0).get(0), 3);
//                                splitResult[0].setBorders(splitResult[0].verticalBorders.get(verticalBorders.size() - 1).get(0), 1);
//                            }
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
                            && (tableModel.isComplete() && 0 == lastFlushedRowBottomBorder.size()))
                            ? LayoutResult.NOTHING
                            : LayoutResult.PARTIAL;
                    if ((status == LayoutResult.NOTHING && Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT)))
                            || wasHeightClipped) {
                        if (wasHeightClipped) {
                            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                            logger.warn(LogMessageConstant.CLIP_ELEMENT);
                            // Process borders
                            if (status == LayoutResult.NOTHING) {
                                ArrayList<Border> topBorders = new ArrayList<Border>();
                                ArrayList<Border> bottomBorders = new ArrayList<Border>();
                                for (int i = 0; i < numberOfColumns; i++) {
                                    topBorders.add(borders[0]);
                                    bottomBorders.add(borders[2]);
                                }
                                // TODO
//                                horizontalBorders.clear();
//                                horizontalBorders.add(topBorders);
//                                horizontalBorders.add(bottomBorders);
                                float bordersWidth = (null == borders[0] ? 0 : borders[0].getWidth()) + (null == borders[2] ? 0 : borders[2].getWidth());
                                occupiedArea.getBBox().moveDown(bordersWidth).increaseHeight(bordersWidth);
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
                        return new LayoutResult(LayoutResult.FULL, occupiedArea, splitResult[0], null);
                    } else {
                        if (hasProperty(Property.HEIGHT)) {
                            splitResult[1].setProperty(Property.HEIGHT, retrieveHeight() - occupiedArea.getBBox().getHeight());
                        }
                        if (status != LayoutResult.NOTHING) {
                            return new LayoutResult(status, occupiedArea, splitResult[0], splitResult[1], null);
                        } else {
                            return new LayoutResult(status, null, splitResult[0], splitResult[1], firstCauseOfNothing);
                        }
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
            if (lastInRow < 0 || lastRow.length != lastInRow + lastRow[lastInRow].getPropertyAsInteger(Property.COLSPAN)) {
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.warn(LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE);
            }
        }

        // process footer renderer with collapsed borders
        if (tableModel.isComplete() && 0 != lastFlushedRowBottomBorder.size() && null != footerRenderer) {
            boolean[] useFooterBorders = collapseFooterBorders(lastFlushedRowBottomBorder, numberOfColumns, rows.size());
            footerRenderer.processRendererBorders(numberOfColumns);

            layoutBox.moveDown(footerRenderer.occupiedArea.getBBox().getHeight()).increaseHeight(footerRenderer.occupiedArea.getBBox().getHeight());
            // apply the difference to set footer and table left/right margins identical
            layoutBox.<Rectangle>applyMargins(0, -rightBorderMaxWidth / 2,
                    0, -leftBorderMaxWidth / 2, false);
            prepareFooterOrHeaderRendererForLayout(footerRenderer, layoutBox.getWidth());
            footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            layoutBox.<Rectangle>applyMargins(0, -rightBorderMaxWidth / 2,
                    0, -leftBorderMaxWidth / 2, true);

            float footerHeight = footerRenderer.getOccupiedAreaBBox().getHeight();
            footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);

            // fix borders
            bordersHandler.updateTopBorder(lastFlushedRowBottomBorder, useFooterBorders);
        }

        // if table is empty we still need to process table borders
        if (0 == childRenderers.size() && null == headerRenderer && null == footerRenderer) {
            List<Border> topHorizontalBorders = new ArrayList<Border>();
            List<Border> bottomHorizontalBorders = new ArrayList<Border>();
            for (int i = 0; i < numberOfColumns; i++) {
                bottomHorizontalBorders.add(Border.NO_BORDER);
            }
            List<Border> leftVerticalBorders = new ArrayList<Border>();
            List<Border> rightVerticalBorders = new ArrayList<Border>();

            // process bottom border of the last added row
            if (tableModel.isComplete() && 0 != lastFlushedRowBottomBorder.size()) {
                bottomHorizontalBorders = lastFlushedRowBottomBorder;
                // hack to process 'margins'
                setBorders(widestLustFlushedBorder, 2);
                setBorders(Border.NO_BORDER, 0);
            }
            // collapse with table bottom border
            for (int i = 0; i < bottomHorizontalBorders.size(); i++) {
                Border border = bottomHorizontalBorders.get(i);
                if (null == border || (null != borders[2] && border.getWidth() < borders[2].getWidth())) {
                    bottomHorizontalBorders.set(i, borders[2]);
                }
                topHorizontalBorders.add(borders[0]);
            }
            // TODO
//            horizontalBorders.set(0, topHorizontalBorders);
//            horizontalBorders.add(bottomHorizontalBorders);
//            leftVerticalBorders.add(borders[3]);
//            rightVerticalBorders.add(borders[1]);
//            verticalBorders = new ArrayList<>();
//            verticalBorders.add(leftVerticalBorders);
//            for (int i = 0; i < numberOfColumns - 1; i++) {
//                verticalBorders.add(new ArrayList<Border>());
//            }
//            verticalBorders.add(rightVerticalBorders);
        }

        // Apply bottom and top border
        if (tableModel.isComplete()) {
            if (null == footerRenderer) {
                if (0 != childRenderers.size()) {
                    occupiedArea.getBBox().moveDown(bottomTableBorderWidth / 2).increaseHeight((bottomTableBorderWidth) / 2);
                    layoutBox.decreaseHeight(bottomTableBorderWidth / 2);
                } else {
                    if (0 != lastFlushedRowBottomBorder.size()) {
                        if (null != widestLustFlushedBorder && widestLustFlushedBorder.getWidth() > bottomTableBorderWidth) {
                            bottomTableBorderWidth = widestLustFlushedBorder.getWidth();
                        }
                    }
                    occupiedArea.getBBox().moveDown(bottomTableBorderWidth).increaseHeight((bottomTableBorderWidth));
                    layoutBox.decreaseHeight(bottomTableBorderWidth);
                }
            }
        } else {
            // the bottom border should be processed and placed lately
            if (0 != heights.size()) {
                // TODO
//                horizontalBorders.get(horizontalBorders.size() - 1).clear();
                heights.set(heights.size() - 1, heights.get(heights.size() - 1) - bottomTableBorderWidth / 2);
            }
            if (null == footerRenderer) {
                if (0 != childRenderers.size()) {
                    occupiedArea.getBBox().moveUp(bottomTableBorderWidth / 2).decreaseHeight((bottomTableBorderWidth / 2));
                    layoutBox.increaseHeight(bottomTableBorderWidth / 2);
                }
            } else {
                // occupied area is right here
                layoutBox.increaseHeight(bottomTableBorderWidth);
            }
        }

        if ((Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))) && 0 != rows.size()) {
            extendLastRow(rows.get(rows.size() - 1), layoutBox);
        }

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


        if (isPositioned()) {
            float y = (float) this.getPropertyAsFloat(Property.Y);
            float relativeY = isFixedLayout() ? 0 : layoutBox.getY();
            move(0, relativeY + y - occupiedArea.getBBox().getY());
        }


        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.endMarginsCollapse(layoutBox);
        }

        applyMargins(occupiedArea.getBBox(), true);
        // if table is empty or is not complete we should delete footer
        if ((tableModel.isSkipLastFooter() || !tableModel.isComplete()) && null != footerRenderer) {
            if (0 != lastFlushedRowBottomBorder.size()) {
                if (null != widestLustFlushedBorder && widestLustFlushedBorder.getWidth() > bottomTableBorderWidth) {
                    bottomTableBorderWidth = widestLustFlushedBorder.getWidth();
                }
                // hack to process 'margins'
                setBorders(widestLustFlushedBorder, 0);
            }
            footerRenderer = null;
            if (tableModel.isComplete()) {
                occupiedArea.getBBox()
                        .moveDown(bottomTableBorderWidth)
                        .increaseHeight(bottomTableBorderWidth);
            }
        }
        adjustFooterAndFixOccupiedArea(layoutBox);

        return new LayoutResult(LayoutResult.FULL, occupiedArea, null, null);
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

        drawBorders(drawContext, null == headerRenderer, null == footerRenderer);
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

    protected float[] calculateScaledColumnWidths(Table tableModel, float tableWidth, float leftBorderWidth, float rightBorderWidth) {
        float[] scaledWidths = new float[tableModel.getNumberOfColumns()];
        float widthSum = 0;
        float totalPointWidth = 0;
        int col;
        for (col = 0; col < tableModel.getNumberOfColumns(); col++) {
            UnitValue columnUnitWidth = tableModel.getColumnWidth(col);
            float columnWidth;
            if (columnUnitWidth.isPercentValue()) {
                columnWidth = tableWidth * columnUnitWidth.getValue() / 100;
                scaledWidths[col] = columnWidth;
                widthSum += columnWidth;
            } else {
                totalPointWidth += columnUnitWidth.getValue();
            }
        }
        float freeTableSpaceWidth = tableWidth - widthSum;

        if (totalPointWidth > 0) {
            for (col = 0; col < tableModel.getNumberOfColumns(); col++) {
                float columnWidth;
                UnitValue columnUnitWidth = tableModel.getColumnWidth(col);
                if (columnUnitWidth.isPointValue()) {
                    columnWidth = (freeTableSpaceWidth / totalPointWidth) * columnUnitWidth.getValue();
                    scaledWidths[col] = columnWidth;
                    widthSum += columnWidth;
                }
            }
        }

        for (col = 0; col < tableModel.getNumberOfColumns(); col++) {
            scaledWidths[col] *= (tableWidth - leftBorderWidth / 2 - rightBorderWidth / 2) / widthSum;
        }

        return scaledWidths;
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
        splitRenderer.horizontalBordersIndexOffset = horizontalBordersIndexOffset;
        splitRenderer.verticalBordersIndexOffset = verticalBordersIndexOffset;

        splitRenderer.heights = heights;
        splitRenderer.columnWidths = columnWidths;
        splitRenderer.countedColumnWidth = countedColumnWidth;
        splitRenderer.totalWidthForColumns = totalWidthForColumns;
        TableRenderer overflowRenderer = createOverflowRenderer(new Table.RowRange(rowRange.getStartRow() + row, rowRange.getFinishRow()));
        if (0 == row && !(hasContent || cellWithBigRowspanAdded)) {
            overflowRenderer.isOriginalNonSplitRenderer = true;
        }
        overflowRenderer.rows = rows.subList(row, rows.size());
        splitRenderer.occupiedArea = occupiedArea;

        overflowRenderer.bordersHandler = bordersHandler;
        overflowRenderer.horizontalBordersIndexOffset = bordersHandler.horizontalBordersIndexOffset;
        overflowRenderer.verticalBordersIndexOffset = bordersHandler.verticalBordersIndexOffset;

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
        overflowRenderer.leftBorderMaxWidth = this.leftBorderMaxWidth;
        overflowRenderer.rightBorderMaxWidth = this.rightBorderMaxWidth;
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
        return countTableMinMaxWidth(availableWidth, true, false).toTableMinMaxWidth(availableWidth);
    }

    private ColumnMinMaxWidth countTableMinMaxWidth(float availableWidth, boolean initializeBorders, boolean isTableBeingLayouted) {
        Rectangle layoutBox = new Rectangle(availableWidth, AbstractRenderer.INF);
        float tableWidth = (float) retrieveWidth(layoutBox.getWidth());
        applyMargins(layoutBox, false);
        if (initializeBorders) {
            // FIXME
            bordersHandler.initializeBorders(((Table) getModelElement()).getLastRowBottomBorder(), true);
            bordersHandler.setTableBoundingBorders(getBorders());
            initializeHeaderAndFooter(true);
            if (!isTableBeingLayouted) {
                saveCellsProperties();
            }
            collapseAllBorders();
        }

        ColumnMinMaxWidth footerColWidth = null;
        if (footerRenderer != null) {
            footerColWidth = footerRenderer.countRegionMinMaxWidth(availableWidth - leftBorderMaxWidth / 2 - rightBorderMaxWidth / 2, null, null);
        }

        ColumnMinMaxWidth headerColWidth = null;
        if (headerRenderer != null) {
            headerColWidth = headerRenderer.countRegionMinMaxWidth(availableWidth - leftBorderMaxWidth / 2 - rightBorderMaxWidth / 2, null, null);
        }

        // Apply halves of the borders. The other halves are applied on a Cell level
        layoutBox.<Rectangle>applyMargins(0, rightBorderMaxWidth / 2, 0, leftBorderMaxWidth / 2, false);
        tableWidth -= rightBorderMaxWidth / 2 + leftBorderMaxWidth / 2;

        ColumnMinMaxWidth tableColWidth = countRegionMinMaxWidth(tableWidth, headerColWidth, footerColWidth);

        countedMaxColumnWidth = tableColWidth.maxWidth;
        countedMinColumnWidth = tableColWidth.minWidth;

        if (initializeBorders) {
            footerRenderer = null;
            headerRenderer = null;
            rightBorderMaxWidth = 0;
            leftBorderMaxWidth = 0;
            // TODO
//            horizontalBorders = null;
//            verticalBorders = null;
            if (!isTableBeingLayouted) {
                restoreCellsProperties();
            }
            //TODO do we need it?
            // delete set properties
            deleteOwnProperty(Property.BORDER_BOTTOM);
            deleteOwnProperty(Property.BORDER_TOP);
        }

        return tableColWidth.setLayoutBoxWidth(layoutBox.getWidth());
    }

    private ColumnMinMaxWidth countRegionMinMaxWidth(float availableWidth, ColumnMinMaxWidth headerWidth, ColumnMinMaxWidth footerWidth) {
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

    float[] getMinColumnWidth() {
        return countedMinColumnWidth;
    }

    float[] getMaxColumnWidth() {
        return countedMaxColumnWidth;
    }

    @Override
    public void drawBorder(DrawContext drawContext) {
        // Do nothing here. Itext7 handles cell and table borders collapse and draws result borders during #drawBorders()
    }

    protected void drawBorders(DrawContext drawContext) {
        drawBorders(drawContext, true, true);
    }

    protected void drawBorders(DrawContext drawContext, boolean drawTop, boolean drawBottom) {
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

        float startX = getOccupiedArea().getBBox().getX();
        float startY = getOccupiedArea().getBBox().getY() + getOccupiedArea().getBBox().getHeight();

        for (IRenderer child : childRenderers) {
            CellRenderer cell = (CellRenderer) child;
            if (cell.getModelElement().getRow() == this.rowRange.getStartRow()) {
                startY = cell.getOccupiedArea().getBBox().getY() + cell.getOccupiedArea().getBBox().getHeight();
                break;
            }
        }

        for (IRenderer child : childRenderers) {
            CellRenderer cell = (CellRenderer) child;
            if (cell.getModelElement().getCol() == 0) {
                startX = cell.getOccupiedArea().getBBox().getX();
                break;
            }
        }

        // process halves of the borders here
        if (childRenderers.size() == 0) {
            Border[] borders = this.getBorders();
            if (null != borders[3]) {
                startX += borders[3].getWidth() / 2;
            }
            if (null != borders[0]) {
                startY -= borders[0].getWidth() / 2;
                if (null != borders[2]) {
                    if (0 == heights.size()) {
                        heights.add(0, borders[0].getWidth() / 2 + borders[2].getWidth() / 2);
                    }
                }
            } else if (null != borders[2]) {
                startY -= borders[2].getWidth() / 2;
            }
        }

        boolean isTagged = drawContext.isTaggingEnabled() && getModelElement() instanceof IAccessibleElement;
        if (isTagged) {
            drawContext.getCanvas().openTag(new CanvasArtifact());
        }

        // Draw bounding borders. Vertical borders are the last to draw in order to collapse with header / footer
        if (drawTop) {
            drawHorizontalBorder(0, startX, startY, drawContext.getCanvas());
        }

        float y1 = startY;
        if (heights.size() > 0) {
            y1 -= (float) heights.get(0);
        }
        for (int i = 1; i < heights.size(); i++) {
            drawHorizontalBorder(i, startX, y1, drawContext.getCanvas());
            if (i < heights.size()) {
                y1 -= (float) heights.get(i);
            }
        }

        float x1 = startX;
        if (countedColumnWidth.length > 0) {
            x1 += countedColumnWidth[0];
        }
        for (int i = 1; i < bordersHandler.numberOfColumns; i++) {
            drawVerticalBorder(i, startY, x1, drawContext.getCanvas());
            if (i < countedColumnWidth.length) {
                x1 += countedColumnWidth[i];
            }
        }

        // Draw bounding borders. Vertical borders are the last to draw in order to collapse with header / footer
        if (drawTop) {
            drawHorizontalBorder(0, startX, startY, drawContext.getCanvas());
        }
        if (drawBottom) {
            drawHorizontalBorder(heights.size(), startX, y1, drawContext.getCanvas());
        }
        // draw left
        drawVerticalBorder(0, startY, startX, drawContext.getCanvas());
        // draw right
        drawVerticalBorder(bordersHandler.numberOfColumns, startY, x1, drawContext.getCanvas());

        if (isTagged) {
            drawContext.getCanvas().closeTag();
        }
    }

    private void drawHorizontalBorder(int i, float startX, float y1, PdfCanvas canvas) {
        List<Border> borders = bordersHandler.horizontalBorders.get(horizontalBordersIndexOffset + i);
        float x1 = startX;
        float x2 = x1 + countedColumnWidth[0];
        if (i == 0) {
            if (bordersHandler.verticalBorders != null && bordersHandler.verticalBorders.size() > 0
                    && bordersHandler.verticalBorders.get(0).size() > verticalBordersIndexOffset && bordersHandler.verticalBorders.get(bordersHandler.numberOfColumns).size() > verticalBordersIndexOffset) {
                Border firstBorder = bordersHandler.verticalBorders.get(0).get(verticalBordersIndexOffset);
                if (firstBorder != null) {
                    x1 -= firstBorder.getWidth() / 2;
                }
            }
        } else if (i == heights.size()) {
            if (bordersHandler.verticalBorders != null && bordersHandler.verticalBorders.size() > 0 && bordersHandler.verticalBorders.get(0).size() > verticalBordersIndexOffset &&
                    bordersHandler.verticalBorders.get(bordersHandler.numberOfColumns) != null && bordersHandler.verticalBorders.get(bordersHandler.numberOfColumns).size() > verticalBordersIndexOffset
                    && bordersHandler.verticalBorders.get(0) != null) {
                Border firstBorder = bordersHandler.verticalBorders.get(0).get(verticalBordersIndexOffset + heights.size() - 1);
                if (firstBorder != null) {
                    x1 -= firstBorder.getWidth() / 2;
                }
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
            if (bordersHandler.verticalBorders != null && bordersHandler.verticalBorders.size() > j && bordersHandler.verticalBorders.get(j) != null && bordersHandler.verticalBorders.get(j).size() > verticalBordersIndexOffset) {
                if (i == 0) {
                    if (bordersHandler.verticalBorders.get(j).get(verticalBordersIndexOffset + i) != null)
                        x2 += bordersHandler.verticalBorders.get(j).get(verticalBordersIndexOffset + i).getWidth() / 2;
                } else if (i == heights.size() && bordersHandler.verticalBorders.get(j).size() >= verticalBordersIndexOffset + i - 1 && bordersHandler.verticalBorders.get(j).get(verticalBordersIndexOffset + i - 1) != null) {
                    x2 += bordersHandler.verticalBorders.get(j).get(verticalBordersIndexOffset + i - 1).getWidth() / 2;
                }
            }

            lastBorder.drawCellBorder(canvas, x1, y1, x2, y1);
        }
    }

    private void drawVerticalBorder(int i, float startY, float x1, PdfCanvas canvas) {
        List<Border> borders = bordersHandler.verticalBorders.get(i);
        float y1 = startY;
        float y2 = y1;
        if (!heights.isEmpty()) {
            y2 = y1 - (float) heights.get(0);
        }
        int j;
        for (j = 1; j < heights.size(); j++) {
            Border prevBorder = borders.get(verticalBordersIndexOffset + j - 1);
            Border curBorder = borders.get(verticalBordersIndexOffset + j);
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
            return;
        }
        Border lastBorder = borders.get(verticalBordersIndexOffset + j - 1);
        if (lastBorder != null) {
            lastBorder.drawCellBorder(canvas, x1, y1, x1, y2);
        }
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

    private void correctCellsOccupiedAreas(LayoutResult[] splits, int row, int[] targetOverflowRowIndex) {
        // Correct occupied areas of all added cells
        for (int k = 0; k <= row; k++) {
            CellRenderer[] currentRow = rows.get(k);
            if (k < row || (row + 1 == heights.size())) {
                for (int col = 0; col < currentRow.length; col++) {
                    CellRenderer cell = (k < row || null == splits[col]) ? currentRow[col] : (CellRenderer) splits[col].getSplitRenderer();
                    if (cell == null) {
                        continue;
                    }
                    float height = 0;
                    int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
                    for (int l = k; l > ((k == row + 1) ? targetOverflowRowIndex[col] : k) - rowspan && l >= 0; l--) {
                        height += (float) heights.get(l);
                    }
                    // Correcting cell bbox only. We don't need #move() here.
                    // This is because of BlockRenderer's specificity regarding occupied area.
                    float shift = height - cell.getOccupiedArea().getBBox().getHeight();
                    Rectangle bBox = cell.getOccupiedArea().getBBox();
                    bBox.moveDown(shift);
                    bBox.setHeight(height);
                    cell.applyVerticalAlignment();
                }
            }
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
            renderer.setBorders(TableBorders.getCollapsedBorder(borders[innerBorder], tableBorders[innerBorder]), innerBorder);
            setBorders(Border.NO_BORDER, innerBorder);
        }
        renderer.setBorders(TableBorders.getCollapsedBorder(borders[1], tableBorders[1]), 1);
        renderer.setBorders(TableBorders.getCollapsedBorder(borders[3], tableBorders[3]), 3);
        renderer.setBorders(TableBorders.getCollapsedBorder(borders[outerBorder], tableBorders[outerBorder]), outerBorder);
        setBorders(Border.NO_BORDER, outerBorder);
        // update bounding borders
        bordersHandler.setTableBoundingBorders(getBorders());
        return renderer;
    }

    private TableRenderer prepareFooterOrHeaderRendererForLayout(TableRenderer renderer, float layoutBoxWidth) {
        renderer.countedColumnWidth = countedColumnWidth;
        renderer.leftBorderMaxWidth = leftBorderMaxWidth;
        renderer.rightBorderMaxWidth = rightBorderMaxWidth;
        if (hasProperty(Property.WIDTH)) {
            renderer.setProperty(Property.WIDTH, UnitValue.createPointValue(layoutBoxWidth));
        }
        return this;
    }

    private TableRenderer processRendererBorders(int numberOfColumns) {
        bordersHandler = new TableBorders(rows, numberOfColumns);
        bordersHandler.setTableBoundingBorders(getBorders());
        bordersHandler.initializeBorders(new ArrayList<Border>(), true);
        bordersHandler.collapseAllBordersAndEmptyRows(rows, getBorders(), rowRange.getStartRow(), rowRange.getFinishRow(), numberOfColumns);
        return this;
    }

    private boolean isHeaderRenderer() {
        return parent instanceof TableRenderer && ((TableRenderer) parent).headerRenderer == this;
    }

    private boolean isFooterRenderer() {
        return parent instanceof TableRenderer && ((TableRenderer) parent).footerRenderer == this;
    }

    /**
     * Returns minWidth
     */
    private float calculateColumnWidths(float availableWidth, boolean calculateTableMaxWidth) {
        if (countedColumnWidth == null || totalWidthForColumns != availableWidth) {
            TableWidths tableWidths = new TableWidths(this, availableWidth, calculateTableMaxWidth, rightBorderMaxWidth, leftBorderMaxWidth);
            if (tableWidths.hasFixedLayout()) {
                countedColumnWidth = tableWidths.fixedLayout();
                return tableWidths.getMinWidth();
            } else {
                ColumnMinMaxWidth minMax = countTableMinMaxWidth(availableWidth, false, true);
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
        return sum + rightBorderMaxWidth / 2 + leftBorderMaxWidth / 2;

    }

    protected TableRenderer saveCellsProperties() {
        CellRenderer[] currentRow;
        int colN = ((Table) getModelElement()).getNumberOfColumns();
        for (int row = 0; row < rows.size(); row++) {
            currentRow = rows.get(row);
            for (int col = 0; col < colN; col++) {
                if (null != currentRow[col]) {
                    currentRow[col].saveProperties();
                }
            }
        }
        return this;
    }

    protected TableRenderer restoreCellsProperties() {
        CellRenderer[] currentRow;
        int colN = ((Table) getModelElement()).getNumberOfColumns();
        for (int row = 0; row < rows.size(); row++) {
            currentRow = rows.get(row);
            for (int col = 0; col < colN; col++) {
                if (null != currentRow[col]) {
                    currentRow[col].restoreProperties();
                }
            }
        }
        return this;
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
