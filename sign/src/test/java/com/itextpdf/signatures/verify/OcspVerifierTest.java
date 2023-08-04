/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.signatures.verify;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.cert.TestCertificateBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class OcspVerifierTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpassphrase".toCharArray();
    private static final String caCertFileName = certsSrc + "rootRsa.pem";

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void validOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        Assert.assertTrue(verifyTest(builder));
    }
    
    @Test
    public void validOcspWithoutOcspResponseBuilderTest() throws IOException, GeneralSecurityException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem")[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        Assert.assertTrue(ocspVerifier.verify(caCert, rootCert, checkDate).isEmpty());
    }

    @Test
    public void invalidRevokedOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        builder.setCertificateStatus(FACTORY.createRevokedStatus(
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -20),
                FACTORY.createCRLReason().getKeyCompromise()));
        Assert.assertFalse(verifyTest(builder));
    }

    @Test
    public void invalidUnknownOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        builder.setCertificateStatus(FACTORY.createUnknownStatus());
        Assert.assertFalse(verifyTest(builder));
    }

    @Test
    public void invalidOutdatedOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        Calendar thisUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCalendar(TimeTestUtil.TEST_DATE_TIME), -30);
        Calendar nextUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCalendar(TimeTestUtil.TEST_DATE_TIME), -15);
        builder.setThisUpdate(thisUpdate);
        builder.setNextUpdate(nextUpdate);
        Assert.assertFalse(verifyTest(builder));
    }

    @Test
    public void expiredIssuerCertTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "intermediateExpiredCert.pem")[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(certsSrc + "intermediateExpiredCert.pem", password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);
        Calendar thisUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCurrentTimeCalendar(), 30);
        Calendar nextUpdate = DateTimeUtil.getCurrentTimeCalendar();
        builder.setThisUpdate(thisUpdate);
        builder.setNextUpdate(nextUpdate);
        Assert.assertTrue(verifyTest(builder, certsSrc + "signCertRsaWithExpiredChain.pem", caCert.getNotBefore()));
    }

    @Test
    public void authorizedOCSPResponderTest() throws GeneralSecurityException, IOException,
            AbstractPKCSException, AbstractOperatorCreationException {
        Date ocspResponderCertStartDate = TimeTestUtil.TEST_DATE_TIME;
        Date ocspResponderCertEndDate = DateTimeUtil.addDaysToDate(ocspResponderCertStartDate, 365 * 100);
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        boolean verifyRes = verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate);
        Assert.assertTrue(verifyRes);
    }

    @Test
    public void expiredAuthorizedOCSPResponderTest_atValidPeriod() throws GeneralSecurityException, IOException,
            AbstractPKCSException, AbstractOperatorCreationException {
        Date ocspResponderCertStartDate = DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, -4);
        Date ocspResponderCertEndDate = DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, 1);
        Date checkDate =TimeTestUtil.TEST_DATE_TIME;

        boolean verifyRes = verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate);
        Assert.assertTrue(verifyRes);
    }

    @Test
    public void expiredAuthorizedOCSPResponderTest_now() {
        Date ocspResponderCertStartDate = DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, -5);
        Date ocspResponderCertEndDate = DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, -1);
        Date checkDate =TimeTestUtil.TEST_DATE_TIME;

        Assert.assertThrows(CertificateExpiredException.class,
                () -> verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate)
        );
        // Not getting here because of exception
        //Assert.assertFalse(verifyRes);
    }
    
    @Test
    public void getOcspResponseNullTest() {
        OCSPVerifier verifier = new OCSPVerifier(null, null);
        Assert.assertNull(verifier.getOcspResponse(null, null));
    }

    private boolean verifyTest(TestOcspResponseBuilder rootRsaOcspBuilder) throws IOException, GeneralSecurityException {
        return verifyTest(rootRsaOcspBuilder, certsSrc + "signCertRsa01.pem", TimeTestUtil.TEST_DATE_TIME);
    }

    private boolean verifyTest(TestOcspResponseBuilder rootRsaOcspBuilder, String checkCertFileName, Date checkDate) throws IOException, GeneralSecurityException {
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];

        X509Certificate rootCert = rootRsaOcspBuilder.getIssuerCert();
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, rootRsaOcspBuilder);
        byte[] basicOcspRespBytes = ocspClient.getEncoded(checkCert, rootCert, null);

        IASN1Primitive var2 = FACTORY.createASN1Primitive(basicOcspRespBytes);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(var2));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        return ocspVerifier.verify(basicOCSPResp, checkCert, rootCert, checkDate);
    }

    public boolean verifyAuthorizedOCSPResponderTest(Date ocspResponderCertStartDate, Date ocspResponderCertEndDate,
            Date checkDate)
            throws IOException, AbstractOperatorCreationException, GeneralSecurityException, AbstractPKCSException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "intermediateRsa.pem")[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(certsSrc + "intermediateRsa.pem", password);
        String checkCertFileName = certsSrc + "signCertRsaWithChain.pem";
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];

        KeyPairGenerator keyGen = SignTestPortUtil.buildRSA2048KeyPairGenerator();
        KeyPair key = keyGen.generateKeyPair();
        PrivateKey ocspRespPrivateKey = key.getPrivate();
        PublicKey ocspRespPublicKey = key.getPublic();
        TestCertificateBuilder certBuilder = new TestCertificateBuilder(ocspRespPublicKey, caCert, caPrivateKey, "CN=iTextTestOCSPResponder, OU=test, O=iText");
        certBuilder.setStartDate(ocspResponderCertStartDate);
        certBuilder.setEndDate(ocspResponderCertEndDate);
        X509Certificate ocspResponderCert = certBuilder.buildAuthorizedOCSPResponderCert();

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(ocspResponderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        byte[] basicOcspRespBytes = ocspClient.getEncoded(checkCert, caCert, null);

        IASN1Primitive var2 = FACTORY.createASN1Primitive(basicOcspRespBytes);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(var2));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        return ocspVerifier.verify(basicOCSPResp, checkCert, caCert, checkDate);
    }
}
