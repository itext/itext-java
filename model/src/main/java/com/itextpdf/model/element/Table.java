package com.itextpdf.model.element;

import com.itextpdf.model.Document;
import com.itextpdf.model.Property;
import com.itextpdf.model.border.Border;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.TableRenderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Table extends BlockElement<Table> implements ILargeElement<Table> {

    private ArrayList<Cell[]> rows;

    private float[] columnWidths;
    private int currentColumn = 0;
    private int currentRow = -1;
    private Table header;
    private Table footer;
    private boolean skipFirstHeader;
    private boolean skipLastFooter;
    private boolean isComplete;
    private List<RowRange> lastAddedRowGroups;
    // Start number of the row "window" (range) that this table currently contain.
    // For large tables we might contain only a few rows, not all of them, other ones might have been flushed.
    private int rowWindowStart = 0;
    private Document document;
    private Cell[] lastAddedRow;

    /**
     * Constructs a {@code Table} with the relative column widths.
     *
     * @param columnWidths the relative column widths
     */
    public Table(float[] columnWidths, boolean largeTable) {
        this.isComplete = !largeTable;
        if (columnWidths == null) {
            throw new NullPointerException("the.widths.array.in.table.constructor.can.not.be.null");
        }
        if (columnWidths.length == 0) {
            throw new IllegalArgumentException("the.widths.array.in.pdfptable.constructor.can.not.have.zero.length");
        }
        this.columnWidths = new float[columnWidths.length];
        float width = 0;
        for (int i = 0; i < columnWidths.length; i++) {
            this.columnWidths[i] = columnWidths[i];
            width += columnWidths[i];
        }
        super.setWidth(width);
        initializeRows();
    }

    public Table(float[] columnWidths) {
        this(columnWidths, false);
    }

    /**
     * Constructs a {@code Table} with {@code numColumns} columns.
     *
     * @param numColumns the number of columns
     */
    public Table(int numColumns, boolean largeTable) {
        this.isComplete = !largeTable;
        if (numColumns <= 0) {
            throw new IllegalArgumentException("the.number.of.columns.in.pdfptable.constructor.must.be.greater.than.zero");
        }
        this.columnWidths = new float[numColumns];
        for (int k = 0; k < numColumns; ++k) {
            this.columnWidths[k] = 1;
        }
        super.setWidth(Property.UnitValue.createPercentValue(100));
        initializeRows();
    }

    public Table(int numColumns) {
        this(numColumns, false);
    }

    /**
     * Sets the full width of the table.
     *
     * @param width the full width of the table.
     */
    @Override
    public Table setWidth(Property.UnitValue width) {
        Property.UnitValue currWidth = getWidth();
        if (!width.equals(currWidth)) {
            super.setWidth(width);
            calculateWidths();
        }
        return this;
    }

    public float getColumnWidth(int column) {
        return columnWidths[column];
    }

    /**
     * Returns the number of columns.
     *
     * @return the number of columns.
     */
    public int getNumberOfColumns() {
        return columnWidths.length;
    }

    /**
     * Returns the number of rows.
     *
     * @return the number of rows.
     */
    public int getNumberOfRows() {
        return rows.size();
    }

    /**
     * Adds a new cell to the header of the table.
     * The header will be displayed in the top of every area of this table.
     * See also {@link #setSkipFirstHeader(boolean)}.
     * @param headerCell a header cell to be added
     */
    public void addHeaderCell(Cell headerCell) {
        if (header == null) {
            header = new Table(columnWidths);
            header.setWidth(getWidth());
        }
        header.addCell(headerCell);
    }

    /**
     * Gets the header of the table. The header is represented as a distinct table and might have its own properties.
     * @return table header or {@code null}, if {@link #addHeaderCell(Cell)} hasn't been called.
     */
    public Table getHeader() {
        return header;
    }

    /**
     * Adds a new cell to the footer of the table.
     * The footer will be displayed in the bottom of every area of this table.
     * See also {@link #setSkipLastFooter(boolean)}.
     * @param footerCell a footer cell
     */
    public void addFooterCell(Cell footerCell) {
        if (footer == null) {
            footer = new Table(columnWidths);
            footer.setWidth(getWidth());
        }
        footer.addCell(footerCell);
    }

    /**
     * Gets the footer of the table. The footer is represented as a distinct table and might have its own properties.
     * @return table footer or {@code null}, if {@link #addFooterCell(Cell)} hasn't been called.
     */
    public Table getFooter() {
        return footer;
    }

    /**
     * Tells you if the first header needs to be skipped (for instance if the
     * header says "continued from the previous page").
     *
     * @return Value of property skipFirstHeader.
     */
    public boolean isSkipFirstHeader() {
        return skipFirstHeader;
    }

    /**
     * Tells you if the last footer needs to be skipped (for instance if the
     * footer says "continued on the next page")
     *
     * @return Value of property skipLastFooter.
     */
    public boolean isSkipLastFooter() {
        return skipLastFooter;
    }

    /**
     * Skips the printing of the first header. Used when printing tables in
     * succession belonging to the same printed table aspect.
     *
     * @param skipFirstHeader New value of property skipFirstHeader.
     */
    public Table setSkipFirstHeader(final boolean skipFirstHeader) {
        this.skipFirstHeader = skipFirstHeader;
        return this;
    }

    /**
     * Skips the printing of the last footer. Used when printing tables in
     * succession belonging to the same printed table aspect.
     *
     * @param skipLastFooter New value of property skipLastFooter.
     */
    public Table setSkipLastFooter(final boolean skipLastFooter) {
        this.skipLastFooter = skipLastFooter;
        return this;
    }

    /**
     * Starts new row. This mean that next cell will be added at the beginning of next line.
     *
     * @return the Table object.
     */
    public Table startNewRow() {
        currentColumn = 0;
        currentRow++;
        if (currentRow >= rows.size()) {
            rows.add(new Cell[columnWidths.length]);
        }
        return this;
        //TODO when rendering starts, make sure, that last row is not empty.
    }

    public Table addCell(Cell cell) {
        // Try to find first empty slot in table.
        // We shall not use colspan or rowspan, 1x1 will be enough.
        while (true) {
            if (currentColumn >= columnWidths.length) {
                startNewRow();
            }
            if (rows.get(currentRow - rowWindowStart)[currentColumn] != null) {
                currentColumn++;
            } else {
                break;
            }
        }

        childElements.add(cell);
        cell.updateCellIndexes(currentRow, currentColumn, columnWidths.length);
        // extend bottom grid of slots to fix rowspan
        while (currentRow - rowWindowStart + cell.getRowspan() > rows.size()) {
            rows.add(new Cell[columnWidths.length]);
        }
        // set cell to all region include colspan and rowspan, except not-null cells.
        // this will help to handle empty rows and columns.
        for (int i = currentRow; i < currentRow + cell.getRowspan(); i++) {
            Cell[] row = rows.get(i - rowWindowStart);
            for (int j = currentColumn; j < currentColumn + cell.getColspan(); j++) {
                if (row[j] == null) {
                    row[j] = cell;
                }
            }
        }
        currentColumn += cell.getColspan();
        return this;
    }

    public Cell getCell(int row, int column) {
        if (row - rowWindowStart < rows.size()) {
            Cell cell = rows.get(row - rowWindowStart)[column];
            // make sure that it is top left corner of cell, even in case colspan or rowspan
            if (cell != null && cell.getRow() == row && cell.getCol() == column) {
                return cell;
            }
        }
        return null;
    }

    @Override
    public TableRenderer createRendererSubTree() {
        TableRenderer rendererRoot = makeRenderer();
        for (IElement child : childElements) {
            boolean childShouldBeAdded = isComplete || cellBelongsToAnyRowGroup((Cell) child, lastAddedRowGroups);
            if (childShouldBeAdded) {
                rendererRoot.addChild(child.createRendererSubTree());
            }
        }
        return rendererRoot;
    }

    @Override
    public TableRenderer makeRenderer() {
        if (nextRenderer != null) {
            if (nextRenderer instanceof TableRenderer) {
                IRenderer renderer = nextRenderer;
                nextRenderer = null;
                return (TableRenderer)renderer;
            } else {
                Logger logger = LoggerFactory.getLogger(Table.class);
                logger.error("Invalid renderer for Table: must be inherited from TableRenderer");
            }
        }
        // In case of large tables, we only add to the renderer the cells from complete row groups,
        // for incomplete ones we may have problem with partial rendering because of cross-dependency.
        lastAddedRowGroups = isComplete ? null : getRowGroups();
        if (isComplete) {
            return new TableRenderer(this, new RowRange(rowWindowStart, rowWindowStart + rows.size() - 1));
        } else {
            int rowWindowFinish = lastAddedRowGroups.size() != 0 ? lastAddedRowGroups.get(lastAddedRowGroups.size() - 1).finishRow : -1;
            return new TableRenderer(this, new RowRange(rowWindowStart, rowWindowFinish));
        }
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Indicates that all the desired content has been added to this large element.
     * After this method is called, more precise rendering is activated.
     * For instance, a table may have a {@link #setSkipLastFooter(boolean)} method set to true,
     * and in case of large table on {@link #flush()} we do not know if any more content will be added,
     * so we might not place the content in the bottom of the page where it would fit, but instead add a footer, and
     * place that content in the start of the page. Technically such result would look all right, but it would be
     * more concise if we placed the content in the bottom and did not start new page. For such cases to be
     * renderered more accurately, one can call {@link #complete()} when some content is still there and not flushed.
     */
    @Override
    public void complete() {
        isComplete = true;
        flush();
    }

    /**
     * Writes the newly added content to the document.
     */
    @Override
    public void flush() {
        Cell[] row = null;
        if (!rows.isEmpty()) {
            row = rows.get(rows.size() - 1);
        }

        document.add(this);
        if (row != null) {
            lastAddedRow = row;
        }
    }

    /**
     * Flushes the content which has just been added to the document.
     * This is a method for internal usage and is called automatically by the docunent.
     */
    @Override
    public void flushContent() {
        if (lastAddedRowGroups == null || lastAddedRowGroups.size() == 0)
            return;
        int firstRow = lastAddedRowGroups.get(0).startRow;
        int lastRow = lastAddedRowGroups.get(lastAddedRowGroups.size() - 1).finishRow;

        for (Iterator<IElement> iterator = childElements.iterator(); iterator.hasNext(); ) {
            IElement cell = iterator.next();
            if (((Cell)cell).getRow() >= firstRow && ((Cell)cell).getRow() <= lastRow) {
                iterator.remove();
            }
        }

        for (int i = 0; i <= lastRow - firstRow; i++) {
            rows.remove(firstRow - rowWindowStart);
        }
        rowWindowStart = lastAddedRowGroups.get(lastAddedRowGroups.size() - 1).getFinishRow() + 1;

        lastAddedRowGroups = null;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Border[] getLastRowBottomBorder() {
        Border[] horizontalBorder = new Border[getNumberOfColumns()];
        if (lastAddedRow != null) {
            for (int i = 0; i < lastAddedRow.length; i++) {
                Cell cell = lastAddedRow[i];
                if (cell != null) {
                    Border border = cell.getProperty(Property.BORDER);
                    if (border == null) {
                        border = cell.getProperty(Property.BORDER_BOTTOM);
                    }
                    horizontalBorder[i] = border;
                }
            }
        }

        return horizontalBorder;
    }

    protected void calculateWidths() {
        Property.UnitValue width = getWidth();
        float total = 0;
        int numCols = getNumberOfColumns();
        for (int k = 0; k < numCols; ++k) {
            total += columnWidths[k];
        }
        for (int k = 0; k < numCols; ++k) {
            columnWidths[k] = width.getValue() * columnWidths[k] / total;
        }
    }

    protected java.util.List<RowRange> getRowGroups() {
        int lastRowWeCanFlush = currentColumn == columnWidths.length ? currentRow : currentRow - 1;
        int[] cellBottomRows = new int[columnWidths.length];
        int currentRowGroupStart = rowWindowStart;
        java.util.List<RowRange> rowGroups = new ArrayList<>();
        while (currentRowGroupStart <= lastRowWeCanFlush) {
            for (int i = 0; i < columnWidths.length; i++) {
                cellBottomRows[i] = currentRowGroupStart;
            }
            int maxRowGroupFinish = cellBottomRows[0] + rows.get(cellBottomRows[0] - rowWindowStart)[0].getRowspan() - 1;
            boolean converged = false;
            boolean rowGroupComplete = true;
            while (!converged) {
                converged = true;
                for (int i = 0; i < columnWidths.length; i++) {
                    while (cellBottomRows[i] < lastRowWeCanFlush && cellBottomRows[i] + rows.get(cellBottomRows[i] - rowWindowStart)[i].getRowspan() - 1 < maxRowGroupFinish) {
                        cellBottomRows[i] += rows.get(cellBottomRows[i] - rowWindowStart)[i].getRowspan();
                    }
                    if (cellBottomRows[i] + rows.get(cellBottomRows[i] - rowWindowStart)[i].getRowspan() - 1 > maxRowGroupFinish) {
                        maxRowGroupFinish = cellBottomRows[i] + rows.get(cellBottomRows[i] - rowWindowStart)[i].getRowspan() - 1;
                        converged = false;
                    } else if (cellBottomRows[i] + rows.get(cellBottomRows[i] - rowWindowStart)[i].getRowspan() - 1 < maxRowGroupFinish) {
                        // don't have enough cells for a row group yet.
                        rowGroupComplete = false;
                    }
                }
            }
            if (rowGroupComplete) {
                rowGroups.add(new RowRange(currentRowGroupStart, maxRowGroupFinish));
            }
            currentRowGroupStart = maxRowGroupFinish + 1;
        }

        return rowGroups;
    }

    private void initializeRows() {
        rows = new ArrayList<>();
        startNewRow();
    }

    private boolean cellBelongsToAnyRowGroup(Cell cell, List<RowRange> rowGroups) {
        return rowGroups != null && rowGroups.size() > 0 && cell.getRow() >= rowGroups.get(0).getStartRow()
                && cell.getRow() <= rowGroups.get(rowGroups.size() - 1).getFinishRow();
    }

    public static class RowRange {
        // The start number of the row group, inclusive
        int startRow;
        // The finish number of the row group, inclusive
        int finishRow;

        public RowRange(int startRow, int finishRow) {
            this.startRow = startRow;
            this.finishRow = finishRow;
        }

        public int getStartRow() {
            return startRow;
        }

        public int getFinishRow() {
            return finishRow;
        }
    }
}
