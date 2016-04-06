package com.itextpdf.pdfa;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

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
public class PdfA2CanvasCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    static final public String destinationFolder = "./target/test/PdfA2CanvasCheckTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canvasCheckTest1() throws IOException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.GraphicStateStackDepthIsGreaterThan28);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);
        pdfDocument.createXmpMetadata();

        pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

        for (int i = 0; i < 29; i++) {
            canvas.saveState();
        }

        for (int i = 0; i < 28; i++) {
            canvas.restoreState();
        }

        pdfDocument.close();
    }

    @Test
    public void canvasCheckTest2() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_canvasCheckTest2.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfA2CanvasCheckTest/cmp_pdfA2b_canvasCheckTest2.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);
        pdfDocument.createXmpMetadata();

        pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

        for (int i = 0; i < 28; i++) {
            canvas.saveState();
        }

        for (int i = 0; i < 28; i++) {
            canvas.restoreState();
        }

        pdfDocument.close();

        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }

    @Test
    public void canvasCheckTest3() throws IOException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.IfSpecifiedRenderingShallBeOneOfTheFollowingRelativecolorimetricAbsolutecolorimetricPerceptualOrSaturation);

        PdfWriter writer = new PdfWriter(new java.io.ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);
        pdfDocument.createXmpMetadata();

        pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

        canvas.setRenderingIntent(new PdfName("Test"));

        pdfDocument.close();
    }
}

