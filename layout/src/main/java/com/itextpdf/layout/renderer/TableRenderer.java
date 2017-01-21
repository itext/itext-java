/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
    private ArrayList<ArrayList<Border>> horizontalBorders;
    private ArrayList<ArrayList<Border>> verticalBorders;
    private float[] columnWidths = null;
    private List<Float> heights = new ArrayList<>();

    private float[] countedMinColumnWidth;
    private float[] countedMaxColumnWidth;

    //TODO remove
    private float[] countedColumnWidth = null;
    private float totalWidthForColumns;

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

        Border[] borders = getBorders();
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
        ArrayList<Border> lastFlushedRowBottomBorder = tableModel.getLastRowBottomBorder();
        Border widestLustFlushedBorder = null;
        for (Border border : lastFlushedRowBottomBorder) {
            if (null != border && (null == widestLustFlushedBorder || widestLustFlushedBorder.getWidth() < border.getWidth())) {
                widestLustFlushedBorder = border;
            }
        }
        if (!Boolean.TRUE.equals(this.<Boolean>getProperty(Property.BORDERS_INITIALIZED))) {
            initializeBorders(lastFlushedRowBottomBorder, area.isEmptyArea());
            if (tableModel.isComplete() && 0 == lastFlushedRowBottomBorder.size()) {
                setProperty(Property.BORDERS_INITIALIZED, true);
            }
        }

        // collapse all cell borders
        if (null != rows && !Boolean.TRUE.equals(this.<Boolean>getProperty(Property.BORDERS_COLLAPSED))) {
            collapseAllBorders(borders, 0, rows.size() - 1, tableModel.getNumberOfColumns());
            if ((tableModel.isComplete() && 0 == lastFlushedRowBottomBorder.size())) {
                setProperty(Property.BORDERS_COLLAPSED, true);
            }
        } else {
            updateFirstRowBorders(tableModel.getNumberOfColumns());
        }

        float topTableBorderWidth = getMaxTopWidth(null); // first row own top border. We will use it in header processing
        float rightTableBorderWidth = getMaxRightWidth(borders[1]);
        float bottomTableBorderWidth = 0;
        float leftTableBorderWidth = getMaxLeftWidth(borders[3]);

        float tableWidth = calculateColumnWidths(layoutBox.getWidth(), new float[]{0, rightTableBorderWidth, 0, leftTableBorderWidth});

        if (layoutBox.getWidth() > tableWidth) {
            //TODO add side borders
            layoutBox.setWidth((float) tableWidth);
        }

        occupiedArea = new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY() + layoutBox.getHeight(), (float) tableWidth, 0));

        Table footerElement = tableModel.getFooter();
        // footer can be skipped, but after the table content will be layouted
        boolean footerShouldBeApplied = !(tableModel.isComplete() && 0 != tableModel.getLastRowBottomBorder().size() && tableModel.isSkipLastFooter());
        if (footerElement != null && footerShouldBeApplied) {
            borders = getBorders();
            footerRenderer = initFooterOrHeaderRenderer(true, borders);
            footerRenderer.processRendererBorders(numberOfColumns);
            float rightFooterBorderWidth = footerRenderer.getMaxRightWidth(footerRenderer.getBorders()[1]);
            float leftFooterBorderWidth = footerRenderer.getMaxLeftWidth(footerRenderer.getBorders()[3]);

            leftTableBorderWidth = Math.max(leftTableBorderWidth, leftFooterBorderWidth);
            rightTableBorderWidth = Math.max(rightTableBorderWidth, rightFooterBorderWidth);

            // apply the difference to set footer and table left/right margins identical
            layoutBox.<Rectangle>applyMargins(0, Math.max(0, rightTableBorderWidth - rightFooterBorderWidth) / 2, 0, Math.max(0, leftTableBorderWidth - leftFooterBorderWidth) / 2, false);
            if (hasProperty(Property.WIDTH)) {
                footerRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(layoutBox.getWidth()));
            }

            LayoutResult result = footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            if (result.getStatus() != LayoutResult.FULL) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, result.getCauseOfNothing());
            }
            float footerHeight = result.getOccupiedArea().getBBox().getHeight();
            footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
            layoutBox.<Rectangle>applyMargins(0, Math.max(0, rightTableBorderWidth - rightFooterBorderWidth) / 2, 0, Math.max(0, leftTableBorderWidth - leftFooterBorderWidth) / 2, true);
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);

            if (!tableModel.isEmpty()) {
                float maxFooterTopBorderWidth = 0;
                ArrayList<Border> footerTopBorders = footerRenderer.horizontalBorders.get(0);
                for (Border border : footerTopBorders) {
                    if (null != border && border.getWidth() > maxFooterTopBorderWidth) {
                        maxFooterTopBorderWidth = border.getWidth();
                    }
                }
                footerRenderer.occupiedArea.getBBox().decreaseHeight(maxFooterTopBorderWidth);
                layoutBox.moveDown(maxFooterTopBorderWidth).increaseHeight(maxFooterTopBorderWidth);
            }
            // we will delete FORCED_PLACEMENT property after adding one row
            // but the footer should be forced placed once more (since we renderer footer twice)
            if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                footerRenderer.setProperty(Property.FORCED_PLACEMENT, true);
            }
        }

        Table headerElement = tableModel.getHeader();
        boolean isFirstHeader = rowRange.getStartRow() == 0 && isOriginalNonSplitRenderer;
        boolean headerShouldBeApplied = (!rows.isEmpty() || tableModel.isComplete()) && (!isOriginalNonSplitRenderer || isFirstHeader && !tableModel.isSkipFirstHeader());
        if (headerElement != null && headerShouldBeApplied) {
            borders = getBorders();
            headerRenderer = initFooterOrHeaderRenderer(false, borders);
            headerRenderer.processRendererBorders(numberOfColumns);
            float rightHeaderBorderWidth = headerRenderer.getMaxRightWidth(headerRenderer.getBorders()[1]);
            float leftHeaderBorderWidth = headerRenderer.getMaxLeftWidth(headerRenderer.getBorders()[3]);

            leftTableBorderWidth = Math.max(leftTableBorderWidth, leftHeaderBorderWidth);
            rightTableBorderWidth = Math.max(rightTableBorderWidth, rightHeaderBorderWidth);

            // apply the difference to set header and table left/right margins identical
            layoutBox.<Rectangle>applyMargins(0, Math.max(0, rightTableBorderWidth - rightHeaderBorderWidth) / 2, 0, Math.max(0, leftTableBorderWidth - leftHeaderBorderWidth) / 2, false);
            if (hasProperty(Property.WIDTH)) {
                headerRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(layoutBox.getWidth()));
            }
            LayoutResult result = headerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            if (result.getStatus() != LayoutResult.FULL) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, result.getCauseOfNothing());
            }
            float headerHeight = result.getOccupiedArea().getBBox().getHeight();
            layoutBox.decreaseHeight(headerHeight);
            layoutBox.<Rectangle>applyMargins(0, Math.max(0, rightTableBorderWidth - rightHeaderBorderWidth) / 2, 0, Math.max(0, leftTableBorderWidth - leftHeaderBorderWidth) / 2, true);
            occupiedArea.getBBox().moveDown(headerHeight).increaseHeight(headerHeight);

            float maxHeaderBottomBorderWidth = 0;
            ArrayList<Border> rowBorders = headerRenderer.horizontalBorders.get(headerRenderer.horizontalBorders.size() - 1);
            for (Border border : rowBorders) {
                if (null != border && maxHeaderBottomBorderWidth < border.getWidth()) {
                    maxHeaderBottomBorderWidth = border.getWidth();
                }
            }

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
        borders = getBorders();
        bottomTableBorderWidth = null == borders[2] ? 0 : borders[2].getWidth();
        if (null != rows && 0 != rows.size()) {
            correctFirstRowTopBorders(borders[0], numberOfColumns);
        }
        topTableBorderWidth = getMaxTopWidth(borders[0]);

        // Apply halves of the borders. The other halves are applied on a Cell level
        layoutBox.<Rectangle>applyMargins(0, rightTableBorderWidth / 2, 0, leftTableBorderWidth / 2, false);
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
            if (row == 1 && Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.FORCED_PLACEMENT))) {
                deleteOwnProperty(Property.FORCED_PLACEMENT);
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

            Border widestRowBottomBorder = null;
            if (row + 1 < horizontalBorders.size()) {
                for (Border border : horizontalBorders.get(row + 1)) {
                    if (null != border && (null == widestRowBottomBorder || border.getWidth() > widestRowBottomBorder.getWidth())) {
                        widestRowBottomBorder = border;
                    }
                }
            }

            // if cell is in the last row on the page, its borders shouldn't collapse with the next row borders
            boolean processAsLast = false;
            while (cellProcessingQueue.size() > 0) {
                CellRendererInfo currentCellInfo = cellProcessingQueue.pop();
                col = currentCellInfo.column;
                CellRenderer cell = currentCellInfo.cellRenderer;
                int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
                int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);

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
                Border collapsedBottomBorder = getCollapsedBorder(oldBottomBorder, footerRenderer != null ? footerRenderer.horizontalBorders.get(0).get(col) : borders[2]);
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
                            if (!processAsLast && footerRenderer != null && tableModel.isSkipLastFooter() && tableModel.isComplete()) {
                                // TODO DEVSIX-1016
//                                LayoutArea potentialArea = new LayoutArea(area.getPageNumber(), layoutBox.clone());
//                                float footerHeight = footerRenderer.getOccupiedArea().getBBox().getHeight();
//                                potentialArea.getBBox().moveDown(footerHeight).increaseHeight(footerHeight);
//                                if (canFitRowsInGivenArea(potentialArea, row, columnWidths, heights)) {
//                                    layoutBox.increaseHeight(footerHeight).moveDown(footerHeight);
//                                    cellProcessingQueue.clear();
//                                    for (addCol = 0; addCol < currentRow.length; addCol++) {
//                                        if (currentRow[addCol] != null) {
//                                            cellProcessingQueue.addLast(new CellRendererInfo(currentRow[addCol], addCol, row));
//                                        }
//                                    }
//                                    deleteOwnProperty(Property.BORDER_BOTTOM);
//                                    bottomTableBorderWidth = null != getBorders()[2] ? getBorders()[2].getWidth() : 0;
//                                    footerRenderer = null;
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
                                            verticalAlignment = addRenderer.<VerticalAlignment>getProperty(Property.VERTICAL_ALIGNMENT);
                                            if (verticalAlignment != null && verticalAlignment.equals(VerticalAlignment.BOTTOM)) {
                                                if (row + addRenderer.getPropertyAsInteger(Property.ROWSPAN) - 1 < addRow) {
                                                    cellProcessingQueue.addLast(new CellRendererInfo(addRenderer, addCol, addRow));
                                                    cellWithBigRowspanAdded = true;
                                                } else {
                                                    horizontalBorders.get(row + 1).set(addCol, addRenderer.getBorders()[2]);
                                                    if (addCol == 0) {
                                                        for (int i = row; i >= 0; i--) {
                                                            if (!checkAndReplaceBorderInArray(verticalBorders, addCol, i, addRenderer.getBorders()[3], false)) {
                                                                break;
                                                            }
                                                        }
                                                    } else if (addCol == numberOfColumns - 1) {
                                                        for (int i = row; i >= 0; i--) {
                                                            if (!checkAndReplaceBorderInArray(verticalBorders, addCol + 1, i, addRenderer.getBorders()[1], true)) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (row + addRenderer.getPropertyAsInteger(Property.ROWSPAN) - 1 >= addRow) {
                                                cellProcessingQueue.addLast(new CellRendererInfo(addRenderer, addCol, addRow));
                                                cellWithBigRowspanAdded = true;
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    // if cell in current row has big rowspan
                                    // we need to process it specially too,
                                    // because some problems (for instance, borders related) can occur
                                    if (cell.getModelElement().getRowspan() > 1) {
                                        cellWithBigRowspanAdded = true;
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
                bottomTableBorderWidth = null == borders[2] ? 0 : borders[2].getWidth();
            }

            if (split || processAsLast || row == rows.size() - 1) {
                // Correct layout area of the last row rendered on the page
                if (heights.size() != 0) {
                    rowHeight = 0;
                    if (split && (hasContent)) {
                        horizontalBorders.add(row + 1, (ArrayList<Border>) horizontalBorders.get(row + 1).clone());
                    }
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
                        Border collapsedWithTableBorder = getCollapsedBorder(cellOwnBottomBorder,
                                footerRenderer != null ? footerRenderer.horizontalBorders.get(0).get(col) : borders[2]);
                        if (null != collapsedWithTableBorder && bottomTableBorderWidth < collapsedWithTableBorder.getWidth()) {
                            bottomTableBorderWidth = collapsedWithTableBorder.getWidth();
                        }
                        float collapsedBorderWidth = null == collapsedWithTableBorder ? 0 : collapsedWithTableBorder.getWidth();
                        if (collapsedWithNextRowBorderWidth != collapsedBorderWidth) {
                            cell.setBorders(collapsedWithTableBorder, 2);
                            for (int i = col; i < col + cell.getPropertyAsInteger(Property.COLSPAN); i++) {
                                horizontalBorders.get(rowN + 1).set(i, collapsedWithTableBorder);
                            }
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
                        for (col = 0; col < numberOfColumns; col++) {
                            if (null == horizontalBorders.get(1).get(col) || horizontalBorders.get(1).get(col).getWidth() < borders[2].getWidth()) {
                                horizontalBorders.get(1).set(col, borders[2]);
                            }
                        }
                    }
                }
                // Correct occupied areas of all added cells
                correctCellsOccupiedAreas(row, targetOverflowRowIndex);
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
                                    : horizontalBorders.get(lastRow),
                            numberOfColumns,
                            rows.size());
                    layoutBox.increaseHeight(bottomTableBorderWidth / 2);
                    occupiedArea.getBBox().moveUp(bottomTableBorderWidth / 2).decreaseHeight(bottomTableBorderWidth / 2);
                }
                footerRenderer.processRendererBorders(numberOfColumns);
                float rightFooterBorderWidth = footerRenderer.getMaxRightWidth(footerRenderer.getBorders()[1]);
                float leftFooterBorderWidth = footerRenderer.getMaxLeftWidth(footerRenderer.getBorders()[3]);

                layoutBox.moveDown(footerRenderer.occupiedArea.getBBox().getHeight()).increaseHeight(footerRenderer.occupiedArea.getBBox().getHeight());
                // apply the difference to set footer and table left/right margins identical
                layoutBox.<Rectangle>applyMargins(0, -rightFooterBorderWidth / 2, 0, -leftFooterBorderWidth / 2, false);
                if (hasProperty(Property.WIDTH)) {
                    footerRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(layoutBox.getWidth()));
                }

                footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
                layoutBox.<Rectangle>applyMargins(0, -rightFooterBorderWidth / 2, 0, -leftFooterBorderWidth / 2, true);
                float footerHeight = footerRenderer.getOccupiedAreaBBox().getHeight();
                footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
                layoutBox.setY(footerRenderer.occupiedArea.getBBox().getTop()).setHeight(occupiedArea.getBBox().getBottom() - layoutBox.getBottom());
                // fix footer borders
                if (!tableModel.isEmpty()) {
                    fixFooterBorders(
                            0 != lastFlushedRowBottomBorder.size() && 0 == row
                                    ? lastFlushedRowBottomBorder
                                    : horizontalBorders.get(lastRow),
                            numberOfColumns,
                            rows.size(),
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
                TableRenderer[] splitResult = !split && processAsLast ? split(row + 1, false) : split(row, hasContent);
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
                                currentRow[col] = null;
                                CellRenderer cellOverflow = (CellRenderer) splits[col].getOverflowRenderer();
                                if (splits[col].getStatus() == LayoutResult.PARTIAL) {
                                    cellOverflow.setBorders(Border.NO_BORDER, 0);
                                    cellSplit.setBorders(Border.NO_BORDER, 2);
                                } else if (Border.NO_BORDER != cellOverflow.<Border>getProperty(Property.BORDER_TOP)) {
                                    cellOverflow.deleteOwnProperty(Property.BORDER_TOP);
                                }
                                if (hasContent) {
                                    for (int j = col; j < col + cellOverflow.getPropertyAsInteger(Property.COLSPAN); j++) {
                                        splitResult[0].horizontalBorders.get(row + 1).set(j, getBorders()[2]);
                                        splitResult[1].horizontalBorders.get(0).set(j, getBorders()[2]);
                                    }
                                }
                                cellOverflow.deleteOwnProperty(Property.BORDER_BOTTOM);
                                cellOverflow.setBorders(cellOverflow.getBorders()[2], 2);
                                rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) cellOverflow.setParent(splitResult[1]);
                            } else {
                                rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) currentRow[col].setParent(splitResult[1]);
                                rows.get(targetOverflowRowIndex[col])[col].deleteOwnProperty(Property.BORDER_TOP);
                            }
                            rows.get(targetOverflowRowIndex[col])[col].occupiedArea = cellOccupiedArea;
                        } else if (currentRow[col] != null) {
                            rowspans[col] = currentRow[col].getModelElement().getRowspan();
                            if (hasContent && split) {
                                columnsWithCellToBeEnlarged[col] = true;
                                // for the future
                                splitResult[1].rows.get(0)[col].setBorders(getBorders()[0], 0);
                                for (int j = col; j < col + currentRow[col].getPropertyAsInteger(Property.COLSPAN); j++) {
                                    splitResult[0].horizontalBorders.get(row + 1).set(j, getBorders()[2]);
                                    splitResult[1].horizontalBorders.get(0).set(j, getBorders()[2]);
                                }
                            } else if (Border.NO_BORDER != currentRow[col].<Border>getProperty(Property.BORDER_TOP)) {
                                splitResult[1].rows.get(0)[col].deleteOwnProperty(Property.BORDER_TOP);
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
                                Border topBorder = currentRow[col].<Border>getProperty(Property.BORDER_TOP);
                                currentRow[col] = null;
                                rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) overflowCell.getRenderer().setParent(this);
                                rows.get(targetOverflowRowIndex[col])[col].deleteProperty(Property.HEIGHT);
                                rows.get(targetOverflowRowIndex[col])[col].deleteProperty(Property.MIN_HEIGHT);
                                rows.get(targetOverflowRowIndex[col])[col].deleteProperty(Property.MAX_HEIGHT);
                                rows.get(targetOverflowRowIndex[col])[col].setProperty(Property.BORDER_TOP, topBorder);
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
                                    Border topBorder = rows.get(i)[col].<Border>getProperty(Property.BORDER_TOP);
                                    rows.get(i)[col].isLastRendererForModelElement = false;
                                    rows.get(i)[col] = null;
                                    rows.get(targetOverflowRowIndex[col])[col] = (CellRenderer) overflowCell.getRenderer().setParent(this);
                                    rows.get(targetOverflowRowIndex[col])[col].setProperty(Property.BORDER_TOP, topBorder);
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

                            splitResult[0].horizontalBorders.clear();
                            splitResult[0].horizontalBorders.add(lastFlushedRowBottomBorder);

                            // hack to process 'margins'
                            splitResult[0].setBorders(widestLustFlushedBorder, 2);
                            splitResult[0].setBorders(Border.NO_BORDER, 0);
                            if (0 != splitResult[0].verticalBorders.size()) {
                                splitResult[0].setBorders(splitResult[0].verticalBorders.get(0).get(0), 3);
                                splitResult[0].setBorders(splitResult[0].verticalBorders.get(verticalBorders.size() - 1).get(0), 1);
                            }
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
                                horizontalBorders.clear();
                                horizontalBorders.add(topBorders);
                                horizontalBorders.add(bottomBorders);
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
            float rightFooterBorderWidth = footerRenderer.getMaxRightWidth(footerRenderer.getBorders()[1]);
            float leftFooterBorderWidth = footerRenderer.getMaxLeftWidth(footerRenderer.getBorders()[3]);

            layoutBox.moveDown(footerRenderer.occupiedArea.getBBox().getHeight()).increaseHeight(footerRenderer.occupiedArea.getBBox().getHeight());
            // apply the difference to set footer and table left/right margins identical
            layoutBox.<Rectangle>applyMargins(0, -rightFooterBorderWidth / 2,
                    0, -leftFooterBorderWidth / 2, false);
            if (hasProperty(Property.WIDTH)) {
                footerRenderer.setProperty(Property.WIDTH, UnitValue.createPointValue(layoutBox.getWidth()));
            }
            footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox)));
            layoutBox.<Rectangle>applyMargins(0, -rightFooterBorderWidth / 2,
                    0, -leftFooterBorderWidth / 2, true);

            float footerHeight = footerRenderer.getOccupiedAreaBBox().getHeight();
            footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);

            // fix borders
            fixFooterBorders(lastFlushedRowBottomBorder, numberOfColumns, rows.size(), useFooterBorders);
        }

        // if table is empty we still need to process table borders
        if (0 == childRenderers.size() && null == headerRenderer && null == footerRenderer) {
            ArrayList<Border> topHorizontalBorders = new ArrayList<Border>();
            ArrayList<Border> bottomHorizontalBorders = new ArrayList<Border>();
            for (int i = 0; i < numberOfColumns; i++) {
                bottomHorizontalBorders.add(Border.NO_BORDER);
            }
            ArrayList<Border> leftVerticalBorders = new ArrayList<Border>();
            ArrayList<Border> rightVerticalBorders = new ArrayList<Border>();

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
            horizontalBorders.set(0, topHorizontalBorders);
            horizontalBorders.add(bottomHorizontalBorders);
            leftVerticalBorders.add(borders[3]);
            rightVerticalBorders.add(borders[1]);
            verticalBorders = new ArrayList<>();
            verticalBorders.add(leftVerticalBorders);
            for (int i = 0; i < numberOfColumns - 1; i++) {
                verticalBorders.add(new ArrayList<Border>());
            }
            verticalBorders.add(rightVerticalBorders);
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
                horizontalBorders.get(horizontalBorders.size() - 1).clear();
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
        TableRenderer splitRenderer = createSplitRenderer(new Table.RowRange(rowRange.getStartRow(), rowRange.getStartRow() + row));
        splitRenderer.rows = rows.subList(0, row);
        int rowN = row;
        if (hasContent) {
            rowN++;
        }
        splitRenderer.horizontalBorders = new ArrayList<>();
        //splitRenderer.horizontalBorders.addAll(horizontalBorders);
        for (int i = 0; i <= rowN; i++) {
            splitRenderer.horizontalBorders.add(horizontalBorders.get(i));
        }
        splitRenderer.verticalBorders = new ArrayList<>();
        //splitRenderer.verticalBorders.addAll(verticalBorders);
        for (int i = 0; i < verticalBorders.size(); i++) {
            splitRenderer.verticalBorders.add(new ArrayList<Border>());
            for (int j = 0; j < ((0 == rowN) ? 1 : rowN); j++) {
                if (verticalBorders.get(i).size() != 0) {
                    splitRenderer.verticalBorders.get(i).add(verticalBorders.get(i).get(j));
                }
            }
        }
        splitRenderer.heights = heights;
        splitRenderer.columnWidths = columnWidths;
        splitRenderer.countedColumnWidth = countedColumnWidth;
        splitRenderer.totalWidthForColumns = totalWidthForColumns;
        TableRenderer overflowRenderer = createOverflowRenderer(new Table.RowRange(rowRange.getStartRow() + row, rowRange.getFinishRow()));
        overflowRenderer.rows = rows.subList(row, rows.size());
        splitRenderer.occupiedArea = occupiedArea;

        overflowRenderer.horizontalBorders = new ArrayList<>();
        //splitRenderer.horizontalBorders.addAll(horizontalBorders);
        for (int i = rowN; i < horizontalBorders.size(); i++) {
            overflowRenderer.horizontalBorders.add((ArrayList<Border>) horizontalBorders.get(i).clone());
        }
        overflowRenderer.verticalBorders = new ArrayList<>();
        //splitRenderer.verticalBorders.addAll(verticalBorders);
        for (int i = 0; i < verticalBorders.size(); i++) {
            overflowRenderer.verticalBorders.add(new ArrayList<Border>());
            for (int j = row; j < verticalBorders.get(i).size(); j++) {
                if (verticalBorders.get(i).size() != 0) {
                    overflowRenderer.verticalBorders.get(i).add(verticalBorders.get(i).get(j));
                }
            }
        }
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
        Table tableModel = (Table)getModelElement();
        processRendererBorders(tableModel.getNumberOfColumns());
        Border[] borders = getBorders();
        float rightTableBorderWidth = getMaxRightWidth(borders[1]);
        float leftTableBorderWidth = getMaxLeftWidth(borders[3]);
        return countTableMinMaxWidth(availableWidth, new float[] {0, rightTableBorderWidth, 0, leftTableBorderWidth}).toTableMinMaxWidth(availableWidth);
    }

    private ColumnMinMaxWidth countTableMinMaxWidth(float availableWidth, float[] collapsedTableBorderWidths) {
        Rectangle layoutBox = new Rectangle(availableWidth, AbstractRenderer.INF);
        Float tableWidth = retrieveWidth(layoutBox.getWidth());
        applyMargins(layoutBox, false);

        float rightTableBorderWidth = collapsedTableBorderWidths[1];
        float leftTableBorderWidth = collapsedTableBorderWidths[3];

        ColumnMinMaxWidth footerColWidth = null, headerColWidth = null;

        Table tableModel = (Table) getModelElement();
        int numberOfColumns =tableModel.getNumberOfColumns();
        if (tableModel.getFooter() != null) {
            footerRenderer = initFooterOrHeaderRenderer(true, getBorders());
            footerRenderer.processRendererBorders(numberOfColumns);
            float rightFooterBorderWidth = footerRenderer.getMaxRightWidth(footerRenderer.getBorders()[1]);
            float leftFooterBorderWidth = footerRenderer.getMaxLeftWidth(footerRenderer.getBorders()[3]);
            leftTableBorderWidth = Math.max(leftTableBorderWidth, leftFooterBorderWidth);
            rightTableBorderWidth = Math.max(rightTableBorderWidth, rightFooterBorderWidth);
            footerColWidth = footerRenderer.countRegionMinMaxWidth(availableWidth - leftTableBorderWidth / 2 - rightTableBorderWidth / 2, null, null);
        }

        boolean isFirstHeader = rowRange.getStartRow() == 0 && isOriginalNonSplitRenderer;
        boolean headerShouldBeApplied = !rows.isEmpty() && (!isOriginalNonSplitRenderer || isFirstHeader && !tableModel.isSkipFirstHeader());
        if (tableModel.getHeader() != null && headerShouldBeApplied) {
            headerRenderer = initFooterOrHeaderRenderer(false, getBorders());
            headerRenderer.processRendererBorders(numberOfColumns);
            float rightHeaderBorderWidth = headerRenderer.getMaxRightWidth(headerRenderer.getBorders()[1]);
            float leftHeaderBorderWidth = headerRenderer.getMaxLeftWidth(headerRenderer.getBorders()[3]);
            leftTableBorderWidth = Math.max(leftTableBorderWidth, leftHeaderBorderWidth);
            rightTableBorderWidth = Math.max(rightTableBorderWidth, rightHeaderBorderWidth);
            headerColWidth = headerRenderer.countRegionMinMaxWidth(availableWidth - leftTableBorderWidth / 2 - rightTableBorderWidth / 2, null, null);
        }

        // Apply halves of the borders. The other halves are applied on a Cell level
        layoutBox.<Rectangle>applyMargins(0, rightTableBorderWidth / 2, 0, leftTableBorderWidth / 2, false);
        tableWidth -= rightTableBorderWidth / 2 + leftTableBorderWidth / 2;

        ColumnMinMaxWidth tableColWidth = countRegionMinMaxWidth(tableWidth, headerColWidth, footerColWidth);
        countedMaxColumnWidth = tableColWidth.maxWidth;
        countedMinColumnWidth = tableColWidth.minWidth;
        return tableColWidth.setLayoutBoxWidth(layoutBox.getWidth());
    }

    private ColumnMinMaxWidth countRegionMinMaxWidth(float availableWidth, ColumnMinMaxWidth headerWidth, ColumnMinMaxWidth footerWidth) {
        Table tableModel = (Table) getModelElement();
        int nrow = rows.size();
        int ncol = tableModel.getNumberOfColumns();
        MinMaxWidth[][] cellsMinMaxWidth = new MinMaxWidth[nrow][ncol];
        Border[] borders = getBorders();
        ColumnMinMaxWidth result = new ColumnMinMaxWidth(ncol);

        int[][] cellsColspan = new int[nrow][ncol];
        for (int row = 0; row < nrow; ++row) {
            for (int col = 0; col < ncol; ++col) {
                CellRenderer cell = rows.get(row)[col];
                if (cell != null) {
                    cell.setParent(this);
                    int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
                    int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
                    //We place the width of big cells in each row of in last column its occupied place and save it's colspan for convenience.
                    int finishCol = col + colspan - 1;
                    cellsMinMaxWidth[row][finishCol] = cell.getMinMaxWidth(availableWidth);
                    cellsColspan[row][finishCol] = colspan;
                    for (int i = 1; i < rowspan; ++i) {
                        cellsMinMaxWidth[row - i][finishCol] = cellsMinMaxWidth[row][finishCol];
                        cellsColspan[row - i][finishCol] = colspan;
                    }
                }
            }
        }

        //The DP is used to count each column width.
        //In next arrays at the index i will be the corresponding sum width first i columns.
        float[] maxColumnsWidth = new float[ncol + 1];
        float[] minColumnsWidth = new float[ncol + 1];
        minColumnsWidth[0] = 0;
        maxColumnsWidth[0] = 0;
        int curColspan;
        for (int col = 0; col < ncol; ++col) {
            for (int row = 0; row < nrow; ++row) {
                if (cellsMinMaxWidth[row][col] != null) {
                    curColspan = cellsColspan[row][col];
                    maxColumnsWidth[col + 1] = Math.max(maxColumnsWidth[col], cellsMinMaxWidth[row][col].getMaxWidth() + maxColumnsWidth[col - curColspan + 1]);
                    minColumnsWidth[col + 1] = Math.max(minColumnsWidth[col], cellsMinMaxWidth[row][col].getMinWidth() + minColumnsWidth[col - curColspan + 1]);
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

        if (drawTop) {
            drawHorizontalBorder(0, startX, startY, drawContext.getCanvas());
        }

        float y1 = startY;
        if (heights.size() > 0) {
            y1 -= (float) heights.get(0);
        }
        for (int i = 1; i < horizontalBorders.size() - 1; i++) {
            drawHorizontalBorder(i, startX, y1, drawContext.getCanvas());
            if (i < heights.size()) {
                y1 -= (float) heights.get(i);
            }
        }
        if (drawBottom) {
            drawHorizontalBorder(horizontalBorders.size() - 1, startX, y1, drawContext.getCanvas());
        }

        float x1 = startX;
        for (int i = 0; i < verticalBorders.size(); i++) {
            drawVerticalBorder(i, startY, x1, drawContext.getCanvas());
            if (i < countedColumnWidth.length) {
                x1 += countedColumnWidth[i];
            }
        }

        if (isTagged) {
            drawContext.getCanvas().closeTag();
        }
    }

    private void drawHorizontalBorder(int i, float startX, float y1, PdfCanvas canvas) {
        ArrayList<Border> borders = horizontalBorders.get(i);
        float x1 = startX;
        float x2 = x1 + countedColumnWidth[0];
        if (i == 0) {
            if (verticalBorders != null && verticalBorders.size() > 0 && verticalBorders.get(0).size() > 0 && verticalBorders.get(verticalBorders.size() - 1).size() > 0) {
                Border firstBorder = verticalBorders.get(0).get(0);
                if (firstBorder != null) {
                    x1 -= firstBorder.getWidth() / 2;
                }
            }
        } else if (i == horizontalBorders.size() - 1) {
            if (verticalBorders != null && verticalBorders.size() > 0 && verticalBorders.get(0).size() > 0 &&
                    verticalBorders.get(verticalBorders.size() - 1) != null && verticalBorders.get(verticalBorders.size() - 1).size() > 0
                    && verticalBorders.get(0) != null) {
                Border firstBorder = verticalBorders.get(0).get(verticalBorders.get(0).size() - 1);
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
            if (verticalBorders != null && verticalBorders.size() > j && verticalBorders.get(j) != null && verticalBorders.get(j).size() > 0) {
                if (i == 0) {
                    if (verticalBorders.get(j).get(i) != null)
                        x2 += verticalBorders.get(j).get(i).getWidth() / 2;
                } else if (i == horizontalBorders.size() - 1 && verticalBorders.get(j).size() >= i - 1 && verticalBorders.get(j).get(i - 1) != null) {
                    x2 += verticalBorders.get(j).get(i - 1).getWidth() / 2;
                }
            }

            lastBorder.drawCellBorder(canvas, x1, y1, x2, y1);
        }
    }

    private void drawVerticalBorder(int i, float startY, float x1, PdfCanvas canvas) {
        ArrayList<Border> borders = verticalBorders.get(i);
        float y1 = startY;
        float y2 = y1;
        if (!heights.isEmpty()) {
            y2 = y1 - (float) heights.get(0);
        }
        int j;
        for (j = 1; j < borders.size(); j++) {
            Border prevBorder = borders.get(j - 1);
            Border curBorder = borders.get(j);
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
        Border lastBorder = borders.get(j - 1);
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

    // important to invoke on each new page
    private void updateFirstRowBorders(int colN) {
        int col = 0;
        int row = 0;
        ArrayList<Border> topBorders = horizontalBorders.get(0);
        topBorders.clear();
        while (col < colN) {
            if (null != rows.get(row)[col]) {
                // we may have deleted collapsed border property trying to process the row as last on the page
                Border collapsedBottomBorder = null;
                int colspan = (int) rows.get(row)[col].getPropertyAsInteger(Property.COLSPAN);
                for (int i = col; i < col + colspan; i++) {
                    topBorders.add(rows.get(row)[col].getBorders()[0]);
                    collapsedBottomBorder = getCollapsedBorder(collapsedBottomBorder, horizontalBorders.get(1).get(i));
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
    private void correctFirstRowTopBorders(Border tableBorder, int colN) {
        int col = 0;
        int row = 0;
        ArrayList<Border> topBorders = horizontalBorders.get(0);
        ArrayList<Border> bordersToBeCollapsedWith = null != headerRenderer
                ? headerRenderer.horizontalBorders.get(headerRenderer.horizontalBorders.size() - 1)
                : new ArrayList<Border>();
        if (null == headerRenderer) {
            for (col = 0; col < colN; col++) {
                bordersToBeCollapsedWith.add(tableBorder);
            }
        }
        col = 0;
        while (col < colN) {
            if (null != rows.get(row)[col]) {
                Border oldTopBorder = rows.get(row)[col].getBorders()[0];
                Border resultCellTopBorder = null;
                Border collapsedBorder = null;
                int colspan = (int) rows.get(row)[col].getPropertyAsInteger(Property.COLSPAN);
                for (int i = col; i < col + colspan; i++) {
                    collapsedBorder = getCollapsedBorder(oldTopBorder, bordersToBeCollapsedWith.get(i));
                    if (null == topBorders.get(i) || (null != collapsedBorder && topBorders.get(i).getWidth() < collapsedBorder.getWidth())) {
                        topBorders.set(i, collapsedBorder);
                    }
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
        if (null != headerRenderer) {
            headerRenderer.horizontalBorders.set(headerRenderer.horizontalBorders.size() - 1, topBorders);
        }
    }

    private void collapseAllBorders(Border[] tableBorders, int startRow, int finishRow, int colN) {
        CellRenderer[] currentRow;
        for (int row = startRow; row <= finishRow; row++) {
            currentRow = rows.get(row);
            boolean hasCells = false;
            for (int col = 0; col < colN; col++) {
                if (null != currentRow[col]) {
                    int colspan = (int)currentRow[col].getPropertyAsInteger(Property.COLSPAN);
                    prepareBuildingBordersArrays(currentRow[col], tableBorders, colN, row, col);
                    buildBordersArrays(currentRow[col], row, col);
                    hasCells = true;
                    col+=colspan-1;
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
                if (row == finishRow) {
                    Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                    logger.warn(LogMessageConstant.LAST_ROW_IS_NOT_COMPLETE);
                }
            }
        }
    }

    private void initializeBorders(ArrayList<Border> lastFlushedRowBottomBorder, boolean isFirstOnPage) {
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

    private float getMaxTopWidth(Border tableTopBorder) {
        float width = null == tableTopBorder ? 0 : tableTopBorder.getWidth();
        ArrayList<Border> topBorders = horizontalBorders.get(0);
        if (0 != topBorders.size()) {
            for (Border border : topBorders) {
                if (null != border) {
                    if (border.getWidth() > width) {
                        width = border.getWidth();
                    }
                }
            }
        }
        return width;
    }

    private float getMaxRightWidth(Border tableRightBorder) {
        float width = null == tableRightBorder ? 0 : tableRightBorder.getWidth();
        if (0 != verticalBorders.size()) {
            ArrayList<Border> rightBorders = verticalBorders.get(verticalBorders.size() - 1);
            if (0 != rightBorders.size()) {
                for (Border border : rightBorders) {
                    if (null != border) {
                        if (border.getWidth() > width) {
                            width = border.getWidth();
                        }
                    }
                }
            }
        }
        return width;
    }

    private float getMaxLeftWidth(Border tableLeftBorder) {
        float width = null == tableLeftBorder ? 0 : tableLeftBorder.getWidth();
        if (0 != verticalBorders.size()) {
            ArrayList<Border> leftBorders = verticalBorders.get(0);
            if (0 != leftBorders.size()) {
                for (Border border : leftBorders) {
                    if (null != border) {
                        if (border.getWidth() > width) {
                            width = border.getWidth();
                        }
                    }
                }
            }
        }
        return width;
    }


    private boolean[] collapseFooterBorders(ArrayList<Border> tableBottomBorders, int colNum, int rowNum) {
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

    private void fixFooterBorders(ArrayList<Border> tableBottomBorders, int colNum, int rowNum, boolean[] useFooterBorders) {
        int j = 0;
        int i = 0;
        while (i < colNum) {
            if (null != footerRenderer.rows.get(j)[i]) {
                for (int k = i; k < i + footerRenderer.rows.get(j)[i].getModelElement().getColspan(); k++) {
                    if (!useFooterBorders[k]) {
                        footerRenderer.horizontalBorders.get(j).set(k, tableBottomBorders.get(k));
                    }
                }
                i += footerRenderer.rows.get(j)[i].getModelElement().getColspan();
                j = 0;
            } else {
                j++;
                if (j == rowNum) {
                    break;
                }
            }
        }
    }

    private void correctCellsOccupiedAreas(int row, int[] targetOverflowRowIndex) {
        // Correct occupied areas of all added cells
        for (int k = 0; k <= row; k++) {
            CellRenderer[] currentRow = rows.get(k);
            if (k < row || (row + 1 == heights.size())) {
                for (int col = 0; col < currentRow.length; col++) {
                    CellRenderer cell = currentRow[col];
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

    private void prepareBuildingBordersArrays(CellRenderer cell, Border[] tableBorders, int colNum, int row, int col) {
        Border[] cellBorders = cell.getBorders();
        int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
        if (0 == col) {
            cell.setProperty(Property.BORDER_LEFT, getCollapsedBorder(cellBorders[3], tableBorders[3]));
        }
        if (colNum == col + colspan) {
            cell.setProperty(Property.BORDER_RIGHT, getCollapsedBorder(cellBorders[1], tableBorders[1]));
        }
    }

    private void buildBordersArrays(CellRenderer cell, int row, int col) {
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

    private void buildBordersArrays(CellRenderer cell, int row, boolean isNeighbourCell) {
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
            int numOfColumns = ((Table) getModelElement()).getNumberOfColumns();
            for (int k = row - rowspan + 1; k <= row; k++) {
                ArrayList<Border> borders = horizontalBorders.get(k);
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
                ArrayList<Border> borders = verticalBorders.get(k);
                if (borders.size() < row + rowspan) {
                    for (int l = borders.size(); l < row + rowspan; l++) {
                        borders.add(null);
                    }
                }
            }
        }
    }

    private float[] getCollapsedBorderWidths(List<CellRenderer[]> rowList, Border[] tableBorders, boolean collapseTop) {
        // Find left, right and top collapsed borders widths.
        // In order to find left and right border widths we try to consider as few rows as possible
        // i.e. the borders still can be drawn outside the layout area.
        float[] widths = {0, -1, 0, -1};

        Border border;
        int row = 0;
        while (row < rowList.size() && (-1 == widths[3] || -1 == widths[1])) {
            CellRenderer[] currentRow = rowList.get(row);
            if (0 == row) {
                int col = 0;
                while (col < currentRow.length) {
                    if (null != rowList.get(row)[col]) {
                        border = rowList.get(row)[col].getBorders()[0];
                        if (null != border && widths[0] < border.getWidth()) {
                            widths[0] = border.getWidth();
                        }
                        col += (int) rowList.get(row)[col].getPropertyAsInteger(Property.COLSPAN);
                        row = 0;
                    } else {
                        row++;
                        if (row == rowList.size()) {
                            break;
                        }
                    }
                }
                row = 0;
            }
            if (0 != currentRow.length) {
                if (null != currentRow[0]) {
                    border = currentRow[0].getBorders()[3];
                    if (null != border && border.getWidth() > widths[3]) {
                        widths[3] = border.getWidth();
                    }
                }
                // the last cell in a row can have big colspan
                int lastInRow = currentRow.length - 1;
                while (lastInRow >= 0 && null == currentRow[lastInRow]) {
                    lastInRow--;
                }
                if (lastInRow >= 0 && currentRow.length == lastInRow + currentRow[lastInRow].getPropertyAsInteger(Property.COLSPAN)) {
                    border = currentRow[lastInRow].getBorders()[1];
                    if (null != border && border.getWidth() > widths[1]) {
                        widths[1] = border.getWidth();
                    }
                }
            }
            row++;
        }
        // collapse with table borders
        if (collapseTop) {
            widths[0] = Math.max(null == tableBorders[0] ? 0 : tableBorders[0].getWidth(), widths[0]);
        }
        widths[1] = Math.max(null == tableBorders[1] ? 0 : tableBorders[1].getWidth(), widths[1]);
        widths[2] = null == tableBorders[2] ? 0 : tableBorders[2].getWidth();
        widths[3] = Math.max(null == tableBorders[3] ? 0 : tableBorders[3].getWidth(), widths[3]);
        return widths;
    }

    /**
     * Returns the collapsed border. We process collapse
     * if the table border width is strictly greater than cell border width.
     *
     * @param cellBorder  cell border
     * @param tableBorder table border
     * @return the collapsed border
     */
    private Border getCollapsedBorder(Border cellBorder, Border tableBorder) {
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

    private boolean checkAndReplaceBorderInArray(ArrayList<ArrayList<Border>> borderArray, int i, int j, Border borderToAdd, boolean hasPriority) {
        if (borderArray.size() <= i) {
            for (int count = borderArray.size(); count <= i; count++) {
                borderArray.add(new ArrayList<Border>());
            }
        }
        ArrayList<Border> borders = borderArray.get(i);
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
            renderer.setBorders(getCollapsedBorder(borders[innerBorder], tableBorders[innerBorder]), innerBorder);
            setBorders(Border.NO_BORDER, innerBorder);
        }
        renderer.setBorders(getCollapsedBorder(borders[1], tableBorders[1]), 1);
        renderer.setBorders(getCollapsedBorder(borders[3], tableBorders[3]), 3);
        renderer.setBorders(getCollapsedBorder(borders[outerBorder], tableBorders[outerBorder]), outerBorder);
        setBorders(Border.NO_BORDER, outerBorder);
        return renderer;
    }

    private TableRenderer processRendererBorders(int numberOfColumns) {
        deleteOwnProperty(Property.BORDERS_INITIALIZED);
        initializeBorders(new ArrayList<Border>(), true);
        setProperty(Property.BORDERS_INITIALIZED, true);
        deleteOwnProperty(Property.BORDERS_COLLAPSED);
        collapseAllBorders(getBorders(), rowRange.getStartRow(), rowRange.getFinishRow(), numberOfColumns);
        setProperty(Property.BORDERS_COLLAPSED, true);
        return this;
    }

    /**
     * Calculate column width
     * @return total table width
     */
    private float calculateColumnWidths(float availableWidth, float[] borders) {
        if (countedColumnWidth == null || totalWidthForColumns != availableWidth) {
            TableWidths tableWidths = new TableWidths(this, availableWidth, borders);
            if (tableWidths.hasFixedLayout()) {
                countedColumnWidth = tableWidths.fixedLayout();
            } else {
                ColumnMinMaxWidth minMax = countTableMinMaxWidth(availableWidth, borders);
                countedColumnWidth = tableWidths.autoLayout(minMax.getMinWidth(), minMax.getMaxWidth());
            }
        }

        float sum = 0;
        for (float column : countedColumnWidth) {
            sum += column;
        }
        return sum + borders[1] / 2 + borders[3] / 2;
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
        private float[] minWidth;
        private float[] maxWidth;
        private float layoutBoxWidth;

        float[] getMinWidth() {
            return minWidth;
        }

        float[] getMaxWidth() {
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
