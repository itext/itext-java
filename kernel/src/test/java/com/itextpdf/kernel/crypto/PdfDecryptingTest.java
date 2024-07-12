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
package com.itextpdf.kernel.crypto;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class PdfDecryptingTest extends ExtendedITextTest {

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/kernel/crypto/PdfDecryptingTest/certs/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/crypto/PdfDecryptingTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/crypto/PdfDecryptingTest/";
    private static final String PROVIDER_NAME = BouncyCastleFactoryCreator.getFactory().getProviderName();

    @BeforeClass
    public static void setUpBeforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
    }

    //
    // .NET with regular BC
    //
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetRegularWithPasswordStandard40() throws IOException {
        decryptWithPassword("dotnet_regular/withPassword/standard40.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetRegularWithPasswordStandard128() throws IOException {
        decryptWithPassword("dotnet_regular/withPassword/standard128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetRegularWithPasswordAes128() throws IOException {
        decryptWithPassword("dotnet_regular/withPassword/aes128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetRegularWithPasswordAes256() throws IOException {
        decryptWithPassword("dotnet_regular/withPassword/aes256.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetRegularWithPasswordAes256Pdf2() throws IOException {
        decryptWithPassword("dotnet_regular/withPassword/aes256Pdf2.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetRegularWithCertificateAes256Rsa() throws IOException, GeneralSecurityException {
        PrivateKey privateKey = readPrivateKey("SHA256withRSA.key", "RSA");
        decryptWithCertificate("dotnet_regular/withCertificate/aes256Rsa.pdf", "SHA256withRSA.crt", privateKey);
    }

    //
    // .NET with FIPS BC
    //
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetWithFipsWithPasswordStandard40() throws IOException {
        decryptWithPassword("dotnet_with_fips/withPassword/standard40.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetWithFipsWithPasswordStandard128() throws IOException {
        decryptWithPassword("dotnet_with_fips/withPassword/standard128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetWithFipsWithPasswordAes128() throws IOException {
        decryptWithPassword("dotnet_with_fips/withPassword/aes128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetWithFipsWithPasswordAes256() throws IOException {
        decryptWithPassword("dotnet_with_fips/withPassword/aes256.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptDotNetWithFipsWithPasswordAes256Pdf2() throws IOException {
        decryptWithPassword("dotnet_with_fips/withPassword/aes256Pdf2.pdf");
    }

    //
    // Java with regular BC
    //
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaRegularWithPasswordStandard40() throws IOException {
        decryptWithPassword("java_regular/withPassword/standard40.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaRegularWithPasswordStandard128() throws IOException {
        decryptWithPassword("java_regular/withPassword/standard128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaRegularWithPasswordAes128() throws IOException {
        decryptWithPassword("java_regular/withPassword/aes128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaRegularWithPasswordAes256() throws IOException {
        decryptWithPassword("java_regular/withPassword/aes256.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaRegularWithPasswordAes256Pdf2() throws IOException {
        decryptWithPassword("java_regular/withPassword/aes256Pdf2.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaRegularWithCertificateAes256Rsa() throws IOException, GeneralSecurityException {
        PrivateKey privateKey = readPrivateKey("SHA256withRSA.key", "RSA");
        decryptWithCertificate("java_regular/withCertificate/aes256Rsa.pdf", "SHA256withRSA.crt", privateKey);
    }

    //
    // Java with FIPS BC
    //
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaWithFipsWithPasswordStandard40() throws IOException {
        decryptWithPassword("java_with_fips/withPassword/standard40.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaWithFipsWithPasswordStandard128() throws IOException {
        decryptWithPassword("java_with_fips/withPassword/standard128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaWithFipsWithPasswordAes128() throws IOException {
        decryptWithPassword("java_with_fips/withPassword/aes128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaWithFIPSWithPasswordAes256() throws IOException {
        decryptWithPassword("java_with_fips/withPassword/aes256.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaWithFipsWithPasswordAes256Pdf2() throws IOException {
        decryptWithPassword("java_with_fips/withPassword/aes256Pdf2.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptJavaWithFipsWithCertificateAes256Rsa() throws IOException, GeneralSecurityException {
        PrivateKey privateKey = readPrivateKey("SHA256withRSA.key", "RSA");
        decryptWithCertificate("java_with_fips/withCertificate/aes256Rsa.pdf", "SHA256withRSA.crt", privateKey);
    }

    //
    // Adobe Acrobat
    //
    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptAdobeWithPasswordStandard40() throws IOException {
        decryptWithPassword("adobe/withPassword/standard40.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptAdobeWithPasswordStandard128() throws IOException {
        decryptWithPassword("adobe/withPassword/standard128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptAdobeWithPasswordAes128() throws IOException {
        decryptWithPassword("adobe/withPassword/aes128.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptAdobeWithPasswordAes256() throws IOException {
        decryptWithPassword("adobe/withPassword/aes256.pdf");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptAdobeWithCertificateAes256Rsa() throws IOException, GeneralSecurityException {
        PrivateKey privateKey = readPrivateKey("SHA256withRSA.key", "RSA");
        decryptWithCertificate("adobe/withCertificate/aes256Rsa.pdf", "SHA256withRSA.crt", privateKey);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void decryptAdobeWithCertificateAes256EcdsaP256() throws IOException, GeneralSecurityException {
        PrivateKey privateKey = readPrivateKey("SHA256withECDSA_P256.key", "EC");
        decryptWithCertificate("adobe/withCertificate/aes256EcdsaP256.pdf", "SHA256withECDSA_P256.crt", privateKey);
    }

    private void decryptWithPassword(String fileName) throws IOException {
        byte[] user = "user".getBytes(StandardCharsets.UTF_8);
        byte[] owner = "owner".getBytes(StandardCharsets.UTF_8);

        decryptWithPassword(fileName, user);
        decryptWithPassword(fileName, owner);
    }

    private void decryptWithPassword(String fileName, byte[] password) throws IOException {
        ReaderProperties readerProperties = new ReaderProperties().setPassword(password);

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + fileName, readerProperties))) {
            Assert.assertTrue(
                    PdfTextExtractor.getTextFromPage(pdfDocument.getFirstPage()).startsWith("Content encrypted by "));
        }
    }

    private void decryptWithCertificate(String fileName, String certificateName, PrivateKey certificateKey)
            throws IOException, CertificateException {
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(CERTS_SRC + certificateName));
        ReaderProperties readerProperties = new ReaderProperties().setPublicKeySecurityParams(
                certificate, certificateKey, PROVIDER_NAME, null);

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + fileName, readerProperties))) {
            Assert.assertTrue(
                    PdfTextExtractor.getTextFromPage(pdfDocument.getFirstPage()).startsWith("Content encrypted by "));
        }
    }

    private PrivateKey readPrivateKey(String privateKeyName, String algorithm)
            throws GeneralSecurityException, IOException {
        try (InputStream pemFile = FileUtil.getInputStreamForFile(CERTS_SRC + privateKeyName)) {
            String start = "-----BEGIN PRIVATE KEY-----";
            String end = "-----END PRIVATE KEY-----";

            String pemContent = new String(StreamUtil.inputStreamToArray(pemFile));
            int startPos = pemContent.indexOf(start);
            int endPos = pemContent.indexOf(end);
            pemContent = pemContent.substring(startPos + start.length(), endPos);
            byte[] encoded = Base64.decode(pemContent);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return KeyFactory.getInstance(algorithm, BouncyCastleFactoryCreator.getFactory().getProviderName())
                    .generatePrivate(keySpec);
        }
    }
}
