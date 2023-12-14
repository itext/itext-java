/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
