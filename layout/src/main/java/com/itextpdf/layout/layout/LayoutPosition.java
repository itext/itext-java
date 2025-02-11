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
package com.itextpdf.layout.layout;

/**
 * We use a simplified version of CSS positioning.
 * See https://www.webkit.org/blog/117/webcore-rendering-iv-absolutefixed-and-relative-positioning
 */
public class LayoutPosition {
    /**
     * Default positioning by normal rules of block and line layout.
     */
    public static final int STATIC = 1;

    /**
     * Relative positioning is exactly like static positioning except that the left, top, right and bottom properties
     * can be used to apply a translation to the object. Relative positioning is literally nothing more than a paint-time translation.
     * As far as layout is concerned, the object is at its original position.
     */
    public static final int RELATIVE = 2;

    /**
     * Absolute positioned objects are positioned relative to the containing block, which is the nearest enclosing
     * ancestor block with a position other than 'static'.
     */
    public static final int ABSOLUTE = 3;

    /**
     * Fixed positioned objects are positioned relative to the viewport, i.e., the page area of the current page.
     */
    public static final int FIXED = 4;

}
