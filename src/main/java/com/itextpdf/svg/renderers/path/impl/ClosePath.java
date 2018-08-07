package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.SvgConstants;

/***
 * Implements closePath(Z) attribute of SVG's path element
 * */
public class ClosePath extends LineTo {

    @Override
    public Point getEndingPoint() {
        float x = getSvgCoordinate(properties, SvgConstants.Attributes.X);
        float y = getSvgCoordinate(properties, SvgConstants.Attributes.Y);
        return new Point(x,y);
    }

}
