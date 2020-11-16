/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfPatternCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgPaintServer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;

import org.slf4j.LoggerFactory;

/**
 * Implementation for the svg &lt;pattern&gt; tag.
 */
public class PatternSvgNodeRenderer extends AbstractBranchSvgNodeRenderer implements ISvgPaintServer {

    private static final double ZERO = 1E-10;

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        final PatternSvgNodeRenderer copy = new PatternSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    @Override
    public Color createColor(SvgDrawContext context, Rectangle objectBoundingBox, float objectBoundingBoxMargin,
            float parentOpacity) {
        if (!context.pushPatternId(getAttribute(Attributes.ID))) {
            // this means that pattern is cycled
            return null;
        }
        try {
            PdfPattern.Tiling tilingPattern = createTilingPattern(context);
            drawPatternContent(context, tilingPattern);
            return (tilingPattern == null) ? null : new PatternColor(tilingPattern);
        } finally {
            context.popPatternId();
        }
    }

    private PdfPattern.Tiling createTilingPattern(SvgDrawContext context) {
        final boolean isObjectBoundingBoxInPatternUnits = isObjectBoundingBoxInPatternUnits();
        final boolean isObjectBoundingBoxInPatternContentUnits = isObjectBoundingBoxInPatternContentUnits();
        final boolean isViewBoxExist = getAttribute(Attributes.VIEWBOX) != null;
        final AffineTransform patternAffineTransform = new AffineTransform();
        double xOffset, yOffset, xStep, yStep;
        Rectangle bbox;
        if (isObjectBoundingBoxInPatternUnits || isViewBoxExist || isObjectBoundingBoxInPatternContentUnits) {
            return null;
        } else {
            final Rectangle currentViewPort = context.getCurrentViewPort();
            final double viewPortX = currentViewPort.getX();
            final double viewPortY = currentViewPort.getY();
            final double viewPortWidth = currentViewPort.getWidth();
            final double viewPortHeight = currentViewPort.getHeight();
            final float em = getCurrentFontSize();
            final float rem = context.getCssContext().getRootFontSize();
            xOffset = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                    getAttribute(Attributes.X), viewPortX, viewPortX, viewPortWidth, em, rem);
            yOffset = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                    getAttribute(Attributes.Y), viewPortY, viewPortY, viewPortHeight, em, rem);
            xStep = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                    getAttribute(Attributes.WIDTH), viewPortX, viewPortX, viewPortWidth, em, rem);
            yStep = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                    getAttribute(Attributes.HEIGHT), viewPortX, viewPortX, viewPortHeight, em, rem);
            bbox = new Rectangle(0F, 0F, (float) xStep, (float) yStep);
            if (!isZero(xOffset, ZERO) || !isZero(yOffset, ZERO)) {
                patternAffineTransform.translate(xOffset, yOffset);
            }
        }
        return createColoredTilingPatternInstance(patternAffineTransform, bbox, xStep, yStep);
    }

    private PdfPattern.Tiling createColoredTilingPatternInstance(AffineTransform patternAffineTransform,
            Rectangle bbox, double xStep, double yStep) {
        PdfPattern.Tiling coloredTilingPattern = new PdfPattern.Tiling(bbox, (float) xStep, (float) yStep,
                true);
        setPatternMatrix(coloredTilingPattern, patternAffineTransform);
        return coloredTilingPattern;
    }

    private void drawPatternContent(SvgDrawContext context, PdfPattern.Tiling pattern) {
        if (pattern == null) {
            return;
        }
        final PdfCanvas patternCanvas = new PdfPatternCanvas(pattern,
                context.getCurrentCanvas().getDocument());
        context.pushCanvas(patternCanvas);
        try {
            for (final ISvgNodeRenderer renderer : this.getChildren()) {
                renderer.draw(context);
            }
        } finally {
            context.popCanvas();
        }
    }

    private void setPatternMatrix(PdfPattern.Tiling pattern, AffineTransform affineTransform) {
        if (!affineTransform.isIdentity()) {
            final double[] patternMatrix = new double[6];
            affineTransform.getMatrix(patternMatrix);
            pattern.setMatrix(new PdfArray(patternMatrix));
        }
    }

    private boolean isObjectBoundingBoxInPatternUnits() {
        final String patternUnits = getAttribute(Attributes.PATTERN_UNITS);
        if (Values.USER_SPACE_ON_USE.equals(patternUnits)) {
            return false;
        } else if (patternUnits != null && !Values.OBJECT_BOUNDING_BOX.equals(patternUnits)) {
            LoggerFactory.getLogger(this.getClass()).warn(MessageFormatUtil.format(
                    SvgLogMessageConstant.PATTERN_INVALID_PATTERN_UNITS_LOG, patternUnits));
        }
        return true;
    }

    private boolean isObjectBoundingBoxInPatternContentUnits() {
        final String patternContentUnits = getAttribute(Attributes.PATTERN_CONTENT_UNITS);
        if (Values.OBJECT_BOUNDING_BOX.equals(patternContentUnits)) {
            return true;
        } else if (patternContentUnits != null && !Values.USER_SPACE_ON_USE
                .equals(patternContentUnits)) {
            LoggerFactory.getLogger(this.getClass()).warn(MessageFormatUtil.format(
                    SvgLogMessageConstant.PATTERN_INVALID_PATTERN_CONTENT_UNITS_LOG, patternContentUnits));
        }
        return false;
    }

    private static boolean isZero(double val, double delta) {
        return -delta < val && val < delta;
    }
}
