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

package com.itextpdf.commons.logs;

/**
 * Class that bundles all the error message templates as constants.
 */
public final class CommonsLogMessageConstant {
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
