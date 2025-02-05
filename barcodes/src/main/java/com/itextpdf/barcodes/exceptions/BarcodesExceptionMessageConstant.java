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
package com.itextpdf.barcodes.exceptions;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class BarcodesExceptionMessageConstant {
    public static final String CODABAR_MUST_HAVE_AT_LEAST_START_AND_STOP_CHARACTER = "Codabar must have at least start "
            + "and stop character.";
    public static final String CODABAR_MUST_HAVE_ONE_ABCD_AS_START_STOP_CHARACTER = "Codabar must have one of 'ABCD' "
            + "as start/stop character.";
    public static final String ILLEGAL_CHARACTER_IN_CODABAR_BARCODE = "Illegal character in Codabar Barcode.";
    public static final String IN_CODABAR_START_STOP_CHARACTERS_ARE_ONLY_ALLOWED_AT_THE_EXTREMES = "In Codabar, "
            + "start/stop characters are only allowed at the extremes.";
    public static final String INVALID_CODEWORD_SIZE = "Invalid codeword size.";
    public static final String MACRO_SEGMENT_ID_MUST_BE_GT_OR_EQ_ZERO = "macroSegmentId must be >= 0";
    public static final String MACRO_SEGMENT_ID_MUST_BE_GT_ZERO = "macroSegmentId must be > 0";
    public static final String MACRO_SEGMENT_ID_MUST_BE_LT_MACRO_SEGMENT_COUNT = "macroSegmentId "
            + "must be < macroSemgentCount";
    public static final String TEXT_CANNOT_BE_NULL = "Text cannot be null.";
    public static final String TEXT_IS_TOO_BIG = "Text is too big.";
    public static final String TEXT_MUST_BE_EVEN = "The text length must be even.";
    public static final String TWO_BARCODE_MUST_BE_EXTERNALLY = "The two barcodes must be composed externally.";
    public static final String THERE_ARE_ILLEGAL_CHARACTERS_FOR_BARCODE_128 = "There are illegal characters for "
            + "barcode 128 in {0}.";

    private BarcodesExceptionMessageConstant(){}
}
