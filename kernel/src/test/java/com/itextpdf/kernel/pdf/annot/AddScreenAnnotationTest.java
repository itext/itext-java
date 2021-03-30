package com.itextpdf.kernel.pdf.annot;


import com.itextpdf.commons.utils.FileUtil;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    public void screenEmbeddedWavFromPathTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenEmbeddedWavFromPathTest.pdf";
        String cmp = sourceFolder + "cmp_" + "screenEmbeddedWavFromPathTest.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename))) {

            PdfFileSpec spec = PdfFileSpec
                    .createEmbeddedFileSpec(pdfDoc, sourceFolder + "sample.wav", null, "sample.wav", null, null);

            addPageWithScreenAnnotation(pdfDoc, spec);
        }

        String errorMessage = new CompareTool().compareByContent(filename, cmp, destinationFolder);
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void screenEmbeddedWavFromStreamTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenEmbeddedWavFromStreamTest.pdf";
        String cmp = sourceFolder + "cmp_" + "screenEmbeddedWavFromStreamTest.pdf";

        try (FileInputStream is = new FileInputStream(sourceFolder + "sample.wav")) {
            try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename))) {

                PdfFileSpec spec = PdfFileSpec
                        .createEmbeddedFileSpec(pdfDoc, is, null, "sample.wav", null, null);

                addPageWithScreenAnnotation(pdfDoc, spec);
            }
        }

        String errorMessage = new CompareTool().compareByContent(filename, cmp, destinationFolder);
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void screenEmbeddedWavFromBytesTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenEmbeddedWavFromBytesTest.pdf";
        String cmp = sourceFolder + "cmp_" + "screenEmbeddedWavFromBytesTest.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename))) {

            byte[] fileStore = Files.readAllBytes(Paths.get(sourceFolder + "sample.wav"));
            PdfFileSpec spec = PdfFileSpec
                    .createEmbeddedFileSpec(pdfDoc, fileStore, null, "sample.wav", null, null, null);

            addPageWithScreenAnnotation(pdfDoc, spec);
        }

        String errorMessage = new CompareTool().compareByContent(filename, cmp, destinationFolder);
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    @Test
    public void screenExternalWavTest() throws IOException, InterruptedException {
        String filename = destinationFolder + "screenExternalWavTest.pdf";
        String cmp = sourceFolder + "cmp_" + "screenExternalWavTest.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename))) {
            FileUtil.copy(sourceFolder + "sample.wav", destinationFolder + "sample.wav");
            PdfFileSpec spec = PdfFileSpec.createExternalFileSpec(pdfDoc, "sample.wav");

            addPageWithScreenAnnotation(pdfDoc, spec);
        }

        String errorMessage = new CompareTool().compareByContent(filename, cmp, destinationFolder);
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }

    private void addPageWithScreenAnnotation(PdfDocument pdfDoc, PdfFileSpec spec) throws IOException {
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

        PdfAction action = PdfAction.createRendition("sample.wav",
                spec, "audio/x-wav", screen);

        screen.setAction(action);

        page1.addAnnotation(screen);
        page1.flush();
    }
}
