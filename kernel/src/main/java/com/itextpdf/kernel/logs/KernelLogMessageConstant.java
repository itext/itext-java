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

    public static final String ERROR_WHILE_FINALIZING_AES_CIPHER = "Exception finalizing AES cipher.";

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

    public static final String MD5_IS_NOT_FIPS_COMPLIANT =
            "MD5 hash algorithm is not FIPS compliant. However we still use this algorithm "
                    + "since it is required according to the PDF specification.";

    public static final String UNABLE_TO_PARSE_COLOR_WITHIN_COLORSPACE =
            "Unable to parse color {0} within {1} color space";
    public static final String CANNOT_MERGE_ENTRY = "Cannot merge entry {0}, entry with such key already exists.";

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
    public static final String FLATTENING_IS_NOT_YET_SUPPORTED =
            "Flattening annotation type {0} is not yet supported, it will not be removed from the page";
    public static final String FORMFIELD_ANNOTATION_WILL_NOT_BE_FLATTENED = "Form field annotation flattening is not "
            + "supported. Use the PdfAcroForm#flattenFields() method instead.";

    public static final String INVALID_DDICTIONARY_FIELD_VALUE = "The default configuration dictionary field {0}" +
            " has a value of {1}, which is not the required value for this field. The field will not be processed.";

    public static final String STRUCT_PARENT_INDEX_MISSED_AND_RECREATED =
            "StructParent index not found in tagged object, so index is recreated.";

    public static final String XOBJECT_STRUCT_PARENT_INDEX_MISSED_AND_RECREATED =
            "XObject has no StructParents index in its stream, so index is recreated";

    //TODO DEVSIX-8490 remove this log message when implemented
    public static final String DUPLICATE_ENTRIES_IN_ORDER_ARRAY_REMOVED = "Duplicated entries in order array are " +
            "removed";

    public static final String FINGERPRINT_DISABLED_BUT_NO_REQUIRED_LICENCE = "Fingerprint disabling is only " +
            "available in non AGPL mode. Fingerprint will be added at the end of the document.";
    public static final String ALGORITHM_NOT_FROM_SPEC =
            "Requested algorithm might not be supported by the pdf specification.";

    public static final String MEMORYLIMITAWAREHANDLER_OVERRIDE_CREATENEWINSTANCE_METHOD =
            "MemoryLimitsAwareHandler#createNewInstance method must be overriden.";

    private KernelLogMessageConstant() {
        //Private constructor will prevent the instantiation of this class directly
    }
}
