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

import com.itextpdf.eutrustedlistsresources.EuropeanTrustedListConfiguration;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.security.cert.Certificate;

@Tag("IntegrationTest")
public class EuropeanTrustedCertificatesResourceLoaderTest extends ExtendedITextTest {

    @Test
    public void loadCertificates() {
        EuropeanTrustedCertificatesResourceLoader loader = new EuropeanTrustedCertificatesResourceLoader(
                new EuropeanTrustedListConfiguration());
        Assertions.assertNotNull(loader.loadCertificates(), "Certificate C should not be null");
        Assertions.assertFalse(loader.loadCertificates().isEmpty(), "There should be certificates loaded");
    }

    @Test
    public void verifyHashValid() {
        EuropeanTrustedListConfiguration config = new EuropeanTrustedListConfiguration();
        EuropeanTrustedListConfiguration.PemCertificateWithHash hash = config.getCertificates().get(0);

        EuropeanTrustedCertificatesResourceLoader loader = new EuropeanTrustedCertificatesResourceLoader(
                new EuropeanTrustedListConfiguration());
        Certificate c = loader.loadCertificates().get(0);

        String expectedHash = hash.getHash();
        AssertUtil.doesNotThrow(() -> {
            EuropeanTrustedCertificatesResourceLoader.verifyCertificate(expectedHash, c);
        }, "The certificate should be verified successfully with the expected hash");

    }

    @Test
    public void verifyHashNullInValid() {
        EuropeanTrustedCertificatesResourceLoader loader = new EuropeanTrustedCertificatesResourceLoader(
                new EuropeanTrustedListConfiguration());
        Certificate c = loader.loadCertificates().get(0);
        Exception e = Assertions.assertThrows((PdfException.class), () -> {
            EuropeanTrustedCertificatesResourceLoader.verifyCertificate(null, c);
        });
        Assertions.assertEquals(SignExceptionMessageConstant.CERTIFICATE_HASH_NULL, e.getMessage());
    }

    @Test
    public void verifyHashRandomStringInValid() {
        EuropeanTrustedCertificatesResourceLoader loader = new EuropeanTrustedCertificatesResourceLoader(
                new EuropeanTrustedListConfiguration());
        Certificate c = loader.loadCertificates().get(0);
        Assertions.assertThrows((Exception.class), () -> {
            EuropeanTrustedCertificatesResourceLoader.verifyCertificate("aklsjaslkfdjaslkfdj", c);
        });
    }


    @Test
    public void verifyHashInvalidBase64InValid() {
        EuropeanTrustedCertificatesResourceLoader loader = new EuropeanTrustedCertificatesResourceLoader(
                new EuropeanTrustedListConfiguration());
        Certificate c = loader.loadCertificates().get(0);
        Assertions.assertThrows((Exception.class), () -> {
            EuropeanTrustedCertificatesResourceLoader.verifyCertificate("**dsf sdfs fsdf @@", c);
        });
    }
}