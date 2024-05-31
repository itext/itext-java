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
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.crypto.fips.AbstractFipsUnapprovedOperationError;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.utils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;


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
@Category(BouncyCastleIntegrationTest.class)
public class PdfEncryptionManuallyPortedTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/crypto/PdfEncryptionManuallyPortedTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto/PdfEncryptionManuallyPortedTest/";

    public static final char[] PRIVATE_KEY_PASS = "testpassphrase".toCharArray();
    // There is also test.pfx to add to Acrobat to be able to open result pdf files. Password for it is also
    // testpassphrase
    public static final String CERT = sourceFolder + "test.cer";
    public static final String PRIVATE_KEY = sourceFolder + "test.pem";

    static final String pageTextContent = "Hello world!";

    // Custom entry in Info dictionary is used because standard entried are gone into metadata in PDF 2.0
    static final String customInfoEntryKey = "Custom";
    static final String customInfoEntryValue = "String";

    private PrivateKey privateKey;

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
        Security.addProvider(FACTORY.getProvider());
    }

    @AfterClass
    public static void afterClass() {
        CompareTool.cleanup(destinationFolder);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithCertificateStandard128() throws IOException, InterruptedException, GeneralSecurityException,
            AbstractPKCSException, AbstractOperatorCreationException {
        String filename = "encryptWithCertificateStandard128.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_128;
        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assert.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION));
        } else {
            encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithCertificateStandard40() throws IOException, InterruptedException, GeneralSecurityException,
            AbstractPKCSException, AbstractOperatorCreationException {
        String filename = "encryptWithCertificateStandard40.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_40;
        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assert.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION));
        } else {
            encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithCertificateStandard128NoCompression() throws IOException, InterruptedException,
            GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String filename = "encryptWithCertificateStandard128NoCompression.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_128;
        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assert.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION));
        } else {
            encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithCertificateStandard40NoCompression() throws IOException, InterruptedException,
            GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String filename = "encryptWithCertificateStandard40NoCompression.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_40;
        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assert.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION));
        } else {
            encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithCertificateAes128() throws IOException, InterruptedException, GeneralSecurityException,
            AbstractPKCSException, AbstractOperatorCreationException {
        String filename = "encryptWithCertificateAes128.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assert.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION));
        } else {
            encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithCertificateAes256() throws IOException, InterruptedException, GeneralSecurityException,
            AbstractPKCSException, AbstractOperatorCreationException {
        String filename = "encryptWithCertificateAes256.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assert.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION));
        } else {
            encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithCertificateAes128NoCompression() throws IOException, InterruptedException,
            GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String filename = "encryptWithCertificateAes128NoCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assert.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION));
        } else {
            encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptWithCertificateAes256NoCompression() throws IOException, InterruptedException,
            GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        String filename = "encryptWithCertificateAes256NoCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        if (FACTORY.isInApprovedOnlyMode()) {
            // RSA PKCS1.5 encryption disallowed
            Assert.assertThrows(AbstractFipsUnapprovedOperationError.class,
                    () -> encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION));
        } else {
            encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openEncryptedDocWithWrongPrivateKey()
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties()
                        .setPublicKeySecurityParams(
                                getPublicCertificate(CERT),
                                PemFileHelper.readPrivateKeyFromPemFile(
                                        FileUtil.getInputStreamForFile(sourceFolder + "wrong.pem"), PRIVATE_KEY_PASS),
                                FACTORY.getProviderName(),
                                null))) {

            Exception e = Assert.assertThrows(PdfException.class,
                    () -> new PdfDocument(reader)
            );
            Assert.assertEquals(KernelExceptionMessageConstant.PDF_DECRYPTION, e.getMessage());
        }
    }


    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void openEncryptedWithCertificateDocWithDefaultKeyLength() throws IOException, CertificateException,
            AbstractOperatorCreationException, AbstractPKCSException {
        Certificate cert = getPublicCertificate(CERT);
        try (PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateWithDefaultKeyLength.pdf",
                new ReaderProperties().setPublicKeySecurityParams(cert, getPrivateKey(),
                        FACTORY.getProviderName(), null));
             PdfDocument document = new PdfDocument(reader)) {
            Assert.assertFalse(document.getTrailer().getAsDictionary(PdfName.Encrypt).containsKey(PdfName.Length));
        }
    }

    public void encryptWithCertificate(String filename, int encryptionType, int compression) throws IOException,
            InterruptedException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        ITextTest.removeCryptographyRestrictions();

        String outFileName = destinationFolder + filename;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        Certificate cert = getPublicCertificate(CERT);
        PdfWriter writer = CompareTool.createTestPdfWriter(outFileName, new WriterProperties()
                .setPublicKeyEncryption(new Certificate[] {cert}, new int[] {permissions}, encryptionType)
                .addXmpMetadata());
        writer.setCompressionLevel(compression);
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setMoreInfo(customInfoEntryKey, customInfoEntryValue);
        PdfPage page = document.addNewPage();
        writeTextBytesOnPageContent(page, pageTextContent);

        page.flush();
        document.close();

        checkDecryptedWithCertificateContent(filename, cert, pageTextContent);

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        compareTool.getOutReaderProperties()
                .setPublicKeySecurityParams(cert, getPrivateKey(), FACTORY.getProviderName(), null);
        compareTool.getCmpReaderProperties()
                .setPublicKeySecurityParams(cert, getPrivateKey(), FACTORY.getProviderName(), null);
        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_" + filename,
                destinationFolder, "diff_");
        if (compareResult != null) {
            Assert.fail(compareResult);
        }

        checkEncryptedWithCertificateDocumentStamping(filename, cert);
        checkEncryptedWithCertificateDocumentAppending(filename, cert);

        ITextTest.restoreCryptographyRestrictions();
    }

    public Certificate getPublicCertificate(String path) throws IOException, CertificateException {
        InputStream is = FileUtil.getInputStreamForFile(path);
        return CryptoUtil.readPublicCertificate(is);
    }

    public PrivateKey getPrivateKey() throws IOException, AbstractPKCSException, AbstractOperatorCreationException {
        if (privateKey == null) {
            privateKey = PemFileHelper.readPrivateKeyFromPemFile(FileUtil.getInputStreamForFile(PRIVATE_KEY), PRIVATE_KEY_PASS);
        }
        return privateKey;
    }

    public void checkDecryptedWithCertificateContent(String filename, Certificate certificate, String pageContent)
            throws IOException, AbstractPKCSException, AbstractOperatorCreationException {
        String src = destinationFolder + filename;
        PdfReader reader = CompareTool.createOutputReader(src, new ReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), FACTORY.getProviderName(), null));
        PdfDocument document = new PdfDocument(reader);
        PdfPage page = document.getPage(1);

        String s = new String(page.getStreamBytes(0));
        Assert.assertTrue("Expected content: \n" + pageContent, s.contains(pageContent));
        Assert.assertEquals("Encrypted custom", customInfoEntryValue,
                document.getTrailer().getAsDictionary(PdfName.Info).getAsString(new PdfName(customInfoEntryKey))
                        .toUnicodeString());

        document.close();
    }

    // basically this is comparing content of decrypted by itext document with content of encrypted document
    public void checkEncryptedWithCertificateDocumentStamping(String filename, Certificate certificate)
            throws IOException, InterruptedException, AbstractPKCSException, AbstractOperatorCreationException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "stamped_" + filename;
        PdfReader reader = CompareTool.createOutputReader(srcFileName, new ReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), FACTORY.getProviderName(), null));
        PdfWriter writer = CompareTool.createTestPdfWriter(outFileName);
        PdfDocument document = new PdfDocument(reader, writer);
        document.close();

        CompareTool compareTool = new CompareTool();
        compareTool.getCmpReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), FACTORY.getProviderName(), null);
        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_" + filename,
                destinationFolder, "diff_");

        if (compareResult != null) {
            Assert.fail(compareResult);
        }
    }

    public void checkEncryptedWithCertificateDocumentAppending(String filename, Certificate certificate)
            throws IOException, InterruptedException, AbstractPKCSException, AbstractOperatorCreationException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "appended_" + filename;
        PdfReader reader = CompareTool.createOutputReader(srcFileName, new ReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), FACTORY.getProviderName(), null));
        PdfWriter writer = CompareTool.createTestPdfWriter(outFileName);
        PdfDocument document = new PdfDocument(reader, writer,
                new StampingProperties().useAppendMode());
        PdfPage newPage = document.addNewPage();
        String helloWorldStringValue = "Hello world string";
        newPage.put(PdfName.Default, new PdfString(helloWorldStringValue));
        writeTextBytesOnPageContent(newPage, "Hello world page_2!");
        document.close();

        PdfReader appendedDocReader = CompareTool.createOutputReader(outFileName, new ReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), FACTORY.getProviderName(), null));
        PdfDocument appendedDoc = new PdfDocument(appendedDocReader);
        PdfPage secondPage = appendedDoc.getPage(2);
        PdfString helloWorldPdfString = secondPage.getPdfObject().getAsString(PdfName.Default);
        String actualHelloWorldStringValue = helloWorldPdfString != null ? helloWorldPdfString.getValue() : null;
        Assert.assertEquals(actualHelloWorldStringValue, helloWorldStringValue);
        appendedDoc.close();

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        compareTool.getOutReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), FACTORY.getProviderName(), null);
        compareTool.getCmpReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), FACTORY.getProviderName(), null);

        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_appended_" + filename,
                destinationFolder, "diff_");

        if (compareResult != null) {
            Assert.fail(compareResult);
        }
    }

    static void writeTextBytesOnPageContent(PdfPage page, String text) throws IOException {
        page.getFirstContentStream().getOutputStream().writeBytes(("q\n" +
                "BT\n" +
                "36 706 Td\n" +
                "0 0 Td\n" +
                "/F1 24 Tf\n" +
                "(" + text + ")Tj\n" +
                "0 0 Td\n" +
                "ET\n" +
                "Q ").getBytes(StandardCharsets.ISO_8859_1));
        page.getResources().addFont(page.getDocument(), PdfFontFactory.createFont(StandardFonts.HELVETICA));
    }
}

