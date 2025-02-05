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
package com.itextpdf.kernel.crypto;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Class containing all the OID values used by iText.
 */
public final class OID {
    public static final String PKCS7_DATA = "1.2.840.113549.1.7.1";
    public static final String PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
    public static final String RSA = "1.2.840.113549.1.1.1";
    public static final String RSASSA_PSS = "1.2.840.113549.1.1.10";
    public static final String RSA_WITH_SHA256 = "1.2.840.113549.1.1.11";
    public static final String AA_SIGNING_CERTIFICATE_V1 = "1.2.840.113549.1.9.16.2.12";
    public static final String AA_SIGNING_CERTIFICATE_V2 = "1.2.840.113549.1.9.16.2.47";
    public static final String MGF1 = "1.2.840.113549.1.1.8";
    public static final String AA_TIME_STAMP_TOKEN = "1.2.840.113549.1.9.16.2.14";
    public static final String AUTHENTICATED_DATA = "1.2.840.113549.1.9.16.1.2";
    public static final String CONTENT_TYPE = "1.2.840.113549.1.9.3";
    public static final String MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    public static final String SIGNING_TIME = "1.2.840.113549.1.9.5";
    public static final String CMS_ALGORITHM_PROTECTION = "1.2.840.113549.1.9.52";
    public static final String DSA = "1.2.840.10040.4.1";
    public static final String ECDSA = "1.2.840.10045.2.1";
    public static final String ADBE_REVOCATION = "1.2.840.113583.1.1.8";
    public static final String TSA = "1.2.840.113583.1.1.9.1";

    public static final String RSA_WITH_SHA3_512 = "2.16.840.1.101.3.4.3.16";
    public static final String SHA_224 = "2.16.840.1.101.3.4.2.4";
    public static final String SHA_256 = "2.16.840.1.101.3.4.2.1";
    public static final String SHA_384 = "2.16.840.1.101.3.4.2.2";
    public static final String SHA_512 = "2.16.840.1.101.3.4.2.3";
    public static final String SHA3_224 = "2.16.840.1.101.3.4.2.7";
    public static final String SHA3_256 = "2.16.840.1.101.3.4.2.8";
    public static final String SHA3_384 = "2.16.840.1.101.3.4.2.9";
    public static final String SHA3_512 = "2.16.840.1.101.3.4.2.10";
    public static final String SHAKE_256 = "2.16.840.1.101.3.4.2.12";

    public static final String ED25519 = "1.3.101.112";
    public static final String ED448 = "1.3.101.113";
    public static final String OCSP = "1.3.6.1.5.5.7.48.1";
    public static final String CA_ISSUERS = "1.3.6.1.5.5.7.48.2";
    public static final String RI_OCSP_RESPONSE = "1.3.6.1.5.5.7.16.2";

    public static final String KDF_PDF_MAC_WRAP_KDF = "1.0.32004.1.1";
    public static final String CT_PDF_MAC_INTEGRITY_INFO = "1.0.32004.1.0";


    private OID() {
        // Empty on purpose. Avoiding instantiation of this class.
    }

    /**
     * Contains all OIDs used by iText in the context of Certificate Extensions.
     */
    public static final class X509Extensions {
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         * <p>
         * "Conforming CAs MUST mark this extension as non-critical."
         */
        public static final String AUTHORITY_KEY_IDENTIFIER = "2.5.29.35";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         * <p>
         * "Conforming CAs MUST mark this extension as non-critical."
         */
        public static final String SUBJECT_KEY_IDENTIFIER = "2.5.29.14";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String KEY_USAGE = "2.5.29.15";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String CERTIFICATE_POLICIES = "2.5.29.32";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String POLICY_MAPPINGS = "2.5.29.33";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String SUBJECT_ALTERNATIVE_NAME = "2.5.29.17";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String ISSUER_ALTERNATIVE_NAME = "2.5.29.18";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         * <p>
         * "Conforming CAs MUST mark this extension as non-critical."
         */
        public static final String SUBJECT_DIRECTORY_ATTRIBUTES = "2.5.29.9";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String BASIC_CONSTRAINTS = "2.5.29.19";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String NAME_CONSTRAINTS = "2.5.29.30";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String POLICY_CONSTRAINTS = "2.5.29.36";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String EXTENDED_KEY_USAGE = "2.5.29.37";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String CRL_DISTRIBUTION_POINTS = "2.5.29.31";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         */
        public static final String INHIBIT_ANY_POLICY = "2.5.29.54";
        /**
         * One of the standard extensions from https://tools.ietf.org/html/rfc5280
         * <p>
         * "The extension MUST be marked as non-critical by conforming CAs."
         */
        public static final String FRESHEST_CRL = "2.5.29.46";

        /**
         * One of the Internet Certificate Extensions also from https://tools.ietf.org/html/rfc5280
         * <p>
         * "The extension MUST be marked as non-critical by conforming CAs."
         */
        public static final String AUTHORITY_INFO_ACCESS = "1.3.6.1.5.5.7.1.1";
        /**
         * One of the Internet Certificate Extensions also from https://tools.ietf.org/html/rfc5280
         * <p>
         * "Conforming CAs MUST mark this extension as non-critical."
         */
        public static final String SUBJECT_INFO_ACCESS = "1.3.6.1.5.5.7.1.11";


        /**
         * One of the {@link #EXTENDED_KEY_USAGE} purposes from https://www.ietf.org/rfc/rfc2459.txt
         */
        public static final String ID_KP_TIMESTAMPING = "1.3.6.1.5.5.7.3.8";


        /**
         * Extension for OCSP responder certificate
         * from https://www.ietf.org/rfc/rfc2560.txt.
         */
        public static final String ID_PKIX_OCSP_NOCHECK = "1.3.6.1.5.5.7.48.1.5";

        /**
         * Extension for certificates from ETSI EN 319 412-1 V1.4.4.
         */
        public static final String VALIDITY_ASSURED_SHORT_TERM = "0.4.0.194121.2.1";

        /**
         * Extension for certificates from RFC 9608 which indicates that no revocation information is available.
         */
        public static final String NO_REV_AVAILABLE = "2.5.29.56";

        /**
         * According to https://tools.ietf.org/html/rfc5280 4.2. "Certificate Extensions":
         * "A certificate-using system MUST reject the certificate if it encounters a critical extension it
         * does not recognize or a critical extension that contains information that it cannot process."
         * <p>
         * This set consists of standard extensions which are defined in RFC specifications and are not mentioned
         * as forbidden to be marked as critical.
         */
        public static final Set<String> SUPPORTED_CRITICAL_EXTENSIONS = Collections.unmodifiableSet(
                new LinkedHashSet<>(Arrays.asList(
                        KEY_USAGE,
                        CERTIFICATE_POLICIES,
                        POLICY_MAPPINGS,
                        SUBJECT_ALTERNATIVE_NAME,
                        ISSUER_ALTERNATIVE_NAME,
                        BASIC_CONSTRAINTS,
                        NAME_CONSTRAINTS,
                        POLICY_CONSTRAINTS,
                        EXTENDED_KEY_USAGE,
                        CRL_DISTRIBUTION_POINTS,
                        INHIBIT_ANY_POLICY,
                        ID_PKIX_OCSP_NOCHECK
                )));
    }
}
