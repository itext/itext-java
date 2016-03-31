package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.IRenderer;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Cell} is one piece of data in an enclosing grid, the {@link Table}.
 * This object is a {@link BlockElement}, giving it a number of visual layout
 * properties. 
 * 
 * A cell can act as a container for a number of layout elements; it can only
 * contain other {@link BlockElement} objects or images. Other types of layout
 * elements must be wrapped in a {@link BlockElement}.
 */
public class Cell extends BlockElement<Cell> {

    private static final Border defaultBorder = new SolidBorder(new com.itextpdf.kernel.color.DeviceRgb(160, 160, 160), 0.5f);

    private int row;
    private int col;
    private int rowspan;
    private int colspan;

    protected PdfName role = PdfName.TD;
    protected AccessibilityProperties tagProperties;

    /**
     * Creates a cell which takes a custom amount of cell spaces in the table.
     * 
     * @param rowspan the number of rows this cell must occupy. Negative numbers will make the argument default to 1.
     * @param colspan the number of columns this cell must occupy. Negative numbers will make the argument default to 1.
     */
    public Cell(int rowspan, int colspan) {
        this.rowspan = Math.max(rowspan, 1);
        this.colspan = Math.max(colspan, 1);
    }

    /**
     * Creates a cell.
     */
    public Cell () {
        this(1, 1);
    }

    /**
     * Gets a cell renderer for this element. Note that this method can be called more than once.
     * By default each element should define its own renderer, but the renderer can be overridden by
     * {@link #setNextRenderer(IRenderer)} method call.
     * @return a cell renderer for this element
     */
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

    /**
     * Adds any block element to the cell's contents.
     * 
     * @param element a {@link BlockElement}
     * @return this Element
     */
    public Cell add(BlockElement element) {
        childElements.add(element);
        return this;
    }

    /**
     * Adds an image to the cell's contents.
     * 
     * @param element an {@link Image}
     * @return this Element
     */
    public Cell add(Image element) {
        childElements.add(element);
        return this;
    }

    /**
     * Adds an embedded table to the cell's contents.
     * 
     * @param element a nested {@link Table}
     * @return this Element
     */
    public Cell add(Table element) {
        childElements.add(element);
        return this;
    }

    /**
     * Directly adds a String of text to this cell. The content is wrapped in a
     * layout element.
     * 
     * @param content a {@link String}
     * @return this Element
     */
    public Cell add(String content) {
        return add(new Paragraph(content));
    }

    /**
     * Clones a cell with its position, properties, and optionally its contents.
     * 
     * @param includeContent whether or not to also include the contents of the cell.
     * @return a clone of this Element
     */
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
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new AccessibilityProperties();
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
