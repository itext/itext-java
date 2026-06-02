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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.FootnoteNumberingConfig;
import com.itextpdf.layout.properties.margins.FootnoteNumberingType;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;

@Tag("IntegrationTest")
public class FootnotePropertiesTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/FootnotePropertiesTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/FootnotePropertiesTest/";

    public static Iterable<Object[]> numberingType() {
        return Arrays.asList(new Object[][]{
                {FootnoteNumberingType.DECIMAL},
                {FootnoteNumberingType.ROMAN_LOWER},
                {FootnoteNumberingType.ROMAN_UPPER},
                {FootnoteNumberingType.ENGLISH_LOWER},
                {FootnoteNumberingType.ENGLISH_UPPER},
                {FootnoteNumberingType.GREEK_LOWER},
                {FootnoteNumberingType.GREEK_UPPER}
        });
    }

    public static Iterable<Object[]> numberingConfig() {
        return Arrays.asList(new Object[][]{
                {FootnoteNumberingConfig.PER_PAGE},
                {FootnoteNumberingConfig.PER_SECTION},
                {FootnoteNumberingConfig.PER_DOCUMENT}
        });
    }

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("numberingType")
    public void footnoteNumberingTypeTest(FootnoteNumberingType numberingType)
            throws IOException, InterruptedException {
        String fileName = "footnoteNumberingType_" + numberingType.toString();
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            document.setFootnotesProperties(new FootnotesProperties()
                    .setFootnoteNumberingType(numberingType)
                    .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_DOCUMENT));

            Footnote footnote = new Footnote("Footnote text");
            FootnoteAnchor anchor = new FootnoteAnchor(footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            FootnoteAnchor anchor2 = new FootnoteAnchor(footnote2);

            Table table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            anchor = new FootnoteAnchor(footnote);
            footnote2 = new Footnote("Footnote text 5");
            anchor2 = new FootnoteAnchor(footnote2);
            table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 1) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 5) {
                    paragraph.add(new FootnoteAnchor(new Footnote("Footnote text 4")));
                }
                if (i == 23) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i < 4) {
                    table.addHeaderCell(new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                } else if (i > 19) {
                    table.addFooterCell(new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.BLUE, 2)));
                } else {
                    table.addCell(paragraph);
                }
            }

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" +
                    TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnoteTableFooterNewPageTest()
            throws IOException, InterruptedException {
        String fileName = "footnoteTableFooterNewPage";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        FootnoteNumberingType numberingType = FootnoteNumberingType.DECIMAL;
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            document.setFootnotesProperties(new FootnotesProperties()
                    .setFootnoteNumberingType(numberingType)
                    .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_DOCUMENT));

            Footnote footnote = new Footnote("Footnote text");
            FootnoteAnchor anchor = new FootnoteAnchor(footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            FootnoteAnchor anchor2 = new FootnoteAnchor(footnote2);

            Table table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            anchor = new FootnoteAnchor(footnote);
            footnote2 = new Footnote("Footnote text 5");
            anchor2 = new FootnoteAnchor(footnote2);
            table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 1) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 15) {
                    paragraph.add(new FootnoteAnchor(new Footnote("Footnote text 4\n\n")));
                }
                if (i == 23) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i < 4) {
                    table.addHeaderCell(new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                } else if (i > 19) {
                    table.addFooterCell(new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.BLUE, 2)));
                } else {
                    table.addCell(paragraph);
                }
            }

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" +
                    TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnoteTableFooterNewPage3Test()
            throws IOException, InterruptedException {
        String fileName = "footnoteTableFooterNewPage3";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        FootnoteNumberingType numberingType = FootnoteNumberingType.DECIMAL;
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            document.setFootnotesProperties(new FootnotesProperties()
                    .setFootnoteNumberingType(numberingType)
                    .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_DOCUMENT));

            Footnote footnote = new Footnote("Footnote text");
            FootnoteAnchor anchor = new FootnoteAnchor(footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            FootnoteAnchor anchor2 = new FootnoteAnchor(footnote2);

            Table table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            anchor = new FootnoteAnchor(footnote);
            footnote2 = new Footnote("Footnote text 5");
            anchor2 = new FootnoteAnchor(footnote2);
            table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 1) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 15) {
                    paragraph.add(new FootnoteAnchor(new Footnote("Footnote\ntext\n4")))
                            .setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 23) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" +
                    TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnoteTableFooterNewPage2Test()
            throws IOException, InterruptedException {
        String fileName = "footnoteTableFooterNewPage2";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        FootnoteNumberingType numberingType = FootnoteNumberingType.DECIMAL;
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {

            document.setFootnotesProperties(new FootnotesProperties()
                    .setFootnoteNumberingType(numberingType)
                    .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_DOCUMENT));

            document.add(new Div().setHeight(580).setWidth(500).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            Footnote footnote = new Footnote("Footnote text 1");
            FootnoteAnchor anchor = new FootnoteAnchor(footnote);
            Footnote footnote2 = new Footnote("Footnote text 3");
            FootnoteAnchor anchor2 = new FootnoteAnchor(footnote2);
            Table table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 1) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 15) {
                    paragraph.add(new FootnoteAnchor(new Footnote("Footnote\ntext\n2")))
                            .setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 23) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i < 4) {
                    table.addHeaderCell(new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.CYAN, 2)));
                } else if (i > 19) {
                    table.addFooterCell(new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.BLUE, 2)));
                } else {
                    table.addCell(paragraph);
                }
            }
            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("numberingConfig")
    public void footnoteNumberingConfigTest(FootnoteNumberingConfig numberingConfig)
            throws IOException, InterruptedException {
        String fileName = "footnoteNumberingConfig_" + numberingConfig.toString();
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            document.setFootnotesProperties(new FootnotesProperties()
                    .setFootnoteNumberingType(FootnoteNumberingType.DECIMAL)
                    .setFootnoteNumberingConfig(numberingConfig));

            for (int i = 1; i < 50; ++i) {
                Paragraph paragraph = new Paragraph("Paragraph " + i);
                Footnote footnote = new Footnote("Footnote text " + i);
                FootnoteAnchor anchor = new FootnoteAnchor(footnote);
                paragraph.add(anchor);
                document.add(paragraph);
                if (i % 15 == 0) {
                    document.add(new Paragraph("SECTION BREAK").setBackgroundColor(ColorConstants.GREEN));
                    document.add(new SectionBreak());
                    document.add(new Paragraph("NEW SECTION").setBackgroundColor(ColorConstants.GREEN));
                }
                if (i % 10 == 0) {
                    document.add(new Paragraph("PAGE BREAK").setBackgroundColor(ColorConstants.CYAN));
                    document.add(new AreaBreak());
                }
            }

        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnoteCustomStyleTest() throws IOException, InterruptedException {
        String fileName = "footnoteCustomStyle";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();

            Style footnoteAnchorLabelStyle = new Style().setMarginLeft(10).setMarginRight(10)
                    .setBackgroundColor(ColorConstants.YELLOW);
            footnoteAnchorLabelStyle.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(12));
            footnoteAnchorLabelStyle.setProperty(Property.TEXT_RISE, 0);

            document.setFootnotesProperties(new FootnotesProperties()
                    .setFootnotesContainerStyle(new Style()
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setBorder(new DashedBorder(ColorConstants.GREEN, 3)))
                    .setFootnoteAnchorLabelStyle(footnoteAnchorLabelStyle)
                    .setFootnoteNumberingType(FootnoteNumberingType.DECIMAL)
                    .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_DOCUMENT));

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());

            FootnoteAnchor anchor = new FootnoteAnchor(footnote);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor).add(
                            new FootnoteAnchor("dummy", new Footnote("One more")))
                    .add(new FootnoteAnchor(new Footnote("Two more")))
                    .add("\n" + TestResourceUtil.getByronStanza());

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 3));
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnoteCustomStyleSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "footnoteCustomStyleSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            FootnoteAnchor anchor = new FootnoteAnchor(footnote);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());

            Style footnoteAnchorStyle = new Style().setMarginLeft(10).setMarginRight(10)
                    .setBackgroundColor(ColorConstants.YELLOW);
            footnoteAnchorStyle.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(12));
            footnoteAnchorStyle.setProperty(Property.TEXT_RISE, 0);
            Div div = new Div()
                    .add(new SectionBreak().setFootnotesProperties(new FootnotesProperties()
                            .setFootnotesContainerStyle(new Style()
                                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                                    .setBorder(new DashedBorder(ColorConstants.GREEN, 3)))
                            .setFootnoteAnchorLabelStyle(footnoteAnchorStyle)
                            .setFootnoteNumberingType(FootnoteNumberingType.DECIMAL)
                            .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_SECTION)))
                    .add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 3));
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void defaultFootnoteStyleTest() throws IOException, InterruptedException {
        String fileName = "defaultFootnoteStyle";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza())
                    .add(new FootnoteAnchor(new Footnote(TestResourceUtil.getByronStanza())))
                    .add("\n" + TestResourceUtil.getByronStanza())
                    .add(new FootnoteAnchor("custom", new Footnote("One more")))
                    .add("\nOne more line.")
                    .add(new FootnoteAnchor(
                            new Text("text").setFontSize(5).setTextRise(3).setBackgroundColor(ColorConstants.GREEN),
                            new Footnote("Two more")))
                    .add("\nTwo more lines.")
                    .add(new FootnoteAnchor(new Footnote("Three more")))
                    .add("\nThree more lines.")
                    .add(new FootnoteAnchor(new Footnote("Four more")));

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 3));
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    @LogMessages(messages =
    @LogMessage(messageTemplate = LayoutLogMessageConstant.FOOTNOTE_NUM_PER_DOCUMENT_SHOULD_BE_FIRST))
    public void footnotePropertiesSectionBreakTest() throws IOException, InterruptedException {
        String fileName = "footnotePropertiesSectionBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        FootnoteNumberingConfig initialNumConfig = FootnoteNumberingConfig.PER_PAGE;
        setFootnotePropertiesForFootnotes(outFileName, initialNumConfig);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    @LogMessages(messages =
    @LogMessage(messageTemplate = LayoutLogMessageConstant.FOOTNOTE_NUM_PER_DOCUMENT_CANNOT_BE_CHANGED, count = 2))
    public void footnotePropertiesPerDocumentTest() throws IOException, InterruptedException {
        String fileName = "footnotePropertiesPerDocument";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        FootnoteNumberingConfig initialNumConfig = FootnoteNumberingConfig.PER_DOCUMENT;
        setFootnotePropertiesForFootnotes(outFileName, initialNumConfig);

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    private static void setFootnotePropertiesForFootnotes(String outFileName, FootnoteNumberingConfig initialNumConfig)
            throws IOException {
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();

            document.setFootnotesProperties(new FootnotesProperties()
                    .setFootnoteNumberingType(FootnoteNumberingType.DECIMAL)
                    .setFootnoteNumberingConfig(initialNumConfig)
                    .setFootnotesContainerStyle(new Style().setBackgroundColor(ColorConstants.GREEN, 0.1F))
                    .setFootnoteAnchorLabelStyle(new Style()
                            .setBorderBottom(new SolidBorder(ColorConstants.GREEN, 1)).setMarginRight(5)));

            for (int i = 1; i < 24; ++i) {
                Footnote footnote = new Footnote("Footnote " + i);
                FootnoteAnchor anchor = new FootnoteAnchor(footnote);
                Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor);
                document.add(p);

                SectionBreak sectionBreak = new SectionBreak();
                if (i % 6 == 0) {
                    FootnotesProperties footnotesProperties = new FootnotesProperties();
                    if (i == 6) {
                        footnotesProperties
                                .setFootnotesContainerStyle(new Style().setBackgroundColor(ColorConstants.RED, 0.1F))
                                .setFootnoteAnchorLabelStyle(new Style()
                                        .setBorderBottom(new SolidBorder(ColorConstants.RED, 1)).setMarginRight(5))
                                .setFootnoteNumberingType(FootnoteNumberingType.ENGLISH_LOWER)
                                .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_SECTION);
                    }
                    if (i == 12) {
                        footnotesProperties
                                .setFootnotesContainerStyle(new Style().setBackgroundColor(ColorConstants.BLUE, 0.1F))
                                .setFootnoteAnchorLabelStyle(new Style()
                                        .setBorderBottom(new SolidBorder(ColorConstants.BLUE, 1)).setMarginRight(5))
                                .setFootnoteNumberingType(FootnoteNumberingType.DECIMAL)
                                .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_DOCUMENT);
                    }
                    if (i == 18) {
                        footnotesProperties
                                .setFootnotesContainerStyle(new Style().setBackgroundColor(ColorConstants.YELLOW, 0.1F))
                                .setFootnoteAnchorLabelStyle(new Style()
                                        .setBorderBottom(new SolidBorder(ColorConstants.YELLOW, 1)).setMarginRight(5))
                                .setFootnoteNumberingType(FootnoteNumberingType.ENGLISH_UPPER)
                                .setFootnoteNumberingConfig(FootnoteNumberingConfig.PER_PAGE);
                    }
                    sectionBreak.setFootnotesProperties(footnotesProperties);
                    document.add(sectionBreak);
                    document.add(new Paragraph("NEW SECTION").setBackgroundColor(ColorConstants.GREEN));
                }
            }
        }
    }
}
