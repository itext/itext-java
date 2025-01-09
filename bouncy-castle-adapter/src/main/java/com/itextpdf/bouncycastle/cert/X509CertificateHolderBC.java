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
package com.itextpdf.bouncycastle.cert;

import com.itextpdf.bouncycastle.asn1.x509.AlgorithmIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

import java.io.IOException;
import java.util.Objects;

import org.bouncycastle.cert.X509CertificateHolder;

/**
 * Wrapper class for {@link X509CertificateHolder}.
 */
public class X509CertificateHolderBC implements IX509CertificateHolder {
    private final X509CertificateHolder certificateHolder;

    /**
     * Creates new wrapper instance for {@link X509CertificateHolder}.
     *
     * @param certificateHolder {@link X509CertificateHolder} to be wrapped
     */
    public X509CertificateHolderBC(X509CertificateHolder certificateHolder) {
        this.certificateHolder = certificateHolder;
    }

    /**
     * Creates new wrapper instance for {@link X509CertificateHolder}.
     *
     * @param bytes bytes array to create {@link X509CertificateHolder} to be wrapped
     * @throws IOException {@link X509CertificateHolder} object cannot be created from byte array.
     */
    public X509CertificateHolderBC(byte[] bytes) throws IOException {
        this.certificateHolder = new X509CertificateHolder(bytes);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link X509CertificateHolder}.
     */
    public X509CertificateHolder getCertificateHolder() {
        return certificateHolder;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getSignatureAlgorithm() {
        return new AlgorithmIdentifierBC(certificateHolder.getSignatureAlgorithm());
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        X509CertificateHolderBC that = (X509CertificateHolderBC) o;
        return Objects.equals(certificateHolder, that.certificateHolder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateHolder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateHolder.toString();
    }
}
