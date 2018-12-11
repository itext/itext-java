/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.ExternalBlankSignatureContainer;
import com.itextpdf.signatures.IExternalSignatureContainer;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignatureAppearance;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;
import com.itextpdf.signatures.testutils.Pkcs12FileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SignDeferredTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/SignDeferredTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/SignDeferredTest/";

    private static final char[] password = "testpass".toCharArray();
    private static final String HASH_ALGORITHM = DigestAlgorithms.SHA256;

    @BeforeClass
    public static void before() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void prepareDocForSignDeferredTest() throws IOException, GeneralSecurityException {
        String input = sourceFolder + "helloWorldDoc.pdf";
        String output = destinationFolder + "newTemplateForSignDeferred.pdf";

        String sigFieldName = "DeferredSignature1";
        PdfName filter = PdfName.Adobe_PPKLite;
        PdfName subFilter = PdfName.Adbe_pkcs7_detached;
        int estimatedSize = 8192;

        PdfReader reader = new PdfReader(input);
        PdfSigner signer = new PdfSigner(reader, new FileOutputStream(output), new StampingProperties());
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance
                .setLayer2Text("Signature field which signing is deferred.")
                .setPageRect(new Rectangle(36, 600, 200, 100))
                .setPageNumber(1);
        signer.setFieldName(sigFieldName);
        IExternalSignatureContainer external = new ExternalBlankSignatureContainer(filter, subFilter);
        signer.signExternalContainer(external, estimatedSize);

        // validate result
        validateTemplateForSignedDeferredResult(output, sigFieldName, filter, subFilter, estimatedSize);
    }

    @Test
    public void deferredHashCalcAndSignTest01() throws IOException, GeneralSecurityException, InterruptedException {
        String srcFileName = sourceFolder + "templateForSignCMSDeferred.pdf";
        String outFileName = destinationFolder + "deferredHashCalcAndSignTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_deferredHashCalcAndSignTest01.pdf";

        String signCertFileName = certsSrc + "signCertRsa01.p12";
        Certificate[] signChain = Pkcs12FileHelper.readFirstChain(signCertFileName, password);
        PrivateKey signPrivateKey = Pkcs12FileHelper.readFirstKey(signCertFileName, password, password);
        IExternalSignatureContainer extSigContainer = new CmsDeferredSigner(signPrivateKey, signChain);

        String sigFieldName = "DeferredSignature1";
        PdfDocument docToSign = new PdfDocument(new PdfReader(srcFileName));
        FileOutputStream outStream = new FileOutputStream(outFileName);
        PdfSigner.signDeferred(docToSign, sigFieldName, outStream, extSigContainer);
        docToSign.close();
        outStream.close();


        // validate result
        PadesSigTest.basicCheckSignedDoc(outFileName, sigFieldName);
        Assert.assertNull(new CompareTool().compareVisually(outFileName, cmpFileName, destinationFolder, null));
    }

    @Test
    public void calcHashOnDocCreationThenDeferredSignTest01() throws IOException, GeneralSecurityException, InterruptedException {
        String input = sourceFolder + "helloWorldDoc.pdf";
        String outFileName = destinationFolder + "calcHashOnDocCreationThenDeferredSignTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_calcHashOnDocCreationThenDeferredSignTest01.pdf";

        // pre-calculate hash on creating pre-signed PDF
        String sigFieldName = "DeferredSignature1";
        PdfName filter = PdfName.Adobe_PPKLite;
        PdfName subFilter = PdfName.Adbe_pkcs7_detached;
        int estimatedSize = 8192;

        PdfReader reader = new PdfReader(input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfSigner signer = new PdfSigner(reader, baos, new StampingProperties());
        signer.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED);
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance
                .setLayer2Text("Signature field which signing is deferred.")
                .setPageRect(new Rectangle(36, 600, 200, 100))
                .setPageNumber(1);
        signer.setFieldName(sigFieldName);
        DigestCalcBlankSigner external = new DigestCalcBlankSigner(filter, subFilter);
        signer.signExternalContainer(external, estimatedSize);
        byte[] docBytesHash = external.getDocBytesHash();
        byte[] preSignedBytes = baos.toByteArray();


        // sign the hash
        String signCertFileName = certsSrc + "signCertRsa01.p12";
        Certificate[] signChain = Pkcs12FileHelper.readFirstChain(signCertFileName, password);
        PrivateKey signPrivateKey = Pkcs12FileHelper.readFirstKey(signCertFileName, password, password);
        byte[] cmsSignature = signDocBytesHash(docBytesHash, signPrivateKey, signChain);


        // fill the signature to the presigned document
        ReadySignatureSigner extSigContainer = new ReadySignatureSigner(cmsSignature);

        PdfDocument docToSign = new PdfDocument(new PdfReader(new ByteArrayInputStream(preSignedBytes)));
        FileOutputStream outStream = new FileOutputStream(outFileName);
        PdfSigner.signDeferred(docToSign, sigFieldName, outStream, extSigContainer);
        docToSign.close();
        outStream.close();


        // validate result
        PadesSigTest.basicCheckSignedDoc(outFileName, sigFieldName);
        Assert.assertNull(new CompareTool().compareVisually(outFileName, cmpFileName, destinationFolder, null));
    }

    static void validateTemplateForSignedDeferredResult(String output, String sigFieldName, PdfName filter, PdfName subFilter, int estimatedSize) throws IOException {
        PdfDocument outDocument = new PdfDocument(new PdfReader(output));

        PdfObject outSigDictObj = PdfAcroForm.getAcroForm(outDocument, false).getField(sigFieldName).getValue();
        Assert.assertTrue(outSigDictObj.isDictionary());

        PdfDictionary outSigDict = (PdfDictionary) outSigDictObj;

        PdfArray byteRange = outSigDict.getAsArray(PdfName.ByteRange);
        Assert.assertNotNull(byteRange);
        Assert.assertTrue(byteRange.size() == 4);

        Assert.assertEquals(filter, outSigDict.getAsName(PdfName.Filter));
        Assert.assertEquals(subFilter, outSigDict.getAsName(PdfName.SubFilter));

        PdfString outSigContents = outSigDict.getAsString(PdfName.Contents);

        Assert.assertTrue(outSigContents.isHexWriting());
        Assert.assertArrayEquals(new byte[estimatedSize], outSigContents.getValueBytes());
    }

    static byte[] calcDocBytesHash(InputStream docBytes) {
        byte[] docBytesHash = null;
        try {
            docBytesHash = DigestAlgorithms.digest(docBytes, SignTestPortUtil.getMessageDigest(HASH_ALGORITHM));
        } catch (Exception e) {
            // dummy catch clause
        }
        return docBytesHash;
    }

    static byte[] signDocBytesHash(byte[] docBytesHash, PrivateKey pk, Certificate[] chain) {
        if (pk == null || chain == null) {
            return null;
        }

        byte[] signatureContent = null;
        try {
            PdfPKCS7 pkcs7 = new PdfPKCS7(null, chain, HASH_ALGORITHM, null, new BouncyCastleDigest(), false);

            byte[] attributes = pkcs7.getAuthenticatedAttributeBytes(docBytesHash, null, null, PdfSigner.CryptoStandard.CMS);

            PrivateKeySignature signature = new PrivateKeySignature(pk, HASH_ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
            byte[] attrSign = signature.sign(attributes);

            pkcs7.setExternalDigest(attrSign, null, signature.getEncryptionAlgorithm());
            signatureContent = pkcs7.getEncodedPKCS7(docBytesHash, null, null, null, PdfSigner.CryptoStandard.CMS);
        } catch (GeneralSecurityException e) {
            // dummy catch clause
        }
        return signatureContent;
    }

    static class CmsDeferredSigner implements IExternalSignatureContainer {
        private PrivateKey pk;
        private Certificate[] chain;

        public CmsDeferredSigner(PrivateKey pk, Certificate[] chain) {
            this.pk = pk;
            this.chain = chain;
        }

        public byte[] sign(InputStream docBytes) {
            byte[] docBytesHash = calcDocBytesHash(docBytes);

            byte[] signatureContent = null;
            if (docBytesHash != null) {
                // sign the hash and create PKCS7 CMS signature
                signatureContent = signDocBytesHash(docBytesHash, pk, chain);
            }

            if (signatureContent == null) {
                signatureContent = new byte[0];
            }

            return signatureContent;
        }

        public void modifySigningDictionary(PdfDictionary signDic) {
        }
    }

    static class DigestCalcBlankSigner implements IExternalSignatureContainer {
        private final PdfName filter;
        private final PdfName subFilter;

        private byte[] docBytesHash;

        public DigestCalcBlankSigner(PdfName filter, PdfName subFilter) {
            this.filter = filter;
            this.subFilter = subFilter;
        }

        public byte[] getDocBytesHash() {
            return docBytesHash;
        }

        public byte[] sign(InputStream docBytes) {
            docBytesHash = calcDocBytesHash(docBytes);
            return new byte[0];
        }

        public void modifySigningDictionary(PdfDictionary signDic) {
            signDic.put(PdfName.Filter, filter);
            signDic.put(PdfName.SubFilter, subFilter);
        }
    }

    static class ReadySignatureSigner implements IExternalSignatureContainer {
        private byte[] cmsSignatureContents;

        public ReadySignatureSigner(byte[] cmsSignatureContents) {
            this.cmsSignatureContents = cmsSignatureContents;
        }


        public byte[] sign(InputStream docBytes) {
            return cmsSignatureContents;
        }

        public void modifySigningDictionary(PdfDictionary signDic) {
        }
    }
}
