package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.geom.PageSize;
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
        createDestinationFolder(DESTINATION_FOLDER);
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
}
