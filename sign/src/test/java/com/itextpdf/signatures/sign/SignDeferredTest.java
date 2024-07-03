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
import com.itextpdf.forms.fields.PdfFormCreator;
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
import com.itextpdf.signatures.TestSignUtils;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.SignaturesCompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class SignDeferredTest extends ExtendedITextTest {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";
    private static final String sourceFolder = "./src/test/resources/com/itextpdf/signatures/sign/SignDeferredTest/";
    private static final String destinationFolder = "./target/test/com/itextpdf/signatures/sign/SignDeferredTest/";

    private static final char[] password = "testpassphrase".toCharArray();
    private static final String HASH_ALGORITHM = DigestAlgorithms.SHA256;

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
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
        PdfSigner signer = new PdfSigner(reader, FileUtil.getFileOutputStream(output), new StampingProperties());
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
    public void prepareDocForSignDeferredNotEnoughSizeTest() throws IOException {
        String input = sourceFolder + "helloWorldDoc.pdf";

        String sigFieldName = "DeferredSignature1";
        PdfName filter = PdfName.Adobe_PPKLite;
        PdfName subFilter = PdfName.Adbe_pkcs7_detached;

        PdfReader reader = new PdfReader(input);
        PdfSigner signer = new PdfSigner(reader, new ByteArrayOutputStream(), new StampingProperties());
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance
                .setLayer2Text("Signature field which signing is deferred.")
                .setPageRect(new Rectangle(36, 600, 200, 100))
                .setPageNumber(1);
        signer.setFieldName(sigFieldName);
        IExternalSignatureContainer external = new ExternalBlankSignatureContainer(filter, subFilter);

        // This size is definitely not enough
        int estimatedSize = -1;
        Exception e = Assertions.assertThrows(IOException.class,
                () -> signer.signExternalContainer(external, estimatedSize));
        Assertions.assertEquals(SignExceptionMessageConstant.NOT_ENOUGH_SPACE, e.getMessage());
    }

    @Test
    public void prepareDocForSignDeferredLittleSpaceTest() throws IOException {
        String input = sourceFolder + "helloWorldDoc.pdf";

        String sigFieldName = "DeferredSignature1";
        PdfName filter = PdfName.Adobe_PPKLite;
        PdfName subFilter = PdfName.Adbe_pkcs7_detached;

        PdfReader reader = new PdfReader(input);
        PdfSigner signer = new PdfSigner(reader, new ByteArrayOutputStream(), new StampingProperties());
        PdfSignatureAppearance appearance = signer.getSignatureAppearance();
        appearance
                .setLayer2Text("Signature field which signing is deferred.")
                .setPageRect(new Rectangle(36, 600, 200, 100))
                .setPageNumber(1);
        signer.setFieldName(sigFieldName);
        IExternalSignatureContainer external = new ExternalBlankSignatureContainer(filter, subFilter);

        // This size is definitely not enough, however, the size check will pass.
        // The test will fail lately on an invalid key
        int estimatedSize = 0;
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> signer.signExternalContainer(external, estimatedSize));
        Assertions.assertEquals(SignExceptionMessageConstant.TOO_BIG_KEY, e.getMessage());
    }

    @Test
    public void deferredHashCalcAndSignTest01() throws IOException, GeneralSecurityException, InterruptedException,
            AbstractPKCSException, AbstractOperatorCreationException {
        String srcFileName = sourceFolder + "templateForSignCMSDeferred.pdf";
        String outFileName = destinationFolder + "deferredHashCalcAndSignTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_deferredHashCalcAndSignTest01.pdf";

        String signCertFileName = certsSrc + "signCertRsa01.pem";
        Certificate[] signChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        IExternalSignatureContainer extSigContainer = new CmsDeferredSigner(signPrivateKey, signChain);

        String sigFieldName = "DeferredSignature1";
        PdfDocument docToSign = new PdfDocument(new PdfReader(srcFileName));
        OutputStream outStream = FileUtil.getFileOutputStream(outFileName);
        PdfSigner.signDeferred(docToSign, sigFieldName, outStream, extSigContainer);
        docToSign.close();
        outStream.close();


        // validate result
        TestSignUtils.basicCheckSignedDoc(outFileName, sigFieldName);
        Assertions.assertNull(new CompareTool().compareVisually(outFileName, cmpFileName, destinationFolder, null));
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    @Test
    public void calcHashOnDocCreationThenDeferredSignTest01() throws IOException, GeneralSecurityException,
            InterruptedException, AbstractPKCSException, AbstractOperatorCreationException {
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
        String signCertFileName = certsSrc + "signCertRsa01.pem";
        Certificate[] signChain = PemFileHelper.readFirstChain(signCertFileName);
        PrivateKey signPrivateKey = PemFileHelper.readFirstKey(signCertFileName, password);
        byte[] cmsSignature = signDocBytesHash(docBytesHash, signPrivateKey, signChain);


        // fill the signature to the presigned document
        ReadySignatureSigner extSigContainer = new ReadySignatureSigner(cmsSignature);

        PdfDocument docToSign = new PdfDocument(new PdfReader(new ByteArrayInputStream(preSignedBytes)));
        OutputStream outStream = FileUtil.getFileOutputStream(outFileName);
        PdfSigner.signDeferred(docToSign, sigFieldName, outStream, extSigContainer);
        docToSign.close();
        outStream.close();


        // validate result
        TestSignUtils.basicCheckSignedDoc(outFileName, sigFieldName);
        Assertions.assertNull(new CompareTool().compareVisually(outFileName, cmpFileName, destinationFolder, null));
        Assertions.assertNull(SignaturesCompareTool.compareSignatures(outFileName, cmpFileName));
    }

    static void validateTemplateForSignedDeferredResult(String output, String sigFieldName, PdfName filter, PdfName subFilter, int estimatedSize) throws IOException {
        PdfDocument outDocument = new PdfDocument(new PdfReader(output));

        PdfObject outSigDictObj = PdfFormCreator.getAcroForm(outDocument, false).getField(sigFieldName).getValue();
        Assertions.assertTrue(outSigDictObj.isDictionary());

        PdfDictionary outSigDict = (PdfDictionary) outSigDictObj;

        PdfArray byteRange = outSigDict.getAsArray(PdfName.ByteRange);
        Assertions.assertNotNull(byteRange);
        Assertions.assertTrue(byteRange.size() == 4);

        Assertions.assertEquals(filter, outSigDict.getAsName(PdfName.Filter));
        Assertions.assertEquals(subFilter, outSigDict.getAsName(PdfName.SubFilter));

        PdfString outSigContents = outSigDict.getAsString(PdfName.Contents);

        Assertions.assertTrue(outSigContents.isHexWriting());
        Assertions.assertArrayEquals(new byte[estimatedSize], outSigContents.getValueBytes());
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

            byte[] attributes = pkcs7.getAuthenticatedAttributeBytes(docBytesHash, PdfSigner.CryptoStandard.CMS, null, null);

            PrivateKeySignature signature =
                    new PrivateKeySignature(pk, HASH_ALGORITHM, FACTORY.getProviderName());
            byte[] attrSign = signature.sign(attributes);

            pkcs7.setExternalSignatureValue(attrSign, null, signature.getSignatureAlgorithmName());
            signatureContent = pkcs7.getEncodedPKCS7(docBytesHash);
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
