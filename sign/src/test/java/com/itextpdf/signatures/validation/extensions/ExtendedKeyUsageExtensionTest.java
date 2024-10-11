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
package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class ExtendedKeyUsageExtensionTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/validation/extensions/ExtendedKeyUsageExtensionTest/";

    @Test
    public void extendedKeyUsageNotSetExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageNoSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(Collections.<String>emptyList());

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageNotSetNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageNoSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Collections.singletonList(ExtendedKeyUsageExtension.TIME_STAMPING));

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageTimestampingExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageTimeStampingCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Collections.singletonList(ExtendedKeyUsageExtension.TIME_STAMPING));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageTimestampingNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageTimeStampingCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Collections.singletonList(ExtendedKeyUsageExtension.OCSP_SIGNING));

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageOcspSigningExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageOcspSigningCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Collections.singletonList(ExtendedKeyUsageExtension.OCSP_SIGNING));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageOcspSigningNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageOcspSigningCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Collections.singletonList(ExtendedKeyUsageExtension.CODE_SIGNING));

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageAnyUsageTest1() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageAnyUsageCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Collections.singletonList(ExtendedKeyUsageExtension.CODE_SIGNING));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageAnyUsageTest2() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageAnyUsageCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Arrays.asList(ExtendedKeyUsageExtension.CODE_SIGNING, ExtendedKeyUsageExtension.OCSP_SIGNING));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageSeveralValues1PartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageSeveralValues1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Arrays.asList(ExtendedKeyUsageExtension.TIME_STAMPING, ExtendedKeyUsageExtension.OCSP_SIGNING));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageSeveralValues1PartiallyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageSeveralValues1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(Arrays.asList(
                ExtendedKeyUsageExtension.TIME_STAMPING, ExtendedKeyUsageExtension.ANY_EXTENDED_KEY_USAGE_OID));

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageSeveralValues2PartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageSeveralValues2Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Arrays.asList(ExtendedKeyUsageExtension.OCSP_SIGNING, ExtendedKeyUsageExtension.CLIENT_AUTH));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void extendedKeyUsageSeveralValues2PartiallyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "extendedKeyUsageSeveralValues2Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        ExtendedKeyUsageExtension extension = new ExtendedKeyUsageExtension(
                Arrays.asList(ExtendedKeyUsageExtension.CODE_SIGNING, ExtendedKeyUsageExtension.CLIENT_AUTH));

        // Certificate contains any_extended_key_usage OID, that's why results is always true.
        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }
}
