package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgAttributeConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.DrawUtils;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;circle&gt; tag.
 */
public class EllipseSvgNodeRenderer extends AbstractSvgNodeRenderer {

    float cx, cy, rx, ry;

    @Override
    protected void doDraw(SvgDrawContext context) {
        PdfCanvas cv = context.getCurrentCanvas();
        if(setParameters()) {
            cv.moveTo(cx + rx, cy);
            DrawUtils.arc(cx - rx, cy - ry, cx + rx, cy + ry, 0, 360, cv);
        }
    }

    /**
     * Fetches a map of String values by calling getAttribute(Strng s) method
     * and maps it's values to arc parmateter cx, cy , rx, ry respectively
     * @return boolean values to indicate whether all values exit or not
     */
    protected boolean setParameters(){
        cx=0; cy=0;
        if(getAttribute(SvgAttributeConstants.CX_ATTRIBUTE) != null){
            cx = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.CX_ATTRIBUTE));
        }
        if(getAttribute(SvgAttributeConstants.CY_ATTRIBUTE) != null){
            cy = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.CY_ATTRIBUTE));
        }

        if(getAttribute(SvgAttributeConstants.RX_ATTRIBUTE) != null
                && CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.RX_ATTRIBUTE)) >0){
            rx = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.RX_ATTRIBUTE));
        }else{
            return false; //No drawing if rx is absent
        }
        if(getAttribute(SvgAttributeConstants.RY_ATTRIBUTE) != null
                &&CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.RY_ATTRIBUTE)) >0){
            ry = CssUtils.parseAbsoluteLength(getAttribute(SvgAttributeConstants.RY_ATTRIBUTE));
        }else{
            return false; //No drawing if ry is absent
        }
        return true;
    }



}
