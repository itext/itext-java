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
package com.itextpdf.layout.properties;

/**
 * The possible values for {@link InlineVerticalAlignment#getType()}.
 */
public enum InlineVerticalAlignmentType {
    // Strut oriented alignments
    BASELINE,
    TEXT_TOP,
    TEXT_BOTTOM,
    SUB,
    SUPER,
    /**
     * Fixed is used when a length value is given in css.
     * It needs a companion value in {@link InlineVerticalAlignment#setValue(float)}
     */
    FIXED,
    /**
     * Fixed is used when a percentage value is given in css.
     * It needs a companion value in {@link InlineVerticalAlignment#setValue(float)}
     */
    FRACTION,
    // middle of x height above baseline
    MIDDLE,
    // From here alignments are box oriented, the others are strut (text line) oriented
    TOP,
    BOTTOM
}
