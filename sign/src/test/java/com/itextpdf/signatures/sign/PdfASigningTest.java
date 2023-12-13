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

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
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
public class PdfASigningTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/PdfASigningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/PdfASigningTest/";
    public static final String keystorePath = "./src/test/resources/com/itextpdf/signatures/certs/signCertRsa01.p12";
    public static final char[] password = "testpass".toCharArray();
    public static final String FONT = "./src/test/resources/com/itextpdf/signatures/font/FreeSans.ttf";

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

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 575, 155, 15))));
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
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), properties);

        signer.setCertificationLevel(certificationLevel);

        PdfFont font = PdfFontFactory.createFont(FONT, "WinAnsi", EmbeddingStrategy.PREFER_EMBEDDED);

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location)
                .setLayer2Font(font)
                .setReuseAppearance(setReuseAppearance);

        if (rectangleForNewField != null) {
            appearance.setPageRect(rectangleForNewField);
        }
        if (fontSize != null) {
            appearance.setLayer2FontSize((float) fontSize);
        }

        signer.setFieldName(name);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, subfilter);
    }

    private static Map<Integer, List<Rectangle>> getTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle>> result = new HashMap<Integer, List<Rectangle>>();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }

}
