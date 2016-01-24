package com.itextpdf.model.element;

import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.tagutils.AccessibleElementProperties;
import com.itextpdf.model.Property;
import com.itextpdf.model.border.Border;
import com.itextpdf.model.border.SolidBorder;
import com.itextpdf.model.renderer.CellRenderer;
import com.itextpdf.model.renderer.IRenderer;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cell extends BlockElement<Cell> {

    private static final Border defaultBorder = new SolidBorder(new com.itextpdf.core.color.DeviceRgb(160, 160, 160), 0.5f);

    private int row;
    private int col;
    private int rowspan;
    private int colspan;

    protected PdfName role = PdfName.TD;
    protected AccessibleElementProperties tagProperties;

    public Cell(int rowspan, int colspan) {
        this.rowspan = Math.max(rowspan, 1);
        this.colspan = Math.max(colspan, 1);
    }

    public Cell () {
        this(1, 1);
    }

    @Override
    public CellRenderer getRenderer() {
        CellRenderer cellRenderer = null;
        if (nextRenderer != null) {
            if (nextRenderer instanceof CellRenderer) {
                IRenderer renderer = nextRenderer;
                nextRenderer = nextRenderer.getNextRenderer();
                cellRenderer = (CellRenderer) renderer;
            } else {
                Logger logger = LoggerFactory.getLogger(Table.class);
                logger.error("Invalid renderer for Table: must be inherited from TableRenderer");
            }
        }
        //cellRenderer could be null in case invalid type (see logger message above)
        return cellRenderer == null ? makeNewRenderer() : cellRenderer;
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

    public Cell add(BlockElement element) {
        childElements.add(element);
        return this;
    }

    public Cell add(Image element) {
        childElements.add(element);
        return this;
    }

    public Cell add(Table element) {
        childElements.add(element);
        return this;
    }

    public Cell add(String content) {
        return add(new Paragraph(content));
    }

    public Cell clone(boolean includeContent) {
        Cell newCell = new Cell(rowspan, colspan);
        newCell.row = row;
        newCell.col = col;
        newCell.properties = new HashMap<>(properties);
        if (includeContent) {
            newCell.childElements = new ArrayList<>(childElements);
        }
        return newCell;
    }

    @Override
    public <T> T getDefaultProperty(Property property) {
        switch (property) {
            case BORDER:
                return (T) defaultBorder;
            default:
                return super.getDefaultProperty(property);
        }
    }

    @Override
    public String toString() {
        return String.format("Cell{row=%d, col=%d, rowspan=%d, colspan=%d}", row, col, rowspan, colspan);
    }

    @Override
    public PdfName getRole() {
        return role;
    }

    @Override
    public void setRole(PdfName role) {
        this.role = role;
        if (PdfName.Artifact.equals(role)) {
            propagateArtifactRoleToChildElements();
        }
    }

    @Override
    public AccessibleElementProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibleElementProperties();
        }
        return tagProperties;
    }

    @Override
    protected CellRenderer makeNewRenderer() {
        return new CellRenderer(this);
    }

    protected Cell updateCellIndexes(int row, int col, int numberOfColumns) {
        this.row = row;
        this.col = col;
        colspan = Math.min(colspan, numberOfColumns - this.col);
        return this;
    }
}
