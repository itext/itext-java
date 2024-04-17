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
package com.itextpdf.signatures.validation.v1.context;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ValidationContextTest extends ExtendedITextTest {
    @Test
    public void testInitializingConstructor() {
        ValidationContext sut = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        Assert.assertEquals(ValidatorContext.CRL_VALIDATOR, sut.getValidatorContext());
        Assert.assertEquals(CertificateSource.CERT_ISSUER, sut.getCertificateSource());
        Assert.assertEquals(TimeBasedContext.HISTORICAL, sut.getTimeBasedContext());
    }

    @Test
    public void testSetAndGetCertificateSource() {
        ValidationContext sut = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        sut = sut.setCertificateSource(CertificateSource.CRL_ISSUER);
        Assert.assertEquals(CertificateSource.CRL_ISSUER, sut.getCertificateSource());
    }

    @Test
    public void testSetAndGetTemporalContext() {
        ValidationContext sut = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        sut = sut.setTimeBasedContext(TimeBasedContext.PRESENT);
        Assert.assertEquals(TimeBasedContext.PRESENT, sut.getTimeBasedContext());
    }

    @Test
    public void testSetAndGetValidator() {
        ValidationContext sut = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        sut = sut.setValidatorContext(ValidatorContext.SIGNATURE_VALIDATOR);
        Assert.assertEquals(ValidatorContext.SIGNATURE_VALIDATOR, sut.getValidatorContext());
    }

    @Test
    public void testEquals() {
        ValidationContext sutA = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        ValidationContext sutB = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        Assert.assertEquals(sutA, sutB);
        Assert.assertEquals(sutB, sutA);
    }

    @Test
    public void testNotEquals() {
        ValidationContext sutA = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        ValidationContext sutB = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                CertificateSource.CERT_ISSUER, TimeBasedContext.HISTORICAL);
        ValidationContext sutC = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.OCSP_ISSUER,
                TimeBasedContext.HISTORICAL);
        ValidationContext sutD = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.PRESENT);
        Assert.assertNotEquals(sutA, sutB);
        Assert.assertNotEquals(sutB, sutA);
        Assert.assertNotEquals(sutC, sutA);
        Assert.assertNotEquals(sutD, sutA);
    }

    @Test
    public void testHashCode() {
        ValidationContext sut0 = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        ValidationContext sutA = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);
        ValidationContext sutB = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                CertificateSource.CERT_ISSUER, TimeBasedContext.HISTORICAL);
        ValidationContext sutC = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.OCSP_ISSUER,
                TimeBasedContext.HISTORICAL);
        ValidationContext sutD = new ValidationContext(ValidatorContext.CRL_VALIDATOR, CertificateSource.CERT_ISSUER,
                TimeBasedContext.PRESENT);

        Assert.assertEquals(sutA.hashCode(), sut0.hashCode());
        Assert.assertNotEquals(sutA.hashCode(), sutB.hashCode());
        Assert.assertNotEquals(sutA.hashCode(), sutC.hashCode());
        Assert.assertNotEquals(sutA.hashCode(), sutD.hashCode());
    }

    @Test
    public void hashCodeTest() {
        ValidationContext vc1 = new ValidationContext(ValidatorContext.OCSP_VALIDATOR,
                CertificateSource.OCSP_ISSUER,
                TimeBasedContext.HISTORICAL);

        ValidationContext vc2 = new ValidationContext(ValidatorContext.OCSP_VALIDATOR,
                CertificateSource.OCSP_ISSUER,
                TimeBasedContext.HISTORICAL);

        ValidationContext vc3 = new ValidationContext(ValidatorContext.CERTIFICATE_CHAIN_VALIDATOR,
                CertificateSource.OCSP_ISSUER,
                TimeBasedContext.HISTORICAL);

        ValidationContext vc4 = new ValidationContext(ValidatorContext.OCSP_VALIDATOR,
                CertificateSource.CERT_ISSUER,
                TimeBasedContext.HISTORICAL);

        ValidationContext vc5 = new ValidationContext(ValidatorContext.OCSP_VALIDATOR,
                CertificateSource.OCSP_ISSUER,
                TimeBasedContext.PRESENT);
        Assert.assertEquals(vc1.hashCode(), vc2.hashCode());
        Assert.assertNotEquals(vc1.hashCode(), vc3.hashCode());
        Assert.assertNotEquals(vc1.hashCode(), vc4.hashCode());
        Assert.assertNotEquals(vc1.hashCode(), vc5.hashCode());
    }
}