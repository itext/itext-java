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
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampToken;
import com.itextpdf.commons.bouncycastle.tsp.ITimeStampTokenInfo;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.builder.TestTimestampTokenBuilder;
import com.itextpdf.test.ExtendedITextTest;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleUnitTest")
public class TSAClientBouncyCastleTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    @Test
    public void setTSAInfoTest() {
        TSAClientBouncyCastle clientBouncyCastle = new TSAClientBouncyCastle("url");
        CustomItsaInfoBouncyCastle infoBouncyCastle = new CustomItsaInfoBouncyCastle();
        clientBouncyCastle.setTSAInfo(infoBouncyCastle);
        Assertions.assertEquals(infoBouncyCastle, clientBouncyCastle.tsaInfo);
    }

    @Test
    public void testTsaClientBouncyCastleConstructor3Args() {
        String userName = "user";
        String password = "password";
        String url = "url";

        TSAClientBouncyCastle tsaClientBouncyCastle = new TSAClientBouncyCastle(url, userName, password);
        Assertions.assertEquals(url, tsaClientBouncyCastle.tsaURL);
        Assertions.assertEquals(userName, tsaClientBouncyCastle.tsaUsername);
        Assertions.assertEquals(password, tsaClientBouncyCastle.tsaPassword);
        Assertions.assertEquals(TSAClientBouncyCastle.DEFAULTTOKENSIZE, tsaClientBouncyCastle.getTokenSizeEstimate());
        Assertions.assertEquals(TSAClientBouncyCastle.DEFAULTHASHALGORITHM, tsaClientBouncyCastle.digestAlgorithm);
    }

    @Test
    public void testTsaClientBouncyCastleConstructorAllArgs() {
        String userName = "user";
        String password = "password";
        String url = "url";
        int tokenSize = 1024;
        String digestAlgorithm = "SHA-1";

        TSAClientBouncyCastle tsaClientBouncyCastle = new TSAClientBouncyCastle(url, userName, password,
                tokenSize, digestAlgorithm);
        Assertions.assertEquals(url, tsaClientBouncyCastle.tsaURL);
        Assertions.assertEquals(userName, tsaClientBouncyCastle.tsaUsername);
        Assertions.assertEquals(password, tsaClientBouncyCastle.tsaPassword);
        Assertions.assertEquals(TSAClientBouncyCastle.DEFAULTTOKENSIZE, tsaClientBouncyCastle.tokenSizeEstimate);
        Assertions.assertEquals(tokenSize, tsaClientBouncyCastle.getTokenSizeEstimate());
        Assertions.assertEquals(digestAlgorithm, tsaClientBouncyCastle.digestAlgorithm);
    }

    @Test
    public void testTsaClientBouncyCastleConstructor1Arg() {
        String url = "url";

        TSAClientBouncyCastle tsaClientBouncyCastle = new TSAClientBouncyCastle(url);
        Assertions.assertEquals(url, tsaClientBouncyCastle.tsaURL);
        Assertions.assertNull(tsaClientBouncyCastle.tsaUsername);
        Assertions.assertNull(tsaClientBouncyCastle.tsaPassword);
        Assertions.assertEquals(TSAClientBouncyCastle.DEFAULTTOKENSIZE, tsaClientBouncyCastle.tokenSizeEstimate);
        Assertions.assertEquals(TSAClientBouncyCastle.DEFAULTHASHALGORITHM, tsaClientBouncyCastle.digestAlgorithm);
    }

    @Test
    public void getTokenSizeEstimateTest() {
        String userName = "user";
        String password = "password";
        String url = "url";
        String digestAlgorithm = "SHA-256";
        int tokenSizeEstimate = 4096;

        TSAClientBouncyCastle tsaClientBouncyCastle = new TSAClientBouncyCastle(url, userName, password,
                tokenSizeEstimate, digestAlgorithm);
        Assertions.assertEquals(tokenSizeEstimate, tsaClientBouncyCastle.getTokenSizeEstimate());
    }

    @Test
    public void setGetTsaReqPolicyTest() {
        String regPolicy = "regPolicy";

        TSAClientBouncyCastle clientBouncyCastle = new TSAClientBouncyCastle("url");
        clientBouncyCastle.setTSAReqPolicy(regPolicy);
        Assertions.assertEquals(regPolicy, clientBouncyCastle.getTSAReqPolicy());
    }

    @Test
    public void getMessageDigestTest() throws GeneralSecurityException {
        String userName = "user";
        String password = "password";
        String url = "url";
        String digestAlgorithm = "SHA-256";
        int tokenSizeEstimate = 4096;

        TSAClientBouncyCastle tsaClientBouncyCastle = new TSAClientBouncyCastle(url, userName, password,
                tokenSizeEstimate, digestAlgorithm);
        MessageDigest digest = tsaClientBouncyCastle.getMessageDigest();
        Assertions.assertNotNull(digest);
        Assertions.assertEquals(digestAlgorithm, digest.getAlgorithm());
    }

    @Test
    public void getTimeStampTokenTest() throws Exception {
        String allowedDigest = "SHA256";
        String signatureAlgorithm = "SHA256withRSA";
        String policyOid = "1.3.6.1.4.1.45794.1.1";

        CustomTsaClientBouncyCastle tsaClientBouncyCastle = new CustomTsaClientBouncyCastle("", signatureAlgorithm,
                allowedDigest);
        tsaClientBouncyCastle.setTSAReqPolicy(policyOid);
        CustomItsaInfoBouncyCastle itsaInfoBouncyCastle = new CustomItsaInfoBouncyCastle();
        tsaClientBouncyCastle.setTSAInfo(itsaInfoBouncyCastle);
        byte[] timestampTokenArray = tsaClientBouncyCastle.getTimeStampToken(tsaClientBouncyCastle
                .getMessageDigest().digest());

        ITimeStampToken expectedToken = BOUNCY_CASTLE_FACTORY.createTimeStampResponse(tsaClientBouncyCastle.getExpectedTsaResponseBytes())
                .getTimeStampToken();
        ITimeStampTokenInfo expectedTsTokenInfo = expectedToken.getTimeStampInfo();
        ITimeStampTokenInfo resultTsTokenInfo = itsaInfoBouncyCastle.getTimeStampTokenInfo();

        Assertions.assertNotNull(timestampTokenArray);
        Assertions.assertNotNull(resultTsTokenInfo);
        Assertions.assertArrayEquals(expectedTsTokenInfo.getEncoded(), resultTsTokenInfo.getEncoded());
        Assertions.assertArrayEquals(expectedToken.getEncoded(), timestampTokenArray);
    }

    @Test
    public void getTimeStampTokenFailureExceptionTest() throws Exception {
        String allowedDigest = "SHA1";
        String signatureAlgorithm = "SHA256withRSA";
        String url = "url";

        CustomTsaClientBouncyCastle tsaClientBouncyCastle = new CustomTsaClientBouncyCastle(url, signatureAlgorithm,
                allowedDigest);
        tsaClientBouncyCastle.setTSAInfo(new CustomItsaInfoBouncyCastle());

        byte[] digest = tsaClientBouncyCastle.getMessageDigest().digest();
        Exception e = Assertions.assertThrows(PdfException.class,
                () -> tsaClientBouncyCastle.getTimeStampToken(digest)
        );

        Assertions.assertEquals(MessageFormatUtil.format(SignExceptionMessageConstant.INVALID_TSA_RESPONSE, url,
                        "128: request contains unknown algorithm"),
                e.getMessage());
    }

    private static final class CustomTsaClientBouncyCastle extends TSAClientBouncyCastle {
        private static final char[] PASSWORD = "testpassphrase".toCharArray();

        private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

        private final PrivateKey tsaPrivateKey;
        private final List<Certificate> tsaCertificateChain;
        private final String signatureAlgorithm;
        private final String allowedDigest;

        private byte[] expectedTsaResponseBytes;

        public CustomTsaClientBouncyCastle(String url, String signatureAlgorithm, String allowedDigest)
                throws Exception {
            super(url);

            this.signatureAlgorithm = signatureAlgorithm;
            this.allowedDigest = allowedDigest;
            tsaPrivateKey = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);

            String tsaCertFileName = CERTS_SRC + "tsCertRsa.pem";
            tsaCertificateChain = Arrays.asList(PemFileHelper.readFirstChain(tsaCertFileName));
        }

        public byte[] getExpectedTsaResponseBytes() {
            return expectedTsaResponseBytes;
        }

        @Override
        protected byte[] getTSAResponse(byte[] requestBytes) {
            TestTimestampTokenBuilder builder = new TestTimestampTokenBuilder(tsaCertificateChain, tsaPrivateKey);
            expectedTsaResponseBytes = builder.createTSAResponse(requestBytes, signatureAlgorithm, allowedDigest);
            return expectedTsaResponseBytes;
        }
    }

    private static final class CustomItsaInfoBouncyCastle implements ITSAInfoBouncyCastle {

        private ITimeStampTokenInfo timeStampTokenInfo;

        @Override
        public void inspectTimeStampTokenInfo(ITimeStampTokenInfo info) {
            this.timeStampTokenInfo = info;
        }

        public ITimeStampTokenInfo getTimeStampTokenInfo() {
            return timeStampTokenInfo;
        }
    }
}
