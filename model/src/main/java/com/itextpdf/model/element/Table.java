package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.TableRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;

public class Table extends BlockElement<Table> implements ILargeElement<Table> {

    private ArrayList<Cell[]> rows;

    private float[] columnWidths;
    private int currentColumn = 0;
    private int currentRow = -1;
    private int headerRows;
    private int footerRows;
    private boolean skipFirstHeader;
    private boolean skipLastFooter;
    private boolean isComplete;
    private List<RowRange> lastAddedRowGroups;
    // Start number of the row "window" (range) that this table currently contain.
    // For large tables we might contain only a few rows, not all of them, other ones might have been flushed.
    private int rowWindowStart = 0;

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
        super.setWidth(0);
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
    public Table setWidth(float width) {
        Float currWidth = getWidth();
        if (currWidth != width) {
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
     * Gets the number of the rows that constitute the header.
     *
     * @return the number of the rows that constitute the header
     */
    public int getHeaderRows() {
        return headerRows;
    }

    /**
     * Gets the number of rows in the footer.
     *
     * @return the number of rows in the footer
     */
    public int getFooterRows() {
        return this.footerRows;
    }

    /**
     * Sets the number of the top rows that constitute the header. This header
     * has only meaning if the table crosses pages.
     *
     * @param headerRows the number of the top rows that constitute the header
     */
    public Table setHeaderRows(int headerRows) {
        if (headerRows < 0) {
            headerRows = 0;
        }
        this.headerRows = headerRows;
        return this;
    }

    /**
     * Sets the number of the top rows that constitute the header
     * and sets the number of rows to be used for the footer. The number of footer
     * rows are subtracted from the header rows. For example, for a table with
     * two header rows and one footer row the code would be:
     * <pre>
     * table.setHeaderRows(3);
     * table.setFooterRows(1);
     * </pre> Row 0 and 1 will be the header rows and row 2 will be the footer
     * row.
     *
     * @param headerRows the number of the top rows that constitute the header
     * @param footerRows the number of rows to be used for the footer
     */
    public Table setHeaderAndFooterRows(int headerRows, int footerRows) {
        if (headerRows < 0) {
            headerRows = 0;
        }
        if (footerRows < 0) {
            footerRows = 0;
        }
        if (footerRows > headerRows) {
            footerRows = headerRows;
        }
        this.headerRows = headerRows;
        this.footerRows = footerRows;
        return this;
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
        // In case of large tables, we only add to the renderer the cells from complete row groups,
        // for incomplete ones we may have problem with partial rendering because of cross-dependency.
        lastAddedRowGroups = isComplete ? null : getRowGroups();
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
        return new TableRenderer(this, new RowRange(rowWindowStart, rowWindowStart + rows.size() - 1));
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }

    @Override
    public void complete() {
        isComplete = true;
    }

    @Override
    public void flush() {
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

    protected void calculateWidths() {
        Float width = getWidth();
        if (width <= 0) {
            return;
        }
        float total = 0;
        int numCols = getNumberOfColumns();
        for (int k = 0; k < numCols; ++k) {
            total += columnWidths[k];
        }
        for (int k = 0; k < numCols; ++k) {
            columnWidths[k] = width * columnWidths[k] / total;
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
