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
package com.itextpdf.kernel.crypto.pdfencryption;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.crypto.CryptoUtil;
import com.itextpdf.kernel.crypto.securityhandler.StandardHandlerUsingAes256;
import com.itextpdf.kernel.exceptions.BadPasswordException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.VersionConforming;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.PemFileHelper;
import com.itextpdf.kernel.xmp.XMPConst;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.properties.XMPProperty;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;



/**
 * Due to import control restrictions by the governments of a few countries,
 * the encryption libraries shipped by default with the Java SDK restrict the
 * length, and as a result the strength, of encryption keys. Be aware that in
 * this test by using {@link ITextTest#removeCryptographyRestrictions()} we
 * remove cryptography restrictions via reflection for testing purposes.
 * <br/>
 * For more conventional way of solving this problem you need to replace the
 * default security JARs in your Java installation with the Java Cryptography
 * Extension (JCE) Unlimited Strength Jurisdiction Policy Files. These JARs
 * are available for download from http://java.oracle.com/ in eligible countries.
 */
@Tag("BouncyCastleIntegrationTest")
public class PdfEncryptionTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/crypto/pdfencryption/PdfEncryptionTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto/pdfencryption/PdfEncryptionTest/";

    public static final char[] PRIVATE_KEY_PASS = "testpassphrase".toCharArray();
    public static final String CERT = sourceFolder + "test.cer";
    public static final String PRIVATE_KEY = sourceFolder + "test.pem";

    private PrivateKey privateKey;

    PdfEncryptionTestUtils encryptionUtil = new PdfEncryptionTestUtils(destinationFolder, sourceFolder);


    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
        Security.addProvider(FACTORY.getProvider());
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordStandard128() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordStandard128.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_128;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordStandard40() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordStandard40.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_40;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordStandard128NoCompression() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordStandard128NoCompression.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_128;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordStandard40NoCompression() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordStandard40NoCompression.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_40;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordAes128() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordAes128.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordAes256() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordAes256.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordAes128NoCompression() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordAes128NoCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordAes256NoCompression() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordAes256NoCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openEncryptedDocWithoutPassword() throws IOException {
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithPasswordStandard40.pdf")) {
            Exception e = Assertions.assertThrows(BadPasswordException.class, () -> new PdfDocument(reader));
            Assertions.assertEquals(KernelExceptionMessageConstant.BAD_USER_PASSWORD, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openEncryptedDocWithWrongPassword() throws IOException {
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithPasswordStandard40.pdf",
                new ReaderProperties().setPassword("wrong_password".getBytes(StandardCharsets.ISO_8859_1)))) {

            Exception e = Assertions.assertThrows(BadPasswordException.class, () -> new PdfDocument(reader));
            Assertions.assertEquals(KernelExceptionMessageConstant.BAD_USER_PASSWORD, e.getMessage());
        }
    }

    @Test
    public void openEncryptedDocWithoutCertificate() throws IOException {
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf")) {

            Exception e = Assertions.assertThrows(PdfException.class, () -> new PdfDocument(reader));
            Assertions.assertEquals(
                    KernelExceptionMessageConstant.CERTIFICATE_IS_NOT_PROVIDED_DOCUMENT_IS_ENCRYPTED_WITH_PUBLIC_KEY_CERTIFICATE,
                    e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openEncryptedDocWithoutPrivateKey() throws IOException, CertificateException {
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties()
                        .setPublicKeySecurityParams(
                                getPublicCertificate(sourceFolder + "wrong.cer"),
                                null,
                                FACTORY.getProviderName(),
                                null))) {

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> new PdfDocument(reader)
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.BAD_CERTIFICATE_AND_KEY, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openEncryptedDocWithWrongCertificate()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties()
                        .setPublicKeySecurityParams(
                                getPublicCertificate(sourceFolder + "wrong.cer"),
                                getPrivateKey(),
                                FACTORY.getProviderName(),
                                null))) {

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> new PdfDocument(reader)
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.BAD_CERTIFICATE_AND_KEY, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openEncryptedDocWithWrongCertificateAndPrivateKey()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties()
                        .setPublicKeySecurityParams(
                                getPublicCertificate(sourceFolder + "wrong.cer"),
                                PemFileHelper.readPrivateKeyFromPemFile(
                                        FileUtil.getInputStreamForFile(sourceFolder + "wrong.pem"), PRIVATE_KEY_PASS),
                                FACTORY.getProviderName(),
                                null))) {

            Exception e = Assertions.assertThrows(PdfException.class,
                    () -> new PdfDocument(reader)
            );
            Assertions.assertEquals(KernelExceptionMessageConstant.BAD_CERTIFICATE_AND_KEY, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void metadataReadingInEncryptedDoc() throws IOException, XMPException {
        PdfReader reader = new PdfReader(sourceFolder + "encryptedWithPlainMetadata.pdf",
                new ReaderProperties().setPassword(PdfEncryptionTestUtils.OWNER));
        PdfDocument doc = new PdfDocument(reader);
        XMPMeta xmpMeta = doc.getXmpMetadata();
        XMPProperty creatorToolXmp = xmpMeta.getProperty(XMPConst.NS_XMP, "CreatorTool");
        doc.close();
        Assertions.assertNotNull(creatorToolXmp);
        Assertions.assertEquals("iText", creatorToolXmp.getValue());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void copyEncryptedDocument() throws GeneralSecurityException, IOException, InterruptedException,
            AbstractPKCSException, AbstractOperatorCreationException {
        // I don't know how this source doc was created. Currently it's not opening by Acrobat and Foxit.
        // If I recreate it using iText, decrypting it in bc-fips on dotnet will start failing. But we probably still
        // want this test.
        PdfDocument srcDoc = new PdfDocument(new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties().
                        setPublicKeySecurityParams(getPublicCertificate(CERT), getPrivateKey(),
                                FACTORY.getProviderName(), null)));
        String fileName = "copiedEncryptedDoc.pdf";
        PdfDocument destDoc = new PdfDocument(CompareTool.createTestPdfWriter(destinationFolder + fileName));
        srcDoc.copyPagesTo(1, 1, destDoc);

        PdfDictionary srcInfo = srcDoc.getTrailer().getAsDictionary(PdfName.Info);
        PdfDictionary destInfo = destDoc.getTrailer().getAsDictionary(PdfName.Info);
        if (destInfo == null) {
            destInfo = new PdfDictionary();
            destDoc.getTrailer().put(PdfName.Info, destInfo);
        }
        for (PdfName srcInfoKey : srcInfo.keySet()) {
            destInfo.put((PdfName) srcInfoKey.copyTo(destDoc), srcInfo.get(srcInfoKey).copyTo(destDoc));
        }

        srcDoc.close();
        destDoc.close();

        Assertions.assertNull(new CompareTool()
                .compareByContent(destinationFolder + fileName, sourceFolder + "cmp_" + fileName,
                        destinationFolder, "diff_"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openDocNoUserPassword() throws IOException {
        String fileName = "noUserPassword.pdf";
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + fileName));
        document.close();

        encryptionUtil.checkDecryptedWithPasswordContent(sourceFolder + fileName, null,
                PdfEncryptionTestUtils.PAGE_TEXT_CONTENT);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void stampDocNoUserPassword() throws IOException {
        String fileName = "stampedNoPassword.pdf";

        try (PdfReader reader = new PdfReader(sourceFolder + "noUserPassword.pdf");
             PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + fileName)) {

            Exception e = Assertions.assertThrows(BadPasswordException.class, () -> new PdfDocument(reader, writer));
            Assertions.assertEquals(BadPasswordException.PdfReaderNotOpenedWithOwnerPassword, e.getMessage());
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordAes128EmbeddedFilesOnly() throws IOException {
        String filename = "encryptWithPasswordAes128EmbeddedFilesOnly.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128 | EncryptionConstants.EMBEDDED_FILES_ONLY;

        String outFileName = destinationFolder + filename;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        PdfWriter writer = CompareTool.createTestPdfWriter(outFileName,
                new WriterProperties().setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER, permissions,
                        encryptionType).addXmpMetadata()
        );
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setMoreInfo(PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_KEY, PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_VALUE);
        PdfPage page = document.addNewPage();
        String textContent = "Hello world!";
        PdfEncryptionTestUtils.writeTextBytesOnPageContent(page, textContent);

        String descripton = "encryptedFile";
        String path = sourceFolder + "pageWithContent.pdf";
        document.addFileAttachment(descripton,
                PdfFileSpec.createEmbeddedFileSpec(document, path, descripton, path, null, null));

        page.flush();
        document.close();

        //TODO DEVSIX-5355 Specific crypto filters for EFF StmF and StrF are not supported at the moment.
        // However we can read embedded files only mode.
        boolean ERROR_IS_EXPECTED = false;
        encryptionUtil.checkDecryptedWithPasswordContent(destinationFolder + filename, PdfEncryptionTestUtils.OWNER,
                textContent, ERROR_IS_EXPECTED);
        encryptionUtil.checkDecryptedWithPasswordContent(destinationFolder + filename, PdfEncryptionTestUtils.USER,
                textContent, ERROR_IS_EXPECTED);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordAes256EmbeddedFilesOnly() throws IOException, InterruptedException {
        String filename = "encryptWithPasswordAes256EmbeddedFilesOnly.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.EMBEDDED_FILES_ONLY;

        String outFileName = destinationFolder + filename;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        PdfWriter writer = CompareTool.createTestPdfWriter(outFileName,
                new WriterProperties().setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER, permissions,
                        encryptionType).addXmpMetadata().setPdfVersion(PdfVersion.PDF_2_0)
        );
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setMoreInfo(PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_KEY, PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_VALUE);
        PdfPage page = document.addNewPage();
        String textContent = "Hello world!";
        PdfEncryptionTestUtils.writeTextBytesOnPageContent(page, textContent);

        String descripton = "encryptedFile";
        String path = sourceFolder + "pageWithContent.pdf";
        document.addFileAttachment(descripton,
                PdfFileSpec.createEmbeddedFileSpec(document, path, descripton, path, null, null));

        page.flush();
        document.close();

        //TODO DEVSIX-5355 Specific crypto filters for EFF StmF and StrF are not supported at the moment.
        // However we can read embedded files only mode.
        boolean ERROR_IS_EXPECTED = false;
        encryptionUtil.checkDecryptedWithPasswordContent(destinationFolder + filename, PdfEncryptionTestUtils.OWNER,
                textContent, ERROR_IS_EXPECTED);
        encryptionUtil.checkDecryptedWithPasswordContent(destinationFolder + filename, PdfEncryptionTestUtils.USER,
                textContent, ERROR_IS_EXPECTED);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptAes256Pdf2NotEncryptMetadata() throws InterruptedException, IOException {
        String filename = "encryptAes256Pdf2NotEncryptMetadata.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptAes256Pdf2NotEncryptMetadata02() throws InterruptedException, IOException {
        String filename = "encryptAes256Pdf2NotEncryptMetadata02.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256 | EncryptionConstants.DO_NOT_ENCRYPT_METADATA;
        encryptWithPassword(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION, true);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptAes256EncryptedStampingUpdate() throws InterruptedException, IOException {
        String filename = "encryptAes256EncryptedStampingUpdate.pdf";
        String src = sourceFolder + "encryptedWithPlainMetadata.pdf";
        String out = destinationFolder + filename;

        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(src, new ReaderProperties().setPassword(PdfEncryptionTestUtils.OWNER)),
                CompareTool.createTestPdfWriter(out, new WriterProperties()
                        .setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER,
                                EncryptionConstants.ALLOW_PRINTING, EncryptionConstants.STANDARD_ENCRYPTION_40)),
                new StampingProperties());

        pdfDoc.close();

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        String compareResult = compareTool.compareByContent(out, sourceFolder + "cmp_" + filename, destinationFolder,
                "diff_", PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.USER);
        if (compareResult != null) {
            Assertions.fail(compareResult);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptAes256FullCompression() throws InterruptedException, IOException {
        String filename = "encryptAes256FullCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION, true);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordAes256Pdf2() throws InterruptedException, IOException {
        String filename = "encryptWithPasswordAes256Pdf2.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION, true);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT, ignore = true),
            @LogMessage(messageTemplate = VersionConforming.DEPRECATED_ENCRYPTION_ALGORITHMS, count = 2)})
    public void encryptWithPasswordAes128Pdf2() throws InterruptedException, IOException {
        String filename = "encryptWithPasswordAes128Pdf2.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        encryptWithPassword2(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION, true);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void stampAndUpdateVersionNewAes256() throws InterruptedException, IOException {
        String filename = "stampAndUpdateVersionNewAes256.pdf";
        PdfDocument doc = new PdfDocument(
                new PdfReader(sourceFolder + "encryptedWithPasswordAes256.pdf",
                        new ReaderProperties().setPassword(PdfEncryptionTestUtils.OWNER)),
                CompareTool.createTestPdfWriter(destinationFolder + filename,
                        new WriterProperties()
                                .setPdfVersion(PdfVersion.PDF_2_0)
                                .setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER, 0,
                                        EncryptionConstants.ENCRYPTION_AES_256)));
        doc.close();
        encryptionUtil.compareEncryptedPdf(filename);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptAes256Pdf2Permissions() throws InterruptedException, IOException {
        String filename = "encryptAes256Pdf2Permissions.pdf";
        int permissions = EncryptionConstants.ALLOW_FILL_IN | EncryptionConstants.ALLOW_SCREENREADERS
                | EncryptionConstants.ALLOW_DEGRADED_PRINTING;
        PdfDocument doc = new PdfDocument(
                CompareTool.createTestPdfWriter(destinationFolder + filename,
                        new WriterProperties()
                                .setPdfVersion(PdfVersion.PDF_2_0)
                                .setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER, permissions,
                                        EncryptionConstants.ENCRYPTION_AES_256)));
        doc.getDocumentInfo().setMoreInfo(PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_KEY, PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_VALUE);
        PdfEncryptionTestUtils.writeTextBytesOnPageContent(doc.addNewPage(), PdfEncryptionTestUtils.PAGE_TEXT_CONTENT);
        doc.close();
        encryptionUtil.compareEncryptedPdf(filename);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithPasswordAes128NoMetadataCompression() throws Exception {
        String srcFilename = "srcEncryptWithPasswordAes128NoMetadataCompression.pdf";
        PdfReader reader = new PdfReader(sourceFolder + srcFilename, new ReaderProperties());
        WriterProperties props = new WriterProperties()
                .setStandardEncryption("superuser".getBytes(), "superowner".getBytes(),
                        EncryptionConstants.ALLOW_PRINTING,
                        EncryptionConstants.ENCRYPTION_AES_128 |
                                EncryptionConstants.DO_NOT_ENCRYPT_METADATA);
        String outFilename = "encryptWithPasswordAes128NoMetadataCompression.pdf";
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + outFilename, props);
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        compareTool.enableEncryptionCompare();
        compareTool.getOutReaderProperties().setPassword("superowner".getBytes());
        compareTool.getCmpReaderProperties().setPassword("superowner".getBytes());
        String outPdf = destinationFolder + outFilename;
        String cmpPdf = sourceFolder + "cmp_" + outFilename;
        Assertions.assertNull(compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void checkMD5LogAbsenceInUnapprovedMode() throws IOException {
        Assumptions.assumeTrue(!FACTORY.isInApprovedOnlyMode());
        String fileName = "noUserPassword.pdf";
        try (PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + fileName))) {
            // this test checks log message absence
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true, count = 2))
    public void decryptAdobeWithPasswordAes256() throws IOException {
        String filename = Paths.get(sourceFolder + "AdobeAes256.pdf").toString();
        decryptWithPassword(filename, "user".getBytes());
        decryptWithPassword(filename, "owner".getBytes());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void decodeDictionaryWithInvalidOwnerHashAes256() {
        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(PdfName.R, new PdfNumber(0));
        //Setting password hash which exceeds 48 bytes and contains non 0 elements after first 48 bytes
        dictionary.put(PdfName.O, new PdfString("Ä\u0010\u001D`¶\u0084nË»j{\fßò\u0089JàN*\u0090ø>No\u0099" +
                "\u0087J \u0013\"V\u008E\fT!\u0082\u0003\u009E£\u008Fc\u0004 ].\u008C\u009C\u009C\u0000" +
                "\u0000\u0000\u0000\u0013\u0000\u0013\u0013\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
                "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0013"));
        Exception e = Assertions.assertThrows(PdfException.class,
                () -> new StandardHandlerUsingAes256(dictionary, "owner".getBytes()));
        Assertions.assertEquals(KernelExceptionMessageConstant.BAD_PASSWORD_HASH, e.getCause().getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openEncryptedWithPasswordDocWithDefaultKeyLength() throws IOException {
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithPasswordWithDefaultKeyLength.pdf",
                new ReaderProperties().setPassword("user".getBytes(StandardCharsets.UTF_8)));
             PdfDocument document = new PdfDocument(reader)) {
            Assertions.assertFalse(document.getTrailer().getAsDictionary(PdfName.Encrypt).containsKey(PdfName.Length));
        }
    }

    public void encryptWithPassword2(String filename, int encryptionType, int compression)
            throws IOException, InterruptedException {
        encryptWithPassword2(filename, encryptionType, compression, false);
    }

    public void encryptWithPassword2(String filename, int encryptionType, int compression, boolean isPdf2)
            throws IOException, InterruptedException {
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        WriterProperties writerProperties = new WriterProperties().setStandardEncryption(PdfEncryptionTestUtils.USER,
                PdfEncryptionTestUtils.OWNER, permissions, encryptionType);
        if (isPdf2) {
            writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        }
        PdfWriter writer = CompareTool.createTestPdfWriter(destinationFolder + filename,
                writerProperties.addXmpMetadata());
        writer.setCompressionLevel(compression);
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setMoreInfo(PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_KEY,
                PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_VALUE);
        PdfPage page = document.addNewPage();
        PdfEncryptionTestUtils.writeTextBytesOnPageContent(page, PdfEncryptionTestUtils.PAGE_TEXT_CONTENT);

        page.flush();
        document.close();

        encryptionUtil.compareEncryptedPdf(filename);

        checkEncryptedWithPasswordDocumentStamping(filename, PdfEncryptionTestUtils.OWNER);
        checkEncryptedWithPasswordDocumentAppending(filename, PdfEncryptionTestUtils.OWNER);
    }

    public void encryptWithPassword(String filename, int encryptionType, int compression, boolean fullCompression)
            throws IOException, InterruptedException {
        String outFileName = destinationFolder + filename;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        PdfWriter writer = CompareTool.createTestPdfWriter(outFileName,
                new WriterProperties()
                        .setStandardEncryption(PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.OWNER, permissions, encryptionType)
                        .addXmpMetadata()
                        .setFullCompressionMode(fullCompression));
        writer.setCompressionLevel(compression);
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setMoreInfo(PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_KEY,
                PdfEncryptionTestUtils.CUSTOM_INFO_ENTRY_VALUE);
        PdfPage page = document.addNewPage();
        PdfEncryptionTestUtils.writeTextBytesOnPageContent(page, PdfEncryptionTestUtils.PAGE_TEXT_CONTENT);

        page.flush();
        document.close();

        encryptionUtil.compareEncryptedPdf(filename);

        checkEncryptedWithPasswordDocumentStamping(filename, PdfEncryptionTestUtils.OWNER);
        checkEncryptedWithPasswordDocumentAppending(filename, PdfEncryptionTestUtils.OWNER);
    }

    public Certificate getPublicCertificate(String path) throws IOException, CertificateException {
        InputStream is = FileUtil.getInputStreamForFile(path);
        return CryptoUtil.readPublicCertificate(is);
    }

    public PrivateKey getPrivateKey() throws IOException, AbstractPKCSException, AbstractOperatorCreationException {
        if (privateKey == null) {
            privateKey = PemFileHelper.readPrivateKeyFromPemFile(
                    FileUtil.getInputStreamForFile(PRIVATE_KEY), PRIVATE_KEY_PASS);
        }
        return privateKey;
    }

    // basically this is comparing content of decrypted by itext document with content of encrypted document
    public void checkEncryptedWithPasswordDocumentStamping(String filename, byte[] password)
            throws IOException, InterruptedException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "stamped_" + filename;
        PdfReader reader = CompareTool.createOutputReader(srcFileName, new ReaderProperties().setPassword(password));
        PdfDocument document = new PdfDocument(reader, CompareTool.createTestPdfWriter(outFileName));
        document.close();

        CompareTool compareTool = new CompareTool();

        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_" + filename,
                destinationFolder, "diff_", PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.USER);

        if (compareResult != null) {
            Assertions.fail(compareResult);
        }
    }

    public void checkEncryptedWithPasswordDocumentAppending(String filename, byte[] password)
            throws IOException, InterruptedException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "appended_" + filename;
        PdfReader reader = CompareTool.createOutputReader(srcFileName, new ReaderProperties().setPassword(password));
        PdfDocument document = new PdfDocument(reader, CompareTool.createTestPdfWriter(outFileName),
                new StampingProperties().useAppendMode());
        PdfPage newPage = document.addNewPage();
        newPage.put(PdfName.Default, new PdfString("Hello world string"));
        PdfEncryptionTestUtils.writeTextBytesOnPageContent(newPage, "Hello world page_2!");
        document.close();

        CompareTool compareTool = new CompareTool().enableEncryptionCompare(false);

        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_appended_" + filename,
                destinationFolder, "diff_", PdfEncryptionTestUtils.USER, PdfEncryptionTestUtils.USER);

        if (compareResult != null) {
            Assertions.fail(compareResult);
        }
    }

    private void decryptWithPassword(String fileName, byte[] password) throws IOException {
        ReaderProperties readerProperties = new ReaderProperties().setPassword(password);
        try (PdfReader reader = new PdfReader(fileName, readerProperties);
             PdfDocument pdfDocument = new PdfDocument(reader)) {
            Assertions.assertTrue(PdfTextExtractor.getTextFromPage(pdfDocument.getFirstPage())
                    .startsWith("Content encrypted by "));
        }
    }
}
