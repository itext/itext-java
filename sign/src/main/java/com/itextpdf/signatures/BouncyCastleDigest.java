/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;

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
            case "2.16.840.1.101.3.4.2.4":
                return MessageDigest.getInstance("SHA224", provider);
            // SHA256
            case "2.16.840.1.101.3.4.2.1":
                return MessageDigest.getInstance("SHA256", provider);
            // SHA384
            case "2.16.840.1.101.3.4.2.2":
                return MessageDigest.getInstance("SHA384", provider);
            // SHA512
            case "2.16.840.1.101.3.4.2.3":
                return MessageDigest.getInstance("SHA512", provider);
            // SHA3-224
            case "2.16.840.1.101.3.4.2.7":
                return MessageDigest.getInstance("SHA3-224", provider);
            // SHA3-256
            case "2.16.840.1.101.3.4.2.8":
                return MessageDigest.getInstance("SHA3-256", provider);
            // SHA3-384
            case "2.16.840.1.101.3.4.2.9":
                return MessageDigest.getInstance("SHA3-384", provider);
            // SHA3-512
            case "2.16.840.1.101.3.4.2.10":
                return MessageDigest.getInstance("SHA3-512", provider);
            // SHAKE-256 (512-bit)
            case "2.16.840.1.101.3.4.2.12":
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
