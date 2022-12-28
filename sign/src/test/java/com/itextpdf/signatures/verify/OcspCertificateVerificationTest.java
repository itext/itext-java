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
package com.itextpdf.signatures.verify;

import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

@Category(UnitTest.class)
public class OcspCertificateVerificationTest extends ExtendedITextTest {

    // Such messageTemplate is equal to any log message. This is required for porting reasons.
    private static final String ANY_LOG_MESSAGE = "{0}";

    private static final String ocspCertsSrc = "./src/test/resources/com/itextpdf/signatures/verify/OcspCertificateVerificationTest/";

    private static final String rootOcspCert = ocspCertsSrc + "ocspRootRsa.p12";
    private static final String signOcspCert = ocspCertsSrc + "ocspSignRsa.p12";
    private static final String notOcspAndOcspCert = ocspCertsSrc + "notOcspAndOcspCertificates.p12";

    private static final char[] password = "testpass".toCharArray();
    private static final String ocspServiceUrl = "http://localhost:9000/demo/ocsp/ocsp-service";

    private static X509Certificate checkCert;
    private static X509Certificate rootCert;

    @BeforeClass
    public static void before() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        checkCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(signOcspCert, password)[0];
        rootCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(rootOcspCert, password)[0];
    }

    @Test
    public void keyStoreWithRootOcspCertificateTest() throws Exception {
        BasicOCSPResp response = getOcspResponse();

        Assert.assertTrue(CertificateVerification.verifyOcspCertificates(
                response, Pkcs12FileHelper.initStore(rootOcspCert, password), null));
    }

    @Test
    public void keyStoreWithSignOcspCertificateTest() throws Exception {
        BasicOCSPResp response = getOcspResponse();

        Assert.assertFalse(CertificateVerification.verifyOcspCertificates(
                response, Pkcs12FileHelper.initStore(signOcspCert, password), null));
    }

    @Test
    public void keyStoreWithNotOcspAndOcspCertificatesTest() throws Exception {
        BasicOCSPResp response = getOcspResponse();

        Assert.assertTrue(CertificateVerification.verifyOcspCertificates(
                response, Pkcs12FileHelper.initStore(notOcspAndOcspCert, password), null));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = ANY_LOG_MESSAGE))
    public void keyStoreWithNotOcspCertificateTest() throws Exception {
        Assert.assertFalse(CertificateVerification.verifyOcspCertificates(
                null, Pkcs12FileHelper.initStore(signOcspCert, password), null));
    }

    private static BasicOCSPResp getOcspResponse() throws Exception {
        TestOcspClient testClient = new TestOcspClient();
        PrivateKey key = Pkcs12FileHelper.readFirstKey(rootOcspCert, password, password);
        testClient.addBuilderForCertIssuer(rootCert, key);
        byte[] ocspResponseBytes = testClient.getEncoded(checkCert, rootCert, ocspServiceUrl);
        ASN1Primitive var2 = ASN1Primitive.fromByteArray(ocspResponseBytes);
        return new BasicOCSPResp(BasicOCSPResponse.getInstance(var2));
    }
}
