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
package com.itextpdf.signatures.sign;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.LtvVerification;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PadesSignatureLevelTest extends ExtendedITextTest {

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PadesSignatureLevelTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PadesSignatureLevelTest/";

    private static final char[] password = "testpass".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void padesSignatureLevelTTest01() throws GeneralSecurityException, IOException {
        String outFileName = destinationFolder + "padesSignatureLevelTTest01.pdf";
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertRsa01.p12";
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";

        Certificate[] signRsaChain = Pkcs12FileHelper.readFirstChain(signCertFileName, password);
        PrivateKey signRsaPrivateKey = Pkcs12FileHelper.readFirstKey(signCertFileName, password, password);
        IExternalSignature pks = new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);

        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaCertFileName, password);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaCertFileName, password, password);

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), new FileOutputStream(outFileName), new StampingProperties());
        signer.setFieldName("Signature1");
        signer.getSignatureAppearance()
                .setPageRect(new Rectangle(50, 650, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setLayer2Text("Approval test signature.\nCreated by iText7.");


        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        signer.signDetached(new BouncyCastleDigest(), pks, signRsaChain, null, null, testTsa, 0, PdfSigner.CryptoStandard.CADES);

        PadesSigTest.basicCheckSignedDoc(destinationFolder + "padesSignatureLevelTTest01.pdf", "Signature1");
    }

    @Test
    public void padesSignatureLevelLTTest01() throws GeneralSecurityException, IOException {
        String outFileName = destinationFolder + "padesSignatureLevelLTTest01.pdf";
        String srcFileName = sourceFolder + "signedPAdES-T.pdf";
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";
        String caCertFileName = certsSrc + "rootRsa.p12";

        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaCertFileName, password);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaCertFileName, password, password);

        X509Certificate caCert = (X509Certificate) Pkcs12FileHelper.readFirstChain(caCertFileName, password)[0];
        PrivateKey caPrivateKey = Pkcs12FileHelper.readFirstKey(caCertFileName, password, password);

        ICrlClient crlClient = new TestCrlClient(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        PdfDocument document = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName), new StampingProperties().useAppendMode());
        LtvVerification ltvVerification = new LtvVerification(document);
        ltvVerification.addVerification("Signature1", ocspClient, crlClient, LtvVerification.CertificateOption.SIGNING_CERTIFICATE, LtvVerification.Level.OCSP_CRL, LtvVerification.CertificateInclusion.YES);
        ltvVerification.merge();
        document.close();

        basicCheckDssDict("padesSignatureLevelLTTest01.pdf");
    }

    @Test
    public void padesSignatureLevelLTATest01() throws GeneralSecurityException, IOException {
        String outFileName = destinationFolder + "padesSignatureLevelLTATest01.pdf";
        String srcFileName = sourceFolder + "signedPAdES-LT.pdf";
        String tsaCertFileName = certsSrc + "tsCertRsa.p12";

        Certificate[] tsaChain = Pkcs12FileHelper.readFirstChain(tsaCertFileName, password);
        PrivateKey tsaPrivateKey = Pkcs12FileHelper.readFirstKey(tsaCertFileName, password, password);

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), new FileOutputStream(outFileName), new StampingProperties().useAppendMode());

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        signer.timestamp(testTsa, "timestampSig1");

        PadesSigTest.basicCheckSignedDoc(destinationFolder + "padesSignatureLevelLTATest01.pdf", "timestampSig1");
    }

    private void basicCheckDssDict(String fileName) throws IOException {
        PdfDocument outDocument = new PdfDocument(new PdfReader(destinationFolder + fileName));
        PdfDictionary dssDict = outDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        Assert.assertNotNull(dssDict);
        Assert.assertEquals(4, dssDict.size());
    }
}
