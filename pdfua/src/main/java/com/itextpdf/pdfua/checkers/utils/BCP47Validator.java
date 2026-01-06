/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.pdfua.checkers.utils;

/**
 * This class is a validator for IETF BCP 47 language tag (RFC 5646).
 *
 * @deprecated in favor of {@link com.itextpdf.kernel.utils.checkers.BCP47Validator}
 */
@Deprecated
public class BCP47Validator {

    private BCP47Validator() {
        // Private constructor will prevent the instantiation of this class directly.
    }

    /**
     * Validate language tag against RFC 5646.
     *
     * @param languageTag language tag string
     *
     * @return {@code true} if it is a valid tag, {@code false} otherwise
     */
    public static boolean validate(String languageTag) {
        return com.itextpdf.kernel.utils.checkers.BCP47Validator.validate(languageTag);
    }
}
