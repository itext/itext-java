package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgTagConstants;

import java.util.HashMap;
import java.util.Map;

/***
 * Implements lineTo(L) attribute of SVG's path element
 * */
public class LineTo extends AbstractPathShape{

    Map<String, String> properties;
    @Override
    public void draw(PdfCanvas canvas) {
        canvas.lineTo( getCoordinate( properties, SvgTagConstants.X ), getCoordinate( properties, SvgTagConstants.Y ) );
    }

    @Override
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public void setCoordinates(String[] coordinates) {
        Map<String, String> map = new HashMap<String, String>();
        map.put( "x", coordinates.length > 0 && !coordinates[0].isEmpty() ? coordinates[0] : "0" );
        map.put( "y", coordinates.length > 1 && !coordinates[1].isEmpty() ? coordinates[1] : "0" );
        setProperties( map );
    }

}
