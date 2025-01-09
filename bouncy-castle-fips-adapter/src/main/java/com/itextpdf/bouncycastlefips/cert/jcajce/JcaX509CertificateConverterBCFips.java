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
package com.itextpdf.bouncycastlefips.cert.jcajce;

import com.itextpdf.bouncycastlefips.cert.X509CertificateHolderBCFips;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;

import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

/**
 * Wrapper class for {@link JcaX509CertificateConverter}.
 */
public class JcaX509CertificateConverterBCFips implements IJcaX509CertificateConverter {
    private final JcaX509CertificateConverter certificateConverter;

    /**
     * Creates new wrapper instance for {@link JcaX509CertificateConverter}.
     *
     * @param certificateConverter {@link JcaX509CertificateConverter} to be wrapped
     */
    public JcaX509CertificateConverterBCFips(JcaX509CertificateConverter certificateConverter) {
        this.certificateConverter = certificateConverter;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaX509CertificateConverter}.
     */
    public JcaX509CertificateConverter getCertificateConverter() {
        return certificateConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate getCertificate(IX509CertificateHolder certificateHolder) throws CertificateException {
        return certificateConverter.getCertificate(
                ((X509CertificateHolderBCFips) certificateHolder).getCertificateHolder());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaX509CertificateConverter setProvider(Provider provider) {
        certificateConverter.setProvider(provider);
        return this;
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
        JcaX509CertificateConverterBCFips that = (JcaX509CertificateConverterBCFips) o;
        return Objects.equals(certificateConverter, that.certificateConverter);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateConverter);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateConverter.toString();
    }
}
