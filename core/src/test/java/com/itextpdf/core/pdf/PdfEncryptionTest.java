package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfRuntimeException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.basics.io.ByteArrayOutputStream;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfStamper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Security;

public class PdfEncryptionTest {

    public static byte[] USER = "Hello".getBytes();
    /** Owner password. */
    public static byte[] OWNER = "World".getBytes();

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfEncryptionTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfEncryptionTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void encryptWithPasswordStandard128() throws IOException, DocumentException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordStandard128.pdf";
        int encryptionType = PdfWriter.STANDARD_ENCRYPTION_128;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard40() throws IOException, DocumentException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordStandard40.pdf";
        int encryptionType = PdfWriter.STANDARD_ENCRYPTION_40;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard128NoCompression() throws IOException, DocumentException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordStandard128NoCompression.pdf";
        int encryptionType = PdfWriter.STANDARD_ENCRYPTION_128;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordStandard40NoCompression() throws IOException, DocumentException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordStandard40NoCompression.pdf";
        int encryptionType = PdfWriter.STANDARD_ENCRYPTION_40;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes128() throws IOException, DocumentException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordAes128.pdf";
        int encryptionType = PdfWriter.ENCRYPTION_AES_128;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes256() throws IOException, DocumentException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordAes256.pdf";
        int encryptionType = PdfWriter.ENCRYPTION_AES_256;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.DEFAULT_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes128NoCompression() throws IOException, DocumentException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordAes128NoCompression.pdf";
        int encryptionType = PdfWriter.ENCRYPTION_AES_128;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.NO_COMPRESSION);
    }

    @Test
    public void encryptWithPasswordAes256NoCompression() throws IOException, DocumentException, XMPException {
        String filename = destinationFolder + "encryptWithPasswordAes256NoCompression.pdf";
        int encryptionType = PdfWriter.ENCRYPTION_AES_256;
        encryptWithPassword(filename, encryptionType, PdfOutputStream.NO_COMPRESSION);
    }

    public void encryptWithPassword(String filename, int encryptionType, int compression) throws XMPException, IOException, DocumentException {
        int permissions = com.itextpdf.core.pdf.PdfWriter.ALLOW_SCREENREADERS;
        com.itextpdf.core.pdf.PdfWriter writer = new com.itextpdf.core.pdf.PdfWriter(new FileOutputStream(filename));
        writer.setCompressionLevel(compression);
        writer.setEncryption(USER, OWNER, permissions, encryptionType);
        com.itextpdf.core.pdf.PdfDocument document = new com.itextpdf.core.pdf.PdfDocument(writer);
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
        page.getResources().addFont(new PdfType1Font(document, new Type1Font(FontConstants.HELVETICA, "")));

        page.flush();
        document.close();

        iText5Decrypt(filename, OWNER);
        iText5Decrypt(filename, USER);
        iText6Decrypt(filename, OWNER, "(Hello world!)");
        iText6Decrypt(filename, USER, "(Hello world!)");
    }

    public void iText6Decrypt(String src, byte[] password, String pageContent) throws IOException {
        PdfReader reader = new com.itextpdf.core.pdf.PdfReader(src, password);
        PdfWriter writer = new com.itextpdf.core.pdf.PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new com.itextpdf.core.pdf.PdfDocument(reader, writer);

        PdfPage page = document.getPage(1);
        Assert.assertTrue("Expected content \n" + pageContent, new String(page.getStreamBytes(0)).contains(pageContent));
        document.close();
    }

    public void iText5Decrypt(String src, byte[] password) throws IOException, DocumentException {
        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(src, OWNER);
        PdfStamper stamper = new PdfStamper(reader, new ByteArrayOutputStream());
        stamper.close();
        reader.close();
    }




}
