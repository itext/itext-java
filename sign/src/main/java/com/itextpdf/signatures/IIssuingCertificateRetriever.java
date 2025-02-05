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

import java.security.cert.CRL;
import java.security.cert.Certificate;
import java.util.Collection;

/**
 * Interface helper to support retrieving CAIssuers certificates from Authority Information Access (AIA) Extension in
 * order to support certificate chains with missing certificates and getting CRL response issuer certificates.
 */
public interface IIssuingCertificateRetriever {
    /**
     * Retrieves missing certificates in chain using certificate Authority Information Access (AIA) Extension.
     *
     * @param chain certificate chain to restore with at least signing certificate.
     *
     * @return full chain of trust or maximum chain that could be restored in case missing certificates cannot be
     * retrieved from AIA extension.
     */
    Certificate[] retrieveMissingCertificates(Certificate[] chain);

    /**
     * Retrieves the certificate chain for the certificate that should be used to verify the signature on the
     * CRL response using CRL Authority Information Access (AIA) Extension and known certificates.
     *
     * @param crl CRL response to retrieve issuer for.
     *
     * @return certificates retrieved from CRL AIA extension or an empty list in case certificates cannot be retrieved.
     */
    Certificate[] getCrlIssuerCertificates(CRL crl);

    /**
     * Retrieves the certificate chaind for the certificates that could be used to verify the signature on the
     * CRL response using CRL Authority Information Access (AIA) Extension and known certificates.
     *
     * @param crl CRL response to retrieve issuer for.
     *
     * @return certificates retrieved from CRL AIA extension or an empty list in case certificates cannot be retrieved.
     */
    Certificate[][] getCrlIssuerCertificatesByName(CRL crl);

    /**
     * Sets trusted certificate list to be used for the missing certificates retrieving by the issuer name.
     *
     * @param certificates certificate list for getting missing certificates in chain
     *                     or CRL response issuer certificates.
     */
    void setTrustedCertificates(Collection<Certificate> certificates);
}
