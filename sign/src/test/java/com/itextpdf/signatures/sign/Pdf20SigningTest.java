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
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
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
public class Pdf20SigningTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/Pdf20SigningTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/Pdf20SigningTest/";
    private static final String KEYSTORE_PATH = "./src/test/resources/com/itextpdf/signatures/certs/signCertRsa01.pem";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

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
    public void signExistingFieldWhenDirectAcroformAndNoSigFlagTest() throws GeneralSecurityException, IOException {
        String srcFile = SOURCE_FOLDER + "signExistingFieldWhenDirectAcroformAndNoSigFlag.pdf";
        String outPdf = DESTINATION_FOLDER + "signExistingFieldWhenDirectAcroformAndNoSigFlag.pdf";

        String fieldName = "Signature1";

        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES,
                PdfSigner.NOT_CERTIFIED);

        PdfDocument doc = new PdfDocument(new PdfReader(outPdf));
        PdfNumber sigFlag = doc.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm)
                .getAsNumber(PdfName.SigFlags);

        Assert.assertEquals(new PdfNumber(3).intValue(), sigFlag.intValue());
    }

    @Test
    public void signPdf2CertificationAfterApprovalTest() {
        String srcFile = SOURCE_FOLDER + "approvalSignedDocPdf2.pdf";
        String outPdf = DESTINATION_FOLDER + "signedPdf2CertificationAfterApproval.pdf";

        Rectangle rect = new Rectangle(30, 50, 200, 100);

        String fieldName = "Signature2";

        Exception e = Assert.assertThrows(PdfException.class,
                () -> sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.RIPEMD160,
                        PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true,
                        PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED, null));
        Assert.assertEquals(
                SignExceptionMessageConstant.CERTIFICATION_SIGNATURE_CREATION_FAILED_DOC_SHALL_NOT_CONTAIN_SIGS,
                e.getMessage());
    }

    @Test
    public void signedTwicePdf2Test() throws GeneralSecurityException, IOException {
        String srcFile = SOURCE_FOLDER + "signedTwice.pdf";
        String cmpPdfFileThree = SOURCE_FOLDER + "cmp_signedTwice.pdf";
        String outPdfFileOne = DESTINATION_FOLDER + "signedOnce.pdf";
        String outPdfFileTwo = DESTINATION_FOLDER + "updated.pdf";
        String outPdfFileThree = DESTINATION_FOLDER + "signedTwice.pdf";

        // sign document
        Rectangle rectangle1 = new Rectangle(36, 100, 200, 100);
        sign(srcFile, "Signature1", outPdfFileOne, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES,
                "Sign 1", "TestCity", rectangle1, false, true);

        // update document
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(outPdfFileOne), new PdfWriter(outPdfFileTwo),
                new StampingProperties().useAppendMode());
        pdfDoc.addNewPage();
        pdfDoc.close();

        // sign document again
        Rectangle rectangle2 = new Rectangle(236, 100, 200, 100);
        sign(outPdfFileTwo, "Signature2", outPdfFileThree, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Sign 2", "TestCity", rectangle2, false, true);
        Map<Integer, List<Rectangle>> map = new HashMap<>();
        List<Rectangle> list = new ArrayList<>();
        list.add(rectangle1);
        list.add(rectangle2);
        map.put(1, list);

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdfFileThree, cmpPdfFileThree));
    }

    @Test
    public void signPdf2CmsTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "signPdf2Cms.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signPdf2Cms.pdf";
        String outPdf = DESTINATION_FOLDER + "signPdf2Cms.pdf";

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CMS, "Test 1",
                "TestCity", rect, false, true, PdfSigner.NOT_CERTIFIED, 12f);

        Assert.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(rect)));

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void signPdf2CadesTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "signPdf2Cades.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signPdf2Cades.pdf";
        String outPdf = DESTINATION_FOLDER + "signPdf2Cades.pdf";

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true, PdfSigner.NOT_CERTIFIED, 12f);

        Assert.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(rect)));

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    protected void sign(String src, String name, String dest, Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, PdfSigner.CryptoStandard subfilter, int certificationLevel)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);

        StampingProperties properties = new StampingProperties();
        properties.useAppendMode();

        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(dest), properties);
        signer.setCertificationLevel(certificationLevel);

        signer.setFieldName(name);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null,
                0, subfilter);
    }

    protected void sign(String src, String name, String dest,
            Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, PdfSigner.CryptoStandard subfilter,
            String reason, String location, Rectangle rectangleForNewField, boolean setReuseAppearance,
            boolean isAppendMode, int certificationLevel, Float fontSize)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);
        StampingProperties properties = new StampingProperties();
        if (isAppendMode) {
            properties.useAppendMode();
        }
        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(dest), properties);

        signer.setCertificationLevel(certificationLevel);
        signer.setFieldName(name);

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location)
                .setReuseAppearance(setReuseAppearance);

        if (rectangleForNewField != null) {
            signer.setPageRect(rectangleForNewField);
        }
        if (fontSize != null) {
            appearance.setLayer2FontSize((float) fontSize);
        }
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, subfilter);
    }

    protected void sign(String src, String name, String dest,
            Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, PdfSigner.CryptoStandard subfilter,
            String reason, String location, Rectangle rectangleForNewField, boolean setReuseAppearance,
            boolean isAppendMode) throws GeneralSecurityException, IOException {
        sign(src, name, dest, chain, pk, digestAlgorithm, subfilter, reason, location, rectangleForNewField,
                setReuseAppearance, isAppendMode, PdfSigner.NOT_CERTIFIED, null);
    }

    private static Map<Integer, List<Rectangle>> getTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }
}
