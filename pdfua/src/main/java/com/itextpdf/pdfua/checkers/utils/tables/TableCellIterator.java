package com.itextpdf.pdfua.checkers.utils.tables;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Table;

import java.util.List;

/**
 * Class that iterates over the cells of a table.
 */
final class TableCellIterator implements ITableIterator<Cell> {

    private List<IElement> children;
    private int index;
    private TableCellIterator headerIterator;
    private TableCellIterator footerIterator;

    private Table table;
    private PdfName location;

    /**
     * Creates a new {@link TableCellIterator} instance.
     *
     * @param table the table that will be iterated.
     */
    public TableCellIterator(Table table) {
        if (table == null) {
            return;
        }
        this.table = table;
        this.children = table.getChildren();
        headerIterator = new TableCellIterator(table.getHeader());
        footerIterator = new TableCellIterator(table.getFooter());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        if (headerIterator != null && headerIterator.hasNext()) {
            return true;
        }
        if (children != null && index < children.size()) {
            return true;
        }
        if (footerIterator != null && footerIterator.hasNext()) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cell next() {
        if (headerIterator != null && headerIterator.hasNext()) {
            location = PdfName.THead;
            return headerIterator.next();
        }
        if (children != null && index < children.size()) {
            location = PdfName.TBody;
            return (Cell) children.get(index++);
        }
        if (footerIterator != null && footerIterator.hasNext()) {
            location = PdfName.TFoot;
            return footerIterator.next();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAmountOfRowsBody() {
        return table.getNumberOfRows();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAmountOfRowsHeader() {
        if (table.getHeader() != null) {
            return table.getHeader().getNumberOfRows();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAmountOfRowsFooter() {
        if (this.table.getFooter() != null) {
            return this.table.getFooter().getNumberOfRows();
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfName getLocation() {
        return this.location;
    }

}
