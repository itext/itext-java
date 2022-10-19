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
package com.itextpdf.signatures.verify;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Primitive;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.cert.TestCertificateBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleUnitTest.class)
public class OcspVerifierTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpass".toCharArray();
    private static final String caCertFileName = certsSrc + "rootRsa.pem";

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void validOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        Assert.assertTrue(verifyTest(builder));
    }
    
    @Test
    public void validOcspWithoutOcspResponseBuilderTest() throws IOException, GeneralSecurityException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem")[0];
        X509Certificate rootCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        Date checkDate = DateTimeUtil.getCurrentTimeDate();

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        Assert.assertTrue(ocspVerifier.verify(caCert, rootCert, checkDate).isEmpty());
    }

    @Test
    public void invalidRevokedOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        builder.setCertificateStatus(FACTORY.createRevokedStatus(
                DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -20),
                FACTORY.createCRLReason().getKeyCompromise()));
        Assert.assertFalse(verifyTest(builder));
    }

    @Test
    public void invalidUnknownOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        builder.setCertificateStatus(FACTORY.createUnknownStatus());
        Assert.assertFalse(verifyTest(builder));
    }

    @Test
    public void invalidOutdatedOcspTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        Calendar thisUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCurrentTimeCalendar(), -30);
        Calendar nextUpdate = DateTimeUtil.addDaysToCalendar(DateTimeUtil.getCurrentTimeCalendar(), -15);
        builder.setThisUpdate(thisUpdate);
        builder.setNextUpdate(nextUpdate);
        Assert.assertFalse(verifyTest(builder));
    }

    @Test
    public void expiredIssuerCertTest01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "intermediateExpiredCert.pem")[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(certsSrc + "intermediateExpiredCert.pem", password);
        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(caCert, caPrivateKey);

        Assert.assertTrue(verifyTest(builder, certsSrc + "signCertRsaWithExpiredChain.pem", caCert.getNotBefore()));
    }

    @Test
    public void authorizedOCSPResponderTest() throws GeneralSecurityException, IOException,
            AbstractPKCSException, AbstractOperatorCreationException {
        Date ocspResponderCertStartDate = DateTimeUtil.getCurrentTimeDate();
        Date ocspResponderCertEndDate = DateTimeUtil.addDaysToDate(ocspResponderCertStartDate, 365 * 100);
        Date checkDate = DateTimeUtil.getCurrentTimeDate();

        boolean verifyRes = verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate);
        Assert.assertTrue(verifyRes);
    }

    @Test
    public void expiredAuthorizedOCSPResponderTest_atValidPeriod() throws GeneralSecurityException, IOException,
            AbstractPKCSException, AbstractOperatorCreationException {
        Date ocspResponderCertStartDate = DateTimeUtil.parse("15/10/2005", "dd/MM/yyyy");
        Date ocspResponderCertEndDate = DateTimeUtil.parse("15/10/2010", "dd/MM/yyyy");
        Date checkDate = DateTimeUtil.parse("15/10/2008", "dd/MM/yyyy");

        boolean verifyRes = verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate);
        Assert.assertTrue(verifyRes);
    }

    @Test
    public void expiredAuthorizedOCSPResponderTest_now() {
        Date ocspResponderCertStartDate = DateTimeUtil.parse("15/10/2005", "dd/MM/yyyy");
        Date ocspResponderCertEndDate = DateTimeUtil.parse("15/10/2010", "dd/MM/yyyy");
        Date checkDate = DateTimeUtil.getCurrentTimeDate();

        Assert.assertThrows(CertificateExpiredException.class,
                () -> verifyAuthorizedOCSPResponderTest(ocspResponderCertStartDate, ocspResponderCertEndDate, checkDate)
        );
        // Not getting here because of exception
        //Assert.assertFalse(verifyRes);
    }
    
    @Test
    public void getOcspResponseNullTest() {
        OCSPVerifier verifier = new OCSPVerifier(null, null);
        Assert.assertNull(verifier.getOcspResponse(null, null));
    }

    private boolean verifyTest(TestOcspResponseBuilder rootRsaOcspBuilder) throws IOException, GeneralSecurityException {
        return verifyTest(rootRsaOcspBuilder, certsSrc + "signCertRsa01.pem", DateTimeUtil.getCurrentTimeDate());
    }

    private boolean verifyTest(TestOcspResponseBuilder rootRsaOcspBuilder, String checkCertFileName, Date checkDate) throws IOException, GeneralSecurityException {
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];

        X509Certificate rootCert = rootRsaOcspBuilder.getIssuerCert();
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(rootCert, rootRsaOcspBuilder);
        byte[] basicOcspRespBytes = ocspClient.getEncoded(checkCert, rootCert, null);

        IASN1Primitive var2 = FACTORY.createASN1Primitive(basicOcspRespBytes);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(var2));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        return ocspVerifier.verify(basicOCSPResp, checkCert, rootCert, checkDate);
    }

    public boolean verifyAuthorizedOCSPResponderTest(Date ocspResponderCertStartDate, Date ocspResponderCertEndDate,
            Date checkDate)
            throws IOException, AbstractOperatorCreationException, GeneralSecurityException, AbstractPKCSException {
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(certsSrc + "intermediateRsa.pem")[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(certsSrc + "intermediateRsa.pem", password);
        String checkCertFileName = certsSrc + "signCertRsaWithChain.pem";
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];

        KeyPairGenerator keyGen = SignTestPortUtil.buildRSA2048KeyPairGenerator();
        KeyPair key = keyGen.generateKeyPair();
        PrivateKey ocspRespPrivateKey = key.getPrivate();
        PublicKey ocspRespPublicKey = key.getPublic();
        TestCertificateBuilder certBuilder = new TestCertificateBuilder(ocspRespPublicKey, caCert, caPrivateKey, "CN=iTextTestOCSPResponder, OU=test, O=iText");
        certBuilder.setStartDate(ocspResponderCertStartDate);
        certBuilder.setEndDate(ocspResponderCertEndDate);
        X509Certificate ocspResponderCert = certBuilder.buildAuthorizedOCSPResponderCert();

        TestOcspResponseBuilder builder = new TestOcspResponseBuilder(ocspResponderCert, ocspRespPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, builder);
        byte[] basicOcspRespBytes = ocspClient.getEncoded(checkCert, caCert, null);

        IASN1Primitive var2 = FACTORY.createASN1Primitive(basicOcspRespBytes);
        IBasicOCSPResp basicOCSPResp = FACTORY.createBasicOCSPResp(FACTORY.createBasicOCSPResponse(var2));

        OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
        return ocspVerifier.verify(basicOCSPResp, checkCert, caCert, checkDate);
    }
}
