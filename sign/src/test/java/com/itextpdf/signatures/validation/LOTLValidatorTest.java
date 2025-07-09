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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

@Tag("BouncyCastleIntegrationTest")
@DisabledInNativeImage
public class LOTLValidatorTest extends ExtendedITextTest {
    private static final String SOURCE = "./src/test/resources/com/itextpdf/signatures/validation/LOTLValidatorTest/";

    @Test
    public void validationTest() {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        LOTLValidator validator = chainBuilder.getLotlValidator();
        ValidationReport report = validator.validate();
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.VALID)
                .hasNumberOfFailures(0)
        );
        List<IServiceContext> trustedCertificates = validator.getNationalTrustedCertificates();
        Assertions.assertFalse(trustedCertificates.isEmpty());
    }

    @Test
    public void lotlUnavailableTest() {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.withLOTLValidator(() -> new LOTLValidator(chainBuilder) {
            @Override
            protected byte[] getLotlBytes() {
                return null;
            }
        });
        ValidationReport report = chainBuilder.getLotlValidator().validate();
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasLogItem(l -> l.withCheckName(LOTLValidator.LOTL_VALIDATION)
                        .withMessage(LOTLValidator.UNABLE_TO_RETRIEVE_LOTL))
        );
    }

    @Test
    public void euJournalCertificatesEmptyTest() {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.withLOTLValidator(() -> new LOTLValidator(chainBuilder) {
            @Override
            protected List<Certificate> getEUJournalCertificates(ValidationReport report) {
                return Collections.<Certificate>emptyList();
            }
        });
        ValidationReport report = chainBuilder.getLotlValidator().validate();
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(2)
                .hasLogItem(l -> l.withCheckName(LOTLValidator.LOTL_VALIDATION)
                        .withMessage(LOTLValidator.LOTL_VALIDATION_UNSUCCESSFUL))
        );
    }

    @Test
    public void lotlWithBrokenPivotsTest() {
        ValidatorChainBuilder chainBuilder = new ValidatorChainBuilder();
        chainBuilder.withLOTLValidator(() -> new LOTLValidator(chainBuilder) {
            @Override
            protected byte[] getLotlBytes() {
                try {
                    return Files.readAllBytes(Paths.get(SOURCE + "eu-lotl-withBrokenPivot.xml"));
                } catch (IOException e) {
                    throw new PdfException(e);
                }
            }
        });
        ValidationReport report = chainBuilder.getLotlValidator().validate();
        AssertValidationReport.assertThat(report, a -> a
                .hasStatus(ValidationResult.INVALID)
                .hasNumberOfFailures(1)
                .hasLogItem(l -> l.withCheckName(LOTLValidator.LOTL_VALIDATION)
                        .withMessage(LOTLValidator.UNABLE_TO_RETRIEVE_PIVOT,
                                t -> "https://ec.europa.eu/tools/lotl/eu-lotl-pivot-335-BrokenUri.xml"))
        );
    }
}
