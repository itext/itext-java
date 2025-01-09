/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.report.CertificateReportItem;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class OCSPValidatorIntegrationTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/OCSPValidatorTest/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static X509Certificate caCert;
    private static PrivateKey caPrivateKey;
    private static X509Certificate checkCert;
    private static X509Certificate responderCert;
    private static PrivateKey ocspRespPrivateKey;
    private IssuingCertificateRetriever certificateRetriever;
    private SignatureValidationProperties parameters;
    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.REVOCATION_DATA_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private ValidatorChainBuilder validatorChainBuilder;

    @BeforeAll
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

    @BeforeEach
    public void setUp() {
        certificateRetriever = new IssuingCertificateRetriever();
        parameters = new SignatureValidationProperties();
        validatorChainBuilder = new ValidatorChainBuilder()
                .withSignatureValidationProperties(parameters)
                .withIssuingCertificateRetrieverFactory(() -> certificateRetriever);
    }

    @Test
    public void validateResponderOcspNoCheckTest() throws GeneralSecurityException, IOException {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        ValidationReport report = validateTest(checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(2)
                .hasLogItem(al -> al
                    .withCheckName(RevocationDataValidator.REVOCATION_DATA_CHECK)
                    .withMessage(RevocationDataValidator.TRUSTED_OCSP_RESPONDER)
                )
                .hasLogItem(al -> al
                    .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                    .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED,
                            l -> ((CertificateReportItem) l).getCertificate().getSubjectX500Principal())
                )
                .hasStatus(ValidationReport.ValidationResult.VALID));
    }

    @Test
    public void validateAuthorizedOCSPResponderWithOcspTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        ValidationReport report = verifyResponderWithOcsp(false);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(2)
                .hasLogItem(al -> al
                    .withCheckName(CertificateChainValidator.CERTIFICATE_CHECK)
                    .withMessage(CertificateChainValidator.CERTIFICATE_TRUSTED, l->
                                ((CertificateReportItem) l).getCertificate().getSubjectX500Principal())
                    )
                        .hasLogItem(al -> al
                                .withCheckName(OCSPValidator.OCSP_CHECK)
                                .withMessage(OCSPValidator.OCSP_RESPONDER_IS_CA)
                        )
                .hasStatus(ValidationReport.ValidationResult.VALID)
                );
    }

    @Test
    public void validateAuthorizedOCSPResponderWithOcspRevokedTest()
            throws AbstractOperatorCreationException, GeneralSecurityException, IOException, AbstractPKCSException {
        String ocspResponderCertFileName = SOURCE_FOLDER + "ocspResponderCertForOcspTest.pem";
        X509Certificate responderCert = (X509Certificate) PemFileHelper.readFirstChain(ocspResponderCertFileName)[0];
        certificateRetriever.addKnownCertificates(Collections.singleton(responderCert));

        ValidationReport report = verifyResponderWithOcsp(true);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(al -> al
                    .withCheckName(OCSPValidator.OCSP_CHECK)
                    .withMessage(OCSPValidator.CERT_IS_REVOKED)
                    .withStatus(ReportItem.ReportItemStatus.INDETERMINATE)
                    )
                );
    }

    @Test
    public void validateAuthorizedOCSPResponderFromTheTrustedStoreTest() throws GeneralSecurityException, IOException {
        ValidationReport report = validateOcspWithoutCertsTest();

        Assertions.assertEquals(0, report.getFailures().size());
        Assertions.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void trustedOcspResponderDoesNotHaveOcspSigningExtensionTest() throws GeneralSecurityException, IOException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        // Configure OCSP signing authority for the certificate in question
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(0)
                .hasStatus(ValidationReport.ValidationResult.VALID)
                );
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
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(1)
                .hasLogItem(al -> al
                    .withCheckName(CertificateChainValidator.EXTENSIONS_CHECK)
                    .withMessage(CertificateChainValidator.EXTENSION_MISSING,
                            l -> OID.X509Extensions.EXTENDED_KEY_USAGE)
                    )
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                );
    }

    @Test
    public void ocspResponseWithoutHashAlgoParametersTest() throws IOException {
        TestOcspClient ocspClient = new TestOcspClient();
        IBasicOCSPResp caBasicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert,
                        SOURCE_FOLDER + "ocspResponseWithoutHashAlgoParameters.dat"))));

        ValidationReport report = new ValidationReport();
        // Configure OCSP signing authority for the certificate in question
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, caBasicOCSPResp.getResponses()[0], caBasicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(0)
                .hasStatus(ValidationReport.ValidationResult.VALID)
        );
    }

    private ValidationReport validateTest(Date checkDate) throws CertificateException, IOException {
        Date thisUpdate = DateTimeUtil.addDaysToDate(checkDate, 1);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setThisUpdate(DateTimeUtil.getCalendar(thisUpdate));
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        parameters.setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                Duration.ofDays(0));
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp, checkDate, checkDate);
        return report;
    }

    private ValidationReport validateOcspWithoutCertsTest() throws IOException, CertificateException {
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        builder.setOcspCertsChain(new IX509CertificateHolder[0]);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(
                FACTORY.createASN1Primitive(ocspClient.getEncoded(checkCert, caCert, null))));

        ValidationReport report = new ValidationReport();
        certificateRetriever.addTrustedCertificates(Arrays.asList(caCert, responderCert));

        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0], basicOCSPResp,
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
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
        certificateRetriever.addTrustedCertificates(Collections.singletonList(caCert));

        TestOcspResponseBuilder builder2 = revokedOcsp ? new TestOcspResponseBuilder(caCert, caPrivateKey,
                FACTORY.createRevokedStatus(
                        DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                        FACTORY.createCRLReason().getKeyCompromise())) :
                new TestOcspResponseBuilder(caCert, caPrivateKey);
        builder2.setThisUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 20)));
        builder2.setNextUpdate(DateTimeUtil.getCalendar(DateTimeUtil.addDaysToDate(checkDate, 30)));
        TestOcspClient ocspClient2 = new TestOcspClient().addBuilderForCertIssuer(caCert, builder2);

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                        TimeBasedContexts.all(), SignatureValidationProperties .OnlineFetching.NEVER_FETCH)
                .setFreshness(ValidatorContexts.all(), CertificateSources.all(), TimeBasedContexts.all(),
                        Duration.ofDays(5));
        if (revokedOcsp) {
            parameters.setContinueAfterFailure(ValidatorContexts.all(), CertificateSources.all(), false);
        }
        RevocationDataValidator revocationDataValidator = validatorChainBuilder.getRevocationDataValidator();
        revocationDataValidator.addOcspClient(ocspClient);
        revocationDataValidator.addOcspClient(ocspClient2);
        validatorChainBuilder.withRevocationDataValidatorFactory(() -> revocationDataValidator);
        OCSPValidator validator = validatorChainBuilder.buildOCSPValidator();
        validator.validate(report, baseContext, checkCert, basicOCSPResp.getResponses()[0],
                basicOCSPResp, checkDate, checkDate);
        return report;
    }
}
