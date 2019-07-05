/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.LtvVerifier;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.test.signutils.Pkcs12FileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: add some validation of results in future
@Category(IntegrationTest.class)
public class SigningTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/SigningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/SigningTest/";
    public static final String keystorePath = "./src/test/resources/com/itextpdf/signatures/sign/SigningTest/test.p12";
    public static final char[] password = "kspass".toCharArray();

    private Certificate[] chain;
    private PrivateKey pk;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Before
    public void init() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        pk = Pkcs12FileHelper.readFirstKey(keystorePath, password, password);
        chain = Pkcs12FileHelper.readFirstChain(keystorePath, password);
    }

    @Test
    public void simpleSigningTest() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleDocument.pdf";
        String fileName = "simpleSignature.pdf";
        String dest = destinationFolder + fileName;

        int x = 36;
        int y = 648;
        int w = 200;
        int h = 100;
        Rectangle rect = new Rectangle(x, y, w, h);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, false, PdfSigner.NOT_CERTIFIED, 12f);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 690, 155, 15))));
    }

    @Test
    public void signingIntoExistingFieldTest01() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "emptySignature01.pdf"; //field is merged with widget and has /P key
        String fileName = "filledSignatureFields01.pdf";
        String dest = destinationFolder + fileName;

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, false, false, PdfSigner.NOT_CERTIFIED, 12f);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 725, 200, 15))));
    }

    @Test
    public void signingIntoExistingFieldTest02() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "emptySignature02.pdf"; //field is merged with widget and widget doesn't have /P key
        String fileName = "filledSignatureFields02.pdf";
        String dest = destinationFolder + fileName;

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, false, false, PdfSigner.NOT_CERTIFIED, 12f);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 725, 200, 15))));

    }

    @Test
    public void signingIntoExistingReuseAppearanceTest() throws GeneralSecurityException, IOException {
        String src = sourceFolder + "emptySigWithAppearance.pdf";
        String dest = destinationFolder + "filledSignatureReuseAppearanceFields.pdf";

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, true, false);
    }

    @Test
    public void signingTaggedDocument() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleTaggedDocument.pdf";
        String dest = destinationFolder + "signedTaggedDocument.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, false);
    }

    @Test
    public void signingTaggedDocumentAppendMode() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleTaggedDocument.pdf";
        String dest = destinationFolder + "signedTaggedDocumentAppendMode.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true);
    }

    @Test
    public void signingDocumentAppendModeIndirectPageAnnots() throws GeneralSecurityException, IOException, InterruptedException {
        String file = "AnnotsIndirect.pdf";
        String src = sourceFolder + file;
        String dest = destinationFolder + "signed" + file;

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true, PdfSigner.NOT_CERTIFIED, 12f);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + file, destinationFolder,
                "diff_", getTestMap(new Rectangle(30, 245, 200, 12))));
    }

    @Test
    public void signPdf2Cms() throws GeneralSecurityException, IOException, InterruptedException {
        String file = "simpleDocPdf2.pdf";
        String src = sourceFolder + file;
        String dest = destinationFolder + "signedCms_" + file;

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CMS, "Test 1", "TestCity", rect, false, true, PdfSigner.NOT_CERTIFIED, 12f);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_signedCms_" + file, destinationFolder,
                "diff_", getTestMap(new Rectangle(30, 245, 200, 12))));
    }

    @Test
    public void signPdf2Cades() throws GeneralSecurityException, IOException, InterruptedException {
        String file = "simpleDocPdf2.pdf";
        String src = sourceFolder + file;
        String dest = destinationFolder + "signedCades_" + file;

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.RIPEMD160,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true, PdfSigner.NOT_CERTIFIED, 12f);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_signedCades_" + file, destinationFolder,
                "diff_", getTestMap(new Rectangle(30, 245, 200, 12))));
    }

    @Test
    public void signPdf2CertificationAfterApproval() throws GeneralSecurityException, IOException, InterruptedException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.CertificationSignatureCreationFailedDocShallNotContainSigs);

        String srcFile = "approvalSignedDocPdf2.pdf";
        String file = "signedPdf2CertificationAfterApproval.pdf";
        String src = sourceFolder + srcFile;
        String dest = destinationFolder + file;

        Rectangle rect = new Rectangle(30, 50, 200, 100);

        String fieldName = "Signature2";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.RIPEMD160,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true, PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED, null);
    }

    @Test
    public void signedTwicePdf2Test() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleDocPdf2.pdf";
        String fileName1 = "signedOnce.pdf";
        String fileName2 = "updated.pdf";
        String fileName3 = "signedTwice.pdf";

        // sign document
        Rectangle rectangle1 = new Rectangle(36, 100, 200, 100);
        sign(src, "Signature1", destinationFolder + fileName1, chain, pk,
                DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Sign 1", "TestCity", rectangle1, false, true);

        // update document
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(destinationFolder + fileName1), new PdfWriter(destinationFolder + fileName2), new StampingProperties().useAppendMode());
        pdfDoc.addNewPage();
        pdfDoc.close();

        // sign document again
        Rectangle rectangle2 = new Rectangle(236, 100, 200, 100);
        sign(destinationFolder + fileName2, "Signature2", destinationFolder + fileName3, chain, pk,
                DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Sign 2", "TestCity", rectangle2, false, true);
        Map<Integer, List<Rectangle>> map = new HashMap<>();
        List<Rectangle> list = new ArrayList<>();
        list.add(rectangle1);
        list.add(rectangle2);
        map.put(1, list);

        Assert.assertNull(new CompareTool().compareVisually(destinationFolder + fileName3, sourceFolder + "cmp_" + fileName3, destinationFolder,
                "diff_", map));
    }

    @Test
    public void signEncryptedDoc01() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "encrypted.pdf";
        String src = sourceFolder + fileName;
        String dest = destinationFolder + "signed_" + fileName;

        String fieldName = "Signature1";

        byte[] ownerPass = "World".getBytes();
        PdfReader reader = new PdfReader(src, new ReaderProperties().setPassword(ownerPass));
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties().useAppendMode());

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason("Test1")
                .setLocation("TestCity");

        signer.setFieldName(fieldName);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(dest, new ReaderProperties().setPassword(ownerPass))));
        verifier.setVerifyRootCertificate(false);
        verifier.verify(null);

        // TODO improve checking in future. At the moment, if the certificate or the signature itself has problems exception will be thrown
    }

    @Test
    public void signEncryptedDoc02() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "encrypted_cert.pdf";
        String src = sourceFolder + fileName;
        String dest = destinationFolder + "signed_" + fileName;

        Certificate cert = CryptoUtil.readPublicCertificate(new FileInputStream(sourceFolder + "test.cer"));
        PrivateKey privateKey = Pkcs12FileHelper.readFirstKey(sourceFolder + "test.p12", password, password);
        PdfReader reader = new PdfReader(src, new ReaderProperties().setPublicKeySecurityParams(cert, privateKey, new BouncyCastleProvider().getName(), null));
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties().useAppendMode());

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        // TODO improve testing, e.g. check ID. For not at least we assert that exception is not thrown
    }

    protected void sign(String src, String name, String dest,
                        Certificate[] chain, PrivateKey pk,
                        String digestAlgorithm, PdfSigner.CryptoStandard subfilter,
                        String reason, String location, Rectangle rectangleForNewField, boolean setReuseAppearance, boolean isAppendMode) throws GeneralSecurityException, IOException {
        sign(src, name, dest, chain, pk, digestAlgorithm, subfilter, reason, location, rectangleForNewField, setReuseAppearance, isAppendMode, PdfSigner.NOT_CERTIFIED, null);
    }

    protected void sign(String src, String name, String dest,
                        Certificate[] chain, PrivateKey pk,
                        String digestAlgorithm, PdfSigner.CryptoStandard subfilter,
                        String reason, String location, Rectangle rectangleForNewField, boolean setReuseAppearance, boolean isAppendMode, int certificationLevel, Float fontSize)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);
        StampingProperties properties = new StampingProperties();
        if (isAppendMode) {
            properties.useAppendMode();
        }
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), properties);

        signer.setCertificationLevel(certificationLevel);

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location)
                .setReuseAppearance(setReuseAppearance);

        if (rectangleForNewField != null) {
            appearance.setPageRect(rectangleForNewField);
        }
        if (fontSize != null) {
            appearance.setLayer2FontSize((float) fontSize);
        }

        signer.setFieldName(name);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, subfilter);
    }

    private static Map<Integer, List<Rectangle>> getTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }
}
