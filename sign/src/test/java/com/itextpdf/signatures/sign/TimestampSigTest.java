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
package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class TimestampSigTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/TimestampSigTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/TimestampSigTest/";

    private static final char[] password = "testpassphrase".toCharArray();


    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void timestampTest01()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String outFileName = destinationFolder + "timestampTest01.pdf";

        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), FileUtil.getFileOutputStream(outFileName), new StampingProperties());

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        signer.timestamp(testTsa, "timestampSig1");

        TestSignUtils.basicCheckSignedDoc(destinationFolder + "timestampTest01.pdf", "timestampSig1");

        Assert.assertNull(
                SignaturesCompareTool.compareSignatures(outFileName, sourceFolder + "cmp_timestampTest01.pdf"));
    }


//        TimeStampToken tsWrong = new TimeStampResponse(Files.readAllBytes(Paths.get("c:\\Users\\yulian\\Desktop\\myTs"))).getTimeStampToken();
//
//        JcaSimpleSignerInfoVerifierBuilder sigVerifBuilder = new JcaSimpleSignerInfoVerifierBuilder();
//        X509Certificate caCert = (X509Certificate)Pkcs12FileHelper.readFirstChain(p12FileName, password)[0];
//        SignerInformationVerifier signerInfoVerif = sigVerifBuilder.setProvider(BouncyCastleProvider.PROVIDER_NAME).build(caCert.getPublicKey());
//        boolean signatureValid = tsWrong.isSignatureValid(signerInfoVerif);
//
}
