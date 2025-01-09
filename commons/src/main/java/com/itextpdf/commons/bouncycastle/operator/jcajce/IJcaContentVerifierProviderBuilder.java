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
package com.itextpdf.commons.bouncycastle.operator.jcajce;

import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.security.PublicKey;

/**
 * This interface represents the wrapper for JcaContentVerifierProviderBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaContentVerifierProviderBuilder {
    /**
     * Calls actual {@code setProvider} method for the wrapped JcaContentVerifierProviderBuilder object.
     *
     * @param provider provider name
     *
     * @return {@link IJcaContentVerifierProviderBuilder} this wrapper object.
     */
    IJcaContentVerifierProviderBuilder setProvider(String provider);

    /**
     * Calls actual {@code build} method for the wrapped JcaContentVerifierProviderBuilder object.
     *
     * @param publicKey public key
     *
     * @return {@link IContentVerifierProvider} the wrapper for built ContentVerifierProvider object.
     *
     * @throws AbstractOperatorCreationException wrapped OperatorCreationException.
     */
    IContentVerifierProvider build(PublicKey publicKey) throws AbstractOperatorCreationException;
}
