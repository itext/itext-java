/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.commons.bouncycastle.cert.ICipherParams;

import java.util.Objects;
import org.bouncycastle.crypto.CipherParameters;

/**
 * Wrapper for BouncyCastle cipher parameters.
 */
public class CipherParamsBC implements ICipherParams {

    private final CipherParameters cipherParameters;

    /**
     * Creates a new instance of {@link CipherParamsBC} with the specified BouncyCastle cipher parameters.
     *
     * @param cipherParameters the BouncyCastle cipher parameters to wrap
     */
    public CipherParamsBC(CipherParameters cipherParameters) {
        this.cipherParameters = cipherParameters;
    }

    /**
     * Returns the wrapped BouncyCastle cipher parameters.
     *
     * @return the wrapped BouncyCastle cipher parameters
     */
    public CipherParameters getCipherParameters() {
        return cipherParameters;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     *
     * @param o the reference object with which to compare
     *
     * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CipherParamsBC that = (CipherParamsBC) o;
        return Objects.equals(cipherParameters, that.cipherParameters);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(cipherParameters);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     *
     * @return a string representation of the wrapped object
     */
    @Override
    public String toString() {
        return this.cipherParameters.toString();
    }
}
