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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@Category(BouncyCastleIntegrationTest.class)
public class PdfPadesSignerLevelsTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final boolean FIPS_MODE = "BCFIPS".equals(FACTORY.getProviderName());

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesSignerLevelsTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfPadesSignerLevelsTest/";
    
    private final Boolean useTempFolder;
    private final Integer comparisonPdfId;
    private final Boolean useSignature;

    private static final char[] password = "testpassphrase".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    public PdfPadesSignerLevelsTest(Object useTempFolder, Object useSignature, Object comparisonPdfId) {
        this.useTempFolder = (Boolean) useTempFolder;
        this.useSignature = (Boolean) useSignature; 
        this.comparisonPdfId = (Integer) comparisonPdfId;
    }

    @Parameterized.Parameters(name = "{2}: folder path: {0}; pass whole signature: {1}")
    public static Iterable<Object[]> createParameters() {
        return Arrays.asList(new Object[] {true, true, 1},
                new Object[] {false, true, 2},
                new Object[] {false, false, 3});
    }
    
    @Test
    public void padesSignatureLevelBTest()
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "padesSignatureLevelBTest" + comparisonPdfId + ".pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertRsa01.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);

        PdfSigner signer = createPdfSigner(srcFileName, outFileName);

        PdfPadesSigner padesSigner = new PdfPadesSigner();
        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        if ((boolean) useSignature) {
            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            padesSigner.signWithBaselineBProfile(signer, signRsaChain, pks);
        } else {
            padesSigner.signWithBaselineBProfile(signer, signRsaChain, signRsaPrivateKey);
        }

        PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void padesSignatureLevelTTest()
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "padesSignatureLevelTTest" + comparisonPdfId + ".pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);

        PdfSigner signer = createPdfSigner(srcFileName, outFileName);
        
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);

        PdfPadesSigner padesSigner = new PdfPadesSigner();
        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        if ((boolean) useSignature) {
            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            padesSigner.signWithBaselineTProfile(signer, signRsaChain, pks, testTsa);
        } else {
            padesSigner.signWithBaselineTProfile(signer, signRsaChain, signRsaPrivateKey, testTsa);
        }

        PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void padesSignatureLevelLTTest()
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "padesSignatureLevelLTTest" + comparisonPdfId + ".pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String caCertFileName = certsSrc + "rootRsa.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);

        PdfSigner signer = createPdfSigner(srcFileName, outFileName);
        
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);

        PdfPadesSigner padesSigner = new PdfPadesSigner();
        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);
        if ((boolean) useSignature) {
            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            padesSigner.signWithBaselineLTProfile(signer, signRsaChain, pks, testTsa);
        } else {
            padesSigner.signWithBaselineLTProfile(signer, signRsaChain, signRsaPrivateKey, testTsa);
        }

        PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void padesSignatureLevelLTATest()
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "padesSignatureLevelLTATest" + comparisonPdfId + ".pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String caCertFileName = certsSrc + "rootRsa.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);

        PdfSigner signer = createPdfSigner(srcFileName, outFileName);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);

        PdfPadesSigner padesSigner = new PdfPadesSigner();
        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient)
                .setTimestampSignatureName("timestampSig1");
        if ((boolean) useSignature) {
            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            padesSigner.signWithBaselineLTAProfile(signer, signRsaChain, pks, testTsa);
        } else {
            padesSigner.signWithBaselineLTAProfile(signer, signRsaChain, signRsaPrivateKey, testTsa);
        }

        PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }
    
    @Test
    public void prolongDocumentSignaturesTest()
            throws GeneralSecurityException, IOException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "prolongDocumentSignaturesTest" + comparisonPdfId + (FIPS_MODE ? "_FIPS.pdf" : ".pdf");
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "padesSignatureLevelLTA.pdf";
        String tsaCertFileName = certsSrc + "tsCertRsa.pem";
        String caCertFileName = certsSrc + "rootRsa.pem";

        Certificate[] tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
        PrivateKey tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, password);
        X509Certificate caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
        PrivateKey caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, password);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);

        PdfPadesSigner padesSigner = new PdfPadesSigner();
        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);
        if ((boolean) useSignature) {
            padesSigner.prolongSignatures(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)),
                    FileUtil.getFileOutputStream(outFileName), testTsa);
        } else {
            padesSigner.prolongSignatures(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)),
                    FileUtil.getFileOutputStream(outFileName));
        }

        PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");
        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }
    
    private PdfSigner createPdfSigner(String srcFileName, String outFileName) throws IOException {
        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), FileUtil.getFileOutputStream(outFileName),
                new StampingProperties());
        signer.setFieldName("Signature1");
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName())
                .setReason("Test")
                .setLocation("TestCity")
                .setDescription("Approval test signature.\nCreated by iText.");
        signer.setPageRect(new Rectangle(50, 650, 200, 100))
                .setSignatureAppearance(appearance);
        return signer;
    }
}
