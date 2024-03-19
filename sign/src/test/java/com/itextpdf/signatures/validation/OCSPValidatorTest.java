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
package com.itextpdf.signatures.validation;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OID;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;

@Category(BouncyCastleUnitTest.class)
public class OCSPValidatorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/OCSPValidatorTest/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final char[] PASSWORD = "testpassphrase".toCharArray();
    private static final long MILLISECONDS_PER_DAY = 86400000L;

    private static X509Certificate caCert;
    private static PrivateKey caPrivateKey;
    private static X509Certificate checkCert;
    private static X509Certificate responderCert;
    private static PrivateKey ocspRespPrivateKey;

    @BeforeClass
    public static void before()
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        Security.addProvider(FACTORY.getProvider());

        String rootCertFileName = SOURCE_FOLDER + "rootCert.pem";
        String checkCertFileName = SOURCE_FOLDER + "signCert.pem";
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCert.pem";

        caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, PASSWORD);
        checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, PASSWORD);
    }

    @Test
    public void validateResponderOcspNoCheckTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        ValidationReport report = validateTest(checkDate);
        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(RevocationDataValidator.REVOCATION_DATA_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.TRUSTED_OCSP_RESPONDER, item.getMessage());
        item = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                item.getCertificate().getSubjectX500Principal()), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void validateAuthorizedOCSPResponderWithOcspTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        ValidationReport report = verifyResponderWithOcsp(false);

        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                item.getCertificate().getSubjectX500Principal()), item.getMessage());
        item = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                item.getCertificate().getSubjectX500Principal()), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void validateAuthorizedOCSPResponderWithOcspRevokedTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertForOcspTest.pem";
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];

        ValidationReport report = verifyResponderWithOcsp(true);

        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getFailures().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(responderCert, item.getCertificate());
        Assert.assertEquals(OCSPValidator.CERT_IS_REVOKED, item.getMessage());
        Assert.assertEquals(responderCert, item.getCertificate());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void validateAuthorizedOCSPResponderFromTheTrustedStoreTest() throws GeneralSecurityException, IOException {
        ValidationReport report = validateOcspWithoutCertsTest(true);

        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                item.getCertificate().getSubjectX500Principal()), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void noResponderFoundInCertsTest() throws GeneralSecurityException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setOcspCertsChain(new IX509CertificateHolder[]{FACTORY.createJcaX509CertificateHolder(caCert)});
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);

        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getFailures().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(OCSPValidator.OCSP_COULD_NOT_BE_VERIFIED, item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void noResponderFoundTest() throws GeneralSecurityException, IOException {
        ValidationReport report = validateOcspWithoutCertsTest(false);

        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getFailures().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(OCSPValidator.OCSP_COULD_NOT_BE_VERIFIED, item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void validationDateAfterNextUpdateTest() throws GeneralSecurityException, IOException {
        // Same next update is set in the test OCSP builder.
        Date nextUpdate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 30);
        Date checkDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 45);
        ValidationReport report = validateTest(checkDate, TimeTestUtil.TEST_DATE_TIME, 50);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(OCSPValidator.OCSP_IS_NO_LONGER_VALID, checkDate, nextUpdate),
                item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void certificateWasRevokedAfterCheckDateTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 10);

        ValidationReport report = validateRevokedTest(checkDate, revocationDate);

        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(3, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(RevocationDataValidator.REVOCATION_DATA_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.TRUSTED_OCSP_RESPONDER, item.getMessage());
        CertificateReportItem certificateCheckItem = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, certificateCheckItem.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                certificateCheckItem.getCertificate().getSubjectX500Principal()), certificateCheckItem.getMessage());
        CertificateReportItem ocspCheckItem = (CertificateReportItem) report.getLogs().get(2);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, ocspCheckItem.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED,
                revocationDate), ocspCheckItem.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void certificateWasRevokedBeforeCheckDateTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1);

        ValidationReport report = validateRevokedTest(checkDate, revocationDate);

        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem ocspCheckItem = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, ocspCheckItem.getCheckName());
        Assert.assertEquals(OCSPValidator.CERT_IS_REVOKED, ocspCheckItem.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INVALID, report.getValidationResult());
    }

    @Test
    public void certificateStatusIsUnknownTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setCertificateStatus(FACTORY.createUnknownStatus());
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem ocspCheckItem = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, ocspCheckItem.getCheckName());
        Assert.assertEquals(OCSPValidator.CERT_STATUS_IS_UNKNOWN, ocspCheckItem.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void serialNumbersDoesNotMatchTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 1)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(caCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.setTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator()
                .setIssuingCertificateRetriever(certificateRetriever);

        validator.validate(report, checkCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp, checkDate);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getFailures().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(OCSPValidator.SERIAL_NUMBERS_DO_NOT_MATCH, item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void ocspForSelfSignedCertTest() throws GeneralSecurityException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(caCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, caCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);
        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.SELF_SIGNED_CERTIFICATE, item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void issuersDoesNotMatchTest() throws GeneralSecurityException, IOException {
        String wrongRootCertFileName = SOURCE_FOLDER + "rootCertForOcspTest.pem";

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();

        OCSPValidator validator = new OCSPValidator()
                .setIssuingCertificateRetriever(new TestIssuingCertificateRetriever(wrongRootCertFileName));

        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());

        CertificateReportItem logItem = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, logItem.getCheckName());
        Assert.assertEquals(OCSPValidator.ISSUERS_DO_NOT_MATCH, logItem.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void certificateDoesNotVerifyWithSuppliedKeyTest()
            throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertForOcspTest.pem";
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, PASSWORD);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);

        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);

        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem ocspCheckItem = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, ocspCheckItem.getCheckName());
        Assert.assertEquals(OCSPValidator.INVALID_OCSP, ocspCheckItem.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INVALID, report.getValidationResult());
    }

    @Test
    public void trustedOcspResponderDoesNotHaveOcspSigningExtensionTest() throws GeneralSecurityException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        // Configure OCSP signing authority for the certificate in question
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, checkCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);
        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());

        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                item.getCertificate().getSubjectX500Principal()), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void authorizedOcspResponderDoesNotHaveOcspSigningExtensionTest()
            throws GeneralSecurityException, IOException {
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertWithoutOcspSigning.pem";
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, 1)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(3, report.getLogs().size());

        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(CertificateChainValidator.EXTENSIONS_CHECK, item.getCheckName());
        // ExtendedKeyUsageExtension.OCSP_SIGNING is missing.
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.EXTENSION_MISSING,
                OID.X509Extensions.EXTENDED_KEY_USAGE), item.getMessage());

        item = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(RevocationDataValidator.REVOCATION_DATA_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.TRUSTED_OCSP_RESPONDER, item.getMessage());
        item = (CertificateReportItem) report.getLogs().get(2);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                item.getCertificate().getSubjectX500Principal()), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void positiveFreshnessPositiveTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        ValidationReport report = validateTest(checkDate, DateTimeUtil.addDaysToDate(checkDate, -3), 5);
        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(RevocationDataValidator.REVOCATION_DATA_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.TRUSTED_OCSP_RESPONDER, item.getMessage());
        item = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                item.getCertificate().getSubjectX500Principal()), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void positiveFreshnessNegativeTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date thisUpdate = DateTimeUtil.addDaysToDate(checkDate, -3);
        ValidationReport report = validateTest(checkDate, thisUpdate, 2);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(OCSPValidator.FRESHNESS_CHECK,
                thisUpdate, checkDate, 2 * MILLISECONDS_PER_DAY), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    @Test
    public void negativeFreshnessPositiveTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        ValidationReport report = validateTest(checkDate, DateTimeUtil.addDaysToDate(checkDate, 5), -3);
        Assert.assertEquals(0, report.getFailures().size());
        Assert.assertEquals(2, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(RevocationDataValidator.REVOCATION_DATA_CHECK, item.getCheckName());
        Assert.assertEquals(RevocationDataValidator.TRUSTED_OCSP_RESPONDER, item.getMessage());
        item = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(CertificateChainValidator.CERTIFICATE_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(CertificateChainValidator.CERTIFICATE_TRUSTED,
                item.getCertificate().getSubjectX500Principal()), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void negativeFreshnessNegativeTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        Date thisUpdate = DateTimeUtil.addDaysToDate(checkDate, 2);
        ValidationReport report = validateTest(checkDate, thisUpdate, -3);
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(1, report.getLogs().size());
        CertificateReportItem item = (CertificateReportItem) report.getLogs().get(0);
        Assert.assertEquals(OCSPValidator.OCSP_CHECK, item.getCheckName());
        Assert.assertEquals(MessageFormatUtil.format(OCSPValidator.FRESHNESS_CHECK,
                thisUpdate, checkDate, -3 * MILLISECONDS_PER_DAY), item.getMessage());
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
    }

    private ValidationReport validateTest(Date checkDate) throws CertificateException, IOException {
        return validateTest(checkDate, DateTimeUtil.addDaysToDate(checkDate, 1), 0);
    }

    private ValidationReport validateTest(Date checkDate, Date thisUpdate, long freshness)
            throws CertificateException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(thisUpdate));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.setFreshness(freshness * MILLISECONDS_PER_DAY);
        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate);
        return report;
    }

    private ValidationReport validateRevokedTest(Date checkDate, Date revocationDate)
            throws IOException, CertificateException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setCertificateStatus(FACTORY.createRevokedStatus(revocationDate,
                FACTORY.createCRLReason().getKeyCompromise()));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate);
        return report;
    }

    private ValidationReport validateOcspWithoutCertsTest(boolean addResponderToTrusted)
            throws IOException, CertificateException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setOcspCertsChain(new IX509CertificateHolder[0]);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));
        if (addResponderToTrusted) {
            certificateRetriever.addTrustedCertificates(Collections.singletonList(responderCert));
        }

        OCSPValidator validator = new OCSPValidator().setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME);
        return report;
    }

    private ValidationReport verifyResponderWithOcsp(boolean revokedOcsp)
            throws IOException, CertificateException, AbstractOperatorCreationException, AbstractPKCSException {
        String rootCertFileName = SOURCE_FOLDER + "rootCertForOcspTest.pem";
        String checkCertFileName = SOURCE_FOLDER + "signCertForOcspTest.pem";
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertForOcspTest.pem";
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;

        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootCertFileName, PASSWORD);
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        PrivateKey ocspRespPrivateKey = PemFileHelper.readFirstKey(ocspResponderCertFileName, PASSWORD);

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, -5)));
        builder.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 5)));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        OCSPValidator validator = new OCSPValidator();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        TestOcspResponseBuilder builder2 = revokedOcsp ? new TestOcspResponseBuilder(caCert, caPrivateKey,
                FACTORY.createRevokedStatus(
                        DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                        FACTORY.createCRLReason().getKeyCompromise())) :
                new TestOcspResponseBuilder(caCert, caPrivateKey);
        builder2.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 20)));
        builder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 30)));
        TestOcspClient ocspClient2 = new TestOcspClient().addBuilderForCertIssuer(caCert, builder2);

        CertificateChainValidator certificateChainValidator = new CertificateChainValidator()
                .addOcspClient(ocspClient2);
        certificateChainValidator.getRevocationDataValidator()
                .setOnlineFetching(RevocationDataValidator.OnlineFetching.NEVER_FETCH);
        if (revokedOcsp) {
            certificateChainValidator.proceedValidationAfterFail(false);
        }
        validator.setCertificateChainValidator(certificateChainValidator);
        validator.setIssuingCertificateRetriever(certificateRetriever);
        validator.validate(report, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate);
        return report;
    }

    private static class TestIssuingCertificateRetriever extends IssuingCertificateRetriever {
        Certificate issuerCertificate;

        public TestIssuingCertificateRetriever(String issuerPath) throws CertificateException, IOException {
            super();
            this.issuerCertificate = PemFileHelper.readFirstChain(issuerPath)[0];
        }

        @Override
        public Certificate retrieveIssuerCertificate(Certificate certificate) {
            return issuerCertificate;
        }
    }
}