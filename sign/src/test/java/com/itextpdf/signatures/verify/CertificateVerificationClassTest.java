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
package com.itextpdf.signatures.verify;

import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TimeStampToken;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CertificateVerificationClassTest extends ExtendedITextTest {

    // Such messageTemplate is equal to any log message. This is required for porting reasons.
    private static final String ANY_LOG_MESSAGE = "{0}";

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
        ITextTest.removeCryptographyRestrictions();
    }

    @AfterClass
    public static void after() {
        ITextTest.restoreCryptographyRestrictions();
    }

    @Test
    public void validCertificateChain01() throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchProviderException {
        Certificate[] certChain = Pkcs12FileHelper.readFirstChain(certsSrc + "signCertRsaWithChain.p12", password);

        String caCertFileName = certsSrc + "rootRsa.p12";
        KeyStore caKeyStore = Pkcs12FileHelper.initStore(caCertFileName, password);

        List<VerificationException> verificationExceptions = CertificateVerification.verifyCertificates(certChain, caKeyStore);

        Assert.assertTrue(verificationExceptions.isEmpty());
    }

    @Test
    public void timestampCertificateAndKeyStoreCorrespondTest() throws Exception {
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";

        KeyStore caKeyStore = Pkcs12FileHelper.initStore(tsaCertFileName, password);

        Assert.assertTrue(verifyTimestampCertificates(tsaCertFileName, caKeyStore));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = "certificate hash does not match certID hash."))
    public void timestampCertificateAndKeyStoreDoNotCorrespondTest() throws Exception {
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";
        String notTsaCertFileName = certsSrc + "rootRsa.p12";

        KeyStore caKeyStore = Pkcs12FileHelper.initStore(notTsaCertFileName, password);

        Assert.assertFalse(verifyTimestampCertificates(tsaCertFileName, caKeyStore));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = ANY_LOG_MESSAGE))
    public void keyStoreWithoutCertificatesTest() throws Exception {
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";

        Assert.assertFalse(verifyTimestampCertificates(tsaCertFileName, null));
    }

    private static boolean verifyTimestampCertificates(String tsaClientCertificate, KeyStore caKeyStore) throws Exception {
        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaClientCertificate, password);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaClientCertificate, password, password);

        TestTsaClient testTsaClient = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        byte[] tsaCertificateBytes = testTsaClient.getTimeStampToken(testTsaClient.getMessageDigest().digest());
        TimeStampToken timeStampToken = new TimeStampToken(
                ContentInfo.getInstance(ASN1Sequence.getInstance(tsaCertificateBytes)));

        return CertificateVerification.verifyTimestampCertificates(timeStampToken, caKeyStore, null);
    }
}
