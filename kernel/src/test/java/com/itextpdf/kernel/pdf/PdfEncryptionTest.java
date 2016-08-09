package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.util.CryptoUtil;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.crypto.BadPasswordException;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import static org.junit.Assert.fail;

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
@Category(IntegrationTest.class)
public class PdfEncryptionTest extends ExtendedITextTest{

    /** User password. */
    public static byte[] USER = "Hello".getBytes();
    /** Owner password. */
    public static byte[] OWNER = "World".getBytes();

    static final String author = "Alexander Chingarev";
    static final String creator = "iText 7";

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfEncryptionTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfEncryptionTest/";

    public static final String CERT = sourceFolder + "test.cer";
    public static final String PRIVATE_KEY = sourceFolder + "test.p12";
    public static final char[] PRIVATE_KEY_PASS = "kspass".toCharArray();
    private PrivateKey privateKey;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void encryptWithPasswordStandard128() throws IOException, XMPException, InterruptedException {
        String filename = "encryptWithPasswordStandard128.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_128;
        encryptWithPassword(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard40() throws IOException, XMPException, InterruptedException {
        String filename = "encryptWithPasswordStandard40.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_40;
        encryptWithPassword(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard128NoCompression() throws IOException, XMPException, InterruptedException {
        String filename = "encryptWithPasswordStandard128NoCompression.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_128;
        encryptWithPassword(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard40NoCompression() throws IOException, XMPException, InterruptedException {
        String filename = "encryptWithPasswordStandard40NoCompression.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_40;
        encryptWithPassword(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes128() throws IOException, XMPException, InterruptedException {
        String filename = "encryptWithPasswordAes128.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        encryptWithPassword(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes256() throws IOException, XMPException, InterruptedException {
        String filename = "encryptWithPasswordAes256.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        encryptWithPassword(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes128NoCompression() throws IOException, XMPException, InterruptedException {
        String filename = "encryptWithPasswordAes128NoCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        encryptWithPassword(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes256NoCompression() throws IOException, XMPException, InterruptedException {
        String filename = "encryptWithPasswordAes256NoCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        encryptWithPassword(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    public void encryptWithCertificateStandard128() throws IOException, XMPException, InterruptedException, GeneralSecurityException {
        String filename = "encryptWithCertificateStandard128.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_128;
        encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithCertificateStandard40() throws IOException, XMPException, InterruptedException, GeneralSecurityException {
        String filename = "encryptWithCertificateStandard40.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_40;
        encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithCertificateStandard128NoCompression() throws IOException, XMPException, InterruptedException, GeneralSecurityException {
        String filename = "encryptWithCertificateStandard128NoCompression.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_128;
        encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    public void encryptWithCertificateStandard40NoCompression() throws IOException, XMPException, InterruptedException, GeneralSecurityException {
        String filename = "encryptWithCertificateStandard40NoCompression.pdf";
        int encryptionType = EncryptionConstants.STANDARD_ENCRYPTION_40;
        encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    public void encryptWithCertificateAes128() throws IOException, XMPException, InterruptedException, GeneralSecurityException {
        String filename = "encryptWithCertificateAes128.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithCertificateAes256() throws IOException, XMPException, InterruptedException, GeneralSecurityException {
        String filename = "encryptWithCertificateAes256.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        encryptWithCertificate(filename, encryptionType, CompressionConstants.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithCertificateAes128NoCompression() throws IOException, XMPException, InterruptedException, GeneralSecurityException {
        String filename = "encryptWithCertificateAes128NoCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_128;
        encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }


    @Test
    public void encryptWithCertificateAes256NoCompression() throws IOException, XMPException, InterruptedException, GeneralSecurityException {
        String filename = "encryptWithCertificateAes256NoCompression.pdf";
        int encryptionType = EncryptionConstants.ENCRYPTION_AES_256;
        encryptWithCertificate(filename, encryptionType, CompressionConstants.NO_COMPRESSION);
    }

    @Test
    public void openEncryptedDocWithoutPassword() throws IOException {
        junitExpectedException.expect(BadPasswordException.class);
        junitExpectedException.expectMessage(BadPasswordException.BadUserPassword);

        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "encryptedWithPasswordStandard40.pdf"));
        doc.close();
    }

    @Test
    public void openEncryptedDocWithWrongPassword() throws IOException {
        junitExpectedException.expect(BadPasswordException.class);
        junitExpectedException.expectMessage(BadPasswordException.BadUserPassword);

        PdfReader reader = new PdfReader(sourceFolder + "encryptedWithPasswordStandard40.pdf",
                new ReaderProperties().setPassword("wrong_password".getBytes()));
        PdfDocument doc = new PdfDocument(reader);
        doc.close();
    }

    @Test
    public void openEncryptedDocWithoutCertificate() throws IOException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.CertificateIsNotProvidedDocumentIsEncryptedWithPublicKeyCertificate);

        PdfDocument doc = new PdfDocument(new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf"));
        doc.close();
    }

    @Test
    public void openEncryptedDocWithoutPrivateKey() throws IOException, CertificateException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.BadCertificateAndKey);

        PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties()
                        .setPublicKeySecurityParams(
                                getPublicCertificate(sourceFolder + "wrong.cer"),
                                null,
                                "BC",
                                null));
        PdfDocument doc = new PdfDocument(reader);
        doc.close();
    }

    @Test
    public void openEncryptedDocWithWrongCertificate() throws IOException, GeneralSecurityException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.BadCertificateAndKey);

        PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties()
                        .setPublicKeySecurityParams(
                                getPublicCertificate(sourceFolder + "wrong.cer"),
                                getPrivateKey(),
                                "BC",
                                null));
        PdfDocument doc = new PdfDocument(reader);
        doc.close();
    }

    @Test
    public void openEncryptedDocWithWrongPrivateKey() throws IOException, GeneralSecurityException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.PdfDecryption);

        PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties()
                        .setPublicKeySecurityParams(
                                getPublicCertificate(CERT),
                                CryptoUtil.readPrivateKeyFromPKCS12KeyStore(new FileInputStream(sourceFolder + "wrong.p12"), "demo", "password".toCharArray()),
                                "BC",
                                null));
        PdfDocument doc = new PdfDocument(reader);
        doc.close();
    }

    @Test
    public void openEncryptedDocWithWrongCertificateAndPrivateKey() throws IOException, GeneralSecurityException {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(PdfException.BadCertificateAndKey);

        PdfReader reader = new PdfReader(sourceFolder + "encryptedWithCertificateAes128.pdf",
                new ReaderProperties()
                        .setPublicKeySecurityParams(
                                getPublicCertificate(sourceFolder + "wrong.cer"),
                                CryptoUtil.readPrivateKeyFromPKCS12KeyStore(new FileInputStream(sourceFolder + "wrong.p12"), "demo", "password".toCharArray()),
                                "BC",
                                null));
        PdfDocument doc = new PdfDocument(reader);
        doc.close();
    }

    public void encryptWithPassword(String filename, int encryptionType, int compression) throws XMPException, IOException, InterruptedException {
        String outFileName = destinationFolder + filename;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        PdfWriter writer = new PdfWriter(outFileName,
                new WriterProperties().setStandardEncryption(USER, OWNER, permissions, encryptionType).addXmpMetadata());
        writer.setCompressionLevel(compression);
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setAuthor(author).
                setCreator(creator);
        PdfPage page = document.addNewPage();
        String textContent = "Hello world!";
        writeTextBytesOnPageContent(page, textContent);

        page.flush();
        document.close();

        checkDecryptedWithPasswordContent(filename, OWNER, textContent);
        checkDecryptedWithPasswordContent(filename, USER, textContent);

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_" + filename, destinationFolder, "diff_", USER, USER);
        if (compareResult != null) {
            fail(compareResult);
        }
        checkEncryptedWithPasswordDocumentStamping(filename, OWNER);
        checkEncryptedWithPasswordDocumentAppending(filename, OWNER);
    }

    public void encryptWithCertificate(String filename, int encryptionType, int compression) throws XMPException, IOException, InterruptedException, GeneralSecurityException {
        ITextTest.removeCryptographyRestrictions();

        String outFileName = destinationFolder + filename;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        Certificate cert = getPublicCertificate(CERT);
        PdfWriter writer = new PdfWriter(outFileName, new WriterProperties()
                .setPublicKeyEncryption(new Certificate[]{cert}, new int[]{permissions}, encryptionType)
                .addXmpMetadata());
        writer.setCompressionLevel(compression);
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setAuthor(author).
                setCreator(creator);
        PdfPage page = document.addNewPage();
        String textContent = "Hello world!";
        writeTextBytesOnPageContent(page, textContent);

        page.flush();
        document.close();

        checkDecryptedWithCertificateContent(filename, cert, textContent);

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        compareTool.getOutReaderProperties().setPublicKeySecurityParams(cert, getPrivateKey(), "BC", null);
        compareTool.getCmpReaderProperties().setPublicKeySecurityParams(cert, getPrivateKey(), "BC", null);
        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_" + filename, destinationFolder, "diff_");
        if (compareResult != null) {
            fail(compareResult);
        }

        checkEncryptedWithCertificateDocumentStamping(filename, cert);
        checkEncryptedWithCertificateDocumentAppending(filename, cert);

        ITextTest.restoreCryptographyRestrictions();
    }

    private void writeTextBytesOnPageContent(PdfPage page, String text) throws IOException {
        page.getFirstContentStream().getOutputStream().writeBytes(("q\n" +
                "BT\n" +
                "36 706 Td\n" +
                "0 0 Td\n" +
                "/F1 24 Tf\n" +
                "(" + text + ")Tj\n" +
                "0 0 Td\n" +
                "ET\n" +
                "Q ").getBytes());
        page.getResources().addFont(page.getDocument(), PdfFontFactory.createFont(FontConstants.HELVETICA));
    }

    public Certificate getPublicCertificate(String path) throws IOException, CertificateException {
        FileInputStream is = new FileInputStream(path);
        return CryptoUtil.readPublicCertificate(is);
    }

    public PrivateKey getPrivateKey() throws GeneralSecurityException, IOException {
        if (privateKey == null) {
            privateKey = CryptoUtil.readPrivateKeyFromPKCS12KeyStore(new FileInputStream(PRIVATE_KEY), "sandbox", PRIVATE_KEY_PASS);
        }
        return privateKey;
    }

    public void checkDecryptedWithPasswordContent(String filename, byte[] password, String pageContent) throws IOException {
        String src = destinationFolder + filename;
        PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(src, new ReaderProperties().setPassword(password));
        PdfDocument document = new com.itextpdf.kernel.pdf.PdfDocument(reader);
        PdfPage page = document.getPage(1);

        Assert.assertTrue("Expected content: \n" + pageContent, new String(page.getStreamBytes(0)).contains(pageContent));
        Assert.assertEquals("Encrypted author", author, document.getDocumentInfo().getAuthor());
        Assert.assertEquals("Encrypted creator", creator, document.getDocumentInfo().getCreator());

        document.close();
    }

    public void checkDecryptedWithCertificateContent(String filename, Certificate certificate, String pageContent) throws IOException, GeneralSecurityException {
        String src = destinationFolder + filename;
        PdfReader reader = new PdfReader(src, new ReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), "BC", null));
        PdfDocument document = new PdfDocument(reader);
        PdfPage page = document.getPage(1);

        String s = new String(page.getStreamBytes(0));
        Assert.assertTrue("Expected content: \n" + pageContent, s.contains(pageContent));
        Assert.assertEquals("Encrypted author", author, document.getDocumentInfo().getAuthor());
        Assert.assertEquals("Encrypted creator", creator, document.getDocumentInfo().getCreator());

        document.close();
    }

    // basically this is comparing content of decrypted by itext document with content of encrypted document
    public void checkEncryptedWithPasswordDocumentStamping(String filename, byte[] password) throws IOException, InterruptedException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "stamped_" + filename;
        PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(password));
        PdfDocument document = new PdfDocument(reader, new PdfWriter(outFileName));
        document.close();

        CompareTool compareTool = new CompareTool();

        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_" + filename, destinationFolder, "diff_", USER, USER);

        if (compareResult != null) {
            fail(compareResult);
        }
    }

    // basically this is comparing content of decrypted by itext document with content of encrypted document
    public void checkEncryptedWithCertificateDocumentStamping(String filename, Certificate certificate) throws IOException, InterruptedException, GeneralSecurityException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "stamped_" + filename;
        PdfReader reader = new PdfReader(srcFileName, new ReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), "BC", null));
        PdfDocument document = new PdfDocument(reader, new PdfWriter(outFileName));
        document.close();

        CompareTool compareTool = new CompareTool();
        compareTool.getCmpReaderProperties().setPublicKeySecurityParams(certificate, getPrivateKey(), "BC", null);
        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_" + filename, destinationFolder, "diff_");

        if (compareResult != null) {
            fail(compareResult);
        }
    }

    public void checkEncryptedWithPasswordDocumentAppending(String filename, byte[] password) throws IOException, InterruptedException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "appended_" + filename;
        PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(password));
        PdfDocument document = new PdfDocument(reader, new PdfWriter(outFileName), new StampingProperties().useAppendMode());
        PdfPage newPage = document.addNewPage();
        newPage.put(PdfName.Default, new PdfString("Hello world string"));
        writeTextBytesOnPageContent(newPage, "Hello world page_2!");
        document.close();

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();

        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_appended_" + filename, destinationFolder, "diff_", USER, USER);

        if (compareResult != null) {
            fail(compareResult);
        }
    }

    public void checkEncryptedWithCertificateDocumentAppending(String filename, Certificate certificate) throws IOException, InterruptedException, GeneralSecurityException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "appended_" + filename;
        PdfReader reader = new PdfReader(srcFileName, new ReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), "BC", null));
        PdfDocument document = new PdfDocument(reader, new PdfWriter(outFileName), new StampingProperties().useAppendMode());
        PdfPage newPage = document.addNewPage();
        String helloWorldStringValue = "Hello world string";
        newPage.put(PdfName.Default, new PdfString(helloWorldStringValue));
        writeTextBytesOnPageContent(newPage, "Hello world page_2!");
        document.close();

        PdfReader appendedDocReader = new PdfReader(outFileName, new ReaderProperties()
                .setPublicKeySecurityParams(certificate, getPrivateKey(), "BC", null));
        PdfDocument appendedDoc = new PdfDocument(appendedDocReader);
        PdfPage secondPage = appendedDoc.getPage(2);
        PdfString helloWorldPdfString = secondPage.getPdfObject().getAsString(PdfName.Default);
        String actualHelloWorldStringValue = helloWorldPdfString != null ? helloWorldPdfString.getValue() : null;
        Assert.assertEquals(actualHelloWorldStringValue, helloWorldStringValue);
        appendedDoc.close();

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        compareTool.getOutReaderProperties().setPublicKeySecurityParams(certificate, getPrivateKey(), "BC", null);
        compareTool.getCmpReaderProperties().setPublicKeySecurityParams(certificate, getPrivateKey(), "BC", null);

        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_appended_" + filename, destinationFolder, "diff_");

        if (compareResult != null) {
            fail(compareResult);
        }
    }
}
