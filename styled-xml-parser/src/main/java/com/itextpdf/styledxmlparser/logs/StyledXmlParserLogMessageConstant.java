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
package com.itextpdf.styledxmlparser.logs;
/**
 * Class that bundles all the error message templates as constants.
 */
public final class StyledXmlParserLogMessageConstant {

    /** The Constant SHORTHAND_PROPERTY_CANNOT_BE_EMPTY. */
    public static final String SHORTHAND_PROPERTY_CANNOT_BE_EMPTY = "{0} shorthand property cannot be empty.";

    /** The Constant DEFAULT_VALUE_OF_CSS_PROPERTY_UNKNOWN. */
    public static final String DEFAULT_VALUE_OF_CSS_PROPERTY_UNKNOWN =
            "Default value of the css property \"{0}\" is unknown.";

    /** The Constant ERROR_ADDING_CHILD_NODE. */
    public static final String ERROR_ADDING_CHILD_NODE = "Error adding child node.";

    /** The Constant ERROR_PARSING_COULD_NOT_MAP_NODE. */
    public static final String ERROR_PARSING_COULD_NOT_MAP_NODE = "Could not map node type: {0}";

    /** The Constant ERROR_PARSING_CSS_SELECTOR. */
    public static final String ERROR_PARSING_CSS_SELECTOR = "Error while parsing css selector: {0}";

    /** The Constant ONLY_THE_LAST_BACKGROUND_CAN_INCLUDE_BACKGROUND_COLOR. */
    public static final String ONLY_THE_LAST_BACKGROUND_CAN_INCLUDE_BACKGROUND_COLOR =
            "Only the last background can include a background color.";

    /** The Constant UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED. */
    public static final String UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED = "Unknown absolute metric length parsed \"{0}\".";

    public static final String UNKNOWN_METRIC_ANGLE_PARSED = "Unknown metric angle parsed: \"{0}\".";

    /** The Constant UNKNOWN__PROPERTY. */
    public static final String UNKNOWN_PROPERTY = "Unknown {0} property: \"{1}\".";

    public static final String URL_IS_EMPTY_IN_CSS_EXPRESSION = "url function is empty in expression:{0}";

    public static final String URL_IS_NOT_CLOSED_IN_CSS_EXPRESSION =
            "url function is not properly closed in expression:{0}";

    /** The Constant QUOTES_PROPERTY_INVALID. */
    public static final String QUOTES_PROPERTY_INVALID =
            "Quote property \"{0}\" is invalid. It should contain even number of <string> values.";

    /** The Constant QUOTE_IS_NOT_CLOSED_IN_CSS_EXPRESSION. */
    public static final String QUOTE_IS_NOT_CLOSED_IN_CSS_EXPRESSION = "The quote is not closed in css expression: {0}";

    /** The Constant INVALID_CSS_PROPERTY_DECLARATION. */
    public static final String INVALID_CSS_PROPERTY_DECLARATION = "Invalid css property declaration: {0}";

    /**
     * The Constant INCORRECT_CHARACTER_SEQUENCE.
     */
    public static final String INCORRECT_CHARACTER_SEQUENCE = "Incorrect character sequence.";

    public static final String INCORRECT_RESOLUTION_UNIT_VALUE =
            "Resolution value unit should be either dpi, dppx or dpcm!";

    /** The Constant RULE_IS_NOT_SUPPORTED. */
    public static final String RULE_IS_NOT_SUPPORTED =
            "The rule @{0} is unsupported. All selectors in this rule will be ignored.";

    /** The Constant RESOURCE_WITH_GIVEN_URL_WAS_FILTERED_OUT. */
    public static final String RESOURCE_WITH_GIVEN_URL_WAS_FILTERED_OUT =
            "Resource with given URL ({0}) was filtered out.";

    /** The Constant UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_DATA_URI. */
    public static final String UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_DATA_URI =
            "Unable to retrieve image with data URI {0}";

    /** The Constant UNABLE_TO_RETRIEVE_RESOURCE_WITH_GIVEN_RESOURCE_SIZE_BYTE_LIMIT. */
    public static final String UNABLE_TO_RETRIEVE_RESOURCE_WITH_GIVEN_RESOURCE_SIZE_BYTE_LIMIT =
            "Unable to retrieve resource with given URL ({0}) and resource size byte limit ({1}).";

    /** The Constant UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI. */
    public static final String UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI =
            "Unable to retrieve image with given base URI ({0}) and image source path ({1})";

    public static final String UNABLE_TO_RESOLVE_IMAGE_URL =
            "Unable to resolve image path with given base URI ({0}) and image source path ({1})";

    /** The Constant UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI. */
    public static final String UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI =
            "Unable to retrieve stream with given base URI ({0}) and source path ({1})";

    public static final String UNABLE_TO_PROCESS_EXTERNAL_CSS_FILE = "Unable to process external css file" ;

    public static final String UNABLE_TO_RETRIEVE_FONT = "Unable to retrieve font:\n {0}";

    public static final String UNSUPPORTED_PSEUDO_CSS_SELECTOR = "Unsupported pseudo css selector: {0}";

    /** The Constant WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES. */
    public static final String WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES =
            "Was not able to define one of the background CSS shorthand properties: {0}";

    /** The Constant ERROR_RESOLVING_PARENT_STYLES. */
    public static final String ERROR_RESOLVING_PARENT_STYLES =
            "Element parent styles are not resolved. Styles for current element might be incorrect.";
    /** The Constant ERROR_LOADING_FONT. */
    public static final String ERROR_LOADING_FONT = "Error while loading font";

    public static final String IMPORT_MUST_COME_BEFORE =
            "Imported rules must come before all other types of rules, except @charset rules and layer creating @layer statements. "
                    + "Rule will be ignored. ";

    public static final String IMPORT_RULE_URL_CAN_NOT_BE_RESOLVED = "Import rule URL can't be resolved because of base URI absence.";

    /**
     * Instantiates a new log message constant.
     */
    private StyledXmlParserLogMessageConstant() {
        //Private constructor will prevent the instantiation of this class directly
    }
}
