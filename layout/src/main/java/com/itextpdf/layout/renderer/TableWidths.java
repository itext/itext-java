package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

final class TableWidths {

    private TableRenderer tableRenderer;
    private int numberOfColumns;
    private float[] collapsedTableBorders;
    private ColumnWidthData[] widths;
    private List<CellInfo> cells;

    private float tableWidth;
    private boolean unspecifiedTableWidth;

    TableWidths(TableRenderer tableRenderer, float availableWidth, float[] collapsedTableBorders) {
        this.tableRenderer = tableRenderer;
        numberOfColumns = ((Table) tableRenderer.getModelElement()).getNumberOfColumns();
        this.collapsedTableBorders = collapsedTableBorders != null ? collapsedTableBorders : new float[]{0, 0, 0, 0};
        calculateTableWidth(availableWidth);
    }

    boolean hasFixedLayout() {
        if (unspecifiedTableWidth) {
            return false;
        } else {
            String layout = tableRenderer.<String>getProperty(Property.TABLE_LAYOUT, "auto");
            return "fixed".equals(layout.toLowerCase());
        }
    }

    float[] autoLayout(float[] minWidths, float[] maxWidths) {
        fillWidths(minWidths, maxWidths);
        fillAndSortCells();

        float minSum = 0;
        for (ColumnWidthData width : widths) minSum += width.min;

        //region Process cells

        HashSet<Integer> minColumns = new HashSet<>(numberOfColumns);
        for (CellInfo cell : cells) {
            //NOTE in automatic layout algorithm percents have higher priority
            UnitValue cellWidth = cell.getWidth();
            if (cellWidth != null && cellWidth.getValue() >= 0) {
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
                                        widths[i].setPercents(percentAddition / pointColumns).setFixed(true);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    //cellWidth has point value
                    if (cell.getCol() == 1) {
                        if (!widths[cell.getCol()].isPercent) {
                            widths[cell.getCol()].setPoints(cellWidth.getValue()).setFixed(true);
                            if (widths[cell.getCol()].hasCollision()) {
                                minColumns.add(cell.getCol());
                            }
                        }
                    } else {
                        int flexibleCols = 0;
                        float colspanRemain = cellWidth.getValue();
                        for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                            if (!widths[i].isPercent) {
                                colspanRemain -= widths[i].width;
                                if (!widths[i].isFixed) flexibleCols++;
                            } else {
                                colspanRemain = -1;
                                break;
                            }
                        }
                        if (colspanRemain > 0) {
                            if (flexibleCols > 0) {
                                // check min width in columns
                                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                                    if (!widths[i].isFixed && widths[i].checkCollision(colspanRemain / flexibleCols)) {
                                        widths[i].setPoints(widths[i].min).setFixed(true);
                                        if ((colspanRemain -= widths[i].min) <= 0 || flexibleCols-- <= 0) {
                                            break;
                                        }
                                    }
                                }
                                if (colspanRemain > 0 && flexibleCols > 0) {
                                    for (int k = cell.getCol(); k < cell.getCol() + cell.getColspan(); k++) {
                                        if (!widths[k].isFixed) {
                                            widths[k].addPoints(colspanRemain / flexibleCols).setFixed(true);
                                        }
                                    }
                                }
                            } else {
                                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                                    widths[i].addPoints(colspanRemain / cell.getColspan());
                                }
                            }
                        }
                    }
                }
            } else if (!widths[cell.getCol()].isFixed) {
                int flexibleCols = 0;
                float remainWidth = 0;
                //if there is no information, try to set max width
                for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                    if (!widths[i].isFixed && !widths[i].isPercent) {
                        remainWidth += widths[i].max - widths[i].width;
                        flexibleCols++;
                    }
                }
                if (remainWidth > 0) {
                    if (flexibleCols > 0) {
                        for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                            if (!widths[i].isFixed && !widths[i].isPercent) {
                                widths[i].addPoints(remainWidth / flexibleCols);
                            }
                        }
                    } else {
                        for (int k = cell.getCol(); k < cell.getCol() + cell.getColspan(); k++) {
                            widths[k].addPoints(remainWidth / cell.getColspan());
                        }
                    }
                }
            }
        }
        for (Integer col : minColumns) {
            if (!widths[col].isPercent && widths[col].isFixed && widths[col].hasCollision()) {
                minSum += widths[col].min - widths[col].width;
                widths[col].setPoints(widths[col].min);
            }
        }

        //endregion

        //region Process columns
        //TODO add colgroup information.
        for (int i = 0; i < numberOfColumns; i++) {
            UnitValue colWidth = getTable().getColumnWidth(i);
            if (colWidth.getValue() >= 0) {
                if (colWidth.isPercentValue()) {
                    if (!widths[i].isPercent && widths[i].isFixed && widths[i].width > widths[i].min) {
                        widths[i].max = widths[i].width;
                        widths[i].setFixed(false);
                    }
                    if (!widths[i].isPercent) {
                        widths[i].setPercents(colWidth.getValue());
                    }

                } else if (!widths[i].isPercent && colWidth.getValue() >= widths[i].min) {
                    if (widths[i].isFixed) {
                        widths[i].setPoints(colWidth.getValue());
                    } else {
                        widths[i].resetPoints(colWidth.getValue());
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
                        widths[i].width -= sumOfPercents + widths[i].width - 100;
                        sumOfPercents += widths[i].width;
                        warn100percent();
                    } else if (sumOfPercents >= 100) {
                        widths[i].resetPoints(widths[i].min);
                        minTableWidth += widths[i].width;
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
            if (unspecifiedTableWidth) {
                float tableWidthBasedOnPercents = sumOfPercents < 100
                        ? totalNonPercent * 100 / (100 - sumOfPercents) : 0;
                for (int i = 0; i < numberOfColumns; i++) {
                    if (widths[i].isPercent) {
                        tableWidthBasedOnPercents = Math.max(widths[i].max * 100 / widths[i].width, tableWidthBasedOnPercents);
                    }
                }
                if (tableWidthBasedOnPercents <= tableWidth) {
                    tableWidth = tableWidthBasedOnPercents;
                    //we don't need more space, columns are done.
                    toBalance = false;
                }
            }

            if (sumOfPercents < 100 && totalNonPercent == 0) {
                // each column has percent value but sum < 100%
                // upscale percents
                for (int i = 0; i < widths.length; i++) {
                    widths[i].width = 100 * widths[i].width / sumOfPercents;
                }
                sumOfPercents = 100;
            }

            if (!toBalance) {
                for (int i = 0; i < numberOfColumns; i++) {
                    widths[i].finalWidth = widths[i].isPercent
                            ? tableWidth * widths[i].width / 100
                            : widths[i].width;
                }
            } else if (sumOfPercents >= 100) {
                sumOfPercents = 100;
                boolean recalculatePercents = false;
                float remainingWidth = tableWidth - minTableWidth;
                for (int i = 0; i < numberOfColumns; i++) {
                    if (widths[i].isPercent) {
                        if (remainingWidth * widths[i].width >= widths[i].min) {
                            widths[i].finalWidth = remainingWidth * widths[i].width / 100;
                        } else {
                            widths[i].finalWidth = widths[i].min;
                            widths[i].isPercent = false;
                            remainingWidth -= widths[i].min;
                            sumOfPercents -= widths[i].width;
                            recalculatePercents = true;
                        }
                    }
                }
                if (recalculatePercents) {
                    for (int i = 0; i < numberOfColumns; i++) {
                        if (widths[i].isPercent) {
                            widths[i].finalWidth = remainingWidth * widths[i].width / sumOfPercents;
                        }
                    }
                }
            } else {
                // We either have some extra space and may extend columns in case fixed table width,
                // or have to decrease columns to fit table width.
                //
                // columns shouldn't be more than its max value in case unspecified table width.
                //columns shouldn't be more than its percentage value.

                // opposite to sumOfPercents, which is sum of percent values.
                float totalPercent = 0;
                float minTotalNonPercent = 0;
                float fixedAddition = 0;
                float flexibleAddition = 0;
                //sum of non fixed non percent columns.
                for (int i = 0; i < numberOfColumns; i++) {
                    if (widths[i].isPercent) {
                        if (tableWidth * widths[i].width >= widths[i].min) {
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
                    if (fixedAddition > 0 && (extraWidth < fixedAddition || flexibleAddition == 0)) {
                        for (int i = 0; i < numberOfColumns; i++) {
                            if (!widths[i].isPercent && widths[i].isFixed) {
                                widths[i].finalWidth += (widths[i].width - widths[i].min) * extraWidth / fixedAddition;
                            }
                        }
                    } else {
                        extraWidth -= fixedAddition;
                        if (extraWidth < flexibleAddition) {
                            for (int i = 0; i < numberOfColumns; i++) {
                                if (!widths[i].isPercent) {
                                    if (widths[i].isFixed) {
                                        widths[i].finalWidth = widths[i].width;
                                    } else {
                                        widths[i].finalWidth += (widths[i].width - widths[i].min) * extraWidth / flexibleAddition;
                                    }
                                }
                            }
                        } else {
                            float totalFixed = 0;
                            float totalFlexible = 0;
                            for (int i = 0; i < numberOfColumns; i++) {
                                if (!widths[i].isPercent) {
                                    if (widths[i].isFixed) {
                                        widths[i].finalWidth = widths[i].width;
                                        totalFixed += widths[i].width;
                                    } else {
                                        totalFlexible += widths[i].width;
                                    }
                                }
                            }
                            extraWidth = tableWidth - totalPercent - totalFixed;
                            for (int i = 0; i < numberOfColumns; i++) {
                                if (!widths[i].isPercent && !widths[i].isFixed) {
                                    widths[i].finalWidth = widths[i].width * extraWidth / totalFlexible;
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
        for (int i = 0; i < numberOfColumns; i++) {
            if (columnWidths[i] == -1) {
                CellRenderer cell = tableRenderer.rows.get(0)[i];
                if (cell != null) {
                    Float cellWidth = cell.retrieveUnitValue(tableWidth, Property.WIDTH);
                    if (cellWidth != null && cellWidth >= 0) {
                        int colspan = cell.getModelElement().getColspan();
                        for (int j = 0; j < colspan; j++) {
                            columnWidths[i + j] = cellWidth / colspan;
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

        if (remainWidth > 0) {
            if (numberOfColumns == processedColumns) {
                //Set remainWidth to all columns.
                for (int i = 0; i < numberOfColumns; i++) {
                    columnWidths[i] += remainWidth / numberOfColumns;
                }
            } else {
                // Set all remain width to the unprocessed columns.
                for (int i = 0; i < numberOfColumns; i++) {
                    if (columnWidths[i] == -1) {
                        columnWidths[i] = remainWidth / (numberOfColumns - processedColumns);
                    }
                }
            }
        } else if (numberOfColumns != processedColumns) {
            //TODO shall we add warning?
            for (int i = 0; i < numberOfColumns; i++) {
                if (columnWidths[i] == -1) {
                    columnWidths[i] = 0;
                }
            }
        }

        return columnWidths;
    }

    //region Common methods

    private void calculateTableWidth(float availableWidth) {
        Float originalTableWidth = tableRenderer.retrieveUnitValue(availableWidth, Property.WIDTH);
        if (originalTableWidth == null || Float.isNaN(originalTableWidth) || originalTableWidth <= 0) {
            tableWidth = availableWidth;
            unspecifiedTableWidth = true;
        } else {
            tableWidth = originalTableWidth < availableWidth ? originalTableWidth : availableWidth;
            unspecifiedTableWidth = false;
        }
        tableWidth -= getMaxLeftBorder() / 2 + getMaxRightBorder() / 2;
    }

    private float getMaxLeftBorder() {
        return collapsedTableBorders[3];
    }

    private float getMaxRightBorder() {
        return collapsedTableBorders[1];
    }

    private Table getTable() {
        return (Table) tableRenderer.getModelElement();
    }

    //endregion

    //region Auto layout utils

    private void fillWidths(float[] minWidths, float[] maxWidths) {
        widths = new ColumnWidthData[minWidths.length];
        for (int i = 0; i < widths.length; i++) {
            widths[i] = new ColumnWidthData(minWidths[i], maxWidths[i]);
        }
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
                    cells.add(new CellInfo(cell, region));
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
        float[] columnWidths = new float[widths.length];
        for (int i = 0; i < widths.length; i++) {
            assert widths[i].finalWidth >= 0;
            columnWidths[i] = widths[i].finalWidth;
            actualWidth += widths[i].finalWidth;
        }
        if (actualWidth > tableWidth + MinMaxWidthUtils.getEps()*widths.length) {
            Logger logger = LoggerFactory.getLogger(TableWidths.class);
            logger.warn(LogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH);
        }
        return columnWidths;
    }

    //endregion

    //region Internal classes

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
            this.width = Math.max(this.width, width);
            return this;
        }

        ColumnWidthData resetPoints(float width) {
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

        /**
         * Check collusion between min value and point width
         *
         * @return true, if {@link #min} greater than {@link #width}.
         */
        boolean hasCollision() {
            assert !isPercent;
            return min > width;
        }

        /**
         * Check collusion between min value and available point width.
         *
         * @param availableWidth additional available point width.
         * @return true, if {@link #min} greater than ({@link #width} + additionalWidth).
         */
        boolean checkCollision(float availableWidth) {
            assert !isPercent;
            return min > width + availableWidth;
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

    private static class CellInfo implements Comparable<CellInfo> {
        private static final byte HEADER = 1;
        private static final byte BODY = 2;
        private static final byte FOOTER = 3;

        private CellRenderer cell;
        private byte region;

        CellInfo(CellRenderer cell, byte region) {
            this.cell = cell;
            this.region = region;
        }

        CellRenderer getCell() {
            return cell;
        }

        int getCol() {
            return cell.getModelElement().getCol();
        }

        int getColspan() {
            return cell.getModelElement().getColspan();
        }

        int getRow() {
            return cell.getModelElement().getRow();
        }

        int getRowspan() {
            return cell.getModelElement().getRowspan();
        }

        UnitValue getWidth() {
            return cell.<UnitValue>getProperty(Property.WIDTH);
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
    }

    //endregion

    @Override
    public String toString() {
        return "width=" + tableWidth + (unspecifiedTableWidth ? "" : "!!");
    }
}
