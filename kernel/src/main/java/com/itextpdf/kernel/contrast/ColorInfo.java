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
 * Abstract base class representing rendering information for contrast analysis.
 * <p>
 * This class encapsulates the common properties shared by all render information types
 * used in contrast checking: the color and geometric path of the rendered element.
 * <p>
 * Subclasses should extend this class to represent specific types of rendered content,
 * such as text or background elements.
 */
public abstract class ColorInfo {
    private final Color color;
    private final Path path;

    /**
     * Constructs a new ContrastInformationRenderInfo with the specified color and path.
     *
     * @param color the color of the rendered element
     * @param path the geometric path defining the shape and position of the rendered element
     */
    public ColorInfo(Color color, Path path) {
        this.color = color;
        this.path = path;
    }

    /**
     * Gets the color of the rendered element.
     *
     * @return the color of this render information
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the geometric path of the rendered element.
     * <p>
     * The path defines the shape and position of the element on the page.
     *
     * @return the path of this render information
     */
    public Path getPath() {
        return path;
    }

}
