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
import com.itextpdf.layout.element.GridContainer;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.grid.FlexValue;
import com.itextpdf.layout.properties.grid.TemplateValue;
import com.itextpdf.layout.properties.grid.GridFlow;
import com.itextpdf.layout.properties.grid.PercentValue;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
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
public class GridPageMarginsTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/GridPageMarginsTest/";
    private static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/layout/GridPageMarginsTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void gridContainerThenSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "gridSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer grid = createThreeColumnGrid();
            for (int i = 1; i <= 6; i++) {
                grid.add(coloredDiv("ITEM " + i, cellColor(i)));
            }

            document.add(grid);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Page 2 — margins1 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridThenTwoSectionBreaksInARowTest() throws IOException, InterruptedException {
        String fileName = "gridTwoSectionBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer grid = createThreeColumnGrid();
            for (int i = 1; i <= 6; i++) {
                grid.add(coloredDiv("ITEM " + i, cellColor(i)));
            }

            document.add(grid);
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
    public void multiPageGridThenSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "gridMultiPageSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer grid = createThreeColumnGrid();
            for (int i = 1; i <= 18; i++) {
                grid.add(new Div()
                        .add(new Paragraph("ITEM " + i + "\n" + TestResourceUtil.getByronStanza()))
                        .setBackgroundColor(cellColor(i)));
            }

            document.add(grid);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Post-grid section — margins1 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridSameMarginsAppliedTwiceTest() throws IOException, InterruptedException {
        String fileName = "gridSameMarginsTwice";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer grid1 = createThreeColumnGrid();
            for (int i = 1; i <= 6; i++) {
                grid1.add(coloredDiv("S1-" + i, cellColor(i)));
            }

            GridContainer grid2 = createThreeColumnGrid();
            for (int i = 1; i <= 6; i++) {
                grid2.add(coloredDiv("S2-" + i, cellColor(i + 3)));
            }

            document.add(grid1);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(grid2);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Third section — same margins again."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridAlternatingSectionAndAreaBreaksTest() throws IOException, InterruptedException {
        String fileName = "gridAltBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.add(buildSmallGrid("S1", 1));
            document.add(new AreaBreak());
            document.add(new Paragraph("Page 2 — no special margins."));
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(buildSmallGrid("S3", 2));
            document.add(new AreaBreak());
            document.add(new Paragraph("Page 4 — still margins1."));
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(buildSmallGrid("S5", 3));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void multiPageGridWithDocumentMarginsTest() throws IOException, InterruptedException {
        String fileName = "gridMultiPageDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            GridContainer grid = createThreeColumnGrid();
            for (int i = 1; i <= 18; i++) {
                grid.add(new Div()
                        .add(new Paragraph("CELL " + i + "\n" + TestResourceUtil.getByronStanza()))
                        .setBackgroundColor(cellColor(i)));
            }

            document.add(grid);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridWithPerPageDocumentMarginsTest() throws IOException, InterruptedException {
        String fileName = "gridPerPageDocMargins";
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

            GridContainer grid = createThreeColumnGrid();
            for (int i = 1; i <= 15; i++) {
                grid.add(new Div()
                        .add(new Paragraph("CELL " + i + "\n" + TestResourceUtil.getByronStanza()))
                        .setBackgroundColor(cellColor(i)));
            }

            document.add(grid);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridDocumentMarginsOverriddenBySectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "gridDocMarginsOverriddenBySectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            GridContainer grid1 = createThreeColumnGrid();
            for (int i = 1; i <= 6; i++) {
                grid1.add(new Div()
                        .add(new Paragraph("S1-" + i + "\n" + TestResourceUtil.getByronStanza()))
                        .setBackgroundColor(cellColor(i)));
            }

            GridContainer grid2 = createTwoColumnGrid();
            for (int i = 1; i <= 4; i++) {
                grid2.add(coloredDiv("S2-" + i, cellColor(i + 2)));
            }

            document.add(grid1);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(grid2);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridWithStaticDocumentMarginsAndSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "gridStaticMarginsAndSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setMargins(80, 80, 80, 80);

            GridContainer grid1 = createThreeColumnGrid();
            for (int i = 1; i <= 6; i++) {
                grid1.add(coloredDiv("BEFORE-" + i, cellColor(i)));
            }

            GridContainer grid2 = createTwoColumnGrid();
            for (int i = 1; i <= 4; i++) {
                grid2.add(coloredDiv("AFTER-" + i, cellColor(i + 3)));
            }

            document.add(grid1);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(grid2);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridWithPageNumberSpecificMarginsTest() throws IOException, InterruptedException {
        String fileName = "gridPageNumMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(2, new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            GridContainer grid = createThreeColumnGrid();
            for (int i = 1; i <= 18; i++) {
                grid.add(new Div()
                        .add(new Paragraph("CELL " + i + "\n" + TestResourceUtil.getByronStanza()))
                        .setBackgroundColor(cellColor(i)));
            }

            document.add(grid);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridContainerWithElementMarginsAndSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "gridElemMarginsSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer grid = createThreeColumnGrid();
            grid.setMargins(40, 40, 40, 40)
                    .setBackgroundColor(new DeviceRgb(220, 220, 220));
            for (int i = 1; i <= 6; i++) {
                grid.add(coloredDiv("ITEM " + i, cellColor(i)));
            }

            document.add(grid);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())));
            document.add(new Paragraph("Page 2 — section margins1 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridItemsWithElementMarginsAndDocumentPageMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "gridItemMarginsDocPageMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            GridContainer grid = createThreeColumnGrid();

            grid.add(new Div()
                    .add(new Paragraph("LARGE MARGIN\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(65, 151, 29))
                    .setMargins(20, 15, 20, 15));

            grid.add(new Div()
                    .add(new Paragraph("NO MARGIN\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(209, 247, 29))
                    .setMargin(0));

            grid.add(new Div()
                    .add(new Paragraph("LARGE PADDING\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(78, 151, 205))
                    .setPaddings(20, 20, 20, 20));

            grid.add(new Div()
                    .add(new Paragraph("MIXED\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(255, 165, 0))
                    .setMarginTop(30).setPaddingBottom(30));

            grid.add(new Div()
                    .add(new Paragraph("DEFAULT\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(200, 100, 100)));

            grid.add(new Div()
                    .add(new Paragraph("SMALL PADDING\n" + TestResourceUtil.getByronStanza()))
                    .setBackgroundColor(new DeviceRgb(100, 200, 100))
                    .setPadding(5));

            document.add(grid);
            document.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 5)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridFractionColumnsThenSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "gridFractionColsSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer grid = new GridContainer();
            List<TemplateValue> columns = new ArrayList<>();
            columns.add(new FlexValue(1));
            columns.add(new FlexValue(2));
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, columns);
            grid.setProperty(Property.GRID_FLOW, GridFlow.ROW);
            for (int i = 1; i <= 6; i++) {
                grid.add(coloredDiv("ITEM " + i, cellColor(i)));
            }

            document.add(grid);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(new Paragraph("Page 2 — margins2 active."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridMixedColumnSizingWithDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "gridMixedColsDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            GridContainer grid = new GridContainer();
            List<TemplateValue> columns = new ArrayList<>();
            columns.add(new FlexValue(1));
            columns.add(new FlexValue(2));
            columns.add(new FlexValue(1));
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, columns);
            grid.setProperty(Property.GRID_FLOW, GridFlow.ROW);
            for (int i = 1; i <= 9; i++) {
                grid.add(new Div()
                        .add(new Paragraph("CELL " + i + "\n" + TestResourceUtil.getByronStanza()))
                        .setBackgroundColor(cellColor(i)));
            }

            document.add(grid);
            document.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 4)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void gridPercentColumnsWithPageSizeSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "gridPercentColsPageSizeSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer grid1 = new GridContainer();
            List<TemplateValue> columns1 = new ArrayList<>();
            columns1.add(new PercentValue(50));
            columns1.add(new PercentValue(50));
            grid1.setProperty(Property.GRID_TEMPLATE_COLUMNS, columns1);
            grid1.setProperty(Property.GRID_FLOW, GridFlow.ROW);
            for (int i = 1; i <= 4; i++) {
                grid1.add(coloredDiv("A4-" + i, cellColor(i)));
            }

            GridContainer grid2 = new GridContainer();
            List<TemplateValue> columns2 = new ArrayList<>();
            columns2.add(new PercentValue(50));
            columns2.add(new PercentValue(50));
            grid2.setProperty(Property.GRID_TEMPLATE_COLUMNS, columns2);
            grid2.setProperty(Property.GRID_FLOW, GridFlow.ROW);
            for (int i = 1; i <= 4; i++) {
                grid2.add(coloredDiv("A4R-" + i, cellColor(i + 2)));
            }

            document.add(grid1);
            document.add(new SectionBreak(PageSize.A4.rotate(),
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(grid2);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.AREA_BREAK_IGNORED)})
    public void areaBreakInsideNestedGridCellWithDocumentMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedGridCellAreaBreakDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            GridContainer outer = createTwoColumnGrid();
            outer.setBackgroundColor(new DeviceRgb(220, 220, 220));

            GridContainer innerLeft = createTwoColumnGrid();
            innerLeft.add(coloredDiv("LEFT-1", new DeviceRgb(65, 151, 29)));
            innerLeft.add(coloredDiv("LEFT-2", new DeviceRgb(209, 247, 29)));

            Div breakCell = new Div()
                    .add(new Paragraph("Before break."))
                    .add(new AreaBreak())
                    .add(new Paragraph("After break."));
            innerLeft.add(breakCell);
            innerLeft.add(coloredDiv("LEFT-4", new DeviceRgb(78, 151, 205)));

            GridContainer innerRight = createTwoColumnGrid();
            innerRight.add(coloredDiv("RIGHT-1", new DeviceRgb(255, 165, 0)));
            innerRight.add(coloredDiv("RIGHT-2", new DeviceRgb(200, 100, 100)));
            innerRight.add(coloredDiv("RIGHT-3", new DeviceRgb(100, 200, 100)));
            innerRight.add(coloredDiv("RIGHT-4", new DeviceRgb(65, 151, 29)));

            outer.add(innerLeft);
            outer.add(innerRight);

            document.add(outer);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedGridsWithDocumentMarginsTest() throws IOException, InterruptedException {
        String fileName = "nestedGridsDocMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            GridContainer outer = createThreeColumnGrid();

            for (int col = 0; col < 3; col++) {
                GridContainer inner = createTwoColumnGrid();
                for (int i = 1; i <= 4; i++) {
                    inner.add(new Div()
                            .add(new Paragraph("C" + col + "-" + i + "\n" + TestResourceUtil.getByronStanza()))
                            .setBackgroundColor(cellColor(col * 2 + i)));
                }
                outer.add(inner);
            }

            document.add(outer);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedGridsAroundAreaBreakTest() throws IOException, InterruptedException {
        String fileName = "nestedGridsAreaBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer outer1 = createTwoColumnGrid();
            for (int col = 0; col < 2; col++) {
                GridContainer inner = createThreeColumnGrid();
                for (int i = 1; i <= 3; i++) {
                    inner.add(coloredDiv("P1-C" + col + "-" + i, cellColor(col * 3 + i)));
                }
                outer1.add(inner);
            }

            GridContainer outer2 = createTwoColumnGrid();
            for (int col = 0; col < 2; col++) {
                GridContainer inner = createThreeColumnGrid();
                for (int i = 1; i <= 3; i++) {
                    inner.add(coloredDiv("P2-C" + col + "-" + i, cellColor(col * 2 + i + 1)));
                }
                outer2.add(inner);
            }

            document.add(outer1);
            document.add(new AreaBreak(PageSize.A5));
            document.add(outer2);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedGridsWithPageSizeSectionBreakTest()
            throws IOException, InterruptedException {
        String fileName = "nestedGridsPageSizeSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer outer1 = createThreeColumnGrid();
            for (int col = 0; col < 3; col++) {
                GridContainer inner = createTwoColumnGrid();
                for (int i = 1; i <= 2; i++) {
                    inner.add(coloredDiv("S1-C" + col + "-" + i, cellColor(col + i)));
                }
                outer1.add(inner);
            }

            GridContainer outer2 = createThreeColumnGrid();
            for (int col = 0; col < 3; col++) {
                GridContainer inner = createTwoColumnGrid();
                for (int i = 1; i <= 2; i++) {
                    inner.add(coloredDiv("S2-C" + col + "-" + i, cellColor(col + i + 2)));
                }
                outer2.add(inner);
            }

            document.add(outer1);
            document.add(new SectionBreak(PageSize.A4.rotate(),
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(outer2);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void deeplyNestedGridsWithDocumentAndSectionMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "deepNestedGridsMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 != 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            GridContainer outer = createTwoColumnGrid();
            outer.setBackgroundColor(new DeviceRgb(240, 240, 240));

            for (int o = 0; o < 2; o++) {
                GridContainer mid = createTwoColumnGrid();
                for (int m = 0; m < 2; m++) {
                    GridContainer inner = createTwoColumnGrid();
                    for (int i = 1; i <= 2; i++) {
                        inner.add(new Div()
                                .add(new Paragraph("O" + o + "M" + m + "I" + i + "\n" + TestResourceUtil.getByronStanza()))
                                .setBackgroundColor(cellColor(o * 4 + m * 2 + i)));
                    }
                    mid.add(inner);
                }
                outer.add(mid);
            }

            document.add(outer);
            document.add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2())));
            document.add(new Paragraph("Final section — margins2 override document margins."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate =
            LayoutLogMessageConstant.GRID_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK)})
    public void areaBreakDirectlyInsideGridContainerTest() throws IOException, InterruptedException {
        String fileName = "areaBreakDirectlyInsideGridContainer";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            GridContainer grid = createThreeColumnGrid();
            grid.add(coloredDiv("BEFORE-1", new DeviceRgb(65, 151, 29)));
            grid.add(coloredDiv("BEFORE-2", new DeviceRgb(209, 247, 29)));
            grid.add(coloredDiv("BEFORE-3", new DeviceRgb(78, 151, 205)));
            grid.add(new AreaBreak());
            grid.add(coloredDiv("AFTER-1", new DeviceRgb(255, 165, 0)));
            grid.add(coloredDiv("AFTER-2", new DeviceRgb(200, 100, 100)));
            grid.add(coloredDiv("AFTER-3", new DeviceRgb(100, 200, 100)));

            document.add(grid);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    LayoutLogMessageConstant.GRID_CONTAINER_SHOULD_NOT_CONTAIN_AREA_OR_SECTION_BREAK)
    })
    public void sectionBreakDirectlyInsideGridContainerTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakDirectlyInsideGridContainer";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins2()));

            GridContainer grid = createThreeColumnGrid();
            grid.add(coloredDiv("BEFORE-1", new DeviceRgb(65, 151, 29)));
            grid.add(coloredDiv("BEFORE-2", new DeviceRgb(209, 247, 29)));
            grid.add(coloredDiv("BEFORE-3", new DeviceRgb(78, 151, 205)));
            grid.add(new SectionBreak(PageSize.A5));
            grid.add(coloredDiv("AFTER-1", new DeviceRgb(255, 165, 0)));
            grid.add(coloredDiv("AFTER-2", new DeviceRgb(200, 100, 100)));
            grid.add(coloredDiv("AFTER-3", new DeviceRgb(100, 200, 100)));

            document.add(grid);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.SECTION_BREAK_IGNORED)})
    public void sectionBreakInsideNestedGridCellTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakInNestedGrid";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer outer = createTwoColumnGrid();

            GridContainer inner = createTwoColumnGrid();
            inner.add(coloredDiv("BEFORE", new DeviceRgb(65, 151, 29)));

            Div breakCell = new Div()
                    .add(new Paragraph("Before section break."))
                    .add(new SectionBreak(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1())))
                    .add(new Paragraph("After section break."));
            inner.add(breakCell);
            inner.add(coloredDiv("AFTER", new DeviceRgb(209, 247, 29)));

            outer.add(inner);
            outer.add(coloredDiv("OTHER CELL", new DeviceRgb(78, 151, 205)));

            document.add(outer);

            document.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 6)));
            document.add(new Paragraph("Page 2 — PageMargins1 should be active here if SectionBreak was honoured."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.AREA_BREAK_IGNORED)})
    public void areaBreakInsideNestedGridCellTest() throws IOException, InterruptedException {
        String fileName = "areaBreakInNestedGrid";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer outer = createTwoColumnGrid();

            GridContainer inner = createTwoColumnGrid();
            inner.add(coloredDiv("BEFORE", new DeviceRgb(65, 151, 29)));

            Div breakCell = new Div()
                    .add(new Paragraph("Before area break."))
                    .add(new AreaBreak())
                    .add(new Paragraph("After area break."));
            inner.add(breakCell);
            inner.add(coloredDiv("AFTER", new DeviceRgb(209, 247, 29)));

            outer.add(inner);
            outer.add(coloredDiv("OTHER CELL", new DeviceRgb(78, 151, 205)));

            document.add(outer);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void nestedGridInnerElementMarginsWithDocumentPageMarginsTest()
            throws IOException, InterruptedException {
        String fileName = "nestedGridInnerElemMarginsDocPageMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            document.setPageMargins(pageNum -> pageNum % 2 == 0,
                    new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            GridContainer outer = createTwoColumnGrid();
            outer.setBackgroundColor(new DeviceRgb(220, 220, 220));

            GridContainer innerLeft = createTwoColumnGrid();
            innerLeft.setMargins(20, 20, 20, 20)
                    .setBackgroundColor(new DeviceRgb(200, 200, 255));
            innerLeft.add(coloredDiv("L-1", new DeviceRgb(65, 151, 29)));
            innerLeft.add(coloredDiv("L-2", new DeviceRgb(209, 247, 29)));
            innerLeft.add(coloredDiv("L-3", new DeviceRgb(78, 151, 205)));
            innerLeft.add(coloredDiv("L-4", new DeviceRgb(255, 165, 0)));

            GridContainer innerRight = createTwoColumnGrid();
            innerRight.add(new Div()
                    .add(new Paragraph("R-1 LARGE MARGIN"))
                    .setBackgroundColor(new DeviceRgb(65, 151, 29))
                    .setMargins(15, 10, 15, 10));
            innerRight.add(new Div()
                    .add(new Paragraph("R-2 NO MARGIN"))
                    .setBackgroundColor(new DeviceRgb(209, 247, 29))
                    .setMargin(0));
            innerRight.add(new Div()
                    .add(new Paragraph("R-3 LARGE PADDING"))
                    .setBackgroundColor(new DeviceRgb(78, 151, 205))
                    .setPaddings(15, 15, 15, 15));
            innerRight.add(new Div()
                    .add(new Paragraph("R-4 DEFAULT"))
                    .setBackgroundColor(new DeviceRgb(255, 165, 0)));

            outer.add(innerLeft);
            outer.add(innerRight);

            document.add(outer);
            document.add(new Paragraph(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 4)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.AREA_BREAK_IGNORED, count = 5),
            @LogMessage(messageTemplate = LayoutLogMessageConstant.SECTION_BREAK_IGNORED, count = 5)})
    public void gridWithTableHeaderAndFooterWithAreaBreakAndSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "gridWithTableHeaderAndFooter";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            GridContainer gridContainer = createTwoColumnGrid();

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

            gridContainer.add(table);
            gridContainer.add(coloredDiv("Second column div", new DeviceRgb(65, 151, 29)));
            document.add(gridContainer);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    private static GridContainer createThreeColumnGrid() {
        GridContainer grid = new GridContainer();
        List<TemplateValue> columns = new ArrayList<>();
        columns.add(new FlexValue(1));
        columns.add(new FlexValue(1));
        columns.add(new FlexValue(1));
        grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, columns);
        grid.setProperty(Property.GRID_FLOW, GridFlow.ROW);
        return grid;
    }

    private static GridContainer createTwoColumnGrid() {
        GridContainer grid = new GridContainer();
        List<TemplateValue> columns = new ArrayList<>();
        columns.add(new FlexValue(1));
        columns.add(new FlexValue(1));
        grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, columns);
        grid.setProperty(Property.GRID_FLOW, GridFlow.ROW);
        return grid;
    }

    private static GridContainer buildSmallGrid(String prefix, int colorOffset) {
        GridContainer grid = createThreeColumnGrid();
        for (int i = 1; i <= 3; i++) {
            grid.add(coloredDiv(prefix + "-" + i, cellColor(i + colorOffset)));
        }
        return grid;
    }

    private static DeviceRgb cellColor(int index) {
        DeviceRgb[] palette = {
                new DeviceRgb(65, 151, 29),
                new DeviceRgb(209, 247, 29),
                new DeviceRgb(78, 151, 205),
                new DeviceRgb(255, 165, 0),
                new DeviceRgb(200, 100, 100),
                new DeviceRgb(100, 200, 100)
        };
        return palette[(index - 1) % palette.length];
    }

    private static Div coloredDiv(String label, DeviceRgb color) {
        return new Div()
                .add(new Paragraph(label))
                .setBackgroundColor(color)
                .setMargin(4)
                .setPadding(6);
    }
}