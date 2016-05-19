package com.itextpdf.pdfa;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

import java.io.*;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfA2EmbeddedFilesCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2EmbeddedFilesCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2EmbeddedFilesCheckTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    @Ignore
    // According to spec, only pdfa-1 or pdfa-2 compliant pdf document are allowed to be added to the
    // conforming pdfa-2 document. We only check they mime type, to define embedded file type, but we don't check
    // the bytes of the file. That's why this test creates invalid pdfa document.
    public void fileSpecCheckTest01() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_fileSpecCheckTest01.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_fileSpecCheckTest01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();


        byte[] somePdf = new byte[25];
        pdfDocument.addFileAttachment("some pdf file", somePdf, "foo.pdf", PdfName.ApplicationPdf, null,
                new PdfName("Data"));

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fileSpecCheckTest02() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_fileSpecCheckTest02.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_fileSpecCheckTest02.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();

        FileInputStream fis = new FileInputStream(sourceFolder + "pdfa.pdf");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        pdfDocument.addFileAttachment("some pdf file", os.toByteArray(), "foo.pdf", PdfName.ApplicationPdf, null, null);

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fileSpecCheckTest03() throws IOException, XMPException {
        junitExpectedException.expect(PdfAConformanceException.class);
        junitExpectedException.expectMessage(PdfAConformanceException.EmbeddedFileShallBeOfPdfMimeType);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();

        ByteArrayOutputStream txt = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(txt);
        out.print("<foo><foo2>Hello world</foo2></foo>");
        out.close();
        pdfDocument.addFileAttachment("foo file", txt.toByteArray(), "foo.xml", PdfName.ApplicationXml, null, PdfName.Source);

        pdfDocument.close();
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
