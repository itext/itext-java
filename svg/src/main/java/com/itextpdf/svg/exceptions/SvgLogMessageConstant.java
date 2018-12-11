/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.exceptions;

/**
 * Class that holds the logging and exception messages.
 */
public final class SvgLogMessageConstant {

    private SvgLogMessageConstant(){};

    public static final String ATTRIBUTES_NULL = "The attributes of this element are null.";
    public static final String COORDINATE_VALUE_ABSENT = "The coordinate value is empty or null.";
    public static final String COULDNOTINSTANTIATE = "Could not instantiate Renderer for tag {0}";
    public static final String DRAW_NO_DRAW = "Can't draw a NoDrawOperationSvgNodeRenderer.";
    @Deprecated
    public static final String ERROR_CLOSING_CSS_STREAM = "An error occured when trying to close the InputStream of the default CSS.";
    public static final String ERROR_INITIALIZING_DEFAULT_CSS = "Error loading the default CSS. Initializing an empty style sheet.";
    @Deprecated
    public static final String FLOAT_PARSING_NAN = "The passed value is not a number.";
    public static final String FONT_NOT_FOUND = "The font wasn't found.";
    public static final String INODEROOTISNULL = "Input root value is null";
    public static final String INVALID_CLOSEPATH_OPERATOR_USE = "The close path operator (Z) may not be used before a move to operation (M)";
    public static final String INVALID_PATH_D_ATTRIBUTE_OPERATORS = "Invalid operators found in path data attribute: {0}";
    public static final String INVALID_TRANSFORM_DECLARATION = "Transformation declaration is not formed correctly.";
    public static final String LOOP ="Loop detected";
    public static final String MISSING_WIDTH="Top Svg tag has no defined width attribute and viewbox width is not present, so browser default of 300px is used";
    public static final String MISSING_HEIGHT="Top Svg tag has no defined height attribute and viewbox height is not present, so browser default of 150px is used";
    public static final String NAMED_OBJECT_NAME_NULL_OR_EMPTY = "The name of the named object can't be null or empty.";
    public static final String NAMED_OBJECT_NULL = "A named object can't be null.";
    public static final String NONINVERTIBLE_TRANSFORMATION_MATRIX_USED_IN_CLIP_PATH = "Non-invertible transformation matrix was used in a clipping path context. Clipped elements may show undefined behavior.";
    public static final String NOROOT = "No root found";
    public static final String PARAMETER_CANNOT_BE_NULL = "Parameters for this method cannot be null.";
    public static final String ROOT_SVG_NO_BBOX = "The root svg tag needs to have a bounding box defined.";
    public static final String POINTS_ATTRIBUTE_INVALID_LIST = "Points attribute {0} on polyline tag does not contain a valid set of points";
    public static final String TAGPARAMETERNULL = "Tag parameter must not be null";
    public static final String TRANSFORM_EMPTY = "The transformation value is empty.";
    public static final String TRANSFORM_INCORRECT_NUMBER_OF_VALUES = "Transformation doesn't contain the right number of values.";
    @Deprecated
    public static final String TRANSFORM_INCORRECT_VALUE_TYPE = "The transformation value is not a number.";
    public static final String TRANSFORM_NULL = "The transformation value is null.";
    public static final String UNMAPPEDTAG = "Could not find implementation for tag {0}";
    public static final String UNKNOWN_TRANSFORMATION_TYPE = "Unsupported type of transformation.";
    public static final String UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI = "Unable to retrieve stream with given base URI ({0}) and source path ({1})";
    /** Message in case the font provider doesn't know about any fonts. */
    @Deprecated
    public static final String FONT_PROVIDER_CONTAINS_ZERO_FONTS = "Font Provider contains zero fonts. At least one font shall be present";
    public static final String UNABLE_TO_RETRIEVE_FONT = "Unable to retrieve font:\n {0}";
    public static final String PATH_WRONG_NUMBER_OF_ARGUMENTS = "Path operator {0} has received {1} arguments, but expects between {2} and {3} arguments. \n Resulting SVG will be incorrect.";

}
