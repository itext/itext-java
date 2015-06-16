package com.itextpdf.model.element;

public class Cell extends BlockElement<Cell> {

    private int rowIdx;
    private int colIdx;
    private int rowspan;
    private int colspan;

    public Cell(int rowspan, int colspan) {
        this.rowspan = Math.max(rowspan, 1);
        this.colspan = Math.max(colspan, 1);
    }

    public Cell () {
        this (1, 1);
    }

    public int getRowspan() {
        return rowspan;
    }

    public int getColspan() {
        return colspan;
    }

    public Cell add(BlockElement element) {
        childElements.add(element);
        return this;
    }

    protected Cell updateCellIndexes(int rowIdx, int colIdx, int numberOfColumns) {
        this.rowIdx = rowIdx;
        this.colIdx = colIdx;
        colspan = Math.min(colspan, numberOfColumns - colIdx);
        return this;
    }
}
