/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.signatures.PdfSignatureAppearance.RenderingMode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * The idea of this test is to check the {@link PdfSignatureAppearance}'s getters.
 * For actual result of setters invocations one should check the integration test for this class.
 */
@Category(BouncyCastleUnitTest.class)
public class PdfSignatureAppearanceUnitTest extends ExtendedITextTest {
    // The source folder points to the integration test, so that the resources are nor duplicated
    public static final String SOURCE_FOLDER
            = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/";
    public static final String DESTINATION_FOLDER
            = "./target/test/com/itextpdf/signatures/sign/PdfSignatureAppearanceUnitTest/";
    public static final String KEYSTORE_PATH
            = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/test.p12";
    public static final char[] PASSWORD = "kspass".toCharArray();

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static Certificate[] chain;

    @BeforeClass
    public static void before() throws KeyStoreException, IOException, CertificateException,
            NoSuchAlgorithmException {
        Security.addProvider(BOUNCY_CASTLE_FACTORY.createProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        chain = Pkcs12FileHelper.readFirstChain(KEYSTORE_PATH, PASSWORD);
    }

    @Test
    public void reasonCaptionTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        String newReasonCaption = "Hello World";

        Assert.assertNull(signatureAppearance.getLayer2Text());

        String layer2Text = signatureAppearance.generateLayer2Text();
        // There is no text from new reason caption in the default layer 2 text
        Assert.assertFalse(layer2Text.contains(newReasonCaption));

        signatureAppearance.setReasonCaption(newReasonCaption);
        layer2Text = signatureAppearance.generateLayer2Text();
        // Now layer 2 text contains text from new reason caption
        Assert.assertTrue(layer2Text.contains(newReasonCaption));
    }

    @Test
    public void locationCaptionTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        String newLocationCaption = "Hello World";

        Assert.assertNull(signatureAppearance.getLayer2Text());

        String layer2Text = signatureAppearance.generateLayer2Text();
        // There is no text from new location caption in the default layer 2 text
        Assert.assertFalse(layer2Text.contains(newLocationCaption));

        signatureAppearance.setLocationCaption(newLocationCaption);
        layer2Text = signatureAppearance.generateLayer2Text();
        // Now layer 2 text contains text from new location caption
        Assert.assertTrue(layer2Text.contains(newLocationCaption));
    }

    @Test
    public void renderingModeSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        RenderingMode defaultMode = signatureAppearance.getRenderingMode();
        Assert.assertEquals(RenderingMode.DESCRIPTION, defaultMode);

        RenderingMode testRenderingMode = RenderingMode.GRAPHIC_AND_DESCRIPTION;
        signatureAppearance.setRenderingMode(testRenderingMode);
        Assert.assertEquals(testRenderingMode, signatureAppearance.getRenderingMode());
    }

    @Test
    public void signatureCreatorSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertEquals("", signatureAppearance.getSignatureCreator());

        String signatureCreator = "Hello World";
        signatureAppearance.setSignatureCreator(signatureCreator);
        Assert.assertEquals(signatureCreator, signatureAppearance.getSignatureCreator());
    }

    @Test
    public void contactSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertEquals("", signatureAppearance.getContact());

        String contact = "Hello World";
        signatureAppearance.setContact(contact);
        Assert.assertEquals(contact, signatureAppearance.getContact());
    }

    @Test
    public void certificateSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertNull(signatureAppearance.getCertificate());

        Certificate testCertificate = chain[0];
        signatureAppearance.setCertificate(testCertificate);
        Assert.assertEquals(testCertificate, signatureAppearance.getCertificate());
    }

    @Test
    public void signatureGraphicSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertNull(signatureAppearance.getSignatureGraphic());

        ImageData testImageData = ImageDataFactory.create(SOURCE_FOLDER + "itext.png");
        signatureAppearance.setSignatureGraphic(testImageData);
        Assert.assertEquals(testImageData, signatureAppearance.getSignatureGraphic());
    }

    @Test
    public void imageSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertNull(signatureAppearance.getImage());

        ImageData testImageData = ImageDataFactory.create(SOURCE_FOLDER + "itext.png");
        signatureAppearance.setImage(testImageData);
        Assert.assertEquals(testImageData, signatureAppearance.getImage());
    }

    @Test
    public void imageScalingSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertEquals(0, signatureAppearance.getImageScale(), 0.0001);

        float newScale = 1F;
        signatureAppearance.setImageScale(newScale);
        Assert.assertEquals(newScale, signatureAppearance.getImageScale(), 0.0001);
    }

    @Test
    public void layer2FontSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertNull(signatureAppearance.getLayer2Font());

        PdfFont newFont = PdfFontFactory.createFont();
        signatureAppearance.setLayer2Font(newFont);
        Assert.assertEquals(newFont, signatureAppearance.getLayer2Font());
    }

    @Test
    public void layer2FontSizeSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertEquals(0, signatureAppearance.getLayer2FontSize(), 0.0001);

        float newSize = 12F;
        signatureAppearance.setLayer2FontSize(newSize);
        Assert.assertEquals(newSize, signatureAppearance.getLayer2FontSize(), 0.0001);
    }


    @Test
    public void layer2FontColorSetGetTest() throws IOException {
        PdfSignatureAppearance signatureAppearance = getTestSignatureAppearance();

        Assert.assertNull(signatureAppearance.getLayer2FontColor());

        Color newColor = ColorConstants.RED;
        signatureAppearance.setLayer2FontColor(newColor);
        Assert.assertEquals(newColor, signatureAppearance.getLayer2FontColor());
    }

    @Test
    public void getAppearanceInvisibleTest() throws IOException {
        PdfSignatureAppearance appearance = new PdfSignatureAppearance(null, new Rectangle(0, 100), 1);
        PdfFormXObject xObject = appearance.getAppearance();

        Assert.assertTrue(new Rectangle(0, 0).equalsWithEpsilon(xObject.getBBox().toRectangle()));
    }

    @Test
    public void getSignDateTest() {
        PdfSignatureAppearance appearance = new PdfSignatureAppearance(null, new Rectangle(100, 100), 1);

        Calendar current = DateTimeUtil.getCurrentTimeCalendar();
        appearance.setSignDate(current);
        Assert.assertEquals(current, appearance.getSignDate());
    }

    private static PdfSignatureAppearance getTestSignatureAppearance() throws IOException {
        String src = SOURCE_FOLDER + "simpleDocument.pdf";
        PdfSigner signer = new PdfSigner(new PdfReader(src), new ByteArrayOutputStream(), new StampingProperties());
        return signer.getSignatureAppearance();
    }
}
