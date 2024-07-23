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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

@Category(BouncyCastleIntegrationTest.class)
public class TaggedPdfSigningTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign/TaggedPdfSigningTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/TaggedPdfSigningTest/";
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
    public void signingTaggedDocumentTest()
            throws GeneralSecurityException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        String srcFile = SOURCE_FOLDER + "simpleTaggedDocument.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_simpleTaggedDocument.pdf";
        String outPdf = DESTINATION_FOLDER + "simpleTaggedDocument.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, false);

        Assert.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(rect)));

        Assert.assertNull(new CompareTool().compareTagStructures(outPdf, cmpPdf));

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void signingTaggedDocumentAppendModeTest()
            throws GeneralSecurityException, IOException, InterruptedException, ParserConfigurationException, SAXException {
        String srcFile = SOURCE_FOLDER + "simpleTaggedDocumentAppendMode.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_simpleTaggedDocumentAppendMode.pdf";
        String outPdf = DESTINATION_FOLDER + "signedTaggedDocumentAppendMode.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        sign(srcFile, fieldName, outPdf, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1",
                "TestCity", rect, false, true);

        Assert.assertNull(new CompareTool().compareVisually(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_",
                getTestMap(rect)));

        Assert.assertNull(new CompareTool().compareTagStructures(outPdf, cmpPdf));

        Assert.assertNull(SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    protected void sign(String src, String name, String dest,
            Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, PdfSigner.CryptoStandard subfilter,
            String reason, String location, Rectangle rectangleForNewField, boolean setReuseAppearance,
            boolean isAppendMode) throws GeneralSecurityException, IOException {
        sign(src, name, dest, chain, pk, digestAlgorithm, subfilter, reason, location, rectangleForNewField,
                setReuseAppearance, isAppendMode, PdfSigner.NOT_CERTIFIED, null);
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

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location)
                .setReuseAppearance(setReuseAppearance);

        if (rectangleForNewField != null) {
            appearance.setPageRect(rectangleForNewField);
        }
        if (fontSize != null) {
            appearance.setLayer2FontSize((float) fontSize);
        }

        signer.setFieldName(name);
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
