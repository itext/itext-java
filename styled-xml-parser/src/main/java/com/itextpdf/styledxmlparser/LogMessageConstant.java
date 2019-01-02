/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.styledxmlparser;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class LogMessageConstant {

    /** The Constant DEFAULT_VALUE_OF_CSS_PROPERTY_UNKNOWN. */
    public static final String DEFAULT_VALUE_OF_CSS_PROPERTY_UNKNOWN = "Default value of the css property \"{0}\" is unknown.";
    /** The Constant ERROR_PARSING_COULD_NOT_MAP_NODE */
    public static final String ERROR_PARSING_COULD_NOT_MAP_NODE = "Could not map node type: {0}";
    /** The Constant ERROR_PARSING_CSS_SELECTOR. */
    public static final String ERROR_PARSING_CSS_SELECTOR = "Error while parsing css selector: {0}";
    /** The Constant UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED. */
    public static final String UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED = "Unknown absolute metric length parsed \"{0}\".";
    public static final String URL_IS_EMPTY_IN_CSS_EXPRESSION = "url function is empty in expression:{0}";
    public static final String URL_IS_NOT_CLOSED_IN_CSS_EXPRESSION = "url function is not properly closed in expression:{0}";
    /** The Constant QUOTES_PROPERTY_INVALID. */
    public static final String QUOTES_PROPERTY_INVALID = "Quote property \"{0}\" is invalid. It should contain even number of <string> values.";
    /** The Constant QUOTE_IS_NOT_CLOSED_IN_CSS_EXPRESSION. */
    public static final String QUOTE_IS_NOT_CLOSED_IN_CSS_EXPRESSION = "The quote is not closed in css expression: {0}";
    /** The Constant INVALID_CSS_PROPERTY_DECLARATION. */
    public static final String INVALID_CSS_PROPERTY_DECLARATION = "Invalid css property declaration: {0}";
    /** The Constant RULE_IS_NOT_SUPPORTED. */
    public static final String RULE_IS_NOT_SUPPORTED = "The rule @{0} is unsupported. All selectors in this rule will be ignored.";
    public static final String NAN = "The passed value (@{0}) is not a number";
    /** The Constant UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI. */
    public static final String UNABLE_TO_RETRIEVE_IMAGE_WITH_GIVEN_BASE_URI = "Unable to retrieve image with given base URI ({0}) and image source path ({1})";
    /** The Constant UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI. */
    public static final String UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI = "Unable to retrieve stream with given base URI ({0}) and source path ({1})";
    public static final String UNABLE_TO_PROCESS_EXTERNAL_CSS_FILE =  "Unable to process external css file" ;
    public static final String UNABLE_TO_RETRIEVE_FONT =  "Unable to retrieve font:\n {0}";
    public static final String UNSUPPORTED_PSEUDO_CSS_SELECTOR = "Unsupported pseudo css selector: {0}";
    /** The Constant WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES. */
    public static final String WAS_NOT_ABLE_TO_DEFINE_BACKGROUND_CSS_SHORTHAND_PROPERTIES = "Was not able to define one of the background CSS shorthand properties: {0}";
    /** The Constant ERROR_RESOLVING_PARENT_STYLES. */
    public static final String ERROR_RESOLVING_PARENT_STYLES = "Element parent styles are not resolved. Styles for current element might be incorrect.";
    /**
     * Instantiates a new log message constant.
     */
    private LogMessageConstant() {
    }

}
