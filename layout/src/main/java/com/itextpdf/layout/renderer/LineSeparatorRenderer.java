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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.Property;

public class LineSeparatorRenderer extends BlockRenderer {

    /**
     * Creates a LineSeparatorRenderer from its corresponding layout object.
     * @param lineSeparator the {@link com.itextpdf.layout.element.LineSeparator} which this object should manage
     */
    public LineSeparatorRenderer(LineSeparator lineSeparator) {
        super(lineSeparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        Rectangle parentBBox = layoutContext.getArea().getBBox().clone();
        if (this.<Float>getProperty(Property.ROTATION_ANGLE) != null) {
            parentBBox.moveDown(AbstractRenderer.INF - parentBBox.getHeight()).setHeight(AbstractRenderer.INF);
        }

        ILineDrawer lineDrawer = this.<ILineDrawer>getProperty(Property.LINE_DRAWER);
        float height = lineDrawer != null ? lineDrawer.getLineWidth() : 0;

        occupiedArea = new LayoutArea(layoutContext.getArea().getPageNumber(), parentBBox.clone());
        applyMargins(occupiedArea.getBBox(), false);

        Float calculatedWidth = retrieveWidth(layoutContext.getArea().getBBox().getWidth());
        if (calculatedWidth == null) {
            calculatedWidth = occupiedArea.getBBox().getWidth();
        }
        if ((occupiedArea.getBBox().getHeight() < height || occupiedArea.getBBox().getWidth() < calculatedWidth) && !hasOwnProperty(Property.FORCED_PLACEMENT)) {
            return new LayoutResult(LayoutResult.NOTHING, null, null, this, this);
        }

        occupiedArea.getBBox().setWidth((float) calculatedWidth).moveUp(occupiedArea.getBBox().getHeight() - height).setHeight(height);

        applyMargins(occupiedArea.getBBox(), true);

        if (this.<Float>getProperty(Property.ROTATION_ANGLE) != null) {
            applyRotationLayout(layoutContext.getArea().getBBox().clone());
            if (isNotFittingLayoutArea(layoutContext.getArea())) {
                if (!Boolean.TRUE.equals(getPropertyAsBoolean(Property.FORCED_PLACEMENT))) {
                    return new LayoutResult(LayoutResult.NOTHING, null, null, this, this);
                }
            }
        }

        return new LayoutResult(LayoutResult.FULL, occupiedArea, this, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IRenderer getNextRenderer() {
        return new LineSeparatorRenderer((LineSeparator) modelElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawChildren(DrawContext drawContext) {
        ILineDrawer lineDrawer = this.<ILineDrawer>getProperty(Property.LINE_DRAWER);
        if (lineDrawer != null) {
            PdfCanvas canvas = drawContext.getCanvas();
            boolean isTagged = drawContext.isTaggingEnabled();
            if (isTagged) {
                canvas.openTag(new CanvasArtifact());
            }
            
            Rectangle area = getOccupiedAreaBBox();
            applyMargins(area, false);
            lineDrawer.draw(canvas, area);
            
            if (isTagged) {
                canvas.closeTag();
            }
        }
    }
}
