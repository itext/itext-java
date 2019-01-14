/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.renderers.impl;


import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.NoninvertibleTransformException;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.css.impl.SvgNodeRendererInheritanceResolver;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgMathUtils;
import org.slf4j.LoggerFactory;

/**
 * Renderer implementing the use tag. This tag allows you to reuse previously defined elements.
 */
public class UseSvgNodeRenderer extends AbstractSvgNodeRenderer {
    @Override
    protected void doDraw(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            String elementToReUse = this.attributesAndStyles.get(SvgConstants.Attributes.XLINK_HREF);

            if (elementToReUse == null) {
                elementToReUse = this.attributesAndStyles.get(SvgConstants.Attributes.HREF);
            }

            if (elementToReUse != null && !elementToReUse.isEmpty() && isValidHref(elementToReUse)) {
                String normalizedName = normalizeName(elementToReUse);
                if (!context.isIdUsedByUseTagBefore(normalizedName)) {
                    ISvgNodeRenderer template = context.getNamedObject(normalizedName);
                    //Clone template
                    ISvgNodeRenderer namedObject = template == null ? null : template.createDeepCopy();
                    //Resolve parent inheritance
                    SvgNodeRendererInheritanceResolver iresolver = new SvgNodeRendererInheritanceResolver();
                    iresolver.applyInheritanceToSubTree(this,namedObject);

                    if (namedObject != null) {
                        if (namedObject instanceof AbstractSvgNodeRenderer) {
                            ((AbstractSvgNodeRenderer) namedObject).setPartOfClipPath(partOfClipPath);
                        }
                        PdfCanvas currentCanvas = context.getCurrentCanvas();

                        float x = 0f;
                        float y = 0f;

                        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.X)) {
                            x = CssUtils.parseAbsoluteLength(this.attributesAndStyles.get(SvgConstants.Attributes.X));
                        }

                        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.Y)) {
                            y = CssUtils.parseAbsoluteLength(this.attributesAndStyles.get(SvgConstants.Attributes.Y));
                        }

                        AffineTransform inverseMatrix = null;
                        if (!SvgMathUtils.compareFloats(x,0) || !SvgMathUtils.compareFloats(y,0)) {
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

    /**
     * The reference value will contain a hashtag character. This method will filter that value.
     *
     * @param name value to be filtered
     * @return filtered value
     */
    private String normalizeName(String name) {
        return name.replace("#", "").trim();
    }

    private boolean isValidHref(String name) {
        return name.startsWith("#");
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        UseSvgNodeRenderer copy = new UseSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }
}
