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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class SequentialSignaturesTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/SequentialSignaturesTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/SequentialSignaturesTest/";

    private static final char[] password = "testpassphrase".toCharArray();

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void sequentialSignOfFileWithAnnots()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        String outFileName = destinationFolder + "sequentialSignOfFileWithAnnots.pdf";
        String srcFileName = sourceFolder + "signedWithAnnots.pdf";
        String cmpFileName = sourceFolder + "cmp_sequentialSignOfFileWithAnnots.pdf";

        Certificate[] signChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        IExternalSignature pks =
                new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256, FACTORY.getProviderName());

        String signatureName = "Signature2";
        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), FileUtil.getFileOutputStream(outFileName), new StampingProperties().useAppendMode());
        signer.setFieldName(signatureName);
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(signatureName)
                .setContent("Approval test signature.\nCreated by iText.");
        signer
                .setPageRect(new Rectangle(50, 350, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);

        signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        TestSignUtils.basicCheckSignedDoc(outFileName, signatureName);
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void secondSignOfTaggedDocTest()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        String outFileName = destinationFolder + "secondSignOfTagged.pdf";
        String srcFileName = sourceFolder + "taggedAndSignedDoc.pdf";
        String cmpFileName = sourceFolder + "cmp_secondSignOfTagged.pdf";

        Certificate[] signChain = PemFileHelper.readFirstChain(signCertFileName);

        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);

        IExternalSignature pks = new PrivateKeySignature(signPrivateKey, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());

        String signatureName = "Signature2";
        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), FileUtil.getFileOutputStream(outFileName),
                new StampingProperties().useAppendMode());

        PdfDocument document = signer.getDocument();
        document.getWriter().setCompressionLevel(CompressionConstants.NO_COMPRESSION);

        signer.setFieldName(signatureName);
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(signatureName)
                .setContent("Approval test signature #2.\nCreated by iText.");
        signer.setPageNumber(1);
        signer.setPageRect(new Rectangle(50, 550, 200, 100))
                .setReason("Test2")
                .setLocation("TestCity2")
                .setSignatureAppearance(appearance);

        signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null,
                null, 0, CryptoStandard.CADES);

        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature1");
        TestSignUtils.basicCheckSignedDoc(outFileName, "Signature2");

        try (PdfDocument twiceSigned = new PdfDocument(new PdfReader(outFileName));
             PdfDocument resource = new PdfDocument(new PdfReader(srcFileName))) {

            float resourceStrElemNumber = resource.getStructTreeRoot().getPdfObject().getAsArray(PdfName.K)
                    .getAsDictionary(0).getAsArray(PdfName.K).size();

            float outStrElemNumber = twiceSigned.getStructTreeRoot().getPdfObject().getAsArray(PdfName.K)
                    .getAsDictionary(0).getAsArray(PdfName.K).size();

            // Here we assert the amount of objects in StructTreeRoot in resource file and twice signed file
            // as the original signature validation failed by Adobe because of struct tree change. If the fix
            // would make this tree unchanged, then the assertion should be adjusted with comparing the tree of
            // objects in StructTreeRoot to ensure that it won't be changed.
            Assertions.assertNotEquals(resourceStrElemNumber, outStrElemNumber);
        }

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }
}
