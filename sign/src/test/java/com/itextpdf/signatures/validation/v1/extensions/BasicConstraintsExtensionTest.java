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
package com.itextpdf.signatures.validation.v1.extensions;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class BasicConstraintsExtensionTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/validation/v1/extensions/BasicConstraintsExtensionTest/";

    @Test
    public void basicConstraintNotSetExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(-2);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintNotSetNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(10);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintMaxLengthExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsMaxCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(true);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintMaxLengthNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsMaxCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(false);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintLength10Test() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraints10Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(10);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintLength5ExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraints5Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(2);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintLength5NotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraints5Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(10);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintFalseExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsFalseCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(false);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void basicConstraintFalseNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "basicConstraintsFalseCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        BasicConstraintsExtension extension = new BasicConstraintsExtension(10);

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }
}
