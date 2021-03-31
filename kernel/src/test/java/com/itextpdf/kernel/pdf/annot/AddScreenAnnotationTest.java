package com.itextpdf.kernel.pdf.annot;


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class AddScreenAnnotationTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/annot/AddScreenAnnotationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/annot/AddScreenAnnotationTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }



    @Test
    public void screenTestExternalWavFile() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        FileOutputStream fos = new FileOutputStream(destinationFolder + "sample.wav");
        FileInputStream fis = new FileInputStream(sourceFolder + "sample.wav");
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        fis.close();
        PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, "sample.wav");

        PdfAction action = PdfAction.createRendition("sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation01.pdf", destinationFolder, "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void screenTestEmbeddedWavFile01() throws IOException {
        String filename = destinationFolder + "screenAnnotation02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, sourceFolder + "sample.wav", null, "sample.wav", null, null);

        PdfAction action = PdfAction.createRendition(sourceFolder + "sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation02.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void screenTestEmbeddedWavFile02() throws IOException {
        String filename = destinationFolder + "screenAnnotation03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, new FileInputStream(sourceFolder + "sample.wav"), null, "sample.wav", null, null);

        PdfAction action = PdfAction.createRendition(sourceFolder + "sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation03.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }

    @Test
    public void screenTestEmbeddedWavFile03() throws IOException {
        String filename = destinationFolder + "screenAnnotation04.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        canvas
                .saveState()
                .beginText()
                .moveText(36, 105)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Click on the area below to play a sound.")
                .endText()
                .restoreState();
        PdfScreenAnnotation screen = new PdfScreenAnnotation(new Rectangle(100, 100));

        InputStream is = new FileInputStream(sourceFolder + "sample.wav");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int reads = is.read();

        while (reads != -1) {
            baos.write(reads);
            reads = is.read();
        }

        PdfFileSpec spec = PdfFileSpec.createEmbeddedFileSpec(pdfDoc, baos.toByteArray(), null, "sample.wav", null, null, null);

        PdfAction action = PdfAction.createRendition(sourceFolder + "sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();

        pdfDoc.close();

//        CompareTool compareTool = new CompareTool();
//        String errorMessage = compareTool.compareByContent(filename, sourceFolder + "cmp_screenAnnotation04.pdf", destinationFolder, "diff_");
//        if (errorMessage != null) {
//            Assert.fail(errorMessage);
//        }
    }
}
