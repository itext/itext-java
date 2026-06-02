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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.testutil.PageMarginsTestUtil;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

@Tag("IntegrationTest")
public class DynamicMarginsPercentageTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/DynamicMarginsPercentageTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/layout/DynamicMarginsPercentageTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static Iterable<Object[]> dataSource() {
        return Arrays.asList(new Object[][]{
                {20},
                {50},
                {100},
                {150},
        });
    }

    // The percentage height value is ignored since the parent is a DocumentRenderer with null height.
    @ParameterizedTest(name = "divPercentHeightTopMarginRenderTest_{0}")
    @MethodSource("dataSource")
    public void divPercentHeightTopMarginRenderTest(int percent)
            throws IOException, InterruptedException {
        String fileName = "divPercentHeightTopMargin" + percent;
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div marginContent = new Div()
                    .setHeight(UnitValue.createPercentValue(percent))
                    .setBackgroundColor(new DeviceRgb(255, 200, 200));
            marginContent.add(new Paragraph(percent + "% height div in top margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    marginContent, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @ParameterizedTest(name = "divPercentWidthTopMarginRenderTest_{0}")
    @MethodSource("dataSource")
    public void divPercentWidthTopMarginRenderTest(int percent)
            throws IOException, InterruptedException {
        String fileName = "divPercentWidthTopMargin" + percent;
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div marginContent = new Div()
                    .setWidth(UnitValue.createPercentValue(percent))
                    .setBackgroundColor(new DeviceRgb(200, 255, 200));
            marginContent.add(new Paragraph(percent + "% width div in top margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    marginContent, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void divPercentHeightBottomMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "divPercentHeightBottomMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div marginContent = new Div()
                    .setHeight(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(200, 200, 255));
            marginContent.add(new Paragraph("50% height div in bottom margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, marginContent, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void divPercentWidthBottomMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "divPercentWidthBottomMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div marginContent = new Div()
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(200, 200, 255));
            marginContent.add(new Paragraph("50% width div in bottom margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, marginContent, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void divPercentWidthLeftMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "divPercentWidthLeftMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div marginContent = new Div()
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(255, 255, 200));
            marginContent.add(new Paragraph("50% width div in left margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, null, marginContent, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void divPercentHeightLeftMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "divPercentHeightLeftMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div marginContent = new Div()
                    .setHeight(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(255, 255, 200));
            marginContent.add(new Paragraph("50% height div in left margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, null, marginContent, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void divPercentWidthRightMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "divPercentWidthRightMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div marginContent = new Div()
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(255, 220, 180));
            marginContent.add(new Paragraph("50% width div in right margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, null, null, marginContent));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void divPercentHeightAndWidthTopMarginRenderTest()
            throws IOException, InterruptedException {
        String fileName = "divPercentHeightAndWidthTopMargin";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div marginContent = new Div()
                    .setHeight(UnitValue.createPercentValue(50))
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(255, 180, 255));
            marginContent.add(new Paragraph("50% height + 50% width div in top margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    marginContent, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedDivPercentHeightTopMarginRenderTest()
            throws IOException, InterruptedException {
        String fileName = "nestedDivPercentHeightTopMargin";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div inner = new Div()
                    .setHeight(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(100, 200, 255));
            inner.add(new Paragraph("50% height inner div"));

            Div outer = new Div()
                    .setHeight(120)
                    .setBackgroundColor(new DeviceRgb(200, 240, 255));
            outer.add(inner);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    outer, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedDivPercentWidthTopMarginRenderTest()
            throws IOException, InterruptedException {
        String fileName = "nestedDivPercentWidthTopMargin";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div inner = new Div()
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(100, 200, 255));
            inner.add(new Paragraph("50% width inner div"));

            Div outer = new Div()
                    .setWidth(300)
                    .setBackgroundColor(new DeviceRgb(200, 240, 255));
            outer.add(inner);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    outer, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages =
        @LogMessage(messageTemplate = LayoutLogMessageConstant.PAGE_CONTENT_CANNOT_BE_DRAWN))
    public void imagePercentHeightTopMargin50Test()
            throws IOException, InterruptedException {
        String fileName = "imgPercentHeightTopMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {
            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"))
                    .setHeight(UnitValue.createPercentValue(50));

            Div marginContent = new Div()
                    .setBackgroundColor(new DeviceRgb(255, 230, 200));
            marginContent.add(image);

            SectionBreak sectionBreak = new SectionBreak(PageMarginsTestUtil.getMarginBoxesWithContent(
                    marginContent, null, null, null));
            document.add(sectionBreak);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    // The image renders incorrectly because PageMarginBoxes#layout does not take left and right margins
    // into account when laying out top and bottom margin boxes, resulting in a wrong calculated height.
    public void imagePercentWidthTopMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "imagePercentWidthTopMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"))
                    .setWidth(UnitValue.createPercentValue(50));

            Div marginContent = new Div()
                    .setBackgroundColor(new DeviceRgb(255, 230, 200));
            marginContent.add(image);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    marginContent, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    // The image renders incorrectly because PageMarginBoxes#layout does not take left and right margins
    // into account when laying out top and bottom margin boxes, resulting in a wrong calculated height.
    public void imagePercentHeightAndWidthTopMarginRenderTest()
            throws IOException, InterruptedException {
        String fileName = "imagePercentHeightAndWidthTopMargin";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"))
                    .setHeight(UnitValue.createPercentValue(50))
                    .setWidth(UnitValue.createPercentValue(50));

            Div marginContent = new Div()
                    .setBackgroundColor(new DeviceRgb(255, 230, 200));
            marginContent.add(image);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    marginContent, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages =
        @LogMessage(messageTemplate = LayoutLogMessageConstant.PAGE_CONTENT_CANNOT_BE_DRAWN))
    public void imagePercentHeightBottomMargin50Test()
            throws IOException, InterruptedException {
        String fileName = "imgPercentHeightBottomMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {
            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"))
                    .setHeight(UnitValue.createPercentValue(50));

            Div marginContent = new Div()
                    .setBackgroundColor(new DeviceRgb(200, 230, 255));
            marginContent.add(image);

            SectionBreak sectionBreak = new SectionBreak(PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, marginContent, null, null));
            document.add(sectionBreak);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    // The image renders incorrectly because PageMarginBoxes#layout does not take left and right margins
    // into account when laying out top and bottom margin boxes, resulting in a wrong calculated height.
    public void imagePercentWidthBottomMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "imagePercentWidthBottomMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"))
                    .setWidth(UnitValue.createPercentValue(50));

            Div marginContent = new Div()
                    .setBackgroundColor(new DeviceRgb(200, 230, 255));
            marginContent.add(image);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, marginContent, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void paragraphPercentHeightTopMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "paragraphPercentHeightTopMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Paragraph p = new Paragraph("50% height paragraph in top margin")
                    .setHeight(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(220, 255, 220));

            Div marginContent = new Div().add(p);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    marginContent, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void paragraphPercentWidthTopMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "paragraphPercentWidthTopMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Paragraph p = new Paragraph("50% width paragraph in top margin")
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(220, 255, 220));

            Div marginContent = new Div().add(p);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    marginContent, null, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void paragraphPercentHeightBottomMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "paragraphPercentHeightBottomMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Paragraph p = new Paragraph("50% height paragraph in bottom margin")
                    .setHeight(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(255, 220, 255));

            Div marginContent = new Div().add(p);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, marginContent, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void paragraphPercentWidthBottomMargin50RenderTest()
            throws IOException, InterruptedException {
        String fileName = "paragraphPercentWidthBottomMargin50";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Paragraph p = new Paragraph("50% width paragraph in bottom margin")
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(255, 220, 255));

            Div marginContent = new Div().add(p);

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    null, marginContent, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void mixedPercentTopAndBottomMarginsRenderTest()
            throws IOException, InterruptedException {
        String fileName = "mixedPercentageTopAndBottomMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div topContent = new Div()
                    .setHeight(UnitValue.createPercentValue(20))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setBackgroundColor(new DeviceRgb(255, 200, 200));
            topContent.add(new Paragraph("20% height + 100% width in top margin"));

            Div bottomContent = new Div()
                    .setHeight(UnitValue.createPercentValue(15))
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(200, 200, 255));
            bottomContent.add(new Paragraph("15% height + 50% width in bottom margin"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    topContent, bottomContent, null, null));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void mixedPercentAllFourMarginsRenderTest()
            throws IOException, InterruptedException {
        String fileName = "mixedPercentageAllFourMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div topContent = new Div()
                    .setHeight(UnitValue.createPercentValue(20))
                    .setBackgroundColor(new DeviceRgb(255, 200, 200));
            topContent.add(new Paragraph("TOP 20%h"));

            Div bottomContent = new Div()
                    .setHeight(UnitValue.createPercentValue(15))
                    .setBackgroundColor(new DeviceRgb(200, 200, 255));
            bottomContent.add(new Paragraph("BTM 15%h"));

            Div leftContent = new Div()
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(200, 255, 200));
            leftContent.add(new Paragraph("L 50%w"));

            Div rightContent = new Div()
                    .setWidth(UnitValue.createPercentValue(50))
                    .setBackgroundColor(new DeviceRgb(255, 255, 200));
            rightContent.add(new Paragraph("R 50%w"));

            document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getMarginBoxesWithContent(
                    topContent, bottomContent, leftContent, rightContent));
            document.add(TestResourceUtil.getTallDiv(2));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }
}