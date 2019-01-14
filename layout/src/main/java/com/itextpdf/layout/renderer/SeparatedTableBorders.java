/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.Property;
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
    protected TableBorders drawHorizontalBorder(int i, float startX, float y1, PdfCanvas canvas, float[] countedColumnWidth) {
        return this;
    }

    @Override
    protected TableBorders drawVerticalBorder(int i, float startY, float x1, PdfCanvas canvas, List<Float> heights) {
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
        float[] indents = new float[4];
        Border[] borders = rows.get(row + startRow - largeTableIndexOffset)[col].getBorders();

        for (int i = 0; i < 4; i++) {
            if (null != borders[i]) {
                indents[i] = borders[i].getWidth();
            }
        }

        return indents;
    }

    protected void buildBordersArrays(CellRenderer cell, int row, int col, int[] rowspansToDeduct) {
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
//        if (borderArray.size() <= i) {
//            for (int count = borderArray.size(); count <= i; count++) {
//                borderArray.add(new ArrayList<Border>());
//            }
//        }
        List<Border> borders = borderArray.get(i);
//        if (borders.isEmpty()) {
//            for (int count = 0; count < j; count++) {
//                borders.add(null);
//            }
//            borders.add(borderToAdd);
//            return true;
//        }
//        if (borders.size() == j) {
//            borders.add(borderToAdd);
//            return true;
//        }
//        if (borders.size() < j) {
//            for (int count = borders.size(); count <= j; count++) {
//                borders.add(count, null);
//            }
//        }
        Border neighbour = borders.get(j);
        if (neighbour == null) {
            borders.set(j, borderToAdd);
        } else {
            Logger logger = LoggerFactory.getLogger(TableRenderer.class);
            logger.warn(LogMessageConstant.UNEXPECTED_BEHAVIOUR_DURING_TABLE_ROW_COLLAPSING);
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
