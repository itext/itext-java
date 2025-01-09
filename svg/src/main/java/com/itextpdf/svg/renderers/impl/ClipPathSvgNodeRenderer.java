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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * This renderer represents a collection of elements (simple shapes and paths).
 * The elements are not drawn visibly, but the union of their shapes will be used
 * to only show the parts of the drawn objects that fall within the clipping path.
 *
 * In PDF, the clipping path operators use the intersection of all its elements, not the union (as in SVG);
 * thus, we need to draw the clipped elements multiple times if the clipping path consists of multiple elements.
 */
public class ClipPathSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {

    private AbstractSvgNodeRenderer clippedRenderer;

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        AbstractBranchSvgNodeRenderer copy = new ClipPathSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    @Override void preDraw(SvgDrawContext context) {}

    @Override
    protected void doDraw(SvgDrawContext context) {
        PdfCanvas currentCanvas = context.getCurrentCanvas();
        for (ISvgNodeRenderer child : getChildren()) {
            currentCanvas.saveState();

            if (child instanceof AbstractSvgNodeRenderer) {
                ((AbstractSvgNodeRenderer) child).setPartOfClipPath(true);
            }

            child.draw(context);

            if (child instanceof AbstractSvgNodeRenderer) {
                ((AbstractSvgNodeRenderer) child).setPartOfClipPath(false);
            }

            if (clippedRenderer != null) {
                clippedRenderer.preDraw(context);
                clippedRenderer.doDraw(context);
                clippedRenderer.postDraw(context);
            }

            currentCanvas.restoreState();
        }

    }

    public void setClippedRenderer(AbstractSvgNodeRenderer clippedRenderer) {
        this.clippedRenderer = clippedRenderer;
    }
}
