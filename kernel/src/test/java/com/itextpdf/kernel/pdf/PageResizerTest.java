/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
        String outFileName =  "testPageResizeForTextOnlyDocument.pdf";

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
        String outFileName =  "testPageResizeForRotatePage.pdf";

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
        String outFileName =  "testPageResizeAspectRatios.pdf";

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
        String inFileName = "gradientTest.pdf";
        String outFileName =  "gradientTest.pdf";

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
        String inFileName = "annotationBorderTest.pdf";
        String outFileName =  "annotationBorderTest.pdf";

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
        String inFileName = "annotationCalloutLineTest.pdf";
        String outFileName =  "annotationCalloutLineTest.pdf";

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
    public void testAnnotationInkList() throws IOException, InterruptedException {
        String inFileName = "annotationInkListTest.pdf";
        String outFileName =  "annotationInkListTest.pdf";

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
        String inFileName = "annotationLineEndpointTest.pdf";
        String outFileName =  "annotationLineEndpointTest.pdf";

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
        String inFileName = "annotationQuadpointsTest.pdf";
        String outFileName =  "annotationQuadpointsTest.pdf";

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
        String inFileName = "annotationRdTest.pdf";
        String outFileName =  "annotationRdTest.pdf";

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
        String inFileName = "annotationVerticesTest.pdf";
        String outFileName =  "annotationVerticesTest.pdf";

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
        String inFileName = "gradientTest.pdf";
        String outFileName = "gradientAspectTest.pdf";

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
        String inFileName = "gradientTest.pdf";
        String outFileName = "gradientAspect2Test.pdf";

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
    //TODO Update when fixing DEVSIX-9448
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
}
