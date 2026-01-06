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
package com.itextpdf.signatures.logs;

/**
 * Class which contains constants to be used in logging inside sign module.
 */
public final class SignLogMessageConstant {
    public static final String COUNTRY_SPECIFIC_FETCHING_FAILED =
            "Country specific Lotl fetching with schema name \"{0}\" failed because of:\n \"{1}\"";
    public static final String EXCEPTION_WITHOUT_MESSAGE =
            "Unexpected exception without message was thrown during keystore processing";
    public static final String UNABLE_TO_PARSE_AIA_CERT = "Unable to parse certificates coming from authority info "
            + "access extension. Those won't be included into the certificate chain.";
    public static final String REVOCATION_DATA_NOT_ADDED_VALIDITY_ASSURED =
            "Revocation data for certificate: \"{0}\" is not added due to validity assured - short term extension.";
    public static final String UNABLE_TO_PARSE_REV_INFO = "Unable to parse signed data revocation info item " +
            "since it is incorrect or unsupported (e.g. SCVP Request and Response).";
    public static final String VALID_CERTIFICATE_IS_REVOKED = "The certificate was valid on the verification date, " +
            "but has been revoked since {0}.";
    public static final String UPDATING_MAIN_LOTL_TO_CACHE_FAILED = "Unable to update cache with main Lotl file. " +
            "Downloading of the main Lotl file failed.\n{0}";
    public static final String UPDATING_PIVOT_TO_CACHE_FAILED = "Unable to pivot files " +
            "because of pivot file fetching failure.\n{0}";
    public static final String FAILED_TO_FETCH_COUNTRY_SPECIFIC_LOTL = "Problem occurred while fetching " +
            "country specific Lotl files.\n{0}";
    public static final String NO_COUNTRY_SPECIFIC_LOTL_FETCHED = "Zero country specific Lotl files were fetched.";
    public static final String FAILED_TO_FETCH_EU_JOURNAL_CERTIFICATES = "Problem occurred while fetching " +
            "EU Journal certificates.\n{0}";
    public static final String OJ_TRANSITION_PERIOD =
            "Main LOTL file contains two Official Journal of European Union links. " +
                    "This usually indicates that transition period for Official Journal has started. " +
                    "Newest version of Official Journal should be used from now on " +
                    "to retrieve trusted certificates and LOTL location.";
    public static final String COUNTRY_NOT_REQUIRED_BY_CONFIGURATION = "Country \"{0}\" is not required by "
            + "lotlFetchingProperties, and not be used when validating.";

    private SignLogMessageConstant() {
        // Private constructor will prevent the instantiation of this class directly
    }
}
