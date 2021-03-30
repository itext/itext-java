/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)
public class Pdf20SigningTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/Pdf20SigningTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/Pdf20SigningTest/";
    public static final String keystorePath = "./src/test/resources/com/itextpdf/signatures/certs/signCertRsa01.p12";
    public static final char[] password = "testpass".toCharArray();

    private Certificate[] chain;
    private PrivateKey pk;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void before() {
        Security.addProvider(new BouncyCastleProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Before
    public void init() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException,
            UnrecoverableKeyException {
        pk = Pkcs12FileHelper.readFirstKey(keystorePath, password, password);
        chain = Pkcs12FileHelper.readFirstChain(keystorePath, password);
    }

    @Test
    public void signExistingFieldWhenDirectAcroformAndNoSigFlagTest() throws GeneralSecurityException, IOException {
        String src = sourceFolder + "signExistingFieldWhenDirectAcroformAndNoSigFlag.pdf";
        String dest = destinationFolder + "signExistingFieldWhenDirectAcroformAndNoSigFlag.pdf";
        String fieldName = "Signature1";

        sign(src, fieldName, dest, chain, pk, DigestAlgorithms.SHA256, PdfSigner.CryptoStandard.CADES, PdfSigner.NOT_CERTIFIED);

        PdfDocument doc = new PdfDocument(new PdfReader(dest));
        PdfNumber sigFlag = doc.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm).getAsNumber(PdfName.SigFlags);

        Assert.assertEquals(new PdfNumber(3).intValue(), sigFlag.intValue());
    }


    protected void sign(String src, String name, String dest, Certificate[] chain, PrivateKey pk,
            String digestAlgorithm, PdfSigner.CryptoStandard subfilter, int certificationLevel)
            throws GeneralSecurityException, IOException {

        PdfReader reader = new PdfReader(src);

        StampingProperties properties = new StampingProperties();
        properties.useAppendMode();

        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), properties);
        signer.setCertificationLevel(certificationLevel);

        signer.setFieldName(name);
        // Creating the signature
        IExternalSignature pks = new PrivateKeySignature(pk, digestAlgorithm, BouncyCastleProvider.PROVIDER_NAME);
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null,
                0, subfilter);
    }
}
