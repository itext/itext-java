package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TransformUtils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * {@link ISvgNodeRenderer} abstract implementation for gradient tags
 * (&lt;linearGradient&gt;, &lt;radialGradient&gt;).
 */
public abstract class AbstractGradientSvgNodeRenderer extends NoDrawOperationSvgNodeRenderer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        throw new UnsupportedOperationException(SvgLogMessageConstant.DRAW_NO_DRAW);
    }

    /**
     * Creates the {@link Color} that represents the corresponding gradient for specified object box
     *
     * @param context                 the current svg draw context
     * @param objectBoundingBox       the coloring object bounding box without any adjustments
     *                                (additional stroke width or others)
     * @param objectBoundingBoxMargin the objectBoundingBoxMargin of the object bounding box
     *                                to be colored (for example - the part of stroke width
     *                                that exceeds the object bounding box, i.e. the half of stroke
     *                                width value)
     * @param parentOpacity           current parent opacity modifier
     * @return the created color
     */
    public abstract Color createColor(SvgDrawContext context, Rectangle objectBoundingBox,
            float objectBoundingBoxMargin,
            float parentOpacity);

    /**
     * Checks whether the gradient units values are on user space on use or object bounding box
     *
     * @return {@code false} if the 'gradientUnits' value of the gradient tag equals
     * to 'userSpaceOnUse', otherwise {@code true}
     */
    protected boolean isObjectBoundingBoxUnits() {
        String gradientUnits = getAttribute(Attributes.GRADIENT_UNITS);
        if (Values.GRADIENT_UNITS_USER_SPACE_ON_USE.equals(gradientUnits)) {
            return false;
        } else if (gradientUnits != null && !Values.GRADIENT_UNITS_OBJECT_BOUNDING_BOX.equals(gradientUnits)) {
            LoggerFactory.getLogger(this.getClass()).warn(MessageFormatUtil.format(
                    SvgLogMessageConstant.GRADIENT_INVALID_GRADIENT_UNITS_LOG, gradientUnits));
        }
        return true;
    }

    /**
     * Evaluates the 'gradientTransform' transformations
     * @return an {@link AffineTransform} object representing the specified gradient transformation
     */
    protected AffineTransform getGradientTransform() {
        String gradientTransform = getAttribute(Attributes.GRADIENT_TRANSFORM);
        if (gradientTransform != null && !gradientTransform.isEmpty()) {
            return TransformUtils.parseTransform(gradientTransform);
        }
        return null;
    }

    /**
     * Construct a list of child stop renderers
     * @return a list of {@link StopSvgNodeRenderer} elements that represents the child stop values
     */
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
     */
    protected GradientSpreadMethod parseSpreadMethod() {
        String spreadMethodValue = getAttribute(Attributes.SPREAD_METHOD);
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
                LoggerFactory.getLogger(this.getClass()).warn(MessageFormatUtil.format(
                        SvgLogMessageConstant.GRADIENT_INVALID_SPREAD_METHOD_LOG, spreadMethodValue));
                return GradientSpreadMethod.PAD;
        }
    }
}
