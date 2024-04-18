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

import com.itextpdf.signatures.validation.v1.SignatureValidationProperties.ContextProperties;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.CertificateSources;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContexts;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContexts;
import com.itextpdf.signatures.validation.v1.extensions.CertificateExtension;
import com.itextpdf.signatures.validation.v1.extensions.KeyUsageExtension;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Duration;
import java.util.Collections;
import java.util.function.Consumer;

@Category(UnitTest.class)
public class SignatureValidationPropertiesTest extends ExtendedITextTest {
    @Before
    public void setUp() {
    }

    @Test
    public void getParametersValueForSpecificTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();

        sut.setParameterValueFor(ValidatorContexts.of(ValidatorContext.OCSP_VALIDATOR, ValidatorContext.CRL_VALIDATOR,
                        ValidatorContext.SIGNATURE_VALIDATOR).getSet(),
                CertificateSources.of(CertificateSource.CRL_ISSUER, CertificateSource.SIGNER_CERT,
                        CertificateSource.TIMESTAMP).getSet(), TimeBasedContexts.of(TimeBasedContext.HISTORICAL).getSet(),
                new IncrementralFreshnessValueSetter(10, 1).getAction());

        // test the last value added
        Assert.assertEquals(Duration.ofDays(18),
                sut.getParametersValueFor(ValidatorContext.SIGNATURE_VALIDATOR, CertificateSource.TIMESTAMP,
                        TimeBasedContext.HISTORICAL,
                        (p -> p.getFreshness())));

        //test the fifth value added
        Assert.assertEquals(Duration.ofDays(14),
                sut.getParametersValueFor(ValidatorContext.CRL_VALIDATOR, CertificateSource.SIGNER_CERT,
                        TimeBasedContext.HISTORICAL,
                        (p -> p.getFreshness())));

        // test the general default
        Assert.assertEquals(SignatureValidationProperties.DEFAULT_FRESHNESS_HISTORICAL,
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
                new IncrementralFreshnessValueSetter(10, 1).getAction());

        // test the general default
        Assert.assertEquals(SignatureValidationProperties.DEFAULT_FRESHNESS_PRESENT_OCSP,
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
        Assert.assertEquals(Duration.ofDays(25),
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
        Assert.assertEquals(Duration.ofDays(-10),
                sut.getFreshness(
                        new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                                TimeBasedContext.HISTORICAL)));

        Assert.assertEquals(SignatureValidationProperties.DEFAULT_FRESHNESS_PRESENT_CRL,
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

        Assert.assertEquals(Boolean.TRUE, sut.getContinueAfterFailure(
                new ValidationContext(ValidatorContext.SIGNATURE_VALIDATOR, CertificateSource.CERT_ISSUER,
                        TimeBasedContext.PRESENT)));
        Assert.assertEquals(Boolean.FALSE, sut.getContinueAfterFailure(
                new ValidationContext(ValidatorContext.SIGNATURE_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.PRESENT)));
    }

    @Test
    public void setRevocationOnlineFetchingTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();
        sut.setRevocationOnlineFetching(ValidatorContexts.of(ValidatorContext.CRL_VALIDATOR), CertificateSources.all(),
                TimeBasedContexts.of(TimeBasedContext.PRESENT),
                SignatureValidationProperties.OnlineFetching.ALWAYS_FETCH);
        Assert.assertEquals(SignatureValidationProperties.DEFAULT_ONLINE_FETCHING, sut.getRevocationOnlineFetching(
                new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.HISTORICAL)));
        Assert.assertEquals(SignatureValidationProperties.DEFAULT_ONLINE_FETCHING, sut.getRevocationOnlineFetching(
                new ValidationContext(ValidatorContext.OCSP_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.PRESENT)));
        Assert.assertEquals(SignatureValidationProperties.OnlineFetching.ALWAYS_FETCH, sut.getRevocationOnlineFetching(
                new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.OCSP_ISSUER,
                        TimeBasedContext.PRESENT)));
    }

    @Test
    public void setRequiredExtensionsTest() {
        SignatureValidationProperties sut = new SignatureValidationProperties();
        sut.setRequiredExtensions(CertificateSources.all(),
                Collections.<CertificateExtension> singletonList(new KeyUsageExtension(1)));
        sut.setRequiredExtensions(CertificateSources.of(CertificateSource.CERT_ISSUER),
                Collections.<CertificateExtension> singletonList(new KeyUsageExtension(2)));
        sut.setRequiredExtensions(CertificateSources.of(CertificateSource.OCSP_ISSUER),
                Collections.<CertificateExtension> singletonList(new KeyUsageExtension(3)));

        Assert.assertEquals(Collections.singletonList(new KeyUsageExtension(1)),
                sut.getRequiredExtensions(new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                        CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT)));
        Assert.assertEquals(Collections.singletonList(new KeyUsageExtension(2)), sut.getRequiredExtensions(
                new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                        CertificateSource.CERT_ISSUER, TimeBasedContext.PRESENT)));
        Assert.assertEquals(Collections.singletonList(new KeyUsageExtension(3)), sut.getRequiredExtensions(
                new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                        CertificateSource.OCSP_ISSUER, TimeBasedContext.HISTORICAL)));
    }

    private static class IncrementralFreshnessValueSetter {
        private int value;
        private final int increment;

        public IncrementralFreshnessValueSetter(int initialValue, int increment) {
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