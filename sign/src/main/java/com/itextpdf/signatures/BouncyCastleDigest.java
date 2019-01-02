/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
