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
package com.itextpdf.commons.bouncycastle.pkcs;

import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPrivateKeyInfo;
import com.itextpdf.commons.bouncycastle.operator.IInputDecryptorProvider;

/**
 * This interface represents the wrapper for PKCS8EncryptedPrivateKeyInfo that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IPKCS8EncryptedPrivateKeyInfo {
    /**
     * Calls actual {@code decryptPrivateKeyInfo} method for the wrapped PKCS8EncryptedPrivateKeyInfo object.
     * 
     * @param decryptorProvider {@link IInputDecryptorProvider} wrapper to be used during decryption
     * @return {@link IPrivateKeyInfo} wrapper for private key info
     * @throws AbstractPKCSException if any issues occur during decryption
     */
    IPrivateKeyInfo decryptPrivateKeyInfo(IInputDecryptorProvider decryptorProvider) throws AbstractPKCSException;
}
