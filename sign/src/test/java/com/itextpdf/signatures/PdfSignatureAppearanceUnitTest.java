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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * The idea of this test is to check the {@link PdfSignatureAppearance}'s getters.
 * For actual result of setters invocations one should check the integration test for this class.
 */
@Tag("BouncyCastleUnitTest")
public class PdfSignatureAppearanceUnitTest extends ExtendedITextTest {
    // The source folder points to the integration test, so that the resources are nor duplicated
    public static final String SOURCE_FOLDER
            = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/";
    public static final String DESTINATION_FOLDER
            = "./target/test/com/itextpdf/signatures/sign/PdfSignatureAppearanceUnitTest/";
    public static final String KEYSTORE_PATH
            = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/test.pem";
    public static final char[] PASSWORD = "kspass".toCharArray();

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static Certificate[] chain;

    @BeforeAll
    public static void before() throws IOException, CertificateException {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        chain = PemFileHelper.readFirstChain(KEYSTORE_PATH);
    }

    @Test
    public void reasonCaptionTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        String newReasonCaption = "Hello World";

        Assertions.assertNull(signatureAppearance.getLayer2Text());

        String layer2Text = signatureAppearance.generateSignatureText().generateDescriptionText();
        // There is no text from new reason caption in the default layer 2 text
        Assertions.assertFalse(layer2Text.contains(newReasonCaption));

        signatureAppearance.setReasonCaption(newReasonCaption);
        layer2Text = signatureAppearance.generateSignatureText().generateDescriptionText();
        // Now layer 2 text contains text from new reason caption
        Assertions.assertTrue(layer2Text.contains(newReasonCaption));
    }

    @Test
    public void locationCaptionTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        String newLocationCaption = "Hello World";

        Assertions.assertNull(signatureAppearance.getLayer2Text());

        String layer2Text = signatureAppearance.generateSignatureText().generateDescriptionText();
        // There is no text from new location caption in the default layer 2 text
        Assertions.assertFalse(layer2Text.contains(newLocationCaption));

        signatureAppearance.setLocationCaption(newLocationCaption);
        layer2Text = signatureAppearance.generateSignatureText().generateDescriptionText();
        // Now layer 2 text contains text from new location caption
        Assertions.assertTrue(layer2Text.contains(newLocationCaption));
    }

    @Test
    public void renderingModeSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        RenderingMode defaultMode = signatureAppearance.getRenderingMode();
        Assertions.assertEquals(RenderingMode.DESCRIPTION, defaultMode);

        RenderingMode testRenderingMode = RenderingMode.GRAPHIC_AND_DESCRIPTION;
        signatureAppearance.setRenderingMode(testRenderingMode);
        Assertions.assertEquals(testRenderingMode, signatureAppearance.getRenderingMode());
    }

    @Test
    public void signatureCreatorSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertEquals("", signatureAppearance.getSignatureCreator());

        String signatureCreator = "Hello World";
        signatureAppearance.setSignatureCreator(signatureCreator);
        Assertions.assertEquals(signatureCreator, signatureAppearance.getSignatureCreator());
    }

    @Test
    public void contactSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertEquals("", signatureAppearance.getContact());

        String contact = "Hello World";
        signatureAppearance.setContact(contact);
        Assertions.assertEquals(contact, signatureAppearance.getContact());
    }

    @Test
    public void certificateSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertNull(signatureAppearance.getCertificate());

        Certificate testCertificate = chain[0];
        signatureAppearance.setCertificate(testCertificate);
        Assertions.assertEquals(testCertificate, signatureAppearance.getCertificate());
    }

    @Test
    public void signatureGraphicSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertNull(signatureAppearance.getSignatureGraphic());

        ImageData testImageData = ImageDataFactory.create(SOURCE_FOLDER + "itext.png");
        signatureAppearance.setSignatureGraphic(testImageData);
        Assertions.assertEquals(testImageData, signatureAppearance.getSignatureGraphic());
    }

    @Test
    public void imageSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertNull(signatureAppearance.getImage());

        ImageData testImageData = ImageDataFactory.create(SOURCE_FOLDER + "itext.png");
        signatureAppearance.setImage(testImageData);
        Assertions.assertEquals(testImageData, signatureAppearance.getImage());
    }

    @Test
    public void imageScalingSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertEquals(0, signatureAppearance.getImageScale(), 0.0001);

        float newScale = 1F;
        signatureAppearance.setImageScale(newScale);
        Assertions.assertEquals(newScale, signatureAppearance.getImageScale(), 0.0001);
    }

    @Test
    public void layer2FontSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertNull(signatureAppearance.getLayer2Font());

        PdfFont newFont = PdfFontFactory.createFont();
        signatureAppearance.setLayer2Font(newFont);
        Assertions.assertEquals(newFont, signatureAppearance.getLayer2Font());
    }

    @Test
    public void setFontProviderAndFamilyTest() throws IOException {
        PdfSignatureAppearance appearance = getTestSignatureAppearance();

        FontProvider fontProvider = new FontProvider();
        fontProvider.getFontSet().addFont(StandardFonts.HELVETICA, "");
        String fontFamilyName = "fontFamily";
        appearance.setFontProvider(fontProvider).setFontFamily(fontFamilyName);
        Assertions.assertEquals(fontProvider,
                appearance.getSignatureAppearance().<FontProvider>getProperty(Property.FONT_PROVIDER));
        Assertions.assertEquals(fontFamilyName,
                ((String[]) appearance.getSignatureAppearance().<String[]>getProperty(Property.FONT))[0]);
    }

    @Test
    public void layer2FontSizeSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertEquals(0, signatureAppearance.getLayer2FontSize(), 0.0001);

        float newSize = 12F;
        signatureAppearance.setLayer2FontSize(newSize);
        Assertions.assertEquals(newSize, signatureAppearance.getLayer2FontSize(), 0.0001);
    }


    @Test
    public void layer2FontColorSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assertions.assertNull(signatureAppearance.getLayer2FontColor());

        Color newColor = ColorConstants.RED;
        signatureAppearance.setLayer2FontColor(newColor);
        Assertions.assertEquals(newColor, signatureAppearance.getLayer2FontColor());
    }

    @Test
    public void getAppearanceInvisibleTest() {
        PdfSignatureAppearance appearance = new PdfSignatureAppearance(
                new PdfDocument(new PdfWriter(new ByteArrayOutputStream())),
                new Rectangle(0, 100), 1);
        PdfFormXObject xObject = appearance.getAppearance();

        Assertions.assertTrue(new Rectangle(0, 0).equalsWithEpsilon(xObject.getBBox().toRectangle()));
    }

    @Test
    public void getSignDateTest() {
        PdfSignatureAppearance appearance = new PdfSignatureAppearance(null, new Rectangle(100, 100), 1);

        Calendar current = DateTimeUtil.getCurrentTimeCalendar();
        appearance.setSignDate(current);
        Assertions.assertEquals(current, appearance.getSignDate());
    }

    @Test
    public void wrongRenderingModeTest() {
        try (Document ignored = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            PdfSignatureAppearance appearance = new PdfSignatureAppearance(null, new Rectangle(100, 100), 1);
            appearance.setRenderingMode(RenderingMode.GRAPHIC_AND_DESCRIPTION);
            Assertions.assertThrows(IllegalStateException.class, () -> appearance.getSignatureAppearance());

            PdfSignatureAppearance appearance2 = new PdfSignatureAppearance(null, new Rectangle(100, 100), 1);
            appearance2.setRenderingMode(RenderingMode.GRAPHIC);
            Assertions.assertThrows(IllegalStateException.class, () -> appearance2.getSignatureAppearance());
        }
    }

    @Test
    public void backgroundImageTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "signatureFieldBackground.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signatureFieldBackground.pdf";
        PdfSignatureAppearance appearance = new PdfSignatureAppearance(null, new Rectangle(100, 100), 1);

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            SignatureFieldAppearance field1 = new SignatureFieldAppearance("field1");
            field1.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field1.setContent("scale -1").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setBorder(new SolidBorder(ColorConstants.RED, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            appearance.setSignatureAppearance(field1)
                    .setImage(ImageDataFactory.create(SOURCE_FOLDER + "1.png"))
                    .setImageScale(-1)
                    .applyBackgroundImage();
            document.add(field1);

            SignatureFieldAppearance field2 = new SignatureFieldAppearance("field2");
            field2.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field2.setContent("scale 0").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setBorder(new SolidBorder(ColorConstants.YELLOW, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            appearance.setSignatureAppearance(field2)
                    .setImage(ImageDataFactory.create(SOURCE_FOLDER + "1.png"))
                    .setImageScale(0)
                    .applyBackgroundImage();
            document.add(field2);

            SignatureFieldAppearance field3 = new SignatureFieldAppearance("field3");
            field3.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            field3.setContent("scale 0.5").setFontColor(ColorConstants.GREEN).setFontSize(50)
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 10)).setHeight(200).setWidth(300)
                    .setProperty(Property.TEXT_ALIGNMENT, TextAlignment.CENTER);
            appearance.setSignatureAppearance(field3)
                    .setImage(ImageDataFactory.create(SOURCE_FOLDER + "1.png"))
                    .setImageScale(0.5f)
                    .applyBackgroundImage();
            document.add(field3);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }

    private static PdfSignatureAppearance getTestSignatureAppearance() throws IOException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";
        PdfSigner signer = new PdfSigner(new PdfReader(src), new ByteArrayOutputStream(), new StampingProperties());
        return signer.getSignatureAppearance();
    }
}
