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
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDEROctetString;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignaturePolicyInfo;
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class PadesSigTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PadesSigTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PadesSigTest/";

    private static final char[] password = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void padesRsaSigTest01()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        signApproval(certsSrc + "signCertRsa01.pem", destinationFolder + "padesRsaSigTest01.pdf");

        TestSignUtils.basicCheckSignedDoc(destinationFolder + "padesRsaSigTest01.pdf", "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(destinationFolder
                + "padesRsaSigTest01.pdf", sourceFolder + "cmp_padesRsaSigTest01.pdf"));
    }

    @Test
    public void padesRsaSigTestWithChain01()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        signApproval(certsSrc + "signCertRsaWithChain.pem", destinationFolder + "padesRsaSigTestWithChain01.pdf");

        TestSignUtils.basicCheckSignedDoc(destinationFolder + "padesRsaSigTestWithChain01.pdf", "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(destinationFolder
                + "padesRsaSigTestWithChain01.pdf", sourceFolder + "cmp_padesRsaSigTestWithChain01.pdf"));
    }

    @Test
    @Disabled("DEVSIX-1620: For some reason signatures created with the given cert (either by iText or acrobat) are considered invalid")
    public void padesDsaSigTest01()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        signApproval(certsSrc + "signCertDsa01.pem", destinationFolder + "padesDsaSigTest01.pdf");
    }

    @Test
    public void padesEccSigTest01()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        signApproval(certsSrc + "signCertEcc01.pem",
                destinationFolder + "padesEccSigTest01.pdf");

        TestSignUtils.basicCheckSignedDoc(destinationFolder + "padesEccSigTest01.pdf", "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(destinationFolder
                + "padesEccSigTest01.pdf", sourceFolder + "cmp_padesEccSigTest01.pdf"));
    }

    @Test
    public void padesEpesProfileTest01()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String notExistingSignaturePolicyOid = "2.16.724.631.3.1.124.2.29.9";
        IASN1ObjectIdentifier asn1PolicyOid = FACTORY.createASN1ObjectIdentifierInstance(
                FACTORY.createASN1ObjectIdentifier(notExistingSignaturePolicyOid));
        IAlgorithmIdentifier hashAlg = FACTORY.createAlgorithmIdentifier(
                FACTORY.createASN1ObjectIdentifier(DigestAlgorithms.getAllowedDigest("SHA1")));

        // indicate that the policy hash value is not known; see ETSI TS 101 733 V2.2.1, 5.8.1
        byte[] zeroSigPolicyHash = {0};
        IDEROctetString hash = FACTORY.createDEROctetString(zeroSigPolicyHash);

        ISignaturePolicyId signaturePolicyId =
                FACTORY.createSignaturePolicyId(asn1PolicyOid, FACTORY.createOtherHashAlgAndValue(hashAlg, hash));
        ISignaturePolicyIdentifier sigPolicyIdentifier = FACTORY.createSignaturePolicyIdentifier(signaturePolicyId);

        signApproval(certsSrc + "signCertRsa01.pem", destinationFolder + "padesEpesProfileTest01.pdf", sigPolicyIdentifier);

        TestSignUtils.basicCheckSignedDoc(destinationFolder + "padesEpesProfileTest01.pdf", "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(destinationFolder +
                "padesEpesProfileTest01.pdf", sourceFolder + "cmp_padesEpesProfileTest01.pdf"));
    }

    @Test
    public void signaturePolicyInfoUnavailableUrlTest()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String signedFileName = destinationFolder + "signaturePolicyInfoUnavailableUrl_signed.pdf";

        SignaturePolicyInfo spi = new SignaturePolicyInfo("1.2.3.4.5.6.7.8.9.10",
                "aVRleHQ0TGlmZVJhbmRvbVRleHQ=", "SHA-1",
                "https://signature-policy.org/not-available");

        signApproval(certsSrc + "signCertRsa01.pem", signedFileName, spi);

        TestSignUtils.basicCheckSignedDoc(signedFileName, "Signature1");
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(signedFileName,
                sourceFolder + "cmp_signaturePolicyInfoUnavailableUrl_signed.pdf"));
    }

    private void signApproval(String signCertFileName, String outFileName)
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        signApproval(signCertFileName, outFileName, null, null);
    }

    private void signApproval(String signCertFileName, String outFileName, SignaturePolicyInfo signaturePolicyInfo)
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        signApproval(signCertFileName, outFileName, null, signaturePolicyInfo);
    }

    private void signApproval(String signCertFileName, String outFileName, ISignaturePolicyIdentifier signaturePolicyId)
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        signApproval(signCertFileName, outFileName, signaturePolicyId, null);
    }

    private void signApproval(String signCertFileName, String outFileName,
            ISignaturePolicyIdentifier sigPolicyIdentifier,
            SignaturePolicyInfo sigPolicyInfo)
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String srcFileName = sourceFolder + "helloWorldDoc.pdf";
        Certificate[] signChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        IExternalSignature pks =
                new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());

        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), FileUtil.getFileOutputStream(outFileName), new StampingProperties());
        signer.setFieldName("Signature1");
        signer.getSignatureAppearance()
                .setPageRect(new Rectangle(50, 650, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setLayer2Text("Approval test signature.\nCreated by iText.");

        if (sigPolicyIdentifier != null) {
            signer.signDetached(pks, signChain, null, null, null, 0,
                    PdfSigner.CryptoStandard.CADES, sigPolicyIdentifier);
        } else if (sigPolicyInfo != null) {
            signer.signDetached(pks, signChain, null, null, null, 0,
                    PdfSigner.CryptoStandard.CADES, sigPolicyInfo);
        } else {
            signer.signDetached(pks, signChain, null, null, null, 0,
                    PdfSigner.CryptoStandard.CADES);
        }
    }
}
