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
package com.itextpdf.svg.css.impl;

import com.itextpdf.styledxmlparser.css.resolve.IStyleInheritance;
import com.itextpdf.svg.SvgConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class that allows you to check if a property is inheritable.
 */
public class SvgAttributeInheritance implements IStyleInheritance {

    /**
     * Set of inheritable SVG style attributes
     * in accordance with "http://www.w3schools.com/cssref/"
     * and "https://developer.mozilla.org/en-US/docs/Web/CSS/Reference"
     */
    private static final Set<String> inheritableProperties = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(

            // clip-rule
            SvgConstants.Attributes.CLIP_RULE,

            // fill
            SvgConstants.Attributes.FILL,

            // fill-rule
            SvgConstants.Attributes.FILL_RULE,

            // stroke
            SvgConstants.Attributes.STROKE,

            // stroke-width
            SvgConstants.Attributes.STROKE_WIDTH,

            // text-anchor
            SvgConstants.Attributes.TEXT_ANCHOR

    )));

    @Override
    public  boolean isInheritable(String cssProperty) {
        return inheritableProperties.contains(cssProperty);
    }
}
