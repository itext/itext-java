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
package com.itextpdf.svg.renderers.path;

import com.itextpdf.svg.renderers.path.impl.PathShapeMapper;

import java.util.Map;

/**
 * A factory for creating {@link IPathShape} objects.
 */
public class SvgPathShapeFactory {

    private SvgPathShapeFactory() {
    }

    /**
     * Creates a configured {@link IPathShape} object based on the passed Svg path data instruction tag.
     *
     * @param name svg path element's path-data instruction name.
     * @return IPathShape implementation
     */
    public static IPathShape createPathShape(String name) {
        return new PathShapeMapper().getMapping().get(name);
    }

    /**
     * Finds the appropriate number of arguments for a path command, based on the passed Svg path data instruction tag.
     *
     * @param name svg path element's path-data instruction name.
     * @return an integer value with the required number of arguments or null if there is no mapping for the given value
     */
    public static int getArgumentCount(String name) {
        Map<String, Integer> map = new PathShapeMapper().getArgumentCount();
        if (map.containsKey(name.toUpperCase())) {
            return (int) map.get(name.toUpperCase());
        }
        return -1;
    }
}
