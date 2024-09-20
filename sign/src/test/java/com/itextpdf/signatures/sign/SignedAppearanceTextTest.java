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
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.signatures.AccessPermissions;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

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
public class SignedAppearanceTextTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/SignedAppearanceTextTest/";
    private static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/signatures/font/";
    private static final String PDFA_FOLDER = "./src/test/resources/com/itextpdf/signatures/pdfa/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/SignedAppearanceTextTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

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
        pk = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);
        chain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem");
    }

    @Test
    public void defaultSignedAppearanceTextTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "simpleDocument.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_defaultSignedAppearanceTextTest.pdf";
        String outPdf = DESTINATION_FOLDER + "defaultSignedAppearanceTextTest.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent(new SignedAppearanceText());
        sign(srcFile, fieldName, outPdf, "Test 1", "TestCity 1", rect, appearance);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(36, 676, 200, 15))));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void signPDFADocumentWithoutSettingFont() throws IOException {
        String srcFile = DESTINATION_FOLDER + "simplePDFA.pdf";
        createSimplePDFADocument(srcFile).close();

        Rectangle rect = new Rectangle(50, 70, 400, 200);
        String fieldName = "Signature1";

        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent(new SignedAppearanceText()
                        .setSignedBy("Test")
                        .setSignDate(DateTimeUtil.getCurrentTimeCalendar())
                        .setLocationLine("Test City"));

        String outPdf = DESTINATION_FOLDER + "signPDFADocumentWithoutSettingFont.pdf";
        Exception e = Assertions.assertThrows(Exception.class, () -> {
            sign(srcFile, fieldName, outPdf, "Test 1", "TestCity 1", rect, appearance);
        });
        Assertions.assertEquals(LayoutExceptionMessageConstant.INVALID_FONT_PROPERTY_VALUE, e.getMessage());
    }

    @Test
    public void signPDFADocumentSettingBadFont() throws IOException {
        String srcFile = DESTINATION_FOLDER + "simplePDFA1.pdf";
        createSimplePDFADocument(srcFile).close();

        Rectangle rect = new Rectangle(50, 70, 400, 200);
        String fieldName = "Signature1";

        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setFont(PdfFontFactory.createFont(StandardFonts.COURIER))
                .setContent(new SignedAppearanceText()
                        .setSignedBy("Test")
                        .setSignDate(DateTimeUtil.getCurrentTimeCalendar())
                        .setLocationLine("Test City"));

        String outPdf = DESTINATION_FOLDER + "signPDFADocumentBadFont.pdf";
        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () -> {
            sign(srcFile, fieldName, outPdf, "Test 1", "TestCity 1", rect, appearance);
        });
        Assertions.assertEquals(e.getMessage(),
                MessageFormatUtil.format(PdfaExceptionMessageConstant.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0,
                        "Courier"));
    }

    @Test
    public void defaultPdfATextTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = DESTINATION_FOLDER + "simplePDFADocument.pdf";
        createSimplePDFADocument(srcFile).close();

        Assertions.assertNull(new VeraPdfValidator().validate(srcFile));  // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String cmpPdf = SOURCE_FOLDER + "cmp_defaultSignedPDFAAppearanceTextTest.pdf";
        String outPdf = DESTINATION_FOLDER + "defaultSignedPDFAAppearanceTextTest.pdf";

        Rectangle rect = new Rectangle(50, 200, 400, 100);

        PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf", EmbeddingStrategy.FORCE_EMBEDDED);
        String fieldName = "Signature1";
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setFont(font)
                .setContent(new SignedAppearanceText()
                        .setSignedBy("Test")
                        .setSignDate(DateTimeUtil.getCurrentTimeCalendar())
                        .setLocationLine("Test City"));

        sign(srcFile, fieldName, outPdf, "Test 1", "TestCity 1", rect, appearance);

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf));  // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(rect)));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }


    @Test
    public void signPdfAWithFormfieldAlreadyExistingTest()
            throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = DESTINATION_FOLDER + "simplePDFADocumentWithSignature.pdf";
        PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf", EmbeddingStrategy.FORCE_EMBEDDED);
        Document doc = createSimplePDFADocument(srcFile);

        String fieldName = "Signature1";
        SignatureFieldAppearance appearanceOg = (SignatureFieldAppearance) new SignatureFieldAppearance(fieldName)
                .setFont(font)
                .setFontColor(ColorConstants.MAGENTA)
                .setContent(new SignedAppearanceText()
                        .setSignedBy("Test")
                        .setReasonLine("Making pdfs safe")
                        .setSignDate(DateTimeUtil.getCurrentTimeCalendar())
                        .setLocationLine("Test City"))
                .setInteractive(true);
        doc.add(appearanceOg);

        doc.close();

        String cmpPdf = SOURCE_FOLDER + "cmp_signPdfAWithFormfieldAlreadyExisting.pdf";
        String outPdf = DESTINATION_FOLDER + "signPdfAWithFormfieldAlreadyExisting.pdf";

        Rectangle rect = new Rectangle(50, 200, 400, 100);

        PdfFont font1 = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf", EmbeddingStrategy.FORCE_EMBEDDED);
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setFont(font1)
                .setContent(new SignedAppearanceText()
                        .setSignedBy("Test")
                        .setSignDate(DateTimeUtil.getCurrentTimeCalendar())
                        .setLocationLine("Test City"));

        sign(srcFile, fieldName, outPdf, "Test 1", "TestCity 1", rect, appearance);

        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    private static Document createSimplePDFADocument(String filename) throws IOException {
        WriterProperties writerProperties = new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0);
        String icmProfile = PDFA_FOLDER + "sRGB Color Space Profile.icm";
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "",
                "http://www.color.org", "sRGB IEC61966-2.1", FileUtil.getInputStreamForFile(icmProfile));
        PdfDocument document = new PdfADocument(new PdfWriter(filename, writerProperties),
                PdfAConformance.PDF_A_4,
                outputIntent);
        Document doc = new Document(document);
        PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf", EmbeddingStrategy.FORCE_EMBEDDED);
        doc.add(new Paragraph("Hello World!").setFont(font));
        document.addNewPage();
        return doc;

    }

    @Test
    public void defaultSignedAppearanceTextAndSignerTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "simpleDocument.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_defaultSignedAppearanceTextAndSignerTest.pdf";
        String outPdf = DESTINATION_FOLDER + "defaultSignedAppearanceTextAndSignerTest.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature2";
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent("", new SignedAppearanceText());
        sign(srcFile, fieldName, outPdf, "Test 2", "TestCity 2", rect, appearance);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(136, 686, 100, 25))));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT), ignore = true)
    public void defaultSignedAppearanceTextWithImageTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "simpleDocument.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_defaultSignedAppearanceTextWithImageTest.pdf";
        String imagePath = SOURCE_FOLDER + "sign.jpg";
        String outPdf = DESTINATION_FOLDER + "defaultSignedAppearanceTextWithImageTest.pdf";

        Rectangle rect = new Rectangle(36, 648, 300, 100);

        String fieldName = "Signature3";
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent(new SignedAppearanceText(), ImageDataFactory.create(imagePath));
        sign(srcFile, fieldName, outPdf, "Test 3", "TestCity 3", rect, appearance);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(186, 681, 150, 36))));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void modifiedSignedAppearanceTextTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "simpleDocument.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_modifiedSignedAppearanceTextTest.pdf";
        String outPdf = DESTINATION_FOLDER + "modifiedSignedAppearanceTextTest.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature4";
        String reason = "Test 4";
        String location = "TestCity 4";
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent(new SignedAppearanceText()
                        .setSignedBy("   wrong signer   ")
                        .setReasonLine("   Signing reason: " + reason)
                        .setLocationLine("   Signing location: " + location)
                        .setSignDate(DateTimeUtil.getCurrentTimeCalendar()));
        sign(srcFile, fieldName, outPdf, reason, location, rect, appearance);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(36, 676, 200, 15))));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    protected void sign(String src, String name, String dest,
                        String reason, String location, Rectangle rectangleForNewField,
                        SignatureFieldAppearance appearance)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);
        StampingProperties properties = new StampingProperties();
        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(dest), properties);

        SignerProperties signerProperties = new SignerProperties()
                .setCertificationLevel(AccessPermissions.UNSPECIFIED)
                .setFieldName(name)
                .setReason(reason)
                .setLocation(location)
                .setSignatureAppearance(appearance);
        if (rectangleForNewField != null) {
            signerProperties.setPageRect(rectangleForNewField);
        }
        signer.setSignerProperties(signerProperties);

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
    }

    private static Map<Integer, List<Rectangle>> getTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }
}
