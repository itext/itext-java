package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgTagConstants;

import java.util.HashMap;
import java.util.Map;

/***
 * Implements curveTo(L) attribute of SVG's path element
 * */
public class QuadraticCurveTo extends AbstractPathShape {
    Map<String, String> properties;

/**
 * Draws a quadratic Bézier curve from the current point to (x,y) using (x1,y1) as the control point
 * */
    @Override
    public void draw(PdfCanvas canvas) {
        canvas.curveTo(
                getCoordinate( properties, SvgTagConstants.X1 ),
                getCoordinate( properties, SvgTagConstants.Y1 ),
                getCoordinate( properties, SvgTagConstants.X ),
                getCoordinate( properties, SvgTagConstants.Y ) );
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public void setCoordinates(String[] coordinates) {
        Map<String, String> map = new HashMap<String, String>();
        map.put( "x1", coordinates.length > 0 && !coordinates[0].isEmpty()? coordinates[0] : "0" );
        map.put( "y1", coordinates.length > 1 && !coordinates[1].isEmpty()? coordinates[1] : "0" );
        map.put( "x", coordinates.length > 2 && !coordinates[2].isEmpty()? coordinates[2] : "0" );
        map.put( "y", coordinates.length > 3 && !coordinates[3].isEmpty()? coordinates[3] : "0" );
        setProperties( map );
    }

    @Override
    public Map<String, String> getCoordinates() {
        return properties;
    }
}
