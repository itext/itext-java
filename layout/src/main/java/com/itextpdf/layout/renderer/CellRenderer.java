/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.Property;
import com.sun.org.apache.xpath.internal.operations.Bool;

public class CellRenderer extends BlockRenderer {

    /**
     * Creates a CellRenderer from its corresponding layout object.
     * @param modelElement the {@link com.itextpdf.layout.element.Cell} which this object should manage
     */
    public CellRenderer(Cell modelElement) {
        super(modelElement);
        setProperty(Property.ROWSPAN, modelElement.getRowspan());
        setProperty(Property.COLSPAN, modelElement.getColspan());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cell getModelElement() {
        return (Cell) super.getModelElement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CellRenderer createSplitRenderer(int layoutResult) {
        CellRenderer splitRenderer = (CellRenderer) getNextRenderer();
        splitRenderer.parent = parent;
        splitRenderer.modelElement = modelElement;
        splitRenderer.occupiedArea = occupiedArea;
        splitRenderer.isLastRendererForModelElement = false;
        splitRenderer.addAllProperties(getOwnProperties());
        return splitRenderer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CellRenderer createOverflowRenderer(int layoutResult) {
        CellRenderer overflowRenderer = (CellRenderer) getNextRenderer();
        overflowRenderer.parent = parent;
        overflowRenderer.modelElement = modelElement;
        overflowRenderer.addAllProperties(getOwnProperties());
        return overflowRenderer;
    }

    @Override
    public void drawBackground(DrawContext drawContext) {
        PdfCanvas canvas = drawContext.getCanvas();
        Matrix ctm = canvas.getGraphicsState().getCtm();

        // Avoid rotation
        Float angle = getPropertyAsFloat(Property.ROTATION_ANGLE);
        boolean avoidRotation = null != angle && null != getProperty(Property.BACKGROUND);
        if (avoidRotation) {
            AffineTransform transform = new AffineTransform(ctm.get(0), ctm.get(1), ctm.get(3), ctm.get(4), ctm.get(6), ctm.get(7));
            try {
                transform = transform.createInverse();
            } catch (NoninvertibleTransformException e) {
                throw new RuntimeException(e);
            }
            transform.concatenate(new AffineTransform());
            canvas.concatMatrix(transform);
            deleteProperty(Property.ROTATION_ANGLE);
        }

        super.drawBackground(drawContext);

        // restore concat matrix and rotation angle
        if (avoidRotation) {
            setProperty(Property.ROTATION_ANGLE, angle);
            canvas.concatMatrix(new AffineTransform(ctm.get(0), ctm.get(1), ctm.get(3), ctm.get(4), ctm.get(6), ctm.get(7)));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawBorder(DrawContext drawContext) {
        // Do nothing here. Border drawing for cells is done on TableRenderer.
    }

    @Override
    protected Rectangle applyBorderBox(Rectangle rect, Border[] borders, boolean reverse) {
        float topWidth = borders[0] != null ? borders[0].getWidth() : 0;
        float rightWidth = borders[1] != null ? borders[1].getWidth() : 0;
        float bottomWidth = borders[2] != null ? borders[2].getWidth() : 0;
        float leftWidth = borders[3] != null ? borders[3].getWidth() : 0;

        float topWidthFactor = this.getModelElement().getRow() == 0 ? 1 : 2;
        float rightWidthFactor = this.getModelElement().getCol() == ((TableRenderer)this.parent).rows.get(this.getModelElement().getRow()).length-1 ? 1 : 2;
        float bottomWidthFactor = this.getModelElement().getRow() == ((TableRenderer)this.parent).rows.size()-1 ? 1 : 2;
        float leftWidthFactor = this.getModelElement().getCol() == 0 ? 1 : 2;

        return rect.<Rectangle>applyMargins(topWidth / topWidthFactor, rightWidth / rightWidthFactor, bottomWidth / bottomWidthFactor, leftWidth / leftWidthFactor, reverse);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new CellRenderer(getModelElement());
    }
}
