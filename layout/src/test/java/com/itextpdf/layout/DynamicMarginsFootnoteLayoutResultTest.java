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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.testutil.LayoutResultTestUtil;
import com.itextpdf.layout.testutil.PageMarginsTestUtil;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("IntegrationTest")
public class DynamicMarginsFootnoteLayoutResultTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/DynamicMarginsFootnoteLayoutResultTest/";
    private static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/layout/DynamicMarginsFootnoteLayoutResultTest/";

    private static final float A4_HEIGHT = PageSize.A4.getHeight();
    private static final float A4_WIDTH = PageSize.A4.getWidth();

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void hugeTopMarginPartialTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            float largeTop = A4_HEIGHT * 0.60f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(largeTop, 0, 0, 0));

            Div tall = TestResourceUtil.getTallDiv(4);
            int status = LayoutResultTestUtil.getLayoutStatus(tall, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, largeTop, 0, 0, 0));

            Assertions.assertEquals(LayoutResult.PARTIAL, status, "Tall element should be split (PARTIAL) when a " +
                    "large dynamic top margin leaves only a small usable area");
        }
    }

    @Test
    public void hugeTopAndBottomMarginsPartialTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            float top = A4_HEIGHT * 0.35f;
            float bottom = A4_HEIGHT * 0.35f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            Div tall = TestResourceUtil.getTallDiv(4);
            int status = LayoutResultTestUtil.getLayoutStatus(tall, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, top, bottom, 0, 0));

            Assertions.assertEquals(LayoutResult.PARTIAL, status, "Tall element should be split (PARTIAL) when both " +
                    "dynamic top and bottom margins are large but a small usable strip remains");
        }
    }

    @Test
    public void dynamicMarginsEvenPagesRenderTest() throws IOException, InterruptedException {
        String fileName = "partialDynamicMarginsEvenPages";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float largeTop = A4_HEIGHT * 0.55f;
            float largeBottom = A4_HEIGHT * 0.20f;
            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    PageMarginsTestUtil.getMarginBoxes(largeTop, largeBottom, 0, 0));

            document.add(TestResourceUtil.getTallDiv(2));
            document.add(new AreaBreak());
            document.add(TestResourceUtil.getTallDiv(3));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void dynamicMarginsOddPagesRenderTest() throws IOException, InterruptedException {
        String fileName = "partialDynamicMarginsOddPages";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float largeTop = A4_HEIGHT * 0.55f;
            float largeBottom = A4_HEIGHT * 0.15f;
            document.setPageMargins(pageNum -> pageNum % 2 != 0,
                    PageMarginsTestUtil.getMarginBoxes(largeTop, largeBottom, 0, 0));

            document.add(TestResourceUtil.getTallDiv(3));
            document.add(new AreaBreak());
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void dynamicMarginsSectionBreakRenderTest() throws IOException, InterruptedException {
        String fileName = "partialDynamicMarginsSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float largeTop = A4_HEIGHT * 0.50f;
            document.setPageMargins(pageNum -> pageNum % 2 == 0, PageMarginsTestUtil.getMarginBoxes(largeTop, 0, 0, 0));

            document.add(TestResourceUtil.getTallDiv(3));
            document.add(new SectionBreak(PageMarginsTestUtil.getMarginBoxes(0, A4_HEIGHT * 0.50f, 0, 0)));
            document.add(TestResourceUtil.getTallDiv(3));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void perPageDynamicMarginsRenderTest() throws IOException, InterruptedException {
        String fileName = "partialPerPageDynamicMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> {
                float top = Math.min(pageNum * 60f, A4_HEIGHT * 0.55f);
                List<PageMarginContent> list = new ArrayList<>();
                list.add(new PageMarginContent(MarginBoxName.TOP, new Div().add(new Paragraph("Header p." + pageNum))
                        .setBackgroundColor(ColorConstants.PINK).setHeight(top)));
                return new PageMarginBoxes(list);
            });

            document.add(TestResourceUtil.getTallDiv(6));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void largeFootnotePartialTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            float footnoteHeight = A4_HEIGHT * 0.55f;
            applyMarginBoxes(document, 0, footnoteHeight, 0, 0);

            Div tall = TestResourceUtil.getTallDiv(3);
            int status = LayoutResultTestUtil.getLayoutStatus(tall, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 0, footnoteHeight, 0, 0));

            Assertions.assertEquals(LayoutResult.PARTIAL, status, "Tall element should be split (PARTIAL) when a " +
                    "large footnote margin leaves less than half the page for content");
        }
    }

    @Test
    public void largeFootnotePartialRenderTest() throws IOException, InterruptedException {
        String fileName = "partialLargeFootnoteMargin";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float footnoteHeight = A4_HEIGHT * 0.55f;
            document.add(new SectionBreak(PageMarginsTestUtil.getFootnoteMarginBoxes(footnoteHeight)));

            Div tall = new Div().setBackgroundColor(new DeviceRgb(78, 151, 205));
            for (int i = 0; i < 5; i++) {
                tall.add(new Paragraph("BLOCK " + i + "\n" + TestResourceUtil.getByronStanza()));
            }
            document.add(tall);
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void largeFootnoteAreaBreakRenderTest() throws IOException, InterruptedException {
        String fileName = "partialFootnoteAreaBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float footnoteHeight = A4_HEIGHT * 0.55f;
            applyMarginBoxes(document, 0, footnoteHeight, 0, 0);

            document.add(TestResourceUtil.getTallDiv(3));
            document.add(new AreaBreak());
            document.add(new Paragraph("Second page — same large footnote margin."));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void largeFootnoteAndHeaderPartialTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            float headerHeight = A4_HEIGHT * 0.30f;
            float footnoteHeight = A4_HEIGHT * 0.35f;
            applyMarginBoxes(document, headerHeight, footnoteHeight, 0, 0);

            Div tall = TestResourceUtil.getTallDiv(4);
            int status = LayoutResultTestUtil.getLayoutStatus(tall, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, headerHeight, footnoteHeight, 0, 0));

            Assertions.assertEquals(LayoutResult.PARTIAL, status, "Tall element should be split (PARTIAL) when both " +
                    "header and large footnote margins leave only a small usable band");
        }
    }

    @Test
    public void dynamicFootnoteMarginsEvenPagesRenderTest() throws IOException, InterruptedException {
        String fileName = "partialDynamicFootnoteEvenPages";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float footnoteHeight = A4_HEIGHT * 0.55f;
            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    PageMarginsTestUtil.getFootnoteMarginBoxes(footnoteHeight));

            document.add(TestResourceUtil.getTallDiv(3));
            document.add(new AreaBreak());
            document.add(TestResourceUtil.getTallDiv(3));
            document.add(new AreaBreak());
            document.add(new Paragraph("Page 4 - Footnote margins."));
            document.add(new AreaBreak());
            document.add(new Paragraph("Page 5 - No margins."));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void extremeTopMarginNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            float hugeTop = A4_HEIGHT - 30f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(hugeTop, 0, 0, 0));

            Div element = new Div().add(new Paragraph(TestResourceUtil.getByronStanza())).setHeight(80);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, hugeTop, 0, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status, "Element should return NOTHING when a dynamic top " +
                    "margin occupies virtually the entire page");
        }
    }

    @Test
    public void extremeTopAndBottomMarginsNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            float hugeMargin = (A4_HEIGHT - 10f) / 2f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(hugeMargin, hugeMargin, 0, 0));

            Div element = new Div().add(new Paragraph(TestResourceUtil.getByronStanza())).setHeight(80);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, hugeMargin, hugeMargin, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status, "Element should return NOTHING when combined " +
                    "dynamic top and bottom margins leave virtually no usable area");
        }
    }

    @Test
    public void extremeFootnoteNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            float hugeFootnote = A4_HEIGHT - 25f;
            applyMarginBoxes(document, 0, hugeFootnote, 0, 0);

            Div element = new Div().add(new Paragraph(TestResourceUtil.getByronStanza())).setHeight(60);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 0, hugeFootnote, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status, "Short element with explicit height should " +
                    "return NOTHING when a huge footnote margin leaves essentially no vertical room");
        }
    }

    @Test
    public void extremeFootnoteKeepTogetherNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            float hugeMargin = (A4_HEIGHT - 8f) / 2f;
            applyMarginBoxes(document, hugeMargin, hugeMargin, 0, 0);

            Div element = new Div().add(new Paragraph(TestResourceUtil.getByronStanza())).setHeight(150)
                    .setKeepTogether(true);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, hugeMargin, hugeMargin, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "keepTogether element taller than available area should return NOTHING");
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void hugeDynamicMarginForcedPlacementRenderTest() throws IOException, InterruptedException {
        String fileName = "hugeDynamicMarginForcedPlacement";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float hugeTop = A4_HEIGHT - 30f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(hugeTop, 0, 0, 0));

            Div forced = new Div().add(new Paragraph("FORCED — huge dynamic top margin, almost no space."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));
            document.add(forced);
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void hugeFootnoteForcedPlacementRenderTest() throws IOException, InterruptedException {
        String fileName = "hugeFootnoteForcedPlacement";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float hugeFootnote = A4_HEIGHT - 30f;
            document.add(new SectionBreak(PageMarginsTestUtil.getFootnoteMarginBoxes(hugeFootnote)));

            Div forced = new Div().add(new Paragraph("FORCED — huge footnote margin, almost no space."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));
            document.add(forced);
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void extremeMarginsRecoveryViaSectionBreakRenderTest() throws IOException, InterruptedException {
        String fileName = "extremeMarginsRecoverySectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float hugeMargin = (A4_HEIGHT - 10f) / 2f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(hugeMargin, hugeMargin, 0, 0));

            Div forced = new Div().add(new Paragraph("PAGE 1 — extreme dynamic margins (FORCED)."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));
            document.add(forced);

            document.add(new SectionBreak(PageMarginsTestUtil.getMarginBoxes(80, 80, 0, 0)));
            document.add(new Div().add(new Paragraph("PAGE 2 — normal margins, content fits fully."))
                    .setBackgroundColor(new DeviceRgb(65, 151, 29)));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(
            messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2))
    public void extremeFootnoteMarginsRecoveryRenderTest() throws IOException, InterruptedException {
        String fileName = "extremeFootnoteMarginsRecovery";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float hugeFootnote = A4_HEIGHT - 30f;
            document.add(new SectionBreak(PageMarginsTestUtil.getFootnoteMarginBoxes(hugeFootnote)));

            Div forced = new Div().add(new Paragraph("PAGE 1 — extreme footnote margin (FORCED)."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));
            document.add(forced);

            document.add(new AreaBreak());
            Div also = new Div().add(new Paragraph("PAGE 2 — same extreme footnote margin (FORCED)."))
                    .setBackgroundColor(new DeviceRgb(255, 180, 80));
            document.add(also);

            document.add(new SectionBreak(PageMarginsTestUtil.getMarginBoxes(60, 60, 0, 0)));
            document.add(new Div().add(new Paragraph("PAGE 3 — normal margins, content fits."))
                    .setBackgroundColor(new DeviceRgb(65, 151, 29)));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void dynamicMarginStatusProgressionTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            Div element = new Div().add(new Paragraph(TestResourceUtil.getByronStanza())).setHeight(350)
                    .setBackgroundColor(new DeviceRgb(65, 151, 29));

            int fullStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 50, 0, 0, 0));
            Assertions.assertEquals(LayoutResult.FULL, fullStatus, "Expected FULL with small dynamic top margin");

            int partialStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, A4_HEIGHT * 0.55f, 0, 0, 0));
            Assertions.assertEquals(LayoutResult.PARTIAL, partialStatus,
                    "Expected PARTIAL with large dynamic top margin");

            float hugeTop = A4_HEIGHT - 30f;
            int nothingStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, hugeTop, 0, 0, 0));
            Assertions.assertEquals(LayoutResult.NOTHING, nothingStatus,
                    "Expected NOTHING with extreme dynamic top margin");
        }
    }

    @Test
    public void footnoteMarginStatusProgressionTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            Div element = new Div().add(new Paragraph(TestResourceUtil.getByronStanza())).setHeight(350)
                    .setBackgroundColor(new DeviceRgb(78, 151, 205));

            int fullStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 0, 50, 0, 0));
            Assertions.assertEquals(LayoutResult.FULL, fullStatus, "Expected FULL with a small footnote margin");

            int partialStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 0, A4_HEIGHT * 0.55f, 0, 0));
            Assertions.assertEquals(LayoutResult.PARTIAL, partialStatus,
                    "Expected PARTIAL with a large footnote margin");

            float hugeBottom = A4_HEIGHT - 30f;
            int nothingStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 0, hugeBottom, 0, 0));
            Assertions.assertEquals(LayoutResult.NOTHING, nothingStatus,
                    "Expected NOTHING with an extreme footnote margin");
        }
    }

    @Test
    public void combinedDynamicAndFootnoteMarginStatusProgressionTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            Div element = new Div().add(new Paragraph(TestResourceUtil.getByronStanza())).setHeight(350)
                    .setBackgroundColor(new DeviceRgb(209, 247, 29));

            int fullStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 50, 50, 0, 0));
            Assertions.assertEquals(LayoutResult.FULL, fullStatus, "Expected FULL when both margins are small");

            float medTop = A4_HEIGHT * 0.30f;
            float medBottom = A4_HEIGHT * 0.30f;
            int partialStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, medTop, medBottom, 0, 0));
            Assertions.assertEquals(LayoutResult.PARTIAL, partialStatus,
                    "Expected PARTIAL when both margins are medium-large");

            float hugeMargin = (A4_HEIGHT - 10f) / 2f;
            int nothingStatus = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, hugeMargin, hugeMargin, 0, 0));
            Assertions.assertEquals(LayoutResult.NOTHING, nothingStatus,
                    "Expected NOTHING when both margins are extreme");
        }
    }

    @Test
    public void hugeTopDynamicMarginWithFootnoteAnchorTest() throws IOException, InterruptedException {
        String fileName = "hugeTopDynamicMarginWithFootnoteAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            float largeTop = A4_HEIGHT * 0.55f;

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(largeTop, 0, 0, 0));
            document.add(div);
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void hugeBottomDynamicMarginWithFootnoteAnchorTest() throws IOException, InterruptedException {
        String fileName = "hugeBottomDynamicMarginWithFootnoteAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            float largeBottom = A4_HEIGHT * 0.55f;

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(0, largeBottom, 0, 0));
            document.add(div);
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void hugeTopAndBottomMarginsWithFootnoteAnchorRenderTest() throws IOException, InterruptedException {
        String fileName = "hugeTopAndBottomMarginsWithFootnoteAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            float top = A4_HEIGHT * 0.35f;
            float bottom = A4_HEIGHT * 0.35f;
            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(top, bottom, 0, 0));

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());

            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        }

        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    private static void applyMarginBoxes(Document document, float top, float bottom, float left, float right) {
        document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxes(top, bottom, left, right));
    }
}
