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
import com.itextpdf.forms.fields.properties.SignedAppearanceText;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.AccessPermissions;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.SignerProperties;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;

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
public class AnnotationsSigningTest extends ExtendedITextTest {
    
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/AnnotationsSigningTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/AnnotationsSigningTest/";
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
    public void signingDocumentAppendModeIndirectPageAnnotsTest()
            throws GeneralSecurityException, IOException, InterruptedException {
        String srcFile = SOURCE_FOLDER + "annotsIndirect.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_annotsIndirect.pdf";
        String outPdf = DESTINATION_FOLDER + "annotsIndirect";

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1",
                "TestCity", rect, false, true, AccessPermissions.UNSPECIFIED, 12f);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(rect)));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void signingIntoExistingFieldWithPKeyTest()
            throws GeneralSecurityException, IOException, InterruptedException {
        //field is merged with widget and has /P key
        String srcFile = SOURCE_FOLDER + "emptySignature01.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptySignature01.pdf";
        String outPdf = DESTINATION_FOLDER + "emptySignature01.pdf";

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1",
                "TestCity", null, false, false, AccessPermissions.UNSPECIFIED, 12f);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(41, 693, 237, 781))));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void signingIntoExistingFieldWithoutPKeyTest()
            throws GeneralSecurityException, IOException, InterruptedException {
        //field is merged with widget and widget doesn't have /P key
        String srcFile = SOURCE_FOLDER + "emptySignature02.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptySignature02.pdf";
        String outPdf = DESTINATION_FOLDER + "emptySignature02.pdf";

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1",
                "TestCity", null, false, false, AccessPermissions.UNSPECIFIED, 12f);

        Assertions.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(new Rectangle(41, 693, 237, 781))));

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void signingIntoExistingReuseAppearanceTest()
            throws GeneralSecurityException, IOException {
        String srcFile = SOURCE_FOLDER + "emptySigWithAppearance.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_emptySigWithAppearance.pdf";
        String outPdf = DESTINATION_FOLDER + "emptySigWithAppearance.pdf";

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1",
                "TestCity", null, true, false);

        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    protected void sign(String src, String name, String dest,
            Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, PdfSigner.CryptoStandard subfilter,
            String reason, String location, Rectangle rectangleForNewField, boolean setReuseAppearance,
            boolean isAppendMode) throws GeneralSecurityException, IOException {
        sign(src, name, dest, chain, pk, digestAlgorithm, subfilter, reason, location, rectangleForNewField,
                setReuseAppearance, isAppendMode, AccessPermissions.UNSPECIFIED, null);
    }

    protected void sign(String src, String name, String dest,
            Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, PdfSigner.CryptoStandard subfilter,
            String reason, String location, Rectangle rectangleForNewField, boolean setReuseAppearance,
            boolean isAppendMode, AccessPermissions certificationLevel, Float fontSize)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);
        StampingProperties properties = new StampingProperties();
        if (isAppendMode) {
            properties.useAppendMode();
        }
        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(dest), properties);

        SignerProperties signerProperties = new SignerProperties()
                .setCertificationLevel(certificationLevel)
                .setFieldName(name);

        // Creating the appearance
        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID)
                .setContent(new SignedAppearanceText());
        signerProperties
                .setReason(reason)
                .setLocation(location)
                .setSignatureAppearance(appearance);
        if (rectangleForNewField != null) {
            signerProperties.setPageRect(rectangleForNewField);
        }
        if (fontSize != null) {
            appearance.setFontSize((float) fontSize);
        }
        signer.setSignerProperties(signerProperties);
        signer.getSignatureField().setReuseAppearance(setReuseAppearance);

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, subfilter);
    }

    private static Map<Integer, List<Rectangle>> getTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }
}
