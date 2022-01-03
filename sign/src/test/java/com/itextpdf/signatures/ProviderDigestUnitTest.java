/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Category(UnitTest.class)
public class ProviderDigestUnitTest extends ExtendedITextTest {

    @Test
    public void getMessageDigestSunPKCS11SHA1Test() throws GeneralSecurityException {
        ProviderDigest providerDigest = new ProviderDigest("SunPKCS11");
        MessageDigest digest = providerDigest.getMessageDigest(DigestAlgorithms.SHA1);
        Assert.assertNotNull(digest);
        Assert.assertEquals("SUN", digest.getProvider().getName());
        Assert.assertEquals(DigestAlgorithms.SHA1, digest.getAlgorithm());
    }

    @Test
    public void getMessageDigestSUNSHA256Test() throws GeneralSecurityException {
        ProviderDigest providerDigest = new ProviderDigest("SUN");
        MessageDigest digest = providerDigest.getMessageDigest(DigestAlgorithms.SHA256);
        Assert.assertNotNull(digest);
        Assert.assertEquals("SUN", digest.getProvider().getName());
        Assert.assertEquals(DigestAlgorithms.SHA256, digest.getAlgorithm());
    }

    @Test
    public void getMessageDigestNoSuchProviderExceptionTest() {
        ProviderDigest providerDigest = new ProviderDigest("doesn't exist");
        Exception e = Assert.assertThrows(NoSuchProviderException.class,
                () -> providerDigest.getMessageDigest(DigestAlgorithms.SHA256));
        Assert.assertEquals("no such provider: doesn't exist", e.getMessage());
    }

    @Test
    public void getMessageDigestNoSuchAlgorithmExceptionTest() {
        ProviderDigest providerDigest = new ProviderDigest("SunPKCS11");
        Exception e = Assert.assertThrows(NoSuchAlgorithmException.class,
                () -> providerDigest.getMessageDigest("doesn't exist"));
        Assert.assertEquals("doesn't exist MessageDigest not available", e.getMessage());
    }

    @Test
    public void getMessageDigestMissingProviderExceptionTest() {
        ProviderDigest providerDigest = new ProviderDigest("");
        Exception e = Assert.assertThrows(IllegalArgumentException.class,
                () -> providerDigest.getMessageDigest(DigestAlgorithms.SHA1));
        Assert.assertEquals("missing provider", e.getMessage());
    }
}
