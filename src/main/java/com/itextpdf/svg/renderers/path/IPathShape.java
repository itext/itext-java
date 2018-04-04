package com.itextpdf.svg.renderers.path;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

import java.util.Map;

public interface IPathShape {
    void draw(PdfCanvas canvas);
    void setProperties(Map<String, String> properties);

    void setCoordinates(String[] coordinates);
    Map<String, String> getCoordinates();

}
