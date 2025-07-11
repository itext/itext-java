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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.validation.AssertValidationReport;
import com.itextpdf.signatures.validation.CertificateChainValidator;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.mocks.MockRevocationDataValidator;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class LOTLCertificateChainValidatorTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/lotl/LOTLCertificateChainValidatorTest/";
    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);

    @Test
    public void lotlTrustedStoreTest() throws CertificateException, IOException {
        MockRevocationDataValidator mockRevocationDataValidator = new MockRevocationDataValidator();
        SignatureValidationProperties properties = new SignatureValidationProperties();
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(rootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        context.addServiceChronologicalInfo(new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900,1,1, 0, 0)));

        ValidatorChainBuilder validatorChainBuilder = new ValidatorChainBuilder();
        LOTLTrustedStore lotlTrustedStore = new LOTLTrustedStore(validatorChainBuilder);
        lotlTrustedStore.addCertificatesWithContext(Collections.<CountryServiceContext>singletonList(context));
        validatorChainBuilder
                .withSignatureValidationProperties(properties)
                .withRevocationDataValidatorFactory(()-> mockRevocationDataValidator)
                .withLOTLTrustedStoreFactory(() -> lotlTrustedStore);

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report1 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.CRL_ISSUER),
                rootCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report1, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(LOTLTrustedStore.CERTIFICATE_TRUSTED,
                                i-> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
        );
    }

    @Test
    public void lotlTrustedStoreChainTest() throws CertificateException, IOException {
        MockRevocationDataValidator mockRevocationDataValidator = new MockRevocationDataValidator();
        SignatureValidationProperties properties = new SignatureValidationProperties();

        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate signingCert = (X509Certificate) certificateChain[0];
        X509Certificate intermediateCert = (X509Certificate) certificateChain[1];
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(rootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        context.addServiceChronologicalInfo(new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900,1,1, 0, 0)));

        ValidatorChainBuilder validatorChainBuilder = new ValidatorChainBuilder();
        LOTLTrustedStore lotlTrustedStore = new LOTLTrustedStore(validatorChainBuilder);
        lotlTrustedStore.addCertificatesWithContext(Collections.<CountryServiceContext>singletonList(context));
        validatorChainBuilder
                .withKnownCertificates(Collections.<Certificate>singletonList(intermediateCert))
                .withSignatureValidationProperties(properties)
                .withRevocationDataValidatorFactory(()-> mockRevocationDataValidator)
                .withLOTLTrustedStoreFactory(() -> lotlTrustedStore);

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report1 = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.CRL_ISSUER),
                signingCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report1, a-> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
                .hasNumberOfLogs(1)
                .hasLogItem(l -> l.withCheckName("Certificate check.")
                        .withMessage(LOTLTrustedStore.CERTIFICATE_TRUSTED,
                                i-> rootCert.getSubjectX500Principal())
                        .withCertificate(rootCert))
        );
    }

    @Test
    public void lotlTrustedStoreExtensionTest() throws CertificateException, IOException {
        MockRevocationDataValidator mockRevocationDataValidator = new MockRevocationDataValidator();
        SignatureValidationProperties properties = new SignatureValidationProperties();
        String chainName = CERTS_SRC + "chain.pem";
        Certificate[] certificateChain = PemFileHelper.readFirstChain(chainName);
        X509Certificate rootCert = (X509Certificate) certificateChain[2];

        CountryServiceContext context = new CountryServiceContext();
        context.addCertificate(rootCert);
        context.setServiceType("http://uri.etsi.org/TrstSvc/Svctype/CA/QC");
        ServiceChronologicalInfo info =new ServiceChronologicalInfo(ServiceChronologicalInfo.GRANTED,
                LocalDateTime.of(1900,1,1, 0, 0));
        info.addExtension(new AdditionalServiceInformationExtension(
                "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication"));
        context.addServiceChronologicalInfo(info);

        ValidatorChainBuilder validatorChainBuilder = new ValidatorChainBuilder();
        LOTLTrustedStore lotlTrustedStore = new LOTLTrustedStore(validatorChainBuilder);
        validatorChainBuilder
                .withSignatureValidationProperties(properties)
                .withRevocationDataValidatorFactory(()-> mockRevocationDataValidator)
                .withLOTLTrustedStoreFactory(() -> lotlTrustedStore);
        lotlTrustedStore.addCertificatesWithContext(Collections.<CountryServiceContext>singletonList(context));

        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        properties.setRequiredExtensions(CertificateSources.all(), Collections.<CertificateExtension>emptyList());

        ValidationReport report = validator.validateCertificate(baseContext.setCertificateSource(CertificateSource.CRL_ISSUER),
                rootCert, TimeTestUtil.TEST_DATE_TIME);

        AssertValidationReport.assertThat(report, a-> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(2)
                .hasNumberOfLogs(2)
                .hasLogItem(l -> l.withCheckName(LOTLTrustedStore.EXTENSIONS_CHECK)
                        .withMessage(LOTLTrustedStore.SCOPE_SPECIFIED_WITH_INVALID_TYPES,
                                i-> rootCert.getSubjectX500Principal(),
                                k -> "http://uri.etsi.org/TrstSvc/TrustedList/SvcInfoExt/ForWebSiteAuthentication")
                        .withCertificate(rootCert))
        );
    }
}
