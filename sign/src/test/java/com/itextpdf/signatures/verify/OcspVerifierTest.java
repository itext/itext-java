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
package com.itextpdf.signatures.verify;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.CertificateVerifier;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.cert.TestCertificateBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class OcspVerifierTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String src = "./src/test/resources/com/itextpdf/signatures/verify/OcspVerifierTest/";
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpassphrase".toCharArray();
    private static final String caCertFileName = certsSrc + "rootRsa.pem";

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void validOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);
        Calendar thisUpdate = DateTimeUtil.getCalendar(caCert.getNotBefore());
        Calendar nextUpdate = DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(caCert.getNotAfter(), 2));
        builder.setThisUpdate(thisUpdate);
        builder.setNextUpdate(nextUpdate);
        builder.setProducedAt(caCert.getNotBefore());
        Assertions.assertTrue(verifyTest(builder, certsSrc + "signCertRsa01.pem", caCert.getNotAfter()));
    }

    @Test
    public void validOcspWithoutOcspResponseBuilderTest() throws IOException, GeneralSecurityException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem")[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        Assertions.assertTrue(ocspVerifier.verify(caCert, rootCert, checkDate).isEmpty());
    }

    @Test
    public void verifyOcspWhenCertificateWasRevokedBeforeSignDateTest()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        builder.setCertificateStatus(FACTORY.createRevokedStatus(
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -20),
                FACTORY.createCRLReason().getKeyCompromise()));
        Assertions.assertFalse(verifyTest(builder));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED))
    public void verifyOcspWhenCertificateWasRevokedAfterSignDateTest()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        String rootCertFileName = src + "rootCert.pem";
        String checkCertFileName = src + "signCert.pem";
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        builder.setCertificateStatus(FACTORY.createRevokedStatus(
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 20),
                FACTORY.createCRLReason().getKeyCompromise()));
        Assertions.assertTrue(verifyTest(builder, checkCertFileName, TimeTestUtil.TEST_DATE_TIME));
    }

    @Test
    public void invalidUnknownOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        builder.setCertificateStatus(FACTORY.createUnknownStatus());
        Assertions.assertFalse(verifyTest(builder));
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
        Assertions.assertFalse(verifyTest(builder));
    }

    @Test
    public void validOcspCreatedAfterSignDateTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        String rootCertFileName = src + "rootCert.pem";
        String checkCertFileName = src + "signCert.pem";
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        Calendar thisUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCalendar(TimeTestUtil.TEST_DATE_TIME), 15);
        Calendar nextUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCalendar(TimeTestUtil.TEST_DATE_TIME), 30);
        builder.setThisUpdate(thisUpdate);
        builder.setNextUpdate(nextUpdate);
        Assertions.assertTrue(verifyTest(builder, checkCertFileName, TimeTestUtil.TEST_DATE_TIME));
    }

    @Test
    public void expiredIssuerCertTest01_atValidPeriod()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "intermediateExpiredCert.pem")[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(certsSrc + "intermediateExpiredCert.pem", password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);
        Calendar thisUpdate = DateTimeUtil.getCalendar(caCert.getNotBefore());
        Calendar nextUpdate = DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(caCert.getNotAfter(), 2));
        builder.setThisUpdate(thisUpdate);
        builder.setNextUpdate(nextUpdate);
        builder.setProducedAt(caCert.getNotBefore());
        Assertions.assertTrue(verifyTest(builder, certsSrc + "signCertRsaWithExpiredChain.pem",
                DateTimeUtil.addDaysToDate(caCert.getNotAfter(), -1)));
    }

    @Test
    public void expiredIssuerCertTest01_afterValidPeriod()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "intermediateExpiredCert.pem")[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(certsSrc + "intermediateExpiredCert.pem", password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);
        Calendar thisUpdate = DateTimeUtil.getCalendar(caCert.getNotBefore());
        Calendar nextUpdate = DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(caCert.getNotAfter(), 2));
        builder.setThisUpdate(thisUpdate);
        builder.setNextUpdate(nextUpdate);
        builder.setProducedAt(DateTimeUtil.addDaysToDate(caCert.getNotAfter(), 1));
        Assertions.assertThrows(CertificateExpiredException.class, () ->
                verifyTest(builder, certsSrc + "signCertRsaWithExpiredChain.pem",
                        DateTimeUtil.addDaysToDate(caCert.getNotAfter(), 1)));
    }

    @Test
    public void authorizedOCSPResponderTest() throws GeneralSecurityException, IOException,
            AbstractPKCSException, AbstractOperatorCreationException {
        Date ocspResponderCertStartDate = TimeTestUtil.TEST_DATE_TIME;
        Date ocspResponderCertEndDate = DateTimeUtil.addDaysToDate(ocspResponderCertStartDate, 365 * 100);
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        boolean verifyRes = verifyAuthorizedOCSPResponderWithOCSPNoCheckTest(ocspResponderCertStartDate,
                ocspResponderCertEndDate, checkDate);
        Assertions.assertTrue(verifyRes);
    }

    @Test
    public void expiredAuthorizedOCSPResponderTest_atValidPeriod() throws GeneralSecurityException, IOException,
            AbstractPKCSException, AbstractOperatorCreationException {
        Date ocspResponderCertStartDate = DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, -4);
        Date ocspResponderCertEndDate = DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, 1);
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        boolean verifyRes = verifyAuthorizedOCSPResponderWithOCSPNoCheckTest(ocspResponderCertStartDate,
                ocspResponderCertEndDate, checkDate);
        Assertions.assertTrue(verifyRes);
    }

    @Test
    public void expiredAuthorizedOCSPResponderTest_afterValidPeriod() {
        Date ocspResponderCertStartDate = DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, -5);
        Date ocspResponderCertEndDate = DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, -1);
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        Assertions.assertThrows(CertificateExpiredException.class, () -> verifyAuthorizedOCSPResponderWithOCSPNoCheckTest(
                ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate)
        );
    }

    @Test
    public void expiredResponderFromRootStoreTestAtValidPeriod() throws GeneralSecurityException, IOException,
            AbstractPKCSException, AbstractOperatorCreationException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        boolean verifyRes = verifyOcspResponseWithResponderFromRootStoreTest(checkDate);
        Assertions.assertTrue(verifyRes);
    }

    @Test
    public void expiredResponderFromRootStoreTestAfterValidPeriod() {
        Date checkDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 365 * 100);
        Assertions.assertThrows(CertificateExpiredException.class, () -> verifyOcspResponseWithResponderFromRootStoreTest(
                checkDate)
        );
    }

    @Test
    public void authorizedOCSPResponderWithOcspTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        String rootCertFileName = src + "rootCertForOcspTest.pem";
        String checkCertFileName = src + "signCertForOcspTest.pem";
        String ocspResponderCertFileName = src + "ocspResponderCertForOcspTest.pem";
        boolean verifyRes = verifyOcspResponseWithRevocationCheckTest(rootCertFileName, checkCertFileName,
                ocspResponderCertFileName, true, false);
        Assertions.assertTrue(verifyRes);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED))
    public void authorizedOCSPResponderWithOcspRevokedStatusTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        String rootCertFileName = src + "rootCertForOcspTest.pem";
        String checkCertFileName = src + "signCertForOcspTest.pem";
        String ocspResponderCertFileName = src + "ocspResponderCertForOcspTest.pem";
        boolean verifyRes = verifyOcspResponseWithRevocationCheckTest(rootCertFileName, checkCertFileName,
                ocspResponderCertFileName, true, true);
        Assertions.assertTrue(verifyRes);
    }

    @Test
    public void authorizedOCSPResponderWithCrlTest() throws GeneralSecurityException, IOException,
            AbstractPKCSException, AbstractOperatorCreationException {
        String rootCertFileName = src + "rootCertForCrlTest.pem";
        String checkCertFileName = src + "signCertForCrlTest.pem";
        String ocspResponderCertFileName = src + "ocspResponderCertForCrlTest.pem";
        boolean verifyRes = verifyOcspResponseWithRevocationCheckTest(rootCertFileName, checkCertFileName,
                ocspResponderCertFileName, false, false);
        Assertions.assertTrue(verifyRes);
    }

    @Test
    public void authorizedOCSPResponderWithoutRevocationDataTest() {
        Date ocspResponderCertStartDate = TimeTestUtil.TEST_DATE_TIME;
        Date ocspResponderCertEndDate = DateTimeUtil.addDaysToDate(ocspResponderCertStartDate, 365 * 100);
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        Assertions.assertThrows(VerificationException.class, () -> verifyAuthorizedOCSPResponderCheckRevDataTest
                (ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate));
    }

    @Test
    public void authorizedOCSPResponderSetResponderOcspsTest() throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        Date ocspResponderCertStartDate = TimeTestUtil.TEST_DATE_TIME;
        Date ocspResponderCertEndDate = DateTimeUtil.addDaysToDate(ocspResponderCertStartDate, 365 * 100);
        Date checkDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 365 * 20);

        Assertions.assertTrue(verifyAuthorizedOCSPResponderWithProvidedOcspsTest(ocspResponderCertStartDate,
                ocspResponderCertEndDate, checkDate));
    }

    @Test
    public void authorizedOCSPResponderSetResponderCrlsTest() throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        Date ocspResponderCertStartDate = TimeTestUtil.TEST_DATE_TIME;
        Date ocspResponderCertEndDate = DateTimeUtil.addDaysToDate(ocspResponderCertStartDate, 365 * 100);
        Date checkDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 365 * 20);

        Assertions.assertTrue(verifyAuthorizedOCSPResponderWithProvidedCrlsTest(ocspResponderCertStartDate,
                ocspResponderCertEndDate, checkDate));
    }

    @Test
    public void ocspResponseCouldNotBeVerifiedTest() throws CertificateException, IOException {
        X509Certificate wrongCert = (X509Certificate)
                PemFileHelper.readFirstChain(certsSrc + "intermediateExpiredCert.pem")[0];
        Assertions.assertThrows(VerificationException.class, () ->
                verifyOcspResponseWithoutResponderAvailableTest(new IX509CertificateHolder[]
                        {FACTORY.createJcaX509CertificateHolder(wrongCert)}));
    }

    @Test
    public void ocspResponseWithoutCertsCouldNotBeVerifiedTest() {
        Assertions.assertThrows(VerificationException.class, () ->
                verifyOcspResponseWithoutResponderAvailableTest(new IX509CertificateHolder[0]));
    }

    @Test
    public void getOcspResponseNullTest() {
        OCSPVerifier verifier = new OCSPVerifier(null, null);
        Assertions.assertNull(verifier.getOcspResponse(null, null));
    }

    @Test
    public void certificateDoesNotVerifyWithSuppliedKeyTest() {
        String rootCertFileName = src + "rootCertForCrlTest.pem";
        String checkCertFileName = src + "signCertForCrlTest.pem";
        String ocspResponderCertFileName = src + "ocspResponderCertForOcspTest.pem";
        Assertions.assertThrows(SignatureException.class, () ->
                verifyOcspResponseWithRevocationCheckTest(rootCertFileName, checkCertFileName,
                        ocspResponderCertFileName, true, false));
    }

    @Test
    public void issuersDoesNotMatchTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        String rootCertFileName = src + "rootCert.pem";
        String wrongRootCertFileName = src + "rootCertForOcspTest.pem";
        String checkCertFileName = src + "signCert.pem";
        String ocspResponderCertFileName = src + "ocspResponderCert.pem";
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        X509Certificate wrongCaCert = (X509Certificate) PemFileHelper.readFirstChain(wrongRootCertFileName)[0];

        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, password);

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        Assertions.assertFalse(ocspVerifier.verify(basicOCSPResp, checkCert, wrongCaCert, checkDate));
    }

    @Test
    public void checkBothOnlineAndProvidedOcspsTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String rootCertFileName = src + "rootCert.pem";
        String checkCertFileName = src + "signCert.pem";
        String ocspResponderCertFileName = src + "ocspResponderCert.pem";
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, password);

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, -5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        builder.setOcspCertsChain(new IX509CertificateHolder[0]);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        byte[] basicOcspRespBytes = ocspClient.getEncoded(checkCert, caCert, null);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(
                FACTORY.createBasicOCSPResponse(FACTORY.createASN1Primitive(basicOcspRespBytes)));

        OCSPVerifier ocspVerifier = new CustomOCSPVerifier(null, Collections.singletonList(basicOCSPResp));
        ocspVerifier.setRootStore(PemFileHelper.initStore(ocspResponderCertFileName, password, FACTORY.getProvider()));

        Assertions.assertTrue(ocspVerifier.verify(checkCert, caCert, checkDate).get(0).toString()
                .contains("Valid OCSPs Found: 2 (1 online)"));
    }

    @Test
    public void authorizedOCSPResponderCreatedAfterSignDateTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        String rootCertFileName = src + "rootCert2.pem";
        String checkCertFileName = src + "signCert2.pem";
        String ocspResponderCertFileName = src + "responderCreatedIn2001.pem";
        Date checkDate = TimeTestUtil.TEST_DATE_TIME; // Feb 14, 2000 14:14:02 UTC

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, password);

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, 365));
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 365)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 370)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        Assertions.assertTrue(ocspVerifier.verify(basicOCSPResp, checkCert, caCert, checkDate));
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

    private boolean verifyAuthorizedOCSPResponderWithOCSPNoCheckTest(Date ocspResponderStartDate, Date ocspResponderEndDate,
                                                                     Date checkDate) throws AbstractOperatorCreationException,
            GeneralSecurityException, IOException, AbstractPKCSException {
        return verifyAuthorizedOCSPResponderTest(ocspResponderStartDate, ocspResponderEndDate, checkDate,
                false, false, false);
    }

    private void verifyAuthorizedOCSPResponderCheckRevDataTest(Date ocspResponderStartDate, Date ocspResponderEndDate,
                                                               Date checkDate)
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        verifyAuthorizedOCSPResponderTest(ocspResponderStartDate, ocspResponderEndDate, checkDate,
                true, false, false);
    }

    private boolean verifyAuthorizedOCSPResponderWithProvidedOcspsTest(Date ocspResponderCertStartDate,
                                                                       Date ocspResponderCertEndDate, Date checkDate)
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        return verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate,
                true, true, false);
    }

    private boolean verifyAuthorizedOCSPResponderWithProvidedCrlsTest(Date ocspResponderCertStartDate,
                                                                      Date ocspResponderCertEndDate, Date checkDate)
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        return verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate,
                true, false, true);
    }

    private void verifyOcspResponseWithoutResponderAvailableTest(IX509CertificateHolder[] ocspCertsChain)
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        Date ocspResponderCertStartDate = TimeTestUtil.TEST_DATE_TIME;
        Date ocspResponderCertEndDate = DateTimeUtil.addDaysToDate(ocspResponderCertStartDate, 365 * 100);
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate, false,
                false, false, false, ocspCertsChain);
    }

    private boolean verifyAuthorizedOCSPResponderTest(Date ocspResponderCertStartDate, Date ocspResponderCertEndDate,
                                                      Date checkDate, boolean checkResponderRevData,
                                                      boolean setResponderOcsps, boolean setResponderCrls)
            throws IOException, AbstractOperatorCreationException, GeneralSecurityException, AbstractPKCSException {
        return verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate, true,
                checkResponderRevData, setResponderOcsps, setResponderCrls, null);
    }

    private boolean verifyAuthorizedOCSPResponderTest(Date ocspResponderCertStartDate, Date ocspResponderCertEndDate,
                                                      Date checkDate, boolean addResponder,
                                                      boolean checkResponderRevData,
                                                      boolean setResponderOcsps, boolean setResponderCrls,
                                                      IX509CertificateHolder[] ocspCertsChain)
            throws IOException, AbstractOperatorCreationException, GeneralSecurityException, AbstractPKCSException {
        String rootCertFileName = src + "rootCert.pem";
        String checkCertFileName = src + "signCert.pem";
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, password);
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];

        KeyPairGenerator keyGen = SignTestPortUtil.buildRSA2048KeyPairGenerator();
        KeyPair key = keyGen.generateKeyPair();
        PrivateKey ocspRespPrivateKey = key.getPrivate();
        PublicKey ocspRespPublicKey = key.getPublic();
        TestCertificateBuilder certBuilder = new TestCertificateBuilder(ocspRespPublicKey, caCert, caPrivateKey, "CN=iTextTestOCSPResponder, OU=test, O=iText");
        certBuilder.setStartDate(ocspResponderCertStartDate);
        certBuilder.setEndDate(ocspResponderCertEndDate);
        X509Certificate ocspResponderCert = certBuilder.buildAuthorizedOCSPResponderCert(checkResponderRevData);

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(ocspResponderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, -5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        if (!addResponder) {
            builder.setOcspCertsChain(ocspCertsChain);
        }
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        byte[] basicOcspRespBytes = ocspClient.getEncoded(checkCert, caCert, null);

        IASN1Primitive var2 = FACTORY.createASN1Primitive(basicOcspRespBytes);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(var2));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);

        if (setResponderOcsps) {
            TestOcspResponseBuilder builder2 = new TestOcspResponseBuilder(caCert, caPrivateKey);
            builder2.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, -5)));
            builder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
            TestOcspClient ocspClient2 = new TestOcspClient().addBuilderForCertIssuer(caCert, builder2);
            ocspVerifier.setOcspClient(ocspClient2);
        }

        if (setResponderCrls) {
            TestCrlBuilder testCrlBuilder = new TestCrlBuilder(caCert, caPrivateKey,
                    DateTimeUtil.addDaysToDate(checkDate, -5));
            testCrlBuilder.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, 5));
            TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(testCrlBuilder);
            ocspVerifier.setCrlClient(crlClient);
        }

        return ocspVerifier.verify(basicOCSPResp, checkCert, caCert, checkDate);
    }

    private boolean verifyOcspResponseWithResponderFromRootStoreTest(Date checkDate)
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        String rootCertFileName = src + "rootCert.pem";
        String checkCertFileName = src + "signCert.pem";
        String ocspResponderCertFileName = src + "ocspResponderCert.pem";

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];

        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, password);

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setProducedAt(DateTimeUtil.addDaysToDate(checkDate, -5));
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, -5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        builder.setOcspCertsChain(new IX509CertificateHolder[0]);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        byte[] basicOcspRespBytes = ocspClient.getEncoded(checkCert, caCert, null);

        IASN1Primitive var2 = FACTORY.createASN1Primitive(basicOcspRespBytes);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(var2));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        ocspVerifier.setRootStore(PemFileHelper.initStore(ocspResponderCertFileName, password, FACTORY.getProvider()));
        return ocspVerifier.verify(basicOCSPResp, checkCert, caCert, checkDate);
    }

    private boolean verifyOcspResponseWithRevocationCheckTest(String rootCertFileName, String checkCertFileName,
                                                              String ocspResponderCertFileName, boolean checkOcsp,
                                                              boolean revokedOcsp)
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, password);
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];

        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, password);

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, -5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        if (checkOcsp) {
            TestOcspResponseBuilder builder2 = revokedOcsp ? new TestOcspResponseBuilder(caCert, caPrivateKey,
                    FACTORY.createRevokedStatus(
                            DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 20),
                            FACTORY.createCRLReason().getKeyCompromise())) :
                    new TestOcspResponseBuilder(caCert, caPrivateKey);
            builder2.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 20)));
            builder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 30)));
            TestOcspClient ocspClient2 = new TestOcspClient().addBuilderForCertIssuer(caCert, builder2);
            ocspVerifier.setOcspClient(ocspClient2);
        } else {
            TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey,
                    DateTimeUtil.addDaysToDate(checkDate, -1));
            crlBuilder.setNextUpdate(DateTimeUtil.addDaysToDate(checkDate, 1));
            TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(crlBuilder);
            ocspVerifier.setCrlClient(crlClient);
        }
        return ocspVerifier.verify(basicOCSPResp, checkCert, caCert, checkDate);
    }

    private static class CustomOCSPVerifier extends OCSPVerifier {
        /**
         * Creates an OCSPVerifier instance.
         *
         * @param verifier the next verifier in the chain
         * @param ocsps    a list of {@link IBasicOCSPResp} OCSP response wrappers for the certificate verification
         */
        public CustomOCSPVerifier(CertificateVerifier verifier, List<IBasicOCSPResp> ocsps) {
            super(verifier, ocsps);
        }

        @Override
        public IBasicOCSPResp getOcspResponse(X509Certificate signCert, X509Certificate issuerCert) {
            String rootCertFileName = src + "rootCert.pem";
            String checkCertFileName = src + "signCert.pem";
            String ocspResponderCertFileName = src + "ocspResponderCert.pem";
            Date checkDate = TimeTestUtil.TEST_DATE_TIME;
            try {
                X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
                PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, password);
                X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
                X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
                TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, caPrivateKey);
                builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
                builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 15)));
                TestOcspClient ocspClient2 = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
                byte[] basicOcspRespBytes = ocspClient2.getEncoded(checkCert, caCert, null);
                return FACTORY.createBasicOCSPResp(
                        FACTORY.createBasicOCSPResponse(FACTORY.createASN1Primitive(basicOcspRespBytes)));
            } catch (Exception ignored) {
            }
            return null;
        }
    }
}
