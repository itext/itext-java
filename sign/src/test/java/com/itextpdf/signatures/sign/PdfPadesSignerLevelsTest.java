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
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPadesSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("BouncyCastleIntegrationTest")
public class PdfPadesSignerLevelsTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final boolean FIPS_MODE = "BCFIPS".equals(FACTORY.getProviderName());

    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfPadesSignerLevelsTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfPadesSignerLevelsTest/";
    private static final char[] password = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    public static Iterable<Object[]> createParameters() {
        return Arrays.asList(new Object[] {true, true, 1},
                new Object[] {false, true, 2},
                new Object[] {false, false, 3});
    }

    @ParameterizedTest(name = "{2}: folder path: {0}; pass whole signature: {1}")
    @MethodSource("createParameters")
    public void padesSignatureLevelBTest(Boolean useTempFolder, Boolean useSignature, Integer comparisonPdfId)
            throws IOException, GeneralSecurityException, AbstractOperatorCreationException, AbstractPKCSException {
        String fileName = "padesSignatureLevelBTest" + comparisonPdfId + ".pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp_" + fileName;
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        String signCertFileName = certsSrc + "signCertRsa01.pem";

        Certificate[] signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);

        SignerProperties signerProperties = createSignerProperties();

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);
        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        if ((boolean) useSignature) {
            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            padesSigner.signWithBaselineBProfile(signerProperties, signRsaChain, pks);
        } else {
            padesSigner.signWithBaselineBProfile(signerProperties, signRsaChain, signRsaPrivateKey);
        }

        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @ParameterizedTest(name = "{2}: folder path: {0}; pass whole signature: {1}")
    @MethodSource("createParameters")
    public void padesSignatureLevelTTest(Boolean useTempFolder, Boolean useSignature, Integer comparisonPdfId)
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

        SignerProperties signerProperties = createSignerProperties();

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);
        
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        if ((boolean) useSignature) {
            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            padesSigner.signWithBaselineTProfile(signerProperties, signRsaChain, pks, testTsa);
        } else {
            padesSigner.signWithBaselineTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);
        }

        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @ParameterizedTest(name = "{2}: folder path: {0}; pass whole signature: {1}")
    @MethodSource("createParameters")
    public void padesSignatureLevelLTTest(Boolean useTempFolder, Boolean useSignature, Integer comparisonPdfId)
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

        SignerProperties signerProperties = createSignerProperties();

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);
        
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);

        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);
        if ((boolean) useSignature) {
            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, pks, testTsa);
        } else {
            padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);
        }

        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @ParameterizedTest(name = "{2}: folder path: {0}; pass whole signature: {1}")
    @MethodSource("createParameters")
    public void padesSignatureLevelLTATest(Boolean useTempFolder, Boolean useSignature, Integer comparisonPdfId)
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

        SignerProperties signerProperties = createSignerProperties();

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);

        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);

        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient)
                .setTimestampSignatureName("timestampSig1");
        if ((boolean) useSignature) {
            IExternalSignature pks =
                    new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());
            padesSigner.signWithBaselineLTAProfile(signerProperties, signRsaChain, pks, testTsa);
        } else {
            padesSigner.signWithBaselineLTAProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);
        }

        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @ParameterizedTest(name = "{2}: folder path: {0}; pass whole signature: {1}")
    @MethodSource("createParameters")
    public void prolongDocumentSignaturesTest(Boolean useTempFolder, Boolean useSignature, Integer comparisonPdfId)
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

        PdfPadesSigner padesSigner = createPdfPadesSigner(srcFileName, outFileName);
        if ((boolean) useTempFolder) {
            padesSigner.setTemporaryDirectoryPath(destinationFolder);
        }
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);
        if ((boolean) useSignature) {
            padesSigner.prolongSignatures(testTsa);
        } else {
            padesSigner.prolongSignatures();
        }

        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    private SignerProperties createSignerProperties() {
        SignerProperties signerProperties = new SignerProperties();
        signerProperties.setFieldName("Signature1");
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent("Approval test signature.\nCreated by iText.");
        signerProperties.setPageRect(new Rectangle(50, 650, 200, 100))
                .setSignatureAppearance(appearance);

        return signerProperties;
    }

    private PdfPadesSigner createPdfPadesSigner(String srcFileName, String outFileName) throws IOException {
        return new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFileName)),
                FileUtil.getFileOutputStream(outFileName));
    }
}
