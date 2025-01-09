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
package com.itextpdf.signatures.validation.extensions;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class KeyUsageExtensionTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/validation/extensions/KeyUsageExtensionTest/";

    @Test
    public void keyUsageNotSetExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(0);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageNotSetNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(8);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageNotSetButConfiguredToReturnFalseTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(8, true);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageKeyCertSignExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageKeyCertSignCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.KEY_CERT_SIGN);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageKeyCertSignPartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageKeyCertSignCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN));

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageKeyCertSignNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageKeyCertSignCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.CRL_SIGN);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageDigitalSignatureTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageDigitalSignatureCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.DIGITAL_SIGNATURE);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageDecipherOnlyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageDecipherOnlyCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.DECIPHER_ONLY);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageDecipherOnlyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageDecipherOnlyCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(KeyUsage.ENCIPHER_ONLY);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys1PartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.CRL_SIGN, KeyUsage.NON_REPUDIATION));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys1ExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.CRL_SIGN,
                KeyUsage.NON_REPUDIATION, KeyUsage.KEY_ENCIPHERMENT));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys1PartiallyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.CRL_SIGN, KeyUsage.DECIPHER_ONLY,
                KeyUsage.KEY_ENCIPHERMENT));

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys2PartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys2Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.DECIPHER_ONLY, KeyUsage.DIGITAL_SIGNATURE));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys2ExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys2Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.DECIPHER_ONLY,
                KeyUsage.DIGITAL_SIGNATURE, KeyUsage.KEY_AGREEMENT));

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageSeveralKeys2PartiallyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys2Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        KeyUsageExtension extension = new KeyUsageExtension(Arrays.asList(KeyUsage.CRL_SIGN, KeyUsage.DECIPHER_ONLY,
                KeyUsage.DIGITAL_SIGNATURE));

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }
}
