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
package com.itextpdf.signatures;

import java.security.GeneralSecurityException;

/**
 * Interface that needs to be implemented to do the actual signing.
 * For instance: you'll have to implement this interface if you want
 * to sign a PDF using a smart card.
 */
public interface IExternalSignature {

    /**
     * Returns the digest algorithm.
     *
     * @return	The digest algorithm (e.g. "SHA-1", "SHA-256,...").
     */
    String getDigestAlgorithmName();

    /**
     * Returns the signature algorithm used for signing, disregarding the digest function.
     *
     * @return The signature algorithm ("RSA", "DSA", "ECDSA", "Ed25519" or "Ed448").
     */
    String getSignatureAlgorithmName();

    /**
     * Return the algorithm parameters that need to be encoded together with the signature mechanism identifier.
     * If there are no parameters, return `null`.
     * A non-null value is required for RSASSA-PSS; see {@link RSASSAPSSMechanismParams}.
     *
     * @return algorithm parameters
     */
    ISignatureMechanismParams getSignatureMechanismParameters();

    /**
     * Signs the given message using the encryption algorithm in combination
     * with the hash algorithm.
     * @param message The message you want to be hashed and signed.
     * @return	A signed message digest.
     * @throws GeneralSecurityException when requested cryptographic algorithm or security provider
     * is not available
     */
    byte[] sign(byte[] message) throws GeneralSecurityException;
}
