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

import com.itextpdf.signatures.validation.SignatureValidationProperties.ContextProperties;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.extensions.KeyUsage;
import com.itextpdf.signatures.validation.extensions.KeyUsageExtension;
import com.itextpdf.test.ExtendedITextTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Consumer;

@Tag("UnitTest")
public class SignatureValidationPropertiesTest extends ExtendedITextTest {

    @Test
    public void getParametersValueForSpecificTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();

        sut.setParameterValueFor(ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR, ValidatorContext.CRL_VALIDATOR,
                        ValidatorContext.SIGNATURE_VALIDATOR).getSet(),
                CertificateSources.of(CertificateSource.CRL_ISSUER, CertificateSource.SIGNER_CERT,
                        CertificateSource.TIMESTAMP).getSet(), TimeBasedContexts.of(TimeBasedContext.HISTORICAL).getSet(),
                new IncrementalFreshnessValueSetter(10, 1).getAction());

        // test the last value added
        Assertions.assertEquals(Duration.ofDays(18),
                sut.getParametersValueFor(ValidatorContext.SIGNATURE_VALIDATOR, CertificateSource.TIMESTAMP,
                        TimeBasedContext.HISTORICAL,
                        (p -> p.getFreshness())));

        //test the fifth value added
        Assertions.assertEquals(Duration.ofDays(14),
                sut.getParametersValueFor(ValidatorContext.CRL_VALIDATOR, CertificateSource.SIGNER_CERT,
                        TimeBasedContext.HISTORICAL,
                        (p -> p.getFreshness())));

        // test the general default
        Assertions.assertEquals(SignatureValidationProperties.DEFAULT_FRESHNESS_HISTORICAL,
                sut.getParametersValueFor(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.HISTORICAL,
                        (p -> p.getFreshness())));
    }


    @Test
    public void getParametersValueForDefaultTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();

        sut.setParameterValueFor(ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR, ValidatorContext.CRL_VALIDATOR,
                        ValidatorContext.SIGNATURE_VALIDATOR).getSet(),
                CertificateSources.of(CertificateSource.CRL_ISSUER, CertificateSource.SIGNER_CERT,
                        CertificateSource.TIMESTAMP).getSet(),
                TimeBasedContexts.of(TimeBasedContext.HISTORICAL).getSet(),
                new IncrementalFreshnessValueSetter(10, 1).getAction());

        // test the general default
        Assertions.assertEquals(SignatureValidationProperties.DEFAULT_FRESHNESS_PRESENT_OCSP,
                sut.getParametersValueFor(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.PRESENT,
                        (p -> p.getFreshness())));
    }

    @Test
    public void setDefaultAsLastShouldOverrideAll() {
        SignatureValidationProperties sut = new SignatureValidationProperties();

        sut.setParameterValueFor(ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR, ValidatorContext.CRL_VALIDATOR,
                        ValidatorContext.SIGNATURE_VALIDATOR).getSet(),
                CertificateSources.of(CertificateSource.CRL_ISSUER, CertificateSource.SIGNER_CERT,
                        CertificateSource.TIMESTAMP).getSet(),
                TimeBasedContexts.of(TimeBasedContext.HISTORICAL).getSet(),
                p -> p.setFreshness(Duration.ofDays(15)));

        sut.setParameterValueFor(ValidatorContexts.all().getSet(),
                CertificateSources.all().getSet(),
                TimeBasedContexts.all().getSet(),
                p -> p.setFreshness(Duration.ofDays(25)));

        // test the general default
        Assertions.assertEquals(Duration.ofDays(25),
                sut.getParametersValueFor(ValidatorContext.OCSP_VALIDATOR, CertificateSource.SIGNER_CERT,
                        TimeBasedContext.PRESENT,
                        (p -> p.getFreshness())));
    }

    @Test
    public void setAndGetFreshnessTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();
        sut.setFreshness(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR),
                CertificateSources.of(CertificateSource.CERT_ISSUER),
                TimeBasedContexts.of(TimeBasedContext.HISTORICAL), Duration.ofDays(-10));
        Assertions.assertEquals(Duration.ofDays(-10),
                sut.getFreshness(
                        new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                                TimeBasedContext.HISTORICAL)));

        Assertions.assertEquals(SignatureValidationProperties.DEFAULT_FRESHNESS_PRESENT_CRL,
                sut.getFreshness(
                        new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                                TimeBasedContext.PRESENT)));
    }

    @Test
    public void setAndGetContinueAfterFailure() {
        SignatureValidationProperties sut = new SignatureValidationProperties();
        sut.setContinueAfterFailure(ValidatorContexts.of(ValidatorContext.SIGNATURE_VALIDATOR),
                CertificateSources.of(CertificateSource.CERT_ISSUER), true);
        sut.setContinueAfterFailure(ValidatorContexts.of(ValidatorContext.SIGNATURE_VALIDATOR),
                CertificateSources.of(CertificateSource.OCSP_ISSUER), false);

        Assertions.assertEquals(Boolean.TRUE, sut.getContinueAfterFailure(
                new ValidationContext(ValidatorContext.SIGNATURE_VALIDATOR, CertificateSource.CERT_ISSUER,
                        TimeBasedContext.PRESENT)));
        Assertions.assertEquals(Boolean.FALSE, sut.getContinueAfterFailure(
                new ValidationContext(ValidatorContext.SIGNATURE_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.PRESENT)));
    }

    @Test
    public void setRevocationOnlineFetchingTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();
        sut.setRevocationOnlineFetching(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR), CertificateSources.all(),
                TimeBasedContexts.of(TimeBasedContext.PRESENT),
                SignatureValidationProperties.OnlineFetching.ALWAYS_FETCH);
        Assertions.assertEquals(SignatureValidationProperties.DEFAULT_ONLINE_FETCHING, sut.getRevocationOnlineFetching(
                new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.HISTORICAL)));
        Assertions.assertEquals(SignatureValidationProperties.DEFAULT_ONLINE_FETCHING, sut.getRevocationOnlineFetching(
                new ValidationContext(ValidatorContext.OCSP_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.PRESENT)));
        Assertions.assertEquals(SignatureValidationProperties.OnlineFetching.ALWAYS_FETCH, sut.getRevocationOnlineFetching(
                new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.PRESENT)));
    }

    @Test
    public void setRequiredExtensionsTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();
        sut.setRequiredExtensions(CertificateSources.all(),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(1)));
        sut.setRequiredExtensions(CertificateSources.of(CertificateSource.CERT_ISSUER),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(2)));
        sut.setRequiredExtensions(CertificateSources.of(CertificateSource.OCSP_ISSUER),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(3)));

        Assertions.assertEquals(Collections.singletonList(new KeyUsageExtension(1)),
                sut.getRequiredExtensions(new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                        CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT)));
        Assertions.assertEquals(Collections.singletonList(new KeyUsageExtension(2)), sut.getRequiredExtensions(
                new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                        CertificateSource.CERT_ISSUER, TimeBasedContext.PRESENT)));
        Assertions.assertEquals(Collections.singletonList(new KeyUsageExtension(3)), sut.getRequiredExtensions(
                new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                        CertificateSource.OCSP_ISSUER, TimeBasedContext.HISTORICAL)));
    }

    @Test
    public void addRequiredExtensionsTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();
        sut.addRequiredExtensions(CertificateSources.all(),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(1)));
        sut.addRequiredExtensions(CertificateSources.of(CertificateSource.CRL_ISSUER),
                Collections.<CertificateExtension>singletonList(new KeyUsageExtension(2)));

        List<CertificateExtension> expectedExtensionsSigner = Collections.<CertificateExtension>singletonList(new KeyUsageExtension(1));
        List<CertificateExtension> expectedExtensionsCrlIssuer = new ArrayList<>();
        expectedExtensionsCrlIssuer.add(new KeyUsageExtension(KeyUsage.CRL_SIGN));
        expectedExtensionsCrlIssuer.add(new KeyUsageExtension(1));
        expectedExtensionsCrlIssuer.add(new KeyUsageExtension(2));
        Assertions.assertEquals(expectedExtensionsSigner,
                sut.getRequiredExtensions(new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                        CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT)));
        Assertions.assertEquals(expectedExtensionsCrlIssuer,
                sut.getRequiredExtensions(new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                        CertificateSource.CRL_ISSUER, TimeBasedContext.HISTORICAL)));
    }

    private static class IncrementalFreshnessValueSetter {
        private final int increment;
        private int value;

        public IncrementalFreshnessValueSetter(int initialValue, int increment) {
            this.value = initialValue;
            this.increment = increment;
        }

        public Consumer<ContextProperties> getAction() {
            return p -> {
                p.setFreshness(Duration.ofDays(value));
                value += increment;
            };
        }
    }
}