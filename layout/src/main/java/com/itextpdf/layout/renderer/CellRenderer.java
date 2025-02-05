/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.IPropertyContainer;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.properties.BorderCollapsePropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.layout.LayoutContext;

/**
 * Represents a renderer for the {@link Cell} layout element.
 */
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
                throw new PdfException(LayoutExceptionMessageConstant.NONINVERTIBLE_MATRIX_CANNOT_BE_PROCESSED, e);
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

    /**
     * Applies spacings on the given rectangle.
     *
     * @param rect    a rectangle spacings will be applied on
     * @param reverse indicates whether spacings will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     *
     * @return a {@link Rectangle border box} of the renderer
     */
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

    /**
     * Applies given spacings on the given rectangle.
     *
     * @param rect    a rectangle spacings will be applied on
     * @param spacings the spacings to be applied on the given rectangle
     * @param reverse indicates whether spacings will be applied
     *                inside (in case of false) or outside (in case of true) the rectangle.
     *
     * @return a {@link Rectangle border box} of the renderer
     */
    protected Rectangle applySpacings(Rectangle rect, float[] spacings, boolean reverse) {
        if (BorderCollapsePropertyValue.SEPARATE.equals(parent.<BorderCollapsePropertyValue>getProperty(Property.BORDER_COLLAPSE))) {
            rect.applyMargins(spacings[0] / 2, spacings[1] / 2, spacings[2] / 2, spacings[3] / 2, reverse);
        } else {
            // Do nothing here. Spacings are meaningless if borders are collapsed.
        }
        return rect;
    }

    /**
     * Gets a new instance of this class to be used as a next renderer, after this renderer is used, if
     * {@link #layout(LayoutContext)} is called more than once.
     *
     * <p>
     * If a renderer overflows to the next area, iText uses this method to create a renderer
     * for the overflow part. So if one wants to extend {@link CellRenderer}, one should override
     * this method: otherwise the default method will be used and thus the default rather than the custom
     * renderer will be created.
     * @return new renderer instance
     */
    @Override
    public IRenderer getNextRenderer() {
        logWarningIfGetNextRendererNotOverridden(CellRenderer.class, this.getClass());
        return new CellRenderer((Cell) getModelElement());
    }
}
