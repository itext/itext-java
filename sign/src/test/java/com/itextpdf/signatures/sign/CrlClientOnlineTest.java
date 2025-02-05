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
package com.itextpdf.signatures.sign;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.X509MockCertificate;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class CrlClientOnlineTest extends ExtendedITextTest {

    private static final String certSrc = "./src/test/resources/com/itextpdf/signatures/sign/CrlClientOnlineTest/";
    private static final String certWithMalformedUrl = certSrc + "certWithMalformedUrl.crt";
    private static final String certWithCorrectUrl = certSrc + "certWithCorrectUrl.crt";
    private static final String chainWithSeveralUrls = certSrc + "chainWithSeveralUrls.pem";
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

        Assertions.assertEquals(2, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Added CRL url: https://examples.com", logLevel = LogLevelConstants.INFO),
    })
    public void addCrlUrlTest() {
        CrlClientOnline crlClientOnline = new CrlClientOnline("https://examples.com");
        Assertions.assertEquals(1, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Skipped CRL url (malformed):", logLevel = LogLevelConstants.INFO),
    })
    public void addEmptyCrlUrlTest() {
        CrlClientOnline crlClientOnline = new CrlClientOnline("");
        Assertions.assertEquals(0, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Skipped CRL url (malformed):", logLevel = LogLevelConstants.INFO),
    })
    public void addWrongCrlUrlTest() {
        CrlClientOnline crlClientOnline = new CrlClientOnline("test");
        Assertions.assertEquals(0, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Checking certificate: ", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Skipped CRL url (malformed): test",
                    logLevel = LogLevelConstants.INFO)
    })
    public void checkCrlCertWithMalformedUrlTest() throws CertificateException, IOException {
        Certificate chain = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(certWithMalformedUrl));
        CrlClientOnline crlClientOnline = new CrlClientOnline(new Certificate[] {chain});
        Assertions.assertEquals(0, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Checking certificate: ", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Added CRL url: http://www.example.com/crl/test.crl",
                    logLevel = LogLevelConstants.INFO)
    })
    public void checkCrlCertWithCorrectUrlTest() throws CertificateException, IOException {
        Certificate chain = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(certWithCorrectUrl));
        CrlClientOnline crlClientOnline = new CrlClientOnline(new Certificate[] {chain});
        Assertions.assertEquals(1, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Checking certificate: ", logLevel = LogLevelConstants.INFO, count = 2),
            @LogMessage(messageTemplate = "Added CRL url: ", logLevel = LogLevelConstants.INFO, count = 4)
    })
    public void checkCrlCertWithSeveralUrlsTest() throws CertificateException, IOException {
        // Root certificate with 1 CRL and leaf certificate with 3 CRLs in 3 Distribution Points.
        Certificate[] chain = PemFileHelper.readFirstChain(chainWithSeveralUrls);
        CrlClientOnline crlClientOnline = new CrlClientOnline(chain);
        Assertions.assertEquals(4, crlClientOnline.getUrlsSize());
    }

    @Test
    public void cannotGetEncodedWhenCertIsNullTest() throws CertificateEncodingException {
        CrlClientOnline crlClientOnline = new CrlClientOnline();
        Assertions.assertNull(crlClientOnline.getEncoded(null, ""));
        Assertions.assertEquals(0, crlClientOnline.getUrlsSize());
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Looking for CRL for certificate ", logLevel = LogLevelConstants.INFO),
            @LogMessage(messageTemplate = "Found CRL url: ", logLevel = LogLevelConstants.INFO, count = 3),
            @LogMessage(messageTemplate = "Checking CRL: ", logLevel = LogLevelConstants.INFO, count = 3),
            @LogMessage(messageTemplate = "Added CRL found at: ", logLevel = LogLevelConstants.INFO, count = 3)
    })
    public void unreachableSeveralCrlDistributionPointsFromTheCertChainTest() throws CertificateException, IOException {
        CrlClientOnline crlClientOnline = new CrlClientOnline() {
            @Override
            protected InputStream getCrlResponse(X509Certificate cert, URL url) {
                return new ByteArrayInputStream(new byte[0]);
            }
        };
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(chainWithSeveralUrls)[1];
        Collection<byte[]> bytes = crlClientOnline.getEncoded(checkCert, null);
        Assertions.assertEquals(3, bytes.size());
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
    public void unreachableCrlDistributionPointTest() throws CertificateEncodingException {
        CrlClientOnline crlClientOnline = new CrlClientOnline("http://www.example.com/crl/test.crl");
        X509Certificate checkCert = new X509MockCertificate();
        Collection<byte[]> bytes = crlClientOnline.getEncoded(checkCert, "http://www.example.com/crl/test.crl");
        Assertions.assertTrue(bytes.isEmpty());
        Assertions.assertEquals(1, crlClientOnline.getUrlsSize());
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
    public void unreachableCrlDistributionPointFromCertChainTest() throws CertificateEncodingException {
        CrlClientOnline crlClientOnline = new CrlClientOnline();
        X509Certificate checkCert = new X509MockCertificate();
        Collection<byte[]> bytes = crlClientOnline.getEncoded(checkCert, "http://www.example.com/crl/test.crl");
        Assertions.assertTrue(bytes.isEmpty());
        Assertions.assertEquals(0, crlClientOnline.getUrlsSize());
    }
}
