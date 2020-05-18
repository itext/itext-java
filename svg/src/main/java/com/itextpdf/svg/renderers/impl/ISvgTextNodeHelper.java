package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.TextRectangle;


/**
 * An interface containing a method to simplify working with SVG text elements.
 * Must be removed in update 7.3 as the methods of this interface will be moved to {@link ISvgTextNodeRenderer}
 */
@Deprecated
public interface ISvgTextNodeHelper {
    /**
     * Return the bounding rectangle of the text element.
     *
     * @param context current {@link SvgDrawContext}
     * @param basePoint end point of previous text element
     * @return created instance of {@link TextRectangle}
     */
    // TODO DEVSIX-3814 This method should be moved to ISvgTextNodeRenderer in 7.2 and this class should be removed
    TextRectangle getTextRectangle(SvgDrawContext context, Point basePoint);
}
