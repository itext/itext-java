/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.svg.css.impl;

import com.itextpdf.styledxmlparser.css.resolve.IStyleInheritance;
import com.itextpdf.svg.SvgConstants.Attributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class that allows you to check if a property is inheritable.
 */
public class SvgAttributeInheritance implements IStyleInheritance {

    /**
     * Set of inheritable SVG style attributes in accordance with "https://www.w3.org/TR/SVG2/propidx.html".
     */
    private static final Set<String> inheritableProperties = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            // The following attributes haven't been supported in iText yet:
            // color-interpolation, color-rendering, glyph-orientation-vertical, image-rendering,
            // paint-order, pointer-events, shape-rendering, text-rendering.

            // All the rest are either here or in com.itextpdf.styledxmlparser.css.resolve.CssInheritance
            Attributes.DIRECTION,
            // TODO DEVSIX-5890 Add Support to SVG dominant-baseline attribute
            Attributes.FILL,
            Attributes.FILL_OPACITY,
            Attributes.FILL_RULE,
            Attributes.MARKER,
            Attributes.MARKER_MID,
            Attributes.MARKER_END,
            Attributes.MARKER_START,
            Attributes.STROKE,
            Attributes.STROKE_DASHARRAY,
            Attributes.STROKE_DASHOFFSET,
            Attributes.STROKE_LINECAP,
            Attributes.STROKE_LINEJOIN,
            Attributes.STROKE_MITERLIMIT,
            Attributes.STROKE_OPACITY,
            Attributes.STROKE_WIDTH,
            Attributes.TEXT_ANCHOR,

            // CLIP_RULE isn't from the spec above, but seems it's required according to some tests
            Attributes.CLIP_RULE
    )));

    @Override
    public  boolean isInheritable(String cssProperty) {
        return inheritableProperties.contains(cssProperty);
    }
}
