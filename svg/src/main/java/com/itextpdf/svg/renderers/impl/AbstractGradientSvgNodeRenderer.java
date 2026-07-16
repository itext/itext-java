/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.commons.logs.LazyLogger;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.commons.utils.StringNormalizer;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.gradients.GradientColorStop;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.kernel.colors.gradients.IGradientBuilder;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgPaintServer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TemplateResolveUtils;
import com.itextpdf.svg.utils.TransformUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} abstract implementation for gradient tags
 * (&lt;linearGradient&gt;, &lt;radialGradient&gt;).
 */
public abstract class AbstractGradientSvgNodeRenderer extends AbstractBranchSvgNodeRenderer implements
        ISvgPaintServer {

    protected static final double CONVERT_COEFF = 0.75;

    /**
     * Gradient node renderers are not directly drawable.
     *
     * @param context the object that knows the place to draw this element and
     *                maintains its state
     * @throws UnsupportedOperationException always, because gradient definitions cannot be drawn as nodes
     */
    @Override
    protected void doDraw(SvgDrawContext context) {
        throw new UnsupportedOperationException(SvgExceptionMessageConstant.DRAW_NO_DRAW);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Color createColor(SvgDrawContext context, Rectangle objectBoundingBox, float objectBoundingBoxMargin,
            float parentOpacity) {
        if (objectBoundingBox == null) {
            return null;
        }

        // createColor is an entry point for gradients when drawing svg, so resolving href values here
        TemplateResolveUtils.resolve(this, context);

        IGradientBuilder builder =
                createGradientBuilderAndConfigureGeometry(context, objectBoundingBox);
        if (builder == null) {
            return null;
        }

        configureGradientBuilderStopsAndSpread(builder, parentOpacity);

        return builder.buildColor(
                objectBoundingBox.applyMargins(objectBoundingBoxMargin, objectBoundingBoxMargin,
                        objectBoundingBoxMargin, objectBoundingBoxMargin, true),
                context.getCurrentCanvasTransform(), context.getCurrentCanvas().getDocument()
        );
    }

    /**
     * Creates and configures gradient builder specific to concrete gradient type.
     *
     * @param context the current svg draw context
     * @param objectBoundingBox target element bounding box
     *
     * @return the configured builder instance for this renderer type
     *
     * @deprecated deprecated in failure of making abstract
     */
    @Deprecated
    protected IGradientBuilder createGradientBuilderAndConfigureGeometry(
            SvgDrawContext context, Rectangle objectBoundingBox) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isHidden() {
        return CommonCssConstants.NONE.equals(this.attributesAndStyles.get(CommonCssConstants.DISPLAY));
    }

    /**
     * Checks whether the gradient units values are on user space on use or object bounding box
     *
     * @return {@code false} if the 'gradientUnits' value of the gradient tag equals
     * to 'userSpaceOnUse', otherwise {@code true}
     */
    protected boolean isObjectBoundingBoxUnits() {
        String gradientUnits = getAttribute(Attributes.GRADIENT_UNITS);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (gradientUnits == null) {
            gradientUnits = getAttribute(StringNormalizer.toLowerCase(Attributes.GRADIENT_UNITS));
        }
        if (Values.USER_SPACE_ON_USE.equals(gradientUnits)) {
            return false;
        } else if (gradientUnits != null && !Values.OBJECT_BOUNDING_BOX.equals(gradientUnits)) {
            final String gradientUnitsToLog = gradientUnits;
            new LazyLogger(this.getClass()).warn(() -> MessageFormatUtil.format(
                    SvgLogMessageConstant.GRADIENT_INVALID_GRADIENT_UNITS_LOG, gradientUnitsToLog));
        }
        return true;
    }

    /**
     * Evaluates the 'gradientTransform' transformations
     * @return an {@link AffineTransform} object representing the specified gradient transformation
     *
     * @deprecated will become private
     */
    @Deprecated
    protected AffineTransform getGradientTransform() {
        String gradientTransform = getAttribute(Attributes.GRADIENT_TRANSFORM);
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (gradientTransform == null) {
            gradientTransform = getAttribute(StringNormalizer.toLowerCase(Attributes.GRADIENT_TRANSFORM));
        }
        if (gradientTransform != null && !gradientTransform.isEmpty()) {
            return TransformUtils.parseTransform(gradientTransform);
        }
        return null;
    }

    /**
     * Creates a transformation from the gradient coordinate space to user space.
     *
     * @param objectBoundingBox target element bounding box
     * @param isObjectBoundingBox whether gradient units are object-bounding-box based
     * @return a composed {@link AffineTransform} to use for gradient rendering
     */
    protected AffineTransform getGradientTransformToUserSpaceOnUse(Rectangle objectBoundingBox,
            boolean isObjectBoundingBox) {
        AffineTransform gradientTransform = new AffineTransform();
        if (isObjectBoundingBox) {
            gradientTransform.translate(objectBoundingBox.getX(), objectBoundingBox.getY());
            // We need to scale with dividing the lengths by 0.75 as further we should
            // concatenate gradient transformation matrix which has no absolute parsing.
            // For example, if gradientTransform is set to translate(1, 1) and gradientUnits
            // is set to "objectBoundingBox" then the gradient should be shifted horizontally
            // and vertically exactly by the size of the element bounding box. So, again,
            // as we parse translate(1, 1) to translation(0.75, 0.75) the bounding box in
            // the gradient vector space should be 0.75x0.75 in order for such translation
            // to shift by the complete size of bounding box.
            gradientTransform
                    .scale(objectBoundingBox.getWidth() / CONVERT_COEFF, objectBoundingBox.getHeight() / CONVERT_COEFF);
        }

        AffineTransform svgGradientTransformation = getGradientTransform();
        if (svgGradientTransformation != null) {
            gradientTransform.concatenate(svgGradientTransformation);
        }
        return gradientTransform;
    }



    /**
     * Construct a list of child stop renderers
     * @return a list of {@link StopSvgNodeRenderer} elements that represents the child stop values
     *
     * @deprecated will become private
     */
    @Deprecated
    protected List<StopSvgNodeRenderer> getChildStopRenderers() {
        List<StopSvgNodeRenderer> stopRenderers = new ArrayList<>();
        for (ISvgNodeRenderer child : getChildren()) {
            if (child instanceof StopSvgNodeRenderer) {
                stopRenderers.add((StopSvgNodeRenderer) child);
            }
        }
        return stopRenderers;
    }

    /**
     * Parses the gradient spread method
     * @return the parsed {@link GradientSpreadMethod} specified in the gradient
     *
     * @deprecated will become private
     */
    @Deprecated
    protected GradientSpreadMethod parseSpreadMethod() {
        String spreadMethodValue = getAttribute(Attributes.SPREAD_METHOD);
        if (spreadMethodValue == null) {
            spreadMethodValue = getAttribute(StringNormalizer.toLowerCase(Attributes.SPREAD_METHOD));
        }
        if (spreadMethodValue == null) {
            // returning svg default spread method
            return GradientSpreadMethod.PAD;
        }
        switch (spreadMethodValue) {
            case Values.SPREAD_METHOD_PAD:
                return GradientSpreadMethod.PAD;
            case Values.SPREAD_METHOD_REFLECT:
                return GradientSpreadMethod.REFLECT;
            case Values.SPREAD_METHOD_REPEAT:
                return GradientSpreadMethod.REPEAT;
            default:
                final String spreadMethodToLog = spreadMethodValue;
                new LazyLogger(this.getClass()).warn(() -> MessageFormatUtil.format(
                        SvgLogMessageConstant.GRADIENT_INVALID_SPREAD_METHOD_LOG, spreadMethodToLog));
                return GradientSpreadMethod.PAD;
        }
    }

    private void configureGradientBuilderStopsAndSpread(IGradientBuilder builder, float parentOpacity) {
        for (GradientColorStop stopColor : parseStops(parentOpacity)) {
            builder.addStopColor(stopColor);
        }
        builder.setSpread(parseSpreadMethod());
    }

    /**
     * Parses gradient stop children into a normalized list of color stops.
     *
     * @param parentOpacity parent element opacity; currently reserved for future stop opacity support
     * @return a list of parsed and normalized {@link GradientColorStop} instances
     */
    // TODO: DEVSIX-4136 opacity is not supported now.
    //  The opacity should be equal to 'parentOpacity * stopRenderer.getStopOpacity() * stopColor[3]'
    private List<GradientColorStop> parseStops(float parentOpacity) {
        List<GradientColorStop> stopsList = new ArrayList<>();
        for (StopSvgNodeRenderer stopRenderer : getChildStopRenderers()) {
            float[] stopColor = stopRenderer.getStopColor();
            double offset = stopRenderer.getOffset();
            stopsList.add(new GradientColorStop(stopColor, offset, OffsetType.RELATIVE));
        }

        if (!stopsList.isEmpty()) {
            GradientColorStop firstStop = stopsList.get(0);
            if (firstStop.getOffset() > 0) {
                stopsList.add(0, new GradientColorStop(firstStop, 0F, OffsetType.RELATIVE));
            }

            GradientColorStop lastStop = stopsList.get(stopsList.size() - 1);
            if (lastStop.getOffset() < 1) {
                stopsList.add(new GradientColorStop(lastStop, 1F, OffsetType.RELATIVE));
            }
        }
        return stopsList;
    }
}
