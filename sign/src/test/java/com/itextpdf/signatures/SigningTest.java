/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.signatures;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

//TODO: add some validation of results in future
@Category(IntegrationTest.class)
public class SigningTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/";
    public static final String destinationFolder = "./target/test/com/itextpdf/signatures/";
    public static final String keystorePath = "./src/test/resources/com/itextpdf/signatures/ks";
    public static final char[] password = "password".toCharArray();

    private BouncyCastleProvider provider;
    private Certificate[] chain;
    private PrivateKey pk;

    @Before
    public void init() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(keystorePath), password);
        String alias = ks.aliases().nextElement();
        pk = (PrivateKey) ks.getKey(alias, password);
        chain = ks.getCertificateChain(alias);

        new File(destinationFolder).mkdirs();
    }

    @Test
    public void simpleSigningTest() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleDocument.pdf";
        String fileName = "simpleSignature.pdf";
        String dest = destinationFolder + fileName;

        int x = 36;
        int y = 648;
        int w = 200;
        int h = 100;
        Rectangle rect = new Rectangle(x, y, w, h);

        String fieldName =  "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, false);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 690, 155, 15))));
    }

    @Test
    public void signingIntoExistingFieldTest01() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "emptySignature01.pdf"; //field is merged with widget and has /P key
        String fileName = "filledSignatureFields01.pdf";
        String dest = destinationFolder + fileName;

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, false, false);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 725, 155, 15))));
    }

    @Test
    public void signingIntoExistingFieldTest02() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "emptySignature02.pdf"; //field is merged with widget and widget doesn't have /P key
        String fileName = "filledSignatureFields02.pdf";
        String dest = destinationFolder + fileName;

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, false, false);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 725, 155, 15))));

    }

    @Test
    public void signingIntoExistingReuseAppearanceTest() throws GeneralSecurityException, IOException {
        String src = sourceFolder + "emptySigWithAppearance.pdf";
        String dest = destinationFolder + "filledSignatureReuseAppearanceFields.pdf";

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, true, false);
    }

    @Test
    public void signingTaggedDocument() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleTaggedDocument.pdf";
        String dest = destinationFolder + "signedTaggedDocument.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, false);
    }

    @Test
    public void signingTaggedDocumentAppendMode() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleTaggedDocument.pdf";
        String dest = destinationFolder + "signedTaggedDocumentAppendMode.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true);
    }

    @Test
    public void signingDocumentAppendModeIndirectPageAnnots() throws GeneralSecurityException, IOException, InterruptedException {
        String file =  "AnnotsIndirect.pdf";
        String src = sourceFolder + file;
        String dest = destinationFolder + "signed" + file;

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk,
                DigestAlgorithms.SHA256, provider.getName(),
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + file, destinationFolder,
                "diff_", getTestMap(new Rectangle(30, 245, 200, 12))));
    }

    protected void sign(String src, String name, String dest,
                        Certificate[] chain, PrivateKey pk,
                        String digestAlgorithm, String provider, PdfSigner.CryptoStandard subfilter,
                        String reason, String location, Rectangle rectangleForNewField, boolean setReuseAppearance, boolean isAppendMode)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), isAppendMode);

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason(reason)
                .setLocation(location)
                .setReuseAppearance(setReuseAppearance);

        if (rectangleForNewField != null) {
            appearance.setPageRect(rectangleForNewField);
        }

        signer.setFieldName(name);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, provider);
        IExternalDigest digest = new BouncyCastleDigest();
        signer.signDetached(digest, pks, chain, null, null, null, 0, subfilter);
    }

    private static Map<Integer, List<Rectangle> > getTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle> > result = new HashMap<Integer, List<Rectangle> >();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }
}
