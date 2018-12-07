package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CanvasTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/CanvasTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/CanvasTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN))
    public void canvasNoPageLinkTest() throws IOException, InterruptedException {
        String testName = "canvasNoPageLinkTest";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(pdfCanvas, pdf, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    public void canvasWithPageLinkTest() throws IOException, InterruptedException {
        String testName = "canvasWithPageLinkTest";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    public void canvasWithPageEnableTaggingTest01() throws IOException, InterruptedException {
        String testName = "canvasWithPageEnableTaggingTest01";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));

        pdf.setTagged();

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN),
            @LogMessage(messageTemplate = LogMessageConstant.PASSED_PAGE_SHALL_BE_ON_WHICH_CANVAS_WILL_BE_RENDERED)})
    public void canvasWithPageEnableTaggingTest02() throws IOException, InterruptedException {
        String testName = "canvasWithPageEnableTaggingTest02";
        String out = destinationFolder + testName + ".pdf";
        String cmp = sourceFolder + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));

        pdf.setTagged();

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);

        // This will disable tagging and also prevent annotations addition. Created tagged document is invalid. Expected log message.
        canvas.enableAutoTagging(null);

        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assert.assertNull(new CompareTool().compareByContent(out, cmp, destinationFolder));
    }
}
