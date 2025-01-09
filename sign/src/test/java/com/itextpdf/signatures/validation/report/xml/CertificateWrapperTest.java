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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.signatures.testutils.PemFileHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Tag("BouncyCastleUnitTest")
public class CertificateWrapperTest extends AbstractCollectableObjectTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static X509Certificate cert1;
    private static X509Certificate cert2;

    @BeforeAll
    public static void setUpFixture() throws CertificateException, IOException {
        cert1 = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "root.pem")[0];
        cert2 = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "signCertDsa01.pem")[0];
    }

    @Test
    public void testEqualInstancesHaveUniqueIds() {
        CertificateWrapper sut1 = new CertificateWrapper(cert1);
        CertificateWrapper sut2 = new CertificateWrapper(cert1);

        Assertions.assertNotEquals(sut1.getIdentifier().getId(), sut2.getIdentifier().getId());
    }

    @Override
    protected void performTestHashForEqualInstances() {
        CertificateWrapper sut1 = new CertificateWrapper(cert1);
        CertificateWrapper sut2 = new CertificateWrapper(cert1);

        Assertions.assertEquals(sut1.hashCode(), sut2.hashCode());
    }

    @Override
    protected void performTestEqualsForEqualInstances() {
        CertificateWrapper sut1 = new CertificateWrapper(cert1);
        CertificateWrapper sut2 = new CertificateWrapper(cert1);

        Assertions.assertEquals(sut1, sut2);
    }

    @Override
    protected void performTestEqualsForDifferentInstances() {
        CertificateWrapper sut1 = new CertificateWrapper(cert1);
        CertificateWrapper sut2 = new CertificateWrapper(cert2);

        Assertions.assertNotEquals(sut1, sut2);
    }

    @Override
    protected void performTestHashForDifferentInstances() {
        CertificateWrapper sut1 = new CertificateWrapper(cert1);
        CertificateWrapper sut2 = new CertificateWrapper(cert2);

        Assertions.assertNotEquals(sut1.hashCode(), sut2.hashCode());
    }

    @Test
    public void testGetBase64ASN1Structure() throws CertificateException, IOException {
        CertificateWrapper sut = new CertificateWrapper(cert1);
        IX509CertificateHolder sutCert = FACTORY.createX509CertificateHolder(
                Base64.decode(sut.getBase64ASN1Structure()));
        IX509CertificateHolder origCert = FACTORY.createX509CertificateHolder(cert1.getEncoded());
        Assertions.assertEquals(origCert, sutCert);
    }

    @Override
    AbstractCollectableObject getCollectableObjectUnderTest() {
        return new CertificateWrapper(cert1);
    }
}
