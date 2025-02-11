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
package com.itextpdf.signatures.verify;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.signatures.CertificateVerification;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class OcspCertificateVerificationTest extends ExtendedITextTest {
    
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final Provider PROVIDER = FACTORY.getProvider();

    // Such messageTemplate is equal to any log message. This is required for porting reasons.
    private static final String ANY_LOG_MESSAGE = "{0}";

    private static final String ocspCertsSrc = "./src/test/resources/com/itextpdf/signatures/verify/OcspCertificateVerificationTest/";

    private static final String rootOcspCert = ocspCertsSrc + "ocspRootRsa.pem";
    private static final String signOcspCert = ocspCertsSrc + "ocspSignRsa.pem";
    private static final String notOcspAndOcspCert = ocspCertsSrc + "notOcspAndOcspCertificates.pem";

    private static final char[] password = "testpassphrase".toCharArray();
    private static final String ocspServiceUrl = "http://localhost:9000/demo/ocsp/ocsp-service";

    private static X509Certificate checkCert;
    private static X509Certificate rootCert;

    @BeforeAll
    public static void before() throws Exception {
        Security.addProvider(PROVIDER);
        checkCert = (X509Certificate) PemFileHelper.readFirstChain(signOcspCert)[0];
        rootCert = (X509Certificate) PemFileHelper.readFirstChain(rootOcspCert)[0];
    }

    @Test
    public void keyStoreWithRootOcspCertificateTest() throws Exception {
        IBasicOCSPResp response = getOcspResponse();

        Assertions.assertTrue(CertificateVerification.verifyOcspCertificates(
                response, PemFileHelper.initStore(rootOcspCert, password, PROVIDER), null));
    }

    @Test
    public void keyStoreWithSignOcspCertificateTest() throws Exception {
        IBasicOCSPResp response = getOcspResponse();

        Assertions.assertFalse(CertificateVerification.verifyOcspCertificates(
                response, PemFileHelper.initStore(signOcspCert, password, PROVIDER), null));
    }

    @Test
    public void keyStoreWithNotOcspAndOcspCertificatesTest() throws Exception {
        IBasicOCSPResp response = getOcspResponse();

        Assertions.assertTrue(CertificateVerification.verifyOcspCertificates(
                response, PemFileHelper.initStore(notOcspAndOcspCert, password, PROVIDER), null));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = ANY_LOG_MESSAGE))
    public void keyStoreWithNotOcspCertificateTest() throws Exception {
        Assertions.assertFalse(CertificateVerification.verifyOcspCertificates(
                null, PemFileHelper.initStore(signOcspCert, password, PROVIDER), null));
    }

    private static IBasicOCSPResp getOcspResponse() throws Exception {
        TestOcspClient testClient = new TestOcspClient();
        PrivateKey key = PemFileHelper.readFirstKey(rootOcspCert, password);
        testClient.addBuilderForCertIssuer(rootCert, key);
        byte[] ocspResponseBytes = testClient.getEncoded(checkCert, rootCert, ocspServiceUrl);
        IASN1Primitive var2 = FACTORY.createASN1Primitive(ocspResponseBytes);
        return FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(var2));
    }
}
