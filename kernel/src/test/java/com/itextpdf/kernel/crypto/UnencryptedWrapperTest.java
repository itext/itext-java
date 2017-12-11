package com.itextpdf.kernel.crypto;

import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfEncryptedPayload;
import com.itextpdf.kernel.pdf.PdfEncryptedPayloadDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfEncryptedPayloadFileSpecFactory;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Category(IntegrationTest.class)
public class UnencryptedWrapperTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/crypto/UnencryptedWrapperTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/crypto/UnencryptedWrapperTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void createSimpleWrapperDocumentTest() throws IOException, InterruptedException {
        createWrapper("customEncryptedDocument.pdf", "simpleUnencryptedWrapper.pdf", "iText");
    }

    @Test
    public void extractCustomEncryptedDocumentTest() throws IOException, InterruptedException {
        extractEncrypted("customEncryptedDocument.pdf", "simpleUnencryptedWrapper.pdf", null);
    }

    @Test
    public void createWrapperForStandardEncryptedTest() throws IOException, InterruptedException {
        createWrapper("standardEncryptedDocument.pdf", "standardUnencryptedWrapper.pdf", "Standard");
    }

    @Test
    public void extractStandardEncryptedDocumentTest() throws IOException, InterruptedException {
        extractEncrypted("standardEncryptedDocument.pdf", "standardUnencryptedWrapper.pdf", "World".getBytes(StandardCharsets.ISO_8859_1));
    }

    private void createWrapper(String encryptedName, String wrapperName, String cryptoFilter) throws IOException, InterruptedException {
        String inPath = sourceFolder + "cmp_" + encryptedName;
        String cmpPath = sourceFolder + "cmp_" + wrapperName;
        String outPath = destinationFolder + wrapperName;
        String diff = "diff_" + wrapperName + "_";

        PdfDocument document = new PdfDocument(new PdfWriter(outPath, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfFileSpec fs = PdfEncryptedPayloadFileSpecFactory.create(document, inPath, new PdfEncryptedPayload(cryptoFilter));
        document.setEncryptedPayload(fs);

        PdfFont font = PdfFontFactory.createFont();
        PdfCanvas canvas = new PdfCanvas(document.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 750).
                setFontAndSize(font, 30).
                showText("Hi! I'm wrapper document.").
                endText().
                restoreState();
        canvas.release();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff));
    }

    private void extractEncrypted(String encryptedName, String wrapperName, byte[] password) throws IOException, InterruptedException {
        String inPath = sourceFolder + "cmp_" + wrapperName;
        String cmpPath = sourceFolder + "cmp_" + encryptedName;
        String outPath = destinationFolder + encryptedName;
        String diff = "diff_" + encryptedName + "_";

        PdfDocument document = new PdfDocument(new PdfReader(inPath));
        PdfEncryptedPayloadDocument encryptedDocument = document.getEncryptedPayloadDocument();
        byte[] encryptedDocumentBytes = encryptedDocument.getDocumentBytes();
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(encryptedDocumentBytes);
        fos.close();
        document.close();

        PdfEncryptedPayload ep = encryptedDocument.getEncryptedPayload();
        Assert.assertEquals(PdfEncryptedPayloadFileSpecFactory.generateFileDisplay(ep), encryptedDocument.getName());
        if (password != null) {
            Assert.assertNull(new CompareTool().compareByContent(outPath, cmpPath, destinationFolder, diff, password, password));
        } else {
            RandomAccessFileOrArray raf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(cmpPath));
            byte[] cmpBytes = new byte[(int) raf.length()];
            raf.readFully(cmpBytes);
            raf.close();
            Assert.assertArrayEquals(cmpBytes, encryptedDocumentBytes);
        }
    }
}
