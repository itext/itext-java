package com.itextpdf.signatures;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.security.cert.X509Certificate;

@Category(UnitTest.class)
public class CertificateUtilTest extends ExtendedITextTest {

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] PASSWORD = "testpass".toCharArray();

    @Test
    public void getTSAURLAdobeExtensionTest() throws Exception {
        X509Certificate tsaCert =
                (X509Certificate) Pkcs12FileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCert.p12", PASSWORD)[0];
        String url = CertificateUtil.getTSAURL(tsaCert);

        Assert.assertEquals("https://itextpdf.com/en", url);
    }

    @Test
    public void getTSAURLUsualTimestampCertificateTest() throws Exception {
        X509Certificate tsaCert =
                (X509Certificate) Pkcs12FileHelper.readFirstChain(CERTS_SRC + "tsCertRsa.p12", PASSWORD)[0];
        String url = CertificateUtil.getTSAURL(tsaCert);

        Assert.assertNull(url);
    }

    @Test
    public void getTSAURLAdobeExtensionNotTaggedTest() throws Exception {
        X509Certificate tsaCert = (X509Certificate)
                Pkcs12FileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCertWithoutTag.p12", PASSWORD)[0];

        Assert.assertThrows(ClassCastException.class, () -> CertificateUtil.getTSAURL(tsaCert));
    }
}
