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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfPatternCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgPaintServer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCoordinateUtils;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.TransformUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for the svg &lt;pattern&gt; tag.
 */
public class PatternSvgNodeRenderer extends AbstractBranchSvgNodeRenderer implements ISvgPaintServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatternSvgNodeRenderer.class);

    private static final double CONVERT_COEFF = 0.75;

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
        if (objectBoundingBox == null) {
            return null;
        }
        if (!context.pushPatternId(getAttribute(Attributes.ID))) {
            // this means that pattern is cycled
            return null;
        }
        try {
            PdfPattern.Tiling tilingPattern = createTilingPattern(context, objectBoundingBox);
            drawPatternContent(context, tilingPattern);
            return (tilingPattern == null) ? null : new PatternColor(tilingPattern);
        } finally {
            context.popPatternId();
        }
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    private PdfPattern.Tiling createTilingPattern(SvgDrawContext context,
                                                  Rectangle objectBoundingBox) {
        final boolean isObjectBoundingBoxInPatternUnits = isObjectBoundingBoxInPatternUnits();
        final boolean isObjectBoundingBoxInPatternContentUnits = isObjectBoundingBoxInPatternContentUnits();

        // evaluate pattern rectangle on target pattern units
        Rectangle originalPatternRectangle = calculateOriginalPatternRectangle(
                context, isObjectBoundingBoxInPatternUnits);

        // get xStep and yStep on target pattern units
        double xStep = originalPatternRectangle.getWidth();
        double yStep = originalPatternRectangle.getHeight();

        if (!xStepYStepAreValid(xStep, yStep)) {
            return null;
        }

        // we have to consider transforming an element that use pattern in corresponding  with SVG logic
        final AffineTransform patternMatrixTransform = context.getCurrentCanvasTransform();

        patternMatrixTransform.concatenate(getPatternTransform());

        if (isObjectBoundingBoxInPatternUnits) {
            patternMatrixTransform.concatenate(getTransformToUserSpaceOnUse(objectBoundingBox));
        }

        patternMatrixTransform.translate(originalPatternRectangle.getX(), originalPatternRectangle.getY());

        final float[] viewBoxValues = SvgCssUtils.parseViewBox(this);
        Rectangle bbox;
        if (viewBoxValues == null || viewBoxValues.length < SvgConstants.Values.VIEWBOX_VALUES_NUMBER) {
            if (isObjectBoundingBoxInPatternUnits != isObjectBoundingBoxInPatternContentUnits) {
                // If pattern units are not the same as pattern content units, then we need to scale
                // the resulted space into a space to draw pattern content. The pattern rectangle origin
                // is already in place, but measures should be adjusted.
                double scaleX, scaleY;
                if (isObjectBoundingBoxInPatternContentUnits) {
                    scaleX = objectBoundingBox.getWidth() / CONVERT_COEFF;
                    scaleY = objectBoundingBox.getHeight() / CONVERT_COEFF;
                } else {
                    scaleX = CONVERT_COEFF / objectBoundingBox.getWidth();
                    scaleY = CONVERT_COEFF / objectBoundingBox.getHeight();
                }
                patternMatrixTransform.scale(scaleX, scaleY);
                xStep /= scaleX;
                yStep /= scaleY;
            }
            bbox = new Rectangle(0F, 0F, (float) xStep, (float) yStep);
        } else {
            if (isViewBoxInvalid(viewBoxValues)) {
                return null;
            }

            // Here we revert scaling to the object's bounding box coordinate system
            // to keep the aspect ratio of the original viewport of the pattern.
            if (isObjectBoundingBoxInPatternUnits) {
                double scaleX = CONVERT_COEFF / objectBoundingBox.getWidth();
                double scaleY = CONVERT_COEFF / objectBoundingBox.getHeight();
                patternMatrixTransform.scale(scaleX, scaleY);
                xStep /= scaleX;
                yStep /= scaleY;
            }

            Rectangle viewBox = new Rectangle(viewBoxValues[0], viewBoxValues[1], viewBoxValues[2], viewBoxValues[3]);
            Rectangle appliedViewBox = calculateAppliedViewBox(viewBox, xStep, yStep);

            double scaleX = (double) appliedViewBox.getWidth() / (double) viewBox.getWidth();
            double scaleY = (double) appliedViewBox.getHeight() / (double) viewBox.getHeight();

            double xOffset = (double) appliedViewBox.getX() / scaleX - (double) viewBox.getX();
            double yOffset = (double) appliedViewBox.getY() / scaleY - (double) viewBox.getY();

            patternMatrixTransform.translate(xOffset, yOffset);

            patternMatrixTransform.scale(scaleX, scaleY);
            xStep /= scaleX;
            yStep /= scaleY;

            double bboxXOriginal = -xOffset / scaleX;
            double bboxYOriginal = -yOffset / scaleY;
            bbox = new Rectangle((float) bboxXOriginal, (float) bboxYOriginal, (float) xStep, (float) yStep);
        }

        return createColoredTilingPatternInstance(patternMatrixTransform, bbox, xStep, yStep);
    }

    private Rectangle calculateAppliedViewBox(Rectangle viewBox, double xStep, double yStep) {
        String[] preserveAspectRatio = retrieveAlignAndMeet();
        Rectangle patternRect = new Rectangle(0f, 0f, (float) xStep, (float) yStep);
        return SvgCoordinateUtils.applyViewBox(viewBox, patternRect, preserveAspectRatio[0], preserveAspectRatio[1]);
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

    private Rectangle calculateOriginalPatternRectangle(SvgDrawContext context,
            boolean isObjectBoundingBoxInPatternUnits) {
        double xOffset, yOffset, xStep, yStep;
        if (isObjectBoundingBoxInPatternUnits) {
            xOffset = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(Attributes.X), 0) * CONVERT_COEFF;
            yOffset = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(Attributes.Y), 0) * CONVERT_COEFF;
            xStep = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(Attributes.WIDTH), 0) * CONVERT_COEFF;
            yStep = SvgCoordinateUtils.getCoordinateForObjectBoundingBox(
                    getAttribute(Attributes.HEIGHT), 0) * CONVERT_COEFF;
        } else {
            final Rectangle currentViewPort = context.getCurrentViewPort();
            final double viewPortX = currentViewPort.getX();
            final double viewPortY = currentViewPort.getY();
            final double viewPortWidth = currentViewPort.getWidth();
            final double viewPortHeight = currentViewPort.getHeight();
            final float em = getCurrentFontSize(context);
            final float rem = context.getCssContext().getRootFontSize();
            // get pattern coordinates in userSpaceOnUse coordinate system
            xOffset = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                    getAttribute(Attributes.X), viewPortX, viewPortX, viewPortWidth, em, rem);
            yOffset = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                    getAttribute(Attributes.Y), viewPortY, viewPortY, viewPortHeight, em, rem);
            xStep = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                    getAttribute(Attributes.WIDTH), viewPortX, viewPortX, viewPortWidth, em, rem);
            yStep = SvgCoordinateUtils.getCoordinateForUserSpaceOnUse(
                    getAttribute(Attributes.HEIGHT), viewPortY, viewPortY, viewPortHeight, em, rem);
        }
        return new Rectangle((float) xOffset, (float) yOffset, (float) xStep, (float) yStep);
    }

    private boolean isObjectBoundingBoxInPatternUnits() {
        String patternUnits = getAttribute(Attributes.PATTERN_UNITS);
        if (patternUnits == null) {
            patternUnits = getAttribute(Attributes.PATTERN_UNITS.toLowerCase());
        }
        if (Values.USER_SPACE_ON_USE.equals(patternUnits)) {
            return false;
        } else if (patternUnits != null && !Values.OBJECT_BOUNDING_BOX.equals(patternUnits)) {
            LoggerFactory.getLogger(this.getClass()).warn(MessageFormatUtil.format(
                    SvgLogMessageConstant.PATTERN_INVALID_PATTERN_UNITS_LOG, patternUnits));
        }
        return true;
    }

    private boolean isObjectBoundingBoxInPatternContentUnits() {
        String patternContentUnits = getAttribute(Attributes.PATTERN_CONTENT_UNITS);
        if (patternContentUnits == null) {
            patternContentUnits = getAttribute(Attributes.PATTERN_CONTENT_UNITS.toLowerCase());
        }
        if (Values.OBJECT_BOUNDING_BOX.equals(patternContentUnits)) {
            return true;
        } else if (patternContentUnits != null && !Values.USER_SPACE_ON_USE
                .equals(patternContentUnits)) {
            LoggerFactory.getLogger(this.getClass()).warn(MessageFormatUtil.format(
                    SvgLogMessageConstant.PATTERN_INVALID_PATTERN_CONTENT_UNITS_LOG, patternContentUnits));
        }
        return false;
    }

    private static PdfPattern.Tiling createColoredTilingPatternInstance(AffineTransform patternAffineTransform,
            Rectangle bbox, double xStep, double yStep) {
        PdfPattern.Tiling coloredTilingPattern = new PdfPattern.Tiling(bbox, (float) xStep, (float) yStep,
                true);
        setPatternMatrix(coloredTilingPattern, patternAffineTransform);
        return coloredTilingPattern;
    }

    private static void setPatternMatrix(PdfPattern.Tiling pattern, AffineTransform affineTransform) {
        if (!affineTransform.isIdentity()) {
            final double[] patternMatrix = new double[6];
            affineTransform.getMatrix(patternMatrix);
            pattern.setMatrix(new PdfArray(patternMatrix));
        }
    }

    private static AffineTransform getTransformToUserSpaceOnUse(Rectangle objectBoundingBox) {
        AffineTransform transform = new AffineTransform();
        transform.translate(objectBoundingBox.getX(), objectBoundingBox.getY());
        transform.scale(objectBoundingBox.getWidth() / CONVERT_COEFF,
                objectBoundingBox.getHeight() / CONVERT_COEFF);
        return transform;
    }

    private static boolean xStepYStepAreValid(double xStep, double yStep) {
        if (xStep < 0 || yStep < 0) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(MessageFormatUtil
                        .format(SvgLogMessageConstant.PATTERN_WIDTH_OR_HEIGHT_IS_NEGATIVE));
            }
            return false;
        } else if (xStep == 0 || yStep == 0) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(MessageFormatUtil
                        .format(SvgLogMessageConstant.PATTERN_WIDTH_OR_HEIGHT_IS_ZERO));
            }
            return false;
        } else {
            return true;
        }
    }

    private static boolean isViewBoxInvalid(float[] viewBoxValues) {
        // if viewBox width or height is zero we should disable rendering
        // of the element (according to the viewBox documentation)
        if (viewBoxValues[2] == 0 || viewBoxValues[3] == 0) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(SvgLogMessageConstant.VIEWBOX_WIDTH_OR_HEIGHT_IS_ZERO);
            }
            return true;
        } else {
            return false;
        }
    }

    private AffineTransform getPatternTransform() {
        String patternTransform = getAttribute(SvgConstants.Attributes.PATTERN_TRANSFORM);
        if (patternTransform == null) {
            patternTransform = getAttribute(SvgConstants.Attributes.PATTERN_TRANSFORM.toLowerCase());
        }
        if (patternTransform != null && !patternTransform.isEmpty()) {
            return TransformUtils.parseTransform(patternTransform);
        }
        return new AffineTransform();
    }
}
