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
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IApplicableSignatureParams;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.RSASSAPSSMechanismParams;
import com.itextpdf.signatures.SecurityIDs;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;

@Category(BouncyCastleUnitTest.class)
public class RSASSAPSSTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/RSASSAPSSTest/";
    private static final String SOURCE_FILE = SOURCE_FOLDER + "helloWorldDoc.pdf";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/RSASSAPSSTest/";
    private static final String SIGNATURE_FIELD = "Signature";
    private static final char[] SAMPLE_KEY_PASSPHRASE = "pdfpdfpdfsecretsecret".toCharArray();

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void signWithRsaSsaPssTest() throws GeneralSecurityException, IOException {
        String digestName = "SHA256";

        String outFileName = "simplePssSignature.pdf";
        String cmpFileName = "cmp_simplePssSignature.pdf";
        doRoundTrip(
                digestName, "RSASSA-PSS",
                outFileName,
                RSASSAPSSMechanismParams.createForDigestAlgorithm(digestName)
        );

        Assert.assertNull(
                SignaturesCompareTool.compareSignatures(
                        Paths.get(DESTINATION_FOLDER, outFileName).toString(),
                        Paths.get(SOURCE_FOLDER, cmpFileName).toString()
                )
        );
    }

    @Test
    public void signWithRsaSsaPssAlternativeNomenclatureTest() throws GeneralSecurityException, IOException {
        String digestName = "SHA256";
        String outFileName = "simplePssAlternativeNomenclatureSignature.pdf";
        String cmpFileName = "cmp_simplePssSignature.pdf";
        doRoundTrip(
                digestName,
                //we should accept the "<digest>withRSA/PSS" convention as well
                "RSA/PSS",
                outFileName,
                RSASSAPSSMechanismParams.createForDigestAlgorithm(digestName)
        );

        Assert.assertNull(
                SignaturesCompareTool.compareSignatures(
                        Paths.get(DESTINATION_FOLDER, outFileName).toString(),
                        Paths.get(SOURCE_FOLDER, cmpFileName).toString()
                )
        );
    }

    @Test
    public void signWithRsaSsaSha384PssTest() throws GeneralSecurityException, IOException {
        String digestName = "SHA384";

        String outFileName = "simplePssSignatureSha384.pdf";
        doRoundTrip(
                digestName, "RSASSA-PSS",
                outFileName,
                RSASSAPSSMechanismParams.createForDigestAlgorithm(digestName)
        );
    }

    @Test
    public void signWithRsaSsaCustomSaltLengthTest() throws GeneralSecurityException, IOException {
        String digestName = "SHA256";

        String outFileName = "customSaltLength.pdf";
        String cmpFileName = "cmp_simplePssSignature.pdf";
        doRoundTrip(
                digestName, "RSASSA-PSS",
                outFileName,
                new RSASSAPSSMechanismParams(
                        FACTORY.createASN1ObjectIdentifier(DigestAlgorithms.getAllowedDigest(digestName)),
                        40,
                        1
                )
        );

        String cmpOut = SignaturesCompareTool.compareSignatures(
                Paths.get(DESTINATION_FOLDER, outFileName).toString(),
                Paths.get(SOURCE_FOLDER, cmpFileName).toString()
        );
        Assert.assertTrue(cmpOut.contains("out: Integer(40)"));
        Assert.assertTrue(cmpOut.contains("cmp: Integer(32)"));
    }

    @Test
    public void rejectMgfDigestDiscrepancy() throws IOException {
        // mgf digest function param is not the same as signature digest function
        String inFileName = "mgfDiscrepancy.pdf";
        try (PdfReader r = new PdfReader(Paths.get(SOURCE_FOLDER, inFileName).toString());
             PdfDocument pdfDoc = new PdfDocument(r)) {

            SignatureUtil u = new SignatureUtil(pdfDoc);
            String provider = FACTORY.getProviderName();
            PdfException pdfException = Assert.assertThrows(
                    PdfException.class,
                    () -> u.readSignatureData(SIGNATURE_FIELD, provider)
            );
        }
    }

    private void doRoundTrip(String digestAlgo, String signatureAlgo, String outFileName, IApplicableSignatureParams params)
            throws GeneralSecurityException, IOException {
        String outFile = Paths.get(DESTINATION_FOLDER, outFileName).toString();
        doSign(digestAlgo, signatureAlgo, outFile, params);
        doVerify(outFile);
    }

    private void doSign(String digestAlgo, String signatureAlgo, String outFile, IApplicableSignatureParams params)
            throws IOException, GeneralSecurityException {
        // write to a file for easier inspection when debugging
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            Certificate root = readCertificate(Paths.get(SOURCE_FOLDER, "ca.crt"));
            Certificate signerCert = readCertificate(Paths.get(SOURCE_FOLDER, "rsa.crt"));
            Certificate[] signChain = new Certificate[]{signerCert, root};
            PrivateKey signPrivateKey = readPrivateKey(Paths.get(SOURCE_FOLDER, "rsa.key.pem"));
            IExternalSignature pks = new PrivateKeySignature(signPrivateKey, digestAlgo, signatureAlgo, FACTORY.getProviderName(), params);

            PdfSigner signer = new PdfSigner(new PdfReader(SOURCE_FILE), fos, new StampingProperties());
            signer.setFieldName(SIGNATURE_FIELD);
            signer.signDetached(
                    new BouncyCastleDigest(), pks, signChain, null, null, null, 0,
                    PdfSigner.CryptoStandard.CMS);
        }
    }

    private void doVerify(String fileName) throws IOException, GeneralSecurityException {
        try (PdfReader r = new PdfReader(fileName); PdfDocument pdfDoc = new PdfDocument(r)) {
            SignatureUtil u = new SignatureUtil(pdfDoc);
            PdfPKCS7 data = u.readSignatureData(SIGNATURE_FIELD, FACTORY.getProviderName());
            Assert.assertEquals(SecurityIDs.ID_RSASSA_PSS, data.getSignatureMechanismOid());
            Assert.assertTrue(data.verifySignatureIntegrityAndAuthenticity());
        }
    }

    private PrivateKey readPrivateKey(Path path) throws GeneralSecurityException {
        try {
            return PemFileHelper.readFirstKey(path.toString(), SAMPLE_KEY_PASSPHRASE);
        } catch (Exception e) {
            throw new KeyException(e);
        }
    }

    private Certificate readCertificate(Path path) throws IOException, GeneralSecurityException {
        byte[] content = Files.readAllBytes(path);
        IX509CertificateHolder certHolder = FACTORY.createX509CertificateHolder(content);
        return FACTORY.createJcaX509CertificateConverter().getCertificate(certHolder);
    }
}
