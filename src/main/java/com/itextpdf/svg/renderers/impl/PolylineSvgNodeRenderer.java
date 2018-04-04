package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.utils.SvgCssUtils;



import java.util.ArrayList;
import java.util.List;

/**
 * {@link ISvgNodeRenderer} implementation for the &lt;polyline&gt; tag.
 */
public class PolylineSvgNodeRenderer extends AbstractSvgNodeRenderer {

    /**
     * A List of {@link Point} objects representing the path to be drawn by the polyline tag
     */
    protected List<Point> points = new ArrayList<>();

    protected List<Point> getPoints() {
        return this.points;
    }

    /**
     * Parses a string of space separated x,y pairs into individual {@link Point} objects and appends them to{@link PolylineSvgNodeRenderer#points}.
     * Throws an {@link SvgProcessingException} if pointsAttribute does not have a valid list of numerical x,y pairs.
     *
     * @param pointsAttribute A string of space separated x,y value pairs
     */
    protected void setPoints(String pointsAttribute) {
        if (pointsAttribute == null) {
            return;
        }

        List<String> points = SvgCssUtils.splitValueList(pointsAttribute);
        if (points.size() % 2 != 0) {
            throw new SvgProcessingException(SvgLogMessageConstant.POINTS_ATTRIBUTE_INVALID_LIST).setMessageParams(pointsAttribute);
        }

        float x, y;
        for (int i = 0; i < points.size(); i = i + 2) {
            x = CssUtils.parseAbsoluteLength(points.get(i));
            y = CssUtils.parseAbsoluteLength(points.get(i + 1));
            this.points.add(new Point(x, y));

        }

    }

    @Override
    protected void doDraw(SvgDrawContext context) {
        String pointsAttribute = attributesAndStyles.containsKey(SvgTagConstants.POINTS) ? attributesAndStyles.get(SvgTagConstants.POINTS) : null;
        setPoints(pointsAttribute);

        PdfCanvas canvas = context.getCurrentCanvas();
        if (points.size() > 1) {
            Point currentPoint = points.get(0);
            canvas.moveTo(currentPoint.getX(), currentPoint.getY());
            for (int x = 1; x < points.size(); x++) {
                currentPoint = points.get(x);
                canvas.lineTo(currentPoint.getX(), currentPoint.getY());
            }
        }


    }
}
