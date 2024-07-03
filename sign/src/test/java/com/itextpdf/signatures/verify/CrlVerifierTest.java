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
package com.itextpdf.signatures.verify;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.signatures.CRLVerifier;
import com.itextpdf.signatures.VerificationException;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.TimeTestUtil;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

@Tag("BouncyCastleUnitTest")
public class CrlVerifierTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final char[] password = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void validCrl01() throws GeneralSecurityException, IOException, AbstractPKCSException,
            AbstractOperatorCreationException {
        String caCertP12FileName = certsSrc + "rootRsa.pem";
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertP12FileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertP12FileName, password);
        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey, DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1));
        Assertions.assertTrue(verifyTest(crlBuilder));
    }

    @Test
    public void invalidRevokedCrl01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        String caCertP12FileName = certsSrc + "rootRsa.pem";
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertP12FileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertP12FileName, password);
        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey, DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1));

        String checkCertFileName = certsSrc + "signCertRsa01.pem";
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];
        crlBuilder.addCrlEntry(checkCert, DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -40),
                FACTORY.createCRLReason().getKeyCompromise());

        Assertions.assertThrows(VerificationException.class, () -> verifyTest(crlBuilder));
    }

    @Test
    public void invalidOutdatedCrl01()
            throws GeneralSecurityException, IOException, AbstractPKCSException, AbstractOperatorCreationException {
        String caCertP12FileName = certsSrc + "rootRsa.pem";
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertP12FileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertP12FileName, password);
        TestCrlBuilder crlBuilder = new TestCrlBuilder(caCert, caPrivateKey, DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -2));
        crlBuilder.setNextUpdate(DateTimeUtil.addDaysToDate(TimeTestUtil.TEST_DATE_TIME, -1));

        Assertions.assertFalse(verifyTest(crlBuilder));
    }

    private boolean verifyTest(TestCrlBuilder crlBuilder) throws GeneralSecurityException, IOException {
        String caCertFileName = certsSrc + "rootRsa.pem";
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        String checkCertFileName = certsSrc + "signCertRsa01.pem";
        X509Certificate checkCert = (X509Certificate) PemFileHelper.readFirstChain(checkCertFileName)[0];


        TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(crlBuilder);
        Collection<byte[]> crlBytesCollection = crlClient.getEncoded(checkCert, null);

        boolean verify = false;
        for (byte[] crlBytes : crlBytesCollection) {
            X509CRL crl = (X509CRL) SignTestPortUtil.parseCrlFromStream(new ByteArrayInputStream(crlBytes));
            CRLVerifier verifier = new CRLVerifier(null, null);
            verify = verifier.verify(crl, checkCert, caCert, TimeTestUtil.TEST_DATE_TIME);
            break;
        }
        return verify;
    }
}
