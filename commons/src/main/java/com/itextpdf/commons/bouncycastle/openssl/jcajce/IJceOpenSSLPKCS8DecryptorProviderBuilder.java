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
package com.itextpdf.commons.bouncycastle.openssl.jcajce;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IInputDecryptorProvider;

import java.security.Provider;

/**
 * This interface represents the wrapper for JceOpenSSLPKCS8DecryptorProviderBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJceOpenSSLPKCS8DecryptorProviderBuilder {
    /**
     * Calls actual {@code setProvider} method for the wrapped JceOpenSSLPKCS8DecryptorProviderBuilder object.
     *
     * @param provider {@link Provider} to be set
     * @return this builder
     */
    IJceOpenSSLPKCS8DecryptorProviderBuilder setProvider(Provider provider);

    /**
     * Calls actual {@code build} method for the wrapped JceOpenSSLPKCS8DecryptorProviderBuilder object.
     * 
     * @param password {@code char[]} which represents password for the private key
     * @return {@link IInputDecryptorProvider} input decryptor provider
     * @throws AbstractOperatorCreationException if any issues occur during provider building
     */
    IInputDecryptorProvider build(char[] password) throws AbstractOperatorCreationException;
}
