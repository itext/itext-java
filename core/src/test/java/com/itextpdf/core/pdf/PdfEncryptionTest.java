package com.itextpdf.core.pdf;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.source.ByteArrayOutputStream;
import com.itextpdf.core.font.PdfFontFactory;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;


import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfEncryptionTest extends ExtendedITextTest{

    public static byte[] USER = "Hello".getBytes();
    /** Owner password. */
    public static byte[] OWNER = "World".getBytes();

    static final String author = "Alexander Chingarev";
    static final String creator = "iText 6";

    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfEncryptionTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void encryptWithPasswordStandard128() throws IOException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordStandard128.pdf";
        int encryptionType = PdfWriter.STANDARD_ENCRYPTION_128;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard40() throws IOException,  XMPException {
        String filename = destinationFolder + "encryptWithPasswordStandard40.pdf";
        int encryptionType = PdfWriter.STANDARD_ENCRYPTION_40;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard128NoCompression() throws IOException,  XMPException {
        String filename = destinationFolder + "encryptWithPasswordStandard128NoCompression.pdf";
        int encryptionType = PdfWriter.STANDARD_ENCRYPTION_128;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard40NoCompression() throws IOException,  XMPException {
        String filename = destinationFolder + "encryptWithPasswordStandard40NoCompression.pdf";
        int encryptionType = PdfWriter.STANDARD_ENCRYPTION_40;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes128() throws IOException,  XMPException {
        String filename = destinationFolder + "encryptWithPasswordAes128.pdf";
        int encryptionType = PdfWriter.ENCRYPTION_AES_128;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes256() throws IOException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordAes256.pdf";
        int encryptionType = PdfWriter.ENCRYPTION_AES_256;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes128NoCompression() throws IOException,  XMPException {
        String filename = destinationFolder + "encryptWithPasswordAes128NoCompression.pdf";
        int encryptionType = PdfWriter.ENCRYPTION_AES_128;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes256NoCompression() throws IOException,  XMPException {
        String filename = destinationFolder + "encryptWithPasswordAes256NoCompression.pdf";
        int encryptionType = PdfWriter.ENCRYPTION_AES_256;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.NO_COMPRESSION);
    }

    public void encryptWithPassword(String filename, int encryptionType, int compression) throws XMPException, IOException  {
        int permissions = com.itextpdf.core.pdf.PdfWriter.ALLOW_SCREENREADERS;
        com.itextpdf.core.pdf.PdfWriter writer = new com.itextpdf.core.pdf.PdfWriter(new FileOutputStream(filename));
        writer.setCompressionLevel(compression);
        writer.setEncryption(USER, OWNER, permissions, encryptionType);
        PdfDocument document = new PdfDocument(writer);
        document.getInfo().setAuthor(author).
                setCreator(creator);
        document.setXmpMetadata();
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
        page.getResources().addFont(document, PdfFontFactory.createStandardFont(FontConstants.HELVETICA));

        page.flush();
        document.close();

        iText7Decrypt(filename, OWNER);
        //iText7Decrypt(filename, USER);
        iText7Decrypt(filename, OWNER, "(Hello world!)");
        //iText7Decrypt(filename, USER, "(Hello world!)");
    }

    public void iText7Decrypt(String src, byte[] password, String pageContent) throws IOException {
        PdfReader reader = new com.itextpdf.core.pdf.PdfReader(src, password);
        PdfDocument document = new com.itextpdf.core.pdf.PdfDocument(reader);
        String author = document.getInfo().getAuthor();
        String creator = document.getInfo().getCreator();
        PdfPage page = document.getPage(1);

        Assert.assertTrue("Expected content: \n" + pageContent, new String(page.getStreamBytes(0)).contains(pageContent));
        Assert.assertEquals("Encrypted author", this.author, document.getInfo().getAuthor());
        Assert.assertEquals("Encrypted creator", this.creator, document.getInfo().getCreator());

        document.close();
    }

    public void iText7Decrypt(String src, byte[] password) throws IOException {
        PdfReader reader = new PdfReader(src, password);
        PdfDocument stamper = new PdfDocument(reader, new PdfWriter(new ByteArrayOutputStream()));
        stamper.close();
        reader.close();
    }




}
