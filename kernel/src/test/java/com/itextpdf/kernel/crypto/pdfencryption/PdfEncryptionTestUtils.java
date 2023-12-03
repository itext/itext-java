package com.itextpdf.kernel.crypto.pdfencryption;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.CompareTool;
import org.junit.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class PdfEncryptionTestUtils {

    private final String destinationFolder;

    private final String sourceFolder;

    public static final String PAGE_TEXT_CONTENT = "Hello world!";

    public static final String CUSTOM_INFO_ENTRY_KEY = "Custom";

    public static final String CUSTOM_INFO_ENTRY_VALUE = "String";

    /**
     * User password.
     */
    public static byte[] USER = "Hello".getBytes(StandardCharsets.ISO_8859_1);

    /**
     * Owner password.
     */
    public static byte[] OWNER = "World".getBytes(StandardCharsets.ISO_8859_1);


    public PdfEncryptionTestUtils(String destinationFolder, String sourceFolder) {
        this.destinationFolder = destinationFolder;
        this.sourceFolder = sourceFolder;
    }

    public void compareEncryptedPdf(String filename) throws IOException, InterruptedException {
        checkDecryptedWithPasswordContent(destinationFolder + filename, OWNER, PAGE_TEXT_CONTENT);
        checkDecryptedWithPasswordContent(destinationFolder + filename, USER, PAGE_TEXT_CONTENT);

        CompareTool compareTool = new CompareTool().enableEncryptionCompare();
        String compareResult = compareTool.compareByContent(destinationFolder + filename,
                sourceFolder + "cmp_" + filename, destinationFolder, "diff_", USER, USER);
        if (compareResult != null) {
            Assert.fail(compareResult);
        }
    }

    public void checkDecryptedWithPasswordContent(String src, byte[] password, String pageContent)
            throws IOException {
        checkDecryptedWithPasswordContent(src, password, pageContent, false);
    }

    public void checkDecryptedWithPasswordContent(String src, byte[] password, String pageContent,
                                                     boolean expectError) throws IOException {
        PdfReader reader = CompareTool.createOutputReader(src, new ReaderProperties().setPassword(password));
        PdfDocument document = new PdfDocument(reader);
        PdfPage page = document.getPage(1);

        boolean expectedContentFound = new String(page.getStreamBytes(0)).contains(pageContent);
        String actualCustomInfoEntry = document.getTrailer().getAsDictionary(PdfName.Info)
                .getAsString(new PdfName(CUSTOM_INFO_ENTRY_KEY)).toUnicodeString();

        if (!expectError) {
            Assert.assertTrue("Expected content: \n" + pageContent, expectedContentFound);
            Assert.assertEquals("Encrypted custom", CUSTOM_INFO_ENTRY_VALUE, actualCustomInfoEntry);
        } else {
            Assert.assertFalse("Expected content: \n" + pageContent, expectedContentFound);
            Assert.assertNotEquals("Encrypted custom", CUSTOM_INFO_ENTRY_VALUE, actualCustomInfoEntry);
        }

        document.close();
    }

    public static void writeTextBytesOnPageContent(PdfPage page, String text) throws IOException {
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
