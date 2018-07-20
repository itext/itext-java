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

import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.LtvVerifier;
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
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO: add some validation of results in future
@Category(IntegrationTest.class)
public class SigningTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/SigningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/SigningTest/";
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
                DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, false);

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
                DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, false, false);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 725, 200, 15))));
    }

    @Test
    public void signingIntoExistingFieldTest02() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "emptySignature02.pdf"; //field is merged with widget and widget doesn't have /P key
        String fileName = "filledSignatureFields02.pdf";
        String dest = destinationFolder + fileName;

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, false, false);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + fileName, destinationFolder,
                "diff_", getTestMap(new Rectangle(67, 725, 200, 15))));

    }

    @Test
    public void signingIntoExistingReuseAppearanceTest() throws GeneralSecurityException, IOException {
        String src = sourceFolder + "emptySigWithAppearance.pdf";
        String dest = destinationFolder + "filledSignatureReuseAppearanceFields.pdf";

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", null, true, false);
    }

    @Test
    public void signingTaggedDocument() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleTaggedDocument.pdf";
        String dest = destinationFolder + "signedTaggedDocument.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, false);
    }

    @Test
    public void signingTaggedDocumentAppendMode() throws GeneralSecurityException, IOException, InterruptedException {
        String src = sourceFolder + "simpleTaggedDocument.pdf";
        String dest = destinationFolder + "signedTaggedDocumentAppendMode.pdf";

        Rectangle rect = new Rectangle(36, 648, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true);
    }

    @Test
    public void signingDocumentAppendModeIndirectPageAnnots() throws GeneralSecurityException, IOException, InterruptedException {
        String file =  "AnnotsIndirect.pdf";
        String src = sourceFolder + file;
        String dest = destinationFolder + "signed" + file;

        Rectangle rect = new Rectangle(30, 200, 200, 100);

        String fieldName = "Signature1";
        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256,
                PdfSigner.CryptoStandard.CADES, "Test 1", "TestCity", rect, false, true);

        Assert.assertNull(new CompareTool().compareVisually(dest, sourceFolder + "cmp_" + file, destinationFolder,
                "diff_", getTestMap(new Rectangle(30, 245, 200, 12))));
    }

    @Test
    public void signEncryptedDoc01() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "encrypted.pdf";
        String src = sourceFolder + fileName;
        String dest = destinationFolder + "signed_" + fileName;

        String fieldName = "Signature1";

        byte[] ownerPass = "World".getBytes();
        PdfReader reader = new PdfReader(src, new ReaderProperties().setPassword(ownerPass));
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), true);

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason("Test1")
                .setLocation("TestCity");

        signer.setFieldName(fieldName);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        LtvVerifier verifier = new LtvVerifier(new PdfDocument(new PdfReader(dest, new ReaderProperties().setPassword(ownerPass))));
        verifier.setVerifyRootCertificate(false);
        verifier.verify(null);

        // TODO improve checking in future. At the moment, if the certificate or the signature itself has problems exception will be thrown
    }

    @Test
    public void signEncryptedDoc02() throws GeneralSecurityException, IOException, InterruptedException {
        String fileName = "encrypted_cert.pdf";
        String src = sourceFolder + fileName;
        String dest = destinationFolder + "signed_" + fileName;

        Certificate cert = CryptoUtil.readPublicCertificate(new FileInputStream(sourceFolder + "test.cer"));
        PrivateKey privateKey = Pkcs12FileHelper.readFirstKey(sourceFolder + "test.p12", password, password);
        PdfReader reader = new PdfReader(src, new ReaderProperties().setPublicKeySecurityParams(cert, privateKey, new BouncyCastleProvider().getName(), null));
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), true);

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        // TODO improve testing, e.g. check ID. For not at least we assert that exception is not thrown
    }

    protected void sign(String src, String name, String dest,
                        Certificate[] chain, PrivateKey pk,
                        String digestAlgorithm, PdfSigner.CryptoStandard subfilter,
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
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, subfilter);
    }

    private static Map<Integer, List<Rectangle> > getTestMap(Rectangle ignoredArea) {
        Map<Integer, List<Rectangle> > result = new HashMap<Integer, List<Rectangle> >();
        result.put(1, Arrays.asList(ignoredArea));
        return result;
    }
}
