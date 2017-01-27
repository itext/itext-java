package com.itextpdf.layout.renderer;

import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;

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
                                percentSum += widths[i].getPercent();
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
                    if (!widths[i].isFixed) {
                        remainWidth += widths[i].max - widths[i].width;
                        flexibleCols++;
                    }
                }
                if (remainWidth > 0) {
                    if (flexibleCols > 0) {
                        for (int i = cell.getCol(); i < cell.getCol() + cell.getColspan(); i++) {
                            if (!widths[i].isFixed) {
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
                    widths[i].setPercents(colWidth.getValue());
                } else if (!widths[i].isPercent && colWidth.getValue() >= widths[i].min) {
                    if (!widths[i].isFixed) {
                        widths[i].resetPoints(colWidth.getValue());
                    } else {
                        widths[i].setPoints(colWidth.getValue());
                    }
                }
            }
        }
        //endregion

        // region recalculate
        if (Math.abs(tableWidth - minSum) < MinMaxWidthUtils.getEps()) {
            for (int i = 0; i < numberOfColumns; i++) {
                widths[i].finalWidth = widths[i].min;
            }
        } else {
            float sumOfPercents = 0;
            // minTableWidth included fixed columns.
            float minTableWidth = 0;
            float totalNonPercent = 0;

            for (int i = 0; i < widths.length; i++) {
                if (widths[i].isPercent) {
                    //some magic...
                    if (sumOfPercents < 100 && sumOfPercents + widths[i].width >= 100) {
                        widths[i].width -= sumOfPercents + widths[i].width - 100;
                    } else if (sumOfPercents >= 100) {
                        widths[i].resetPoints(widths[i].min);
                        minTableWidth += widths[i].width;
                    }
                    sumOfPercents += widths[i].width;
                } else {
                    minTableWidth += widths[i].min;
                    totalNonPercent += widths[i].width;
                }
            }

            if (sumOfPercents >= 100) {
                sumOfPercents = 100;
                float remainingWidth = tableWidth - minTableWidth;
                boolean recalculatePercents = false;
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
                //hasExtraSpace means that we have some extra space and(!) may extend columns.
                //columns shouldn't be more than its max value or its percentage value.
                boolean hasExtraSpace = true;
                if (unspecifiedTableWidth) {
                    float tableWidthBasedOnPercents = totalNonPercent * 100 / (100 - sumOfPercents);
                    for (int i = 0; i < numberOfColumns; i++) {
                        if (widths[i].isPercent) {
                            tableWidthBasedOnPercents = Math.max(widths[i].min * 100 / widths[i].width, tableWidthBasedOnPercents);
                        }
                    }
                    if (tableWidthBasedOnPercents <= tableWidth) {
                        for (int i = 0; i < numberOfColumns; i++) {
                            widths[i].finalWidth = widths[i].isPercent
                                    ? tableWidthBasedOnPercents * widths[i].width / 100
                                    : widths[i].width;
                        }
                        //we don't need more space, columns are done.
                        hasExtraSpace = false;
                    }
                }

                //need to decrease some column.
                if (hasExtraSpace) {
                    // opposite to sumOfPercents, which is sum of percent values.
                    float totalPercent = 0;
                    //if didn't sum columns with percent in case sumOfPercents > 100, recalculating needed.
                    totalNonPercent = 0;
                    float minTotalNonPercent = 0;
                    //sum of non fixed non percent columns.
                    float totalFlexible = 0;
                    boolean recalculatePercents = false;
                    for (int i = 0; i < numberOfColumns; i++) {
                        if (widths[i].isPercent) {
                            if (tableWidth * widths[i].width >= widths[i].min) {
                                widths[i].finalWidth = tableWidth * widths[i].width / 100;
                                totalPercent += widths[i].finalWidth;
                            } else {
                                sumOfPercents -= widths[i].width;
                                widths[i].resetPoints(widths[i].min);
                                widths[i].finalWidth = widths[i].min;
                                totalNonPercent += widths[i].min;
                                minTotalNonPercent += widths[i].min;
                                totalFlexible += widths[i].min;
                                recalculatePercents = true;
                            }
                        } else {
                            widths[i].finalWidth = widths[i].min;
                            totalNonPercent += widths[i].width;
                            minTotalNonPercent += widths[i].min;
                            if (!widths[i].isFixed) totalFlexible += widths[i].width;
                        }
                    }
                    // collision between minWidth and percent value.
                    if (recalculatePercents) {
                        if (totalPercent + minTotalNonPercent > tableWidth) {
                            float extraWidth = tableWidth - minTotalNonPercent;
                            if (sumOfPercents > 0) {
                                for (int i = 0; i < numberOfColumns; i++) {
                                    if (widths[i].isPercent) {
                                        widths[i].finalWidth = extraWidth * widths[i].width / sumOfPercents;
                                    }
                                }
                            }
                            //we already use more than we have.
                            hasExtraSpace = false;
                        }
                    }
                    // still has some free space.
                    if (hasExtraSpace) {
                        float extraWidth = tableWidth - minTotalNonPercent - totalPercent;
                        if (totalNonPercent > extraWidth + MinMaxWidthUtils.getEps()) {
                            float remainingPercentageWidth = totalNonPercent - minTotalNonPercent;
                            if (remainingPercentageWidth > 0) {
                                for (int i = 0; i < numberOfColumns; i++) {
                                    if (!widths[i].isPercent) {
                                        float addition = widths[i].width - widths[i].min;
                                        widths[i].finalWidth = widths[i].min
                                                + addition * extraWidth / remainingPercentageWidth;
                                    }
                                }
                            }
                        } else if (totalNonPercent == 0) {
                            if (totalPercent > 0) {
                                for (int i = 0; i < numberOfColumns; i++) {
                                    widths[i].finalWidth += extraWidth * widths[i].finalWidth / totalPercent;
                                }
                            }
                        } else if (totalFlexible == 0) {
                            float addition = extraWidth - totalNonPercent;
                            for (int i = 0; i < numberOfColumns; i++) {
                                if (!widths[i].isPercent) {
                                    widths[i].finalWidth += widths[i].width + addition * widths[i].width / totalNonPercent;
                                }
                            }
                        } else {
                            float addition = extraWidth - totalNonPercent;
                            for (int i = 0; i < numberOfColumns; i++) {
                                if (!widths[i].isPercent && !widths[i].isFixed) {
                                    widths[i].finalWidth += widths[i].width + addition * widths[i].width / totalFlexible;
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

    //region Auto layout

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

    private float[] extractWidths() {
        float[] columnWidths = new float[widths.length];
        for (int i = 0; i < widths.length; i++) {
            assert widths[i].finalWidth >= 0;
            columnWidths[i] = widths[i].finalWidth;
        }
        return columnWidths;
    }

    //endregion

    //region Internal classes

    private static class ColumnWidthData {
        final float min;
        final float max;
        float width = 0;
        float finalWidth = -1;
        boolean isPercent = false;
        //true means that this column has cell property based width.
        boolean isFixed = false;

        ColumnWidthData(float min, float max) {
            this.min = min > 0 ? min + MinMaxWidthUtils.getEps() : 0;
            // All browsers implement a size limit on the cell's max width.
            // This limit is based on KHTML's representation that used 16 bits widths.
            this.max = max > 0 ? Math.min(max + MinMaxWidthUtils.getEps(), 32760) : 0;
        }

        /**
         * Gets percents based on 1.
         */
        public float getPercent() {
            return width;
        }

        public ColumnWidthData setPoints(float width) {
            assert !isPercent;
            this.width = Math.max(this.width, width);
            return this;
        }

        public ColumnWidthData resetPoints(float width) {
            this.width = width;
            this.isPercent = false;
            return this;
        }

        public ColumnWidthData addPoints(float width) {
            assert !isPercent;
            this.width += width;
            return this;
        }

        public ColumnWidthData setPercents(float percent) {
            if (isPercent) {
                width = Math.max(width, percent);
            } else {
                isPercent = true;
                width = percent;
            }
            return this;
        }

        public ColumnWidthData addPercents(float width) {
            assert isPercent;
            this.width += width;
            return this;
        }

        public ColumnWidthData setFixed(boolean fixed) {
            this.isFixed = fixed;
            return this;
        }

        /**
         * Check collusion between min value and point width
         *
         * @return true, if {@link #min} greater than {@link #width}.
         */
        public boolean hasCollision() {
            assert !isPercent;
            return min > width;
        }

        /**
         * Check collusion between min value and available point width.
         *
         * @param availableWidth additional available point width.
         * @return true, if {@link #min} greater than ({@link #width} + additionalWidth).
         */
        public boolean checkCollision(float availableWidth) {
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
