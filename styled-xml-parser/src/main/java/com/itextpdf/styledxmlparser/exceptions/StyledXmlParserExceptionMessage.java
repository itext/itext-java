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

/**
 * Class containing constants to be used in exceptions in the SXP module.
 */
public final class StyledXmlParserExceptionMessage {
    public static final String INVALID_TOKEN_AT_THE_BEGINNING_OF_SELECTOR
            = "Invalid token detected at the beginning of the selector string: \"{0}\"";
    public static final String READING_BYTE_LIMIT_MUST_NOT_BE_LESS_ZERO = "The reading byte limit argument must not be less than zero.";

    private StyledXmlParserExceptionMessage() {
    }
}
