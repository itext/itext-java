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
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class PdfSignatureAppearanceTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/";
    public static final String KEYSTORE_PATH = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/test.pem";
    public static final char[] PASSWORD = "testpassphrase".toCharArray();

    private Certificate[] chain;
    private PrivateKey pk;

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        pk = PemFileHelper.readFirstKey(KEYSTORE_PATH, PASSWORD);
        chain = PemFileHelper.readFirstChain(KEYSTORE_PATH);
    }

    @Test
    public void textAutoscaleTest01() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest01.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.DESCRIPTION);

        assertAppearanceFontSize(dest, 13.94f);
    }

    @Test
    public void textAutoscaleTest02() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest02.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 150, 50);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.DESCRIPTION);

        assertAppearanceFontSize(dest, 6.83f);
    }

    @Test
    public void textAutoscaleTest03() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest03.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION);

        assertAppearanceFontSize(dest, 44.35f);
    }

    @Test
    public void textAutoscaleTest04() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest04.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION);

        assertAppearanceFontSize(dest, 21.25f);
    }

    @Test
    public void textAutoscaleTest05() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest05.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);

        assertAppearanceFontSize(dest, 12.77f);
    }

    @Test
    public void textAutoscaleTest06() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest06.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);

        assertAppearanceFontSize(dest, 6.26f);
    }

    @Test
    public void testSigningInAppendModeWithHybridDocument()
            throws IOException, GeneralSecurityException, InterruptedException {
        String src = SOURCE_FOLDER + "hybrid.pdf";
        String dest = DESTINATION_FOLDER + "signed_hybrid.pdf";
        String cmp = SOURCE_FOLDER + "cmp_signed_hybrid.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest),
                new StampingProperties().useAppendMode());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

        appearance.setLayer2FontSize(13.8f)
                .setPageRect(new Rectangle(36, 748, 200, 100))
                .setPageNumber(1)
                .setReason("Test")
                .setLocation("Nagpur");

        signer.setFieldName("Sign1");

        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        // Make sure iText can open the document
        new PdfDocument(new PdfReader(dest)).close();

        // Assert that the document can be rendered correctly
        Assert.assertNull(new CompareTool().compareVisually(dest, cmp, DESTINATION_FOLDER, "diff_",
                getIgnoredAreaTestMap(new Rectangle(36, 748, 200, 100))));
    }

    @Test
    public void fontColorTest01() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "fontColorTest01.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        String src = SOURCE_FOLDER + "simpleDocument.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());
        // Creating the appearance
        signer.getSignatureAppearance()
                .setLayer2FontColor(ColorConstants.RED)
                .setLayer2Text("Verified and signed by me.")
                .setPageRect(rect);

        signer.setFieldName("Signature1");
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Assert.assertNull(new CompareTool().compareVisually(dest, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER,
                "diff_"));
    }

    @Test
    public void signaturesOnRotatedPages() throws IOException, GeneralSecurityException, InterruptedException {
        StringBuilder assertionResults = new StringBuilder();

        for (int i = 1; i <= 4; i++) {
            testSignatureOnRotatedPage(i, PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION,
                    assertionResults);
            testSignatureOnRotatedPage(i, PdfSignatureAppearance.RenderingMode.GRAPHIC, assertionResults);
            testSignatureOnRotatedPage(i, PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION, assertionResults);
            testSignatureOnRotatedPage(i, PdfSignatureAppearance.RenderingMode.DESCRIPTION, assertionResults);
        }

        Assert.assertEquals("", assertionResults.toString());
    }

    @Test
    public void signatureFieldNotMergedWithWidgetTest() throws IOException, GeneralSecurityException {
        try (PdfDocument outputDoc = new PdfDocument(new PdfReader(
                SOURCE_FOLDER + "signatureFieldNotMergedWithWidget.pdf"))) {

            SignatureUtil sigUtil = new SignatureUtil(outputDoc);
            PdfPKCS7 signatureData = sigUtil.readSignatureData("Signature1");
            Assert.assertTrue(signatureData.verifySignatureIntegrityAndAuthenticity());
        }
    }

    @Test
    // TODO: DEVSIX-5162 (the signature is expected to have auto-generated appearance, but now it's empty)
    public void signExistingNotMergedFieldNotReusedAPTest() throws GeneralSecurityException,
            IOException, InterruptedException {
        // Field is not merged with widget and has /P key
        String src = SOURCE_FOLDER + "emptyFieldNotMerged.pdf";
        String fileName = "signExistingNotMergedFieldNotReusedAP.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfReader reader = new PdfReader(src);

        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());
        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        signer.getSignatureAppearance()
                .setLayer2Text("Verified and signed by me.")
                .setReason("Test 1")
                .setLocation("TestCity")
                .setReuseAppearance(false);
        signer.setFieldName("Signature1");

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null,
                0, PdfSigner.CryptoStandard.CADES);

        Assert.assertNull(new CompareTool().compareVisually(
                dest, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    // TODO: DEVSIX-5162 (signature appearance expected to be updated (reused appearance will be used as a background))
    public void signExistingNotMergedFieldReusedAPTest() throws GeneralSecurityException,
            IOException, InterruptedException {
        // Field is not merged with widget and has /P key
        String src = SOURCE_FOLDER + "emptyFieldNotMerged.pdf";
        String fileName = "signExistingNotMergedFieldReusedAP.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfReader reader = new PdfReader(src);

        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());
        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        signer.getSignatureAppearance()
                .setLayer2Text("Verified and signed by me.")
                .setReason("Test 1")
                .setLocation("TestCity")
                .setReuseAppearance(true);
        signer.setFieldName("Signature1");

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null,
                0, PdfSigner.CryptoStandard.CADES);

        Assert.assertNull(new CompareTool().compareVisually(
                dest, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    // TODO: DEVSIX-5162 (remove expected exception after fix)
    public void signExistingNotMergedFieldReusedAPEntryNDicTest() throws IOException {
        // Field is not merged with widget and has /P key
        String src = SOURCE_FOLDER + "emptyFieldNotMergedEntryNDict.pdf";
        String fileName = "signExistingNotMergedFieldReusedAPEntryNDic.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfReader reader = new PdfReader(src);

        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new StampingProperties());
        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        signer.getSignatureAppearance()
                .setLayer2Text("Verified and signed by me.")
                .setReason("Test 1")
                .setLocation("TestCity")
                .setReuseAppearance(true);
        signer.setFieldName("Signature1");

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());

        Assert.assertThrows(NullPointerException.class, () -> signer.signDetached(new BouncyCastleDigest(),
                pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES));
    }

    @Test
    public void layer0Test() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";
        String fileName = "layer0Test.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        signer.setFieldName("Signature1");

        Rectangle rect = new Rectangle(0, 600, 100, 100);
        appearance.setPageRect(rect);
        // If we do not set any text, the text will be generated and the current date will be used,
        // which we want to avoid because of visual comparison
        appearance.setLayer2Text("Hello");

        PdfFormXObject layer0 = appearance.getLayer0();

        // Draw red rectangle with blue border
        new PdfCanvas(layer0, signer.getDocument())
                .saveState()
                .setFillColor(ColorConstants.RED)
                .setStrokeColor(ColorConstants.BLUE)
                .rectangle(0, 0, 100, 100)
                .fillStroke()
                .restoreState();

        // Get the same layer once more, so that the logic when n0 is not null is triggered
        layer0 = appearance.getLayer0();

        // Draw yellow circle with black border
        new PdfCanvas(layer0, signer.getDocument())
                .saveState()
                .setFillColor(ColorConstants.YELLOW)
                .setStrokeColor(ColorConstants.BLACK)
                .circle(50, 50, 50)
                .fillStroke()
                .restoreState();

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        compareSignatureAppearances(dest, SOURCE_FOLDER + "cmp_" + fileName);
    }

    @Test
    public void layer0WithImageTest() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";
        String fileName = "layer0WithImageTest.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setImage(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));
        signer.setFieldName("Signature1");

        Rectangle rect = new Rectangle(0, 600, 100, 100);
        appearance.setPageRect(rect);
        // If we do not set any text, the text will be generated and the current date will be used,
        // which we want to avoid because of visual comparison
        appearance.setLayer2Text("Hello");

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        compareSignatureAppearances(dest, SOURCE_FOLDER + "cmp_" + fileName);
    }

    @Test
    public void layer0WithImageAndPositiveImageScaleTest() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";
        String fileName = "layer0WithImageAndPositiveImageScaleTest.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setImage(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));
        appearance.setImageScale(1.5F);
        signer.setFieldName("Signature1");

        Rectangle rect = new Rectangle(0, 600, 100, 100);
        appearance.setPageRect(rect);
        // If we do not set any text, the text will be generated and the current date will be used,
        // which we want to avoid because of visual comparison
        appearance.setLayer2Text("Hello");

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        compareSignatureAppearances(dest, SOURCE_FOLDER + "cmp_" + fileName);
    }

    @Test
    public void layer0WithImageAndNegativeImageScaleTest() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";
        String fileName = "layer0WithImageAndNegativeImageScale.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance.setImage(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));
        appearance.setImageScale(-15F);
        signer.setFieldName("Signature1");

        Rectangle rect = new Rectangle(0, 600, 100, 100);
        appearance.setPageRect(rect);
        // If we do not set any text, the text will be generated and the current date will be used,
        // which we want to avoid because of visual comparison
        appearance.setLayer2Text("Hello");

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        compareSignatureAppearances(dest, SOURCE_FOLDER + "cmp_" + fileName);
    }

    @Test
    public void layer2Test() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";
        String fileName = "layer2Test.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        signer.setFieldName("Signature1");

        Rectangle rect = new Rectangle(0, 600, 100, 100);
        appearance.setPageRect(rect);

        PdfFormXObject layer2 = appearance.getLayer2();

        // Draw red rectangle with blue border
        new PdfCanvas(layer2, signer.getDocument())
                .saveState()
                .setFillColor(ColorConstants.RED)
                .setStrokeColor(ColorConstants.BLUE)
                .rectangle(0, 0, 100, 100)
                .fillStroke()
                .restoreState();

        // Get the same layer once more, so that the logic when n0 is not null is triggered
        layer2 = appearance.getLayer2();

        // Draw yellow circle with black border
        new PdfCanvas(layer2, signer.getDocument())
                .saveState()
                .setFillColor(ColorConstants.YELLOW)
                .setStrokeColor(ColorConstants.BLACK)
                .circle(50, 50, 50)
                .fillStroke()
                .restoreState();

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        compareSignatureAppearances(dest, SOURCE_FOLDER + "cmp_" + fileName);
    }

    private static void compareSignatureAppearances(String outPdf, String cmpPdf) throws IOException {
        ITextTest.printOutCmpPdfNameAndDir(outPdf, cmpPdf);
        try (PdfDocument outDoc = new PdfDocument(new PdfReader(outPdf))) {
            try (PdfDocument cmpDoc = new PdfDocument(new PdfReader(cmpPdf))) {
                PdfDictionary outN = (PdfDictionary) PdfFormCreator.getAcroForm(outDoc, false)
                        .getField("Signature1").getPdfObject()
                        .getAsDictionary(PdfName.AP).get(PdfName.N).getIndirectReference().getRefersTo();
                PdfDictionary cmpN = (PdfDictionary) PdfFormCreator.getAcroForm(cmpDoc, false)
                        .getField("Signature1").getPdfObject()
                        .getAsDictionary(PdfName.AP).get(PdfName.N).getIndirectReference().getRefersTo();
                Assert.assertNull(new CompareTool().compareDictionariesStructure(outN, cmpN));
            }
        }
    }

    private void testSignatureOnRotatedPage(int pageNum, PdfSignatureAppearance.RenderingMode renderingMode,
            StringBuilder assertionResults) throws IOException, GeneralSecurityException, InterruptedException {
        String fileName = "signaturesOnRotatedPages" + pageNum + "_mode_" + renderingMode.name() + ".pdf";
        String src = SOURCE_FOLDER + "documentWithRotatedPages.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest),
                new StampingProperties().useAppendMode());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

        appearance
                .setLayer2Text("Digitally signed by Test User. All rights reserved. Take care!")
                .setPageRect(new Rectangle(100, 100, 100, 50))
                .setRenderingMode(renderingMode)
                .setSignatureGraphic(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"))
                .setPageNumber(pageNum);

        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        // Make sure iText can open the document
        new PdfDocument(new PdfReader(dest)).close();

        try {
            // TODO DEVSIX-864 compareVisually() should be changed to compareByContent() because it slows down the test
            String testResult = new CompareTool().compareVisually(dest, SOURCE_FOLDER + "cmp_" + fileName,
                    DESTINATION_FOLDER, "diff_");
            if (null != testResult) {
                assertionResults.append(testResult);
            }
        } catch (CompareTool.CompareToolExecutionException e) {
            assertionResults.append(e.getMessage());
        }
    }

    private void testSignatureAppearanceAutoscale(String dest, Rectangle rect,
            PdfSignatureAppearance.RenderingMode renderingMode) throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());
        // Creating the appearance
        signer.getSignatureAppearance()
                .setLayer2FontSize(0)
                .setReason("Test 1")
                .setLocation("TestCity")
                .setPageRect(rect)
                .setRenderingMode(renderingMode)
                .setSignatureGraphic(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));

        signer.setFieldName("Signature1");
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
    }

    private static void assertAppearanceFontSize(String filename, float expectedFontSize) throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(filename));
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, false);
        PdfStream stream = acroForm.getField("Signature1").getWidgets().get(0).getNormalAppearanceObject().
                getAsDictionary(PdfName.Resources).getAsDictionary(PdfName.XObject)
                .getAsStream(new PdfName("FRM")).getAsDictionary(PdfName.Resources)
                .getAsDictionary(PdfName.XObject).getAsStream(new PdfName("n2"));
        String[] streamContents = new String(stream.getBytes()).split("\\s");
        String fontSize = null;
        for (int i = 1; i < streamContents.length; i++) {
            if ("Tf".equals(streamContents[i])) {
                fontSize = streamContents[i - 1];
                break;
            }
        }
        float foundFontSize = Float.parseFloat(fontSize);
        Assert.assertTrue(MessageFormatUtil.format("Font size: exptected {0}, found {1}",
                expectedFontSize, fontSize), Math.abs(foundFontSize - expectedFontSize) < 0.1 * expectedFontSize);
    }

    private static Map<Integer, List<Rectangle>> getIgnoredAreaTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }

}
