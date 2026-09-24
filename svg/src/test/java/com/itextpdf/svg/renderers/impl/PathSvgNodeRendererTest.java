/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
    Authors: Apryse Software.

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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.styledxmlparser.node.impl.jsoup.JsoupXmlParser;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.DefaultSvgProcessor;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.IBranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PathSvgNodeRendererTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/PathSvgNodeRendererTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/PathSvgNodeRendererTest/";
    private ISvgConverterProperties properties;

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void before() {
        properties = new SvgConverterProperties()
                .setBaseUri(SOURCE_FOLDER);
    }
    @Test
    public void pathNodeRendererMoveToTest() throws IOException, InterruptedException {
        String filename = "pathNodeRendererMoveToTest.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put("d", "M 100,100, L300,100,L200,300,z");

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles(pathShapes);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        pathRenderer.draw(context);
        doc.close();

        String result = new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER, "diff_");

        if (result != null && ! result.contains("No visual differences")) {
            Assertions.fail(result);
        }
    }

    @Test
    public void pathNodeRendererMoveToTest1() throws IOException, InterruptedException {
        String filename = "pathNodeRendererMoveToTest1.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put("d", "M 100 100 l300 100 L200 300 z");

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles(pathShapes);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        pathRenderer.draw(context);
        doc.close();

        String result = new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER, "diff_");

        if (result != null && ! result.contains("No visual differences")) {
            Assertions.fail(result);
        }
    }

    @Test
    public void pathNodeRendererCurveToTest() throws IOException, InterruptedException {
        String filename = "pathNodeRendererCurveToTest.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put("d", "M100,200 C100,100 250,100 250,200 S400,300 400,200,z");

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles(pathShapes);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        pathRenderer.draw(context);
        doc.close();
        String result = new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER, "diff_");

        if (result != null && ! result.contains("No visual differences")) {
            Assertions.fail(result);
        }
    }

    @Test
    public void pathNodeRendererCurveToTest1() throws IOException, InterruptedException {
        String filename = "pathNodeRendererCurveToTest1.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put("d", "M100 200 C100 300 250 300 250 200 S400 100 400 200 z");

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles(pathShapes);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        pathRenderer.draw(context);
        doc.close();

        String result = new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER, "diff_");

        if (result != null && ! result.contains("No visual differences")) {
            Assertions.fail(result);
        }
    }

    @Test
    public void pathNodeRendererQCurveToCurveToTest() throws IOException, InterruptedException {
        String filename = "pathNodeRendererQCurveToCurveToTest.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put("d", "M200,300 Q400,50 600,300,z");

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles(pathShapes);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        pathRenderer.draw(context);
        doc.close();
        String result = new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER, "diff_");

        if (result != null && ! result.contains("No visual differences")) {
            Assertions.fail(result);
        }
    }

    @Test
    public void pathNodeRendererQCurveToCurveToTest1() throws IOException, InterruptedException {
        String filename = "pathNodeRendererQCurveToCurveToTest1.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        Map<String, String> pathShapes = new HashMap<String, String>();
        pathShapes.put("d", "M200 300 Q400 50 600 300 z");

        ISvgNodeRenderer pathRenderer = new PathSvgNodeRenderer();
        pathRenderer.setAttributesAndStyles(pathShapes);

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        pathRenderer.draw(context);
        doc.close();
        String result = new CompareTool().compareByContent(DESTINATION_FOLDER + filename, SOURCE_FOLDER + "cmp_" + filename, DESTINATION_FOLDER, "diff_");

        if (result != null && ! result.contains("No visual differences")) {
            Assertions.fail(result);
        }
    }

    @Test
    public void smoothCurveTest1() throws IOException {
        String filename = "smoothCurveTest1.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        String svgFilename = "smoothCurveTest1.svg";
        InputStream xmlStream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + svgFilename);
        IElementNode rootTag = new JsoupXmlParser().parse(xmlStream, "ISO-8859-1");

        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        IBranchSvgNodeRenderer root = (IBranchSvgNodeRenderer) processor.process(rootTag, null).getRootRenderer();

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        Assertions.assertTrue(root.getChildren().get(0) instanceof PathSvgNodeRenderer);
        root.getChildren().get(0).draw(context);
        doc.close();
    }

    @Test
    public void smoothCurveTest2() throws IOException {
        String filename = "smoothCurveTest2.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        String svgFilename = "smoothCurveTest2.svg";
        InputStream xmlStream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + svgFilename);
        IElementNode rootTag = new JsoupXmlParser().parse(xmlStream, "ISO-8859-1");

        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        IBranchSvgNodeRenderer root = (IBranchSvgNodeRenderer) processor.process(rootTag, null).getRootRenderer();

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        Assertions.assertTrue(root.getChildren().get(0) instanceof PathSvgNodeRenderer);
        root.getChildren().get(0).draw(context);
        doc.close();
    }

    @Test
    public void smoothCurveTest3() throws IOException {
        String filename = "smoothCurveTest3.pdf";
        PdfDocument doc = new PdfDocument(CompareTool.createTestPdfWriter(DESTINATION_FOLDER + filename));
        doc.addNewPage();

        String svgFilename = "smoothCurveTest3.svg";
        InputStream xmlStream = FileUtil.getInputStreamForFile(SOURCE_FOLDER + svgFilename);
        IElementNode rootTag = new JsoupXmlParser().parse(xmlStream, "ISO-8859-1");

        DefaultSvgProcessor processor = new DefaultSvgProcessor();
        IBranchSvgNodeRenderer root = (IBranchSvgNodeRenderer) processor.process(rootTag, null).getRootRenderer();

        SvgDrawContext context = new SvgDrawContext(null, null);
        PdfCanvas cv = new PdfCanvas(doc, 1);
        context.pushCanvas(cv);
        Assertions.assertTrue(root.getChildren().get(0) instanceof PathSvgNodeRenderer);
        root.getChildren().get(0).draw(context);
        doc.close();
    }

    @Test
    public void pathNodeRendererCurveComplexTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "curves");
    }

    @Test
    public void pathZOperatorMultipleZTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathZOperatorMultipleZTest");
    }

    @Test
    public void pathZOperatorSingleZTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathZOperatorSingleZTest");
    }

    @Test
    public void pathZOperatorSingleZInstructionsAfterTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathZOperatorSingleZInstructionsAfterTest");
    }

    @Test
    public void invalidZOperatorTest() throws IOException, InterruptedException {
        Assertions.assertThrows(SvgProcessingException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "invalidZOperatorTest01")
        );
    }

    @Test
    public void invalidOperatorTest() throws IOException, InterruptedException {
        Assertions.assertThrows(SvgProcessingException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "invalidOperatorTest01")
        );
    }

    @Test
    public void pathLOperatorMultipleCoordinates() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathLOperatorMultipleCoordinates");
    }

    @Test
    public void pathVOperatorTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathVOperatorTest01");
    }

    @Test
    public void pathZOperatorContinuePathingTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathZOperatorContinuePathingTest");
    }

    @Test
    public void pathVOperatorMultipleArgumentsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathVOperatorMultipleArgumentsTest");
    }

    @Test
    public void pathHOperatorSimpleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathHOperatorSimpleTest");
    }

    @Test
    public void pathHandVOperatorTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathHandVOperatorTest");
    }

    @Test
    public void curveToContinuePathingTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "curveToContinuePathingTest");
    }

    @Test
    public void relativeHorizontalLineToTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativeHorizontalLineTo");
    }

    @Test
    public void relativeVerticalLineToTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativeVerticalLineTo");
    }

    @Test
    public void combinedRelativeVerticalLineToAndRelativeHorizontalLineToTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "combinedRelativeVerticalLineToAndRelativeHorizontalLineTo");
    }

    @Test
    public void multipleRelativeHorizontalLineToTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleRelativeHorizontalLineTo");
    }

    @Test
    public void multipleRelativeVerticalLineToTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "multipleRelativeVerticalLineTo");
    }

    @Test
    public void moveToRelativeMultipleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "moveToRelativeMultiple");
    }

    @Test
    public void moveToAbsoluteMultipleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "moveToAbsoluteMultiple");
    }

    @Test
    public void iTextLogoTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "iTextLogo");
    }

    @Test
    public void eofillUnsuportedPathTest() throws IOException, InterruptedException {
        Assertions.assertThrows(SvgProcessingException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "eofillUnsuportedPathTest")
        );
    }

    @Test
    public void multiplePairsAfterMoveToRelativeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "multiplePairsAfterMoveToRelative");
    }

    @Test
    public void multiplePairsAfterMoveToAbsoluteTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "multiplePairsAfterMoveToAbsolute");
    }

    @Test
    public void pathHOperatorAbsoluteAfterMultiplePairsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathHOperatorAbsoluteAfterMultiplePairs");
    }

    @Test
    public void pathHOperatorRelativeAfterMultiplePairsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathHOperatorRelativeAfterMultiplePairs");
    }

    @Test
    public void patternXlinkTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHref");
    }

    @Test
    public void patternXlinkHrefPatternContentUnits1Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefPatternContentUnits1");
    }

    @Test
    public void patternXlinkHrefPatternContentUnits2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefPatternContentUnits2");
    }

    @Test
    public void patternXlinkHrefPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefPatternUnits");
    }

    @Test
    public void patternXlinkHrefPreserveAR1Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefPreserveAR1", properties);
    }

    @Test
    public void patternXlinkHrefPreserveAR2Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefPreserveAR2", properties);
    }

    @Test
    public void patternHrefTransitivePatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefTransitivePatternUnits");
    }

    @Test
    public void patternHrefTransitivePCUTopLayerTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefTransitivePCUTopLayer");
    }

    @Test
    public void patternHrefTransitivePCUBottomLayerTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefTransitivePCUBottomLayer");
    }

    @Test
    public void patternHrefTransitivePCU2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefTransitivePCU2");
    }

    @Test
    public void patternHrefTransitivePresAR1Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefTransitivePresAR1", properties);
    }

    @Test
    public void patternHrefTransitivePresAR2Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "patternHrefTransitivePresAR2", properties);
    }

    @Test
    public void closedPathIsCutTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "closedPathIsCutTest", properties);
    }
}
