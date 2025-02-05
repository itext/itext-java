/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.pdfua.checkers.utils.tables;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfua.checkers.utils.PdfUAValidationContext;

import java.util.List;

/**
 * Class that iterates over the cells of a table.
 */
final class TableCellIterator implements ITableIterator<Cell> {

    final PdfUAValidationContext context;
    private List<IElement> children;
    private int index;
    private TableCellIterator headerIterator;
    private TableCellIterator footerIterator;
    private Table table;
    private PdfName location;
    private Cell currentCell;

    /**
     * Creates a new {@link TableCellIterator} instance.
     *
     * @param table the table that will be iterated.
     * @param context the validation context.
     */
    public TableCellIterator(Table table, PdfUAValidationContext context) {
        this.context = context;
        if (table == null) {
            return;
        }
        this.table = table;
        this.children = table.getChildren();
        headerIterator = new TableCellIterator(table.getHeader(), context);
        footerIterator = new TableCellIterator(table.getFooter(), context);
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
            currentCell = headerIterator.next();
            return currentCell;
        }
        if (children != null && index < children.size()) {
            location = PdfName.TBody;
            currentCell = (Cell) children.get(index++);
            return currentCell;
        }
        if (footerIterator != null && footerIterator.hasNext()) {
            location = PdfName.TFoot;
            currentCell = footerIterator.next();
            return currentCell;
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
    public int getNumberOfColumns() {
        return this.table.getNumberOfColumns();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getRow() {
        PdfName location = getLocation();
        int row = currentCell.getRow();
        if (location == PdfName.TBody) {
            row += this.getAmountOfRowsHeader();
        }
        if (location == PdfName.TFoot) {
            row += this.getAmountOfRowsHeader();
            row += this.getAmountOfRowsBody();
        }
        return row;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCol() {
        return currentCell.getCol();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowspan() {
        return currentCell.getRowspan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColspan() {
        return currentCell.getColspan();
    }


    private PdfName getLocation() {
        return this.location;
    }
}
