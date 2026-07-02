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
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.FlexDirectionPropertyValue;
import com.itextpdf.layout.properties.FlexWrapPropertyValue;
import com.itextpdf.layout.properties.JustifyContent;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.renderer.FlexContainerRenderer;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("IntegrationTest")
public class FlexPageMarginsTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/FlexPageMarginsTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/layout/FlexPageMarginsTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void flexContainerThenSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "flexSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createRowFlexContainer();
            flex.add(coloredDiv("FLEX ITEM 1", new DeviceRgb(65, 151, 29)));
            flex.add(coloredDiv("FLEX ITEM 2", new DeviceRgb(209, 247, 29)));
            flex.add(coloredDiv("FLEX ITEM 3", new DeviceRgb(78, 151, 205)));

            document.add(flex);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Page 2 — margins1 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void flexThenTwoSectionBreaksInARowTest() throws IOException, InterruptedException {
        String fileName = "flexTwoSectionBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createRowFlexContainer();
            flex.add(coloredDiv("ITEM A", new DeviceRgb(65, 151, 29)));
            flex.add(coloredDiv("ITEM B", new DeviceRgb(209, 247, 29)));

            document.add(flex);
            document.add(new SectionBreak(PageSize.A4.rotate(),
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new SectionBreak(PageSize.A5,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(new Paragraph("Final page — A5 with margins2."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void flexAlternatingSectionAndAreaBreaksTest() throws IOException, InterruptedException {
        String fileName = "flexAltBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex1 = createRowFlexContainer();
            flex1.add(coloredDiv("S1-A", new DeviceRgb(65, 151, 29)));
            flex1.add(coloredDiv("S1-B", new DeviceRgb(209, 247, 29)));

            Div flex2 = createRowFlexContainer();
            flex2.add(coloredDiv("S3-A", new DeviceRgb(78, 151, 205)));
            flex2.add(coloredDiv("S3-B", new DeviceRgb(255, 165, 0)));

            Div flex3 = createRowFlexContainer();
            flex3.add(coloredDiv("S5-A", new DeviceRgb(200, 100, 100)));
            flex3.add(coloredDiv("S5-B", new DeviceRgb(100, 200, 100)));

            document.add(flex1);
            document.add(new AreaBreak());
            document.add(new Paragraph("Page 2 — no special margins."));
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(flex2);
            document.add(new AreaBreak());
            document.add(new Paragraph("Page 4 — still margins1."));
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(flex3);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void multiPageFlexThenSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "flexMultiPageSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createRowFlexContainer();
            for (int i = 1; i <= 20; i++) {
                flex.add(new Div()
                        .add(new Paragraph("ITEM " + i))
                        .setWidth(UnitValue.createPercentValue(30))
                        .setHeight(200)
                        .setBackgroundColor(i % 2 == 0
                                ? new DeviceRgb(65, 151, 29)
                                : new DeviceRgb(209, 247, 29))
                        .setMargin(5));
            }

            document.add(flex);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Post-flex section — margins1 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void multiPageFlexWithDocumentMarginsTest() throws IOException, InterruptedException {
        String fileName = "flexMultiPageDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            Div flex = createColumnFlexContainer();
            for (int i = 0; i < 3; i++) {
                Div row = createRowFlexContainer();
                for (int j = 0; j < 3; j++) {
                    row.add(new Div()
                            .add(new Paragraph("R" + i + "C" + j + "\n" + TestResourceUtil.getByronStanza()))
                            .setWidth(UnitValue.createPercentValue(30))
                            .setBackgroundColor(j % 2 == 0
                                    ? new DeviceRgb(65, 151, 29)
                                    : new DeviceRgb(209, 247, 29))
                            .setMargin(5));
                }
                flex.add(row);
            }

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void flexWithPerPageDocumentMarginsTest() throws IOException, InterruptedException {
        String fileName = "flexPerPageDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> {
                List<PageMarginContent> margins = new ArrayList<>();
                margins.add(new PageMarginContent(MarginBoxName.TOP,
                        new Div()
                                .add(new Paragraph("Page " + pageNum))
                                .setBackgroundColor(ColorConstants.PINK)
                                .setTextAlignment(TextAlignment.CENTER)));
                return new PageMarginBoxes(margins);
            });

            Div flex = createColumnFlexContainer();
            for (int i = 0; i < 4; i++) {
                Div row = createRowFlexContainer();
                for (int j = 0; j < 3; j++) {
                    row.add(new Div()
                            .add(new Paragraph(TestResourceUtil.getByronStanza()))
                            .setWidth(UnitValue.createPercentValue(30))
                            .setBackgroundColor(j % 2 == 0
                                    ? new DeviceRgb(65, 151, 29)
                                    : new DeviceRgb(209, 247, 29))
                            .setMargin(4));
                }
                flex.add(row);
            }

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void flexDocumentMarginsOverriddenBySectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "flexDocMarginsOverriddenBySectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            Div flex1 = createRowFlexContainer();
            flex1.add(coloredDiv("S1-A", new DeviceRgb(65, 151, 29)));
            flex1.add(coloredDiv("S1-B", new DeviceRgb(209, 247, 29)));
            flex1.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 3)));

            Div flex2 = createRowFlexContainer();
            flex2.add(coloredDiv("S2-A", new DeviceRgb(78, 151, 205)));
            flex2.add(coloredDiv("S2-B", new DeviceRgb(255, 165, 0)));

            document.add(flex1);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(flex2);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void flexContainerWithElementMarginsAndSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "flexElementMarginsSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createRowFlexContainer();
            flex.setMargins(50, 50, 50, 50)
                    .setBackgroundColor(new DeviceRgb(220, 220, 220));
            flex.add(coloredDiv("ITEM 1", new DeviceRgb(65, 151, 29)));
            flex.add(coloredDiv("ITEM 2", new DeviceRgb(209, 247, 29)));
            flex.add(coloredDiv("ITEM 3", new DeviceRgb(78, 151, 205)));

            document.add(flex);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Page 2 — section margins1 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void flexItemsWithElementMarginsAndDocumentPageMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "flexItemMarginsDocPageMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div flex = createRowFlexContainer();

            Div item1 = new Div()
                    .add(new Paragraph("LARGE MARGIN\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(65, 151, 29))
                    .setWidth(UnitValue.createPercentValue(28))
                    .setMargins(30, 20, 30, 20);

            Div item2 = new Div()
                    .add(new Paragraph("NO MARGIN\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(209, 247, 29))
                    .setWidth(UnitValue.createPercentValue(28))
                    .setMargin(0);

            Div item3 = new Div()
                    .add(new Paragraph("LARGE PADDING\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(78, 151, 205))
                    .setWidth(UnitValue.createPercentValue(28))
                    .setPaddings(25, 25, 25, 25);

            flex.add(item1);
            flex.add(item2);
            flex.add(item3);

            document.add(flex);
            document.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 8)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void flexWithStaticDocumentMarginsAndSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "flexStaticMarginsAndSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setMargins(80, 80, 80, 80);

            Div flex = createRowFlexContainer();
            flex.add(coloredDiv("ITEM 1", new DeviceRgb(65, 151, 29)));
            flex.add(coloredDiv("ITEM 2", new DeviceRgb(209, 247, 29)));
            flex.add(coloredDiv("ITEM 3", new DeviceRgb(78, 151, 205)));

            document.add(flex);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(new Div()
                    .add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 3)))
                    .setBackgroundColor(new DeviceRgb(255, 165, 0)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void flexWithPageNumberSpecificMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "flexPageNumMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(1, new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div flex = createColumnFlexContainer();
            for (int i = 0; i < 3; i++) {
                flex.add(new Div()
                        .add(new Paragraph("ITEM " + i + "\n" + TestResourceUtil.getByronStanza()))
                        .setBackgroundColor(i % 2 == 0
                                ? new DeviceRgb(65, 151, 29)
                                : new DeviceRgb(209, 247, 29)));
            }

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedFlexAroundSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "nestedFlexSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div outer1 = createColumnFlexContainer();
            Div inner1 = createRowFlexContainer();
            inner1.add(coloredDiv("S1-A", new DeviceRgb(65, 151, 29)));
            inner1.add(coloredDiv("S1-B", new DeviceRgb(209, 247, 29)));
            outer1.add(coloredDiv("S1 TOP", new DeviceRgb(78, 151, 205)));
            outer1.add(inner1);

            Div outer2 = createColumnFlexContainer();
            Div inner2 = createRowFlexContainer();
            inner2.add(coloredDiv("S2-A", new DeviceRgb(200, 100, 100)));
            inner2.add(coloredDiv("S2-B", new DeviceRgb(100, 200, 100)));
            outer2.add(coloredDiv("S2 TOP", new DeviceRgb(255, 165, 0)));
            outer2.add(inner2);

            document.add(outer1);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(outer2);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void deeplyNestedFlexWithDocumentAndSectionMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "deepNestedFlexMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 != 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div outerCol = createColumnFlexContainer();

            for (int row = 0; row < 3; row++) {
                Div midRow = createRowFlexContainer();
                for (int col = 0; col < 2; col++) {
                    Div innerCol = createColumnFlexContainer();
                    innerCol.setWidth(UnitValue.createPercentValue(45)).setMargin(4);
                    innerCol.add(new Div()
                            .add(new Paragraph("R" + row + "C" + col + "-TOP\n" + TestResourceUtil.getByronStanza()))
                            .setBackgroundColor(col == 0
                                    ? new DeviceRgb(65, 151, 29)
                                    : new DeviceRgb(209, 247, 29)));
                    innerCol.add(new Div()
                            .add(new Paragraph("R" + row + "C" + col + "-BOT\n" + TestResourceUtil.getByronStanza()))
                            .setBackgroundColor(col == 0
                                    ? new DeviceRgb(78, 151, 205)
                                    : new DeviceRgb(255, 165, 0)));
                    midRow.add(innerCol);
                }
                outerCol.add(midRow);
            }

            document.add(outerCol);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(new Paragraph("Final section — margins2 override document margins."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate =
            LayoutLogMessageConstant.FLEX_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK)
    })
    public void sectionBreakInsideFlexContainerTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakInsideFlexContainer";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createRowFlexContainer();
            flex.add(coloredDiv("ITEM A", new DeviceRgb(65, 151, 29)));
            flex.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            flex.add(coloredDiv("ITEM B", new DeviceRgb(209, 247, 29)));

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.SECTION_BREAK_IGNORED)})
    public void sectionBreakOnFlexItemChildTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakOnFlexItemChild";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createRowFlexContainer();

            Div item = new Div()
                    .setBackgroundColor(new DeviceRgb(65, 151, 29))
                    .setWidth(UnitValue.createPercentValue(80));
            item.add(new Paragraph("Content before break."));
            item.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            item.add(new Paragraph("Content after break."));

            flex.add(item);

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.AREA_BREAK_IGNORED)})
    public void areaBreakOnFlexItemChildTest() throws IOException, InterruptedException {
        String fileName = "flexItemChildAreaBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createColumnFlexContainer();

            Div item = new Div()
                    .setBackgroundColor(new DeviceRgb(209, 247, 29))
                    .setWidth(UnitValue.createPercentValue(80));
            item.add(new Paragraph("Content before area break in item."));
            item.add(new AreaBreak());
            item.add(new Paragraph("Content after area break in item."));

            flex.add(coloredDiv("ITEM ABOVE", new DeviceRgb(65, 151, 29)));
            flex.add(item);
            flex.add(coloredDiv("ITEM BELOW", new DeviceRgb(78, 151, 205)));

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate =
            LayoutLogMessageConstant.FLEX_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK)})
    public void areaBreakInFlexWithDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "flexAreaBreakDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div flex = createColumnFlexContainer();
            flex.add(coloredDiv("Before break.", new DeviceRgb(65, 151, 29)));
            flex.add(new AreaBreak());
            flex.add(coloredDiv("After break.", new DeviceRgb(209, 247, 29)));

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate =
            LayoutLogMessageConstant.FLEX_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK)})
    public void areaBreakInFlexThenSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "flexAreaBreakThenSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createColumnFlexContainer();
            flex.add(coloredDiv("Before break.", new DeviceRgb(65, 151, 29)));
            flex.add(new AreaBreak());
            flex.add(coloredDiv("After break.", new DeviceRgb(209, 247, 29)));

            document.add(flex);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(new Paragraph("Page 2 — margins2 active after section break."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate =
            LayoutLogMessageConstant.FLEX_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK, count = 2)
    })
    public void multipleAreaBreaksInNestedFlexWithDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedFlexMultiAreaBreakDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 != 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div outer = createColumnFlexContainer();

            Div row1 = createRowFlexContainer();
            row1.add(coloredDiv("DIV 1 - A", new DeviceRgb(65, 151, 29)));
            row1.add(coloredDiv("DIV 1 - B", new DeviceRgb(209, 247, 29)));

            Div row2 = createRowFlexContainer();
            row2.add(coloredDiv("DIV 2 - A", new DeviceRgb(78, 151, 205)));
            row2.add(coloredDiv("DIV 2 - B", new DeviceRgb(255, 165, 0)));

            Div row3 = createRowFlexContainer();
            row3.add(coloredDiv("DIV 3 - A", new DeviceRgb(200, 100, 100)));
            row3.add(coloredDiv("DIV 3 - B", new DeviceRgb(100, 200, 100)));

            outer.add(row1);
            outer.add(new AreaBreak());
            outer.add(row2);
            outer.add(new AreaBreak());
            outer.add(row3);

            document.add(outer);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.AREA_BREAK_IGNORED)})
    public void areaBreakOnNestedFlexItemWithDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedFlexItemAreaBreakDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            Div flex = createColumnFlexContainer();

            Div item = new Div()
                    .setBackgroundColor(new DeviceRgb(220, 220, 255))
                    .setWidth(UnitValue.createPercentValue(80));
            item.add(new Paragraph("Item content before nested area break."));
            item.add(new Div()
                    .add(new Paragraph("Inner div before break."))
                    .add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 5)))
                    .add(new AreaBreak())
                    .add(new Paragraph("Inner div after break."))
                    .setBackgroundColor(new DeviceRgb(209, 247, 29)));
            item.add(new Paragraph("Item content after nested area break."));

            flex.add(coloredDiv("ABOVE", new DeviceRgb(65, 151, 29)));
            flex.add(item);
            flex.add(coloredDiv("BELOW", new DeviceRgb(78, 151, 205)));

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate =
            LayoutLogMessageConstant.FLEX_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK)})
    public void areaBreakWithPageSizeInFlexWithDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "flexAreaBreakPageSizeDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div flex = createColumnFlexContainer();
            flex.add(coloredDiv("A4 PAGE - ITEM", new DeviceRgb(65, 151, 29)));
            flex.add(new AreaBreak(PageSize.A5));
            flex.add(coloredDiv("A5 PAGE - ITEM", new DeviceRgb(209, 247, 29)));

            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedFlexOuterElementMarginsWithDocumentPageMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedFlexOuterElemMarginsDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div outer = createRowFlexContainer();
            outer.setMargins(40, 40, 40, 40)
                    .setBackgroundColor(new DeviceRgb(220, 220, 220));

            Div inner1 = createColumnFlexContainer();
            inner1.setWidth(UnitValue.createPercentValue(45)).setMargin(5);
            inner1.add(coloredDiv("INNER-1 A", new DeviceRgb(65, 151, 29)));
            inner1.add(coloredDiv("INNER-1 B", new DeviceRgb(209, 247, 29)));

            Div inner2 = createColumnFlexContainer();
            inner2.setWidth(UnitValue.createPercentValue(45)).setMargin(5);
            inner2.add(coloredDiv("INNER-2 A", new DeviceRgb(78, 151, 205)));
            inner2.add(coloredDiv("INNER-2 B", new DeviceRgb(255, 165, 0)));

            outer.add(inner1);
            outer.add(inner2);

            document.add(outer);
            document.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 6)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void asymmetricNestedFlexWithDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedFlexAsymmetricDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            Div outer = createRowFlexContainer();

            Div leftCol = createColumnFlexContainer();
            leftCol.setWidth(UnitValue.createPercentValue(45)).setMargin(5);
            Div leftInner = createColumnFlexContainer();
            leftInner.add(coloredDiv("L-DEEP A", new DeviceRgb(65, 151, 29)));
            leftInner.add(coloredDiv("L-DEEP B", new DeviceRgb(209, 247, 29)));
            leftCol.add(coloredDiv("L-TOP", new DeviceRgb(78, 151, 205)));
            leftCol.add(leftInner);

            Div rightCol = createColumnFlexContainer();
            rightCol.setWidth(UnitValue.createPercentValue(45)).setMargin(5);
            rightCol.add(coloredDiv("R A", new DeviceRgb(255, 165, 0)));
            rightCol.add(coloredDiv("R B", new DeviceRgb(200, 100, 100)));
            rightCol.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 3)));

            outer.add(leftCol);
            outer.add(rightCol);

            document.add(outer);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedFlexOverflowWithStaticDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedFlexOverflowStaticMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setMargins(80, 80, 80, 80);

            Div outer = createColumnFlexContainer();
            for (int i = 0; i < 4; i++) {
                Div row = createRowFlexContainer();
                for (int j = 0; j < 3; j++) {
                    row.add(new Div()
                            .add(new Paragraph("R" + i + "C" + j + "\n" + TestResourceUtil.getByronStanza()))
                            .setWidth(UnitValue.createPercentValue(30))
                            .setBackgroundColor(j % 2 == 0
                                    ? new DeviceRgb(65, 151, 29)
                                    : new DeviceRgb(209, 247, 29))
                            .setMargin(4));
                }
                outer.add(row);
            }

            document.add(outer);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedFlexOverflowPageNumberSpecificMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedFlexOverflowPageNumMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(3, new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div outer = createColumnFlexContainer();
            for (int i = 0; i < 3; i++) {
                Div row = createRowFlexContainer();
                for (int j = 0; j < 3; j++) {
                    row.add(new Div()
                            .add(new Paragraph("R" + i + "C" + j + "\n" + TestResourceUtil.getByronStanza()))
                            .setWidth(UnitValue.createPercentValue(30))
                            .setBackgroundColor(j % 2 == 0
                                    ? new DeviceRgb(65, 151, 29)
                                    : new DeviceRgb(209, 247, 29))
                            .setMargin(4));
                }
                outer.add(row);
            }

            document.add(outer);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedFlexSameMarginsAppliedTwiceTest()
            throws IOException, InterruptedException {
        String fileName = "nestedFlexSameMarginsTwice";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex1 = createColumnFlexContainer();
            Div row1 = createRowFlexContainer();
            row1.add(coloredDiv("S1 ITEM A", new DeviceRgb(65, 151, 29)));
            row1.add(coloredDiv("S1 ITEM B", new DeviceRgb(209, 247, 29)));
            flex1.add(row1);
            flex1.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 3)));

            Div flex2 = createColumnFlexContainer();
            Div row2 = createRowFlexContainer();
            row2.add(coloredDiv("S2 ITEM A", new DeviceRgb(78, 151, 205)));
            row2.add(coloredDiv("S2 ITEM B", new DeviceRgb(255, 165, 0)));
            flex2.add(row2);
            flex2.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 3)));

            document.add(flex1);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(flex2);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Third section — same margins again."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.SECTION_BREAK_IGNORED, count = 4),
            @LogMessage(messageTemplate = LayoutLogMessageConstant.AREA_BREAK_IGNORED, count = 4)})
    public void flexWithTableHeaderAndFooterWithAreaBreakAndSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "flexWithTableHeaderAndFooter";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            Div flex = createRowFlexContainer();

            Table table = new Table(3);

            Cell headerCell = new Cell().add(new Div()
                    .add(new Paragraph("Before section break"))
                    .add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())))
                    .add(new Paragraph("After section break")));
            table.addHeaderCell(headerCell);
            table.addHeaderCell(new Cell());
            table.addHeaderCell(new Cell());

            table.addCell("Table cell content 1");
            table.addCell("Table cell content 2");
            table.addCell("Table cell content 3");

            Cell footerCell = new Cell().add(new Div()
                    .add(new Paragraph("Before area break"))
                    .add(new AreaBreak())
                    .add(new Paragraph("After area break")));
            table.addFooterCell(footerCell);
            table.addFooterCell(new Cell());
            table.addFooterCell(new Cell());

            flex.add(table);
            flex.add(coloredDiv("Second element", new DeviceRgb(65, 151, 29)));
            document.add(flex);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    private static Div createRowFlexContainer() {
        Div flex = new Div();
        flex.setNextRenderer(new FlexContainerRenderer(flex));
        flex.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.ROW);
        flex.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.WRAP);
        flex.setProperty(Property.JUSTIFY_CONTENT, JustifyContent.FLEX_START);
        return flex;
    }

    private static Div createColumnFlexContainer() {
        Div flex = new Div();
        flex.setNextRenderer(new FlexContainerRenderer(flex));
        flex.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        flex.setProperty(Property.FLEX_WRAP, FlexWrapPropertyValue.NOWRAP);
        return flex;
    }

    private static Div coloredDiv(String label, DeviceRgb color) {
        return new Div()
                .add(new Paragraph(label))
                .setBackgroundColor(color)
                .setWidth(UnitValue.createPercentValue(30))
                .setMargin(5)
                .setPadding(8);
    }
}