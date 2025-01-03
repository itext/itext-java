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
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.css.impl.SvgNodeRendererInheritanceResolver;
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
            String elementToReUse = getAttribute(SvgConstants.Attributes.HREF);

            if (elementToReUse == null) {
                elementToReUse = getAttribute(SvgConstants.Attributes.XLINK_HREF);
            }

            if (elementToReUse != null && !elementToReUse.isEmpty() && isValidHref(elementToReUse)) {
                String normalizedName = SvgTextUtil.filterReferenceValue(elementToReUse);
                if (!context.isIdUsedByUseTagBefore(normalizedName)) {
                    ISvgNodeRenderer template = context.getNamedObject(normalizedName);
                    // Clone template
                    ISvgNodeRenderer clonedObject = template == null ? null : template.createDeepCopy();
                    // Resolve parent inheritance
                    SvgNodeRendererInheritanceResolver.applyInheritanceToSubTree(this, clonedObject, context.getCssContext());

                    if (clonedObject != null) {
                        if (clonedObject instanceof AbstractSvgNodeRenderer) {
                            ((AbstractSvgNodeRenderer) clonedObject).setPartOfClipPath(partOfClipPath);
                        }
                        PdfCanvas currentCanvas = context.getCurrentCanvas();

                        // If X or Y attribute is null, then default 0 value will be returned
                        float x = parseHorizontalLength(getAttribute(Attributes.X), context);
                        float y = parseVerticalLength(getAttribute(Attributes.Y), context);

                        AffineTransform inverseMatrix = null;
                        if (!CssUtils.compareFloats(x, 0) || !CssUtils.compareFloats(y, 0)) {
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

                        // Setting the parent of the referenced element to this instance
                        clonedObject.setParent(this);
                        // Width, and height have no effect on use elements, unless the element referenced has a viewBox
                        // i.e. they only have an effect when use refers to a svg or symbol element.
                        if (clonedObject instanceof SvgTagSvgNodeRenderer || clonedObject instanceof SymbolSvgNodeRenderer) {
                            if (getAttribute(Attributes.WIDTH) != null) {
                                float width = parseHorizontalLength(getAttribute(Attributes.WIDTH), context);
                                clonedObject.setAttribute(Attributes.WIDTH, Float.toString(width) + CommonCssConstants.PT);
                            }
                            if (getAttribute(Attributes.HEIGHT) != null) {
                                float height = parseVerticalLength(getAttribute(Attributes.HEIGHT), context);
                                clonedObject.setAttribute(Attributes.HEIGHT, Float.toString(height) + CommonCssConstants.PT);
                            }
                        }

                        clonedObject.draw(context);

                        // Unsetting the parent of the referenced element
                        clonedObject.setParent(null);
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
