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

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.font.otf.GlyphLine.GlyphLinePart;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.io.util.TextUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.font.FontInfo;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSelector;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.layout.font.selectorstrategy.AbstractFontSelectorStrategy;
import com.itextpdf.layout.font.selectorstrategy.IFontSelectorStrategy;
import com.itextpdf.layout.font.selectorstrategy.IFontSelectorStrategyFactory;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.FootnotesUtil;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.FootnoteRenderer;
import com.itextpdf.layout.renderer.TableRenderer;
import com.itextpdf.layout.renderer.TextPreprocessingUtil;
import com.itextpdf.layout.testutil.PageMarginsTestUtil;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xml.sax.SAXException;
import org.junit.jupiter.api.AfterAll;

@Tag("IntegrationTest")
public class PageMarginsTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/PageMarginsTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/PageMarginsTest/";
    private static final String FONTS = "./src/test/resources/com/itextpdf/layout/fonts/";

    private static final String DOG = "./src/test/resources/com/itextpdf/layout/PageMarginsTest/DOG.bmp";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @AfterAll
    public static void afterClass() {
        CompareTool.cleanup(DESTINATION_FOLDER);
    }

    @Test
    public void footnoteTest() throws IOException, InterruptedException {
        String fileName = "footnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.CYAN);
            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Footnote footnote2 = new Footnote(new Paragraph("Footnote text 2").setMargin(0));
            footnote2.setBackgroundColor(ColorConstants.ORANGE);
            FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);
            Footnote footnote3 = new Footnote("Footnote text 3\nSecond line\nThird line\nFourth line");
            footnote3.setBackgroundColor(ColorConstants.RED);
            FootnoteAnchor anchor3 = new FootnoteAnchor("[3]", footnote3);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            p.add(anchor);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor2);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor3);

            for (int i = 0; i < 5; i++) {
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());
            }

            SectionBreak sectionBreak = new SectionBreak()
                    .setPageMargins(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.FONT_PROPERTY_MUST_BE_PDF_FONT_OBJECT, logLevel = LogLevelConstants.ERROR)
    })
    public void footnoteWithFontFamilyTest() throws IOException, InterruptedException {
        String fileName = "footnoteWithFontFamily";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {

            FontProvider provider = new FontProvider();
            provider.getFontSet().addFont(StandardFonts.HELVETICA, null, "helvetica");
            provider.getFontSet().addFont(StandardFonts.COURIER, null, "courier");

            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.CYAN);

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            anchor.setProperty(Property.FONT, new String[] { "helvetica"});
            anchor.setProperty(Property.FONT_PROVIDER, provider);

            Footnote footnote2 = new Footnote(new Paragraph("Footnote text 2").setMargin(0));
            footnote2.setBackgroundColor(ColorConstants.ORANGE);
            FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);
            Footnote footnote3 = new Footnote("Footnote text 3\nSecond line\nThird line\nFourth line");
            footnote3.setBackgroundColor(ColorConstants.RED);
            FootnoteAnchor anchor3 = new FootnoteAnchor("[3]", footnote3);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            p.add(anchor);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor2);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor3);

            for (int i = 0; i < 5; i++) {
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());
            }

            SectionBreak sectionBreak = new SectionBreak()
                    .setPageMargins(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }


    @Test
    public void footnoteAnchorWithMultipleResolvedFontTest() {
        String fileName = "footnoteAnchorFonts";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        Exception exception = Assertions.assertThrows(PdfException.class, () -> {
            try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                    Document document = new Document(pdfDocument)) {

                FontProvider provider = new FontProvider();
                // This font only contains latin script
                provider.getFontSet().addFont(FONTS + "NotoSansCJKjp-Regular.otf", null, "NotoSansCJK");
                // This font does contains Cyrillic script
                provider.getFontSet().addFont(FONTS + "NotoSans-Regular.ttf", null, "NotoSans");

                Footnote footnote = new Footnote("Footnote text");
                footnote.setBackgroundColor(ColorConstants.CYAN);

                FootnoteAnchor anchor = new FootnoteAnchor("Д H", footnote);
                anchor.setProperty(Property.FONT, new String[] {"NotoSansCJK", "NotoSans"});
                anchor.setProperty(Property.FONT_PROVIDER, provider);

                Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
                p.add(anchor);
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());

                Div div1 = new Div();
                div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
                document.add(div1);
            }
        });
        Assertions.assertEquals(LayoutExceptionMessageConstant.FOOTNOTE_ANCHOR_LAYOUT_CONSISTENCY, exception.getMessage());
    }


    @Test
    public void footnoteInTableTest() throws IOException, InterruptedException {
        String fileName = "footnoteInTable";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.PINK);
            FootnoteAnchor anchor = new FootnoteAnchor(new Text("1").setFontSize(6).setTextRise(7), footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            footnote2.setBackgroundColor(ColorConstants.YELLOW);
            FootnoteAnchor anchor2 = new FootnoteAnchor(new Text("2").setFontSize(6).setTextRise(7), footnote2);

            Image img = loadImage();
            Table table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            table.addCell(img);
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            footnote.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2));
            anchor = new FootnoteAnchor(new Text("3").setFontSize(6).setTextRise(7), footnote);
            footnote2 = new Footnote("Footnote text 4");
            footnote2.setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 2));
            anchor2 = new FootnoteAnchor(new Text("4").setFontSize(6).setTextRise(7), footnote2);
            table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            table.addCell(img);

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" + TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnoteInTableTaggingTest()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteInTableTagging";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.PINK);
            FootnoteAnchor anchor = new FootnoteAnchor(new Text("1").setFontSize(6).setTextRise(7), footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            footnote2.setBackgroundColor(ColorConstants.YELLOW);
            FootnoteAnchor anchor2 = new FootnoteAnchor(new Text("2").setFontSize(6).setTextRise(7), footnote2);

            Image img = loadImage();
            Table table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            table.addCell(img);
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            footnote.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2));
            anchor = new FootnoteAnchor(new Text("3").setFontSize(6).setTextRise(7), footnote);
            footnote2 = new Footnote("Footnote text 4");
            footnote2.setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 2));
            anchor2 = new FootnoteAnchor(new Text("4").setFontSize(6).setTextRise(7), footnote2);
            table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            table.addCell(img);

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" + TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }

    @Test
    public void footnoteInTableFooterTest() throws IOException, InterruptedException {
        String fileName = "footnoteInTableFooter";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
            Document document = new Document(pdfDocument)) {
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.PINK);
            FootnoteAnchor anchor = new FootnoteAnchor(new Text("1").setFontSize(6).setTextRise(7), footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            footnote2.setBackgroundColor(ColorConstants.YELLOW);
            FootnoteAnchor anchor2 = new FootnoteAnchor(new Text("2").setFontSize(6).setTextRise(7), footnote2);

            Image img = loadImage();
            Table table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            table.addCell(img);
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            footnote.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2));
            anchor = new FootnoteAnchor(new Text("3").setFontSize(6).setTextRise(7), footnote);
            footnote2 = new Footnote("Footnote text 4");
            footnote2.setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 2));
            anchor2 = new FootnoteAnchor(new Text("4").setFontSize(6).setTextRise(7), footnote2);
            table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 1) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
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

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" + TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnoteInTableFooterTaggingTest()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteInTableFooterTagging";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.PINK);
            FootnoteAnchor anchor = new FootnoteAnchor(new Text("1").setFontSize(6).setTextRise(7), footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            footnote2.setBackgroundColor(ColorConstants.YELLOW);
            FootnoteAnchor anchor2 = new FootnoteAnchor(new Text("2").setFontSize(6).setTextRise(7), footnote2);

            Image img = loadImage();
            Table table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            table.addCell(img);
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            footnote.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2));
            anchor = new FootnoteAnchor(new Text("3").setFontSize(6).setTextRise(7), footnote);
            footnote2 = new Footnote("Footnote text 4");
            footnote2.setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 2));
            anchor2 = new FootnoteAnchor(new Text("4").setFontSize(6).setTextRise(7), footnote2);
            table = new Table(4);
            for (int i = 0; i < 24; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 1) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
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

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" + TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }

    @Test
    public void footnoteInTableHeaderTest() throws IOException, InterruptedException {
        String fileName = "footnoteInTableHeader";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.PINK);
            FootnoteAnchor anchor = new FootnoteAnchor(new Text("1").setFontSize(6).setTextRise(7), footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            footnote2.setBackgroundColor(ColorConstants.YELLOW);
            FootnoteAnchor anchor2 = new FootnoteAnchor(new Text("2").setFontSize(6).setTextRise(7), footnote2);

            Image img = loadImage();
            Table table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            table.addCell(img);
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            footnote.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2));
            anchor = new FootnoteAnchor(new Text("3").setFontSize(6).setTextRise(7), footnote);
            footnote2 = new Footnote("Footnote text 4");
            footnote2.setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 2));
            anchor2 = new FootnoteAnchor(new Text("4").setFontSize(6).setTextRise(7), footnote2);
            table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 1) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i < 4) {
                    table.addHeaderCell(new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2)));
                } else {
                    table.addCell(paragraph);
                }
            }
            table.addCell(img);

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" + TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnoteInTableHeaderTaggingTest()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteInTableHeaderTagging";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.PINK);
            FootnoteAnchor anchor = new FootnoteAnchor(new Text("1").setFontSize(6).setTextRise(7), footnote);
            Footnote footnote2 = new Footnote("Footnote text 2");
            footnote2.setBackgroundColor(ColorConstants.YELLOW);
            FootnoteAnchor anchor2 = new FootnoteAnchor(new Text("2").setFontSize(6).setTextRise(7), footnote2);

            Image img = loadImage();
            Table table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 5) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                table.addCell(paragraph);
            }
            table.addCell(img);
            document.add(table);

            footnote = new Footnote("Footnote text 3");
            footnote.setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2));
            anchor = new FootnoteAnchor(new Text("3").setFontSize(6).setTextRise(7), footnote);
            footnote2 = new Footnote("Footnote text 4");
            footnote2.setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 2));
            anchor2 = new FootnoteAnchor(new Text("4").setFontSize(6).setTextRise(7), footnote2);
            table = new Table(4);
            for (int i = 0; i < 23; ++i) {
                Paragraph paragraph = new Paragraph("Cell " + i);
                if (i == 1) {
                    paragraph.add(anchor).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i == 19) {
                    paragraph.add(anchor2).setBorder(new SolidBorder(ColorConstants.GREEN, 1));
                }
                if (i < 4) {
                    table.addHeaderCell(new Cell().add(paragraph).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2)));
                } else {
                    table.addCell(paragraph);
                }
            }
            table.addCell(img);

            document.add(new Paragraph(TestResourceUtil.getByronStanza() + "\n\n" + TestResourceUtil.getByronStanza() + "\n\n" + "Two more \nlines"));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }

    @Test
    public void pageMarginsComplexTest() throws IOException, InterruptedException {
        String fileName = "pageMargins";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.addNewPage();

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 5; i++) {
                p.add(TestResourceUtil.getByronStanza());
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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 5; i++) {
                p.add(TestResourceUtil.getByronStanza());
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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 5; i++) {
                p.add(TestResourceUtil.getByronStanza());
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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 5; i++) {
                p.add(TestResourceUtil.getByronStanza());
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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            // Set static margins
            document.setMargins(100, 100, 100, 100);

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            List<PageMarginContent> elements3 = new ArrayList<>();
            elements3.add(new PageMarginContent(MarginBoxName.BOTTOM, new Div()
                    .add(new Paragraph("TEST BOTTOM MARGIN\nWITH SOME FOOTNOTE"))
                    .setBackgroundColor(ColorConstants.CYAN)
                    .setMinHeight(50)));

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 5; i++) {
                p.add(TestResourceUtil.getByronStanza());
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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
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

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 5; i++) {
                p.add(TestResourceUtil.getByronStanza());
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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            List<PageMarginContent> elements2 = PageMarginsTestUtil.getPageMargins2();

            document.setPageMargins(pageNum -> pageNum > 0 && pageNum % 2 == 0, new PageMarginBoxes(elements));
            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements2));

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 7; i++) {
                p.add(TestResourceUtil.getByronStanza());
            }

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));
            document.add(new Paragraph(TestResourceUtil.getByronStanza()));
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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            SectionBreak sectionBreak = new SectionBreak(PageSize.A5);
            AreaBreak areaBreak = new AreaBreak(PageSize.A5.rotate());

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 7; i++) {
                p.add(TestResourceUtil.getByronStanza());
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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

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
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

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
    public void relativePositionTaggingTest()
         throws IOException, ParserConfigurationException, SAXException {
        String fileName = "relativePositionTagging";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();
            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));

            Div div1 = new Div().add(p).setBackgroundColor(new DeviceRgb(65, 151, 29));
            div1.setRelativePosition(50, 50, 0, 0);

            Div div2 = new Div().add(p).setBackgroundColor(new DeviceRgb(209, 247, 29));

            document.add(div1)
                    .add(sectionBreak)
                    .add(div2);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }

    @Test
    public void footnoteNotLinkedToElementTest() throws IOException, InterruptedException {
        String fileName = "footnoteNotLinkedToElement";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

            Footnote paragraphFootnote = new Footnote("Footnote text");
            paragraphFootnote.setBackgroundColor(ColorConstants.RED);
            FootnoteAnchor anchor = new FootnoteAnchor("1", paragraphFootnote);
            p.add(anchor);
            for (int i = 0; i < 3; i++) {
                p.add("\n\n").add(TestResourceUtil.getByronStanza());
            }

            PageMarginBoxes pageMarginBoxes = new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1());
            SectionBreak sectionBreak = new SectionBreak().setPageMargins(pageMarginBoxes);

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBackgroundColor(ColorConstants.CYAN);
            // This API is not supposed to be used by the users, but this util class can't be hidden.
            FootnotesUtil.addFootnotesToPage(1, Collections.singletonList((FootnoteRenderer) footnote.createRendererSubTree()), pageMarginBoxes,
                    document.getFootnotesProperties());

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnotesOneByOneTest() throws IOException, InterruptedException {
        String fileName = "footnotesOneByOne";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
            Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            for (int i = 0; i < 2; i++) {
                p.add("\n\n").add(TestResourceUtil.getByronStanza());
            }

            Footnote footnote1 = new Footnote(TestResourceUtil.getByronStanza());
            footnote1.setBackgroundColor(ColorConstants.YELLOW);
            FootnoteAnchor anchor1 = new FootnoteAnchor("1", footnote1);
            Footnote footnote2 = new Footnote(TestResourceUtil.getByronStanza());
            footnote2.setBackgroundColor(ColorConstants.PINK);
            FootnoteAnchor anchor2 = new FootnoteAnchor("2", footnote2);
            p.add(anchor1).add(anchor2);

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 3));
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void footnotesOneByOne2Test() throws IOException, InterruptedException {
        String fileName = "footnotesOneByOne2";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();
            document.setFontSize(20);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());

            Footnote footnote1 = new Footnote(TestResourceUtil.getByronStanza());
            footnote1.setBackgroundColor(ColorConstants.YELLOW);
            FootnoteAnchor anchor1 = new FootnoteAnchor("1", footnote1);
            Footnote footnote2 = new Footnote(TestResourceUtil.getByronStanza());
            footnote2.setBackgroundColor(ColorConstants.PINK);
            FootnoteAnchor anchor2 = new FootnoteAnchor("2", footnote2);
            p.add(anchor1).add(anchor2);

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 3));
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void imageAsFootnoteAnchorTest() throws IOException, InterruptedException {
        String fileName = "imageAsFootnoteAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
            Document document = new Document(pdfDocument)) {

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bulb.gif"));
            image.setWidth(15);
            FootnoteAnchor anchor = new FootnoteAnchor(image, footnote);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor).add(TestResourceUtil.getByronStanza());

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 3));
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER,
                "diff_" + fileName));
    }

    @Test
    public void imageAsFootnoteAnchorTaggingTest()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "imageAsFootnoteTaggingAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
            Document document = new Document(pdfDocument)) {
            pdfDocument.setTagged();

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bulb.gif"));
            image.setWidth(15);
            FootnoteAnchor anchor = new FootnoteAnchor(image, footnote);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor).add(TestResourceUtil.getByronStanza());

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 3));
            document.add(div);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
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

    @Test
    public void footnoteTaggingPdfV17Test()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteV17";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_1_7);
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, writerProperties));
                Document document = new Document(pdfDocument)) {

            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.CYAN);
            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Footnote footnote2 = new Footnote(new Paragraph("Footnote text 2").setMargin(0));
            footnote2.setBackgroundColor(ColorConstants.ORANGE);
            FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);
            Footnote footnote3 = new Footnote("Footnote text 3\nSecond line\nThird line\nFourth line");
            footnote3.setBackgroundColor(ColorConstants.RED);
            FootnoteAnchor anchor3 = new FootnoteAnchor("[3]", footnote3);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            p.add(anchor);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor2);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor3);

            for (int i = 0; i < 5; i++) {
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());
            }

            SectionBreak sectionBreak = new SectionBreak()
                    .setPageMargins(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }


    @Test
    public void footnotePdfV17ManualNoteTagIdTest()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteV17_ID";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_1_7);
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, writerProperties));
                Document document = new Document(pdfDocument)) {

            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.CYAN);
            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Footnote footnote2 = new Footnote(new Paragraph("Footnote text 2").setMargin(0));
            footnote2.setBackgroundColor(ColorConstants.ORANGE);
            FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);
            Paragraph fn3p = new Paragraph("Footnote text 3\nSecond line\nThird line\nFourth line");

            Footnote footnote3 = new Footnote(fn3p);
            footnote3.setBackgroundColor(ColorConstants.RED);
            footnote3.getAccessibilityProperties().setStructureElementIdString("TEST ID3");
            FootnoteAnchor anchor3 = new FootnoteAnchor("[3]", footnote3);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            p.add(anchor);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor2);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor3);

            for (int i = 0; i < 5; i++) {
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());
            }

            SectionBreak sectionBreak = new SectionBreak()
                    .setPageMargins(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }

    @Test
    public void footnotePdfV17ManualTagRoleTest()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteV17_Role";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_1_7);
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, writerProperties));
                Document document = new Document(pdfDocument)) {

            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.CYAN);
            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            anchor.getAccessibilityProperties().setRole(StandardRoles.LBL);
            Footnote footnote2 = new Footnote(new Paragraph("Footnote text 2").setMargin(0));
            footnote2.setBackgroundColor(ColorConstants.ORANGE);
            FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);
            Paragraph fn3p = new Paragraph("Footnote text 3\nSecond line\nThird line\nFourth line");

            Footnote footnote3 = new Footnote(fn3p);
            footnote3.setBackgroundColor(ColorConstants.RED);
            footnote3.getAccessibilityProperties().setRole(StandardRoles.QUOTE);
            FootnoteAnchor anchor3 = new FootnoteAnchor("[3]", footnote3);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            p.add(anchor);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor2);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor3);

            for (int i = 0; i < 5; i++) {
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());
            }

            SectionBreak sectionBreak = new SectionBreak()
                    .setPageMargins(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }


    @Test
    public void footnoteTaggingPdfV20Test()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteV20";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, writerProperties));
                Document document = new Document(pdfDocument)) {

            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.CYAN);
            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Footnote footnote2 = new Footnote(new Paragraph("Footnote text 2").setMargin(0));
            footnote2.setBackgroundColor(ColorConstants.ORANGE);
            FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);
            Footnote footnote3 = new Footnote("Footnote text 3\nSecond line\nThird line\nFourth line");
            footnote3.setBackgroundColor(ColorConstants.RED);
            FootnoteAnchor anchor3 = new FootnoteAnchor("[3]", footnote3);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            p.add(anchor);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor2);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor3);

            for (int i = 0; i < 5; i++) {
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());
            }

            SectionBreak sectionBreak = new SectionBreak()
                    .setPageMargins(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }

    @Test
    public void footnotePdfV20ManualNoteTagIdTest()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteV20_ID";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, writerProperties));
                Document document = new Document(pdfDocument)) {

            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.CYAN);
            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Footnote footnote2 = new Footnote(new Paragraph("Footnote text 2").setMargin(0));
            footnote2.setBackgroundColor(ColorConstants.ORANGE);
            FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);
            Paragraph fn3p = new Paragraph("Footnote text 3\nSecond line\nThird line\nFourth line");

            Footnote footnote3 = new Footnote(fn3p);
            footnote3.setBackgroundColor(ColorConstants.RED);
            footnote3.getAccessibilityProperties().setStructureElementIdString("TEST ID3");
            FootnoteAnchor anchor3 = new FootnoteAnchor("[3]", footnote3);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            p.add(anchor);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor2);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor3);

            for (int i = 0; i < 5; i++) {
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());
            }

            SectionBreak sectionBreak = new SectionBreak()
                    .setPageMargins(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }

    @Test
    public void footnotePdfV20ManualTagRoleTest()
            throws IOException, ParserConfigurationException, SAXException {
        String fileName = "footnoteV20_Role";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".xml";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName, writerProperties));
                Document document = new Document(pdfDocument)) {

            pdfDocument.setTagged();
            Footnote footnote = new Footnote("Footnote text");
            footnote.setBackgroundColor(ColorConstants.CYAN);
            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            anchor.getAccessibilityProperties().setRole(StandardRoles.LINK);
            Footnote footnote2 = new Footnote(new Paragraph("Footnote text 2").setMargin(0));
            footnote2.setBackgroundColor(ColorConstants.ORANGE);
            FootnoteAnchor anchor2 = new FootnoteAnchor("[2]", footnote2);
            Paragraph fn3p = new Paragraph("Footnote text 3\nSecond line\nThird line\nFourth line");

            Footnote footnote3 = new Footnote(fn3p);
            footnote3.setBackgroundColor(ColorConstants.RED);
            footnote3.getAccessibilityProperties().setRole(StandardRoles.ASIDE);
            FootnoteAnchor anchor3 = new FootnoteAnchor("[3]", footnote3);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza());
            p.add(anchor);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor2);
            p.add("\n\n");
            p.add(TestResourceUtil.getByronStanza());
            p.add(anchor3);

            for (int i = 0; i < 5; i++) {
                p.add("\n\n");
                p.add(TestResourceUtil.getByronStanza());
            }

            SectionBreak sectionBreak = new SectionBreak()
                    .setPageMargins(new PageMarginBoxes(PageMarginsTestUtil.getPageMargins1()));

            Div div1 = new Div();
            div1.add(p).setBorder(new SolidBorder(ColorConstants.MAGENTA, 5));
            document.add(sectionBreak);
            document.add(div1);
        }
        Assertions.assertNull(new CompareTool().compareTagStructureAgainstXml(outFileName, cmpFileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.SECTION_BREAK_IGNORED)})
    public void sectionBreakInsideTableHeaderTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakInsideTableHeader";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));

            Table table = new Table(4);

            table.addHeaderCell("Header text");
            table.addHeaderCell(new Cell());
            table.addHeaderCell(new Div()
                    .add(new Paragraph("Before section break"))
                    .add(sectionBreak)
                    .add(new Paragraph("After section break")));
            table.addHeaderCell(new Cell());

            table.addCell("Table cell content 1");
            table.addCell("Table cell content 2");
            table.addCell("Table cell content 3");
            table.addCell("Table cell content 4");

            table.addFooterCell("Footer text");
            table.addFooterCell(new Cell());
            table.addFooterCell(new Cell());
            table.addFooterCell(new Cell());

            document.add(table);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    // TODO DEVSIX-10049 Fix SectionBreak margins not working
    @Test
    public void sectionBreakInsideTableBodyTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakInsideTableBody";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));

            Table table = new Table(4);

            table.addHeaderCell("Header text");
            table.addHeaderCell(new Cell());
            table.addHeaderCell(new Cell());
            table.addHeaderCell(new Cell());

            table.addCell("Table cell content 1");
            table.addCell("Table cell content 2");
            table.addCell(new Div()
                    .add(new Paragraph("Before section break"))
                    .add(sectionBreak)
                    .add(new Paragraph("After section break"))
            );
            table.addCell("Table cell content 4");

            table.addFooterCell("Footer text");
            table.addFooterCell(new Cell());
            table.addFooterCell(new Cell());
            table.addFooterCell(new Cell());

            document.add(table);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.SECTION_BREAK_IGNORED)})
    public void sectionBreakInsideTableFooterTest() throws IOException, InterruptedException {
        String fileName = "sectionBreakInsideTableFooter";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
                Document document = new Document(pdfDoc)) {

            List<PageMarginContent> elements = PageMarginsTestUtil.getPageMargins1();
            SectionBreak sectionBreak = new SectionBreak(new PageMarginBoxes(elements));

            Table table = new Table(4);

            table.addHeaderCell("Header text");
            table.addHeaderCell(new Cell());
            table.addHeaderCell(new Cell());
            table.addHeaderCell(new Cell());

            table.addCell("Table cell content 1");
            table.addCell("Table cell content 2");
            table.addCell("Table cell content 3");
            table.addCell("Table cell content 4");

            table.addFooterCell("Footer text");
            table.addFooterCell(new Cell());
            table.addFooterCell(new Div()
                    .add(new Paragraph("Before section break"))
                    .add(sectionBreak)
                    .add(new Paragraph("After section break")));
            table.addFooterCell(new Cell());

            document.add(table);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    private static Image loadImage() {
        try {
            return new Image(ImageDataFactory.create(DOG));
        } catch (MalformedURLException e) {
            throw new PdfException(e.getMessage());
        }
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
