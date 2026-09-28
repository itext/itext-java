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
package com.itextpdf.bouncycastle.rsa;

import com.itextpdf.bouncycastle.cert.CipherParamsBC;
import com.itextpdf.commons.bouncycastle.cert.ICipherParams;
import com.itextpdf.commons.bouncycastle.rsa.IRSADigestSigner;

import java.util.Objects;
import org.bouncycastle.crypto.signers.RSADigestSigner;

/**
 * Wrapper class for {@link RSADigestSigner}.
 */
public class RSADigestSignerBC implements IRSADigestSigner {

    private final RSADigestSigner rsaDigestSigner;

    /**
     * Creates a new instance of {@link RSADigestSignerBC} with the specified BouncyCastle RSADigestSigner.
     *
     * @param rsaDigestSigner the BouncyCastle RSADigestSigner to wrap
     */
    public RSADigestSignerBC(RSADigestSigner rsaDigestSigner) {
        this.rsaDigestSigner = rsaDigestSigner;
    }

    /**
     * Returns the wrapped BouncyCastle RSADigestSigner.
     *
     * @return the wrapped BouncyCastle RSADigestSigner
     */
    public RSADigestSigner getRsaDigestSigner() {
        return rsaDigestSigner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(boolean forSigning, ICipherParams parameters) {
        rsaDigestSigner.init(forSigning, ((CipherParamsBC) parameters).getCipherParameters());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(byte[] input, int inOff, int length) {
        rsaDigestSigner.update(input, inOff, length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean verifySignature(byte[] signature) {
        return rsaDigestSigner.verifySignature(signature);
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
        RSADigestSignerBC that = (RSADigestSignerBC) o;
        return Objects.equals(rsaDigestSigner, that.rsaDigestSigner);
    }

    /**
     * Returns a hash code value based on the wrapped object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(rsaDigestSigner);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     *
     * @return a string representation of the wrapped object
     */
    @Override
    public String toString() {
        return rsaDigestSigner.toString();
    }
}
