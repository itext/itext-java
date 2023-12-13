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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.jcajce.provider.digest.GOST3411;
import org.bouncycastle.jcajce.provider.digest.MD2;
import org.bouncycastle.jcajce.provider.digest.MD5;
import org.bouncycastle.jcajce.provider.digest.RIPEMD128;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.jcajce.provider.digest.RIPEMD256;
import org.bouncycastle.jcajce.provider.digest.SHA1;
import org.bouncycastle.jcajce.provider.digest.SHA224;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.bouncycastle.jcajce.provider.digest.SHA384;
import org.bouncycastle.jcajce.provider.digest.SHA512;

/**
 * Implementation for digests accessed directly from the BouncyCastle library bypassing
 * any provider definition.
 */
public class BouncyCastleDigest implements IExternalDigest {

    @Override
    public MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException {
        String oid = DigestAlgorithms.getAllowedDigest(hashAlgorithm);

        switch (oid) {
            case "1.2.840.113549.2.2":      //MD2
                return new MD2.Digest();
            case "1.2.840.113549.2.5":      //MD5
                return new MD5.Digest();
            case "1.3.14.3.2.26":           //SHA1
                return new SHA1.Digest();
            case "2.16.840.1.101.3.4.2.4":  //SHA224
                return new SHA224.Digest();
            case "2.16.840.1.101.3.4.2.1":  //SHA256
                return new SHA256.Digest();
            case "2.16.840.1.101.3.4.2.2":  //SHA384
                return new SHA384.Digest();
            case "2.16.840.1.101.3.4.2.3":  //SHA512
                return new SHA512.Digest();
            case "1.3.36.3.2.2":            //RIPEMD128
                return new RIPEMD128.Digest();
            case "1.3.36.3.2.1":            //RIPEMD160
                return new RIPEMD160.Digest();
            case "1.3.36.3.2.3":            //RIPEMD256
                return new RIPEMD256.Digest();
            case "1.2.643.2.2.9":           //GOST3411
                return new GOST3411.Digest();
            default:
                throw new NoSuchAlgorithmException(hashAlgorithm);
        }
    }
}
