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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.crypto.OID.X509Extensions;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class CertificateExtensionTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/validation/extensions/CertificateExtensionTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @Test
    public void keyUsageNotSetExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, null);

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageNotSetNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageNotSetCert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(98).toASN1Primitive());

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageWrongOIDTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                X509Extensions.BASIC_CONSTRAINTS, FACTORY.createKeyUsage(98).toASN1Primitive());

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsageExpectedValueTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(98).toASN1Primitive());

        Assertions.assertTrue(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsagePartiallyExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(66).toASN1Primitive());

        // CertificateExtension#existsInCertificate only returns true in case of complete match, therefore false.
        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void keyUsagePartiallyNotExpectedTest() throws CertificateException, IOException {
        String certName = certsSrc + "keyUsageSeveralKeys1Cert.pem";
        X509Certificate certificate = (X509Certificate) PemFileHelper.readFirstChain(certName)[0];

        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        Assertions.assertFalse(extension.existsInCertificate(certificate));
    }

    @Test
    public void equalsTest() {
        CertificateExtension extension1 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        CertificateExtension extension2 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        Assertions.assertEquals(extension1, extension2);
    }

    @Test
    public void equalsOtherTypeTest() {
        CertificateExtension extension1 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        Assertions.assertNotEquals("extension1", extension1);
    }

    @Test
    public void equalsOtherExtensionTest() {
        CertificateExtension extension1 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        CertificateExtension extension2 = new CertificateExtension(
                X509Extensions.EXTENDED_KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        Assertions.assertNotEquals(extension1, extension2);
    }

    @Test
    public void equalsOtherValueTest() {
        CertificateExtension extension1 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        CertificateExtension extension2 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32800).toASN1Primitive());

        Assertions.assertNotEquals(extension1, extension2);
    }

    @Test
    public void sameHashCode() {
        CertificateExtension extension1 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        CertificateExtension extension2 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        Assertions.assertEquals(extension1.hashCode(), extension2.hashCode());
    }

    @Test
    public void hashOtherValueTest() {
        CertificateExtension extension1 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        CertificateExtension extension2 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32800).toASN1Primitive());

        Assertions.assertNotEquals(extension1.hashCode(), extension2.hashCode());
    }

    @Test
    public void hashOtherExtensionTest() {
        CertificateExtension extension1 = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        CertificateExtension extension2 = new CertificateExtension(
                X509Extensions.EXTENDED_KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());

        Assertions.assertNotEquals(extension1.hashCode(), extension2.hashCode());
    }


    @Test
    public void getExtensionValueTest() {
        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());
        Assertions.assertEquals(FACTORY.createKeyUsage(32802).toASN1Primitive(), extension.getExtensionValue());
    }

    @Test
    public void getExtensionOidTest() {
        CertificateExtension extension = new CertificateExtension(
                OID.X509Extensions.KEY_USAGE, FACTORY.createKeyUsage(32802).toASN1Primitive());
        Assertions.assertEquals(OID.X509Extensions.KEY_USAGE, extension.getExtensionOid());
    }

}
