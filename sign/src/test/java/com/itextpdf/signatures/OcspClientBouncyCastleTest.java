/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
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
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
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

    @BeforeClass
    public static void before() {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
    }

    @Before
    public void setUp()
            throws CertificateException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        builder = createBuilder(BOUNCY_CASTLE_FACTORY.createCertificateStatus().getGood());
        checkCert = (X509Certificate) PemFileHelper.readFirstChain(signOcspCert)[0];
        rootCert = builder.getIssuerCert();
    }

    @Test
    public void getOcspResponseWhenCheckCertIsNullTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractOCSPException {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle(null);
        Assert.assertNull(castle.getOcspResponse(null, rootCert, ocspServiceUrl));
    }

    @Test
    public void getOcspResponseWhenRootCertIsNullTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractOCSPException {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle(null);
        Assert.assertNull(castle.getOcspResponse(checkCert, null, ocspServiceUrl));
    }

    @Test
    public void getOcspResponseWhenRootAndCheckCertIsNullTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractOCSPException {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle(null);
        Assert.assertNull(castle.getOcspResponse(null, null, ocspServiceUrl));
    }

    @Test
    public void getOcspResponseWhenUrlCertIsNullTest() {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle(null);
        Assert.assertThrows(ConnectException.class,
                () -> castle.getOcspResponse(checkCert, rootCert, null));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Getting OCSP from http://asd", logLevel = LogLevelConstants.INFO),
    })
    public void incorrectUrlTest() {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle(null);
        Assert.assertThrows(UnknownHostException.class,
                () -> castle.getOcspResponse(checkCert, rootCert, "http://asd"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Getting OCSP from", logLevel = LogLevelConstants.INFO),
    })
    public void malformedUrlTest() {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle(null);
        Assert.assertThrows(MalformedURLException.class,
                () -> castle.getOcspResponse(checkCert, rootCert, ""));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = "Getting OCSP from http://localhost:9000/demo/ocsp/ocsp-service", logLevel
                    = LogLevelConstants.INFO),
    })
    public void connectionRefusedTest() {
        OcspClientBouncyCastle castle = new OcspClientBouncyCastle(null);
        Assert.assertThrows(ConnectException.class,
                () -> castle.getOcspResponse(checkCert, rootCert, ocspServiceUrl));
    }

    @Test
    public void getBasicOcspRespTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = createOcspClient();

        IBasicOCSPResp basicOCSPResp = ocspClientBouncyCastle
                .getBasicOCSPResp(checkCert, rootCert, ocspServiceUrl);
        Assert.assertNotNull(basicOCSPResp);
        Assert.assertTrue(basicOCSPResp.getResponses().length > 0);
    }

    @Test
    public void getBasicOcspRespNullTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = new OcspClientBouncyCastle(null);

        IBasicOCSPResp basicOCSPResp = ocspClientBouncyCastle
                .getBasicOCSPResp(checkCert, null, ocspServiceUrl);
        Assert.assertNull(basicOCSPResp);
    }

    @Test
    @LogMessages(messages =
    @LogMessage(messageTemplate = "OCSP response could not be verified"))
    public void getBasicOCSPRespLogMessageTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = createOcspClient();

        IBasicOCSPResp basicOCSPResp = ocspClientBouncyCastle.getBasicOCSPResp(null, null, null);
        Assert.assertNull(basicOCSPResp);
    }

    @Test
    public void getEncodedTest() {
        OcspClientBouncyCastle ocspClientBouncyCastle = createOcspClient();

        byte[] encoded = ocspClientBouncyCastle.getEncoded(checkCert, rootCert, ocspServiceUrl);
        Assert.assertNotNull(encoded);
        Assert.assertTrue(encoded.length > 0);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.OCSP_STATUS_IS_REVOKED),
    })
    public void ocspStatusIsRevokedTest()
            throws CertificateException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        IRevokedStatus status = BOUNCY_CASTLE_FACTORY.createRevokedStatus(DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -20),
                BOUNCY_CASTLE_FACTORY.createOCSPResp().getSuccessful());
        TestOcspResponseBuilder responseBuilder = createBuilder(status);
        OcspClientBouncyCastle ocspClientBouncyCastle = createTestOcspClient(responseBuilder);

        byte[] encoded = ocspClientBouncyCastle.getEncoded(checkCert, rootCert, ocspServiceUrl);
        Assert.assertNull(encoded);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.OCSP_STATUS_IS_UNKNOWN),
    })
    public void ocspStatusIsUnknownTest()
            throws CertificateException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        IUnknownStatus status = BOUNCY_CASTLE_FACTORY.createUnknownStatus();
        TestOcspResponseBuilder responseBuilder = createBuilder(status);
        OcspClientBouncyCastle ocspClientBouncyCastle = createTestOcspClient(responseBuilder);

        byte[] encoded = ocspClientBouncyCastle.getEncoded(checkCert, rootCert, ocspServiceUrl);
        Assert.assertNull(encoded);
    }

    private static OcspClientBouncyCastle createOcspClient() {
        return createOcspClient(builder);
    }

    private static OcspClientBouncyCastle createOcspClient(TestOcspResponseBuilder builder) {
        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        return new TestOcspClientBouncyCastle(ocspVerifier, builder);
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

        public TestOcspClientBouncyCastle(OCSPVerifier verifier, TestOcspResponseBuilder testBuilder) {
            super(verifier);
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
