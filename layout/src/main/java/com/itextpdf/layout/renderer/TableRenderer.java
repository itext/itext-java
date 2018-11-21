/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.margincollapse.MarginsCollapseHandler;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.CaptionSide;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.layout.tagging.LayoutTaggingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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
    protected DivRenderer captionRenderer;
    /**
     * True for newly created renderer. For split renderers this is set to false. Used for tricky layout.
     */
    protected boolean isOriginalNonSplitRenderer = true;
    TableBorders bordersHandler;
    private float[] columnWidths = null;
    private List<Float> heights = new ArrayList<>();
    private float[] countedColumnWidth = null;
    private float totalWidthForColumns;
    private float topBorderMaxWidth;

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
        if (bordersHandler instanceof SeparatedTableBorders) {
            super.applyBorderBox(rect, borders, reverse);
        } else {
            // Do nothing here. Applying border box for tables is indeed difficult operation and is done on #layout()
        }
        return rect;
    }

    @Override
    protected Rectangle applyPaddings(Rectangle rect, UnitValue[] paddings, boolean reverse) {
        if (bordersHandler instanceof SeparatedTableBorders) {
            super.applyPaddings(rect, paddings, reverse);
        } else {
            // Do nothing here. Tables with collapsed borders don't have padding.
        }
        return rect;
    }

    @Override
    public Rectangle applyPaddings(Rectangle rect, boolean reverse) {
        if (bordersHandler instanceof SeparatedTableBorders) {
            super.applyPaddings(rect, reverse);
        } else {
            // Do nothing here. Tables with collapsed borders don't have padding.
        }
        return rect;
    }

    /**
     * Applies the given spacings on the given rectangle
     *
     * @param rect              a rectangle spacings will be applied on.
     * @param horizontalSpacing the horizontal spacing to be applied on the given rectangle
     * @param verticalSpacing   the vertical spacing to be applied on the given rectangle
     * @param reverse           indicates whether the spacings will be applied
     *                          inside (in case of false) or outside (in case of false) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    private Rectangle applySpacing(Rectangle rect, float horizontalSpacing, float verticalSpacing, boolean reverse) {
        if (bordersHandler instanceof SeparatedTableBorders) {
            return rect.applyMargins(verticalSpacing / 2, horizontalSpacing / 2, verticalSpacing / 2, horizontalSpacing / 2, reverse);
        } else {
            // Do nothing here. Tables with collapsed borders don't have spacing.
        }
        return rect;
    }

    /**
     * Applies the given horizontal or vertical spacing on the given rectangle
     *
     * @param rect         a rectangle spacings will be applied on.
     * @param spacing      the horizontal or vertical spacing to be applied on the given rectangle
     * @param isHorizontal defines whether the provided spacing should be applied as a horizontal or a vertical one
     * @param reverse      indicates whether the spacings will be applied
     *                     inside (in case of false) or outside (in case of false) the rectangle.
     * @return a {@link Rectangle border box} of the renderer
     */
    private Rectangle applySingleSpacing(Rectangle rect, float spacing, boolean isHorizontal, boolean reverse) {
        if (bordersHandler instanceof SeparatedTableBorders) {
            if (isHorizontal) {
                return rect.applyMargins(0, spacing / 2, 0, spacing / 2, reverse);
            } else {
                return rect.applyMargins(spacing / 2, 0, spacing / 2, 0, reverse);
            }
        } else {
            // Do nothing here. Tables with collapsed borders don't have spacing.
        }
        return rect;
    }

    Table getTable() {
        return (Table) getModelElement();
    }

    private void initializeHeaderAndFooter(boolean isFirstOnThePage) {
        Table table = (Table) getModelElement();
        Border[] tableBorder = getBorders();

        Table headerElement = table.getHeader();
        boolean isFirstHeader = rowRange.getStartRow() == 0 && isOriginalNonSplitRenderer;
        boolean headerShouldBeApplied = (table.isComplete() || !rows.isEmpty()) && (isFirstOnThePage && (!table.isSkipFirstHeader() || !isFirstHeader))
                && !Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.IGNORE_HEADER));
        if (headerElement != null && headerShouldBeApplied) {
            headerRenderer = initFooterOrHeaderRenderer(false, tableBorder);
        }

        Table footerElement = table.getFooter();
        // footer can be skipped, but after the table content will be layouted
        boolean footerShouldBeApplied = !(table.isComplete() && 0 != table.getLastRowBottomBorder().size() && table.isSkipLastFooter())
                && !Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.IGNORE_FOOTER));
        if (footerElement != null && footerShouldBeApplied) {
            footerRenderer = initFooterOrHeaderRenderer(true, tableBorder);
        }
    }

    private void initializeCaptionRenderer(Div caption) {
        if (isOriginalNonSplitRenderer && null != caption) {
            captionRenderer = (DivRenderer) caption.createRendererSubTree();
            captionRenderer.setParent(this.parent);
            LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper != null) {
                taggingHelper.addKidsHint(this, Collections.<IRenderer>singletonList(captionRenderer));
                LayoutTaggingHelper.addTreeHints(taggingHelper, captionRenderer);
            }
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
        Float blockMinHeight = retrieveMinHeight();
        Float blockMaxHeight = retrieveMaxHeight();

        LayoutArea area = layoutContext.getArea();
        boolean wasParentsHeightClipped = layoutContext.isClippedHeight();
        boolean wasHeightClipped = false;
        Rectangle layoutBox = area.getBBox().clone();

        Table tableModel = (Table) getModelElement();
        if (!tableModel.isComplete()) {
            setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(0f));
        }
        if (rowRange.getStartRow() != 0) {
            setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(0f));
        }

        // we can invoke #layout() twice (processing KEEP_TOGETHER for instance)
        // so we need to clear the results of previous #layout() invocation
        heights.clear();
        childRenderers.clear();

        // Cells' up moves occured while split processing
        // key is column number (there can be only one move during one split)
        // value is the previous row number of the cell
        Map<Integer, Integer> rowMoves = new HashMap<>();

        int row, col;

        int numberOfColumns = ((Table) getModelElement()).getNumberOfColumns();

        // The last flushed row. Empty list if the table hasn't been set incomplete
        List<Border> lastFlushedRowBottomBorder = tableModel.getLastRowBottomBorder();
        boolean isAndWasComplete = tableModel.isComplete() && 0 == lastFlushedRowBottomBorder.size();
        boolean isFirstOnThePage = 0 == rowRange.getStartRow() || isFirstOnRootArea(true);

        if (!isFooterRenderer() && !isHeaderRenderer()) {
            if (isOriginalNonSplitRenderer) {
                boolean isSeparated = BorderCollapsePropertyValue.SEPARATE.equals(this.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE));
                bordersHandler = isSeparated
                        ? (TableBorders) new SeparatedTableBorders(rows, numberOfColumns, getBorders(), !isAndWasComplete ? rowRange.getStartRow() : 0)
                        : (TableBorders) new CollapsedTableBorders(rows, numberOfColumns, getBorders(), !isAndWasComplete ? rowRange.getStartRow() : 0);
                bordersHandler.initializeBorders();
            }
        }
        bordersHandler.setRowRange(rowRange.getStartRow(), rowRange.getFinishRow());
        initializeHeaderAndFooter(isFirstOnThePage);

        // update
        bordersHandler.updateBordersOnNewPage(isOriginalNonSplitRenderer, isFooterRenderer() || isHeaderRenderer(), this, headerRenderer, footerRenderer);
        if (isOriginalNonSplitRenderer) {
            correctRowRange();
        }
        float horizontalBorderSpacing = bordersHandler instanceof SeparatedTableBorders && null != this.getPropertyAsFloat(Property.HORIZONTAL_BORDER_SPACING)
                ? (float) this.getPropertyAsFloat(Property.HORIZONTAL_BORDER_SPACING)
                : 0f;
        float verticalBorderSpacing = bordersHandler instanceof SeparatedTableBorders && null != this.getPropertyAsFloat(Property.VERTICAL_BORDER_SPACING)
                ? (float) this.getPropertyAsFloat(Property.VERTICAL_BORDER_SPACING)
                : 0f;
        if (!isAndWasComplete && !isFirstOnThePage) {
            layoutBox.increaseHeight(verticalBorderSpacing);
        }
        if (isOriginalRenderer()) {
            UnitValue[] margins = getMargins();
            if (!margins[1].isPointValue()) {
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_RIGHT));
            }
            if (!margins[3].isPointValue()) {
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_LEFT));
            }
            UnitValue[] paddings = getPaddings();
            if (!paddings[1].isPointValue()) {
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_RIGHT));
            }
            if (!paddings[3].isPointValue()) {
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_LEFT));
            }
            calculateColumnWidths(layoutBox.getWidth()
                    - margins[1].getValue() - margins[3].getValue()
                    - paddings[1].getValue() - paddings[3].getValue());
        }
        float tableWidth = getTableWidth();

        MarginsCollapseHandler marginsCollapseHandler = null;
        boolean marginsCollapsingEnabled = Boolean.TRUE.equals(getPropertyAsBoolean(Property.COLLAPSING_MARGINS));
        if (marginsCollapsingEnabled) {
            marginsCollapseHandler = new MarginsCollapseHandler(this, layoutContext.getMarginsCollapseInfo());
        }

        List<Rectangle> siblingFloatRendererAreas = layoutContext.getFloatRendererAreas();
        float clearHeightCorrection = FloatingHelper.calculateClearHeightCorrection(this, siblingFloatRendererAreas, layoutBox);
        FloatPropertyValue floatPropertyValue = this.<FloatPropertyValue>getProperty(Property.FLOAT);
        if (FloatingHelper.isRendererFloating(this, floatPropertyValue)) {
            layoutBox.decreaseHeight(clearHeightCorrection);
            FloatingHelper.adjustFloatedTableLayoutBox(this, layoutBox, tableWidth, siblingFloatRendererAreas, floatPropertyValue);
        } else {
            clearHeightCorrection = FloatingHelper.adjustLayoutBoxAccordingToFloats(siblingFloatRendererAreas, layoutBox, tableWidth, clearHeightCorrection, marginsCollapseHandler);
        }

        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.startMarginsCollapse(layoutBox);
        }
        applyMargins(layoutBox, false);
        applyFixedXOrYPosition(true, layoutBox);
        applyPaddings(layoutBox, false);

        if (null != blockMaxHeight && blockMaxHeight <= layoutBox.getHeight()
                && !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
            layoutBox.moveUp(layoutBox.getHeight() - (float) blockMaxHeight).setHeight((float) blockMaxHeight);
            wasHeightClipped = true;
        }

        initializeCaptionRenderer(getTable().getCaption());
        if (captionRenderer != null) {
            float minCaptionWidth = captionRenderer.getMinMaxWidth().getMinWidth();
            LayoutResult captionLayoutResult = captionRenderer.layout(new LayoutContext(
                    new LayoutArea(area.getPageNumber(), new Rectangle(layoutBox.getX(), layoutBox.getY(), Math.max(tableWidth, minCaptionWidth), layoutBox.getHeight())), wasHeightClipped || wasParentsHeightClipped));
            if (LayoutResult.FULL != captionLayoutResult.getStatus()) {
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, captionLayoutResult.getCauseOfNothing());
            }
            float captionHeight = captionLayoutResult.getOccupiedArea().getBBox().getHeight();
            if (CaptionSide.BOTTOM.equals(tableModel.getCaption().<CaptionSide>getProperty(Property.CAPTION_SIDE))) {
                captionRenderer.move(0, -(layoutBox.getHeight() - captionHeight));
                layoutBox.decreaseHeight(captionHeight);
                layoutBox.moveUp(captionHeight);
            } else {
                layoutBox.decreaseHeight(captionHeight);
            }
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

            LayoutResult result = footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox), wasHeightClipped || wasParentsHeightClipped));
            if (result.getStatus() != LayoutResult.FULL) {
                // we've changed it during footer initialization. However, now we need to process borders again as they were.
                deleteOwnProperty(Property.BORDER_BOTTOM);
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, result.getCauseOfNothing());
            }
            float footerHeight = result.getOccupiedArea().getBBox().getHeight();
            footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);
            // The footer has reserved the space for its top border-spacing.
            // However, since this space is shared with the table, it may be used by the table.
            layoutBox.moveDown(verticalBorderSpacing).increaseHeight(verticalBorderSpacing);
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
            } else if (null != footerRenderer) {
                footerRenderer.bordersHandler.collapseTableWithHeader(headerRenderer.bordersHandler, true);
            }
            topBorderMaxWidth = bordersHandler.getMaxTopWidth(); // first row own top border. We will use it while header processing
            LayoutResult result = headerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox), wasHeightClipped || wasParentsHeightClipped));
            if (result.getStatus() != LayoutResult.FULL) {
                // we've changed it during header initialization. However, now we need to process borders again as they were.
                deleteOwnProperty(Property.BORDER_TOP);
                return new LayoutResult(LayoutResult.NOTHING, null, null, this, result.getCauseOfNothing());
            }
            float headerHeight = result.getOccupiedArea().getBBox().getHeight();
            layoutBox.decreaseHeight(headerHeight);
            occupiedArea.getBBox().moveDown(headerHeight).increaseHeight(headerHeight);
            bordersHandler.fixHeaderOccupiedArea(occupiedArea.getBBox(), layoutBox);
            // The header has reserved the space for its bottom border-spacing.
            // However, since this space is shared with the table, it may be used by the table.
            layoutBox.increaseHeight(verticalBorderSpacing);
            occupiedArea.getBBox().moveUp(verticalBorderSpacing).decreaseHeight(verticalBorderSpacing);
        }

        // Apply spacings. Since occupiedArea was already created it's a bit more difficult for the latter.
        applySpacing(layoutBox, horizontalBorderSpacing, verticalBorderSpacing, false);
        applySingleSpacing(occupiedArea.getBBox(), (float) horizontalBorderSpacing, true, false);
        occupiedArea.getBBox().moveDown(verticalBorderSpacing / 2);

        topBorderMaxWidth = bordersHandler.getMaxTopWidth();
        bordersHandler.applyLeftAndRightTableBorder(layoutBox, false);
        // Table should have a row and some child elements in order to be considered non empty
        bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox,
                tableModel.isEmpty() || 0 == rows.size(), isAndWasComplete, false);
        if (bordersHandler instanceof SeparatedTableBorders) {
            float bottomBorderWidth = bordersHandler.getMaxBottomWidth();
            layoutBox
                    .moveUp(bottomBorderWidth)
                    .decreaseHeight(bottomBorderWidth);
        }

        LayoutResult[] splits = new LayoutResult[numberOfColumns];
        // This represents the target row index for the overflow renderer to be placed to.
        // Usually this is just the current row id of a cell, but it has valuable meaning when a cell has rowspan.
        int[] targetOverflowRowIndex = new int[numberOfColumns];
        // if this is the last renderer, we will use that information to enlarge rows proportionally
        List<Boolean> rowsHasCellWithSetHeight = new ArrayList<>();

        for (row = 0; row < rows.size(); row++) {
            List<Rectangle> childFloatRendererAreas = new ArrayList<>();
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
                if (!(bordersHandler instanceof SeparatedTableBorders)) {
                    bordersHandler.applyCellIndents(cellArea.getBBox(), cellIndents[0], cellIndents[1], cellIndents[2] + widestRowBottomBorderWidth, cellIndents[3], false);
                }
                // update cell width
                cellWidth = cellArea.getBBox().getWidth();

                // create hint for cell if not yet created
                LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
                if (taggingHelper != null) {
                    taggingHelper.addKidsHint(this, Collections.<IRenderer>singletonList(cell));
                    LayoutTaggingHelper.addTreeHints(taggingHelper, cell);
                }

                LayoutResult cellResult = cell.setParent(this).layout(new LayoutContext(cellArea, null, childFloatRendererAreas, wasHeightClipped || wasParentsHeightClipped));

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
//                    if (cellResult.getStatus() != LayoutResult.FULL || Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.FORCED_PLACEMENT))) { TODO DEVSIX-1735
                    if (cellResult.getStatus() != LayoutResult.FULL) {
                        // first time split occurs
                        if (!split) {
                            int addCol;
                            // This is a case when last footer should be skipped and we might face an end of the table.
                            // We check if we can fit all the rows right now and the split occurred only because we reserved
                            // space for footer before, and if yes we skip footer and write all the content right now.
                            boolean skipLastFooter = null != footerRenderer && tableModel.isSkipLastFooter() && tableModel.isComplete()
                                    && !Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.FORCED_PLACEMENT));
                            if (skipLastFooter) {
                                LayoutArea potentialArea = new LayoutArea(area.getPageNumber(), layoutBox.clone());
                                applySingleSpacing(potentialArea.getBBox(), horizontalBorderSpacing, true, true);
                                // Fix layout area
                                Border widestRowTopBorder = bordersHandler.getWidestHorizontalBorder(rowRange.getStartRow() + row);
                                if (bordersHandler instanceof CollapsedTableBorders && null != widestRowTopBorder) {
                                    potentialArea.getBBox().increaseHeight((float) widestRowTopBorder.getWidth() / 2);
                                }
                                if (null == headerRenderer) {
                                    potentialArea.getBBox().increaseHeight(bordersHandler.getMaxTopWidth());
                                }
                                bordersHandler.applyLeftAndRightTableBorder(potentialArea.getBBox(), true);
                                float footerHeight = footerRenderer.getOccupiedArea().getBBox().getHeight();
                                potentialArea.getBBox().moveDown(footerHeight - (float) verticalBorderSpacing / 2).increaseHeight(footerHeight);

                                TableRenderer overflowRenderer = createOverflowRenderer(new Table.RowRange(rowRange.getStartRow() + row, rowRange.getFinishRow()));
                                overflowRenderer.rows = rows.subList(row, rows.size());
                                overflowRenderer.setProperty(Property.IGNORE_HEADER, true);
                                overflowRenderer.setProperty(Property.IGNORE_FOOTER, true);
                                overflowRenderer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(0));
                                overflowRenderer.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(0));
                                overflowRenderer.setProperty(Property.MARGIN_LEFT, UnitValue.createPointValue(0));
                                overflowRenderer.setProperty(Property.MARGIN_RIGHT, UnitValue.createPointValue(0));
                                // we've already applied the top table border on header
                                if (null != headerRenderer) {
                                    overflowRenderer.setProperty(Property.BORDER_TOP, Border.NO_BORDER);
                                }
                                overflowRenderer.bordersHandler = bordersHandler;
                                // save old bordersHandler properties
                                bordersHandler.skipFooter(overflowRenderer.getBorders());
                                if (null != headerRenderer) {
                                    bordersHandler.skipHeader(overflowRenderer.getBorders());
                                }
                                int savedStartRow = overflowRenderer.bordersHandler.startRow;
                                overflowRenderer.bordersHandler.setStartRow(row);
                                prepareFooterOrHeaderRendererForLayout(overflowRenderer, potentialArea.getBBox().getWidth());
                                LayoutResult res = overflowRenderer.layout(new LayoutContext(potentialArea, wasHeightClipped || wasParentsHeightClipped));
                                bordersHandler.setStartRow(savedStartRow);
                                if (LayoutResult.FULL == res.getStatus()) {
                                    if (taggingHelper != null) {
                                        // marking as artifact to get rid of all tagging hints from this renderer
                                        taggingHelper.markArtifactHint(footerRenderer);
                                    }
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
                                    if (null != headerRenderer) {
                                        bordersHandler.collapseTableWithHeader(headerRenderer.bordersHandler, false);
                                    }
                                    bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, false);
                                    bordersHandler.tableBoundingBorders[2] = Border.NO_BORDER;
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
                if (null != footerRenderer && tableModel.isComplete() && tableModel.isSkipLastFooter() && !split
                        && !Boolean.TRUE.equals(this.<Boolean>getOwnProperty(Property.FORCED_PLACEMENT))) {
                    LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
                    if (taggingHelper != null) {
                        // marking as artifact to get rid of all tagging hints from this renderer
                        taggingHelper.markArtifactHint(footerRenderer);
                    }
                    footerRenderer = null;
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
                if (!hasContent && childRenderers.size() == 0) {
                    bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox, true);
                } else {
                    bordersHandler.applyBottomTableBorder(occupiedArea.getBBox(), layoutBox, tableModel.isEmpty(), false, true);
                }
                if (!(bordersHandler instanceof SeparatedTableBorders)) {
                    layoutBox.moveDown(footerRenderer.occupiedArea.getBBox().getHeight()).increaseHeight(footerRenderer.occupiedArea.getBBox().getHeight());
                    // apply the difference to set footer and table left/right margins identical
                    bordersHandler.applyLeftAndRightTableBorder(layoutBox, true);
                    prepareFooterOrHeaderRendererForLayout(footerRenderer, layoutBox.getWidth());

                    bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, hasContent || 0 != childRenderers.size());
                    if (bordersHandler instanceof CollapsedTableBorders) {
                        footerRenderer.setBorders(CollapsedTableBorders.getCollapsedBorder(footerRenderer.getBorders()[2], getBorders()[2]), 2);
                    }
                    footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox), wasHeightClipped || wasParentsHeightClipped));
                    bordersHandler.applyLeftAndRightTableBorder(layoutBox, false);
                    float footerHeight = footerRenderer.getOccupiedAreaBBox().getHeight();
                    footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
                    layoutBox.setY(footerRenderer.occupiedArea.getBBox().getTop()).setHeight(occupiedArea.getBBox().getBottom() - layoutBox.getBottom());
                }
            }
            if (!split) {
                childRenderers.addAll(currChildRenderers);
                currChildRenderers.clear();
            }
            if (split && footerRenderer != null) {
                LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
                if (taggingHelper != null) {
                    taggingHelper.markArtifactHint(footerRenderer);
                }
            }
            if (split) {
                if (marginsCollapsingEnabled) {
                    marginsCollapseHandler.endMarginsCollapse(layoutBox);
                }
                TableRenderer[] splitResult = split(row, hasContent, cellWithBigRowspanAdded);
                OverflowRowsWrapper overflowRows = new OverflowRowsWrapper(splitResult[1]);
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
                                rowspans[col] = ((Cell) cellSplit.getModelElement()).getRowspan();
                            }
                            if (splits[col].getStatus() != LayoutResult.NOTHING && (hasContent || cellWithBigRowspanAdded)) {
                                childRenderers.add(cellSplit);
                            }
                            LayoutArea cellOccupiedArea = currentRow[col].getOccupiedArea();
                            if (hasContent || cellWithBigRowspanAdded || splits[col].getStatus() == LayoutResult.NOTHING) {
                                CellRenderer cellOverflow = (CellRenderer) splits[col].getOverflowRenderer();
                                CellRenderer originalCell = currentRow[col];
                                currentRow[col] = null;
                                rows.get(targetOverflowRowIndex[col])[col] = originalCell;
                                overflowRows.setCell(0, col, null);
                                overflowRows.setCell(targetOverflowRowIndex[col] - row, col, (CellRenderer) cellOverflow.setParent(splitResult[1]));
                            } else {
                                overflowRows.setCell(targetOverflowRowIndex[col] - row, col, (CellRenderer) currentRow[col].setParent(splitResult[1]));
                            }
                            overflowRows.getCell(targetOverflowRowIndex[col] - row, col).occupiedArea = cellOccupiedArea;
                        } else if (currentRow[col] != null) {
                            if (hasContent) {
                                rowspans[col] = ((Cell) currentRow[col].getModelElement()).getRowspan();
                            }
                            boolean isBigRowspannedCell = 1 != ((Cell) currentRow[col].getModelElement()).getRowspan();
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
                                CellRenderer overflowCell = (CellRenderer) ((Cell) currentRow[col].getModelElement()).clone(true).getRenderer(); // we will change properties
                                overflowCell.setParent(this);
                                overflowCell.deleteProperty(Property.HEIGHT);
                                overflowCell.deleteProperty(Property.MIN_HEIGHT);
                                overflowCell.deleteProperty(Property.MAX_HEIGHT);
                                overflowRows.setCell(0, col, null);
                                overflowRows.setCell(targetOverflowRowIndex[col] - row, col, overflowCell);
                                childRenderers.add(currentRow[col]);
                                CellRenderer originalCell = currentRow[col];
                                currentRow[col] = null;
                                rows.get(targetOverflowRowIndex[col])[col] = originalCell;
                                originalCell.isLastRendererForModelElement = false;
                                overflowCell.setProperty(Property.TAGGING_HINT_KEY, originalCell.<Object>getProperty(Property.TAGGING_HINT_KEY));
                            } else {
                                childRenderers.add(currentRow[col]);
                                // shift all cells in the column up
                                int i = row;
                                for (; i < row + minRowspan && i + 1 < rows.size() && splitResult[1].rows.get(i + 1 - row)[col] != null; i++) {
                                    overflowRows.setCell(i - row, col, splitResult[1].rows.get(i + 1 - row)[col]);
                                    overflowRows.setCell(i + 1 - row, col, null);
                                    rows.get(i)[col] = rows.get(i + 1)[col];
                                    rows.get(i + 1)[col] = null;
                                }
                                // the number of cells behind is less then minRowspan-1
                                // so we should process the last cell in the column as in the case 1 == minRowspan
                                if (i != row + minRowspan - 1 && null != rows.get(i)[col]) {
                                    CellRenderer overflowCell = (CellRenderer) ((Cell) rows.get(i)[col].getModelElement()).getRenderer().setParent(this);
                                    overflowRows.setCell(i - row, col, null);
                                    overflowRows.setCell(targetOverflowRowIndex[col] - row, col, overflowCell);
                                    CellRenderer originalCell = rows.get(i)[col];
                                    rows.get(i)[col] = null;
                                    rows.get(targetOverflowRowIndex[col])[col] = originalCell;
                                    originalCell.isLastRendererForModelElement = false;
                                    overflowCell.setProperty(Property.TAGGING_HINT_KEY, originalCell.<Object>getProperty(Property.TAGGING_HINT_KEY));
                                }
                            }
                            overflowRows.getCell(targetOverflowRowIndex[col] - row, col).occupiedArea = cellOccupiedArea;
                        }
                    }
                }

                applySpacing(layoutBox, horizontalBorderSpacing, verticalBorderSpacing, true);
                applySingleSpacing(occupiedArea.getBBox(), horizontalBorderSpacing, true, true);
                if (null != footerRenderer) {
                    layoutBox.moveUp(verticalBorderSpacing).decreaseHeight(verticalBorderSpacing);
                }
                if (null != headerRenderer || !tableModel.isEmpty()) {
                    layoutBox.decreaseHeight(verticalBorderSpacing);
                }
                if (0 == row && !hasContent && null == headerRenderer) {
                    occupiedArea.getBBox().moveUp((float) verticalBorderSpacing / 2);
                } else {
                    applySingleSpacing(occupiedArea.getBBox(), verticalBorderSpacing, false, true);
                }
                // if only footer should be processed
                if (!isAndWasComplete && null != footerRenderer && 0 == splitResult[0].rows.size()) {
                    layoutBox.increaseHeight(verticalBorderSpacing);
                }
                // Apply borders if there is no footer
                if (null == footerRenderer) {
                    if (0 != this.childRenderers.size()) {
                        bordersHandler.applyBottomTableBorder(occupiedArea.getBBox(), layoutBox, false);
                    } else {
                        bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox, true);
                        // process bottom border of the last added row if there is no footer
                        if (!isAndWasComplete && !isFirstOnThePage) {
                            bordersHandler.applyTopTableBorder(occupiedArea.getBBox(), layoutBox, 0 == childRenderers.size(), true, false);
                        }
                    }
                }
                if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA))
                        || Boolean.TRUE.equals(getPropertyAsBoolean(Property.FILL_AVAILABLE_AREA_ON_SPLIT))) {
                    extendLastRow(splitResult[1].rows.get(0), layoutBox);
                }
                adjustFooterAndFixOccupiedArea(layoutBox, 0 != heights.size() ? verticalBorderSpacing : 0);
                adjustCaptionAndFixOccupiedArea(layoutBox, 0 != heights.size() ? verticalBorderSpacing : 0);

                // On the next page we need to process rows without any changes except moves connected to actual cell splitting
                for (Map.Entry<Integer, Integer> entry : rowMoves.entrySet()) {
                    // Move the cell back to its row if there was no actual split
                    if (null == splitResult[1].rows.get((int) entry.getValue() - splitResult[0].rows.size())[entry.getKey()]) {
                        CellRenderer originalCellRenderer = rows.get(row)[entry.getKey()];
                        CellRenderer overflowCellRenderer = splitResult[1].rows.get(row - splitResult[0].rows.size())[entry.getKey()];
                        rows.get((int) entry.getValue())[entry.getKey()] = originalCellRenderer;
                        rows.get(row)[entry.getKey()] = null;
                        overflowRows.setCell((int) entry.getValue() - splitResult[0].rows.size(), entry.getKey(), overflowCellRenderer);
                        overflowRows.setCell(row - splitResult[0].rows.size(), entry.getKey(), null);
                    }
                }

                if ((isKeepTogether() && 0 == lastFlushedRowBottomBorder.size()) && !Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                    return new LayoutResult(LayoutResult.NOTHING, null, null, this, null == firstCauseOfNothing ? this : firstCauseOfNothing);
                } else {
                    int status = ((occupiedArea.getBBox().getHeight()
                            - (null == footerRenderer ? 0 : footerRenderer.getOccupiedArea().getBBox().getHeight())
                            - (null == headerRenderer ? 0 : headerRenderer.getOccupiedArea().getBBox().getHeight() - headerRenderer.bordersHandler.getMaxBottomWidth())
                            == 0)
                            && (isAndWasComplete || isFirstOnThePage))
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
                        applyFixedXOrYPosition(false, layoutBox);
                        applyPaddings(occupiedArea.getBBox(), true);
                        applyMargins(occupiedArea.getBBox(), true);

                        LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, siblingFloatRendererAreas, layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                        return new LayoutResult(LayoutResult.FULL, editedArea, splitResult[0], null);
                    } else {
                        updateHeightsOnSplit(false, splitResult[0], splitResult[1]);
                        applyFixedXOrYPosition(false, layoutBox);
                        applyPaddings(occupiedArea.getBBox(), true);
                        applyMargins(occupiedArea.getBBox(), true);

                        LayoutArea editedArea = null;
                        if (status != LayoutResult.NOTHING) {
                            editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, siblingFloatRendererAreas, layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);
                        }
                        return new LayoutResult(status, editedArea, splitResult[0], splitResult[1], null == firstCauseOfNothing ? this : firstCauseOfNothing);
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
        if (!(bordersHandler instanceof SeparatedTableBorders) && tableModel.isComplete() && (0 != lastFlushedRowBottomBorder.size() || tableModel.isEmpty()) && null != footerRenderer) {
            layoutBox.moveDown(footerRenderer.occupiedArea.getBBox().getHeight()).increaseHeight(footerRenderer.occupiedArea.getBBox().getHeight());
            // apply the difference to set footer and table left/right margins identical
            bordersHandler.applyLeftAndRightTableBorder(layoutBox, true);
            prepareFooterOrHeaderRendererForLayout(footerRenderer, layoutBox.getWidth());
            if (0 != rows.size() || !isAndWasComplete) {
                bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, true);
            } else if (null != headerRenderer) {
                headerRenderer.bordersHandler.collapseTableWithFooter(footerRenderer.bordersHandler, true);
            }

            footerRenderer.layout(new LayoutContext(new LayoutArea(area.getPageNumber(), layoutBox), wasHeightClipped || wasParentsHeightClipped));
            bordersHandler.applyLeftAndRightTableBorder(layoutBox, false);

            float footerHeight = footerRenderer.getOccupiedAreaBBox().getHeight();
            footerRenderer.move(0, -(layoutBox.getHeight() - footerHeight));
            layoutBox.moveUp(footerHeight).decreaseHeight(footerHeight);
        }

        applySpacing(layoutBox, horizontalBorderSpacing, verticalBorderSpacing, true);
        applySingleSpacing(occupiedArea.getBBox(), horizontalBorderSpacing, true, true);
        if (null != footerRenderer) {
            layoutBox.moveUp(verticalBorderSpacing).decreaseHeight(verticalBorderSpacing);
        }
        if (null != headerRenderer || !tableModel.isEmpty()) {
            layoutBox.decreaseHeight(verticalBorderSpacing);
        }
        if (tableModel.isEmpty() && null == headerRenderer) {
            occupiedArea.getBBox().moveUp((float) verticalBorderSpacing / 2);
        } else if (isAndWasComplete || 0 != rows.size()) {
            applySingleSpacing(occupiedArea.getBBox(), verticalBorderSpacing, false, true);//
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

        applyFixedXOrYPosition(false, layoutBox);

        if (marginsCollapsingEnabled) {
            marginsCollapseHandler.endMarginsCollapse(layoutBox);
        }

        applyPaddings(occupiedArea.getBBox(), true);
        applyMargins(occupiedArea.getBBox(), true);

        // we should process incomplete table's footer only during splitting
        if (!tableModel.isComplete() && null != footerRenderer) {
            LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper != null) {
                // marking as artifact to get rid of all tagging hints from this renderer
                taggingHelper.markArtifactHint(footerRenderer);
            }
            footerRenderer = null;
            bordersHandler.skipFooter(bordersHandler.tableBoundingBorders);
        }
        adjustFooterAndFixOccupiedArea(layoutBox, null != headerRenderer || !tableModel.isEmpty() ? verticalBorderSpacing : 0);
        adjustCaptionAndFixOccupiedArea(layoutBox, null != headerRenderer || !tableModel.isEmpty() ? verticalBorderSpacing : 0);

        FloatingHelper.removeFloatsAboveRendererBottom(siblingFloatRendererAreas, this);

        if (!isAndWasComplete && !isFirstOnThePage && (0 != rows.size() || (null != footerRenderer && tableModel.isComplete()))) {
            occupiedArea.getBBox().decreaseHeight(verticalBorderSpacing);
        }

        LayoutArea editedArea = FloatingHelper.adjustResultOccupiedAreaForFloatAndClear(this, siblingFloatRendererAreas, layoutContext.getArea().getBBox(), clearHeightCorrection, marginsCollapsingEnabled);

        return new LayoutResult(LayoutResult.FULL, editedArea, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(DrawContext drawContext) {
        boolean isTagged = drawContext.isTaggingEnabled();
        LayoutTaggingHelper taggingHelper = null;
        if (isTagged) {
            taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
            if (taggingHelper == null) {
                isTagged = false;
            } else {
                TagTreePointer tagPointer = taggingHelper.useAutoTaggingPointerAndRememberItsPosition(this);
                if (taggingHelper.createTag(this, tagPointer)) {
                    tagPointer.getProperties().addAttributes(0, AccessibleAttributesApplier.getLayoutAttributes(this, tagPointer));
                }
            }
        }

        beginTransformationIfApplied(drawContext.getCanvas());

        applyDestinationsAndAnnotation(drawContext);

        boolean relativePosition = isRelativePosition();
        if (relativePosition) {
            applyRelativePositioningTranslation(false);
        }

        beginElementOpacityApplying(drawContext);
        float captionHeight = null != captionRenderer ? captionRenderer.getOccupiedArea().getBBox().getHeight() : 0;
        boolean isBottomCaption = CaptionSide.BOTTOM.equals(0 != captionHeight ? captionRenderer.<CaptionSide>getProperty(Property.CAPTION_SIDE) : null);
        if (0 != captionHeight) {
            occupiedArea.getBBox().applyMargins(isBottomCaption ? 0 : captionHeight, 0, isBottomCaption ? captionHeight : 0 , 0, false);
        }
        drawBackground(drawContext);
        if (bordersHandler instanceof SeparatedTableBorders && !isHeaderRenderer() && !isFooterRenderer()) {
            drawBorder(drawContext);
        }
        drawChildren(drawContext);
        drawPositionedChildren(drawContext);
        if (0 != captionHeight) {
            occupiedArea.getBBox().applyMargins(isBottomCaption ? 0 : captionHeight, 0, isBottomCaption ? captionHeight : 0 , 0, true);
        }
        drawCaption(drawContext);
        endElementOpacityApplying(drawContext);

        if (relativePosition) {
            applyRelativePositioningTranslation(true);
        }

        flushed = true;

        endTransformationIfApplied(drawContext.getCanvas());

        if (isTagged) {
            if (isLastRendererForModelElement && ((Table) getModelElement()).isComplete()) {
                taggingHelper.finishTaggingHint(this);
            }
            taggingHelper.restoreAutoTaggingPointerPosition(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawChildren(DrawContext drawContext) {
        if (headerRenderer != null) {
            headerRenderer.draw(drawContext);
        }

        for (IRenderer child : childRenderers) {
            child.draw(drawContext);
        }

        if (bordersHandler instanceof CollapsedTableBorders) {
            drawBorders(drawContext);
        }

        if (footerRenderer != null) {
            footerRenderer.draw(drawContext);
        }
    }

    protected void drawBackgrounds(DrawContext drawContext) {
        boolean shrinkBackgroundArea = bordersHandler instanceof CollapsedTableBorders && (isHeaderRenderer() || isFooterRenderer());
        if (shrinkBackgroundArea) {
            occupiedArea.getBBox().applyMargins(bordersHandler.getMaxTopWidth() / 2, bordersHandler.getRightBorderMaxWidth() / 2,
                    bordersHandler.getMaxBottomWidth() / 2, bordersHandler.getLeftBorderMaxWidth() / 2, false);
        }
        super.drawBackground(drawContext);
        if (shrinkBackgroundArea) {
            occupiedArea.getBBox().applyMargins(bordersHandler.getMaxTopWidth() / 2, bordersHandler.getRightBorderMaxWidth() / 2,
                    bordersHandler.getMaxBottomWidth() / 2, bordersHandler.getLeftBorderMaxWidth() / 2, true);
        }
        if (null != headerRenderer) {
            headerRenderer.drawBackgrounds(drawContext);
        }
        if (null != footerRenderer) {
            footerRenderer.drawBackgrounds(drawContext);
        }
    }


    protected void drawCaption(DrawContext drawContext) {
        if (null != captionRenderer && !isFooterRenderer() && !isHeaderRenderer()) {
            captionRenderer.draw(drawContext);
        }
    }

    @Override
    public void drawBackground(DrawContext drawContext) {
        // draw background once for body/header/footer
        if (!isFooterRenderer() && !isHeaderRenderer()) {
            drawBackgrounds(drawContext);
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

    protected TableRenderer[] split(int row) {
        return split(row, false);
    }

    protected TableRenderer[] split(int row, boolean hasContent) {
        return split(row, hasContent, false);
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
            overflowRenderer.isOriginalNonSplitRenderer = isOriginalNonSplitRenderer;
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
        splitRenderer.captionRenderer = captionRenderer;

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
    public MinMaxWidth getMinMaxWidth() {
        initializeTableLayoutBorders();
        float rightMaxBorder = bordersHandler.getRightBorderMaxWidth();
        float leftMaxBorder = bordersHandler.getLeftBorderMaxWidth();
        TableWidths tableWidths = new TableWidths(this, MinMaxWidthUtils.getInfWidth(), true, rightMaxBorder, leftMaxBorder);
        float[] columns = tableWidths.layout();
        float minWidth = tableWidths.getMinWidth();
        cleanTableLayoutBorders();

        float maxColTotalWidth = 0;
        for (float column : columns) {
            maxColTotalWidth += column;
        }
        UnitValue marginRightUV = this.getPropertyAsUnitValue(Property.MARGIN_RIGHT);
        if (!marginRightUV.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_RIGHT));
        }
        UnitValue marginLefttUV = this.getPropertyAsUnitValue(Property.MARGIN_LEFT);
        if (!marginLefttUV.isPointValue()) {
            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
            logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_LEFT));
        }
        float additionalWidth = marginLefttUV.getValue() + marginRightUV.getValue() + rightMaxBorder / 2 + leftMaxBorder / 2;
        return new MinMaxWidth(minWidth, maxColTotalWidth, additionalWidth);
    }

    /**
     * @deprecated Will be removed in next major release (iText 7.2).
     * The aim of this method overriding here is achieved by overriding {@link #allowLastYLineRecursiveExtraction} method.
     */
    @Override
    @Deprecated
    protected Float getLastYLineRecursively() {
        return null;
    }

    @Override
    protected boolean allowLastYLineRecursiveExtraction() {
        return false;
    }

    private void initializeTableLayoutBorders() {
        boolean isSeparated = BorderCollapsePropertyValue.SEPARATE.equals(this.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE));
        bordersHandler = isSeparated
                ? (TableBorders) new SeparatedTableBorders(rows, ((Table) getModelElement()).getNumberOfColumns(), getBorders())
                : (TableBorders) new CollapsedTableBorders(rows, ((Table) getModelElement()).getNumberOfColumns(), getBorders());
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
        if (bordersHandler instanceof SeparatedTableBorders) {
            super.drawBorder(drawContext);
        } else {
            // Do nothing here. Itext7 handles cell and table borders collapse and draws result borders during #drawBorders()
        }
    }

    protected void drawBorders(DrawContext drawContext) {
        drawBorders(drawContext, null != headerRenderer, null != footerRenderer);
    }

    private void drawBorders(DrawContext drawContext, boolean hasHeader, boolean hasFooter) {
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
            UnitValue topMargin = this.getPropertyAsUnitValue(Property.MARGIN_TOP);
            if (null != topMargin && !topMargin.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_LEFT));
            }
            startY -= null == topMargin ? 0 : topMargin.getValue();
        }
        if (hasProperty(Property.MARGIN_LEFT)) {
            UnitValue leftMargin = this.getPropertyAsUnitValue(Property.MARGIN_LEFT);
            if (null != leftMargin && !leftMargin.isPointValue()) {
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.MARGIN_LEFT));
            }
            startX += +(null == leftMargin ? 0 : leftMargin.getValue());
        }


        // process halves of the borders here
        if (childRenderers.size() == 0) {
            Border[] borders = bordersHandler.tableBoundingBorders;
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

        boolean isTagged = drawContext.isTaggingEnabled();
        if (isTagged) {
            drawContext.getCanvas().openTag(new CanvasArtifact());
        }

        // considering these values itext will draw table borders correctly
        boolean isTopTablePart = isTopTablePart();
        boolean isBottomTablePart = isBottomTablePart();
        boolean isComplete = getTable().isComplete();
        boolean isFooterRendererOfLargeTable = isFooterRendererOfLargeTable();

        bordersHandler.setRowRange(rowRange.getStartRow(), rowRange.getStartRow() + heights.size() - 1);

        if (bordersHandler instanceof CollapsedTableBorders) {
            if (hasFooter) {
                ((CollapsedTableBorders) bordersHandler).setBottomBorderCollapseWith(footerRenderer.bordersHandler.getFirstHorizontalBorder());
            } else if (isBottomTablePart) {
                ((CollapsedTableBorders) bordersHandler).setBottomBorderCollapseWith(null);
            }
        }
        // we do not need to fix top border, because either this is header or the top border has been already written
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

    private void applyFixedXOrYPosition(boolean isXPosition, Rectangle layoutBox) {
        if (isPositioned()) {
            if (isFixedLayout()) {
                if (isXPosition) {
                    float x = (float) this.getPropertyAsFloat(Property.LEFT);
                    layoutBox.setX(x);
                } else {
                    float y = (float) this.getPropertyAsFloat(Property.BOTTOM);
                    move(0, y - occupiedArea.getBBox().getY());
                }
            }
        }
    }

    /**
     * If there is some space left, we will move the footer up, because initially the footer is at the very bottom of the area.
     * We also will adjust the occupied area by the footer's size if it is present.
     *
     * @param layoutBox the layout box which represents the area which is left free.
     */
    private void adjustFooterAndFixOccupiedArea(Rectangle layoutBox, float verticalBorderSpacing) {
        if (footerRenderer != null) {
            footerRenderer.move(0, layoutBox.getHeight() + verticalBorderSpacing);
            float footerHeight = footerRenderer.getOccupiedArea().getBBox().getHeight() - verticalBorderSpacing;
            occupiedArea.getBBox().moveDown(footerHeight).increaseHeight(footerHeight);
        }
    }

    /**
     * If there is some space left, we will move the caption up, because initially the caption is at the very bottom of the area.
     * We also will adjust the occupied area by the caption's size if it is present.
     *
     * @param layoutBox the layout box which represents the area which is left free.
     */
    private void adjustCaptionAndFixOccupiedArea(Rectangle layoutBox, float verticalBorderSpacing) {
        if (captionRenderer != null) {
            float captionHeight = captionRenderer.getOccupiedArea().getBBox().getHeight();
            occupiedArea.getBBox().moveDown(captionHeight).increaseHeight(captionHeight);
            if (CaptionSide.BOTTOM.equals(captionRenderer.<CaptionSide>getProperty(Property.CAPTION_SIDE))) {
                captionRenderer.move(0, layoutBox.getHeight() + verticalBorderSpacing);
            } else {
                occupiedArea.getBBox().moveUp(captionHeight);
            }
        }
    }


    private void correctLayoutedCellsOccupiedAreas(LayoutResult[] splits, int row, int[] targetOverflowRowIndex,
                                                   Float blockMinHeight, Rectangle layoutBox,
                                                   List<Boolean> rowsHasCellWithSetHeight, boolean isLastRenderer,
                                                   boolean processBigRowspan, boolean skip) {
        // correct last height
        int finish = bordersHandler.getFinishRow();
        bordersHandler.setFinishRow(rowRange.getFinishRow());
        Border currentBorder = bordersHandler.getWidestHorizontalBorder(finish + 1); // TODO Correct for collapsed borders only
        bordersHandler.setFinishRow(finish);
        if (skip) {
            // Update bordersHandler
            bordersHandler.tableBoundingBorders[2] = getBorders()[2];
            bordersHandler.skipFooter(bordersHandler.tableBoundingBorders);
        }
        float currentBottomIndent = bordersHandler instanceof CollapsedTableBorders
                ? null == currentBorder ? 0 : currentBorder.getWidth()
                : 0;
        float realBottomIndent = bordersHandler instanceof CollapsedTableBorders
                ? bordersHandler.getMaxBottomWidth()
                : 0;
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
                    float[] indents = bordersHandler.getCellBorderIndents(bordersHandler instanceof SeparatedTableBorders ? row : targetOverflowRowIndex[col], col, rowspan, colspan);
                    for (int l = heights.size() - 1 - 1; l > targetOverflowRowIndex[col] - rowspan && l >= 0; l--) {
                        height += (float) heights.get(l);
                    }
                    float cellHeightInLastRow;
                    if (bordersHandler instanceof SeparatedTableBorders) {
                        cellHeightInLastRow = cell.getOccupiedArea().getBBox().getHeight() - height;
                    } else {
                        cellHeightInLastRow = cell.getOccupiedArea().getBBox().getHeight() + indents[0] / 2 + indents[2] / 2 - height;
                    }
                    if (heights.get(heights.size() - 1) < cellHeightInLastRow) {
                        if (bordersHandler instanceof SeparatedTableBorders) {
                            float differenceToConsider = cellHeightInLastRow - heights.get(heights.size() - 1);
                            occupiedArea.getBBox().moveDown(differenceToConsider);
                            occupiedArea.getBBox().increaseHeight(differenceToConsider);
                        }
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
            float[] indents = bordersHandler.getCellBorderIndents(currentRowIndex < row || bordersHandler instanceof SeparatedTableBorders ? currentRowIndex : targetOverflowRowIndex[col], col, rowspan, colspan);
            // process rowspan
            for (int l = (currentRowIndex < row ? currentRowIndex : heights.size() - 1) - 1; l > (currentRowIndex < row ? currentRowIndex : targetOverflowRowIndex[col]) - rowspan && l >= 0; l--) {
                height += (float) heights.get(l);
                if (Boolean.FALSE.equals(rowsHasCellWithSetHeight.get(l))) {
                    rowspanOffset += additionalCellHeight;
                }
            }
            height += (float) heights.get(currentRowIndex < row ? currentRowIndex : heights.size() - 1);
            if (!(bordersHandler instanceof SeparatedTableBorders)) {
                height -= indents[0] / 2 + indents[2] / 2;
            }
            // Correcting cell bbox only. We don't need #move() here.
            // This is because of BlockRenderer's specificity regarding occupied area.
            float shift = height - cell.getOccupiedArea().getBBox().getHeight();
            Rectangle bBox = cell.getOccupiedArea().getBBox();
            bBox.moveDown(shift);
            try {
                cell.move(0, -(cumulativeShift - rowspanOffset));
            } catch (NullPointerException e) {  // TODO Remove try-catch when DEVSIX-1001 is resolved.
                Logger logger = LoggerFactory.getLogger(TableRenderer.class);
                logger.error(MessageFormatUtil.format(LogMessageConstant.OCCUPIED_AREA_HAS_NOT_BEEN_INITIALIZED, "Some of the cell's content might not end up placed correctly."));
            }
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
        boolean isSeparated = BorderCollapsePropertyValue.SEPARATE.equals(this.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE));
        Table footerOrHeader = footer ? table.getFooter() : table.getHeader();
        int innerBorder = footer ? 0 : 2;
        int outerBorder = footer ? 2 : 0;
        TableRenderer renderer = (TableRenderer) footerOrHeader.createRendererSubTree().setParent(this);
        ensureFooterOrHeaderHasTheSamePropertiesAsParentTableRenderer(renderer);
        boolean firstHeader = !footer && rowRange.getStartRow() == 0 && isOriginalNonSplitRenderer;
        LayoutTaggingHelper taggingHelper = this.<LayoutTaggingHelper>getProperty(Property.TAGGING_HELPER);
        if (taggingHelper != null) {
            taggingHelper.addKidsHint(this, Collections.<IRenderer>singletonList(renderer));
            LayoutTaggingHelper.addTreeHints(taggingHelper, renderer);
            if (!footer && !firstHeader) { // whether footer is not the last and requires marking as artifact is defined later during table renderer layout
                taggingHelper.markArtifactHint(renderer);
            }
        }

        if (bordersHandler instanceof SeparatedTableBorders) {
            if (table.isEmpty()) {
                // A footer and a header share the same inner border. However it should be processed only ones.
                if (!footer || null == headerRenderer) {
                    renderer.setBorders(tableBorders[innerBorder], innerBorder);
                }
                bordersHandler.tableBoundingBorders[innerBorder] = Border.NO_BORDER;
            }
            renderer.setBorders(tableBorders[1], 1);
            renderer.setBorders(tableBorders[3], 3);
            renderer.setBorders(tableBorders[outerBorder], outerBorder);
            bordersHandler.tableBoundingBorders[outerBorder] = Border.NO_BORDER;
        } else if (bordersHandler instanceof CollapsedTableBorders) {
            Border[] borders = renderer.getBorders();
            if (table.isEmpty()) {
                renderer.setBorders(CollapsedTableBorders.getCollapsedBorder(borders[innerBorder], tableBorders[innerBorder]), innerBorder);
                bordersHandler.tableBoundingBorders[innerBorder] = Border.NO_BORDER;
            }
            renderer.setBorders(CollapsedTableBorders.getCollapsedBorder(borders[1], tableBorders[1]), 1);
            renderer.setBorders(CollapsedTableBorders.getCollapsedBorder(borders[3], tableBorders[3]), 3);
            renderer.setBorders(CollapsedTableBorders.getCollapsedBorder(borders[outerBorder], tableBorders[outerBorder]), outerBorder);
            bordersHandler.tableBoundingBorders[outerBorder] = Border.NO_BORDER;
        }

        renderer.bordersHandler = isSeparated
                ? (TableBorders) new SeparatedTableBorders(renderer.rows, ((Table) renderer.getModelElement()).getNumberOfColumns(), renderer.getBorders())
                : (TableBorders) new CollapsedTableBorders(renderer.rows, ((Table) renderer.getModelElement()).getNumberOfColumns(), renderer.getBorders());
        renderer.bordersHandler.initializeBorders();
        renderer.bordersHandler.setRowRange(renderer.rowRange.getStartRow(), renderer.rowRange.getFinishRow());
        renderer.bordersHandler.processAllBordersAndEmptyRows();
        renderer.correctRowRange();
        return renderer;
    }

    private void ensureFooterOrHeaderHasTheSamePropertiesAsParentTableRenderer(TableRenderer headerOrFooterRenderer) {
        headerOrFooterRenderer.setProperty(Property.BORDER_COLLAPSE, this.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE));
        if (bordersHandler instanceof SeparatedTableBorders) {
            headerOrFooterRenderer.setProperty(Property.HORIZONTAL_BORDER_SPACING, this.getPropertyAsFloat(Property.HORIZONTAL_BORDER_SPACING));
            headerOrFooterRenderer.setProperty(Property.VERTICAL_BORDER_SPACING, this.getPropertyAsFloat(Property.VERTICAL_BORDER_SPACING));
            headerOrFooterRenderer.setProperty(Property.BORDER, Border.NO_BORDER);
            headerOrFooterRenderer.setProperty(Property.BORDER_LEFT, Border.NO_BORDER);
            headerOrFooterRenderer.setProperty(Property.BORDER_TOP, Border.NO_BORDER);
            headerOrFooterRenderer.setProperty(Property.BORDER_RIGHT, Border.NO_BORDER);
            headerOrFooterRenderer.setProperty(Property.BORDER_BOTTOM, Border.NO_BORDER);
        }
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
    private void calculateColumnWidths(float availableWidth) {
        if (countedColumnWidth == null || totalWidthForColumns != availableWidth) {
            TableWidths tableWidths = new TableWidths(this, availableWidth, false, bordersHandler.rightBorderMaxWidth, bordersHandler.leftBorderMaxWidth);
            countedColumnWidth = tableWidths.layout();
        }
    }

    private float getTableWidth() {
        float sum = 0;
        for (float column : countedColumnWidth) {
            sum += column;
        }
        if (bordersHandler instanceof SeparatedTableBorders) {
            sum += bordersHandler.getRightBorderMaxWidth() + bordersHandler.getLeftBorderMaxWidth();
            Float horizontalSpacing = this.getPropertyAsFloat(Property.HORIZONTAL_BORDER_SPACING);
            sum += (null == horizontalSpacing) ? 0 : (float) horizontalSpacing;
        } else {
            sum += bordersHandler.getRightBorderMaxWidth() / 2 + bordersHandler.getLeftBorderMaxWidth() / 2;
        }
        return sum;
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

    /**
     * Utility class that copies overflow renderer rows on cell replacement so it won't affect original renderer
     */
    private static class OverflowRowsWrapper {
        private TableRenderer overflowRenderer;
        private HashMap<Integer, Boolean> isRowReplaced = new HashMap<>();
        private boolean isReplaced = false;

        public OverflowRowsWrapper(TableRenderer overflowRenderer) {
            this.overflowRenderer = overflowRenderer;
        }

        public CellRenderer getCell(int row, int col) {
            return overflowRenderer.rows.get(row)[col];
        }

        public CellRenderer setCell(int row, int col, CellRenderer newCell) {
            if (!isReplaced) {
                overflowRenderer.rows = new ArrayList<>(overflowRenderer.rows);
                isReplaced = true;
            }
            if (!Boolean.TRUE.equals(isRowReplaced.get(row))) {
                overflowRenderer.rows.set(row, (CellRenderer[]) overflowRenderer.rows.get(row).clone());
            }
            return overflowRenderer.rows.get(row)[col] = newCell;
        }
    }
}
