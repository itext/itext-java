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
package com.itextpdf.signatures;

import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.security.cert.X509Certificate;
import java.util.List;

@Tag("BouncyCastleUnitTest")
public class CertificateUtilTest extends ExtendedITextTest {

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    @Test
    public void getTSAURLAdobeExtensionTest() throws Exception {
        X509Certificate tsaCert =
                (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCert.pem")[0];
        String url = CertificateUtil.getTSAURL(tsaCert);

        Assertions.assertEquals("https://itextpdf.com/en", url);
    }

    @Test
    public void getTSAURLUsualTimestampCertificateTest() throws Exception {
        X509Certificate tsaCert =
                (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "tsCertRsa.pem")[0];
        String url = CertificateUtil.getTSAURL(tsaCert);

        Assertions.assertNull(url);
    }

    @Test
    public void getTSAURLAdobeExtensionNotTaggedTest() throws Exception {
        X509Certificate tsaCert = (X509Certificate)
                PemFileHelper.readFirstChain(CERTS_SRC + "adobeExtensionCertWithoutTag.pem")[0];

        Assertions.assertThrows(NullPointerException.class, () -> CertificateUtil.getTSAURL(tsaCert));
    }
    
    @Test
    public void getCRLFromStringNullTest() throws CertificateException, CRLException, IOException {
        Assertions.assertNull(CertificateUtil.getCRL((String) null));
    }

    @Test
    public void getCRLsFromCertificateWithoutCRLTest() throws IOException, CertificateException, CRLException {
        X509Certificate tsaCert =
                (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "rootRsa.pem")[0];
        List<CRL> crls = CertificateUtil.getCRLs(tsaCert);

        Assertions.assertTrue(crls.isEmpty());
    }
}
