/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

// The behavior is different on .NET
@Category(BouncyCastleUnitTest.class)
public class PdfPKCS7ManuallyPortedTest extends PdfPKCS7BasicTest {

    @Test
    public void verifyEd25519SignatureTest()
            throws IOException, GeneralSecurityException {
        // ED25519 is not available in FIPS approved mode
        if (BOUNCY_CASTLE_FACTORY.isInApprovedOnlyMode()) {
            Assert.assertThrows(PdfException.class,
                    () -> verifyIsoExtensionExample("Ed25519", "sample-ed25519-sha512.pdf"));
        } else {
            verifyIsoExtensionExample("Ed25519", "sample-ed25519-sha512.pdf");
        }
    }

    @Test
    public void verifyNistECDSASha3SignatureTest() throws IOException, GeneralSecurityException {
        verifyIsoExtensionExample("SHA3-256withECDSA", "sample-nistp256-sha3_256.pdf");
    }

    @Test
    public void verifyBrainpoolSha3SignatureTest() throws IOException, GeneralSecurityException {
        verifyIsoExtensionExample("SHA3-384withECDSA", "sample-brainpoolP384r1-sha3_384.pdf");
    }

    @Test
    public void verifyRsaSha3SignatureTest() throws IOException, GeneralSecurityException {
        verifyIsoExtensionExample("SHA3-256withRSA", "sample-rsa-sha3_256.pdf");
    }
}
