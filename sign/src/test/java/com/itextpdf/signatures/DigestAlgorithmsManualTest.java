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
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class DigestAlgorithmsManualTest extends ExtendedITextTest {

    @Test
    public void digestSHA1SunPKCS11Test() throws GeneralSecurityException, IOException {
        InputStream data = new ByteArrayInputStream(new byte[] {13, 16, 20, 0, 10});
        byte[] hash = DigestAlgorithms.digest(data, DigestAlgorithms.SHA1, "SunPKCS11");
        byte[] expected =
                new byte[] {15, 20, 1, 9, -106, 49, -37, -65, -45, -63, 53, -70, 76, -71, 102, -68, 78, -51, -100, 50};
        Assertions.assertArrayEquals(expected, hash);
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6446 fix differences in java.security)
    public void digestSHA256SUNTest() throws GeneralSecurityException, IOException {
        InputStream data = new ByteArrayInputStream(new byte[] {13, 16, 20, 0, 10});
        byte[] hash = DigestAlgorithms.digest(data, DigestAlgorithms.SHA256, "SUN");
        byte[] expected =
                new byte[] {19, -84, -84, -45, -36, 121, -15, -18, -89, 97, -17, 51, 81,
                        119, -42, -59, -31, 121, -87, -82, -45, 119, 61, 92, 110, -99, 105, 4, 97, 12, 127, -62};
        Assertions.assertArrayEquals(expected, hash);
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.ALGORITHM_NOT_FROM_SPEC, ignore = true)})
    @Test
    public void notAllowedNameGetAllowedDigestTest() {
        String name = "SM3";
        String oid = "1.2.156.10197.1.401";
        Assertions.assertEquals(oid, DigestAlgorithms.getAllowedDigest(name));
    }
}
