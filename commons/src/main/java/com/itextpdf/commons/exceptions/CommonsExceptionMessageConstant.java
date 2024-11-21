/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.commons.exceptions;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class CommonsExceptionMessageConstant {
    /**
     * Message warns about overriding of the identifier of identifiable element. List of params:
     *
     * <ul>
     * <li>0th is an original element identifier;
     * <li>1st is a new element identifier;
     * </ul>
     */
    public static final String ELEMENT_ALREADY_HAS_IDENTIFIER = "Element already has sequence id: {0}, new id {1} " +
            "will be ignored";

    public static final String FILE_SHOULD_EXIST = "File should exist.";
    public static final String FILE_NAME_ALREADY_EXIST = "File name: {0}, already exists.";
    public static final String FILE_NAME_CAN_NOT_BE_NULL = "File name can not be null.";
    public static final String FILE_NAME_SHOULD_BE_UNIQUE = "File name should be unique.";
    public static final String INVALID_USAGE_CONFIGURATION_FORBIDDEN = "Invalid usage of placeholder \"{0}\": any "
            + "configuration is forbidden";
    public static final String INVALID_USAGE_FORMAT_REQUIRED = "Invalid usage of placeholder \"{0}\": format is "
            + "required";
    public static final String JSON_OBJECT_CAN_NOT_BE_NULL = "Passed json object can not be null";
    public static final String NO_EVENTS_WERE_REGISTERED_FOR_THE_DOCUMENT = "No events were registered for the "
            + "document!";
    public static final String PATTERN_CONTAINS_OPEN_QUOTATION = "Pattern contains open quotation!";
    public static final String PATTERN_CONTAINS_UNEXPECTED_CHARACTER = "Pattern contains unexpected character {0}";
    public static final String PATTERN_CONTAINS_UNEXPECTED_COMPONENT = "Pattern contains unexpected component {0}";
    public static final String PRODUCT_NAME_CAN_NOT_BE_NULL = "Product name can not be null.";
    public static final String STREAM_CAN_NOT_BE_NULL = "Passed stream can not be null";
    public static final String UNKNOWN_ITEXT_EXCEPTION = "Unknown ITextException.";

    public static final String ZIP_ENTRY_NOT_FOUND = "Zip entry not found for name: {0}";
    public static final String UNSUPPORTED_OPERATION = "This operation is not supported.";

    private CommonsExceptionMessageConstant() {
        // Empty constructor.
    }
}
