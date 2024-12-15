/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.impl.SvgNodeRendererInheritanceResolver;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgTextUtil;

import org.slf4j.LoggerFactory;

/**
 * Renderer implementing the use tag. This tag allows you to reuse previously defined elements.
 */
public class UseSvgNodeRenderer extends AbstractSvgNodeRenderer {
    @Override
    protected void doDraw(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            String elementToReUse = this.attributesAndStyles.get(SvgConstants.Attributes.HREF);

            if (elementToReUse == null) {
                elementToReUse = this.attributesAndStyles.get(SvgConstants.Attributes.XLINK_HREF);
            }

            if (elementToReUse != null && !elementToReUse.isEmpty() && isValidHref(elementToReUse)) {
                String normalizedName = SvgTextUtil.filterReferenceValue(elementToReUse);
                if (!context.isIdUsedByUseTagBefore(normalizedName)) {
                    ISvgNodeRenderer template = context.getNamedObject(normalizedName);
                    // Clone template
                    ISvgNodeRenderer namedObject = template == null ? null : template.createDeepCopy();
                    // Resolve parent inheritance
                    SvgNodeRendererInheritanceResolver.applyInheritanceToSubTree(this, namedObject, context.getCssContext());

                    if (namedObject != null) {
                        if (namedObject instanceof AbstractSvgNodeRenderer) {
                            ((AbstractSvgNodeRenderer) namedObject).setPartOfClipPath(partOfClipPath);
                        }
                        PdfCanvas currentCanvas = context.getCurrentCanvas();

                        float x = 0f;
                        float y = 0f;

                        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.X)) {
                            x = CssDimensionParsingUtils.parseAbsoluteLength(this.attributesAndStyles.get(SvgConstants.Attributes.X));
                        }

                        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.Y)) {
                            y = CssDimensionParsingUtils.parseAbsoluteLength(this.attributesAndStyles.get(SvgConstants.Attributes.Y));
                        }
                        AffineTransform inverseMatrix = null;
                        if (!CssUtils.compareFloats(x,0) || !CssUtils.compareFloats(y,0)) {
                            AffineTransform translation = AffineTransform.getTranslateInstance(x, y);
                            currentCanvas.concatMatrix(translation);
                            if (partOfClipPath) {
                                try {
                                    inverseMatrix = translation.createInverse();
                                } catch (NoninvertibleTransformException ex) {
                                    LoggerFactory.getLogger(UseSvgNodeRenderer.class)
                                            .warn(SvgLogMessageConstant.NONINVERTIBLE_TRANSFORMATION_MATRIX_USED_IN_CLIP_PATH, ex);
                                }
                            }
                        }

                        // setting the parent of the referenced element to this instance
                        namedObject.setParent(this);
                        namedObject.draw(context);
                        // unsetting the parent of the referenced element
                        namedObject.setParent(null);
                        if (inverseMatrix != null) {
                            currentCanvas.concatMatrix(inverseMatrix);
                        }
                    }
                }
            }
        }
    }

    @Override void postDraw(SvgDrawContext context) {}

    private boolean isValidHref(String name) {
        return name.startsWith("#");
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        UseSvgNodeRenderer copy = new UseSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }
}
