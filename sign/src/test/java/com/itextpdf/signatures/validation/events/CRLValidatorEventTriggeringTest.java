/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation.events;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.TimestampConstants;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.mocks.MockChainValidator;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class CRLValidatorEventTriggeringTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/CRLValidatorTest/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final char[] KEY_PASSWORD = "testpassphrase".toCharArray();

    private MockChainValidator mockChainValidator;

    private X509Certificate crlIssuerCert;
    private PrivateKey crlIssuerKey;
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
    public void algorithmReportingTest() throws Exception {
        retrieveTestResources("happyPath");
        byte[] crl = createCrl(
                crlIssuerCert,
                crlIssuerKey,
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -5),
                DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, +5)
        );
        MockEventListener testEventListener = new MockEventListener();
        validatorChainBuilder.getEventManager().register(testEventListener);
        performValidation("happyPath", TimeTestUtil.TEST_DATE_TIME, crl);
        Assertions.assertTrue(testEventListener.getEvents().stream().anyMatch(e -> e instanceof AlgorithmUsageEvent));
        Assertions.assertTrue(testEventListener.getEvents().stream().anyMatch(
                e -> "CRL response check.".equals(((AlgorithmUsageEvent)e).getUsageLocation())));
    }

    private void retrieveTestResources(String path) throws Exception {
        String resourcePath = SOURCE_FOLDER + path + "/";
        crlIssuerCert = (X509Certificate) PemFileHelper.readFirstChain(resourcePath + "crl-issuer.cert.pem")[0];
        crlIssuerKey = PemFileHelper.readFirstKey(SOURCE_FOLDER + "keys/crl-key.pem", KEY_PASSWORD);
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
        validatorChainBuilder.buildCRLValidator().validate(result, context, certificateUnderTest,
                (X509CRL) CertificateUtil.parseCrlFromStream(new ByteArrayInputStream(encodedCrl)), testDate, testDate);
        return result;
    }
}
