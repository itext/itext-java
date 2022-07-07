/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures.verify;

import com.itextpdf.bouncycastle.tsp.TimeStampTokenBC;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.SignaturesTestUtils;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CertificateVerificationClassTest extends ExtendedITextTest {

    // Such messageTemplate is equal to any log message. This is required for porting reasons.
    private static final String ANY_LOG_MESSAGE = "{0}";
    private static final int COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME = -1;

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] PASSWORD = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
        ITextTest.removeCryptographyRestrictions();
    }

    @AfterClass
    public static void after() {
        ITextTest.restoreCryptographyRestrictions();
    }

    @Test
    public void validCertificateChain01() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException {
        Certificate[] certChain = Pkcs12FileHelper.readFirstChain(CERTS_SRC + "signCertRsaWithChain.p12", PASSWORD);

        String caCertFileName = CERTS_SRC + "rootRsa.p12";
        KeyStore caKeyStore = Pkcs12FileHelper.initStore(caCertFileName, PASSWORD);

        List<VerificationException> verificationExceptions = CertificateVerification.verifyCertificates(certChain, caKeyStore);

        Assert.assertTrue(verificationExceptions.isEmpty());
    }

    @Test
    public void timestampCertificateAndKeyStoreCorrespondTest() throws Exception {
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.p12";

        KeyStore caKeyStore = Pkcs12FileHelper.initStore(tsaCertFileName, PASSWORD);

        Assert.assertTrue(verifyTimestampCertificates(tsaCertFileName, caKeyStore));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = "certificate hash does not match certID hash."))
    public void timestampCertificateAndKeyStoreDoNotCorrespondTest() throws Exception {
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.p12";
        String notTsaCertFileName = CERTS_SRC + "rootRsa.p12";

        KeyStore caKeyStore = Pkcs12FileHelper.initStore(notTsaCertFileName, PASSWORD);

        Assert.assertFalse(verifyTimestampCertificates(tsaCertFileName, caKeyStore));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = ANY_LOG_MESSAGE))
    public void keyStoreWithoutCertificatesTest() throws Exception {
        String tsaCertFileName = CERTS_SRC + "tsCertRsa.p12";

        Assert.assertFalse(verifyTimestampCertificates(tsaCertFileName, null));
    }

    @Test
    public void expiredCertificateTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {

        final X509Certificate expiredCert =
                (X509Certificate) Pkcs12FileHelper.readFirstChain(CERTS_SRC + "expiredCert.p12", PASSWORD)[0];

        final String verificationResult = CertificateVerification.verifyCertificate(expiredCert, null);
        final String expectedResultString = SignaturesTestUtils.getExpiredMessage(expiredCert);

        Assert.assertEquals(expectedResultString, verificationResult);
    }

    @Test
    public void unsupportedCriticalExtensionTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {

        final X509Certificate unsupportedExtensionCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(
                CERTS_SRC + "unsupportedCriticalExtensionCert.p12", PASSWORD)[0];

        final String verificationResult = CertificateVerification.verifyCertificate(unsupportedExtensionCert, null);

        Assert.assertEquals(CertificateVerification.HAS_UNSUPPORTED_EXTENSIONS, verificationResult);
    }

    @Test
    public void clrWithGivenCertificateTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            UnrecoverableKeyException, CRLException {

        final String caCertFileName = CERTS_SRC + "rootRsa.p12";
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, PASSWORD)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, PASSWORD, PASSWORD);

        final String checkCertFileName = CERTS_SRC + "signCertRsa01.p12";
        X509Certificate checkCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(checkCertFileName, PASSWORD)[0];

        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey,
                DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                        COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME));
        crlBuilder.addCrlEntry(caCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                        COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME),
                CRLReason.keyCompromise);

        TestCrlBuilder crlForCheckBuilder = new TestCrlBuilder(caCert, caPrivateKey,
                DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                        COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME));
        crlForCheckBuilder.addCrlEntry(checkCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                        COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME),
                CRLReason.keyCompromise);

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

        Assert.assertEquals(CertificateVerification.CERTIFICATE_REVOKED, verificationResult);
    }

    @Test
    public void validCertWithEmptyCrlCollectionTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        final String caCertFileName = CERTS_SRC + "rootRsa.p12";
        X509Certificate rootCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, PASSWORD)[0];

        final String verificationResult = CertificateVerification.verifyCertificate(rootCert, Collections.<CRL>emptyList());

        Assert.assertNull(verificationResult);
    }

    @Test
    public void validCertWithCrlDoesNotContainCertTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            UnrecoverableKeyException, CRLException {
        final int COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME = -1;
        final String rootCertFileName = CERTS_SRC + "rootRsa.p12";
        X509Certificate rootCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(rootCertFileName, PASSWORD)[0];

        final String certForAddingToCrlName = CERTS_SRC + "signCertRsa01.p12";
        X509Certificate certForCrl = (X509Certificate) Pkcs12FileHelper.readFirstChain(certForAddingToCrlName,
                PASSWORD)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(certForAddingToCrlName, PASSWORD, PASSWORD);

        TestCrlBuilder crlForCheckBuilder = new TestCrlBuilder(certForCrl, caPrivateKey,
                DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(),
                        COUNTER_TO_MAKE_CRL_AVAILABLE_AT_THE_CURRENT_TIME));

        TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(crlForCheckBuilder);

        Collection<byte[]> crlBytesForRootCertCollection = crlClient.getEncoded(certForCrl, null);

        final List<CRL> crls = new ArrayList<>();
        for (byte[] crlBytes : crlBytesForRootCertCollection) {
            crls.add(SignTestPortUtil.parseCrlFromStream(new ByteArrayInputStream(crlBytes)));
        }

        Assert.assertNull(CertificateVerification.verifyCertificate(rootCert, crls));
    }

    @Test
    public void emptyCertChainTest() {
        Certificate[] emptyCertChain = new Certificate[] {};
        final String expectedResult = MessageFormatUtil.format("Certificate Unknown failed: {0}",
                SignExceptionMessageConstant.INVALID_STATE_WHILE_CHECKING_CERT_CHAIN);

        List<VerificationException> resultedExceptionList = CertificateVerification.verifyCertificates(emptyCertChain,
                null, (Collection<CRL>) null);

        Assert.assertEquals(1, resultedExceptionList.size());
        Assert.assertEquals(expectedResult, resultedExceptionList.get(0).getMessage());
    }

    @Test
    public void validCertChainWithEmptyKeyStoreTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            NoSuchProviderException {
        final String validCertChainFileName = CERTS_SRC + "signCertRsaWithChain.p12";
        final String emptyCertChain = CERTS_SRC + "emptyCertChain.p12";

        Certificate[] validCertChain = Pkcs12FileHelper.readFirstChain(validCertChainFileName, PASSWORD);
        KeyStore emptyKeyStore = Pkcs12FileHelper.initStore(emptyCertChain, PASSWORD);

        List<VerificationException> resultedExceptionList = CertificateVerification.verifyCertificates(validCertChain,
                emptyKeyStore, (Collection<CRL>) null);

        final String expectedResult = MessageFormatUtil.format(
                SignExceptionMessageConstant.CERTIFICATE_TEMPLATE_FOR_EXCEPTION_MESSAGE,
                ((X509Certificate) validCertChain[2]).getSubjectDN().getName(),
                SignExceptionMessageConstant.CANNOT_BE_VERIFIED_CERTIFICATE_CHAIN);

        Assert.assertEquals(1, resultedExceptionList.size());
        Assert.assertEquals(expectedResult, resultedExceptionList.get(0).getMessage());
    }

    @Test
    public void validCertChainWithRootCertAsKeyStoreTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            NoSuchProviderException {
        final String validCertChainFileName = CERTS_SRC + "signCertRsaWithChain.p12";
        final String emptyCertChain = CERTS_SRC + "rootRsa.p12";

        Certificate[] validCertChain = Pkcs12FileHelper.readFirstChain(validCertChainFileName, PASSWORD);
        KeyStore emptyKeyStore = Pkcs12FileHelper.initStore(emptyCertChain, PASSWORD);

        List<VerificationException> resultedExceptionList = CertificateVerification.verifyCertificates(validCertChain,
                emptyKeyStore, (Collection<CRL>) null);

        Assert.assertEquals(0, resultedExceptionList.size());
    }

    @Test
    public void certChainWithExpiredCertTest()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        final String validCertChainFileName = CERTS_SRC + "signCertRsaWithExpiredChain.p12";

        Certificate[] validCertChain = Pkcs12FileHelper.readFirstChain(validCertChainFileName, PASSWORD);

        X509Certificate expectedExpiredCert = (X509Certificate) validCertChain[1];
        final String expiredCertName = expectedExpiredCert.getSubjectDN().getName();
        X509Certificate rootCert = (X509Certificate) validCertChain[2];
        final String rootCertName = rootCert.getSubjectDN().getName();

        List<VerificationException> resultedExceptionList = CertificateVerification.verifyCertificates(validCertChain,
                null, (Collection<CRL>) null);

        Assert.assertEquals(2, resultedExceptionList.size());
        final String expectedFirstResultMessage = MessageFormatUtil.format(
                SignExceptionMessageConstant.CERTIFICATE_TEMPLATE_FOR_EXCEPTION_MESSAGE,
                expiredCertName, SignaturesTestUtils.getExpiredMessage(expectedExpiredCert));
        final String expectedSecondResultMessage = MessageFormatUtil.format(
                SignExceptionMessageConstant.CERTIFICATE_TEMPLATE_FOR_EXCEPTION_MESSAGE,
                rootCertName, SignExceptionMessageConstant.CANNOT_BE_VERIFIED_CERTIFICATE_CHAIN);

        Assert.assertEquals(expectedFirstResultMessage, resultedExceptionList.get(0).getMessage());
        Assert.assertEquals(expectedSecondResultMessage, resultedExceptionList.get(1).getMessage());
    }

    private static boolean verifyTimestampCertificates(String tsaClientCertificate, KeyStore caKeyStore)
            throws Exception {
        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaClientCertificate, PASSWORD);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaClientCertificate, PASSWORD, PASSWORD);

        TestTsaClient testTsaClient = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        byte[] tsaCertificateBytes = testTsaClient.getTimeStampToken(testTsaClient.getMessageDigest().digest());
        TimeStampToken timeStampToken = new TimeStampToken(
                ContentInfo.getInstance(ASN1Sequence.getInstance(tsaCertificateBytes)));

        return CertificateVerification.verifyTimestampCertificates(new TimeStampTokenBC(timeStampToken), caKeyStore,
                null);
    }
}
