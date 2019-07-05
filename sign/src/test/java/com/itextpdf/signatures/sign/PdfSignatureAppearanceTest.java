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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
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
import org.junit.Test;
import org.junit.experimental.categories.Category;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Category(IntegrationTest.class)
public class PdfSignatureAppearanceTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfSignatureAppearanceTest/";
    public static final String keystorePath = "./src/test/resources/com/itextpdf/signatures/sign/SigningTest/test.p12";
    public static final char[] password = "kspass".toCharArray();

    private Certificate[] chain;
    private PrivateKey pk;

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
    public void textAutoscaleTest01() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "textAutoscaleTest01.pdf";
        String dest = destinationFolder + fileName;

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.DESCRIPTION);

        assertAppearanceFontSize(dest, 13.94f);
    }

    @Test
    public void textAutoscaleTest02() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "textAutoscaleTest02.pdf";
        String dest = destinationFolder + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.DESCRIPTION);

        assertAppearanceFontSize(dest, 6.83f);
    }

    @Test
    public void textAutoscaleTest03() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "textAutoscaleTest03.pdf";
        String dest = destinationFolder + fileName;

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION);

        assertAppearanceFontSize(dest, 44.35f);
    }

    @Test
    public void textAutoscaleTest04() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "textAutoscaleTest04.pdf";
        String dest = destinationFolder + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION);

        assertAppearanceFontSize(dest, 21.25f);
    }

    @Test
    public void textAutoscaleTest05() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "textAutoscaleTest05.pdf";
        String dest = destinationFolder + fileName;

        Rectangle rect = new Rectangle(36, 648, 200, 100);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);

        assertAppearanceFontSize(dest, 12.77f);
    }

    @Test
    public void textAutoscaleTest06() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "textAutoscaleTest06.pdf";
        String dest = destinationFolder + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        testSignatureAppearanceAutoscale(dest, rect, PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);

        assertAppearanceFontSize(dest, 6.26f);
    }

    @Test
    public void testSigningInAppendModeWithHybridDocument() throws IOException, GeneralSecurityException, InterruptedException {
        String src = sourceFolder + "hybrid.pdf";
        String dest = destinationFolder + "signed_hybrid.pdf";
        String cmp = sourceFolder + "cmp_signed_hybrid.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties().useAppendMode());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

        appearance.setLayer2FontSize(13.8f)
                .setPageRect(new Rectangle(36, 748, 200, 100))
                .setPageNumber(1)
                .setReason("Test")
                .setLocation("Nagpur");

        signer.setFieldName("Sign1");

        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        // Make sure iText can open the document
        new PdfDocument(new PdfReader(dest)).close();

        // Assert that the document can be rendered correctly
        Assert.assertNull(new CompareTool().compareVisually(dest, cmp, destinationFolder, "diff_",
                getIgnoredAreaTestMap(new Rectangle(36, 748, 200, 100))));
    }

    @Test
    public void fontColorTest01() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "fontColorTest01.pdf";
        String dest = destinationFolder + fileName;

        Rectangle rect = new Rectangle(36, 648, 100, 50);
        String src = sourceFolder + "simpleDocument.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());
        // Creating the appearance
        signer.getSignatureAppearance()
                .setLayer2FontColor(ColorConstants.RED)
                .setLayer2Text("Verified and signed by me.")
                .setPageRect(rect);

        signer.setFieldName("Signature1");
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_"));
    }

    @Test
    public void signaturesOnRotatedPages() throws IOException, GeneralSecurityException, InterruptedException {
        StringBuilder assertionResults = new StringBuilder();

        for (int i = 1; i <= 4; i++) {
            testSignatureOnRotatedPage(i, PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION, assertionResults);
            testSignatureOnRotatedPage(i, PdfSignatureAppearance.RenderingMode.GRAPHIC, assertionResults);
            testSignatureOnRotatedPage(i, PdfSignatureAppearance.RenderingMode.NAME_AND_DESCRIPTION, assertionResults);
            testSignatureOnRotatedPage(i, PdfSignatureAppearance.RenderingMode.DESCRIPTION, assertionResults);
        }

        Assert.assertEquals("", assertionResults.toString());
    }

    private void testSignatureOnRotatedPage(int pageNum, PdfSignatureAppearance.RenderingMode renderingMode, StringBuilder assertionResults) throws IOException, GeneralSecurityException, InterruptedException {
        String fileName = "signaturesOnRotatedPages" + pageNum + "_mode_" + renderingMode.name() + ".pdf";
        String src = sourceFolder + "documentWithRotatedPages.pdf";
        String dest = destinationFolder + fileName;

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties().useAppendMode());

        PdfSignatureAppearance appearance = signer.getSignatureAppearance();

        appearance
                .setLayer2Text("Digitally signed by Test User. All rights reserved. Take care!")
                .setPageRect(new Rectangle(100, 100, 100, 50))
                .setRenderingMode(renderingMode)
                .setSignatureGraphic(ImageDataFactory.create(sourceFolder + "itext.png"))
                .setPageNumber(pageNum);

        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        // Make sure iText can open the document
        new PdfDocument(new PdfReader(dest)).close();

        try {
            String testResult = new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder, "diff_");
            if (null != testResult) {
                assertionResults.append(testResult);
            }
        } catch (CompareTool.CompareToolExecutionException e) {
            assertionResults.append(e.getMessage());
        }
    }

    private void testSignatureAppearanceAutoscale(String dest, Rectangle rect, PdfSignatureAppearance.RenderingMode renderingMode) throws IOException, GeneralSecurityException {
        String src = sourceFolder + "simpleDocument.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), new StampingProperties());
        // Creating the appearance
        signer.getSignatureAppearance()
                .setLayer2FontSize(0)
                .setReason("Test 1")
                .setLocation("TestCity")
                .setPageRect(rect)
                .setRenderingMode(renderingMode)
                .setSignatureGraphic(ImageDataFactory.create(sourceFolder + "itext.png"));

        signer.setFieldName("Signature1");
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
    }

    private static void assertAppearanceFontSize(String filename, float expectedFontSize) throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(filename));
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);
        PdfStream stream = acroForm.getField("Signature1").getWidgets().get(0).getNormalAppearanceObject().
                getAsDictionary(PdfName.Resources).getAsDictionary(PdfName.XObject).getAsStream(new PdfName("FRM")).getAsDictionary(PdfName.Resources).
                getAsDictionary(PdfName.XObject).getAsStream(new PdfName("n2"));
        String[] streamContents = new String(stream.getBytes()).split("\\s");
        String fontSize = null;
        for (int i = 1; i < streamContents.length; i++) {
            if ("Tf".equals(streamContents[i])) {
                fontSize = streamContents[i - 1];
                break;
            }
        }
        float foundFontSize = Float.parseFloat(fontSize);
        Assert.assertTrue(MessageFormatUtil.format("Font size: exptected {0}, found {1}", expectedFontSize, fontSize), Math.abs(foundFontSize - expectedFontSize) < 0.1 * expectedFontSize);
    }

    private static Map<Integer, List<Rectangle>> getIgnoredAreaTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }

}
