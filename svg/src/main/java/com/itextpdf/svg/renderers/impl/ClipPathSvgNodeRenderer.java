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

import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This renderer represents a collection of elements (simple shapes and paths).
 * The elements are not drawn visibly, but the union of their shapes will be used
 * to only show the parts of the drawn objects that fall within the clipping path.
 *
 * <p>
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
        if (clippedRenderer == null) {
            // clipPath element is applicable only for some particular elements, without it, no drawing needed
            return;
        }

        PdfCanvas currentCanvas = context.getCurrentCanvas();
        for (ISvgNodeRenderer child : getChildren()) {
            if (child instanceof AbstractSvgNodeRenderer
                    && ((AbstractSvgNodeRenderer) child).isHidden()) {
                continue;
            }
            currentCanvas.saveState();

            child.setParent(this);
            child.draw(context);

            if (!(child instanceof TextSvgBranchRenderer)) {
                // TextSvgBranchRenderer by itself will call drawClippedRenderer after each sub-element drawing
                drawClippedRenderer(context);

            }
            if (!context.getClippingElementTransform().isIdentity()) {
                context.resetClippingElementTransform();
            }

            currentCanvas.restoreState();
        }
    }

    /**
     * Draw the clipped renderer.
     *
     * @param context the context on which clipped renderer will be drawn
     */
    public void drawClippedRenderer(SvgDrawContext context) {
        if (!context.getClippingElementTransform().isIdentity()) {
            try {
                context.getCurrentCanvas().concatMatrix(context.getClippingElementTransform().createInverse());
            } catch (NoninvertibleTransformException e) {
                Logger logger = LoggerFactory.getLogger(ClipPathSvgNodeRenderer.class);
                logger.warn(SvgLogMessageConstant.NONINVERTIBLE_TRANSFORMATION_MATRIX_USED_IN_CLIP_PATH);
            }
        }
        clippedRenderer.preDraw(context);
        clippedRenderer.doDraw(context);
        clippedRenderer.postDraw(context);
        // Returning canvas matrix to its original state isn't required
        // because after drawClippedRenderer graphic state will be restored
    }

    /**
     * Sets the clipped renderer.
     *
     * @param clippedRenderer the clipped renderer
     */
    public void setClippedRenderer(AbstractSvgNodeRenderer clippedRenderer) {
        this.clippedRenderer = clippedRenderer;
    }

    @Override
    protected boolean isHidden() {
        return CommonCssConstants.NONE.equals(this.attributesAndStyles.get(CommonCssConstants.DISPLAY))
                && !CommonCssConstants.HIDDEN.equals(this.attributesAndStyles.get(CommonCssConstants.VISIBILITY));
    }
}
