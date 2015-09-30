package com.itextpdf.pdfa;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfAConformanceLevel;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfOutputIntent;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.*;

@Category(IntegrationTest.class)
public class PdfA1CanvasCheckTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canvasCheckTest1() throws IOException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.GraphicStateStackDepthIsGreaterThan28);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        pdfDocument.setXmpMetadata();

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
    public void canvasCheckTest2() throws IOException, XMPException {

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        pdfDocument.setXmpMetadata();

        pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

        for (int i = 0; i < 28; i++) {
            canvas.saveState();
        }

        for (int i = 0; i < 28; i++) {
            canvas.restoreState();
        }

        pdfDocument.close();
    }

    @Test
    public void canvasCheckTest3() throws IOException, XMPException {
        thrown.expect(PdfAConformanceException.class);
        thrown.expectMessage(PdfAConformanceException.IfSpecifiedRenderingShallBeOneOfTheFollowingRelativecolorimetricAbsolutecolorimetricPerceptualOrSaturation);

        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, outputIntent);
        pdfDocument.setXmpMetadata();

        pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

        canvas.setRenderingIntent(new PdfName("Test"));

        pdfDocument.close();
    }
}
