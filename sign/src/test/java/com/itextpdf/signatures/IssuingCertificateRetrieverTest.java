/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.mocks.MockResourceRetriever;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
class IssuingCertificateRetrieverTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @Test
    public void testResourceRetrieverUsage() throws CertificateException, IOException {

        Certificate[] cert = PemFileHelper.readFirstChain(CERTS_SRC + "intermediate.pem");
        final List<URL> urlsCalled = new ArrayList<>();

        MockResourceRetriever mockRetriever = new MockResourceRetriever();
        mockRetriever.onGetInputStreamByUrl(u ->  {
            urlsCalled.add(u);
            try {
                return FileUtil.getInputStreamForFile(CERTS_SRC + "root.pem");
            } catch (IOException e) {
                throw new RuntimeException("Error reading certificate.", e);
            }
        });
        ValidatorChainBuilder builder = new ValidatorChainBuilder().withResourceRetriever(() -> mockRetriever);
        builder.getCertificateRetriever().retrieveIssuerCertificate(cert[0]);

        Assertions.assertEquals(1, urlsCalled.size());
        Assertions.assertEquals("http://test.example.com/example-ca/certs/ca/ca.crt", urlsCalled.get(0).toString());
    }

    @Test
    public void ocspWithKeyHashTest() throws CertificateException, IOException, AbstractOperatorCreationException, AbstractPKCSException, AbstractOCSPException {
        X509Certificate cert = (X509Certificate) PemFileHelper.readFirstChain(CERTS_SRC + "rootRsa.pem")[0];
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(CERTS_SRC + "rootRsa.pem", PASSWORD);

        TestOcspClient testOcspClient = new TestOcspClient();
        TestOcspResponseBuilder responseBuilder = new TestOcspResponseBuilder(cert, signRsaPrivateKey);
        responseBuilder.setResponseBuilder(FACTORY.createBasicOCSPRespBuilder(FACTORY.createRespID(cert)));
        testOcspClient.addBuilderForCertIssuer(cert, responseBuilder);

        IBasicOCSPResp ocspResponse = testOcspClient.getBasicOcspResp(cert, cert);
        IssuingCertificateRetriever issuingCertificateRetriever = new IssuingCertificateRetriever();
        Set<Certificate> retrievers = issuingCertificateRetriever.retrieveOCSPResponderByNameCertificate(ocspResponse);

        Assertions.assertEquals(1, retrievers.size());
        Assertions.assertTrue(retrievers.contains(cert));
    }
}
