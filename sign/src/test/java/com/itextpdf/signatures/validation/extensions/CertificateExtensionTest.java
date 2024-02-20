package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.signatures.OID;
import com.itextpdf.signatures.OID.X509Extensions;
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
public class CertificateExtensionTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/validation/extensions/CertificateExtensionTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @Test
    public void keyUsageNotSetExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, null);

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageNotSetNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(98).toASN1Primitive());

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageWrongOIDTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                X509Extensions.BASIC_CONSTRAINTS, FACTORY.createKeyUsage(98).toASN1Primitive());

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageExpectedValueTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(98).toASN1Primitive());

        Assert.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsagePartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(66).toASN1Primitive());

        // CertificateExtension#existsInCertificate only returns true in case of complete match, therefore false.
        Assert.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsagePartiallyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        Assert.assertFalse(extension.existsInCertificate(certificate));
    }
}
