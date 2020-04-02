package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Tags;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * {@link ISvgNodeRenderer} implementation for the gradient &lt;stop&gt; tag.
 */
public class StopSvgNodeRenderer extends NoDrawOperationSvgNodeRenderer {

    /**
     * Evaluates the stop color offset value
     *
     * @return the stop color offset value in [0, 1] range
     */
    public double getOffset() {
        Double offset = null;
        String offsetAttribute = getAttribute(Attributes.OFFSET);
        if (CssUtils.isPercentageValue(offsetAttribute)) {
            offset = (double) CssUtils.parseRelativeValue(offsetAttribute, 1);
        } else if (CssUtils.isNumericValue(offsetAttribute)) {
            offset = CssUtils.parseDouble(offsetAttribute);
        }
        double result = offset != null ? offset.doubleValue() : 0d;
        return result > 1d ? 1d : result > 0d ? result : 0d;
    }

    /**
     * Evaluates the rgba array of the specified stop color
     *
     * @return the array of 4 floats which contains the rgba value corresponding
     * to the specified stop color
     */
    public float[] getStopColor() {
        float[] color = null;
        String colorValue = getAttribute(Tags.STOP_COLOR);
        if (colorValue != null) {
            color = WebColors.getRGBAColor(colorValue);
        }
        if (color == null) {
            color = WebColors.getRGBAColor("black");
        }
        return color;
    }

    /**
     * Evaluates the stop opacity of the specified stop color
     *
     * @return the stop opacity value specified in the stop color
     */
    public float getStopOpacity() {
        Float result = null;
        String opacityValue = getAttribute(Tags.STOP_OPACITY);
        if (opacityValue != null && !SvgConstants.Values.NONE.equalsIgnoreCase(opacityValue)) {
            result = CssUtils.parseFloat(opacityValue);
        }
        return result != null ? result.floatValue() : 1f;
    }

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        StopSvgNodeRenderer copy = new StopSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        return copy;
    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        throw new UnsupportedOperationException(SvgLogMessageConstant.DRAW_NO_DRAW);
    }
}
