package com.itextpdf.svg.renderers.impl;

import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgAttributeConstants;
import com.itextpdf.svg.renderers.SvgDrawContext;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;circle&gt; tag.
 */
public class CircleSvgNodeRenderer extends EllipseSvgNodeRenderer {


    @Override
    protected boolean setParameters(){
        cx=0; cy=0;
        if(getAttribute(SvgAttributeConstants.CX_ATTRIBUTE) != null){
            cx = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.CX_ATTRIBUTE));
        }
        if(getAttribute(SvgAttributeConstants.CY_ATTRIBUTE) != null){
            cy = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.CY_ATTRIBUTE));
        }

        if(getAttribute(SvgAttributeConstants.R_ATTRIBUTE) != null
                && CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.R_ATTRIBUTE)) >0){
            rx = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.R_ATTRIBUTE));
            ry=rx;
        }else{
            return false; //No drawing if rx is absent
        }
        return true;

    }

}
