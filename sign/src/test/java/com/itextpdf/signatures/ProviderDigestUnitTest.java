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

import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.test.ExtendedITextTest;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ProviderDigestUnitTest extends ExtendedITextTest {

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6446 fix differences in java.security)
    public void getMessageDigestSunPKCS11SHA1Test() throws GeneralSecurityException {
        ProviderDigest providerDigest = new ProviderDigest("SunPKCS11");
        MessageDigest digest = providerDigest.getMessageDigest(DigestAlgorithms.SHA1);
        Assertions.assertNotNull(digest);
        Assertions.assertEquals("SUN", digest.getProvider().getName());
        Assertions.assertEquals(DigestAlgorithms.SHA1, digest.getAlgorithm());
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6446 fix differences in java.security)
    public void getMessageDigestSUNSHA256Test() throws GeneralSecurityException {
        ProviderDigest providerDigest = new ProviderDigest("SUN");
        MessageDigest digest = providerDigest.getMessageDigest(DigestAlgorithms.SHA256);
        Assertions.assertNotNull(digest);
        Assertions.assertEquals("SUN", digest.getProvider().getName());
        Assertions.assertEquals(DigestAlgorithms.SHA256, digest.getAlgorithm());
    }

    @Test
    public void getMessageDigestNoSuchProviderExceptionTest() {
        ProviderDigest providerDigest = new ProviderDigest("doesn't exist");
        Exception e = Assertions.assertThrows(NoSuchProviderException.class,
                () -> providerDigest.getMessageDigest(DigestAlgorithms.SHA256));
        Assertions.assertEquals("no such provider: doesn't exist", e.getMessage());
    }

    @Test
    public void getMessageDigestNoSuchAlgorithmExceptionTest() {
        ProviderDigest providerDigest = new ProviderDigest("SunPKCS11");
        Exception e = Assertions.assertThrows(NoSuchAlgorithmException.class,
                () -> providerDigest.getMessageDigest("doesn't exist"));
        Assertions.assertEquals("doesn't exist MessageDigest not available", e.getMessage());
    }

    @Test
    public void getMessageDigestMissingProviderExceptionTest() {
        ProviderDigest providerDigest = new ProviderDigest("");
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> providerDigest.getMessageDigest(DigestAlgorithms.SHA1));
        Assertions.assertEquals("missing provider", e.getMessage());
    }
}
