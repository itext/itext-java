package com.itextpdf.model.element;

import com.itextpdf.canvas.color.DeviceRgb;
import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.BlockRenderer;
import com.itextpdf.model.renderer.IRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cell extends BlockElement<Cell> {

    private int row;
    private int col;
    private int rowspan;
    private int colspan;

    public Cell(int rowspan, int colspan) {
        this.rowspan = Math.max(rowspan, 1);
        this.colspan = Math.max(colspan, 1);
    }

    public Cell () {
        this (1, 1);
    }

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            if (nextRenderer instanceof BlockRenderer && nextRenderer.getModelElement() instanceof Cell) {
                IRenderer renderer = nextRenderer;
                nextRenderer = null;
                return renderer;
            } else {
                Logger logger = LoggerFactory.getLogger(Table.class);
                logger.error("Invalid renderer for Table: must be inherited from TableRenderer");
            }
        }
        Property.BorderConfig borders = new Property.BorderConfig(new DeviceRgb(160, 160, 160),
                0.5f, Property.BorderConfig.BorderStyle.SOLID);
        return new BlockRenderer(this).setProperty(Property.BORDER, borders);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getRowspan() {
        return rowspan;
    }

    public int getColspan() {
        return colspan;
    }

    public Cell add(AbstractElement element) {
        childElements.add(element);
        return this;
    }

    protected Cell updateCellIndexes(int row, int col, int numberOfColumns) {
        this.row = row;
        this.col = col;
        colspan = Math.min(colspan, numberOfColumns - this.col);
        setProperty(Property.ROW, row);
        setProperty(Property.ROWSPAN, rowspan);
        setProperty(Property.COLSPAN, colspan);
        return this;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "row=" + row +
                ", col=" + col +
                ", rowspan=" + rowspan +
                ", colspan=" + colspan +
                '}';
    }
}
