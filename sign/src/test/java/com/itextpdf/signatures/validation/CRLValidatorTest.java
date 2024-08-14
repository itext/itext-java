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
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.TimestampConstants;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.mocks.MockChainValidator;
import com.itextpdf.signatures.validation.mocks.MockIssuingCertificateRetriever;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Tag("BouncyCastleUnitTest")
public class CRLValidatorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/CRLValidatorTest/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final char[] KEY_PASSWORD = "testpassphrase".toCharArray();

    private MockChainValidator mockChainValidator;

    private X509Certificate crlIssuerCert;
    private X509Certificate signCert;
    private PrivateKey crlIssuerKey;
    private PrivateKey intermediateKey;
    private IssuingCertificateRetriever certificateRetriever;
    private ValidatorChainBuilder validatorChainBuilder;

    @BeforeAll
    public static void setUpOnce() {
        Security.addProvider(FACTORY.getProvider());
    }

    @BeforeEach
    public void setUp() {
        certificateRetriever = new IssuingCertificateRetriever();
        SignatureValidationProperties parameters = new SignatureValidationProperties();
        mockChainValidator = new MockChainValidator();
        validatorChainBuilder = new ValidatorChainBuilder()
                .withIssuingCertificateRetrieverFactory(()-> certificateRetriever)
                .withSignatureValidationProperties(parameters)
                .withCertificateChainValidatorFactory(()-> mockChainValidator);
    }

    @Test
    public void happyPathTest() throws Exception {
        retrieveTestResources("happyPath");
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5)
        );

        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID));
    }

    @Test
    public void nextUpdateBeforeValidationTest() throws Exception {
        retrieveTestResources("happyPath");
        Date nextUpdate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5);
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -15),
                nextUpdate
        );
        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la
                        .withMessage(CRLValidator.UPDATE_DATE_BEFORE_CHECK_DATE, l -> nextUpdate, l -> TimeTestUtil.TEST_DATE_TIME)
                ));
    }

    @Test
    public void chainValidatorUsageTest() throws Exception {
        retrieveTestResources("happyPath");
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5)
        );
        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);

        Assertions.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());

        Assertions.assertEquals(1, mockChainValidator.verificationCalls.size());
        Assertions.assertEquals(crlIssuerCert, mockChainValidator.verificationCalls.get(0).certificate);
        Assertions.assertEquals(CertificateSource.CRL_ISSUER,
                mockChainValidator.verificationCalls.get(0).context.getCertificateSource());
        Assertions.assertEquals(ValidatorContext.CRL_VALIDATOR,
                mockChainValidator.verificationCalls.get(0).context.getValidatorContext());
    }

    @Test
    public void issuerCertificateIsNotFoundTest() throws Exception {
        retrieveTestResources("missingIssuer");
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5)
        );
        ValidationReport report = performValidation("missingIssuer", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la
                        .withMessage(CRLValidator.CRL_ISSUER_NOT_FOUND))
        );
    }

    @Test
    public void crlIssuerAndSignCertHaveNoSharedRootTest() throws Exception {
        retrieveTestResources("crlIssuerAndSignCertHaveNoSharedRoot");
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5)
        );
        ValidationReport report = performValidation("crlIssuerAndSignCertHaveNoSharedRoot",
                TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la
                        .withMessage(CRLValidator.CRL_ISSUER_NO_COMMON_ROOT))
        );
    }

    @Test
    // CRL has the certificate revoked before signing date
    public void crlIssuerRevokedBeforeSigningDate() throws Exception {
        retrieveTestResources("crlIssuerRevokedBeforeSigningDate");
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -2);
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5),
                signCert, revocationDate, 1

        );
        ValidationReport report = performValidation("crlIssuerRevokedBeforeSigningDate",
                TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(al -> al
                        .withStatus(ReportItem.ReportItemStatus.INVALID)
                        .withMessage(CRLValidator.CERTIFICATE_REVOKED, i -> crlIssuerCert.getSubjectX500Principal(),
                                i -> revocationDate))
        );

    }

    @Test
    // CRL has the certificate revoked after signing date
    public void crlRevokedAfterSigningDate() throws Exception {
        retrieveTestResources("happyPath");
        Date revocationDate = DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +20);
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +18),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +23),
                signCert, revocationDate, 1

        );
        ValidationReport report = performValidation("happyPath",
                TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(la -> la
                        .withMessage(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED, i -> revocationDate)
                        .withStatus(ReportItem.ReportItemStatus.INFO)
                        .withCertificate(signCert)
                ));
    }

    @Test
    //CRL response is invalid (signature not matching)
    public void crlSignatureMismatch() throws Exception {
        retrieveTestResources("happyPath");
        byte[] crl = createCrl(
                crlIssuerCert,
                intermediateKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +18),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +23),
                signCert, DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +20), 1

        );
        ValidationReport report = performValidation("happyPath",
                TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(la -> la
                        .withMessage(CRLValidator.CRL_INVALID)
                        .withStatus(ReportItem.ReportItemStatus.INDETERMINATE)));
    }

    @Test
    public void crlContainsOnlyCACertsTest() throws Exception {
        String crlPath = SOURCE_FOLDER + "issuingDistributionPointTest/onlyCA.crl";
        ValidationReport report = checkCrlScope(crlPath);
        AssertValidationReport.assertThat(report, a -> a
                .hasLogItem(la -> la
                        .withMessage(CRLValidator.CERTIFICATE_IS_NOT_IN_THE_CRL_SCOPE)
                        .withStatus(ReportItem.ReportItemStatus.INDETERMINATE)));
    }

    @Test
    public void crlContainsOnlyUserCertsTest() throws Exception {
        String crlPath = SOURCE_FOLDER + "issuingDistributionPointTest/onlyUser.crl";
        ValidationReport report = checkCrlScope(crlPath);
        Assertions.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
    }

    @Test
    public void crlContainsOnlyAttributeCertsTest() throws Exception {
        String crlPath = SOURCE_FOLDER + "issuingDistributionPointTest/onlyAttr.crl";
        ValidationReport report = checkCrlScope(crlPath);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la
                        .withMessage(CRLValidator.ATTRIBUTE_CERTS_ASSERTED)));
    }

    @Test
    public void onlySomeReasonsTest() throws Exception {
        String root = SOURCE_FOLDER + "issuingDistributionPointTest/root.pem";
        String sign = SOURCE_FOLDER + "issuingDistributionPointTest/sign.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(root)[0];
        PrivateKey rootKey = PemFileHelper.readFirstKey(root, KEY_PASSWORD);
        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(sign)[0];
        TestCrlBuilder builder = new TestCrlBuilder(rootCert, rootKey);
        builder.addExtension(FACTORY.createExtension().getIssuingDistributionPoint(), true,
                FACTORY.createIssuingDistributionPoint(null, false, false,
                        FACTORY.createReasonFlags(CRLValidator.ALL_REASONS - 31), false, false));
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
        ValidationReport report = new ValidationReport();
        ValidationContext context = new ValidationContext(
                ValidatorContext.REVOCATION_DATA_VALIDATOR, CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        CRLValidator validator = validatorChainBuilder.getCRLValidator();
        validator.validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(builder.makeCrl())),
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(al -> al
                        .withMessage(CRLValidator.ONLY_SOME_REASONS_CHECKED)
                        .withCertificate(signCert)
                )
        );
    }

    @Test
    public void checkLessReasonsTest() throws Exception {
        String fullCrlPath = SOURCE_FOLDER + "issuingDistributionPointTest/onlyUser.crl";
        String root = SOURCE_FOLDER + "issuingDistributionPointTest/root.pem";
        String sign = SOURCE_FOLDER + "issuingDistributionPointTest/sign.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(root)[0];
        PrivateKey rootKey = PemFileHelper.readFirstKey(root, KEY_PASSWORD);
        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(sign)[0];
        TestCrlBuilder builder = new TestCrlBuilder(rootCert, rootKey);
        builder.addExtension(FACTORY.createExtension().getIssuingDistributionPoint(), true,
                FACTORY.createIssuingDistributionPoint(null, false, false,
                        FACTORY.createReasonFlags(CRLValidator.ALL_REASONS - 31), false, false));
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
        ValidationReport report = new ValidationReport();
        ValidationContext context = new ValidationContext(
                ValidatorContext.REVOCATION_DATA_VALIDATOR, CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        CRLValidator validator = validatorChainBuilder.getCRLValidator();
        // Validate full CRL.
        validator.validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(FileUtil.getInputStreamForFile(fullCrlPath)),
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        // Validate CRL with onlySomeReasons.
        validator.validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(builder.makeCrl())),
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
        );
    }

    @Test
    public void removeFromCrlTest() throws Exception {
        String root = SOURCE_FOLDER + "issuingDistributionPointTest/root.pem";
        String sign = SOURCE_FOLDER + "issuingDistributionPointTest/sign.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(root)[0];
        PrivateKey rootKey = PemFileHelper.readFirstKey(root, KEY_PASSWORD);
        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(sign)[0];
        TestCrlBuilder builder = new TestCrlBuilder(rootCert, rootKey);
        builder.addCrlEntry(signCert, DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1),
                FACTORY.createCRLReason().getRemoveFromCRL());
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
        ValidationReport report = new ValidationReport();
        ValidationContext context = new ValidationContext(
                ValidatorContext.REVOCATION_DATA_VALIDATOR, CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validatorChainBuilder.getCRLValidator().validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(builder.makeCrl())),
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasLogItem(la -> la
                        .withCertificate(signCert)
                        .withCheckName(CRLValidator.CRL_CHECK)
                        .withMessage(CRLValidator.CERTIFICATE_IS_UNREVOKED))
        );
    }

    @Test
    public void fullCrlButDistributionPointWithReasonsTest() throws Exception {
        Date checkDate = TimeTestUtil.TEST_DATE_TIME;
        X509Certificate caCert = (X509Certificate)
                PemFileHelper.readFirstChain(SOURCE_FOLDER + "issuingDistributionPointTest/rootCert.pem")[0];
        PrivateKey caPrivateKey =
                PemFileHelper.readFirstKey(SOURCE_FOLDER + "issuingDistributionPointTest/rootCert.pem", KEY_PASSWORD);
        X509Certificate cert = (X509Certificate)
                PemFileHelper.readFirstChain(SOURCE_FOLDER + "issuingDistributionPointTest/certWithDPReasons.pem")[0];
        TestCrlBuilder builder = new TestCrlBuilder(caCert, caPrivateKey);
        builder.addExtension(FACTORY.createExtension().getIssuingDistributionPoint(), true,
                FACTORY.createIssuingDistributionPoint(FACTORY.createDistributionPointName(FACTORY.createCRLDistPoint(
                                CertificateUtil.getExtensionValue(cert,
                                        FACTORY.createExtension().getCRlDistributionPoints().getId()))
                        .getDistributionPoints()[0].getCRLIssuer()), false, false, null, false, false));

        certificateRetriever.setTrustedCertificates(Collections.singletonList(caCert));
        ValidationReport report = new ValidationReport();
        ValidationContext context = new ValidationContext(
                ValidatorContext.REVOCATION_DATA_VALIDATOR, CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validatorChainBuilder.getCRLValidator().validate(report, context, cert,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(builder.makeCrl())), checkDate,
                checkDate);

        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(la -> la
                        .withStatus(ReportItem.ReportItemStatus.INDETERMINATE)
                        .withCertificate(cert)
                        .withMessage(CRLValidator.ONLY_SOME_REASONS_CHECKED))
        );
    }

    @Test
    public void noExpiredCertOnCrlExtensionTest() throws Exception {
        // Certificate is expired on 01/01/2400.
        retrieveTestResources("happyPath");
        TestCrlBuilder builder = new TestCrlBuilder(crlIssuerCert, crlIssuerKey,
                DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, 401));
        byte[] crl = builder.makeCrl();
        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName(CRLValidator.CRL_CHECK)
                        .withMessage(CRLValidator.CERTIFICATE_IS_EXPIRED, i -> signCert.getNotAfter())
                        .withCertificate(signCert))
        );
    }

    @Test
    public void certExpiredBeforeDateFromExpiredCertOnCrlTest() throws Exception {
        // Certificate is expired on 01/01/2400.
        retrieveTestResources("happyPath");
        TestCrlBuilder builder = new TestCrlBuilder(crlIssuerCert, crlIssuerKey,
                DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, 401));
        builder.addExtension(FACTORY.createExtension().getExpiredCertsOnCRL(), false,
                FACTORY.createASN1GeneralizedTime(DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, 400)));
        byte[] crl = builder.makeCrl();
        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l
                        .withCheckName(CRLValidator.CRL_CHECK)
                        .withMessage(CRLValidator.CERTIFICATE_IS_EXPIRED, i -> signCert.getNotAfter())
                        .withCertificate(signCert))
        );
    }

    @Test
    public void certExpiredAfterDateFromExpiredCertOnCrlExtensionTest() throws Exception {
        // Certificate is expired on 01/01/2400.
        retrieveTestResources("happyPath");
        TestCrlBuilder builder = new TestCrlBuilder(crlIssuerCert, crlIssuerKey,
                DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, 401));
        builder.addExtension(FACTORY.createExtension().getExpiredCertsOnCRL(), false,
                FACTORY.createASN1GeneralizedTime(DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, 399)));
        byte[] crl = builder.makeCrl();
        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0));
    }

    @Test
    public void certificateRetrieverFailureTest() throws Exception {
        retrieveTestResources("happyPath");
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5)
        );
        MockIssuingCertificateRetriever mockCertificateRetriever = new MockIssuingCertificateRetriever();
        mockCertificateRetriever.ongetCrlIssuerCertificatesDo(c -> {throw new RuntimeException("just testing");});
        validatorChainBuilder.withIssuingCertificateRetrieverFactory(() -> mockCertificateRetriever);
        validatorChainBuilder.withCRLValidatorFactory(() -> new CRLValidator(validatorChainBuilder));

        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l -> l.withMessage(CRLValidator.CRL_ISSUER_REQUEST_FAILED)));

    }


    @Test
    public void chainValidatorFailureTest() throws Exception {
        retrieveTestResources("happyPath");
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5)
        );
        mockChainValidator.onCallDo(c -> {throw new RuntimeException("Just testing");});

        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasLogItem(l -> l.withMessage(CRLValidator.CRL_ISSUER_CHAIN_FAILED)));
    }


    @Test
    public void providedTimeIsUsedForResponderValidation() throws Exception {
        retrieveTestResources("happyPath");
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5)
        );
        mockChainValidator.onCallDo(c -> Assertions.assertEquals(TimeTestUtil.TEST_DATE_TIME, c.checkDate));

        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationReport.ValidationResult.VALID));
    }

    private ValidationReport checkCrlScope(String crlPath) throws Exception {
        String root = SOURCE_FOLDER + "issuingDistributionPointTest/root.pem";
        String sign = SOURCE_FOLDER + "issuingDistributionPointTest/sign.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(root)[0];
        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(sign)[0];
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
        ValidationReport report = new ValidationReport();
        ValidationContext context = new ValidationContext(
                ValidatorContext.REVOCATION_DATA_VALIDATOR, CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validatorChainBuilder.getCRLValidator().validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(FileUtil.getInputStreamForFile(crlPath)),
                TimeTestUtil.TEST_DATE_TIME, TimeTestUtil.TEST_DATE_TIME);
        return report;
    }

    private void retrieveTestResources(String path) throws Exception {
        String resourcePath = SOURCE_FOLDER + path + "/";
        crlIssuerCert = (X509Certificate) PemFileHelper.readFirstChain(resourcePath + "crl-issuer.cert.pem")[0];
        signCert = (X509Certificate) PemFileHelper.readFirstChain(resourcePath + "sign.cert.pem")[0];
        crlIssuerKey = PemFileHelper.readFirstKey(SOURCE_FOLDER + "keys/crl-key.pem", KEY_PASSWORD);
        intermediateKey = PemFileHelper.readFirstKey(SOURCE_FOLDER + "keys/im_key.pem", KEY_PASSWORD);
    }


    private byte[] createCrl(X509Certificate issuerCert, PrivateKey issuerKey, Date issueDate, Date nextUpdate)
            throws Exception {
        return createCrl(issuerCert, issuerKey, issueDate, nextUpdate,
                null, (Date) TimestampConstants.UNDEFINED_TIMESTAMP_DATE, 0);
    }

    private byte[] createCrl(X509Certificate issuerCert, PrivateKey issuerKey, Date issueDate, Date nextUpdate,
                             X509Certificate revokedCert, Date revocationDate, int reason)
            throws Exception {
        TestCrlBuilder builder = new TestCrlBuilder(issuerCert, issuerKey, issueDate);
        if (nextUpdate != null) {
            builder.setNextUpdate(nextUpdate);
        }
        if (revocationDate != TimestampConstants.UNDEFINED_TIMESTAMP_DATE && revokedCert != null) {
            builder.addCrlEntry(revokedCert, revocationDate, reason);
        }
        return builder.makeCrl();
    }

    public ValidationReport performValidation(String testName, Date testDate, byte[] encodedCrl)
            throws Exception {
        String resourcePath = SOURCE_FOLDER + testName + '/';
        String missingCertsFileName = resourcePath + "chain.pem";
        Certificate[] knownCerts = PemFileHelper.readFirstChain(missingCertsFileName);

        certificateRetriever.addKnownCertificates(Arrays.asList(knownCerts));


        X509Certificate certificateUnderTest =
                (X509Certificate) PemFileHelper.readFirstChain(resourcePath + "sign.cert.pem")[0];
        ValidationReport result = new ValidationReport();
        ValidationContext context = new ValidationContext(
                ValidatorContext.REVOCATION_DATA_VALIDATOR, CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validatorChainBuilder.getCRLValidator().validate(result, context, certificateUnderTest,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(encodedCrl)), testDate, testDate);
        return result;
    }
}
