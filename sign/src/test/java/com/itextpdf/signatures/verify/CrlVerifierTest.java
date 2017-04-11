package com.itextpdf.signatures.verify;

import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.signatures.CRLVerifier;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.testutils.Pkcs12FileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class CrlVerifierTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpass".toCharArray();

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void validCrl01() throws GeneralSecurityException, IOException, OCSPException {
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(certsSrc + "rootRsa.p12", password)[0];
        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -1));
        Assert.assertTrue(verifyTest(crlBuilder));
    }

    @Test
    public void invalidRevokedCrl01() throws GeneralSecurityException, IOException, OCSPException {
        junitExpectedException.expect(VerificationException.class);

        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(certsSrc + "rootRsa.p12", password)[0];
        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -1));

        String checkCertFileName = certsSrc + "signCertRsa01.p12";
        X509Certificate checkCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(checkCertFileName, password)[0];
        crlBuilder.addCrlEntry(checkCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -40), CRLReason.keyCompromise);

        verifyTest(crlBuilder);
    }

    @Test
    public void invalidOutdatedCrl01() throws GeneralSecurityException, IOException, OCSPException {
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(certsSrc + "rootRsa.p12", password)[0];
        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -2));
        crlBuilder.setNextUpdate(DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -1));

        Assert.assertFalse(verifyTest(crlBuilder));
    }

    private boolean verifyTest(TestCrlBuilder crlBuilder) throws GeneralSecurityException, IOException {
        String caCertFileName = certsSrc + "rootRsa.p12";
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, password)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, password, password);
        String checkCertFileName = certsSrc + "signCertRsa01.p12";
        X509Certificate checkCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(checkCertFileName, password)[0];


        TestCrlClient crlClient = new TestCrlClient(crlBuilder, caPrivateKey);
        Collection<byte[]> crlBytesCollection = crlClient.getEncoded(checkCert, null);

        boolean verify = false;
        for (byte[] crlBytes : crlBytesCollection) {
            X509CRL crl = (X509CRL) SignTestPortUtil.parseCrlFromStream(new ByteArrayInputStream(crlBytes));
            CRLVerifier verifier = new CRLVerifier(null, null);
            verify = verifier.verify(crl, checkCert, caCert, DateTimeUtil.getCurrentTimeDate());
            break;
        }
        return verify;
    }
}
