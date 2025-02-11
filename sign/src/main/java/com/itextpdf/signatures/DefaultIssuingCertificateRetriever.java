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
 * Empty {@link IIssuingCertificateRetriever} implementation for compatibility with the older code.
 */
class DefaultIssuingCertificateRetriever implements IIssuingCertificateRetriever {

    /**
     * Creates {@link DefaultIssuingCertificateRetriever} instance.
     */
    public DefaultIssuingCertificateRetriever() {
        // Empty constructor.
    }

    /**
     * {@inheritDoc}
     *
     * @param chain {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Certificate[] retrieveMissingCertificates(Certificate[] chain) {
        return chain;
    }

    /**
     * {@inheritDoc}
     *
     * @param crl {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Certificate[] getCrlIssuerCertificates(CRL crl) {
        return new Certificate[0];
    }

    @Override
    public Certificate[][] getCrlIssuerCertificatesByName(CRL crl) {
        return new Certificate[0][];
    }

    /**
     * {@inheritDoc}
     *
     * @param certificates {@inheritDoc}
     */
    @Override
    public void setTrustedCertificates(Collection<Certificate> certificates) {
        // Do nothing.
    }
}
