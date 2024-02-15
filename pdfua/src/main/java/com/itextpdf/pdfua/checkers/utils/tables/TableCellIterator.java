/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
