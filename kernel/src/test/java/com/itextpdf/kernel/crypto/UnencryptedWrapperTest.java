package com.itextpdf.kernel.crypto;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.util.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfEncryptedPayload;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.kernel.pdf.filespec.PdfEncryptedPayloadFileSpecFactory;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
        String inPath = sourceFolder + "cmp_customEncryptedDocument.pdf";
        String cmpPath = sourceFolder + "cmp_simpleUnencryptedWrapper.pdf";
        String outPath = destinationFolder + "simpleUnencryptedWrapper.pdf";
        String diff = "diff_simpleUnencryptedWrapper.pdf_";

        PdfDocument document = new PdfDocument(new PdfWriter(outPath, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));
        PdfFileSpec fs = PdfEncryptedPayloadFileSpecFactory.create(document, inPath, new PdfEncryptedPayload("iText"));
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

    @Test
    public void extractCustomEncryptedDocumentTest() throws IOException, InterruptedException {
        String inPath = sourceFolder + "cmp_simpleUnencryptedWrapper.pdf";
        String cmpPath = sourceFolder + "cmp_customEncryptedDocument.pdf";
        String outPath = destinationFolder + "customEncryptedDocument.pdf";

        PdfDocument document = new PdfDocument(new PdfReader(inPath));
        PdfStream stream = document.getEncryptedPayloadAsStream();
        byte[] encryptedDocumentBytes = stream.getBytes();
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(encryptedDocumentBytes);
        fos.close();
        document.close();

        //TODO: check files by bytes
        Assert.assertNotNull(encryptedDocumentBytes);
    }


}
