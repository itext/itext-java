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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.TableRenderer;
import com.itextpdf.layout.testutil.PageMarginsTestUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("IntegrationTest")
public class PageMarginsTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/PageMarginsTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/PageMarginsTest/";

    private static final String TEXT_BYRON = "When a man hath no freedom to fight for at home,\n" +
            "    Let him combat for that of his neighbours;\n" +
            "Let him think of the glories of Greece and of Rome,\n" +
            "    And get knocked on the head for his labours.\n" +
            "\n" +
            "To do good to Mankind is the chivalrous plan,\n" +
            "    And is always as nobly requited;\n" +
            "Then battle for Freedom wherever you can,\n" +
            "    And, if not shot or hanged, you'll get knighted.";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void pageMarginsComplexTest() throws IOException, InterruptedException {
        String fileName = "pageMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.addNewPage();

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 5; i++) {
                p.add(TEXT_BYRON);
            }

            SectionBreak sectionBreak = new SectionBreak().setPageMargins(new PageMarginBoxes(elements));
            SectionBreak sectionBreak2 = new SectionBreak(new PageMarginBoxes(elements2));

            Div div1 = new Div();
            Div div2 = new Div();
            div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            div2.add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));
            document.add(sectionBreak);
            document.add(div1);

            document.add(sectionBreak2);
            document.add(div2);

            Div div = new Div().setBackgroundColor(new DeviceRgb(78, 151, 205))
                    .add(p)
                    .add(new SectionBreak())
                    .add(div1)
                    .add(sectionBreak2)
                    .add(sectionBreak)
                    .add(div2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void pageSizesTest() throws IOException, InterruptedException {
        String fileName = "pageSizes";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 5; i++) {
                p.add(TEXT_BYRON);
            }

            SectionBreak sectionBreak = new SectionBreak().setPageSize(PageSize.A4.rotate());
            SectionBreak sectionBreak2 = new SectionBreak().setPageSize(PageSize.A5);

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(sectionBreak);
            document.add(div1);

            document.add(sectionBreak2);
            document.add(div2);

            Div div = new Div().setBackgroundColor(new DeviceRgb(78, 151, 205))
                    .add(p)
                    .add(new SectionBreak())
                    .add(div1)
                    .add(sectionBreak2)
                    .add(sectionBreak)
                    .add(div2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void sectionBreakAfterAreaBreakTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakAfterAreaBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            Paragraph p = new Paragraph(TEXT_BYRON);

            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));
            SectionBreak sectionBreak2 = new SectionBreak(new PageMarginBoxes(elements2));

            Div div1 = new Div();
            Div div2 = new Div();
            div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            div2.add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(sectionBreak);
            document.add(div1);
            document.add(new AreaBreak());
            document.add(sectionBreak2);
            document.add(div2);

            Div div = new Div().setBackgroundColor(new DeviceRgb(78, 151, 205))
                    .add(sectionBreak)
                    .add(div1)
                    .add(new AreaBreak())
                    .add(sectionBreak2)
                    .add(div2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void sectionBreakAfterAreaBreakPageSizeTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakAfterAreaBreakPageSize";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph(TEXT_BYRON);

            SectionBreak sectionBreak = new SectionBreak(PageSize.A4.rotate());
            SectionBreak sectionBreak2 = new SectionBreak(PageSize.A5.rotate());

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            // Page 1 will be created with the PageSize from sectionBreak.
            document.add(sectionBreak);
            document.add(div1);
            // Page 2 will be created with the PageSize from AreaBreak.
            document.add(new AreaBreak(PageSize.A5));
            // Page 3 will be created with the PageSize from sectionBreak2.
            document.add(sectionBreak2);
            document.add(div2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void twoSectionBreaksInARowTest() throws IOException, InterruptedException {
        String fileName = "twoSectionBreaksInARow";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            Paragraph p = new Paragraph(TEXT_BYRON);

            SectionBreak sectionBreak = new SectionBreak(PageSize.A4.rotate(), new PageMarginBoxes(elements));
            SectionBreak sectionBreak2 = new SectionBreak(PageSize.A5, new PageMarginBoxes(elements2));

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            // In such cases we'll add new empty page with page size and page margins from the 1st sectionBreak,
            // and after one more new page with page size and page margins from the 2nd sectionBreak2.
            document.add(div1).add(sectionBreak).add(sectionBreak2).add(div2);

            Div div = new Div().add(div1).add(sectionBreak).add(sectionBreak2).add(div2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void sectionBreakAfterContentTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakAfterContent";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 5; i++) {
                p.add(TEXT_BYRON);
            }

            SectionBreak sectionBreak = new SectionBreak(PageSize.A3.rotate(), new PageMarginBoxes(elements));

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(div1).add(sectionBreak).add(div2).add(new SectionBreak());

            Div div = new Div().add(div1).add(sectionBreak).add(div2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void sectionBreakWithSameMarginsAfterContentTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakWithSameMarginsAfterContent";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 5; i++) {
                p.add(TEXT_BYRON);
            }

            List<PageMarginContent> pageMargins = PageMarginsTestUtil.getPageMargins1();
            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(pageMargins));
            SectionBreak sectionBreak1 = new SectionBreak(new PageMarginBoxes(pageMargins));
            SectionBreak sectionBreak2 = new SectionBreak(PageSize.A3, new PageMarginBoxes(pageMargins));

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(sectionBreak).add(div1)
                    .add(sectionBreak1).add(div2)
                    .add(sectionBreak2).add(div1);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void differentSectionBreaksTest() throws IOException, InterruptedException {
        String fileName = "differentSectionBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            Paragraph p = new Paragraph(TEXT_BYRON);

            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));
            SectionBreak sectionBreak2 = new SectionBreak(PageSize.A4.rotate(), new PageMarginBoxes(elements2));

            Div div1 = new Div();
            Div div2 = new Div();
            div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            div2.add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(sectionBreak)
                    .add(div1)
                    .add(sectionBreak2)
                    .add(div2);
            Div div = new Div().add(sectionBreak)
                    .add(div1)
                    .add(sectionBreak2)
                    .add(div2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void staticMarginsTest() throws IOException, InterruptedException {
        String fileName = "staticMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            // Set static margins
            document.setMargins(100, 100, 100, 100);

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            List<PageMarginContent> elements3 = new ArrayList<>();
            elements3.add(new PageMarginContent(MarginBoxName.BOTTOM, new Div()
                    .add(new Paragraph("TEST BOTTOM MARGIN\nWITH SOME FOOTNOTE"))
                    .setBackgroundColor(ColorConstants.CYAN)
                    .setMinHeight(50)));

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 5; i++) {
                p.add(TEXT_BYRON);
            }
            Div div1 = new Div();
            Div div2 = new Div();
            div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            div2.add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(div1)
                    .add(new SectionBreak(new PageMarginBoxes(elements)))
                    .add(div2)
                    .add(new SectionBreak())
                    .add(div1)
                    .add(new SectionBreak(new PageMarginBoxes(elements3)))
                    .add(div2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void pageMarginsViaDocumentTest() throws IOException, InterruptedException {
        String fileName = "pageMarginsViaDocument";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();
            document.setPageMargins(1, new PageMarginBoxes(elements));
            document.setPageMargins(pageNum -> pageNum % 2 == 0, new PageMarginBoxes(elements2));
            document.setPageMargins(pageNum -> {
                if (pageNum % 2 != 0) {
                    List<PageMarginContent> margins = new ArrayList<>();
                    margins.add(new PageMarginContent(MarginBoxName.TOP, new Div()
                            .add(new Paragraph("Function is used for Page Margins"))
                            .setBackgroundColor(ColorConstants.PINK)
                            .setTextAlignment(TextAlignment.CENTER)));
                    margins.add(new PageMarginContent(MarginBoxName.BOTTOM, new Div()
                            .add(new Paragraph("Page " + pageNum))
                            .setBackgroundColor(ColorConstants.PINK)
                            .setTextAlignment(TextAlignment.CENTER)));
                    return new PageMarginBoxes(margins);
                }
                return null;
            });

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 5; i++) {
                p.add(TEXT_BYRON);
            }

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));
            document.add(div1);
            document.add(div2);
            pdfDocument.addNewPage();
            pdfDocument.addNewPage();
            pdfDocument.addNewPage(PageSize.A4.rotate());
            pdfDocument.addNewPage(PageSize.A4.rotate());
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void pageMarginsViaDocumentAndSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "pageMarginsViaDocumentAndSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            document.setPageMargins(pageNum -> pageNum > 0 && pageNum % 2 == 0, new PageMarginBoxes(elements));
            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements2));

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 7; i++) {
                p.add(TEXT_BYRON);
            }

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));
            document.add(new Paragraph(TEXT_BYRON));
            document.add(sectionBreak);
            document.add(div1);
            document.add(div2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void pageSizeViaAreaBreakAndSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "pageSizeViaAreaBreakAndSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            SectionBreak sectionBreak = new SectionBreak(PageSize.A5);
            AreaBreak areaBreak = new AreaBreak(PageSize.A5.rotate());

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 7; i++) {
                p.add(TEXT_BYRON);
            }

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));
            document.add(sectionBreak);
            document.add(div1);
            document.add(areaBreak);
            document.add(div2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void fixedPositionTest() throws IOException, InterruptedException {
        String fileName = "fixedPosition";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            Paragraph p = new Paragraph(TEXT_BYRON);

            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            div1.setFixedPosition(0, 100, 300);

            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(div1)
                    .add(sectionBreak)
                    .add(div2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void relativePositionTest() throws IOException, InterruptedException {
        String fileName = "relativePosition";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();
            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            Paragraph p = new Paragraph(TEXT_BYRON);

            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            div1.setRelativePosition(50, 50, 0, 0);

            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(div1)
                    .add(sectionBreak)
                    .add(div2);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void staticPageMarginContentTest() throws IOException, InterruptedException {
        String fileName = "staticPageMarginContent";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = Arrays.asList(
                    new PageMarginContent(MarginBoxName.TOP, 30),
                    new PageMarginContent(MarginBoxName.RIGHT, 60),
                    new PageMarginContent(MarginBoxName.BOTTOM, 200.5f),
                    new PageMarginContent(MarginBoxName.LEFT, 150)
            );

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 5; i++) {
                p.add(TEXT_BYRON);
            }

            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));

            Div div1 = new Div();
            div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));

            document.add(sectionBreak)
                    .add(div1);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void staticAndDynamicPageMarginContentTest() throws IOException, InterruptedException {
        String fileName = "staticAndDynamicPageMarginContent";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = Arrays.asList(
                    new PageMarginContent(MarginBoxName.TOP, new Div()
                        .add(new Paragraph("TEST TOP MARGIN"))
                        .setBackgroundColor(ColorConstants.PINK).setHeight(100)),
                    new PageMarginContent(MarginBoxName.RIGHT, new Div()
                        .add(new Paragraph("TEST RIGHT MARGIN")
                        .setBackgroundColor(ColorConstants.YELLOW).setWidth(150))),
                    new PageMarginContent(MarginBoxName.BOTTOM, 200),
                    new PageMarginContent(MarginBoxName.LEFT, 50)
            );

            Paragraph p = new Paragraph(TEXT_BYRON);
            for (int i = 0; i < 5; i++) {
                p.add(TEXT_BYRON);
            }

            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));

            Div div1 = new Div();
            div1.add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));

            document.add(sectionBreak)
                    .add(div1);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void registerPageMarginsHeaderTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument);

        int columnNum = 1;
        String values = "red;loop;long";
        addFooterTable(columnNum, values, document);

        columnNum = 2;
        values = "footertext;blurb";
        addFooterTable(columnNum, values, document);
        Assertions.assertDoesNotThrow(() -> document.close());
    }

    private void addFooterTable(int numColumns, String values, Document document) {
        String[] tableValues = values.split(";");
        Table tab = new Table(numColumns, false);
        for (String tableVal : tableValues) {
            tab.addCell(tableVal);
        }
        tab.useAllAvailableWidth();
        PdfDocument pdfDocument = document.getPdfDocument();
        AbstractPdfDocumentEventHandler handler = new TableHandler(tab, document);
        document.setMargins(document.getTopMargin(), 36, 36, 36);
        pdfDocument.addEventHandler(PdfDocumentEvent.INSERT_PAGE, handler);
    }

    private static class TableHandler extends AbstractPdfDocumentEventHandler {
        private final Table table;
        private final Document doc;

        public TableHandler(Table table, Document doc) {
            this.table = table;
            this.doc = doc;
        }

        /**
         * Returns the table height in float.
         *
         * @return table height
         */
        public float getTableHeight() {
            float height = 0.0f;
            if (table != null) {
                TableRenderer renderer = (TableRenderer) table.createRendererSubTree();
                renderer.setParent(new DocumentRenderer(doc));
                LayoutResult result = renderer.layout(new LayoutContext(new LayoutArea(0, PageSize.A4)));
                height = result.getOccupiedArea().getBBox().getHeight();
            }
            return height;
        }

        private void addTable(Rectangle pageSize, PdfCanvas pdfCanvas) {
            Rectangle rect = new Rectangle((pageSize.getX() + doc.getLeftMargin()),
                    (pageSize.getBottom() + doc.getBottomMargin()),
                    (pageSize.getWidth() - doc.getRightMargin() - doc.getLeftMargin()),
                    getTableHeight());
            try (Canvas canvasForTable = new Canvas(pdfCanvas, rect)) {
                canvasForTable.add(table);
            }
        }

        /**
         * Adds content to the bottom of the page.
         *
         * @param event event data
         */
        @Override
        public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(
                    page.getLastContentStream(), page.getResources(), pdf);
            if (table != null) {
                addTable(pageSize, pdfCanvas);
            }
        }
    }
}
