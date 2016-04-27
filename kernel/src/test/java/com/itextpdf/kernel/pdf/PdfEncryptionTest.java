package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;


import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfEncryptionTest extends ExtendedITextTest{

    /** User password. */
    public static byte[] USER = "Hello".getBytes();
    /** Owner password. */
    public static byte[] OWNER = "World".getBytes();

    static final String author = "Alexander Chingarev";
    static final String creator = "iText 6";

    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfEncryptionTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfEncryptionTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
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

    public void encryptWithPassword(String filename, int encryptionType, int compression) throws XMPException, IOException, InterruptedException {
        String outFileName = destinationFolder + filename;
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS;
        com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(new FileOutputStream(outFileName),
                new WriterProperties().setStandardEncryption(USER, OWNER, permissions, encryptionType).addXmpMetadata());
        writer.setCompressionLevel(compression);
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setAuthor(author).
                setCreator(creator);
        PdfPage page = document.addNewPage();
        page.getFirstContentStream().getOutputStream().writeBytes(("q\n" +
                "BT\n" +
                "36 706 Td\n" +
                "0 0 Td\n" +
                "/F1 24 Tf\n" +
                "(Hello world!)Tj\n" +
                "0 0 Td\n" +
                "ET\n" +
                "Q ").getBytes());
        page.getResources().addFont(document, PdfFontFactory.createFont(FontConstants.HELVETICA));

        page.flush();
        document.close();

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_" + filename, destinationFolder, "diff_", USER, USER);
        if (compareResult != null) {
            fail(compareResult);
        }

        checkDecryptedContent(filename, OWNER, "(Hello world!)");
        checkDecryptedContent(filename, USER, "(Hello world!)");
        checkDocumentStamping(filename, OWNER);
        checkDocumentAppending(filename, OWNER);
    }

    public void checkDecryptedContent(String filename, byte[] password, String pageContent) throws IOException {
        String src = destinationFolder + filename;
        PdfReader reader = new com.itextpdf.kernel.pdf.PdfReader(src, new ReaderProperties().setPassword(password));
        PdfDocument document = new com.itextpdf.kernel.pdf.PdfDocument(reader);
        PdfPage page = document.getPage(1);

        Assert.assertTrue("Expected content: \n" + pageContent, new String(page.getStreamBytes(0)).contains(pageContent));
        Assert.assertEquals("Encrypted author", author, document.getDocumentInfo().getAuthor());
        Assert.assertEquals("Encrypted creator", creator, document.getDocumentInfo().getCreator());

        document.close();
    }

    public void checkDocumentStamping(String filename, byte[] password) throws IOException, InterruptedException {
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

    public void checkDocumentAppending(String filename, byte[] password) throws IOException, InterruptedException {
        String srcFileName = destinationFolder + filename;
        String outFileName = destinationFolder + "appended_" + filename;
        PdfReader reader = new PdfReader(srcFileName, new ReaderProperties().setPassword(password));
        PdfDocument document = new PdfDocument(reader, new PdfWriter(outFileName), new StampingProperties().useAppendMode());
        PdfPage newPage = document.addNewPage();
        newPage.put(PdfName.Default, new PdfString("Hello world string"));
        document.close();

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();

        String compareResult = compareTool.compareByContent(outFileName, sourceFolder + "cmp_appended_" + filename, destinationFolder, "diff_", USER, USER);

        if (compareResult != null) {
            fail(compareResult);
        }
    }




}
