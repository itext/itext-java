package com.itextpdf.pdfa;

import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfAFontTest {

    static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    static final String outputDir = "./target/test/PdfAFontTest/";

    @BeforeClass
    public static void beforeClass() {
        new File(outputDir).mkdirs();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void fontCheckPdfA1_01() throws IOException, XMPException, InterruptedException {
        String outPdf = outputDir + "pdfA1b_fontCheckPdfA1_01.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(DeviceRgb.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fontCheckPdfA1_02() throws IOException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AllFontsMustBeEmbeddedThisOneIsnt1);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "WinAnsi");
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(DeviceRgb.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();
        doc.close();
    }

    @Test
    public void fontCheckPdfA1_03() throws IOException, XMPException, InterruptedException {
        String outPdf = outputDir + "pdfA1b_fontCheckPdfA1_03.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_03.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "Identity-H", false);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(DeviceRgb.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fontCheckPdfA1_04() throws IOException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.AllFontsMustBeEmbeddedThisOneIsnt1);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont("Helvetica", "WinAnsi", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(DeviceRgb.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();
        doc.close();
    }

    @Test
    public void fontCheckPdfA1_05() throws IOException, XMPException, InterruptedException {
        String outPdf = outputDir + "pdfA1b_fontCheckPdfA1_05.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_05.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "NotoSansCJKtc-Light.otf", "Identity-H");
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(DeviceRgb.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fontCheckPdfA2_01() throws IOException, XMPException, InterruptedException {
        String outPdf = outputDir + "pdfA2b_fontCheckPdfA2_01.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA2b_fontCheckPdfA2_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "Identity-H", false);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(DeviceRgb.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-2B")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fontCheckPdfA3_01() throws IOException, XMPException, InterruptedException {
        String outPdf = outputDir + "pdfA3b_fontCheckPdfA3_01.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA3b_fontCheckPdfA3_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "Identity-H", false);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(DeviceRgb.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-3B")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void cidFontCheckTest1() throws  XMPException,IOException, InterruptedException {
        String outPdf = outputDir + "pdfA2b_cidFontCheckTest1.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest1.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", "Identity-H", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 12)
                .showText("Hello World")
                .endText()
                .restoreState();


        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void cidFontCheckTest2() throws XMPException, IOException, InterruptedException {
        String outPdf = outputDir + "pdfA2b_cidFontCheckTest2.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest2.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "Puritan2.otf", "Identity-H", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 12)
                .showText("Hello World")
                .endText()
                .restoreState();


        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void cidFontCheckTest3() throws XMPException, IOException, InterruptedException {
        String outPdf = outputDir + "pdfA2b_cidFontCheckTest3.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest3.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "NotoSansCJKtc-Light.otf", "Identity-H", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 12)
                .showText("Hello World")
                .endText()
                .restoreState();


        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, outputDir, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}