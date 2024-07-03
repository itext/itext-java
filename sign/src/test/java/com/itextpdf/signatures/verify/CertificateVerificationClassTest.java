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
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.SignaturesTestUtils;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class CertificateVerificationClassTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final Provider PROVIDER = FACTORY.getProvider();

    // Such messageTemplate is equal to any log message. This is required for porting reasons.
    private static final String ANY_LOG_MESSAGE = "{0}";
    private static final int COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME = -1;

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(PROVIDER);
        ITextTest.removeCryptographyRestrictions();
    }

    @AfterAll
    public static void after() {
        ITextTest.restoreCryptographyRestrictions();
    }

    @Test
    public void validCertificateChain01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        Certificate[] certChain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsaWithChain.pem");

        String caCertFileName = CERTS_SRC + "rootRsa.pem";
        KeyStore caKeyStore = PemFileHelper.initStore(caCertFileName, PASSWORD, PROVIDER);

        List<VerificationException> verificationExceptions = CertificateVerification.verifyCertificates(certChain, caKeyStore);

        Assertions.assertTrue(verificationExceptions.isEmpty());
    }

    @Test
    public void timestampCertificateAndKeyStoreCorrespondTest() throws Exception {
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";

        KeyStore caKeyStore = PemFileHelper.initStore(tsaCertFileName, PASSWORD, PROVIDER);

        Assertions.assertTrue(verifyTimestampCertificates(tsaCertFileName, caKeyStore));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = "certificate hash does not match certID hash."))
    public void timestampCertificateAndKeyStoreDoNotCorrespondTest() throws Exception {
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";
        String notTsaCertFileName = CERTS_SRC + "rootRsa.pem";

        KeyStore caKeyStore = PemFileHelper.initStore(notTsaCertFileName, PASSWORD, PROVIDER);

        Assertions.assertFalse(verifyTimestampCertificates(tsaCertFileName, caKeyStore));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = ANY_LOG_MESSAGE))
    public void keyStoreWithoutCertificatesTest() throws Exception {
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";

        Assertions.assertFalse(verifyTimestampCertificates(tsaCertFileName, null));
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6447 fix different X509Certificate#checkValidity behavior)
    public void expiredCertificateTest() throws CertificateException, IOException {

        final X509Certificate expiredCert =
                (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "expiredCert.pem")[0];

        final String verificationResult = CertificateVerification.verifyCertificate(expiredCert, null);
        final String expectedResultString = SignaturesTestUtils.getExpiredMessage(expiredCert);

        Assertions.assertEquals(expectedResultString, verificationResult);
    }

    @Test
    public void unsupportedCriticalExtensionTest() throws CertificateException, IOException {

        final X509Certificate unsupportedExtensionCert = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "unsupportedCriticalExtensionCert.pem")[0];

        final String verificationResult = CertificateVerification.verifyCertificate(unsupportedExtensionCert, null);

        Assertions.assertEquals(CertificateVerification.HAS_UNSUPPORTED_EXTENSIONS, verificationResult);
    }

    @Test
    public void clrWithGivenCertificateTest()
            throws CertificateException, IOException, CRLException, AbstractPKCSException, AbstractOperatorCreationException {

        final String caCertFileName = CERTS_SRC + "rootRsa.pem";
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, PASSWORD);

        final String checkCertFileName = CERTS_SRC + "signCertRsa01.pem";
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];

        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey,
                DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                        COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME));
        crlBuilder.addCrlEntry(caCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME), FACTORY.createCRLReason().getKeyCompromise());

        TestCrlBuilder crlForCheckBuilder = new TestCrlBuilder(caCert, caPrivateKey,
                DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                        COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME));
        crlForCheckBuilder.addCrlEntry(checkCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME), FACTORY.createCRLReason().getKeyCompromise());

        TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(crlBuilder);
        TestCrlClient crlForCheckClient = new TestCrlClient().addBuilderForCertIssuer(crlForCheckBuilder);

        Collection<byte[]> crlBytesForRootCertCollection = crlClient.getEncoded(caCert, null);
        Collection<byte[]> crlBytesForCheckCertCollection = crlForCheckClient.getEncoded(checkCert, null);

        List<CRL> crls = new ArrayList<>();
        for (byte[] crlBytes : crlBytesForRootCertCollection) {
            crls.add(SignTestPortUtil.parseCrlFromStream(new ByteArrayInputStream(crlBytes)));
        }
        for (byte[] crlBytes : crlBytesForCheckCertCollection) {
            crls.add(SignTestPortUtil.parseCrlFromStream(new ByteArrayInputStream(crlBytes)));
        }

        final String verificationResult = CertificateVerification.verifyCertificate(checkCert, crls);

        Assertions.assertEquals(CertificateVerification.CERTIFICATE_REVOKED, verificationResult);
    }

    @Test
    public void validCertWithEmptyCrlCollectionTest() throws CertificateException, IOException {
        final String caCertFileName = CERTS_SRC + "rootRsa.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];

        final String verificationResult = CertificateVerification.verifyCertificate(rootCert, Collections.<CRL>emptyList());

        Assertions.assertNull(verificationResult);
    }

    @Test
    public void validCertWithCrlDoesNotContainCertTest()
            throws CertificateException, IOException, CRLException, AbstractPKCSException, AbstractOperatorCreationException {
        final int COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME = -1;
        final String rootCertFileName = CERTS_SRC + "rootRsa.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];

        final String certForAddingToCrlName = CERTS_SRC + "signCertRsa01.pem";
        X509Certificate certForCrl = (X509Certificate) PemFileHelper.readFirstChain(certForAddingToCrlName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(certForAddingToCrlName, PASSWORD);

        TestCrlBuilder crlForCheckBuilder = new TestCrlBuilder(certForCrl, caPrivateKey,
                DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                        COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME));

        TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(crlForCheckBuilder);

        Collection<byte[]> crlBytesForRootCertCollection = crlClient.getEncoded(certForCrl, null);

        final List<CRL> crls = new ArrayList<>();
        for (byte[] crlBytes : crlBytesForRootCertCollection) {
            crls.add(SignTestPortUtil.parseCrlFromStream(new ByteArrayInputStream(crlBytes)));
        }

        Assertions.assertNull(CertificateVerification.verifyCertificate(rootCert, crls));
    }

    @Test
    public void emptyCertChainTest() throws CertificateEncodingException {
        Certificate[] emptyCertChain = new Certificate[] {};
        final String expectedResult = MessageFormatUtil.format("Certificate Unknown failed: {0}",
                SignExceptionMessageConstant.INVALID_STATE_WHILE_CHECKING_CERT_CHAIN);

        List<VerificationException> resultedExceptionList = CertificateVerification.verifyCertificates(emptyCertChain,
                null, (Collection<CRL>) null);

        Assertions.assertEquals(1, resultedExceptionList.size());
        Assertions.assertEquals(expectedResult, resultedExceptionList.get(0).getMessage());
    }

    @Test
    public void validCertChainWithEmptyKeyStoreTest()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        final String validCertChainFileName = CERTS_SRC + "signCertRsaWithChain.pem";
        final String emptyCertChain = CERTS_SRC + "emptyCertChain.pem";

        Certificate[] validCertChain = PemFileHelper.readFirstChain(validCertChainFileName);
        KeyStore emptyKeyStore = PemFileHelper.initStore(emptyCertChain, PASSWORD, PROVIDER);

        List<VerificationException> resultedExceptionList = CertificateVerification.verifyCertificates(validCertChain,
                emptyKeyStore, (Collection<CRL>) null);

        final String expectedResult = MessageFormatUtil.format(
                SignExceptionMessageConstant.CERTIFICATE_TEMPLATE_FOR_EXCEPTION_MESSAGE,
                FACTORY.createX500Name((X509Certificate) validCertChain[2]).toString(),
                SignExceptionMessageConstant.CANNOT_BE_VERIFIED_CERTIFICATE_CHAIN);

        Assertions.assertEquals(1, resultedExceptionList.size());
        Assertions.assertEquals(expectedResult, resultedExceptionList.get(0).getMessage());
    }

    @Test
    public void validCertChainWithRootCertAsKeyStoreTest()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        final String validCertChainFileName = CERTS_SRC + "signCertRsaWithChain.pem";
        final String emptyCertChain = CERTS_SRC + "rootRsa.pem";

        Certificate[] validCertChain = PemFileHelper.readFirstChain(validCertChainFileName);
        KeyStore emptyKeyStore = PemFileHelper.initStore(emptyCertChain, PASSWORD, PROVIDER);

        List<VerificationException> resultedExceptionList = CertificateVerification.verifyCertificates(validCertChain,
                emptyKeyStore, (Collection<CRL>) null);

        Assertions.assertEquals(0, resultedExceptionList.size());
    }

    @Test
    // Android-Conversion-Ignore-Test (TODO DEVSIX-6447 fix different X509Certificate#checkValidity behavior)
    public void certChainWithExpiredCertTest()
            throws CertificateException, IOException {
        final String validCertChainFileName = CERTS_SRC + "signCertRsaWithExpiredChain.pem";

        Certificate[] validCertChain = PemFileHelper.readFirstChain(validCertChainFileName);

        X509Certificate expectedExpiredCert = (X509Certificate) validCertChain[1];
        final String expiredCertName = FACTORY.createX500Name(expectedExpiredCert).toString();
        X509Certificate rootCert = (X509Certificate) validCertChain[2];
        final String rootCertName = FACTORY.createX500Name(rootCert).toString();

        List<VerificationException> resultedExceptionList = CertificateVerification.verifyCertificates(validCertChain,
                null, (Collection<CRL>) null);

        Assertions.assertEquals(2, resultedExceptionList.size());
        final String expectedFirstResultMessage = MessageFormatUtil.format(
                SignExceptionMessageConstant.CERTIFICATE_TEMPLATE_FOR_EXCEPTION_MESSAGE,
                expiredCertName, SignaturesTestUtils.getExpiredMessage(expectedExpiredCert));
        final String expectedSecondResultMessage = MessageFormatUtil.format(
                SignExceptionMessageConstant.CERTIFICATE_TEMPLATE_FOR_EXCEPTION_MESSAGE,
                rootCertName, SignExceptionMessageConstant.CANNOT_BE_VERIFIED_CERTIFICATE_CHAIN);

        Assertions.assertEquals(expectedFirstResultMessage, resultedExceptionList.get(0).getMessage());
        Assertions.assertEquals(expectedSecondResultMessage, resultedExceptionList.get(1).getMessage());
    }

    private static boolean verifyTimestampCertificates(String tsaClientCertificate, KeyStore caKeyStore)
            throws Exception {
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaClientCertificate);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaClientCertificate, PASSWORD);

        TestTsaClient testTsaClient = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        byte[] tsaCertificateBytes = testTsaClient.getTimeStampToken(testTsaClient.getMessageDigest().digest());
        ITimeStampToken timeStampToken = FACTORY.createTimeStampToken(
                FACTORY.createContentInfo(FACTORY.createASN1Sequence(tsaCertificateBytes)));

        return CertificateVerification.verifyTimestampCertificates(timeStampToken, caKeyStore, null);
    }
}
