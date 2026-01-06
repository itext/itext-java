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

import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.validation.CertificateChainValidator;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.mocks.MockRevocationDataValidator;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class CertificateChainValidatorEventTriggeringTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/validation/CertificateChainValidatorTest/";

    private final ValidationContext baseContext = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
            CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);

    private ValidatorChainBuilder setUpValidatorChain(IssuingCertificateRetriever certificateRetriever, SignatureValidationProperties properties, MockRevocationDataValidator mockRevocationDataValidator) {
        ValidatorChainBuilder validatorChainBuilder = new ValidatorChainBuilder();
        validatorChainBuilder
                .withIssuingCertificateRetrieverFactory(()-> certificateRetriever)
                .withSignatureValidationProperties(properties)
                .withRevocationDataValidatorFactory(()-> mockRevocationDataValidator);
        return validatorChainBuilder;
    }

    @Test
    public void algoritmEventTest() throws CertificateException, IOException {
        MockRevocationDataValidator mockRevocationDataValidator = new MockRevocationDataValidator();
        IssuingCertificateRetriever certificateRetriever = new IssuingCertificateRetriever();
        SignatureValidationProperties properties = new SignatureValidationProperties();
        String chainName = CERTS_SRC + "chain.pem";

        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(chainName)[0];

        ValidatorChainBuilder validatorChainBuilder = setUpValidatorChain(certificateRetriever, properties,
                mockRevocationDataValidator);
        CertificateChainValidator validator = validatorChainBuilder.buildCertificateChainValidator();
        certificateRetriever.setTrustedCertificates(Collections.<Certificate>singletonList(rootCert));

        MockEventListener testEventListener = new MockEventListener();
        validatorChainBuilder.getEventManager().register(testEventListener);

        ValidationReport report =
                validator.validateCertificate(baseContext, rootCert, TimeTestUtil.TEST_DATE_TIME);

        Assertions.assertTrue(testEventListener.getEvents().stream().anyMatch(e -> e instanceof AlgorithmUsageEvent));
        Assertions.assertTrue(testEventListener.getEvents().stream().anyMatch(
                e -> "Certificate check.".equals(((AlgorithmUsageEvent)e).getUsageLocation())));
    }
}
