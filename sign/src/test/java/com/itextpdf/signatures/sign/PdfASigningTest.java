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
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;  // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class PdfASigningTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfASigningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfASigningTest/";
    public static final String keystorePath = "./src/test/resources/com/itextpdf/signatures/certs/signCertRsa01.pem";
    public static final char[] password = "testpassphrase".toCharArray();
    public static final String FONT = "./src/test/resources/com/itextpdf/signatures/font/FreeSans.ttf";

    private Certificate[] chain;
    private PrivateKey pk;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @BeforeEach
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        pk = PemFileHelper.readFirstKey(keystorePath, password);
        chain = PemFileHelper.readFirstChain(keystorePath);
    }

    @Test
    public void simpleSigningTest() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simplePdfADocument.pdf";
        String fileName = "simpleSignature.pdf";
        String dest = destinationFolder + fileName;

        int x = 36;
        int y = 548;
        int w = 200;
        int h = 100;
        Rectangle rect = new Rectangle(x, y, w, h);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, false, PdfSigner.NOT_CERTIFIED, 12f);

        Assertions.assertNull(new VeraPdfValidator().validate(dest)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(dest, sourceFolder + "cmp_" + fileName));
        Assertions.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(27, 550, 195, 40))));
    }

    @Test
    public void signingPdfA2DocumentTest() throws IOException, GeneralSecurityException {
        String src = sourceFolder + "simplePdfA2Document.pdf";
        String out = destinationFolder + "signedPdfA2Document.pdf";

        PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(src));
        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(out), new StampingProperties());
        signer.setFieldLockDict(new PdfSigFieldLock());
        signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);

        IExternalSignature pks =
                new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Assertions.assertNull(new VeraPdfValidator().validate(out)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void signPdf2CmsTest() {
        String srcFile = sourceFolder + "simplePdfA4Document.pdf";
        String outPdf = destinationFolder + "signPdfCms.pdf";

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";


        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () ->
                sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CMS, "Test 1",
                        "TestCity", rect, false, true, PdfSigner.NOT_CERTIFIED, 12f));
        Assertions.assertEquals(PdfaExceptionMessageConstant.SIGNATURE_SHALL_CONFORM_TO_ONE_OF_THE_PADES_PROFILE, e.getMessage());
    }

    @Test
    public void signPdf2CadesTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = sourceFolder + "simplePdfA4Document.pdf";
        String cmpPdf = sourceFolder + "cmp_signPdfCades.pdf";
        String outPdf = destinationFolder + "signPdfCades.pdf";

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true, PdfSigner.NOT_CERTIFIED, 12f);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, destinationFolder, "diff_",
                getTestMap(rect)));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void failedSigningPdfA2DocumentTest() throws IOException {
        String src = sourceFolder + "simplePdfADocument.pdf";
        String out = destinationFolder + "signedPdfADocument2.pdf";

        PdfReader reader = new PdfReader(FileUtil.getInputStreamForFile(src));
        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(out), new StampingProperties());
        signer.setFieldLockDict(new PdfSigFieldLock());
        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);

        int x = 36;
        int y = 548;
        int w = 200;
        int h = 100;
        Rectangle rect = new Rectangle(x, y, w, h);
        PdfFont font = PdfFontFactory.createFont("Helvetica","WinAnsi",
                EmbeddingStrategy.PREFER_EMBEDDED);

        SignatureFieldAppearance appearance = new SignatureFieldAppearance(signer.getFieldName())
                .setContent(new SignedAppearanceText())
                .setFont(font);
        signer.setPageRect(rect)
                .setReason("pdfA test")
                .setLocation("TestCity")
                .setSignatureAppearance(appearance);
        signer
                .getSignatureField().setReuseAppearance(false);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());

        Exception e = Assertions.assertThrows(PdfAConformanceException.class, () ->
                signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null,
                        0, PdfSigner.CryptoStandard.CADES));
        Assertions.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0,
                        "Helvetica"), e.getMessage());
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
        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(dest), properties);

        signer.setCertificationLevel(certificationLevel);

        PdfFont font = PdfFontFactory.createFont(FONT, "WinAnsi", EmbeddingStrategy.PREFER_EMBEDDED);
        signer.setFieldName(name);

        // Creating the appearance
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(name)
                .setContent(new SignedAppearanceText());
        appearance.setFont(font);
        signer
                .setReason(reason)
                .setLocation(location)
                .setSignatureAppearance(appearance);
        if (rectangleForNewField != null) {
            signer.setPageRect(rectangleForNewField);
        }
        if (fontSize != null) {
            appearance.setFontSize((float) fontSize);
        }
        signer.getSignatureField().setReuseAppearance(setReuseAppearance);

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, subfilter);
    }

    private static Map<Integer, List<Rectangle>> getTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Collections.singletonList(ignoredArea));
        return result;
    }
}
