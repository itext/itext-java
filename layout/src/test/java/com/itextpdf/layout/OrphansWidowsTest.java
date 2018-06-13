package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.ParagraphOrphansControl;
import com.itextpdf.layout.property.ParagraphWidowsControl;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;
import com.itextpdf.layout.testutil.OrphansWidowsTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OrphansWidowsTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/OrphansWidowsTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/OrphansWidowsTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PREMATURE_CALL_OF_HANDLE_VIOLATION_METHOD)})
    public void prematureCallOfHandleViolatedOrphans() {
        ParagraphOrphansControl orphansControl = new ParagraphOrphansControl(2);
        orphansControl.handleViolatedOrphans(new ParagraphRenderer(new Paragraph()), "");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.PREMATURE_CALL_OF_HANDLE_VIOLATION_METHOD)})
    public void prematureCallOfHandleViolatedWidows() {
        ParagraphWidowsControl widowsControl = new ParagraphWidowsControl(2, 1, false);
        widowsControl.handleViolatedWidows(new ParagraphRenderer(new Paragraph()), "");
    }

    @Test
    public void min3OrphansTest01LeftLines1() throws IOException, InterruptedException {
        runMinThreeOrphansTest("min3OrphansTest01LeftLines1", 1);
    }

    @Test
    public void min3OrphansTest01LeftLines2() throws IOException, InterruptedException {
        runMinThreeOrphansTest("min3OrphansTest01LeftLines2", 2);
    }

    @Test
    public void min3OrphansTest01LeftLines3() throws IOException, InterruptedException {
        runMinThreeOrphansTest("min3OrphansTest01LeftLines3", 3);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ORPHANS_CONSTRAINT_VIOLATED)})
    public void violatedOrphans() throws IOException, InterruptedException {
        runMinThreeOrphansTest("violatedOrphans", 1, true, false);
    }

    @Test
    public void min3WidowsTest01LeftLines1() throws IOException, InterruptedException {
        runMinThreeWidowsTest("min3WidowsTest01LeftLines1", 1);
    }

    @Test
    public void min3WidowsTest01LeftLines2() throws IOException, InterruptedException {
        runMinThreeWidowsTest("min3WidowsTest01LeftLines2", 2);
    }

    @Test
    public void min3WidowsTest01LeftLines3() throws IOException, InterruptedException {
        runMinThreeWidowsTest("min3WidowsTest01LeftLines3", 3);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.WIDOWS_CONSTRAINT_VIOLATED)})
    public void violatedWidowsInabilityToFix() throws IOException, InterruptedException {
        runMinThreeWidowsTest("violatedWidowsInabilityToFix", 1, false, false);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.WIDOWS_CONSTRAINT_VIOLATED)})
    public void violatedWidowsForcedPlacement() throws IOException, InterruptedException {
        runMinThreeWidowsTest("violatedWidowsForcedPlacement", 1, true, true);
    }

    @Test
    public void marginCollapseAndOrphansRestriction() throws IOException, InterruptedException {
        runMinThreeOrphansTest("marginCollapseAndOrphansRestriction", 3, false, true);
    }

    @Test
    public void multipleLayoutCallsProduceSameLayoutResult() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);
        int minAllowedWidows = 5;
        int overflowedToNextPageLinesNum = 2;
        ParagraphWidowsControl widowsControl = new ParagraphWidowsControl(minAllowedWidows,
                minAllowedWidows - overflowedToNextPageLinesNum, false);
        Paragraph widowsParagraph = new Paragraph(OrphansWidowsTestUtil.paraText).setWidowsControl(widowsControl);

        IRenderer paragraphRenderer = widowsParagraph.createRendererSubTree().setParent(document.getRenderer());
        Rectangle effectiveArea = document.getPageEffectiveArea(pdfDocument.getDefaultPageSize());
        float linesHeight = OrphansWidowsTestUtil.calculateHeightForLinesNum(document, widowsParagraph,
                effectiveArea.getWidth(), overflowedToNextPageLinesNum, false);
        Rectangle layoutAreaRect = new Rectangle(effectiveArea).setHeight(linesHeight + 5);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(1, layoutAreaRect));
        LayoutResult firstLayoutResult = paragraphRenderer.layout(layoutContext);
        LayoutResult secondLayoutResult = paragraphRenderer.layout(layoutContext);

        // toString() comparison is used since it contains report on status, areaBreak and occupiedArea
        Assert.assertEquals(firstLayoutResult.toString(), secondLayoutResult.toString());
        ParagraphRenderer firstSplitRenderer = (ParagraphRenderer) firstLayoutResult.getSplitRenderer();
        ParagraphRenderer secondSplitRenderer = (ParagraphRenderer) secondLayoutResult.getSplitRenderer();
        Assert.assertNotNull(firstSplitRenderer);
        Assert.assertNotNull(secondSplitRenderer);
        Assert.assertEquals(firstSplitRenderer.toString(), secondSplitRenderer.toString());
        Assert.assertNotNull(firstLayoutResult.getOverflowRenderer());
        Assert.assertNotNull(secondLayoutResult.getOverflowRenderer());
    }

    @Test
    public void orphansWidowsAwareAndDirectLayoutProduceSameResult() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);
        int minAllowedWidows = 3;
        int overflowedToNextPageLinesNum = 5;

        Paragraph widowsParagraph = new Paragraph(OrphansWidowsTestUtil.paraText);
        IRenderer paragraphRenderer = widowsParagraph.createRendererSubTree().setParent(document.getRenderer());
        Rectangle effectiveArea = document.getPageEffectiveArea(pdfDocument.getDefaultPageSize());
        float linesHeight = OrphansWidowsTestUtil.calculateHeightForLinesNum(document, widowsParagraph,
                effectiveArea.getWidth(), overflowedToNextPageLinesNum, false);
        Rectangle layoutAreaRect = new Rectangle(effectiveArea).setHeight(linesHeight + 5);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(1, layoutAreaRect));
        LayoutResult noWidowsControlLayoutResult = paragraphRenderer.layout(layoutContext);

        ParagraphWidowsControl widowsControl = new ParagraphWidowsControl(minAllowedWidows,1, false);
        widowsParagraph.setWidowsControl(widowsControl);
        LayoutResult widowsControlLayoutResult = paragraphRenderer.layout(layoutContext);

        // toString() comparison is used since it contains report on status, areaBreak and occupiedArea
        Assert.assertEquals(noWidowsControlLayoutResult.toString(), widowsControlLayoutResult.toString());
        ParagraphRenderer firstSplitRenderer = (ParagraphRenderer) noWidowsControlLayoutResult.getSplitRenderer();
        ParagraphRenderer secondSplitRenderer = (ParagraphRenderer) widowsControlLayoutResult.getSplitRenderer();
        Assert.assertNotNull(firstSplitRenderer);
        Assert.assertNotNull(secondSplitRenderer);
        Assert.assertEquals(firstSplitRenderer.toString(), secondSplitRenderer.toString());
        Assert.assertNotNull(noWidowsControlLayoutResult.getOverflowRenderer());
        Assert.assertNotNull(widowsControlLayoutResult.getOverflowRenderer());
    }

    private static void runMinThreeOrphansTest(String testName, int linesLeft) throws InterruptedException, IOException {
        runMinThreeOrphansTest(testName, linesLeft, false, false);
    }

    private static void runMinThreeOrphansTest(String testName, int linesLeft, boolean forcedPlacement, boolean marginCollapseTestCase) throws InterruptedException, IOException {
        Paragraph testPara = new Paragraph();
        testPara.setOrphansControl(new ParagraphOrphansControl(3));
        if (forcedPlacement) {
            testPara.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
        }

        runTest(testName, linesLeft, true, testPara, marginCollapseTestCase);
    }

    private static void runMinThreeWidowsTest(String testName, int linesLeft) throws InterruptedException, IOException {
        runMinThreeWidowsTest(testName, linesLeft, false, true);
    }

    private static void runMinThreeWidowsTest(String testName, int linesLeft, boolean forcedPlacement, boolean overflowParagraphOnViolation)
            throws IOException, InterruptedException {
        Paragraph testPara = new Paragraph();
        testPara.setWidowsControl(new ParagraphWidowsControl(3, 1, overflowParagraphOnViolation));
        if (forcedPlacement) {
            testPara.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
        }
        runTest(testName, linesLeft, false, testPara);
    }

    private static void runTest(String testName, int linesLeft, boolean orphans, Paragraph testPara) throws InterruptedException, IOException {
        runTest(testName, linesLeft, orphans, testPara, false);
    }

    private static void runTest(String testName, int linesLeft, boolean orphans, Paragraph testPara, boolean marginCollapseTestCase) throws InterruptedException, IOException {
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";

        OrphansWidowsTestUtil.produceOrphansWidowsTestCase(outPdf, linesLeft, orphans, testPara, marginCollapseTestCase);

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }
}
