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

import com.itextpdf.bouncycastlefips.operator.InputDecryptorProviderBCFips;
import com.itextpdf.bouncycastlefips.operator.OperatorCreationExceptionBCFips;
import com.itextpdf.commons.bouncycastle.openssl.jcajce.IJceOpenSSLPKCS8DecryptorProviderBuilder;
import com.itextpdf.commons.bouncycastle.operator.IInputDecryptorProvider;

import java.security.Provider;
import java.util.Objects;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.OperatorCreationException;

/**
 * Wrapper class for {@link JceOpenSSLPKCS8DecryptorProviderBuilder}.
 */
public class JceOpenSSLPKCS8DecryptorProviderBuilderBCFips implements IJceOpenSSLPKCS8DecryptorProviderBuilder {
    private final JceOpenSSLPKCS8DecryptorProviderBuilder providerBuilder;

    /**
     * Creates new wrapper instance for {@link JceOpenSSLPKCS8DecryptorProviderBuilder}.
     *
     * @param providerBuilder {@link JceOpenSSLPKCS8DecryptorProviderBuilder} to be wrapped
     */
    public JceOpenSSLPKCS8DecryptorProviderBuilderBCFips(JceOpenSSLPKCS8DecryptorProviderBuilder providerBuilder) {
        this.providerBuilder = providerBuilder;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link JceOpenSSLPKCS8DecryptorProviderBuilder}.
     */
    public JceOpenSSLPKCS8DecryptorProviderBuilder getProviderBuilder() {
        return providerBuilder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IJceOpenSSLPKCS8DecryptorProviderBuilder setProvider(Provider provider) {
        providerBuilder.setProvider(provider);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IInputDecryptorProvider build(char[] password) throws OperatorCreationExceptionBCFips {
        try {
            return new InputDecryptorProviderBCFips(providerBuilder.build(password));
        } catch (OperatorCreationException e) {
            throw new OperatorCreationExceptionBCFips(e);
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
        JceOpenSSLPKCS8DecryptorProviderBuilderBCFips that = (JceOpenSSLPKCS8DecryptorProviderBuilderBCFips) o;
        return Objects.equals(providerBuilder, that.providerBuilder);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(providerBuilder);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return providerBuilder.toString();
    }
}
