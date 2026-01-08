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
package com.itextpdf.kernel.contrast;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Path;

/**
 * Represents rendering information for a background element in contrast analysis.
 * <p>
 * This class extends {@link ColorInfo} to specifically represent
 * background shapes and their colors, which are used to calculate contrast ratios
 * against text elements for accessibility compliance.
 * <p>
 * Background elements include filled paths, rectangles, and other non-text content
 * that may appear behind text on a PDF page.
 */
public class BackgroundColorInfo extends ColorInfo {

    /**
     * Constructs a new {@link BackgroundColorInfo} with the specified color and path.
     *
     * @param color the fill color of the background element
     * @param path the geometric path defining the shape and position of the background element
     */
    public BackgroundColorInfo(Color color, Path path) {
        super(color, path);
    }

}
