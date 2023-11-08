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
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

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

@Category(BouncyCastleIntegrationTest.class)
public class SignedAppearanceTextTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/SignedAppearanceTextTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/SignedAppearanceTextTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

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
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(fieldName)
                .setContent(new SignedAppearanceText());
        sign(srcFile, fieldName, outPdf, "Test 1", "TestCity 1", rect, appearance);

        Assert.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(36, 676, 200, 15))));

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void defaultSignedAppearanceTextAndSignerTest() throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "simpleDocument.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_defaultSignedAppearanceTextAndSignerTest.pdf";
        String outPdf = DESTINATION_FOLDER + "defaultSignedAppearanceTextAndSignerTest.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature2";
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(fieldName)
                .setContent("", new SignedAppearanceText());
        sign(srcFile, fieldName, outPdf, "Test 2", "TestCity 2", rect, appearance);

        Assert.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(136, 686, 100, 25))));

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
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
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(fieldName)
                .setContent(new SignedAppearanceText(), ImageDataFactory.create(imagePath));
        sign(srcFile, fieldName, outPdf, "Test 3", "TestCity 3", rect, appearance);

        Assert.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(186, 681, 150, 36))));

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
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
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(fieldName)
                .setContent(new SignedAppearanceText()
                        .setSignedBy("wrong signer")
                        .setReasonLine("Signing reason: " + reason)
                        .setLocationLine("Signing location: " + location)
                        .setSignDate(DateTimeUtil.getCurrentTimeCalendar()));
        sign(srcFile, fieldName, outPdf, reason, location, rect, appearance);

        Assert.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(36, 676, 200, 15))));

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    protected void sign(String src, String name, String dest,
                        String reason, String location, Rectangle rectangleForNewField,
                        SignatureFieldAppearance appearance)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);
        StampingProperties properties = new StampingProperties();
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), properties);

        signer.setCertificationLevel(PdfSigner.NOT_CERTIFIED);
        signer.setFieldName(name);
        signer.setReason(reason).setLocation(location).setSignatureAppearance(appearance);
        if (rectangleForNewField != null) {
            signer.setPageRect(rectangleForNewField);
        }

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
