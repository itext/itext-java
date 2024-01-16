package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.properties.Property;

class GridCell {
    private final IRenderer value;
    private final IntRectangle gridArea;
    private Rectangle layoutArea = new Rectangle(0.0f, 0.0f, 0.0f,0.0f);
    private boolean isValueFitOnCellArea = true;

    /**
     * Create a grid cell and init value renderer position on a grid based on its properties.
     *
     * @param value item renderer
     */
    GridCell(IRenderer value) {
        this.value = value;
        final int[] rowValues = initRowColumnsValues(value.<Integer>getProperty(Property.GRID_ROW_START),
                value.<Integer>getProperty(Property.GRID_ROW_END));
        int height = rowValues[0] == 0 ? 1 : rowValues[1] - rowValues[0];

        final int[] columnValues = initRowColumnsValues(value.<Integer>getProperty(Property.GRID_COLUMN_START),
                value.<Integer>getProperty(Property.GRID_COLUMN_END));
        int width = columnValues[0] == 0 ? 1 : columnValues[1] - columnValues[0];
        gridArea = new IntRectangle(columnValues[0] - 1, rowValues[0] - 1, width, height);
    }

    int getColumnStart() {
        return gridArea.getLeft();
    }

    int getColumnEnd() {
        return gridArea.getRight();
    }

    int getRowStart() {
        return gridArea.getBottom();
    }

    int getRowEnd() {
        return gridArea.getTop();
    }

    int getGridHeight() {
        return gridArea.getHeight();
    }

    int getGridWidth() {
        return gridArea.getWidth();
    }

    IRenderer getValue() {
        return value;
    }

    boolean isValueFitOnCellArea() {
        return isValueFitOnCellArea;
    }

    Rectangle getLayoutArea() {
        return layoutArea;
    }

    void setLayoutArea(Rectangle layoutArea) {
        this.layoutArea = layoutArea;
    }

    void setValueFitOnCellArea(boolean valueFitOnCellArea) {
        isValueFitOnCellArea = valueFitOnCellArea;
    }

    void setStartingRowAndColumn(int row, int column) {
        this.gridArea.setY(row);
        this.gridArea.setX(column);
    }

    /**
     * init row/column start/end value
     * if start > end values are swapped
     * if only start or end are specified - other value is initialized so cell would have height/width = 1
     *
     * @param start x/y pos of cell on a grid
     * @param end x/y + width/height pos of cell on a grid
     * @return
     */
    private int[] initRowColumnsValues(Integer start, Integer end) {
        int[] result = new int[] {0, 0};
        if (start != null && end != null) {
            result[0] = (int)start;
            result[1] = (int)end;
            if (start > end) {
                result[0] = (int)end;
                result[1] = (int)start;
            }
        } else if (start != null) {
            result[0] = (int)start;
            result[1] = (int)start + 1;
        } else if (end != null) {
            result[0] = end <= 1 ? 1 : ((int)end) - 1;
            result[1] = end <= 1 ? 2 : (int)end;
        }
        return result;
    }

    private static class IntRectangle {
        private int x;
        private int y;
        private int width;
        private int height;

        public IntRectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getLeft() {
            return x;
        }

        public int getRight() {
            return x + width;
        }

        public int getTop() {
            return y + height;
        }

        public int getBottom() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "Rectangle: start(" + x + ',' + y + ") ," + width + 'x' + height;
        }
    }
}