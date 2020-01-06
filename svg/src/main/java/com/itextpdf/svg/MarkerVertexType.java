/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg;

import com.itextpdf.svg.SvgConstants.Attributes;

/**
 * Defines a property of markable elements (&lt;path&gt;, &lt;line&gt;, &lt;polyline&gt; or
 * &lt;polygon&gt;) which is used to determine at which verticies a marker should be drawn.
 */
public enum MarkerVertexType {
    /**
     * Specifies that marker will be drawn only at the first vertex of element.
     */
    MARKER_START(Attributes.MARKER_START),

    /**
     * Specifies that marker will be drawn at every vertex except the first and last.
     */
    MARKER_MID(Attributes.MARKER_MID),

    /**
     * Specifies that marker will be drawn only at the last vertex of element.
     */
    MARKER_END(Attributes.MARKER_END);

    private final String name;

    private MarkerVertexType(String s) {
        this.name = s;
    }

    public String toString() {
        return this.name;
    }
}
