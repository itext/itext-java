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
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PageResizer.HorizontalAnchorPoint;
import com.itextpdf.kernel.pdf.PageResizer.ResizeType;
import com.itextpdf.kernel.pdf.PageResizer.VerticalAnchorPoint;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import java.util.Arrays;
import java.util.Collection;

import java.io.IOException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PageResizerTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/kernel/pdf/PageResizerTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PageResizerTest/";

    private static Collection<Object[]> appendModes() {
        return Arrays.asList(new Object[][]{
                {true},
                {false}
        });
    }

    @BeforeAll
    public static void setup() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPageResizeForTextOnlyDocumentResizer(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "simple_pdf.pdf";
        String outFileName =  "pageResizeForTextOnlyDocument.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer firstPageResizer = new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO);
            firstPageResizer.resize(pdfDocument.getPage(1));
            PageResizer secondPageResizer = new PageResizer(new PageSize(298, 120),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO);
            secondPageResizer.resize(pdfDocument.getPage(2));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPageResizeForRotatePage(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "singlePageDocumentWithRotation.pdf";
        String outFileName =  "pageResizeForRotatePage.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer pageResizer = new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO);
            pageResizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPageResizeAspectRatios(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "10PagesDocumentWithLeafs.pdf";
        String outFileName =  "pageResizeAspectRatios.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {

            new PageResizer(PageSize.A6, PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
            new PageResizer(PageSize.EXECUTIVE, PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(2));
            new PageResizer(PageSize.EXECUTIVE, PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(3));
            new PageResizer(PageSize.LEGAL, PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(4));
            new PageResizer(PageSize.LEGAL, PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(5));
            new PageResizer(PageSize.LEDGER, PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(6));
            new PageResizer(PageSize.LEDGER, PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(7));
            new PageResizer(PageSize.LETTER, PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(8));
            new PageResizer(PageSize.LETTER, PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(9));
            new PageResizer(new PageSize(PageSize.LEDGER.getWidth() * 2, PageSize.LEDGER.getHeight()*2),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(10));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testGradients(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "gradient.pdf";
        String outFileName =  "gradient.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAnnotationBorder(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "annotationBorder.pdf";
        String outFileName =  "annotationBorder.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAnnotationCalloutLine(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "annotationCalloutLine.pdf";
        String outFileName =  "annotationCalloutLine.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAnnotationRichText(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "annotationRichText.pdf";
        String outFileName =  "annotationRichText.pdf";
        String outFileNameReverted = "annotationRichTextReverted.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));

        // Reverting
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + outFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileNameReverted))) {
            PageResizer resizer = new PageResizer(new PageSize(PageSize.A4), ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileNameReverted,
                        SOURCE_FOLDER + "cmp_" + outFileNameReverted, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAnnotationInkList(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "annotationInkList.pdf";
        String outFileName =  "annotationInkList.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAnnotationLineEndpoint(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "annotationLineEndpoint.pdf";
        String outFileName =  "annotationLineEndpoint.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAnnotationQuadpoints(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "annotationQuadpoints.pdf";
        String outFileName =  "annotationQuadpoints.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAnnotationRd(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "annotationRd.pdf";
        String outFileName =  "annotationRd.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAnnotationVertices(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "annotationVertices.pdf";
        String outFileName =  "annotationVertices.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testGradientsWithAspectRatio(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "gradient.pdf";
        String outFileName = "gradientAspect.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(PageSize.LEDGER,
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testGradientsWithAspect2Ratio(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "gradient.pdf";
        String outFileName = "gradientAspect2.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testGradientsType0Function(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "gradientFct0.pdf";
        String outFileName = "gradientFct0.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAcroFormResizeShrink(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "datasheet.pdf";
        String outFileName = "datasheetShrink.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try ( PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAcroFormResizeGrow(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "datasheet.pdf";
        String outFileName = "datasheetGrow.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(PageSize.A3,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testAcroFormResizeStretch(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "datasheet.pdf";
        String outFileName = "datasheetStretch.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(PageSize.LEDGER,
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testGSManipulationPage(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "gsstackmanipulation.pdf";
        String outFileName = "gsstackmanipulation.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testHorizontalAnchoringLeft(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "haLeft.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer resizer = new PageResizer(new PageSize(PageSize.A5.getHeight(), PageSize.A5.getWidth()),
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setHorizontalAnchorPoint(HorizontalAnchorPoint.LEFT);
            Assertions.assertEquals(HorizontalAnchorPoint.LEFT, resizer.getHorizontalAnchorPoint());
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testHorizontalAnchoringCenter(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "haCenter.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer resizer = new PageResizer(new PageSize(PageSize.A5.getHeight(), PageSize.A5.getWidth()),
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setHorizontalAnchorPoint(HorizontalAnchorPoint.CENTER);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testHorizontalAnchoringRight(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "haRight.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer resizer = new PageResizer(new PageSize(PageSize.A5.getHeight(), PageSize.A5.getWidth()),
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setHorizontalAnchorPoint(HorizontalAnchorPoint.RIGHT);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testVerticalAnchoringTop(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "vaTop.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer resizer = new PageResizer(PageSize.A4,
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.TOP);
            Assertions.assertEquals(VerticalAnchorPoint.TOP, resizer.getVerticalAnchorPoint());
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testVerticalAnchoringCenter(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "vaCenter.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer resizer = new PageResizer(PageSize.A4,
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.CENTER);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testVerticalAnchoringBottom(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "vaBottom.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer resizer = new PageResizer(PageSize.A4,
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.BOTTOM);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testFormFieldsDA(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "formFieldsDA.pdf";
        String outFileName = "formFieldsDA.pdf";
        String outFileNameReverted = "formFieldsDAReverted.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            PageResizer resizer = new PageResizer(new PageSize(1200, 1200), ResizeType.DEFAULT);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.BOTTOM);
            resizer.resize(pdfDocument.getPage(1));

            resizer = new PageResizer(new PageSize(1200, 1200), ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.BOTTOM);
            resizer.resize(pdfDocument.getPage(2));

            resizer = new PageResizer(new PageSize(400, 400), ResizeType.DEFAULT);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.BOTTOM);
            resizer.resize(pdfDocument.getPage(3));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));

        // Reverting
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + outFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileNameReverted))) {
            PageResizer resizer = new PageResizer(new PageSize(PageSize.A4), ResizeType.DEFAULT);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.BOTTOM);
            resizer.resize(pdfDocument.getPage(1));

            resizer = new PageResizer(new PageSize(PageSize.A4), ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.BOTTOM);
            resizer.resize(pdfDocument.getPage(2));

            resizer = new PageResizer(new PageSize(PageSize.A4), ResizeType.DEFAULT);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.BOTTOM);
            resizer.resize(pdfDocument.getPage(3));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileNameReverted,
                        SOURCE_FOLDER + "cmp_" + outFileNameReverted, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void annotationsRightAnchoringTest(boolean appendMode) throws IOException, InterruptedException {
        String[] pdfFiles = new String[]{"annotationVertices.pdf", "annotationBorder.pdf",
                "annotationQuadpoints.pdf", "annotationRd.pdf"};

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        for (String pdfFileName : pdfFiles) {
            String outPdf = pdfFileName.substring(0, pdfFileName.length() - 4) + "Right.pdf";
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + pdfFileName),
                    CompareTool.createTestPdfWriter(DESTINATION_FOLDER + outPdf), props)) {
                PageResizer pr = new PageResizer(new PageSize(PageSize.A4.getWidth() * 2, PageSize.A4.getHeight()),
                        PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO);
                pr.setVerticalAnchorPoint(VerticalAnchorPoint.CENTER);
                pr.setHorizontalAnchorPoint(HorizontalAnchorPoint.RIGHT);
                pr.resize(pdfDocument.getPage(1));
            }

            Assertions.assertNull(new CompareTool()
                    .compareByContent(DESTINATION_FOLDER + outPdf,
                            SOURCE_FOLDER + "cmp_" + outPdf, DESTINATION_FOLDER, "diff"));
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void annotationsTopAnchoringTest(boolean appendMode) throws IOException, InterruptedException {
        String[] pdfFiles = new String[]{"annotationVertices.pdf", "annotationBorder.pdf",
                "annotationQuadpoints.pdf", "annotationRd.pdf"};

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        for (String pdfFileName : pdfFiles) {
            String outPdf = pdfFileName.substring(0, pdfFileName.length() - 4) + "Top.pdf";
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + pdfFileName),
                    CompareTool.createTestPdfWriter(DESTINATION_FOLDER + outPdf), props)) {
                PageResizer pr = new PageResizer(new PageSize(PageSize.A4.getWidth(), PageSize.A4.getHeight() * 2),
                        PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO);
                pr.setVerticalAnchorPoint(VerticalAnchorPoint.TOP);
                pr.setHorizontalAnchorPoint(HorizontalAnchorPoint.CENTER);
                pr.resize(pdfDocument.getPage(1));
            }

            Assertions.assertNull(new CompareTool()
                    .compareByContent(DESTINATION_FOLDER + outPdf,
                            SOURCE_FOLDER + "cmp_" + outPdf, DESTINATION_FOLDER, "diff"));
        }
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfASignatureFieldDefault(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfASignatureFieldDefault.pdf";
        String outFileName = "pdfASignatureFieldDefault.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfASignatureFieldAspect(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfASignatureFieldAspect.pdf";
        String outFileName = "pdfASignatureFieldAspect.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfAFormFieldsDefault(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfAFormFieldsDefault.pdf";
        String outFileName = "pdfAFormFieldsDefault.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfAFormFieldsAspect(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfAFormFieldsAspect.pdf";
        String outFileName = "pdfAFormFieldsAspect.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfUA1ButtonDefault(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfUA1ButtonDefault.pdf";
        String outFileName = "pdfUA1ButtonDefault.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfUA1ButtonAspect(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfUA1ButtonAspect.pdf";
        String outFileName = "pdfUA1ButtonAspect.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfUA2RadioButtonDefault(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfUA2RadioButtonDefault.pdf";
        String outFileName = "pdfUA2RadioButtonDefault.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfUA2RadioButtonAspect(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfUA2RadioButtonAspect.pdf";
        String outFileName = "pdfUA2RadioButtonAspect.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfUA1SignatureField(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfUA1SignatureField.pdf";
        String outFileName = "pdfUA1SignatureField.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testPdfUA2SignatureField(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "pdfUA2SignatureField.pdf";
        String outFileName = "pdfUA2SignatureField.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); 
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testNestedForms(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "nestedForms.pdf";
        String outFileName = "nestedForms.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testNestedMixedXObjectsDefault(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "nestedMixedXObjectsDefault.pdf";
        String outFileName = "nestedMixedXObjectsDefault.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testNestedMixedXObjectsAspect(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "nestedMixedXObjectsAspect.pdf";
        String outFileName = "nestedMixedXObjectsAspect.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testImageDefault(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "imageDefault.pdf";
        String outFileName = "imageDefault.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("appendModes")
    public void testImageAspect(boolean appendMode) throws IOException, InterruptedException {
        String inFileName = "imageAspect.pdf";
        String outFileName = "imageAspect.pdf";

        StampingProperties props = new StampingProperties();
        if (appendMode) {
            props.useAppendMode();
        }
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName), props)) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }
}
