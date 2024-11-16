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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.MarkerVertexType;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.IMarkerCapable;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;
import com.itextpdf.svg.utils.SvgTextUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;marker&gt; tag.
 */
public class MarkerSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {

    // Default marker width in point units (3 px)
    private static final float DEFAULT_MARKER_WIDTH = 2.25f;

    // Default marker height in point units (3 px)
    private static final float DEFAULT_MARKER_HEIGHT = 2.25f;

    // Default refX value
    private static final float DEFAULT_REF_X = 0f;

    // Default refY value
    private static final float DEFAULT_REF_Y = 0f;

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        MarkerSvgNodeRenderer copy = new MarkerSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    @Override
    public Rectangle getObjectBoundingBox(SvgDrawContext context) {
        return null;
    }

    @Override
    void preDraw(SvgDrawContext context) {
        super.preDraw(context);
        float[] markerWidthHeight = getMarkerWidthHeightValues();
        float markerWidth = markerWidthHeight[0];
        float markerHeight = markerWidthHeight[1];
        String xAttribute = this.getAttribute(SvgConstants.Attributes.X);
        String yAttribute = this.getAttribute(SvgConstants.Attributes.Y);
        float x = xAttribute == null ? 0f : CssDimensionParsingUtils.parseAbsoluteLength(xAttribute);
        float y = yAttribute == null ? 0f : CssDimensionParsingUtils.parseAbsoluteLength(yAttribute);
        Rectangle markerViewport = new Rectangle(x, y, markerWidth, markerHeight);
        context.addViewPort(markerViewport);
    }

    void applyMarkerAttributes(SvgDrawContext context) {
        applyRotation(context);
        applyUserSpaceScaling(context);
        applyCoordinatesTranslation(context);
    }

    static void drawMarker(SvgDrawContext context, String moveX, String moveY, MarkerVertexType markerToUse,
            AbstractSvgNodeRenderer parent) {
        String elementToReUse = parent.attributesAndStyles.get(markerToUse.toString());
        String normalizedName = SvgTextUtil.filterReferenceValue(elementToReUse);
        ISvgNodeRenderer template = context.getNamedObject(normalizedName);
        //Clone template
        ISvgNodeRenderer namedObject = template == null ? null : template.createDeepCopy();
        if (namedObject instanceof MarkerSvgNodeRenderer &&
                // Having markerWidth or markerHeight with negative or zero value disables rendering of the element .
                markerWidthHeightAreCorrect((MarkerSvgNodeRenderer) namedObject)) {
            // setting the parent of the referenced element to this instance
            namedObject.setParent(parent);
            namedObject.setAttribute(SvgConstants.Tags.MARKER, markerToUse.toString());
            namedObject.setAttribute(SvgConstants.Attributes.X, moveX);
            namedObject.setAttribute(SvgConstants.Attributes.Y, moveY);
            namedObject.draw(context);
            // unsetting the parent of the referenced element
            namedObject.setParent(null);
        }
    }

    @Override
    protected void applyViewBox(SvgDrawContext context) {
        if (this.attributesAndStyles != null) {
            float[] markerWidthHeight = getMarkerWidthHeightValues();
            float markerWidth = markerWidthHeight[0];
            float markerHeight = markerWidthHeight[1];
            float[] values = getViewBoxValues(markerWidth, markerHeight);
            Rectangle currentViewPort = context.getCurrentViewPort();
            super.calculateAndApplyViewBox(context, values, currentViewPort);
        }
    }

    private float[] getMarkerWidthHeightValues() {
        float markerWidth = DEFAULT_MARKER_WIDTH;
        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.MARKER_WIDTH)) {
            String markerWidthRawValue = attributesAndStyles.get(SvgConstants.Attributes.MARKER_WIDTH);
            markerWidth = CssDimensionParsingUtils.parseAbsoluteLength(markerWidthRawValue);
        } else if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.MARKER_WIDTH.toLowerCase())) {
            // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
            String markerWidthRawValue = attributesAndStyles.get(SvgConstants.Attributes.MARKER_WIDTH.toLowerCase());
            markerWidth = CssDimensionParsingUtils.parseAbsoluteLength(markerWidthRawValue);
        }
        float markerHeight = DEFAULT_MARKER_HEIGHT;
        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.MARKER_HEIGHT)) {
            String markerHeightRawValue = attributesAndStyles.get(SvgConstants.Attributes.MARKER_HEIGHT);
            markerHeight = CssDimensionParsingUtils.parseAbsoluteLength(markerHeightRawValue);
        } else if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.MARKER_HEIGHT.toLowerCase())) {
            // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
            String markerHeightRawValue = attributesAndStyles.get(SvgConstants.Attributes.MARKER_HEIGHT.toLowerCase());
            markerHeight = CssDimensionParsingUtils.parseAbsoluteLength(markerHeightRawValue);
        }
        return new float[] {markerWidth, markerHeight};
    }

    private static boolean markerWidthHeightAreCorrect(MarkerSvgNodeRenderer namedObject) {
        Logger log = LoggerFactory.getLogger(MarkerSvgNodeRenderer.class);
        String markerWidth = namedObject.getAttribute(SvgConstants.Attributes.MARKER_WIDTH);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (markerWidth == null) {
            markerWidth = namedObject.getAttribute(SvgConstants.Attributes.MARKER_WIDTH.toLowerCase());
        }
        String markerHeight = namedObject.getAttribute(SvgConstants.Attributes.MARKER_HEIGHT);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (markerHeight == null) {
            markerHeight = namedObject.getAttribute(SvgConstants.Attributes.MARKER_HEIGHT.toLowerCase());
        }
        boolean isCorrect = true;
        if (markerWidth != null) {
            float absoluteMarkerWidthValue = CssDimensionParsingUtils.parseAbsoluteLength(markerWidth);
            if (absoluteMarkerWidthValue == 0) {
                log.warn(SvgLogMessageConstant.MARKER_WIDTH_IS_ZERO_VALUE);
                isCorrect = false;
            } else if (absoluteMarkerWidthValue < 0) {
                log.warn(SvgLogMessageConstant.MARKER_WIDTH_IS_NEGATIVE_VALUE);
                isCorrect = false;
            }
        }
        if (markerHeight != null) {
            float absoluteMarkerHeightValue = CssDimensionParsingUtils.parseAbsoluteLength(markerHeight);
            if (absoluteMarkerHeightValue == 0) {
                log.warn(SvgLogMessageConstant.MARKER_HEIGHT_IS_ZERO_VALUE);
                isCorrect = false;
            } else if (absoluteMarkerHeightValue < 0) {
                log.warn(SvgLogMessageConstant.MARKER_HEIGHT_IS_NEGATIVE_VALUE);
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    private void applyRotation(SvgDrawContext context) {
        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.ORIENT)) {
            String orient = this.attributesAndStyles.get(SvgConstants.Attributes.ORIENT);
            double rotAngle = Double.NaN;
            // If placed by marker-start, the marker is oriented 180° different from
            // the orientation that would be used if auto was specified.
            // For all other markers, auto-start-reverse means the same as auto.
            if (SvgConstants.Values.AUTO.equals(orient) || (SvgConstants.Values.AUTO_START_REVERSE.equals(orient)
                    && !SvgConstants.Attributes.MARKER_START
                    .equals(this.attributesAndStyles.get(SvgConstants.Tags.MARKER)))) {
                rotAngle = ((IMarkerCapable) getParent()).getAutoOrientAngle(this, false);
            } else if (SvgConstants.Values.AUTO_START_REVERSE.equals(orient) && SvgConstants.Attributes.MARKER_START
                    .equals(this.attributesAndStyles.get(SvgConstants.Tags.MARKER))) {
                rotAngle = ((IMarkerCapable) getParent()).getAutoOrientAngle(this, true);
            } else if (CssTypesValidationUtils.isAngleValue(orient) || CssTypesValidationUtils.isNumber(orient)) {
                rotAngle = CssDimensionParsingUtils.parseAngle(this.attributesAndStyles.get(SvgConstants.Attributes.ORIENT));
            }
            if (!Double.isNaN(rotAngle)) {
                context.getCurrentCanvas().concatMatrix(AffineTransform.getRotateInstance(rotAngle));
            }
        }
    }

    private void applyUserSpaceScaling(SvgDrawContext context) {
        boolean markerUnitsEqualsStrokeWidth =
                !this.attributesAndStyles.containsKey(SvgConstants.Attributes.MARKER_UNITS) ||
                        SvgConstants.Values.STROKEWIDTH.equals(
                                this.attributesAndStyles.get(SvgConstants.Attributes.MARKER_UNITS));
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        boolean markerUnitsLowerEqualsStrokeWidth =
                !this.attributesAndStyles.containsKey(SvgConstants.Attributes.MARKER_UNITS.toLowerCase()) ||
                        SvgConstants.Values.STROKEWIDTH.equals(
                                this.attributesAndStyles.get(SvgConstants.Attributes.MARKER_UNITS.toLowerCase()));
        if (markerUnitsEqualsStrokeWidth && markerUnitsLowerEqualsStrokeWidth) {
            String parentValue = this.getParent().getAttribute(SvgConstants.Attributes.STROKE_WIDTH);
            if (parentValue != null) {
                // If stroke width is a percentage value is always computed as a percentage of the normalized viewBox diagonal length.
                double rootViewPortHeight = context.getRootViewPort().getHeight();
                double rootViewPortWidth = context.getRootViewPort().getWidth();
                double viewBoxDiagonalLength = CssUtils.convertPxToPts(Math
                        .sqrt(rootViewPortHeight * rootViewPortHeight + rootViewPortWidth * rootViewPortWidth));
                float strokeWidthScale = CssUtils
                        .convertPtsToPx(SvgCssUtils.parseAbsoluteLength(
                                this, parentValue, (float) viewBoxDiagonalLength, 1f, context));
                context.getCurrentCanvas()
                        .concatMatrix(AffineTransform.getScaleInstance(strokeWidthScale, strokeWidthScale));
            }
        }
    }

    private void applyCoordinatesTranslation(SvgDrawContext context) {
        float xScale = 1;
        float yScale = 1;
        float[] viewBox = SvgCssUtils.parseViewBox(this);
        if (viewBox != null && viewBox.length == SvgConstants.Values.VIEWBOX_VALUES_NUMBER) {
            xScale = context.getCurrentViewPort().getWidth() / viewBox[2];
            yScale = context.getCurrentViewPort().getHeight() / viewBox[3];
        }
        float moveX = DEFAULT_REF_X;
        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.REFX)) {
            String refX = this.attributesAndStyles.get(SvgConstants.Attributes.REFX);
            moveX = SvgCssUtils.parseAbsoluteLength(this, refX, context.getRootViewPort().getWidth(), moveX, context);
            //Apply scale
            moveX *= -1 * xScale;
        } else if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.REFX.toLowerCase())) {
            // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
            String refX = this.attributesAndStyles.get(SvgConstants.Attributes.REFX.toLowerCase());
            moveX = SvgCssUtils.parseAbsoluteLength(this, refX, context.getRootViewPort().getWidth(), moveX, context);
            //Apply scale
            moveX *= -1 * xScale;
        }
        float moveY = DEFAULT_REF_Y;
        if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.REFY)) {
            String refY = this.attributesAndStyles.get(SvgConstants.Attributes.REFY);
            moveY = SvgCssUtils.parseAbsoluteLength(this, refY, context.getRootViewPort().getHeight(), moveY, context);
            moveY *= -1 * yScale;
        } else if (this.attributesAndStyles.containsKey(SvgConstants.Attributes.REFY.toLowerCase())) {
            // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
            String refY = this.attributesAndStyles.get(SvgConstants.Attributes.REFY.toLowerCase());
            moveY = SvgCssUtils.parseAbsoluteLength(this, refY, context.getRootViewPort().getHeight(), moveY, context);
            moveY *= -1 * yScale;
        }
        AffineTransform translation = AffineTransform.getTranslateInstance(moveX, moveY);
        if (!translation.isIdentity()) {
            context.getCurrentCanvas().concatMatrix(translation);
        }
    }

    private float[] getViewBoxValues(float defaultWidth, float defaultHeight) {
        float[] values = SvgCssUtils.parseViewBox(this);
        if (values == null || values.length < SvgConstants.Values.VIEWBOX_VALUES_NUMBER) {
            //If viewBox is not specified or incorrect, it's width and height are the same as passed defaults
            return new float[] {0, 0, defaultWidth, defaultHeight};
        } else {
            return values;
        }
    }
}

