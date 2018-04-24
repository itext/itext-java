package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.renderers.path.IPathShape;

import java.util.Map;

/***
 * Implements closePath(Z) attribute of SVG's path element
 * */
public class ClosePath extends AbstractPathShape{

    @Override
    public void draw(PdfCanvas canvas) {
        canvas.closePathStroke();
    }

    @Override
    public void setProperties(Map<String, String> properties) {

    }

    @Override
    public void setCoordinates(String[] coordinates) {

    }
}
