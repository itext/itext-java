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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.BorderCollapsePropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;

public class CellRenderer extends BlockRenderer {
    /**
     * Creates a CellRenderer from its corresponding layout object.
     *
     * @param modelElement the {@link com.itextpdf.layout.element.Cell} which this object should manage
     */
    public CellRenderer(Cell modelElement) {
        super(modelElement);
        assert modelElement != null;
        setProperty(Property.ROWSPAN, modelElement.getRowspan());
        setProperty(Property.COLSPAN, modelElement.getColspan());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPropertyContainer getModelElement() {
        return super.getModelElement();
    }

    @Override
    protected Float retrieveWidth(float parentBoxWidth) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractRenderer createSplitRenderer(int layoutResult) {
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
    protected AbstractRenderer createOverflowRenderer(int layoutResult) {
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
        Float angle = this.getPropertyAsFloat(Property.ROTATION_ANGLE);
        boolean avoidRotation = null != angle && hasProperty(Property.BACKGROUND);
        boolean restoreRotation = hasOwnProperty(Property.ROTATION_ANGLE);
        if (avoidRotation) {
            AffineTransform transform = new AffineTransform(ctm.get(0), ctm.get(1), ctm.get(3), ctm.get(4), ctm.get(6), ctm.get(7));
            try {
                transform = transform.createInverse();
            } catch (NoninvertibleTransformException e) {
                throw new PdfException(PdfException.NoninvertibleMatrixCannotBeProcessed, e);
            }
            transform.concatenate(new AffineTransform());
            canvas.concatMatrix(transform);
            setProperty(Property.ROTATION_ANGLE, null);
        }

        super.drawBackground(drawContext);

        // restore concat matrix and rotation angle
        if (avoidRotation) {
            if (restoreRotation) {
                setProperty(Property.ROTATION_ANGLE, angle);
            } else {
                deleteOwnProperty(Property.ROTATION_ANGLE);
            }
            canvas.concatMatrix(new AffineTransform(ctm.get(0), ctm.get(1), ctm.get(3), ctm.get(4), ctm.get(6), ctm.get(7)));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawBorder(DrawContext drawContext) {
        if (BorderCollapsePropertyValue.SEPARATE.equals(parent.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE))) {
            super.drawBorder(drawContext);
        } else {
            // Do nothing here. Border drawing for cells is done on TableRenderer.
        }
    }

    @Override
    protected Rectangle applyBorderBox(Rectangle rect, Border[] borders, boolean reverse) {
        if (BorderCollapsePropertyValue.SEPARATE.equals(parent.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE))) {
            super.applyBorderBox(rect, borders, reverse);
        } else {
            // Do nothing here. Borders are processed on TableRenderer level.
        }

        return rect;
    }

    @Override
    protected Rectangle applyMargins(Rectangle rect, UnitValue[] margins, boolean reverse) {
        // If borders are separated, process border's spacing here.
        if (BorderCollapsePropertyValue.SEPARATE.equals(parent.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE))) {
            applySpacings(rect, reverse);
        }
        return rect;
    }

    protected Rectangle applySpacings(Rectangle rect, boolean reverse) {
        if (BorderCollapsePropertyValue.SEPARATE.equals(parent.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE))) {
            Float verticalBorderSpacing = this.parent.<Float>getProperty(Property.VERTICAL_BORDER_SPACING);
            Float horizontalBorderSpacing = this.parent.<Float>getProperty(Property.HORIZONTAL_BORDER_SPACING);
            float[] cellSpacings = new float[4];
            for (int i = 0; i < cellSpacings.length; i++) {
                cellSpacings[i] = 0 == i % 2
                        ? null != verticalBorderSpacing ? (float) verticalBorderSpacing : 0f
                        : null != horizontalBorderSpacing ? (float) horizontalBorderSpacing : 0f;
            }
            applySpacings(rect, cellSpacings, reverse);
        } else {
            // Do nothing here. Spacings are meaningless if borders are collapsed.
        }
        return rect;
    }

    protected Rectangle applySpacings(Rectangle rect, float[] spacings, boolean reverse) {
        if (BorderCollapsePropertyValue.SEPARATE.equals(parent.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE))) {
            rect.applyMargins(spacings[0] / 2, spacings[1] / 2, spacings[2] / 2, spacings[3] / 2, reverse);
        } else {
            // Do nothing here. Spacings are meaningless if borders are collapsed.
        }
        return rect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new CellRenderer((Cell) getModelElement());
    }
}
