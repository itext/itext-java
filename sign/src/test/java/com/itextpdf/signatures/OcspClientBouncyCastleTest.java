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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;


import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

@Tag("BouncyCastleUnitTest")
public class OcspClientBouncyCastleTest extends ExtendedITextTest {
    private static final String ocspCertsSrc = "./src/test/resources/com/itextpdf/signatures/OcspClientBouncyCastleTest/";
    private static final String rootOcspCert = ocspCertsSrc + "ocspRootRsa.pem";
    private static final String signOcspCert = ocspCertsSrc + "ocspSignRsa.pem";
    private static final char[] password = "testpassphrase".toCharArray();
    private static final String ocspServiceUrl = "http://localhost:9000/demo/ocsp/ocsp-service";
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static X509Certificate checkCert;
    private static X509Certificate rootCert;
    private static TestOcspResponseBuilder builder;

    @BeforeAll
    public static void before() {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
    }

    @BeforeEach
    public void setUp()
            throws CertificateException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        builder = createBuilder(BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood());
        checkCert = (X509Certificate) PemFileHelper.readFirstChain(signOcspCert)[0];
        rootCert = builder.getIssuerCert();
    }

    @Test
    public void getOcspResponseWhenCheckCertIsNullTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractOCSPException {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle();
        Assertions.assertNull(castle.getOcspResponse(null, rootCert, ocspServiceUrl));
    }

    @Test
    public void getOcspResponseWhenRootCertIsNullTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractOCSPException {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle();
        Assertions.assertNull(castle.getOcspResponse(checkCert, null, ocspServiceUrl));
    }

    @Test
    public void getOcspResponseWhenRootAndCheckCertIsNullTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractOCSPException {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle();
        Assertions.assertNull(castle.getOcspResponse(null, null, ocspServiceUrl));
    }

    @Test
    public void getOcspResponseWhenUrlCertIsNullTest() {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle();
        Assertions.assertThrows(ConnectException.class,
                () -> castle.getOcspResponse(checkCert, rootCert, null));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Getting OCSP from http://asd", logLevel = LogLevelConstants.INFO),
    })
    public void incorrectUrlTest() {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle();
        Assertions.assertThrows(UnknownHostException.class,
                () -> castle.getOcspResponse(checkCert, rootCert, "http://asd"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Getting OCSP from", logLevel = LogLevelConstants.INFO),
    })
    public void malformedUrlTest() {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle();
        Assertions.assertThrows(MalformedURLException.class,
                () -> castle.getOcspResponse(checkCert, rootCert, ""));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Getting OCSP from http://localhost:9000/demo/ocsp/ocsp-service", logLevel
                    = LogLevelConstants.INFO),
    })
    public void connectionRefusedTest() {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle();
        Assertions.assertThrows(ConnectException.class,
                () -> castle.getOcspResponse(checkCert, rootCert, ocspServiceUrl));
    }

    @Test
    public void getBasicOcspRespTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = createOcspClient();

        IBasicOCSPResp basicOCSPResp = ocspClientBouncyCastle
                .getBasicOCSPResp(checkCert, rootCert, ocspServiceUrl);
        Assertions.assertNotNull(basicOCSPResp);
        Assertions.assertTrue(basicOCSPResp.getResponses().length > 0);
    }

    @Test
    public void getBasicOcspRespNullTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = new OcspClientBouncyCastle();

        IBasicOCSPResp basicOCSPResp = ocspClientBouncyCastle
                .getBasicOCSPResp(checkCert, null, ocspServiceUrl);
        Assertions.assertNull(basicOCSPResp);
    }

    @Test
    public void getEncodedTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = createOcspClient();

        byte[] encoded = ocspClientBouncyCastle.getEncoded(checkCert, rootCert, ocspServiceUrl);
        Assertions.assertNotNull(encoded);
        Assertions.assertTrue(encoded.length > 0);
    }

    @Test
    public void ocspStatusIsRevokedTest()
            throws CertificateException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        IRevokedStatus status = BOUNCY_CASTLE_FACTORY.createRevokedStatus(DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -20),
                BOUNCY_CASTLE_FACTORY.createOCSPResp().getSuccessful());
        TestOcspResponseBuilder responseBuilder = createBuilder(status);
        OcspClientBouncyCastle ocspClientBouncyCastle = createTestOcspClient(responseBuilder);

        byte[] encoded = ocspClientBouncyCastle.getEncoded(checkCert, rootCert, ocspServiceUrl);
        Assertions.assertNotNull(
                BOUNCY_CASTLE_FACTORY.createRevokedStatus(OcspClientBouncyCastle.getCertificateStatus(encoded)));
    }

    @Test
    public void ocspStatusIsUnknownTest()
            throws CertificateException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        IUnknownStatus status = BOUNCY_CASTLE_FACTORY.createUnknownStatus();
        TestOcspResponseBuilder responseBuilder = createBuilder(status);
        OcspClientBouncyCastle ocspClientBouncyCastle = createTestOcspClient(responseBuilder);

        byte[] encoded = ocspClientBouncyCastle.getEncoded(checkCert, rootCert, ocspServiceUrl);
        Assertions.assertNotEquals(BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood(),
                OcspClientBouncyCastle.getCertificateStatus(encoded));
        Assertions.assertNull(
                BOUNCY_CASTLE_FACTORY.createRevokedStatus(OcspClientBouncyCastle.getCertificateStatus(encoded)));
    }

    @Test
    public void invalidOcspStatusIsNullTest() {
        byte[] encoded = new byte[0];
        Assertions.assertNull(OcspClientBouncyCastle.getCertificateStatus(encoded));
    }

    private static OcspClientBouncyCastle createOcspClient() {
        return createOcspClient(builder);
    }

    private static OcspClientBouncyCastle createOcspClient(TestOcspResponseBuilder builder) {
        return new TestOcspClientBouncyCastle(builder);
    }

    private static OcspClientBouncyCastle createTestOcspClient(TestOcspResponseBuilder responseBuilder) {
        return createOcspClient(responseBuilder);
    }

    private static TestOcspResponseBuilder createBuilder(ICertificateStatus status)
            throws CertificateException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(rootOcspCert)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(rootOcspCert, password);
        return new TestOcspResponseBuilder(caCert, caPrivateKey, status);
    }

    private static final class TestOcspClientBouncyCastle extends OcspClientBouncyCastle {
        private static TestOcspResponseBuilder testOcspBuilder;

        public TestOcspClientBouncyCastle(TestOcspResponseBuilder testBuilder) {
            super();
            testOcspBuilder = testBuilder;
        }

        @Override
        IOCSPResp getOcspResponse(X509Certificate chCert, X509Certificate rCert, String url)
                throws AbstractOCSPException {
            try {
                ICertificateID id = SignTestPortUtil.generateCertificateId(rootCert, checkCert.getSerialNumber(),
                        BOUNCY_CASTLE_FACTORY.createCertificateID().getHashSha1());
                IBasicOCSPResp basicOCSPResp = testOcspBuilder.makeOcspResponseObject(SignTestPortUtil
                        .generateOcspRequestWithNonce(id).getEncoded());
                return BOUNCY_CASTLE_FACTORY.createOCSPRespBuilder().build(BOUNCY_CASTLE_FACTORY.createOCSPRespBuilderInstance().getSuccessful(), basicOCSPResp);
            } catch (Exception e) {
                throw BOUNCY_CASTLE_FACTORY.createAbstractOCSPException(e);
            }
        }
    }
}
