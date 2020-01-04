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
