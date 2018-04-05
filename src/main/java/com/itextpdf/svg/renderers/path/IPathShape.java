package com.itextpdf.svg.renderers.path;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.renderers.SvgDrawContext;

import java.util.Map;
/**
 * Interface for IPathShape, which draws the Path-data's d element instructions.
 */
public interface IPathShape {
    /**
     * Draws this instruction to a canvas object.
     * @param canvas to which this instruction is drawn
     */
    void draw(PdfCanvas canvas);

    /**
     * Sets the map of attributes that this path instruction needs.
     * @param properties maps key names to values.
     */
    void setProperties(Map<String, String> properties);

    /**
     * @param coordinates an array containing point values for path coordinates
     * This method Mapps point attributes to their respective values
     */
    void setCoordinates(String[] coordinates);
    Map<String, String> getCoordinates();

}
