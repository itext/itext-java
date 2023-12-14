/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

public class SvgExceptionMessageConstant {

    public static final String PATH_OBJECT_MUST_HAVE_D_ATTRIBUTE = "A Path object must have an attribute with the name 'd'.";

    public static final String COORDINATE_ARRAY_LENGTH_MUST_BY_DIVISIBLE_BY_CURRENT_COORDINATES_ARRAY_LENGTH = "Array of current coordinates must have length that is divisible by the length of the array with current coordinates";
    public static final String CURVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 = "(x1 y1 x2 y2 x y)+ parameters are expected for curves. Got: {0}";
    public static final String INVALID_SMOOTH_CURVE_USE = "The smooth curve operations (S, s, T, t) may not be used as a first operator in path.";
    public static final String QUADRATIC_CURVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 = "(x1 y1 x y)+ parameters are expected for quadratic curves. Got: {0}";
    public static final String MEET_OR_SLICE_ARGUMENT_IS_INCORRECT =
            "The meetOrSlice argument is incorrect. It must be `meet`, `slice` or null.";
    public static final String MOVE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 = "(x y)+ parameters are expected for moveTo operator. Got: {0}";
    public static final String LINE_TO_EXPECTS_FOLLOWING_PARAMETERS_GOT_0 = "(x y)+ parameters are expected for lineTo operator. Got: {0}";

    public static final String COULD_NOT_DETERMINE_MIDDLE_POINT_OF_ELLIPTICAL_ARC = "Could not determine the middle point of the ellipse traced by this elliptical arc";

    public static final String CURRENT_VIEWPORT_IS_NULL =
            "The current viewport is null. The viewBox applying could not be processed.";

    public static final String VIEWBOX_IS_INCORRECT =
            "The viewBox is incorrect. The viewBox applying could not be processed.";
}
