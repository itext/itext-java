package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class AddTextMarkupAnnotationTest extends ExtendedITextTest {

    public static final String sourceFolder =
            "./src/test/resources/com/itextpdf/kernel/pdf/annot/AddTextMarkupAnnotationTest/";
    public static final String destinationFolder =
            "./target/test/com/itextpdf/kernel/pdf/annot/AddTextMarkupAnnotationTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void textMarkupTest01() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Underline!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createUnderline(PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation01.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest02() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Highlight!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createHighLight(PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation02.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest03() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Squiggly!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createSquiggly(PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation03.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assert.assertNull(errorMessage);
        }
    }

    @Test
    public void textMarkupTest04() throws IOException, InterruptedException {
        String filename = destinationFolder + "textMarkupAnnotation04.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(filename));

        PdfPage page1 = pdfDoc.addNewPage();

        PdfCanvas canvas = new PdfCanvas(page1);
        //Initialize canvas and write text to it
        canvas
                .saveState()
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Strikeout!")
                .endText()
                .restoreState();

        float[] points = {36, 765, 109, 765, 36, 746, 109, 746};
        PdfTextMarkupAnnotation markup = PdfTextMarkupAnnotation.createStrikeout(PageSize.A4, points);
        markup.setContents(new PdfString("TextMarkup"));
        float[] rgb = {1, 0, 0};
        PdfArray colors = new PdfArray(rgb);
        markup.setColor(colors);
        page1.addAnnotation(markup);
        page1.flush();
        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool
                .compareByContent(filename, sourceFolder + "cmp_textMarkupAnnotation04.pdf", destinationFolder,
                        "diff_");
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}
