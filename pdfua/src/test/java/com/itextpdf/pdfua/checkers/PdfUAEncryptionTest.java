package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Category(IntegrationTest.class)
public class PdfUAEncryptionTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAEncryptionTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAEncryptionTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final byte[] USER_PASSWORD = "user".getBytes(StandardCharsets.UTF_8);
    private static final byte[] OWNER_PASSWORD = "owner".getBytes(StandardCharsets.UTF_8);

    @BeforeClass
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void encryptWithPassword()
            throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "encryptWithPassword.pdf";
        WriterProperties writerProperties = PdfUATestPdfDocument.createWriterProperties()
                .setStandardEncryption(USER_PASSWORD, OWNER_PASSWORD, -1, 3);
        try (PdfWriter writer = new PdfWriter(outPdf,
                writerProperties);
             PdfUATestPdfDocument document = new PdfUATestPdfDocument(writer)) {
            writeTextToDocument(document);
        }
        Assert.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_" + "encryptWithPassword.pdf", DESTINATION_FOLDER, "diff", USER_PASSWORD, USER_PASSWORD));
    }

    @Test
    public void encryptWithPasswordWithInvalidPermissionsTest()
            throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "encryptWithPassword2.pdf";
        WriterProperties writerProperties = PdfUATestPdfDocument.createWriterProperties()
                .setStandardEncryption(USER_PASSWORD, OWNER_PASSWORD, 0, 3);
        PdfUATestPdfDocument document = new PdfUATestPdfDocument(new PdfWriter(outPdf, writerProperties));
        writeTextToDocument(document);
        Exception e = Assert.assertThrows(PdfUAConformanceException.class, () -> document.close());
        Assert.assertEquals(PdfUAExceptionMessageConstants.TENTH_BIT_OF_P_VALUE_IN_ENCRYPTION_SHOULD_BE_NON_ZERO,
                e.getMessage());
    }

    private void writeTextToDocument(PdfDocument document) throws IOException {
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P, page));
        PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page, paragraph));
        canvas
                .openTag(new CanvasTag(mcr))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .restoreState()
                .closeTag();
    }
}
