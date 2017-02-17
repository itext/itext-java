package com.itextpdf.layout.renderer;


import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Property;

import java.util.ArrayList;
import java.util.List;

public abstract class TableBorders {
    protected List<List<Border>> horizontalBorders;
    protected List<List<Border>> verticalBorders;

    protected final int numberOfColumns;

    protected Border[] tableBoundingBorders;
    protected List<CellRenderer[]> rows; // TODO Maybe Table ?

    protected Table.RowRange rowRange;

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns) {
        this.rows = rows;
        this.numberOfColumns = numberOfColumns;
        verticalBorders = new ArrayList<>();
        horizontalBorders = new ArrayList<>();
        tableBoundingBorders = new Border[4];
    }

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders) {
        this(rows, numberOfColumns);
        setTableBoundingBorders(tableBoundingBorders);
    }

    protected void initializeBorders(List<Border> lastFlushedRowBottomBorder, boolean isFirstOnPage) {
        List<Border> tempBorders;
        // initialize vertical borders
        if (0 != rows.size()) {
            while (numberOfColumns + 1 > verticalBorders.size()) {
                tempBorders = new ArrayList<Border>();
                while (rows.size() > tempBorders.size()) {
                    tempBorders.add(null);
                }
                verticalBorders.add(tempBorders);
            }
        }
        // initialize horizontal borders
        while (rows.size() + 1 > horizontalBorders.size()) {
            tempBorders = new ArrayList<Border>();
            while (numberOfColumns > tempBorders.size()) {
                tempBorders.add(null);
            }
            horizontalBorders.add(tempBorders);
        }
        // Notice that the first row on the page shouldn't collapse with the last on the previous one
        if (null != lastFlushedRowBottomBorder && 0 < lastFlushedRowBottomBorder.size() && !isFirstOnPage) { // TODO
            tempBorders = new ArrayList<Border>();
            for (Border border : lastFlushedRowBottomBorder) {
                tempBorders.add(border);
            }
            horizontalBorders.set(0, tempBorders);
        }
    }

    protected TableBorders setTableBoundingBorders(Border[] borders) {
        tableBoundingBorders = new Border[4];
        if (null != borders) {
            for (int i = 0; i < borders.length; i++) {
                tableBoundingBorders[i] = borders[i];
            }
        }
        return this;
    }

    protected TableBorders setRowRange(Table.RowRange rowRange) {
        this.rowRange = rowRange;
        return this;
    }

    protected TableBorders setStartRow(int row) {
        this.rowRange = new Table.RowRange(row, rowRange.getFinishRow());
        return this;
    }

    protected TableBorders setFinishRow(int row) {
        this.rowRange = new Table.RowRange(rowRange.getStartRow(), row);
        return this;
    }


    // region getters

    abstract  public List<Border> getVerticalBorder(int index);

    abstract  public List<Border> getHorizontalBorder(int index);


    public float getMaxTopWidth() {
        float width = 0;
        Border widestBorder = getWidestHorizontalBorder(rowRange.getStartRow());
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }


    public float getMaxBottomWidth() {
        float width = 0;
        Border widestBorder = getWidestHorizontalBorder(rowRange.getFinishRow() + 1);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public float getMaxRightWidth() {
        float width = 0;
        Border widestBorder = getWidestVerticalBorder(verticalBorders.size() - 1);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public float getMaxLeftWidth() {
        float width = 0;
        Border widestBorder = getWidestVerticalBorder(0);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public Border getWidestVerticalBorder(int col) {
        Border theWidestBorder = null;
        if (col >= 0 && col < verticalBorders.size()) {
            theWidestBorder = getWidestBorder(getVerticalBorder(col));
        }
        return theWidestBorder;
    }

    public Border getWidestVerticalBorder(int col, int start, int end) {
        Border theWidestBorder = null;
        if (col >= 0 && col < verticalBorders.size()) {
            theWidestBorder = getWidestBorder(getVerticalBorder(col), start, end);
        }
        return theWidestBorder;
    }

    public Border getWidestHorizontalBorder(int row) {
        Border theWidestBorder = null;
        if (row >= 0 && row < horizontalBorders.size()) {
            theWidestBorder = getWidestBorder(getHorizontalBorder(row));
        }
        return theWidestBorder;
    }

    public Border getWidestHorizontalBorder(int row, int start, int end) {
        Border theWidestBorder = null;
        if (row >= 0 && row < horizontalBorders.size()) {
            theWidestBorder = getWidestBorder(getHorizontalBorder(row), start, end);
        }
        return theWidestBorder;
    }

    public List<Border> getFirstHorizontalBorder() {
        return getHorizontalBorder(rowRange.getStartRow());
    }

    public List<Border> getLastHorizontalBorder() {
        return getHorizontalBorder(rowRange.getFinishRow() + 1);
    }

    public List<Border> getFirstVerticalBorder() {
        return getVerticalBorder(0);
    }

    public List<Border> getLastVerticalBorder() {
        return getVerticalBorder(verticalBorders.size() - 1);
    }


    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public Border[] getTableBoundingBorders() {
        return tableBoundingBorders;
    }

    public int getVerticalBordersSize() {
        return verticalBorders.size();
    }

    public int getHorizontalBordersSize() {
        return verticalBorders.size();
    }



    public float[] getCellBorderIndents(int row, int col, int rowspan, int colspan) {
        float[] indents = new float[4];
        List<Border> borderList;
        Border border;
        // process top border
        borderList = getHorizontalBorder(rowRange.getStartRow() + row - rowspan + 1);
        for (int i = col; i < col + colspan; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[0]) {
                indents[0] = border.getWidth();
            }
        }
        // process right border
        borderList = getVerticalBorder(col + colspan);
        for (int i = rowRange.getStartRow() + row - rowspan + 1; i < rowRange.getStartRow() + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[1]) {
                indents[1] = border.getWidth();
            }
        }
        // process bottom border
        borderList = getHorizontalBorder(rowRange.getStartRow() + row + 1);
        for (int i = col; i < col + colspan; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[2]) {
                indents[2] = border.getWidth();
            }
        }
        // process left border
        borderList = getVerticalBorder(col);
        for (int i = rowRange.getStartRow() + row - rowspan + 1; i < rowRange.getStartRow() + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[3]) {
                indents[3] = border.getWidth();
            }
        }
        return indents;
    }

    // endregion

    //region static

    /**
     * Returns the collapsed border. We process collapse
     * if the table border width is strictly greater than cell border width.
     *
     * @param cellBorder  cell border
     * @param tableBorder table border
     * @return the collapsed border
     */
    public static Border getCollapsedBorder(Border cellBorder, Border tableBorder) {
        if (null != tableBorder) {
            if (null == cellBorder || cellBorder.getWidth() < tableBorder.getWidth()) {
                return tableBorder;
            }
        }
        if (null != cellBorder) {
            return cellBorder;
        } else {
            return Border.NO_BORDER;
        }
    }

    public static Border getCellSideBorder(Cell cellModel, int borderType) {
        Border cellModelSideBorder = cellModel.getProperty(borderType);
        if (null == cellModelSideBorder && !cellModel.hasProperty(borderType)) {
            cellModelSideBorder = cellModel.getProperty(Property.BORDER);
            if (null == cellModelSideBorder && !cellModel.hasProperty(Property.BORDER)) {
                cellModelSideBorder = cellModel.getDefaultProperty(Property.BORDER); // TODO Maybe we need to foresee the possibility of default side border property
            }
        }
        return cellModelSideBorder;
    }

    public static Border getWidestBorder(List<Border> borderList) {
        Border theWidestBorder = null;
        if (0 != borderList.size()) {
            for (Border border : borderList) {
                if (null != border && (null == theWidestBorder || border.getWidth() > theWidestBorder.getWidth())) {
                    theWidestBorder = border;
                }
            }
        }
        return theWidestBorder;
    }

    public static Border getWidestBorder(List<Border> borderList, int start, int end) {
        Border theWidestBorder = null;
        if (0 != borderList.size()) {
            for (Border border : borderList.subList(start, end)) {
                if (null != border && (null == theWidestBorder || border.getWidth() > theWidestBorder.getWidth())) {
                    theWidestBorder = border;
                }
            }
        }
        return theWidestBorder;
    }

    public static List<Border> getBorderList(Border border, int size) {
        List<Border> borderList = new ArrayList<Border>();
        for (int i = 0; i < size; i++) {
            borderList.add(border);
        }
        return borderList;
    }

    public static List<Border> getBorderList(List<Border> originalList, Border borderToCollapse, int size) {
        List<Border> borderList = new ArrayList<Border>();
        if (null != originalList) {
            borderList.addAll(originalList);
        }
        while (borderList.size() < size) {
            borderList.add(borderToCollapse);
        }
        int end = null == originalList ? size : Math.min(originalList.size(), size);
        for (int i = 0; i < end; i++) {
            if (null == borderList.get(i) || (null != borderToCollapse && borderList.get(i).getWidth() <= borderToCollapse.getWidth())) {
                borderList.set(i, borderToCollapse);
            }
        }
        return borderList;
    }

    // endregion
}
