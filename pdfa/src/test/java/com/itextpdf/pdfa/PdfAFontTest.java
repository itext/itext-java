package com.itextpdf.pdfa;

import com.itextpdf.basics.LogMessageConstant;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.color.DeviceRgb;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfAConformanceLevel;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfOutputIntent;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfAFontTest {

    static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    static final String outputDir = "./target/test/PdfA2/";

    @BeforeClass
    static public void beforeClass() {
        new File(outputDir).mkdirs();
    }


    public void fontCheckPdfA1_01() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputDir + "fontCheckPdfA1_01.pdf"));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "FreeMonoBold.ttf", "WinAnsi", true);
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

    @Test(expected = PdfAConformanceException.class)
    public void fontCheckPdfA1_02() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "FreeMonoBold.ttf", "WinAnsi");
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
    public void fontCheckPdfA1_03() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputDir + "fontCheckPdfA1_03.pdf"));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "FreeMonoBold.ttf", "Identity-H", false);
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

    @Test(expected = PdfAConformanceException.class)
    public void fontCheckPdfA1_04() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFont.createFont(doc, "Helvetica", "WinAnsi", true);
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
    @Ignore
    public void fontCheckPdfA1_05() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputDir + "fontCheckPdfA1_05.pdf"));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "NotoSansCJKjp-Bold.otf", "Identity-H");
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
    public void fontCheckPdfA2_01() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputDir + "fontCheckPdfA2_01.pdf"));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "FreeMonoBold.ttf", "Identity-H", false);
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
    }

    @Test
    public void fontCheckPdfA3_01() throws IOException, XMPException {
        PdfWriter writer = new PdfWriter(new FileOutputStream(outputDir + "fontCheckPdfA3_01.pdf"));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "FreeMonoBold.ttf", "Identity-H", false);
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
    }


    public void cidFontCheckTest1() throws  XMPException,IOException, InterruptedException {
        String outPdf = outputDir + "cidFontCheckTest1.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(outPdf));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "FreeMonoBold.ttf", "Identity-H", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 12)
                .showText("Hello World")
                .endText()
                .restoreState();


        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, sourceFolder + "cidset/cmp_cidFontCheckTest1.pdf", outputDir, "diff_"));
    }

    @Test
    public void cidFontCheckTest2() throws XMPException, IOException, InterruptedException {
        String outPdf = outputDir + "cidFontCheckTest2.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(outPdf));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "Puritan2.otf", "Identity-H", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 12)
                .showText("Hello World")
                .endText()
                .restoreState();


        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, sourceFolder + "cidset/cmp_cidFontCheckTest2.pdf", outputDir, "diff_"));
    }

    @Test
    public void cidFontCheckTest3() throws XMPException, IOException, InterruptedException {
        String outPdf = outputDir + "cidFontCheckTest3.pdf";
        PdfWriter writer = new PdfWriter(new FileOutputStream(outPdf));
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.setXmpMetadata();
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFont.createFont(doc, sourceFolder + "NotoSansCJKjp-Bold.otf", "Identity-H", true);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 12)
                .showText("Hello World")
                .endText()
                .restoreState();


        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outPdf, sourceFolder + "cidset/cmp_cidFontCheckTest3.pdf", outputDir, "diff_"));
    }
}