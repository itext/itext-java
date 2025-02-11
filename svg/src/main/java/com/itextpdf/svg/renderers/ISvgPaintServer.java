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
package com.itextpdf.svg.renderers;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;

/**
 * Interface for working with paint servers. These are the elements that are referenced from the fill or stroke of an
 * object.
 */
public interface ISvgPaintServer extends INoDrawSvgNodeRenderer {
    /**
     * Creates the {@link Color} that represents the corresponding paint server for specified object box.
     *
     * @param context                 the current svg draw context
     * @param objectBoundingBox       the coloring object bounding box without any adjustments
     *                                (additional stroke width or others)
     * @param objectBoundingBoxMargin the objectBoundingBoxMargin of the object bounding box
     *                                to be colored (for example - the part of stroke width
     *                                that exceeds the object bounding box, i.e. the half of stroke
     *                                width value)
     * @param parentOpacity           current parent opacity modifier
     * @return the created color
     */
    Color createColor(SvgDrawContext context, Rectangle objectBoundingBox, float objectBoundingBoxMargin,
            float parentOpacity);
}
