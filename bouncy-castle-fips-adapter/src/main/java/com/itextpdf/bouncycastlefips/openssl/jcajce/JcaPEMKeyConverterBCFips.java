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
package com.itextpdf.bouncycastlefips.openssl.jcajce;

import com.itextpdf.bouncycastlefips.asn1.pcks.PrivateKeyInfoBCFips;
import com.itextpdf.bouncycastlefips.openssl.PEMExceptionBCFips;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPrivateKeyInfo;
import com.itextpdf.commons.bouncycastle.openssl.jcajce.IJcaPEMKeyConverter;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.Objects;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

/**
 * Wrapper class for {@link JcaPEMKeyConverter}.
 */
public class JcaPEMKeyConverterBCFips implements IJcaPEMKeyConverter {
    private final JcaPEMKeyConverter keyConverter;

    /**
     * Creates new wrapper instance for {@link JcaPEMKeyConverter}.
     *
     * @param keyConverter {@link JcaPEMKeyConverter} to be wrapped
     */
    public JcaPEMKeyConverterBCFips(JcaPEMKeyConverter keyConverter) {
        this.keyConverter = keyConverter;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JcaPEMKeyConverter}.
     */
    public JcaPEMKeyConverter getKeyConverter() {
        return keyConverter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJcaPEMKeyConverter setProvider(Provider provider) {
        keyConverter.setProvider(provider);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PrivateKey getPrivateKey(IPrivateKeyInfo privateKeyInfo) throws PEMExceptionBCFips {
        try {
            return keyConverter.getPrivateKey(((PrivateKeyInfoBCFips) privateKeyInfo).getPrivateKeyInfo());
        } catch (PEMException e) {
            throw new PEMExceptionBCFips(e);
        }
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
        JcaPEMKeyConverterBCFips that = (JcaPEMKeyConverterBCFips) o;
        return Objects.equals(keyConverter, that.keyConverter);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(keyConverter);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return keyConverter.toString();
    }
}
