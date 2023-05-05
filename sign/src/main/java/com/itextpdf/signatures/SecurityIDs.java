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
package com.itextpdf.signatures;

/**
 * A list of IDs that are used by the security classes
 */
public class SecurityIDs {

    public static final String ID_PKCS7_DATA = "1.2.840.113549.1.7.1";
    public static final String ID_PKCS7_SIGNED_DATA = "1.2.840.113549.1.7.2";
    public static final String ID_RSA = "1.2.840.113549.1.1.1";
    public static final String ID_RSASSA_PSS = "1.2.840.113549.1.1.10";
    public static final String ID_RSA_WITH_SHA256 = "1.2.840.113549.1.1.11";
    public static final String ID_RSA_WITH_SHA3_512 = "2.16.840.1.101.3.4.3.16";
    public static final String ID_DSA = "1.2.840.10040.4.1";
    public static final String ID_ECDSA = "1.2.840.10045.2.1";

    public static final String ID_ED25519 = "1.3.101.112";
    public static final String ID_ED448 = "1.3.101.113";
    public static final String ID_SHA512 = "2.16.840.1.101.3.4.2.3";
    public static final String ID_SHAKE256 = "2.16.840.1.101.3.4.2.12";
    public static final String ID_CONTENT_TYPE = "1.2.840.113549.1.9.3";
    public static final String ID_MESSAGE_DIGEST = "1.2.840.113549.1.9.4";
    public static final String ID_SIGNING_TIME = "1.2.840.113549.1.9.5";
    public static final String ID_ADBE_REVOCATION = "1.2.840.113583.1.1.8";
    public static final String ID_TSA = "1.2.840.113583.1.1.9.1";
    public static final String ID_OCSP = "1.3.6.1.5.5.7.48.1";
    public static final String ID_AA_SIGNING_CERTIFICATE_V1 = "1.2.840.113549.1.9.16.2.12";
    public static final String ID_AA_SIGNING_CERTIFICATE_V2 = "1.2.840.113549.1.9.16.2.47";
    public static final String ID_MGF1 = "1.2.840.113549.1.1.8";
}
