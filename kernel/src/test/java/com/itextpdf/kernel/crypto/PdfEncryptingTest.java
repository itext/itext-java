/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.crypto.fips.AbstractFipsUnapprovedOperationError;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("BouncyCastleIntegrationTest")
public class PdfEncryptingTest extends ExtendedITextTest {
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/kernel/crypto/PdfEncryptingTest/certs/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/kernel/crypto/PdfEncryptingTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/crypto/PdfEncryptingTest/";

    private static final byte[] USER_PASSWORD = "user".getBytes(StandardCharsets.UTF_8);
    private static final byte[] OWNER_PASSWORD = "owner".getBytes(StandardCharsets.UTF_8);

    private static final String PROVIDER_NAME = BouncyCastleFactoryCreator.getFactory().getProviderName();

    @BeforeAll
    public static void setUpBeforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void encryptWithPasswordStandard40() throws IOException, InterruptedException {
        encryptWithPassword("encryptWithPasswordStandard40.pdf",
                EncryptionConstants.STANDARD_ENCRYPTION_40, false);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void encryptWithPasswordStandard128() throws IOException, InterruptedException {
        encryptWithPassword("encryptWithPasswordStandard128.pdf",
                EncryptionConstants.STANDARD_ENCRYPTION_128, false);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT), ignore = true)
    public void encryptWithPasswordAes128() throws IOException, InterruptedException {
        encryptWithPassword("encryptWithPasswordAes128.pdf", EncryptionConstants.ENCRYPTION_AES_128,
                false);
    }

    @Test
    public void encryptWithPasswordAes256() throws IOException, InterruptedException {
        encryptWithPassword("encryptWithPasswordAes256.pdf", EncryptionConstants.ENCRYPTION_AES_256,
                false);
    }

    @Test
    public void encryptWithPasswordAes256Pdf2() throws IOException, InterruptedException {
        encryptWithPassword("encryptWithPasswordAes256Pdf2.pdf", EncryptionConstants.ENCRYPTION_AES_256,
                true);
    }

    @Test
    public void encryptWithCertificateAes256Rsa() throws GeneralSecurityException, IOException, InterruptedException {
        if (BouncyCastleFactoryCreator.getFactory().isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assertions.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate("encryptWithCertificateAes256Rsa.pdf", "SHA256withRSA.crt"));
        } else {
            encryptWithCertificate("encryptWithCertificateAes256Rsa.pdf", "SHA256withRSA.crt");
        }
    }

    @Test
    public void encryptWithCertificateAes256EcdsaP256() {
        String exceptionTest = Assertions.assertThrows(PdfException.class,
                () -> encryptWithCertificate("encryptWithCertificateAes256EcdsaP256.pdf", "SHA256withECDSA_P256.crt"))
                .getMessage();
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.ALGORITHM_IS_NOT_SUPPORTED, "1.2.840.10045.2.1"), exceptionTest);
    }

    @Test
    public void encryptWithCertificateAes256EcdsaBrainpoolP256R1() {
        String exceptionTest = Assertions.assertThrows(PdfException.class,
                () -> encryptWithCertificate(
                        "encryptWithCertificateAes256EcdsaBrainpoolP256R1.pdf", "SHA256withECDSA_brainpoolP256r1.crt"))
                .getMessage();
        Assertions.assertEquals(MessageFormatUtil.format(
                KernelExceptionMessageConstant.ALGORITHM_IS_NOT_SUPPORTED, "1.2.840.10045.2.1"), exceptionTest);
    }

    private void encryptWithPassword(String fileName, int encryptionType, boolean pdf2)
            throws IOException, InterruptedException {
        WriterProperties writerProperties = new WriterProperties()
                .setStandardEncryption(USER_PASSWORD, OWNER_PASSWORD, -1, encryptionType);
        if (pdf2) {
            writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        }

        try (PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + fileName, writerProperties.addXmpMetadata());
                PdfDocument document = new PdfDocument(writer)) {
            writeTextToDocument(document);
        }
        Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + fileName,
                SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER, "diff", USER_PASSWORD, USER_PASSWORD));
    }

    private void encryptWithCertificate(String fileName, String certificatePath)
            throws IOException, GeneralSecurityException, InterruptedException {
        Certificate certificate = CryptoUtil.readPublicCertificate(FileUtil.getInputStreamForFile(CERTS_SRC + certificatePath));
        WriterProperties writerProperties = new WriterProperties().setPublicKeyEncryption(
                new Certificate[] {certificate}, new int[] {-1}, EncryptionConstants.ENCRYPTION_AES_256);
        try (PdfWriter writer = CompareTool.createTestPdfWriter(DESTINATION_FOLDER + fileName, writerProperties.addXmpMetadata());
                PdfDocument document = new PdfDocument(writer)) {
            writeTextToDocument(document);
        }
        CompareTool compareTool = new CompareTool();
        PrivateKey privateKey = readPrivateKey("SHA256withRSA.key", "RSA");
        compareTool.getCmpReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);
        compareTool.getOutReaderProperties().setPublicKeySecurityParams(certificate, privateKey, PROVIDER_NAME, null);
        Assertions.assertNull(compareTool
                .compareByContent(DESTINATION_FOLDER + fileName, SOURCE_FOLDER + "cmp_" + fileName, DESTINATION_FOLDER,
                        "diff"));
    }
    
    private void writeTextToDocument(PdfDocument document) throws IOException {
        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        canvas.saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Content encrypted by iText")
                .endText()
                .restoreState();
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
