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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.signutils.Pkcs12FileHelper;

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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class EncryptedSigningTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/sign"
            + "/EncryptedSigningTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/sign/EncryptedSigningTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] PASSWORD = "testpass".toCharArray();

    private Certificate[] chain;
    private PrivateKey pk;

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void init()
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
            UnrecoverableKeyException {
        pk = Pkcs12FileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.p12", PASSWORD, PASSWORD);
        chain = Pkcs12FileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.p12", PASSWORD);
    }

    @Test
    public void signEncryptedPdfTest() throws GeneralSecurityException, IOException {
        String srcFile = SOURCE_FOLDER + "encrypted.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signedEncrypted.pdf";
        String outPdf = DESTINATION_FOLDER + "signedEncrypted.pdf";

        String fieldName = "Signature1";

        byte[] ownerPass = "World".getBytes();
        PdfReader reader = new PdfReader(srcFile, new ReaderProperties().setPassword(ownerPass));
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(outPdf),
                new StampingProperties().useAppendMode());

        // Creating the appearance
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason("Test1")
                .setLocation("TestCity");

        signer.setFieldName(fieldName);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        //TODO DEVSIX-5637 Improve SignaturesCompareTool#compareSignatures to check encrypted pdf
        Assert.assertEquals(PdfException.BadUserPassword, SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }

    @Test
    public void signCertificateSecurityPdfTest() throws IOException, GeneralSecurityException {
        String srcFile = SOURCE_FOLDER + "signCertificateSecurityPdf.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_signCertificateSecurityPdf.pdf";
        String outPdf = DESTINATION_FOLDER + "signCertificateSecurityPdf.pdf";

        PdfReader reader = new PdfReader(srcFile, new ReaderProperties()
                .setPublicKeySecurityParams(chain[0], pk, new BouncyCastleProvider().getName(), null));
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(outPdf),
                new StampingProperties().useAppendMode());

        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
                BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        //TODO DEVSIX-5637 Improve SignaturesCompareTool#compareSignatures to check encrypted pdf
        Assert.assertEquals(PdfException.CertificateIsNotProvidedDocumentIsEncryptedWithPublicKeyCertificate,
                SignaturesCompareTool.compareSignatures(outPdf, cmpPdf));
    }
}
