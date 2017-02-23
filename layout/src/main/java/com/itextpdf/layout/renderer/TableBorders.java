package com.itextpdf.layout.renderer;


import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.Property;

import java.util.ArrayList;
import java.util.List;

public abstract class TableBorders {
    protected List<List<Border>> horizontalBorders = new ArrayList<>();
    protected List<List<Border>> verticalBorders = new ArrayList<>();

    protected final int numberOfColumns;

    protected Border[] tableBoundingBorders = new Border[4];

    protected List<CellRenderer[]> rows;

    protected int startRow;
    protected int finishRow;

    protected float leftBorderMaxWidth;
    protected float rightBorderMaxWidth;

    protected int largeTableIndexOffset = 0;

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns) {
        this.rows = rows;
        this.numberOfColumns = numberOfColumns;
    }

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders) {
        this(rows, numberOfColumns);
        setTableBoundingBorders(tableBoundingBorders);
    }

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns, int largeTableIndexOffset) {
        this(rows, numberOfColumns);
        this.largeTableIndexOffset = largeTableIndexOffset;
    }

    public TableBorders(List<CellRenderer[]> rows, int numberOfColumns, Border[] tableBoundingBorders, int largeTableIndexOffset) {
        this(rows, numberOfColumns, tableBoundingBorders);
        this.largeTableIndexOffset = largeTableIndexOffset;
    }
    // region abstract

    // region draw
    abstract protected TableBorders drawHorizontalBorder(int i, float startX, float y1, PdfCanvas canvas, float[] countedColumnWidth);
    abstract protected TableBorders drawVerticalBorder(int i, float startY, float x1, PdfCanvas canvas, List<Float> heights);
    // endregion

    // region area occupation
    abstract protected TableBorders applyTopBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean isComplete, boolean reverse);
    abstract protected TableBorders applyTopBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean reverse);
    abstract protected TableBorders applyBottomBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean isEmpty, boolean reverse);
    abstract protected TableBorders applyBottomBorder(Rectangle occupiedBox, Rectangle layoutBox, boolean reverse);
    abstract protected TableBorders applyLeftAndRightBorder(Rectangle layoutBox, boolean reverse);

    abstract protected TableBorders skipFooter(Border[] borders);
    abstract protected TableBorders considerFooter(TableBorders footerBordersHandler, boolean hasContent);
    abstract protected TableBorders considerHeader(TableBorders headerBordersHandler, boolean changeThis);
    abstract protected TableBorders considerHeaderOccupiedArea(Rectangle occupiedBox, Rectangle layoutBox);

    abstract protected TableBorders applyCellIndents(Rectangle box, float topIndent, float rightIndent, float bottomIndent, float leftIndent, boolean reverse);
    // endregion

    // region getters
    abstract public List<Border> getVerticalBorder(int index);
    abstract public List<Border> getHorizontalBorder(int index);
    abstract protected float getCellVerticalAddition(float[] indents);
    // endregion

    abstract protected TableBorders updateOnNewPage(boolean isOriginalNonSplitRenderer, boolean isFooterOrHeader, TableRenderer currentRenderer, TableRenderer headerRenderer, TableRenderer footerRenderer);
    // endregion

    // region init
    protected TableBorders initializeBorders() {
        List<Border> tempBorders;
        // initialize vertical borders
        while (numberOfColumns + 1 > verticalBorders.size()) {
            tempBorders = new ArrayList<Border>();
            while ((int) Math.max(rows.size(), 1) > tempBorders.size()) {
                tempBorders.add(null);
            }
            verticalBorders.add(tempBorders);
        }
        // initialize horizontal borders
        while ((int) Math.max(rows.size(), 1) + 1 > horizontalBorders.size()) {
            tempBorders = new ArrayList<Border>();
            while (numberOfColumns > tempBorders.size()) {
                tempBorders.add(null);
            }
            horizontalBorders.add(tempBorders);
        }
        return this;
    }
    // endregion

    // region setters
    protected TableBorders setTableBoundingBorders(Border[] borders) {
        tableBoundingBorders = new Border[4];
        if (null != borders) {
            for (int i = 0; i < borders.length; i++) {
                tableBoundingBorders[i] = borders[i];
            }
        }
        return this;
    }

    protected TableBorders setRowRange(int startRow, int finishRow) {
        this.startRow = startRow;
        this.finishRow = finishRow;
        return this;
    }

    protected TableBorders setStartRow(int row) {
        this.startRow = row;
        return this;
    }

    protected TableBorders setFinishRow(int row) {
        this.finishRow = row;
        return this;
    }
    // endregion

    // region getters
    public float getLeftBorderMaxWidth() {
        return leftBorderMaxWidth;
    }

    public float getRightBorderMaxWidth() {
        return rightBorderMaxWidth;
    }

    public float getMaxTopWidth() {
        float width = 0;
        Border widestBorder = getWidestHorizontalBorder(startRow);
        if (null != widestBorder && widestBorder.getWidth() >= width) {
            width = widestBorder.getWidth();
        }
        return width;
    }

    public float getMaxBottomWidth() {
        float width = 0;
        Border widestBorder = getWidestHorizontalBorder(finishRow + 1);
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
        return getWidestBorder(getVerticalBorder(col));
    }

    public Border getWidestVerticalBorder(int col, int start, int end) {
        return getWidestBorder(getVerticalBorder(col), start, end);
    }

    public Border getWidestHorizontalBorder(int row) {
        return getWidestBorder(getHorizontalBorder(row));
    }

    public Border getWidestHorizontalBorder(int row, int start, int end) {
        return getWidestBorder(getHorizontalBorder(row), start, end);
    }

    public List<Border> getFirstHorizontalBorder() {
        return getHorizontalBorder(startRow);
    }

    public List<Border> getLastHorizontalBorder() {
        return getHorizontalBorder(finishRow + 1);
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

    public int getStartRow() {
        return startRow;
    }

    public int getFinishRow() {
        return finishRow;
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
        borderList = getHorizontalBorder(startRow + row - rowspan + 1);
        for (int i = col; i < col + colspan; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[0]) {
                indents[0] = border.getWidth();
            }
        }
        // process right border
        borderList = getVerticalBorder(col + colspan);
        for (int i = startRow + row - rowspan + 1; i < startRow + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[1]) {
                indents[1] = border.getWidth();
            }
        }
        // process bottom border
        borderList = getHorizontalBorder(startRow + row + 1);
        for (int i = col; i < col + colspan; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[2]) {
                indents[2] = border.getWidth();
            }
        }
        // process left border
        borderList = getVerticalBorder(col);
        for (int i = startRow + row - rowspan + 1; i < startRow + row + 1; i++) {
            border = borderList.get(i);
            if (null != border && border.getWidth() > indents[3]) {
                indents[3] = border.getWidth();
            }
        }
        return indents;
    }
    // endregion

    //region static
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
