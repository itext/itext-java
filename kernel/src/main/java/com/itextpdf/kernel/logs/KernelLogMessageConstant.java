/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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

package com.itextpdf.kernel.logs;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class KernelLogMessageConstant {

    public static final String CORRUPTED_OUTLINE_DICTIONARY_HAS_INFINITE_LOOP =
            "Document outline dictionary is corrupted: some outline (PDF object: \"{0}\") has wrong first/next link "
                    + "entry. Next outlines in this dictionary will be unprocessed.";

    public static final String DCTDECODE_FILTER_DECODING =
            "DCTDecode filter decoding into the bit map is not supported. The stream data would be left in JPEG "
                    + "baseline format";

    public static final String FEATURE_IS_NOT_SUPPORTED =
            "Exception was thrown: {0}. The feature {1} is probably not supported by your XML processor.";

    public static final String FULL_COMPRESSION_APPEND_MODE_XREF_TABLE_INCONSISTENCY =
            "Full compression mode requested in append mode but the original document has cross-reference table, " +
                    "not cross-reference stream. " +
                    "Falling back to cross-reference table in appended document and switching full compression off";

    public static final String FULL_COMPRESSION_APPEND_MODE_XREF_STREAM_INCONSISTENCY =
            "Full compression mode was requested to be switched off in append mode but the original document has "
                    + "cross-reference stream, not cross-reference table. Falling back to cross-reference stream in "
                    + "appended document and switching full compression on";

    public static final String JPXDECODE_FILTER_DECODING =
            "JPXDecode filter decoding into the bit map is not supported. The stream data would be left in JPEG2000 "
                    + "format";

    public static final String UNABLE_TO_PARSE_COLOR_WITHIN_COLORSPACE =
            "Unable to parse color {0} within {1} color space";

    /**
     * Message warns about unexpected product name which was mentioned as involved into PDF
     * processing. List of params:
     *
     * <ul>
     * <li>0th is a name of unknown product;
     * </ul>
     */
    public static final String UNKNOWN_PRODUCT_INVOLVED =
            "Unknown product {0} was involved into PDF processing. It will be ignored";

    /**
     * Message warns that some event was reported but wasn't confirmed. Probably some processing has failed.
     * List of params:
     *
     * <ul>
     * <li>0th is a name of product for which event was reported;
     * <li>1st is an event type;
     * </ul>
     */
    public static final String UNCONFIRMED_EVENT =
            "Event for the product {0} with type {1} was reported but was not confirmed. Probably appropriate process "
                    + "fail";

    private KernelLogMessageConstant() {
        //Private constructor will prevent the instantiation of this class directly
    }
}
