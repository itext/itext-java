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
package com.itextpdf.kernel;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class KernelLogMessageConstant {

    public static final String DCTDECODE_FILTER_DECODING = "DCTDecode filter decoding into the "
            + "bit map is not supported. The stream data would be left in JPEG baseline format";

    public static final String FEATURE_IS_NOT_SUPPORTED =
            "Exception was thrown: {0}. The feature {1} is probably not supported by your XML processor.";

    public static final String FULL_COMPRESSION_APPEND_MODE_XREF_TABLE_INCONSISTENCY = "Full compression mode requested "
            + "in append mode but the original document has cross-reference table, not cross-reference stream. "
            + "Falling back to cross-reference table in appended document and switching full compression off";

    public static final String FULL_COMPRESSION_APPEND_MODE_XREF_STREAM_INCONSISTENCY = "Full compression mode was "
            + "requested to be switched off in append mode but the original document has cross-reference stream, not "
            + "cross-reference table. Falling back to cross-reference stream in appended document and switching full "
            + "compression on";

    public static final String JPXDECODE_FILTER_DECODING = "JPXDecode filter decoding into the "
            + "bit map is not supported. The stream data would be left in JPEG2000 format";

    public static final String UNABLE_TO_PARSE_COLOR_WITHIN_COLORSPACE = "Unable to parse color {0} within {1} color space";

    private KernelLogMessageConstant() {
        //Private constructor will prevent the instantiation of this class directly
    }

}
