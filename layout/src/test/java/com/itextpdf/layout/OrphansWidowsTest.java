/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
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
        Paragraph widowsParagraph = new Paragraph(OrphansWidowsTestUtil.PARA_TEXT).setWidowsControl(widowsControl);

        IRenderer paragraphRenderer = widowsParagraph.createRendererSubTree().setParent(document.getRenderer());
        Rectangle effectiveArea = document.getPageEffectiveArea(pdfDocument.getDefaultPageSize());
        float linesHeight = OrphansWidowsTestUtil.calculateHeightForLinesNum(document, widowsParagraph,
                effectiveArea.getWidth(), overflowedToNextPageLinesNum, false);
        Rectangle layoutAreaRect = new Rectangle(effectiveArea)
                .setHeight(linesHeight + OrphansWidowsTestUtil.LINES_SPACE_EPS);
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

        ParagraphRenderer firstOverflowRenderer = (ParagraphRenderer) firstLayoutResult.getOverflowRenderer();
        ParagraphRenderer secondOverflowRenderer = (ParagraphRenderer) secondLayoutResult.getOverflowRenderer();
        Assert.assertNotNull(firstOverflowRenderer);
        Assert.assertNotNull(secondOverflowRenderer);
        List<IRenderer> firstOverflowRendererChildren = firstOverflowRenderer.getChildRenderers();
        List<IRenderer> secondOverflowRendererChildren = secondOverflowRenderer.getChildRenderers();
        Assert.assertNotNull(firstOverflowRendererChildren);
        Assert.assertNotNull(secondOverflowRendererChildren);
        Assert.assertEquals(firstOverflowRendererChildren.size(), secondOverflowRendererChildren.size());
    }

    @Test
    public void orphansWidowsAwareAndDirectLayoutProduceSameResult() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);
        int minAllowedWidows = 3;
        int overflowedToNextPageLinesNum = 5;

        Paragraph widowsParagraph = new Paragraph(OrphansWidowsTestUtil.PARA_TEXT);
        IRenderer paragraphRenderer = widowsParagraph.createRendererSubTree().setParent(document.getRenderer());
        Rectangle effectiveArea = document.getPageEffectiveArea(pdfDocument.getDefaultPageSize());
        float linesHeight = OrphansWidowsTestUtil.calculateHeightForLinesNum(document, widowsParagraph,
                effectiveArea.getWidth(), overflowedToNextPageLinesNum, false);
        Rectangle layoutAreaRect = new Rectangle(effectiveArea)
                .setHeight(linesHeight + OrphansWidowsTestUtil.LINES_SPACE_EPS);
        LayoutContext layoutContext = new LayoutContext(new LayoutArea(1, layoutAreaRect));
        LayoutResult noWidowsControlLayoutResult = paragraphRenderer.layout(layoutContext);

        ParagraphWidowsControl widowsControl = new ParagraphWidowsControl(minAllowedWidows, 1, false);
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

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT)})
    public void maxHeightLimitCausesOrphans() throws IOException, InterruptedException {
        runMaxHeightLimit("maxHeightLimitCausesOrphans", true);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT)})
    public void maxHeightLimitCausesWidows() throws IOException, InterruptedException {
        runMaxHeightLimit("maxHeightLimitCausesWidows", false);
    }

    @Test
    public void canvasHeightCausesOrphansViolation() throws IOException, InterruptedException {
        runCanvasSize("canvasHeightCausesOrphansViolation", true);
    }

    /* NOTE in this test the last possibly fitting line is removed as if to fix widows violation!
     * When the area is limited by highlevel conditions like paragraph's or div's size,
     * there's no attempt to fix orphans or widows. In this test case on the lowlevel canvas limitation
     * there is an attempt of fixing widows.
     */
    @Test
    public void canvasHeightCausesWidowsViolation() throws IOException, InterruptedException {
        runCanvasSize("canvasHeightCausesWidowsViolation", false);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT)})
    public void divSizeCausesOrphans() throws IOException, InterruptedException {
        runDivSize("divSizeCausesOrphans", true);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.CLIP_ELEMENT)})
    public void divSizeCausesWidows() throws IOException, InterruptedException {
        runDivSize("divSizeCausesWidows", false);
    }

    @Test
    public void keepTogetherOrphans() throws IOException, InterruptedException {
        runKeepTogether("keepTogetherOrphans", true, false);
    }

    @Test
    public void keepTogetherWidows() throws IOException, InterruptedException {
        runKeepTogether("keepTogetherWidows", false, false);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void keepTogetherLargeParagraphOrphans() throws IOException, InterruptedException {
        runKeepTogether("keepTogetherLargeParagraphOrphans", true, true);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void keepTogetherLargeParagraphWidows() throws IOException, InterruptedException {
        runKeepTogether("keepTogetherLargeParagraphWidows", false, true);
    }

    @Test
    public void inlineImageOrphans() throws IOException, InterruptedException {
        runInlineImage("inlineImageOrphans", true);
    }

    @Test
    public void inlineImageWidows() throws IOException, InterruptedException {
        runInlineImage("inlineImageWidows", false);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)})
    public void hugeInlineImageOrphans() throws IOException, InterruptedException {
        runHugeInlineImage("hugeInlineImageOrphans", true);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2),
            @LogMessage(messageTemplate = LogMessageConstant.WIDOWS_CONSTRAINT_VIOLATED)})
    public void hugeInlineImageWidows() throws IOException, InterruptedException {
        runHugeInlineImage("hugeInlineImageWidows", false);
    }

    @Test
    public void customParagraphAndRendererOrphans() throws IOException, InterruptedException {
        runCustomParagraphAndRendererTest("customParagraphAndRendererOrphans", true);
    }

    @Test
    public void customParagraphAndRendererWidows() throws IOException, InterruptedException {
        runCustomParagraphAndRendererTest("customParagraphAndRendererWidows", false);
    }

    @Test
    public void unexpectedlyWideNextArea() throws IOException, InterruptedException {
        runUnexpectedWidthOfNextAreaTest("unexpectedlyWideNextArea", true);
    }

    @Test
    public void unexpectedlyNarrowNextArea() throws IOException, InterruptedException {
        runUnexpectedWidthOfNextAreaTest("unexpectedlyNarrowNextArea", false);
    }

    @Test
    public void inlineBlockOrphans() throws IOException, InterruptedException {
        runInlineBlockTest("inlineBlockOrphans", true);
    }

    @Test
    public void inlineBlockWidows() throws IOException, InterruptedException {
        runInlineBlockTest("inlineBlockWidows", false);
    }

    @Test
    public void inlineFloatOrphans() throws IOException, InterruptedException {
        runInlineFloatTest("inlineFloatOrphans", true);
    }

    @Test
    public void inlineFloatWidows() throws IOException, InterruptedException {
        runInlineFloatTest("inlineFloatWidows", false);
    }

    @Test
    public void floatingDivOrphans() throws IOException, InterruptedException {
        runFloatingDiv("floatingDivOrphans", true);
    }

    @Test
    public void floatingDivWidows() throws IOException, InterruptedException {
        runFloatingDiv("floatingDivWidows", false);
    }

    @Test
    public void singleLineParagraphOrphans() throws IOException, InterruptedException {
        runOrphansWidowsBiggerThanLinesCount("singleLineParagraphOrphans", true, true);
    }

    @Test
    public void singleLineParagraphWidows() throws IOException, InterruptedException {
        runOrphansWidowsBiggerThanLinesCount("singleLineParagraphWidows", false, true);
    }

    @Test
    public void twoLinesParagraphMin3Orphans() throws IOException, InterruptedException {
        runOrphansWidowsBiggerThanLinesCount("twoLinesParagraphMin3Orphans", true, false);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.WIDOWS_CONSTRAINT_VIOLATED)})
    public void twoLinesParagraphMin3Widows() throws IOException, InterruptedException {
        runOrphansWidowsBiggerThanLinesCount("twoLinesParagraphMin3Widows", false, false);
    }

    @Test
    public void orphansAndWidowsTest() throws IOException, InterruptedException {
        runOrphansAndWidows("orphansAndWidowsTest");
    }

    @Test
    public void widowsControlOnPagesTest() throws IOException, InterruptedException {
        String testName = "widowsControlOnPagesTest";

        Paragraph testPara = new Paragraph();
        testPara.setWidowsControl(new ParagraphWidowsControl
                (3,1, true));

        runTestOnPage(testName, testPara, false);
    }

    @Test
    public void orphansControlOnPagesTest() throws IOException, InterruptedException {
        String testName = "orphansControlOnPagesTest";

        Paragraph testPara = new Paragraph();
        testPara.setOrphansControl(new ParagraphOrphansControl(3));

        runTestOnPage(testName, testPara, true);
    }

    private static void runMaxHeightLimit(String fileName, boolean orphans) throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsAndMaxHeightLimitTestCase(outPdf, orphans);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runCanvasSize(String fileName, boolean orphans) throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsOnCanvasOfLimitedSizeTestCase(outPdf, orphans);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runDivSize(String fileName, boolean orphans) throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsWithinDivOfLimitedSizeTestCase(outPdf, orphans);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runKeepTogether(String fileName, boolean orphans, boolean large)
            throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsKeepTogetherTestCase(outPdf, orphans, large);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runInlineImage(String fileName, boolean orphans) throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        String imagePath = sourceFolder + "bulb.gif";
        OrphansWidowsTestUtil.produceOrphansWidowsInlineImageTestCase(outPdf, imagePath, orphans);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runHugeInlineImage(String fileName, boolean orphans) throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        String imagePath = sourceFolder + "imageA4.png";
        OrphansWidowsTestUtil.produceOrphansWidowsHugeInlineImageTestCase(outPdf, imagePath, orphans);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runCustomParagraphAndRendererTest(String fileName, boolean orphans)
            throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        CustomParagraph customParagraph = new CustomParagraph();
        if (orphans) {
            customParagraph.setOrphansControl(new ParagraphOrphansControl(3));
        } else {
            customParagraph.setWidowsControl(new ParagraphWidowsControl(3, 1, false));
        }
        OrphansWidowsTestUtil.produceOrphansWidowsTestCase(outPdf, 2, orphans, customParagraph, false);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runUnexpectedWidthOfNextAreaTest(String fileName, boolean wide)
            throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsUnexpectedWidthOfNextAreaTestCase(outPdf, wide);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runInlineBlockTest(String fileName, boolean orphans)
            throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsInlineBlockTestCase(outPdf, orphans);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runInlineFloatTest(String fileName, boolean orphans)
            throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsInlineFloatTestCase(outPdf, orphans);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runFloatingDiv(String fileName, boolean orphans)
            throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsFloatingDivTestCase(outPdf, orphans);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runOrphansWidowsBiggerThanLinesCount(String fileName, boolean orphans, boolean singleLine)
            throws IOException, InterruptedException {
        String outPdf = destinationFolder + fileName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + fileName + ".pdf";
        OrphansWidowsTestUtil.produceOrphansWidowsBiggerThanLinesCountTestCase(outPdf, orphans, singleLine);
        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runMinThreeOrphansTest(String testName, int linesLeft)
            throws InterruptedException, IOException {
        runMinThreeOrphansTest(testName, linesLeft, false, false);
    }

    private static void runMinThreeOrphansTest(String testName, int linesLeft, boolean forcedPlacement,
            boolean marginCollapseTestCase) throws InterruptedException, IOException {
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

    private static void runMinThreeWidowsTest(String testName, int linesLeft, boolean forcedPlacement,
            boolean overflowParagraphOnViolation)
            throws IOException, InterruptedException {
        Paragraph testPara = new Paragraph();
        testPara.setWidowsControl(new ParagraphWidowsControl(3, 1, overflowParagraphOnViolation));
        if (forcedPlacement) {
            testPara.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
        }
        runTest(testName, linesLeft, false, testPara);
    }

    private static void runOrphansAndWidows(String testName)
            throws InterruptedException, IOException {
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";

        Paragraph testPara = new Paragraph();
        testPara
                .setOrphansControl(new ParagraphOrphansControl(3))
                .setWidowsControl(new ParagraphWidowsControl
                        (3, 1, true));

        OrphansWidowsTestUtil.produceOrphansAndWidowsTestCase(outPdf, testPara);

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runTestOnPage(String testName, Paragraph testPara, boolean orphans)
            throws InterruptedException, IOException {
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";

        int linesLeft = 1;

        OrphansWidowsTestUtil.produceOrphansOrWidowsTestCase(outPdf, linesLeft, orphans, testPara);

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static void runTest(String testName, int linesLeft, boolean orphans, Paragraph testPara)
            throws InterruptedException, IOException {
        runTest(testName, linesLeft, orphans, testPara, false);
    }

    private static void runTest(String testName, int linesLeft, boolean orphans, Paragraph testPara,
            boolean marginCollapseTestCase) throws InterruptedException, IOException {
        String outPdf = destinationFolder + testName + ".pdf";
        String cmpPdf = sourceFolder + "cmp_" + testName + ".pdf";

        OrphansWidowsTestUtil
                .produceOrphansWidowsTestCase(outPdf, linesLeft, orphans, testPara, marginCollapseTestCase);

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private static class CustomParagraphRenderer extends ParagraphRenderer {
        public CustomParagraphRenderer(CustomParagraph modelElement) {
            super(modelElement);
        }
    }

    private static class CustomParagraph extends Paragraph {
        @Override
        protected IRenderer makeNewRenderer() {
            return new CustomParagraphRenderer(this);
        }
    }
}
