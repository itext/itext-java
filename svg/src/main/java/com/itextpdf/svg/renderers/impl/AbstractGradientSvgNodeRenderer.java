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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.gradients.GradientSpreadMethod;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Values;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgPaintServer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TransformUtils;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 * {@link ISvgNodeRenderer} abstract implementation for gradient tags
 * (&lt;linearGradient&gt;, &lt;radialGradient&gt;).
 */
public abstract class AbstractGradientSvgNodeRenderer extends AbstractBranchSvgNodeRenderer implements
        ISvgPaintServer {

    @Override
    protected void doDraw(SvgDrawContext context) {
        throw new UnsupportedOperationException(SvgExceptionMessageConstant.DRAW_NO_DRAW);
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
            getAttribute(Attributes.GRADIENT_UNITS.toLowerCase());
        }
        if (Values.USER_SPACE_ON_USE.equals(gradientUnits)) {
            return false;
        } else if (gradientUnits != null && !Values.OBJECT_BOUNDING_BOX.equals(gradientUnits)) {
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
        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        if (gradientTransform == null) {
            gradientTransform = getAttribute(Attributes.GRADIENT_TRANSFORM.toLowerCase());
        }
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
            spreadMethodValue = getAttribute(Attributes.SPREAD_METHOD.toLowerCase());
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
                LoggerFactory.getLogger(this.getClass()).warn(MessageFormatUtil.format(
                        SvgLogMessageConstant.GRADIENT_INVALID_SPREAD_METHOD_LOG, spreadMethodValue));
                return GradientSpreadMethod.PAD;
        }
    }
}
