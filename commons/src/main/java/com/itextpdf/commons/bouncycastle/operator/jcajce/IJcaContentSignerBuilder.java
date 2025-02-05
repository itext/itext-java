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
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.security.PrivateKey;

/**
 * This interface represents the wrapper for JcaContentSignerBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaContentSignerBuilder {
    /**
     * Calls actual {@code build} method for the wrapped JcaContentSignerBuilder object.
     *
     * @param pk private key
     *
     * @return {@link IContentSigner} the wrapper for built ContentSigner object.
     *
     * @throws AbstractOperatorCreationException wrapped OperatorCreationException.
     */
    IContentSigner build(PrivateKey pk) throws AbstractOperatorCreationException;

    /**
     * Calls actual {@code setProvider} method for the wrapped JcaContentSignerBuilder object.
     *
     * @param providerName provider name
     *
     * @return {@link IJcaContentSignerBuilder} this wrapper object.
     */
    IJcaContentSignerBuilder setProvider(String providerName);
}
