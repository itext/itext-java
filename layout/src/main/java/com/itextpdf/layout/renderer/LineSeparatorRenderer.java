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

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.CanvasArtifact;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.Property;

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
