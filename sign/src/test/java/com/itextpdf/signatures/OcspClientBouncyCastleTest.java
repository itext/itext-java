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
package com.itextpdf.signatures;

import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.OCSPRespBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

@Category(UnitTest.class)
public class OcspClientBouncyCastleTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpass".toCharArray();
    private static final String caCertFileName = certsSrc + "rootRsa.p12";

    private static X509Certificate checkCert;
    private static X509Certificate rootCert;
    private static TestOcspResponseBuilder builder;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Before
    public void setUp() throws Exception {
        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, password)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, password, password);
        builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        checkCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(certsSrc + "signCertRsa01.p12", password)[0];
        rootCert = builder.getIssuerCert();
    }

    @Test
    public void getBasicOCSPRespTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = createOcspClient();

        BasicOCSPResp basicOCSPResp = ocspClientBouncyCastle.getBasicOCSPResp(checkCert, rootCert, null);
        Assert.assertNotNull(basicOCSPResp);
        Assert.assertTrue(basicOCSPResp.getResponses().length > 0);
    }

    @Test
    public void getBasicOCSPRespNullTest() {
        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        OcspClientBouncyCastle ocspClientBouncyCastle = new OcspClientBouncyCastle(ocspVerifier);

        BasicOCSPResp basicOCSPResp = ocspClientBouncyCastle.getBasicOCSPResp(checkCert, null, null);
        Assert.assertNull(basicOCSPResp);
    }

    @Test
    @LogMessages(messages =
    @LogMessage(messageTemplate = "OCSP response could not be verified"))
    public void getBasicOCSPRespLogMessageTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = createOcspClient();

        BasicOCSPResp basicOCSPResp = ocspClientBouncyCastle.getBasicOCSPResp(null, null, null);
        Assert.assertNull(basicOCSPResp);
    }

    @Test
    public void getEncodedTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = createOcspClient();

        byte[] encoded = ocspClientBouncyCastle.getEncoded(checkCert, rootCert, null);
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.length > 0);
    }

    private static OcspClientBouncyCastle createOcspClient() {
        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        return new TestOcspClientBouncyCastle(ocspVerifier);
    }

    private static final class TestOcspClientBouncyCastle extends OcspClientBouncyCastle {
        public TestOcspClientBouncyCastle(OCSPVerifier verifier) {
            super(verifier);
        }

        @Override
        OCSPResp getOcspResponse(X509Certificate chCert, X509Certificate rCert, String url) throws OCSPException {
            try {
                CertificateID id = SignTestPortUtil.generateCertificateId(rootCert, checkCert.getSerialNumber(),
                        CertificateID.HASH_SHA1);
                BasicOCSPResp basicOCSPResp = builder.makeOcspResponseObject(SignTestPortUtil
                        .generateOcspRequestWithNonce(id).getEncoded());
                return new OCSPRespBuilder().build(OCSPRespBuilder.SUCCESSFUL, basicOCSPResp);
            } catch (Exception e) {
                throw new OCSPException(e.getMessage());
            }
        }
    }
}
