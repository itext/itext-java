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
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;

@Tag("BouncyCastleUnitTest")
public class RevocationDataValidatorIntegrationTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/signatures/validation/RevocationDataValidatorTest/";
    private static final char[] PASSWORD = "testpassphrase".toCharArray();


    private static X509Certificate caCert;
    private static PrivateKey caPrivateKey;
    private static X509Certificate checkCert;
    private static X509Certificate responderCert;
    private static PrivateKey ocspRespPrivateKey;
    private IssuingCertificateRetriever certificateRetriever;
    private SignatureValidationProperties parameters;
    private ValidatorChainBuilder validatorChainBuilder;
    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.SIGNATURE_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);

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
                .withIssuingCertificateRetrieverFactory(() -> certificateRetriever)
                .withSignatureValidationProperties(parameters);
    }


    @Test
    public void crlWithOnlySomeReasonsTest() throws Exception {
        TestCrlBuilder builder1 = new TestCrlBuilder(caCert, caPrivateKey);
        builder1.addExtension(FACTORY.createExtension().getIssuingDistributionPoint(), true,
                FACTORY.createIssuingDistributionPoint(null, false, false,
                        FACTORY.createReasonFlags(CRLValidator.ALL_REASONS - 31), false, false));
        TestCrlBuilder builder2 = new TestCrlBuilder(caCert, caPrivateKey);
        builder2.addExtension(FACTORY.createExtension().getIssuingDistributionPoint(), true,
                FACTORY.createIssuingDistributionPoint(null, false, false,
                        FACTORY.createReasonFlags(31), false, false));
        TestCrlClient crlClient = new TestCrlClient()
                .addBuilderForCertIssuer(builder1)
                .addBuilderForCertIssuer(builder2);
        TestOcspResponseBuilder ocspBuilder = new TestOcspResponseBuilder(responderCert, ocspRespPrivateKey);
        ocspBuilder.setProducedAt(DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -100));

        certificateRetriever.setTrustedCertificates(Collections.singletonList(caCert));

        parameters.setRevocationOnlineFetching(ValidatorContexts.all(), CertificateSources.all(),
                TimeBasedContexts.all(), SignatureValidationProperties.OnlineFetching.NEVER_FETCH);


        ValidationReport report = new ValidationReport();
        RevocationDataValidator validator = validatorChainBuilder.buildRevocationDataValidator();
        validator
                .addOcspClient(new TestOcspClient().addBuilderForCertIssuer(caCert, ocspBuilder))
                .addCrlClient(crlClient);

        validator.validate(report, baseContext, checkCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a -> a
                .hasNumberOfFailures(0)
                .hasLogItem(la -> la
                        .withCertificate(checkCert)
                        .withStatus(ReportItem.ReportItemStatus.INFO)
                        .withMessage(CRLValidator.ONLY_SOME_REASONS_CHECKED)
                ));

    }
}
