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

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;

/**
 * Interface that needs to be implemented if you want to embed
 * Certificate Revocation Lists (CRL) into your PDF.
 */
public interface ICrlClient {

    /**
     * Gets an encoded byte array.
     *
     * @param    checkCert The certificate which a CRL URL can be obtained from.
     * @param    url    A CRL url if you don't want to obtain it from the certificate.
     * @return A collection of byte array each representing a crl. It may return null or an empty collection.
     * @throws CertificateEncodingException if an encoding error occurs in {@link X509Certificate}.
     */
    Collection<byte[]> getEncoded(X509Certificate checkCert, String url)
            throws CertificateEncodingException;
}
