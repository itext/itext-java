/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.canvas.parser.filter;

import com.itextpdf.kernel.geom.LineSegment;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;

/**
 * This {@link IEventFilter} implementation only accepts text render events within the specified
 * rectangular region.
 */
public class TextRegionEventFilter implements IEventFilter {

    private final Rectangle filterRect;

    /**
     * Constructs a filter instance.
     * @param filterRect the rectangle to filter text against
     */
    public TextRegionEventFilter(Rectangle filterRect) {
        this.filterRect = filterRect;
    }

    @Override
    public boolean accept(IEventData data, EventType type) {
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
