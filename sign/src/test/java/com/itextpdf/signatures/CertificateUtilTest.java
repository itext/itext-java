/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.signatures;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.security.cert.X509Certificate;

@Category(UnitTest.class)
public class CertificateUtilTest extends ExtendedITextTest {

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] PASSWORD = "testpass".toCharArray();

    @Test
    public void getTSAURLAdobeExtensionTest() throws Exception {
        X509Certificate tsaCert =
                (X509Certificate) Pkcs12FileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCert.p12", PASSWORD)[0];
        String url = CertificateUtil.getTSAURL(tsaCert);

        Assert.assertEquals("https://itextpdf.com/en", url);
    }

    @Test
    public void getTSAURLUsualTimestampCertificateTest() throws Exception {
        X509Certificate tsaCert =
                (X509Certificate) Pkcs12FileHelper.readFirstChain(CERTS_SRC + "tsCertRsa.p12", PASSWORD)[0];
        String url = CertificateUtil.getTSAURL(tsaCert);

        Assert.assertNull(url);
    }

    @Test
    public void getTSAURLAdobeExtensionNotTaggedTest() throws Exception {
        X509Certificate tsaCert = (X509Certificate)
                Pkcs12FileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCertWithoutTag.p12", PASSWORD)[0];

        Assert.assertThrows(ClassCastException.class, () -> CertificateUtil.getTSAURL(tsaCert));
    }
}
