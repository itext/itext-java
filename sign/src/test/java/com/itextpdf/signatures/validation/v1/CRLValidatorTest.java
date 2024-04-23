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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.TimestampConstants;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.report.CertificateReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

@Category(BouncyCastleUnitTest.class)
public class CRLValidatorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/CRLValidatorTest/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final char[] KEY_PASSWORD = "testpassphrase".toCharArray();

    private CRLValidator validator;
    private MockChainValidator mockChainValidator;
    private X509Certificate crlIssuerCert;
    private X509Certificate signCert;
    private PrivateKey crlIssuerKey;
    private PrivateKey intermediateKey;
    private IssuingCertificateRetriever certificateRetriever;

    @BeforeClass
    public static void setUpOnce() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Before
    public void setUp() {
        certificateRetriever = new IssuingCertificateRetriever();
        SignatureValidationProperties parameters = new SignatureValidationProperties();
        mockChainValidator = new MockChainValidator();
        ValidatorChainBuilder builder = new ValidatorChainBuilder()
                .withIssuingCertificateRetriever(certificateRetriever)
                .withSignatureValidationProperties(parameters)
                .withCertificateChainValidator(mockChainValidator);
        validator = new CRLValidator(builder);
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
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
        Assert.assertTrue(report.getFailures().isEmpty());
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
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(MessageFormatUtil.format(CRLValidator.UPDATE_DATE_BEFORE_CHECK_DATE,
                nextUpdate, TimeTestUtil.TEST_DATE_TIME), report.getFailures().get(0).getMessage());
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
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
        Assert.assertTrue(report.getFailures().isEmpty());

        Assert.assertEquals(1, mockChainValidator.verificationCalls.size());
        Assert.assertEquals(crlIssuerCert, mockChainValidator.verificationCalls.get(0).certificate);
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
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(CRLValidator.CRL_ISSUER_NOT_FOUND, report.getFailures().get(0).getMessage());
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
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(CRLValidator.CRL_ISSUER_NO_COMMON_ROOT, report.getFailures().get(0).getMessage());
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
        Assert.assertEquals(ValidationReport.ValidationResult.INVALID, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(MessageFormatUtil.format(CRLValidator.CERTIFICATE_REVOKED,
                        crlIssuerCert.getSubjectX500Principal(), revocationDate),
                report.getFailures().get(0).getMessage());
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
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(2, report.getLogs().size());
        Assert.assertEquals(
                MessageFormatUtil.format(SignLogMessageConstant.VALID_CERTIFICATE_IS_REVOKED, revocationDate),
                report.getLogs().get(1).getMessage());
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
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(CRLValidator.CRL_INVALID, report.getFailures().get(0).getMessage());
    }

    @Test
    public void crlContainsOnlyCACertsTest() throws Exception {
        String crlPath = SOURCE_FOLDER + "issuingDistributionPointTest/onlyCA.crl";
        ValidationReport report = checkCrlScope(crlPath);
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(CRLValidator.CERTIFICATE_IS_NOT_IN_THE_CRL_SCOPE, report.getFailures().get(0).getMessage());
    }

    @Test
    public void crlContainsOnlyUserCertsTest() throws Exception {
        String crlPath = SOURCE_FOLDER + "issuingDistributionPointTest/onlyUser.crl";
        ValidationReport report = checkCrlScope(crlPath);
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(0, report.getFailures().size());
    }

    @Test
    public void crlContainsOnlyAttributeCertsTest() throws Exception {
        String crlPath = SOURCE_FOLDER + "issuingDistributionPointTest/onlyAttr.crl";
        ValidationReport report = checkCrlScope(crlPath);
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        Assert.assertEquals(CRLValidator.ATTRIBUTE_CERTS_ASSERTED, report.getFailures().get(0).getMessage());
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
                ValidatorContext.REVOCATION_DATA_VALIDATOR,  CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validator.validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(builder.makeCrl())),
                TimeTestUtil.TEST_DATE_TIME);
        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        CertificateReportItem reportItem = (CertificateReportItem) report.getFailures().get(0);
        Assert.assertEquals(signCert, reportItem.getCertificate());
        Assert.assertEquals(CRLValidator.ONLY_SOME_REASONS_CHECKED, reportItem.getMessage());
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
                ValidatorContext.REVOCATION_DATA_VALIDATOR,  CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        // Validate full CRL.
        validator.validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(FileUtil.getInputStreamForFile(fullCrlPath)),
                TimeTestUtil.TEST_DATE_TIME);
        // Validate CRL with onlySomeReasons.
        validator.validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(builder.makeCrl())),
                TimeTestUtil.TEST_DATE_TIME);
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(0, report.getFailures().size());
        CertificateReportItem reportItem = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(signCert, reportItem.getCertificate());
        Assert.assertEquals(CRLValidator.SAME_REASONS_CHECK, reportItem.getMessage());
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
                ValidatorContext.REVOCATION_DATA_VALIDATOR,  CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validator.validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(builder.makeCrl())),
                TimeTestUtil.TEST_DATE_TIME);
        Assert.assertEquals(ValidationReport.ValidationResult.VALID, report.getValidationResult());
        Assert.assertEquals(0, report.getFailures().size());
        CertificateReportItem reportItem = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(signCert, reportItem.getCertificate());
        Assert.assertEquals(CRLValidator.CERTIFICATE_IS_UNREVOKED, reportItem.getMessage());
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
                ValidatorContext.REVOCATION_DATA_VALIDATOR,  CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validator.validate(report, context, cert,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(builder.makeCrl())), checkDate);

        Assert.assertEquals(ValidationReport.ValidationResult.INDETERMINATE, report.getValidationResult());
        Assert.assertEquals(1, report.getFailures().size());
        CertificateReportItem reportItem = (CertificateReportItem) report.getLogs().get(1);
        Assert.assertEquals(ReportItem.ReportItemStatus.INDETERMINATE, reportItem.getStatus());
        Assert.assertEquals(cert, reportItem.getCertificate());
        Assert.assertEquals(CRLValidator.ONLY_SOME_REASONS_CHECKED, reportItem.getMessage());
    }

    @Test
    public void noExpiredCertOnCrlExtensionTest() throws Exception {
        // Certificate is expired on 01/01/2400.
        retrieveTestResources("happyPath");
        TestCrlBuilder builder = new TestCrlBuilder(crlIssuerCert, crlIssuerKey,
                DateTimeUtil.addYearsToDate(TimeTestUtil.TEST_DATE_TIME, 401));
        byte[] crl = builder.makeCrl();
        ValidationReport report = performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        new AssertValidationReport(report)
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals(CRLValidator.CRL_CHECK)
                                && l.getMessage().equals(MessageFormatUtil.format(CRLValidator.CERTIFICATE_IS_EXPIRED,
                                signCert.getNotAfter()))
                                && ((CertificateReportItem) l).getCertificate().equals(signCert),
                        CRLValidator.CERTIFICATE_IS_EXPIRED)
                .doAssert();
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
        new AssertValidationReport(report)
                .hasStatus(ValidationReport.ValidationResult.INDETERMINATE)
                .hasNumberOfFailures(1)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.getCheckName().equals(CRLValidator.CRL_CHECK)
                                && l.getMessage().equals(MessageFormatUtil.format(CRLValidator.CERTIFICATE_IS_EXPIRED,
                                signCert.getNotAfter()))
                                && ((CertificateReportItem) l).getCertificate().equals(signCert),
                        CRLValidator.CERTIFICATE_IS_EXPIRED)
                .doAssert();
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
        new AssertValidationReport(report)
                .hasStatus(ValidationReport.ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .doAssert();
    }

    private ValidationReport checkCrlScope(String crlPath) throws Exception {
        String root = SOURCE_FOLDER + "issuingDistributionPointTest/root.pem";
        String sign = SOURCE_FOLDER + "issuingDistributionPointTest/sign.pem";
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(root)[0];
        X509Certificate signCert = (X509Certificate) PemFileHelper.readFirstChain(sign)[0];
        certificateRetriever.setTrustedCertificates(Collections.singletonList(rootCert));
        ValidationReport report = new ValidationReport();
        ValidationContext context = new ValidationContext(
                ValidatorContext.REVOCATION_DATA_VALIDATOR,  CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validator.validate(report, context, signCert,
                (X509CRL) CertificateUtil.parseCrlFromStream(FileUtil.getInputStreamForFile(crlPath)),
                TimeTestUtil.TEST_DATE_TIME);
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
        TestCrlBuilder builder = new TestCrlBuilder(issuerCert, issuerKey);
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
                ValidatorContext.REVOCATION_DATA_VALIDATOR,  CertificateSource.SIGNER_CERT,
                TimeBasedContext.PRESENT);
        validator.validate(result, context, certificateUnderTest, (X509CRL) CertificateUtil.parseCrlFromStream(
                new ByteArrayInputStream(encodedCrl)), testDate);
        return result;
    }
}
