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

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Creates an iterator to iterate over the table structures.
 */
public class TableStructElementIterator implements ITableIterator<PdfStructElem> {
    private final List<PdfStructElem> all = new ArrayList<>();
    private final HashMap<PdfStructElem, Tuple2<Integer, Integer>> locationCache = new HashMap<>();
    private int amountOfCols = 0;
    private int amountOfRowsHeader = 0;
    private int amountOfRowsBody = 0;
    private int amountOfRowsFooter = 0;
    private int iterIndex = 0;

    private PdfStructElem currentValue;


    /**
     * Creates a new {@link TableStructElementIterator} instance.
     *
     * @param tableStructElem The root table struct element.
     */
    public TableStructElementIterator(PdfStructElem tableStructElem) {
        flattenElements(tableStructElem);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return iterIndex < all.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PdfStructElem next() {
        currentValue = all.get(iterIndex++);
        return currentValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAmountOfRowsBody() {
        return this.amountOfRowsBody;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAmountOfRowsHeader() {
        return this.amountOfRowsHeader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAmountOfRowsFooter() {
        return this.amountOfRowsFooter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumberOfColumns() {
        return this.amountOfCols;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRow() {
        return locationCache.get(currentValue).getFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCol() {
        return locationCache.get(currentValue).getSecond();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowspan() {
        return getRowspan(currentValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColspan() {
        return getColspan(currentValue);
    }

    private void flattenElements(PdfStructElem table) {
        List<PdfStructElem> rows = extractTableRows(table);
        setAmountOfCols(rows);
        build2DRepresentationOfTagTreeStructures(rows);
    }

    private List<PdfStructElem> extractTableRows(PdfStructElem table) {
        final List<IStructureNode> kids = table.getKids();
        final List<PdfStructElem> rows = new ArrayList<>();
        for (final IStructureNode kid : kids) {
            if (kid == null) {
                continue;
            }
            if (PdfName.THead.equals(kid.getRole())) {
                final List<PdfStructElem> headerRows = extractAllTrTags(kid.getKids());
                this.amountOfRowsHeader = headerRows.size();
                rows.addAll(headerRows);
            } else if (PdfName.TBody.equals(kid.getRole())) {
                final List<PdfStructElem> bodyRows = extractAllTrTags(kid.getKids());
                this.amountOfRowsBody += bodyRows.size();
                rows.addAll(bodyRows);
            } else if (PdfName.TFoot.equals(kid.getRole())) {
                final List<PdfStructElem> footerRows = extractAllTrTags(kid.getKids());
                this.amountOfRowsFooter = footerRows.size();
                rows.addAll(footerRows);
            } else if (PdfName.TR.equals(kid.getRole())) {
                final List<PdfStructElem> bodyRows = extractAllTrTags(Collections.singletonList(kid));
                this.amountOfRowsBody += bodyRows.size();
                rows.addAll(bodyRows);
            }
        }
        return rows;
    }

    private void build2DRepresentationOfTagTreeStructures(List<PdfStructElem> rows) {
        // A matrix which is filled by true for all occupied cells taking colspan and rowspan into account
        final boolean[][] arr = new boolean[rows.size()][];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = new boolean[amountOfCols];
        }
        for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
            final List<PdfStructElem> cells = extractCells(rows.get(rowIdx));
            for (PdfStructElem cell : cells) {
                final int colSpan = getColspan(cell);
                final int rowSpan = getRowspan(cell);
                int firstOpenColIndex = -1;
                for (int i = 0; i < amountOfCols; i++) {
                    if (!arr[rowIdx][i]) {
                        firstOpenColIndex = i;
                        break;
                    }
                }
                // Set the colspan and rowspan of each cell with a placeholder
                for (int i = rowIdx; i < rowIdx + rowSpan; i++) {
                    for (int j = firstOpenColIndex; j < firstOpenColIndex + colSpan; j++) {
                        arr[i][j] = true;
                    }
                }
                locationCache.put(cell, new Tuple2<>(rowIdx, firstOpenColIndex));
                all.add(cell);
            }
        }

        // Now go over the matrix and convert remaining false (empty spaces) into dummy struct elems
        for (int rowIdx = 0; rowIdx < arr.length; rowIdx++) {
            for (int colIdx = 0; colIdx < arr[rowIdx].length; colIdx++) {
                if (!arr[rowIdx][colIdx]) {
                    PdfStructElem pdfStructElem = new PdfStructElem(new PdfDictionary());
                    locationCache.put(pdfStructElem, new Tuple2<>(rowIdx, colIdx));
                    all.add(pdfStructElem);
                }
            }
        }
    }

    private void setAmountOfCols(List<PdfStructElem> rows) {
        for (final PdfStructElem row : rows) {
            int amt = 0;
            for (final PdfStructElem kid : extractCells(row)) {
                amt += getColspan(kid);
            }
            amountOfCols = Math.max(amt, amountOfCols);
        }
    }

    private static int getColspan(PdfStructElem structElem) {
        return getIntValueFromAttributes(structElem, PdfName.ColSpan);
    }

    private static int getRowspan(PdfStructElem structElem) {
        return getIntValueFromAttributes(structElem, PdfName.RowSpan);
    }

    private static int getIntValueFromAttributes(PdfStructElem elem, PdfName name) {
        PdfObject object = elem.getAttributes(false);
        if (object instanceof PdfArray) {
            PdfArray array = (PdfArray) object;
            for (PdfObject pdfObject : array) {
                if (pdfObject instanceof PdfDictionary) {
                    PdfNumber f = ((PdfDictionary) pdfObject).getAsNumber(name);
                    if (f != null) {
                        return f.intValue();

                    }
                }
            }
        } else if (object instanceof PdfDictionary) {
            PdfNumber f = ((PdfDictionary) object).getAsNumber(name);
            if (f != null) {
                return f.intValue();
            }
        }
        return 1;
    }

    private static List<PdfStructElem> extractCells(PdfStructElem row) {
        final List<PdfStructElem> elems = new ArrayList<>();
        for (final IStructureNode kid : row.getKids()) {
            if (kid instanceof PdfStructElem && (PdfName.TH.equals(kid.getRole()) || PdfName.TD.equals(
                    kid.getRole()))) {
                elems.add((PdfStructElem) kid);
            }
        }
        return elems;
    }

    private static List<PdfStructElem> extractAllTrTags(List<IStructureNode> possibleTrs) {
        final List<PdfStructElem> elems = new ArrayList<>();
        for (final IStructureNode possibleTr : possibleTrs) {
            if (possibleTr instanceof PdfStructElem && PdfName.TR.equals(possibleTr.getRole())) {
                elems.add((PdfStructElem) possibleTr);
            }
        }
        return elems;
    }

}
