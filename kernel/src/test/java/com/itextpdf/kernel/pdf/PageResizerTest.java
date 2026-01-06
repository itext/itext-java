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
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import org.junit.jupiter.api.*;

import java.io.IOException;

@Tag("IntegrationTest")
public class PageResizerTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/kernel/pdf/PageResizerTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/pdf/PageResizerTest/";

    @BeforeAll
    public static void setup() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void testPageResizeForTextOnlyDocumentResizer() throws IOException, InterruptedException {
        String inFileName = "simple_pdf.pdf";
        String outFileName =  "pageResizeForTextOnlyDocument.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
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

    @Test
    public void testPageResizeForRotatePage() throws IOException, InterruptedException {
        String inFileName = "singlePageDocumentWithRotation.pdf";
        String outFileName =  "pageResizeForRotatePage.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            PageResizer pageResizer = new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO);
            pageResizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testPageResizeAspectRatios() throws IOException, InterruptedException {
        String inFileName = "10PagesDocumentWithLeafs.pdf";
        String outFileName =  "pageResizeAspectRatios.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {

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

    @Test
    public void testGradients() throws IOException, InterruptedException {
        String inFileName = "gradient.pdf";
        String outFileName =  "gradient.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAnnotationBorder() throws IOException, InterruptedException {
        String inFileName = "annotationBorder.pdf";
        String outFileName =  "annotationBorder.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAnnotationCalloutLine() throws IOException, InterruptedException {
        String inFileName = "annotationCalloutLine.pdf";
        String outFileName =  "annotationCalloutLine.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAnnotationRichText() throws IOException, InterruptedException {
        String inFileName = "annotationRichText.pdf";
        String outFileName =  "annotationRichText.pdf";
        String outFileNameReverted = "annotationRichTextReverted.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
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

    @Test
    public void testAnnotationInkList() throws IOException, InterruptedException {
        String inFileName = "annotationInkList.pdf";
        String outFileName =  "annotationInkList.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAnnotationLineEndpoint() throws IOException, InterruptedException {
        String inFileName = "annotationLineEndpoint.pdf";
        String outFileName =  "annotationLineEndpoint.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAnnotationQuadpoints() throws IOException, InterruptedException {
        String inFileName = "annotationQuadpoints.pdf";
        String outFileName =  "annotationQuadpoints.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAnnotationRd() throws IOException, InterruptedException {
        String inFileName = "annotationRd.pdf";
        String outFileName =  "annotationRd.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAnnotationVertices() throws IOException, InterruptedException {
        String inFileName = "annotationVertices.pdf";
        String outFileName =  "annotationVertices.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testGradientsWithAspectRatio() throws IOException, InterruptedException {
        String inFileName = "gradient.pdf";
        String outFileName = "gradientAspect.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(PageSize.LEDGER,
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testGradientsWithAspect2Ratio() throws IOException, InterruptedException {
        String inFileName = "gradient.pdf";
        String outFileName = "gradientAspect2.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testGradientsType0Function() throws IOException, InterruptedException {
        String inFileName = "gradientFct0.pdf";
        String outFileName = "gradientFct0.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAcroFormResizeShrink() throws IOException, InterruptedException {
        String inFileName = "datasheet.pdf";
        String outFileName = "datasheetShrink.pdf";

        try ( PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAcroFormResizeGrow() throws IOException, InterruptedException {
        String inFileName = "datasheet.pdf";
        String outFileName = "datasheetGrow.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(PageSize.A3,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testAcroFormResizeStretch() throws IOException, InterruptedException {
        String inFileName = "datasheet.pdf";
        String outFileName = "datasheetStretch.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(PageSize.LEDGER,
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testGSManipulationPage() throws IOException, InterruptedException {
        String inFileName = "gsstackmanipulation.pdf";
        String outFileName = "gsstackmanipulation.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(PageSize.A6,
                    PageResizer.ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }


    @Test
    public void testHorizontalAnchoringLeft() throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "haLeft.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
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

    @Test
    public void testHorizontalAnchoringCenter() throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "haCenter.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            PageResizer resizer = new PageResizer(new PageSize(PageSize.A5.getHeight(), PageSize.A5.getWidth()),
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setHorizontalAnchorPoint(HorizontalAnchorPoint.CENTER);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testHorizontalAnchoringRight() throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "haRight.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            PageResizer resizer = new PageResizer(new PageSize(PageSize.A5.getHeight(), PageSize.A5.getWidth()),
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setHorizontalAnchorPoint(HorizontalAnchorPoint.RIGHT);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testVerticalAnchoringTop() throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "vaTop.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
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

    @Test
    public void testVerticalAnchoringCenter() throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "vaCenter.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            PageResizer resizer = new PageResizer(PageSize.A4,
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.CENTER);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testVerticalAnchoringBottom() throws IOException, InterruptedException {
        String inFileName = "squareSource.pdf";
        String outFileName = "vaBottom.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            PageResizer resizer = new PageResizer(PageSize.A4,
                    ResizeType.MAINTAIN_ASPECT_RATIO);
            resizer.setVerticalAnchorPoint(VerticalAnchorPoint.BOTTOM);
            resizer.resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testFormFieldsDA() throws IOException, InterruptedException {
        String inFileName = "formFieldsDA.pdf";
        String outFileName = "formFieldsDA.pdf";
        String outFileNameReverted = "formFieldsDAReverted.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
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

    @Test
    public void annotationsRightAnchoringTest() throws IOException, InterruptedException {
        String[] pdfFiles = new String[]{"annotationVertices.pdf", "annotationBorder.pdf",
                "annotationQuadpoints.pdf", "annotationRd.pdf"};
        for (String pdfFileName : pdfFiles) {
            String outPdf = pdfFileName.substring(0, pdfFileName.length() - 4) + "Right.pdf";
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + pdfFileName),
                    CompareTool.createTestPdfWriter(DESTINATION_FOLDER + outPdf))) {
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

    @Test
    public void annotationsTopAnchoringTest() throws IOException, InterruptedException {
        String[] pdfFiles = new String[]{"annotationVertices.pdf", "annotationBorder.pdf",
                "annotationQuadpoints.pdf", "annotationRd.pdf"};
        for (String pdfFileName : pdfFiles) {
            String outPdf = pdfFileName.substring(0, pdfFileName.length() - 4) + "Top.pdf";
            try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + pdfFileName),
                    CompareTool.createTestPdfWriter(DESTINATION_FOLDER + outPdf))) {
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


    @Test
    public void testPdfASignatureFieldDefault() throws IOException, InterruptedException {
        String inFileName = "pdfASignatureFieldDefault.pdf";
        String outFileName = "pdfASignatureFieldDefault.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    PageResizer.ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfASignatureFieldAspect() throws IOException, InterruptedException {
        String inFileName = "pdfASignatureFieldAspect.pdf";
        String outFileName = "pdfASignatureFieldAspect.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfAFormFieldsDefault() throws IOException, InterruptedException {
        String inFileName = "pdfAFormFieldsDefault.pdf";
        String outFileName = "pdfAFormFieldsDefault.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfAFormFieldsAspect() throws IOException, InterruptedException {
        String inFileName = "pdfAFormFieldsAspect.pdf";
        String outFileName = "pdfAFormFieldsAspect.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfUA1ButtonDefault() throws IOException, InterruptedException {
        String inFileName = "pdfUA1ButtonDefault.pdf";
        String outFileName = "pdfUA1ButtonDefault.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfUA1ButtonAspect() throws IOException, InterruptedException {
        String inFileName = "pdfUA1ButtonAspect.pdf";
        String outFileName = "pdfUA1ButtonAspect.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfUA2RadioButtonDefault() throws IOException, InterruptedException {
        String inFileName = "pdfUA2RadioButtonDefault.pdf";
        String outFileName = "pdfUA2RadioButtonDefault.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfUA2RadioButtonAspect() throws IOException, InterruptedException {
        String inFileName = "pdfUA2RadioButtonAspect.pdf";
        String outFileName = "pdfUA2RadioButtonAspect.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfUA1SignatureField() throws IOException, InterruptedException {
        String inFileName = "pdfUA1SignatureField.pdf";
        String outFileName = "pdfUA1SignatureField.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testPdfUA2SignatureField() throws IOException, InterruptedException {
        String inFileName = "pdfUA2SignatureField.pdf";
        String outFileName = "pdfUA2SignatureField.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
        Assertions.assertNull(new VeraPdfValidator().validate(DESTINATION_FOLDER + outFileName)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }

    @Test
    public void testNestedForms() throws IOException, InterruptedException {
        String inFileName = "nestedForms.pdf";
        String outFileName = "nestedForms.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testNestedMixedXObjectsDefault() throws IOException, InterruptedException {
        String inFileName = "nestedMixedXObjectsDefault.pdf";
        String outFileName = "nestedMixedXObjectsDefault.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testNestedMixedXObjectsAspect() throws IOException, InterruptedException {
        String inFileName = "nestedMixedXObjectsAspect.pdf";
        String outFileName = "nestedMixedXObjectsAspect.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testImageDefault() throws IOException, InterruptedException {
        String inFileName = "imageDefault.pdf";
        String outFileName = "imageDefault.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.DEFAULT).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void testImageAspect() throws IOException, InterruptedException {
        String inFileName = "imageAspect.pdf";
        String outFileName = "imageAspect.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(SOURCE_FOLDER + inFileName),
                new PdfWriter(DESTINATION_FOLDER + outFileName))) {
            new PageResizer(new PageSize(PageSize.A4.getWidth()/2,PageSize.A4.getHeight()),
                    ResizeType.MAINTAIN_ASPECT_RATIO).resize(pdfDocument.getPage(1));
        }
        Assertions.assertNull(new CompareTool()
                .compareByContent(DESTINATION_FOLDER + outFileName,
                        SOURCE_FOLDER + "cmp_" + outFileName, DESTINATION_FOLDER, "diff"));
    }
}
