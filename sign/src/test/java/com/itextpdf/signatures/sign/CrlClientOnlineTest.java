/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures.sign;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.signatures.CrlClientOnline;
import com.itextpdf.signatures.testutils.X509MockCertificate;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
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
    public void cannotGetEncodedWhenCertIsNullTest() {
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
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_DISTRIBUTION_POINT, logLevel =
                    LogLevelConstants.INFO)
    })
    public void unreachableCrlDistributionPointTest() {
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
            @LogMessage(messageTemplate = LogMessageConstant.INVALID_DISTRIBUTION_POINT, logLevel =
                    LogLevelConstants.INFO)
    })
    public void unreachableCrlDistributionPointFromCertChainTest() {
        CrlClientOnline crlClientOnline = new CrlClientOnline();
        X509Certificate checkCert = new X509MockCertificate();
        Collection<byte[]> bytes = crlClientOnline.getEncoded(checkCert, "http://www.example.com/crl/test.crl");
        Assert.assertTrue(bytes.isEmpty());
        Assert.assertEquals(0, crlClientOnline.getUrlsSize());
    }
}
