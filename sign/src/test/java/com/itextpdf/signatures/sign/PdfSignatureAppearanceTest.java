/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.Pkcs12FileHelper;
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
import com.itextpdf.io.util.MessageFormatUtil;

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

    private void testSignatureAppearanceAutoscale(String dest, Rectangle rect, PdfSignatureAppearance.RenderingMode renderingMode) throws IOException, GeneralSecurityException {
        String src = sourceFolder + "simpleDocument.pdf";

        PdfSigner signer = new PdfSigner(new PdfReader(src), new FileOutputStream(dest), false);
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


}
