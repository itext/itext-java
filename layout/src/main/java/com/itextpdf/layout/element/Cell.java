/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.layout.element;

import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.DefaultAccessibilityProperties;
import com.itextpdf.kernel.pdf.tagutils.AccessibilityProperties;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.CellRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.util.MessageFormatUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

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

    private static final Border DEFAULT_BORDER = new SolidBorder(0.5f);

    private int row;
    private int col;
    private int rowspan;
    private int colspan;

    protected DefaultAccessibilityProperties tagProperties;

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
    public IRenderer getRenderer() {
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

    /**
     * Gets {@link #row the number of the row} in which the cell is located.
     * @return the row number
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets {@link #row the number of the column} in which the cell is located.
     * @return the column number
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets the {@link #rowspan rowspan} of the cell.
     * @return the rowspan
     */
    public int getRowspan() {
        return rowspan;
    }

    /**
     * Gets the {@link #colspan colspan} of the cell.
     * @return the colspan
     */
    public int getColspan() {
        return colspan;
    }

    /**
     * Adds any block element to the cell's contents.
     *
     * @param element a {@link BlockElement}
     * @return this Element
     */
    public Cell add(IBlockElement element) {
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
        if (null != styles) {
            newCell.styles = new LinkedHashSet<>(styles);
        }
        if (includeContent) {
            newCell.childElements = new ArrayList<>(childElements);
        }
        return newCell;
    }

    @Override
    public <T1> T1 getDefaultProperty(int property) {
        switch (property) {
            case Property.BORDER:
                return (T1) (Object) DEFAULT_BORDER;
            case Property.PADDING_BOTTOM:
            case Property.PADDING_LEFT:
            case Property.PADDING_RIGHT:
            case Property.PADDING_TOP:
                return (T1) (Object) UnitValue.createPointValue(2f);
            default:
                return super.<T1>getDefaultProperty(property);
        }
    }

    @Override
    public String toString() {
        return MessageFormatUtil.format("Cell[row={0}, col={1}, rowspan={2}, colspan={3}]", row, col, rowspan, colspan);
    }

    @Override
    public AccessibilityProperties getAccessibilityProperties() {
        if (tagProperties == null) {
            tagProperties = new DefaultAccessibilityProperties(StandardRoles.TD);
        }
        return tagProperties;
    }

    @Override
    protected IRenderer makeNewRenderer() {
        return new CellRenderer(this);
    }

    protected Cell updateCellIndexes(int row, int col, int numberOfColumns) {
        this.row = row;
        this.col = col;
        colspan = Math.min(colspan, numberOfColumns - this.col);
        return this;
    }
}
