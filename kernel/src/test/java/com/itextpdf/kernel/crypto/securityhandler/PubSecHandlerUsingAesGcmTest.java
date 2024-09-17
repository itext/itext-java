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
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
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
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
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
public class PubSecHandlerUsingAesGcmTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/kernel/crypto/securityhandler/PubSecHandlerUsingAesGcmTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/kernel/crypto/securityhandler/PubSecHandlerUsingAesGcmTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @BeforeAll
    public static void setUp() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        Security.addProvider(FACTORY.getProvider());
    }

    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
                    ignore = true)})
    @Test
    public void testSimpleEncryptDecryptTest() throws Exception {
        String fileName = "simpleEncryptDecrypt.pdf";
        String srcFile = SOURCE_FOLDER + fileName;
        String outFile = DESTINATION_FOLDER + fileName;

        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assertions.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> doEncrypt(srcFile, outFile, true));
        } else {
            doEncrypt(srcFile, outFile, true);
            decryptWithCertificate(fileName, DESTINATION_FOLDER, "test.cer", "test.pem");
        }
    }

    @LogMessages(messages = {@LogMessage(messageTemplate = VersionConforming.NOT_SUPPORTED_AES_GCM, ignore = true),
            @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
                    ignore = true)})
    @Test
    public void testSimpleEncryptDecryptPdf15Test() throws Exception {
        String fileName = "simpleEncryptDecrypt.pdf";
        String srcFile = SOURCE_FOLDER + fileName;
        String outFile = DESTINATION_FOLDER + fileName;

        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assertions.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> doEncrypt(srcFile, outFile, false));
        } else {
            doEncrypt(srcFile, outFile, false);
            decryptWithCertificate(fileName, DESTINATION_FOLDER, "test.cer", "test.pem");
        }
    }

    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    @Test
    public void decryptExternalFileTest() throws Exception {
        decryptWithCertificate("externalFile.pdf", SOURCE_FOLDER, "decrypter.cert.pem", "signerkey.pem");
    }

    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true)})
    @Test
    public void invalidCryptFilterTest() {
        String fileName = "invalidCryptFilter.pdf";
        Exception e = Assertions.assertThrows(PdfException.class,
                () -> decryptWithCertificate(fileName, SOURCE_FOLDER, "test.cer", "test.pem"));
        Assertions.assertEquals(KernelExceptionMessageConstant.NO_COMPATIBLE_ENCRYPTION_FOUND, e.getMessage());
    }

    @Test
    public void encryptPdfWithMissingCFTest() throws Exception {
        PrivateKey certificateKey = PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "signerkey.pem"), PASSWORD);
        Certificate certificate = CryptoUtil.readPublicCertificate(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "decrypter.cert.pem"));
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.V, new PdfNumber(6));
        encMap.put(PdfName.EncryptMetadata, PdfBoolean.TRUE);
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, certificateKey,
                certificate, FACTORY.getProviderName(), null));
        Assertions.assertEquals(KernelExceptionMessageConstant.CF_NOT_FOUND_ENCRYPTION, e.getMessage());
    }

    @Test
    public void encryptPdfWithMissingDefaultCryptFilterTest() throws Exception {
        PrivateKey certificateKey = PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "signerkey.pem"), PASSWORD);
        Certificate certificate = CryptoUtil.readPublicCertificate(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "decrypter.cert.pem"));
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.V, new PdfNumber(6));
        PdfDictionary embeddedFilesDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.FlateDecode, new PdfDictionary());
        encMap.put(PdfName.CF, embeddedFilesDict);
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, certificateKey,
                certificate, FACTORY.getProviderName(), null));
        Assertions.assertEquals(KernelExceptionMessageConstant.DEFAULT_CRYPT_FILTER_NOT_FOUND_ENCRYPTION,
                e.getMessage());
    }

    @Test
    public void encryptPdfWithMissingCFMTest() throws Exception {
        PrivateKey certificateKey = PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "signerkey.pem"), PASSWORD);
        Certificate certificate = CryptoUtil.readPublicCertificate(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "decrypter.cert.pem"));
        HashMap<PdfName, PdfObject> encMap = new HashMap<PdfName, PdfObject>();
        encMap.put(PdfName.V, new PdfNumber(6));
        PdfDictionary embeddedFilesDict = new PdfDictionary();
        embeddedFilesDict.put(PdfName.DefaultCryptFilter, new PdfDictionary());
        encMap.put(PdfName.CF, embeddedFilesDict);
        PdfDictionary dictionary = new PdfDictionary(encMap);
        Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfEncryption(dictionary, certificateKey,
                certificate, FACTORY.getProviderName(), null));
        Assertions.assertEquals(KernelExceptionMessageConstant.NO_COMPATIBLE_ENCRYPTION_FOUND, e.getMessage());
    }

    private void doEncrypt(String input, String output, boolean isPdf20) throws IOException, CertificateException {
        Certificate certificate = CryptoUtil.readPublicCertificate(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + "test.cer"));
        WriterProperties writerProperties = new WriterProperties().setPublicKeyEncryption(
                new Certificate[] {certificate}, new int[] {EncryptionConstants.ALLOW_PRINTING},
                EncryptionConstants.ENCRYPTION_AES_GCM);
        if (isPdf20) {
            writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        }
        // Instantiate input/output document.
        try (PdfDocument docIn = new PdfDocument(new PdfReader(input)); PdfDocument docOut = new PdfDocument(
                new PdfWriter(output, writerProperties))) {
            // Copy one page from input to output.
            docIn.copyPagesTo(1, 1, docOut);
        }
    }

    private void decryptWithCertificate(String fileName, String srcFileFolder,
                                        String certificateName, String privateKeyName) throws Exception {
        String srcFile = srcFileFolder + fileName;
        String cmpFile = SOURCE_FOLDER + "cmp_" + fileName;
        String outFile = DESTINATION_FOLDER + "decrypted_" + fileName;

        Certificate certificate = CryptoUtil.readPublicCertificate(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + certificateName));
        PrivateKey privateKey = PemFileHelper.readPrivateKeyFromPemFile(
                FileUtil.getInputStreamForFile(SOURCE_FOLDER + privateKeyName), PASSWORD);
        ReaderProperties readerProperties = new ReaderProperties().setPublicKeySecurityParams(
                certificate, privateKey, FACTORY.getProviderName(), null);

        PdfDocument ignored = new PdfDocument(new PdfReader(srcFile, readerProperties), new PdfWriter(outFile));
        ignored.close();

        String errorMessage = new CompareTool().compareByContent(outFile, cmpFile, DESTINATION_FOLDER, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }
}
