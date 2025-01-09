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
package com.itextpdf.commons.logs;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class CommonsLogMessageConstant {
    /**
     * Message notifies that base64 encoding or decoding failed.
     */
    public static final String BASE_64_EXCEPTION = "Exception during base64 encoding or decoding.";

    /**
     * Message notifies that an invalid statistics name was received, because there is no aggregator for it.
     *
     * <ul>
     * <li>0th is a statistics name which is invalid;
     * </ul>
     */
    public static final String INVALID_STATISTICS_NAME =
            "Statistics name {0} is invalid. Cannot find corresponding statistics aggregator.";

    /**
     * Message notifies that files archiving operation failed.
     *
     * <ul>
     * <li>0th is a message of thrown exception;
     * </ul>
     */
    public static final String LOCAL_FILE_COMPRESSION_FAILED = "Cannot archive files into zip. "
            + "Exception message: {0}.";

    /**
     * Message notifies that archive is suspicious to be a zip bomb due to large ratio between the compressed and
     * uncompressed archive entry.
     *
     * <ul>
     * <li>0th is a threshold ratio;
     * </ul>
     */
    public static final String RATIO_IS_HIGHLY_SUSPICIOUS = "Ratio between compressed and uncompressed data is highly"
            + " suspicious, looks like a Zip Bomb Attack. Threshold ratio is {0}.";

    /**
     * Message notifies that archive is suspicious to be a zip bomb because the number of file entries extracted from
     * the archive is greater than a predefined threshold.
     *
     * <ul>
     * <li>0th is a threshold number of file entries in the archive;
     * </ul>
     */
    public static final String TOO_MUCH_ENTRIES_IN_ARCHIVE = "Too much entries in this archive, can lead to inodes "
            + "exhaustion of the system, looks like a Zip Bomb Attack. Threshold number of file entries is {0}.";

    /**
     * Message notifies that some exception has been thrown during json deserialization from object.
     * List of params:
     *
     * <ul>
     * <li>0th is a class name of thrown exception;
     * <li>1st is a message of thrown exception;
     * </ul>
     */
    public static final String UNABLE_TO_DESERIALIZE_JSON =
            "Unable to deserialize json. Exception {0} was thrown with the message: {1}.";

    /**
     * Message notifies that some exception has been thrown during json serialization to object.
     * List of params:
     *
     * <ul>
     * <li>0th is a class name of thrown exception;
     * <li>1st is a message of thrown exception;
     * </ul>
     */
    public static final String UNABLE_TO_SERIALIZE_OBJECT =
            "Unable to serialize object. Exception {0} was thrown with the message: {1}.";

    /**
     * Message notifies that archive is suspicious to be a zip bomb due to large total size of the uncompressed data.
     *
     * <ul>
     * <li>0th is a threshold size;
     * </ul>
     */
    public static final String UNCOMPRESSED_DATA_SIZE_IS_TOO_MUCH = "The uncompressed data size is too much for the"
            + " application resource capacity, looks like a Zip Bomb Attack. Threshold size is {0}.";

    /**
     * Message notifies that unknown placeholder was ignored during parsing of the producer line
     * format. List of params:
     *
     * <ul>
     * <li>0th is a name of ignored placeholder;
     * </ul>
     */
    public static final String UNKNOWN_PLACEHOLDER_WAS_IGNORED = "Unknown placeholder {0} was ignored";

    /**
     * Message warns that some event is at confirmation stage but it is not known. Probably some processing has failed.
     * List of params:
     *
     * <ul>
     * <li>0th is a name of product for which event was reported;
     * <li>1st is an event type;
     * </ul>
     */
    public static final String UNREPORTED_EVENT =
            "Event for the product {0} with type {1} attempted to be confirmed but it had not been reported yet. "
                    + "Probably appropriate process fail";

    private CommonsLogMessageConstant() {
        // Private constructor will prevent the instantiation of this class directly.
    }
}
