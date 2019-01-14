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
import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class TableWidths {

    private final TableRenderer tableRenderer;
    private final int numberOfColumns;
    private final float rightBorderMaxWidth;
    private final float leftBorderMaxWidth;
    private final ColumnWidthData[] widths;
    private final float horizontalBorderSpacing;
    private List<CellInfo> cells;

    private float tableWidth;
    private boolean fixedTableWidth;
    private boolean fixedTableLayout = false;
    private float layoutMinWidth;
    private float tableMinWidth;
    private float tableMaxWidth;

    TableWidths(TableRenderer tableRenderer, float availableWidth, boolean calculateTableMaxWidth,
                float rightBorderMaxWidth, float leftBorderMaxWidth) {
        this.tableRenderer = tableRenderer;
        this.numberOfColumns = ((Table) tableRenderer.getModelElement()).getNumberOfColumns();
        this.widths = new ColumnWidthData[numberOfColumns];
        this.rightBorderMaxWidth = rightBorderMaxWidth;
        this.leftBorderMaxWidth = leftBorderMaxWidth;
        if (tableRenderer.bordersHandler instanceof SeparatedTableBorders) {
            Float horizontalSpacing = tableRenderer.getPropertyAsFloat(Property.HORIZONTAL_BORDER_SPACING);
            horizontalBorderSpacing = null == horizontalSpacing ? 0 : (float) horizontalSpacing;
        } else {
            horizontalBorderSpacing = 0;
        }
        calculateTableWidth(availableWidth, calculateTableMaxWidth);
    }

    boolean hasFixedLayout() {
        return fixedTableLayout;
    }

    float[] layout() {
        if (hasFixedLayout()) {
            return fixedLayout();
        } else {
            return autoLayout();
        }
    }

    float getMinWidth() {
        return layoutMinWidth;
    }

    float[] autoLayout() {
        assert tableRenderer.getTable().isComplete();
        fillAndSortCells();
        calculateMinMaxWidths();

        float minSum = 0;
        for (ColumnWidthData width : widths) minSum += width.min;

        //region Process cells

        for (CellInfo cell : cells) {
            // For automatic layout algorithm percents have higher priority
            // value must be > 0, while for fixed layout >= 0
            UnitValue cellWidth = getCellWidth(cell.getCell(), false);
            if (cellWidth != null) {
                assert cellWidth.getValue() > 0;
                if (cellWidth.isPercentValue()) {
                    //cellWidth has percent value
                    if (cell.getColspan() == 1) {
                        widths[cell.getCol()].setPercents(cellWidth.getValue());
                    } else {
                        int pointColumns = 0;
                        float percentSum = 0;
                        for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                            if (!widths[i].isPercent) {
                                pointColumns++;
                            } else {
                                percentSum += widths[i].width;
                            }
                        }
                        float percentAddition = cellWidth.getValue() - percentSum;
                        if (percentAddition > 0) {
                            if (pointColumns == 0) {
                                //ok, add percents to each column
                                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                                    widths[i].addPercents(percentAddition / cell.getColspan());
                                }
                            } else {
                                // set percent only to cells without one
                                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                                    if (!widths[i].isPercent) {
                                        widths[i].setPercents(percentAddition / pointColumns);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //cellWidth has point value
                    if (cell.getColspan() == 1) {
                        if (!widths[cell.getCol()].isPercent) {
                            if (widths[cell.getCol()].min <= cellWidth.getValue()) {
                                widths[cell.getCol()].setPoints(cellWidth.getValue()).setFixed(true);
                            } else {
                                widths[cell.getCol()].setPoints(widths[cell.getCol()].min);
                            }
                        }
                    } else {
                        int flexibleCols = 0;
                        float remainWidth = cellWidth.getValue();
                        for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                            if (!widths[i].isPercent) {
                                remainWidth -= widths[i].width;
                                if (!widths[i].isFixed) {
                                    flexibleCols++;
                                }
                            } else {
                                // if any col has percent value, we cannot predict remaining width.
                                remainWidth = 0;
                                break;
                            }
                        }
                        if (remainWidth > 0) {
                            int[] flexibleColIndexes = ArrayUtil.fillWithValue(new int[cell.getColspan()], -1);
                            if (flexibleCols > 0) {
                                // check min width in columns
                                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                                    if (!widths[i].isFlexible())
                                        continue;
                                    if (widths[i].min > widths[i].width + remainWidth / flexibleCols) {
                                        widths[i].resetPoints(widths[i].min);
                                        remainWidth -= widths[i].min - widths[i].width;
                                        flexibleCols--;
                                        if (flexibleCols == 0 || remainWidth <= 0) {
                                            break;
                                        }
                                    } else {
                                        flexibleColIndexes[i - cell.getCol()] = i;
                                    }
                                }
                                if (flexibleCols > 0 && remainWidth > 0) {
                                    for (int i = 0; i < flexibleColIndexes.length; i++) {
                                        if (flexibleColIndexes[i] >= 0) {
                                            widths[flexibleColIndexes[i]].addPoints(remainWidth / flexibleCols).setFixed(true);
                                        }
                                    }
                                }
                            } else {
                                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                                    widths[i].addPoints(remainWidth / cell.getColspan());
                                }
                            }
                        }
                    }
                }
            } else if (widths[cell.getCol()].isFlexible()) {
                //if there is no information, try to set max width
                int flexibleCols = 0;
                float remainWidth = 0;
                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                    if (widths[i].isFlexible()) {
                        remainWidth += widths[i].max - widths[i].width;
                        flexibleCols++;
                    }
                }
                if (remainWidth > 0) { // flexibleCols > 0 too
                    for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                        if (widths[i].isFlexible()) {
                            widths[i].addPoints(remainWidth / flexibleCols);
                        }
                    }
                }
            }
        }

        //endregion

        //region Process columns
        //TODO add colgroup information.
        for (int i = 0; i < numberOfColumns; i++) {
            UnitValue colWidth = getTable().getColumnWidth(i);
            if (colWidth != null && colWidth.getValue() > 0) {
                if (colWidth.isPercentValue()) {
                    if (!widths[i].isPercent) {
                        if (widths[i].isFixed && widths[i].width > widths[i].min) {
                            widths[i].max = widths[i].width;
                        }
                        widths[i].setPercents(colWidth.getValue());
                    }
                } else if (!widths[i].isPercent && colWidth.getValue() >= widths[i].min) {
                    if (widths[i].isFixed) {
                        widths[i].setPoints(colWidth.getValue());
                    } else {
                        widths[i].resetPoints(colWidth.getValue()).setFixed(true);
                    }
                }
            }
        }
        //endregion

        // region recalculate
        if (tableWidth - minSum < 0) {
            for (int i = 0; i < numberOfColumns; i++) {
                widths[i].finalWidth = widths[i].min;
            }
        } else {
            float sumOfPercents = 0;
            // minTableWidth include only non percent columns.
            float minTableWidth = 0;
            float totalNonPercent = 0;

            // validate sumOfPercents, last columns will be set min width, if sum > 100.
            for (int i = 0; i < widths.length; i++) {
                if (widths[i].isPercent) {
                    if (sumOfPercents < 100 && sumOfPercents + widths[i].width > 100) {
                        widths[i].width = 100 - sumOfPercents;
                        sumOfPercents += widths[i].width;
                        warn100percent();
                    } else if (sumOfPercents >= 100) {
                        widths[i].resetPoints(widths[i].min);
                        minTableWidth += widths[i].min;
                        warn100percent();
                    } else {
                        sumOfPercents += widths[i].width;
                    }
                } else {
                    minTableWidth += widths[i].min;
                    totalNonPercent += widths[i].width;
                }
            }
            assert sumOfPercents <= 100;

            boolean toBalance = true;
            if (!fixedTableWidth) {
                float tableWidthBasedOnPercents = sumOfPercents < 100
                        ? totalNonPercent * 100 / (100 - sumOfPercents) : 0;
                for (int i = 0; i < numberOfColumns; i++) {
                    if (widths[i].isPercent && widths[i].width > 0) {
                        tableWidthBasedOnPercents = Math.max(widths[i].max * 100 / widths[i].width, tableWidthBasedOnPercents);
                    }
                }

                if (tableWidthBasedOnPercents <= tableWidth) {
                    if (tableWidthBasedOnPercents >= minTableWidth) {
                        tableWidth = tableWidthBasedOnPercents;
                        //we don't need more space, columns are done based on column's max width.
                        toBalance = false;
                    } else {
                        tableWidth = minTableWidth;
                    }
                }
            }

            if (sumOfPercents > 0 && sumOfPercents < 100 && totalNonPercent == 0) {
                // each column has percent value but sum < 100%
                // upscale percents
                for (int i = 0; i < widths.length; i++) {
                    widths[i].width = 100 * widths[i].width / sumOfPercents;
                }
                sumOfPercents = 100;
            }

            if (!toBalance) {
                //column width based on max width, no need to check min width.
                for (int i = 0; i < numberOfColumns; i++) {
                    widths[i].finalWidth = widths[i].isPercent
                            ? tableWidth * widths[i].width / 100
                            : widths[i].width;
                }
            } else if (sumOfPercents >= 100) {
                sumOfPercents = 100;
                boolean recalculatePercents = false;
                float remainWidth = tableWidth - minTableWidth;
                for (int i = 0; i < numberOfColumns; i++) {
                    if (widths[i].isPercent) {
                        if (remainWidth * widths[i].width / 100 >= widths[i].min) {
                            widths[i].finalWidth = remainWidth * widths[i].width / 100;
                        } else {
                            widths[i].finalWidth = widths[i].min;
                            widths[i].isPercent = false;
                            remainWidth -= widths[i].min;
                            sumOfPercents -= widths[i].width;
                            recalculatePercents = true;
                        }
                    } else {
                        widths[i].finalWidth = widths[i].min;
                    }
                }
                if (recalculatePercents) {
                    for (int i = 0; i < numberOfColumns; i++) {
                        if (widths[i].isPercent) {
                            widths[i].finalWidth = remainWidth * widths[i].width / sumOfPercents;
                        }
                    }
                }
            } else {
                // We either have some extra space and may extend columns in case fixed table width,
                // or have to decrease columns to fit table width.
                //
                // columns shouldn't be more than its max value in case unspecified table width.
                // columns shouldn't be more than its percentage value.

                // opposite to sumOfPercents, which is sum of percent values in points.
                float totalPercent = 0;
                float minTotalNonPercent = 0;
                float fixedAddition = 0;
                float flexibleAddition = 0;
                boolean hasFlexibleCell = false;
                //sum of non fixed non percent columns.
                for (int i = 0; i < numberOfColumns; i++) {
                    if (widths[i].isPercent) {
                        if (tableWidth * widths[i].width / 100 >= widths[i].min) {
                            widths[i].finalWidth = tableWidth * widths[i].width / 100;
                            totalPercent += widths[i].finalWidth;
                        } else {
                            sumOfPercents -= widths[i].width;
                            widths[i].resetPoints(widths[i].min);
                            widths[i].finalWidth = widths[i].min;
                            minTotalNonPercent += widths[i].min;
                        }
                    } else {
                        widths[i].finalWidth = widths[i].min;
                        minTotalNonPercent += widths[i].min;
                        float addition = widths[i].width - widths[i].min;
                        if (widths[i].isFixed) {
                            fixedAddition += addition;
                        } else {
                            flexibleAddition += addition;
                            hasFlexibleCell = true;
                        }
                    }
                }
                if (totalPercent + minTotalNonPercent > tableWidth) {
                    // collision between minWidth and percent value.
                    float extraWidth = tableWidth - minTotalNonPercent;
                    if (sumOfPercents > 0) {
                        for (int i = 0; i < numberOfColumns; i++) {
                            if (widths[i].isPercent) {
                                widths[i].finalWidth = extraWidth * widths[i].width / sumOfPercents;
                            }
                        }
                    }
                } else {
                    float extraWidth = tableWidth - totalPercent - minTotalNonPercent;
                    if (fixedAddition > 0 && (extraWidth < fixedAddition || !hasFlexibleCell)) {
                        for (int i = 0; i < numberOfColumns; i++) {
                            //only points could be fixed
                            if (widths[i].isFixed) {
                                widths[i].finalWidth += (widths[i].width - widths[i].min) * extraWidth / fixedAddition;
                            }
                        }
                    } else {
                        extraWidth -= fixedAddition;
                        if (extraWidth < flexibleAddition) {
                            for (int i = 0; i < numberOfColumns; i++) {
                                if (widths[i].isFixed) {
                                    widths[i].finalWidth = widths[i].width;
                                } else if (!widths[i].isPercent) {
                                    widths[i].finalWidth += (widths[i].width - widths[i].min) * extraWidth / flexibleAddition;
                                }
                            }
                        } else {
                            float totalFixed = 0;
                            float totalFlexible = 0;
                            float flexibleCount = 0;
                            for (int i = 0; i < numberOfColumns; i++) {
                                if (widths[i].isFixed) {
                                    widths[i].finalWidth = widths[i].width;
                                    totalFixed += widths[i].width;
                                } else if (!widths[i].isPercent) {
                                    totalFlexible += widths[i].width;
                                    flexibleCount++;
                                }
                            }
                            assert totalFlexible > 0 || flexibleCount > 0;
                            extraWidth = tableWidth - totalPercent - totalFixed;
                            for (int i = 0; i < numberOfColumns; i++) {
                                if (!widths[i].isPercent && !widths[i].isFixed) {
                                    widths[i].finalWidth = totalFlexible > 0
                                            ? widths[i].width * extraWidth / totalFlexible
                                            : extraWidth / flexibleCount;
                                }
                            }
                        }
                    }
                }
            }
        }
        //endregion

        return extractWidths();
    }

    float[] fixedLayout() {
        float[] columnWidths = new float[numberOfColumns];
        //fill columns from col info
        for (int i = 0; i < numberOfColumns; i++) {
            UnitValue colWidth = getTable().getColumnWidth(i);
            if (colWidth == null || colWidth.getValue() < 0) {
                columnWidths[i] = -1;
            } else if (colWidth.isPercentValue()) {
                columnWidths[i] = colWidth.getValue() * tableWidth / 100;
            } else {
                columnWidths[i] = colWidth.getValue();
            }
        }
        //fill columns with -1 from cell info.
        int processedColumns = 0;
        float remainWidth = tableWidth;
        CellRenderer[] firtsRow;
        if (tableRenderer.headerRenderer != null && tableRenderer.headerRenderer.rows.size() > 0) {
            firtsRow = tableRenderer.headerRenderer.rows.get(0);
        } else if (tableRenderer.rows.size() > 0 && getTable().isComplete() && 0 == getTable().getLastRowBottomBorder().size()) {
            firtsRow = tableRenderer.rows.get(0);
        } else {
            //most likely it is large table
            firtsRow = null;
        }

        if (firtsRow != null && getTable().isComplete() && 0 == getTable().getLastRowBottomBorder().size()) { // only for not large tables
            for (int i = 0; i < numberOfColumns; i++) {
                if (columnWidths[i] == -1) {
                    CellRenderer cell = firtsRow[i];
                    if (cell != null) {
                        UnitValue cellWidth = getCellWidth(cell, true);
                        if (cellWidth != null) {
                            assert cellWidth.getValue() >= 0;
                            float width = cellWidth.isPercentValue()
                                    ? tableWidth * cellWidth.getValue() / 100
                                    : cellWidth.getValue();
                            int colspan = ((Cell) cell.getModelElement()).getColspan();
                            for (int j = 0; j < colspan; j++) {
                                columnWidths[i + j] = width / colspan;
                            }
                            remainWidth -= columnWidths[i];
                            processedColumns++;
                        }
                    }
                } else {
                    remainWidth -= columnWidths[i];
                    processedColumns++;
                }
            }
        } else {
            for (int i = 0; i < numberOfColumns; i++) {
                if (columnWidths[i] != -1) {
                    processedColumns++;
                    remainWidth -= columnWidths[i];
                }
            }
        }

        if (remainWidth > 0) {
            if (numberOfColumns == processedColumns) {
                //Set remaining width to all columns.
                for (int i = 0; i < numberOfColumns; i++) {
                    columnWidths[i] = tableWidth * columnWidths[i] / (tableWidth - remainWidth);
                }
            } else {
                // Set remaining width to the unprocessed columns.
                for (int i = 0; i < numberOfColumns; i++) {
                    if (columnWidths[i] == -1) {
                        columnWidths[i] = remainWidth / (numberOfColumns - processedColumns);
                    }
                }
            }
        } else if (numberOfColumns != processedColumns) {
//            Logger logger = LoggerFactory.getLogger(TableWidths.class);
//            logger.warn(LogMessageConstant.SUM_OF_TABLE_COLUMNS_IS_GREATER_THAN_TABLE_WIDTH);
            for (int i = 0; i < numberOfColumns; i++) {
                if (columnWidths[i] == -1) {
                    columnWidths[i] = 0;
                }
            }
        }

        if (tableRenderer.bordersHandler instanceof SeparatedTableBorders) {
            for (int i = 0; i < numberOfColumns; i++) {
                columnWidths[i] += horizontalBorderSpacing;
            }
        }
        return columnWidths;
    }

    //region Common methods

    private void calculateTableWidth(float availableWidth, boolean calculateTableMaxWidth) {
        fixedTableLayout = "fixed".equals(tableRenderer
                .<String>getProperty(Property.TABLE_LAYOUT, "auto").toLowerCase());
        UnitValue width = tableRenderer.<UnitValue>getProperty(Property.WIDTH);
        if (fixedTableLayout && width != null && width.getValue() >= 0) {
            if (0 != getTable().getLastRowBottomBorder().size()) {
                width = getTable().getWidth();
            } else if (!getTable().isComplete() && null != getTable().getWidth() && getTable().getWidth().isPercentValue()) {
                getTable().setWidth((float) tableRenderer.retrieveUnitValue(availableWidth, Property.WIDTH));
            }
            fixedTableWidth = true;
            tableWidth = (float) retrieveTableWidth(width, availableWidth);
            layoutMinWidth = width.isPercentValue() ? 0 : tableWidth;
        } else {
            fixedTableLayout = false;
            //min width will initialize later
            layoutMinWidth = -1;
            if (calculateTableMaxWidth) {
                fixedTableWidth = false;
                tableWidth = retrieveTableWidth(availableWidth);
            } else if (width != null && width.getValue() >= 0) {
                fixedTableWidth = true;
                tableWidth = (float) retrieveTableWidth(width, availableWidth);
            } else {
                fixedTableWidth = false;
                tableWidth = retrieveTableWidth(availableWidth);
            }
        }
        Float min = retrieveTableWidth(tableRenderer.<UnitValue>getProperty(Property.MIN_WIDTH), availableWidth);
        Float max = retrieveTableWidth(tableRenderer.<UnitValue>getProperty(Property.MAX_WIDTH), availableWidth);

        tableMinWidth = min != null ? (float) min : layoutMinWidth;
        tableMaxWidth = max != null ? (float) max : tableWidth;

        if (tableMinWidth > tableMaxWidth)
            tableMaxWidth = tableMinWidth;

        if (tableMinWidth > tableWidth)
            tableWidth = tableMinWidth;

        if (tableMaxWidth < tableWidth)
            tableWidth = tableMaxWidth;
    }

    private Float retrieveTableWidth(UnitValue width, float availableWidth) {
        if (width == null) return null;
        return retrieveTableWidth(width.isPercentValue()
                ? width.getValue() * availableWidth / 100
                : width.getValue());
    }

    private float retrieveTableWidth(float width) {
        if (BorderCollapsePropertyValue.SEPARATE.equals(tableRenderer.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE))) {
            width -= (rightBorderMaxWidth + leftBorderMaxWidth);
            width -= (numberOfColumns + 1) * horizontalBorderSpacing;
        } else {
            width -= (rightBorderMaxWidth + leftBorderMaxWidth) / 2;
        }
        return Math.max(width, 0);
    }

    private Table getTable() {
        return (Table) tableRenderer.getModelElement();
    }

    //endregion

    //region Auto layout utils

    private void calculateMinMaxWidths() {
        float[] minWidths = new float[numberOfColumns];
        float[] maxWidths = new float[numberOfColumns];

        for (CellInfo cell : cells) {
            cell.setParent(tableRenderer);
            MinMaxWidth minMax = cell.getCell().getMinMaxWidth();
            float[] indents = getCellBorderIndents(cell);
            if (BorderCollapsePropertyValue.SEPARATE.equals(tableRenderer.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE))) {
                minMax.setAdditionalWidth((float) (minMax.getAdditionalWidth() - horizontalBorderSpacing));
            } else {
                minMax.setAdditionalWidth(minMax.getAdditionalWidth() + indents[1] / 2 + indents[3] / 2);
            }

            if (cell.getColspan() == 1) {
                minWidths[cell.getCol()] = Math.max(minMax.getMinWidth(), minWidths[cell.getCol()]);
                maxWidths[cell.getCol()] = Math.max(minMax.getMaxWidth(), maxWidths[cell.getCol()]);
            } else {
                float remainMin = minMax.getMinWidth();
                float remainMax = minMax.getMaxWidth();
                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                    remainMin -= minWidths[i];
                    remainMax -= maxWidths[i];
                }
                if (remainMin > 0) {
                    for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                        minWidths[i] += remainMin / cell.getColspan();
                    }
                }
                if (remainMax > 0) {
                    for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                        maxWidths[i] += remainMax / cell.getColspan();
                    }
                }
            }
        }

        for (int i = 0; i < widths.length; i++) {
            widths[i] = new ColumnWidthData(minWidths[i], maxWidths[i]);
        }
    }

    private float[] getCellBorderIndents(CellInfo cell) {
        TableRenderer renderer;
        if (cell.region == CellInfo.HEADER) {
            renderer = tableRenderer.headerRenderer;
        } else if (cell.region == CellInfo.FOOTER) {
            renderer = tableRenderer.footerRenderer;
        } else {
            renderer = tableRenderer;
        }
        return renderer.bordersHandler.getCellBorderIndents(cell.getRow(), cell.getCol(), cell.getRowspan(), cell.getColspan());
    }

    private void fillAndSortCells() {
        cells = new ArrayList<>();
        if (tableRenderer.headerRenderer != null) {
            fillRendererCells(tableRenderer.headerRenderer, CellInfo.HEADER);
        }
        fillRendererCells(tableRenderer, CellInfo.BODY);
        if (tableRenderer.footerRenderer != null) {
            fillRendererCells(tableRenderer.footerRenderer, CellInfo.FOOTER);
        }
        // Cells are sorted, because we need to process cells without colspan
        // and process from top left to bottom right for other cases.
        Collections.sort(cells);
    }

    private void fillRendererCells(TableRenderer renderer, byte region) {
        for (int row = 0; row < renderer.rows.size(); row++) {
            for (int col = 0; col < numberOfColumns; col++) {
                CellRenderer cell = renderer.rows.get(row)[col];
                if (cell != null) {
                    cells.add(new CellInfo(cell, row, col, region));
                }
            }
        }
    }

    private void warn100percent() {
        Logger logger = LoggerFactory.getLogger(TableWidths.class);
        logger.warn(LogMessageConstant.SUM_OF_TABLE_COLUMNS_IS_GREATER_THAN_100);
    }

    private float[] extractWidths() {
        float actualWidth = 0;
        layoutMinWidth = 0;
        float[] columnWidths = new float[widths.length];
        for (int i = 0; i < widths.length; i++) {
            assert widths[i].finalWidth >= 0;
            columnWidths[i] = widths[i].finalWidth + horizontalBorderSpacing;
            actualWidth += widths[i].finalWidth;
            layoutMinWidth += widths[i].min + horizontalBorderSpacing;
        }
        if (actualWidth > tableWidth + MinMaxWidthUtils.getEps() * widths.length) {
            Logger logger = LoggerFactory.getLogger(TableWidths.class);
            logger.warn(LogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH);
        }
        return columnWidths;
    }

    //endregion

    //region Internal classes

    @Override
    public String toString() {
        return "width=" + tableWidth + (fixedTableWidth ? "!!" : "");
    }

    private static class ColumnWidthData {
        final float min;
        float max;
        float width = 0;
        float finalWidth = -1;
        boolean isPercent = false;
        //true means that this column has cell property based width.
        boolean isFixed = false;

        ColumnWidthData(float min, float max) {
            assert min >= 0;
            assert max >= 0;
            this.min = min > 0 ? min + MinMaxWidthUtils.getEps() : 0;
            // All browsers implement a size limit on the cell's max width.
            // This limit is based on KHTML's representation that used 16 bits widths.
            this.max = max > 0 ? Math.min(max + MinMaxWidthUtils.getEps(), 32760) : 0;
        }

        ColumnWidthData setPoints(float width) {
            assert !isPercent;
            assert this.min <= width;
            this.width = Math.max(this.width, width);
            return this;
        }

        ColumnWidthData resetPoints(float width) {
            assert this.min <= width;
            this.width = width;
            this.isPercent = false;
            return this;
        }

        ColumnWidthData addPoints(float width) {
            assert !isPercent;
            this.width += width;
            return this;
        }

        ColumnWidthData setPercents(float percent) {
            if (isPercent) {
                width = Math.max(width, percent);
            } else {
                isPercent = true;
                width = percent;
            }
            isFixed = false;
            return this;
        }

        ColumnWidthData addPercents(float width) {
            assert isPercent;
            this.width += width;
            return this;
        }

        ColumnWidthData setFixed(boolean fixed) {
            this.isFixed = fixed;
            return this;
        }

        boolean isFlexible() {
            return !this.isFixed && !this.isPercent;
        }

        @Override
        public String toString() {
            return "w=" + width +
                    (isPercent ? "%" : "pt") +
                    (isFixed ? " !!" : "") +
                    ", min=" + min +
                    ", max=" + max +
                    ", finalWidth=" + finalWidth;
        }
    }

    static private final UnitValue ZeroWidth = UnitValue.createPointValue(0);

    private UnitValue getCellWidth(CellRenderer cell, boolean zeroIsValid) {
        UnitValue widthValue = cell.<UnitValue>getProperty(Property.WIDTH);
        //zero has special meaning in fixed layout, we shall not add padding to zero value
        if (widthValue == null || widthValue.getValue() < 0) {
            return null;
        } else if (widthValue.getValue() == 0) {
            return zeroIsValid ? ZeroWidth : null;
        } else if (widthValue.isPercentValue()) {
            return widthValue;
        } else {
            widthValue = resolveMinMaxCollision(cell, widthValue);
            if (!AbstractRenderer.isBorderBoxSizing(cell)) {
                Border[] borders = cell.getBorders();
                if (borders[1] != null) {
                    widthValue.setValue(widthValue.getValue() +
                            ((tableRenderer.bordersHandler instanceof SeparatedTableBorders)
                                    ? borders[1].getWidth()
                                    : borders[1].getWidth() / 2));
                }
                if (borders[3] != null) {
                    widthValue.setValue(widthValue.getValue() +
                            ((tableRenderer.bordersHandler instanceof SeparatedTableBorders)
                                    ? borders[3].getWidth()
                                    : borders[3].getWidth() / 2));
                }
                UnitValue[] paddings = cell.getPaddings();
                if (!paddings[1].isPointValue()) {
                    Logger logger = LoggerFactory.getLogger(TableWidths.class);
                    logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_LEFT));
                }
                if (!paddings[3].isPointValue()) {
                    Logger logger = LoggerFactory.getLogger(TableWidths.class);
                    logger.error(MessageFormatUtil.format(LogMessageConstant.PROPERTY_IN_PERCENTS_NOT_SUPPORTED, Property.PADDING_RIGHT));
                }
                widthValue.setValue(widthValue.getValue() + paddings[1].getValue() + paddings[3].getValue());
            }
            return widthValue;
        }
    }

    private UnitValue resolveMinMaxCollision(CellRenderer cell, UnitValue widthValue) {
        assert widthValue.isPointValue();

        UnitValue minWidthValue = cell.<UnitValue>getProperty(Property.MIN_WIDTH);
        if (minWidthValue != null && minWidthValue.isPointValue()
                && minWidthValue.getValue() > widthValue.getValue()) {
            return minWidthValue;
        }
        UnitValue maxWidthValue = cell.<UnitValue>getProperty(Property.MAX_WIDTH);
        if (maxWidthValue != null && maxWidthValue.isPointValue()
                && maxWidthValue.getValue() < widthValue.getValue()) {
            return maxWidthValue;
        }
        return widthValue;
    }

    private static class CellInfo implements Comparable<CellInfo> {
        static final byte HEADER = 1;
        static final byte BODY = 2;
        static final byte FOOTER = 3;

        private final CellRenderer cell;
        private final int row;
        private final int col;
        final byte region;

        CellInfo(CellRenderer cell, int row, int col, byte region) {
            this.cell = cell;
            this.region = region;
            //we cannot use getModelElement().getCol() or getRow(), because its may be changed during layout.
            this.row = row;
            this.col = col;
        }

        CellRenderer getCell() {
            return cell;
        }

        int getCol() {
            return col;
        }

        int getColspan() {
            //we cannot use getModelElement().getColspan(), because it may be changed during layout.
            return (int) cell.getPropertyAsInteger(Property.COLSPAN);
        }

        int getRow() {
            return row;
        }

        int getRowspan() {
            //we cannot use getModelElement().getRowspan(), because it may be changed during layout.
            return (int) cell.getPropertyAsInteger(Property.ROWSPAN);
        }

        @Override
        public int compareTo(CellInfo o) {
            if (getColspan() == 1 ^ o.getColspan() == 1) {
                return getColspan() - o.getColspan();
            }
            if (region == o.region && getRow() == o.getRow()) {
                return getCol() + getColspan() - o.getCol() - o.getColspan();
            }
            return region == o.region ? getRow() - o.getRow() : region - o.region;
        }

        @Override
        public String toString() {
            String str = MessageFormatUtil.format("row={0}, col={1}, rowspan={2}, colspan={3}, ",
                    getRow(), getCol(), getRowspan(), getColspan());
            if (region == HEADER) {
                str += "header";
            } else if (region == BODY) {
                str += "body";
            } else if (region == FOOTER) {
                str += "footer";
            }
            return str;
        }

        public void setParent(TableRenderer tableRenderer) {
            if (region == HEADER) {
                cell.setParent(tableRenderer.headerRenderer);
            } else if (region == FOOTER) {
                cell.setParent(tableRenderer.footerRenderer);
            } else {
                cell.setParent(tableRenderer);
            }
        }
    }

    //endregion
}
