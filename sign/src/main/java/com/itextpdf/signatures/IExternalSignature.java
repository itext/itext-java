/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
 * @author Paulo Soares
 */
public interface IExternalSignature {

    /**
     * Returns the hash algorithm.
     * @return	The hash algorithm (e.g. "SHA-1", "SHA-256,...").
     */
    String getHashAlgorithm();

    /**
     * Returns the encryption algorithm used for signing.
     * @return The encryption algorithm ("RSA" or "DSA").
     */
    String getEncryptionAlgorithm();

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
