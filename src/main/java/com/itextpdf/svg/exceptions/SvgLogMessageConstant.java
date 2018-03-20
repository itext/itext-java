package com.itextpdf.svg.exceptions;

/**
 * Class that holds the logging and exception messages.
 */
public final class SvgLogMessageConstant {

    private SvgLogMessageConstant(){};

    public static final String COULDNOTINSTANTIATE = "Could not instantiate Renderer for tag {0}";
    public static final String FLOAT_PARSING_NAN = "The passed value is not a number.";
    public static final String INODEROOTISNULL = "Input root value is null";
    public static final String INVALID_TRANSFORM_DECLARATION = "Transformation declaration is not formed correctly.";
    public static final String LOOP ="Loop detected";
    public static final String NAMED_OBJECT_NAME_NULL_OR_EMPTY = "The name of the named object can't be null or empty.";
    public static final String NAMED_OBJECT_NULL = "A named object can't be null.";
    public static final String NOROOT = "No root found";
    public static final String ROOT_SVG_NO_BBOX = "The root svg tag needs to have a bounding box defined.";
    public static final String POINTS_ATTRIBUTE_INVALID_LIST = "Points attribute {0} on polyline tag does not contain a valid set of points";
    public static final String TAGPARAMETERNULL = "Tag parameter must not be null";
    public static final String TRANSFORM_EMPTY = "The transformation value is empty.";
    public static final String TRANSFORM_INCORRECT_NUMBER_OF_VALUES = "Transformation doesn't contain the right number of values.";
    public static final String TRANSFORM_INCORRECT_VALUE_TYPE = "The transformation value is not a number.";
    public static final String TRANSFORM_NULL = "The transformation value is null.";
    public static final String UNMAPPEDTAG = "Could not find implementation for tag {0}";
    public static final String UNKNOWN_TRANSFORMATION_TYPE = "Unsupported type of transformation.";
    public static final String PARAMETER_CANNOT_BE_NULL = "Parameters for this method cannot be null.";
}
