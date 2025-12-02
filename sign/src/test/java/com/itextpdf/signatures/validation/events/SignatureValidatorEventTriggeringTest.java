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
package com.itextpdf.signatures.validation.events;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.SignatureValidator;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.mocks.MockChainValidator;
import com.itextpdf.signatures.validation.mocks.MockDocumentRevisionsValidator;
import com.itextpdf.signatures.validation.mocks.MockIssuingCertificateRetriever;
import com.itextpdf.signatures.validation.mocks.MockRevocationDataValidator;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class SignatureValidatorEventTriggeringTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/SignatureValidatorTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    private ValidatorChainBuilder createValidatorChainBuilder(MockIssuingCertificateRetriever mockCertificateRetriever,
            SignatureValidationProperties parameters, MockChainValidator mockCertificateChainValidator,
            MockDocumentRevisionsValidator mockDocumentRevisionsValidator){
        return new ValidatorChainBuilder()
                .withIssuingCertificateRetrieverFactory(() -> mockCertificateRetriever)
                .withSignatureValidationProperties(parameters)
                .withCertificateChainValidatorFactory(() -> mockCertificateChainValidator)
                .withRevocationDataValidatorFactory(() -> new MockRevocationDataValidator())
                .withDocumentRevisionsValidatorFactory(() -> mockDocumentRevisionsValidator);
    }

    @Test
    public void algorithmReportingTest() throws IOException {
        MockChainValidator mockCertificateChainValidator = new MockChainValidator();
        SignatureValidationProperties parameters = new SignatureValidationProperties();
        MockIssuingCertificateRetriever mockCertificateRetriever = new MockIssuingCertificateRetriever();
        MockDocumentRevisionsValidator mockDocumentRevisionsValidator = new MockDocumentRevisionsValidator();
        ValidatorChainBuilder builder = createValidatorChainBuilder(mockCertificateRetriever, parameters,
                mockCertificateChainValidator, mockDocumentRevisionsValidator);
        MockEventListener testEventHandler = new MockEventListener();
        builder.getEventManager().register(testEventHandler);

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "docWithDss.pdf"))) {
            SignatureValidator signatureValidator = builder.buildSignatureValidator(document);
            signatureValidator.validateSignatures();
        }

        Assertions.assertEquals(2,
                testEventHandler.getEvents().stream().filter(e -> e instanceof AlgorithmUsageEvent).count());
        Assertions.assertEquals(2,
                testEventHandler.getEvents().stream().filter(e ->
                        e instanceof AlgorithmUsageEvent &&
                                "Signature verification check.".equals(((AlgorithmUsageEvent)e)
                                .getUsageLocation())).count());
    }
}
