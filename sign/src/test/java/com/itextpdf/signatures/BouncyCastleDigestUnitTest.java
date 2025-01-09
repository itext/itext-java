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
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.test.ExtendedITextTest;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class BouncyCastleDigestUnitTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @Test
    public void getMessageDigestMD2Test() {
        IExternalDigest digest = new BouncyCastleDigest();
        Assertions.assertThrows(NoSuchAlgorithmException.class, () -> digest.getMessageDigest("MD2"));
    }

    @Test
    public void getMessageDigestMD5Test() throws GeneralSecurityException {
        IExternalDigest digest = new BouncyCastleDigest();
        Assertions.assertThrows(NoSuchAlgorithmException.class, () -> digest.getMessageDigest("MD5"));
    }

    @Test
    public void getMessageDigestSHA1Test() throws GeneralSecurityException {
        getMessageDigestTest("SHA1", "SHA-1");
    }

    @Test
    public void getMessageDigestSHA224Test() throws GeneralSecurityException {
        getMessageDigestTest("SHA224", "SHA-224");
    }

    @Test
    public void getMessageDigestSHA256Test() throws GeneralSecurityException {
        getMessageDigestTest("SHA256", "SHA-256");
    }

    @Test
    public void getMessageDigestSHA384Test() throws GeneralSecurityException {
        getMessageDigestTest("SHA384", "SHA-384");
    }

    @Test
    public void getMessageDigestSHA512Test() throws GeneralSecurityException {
        getMessageDigestTest("SHA512", "SHA-512");
    }

    @Test
    public void getMessageDigestRIPEMD128Test() throws GeneralSecurityException {
        if (FACTORY.isInApprovedOnlyMode()) {
            Assertions.assertThrows(NoSuchAlgorithmException.class,
                    () -> new BouncyCastleDigest().getMessageDigest("RIPEMD128"));
        } else {
            getMessageDigestTest("RIPEMD128", "RIPEMD128");
        }
    }

    @Test
    public void getMessageDigestRIPEMD160Test() throws GeneralSecurityException {
        if (FACTORY.isInApprovedOnlyMode()) {
            Assertions.assertThrows(NoSuchAlgorithmException.class,
                    () -> new BouncyCastleDigest().getMessageDigest("RIPEMD160"));
        } else {
            getMessageDigestTest("RIPEMD160", "RIPEMD160");
        }
    }

    @Test
    public void getMessageDigestRIPEMD256Test() throws GeneralSecurityException {
        if (FACTORY.isInApprovedOnlyMode()) {
            Assertions.assertThrows(NoSuchAlgorithmException.class,
                    () -> new BouncyCastleDigest().getMessageDigest("RIPEMD256"));
        } else {
            getMessageDigestTest("RIPEMD256", "RIPEMD256");
        }
    }

    @Test
    public void getMessageDigestGOST3411Test() throws GeneralSecurityException {
        if (FACTORY.isInApprovedOnlyMode()) {
            Assertions.assertThrows(NoSuchAlgorithmException.class,
                    () -> new BouncyCastleDigest().getMessageDigest("GOST3411"));
        } else {
            getMessageDigestTest("GOST3411", "GOST3411");
        }
    }

    @Test
    public void getMessageDigestNullTest() {
        IExternalDigest digest = new BouncyCastleDigest();
        Assertions.assertThrows(IllegalArgumentException.class, () -> digest.getMessageDigest(null));
    }

    @Test
    public void getMessageDigestUnknownTest() {
        IExternalDigest digest = new BouncyCastleDigest();
        Assertions.assertThrows(NoSuchAlgorithmException.class, () -> digest.getMessageDigest("unknown"));
    }

    private static void getMessageDigestTest(String hashAlgorithm, String expectedDigestAlgorithm)
            throws GeneralSecurityException {
        MessageDigest digest = new BouncyCastleDigest().getMessageDigest(hashAlgorithm);
        Assertions.assertNotNull(digest);
        Assertions.assertEquals(expectedDigestAlgorithm, digest.getAlgorithm());
    }
}
