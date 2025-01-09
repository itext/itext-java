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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that represents a matrix of cells in a table.
 * It is used to check if the table has valid headers and scopes for the cells.
 *
 * @param <T> The type of the cell.
 */
abstract class AbstractResultMatrix<T> {

    protected final ITableIterator<T> iterator;
    //We can't use an array because it is not autoportable
    private final List<T> cellMatrix;
    private final int rows;
    private final int cols;

    /**
     * Creates a new {@link AbstractResultMatrix} instance.
     *
     * @param iterator The iterator that will be used to iterate over the cells.
     */
    protected AbstractResultMatrix(ITableIterator<T> iterator) {
        this.rows = iterator.getAmountOfRowsHeader() + iterator.getAmountOfRowsBody() +
                iterator.getAmountOfRowsFooter();
        this.cols = iterator.getNumberOfColumns();
        this.iterator = iterator;
        cellMatrix = this.<T>createFixedSizedList(rows * cols, null);
    }


    /**
     * Runs the algorithm to check if the table has valid headers and scopes for the cells.
     *
     * @throws PdfUAConformanceException if the table doesn't have valid headers and scopes for the cells.
     */
    public void checkValidTableTagging() {
        final Set<String> knownIds = new HashSet<>();
        // We use boxed boolean array so we can don't duplicate our setCell methods.
        // But we fill default with false so we can avoid the null check.
        final List<Boolean> scopeMatrix = this.<Boolean>createFixedSizedList(rows * cols, false);
        boolean hasUnknownHeaders = false;
        while (iterator.hasNext()) {
            final T cell = iterator.next();
            final String role = getRole(cell);
            final int rowspan = iterator.getRowspan();
            final int colspan = iterator.getColspan();
            final int colIdx = iterator.getCol();
            final int rowIdx = iterator.getRow();

            this.setCell(rowIdx, rowspan, colIdx, colspan, cellMatrix, cell);

            if (StandardRoles.TH.equals(role)) {
                final byte[] id = getElementId(cell);
                if (id != null) {
                    knownIds.add(new String(id, StandardCharsets.UTF_8));
                }
                final String scope = getScope(cell);
                if (PdfName.Column.getValue().equals(scope)) {
                    this.setColumnValue(colIdx, colspan, scopeMatrix, true);
                } else if (PdfName.Row.getValue().equals(scope)) {
                    this.setRowValue(rowIdx, rowspan, scopeMatrix, true);
                } else if (PdfName.Both.getValue().equals(scope)) {
                    this.setColumnValue(colIdx, colspan, scopeMatrix, true);
                    this.setRowValue(rowIdx, rowspan, scopeMatrix, true);
                } else {
                    hasUnknownHeaders = true;
                }
            } else if (!StandardRoles.TD.equals(role)) {
                final String message = MessageFormatUtil.format(PdfUAExceptionMessageConstants.CELL_HAS_INVALID_ROLE,
                        getNormalizedRow(rowIdx), getLocationInTable(rowIdx), colIdx);
                throw new PdfUAConformanceException(message);
            }

        }
        validateTableCells(knownIds, scopeMatrix, hasUnknownHeaders);
    }

    private void setRowValue(int row, int rowSpan, List<Boolean> arr, boolean value) {
        setCell(row, rowSpan, 0, this.cols, arr, value);
    }


    abstract List<byte[]> getHeaders(T cell);

    abstract String getScope(T cell);

    abstract byte[] getElementId(T cell);

    abstract String getRole(T cell);

    private void validateTableCells(Set<String> knownIds, List<Boolean> scopeMatrix, boolean hasUnknownHeaders) {
        final StringBuilder sb = new StringBuilder();
        boolean areAllTDCellsValid = true;
        for (int i = 0; i < this.cellMatrix.size(); i++) {
            final T cell = this.cellMatrix.get(i);
            if (cell == null) {
                final String message = MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.TABLE_CONTAINS_EMPTY_CELLS,
                        getNormalizedRow(i), getLocationInTable(i), i % this.cols);
                throw new PdfUAConformanceException(message);
            }
            final String role = getRole(cell);
            if (!StandardRoles.TD.equals(role)) {
                continue;
            }
            if (hasValidHeaderIds(cell, knownIds)) {
                continue;
            }

            final boolean hasConnectedHeader = (boolean) scopeMatrix.get(i);
            if (!hasConnectedHeader && hasUnknownHeaders) {
                // we don't want to break here, we want to collect all the errors
                areAllTDCellsValid = false;
                int row = i / this.cols;
                int col = i % this.cols;
                final String location = getLocationInTable(row);
                final String message = MessageFormatUtil.format(
                        PdfUAExceptionMessageConstants.CELL_CANT_BE_DETERMINED_ALGORITHMICALLY,
                        getNormalizedRow(row),
                        col,
                        location);
                sb.append(message).append('\n');

            }
        }
        if (!areAllTDCellsValid) {
            throw new PdfUAConformanceException(sb.toString());
        }

    }

    private String getLocationInTable(int row) {
        if (row < iterator.getAmountOfRowsHeader()) {
            return "Header";
        } else if (row < iterator.getAmountOfRowsHeader() + iterator.getAmountOfRowsBody()) {
            return "Body";
        } else {
            return "Footer";
        }
    }

    private int getNormalizedRow(int row) {
        if (row < iterator.getAmountOfRowsHeader()) {
            return row;
        } else if (row < iterator.getAmountOfRowsHeader() + iterator.getAmountOfRowsBody()) {
            return row - iterator.getAmountOfRowsHeader();
        } else {
            return row - iterator.getAmountOfRowsHeader() - iterator.getAmountOfRowsBody();
        }
    }

    private <Z> void setCell(int row, int rowSpan, int col, int colSpan, List<Z> arr, Z value) {
        for (int i = row; i < row + rowSpan; i++) {
            for (int j = col; j < col + colSpan; j++) {
                arr.set(i * this.cols + j, value);
            }
        }
    }

    private void setColumnValue(int col, int colSpan, List<Boolean> arr, boolean value) {
        setCell(0, this.rows, col, colSpan, arr, value);
    }

    private boolean hasValidHeaderIds(T cell, Set<String> knownIds) {
        final List<byte[]> headers = getHeaders(cell);
        if (headers == null) {
            return false;
        }
        if (headers.isEmpty()) {
            return false;
        }
        for (byte[] knownId : headers) {
            if (!knownIds.contains(new String(knownId, StandardCharsets.UTF_8))) {
                return false;
            }
        }
        return true;
    }


    private static <Z> List<Z> createFixedSizedList(int capacity, Object defaultValue) {
        List<Z> arr = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            arr.add((Z)defaultValue);
        }
        return arr;
    }
}
