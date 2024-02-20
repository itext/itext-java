package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class BasicConstraintsExtensionTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/validation/extensions/BasicConstraintsExtensionTest/";

    @Test
    public void basicConstraintNotSetExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(-2);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintNotSetNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(10);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintMaxLengthExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsMaxCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(true);

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintMaxLengthNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsMaxCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(false);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintLength10Test() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraints10Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(10);

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintLength5ExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraints5Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(2);

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintLength5NotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraints5Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(10);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintFalseExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsFalseCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(false);

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintFalseNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsFalseCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(10);

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }
}
