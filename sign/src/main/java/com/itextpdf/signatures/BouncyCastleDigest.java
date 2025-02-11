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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * Implementation for digests accessed directly from the BouncyCastle library bypassing
 * any provider definition.
 */
public class BouncyCastleDigest implements IExternalDigest {

    @Override
    public MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException {
        String oid = DigestAlgorithms.getAllowedDigest(hashAlgorithm);

        if (oid == null) {
            throw new NoSuchAlgorithmException(hashAlgorithm);
        }

        Provider provider = BouncyCastleFactoryCreator.getFactory().getProvider();
        switch (oid) {
            // SHA1
            case "1.3.14.3.2.26":
                return MessageDigest.getInstance("SHA1", provider);
            // SHA224
            case OID.SHA_224:
                return MessageDigest.getInstance("SHA224", provider);
            // SHA256
            case OID.SHA_256:
                return MessageDigest.getInstance("SHA256", provider);
            // SHA384
            case OID.SHA_384:
                return MessageDigest.getInstance("SHA384", provider);
            // SHA512
            case OID.SHA_512:
                return MessageDigest.getInstance("SHA512", provider);
            // SHA3-224
            case OID.SHA3_224:
                return MessageDigest.getInstance("SHA3-224", provider);
            // SHA3-256
            case OID.SHA3_256:
                return MessageDigest.getInstance("SHA3-256", provider);
            // SHA3-384
            case OID.SHA3_384:
                return MessageDigest.getInstance("SHA3-384", provider);
            // SHA3-512
            case OID.SHA3_512:
                return MessageDigest.getInstance("SHA3-512", provider);
            // SHAKE-256 (512-bit)
            case OID.SHAKE_256:
                return MessageDigest.getInstance("SHAKE256", provider);
            // RIPEMD128
            case "1.3.36.3.2.2":
                return MessageDigest.getInstance("RIPEMD128", provider);
            // RIPEMD160
            case "1.3.36.3.2.1":
                return MessageDigest.getInstance("RIPEMD160", provider);
            // RIPEMD256
            case "1.3.36.3.2.3":
                return MessageDigest.getInstance("RIPEMD256", provider);
            // GOST3411
            case "1.2.643.2.2.9":
                return MessageDigest.getInstance("GOST3411", provider);
            default:
                throw new NoSuchAlgorithmException(hashAlgorithm);
        }
    }
}
