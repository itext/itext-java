package com.itextpdf.signatures;

import com.itextpdf.signatures.testutils.X509MockCertificate;
import com.itextpdf.test.annotations.type.UnitTest;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class CertificateSupportedCriticalExtensionsTest {

    @BeforeClass
    public static void beforeClass() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void supportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.KEY_USAGE, OID.X509Extensions.BASIC_CONSTRAINTS)
                .setKeyUsage(true, true);

        Assert.assertFalse(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void basicConstraintsSupportedTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.BASIC_CONSTRAINTS)
                .setKeyUsage(true, true);

        Assert.assertFalse(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void extendedKeyUsageWithIdKpTimestampingTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.EXTENDED_KEY_USAGE, OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setExtendedKeyUsage(OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setKeyUsage(true, true);

        Assert.assertTrue(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void extendedKeyUsageWithoutIdKpTimestampingTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.EXTENDED_KEY_USAGE)
                .setExtendedKeyUsage("Not ID KP TIMESTAMPING.")
                .setKeyUsage(true, true);

        Assert.assertTrue(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void idKpTimestampingWithoutExtendedKeyUsageTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs(OID.X509Extensions.ID_KP_TIMESTAMPING)
                .setKeyUsage(true, true);

        Assert.assertTrue(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void unsupportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(true)
                .setCriticalExtensionOIDs("Totally not supported OID")
                .setKeyUsage(true, true);

        Assert.assertTrue(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void noUnsupportedCriticalOIDsTest() {
        X509MockCertificate cert = new X509MockCertificate();

        cert.setHasUnsupportedCriticalExtension(false);

        Assert.assertFalse(SignUtils.hasUnsupportedCriticalExtension(cert));
    }

    @Test
    public void certificateIsNullTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage("X509Certificate can't be null.");

        SignUtils.hasUnsupportedCriticalExtension(null);
    }
}
