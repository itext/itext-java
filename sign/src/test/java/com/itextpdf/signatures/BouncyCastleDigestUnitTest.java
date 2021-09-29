/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BouncyCastleDigestUnitTest extends ExtendedITextTest {

    @Test
    public void getMessageDigestMD2Test() throws GeneralSecurityException {
        getMessageDigestTest("MD2", "MD2");
    }

    @Test
    public void getMessageDigestMD5Test() throws GeneralSecurityException {
        getMessageDigestTest("MD5", "MD5");
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
        getMessageDigestTest("RIPEMD128", "RIPEMD128");
    }

    @Test
    public void getMessageDigestRIPEMD160Test() throws GeneralSecurityException {
        getMessageDigestTest("RIPEMD160", "RIPEMD160");
    }

    @Test
    public void getMessageDigestRIPEMD256Test() throws GeneralSecurityException {
        getMessageDigestTest("RIPEMD256", "RIPEMD256");
    }

    @Test
    public void getMessageDigestGOST3411Test() throws GeneralSecurityException {
        getMessageDigestTest("GOST3411", "GOST3411");
    }

    @Test
    // TODO DEVSIX-5800 throw an correct exception if there is no digest for an algorithm
    public void getMessageDigestNullTest() {
        IExternalDigest digest = new BouncyCastleDigest();
        Assert.assertThrows(NullPointerException.class, () -> digest.getMessageDigest(null));
    }

    @Test
    // TODO DEVSIX-5800 throw an correct exception if there is no digest for an algorithm
    public void getMessageDigestUnknownTest() {
        IExternalDigest digest = new BouncyCastleDigest();
        Assert.assertThrows(NullPointerException.class, () -> digest.getMessageDigest("unknown"));
    }

    private static void getMessageDigestTest(String hashAlgorithm, String expectedDigestAlgorithm)
            throws GeneralSecurityException {
        MessageDigest digest = new BouncyCastleDigest().getMessageDigest(hashAlgorithm);
        Assert.assertNotNull(digest);
        Assert.assertEquals(expectedDigestAlgorithm, digest.getAlgorithm());
    }
}
