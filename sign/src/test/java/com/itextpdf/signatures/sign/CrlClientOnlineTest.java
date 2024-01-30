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
package com.itextpdf.signatures.sign;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.testutils.X509MockCertificate;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class CrlClientOnlineTest extends ExtendedITextTest {

    private static final String certSrc = "./src/test/resources/com/itextpdf/signatures/sign/CrlClientOnlineTest/";
    private static final String certWithMalformedUrl = certSrc + "certWithMalformedUrl.crt";
    private static final String certWithCorrectUrl = certSrc + "certWithCorrectUrl.crt";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/";

    @Test
    public void crlClientOnlineURLConstructorTest() throws MalformedURLException {

        String PROTOCOL = "file://";
        URL[] urls = new URL[] {
                new URL(PROTOCOL + destinationFolder + "duplicateFolder"),
                new URL(PROTOCOL + destinationFolder + "duplicateFolder"),
                new URL(PROTOCOL + destinationFolder + "uniqueFolder"),
        };
        CrlClientOnline crlClientOnline = new CrlClientOnline(urls);

        Assert.assertEquals(2, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: https://examples.com", logLevel = LogLevelConstants.INFO),
    })
    public void addCrlUrlTest() {
        CrlClientOnline crlClientOnline = new CrlClientOnline("https://examples.com");
        Assert.assertEquals(1, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Skipped CRL url (malformed):", logLevel = LogLevelConstants.INFO),
    })
    public void addEmptyCrlUrlTest() {
        CrlClientOnline crlClientOnline = new CrlClientOnline("");
        Assert.assertEquals(0, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Skipped CRL url (malformed):", logLevel = LogLevelConstants.INFO),
    })
    public void addWrongCrlUrlTest() {
        CrlClientOnline crlClientOnline = new CrlClientOnline("test");
        Assert.assertEquals(0, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Checking certificate: ", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url (malformed): test",
                    logLevel = LogLevelConstants.INFO)
    })
    public void checkCrlCertWithMalformedUrlTest() throws CertificateException, FileNotFoundException {
        Certificate chain = CryptoUtil.readPublicCertificate(new FileInputStream(certWithMalformedUrl));
        CrlClientOnline crlClientOnline = new CrlClientOnline(new Certificate[] {chain});
        Assert.assertEquals(0, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Checking certificate: ", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Added CRL url: http://www.example.com/crl/test.crl",
                    logLevel = LogLevelConstants.INFO)
    })
    public void checkCrlCertWithCorrectUrlTest() throws CertificateException, FileNotFoundException {
        Certificate chain = CryptoUtil.readPublicCertificate(new FileInputStream(certWithCorrectUrl));
        CrlClientOnline crlClientOnline = new CrlClientOnline(new Certificate[] {chain});
        Assert.assertEquals(1, crlClientOnline.getUrlsSize());
    }

    @Test
    public void cannotGetEncodedWhenCertIsNullTest() throws CertificateEncodingException, IOException {
        CrlClientOnline crlClientOnline = new CrlClientOnline();
        Assert.assertNull(crlClientOnline.getEncoded(null, ""));
        Assert.assertEquals(0, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: http://www.example.com/crl/test.crl", logLevel =
                    LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://www.example.com/crl/test.crl", logLevel =
                    LogLevelConstants.INFO),
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_DISTRIBUTION_POINT, logLevel =
                    LogLevelConstants.INFO)
    })
    public void unreachableCrlDistributionPointTest() throws CertificateEncodingException, IOException {
        CrlClientOnline crlClientOnline = new CrlClientOnline("http://www.example.com/crl/test.crl");
        X509Certificate checkCert = new X509MockCertificate();
        Collection<byte[]> bytes = crlClientOnline.getEncoded(checkCert, "http://www.example.com/crl/test.crl");
        Assert.assertTrue(bytes.isEmpty());
        Assert.assertEquals(1, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate ", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Found CRL url: http://www.example.com/crl/test.crl", logLevel =
                    LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Checking CRL: http://www.example.com/crl/test.crl", logLevel =
                    LogLevelConstants.INFO),
            @LogMessage(messageTemplate = IoLogMessageConstant.INVALID_DISTRIBUTION_POINT, logLevel =
                    LogLevelConstants.INFO)
    })
    public void unreachableCrlDistributionPointFromCertChainTest() throws CertificateEncodingException, IOException {
        CrlClientOnline crlClientOnline = new CrlClientOnline();
        X509Certificate checkCert = new X509MockCertificate();
        Collection<byte[]> bytes = crlClientOnline.getEncoded(checkCert, "http://www.example.com/crl/test.crl");
        Assert.assertTrue(bytes.isEmpty());
        Assert.assertEquals(0, crlClientOnline.getUrlsSize());
    }
}
