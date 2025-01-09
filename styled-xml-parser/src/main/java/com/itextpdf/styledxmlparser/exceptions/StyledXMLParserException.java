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
package com.itextpdf.styledxmlparser.exceptions;

import com.itextpdf.commons.exceptions.ITextException;

/**
 * Runtime exception that gets thrown if something goes wrong in the HTML to PDF conversion.
 */
public class StyledXMLParserException extends ITextException {

    /** The Constant INVALID_GRADIENT_VALUE. */
    public static final String INVALID_GRADIENT_FUNCTION_ARGUMENTS_LIST = "Invalid gradient function arguments list: {0}";
    /** The Constant INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING. */
    public static final String INVALID_GRADIENT_TO_SIDE_OR_CORNER_STRING = "Invalid direction string: {0}";
    /** The Constant INVALID_GRADIENT_COLOR_STOP_VALUE. */
    public static final String INVALID_GRADIENT_COLOR_STOP_VALUE = "Invalid color stop value: {0}";
    /** The Constant NAN. */
    public static final String NAN = "The passed value (@{0}) is not a number";

    /** Message in case the font provider doesn't know about any fonts. */
    public static final String FontProviderContainsZeroFonts = "Font Provider contains zero fonts. At least one font shall be present";
    /** The Constant UnsupportedEncodingException. */
    public static final String UnsupportedEncodingException = "Unsupported encoding exception.";

    /**
     * Creates a new {@link StyledXMLParserException} instance.
     *
     * @param message the message
     */
    public StyledXMLParserException(String message) {
        super(message);
    }
    
    /**  Serial version UID. */
}
