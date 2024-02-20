package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class KeyUsageExtensionTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/validation/extensions/KeyUsageExtensionTest/";

    @Test
    public void keyUsageNotSetExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(0);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageNotSetNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(8);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageKeyCertSignExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageKeyCertSignCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.KEY_CERT_SIGN);

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageKeyCertSignPartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageKeyCertSignCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN));

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageKeyCertSignNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageKeyCertSignCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.CRL_SIGN);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageDigitalSignatureTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageDigitalSignatureCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.DIGITAL_SIGNATURE);

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageDecipherOnlyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageDecipherOnlyCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.DECIPHER_ONLY);

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageDecipherOnlyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageDecipherOnlyCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.ENCIPHER_ONLY);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys1PartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.CRL_SIGN, KeyUsage.NON_REPUDIATION));

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys1ExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.CRL_SIGN,
                KeyUsage.NON_REPUDIATION, KeyUsage.KEY_ENCIPHERMENT));

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys1PartiallyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.CRL_SIGN, KeyUsage.DECIPHER_ONLY,
                KeyUsage.KEY_ENCIPHERMENT));

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys2PartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys2Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.DECIPHER_ONLY, KeyUsage.DIGITAL_SIGNATURE));

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys2ExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys2Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.DECIPHER_ONLY,
                KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_AGREEMENT));

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys2PartiallyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys2Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.CRL_SIGN, KeyUsage.DECIPHER_ONLY,
                KeyUsage.DIGITAL_SIGNATURE));

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }
}
