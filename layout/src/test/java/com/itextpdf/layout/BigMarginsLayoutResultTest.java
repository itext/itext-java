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
package com.itextpdf.layout;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.testutil.LayoutResultTestUtil;
import com.itextpdf.layout.testutil.PageMarginsTestUtil;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.net.MalformedURLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Tag("IntegrationTest")
public class BigMarginsLayoutResultTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/BigMarginsLayoutResultTest/";
    private static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/layout/BigMarginsLayoutResultTest/";

    private static final float A4_HEIGHT = PageSize.A4.getHeight();
    private static final float A4_WIDTH  = PageSize.A4.getWidth();

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void staticLargeTopBottomPartialTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.35f;
            float bottom = A4_HEIGHT * 0.35f;
            document.setMargins(top, 36, bottom, 36);

            Div tall = TestResourceUtil.getTallDiv(4);
            int status = LayoutResultTestUtil.getLayoutStatus(tall, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 0, top, bottom, 36, 36));

            Assertions.assertEquals(LayoutResult.PARTIAL, status,
                    "Tall element should be PARTIAL when static top+bottom margins are large");
        }
    }

    @Test
    public void staticExtremeTopBottomNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float each = (A4_HEIGHT - 10f) / 2f;
            document.setMargins(each, 36, each, 36);

            Div element = new Div()
                    .add(new Paragraph(TestResourceUtil.getByronStanza()))
                    .setHeight(80);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 0, each, each, 36, 36));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element should return NOTHING when static top+bottom margins "
                            + "leave virtually no vertical space");
        }
    }

    @Test
    public void staticLargeTopBottomMarginsPartialRenderTest()
            throws IOException, InterruptedException {
        String fileName = "staticLargeTopBottomPartial";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.35f;
            float bottom = A4_HEIGHT * 0.35f;
            document.setMargins(top, 36, bottom, 36);

            document.add(TestResourceUtil.getTallDiv(5));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void staticExtremeTopBottomMarginsForcedPlacementRenderTest()
            throws IOException, InterruptedException {
        String fileName = "staticExtremeTopBottomForced";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float each = (A4_HEIGHT - 10f) / 2f;
            document.setMargins(each, 36, each, 36);

            Div forced = new Div()
                    .add(new Paragraph("FORCED — almost no vertical space left."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));

            document.add(forced);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void staticAllFourLargePartialTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float v = A4_HEIGHT * 0.30f;
            float h = A4_WIDTH  * 0.30f;
            document.setMargins(v, h, v, h);

            Div tall = TestResourceUtil.getTallDiv(4);
            int status = LayoutResultTestUtil.getLayoutStatus(tall, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 0, v, v, h, h));

            Assertions.assertEquals(LayoutResult.PARTIAL, status,
                    "Tall element should be PARTIAL when all four static margins are large");
        }
    }

    @Test
    public void staticAllFourExtremeNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float v = (A4_HEIGHT - 10f) / 2f;
            float h = (A4_WIDTH  - 10f) / 2f;
            document.setMargins(v, h, v, h);

            Div element = new Div()
                    .add(new Paragraph(TestResourceUtil.getByronStanza()))
                    .setHeight(80);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 0, v, v, h, h));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element should return NOTHING when all four static margins are extreme");
        }
    }

    @Test
    public void staticAllFourLargeMarginsPartialRenderTest()
            throws IOException, InterruptedException {
        String fileName = "staticAllFourLargeMarginsPartial";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float v = A4_HEIGHT * 0.30f;
            float h = A4_WIDTH  * 0.30f;
            document.setMargins(v, h, v, h);

            document.add(TestResourceUtil.getTallDiv(5));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void staticAllFourLargeMarginsWithSectionBreakRenderTest()
            throws IOException, InterruptedException {
        String fileName = "staticAllFourLargeMarginsSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float v = A4_HEIGHT * 0.30f;
            float h = A4_WIDTH  * 0.30f;
            document.setMargins(v, h, v, h);

            document.add(TestResourceUtil.getTallDiv(3));
            document.add(new SectionBreak(PageMarginsTestUtil.getMarginBoxes(60, 60, 0, 0)));
            document.add(new Div()
                    .add(new Paragraph("After section break — smaller margin boxes."))
                    .setBackgroundColor(new DeviceRgb(65, 151, 29)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void pageMarginLargeTopBottomPartialTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.40f;
            float bottom = A4_HEIGHT * 0.30f;
            document.setPageMargins(1, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            Div tall = TestResourceUtil.getTallDiv(4);
            int status = LayoutResultTestUtil.getLayoutStatus(tall, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, top, bottom, 0, 0));

            Assertions.assertEquals(LayoutResult.PARTIAL, status,
                    "Tall element should be PARTIAL when per-page margin boxes on page 1 are large");
        }
    }

    @Test
    public void pageMarginExtremeTopBottomNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float each = (A4_HEIGHT - 10f) / 2f;
            document.setPageMargins(1, PageMarginsTestUtil.getMarginBoxes(each, each, 0, 0));

            Div element = new Div()
                    .add(new Paragraph(TestResourceUtil.getByronStanza()))
                    .setHeight(80);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, each, each, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element should return NOTHING when per-page margin boxes on page 1 are extreme");
        }
    }

    @Test
    public void pageMarginLargeOnSpecificPagePartialRenderTest()
            throws IOException, InterruptedException {
        String fileName = "pageMarginLargeOnPage2Partial";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.45f;
            float bottom = A4_HEIGHT * 0.25f;
            document.setPageMargins(2, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            document.add(new Paragraph("Page 1 — no special margin boxes."));
            document.add(new AreaBreak());
            document.add(TestResourceUtil.getTallDiv(2));
            document.add(new Paragraph("Page 3 — no special margin boxes."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void pageMarginAllFourSidesLargeOnPage1PartialRenderTest()
            throws IOException, InterruptedException {
        String fileName = "pageMarginAllFourLargeOnPage1";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float v = A4_HEIGHT * 0.30f;
            float h = A4_WIDTH  * 0.28f;
            document.setPageMargins(1, PageMarginsTestUtil.getMarginBoxes(v, v, h, h));

            document.add(TestResourceUtil.getTallDiv(4));
            document.add(new Paragraph("Page 2 — no margin boxes."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void pageMarginPredicateLargeTopBottomPartialTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.40f;
            float bottom = A4_HEIGHT * 0.30f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            Div tall = TestResourceUtil.getTallDiv(4);
            int status = LayoutResultTestUtil.getLayoutStatus(tall, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, top, bottom, 0, 0));

            Assertions.assertEquals(LayoutResult.PARTIAL, status,
                    "Tall element should be PARTIAL when predicate-based top+bottom margin boxes "
                            + "are large on all pages");
        }
    }

    @Test
    public void pageMarginPredicateExtremeTopBottomNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float each = (A4_HEIGHT - 10f) / 2f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(each, each, 0, 0));

            Div element = new Div()
                    .add(new Paragraph(TestResourceUtil.getByronStanza()))
                    .setHeight(80);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, each, each, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element should return NOTHING when predicate-based top+bottom margin boxes "
                            + "are extreme on all pages");
        }
    }

    @Test
    public void pageMarginPredicateLargeTopBottomPartialRenderTest()
            throws IOException, InterruptedException {
        String fileName = "pageMarginPredicateLargeTopBottomPartial";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.40f;
            float bottom = A4_HEIGHT * 0.30f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            document.add(TestResourceUtil.getTallDiv(5));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void pageMarginPredicateAllFourSidesLargePartialRenderTest()
            throws IOException, InterruptedException {
        String fileName = "pageMarginPredicateAllFourLargePartial";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float v = A4_HEIGHT * 0.30f;
            float h = A4_WIDTH  * 0.28f;
            document.setPageMargins(pageNum -> pageNum % 2 == 0, PageMarginsTestUtil.getMarginBoxes(v, v, h, h));

            document.add(TestResourceUtil.getTallDiv(3));
            document.add(new AreaBreak());
            document.add(TestResourceUtil.getTallDiv(3));
            document.add(new AreaBreak());
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.PAGE_CONTENT_CANNOT_BE_DRAWN, count = 2),
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void pageMarginPredicateAllFourExtremeForcedRenderTest()
            throws IOException, InterruptedException {
        String fileName = "pageMarginPredicateAllFourExtremeForced";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float v = (A4_HEIGHT - 10f) / 2f;
            float h = (A4_WIDTH  - 10f) / 2f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(v, v, h, h));

            Div forced = new Div()
                    .add(new Paragraph("FORCED — all four margin boxes are extreme."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));
            document.add(forced);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void pageMarginPredicateLargeThenSectionBreakRenderTest()
            throws IOException, InterruptedException {
        String fileName = "pageMarginPredicateLargeThenSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.40f;
            float bottom = A4_HEIGHT * 0.30f;
            document.setPageMargins(pageNum -> pageNum % 2 != 0, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            document.add(TestResourceUtil.getTallDiv(4));
            document.add(new SectionBreak(PageMarginsTestUtil.getMarginBoxes(60, 60, 0, 0)));
            document.add(new Div()
                    .add(new Paragraph("Section 2 — comfortable margin boxes."))
                    .setBackgroundColor(new DeviceRgb(65, 151, 29)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void mixedStaticAndPageMarginRenderTest()
            throws IOException, InterruptedException {
        String fileName = "mixedStaticAndPageMargin";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setMargins(60, 60, 60, 60);
            document.setPageMargins(2,
                    PageMarginsTestUtil.getMarginBoxes(A4_HEIGHT * 0.30f, A4_HEIGHT * 0.25f, 0, 0));

            document.add(TestResourceUtil.getTallDiv(6));
            document.add(new AreaBreak());
            document.add(TestResourceUtil.getTallDiv(4));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void extremePageMarginsUnsplittableImageNothingTest() throws MalformedURLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.40f;
            float bottom = A4_HEIGHT * 0.40f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));

            int status = LayoutResultTestUtil.getLayoutStatusForImage(image, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, top, bottom, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Unsplittable image should return NOTHING when extreme dynamic margins "
                            + "leave less space than the image height");
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void extremePageMarginsUnsplittableImageNothingRenderTest()
            throws IOException, InterruptedException {
        String fileName = "extremePageMarginsUnsplittableImageNothing";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.40f;
            float bottom = A4_HEIGHT * 0.40f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            document.add(image);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void extremeStaticMarginsUnsplittableImageNothingTest() throws MalformedURLException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                Document document = new Document(pdfDoc)) {

            float top    = A4_HEIGHT * 0.40f;
            float bottom = A4_HEIGHT * 0.40f;
            document.setMargins(top, 36, bottom, 36);

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            int status = LayoutResultTestUtil.getLayoutStatusForImage(image, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 0, top, bottom, 36, 36));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Unsplittable image should return NOTHING when extreme static margins "
                            + "leave less space than the image height");
        }
    }
}