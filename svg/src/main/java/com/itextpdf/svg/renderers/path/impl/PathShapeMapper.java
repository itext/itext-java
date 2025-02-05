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
package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.IPathShapeMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link IPathShapeMapper} that will be used by
 * sub classes of {@link PathSvgNodeRenderer} To map the path-data
 * instructions(moveto, lineto, corveto ...) to their respective implementations.
 */
public class PathShapeMapper implements IPathShapeMapper {
    @Override
    public Map<String, IPathShape> getMapping() {
        Map<String, IPathShape> result = new HashMap<>();
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO, new LineTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_LINE_TO, new LineTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO_V, new VerticalLineTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_LINE_TO_V, new VerticalLineTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO_H, new HorizontalLineTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_LINE_TO_H, new HorizontalLineTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_CLOSE_PATH, new ClosePath());
        result.put(SvgConstants.Attributes.PATH_DATA_CLOSE_PATH.toLowerCase(), new ClosePath());
        result.put(SvgConstants.Attributes.PATH_DATA_MOVE_TO, new MoveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_MOVE_TO, new MoveTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_CURVE_TO, new CurveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_CURVE_TO, new CurveTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_CURVE_TO_S, new SmoothSCurveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_CURVE_TO_S, new SmoothSCurveTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_QUAD_CURVE_TO, new QuadraticCurveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_QUAD_CURVE_TO, new QuadraticCurveTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_SHORTHAND_CURVE_TO, new QuadraticSmoothCurveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_SHORTHAND_CURVE_TO, new QuadraticSmoothCurveTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_ELLIPTICAL_ARC_A, new EllipticalCurveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_ELLIPTICAL_ARC_A, new EllipticalCurveTo(true));
        return result;
    }

    @Override
    public Map<String, Integer> getArgumentCount() {
        Map<String, Integer> result = new HashMap<>();
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO, LineTo.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO_V, VerticalLineTo.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO_H, HorizontalLineTo.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_CLOSE_PATH, ClosePath.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_MOVE_TO, MoveTo.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_CURVE_TO, CurveTo.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_CURVE_TO_S, SmoothSCurveTo.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_QUAD_CURVE_TO, QuadraticCurveTo.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_SHORTHAND_CURVE_TO, QuadraticSmoothCurveTo.ARGUMENT_SIZE);
        result.put(SvgConstants.Attributes.PATH_DATA_ELLIPTICAL_ARC_A, EllipticalCurveTo.ARGUMENT_SIZE);
        return result;
    }

}
