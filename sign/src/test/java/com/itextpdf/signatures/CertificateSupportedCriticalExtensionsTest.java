/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.signatures.testutils.X509MockCertificate;
import com.itextpdf.test.ExtendedITextTest;

import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class CertificateSupportedCriticalExtensionsTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    @BeforeAll
    public static void beforeClass() {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
    }

    @Test
    public void supportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.KEY_USAGE, OID.X509Extensions.BASIC_CONSTRAINTS)
                .setKeyUsage(true, true);

        Assertions.assertFalse(CertificateVerification.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void basicConstraintsSupportedTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.BASIC_CONSTRAINTS)
                .setKeyUsage(true, true);

        Assertions.assertFalse(CertificateVerification.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void extendedKeyUsageWithIdKpTimestampingTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.EXTENDED_KEY_USAGE, OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setExtendedKeyUsage(OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setKeyUsage(true, true);

        Assertions.assertTrue(CertificateVerification.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void extendedKeyUsageWithoutIdKpTimestampingTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.EXTENDED_KEY_USAGE)
                .setExtendedKeyUsage("Not ID KP TIMESTAMPING.")
                .setKeyUsage(true, true);

        Assertions.assertFalse(CertificateVerification.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void idKpTimestampingWithoutExtendedKeyUsageTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setKeyUsage(true, true);

        Assertions.assertTrue(CertificateVerification.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void unsupportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs("Totally not supported OID")
                .setKeyUsage(true, true);

        Assertions.assertTrue(CertificateVerification.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void noUnsupportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(false);

        Assertions.assertFalse(CertificateVerification.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void certificateIsNullTest() {
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> CertificateVerification.hasUnsupportedCriticalExtension(null)
        );
        Assertions.assertEquals("X509Certificate can't be null.", e.getMessage());
    }
}
