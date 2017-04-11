package com.itextpdf.signatures.verify;

import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.testutils.Pkcs12FileHelper;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.UnknownStatus;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class OcspVerifierTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void validOcspTest01() throws GeneralSecurityException, IOException, OCSPException {
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(certsSrc + "rootRsa.p12", password)[0];
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert);

        Assert.assertTrue(verifyTest(builder));
    }

    @Test
    public void invalidRevokedOcspTest01() throws GeneralSecurityException, IOException, OCSPException {
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(certsSrc + "rootRsa.p12", password)[0];
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert);

        builder.setCertificateStatus(new RevokedStatus(DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -20), CRLReason.keyCompromise));
        Assert.assertFalse(verifyTest(builder));
    }

    @Test
    public void invalidUnknownOcspTest01() throws GeneralSecurityException, IOException, OCSPException {
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(certsSrc + "rootRsa.p12", password)[0];
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert);

        builder.setCertificateStatus(new UnknownStatus());
        Assert.assertFalse(verifyTest(builder));
    }

    @Test
    public void invalidOutdatedOcspTest01() throws GeneralSecurityException, IOException, OCSPException {
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(certsSrc + "rootRsa.p12", password)[0];
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert);

        Calendar thisUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCurrentTimeCalendar(), -30);
        Calendar nextUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCurrentTimeCalendar(), -15);
        builder.setThisUpdate(thisUpdate);
        builder.setNextUpdate(nextUpdate);
        Assert.assertFalse(verifyTest(builder));
    }

    private boolean verifyTest(TestOcspResponseBuilder builder) throws IOException, GeneralSecurityException {
        String caCertFileName = certsSrc + "rootRsa.p12";
        String checkCertFileName = certsSrc + "signCertRsa01.p12";
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, password)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, password, password);
        X509Certificate checkCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(checkCertFileName, password)[0];


        TestOcspClient ocspClient = new TestOcspClient(builder, caPrivateKey);
        byte[] basicOcspRespBytes = ocspClient.getEncoded(checkCert, caCert, null);

        ASN1Primitive var2 = ASN1Primitive.fromByteArray(basicOcspRespBytes);
        BasicOCSPResp basicOCSPResp = new BasicOCSPResp(BasicOCSPResponse.getInstance(var2));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        return ocspVerifier.verify(basicOCSPResp, checkCert, caCert, DateTimeUtil.getCurrentTimeDate());
    }
}
