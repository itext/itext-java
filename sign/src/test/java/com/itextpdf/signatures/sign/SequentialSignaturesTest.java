/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.signatures.sign;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PdfSigner.CryptoStandard;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class SequentialSignaturesTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/SequentialSignaturesTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/SequentialSignaturesTest/";

    private static final char[] password = "testpassphrase".toCharArray();

    @BeforeClass
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
        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), new FileOutputStream(outFileName), new StampingProperties().useAppendMode());
        signer.setFieldName(signatureName);
        signer.getSignatureAppearance()
                .setPageRect(new Rectangle(50, 350, 200, 100))
                .setReason("Test")
                .setLocation("TestCity")
                .setLayer2Text("Approval test signature.\nCreated by iText7.");

        signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        PadesSigTest.basicCheckSignedDoc(outFileName, signatureName);
        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
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
        PdfSigner signer = new PdfSigner(new PdfReader(srcFileName), new FileOutputStream(outFileName),
                new StampingProperties().useAppendMode());

        PdfDocument document = signer.getDocument();
        document.getWriter().setCompressionLevel(CompressionConstants.NO_COMPRESSION);

        signer.setFieldName(signatureName);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setPageNumber(1);
        signer.getSignatureAppearance()
                .setPageRect(new Rectangle(50, 550, 200, 100))
                .setReason("Test2")
                .setLocation("TestCity2")
                .setLayer2Text("Approval test signature #2.\nCreated by iText7.");

        signer.signDetached(new BouncyCastleDigest(), pks, signChain, null, null,
                null, 0, CryptoStandard.CADES);

        PadesSigTest.basicCheckSignedDoc(outFileName, "Signature1");
        PadesSigTest.basicCheckSignedDoc(outFileName, "Signature2");

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
            Assert.assertNotEquals(resourceStrElemNumber, outStrElemNumber);
        }

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }
}
