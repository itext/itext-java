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
package com.itextpdf.svg.exceptions;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class SvgExceptionMessageConstant {

    public static final String ARC_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 =
            "(rx ry rot largearc sweep x y)+ parameters are expected for elliptical arcs. Got: {0}";
    public static final String COORDINATE_ARRAY_LENGTH_MUST_BY_DIVISIBLE_BY_CURRENT_COORDINATES_ARRAY_LENGTH =
            "Array of current coordinates must have length that is divisible by the length of the array with current "
                    + "coordinates";
    public static final String COULD_NOT_DETERMINE_MIDDLE_POINT_OF_ELLIPTICAL_ARC =
            "Could not determine the middle point of the ellipse traced by this elliptical arc";
    public static final String CURVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 =
            "(x1 y1 x2 y2 x y)+ parameters are expected for curves. Got: {0}";
    public static final String DRAW_NO_DRAW = "The renderer cannot be drawn.";
    public static final String FAILED_TO_PARSE_INPUTSTREAM = "Failed to parse InputStream.";
    public static final String FONT_NOT_FOUND = "The font wasn't found.";
    public static final String I_NODE_ROOT_IS_NULL = "Input root value is null";
    public static final String MEET_OR_SLICE_ARGUMENT_IS_INCORRECT =
            "The meetOrSlice argument is incorrect. It must be `meet`, `slice` or null.";
    public static final String CURRENT_VIEWPORT_IS_NULL =
            "The current viewport is null. The viewBox applying could not be processed.";
    public static final String VIEWBOX_IS_INCORRECT =
            "The viewBox is incorrect. The viewBox applying could not be processed.";
    public static final String INVALID_CLOSEPATH_OPERATOR_USE =
            "The close path operator (Z) may not be used before a move to operation (M)";
    public static final String INVALID_PATH_D_ATTRIBUTE_OPERATORS =
            "Invalid operators found in path data attribute: {0}";
    public static final String INVALID_SMOOTH_CURVE_USE =
            "The smooth curve operations (S, s, T, t) may not be used as a first operator in path.";
    public static final String INVALID_TRANSFORM_DECLARATION = "Transformation declaration is not formed correctly.";
    public static final String LINE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 =
            "(x y)+ parameters are expected for lineTo operator. Got: {0}";
    public static final String MOVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 =
            "(x y)+ parameters are expected for moveTo operator. Got: {0}";
    public static final String NAMED_OBJECT_NAME_NULL_OR_EMPTY = "The name of the named object can't be null or empty.";
    public static final String NAMED_OBJECT_NULL = "A named object can't be null.";
    public static final String NO_ROOT = "No root found";
    public static final String PARAMETER_CANNOT_BE_NULL = "Parameters cannot be null.";
    public static final String POINTS_ATTRIBUTE_INVALID_LIST =
            "Points attribute {0} on polyline tag does not contain a valid set of points";
    public static final String QUADRATIC_CURVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 =
            "(x1 y1 x y)+ parameters are expected for quadratic curves. Got: {0}";
    public static final String ROOT_SVG_NO_BBOX = "The root svg tag needs to have a bounding box defined.";
    public static final String TAG_PARAMETER_NULL = "Tag parameter must not be null";
    public static final String TRANSFORM_EMPTY = "The transformation value is empty.";
    public static final String TRANSFORM_INCORRECT_NUMBER_OF_VALUES =
            "Transformation doesn't contain the right number of values.";
    public static final String TRANSFORM_NULL = "The transformation value is null.";
    public static final String UNKNOWN_TRANSFORMATION_TYPE = "Unsupported type of transformation.";

    public static final String ILLEGAL_RELATIVE_VALUE_NO_VIEWPORT_IS_SET
            = "Relative value can't be resolved, no viewport is set.";

    private SvgExceptionMessageConstant() {
    }
}
