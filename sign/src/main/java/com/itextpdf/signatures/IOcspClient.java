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
package com.itextpdf.signatures;

import java.security.cert.X509Certificate;

/**
 * Interface for the Online Certificate Status Protocol (OCSP) Client.
 */
public interface IOcspClient {

    /**
     * Fetch a DER-encoded BasicOCSPResponse from an OCSP responder. The method should not throw
     * an exception.
     *
     * <p>
     *     Note: do not pass in the full DER-encoded OCSPResponse object obtained from the responder,
     *     only the DER-encoded BasicOCSPResponse value contained in the response data.
     *
     * @param checkCert  Certificate to check.
     * @param issuerCert The parent certificate.
     * @param url        The URL of the OCSP responder endpoint. If null, implementations can
     *                   attempt to obtain a URL from the AuthorityInformationAccess extension of
     *                   the certificate, or from another implementation-specific source.
     * @return a byte array containing a DER-encoded BasicOCSPResponse structure or null if one
     *         could not be obtained
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6960#section-4.2.1">RFC 6960 § 4.2.1</a>
     */
    byte[] getEncoded(X509Certificate checkCert, X509Certificate issuerCert, String url);
}
