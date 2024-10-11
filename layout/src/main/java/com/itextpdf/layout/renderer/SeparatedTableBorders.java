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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class SeparatedTableBorders extends TableBorders {
    public SeparatedTableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders) {
        super(rows, numberOfColumns, tableBoundingBorders);
    }

    public SeparatedTableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders, int largeTableIndexOffset) {
        super(rows, numberOfColumns, tableBoundingBorders, largeTableIndexOffset);
    }

    @Override
    protected TableBorders drawHorizontalBorder(PdfCanvas canvas, TableBorderDescriptor borderDescriptor) {
        return this;
    }

    @Override
    protected TableBorders drawVerticalBorder(PdfCanvas canvas, TableBorderDescriptor borderDescriptor) {
        return this;
    }

    @Override
    protected TableBorders applyTopTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean force, boolean reverse) {
        return applyTopTableBorder(occupiedBox, layoutBox, reverse);
    }

    @Override
    protected TableBorders applyTopTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean reverse) {
        float topIndent = (reverse ? -1 : 1) * getMaxTopWidth();
        layoutBox.decreaseHeight(topIndent);
        occupiedBox.moveDown(topIndent).increaseHeight(topIndent);
        return this;
    }

    @Override
    protected TableBorders applyBottomTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean force, boolean reverse) {
        return applyBottomTableBorder(occupiedBox, layoutBox, reverse);
    }

    @Override
    protected TableBorders applyBottomTableBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean reverse) {
        float bottomTableBorderWidth = (reverse ? -1 : 1) * getMaxBottomWidth();
        layoutBox.decreaseHeight(bottomTableBorderWidth);
        occupiedBox.moveDown(bottomTableBorderWidth).increaseHeight(bottomTableBorderWidth);
        return this;
    }

    @Override
    protected TableBorders applyLeftAndRightTableBorder(Rectangle layoutBox, boolean reverse) {
        if (null != layoutBox) {
            layoutBox.applyMargins(0, rightBorderMaxWidth, 0, leftBorderMaxWidth, reverse);
        }

        return this;
    }

    @Override
    protected TableBorders skipFooter(Border[] borders) {
        setTableBoundingBorders(borders);
        return this;
    }

    @Override
    protected TableBorders skipHeader(Border[] borders) {
        return this;
    }

    @Override
    protected TableBorders collapseTableWithFooter(TableBorders footerBordersHandler, boolean hasContent) {
        return this;
    }

    @Override
    protected TableBorders collapseTableWithHeader(TableBorders headerBordersHandler, boolean updateBordersHandler) {
        return this;
    }

    @Override
    protected TableBorders fixHeaderOccupiedArea(Rectangle occupiedBox, Rectangle layoutBox) {
        return this;
    }

    @Override
    protected TableBorders applyCellIndents(Rectangle box, float topIndent, float rightIndent, float bottomIndent, float leftIndent, boolean reverse) {
        box.applyMargins(topIndent, rightIndent, bottomIndent, leftIndent, false);
        return this;
    }

    @Override
    public List<Border> getVerticalBorder(int index) {
        return verticalBorders.get(index);
    }

    @Override
    public List<Border> getHorizontalBorder(int index) {
        return horizontalBorders.get(index - largeTableIndexOffset);
    }

    @Override
    protected float getCellVerticalAddition(float[] indents) {
        return 0;
    }

    @Override
    protected TableBorders updateBordersOnNewPage(boolean isOriginalNonSplitRenderer, boolean isFooterOrHeader, TableRenderer currentRenderer, TableRenderer headerRenderer, TableRenderer footerRenderer) {
        if (!isFooterOrHeader) {
            // collapse all cell borders
            if (isOriginalNonSplitRenderer) {
                if (null != rows) {
                    processAllBordersAndEmptyRows();
                    rightBorderMaxWidth = getMaxRightWidth();
                    leftBorderMaxWidth = getMaxLeftWidth();
                }
            }
        }
        if (null != footerRenderer) {
            float rightFooterBorderWidth = footerRenderer.bordersHandler.getMaxRightWidth();
            float leftFooterBorderWidth = footerRenderer.bordersHandler.getMaxLeftWidth();

            leftBorderMaxWidth = Math.max(leftBorderMaxWidth, leftFooterBorderWidth);
            rightBorderMaxWidth = Math.max(rightBorderMaxWidth, rightFooterBorderWidth);
        }

        if (null != headerRenderer) {
            float rightHeaderBorderWidth = headerRenderer.bordersHandler.getMaxRightWidth();
            float leftHeaderBorderWidth = headerRenderer.bordersHandler.getMaxLeftWidth();

            leftBorderMaxWidth = Math.max(leftBorderMaxWidth, leftHeaderBorderWidth);
            rightBorderMaxWidth = Math.max(rightBorderMaxWidth, rightHeaderBorderWidth);
        }

        return this;
    }


    @Override
    public float[] getCellBorderIndents(int row, int col, int rowspan, int colspan) {
        return new float[] {0, 0, 0, 0};
    }

    @Override
    protected void buildBordersArrays(CellRenderer cell, int row, int col) {
        int colspan = (int) cell.getPropertyAsInteger(Property.COLSPAN);
        int rowspan = (int) cell.getPropertyAsInteger(Property.ROWSPAN);
        int colN = ((Cell) cell.getModelElement()).getCol();
        Border[] cellBorders = cell.getBorders();

        // cell with big rowspan was splitted
        if (row + 1 - rowspan < 0) {
            rowspan = row + 1;
        }

        // consider top border
        for (int i = 0; i < colspan; i++) {
            checkAndReplaceBorderInArray(horizontalBorders, 2 * (row + 1 - rowspan), colN + i, cellBorders[0], false);
        }
        // consider bottom border
        for (int i = 0; i < colspan; i++) {
            checkAndReplaceBorderInArray(horizontalBorders, 2 * row + 1, colN + i, cellBorders[2], true);
        }
        // consider left border
        for (int j = row - rowspan + 1; j <= row; j++) {
            checkAndReplaceBorderInArray(verticalBorders, 2 * colN, j, cellBorders[3], false);
        }
        // consider right border
        for (int i = row - rowspan + 1; i <= row; i++) {
            checkAndReplaceBorderInArray(verticalBorders, 2 * (colN + colspan) - 1, i, cellBorders[1], true);
        }
    }

    protected boolean checkAndReplaceBorderInArray(List<List<Border>> borderArray, int i, int j, Border borderToAdd, boolean hasPriority) {
        List<Border> borders = borderArray.get(i);
        Border neighbour = borders.get(j);
        if (neighbour == null) {
            borders.set(j, borderToAdd);
        } else {
            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
            logger.warn(IoLogMessageConstant.UNEXPECTED_BEHAVIOUR_DURING_TABLE_ROW_COLLAPSING);
        }

        return true;
    }

    @Override
    protected TableBorders initializeBorders() {
        List<Border> tempBorders;
        // initialize vertical borders
        while (2 * Math.max(numberOfColumns, 1) > verticalBorders.size()) {
            tempBorders = new ArrayList<Border>();
            while ((int) 2 * Math.max(rows.size(), 1) > tempBorders.size()) {
                tempBorders.add(null);
            }
            verticalBorders.add(tempBorders);
        }
        // initialize horizontal borders
        while ((int) 2 * Math.max(rows.size(), 1) > horizontalBorders.size()) {
            tempBorders = new ArrayList<Border>();
            while (numberOfColumns > tempBorders.size()) {
                tempBorders.add(null);
            }
            horizontalBorders.add(tempBorders);
        }
        return this;
    }

    @Override
    public List<Border> getFirstHorizontalBorder() {
        return getHorizontalBorder(2 * startRow);
    }

    @Override
    public List<Border> getLastHorizontalBorder() {
        return getHorizontalBorder(2 * finishRow + 1);
    }

    @Override
    public float getMaxTopWidth() {
        return null == tableBoundingBorders[0] ? 0 : tableBoundingBorders[0].getWidth();
    }

    @Override
    public float getMaxBottomWidth() {
        return null == tableBoundingBorders[2] ? 0 : tableBoundingBorders[2].getWidth();
    }

    @Override
    public float getMaxRightWidth() {
        return null == tableBoundingBorders[1] ? 0 : tableBoundingBorders[1].getWidth();
    }

    @Override
    public float getMaxLeftWidth() {
        return null == tableBoundingBorders[3] ? 0 : tableBoundingBorders[3].getWidth();
    }
}
