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

import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPrivateKeyInfo;
import com.itextpdf.commons.bouncycastle.openssl.AbstractPEMException;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Provider;

/**
 * This interface represents the wrapper for JcaPEMKeyConverter that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaPEMKeyConverter {
    /**
     * Calls actual {@code setProvider} method for the wrapped JcaPEMKeyConverter object.
     * 
     * @param provider {@link Provider} to be set
     * @return this converter
     */
    IJcaPEMKeyConverter setProvider(Provider provider);

    /**
     * Calls actual {@code getPrivateKey} method for the wrapped JcaPEMKeyConverter object.
     * 
     * @param privateKeyInfo {@link IPrivateKeyInfo} information about private key
     * @return {@link PrivateKey} private key instance
     * @throws AbstractPEMException if any issues occur during private key creation
     */
    PrivateKey getPrivateKey(IPrivateKeyInfo privateKeyInfo) throws AbstractPEMException;
}
