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
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.BackgroundPosition;
import com.itextpdf.layout.properties.BackgroundRepeat;
import com.itextpdf.layout.properties.BackgroundSize;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.signatures.AccessPermissions;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class SignatureAppearanceTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/SignatureAppearanceTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/SignatureAppearanceTest/";
    public static final String KEYSTORE_PATH = "./src/test/resources/com/itextpdf/signatures/sign/SignatureAppearanceTest/test.pem";
    public static final char[] PASSWORD = "testpassphrase".toCharArray();

    private Certificate[] chain;
    private PrivateKey pk;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
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
        testSignatureAppearanceAutoscale(dest, rect, null, null);

        assertAppearanceFontSize(dest, 13.72f);
    }

    @Test
    public void textAutoscaleTest02() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest02.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 150, 50);
        testSignatureAppearanceAutoscale(dest, rect, null, null);

        assertAppearanceFontSize(dest, 7.73f);
    }

    @Test
    public void textAutoscaleTest03() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest03.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        testSignatureAppearanceAutoscale(dest, rect, "SignerName", null);

        assertAppearanceFontSize(dest, 44.35f);
    }

    @Test
    public void textAutoscaleTest04() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest04.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        testSignatureAppearanceAutoscale(dest, rect, "SignerName", null);

        assertAppearanceFontSize(dest, 21.25f);
    }

    @Test
    public void textAutoscaleTest05() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest05.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        testSignatureAppearanceAutoscale(dest, rect, null, ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));

        assertAppearanceFontSize(dest, 12.77f);
    }

    @Test
    public void textAutoscaleTest06() throws GeneralSecurityException, IOException {
        String fileName = "textAutoscaleTest06.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        testSignatureAppearanceAutoscale(dest, rect, null, ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));

        assertAppearanceFontSize(dest, 6.26f);
    }

    @Test
    public void testSigningInAppendModeWithHybridDocument()
            throws IOException, GeneralSecurityException, InterruptedException {
        String src = SOURCE_FOLDER + "hybrid.pdf";
        String dest = DESTINATION_FOLDER + "signed_hybrid.pdf";
        String cmp = SOURCE_FOLDER + "cmp_signed_hybrid.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), FileUtil.getFileOutputStream(dest),
                new StampingProperties().useAppendMode());

        String fieldName = "Sign1";

        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent(new SignedAppearanceText()).setFontSize(13.8f);

        SignerProperties signerProperties = new SignerProperties()
                .setFieldName(fieldName)
                .setReason("Test")
                .setLocation("Nagpur")
                .setPageRect(new Rectangle(36, 748, 250, 100))
                .setPageNumber(1)
                .setSignatureAppearance(appearance)
                .setCertificationLevel(AccessPermissions.UNSPECIFIED);
        signer.setSignerProperties(signerProperties);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        // Make sure iText can open the document
        new PdfDocument(new PdfReader(dest)).close();

        // Assert that the document can be rendered correctly
        Assertions.assertNull(new CompareTool().compareVisually(dest, cmp, DESTINATION_FOLDER, "diff_",
                getIgnoredAreaTestMap(new Rectangle(36, 748, 250, 100))));
    }

    @Test
    public void fontColorTest01() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "fontColorTest01.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        String src = SOURCE_FOLDER + "simpleDocument.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties().setFieldName("Signature1");
        signer.setSignerProperties(signerProperties);
        // Creating the appearance
        signerProperties
                .setPageRect(rect)
                .setSignatureAppearance(new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                        .setFontColor(ColorConstants.RED)
                        .setContent("Verified and signed by me."));

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(new CompareTool().compareVisually(dest, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER,
                "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT), ignore = true)
    public void signaturesOnRotatedPages() throws IOException, GeneralSecurityException, InterruptedException {
        StringBuilder assertionResults = new StringBuilder();

        for (int i = 1; i <= 4; i++) {
            testSignatureOnRotatedPage(i, true, false, true, assertionResults);
            testSignatureOnRotatedPage(i, false, false, true, assertionResults);
            testSignatureOnRotatedPage(i, true, true, false, assertionResults);
            testSignatureOnRotatedPage(i, true, false, false, assertionResults);
        }

        Assertions.assertEquals("", assertionResults.toString());
    }

    @Test
    public void signatureFieldNotMergedWithWidgetTest() throws IOException, GeneralSecurityException {
        try (PdfDocument outputDoc = new PdfDocument(new PdfReader(
                SOURCE_FOLDER + "signatureFieldNotMergedWithWidget.pdf"))) {

            SignatureUtil sigUtil = new SignatureUtil(outputDoc);
            PdfPKCS7 signatureData = sigUtil.readSignatureData("Signature1");
            Assertions.assertTrue(signatureData.verifySignatureIntegrityAndAuthenticity());
        }
    }

    @Test
    public void signExistingNotMergedFieldNotReusedAPTest() throws GeneralSecurityException,
            IOException, InterruptedException {
        // Field is not merged with widget and has /P key
        String src = SOURCE_FOLDER + "emptyFieldNotMerged.pdf";
        String fileName = "signExistingNotMergedFieldNotReusedAP.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfReader reader = new PdfReader(src);

        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties()
                .setFieldName("Signature1")
                .setCertificationLevel(AccessPermissions.UNSPECIFIED)
                .setReason("Test 1")
                .setLocation("TestCity");
        signer.setSignerProperties(signerProperties);
        signerProperties.setSignatureAppearance(new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                        .setContent("Verified and signed by me."));
        signer.getSignatureField().setReuseAppearance(false);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null,
                0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(new CompareTool().compareVisually(
                dest, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void signExistingNotMergedFieldReusedAPTest() throws GeneralSecurityException,
            IOException, InterruptedException {
        // Field is not merged with widget and has /P key
        String src = SOURCE_FOLDER + "emptyFieldNotMerged.pdf";
        String fileName = "signExistingNotMergedFieldReusedAP.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfReader reader = new PdfReader(src);

        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties()
                .setCertificationLevel(AccessPermissions.UNSPECIFIED)
                .setFieldName("Signature1");
        signer.setSignerProperties(signerProperties);
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent("SIGNED")
                .setFontColor(ColorConstants.GREEN);
        appearance.setProperty(Property.VERTICAL_ALIGNMENT, VerticalAlignment.MIDDLE);
        signerProperties.setReason("Test 1")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);
        signer.getSignatureField().setReuseAppearance(true);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null,
                0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(new CompareTool().compareVisually(
                dest, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void signExistingNotMergedFieldReusedAPEntryNDicTest()
            throws IOException, GeneralSecurityException, InterruptedException {
        // Field is not merged with widget and has /P key
        String src = SOURCE_FOLDER + "emptyFieldNotMergedEntryNDict.pdf";
        String fileName = "signExistingNotMergedFieldReusedAPEntryNDic.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfReader reader = new PdfReader(src);

        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties()
                .setCertificationLevel(AccessPermissions.UNSPECIFIED)
                .setFieldName("Signature1")
                .setReason("Test 1")
                .setLocation("TestCity");
        signer.setSignerProperties(signerProperties);
        signerProperties.setSignatureAppearance(new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                        .setContent("Verified and signed by me."));
        signer.getSignatureField().setReuseAppearance(true);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());

        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(new CompareTool().compareVisually(
                dest, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void backgroundImageTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "signatureFieldBackground.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signatureFieldBackground.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SignatureFieldAppearance field1 = new SignatureFieldAppearance("field1");
            field1.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field1.setContent("scale -1").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setBorder(new SolidBorder(ColorConstants.RED, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            applyBackgroundImage(field1, ImageDataFactory.create(SOURCE_FOLDER + "1.png"), -1);
            document.add(field1);

            SignatureFieldAppearance field2 = new SignatureFieldAppearance("field2");
            field2.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field2.setContent("scale 0").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            applyBackgroundImage(field2, ImageDataFactory.create(SOURCE_FOLDER + "1.png"), 0);
            document.add(field2);

            SignatureFieldAppearance field3 = new SignatureFieldAppearance("field3");
            field3.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field3.setContent("scale 0.5").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            applyBackgroundImage(field3, ImageDataFactory.create(SOURCE_FOLDER + "1.png"), 0.5f);
            document.add(field3);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    @Test
    public void createAndSignSignatureFieldTest() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "noSignatureField.pdf";
        String dest = DESTINATION_FOLDER + "createdAndSignedSignatureField.pdf";
        String fieldName = "Signature1";

        String unsignedDoc = DESTINATION_FOLDER + "unsignedSignatureField.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(src), new PdfWriter(unsignedDoc));

        PdfSignatureFormField field = new SignatureFormFieldBuilder(document, fieldName)
                .setPage(1).setWidgetRectangle(new Rectangle(45, 509, 517, 179)).createSignature();
        PdfFormCreator.getAcroForm(document, true).addField(field);
        document.close();

        PdfSigner signer = new PdfSigner(new PdfReader(unsignedDoc), FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties().setFieldName(fieldName);
        signer.setSignerProperties(signerProperties);
        // Creating the appearance
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent("Test signature field appearance. Test signature field appearance. " +
                        "Test signature field appearance. Test signature field appearance");
        signerProperties
                .setReason("Appearance is tested")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        compareSignatureAppearances(dest, SOURCE_FOLDER + "cmp_createdAndSignedSignatureField.pdf");
    }

    @Test
    public void signExistedSignatureFieldTest() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "unsignedSignatureField.pdf";
        String fileName = "signedSignatureField.pdf";
        String dest = DESTINATION_FOLDER + fileName;

        PdfSigner signer = new PdfSigner(new PdfReader(src), FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties().setFieldName("Signature1");
        signer.setSignerProperties(signerProperties);

        // Creating the appearance
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent("Test signature field appearance. Test signature field appearance. " +
                        "Test signature field appearance. Test signature field appearance");
        signerProperties.setReason("Appearance is tested")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);
        signer.getSignatureField().setReuseAppearance(true);

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        compareSignatureAppearances(dest, SOURCE_FOLDER + "cmp_" + fileName);
    }

    @Test
    public void reuseAppearanceTest() throws GeneralSecurityException,
            IOException, InterruptedException {
        // Field is not merged with widget and has /P key
        String src = SOURCE_FOLDER + "emptyFieldNotMerged.pdf";
        String fileName = "reuseAppearance.pdf";
        testReuseAppearance(src, fileName);
    }

    @Test
    public void fieldLayersTest() throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "noSignatureField.pdf";
        String fileName = "fieldLayersTest.pdf";
        testLayers(src, fileName);
    }

    @Test
    public void signatureFieldAppearanceTest() throws IOException, GeneralSecurityException, InterruptedException {
        String fileName = "signatureFieldAppearanceTest.pdf";
        String src = SOURCE_FOLDER + "noSignatureField.pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + fileName;
        String dest = DESTINATION_FOLDER + fileName;
        String fieldName = "Signature1";

        PdfSigner signer = new PdfSigner(new PdfReader(src), FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties().setFieldName(fieldName);
        signer.setSignerProperties(signerProperties);
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent("Signature field")
                .setBackgroundColor(ColorConstants.GREEN)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 3))
                .setFontColor(ColorConstants.DARK_GRAY)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER);
        signerProperties
                .setPageRect(new Rectangle(250, 500, 100, 100))
                .setReason("Test 1")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(dest, cmp));
        Assertions.assertNull(new CompareTool().compareVisually(dest, cmp, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void emptySignatureAppearanceTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "simpleDocument.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptySignatureAppearance.pdf";
        String outPdf = DESTINATION_FOLDER + "emptySignatureAppearance.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        String fieldName = "Signature1";
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);

        PdfSigner signer = new PdfSigner(new PdfReader(srcFile), FileUtil.getFileOutputStream(outPdf), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties()
                .setCertificationLevel(AccessPermissions.UNSPECIFIED)
                .setFieldName(fieldName)
                .setReason("test reason")
                .setLocation("test location")
                .setSignatureAppearance(appearance)
                .setPageRect(rect);
        signer.setSignerProperties(signerProperties);

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_"));
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
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
                Assertions.assertNull(new CompareTool().compareDictionariesStructure(outN, cmpN));
            }
        }
    }

    private void testReuseAppearance(String src, String fileName)
            throws IOException, GeneralSecurityException, InterruptedException {
        String cmp = SOURCE_FOLDER + "cmp_" + fileName;
        String dest = DESTINATION_FOLDER + fileName;
        String fieldName = "Signature1";

        PdfSigner signer = new PdfSigner(new PdfReader(src), FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties().setFieldName(fieldName);
        signer.setSignerProperties(signerProperties);
        signer.getSignatureField().setReuseAppearance(true);

        signerProperties
                .setReason("Test 1")
                .setLocation("TestCity")
                .setSignatureAppearance(new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                        .setContent("New appearance").setFontColor(ColorConstants.GREEN));

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(new CompareTool().compareVisually(dest, cmp, DESTINATION_FOLDER, "diff_"));
    }

    private void testLayers(String src, String fileName)
            throws IOException, GeneralSecurityException {
        String dest = DESTINATION_FOLDER + fileName;
        String fieldName = "Signature1";

        PdfSigner signer = new PdfSigner(new PdfReader(src), FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties()
                .setFieldName(fieldName)
                .setPageRect(new Rectangle(250, 500, 100, 100))
                .setReason("Test 1")
                .setLocation("TestCity")
                .setSignatureAppearance(new SignatureFieldAppearance(SignerProperties.IGNORED_ID));
        signer.setSignerProperties(signerProperties);

        PdfFormXObject layer0 = new PdfFormXObject(new Rectangle(0, 0, 100, 100));
        // Draw pink rectangle with blue border
        new PdfCanvas(layer0, signer.getDocument())
                .saveState()
                .setFillColor(ColorConstants.PINK)
                .setStrokeColor(ColorConstants.BLUE)
                .rectangle(0, 0, 100, 100)
                .fillStroke()
                .restoreState();

        PdfFormXObject layer2 = new PdfFormXObject(new Rectangle(0, 0, 100, 100));
        // Draw yellow circle with gray border
        new PdfCanvas(layer2, signer.getDocument())
                .saveState()
                .setFillColor(ColorConstants.YELLOW)
                .setStrokeColor(ColorConstants.DARK_GRAY)
                .circle(50, 50, 50)
                .fillStroke()
                .restoreState();

        signer.getSignatureField().setBackgroundLayer(layer0).setSignatureAppearanceLayer(layer2);

        // Signing
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        compareSignatureAppearances(dest, SOURCE_FOLDER + "cmp_" + fileName);
    }

    private void testSignatureOnRotatedPage(int pageNum, boolean useDescription, boolean useSignerName,
                                            boolean useImage, StringBuilder assertionResults)
            throws IOException, GeneralSecurityException, InterruptedException {

        String fileName = "signaturesOnRotatedPages" + pageNum + "_mode_";
        String src = SOURCE_FOLDER + "documentWithRotatedPages.pdf";

        String signatureName = "Signature1";
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
        String description = "Digitally signed by Test User. All rights reserved. Take care!";
        if (useImage) {
            if (useDescription) {
                appearance.setContent(description, ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));
                fileName += "GRAPHIC_AND_DESCRIPTION.pdf";
            } else {
                appearance.setContent(ImageDataFactory.create(SOURCE_FOLDER + "itext.png"));
                fileName += "GRAPHIC.pdf";
            }
        } else if (useSignerName) {
            appearance.setContent("signerName", description);
            fileName += "NAME_AND_DESCRIPTION.pdf";
        } else {
            appearance.setContent(description);
            fileName += "DESCRIPTION.pdf";
        }

        String dest = DESTINATION_FOLDER + fileName;
        PdfSigner signer = new PdfSigner(new PdfReader(src), FileUtil.getFileOutputStream(dest),
                new StampingProperties().useAppendMode());
        SignerProperties signerProperties = new SignerProperties()
                .setFieldName(signatureName)
                .setPageRect(new Rectangle(100, 100, 100, 50))
                .setPageNumber(pageNum)
                .setSignatureAppearance(appearance)
                .setCertificationLevel(AccessPermissions.UNSPECIFIED);
        signer.setSignerProperties(signerProperties);

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

    private void testSignatureAppearanceAutoscale(String dest, Rectangle rect, String signerName, ImageData image)
            throws IOException, GeneralSecurityException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), FileUtil.getFileOutputStream(dest), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties().setFieldName("Signature1");
        signer.setSignerProperties(signerProperties);
        // Creating the appearance
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
        if (image != null) {
            appearance.setContent(new SignedAppearanceText(), image);
        } else if (signerName != null) {
            appearance.setContent(signerName, new SignedAppearanceText());
        } else {
            appearance.setContent(new SignedAppearanceText());
        }
        appearance.setFontSize(0);
        signerProperties
                .setReason("Test 1")
                .setLocation("TestCity")
                .setPageRect(rect)
                .setSignatureAppearance(appearance);

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
    }

    void applyBackgroundImage(SignatureFieldAppearance appearance, ImageData image, float imageScale) {
        if (image != null) {
            BackgroundRepeat repeat = new BackgroundRepeat(BackgroundRepeat.BackgroundRepeatValue.NO_REPEAT);
            BackgroundPosition position = new BackgroundPosition()
                    .setPositionX(BackgroundPosition.PositionX.CENTER)
                    .setPositionY(BackgroundPosition.PositionY.CENTER);
            BackgroundSize size = new BackgroundSize();
            final float EPS = 1e-5f;
            if (Math.abs(imageScale) < EPS) {
                size.setBackgroundSizeToValues(UnitValue.createPercentValue(100),
                        UnitValue.createPercentValue(100));
            } else {
                if (imageScale < 0) {
                    size.setBackgroundSizeToContain();
                } else {
                    size.setBackgroundSizeToValues(
                            UnitValue.createPointValue(imageScale * image.getWidth()),
                            UnitValue.createPointValue(imageScale * image.getHeight()));
                }
            }
            appearance.setBackgroundImage(new BackgroundImage.Builder()
                    .setImage(new PdfImageXObject(image))
                    .setBackgroundSize(size)
                    .setBackgroundRepeat(repeat)
                    .setBackgroundPosition(position)
                    .build());
        }
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
        Assertions.assertTrue(Math.abs(foundFontSize - expectedFontSize) < 0.1 * expectedFontSize,
                MessageFormatUtil.format("Font size: expected {0}, found {1}", expectedFontSize, fontSize));
    }

    private static Map<Integer, List<Rectangle>> getIgnoredAreaTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }

}
