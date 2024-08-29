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
package com.itextpdf.kernel.crypto.securityhandler;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.crypto.fips.AbstractFipsUnapprovedOperationError;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfBoolean;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfEncryption;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;

import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleIntegrationTest")
public class PublicHandlerUsingAesGcmTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/crypto/securityhandler/PublicHandlerUsingAesGcmTest/";
    public static final String DESTINATION_FOLDER = "./target/test_output/";
    public static final String TEST_INPUT = SOURCE_FOLDER + "test-document.pdf";


    @BeforeAll
    public static void setUp() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        Security.addProvider(FACTORY.getProvider());
    }

    @LogMessages(messages = {@LogMessage(messageTemplate = VersionConforming.NOT_SUPPORTED_AES_GCM, ignore = true),
            @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
                    ignore = true)})
    @Test
    public void testSimpleEncryptDecryptTest() throws Exception {
        String outFile = DESTINATION_FOLDER + "test-output.pdf";
        InputStream fin = FileUtil.getInputStreamForFile(TEST_INPUT);
        OutputStream fout = FileUtil.getFileOutputStream(outFile);

        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assertions.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> doEncrypt(fin, fout));
        } else {
            doEncrypt(fin, fout);
            PrivateKey privateKey = PemFileHelper.readPrivateKeyFromPemFile(
                    FileUtil.getInputStreamForFile(SOURCE_FOLDER + "test.pem"), "testpassphrase".toCharArray());
            decryptWithCertificate("test-document.pdf", "test.cer", privateKey);
        }
    }

    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    @Test
    public void decryptExternalFileTest() throws Exception {
        PrivateKey privateKey = PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "signerkey.pem"), "testpassphrase".toCharArray());
        decryptWithCertificate("externalFile.pdf", "decrypter.cert.pem", privateKey);
    }

    @Test
    public void invalidPdfEncryptionTest01() throws Exception {
        PrivateKey certificateKey = PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "signerkey.pem"), "testpassphrase".toCharArray());
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "decrypter.cert.pem"));
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.R, new PdfNumber(7));
        encMap.put(PdfName.V, new PdfNumber(6));
        encMap.put(PdfName.EncryptMetadata, PdfBoolean.TRUE);
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, certificateKey,
                certificate, FACTORY.getProviderName(), null));
    }

    @Test
    public void invalidPdfEncryptionTest02() throws Exception {
        PrivateKey certificateKey = PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "signerkey.pem"), "testpassphrase".toCharArray());
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "decrypter.cert.pem"));
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.R, new PdfNumber(7));
        encMap.put(PdfName.V, new PdfNumber(6));
        PdfDictionary embeddedFilesDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.FlateDecode, new PdfDictionary());
        encMap.put(PdfName.CF, embeddedFilesDict);
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, certificateKey,
                certificate, FACTORY.getProviderName(), null));
    }

    @Test
    public void invalidPdfEncryptionTest03() throws Exception {
        PrivateKey certificateKey = PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "signerkey.pem"), "testpassphrase".toCharArray());
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "decrypter.cert.pem"));
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.R, new PdfNumber(7));
        encMap.put(PdfName.V, new PdfNumber(6));
        PdfDictionary embeddedFilesDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.DefaultCryptFilter, new PdfDictionary());
        encMap.put(PdfName.CF, embeddedFilesDict);
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, certificateKey,
                certificate, FACTORY.getProviderName(), null));
    }


    private void doEncrypt(InputStream input, OutputStream output) throws IOException, CertificateException {

        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "test.cer"));

        WriterProperties writerProperties = new WriterProperties().setPublicKeyEncryption(
                new Certificate[] {certificate}, new int[]{EncryptionConstants.ALLOW_PRINTING}, EncryptionConstants.ENCRYPTION_AES_GCM);


        // instantiate input/output document
        PdfDocument docIn = new PdfDocument(new PdfReader(input));
        PdfDocument docOut = new PdfDocument(new PdfWriter(output, writerProperties));
        // copy one page from input to output
        docIn.copyPagesTo(1, 1, docOut);
        docIn.close();
        docOut.close();
    }



    private void decryptWithCertificate(String fileName, String certificateName, PrivateKey certificateKey)
            throws IOException, CertificateException, InterruptedException {
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(SOURCE_FOLDER + certificateName));
        ReaderProperties readerProperties = new ReaderProperties().setPublicKeySecurityParams(
                certificate, certificateKey, FACTORY.getProviderName(), null);

        String decryptedPath = DESTINATION_FOLDER + "decrypted_" + fileName;
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + fileName, readerProperties), new PdfWriter(
                decryptedPath))) {
            pdfDocument.close();
            CompareTool compareTool = new CompareTool();
            String errorMessage = compareTool.compareByContent(decryptedPath, SOURCE_FOLDER + "cmp_" + fileName,
                    DESTINATION_FOLDER, "diff_");
            if (errorMessage != null) {
                Assertions.fail(errorMessage);
            }

        }
    }
}
