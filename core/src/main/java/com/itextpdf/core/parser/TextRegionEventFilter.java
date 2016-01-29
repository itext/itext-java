package com.itextpdf.core.parser;

import com.itextpdf.core.geom.Rectangle;

/**
 * This {@link EventFilter} implementation only accepts text render events within the specified
 * rectangular region.
 */
public class TextRegionEventFilter implements EventFilter {

    private final Rectangle filterRect;

    /**
     * Constructs a filter instance.
     * @param filterRect the rectangle to filter text against
     */
    public TextRegionEventFilter(Rectangle filterRect) {
        this.filterRect = filterRect;
    }

    @Override
    public boolean accept(EventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;

            LineSegment segment = renderInfo.getBaseline();
            Vector startPoint = segment.getStartPoint();
            Vector endPoint = segment.getEndPoint();

            float x1 = startPoint.get(Vector.I1);
            float y1 = startPoint.get(Vector.I2);
            float x2 = endPoint.get(Vector.I1);
            float y2 = endPoint.get(Vector.I2);

            return filterRect == null || filterRect.intersectsLine(x1, y1, x2, y2);
        } else {
            return false;
        }
    }
}
