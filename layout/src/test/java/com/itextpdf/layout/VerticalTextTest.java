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

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.InlineVerticalAlignment;
import com.itextpdf.layout.properties.InlineVerticalAlignmentType;
import com.itextpdf.layout.properties.LineHeight;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.Underline;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

@Tag("IntegrationTest")
public class VerticalTextTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/VerticalTextTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/VerticalTextTest/";
    private static final String EXPANDED_FONT =
            "./src/test/resources/com/itextpdf/layout/fonts/BioRhymeExpanded-Regular.ttf";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicVerticalTextTest() throws IOException, InterruptedException {
        String fileName = "basicVerticalText";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.add(new Text("some text"));
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextDifferentFontsInParagraphTest() throws IOException, InterruptedException {
        String fileName = "verticalTextDifferentFontsInParagraph";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            Text text1 = new Text("some text in courier font.\nFont size is 25.\n");
            PdfFont courier = PdfFontFactory.createFont(StandardFonts.COURIER);
            text1.setFont(courier);
            text1.setFontSize(25);
            paragraph.add(text1);

            Text text2 = new Text("some text in times new roman font.\nFont size is 20.");
            PdfFont timesRoman = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            text2.setFont(timesRoman);
            text2.setFontSize(20);
            paragraph.add(text2);

            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextDifferentFontsInLineTest() throws IOException, InterruptedException {
        String fileName = "verticalTextDifferentFontsInLineTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            Text text1 = new Text("some text in courier font. Font size is 25.");
            PdfFont courier = PdfFontFactory.createFont(StandardFonts.COURIER);
            text1.setFont(courier);
            text1.setFontSize(25);
            text1.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.add(text1);

            Text text2 = new Text("some text in times new roman font. Font size is 40.");
            PdfFont timesRoman = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            text2.setFont(timesRoman);
            text2.setFontSize(40);
            text2.setBackgroundColor(ColorConstants.CYAN);
            paragraph.add(text2);

            Text text3 = new Text("some text in helvetica bold font. Font size is 10.");
            PdfFont helvetica = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            text3.setFont(helvetica);
            text3.setFontSize(10);
            text3.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.add(text3);

            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void severalTextChunksVerticalTextTest() throws IOException, InterruptedException {
        String fileName = "severalTextChunksVerticalText";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.add(new Text("first text chunk "));
            paragraph.add(new Text("second text chunk "));
            paragraph.add(new Text("third text chunk "));
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void longVerticalTextTest() throws IOException, InterruptedException {
        String fileName = "longVerticalText";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.add(new Text("some long vertical text to trigger multiple line breaks. Font size will be also " +
                    "increased to make it easier."));
            paragraph.setFontSize(25);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void longVerticalTextWithLineBreaksTest() throws IOException, InterruptedException {
        String fileName = "longVerticalTextWithLineBreaks";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.add(new Text("some long vertical text\nto trigger multiple line breaks.\nFont size will be " +
                    "also increased\nto make it easier."));
            paragraph.setFontSize(25);
            paragraph.setBorder(new SolidBorder(1));
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void longVerticalTextWithPageBreakTest() throws IOException, InterruptedException {
        String fileName = "longVerticalTextWithPageBreak";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.add(new Text("some long vertical\ntext to trigger multiple line breaks.\nFont size will be " +
                    "also increased to make it easier.\n"));
            paragraph.add(new Text("Additional chunk of text,\n to trigger page break.\n" +
                    "Font size increased even further."));
            paragraph.setFontSize(35);
            paragraph.setBorder(new SolidBorder(1));
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextWithStyleAdjustmentsTest() throws IOException, InterruptedException {
        String fileName = "verticalTextWithStyleAdjustments";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            document.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            document.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Paragraph paragraph = new Paragraph();
            Text text = new Text("some text");
            text.setProperty(Property.ITALIC_SIMULATION, true);
            text.setProperty(Property.BOLD_SIMULATION, true);
            text.setProperty(Property.UNDERLINE, Collections.singletonList(
                    new Underline(ColorConstants.RED, 1, .75F, 0, 0, 1 / 2F, PdfCanvasConstants.LineCapStyle.BUTT)));
            text.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.add(text);
            paragraph.add(new Text("Normal some text\nsome text").setBackgroundColor(ColorConstants.LIGHT_GRAY));
            paragraph.setBorder(new SolidBorder(1));
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextAndHorizontalTextTest() throws IOException, InterruptedException {
        String fileName = "verticalTextAndHorizontalText";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBorder(new SolidBorder(ColorConstants.BLACK, 2));

            Text verticalText = new Text("vertical text.");
            verticalText.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            verticalText.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            verticalText.setBorder(new SolidBorder(ColorConstants.RED, 1));

            paragraph.add(verticalText);
            paragraph.add(new Text("horizontal text.").setBorder(new SolidBorder(ColorConstants.BLUE, 1)));

            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextWithLongWordsTest() throws IOException, InterruptedException {
        String fileName = "verticalTextWithLongWords";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            document.add(new Paragraph("Long word, first line and first word:"));
            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            Text longWordText = new Text("Tooooooooolongword");
            longWordText.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setBorder(new SolidBorder(1));
            paragraph.setHeight(100);
            paragraph.add(longWordText);
            paragraph.add(" and usual words length now");
            document.add(paragraph);

            document.add(new Paragraph("Long word, first line and not first word:"));
            paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            longWordText = new Text("Tooooooooolongword");
            longWordText.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setBorder(new SolidBorder(1));
            paragraph.setHeight(100);
            paragraph.add("Abc ");
            paragraph.add(longWordText);
            paragraph.add(" and usual words length now");
            document.add(paragraph);

            document.add(new Paragraph("Long word, not first line:"));
            paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            longWordText = new Text("Tooooooooolongword");
            longWordText.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setBorder(new SolidBorder(1));
            paragraph.setHeight(100);
            paragraph.add("Abc\n");
            paragraph.add(longWordText);
            paragraph.add(" and usual words length now");
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.CLIP_ELEMENT, count = 2))
    public void verticalTextWithMaxHeightWidthParagraphTest() throws IOException, InterruptedException {
        String fileName = "verticalTextWithMaxHeightParagraph";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            Text longText = new Text(
                    "Pretty long text example is provided here, " +
                            "especially given its font-size is set to bigger value"
            );
            longText.setFontSize(32);
            longText.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setBorder(new SolidBorder(1));
            paragraph.add(longText);

            paragraph.setHeight(100);
            paragraph.setWidth(100);
            document.add(paragraph);

            paragraph.setHeight(500);
            paragraph.setWidth(300);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextSpaceTrimmingTest() throws IOException, InterruptedException {
        String fileName = "verticalTextSpaceTrimming";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Text normalText = new Text("Normal\ntext").setBackgroundColor(ColorConstants.CYAN);
            Text whitespacesRiddenText = new Text("     Hello     \n \n World    \n        \n  ")
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Text threeMSpaceWrappedText = new Text(" MMM ").setBackgroundColor(ColorConstants.LIGHT_GRAY);

            Paragraph vParagraph = new Paragraph();
            vParagraph.setBorder(new SolidBorder(1));
            vParagraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            vParagraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            vParagraph.add(whitespacesRiddenText);
            vParagraph.add(normalText);
            document.add(vParagraph);

            Paragraph hParagraph = new Paragraph();
            hParagraph.setBorder(new SolidBorder(1));
            hParagraph.add(whitespacesRiddenText);
            hParagraph.add(normalText);
            document.add(hParagraph);

            vParagraph = new Paragraph();
            vParagraph.setBorder(new SolidBorder(1));
            vParagraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            vParagraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            // fine-tune height to fit threeMSpaceWrappedText characters
            vParagraph.setFontSize(12);
            vParagraph.setHeight(12 * 5 + 12);
            // fully fits
            vParagraph.add(threeMSpaceWrappedText).add("\n");
            // trailing text elem space shouldn't fit on the line
            vParagraph.add("M").add(threeMSpaceWrappedText).add("M").add("\n");
            vParagraph.add("ABC                      ");
            vParagraph.add(normalText);
            document.add(vParagraph);

            hParagraph = new Paragraph();
            hParagraph.setBorder(new SolidBorder(1));
            // fine-tune height to fit threeMSpaceWrappedText characters
            hParagraph.setFontSize(12);
            hParagraph.setWidth((float) (12 * 3 + 12 / 2.4 * 2));
            // fully fits
            hParagraph.add(threeMSpaceWrappedText).add("\n");
            // trailing text elem space shouldn't fit on the line
            hParagraph.add("M").add(threeMSpaceWrappedText).add("M").add("\n");
            hParagraph.add("ABC                      ");
            hParagraph.add(normalText);
            document.add(hParagraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    // TODO DEVSIX-10180 Support text rise in html mode for vertical text
    public void lineThroughWithTextRiseTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "lineThroughWithTextRise.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_lineThroughWithTextRise.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Text textUp = new Text("textRise10f_with_lineThrough");
            textUp.setTextRise(-10f);
            textUp.setLineThrough();
            textUp.setFontColor(ColorConstants.GREEN);

            Text textDown = new Text("textRise-10f_with_lineThrough");
            textDown.setTextRise(-10f);
            textDown.setLineThrough();
            textDown.setFontColor(ColorConstants.RED);

            Paragraph n = new Paragraph("baseline");
            n.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            n.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            n.add(textUp).add(textDown);

            document.add(n);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void underlineTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "underline.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_underline.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            document.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            document.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Underline underline = new Underline(null, 0, 0.1f, 0, -0.1f, PdfCanvasConstants.LineCapStyle.BUTT)
                    .setStrokeWidth(2).setStrokeColor(new TransparentColor(ColorConstants.PINK, 0.5f))
                    .setDashPattern(new float[]{5, 5, 10, 5}, 5);
            Paragraph p = new Paragraph("Yellow text with pink stroked dashed underline.")
                    .setFontSize(45).setFontColor(ColorConstants.YELLOW)
                    .setUnderline(underline);

            TransparentColor strokeColor = new TransparentColor(ColorConstants.GREEN, 0.5f);
            Underline underline2 = new Underline(ColorConstants.DARK_GRAY, 0, 0.1f, 0, 0.3f,
                    PdfCanvasConstants.LineCapStyle.BUTT).setStrokeWidth(1).setStrokeColor(strokeColor);
            Paragraph p2 = new Paragraph("Text with line-through and default underline.").setFontSize(50)
                    .setStrokeWidth(1).setFontColor(ColorConstants.DARK_GRAY).setStrokeColor(strokeColor)
                    .setUnderline(underline2)
                    .setUnderline();

            Underline underline3 = new Underline(null, 0, 0.1f, 0, 0.9f, PdfCanvasConstants.LineCapStyle.BUTT);
            Paragraph p3 = new Paragraph("Text with null font color and default overline.")
                    .setFontSize(50).setFontColor((TransparentColor) null)
                    .setUnderline(underline3);

            // This line should be around the middle of the text compared to horizontal text.
            Underline underline4 = new Underline(null, 0, 0.1f, 15, 0f, PdfCanvasConstants.LineCapStyle.BUTT);
            Paragraph p4 = new Paragraph("Text with custom yPosition (15).").setFontSize(50)
                    .setUnderline(underline4);

            document.add(p).add(p2).add(p3).add(p4);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void fontStyleSimulationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fontStyleSimulationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_fontStyleSimulationTest01.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            document.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            document.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Paragraph p = new Paragraph("I'm underlined").setUnderline();
            document.add(p);

            p = new Paragraph("I'm strikethrough").setLineThrough();
            document.add(p);

            p = new Paragraph(new Text("I'm a bold simulation font")
                    .setBackgroundColor(ColorConstants.GREEN)).simulateBold();
            document.add(p);

            p = new Paragraph(new Text("I'm an italic simulation font")
                    .setBackgroundColor(ColorConstants.GREEN)).simulateItalic();
            document.add(p);

            p = new Paragraph(new Text("I'm a super bold italic underlined linethrough piece of text and no one " +
                    "can be better than me, even if such a long description will cause me to occupy two lines")
                    .setBackgroundColor(ColorConstants.GREEN))
                    .simulateItalic().simulateBold().setUnderline().setLineThrough();
            document.add(p);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void verticalTextLineWidthTest() throws IOException, InterruptedException {
        String fileName = "verticalTextLineWidth";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div();
            div.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            div.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            PdfFont helvetica = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            div.setFont(helvetica);

            Paragraph paragraph = new Paragraph()
                    .setHeight(500).setFontSize(50).setBackgroundColor(new DeviceRgb(187, 187, 255));
            Text text1 = new Text("WWWWWWW").setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(text1);
            Text text2 = new Text("aaaaaaaa").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(20);
            paragraph.add(text2);
            Text text3 = new Text("iiiiii").setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(text3);
            Text text4 = new Text("jjjj").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(80);
            paragraph.add(text4);
            Text text5 = new Text("......").setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(text5);
            Text text6 = new Text("Wow!").setBackgroundColor(new DeviceRgb(173, 255, 47));
            paragraph.add(text6);
            paragraph.add("Hello World");

            Paragraph paragraph2 = new Paragraph()
                    .setHeight(500).setFontSize(20).setBackgroundColor(new DeviceRgb(255, 0, 204));
            text1 = new Text("WWWWWWWwwwwWWWWW").setBackgroundColor(ColorConstants.YELLOW);
            paragraph2.add(text1);
            text2 = new Text("Waaaaaaaa").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(20);
            paragraph2.add(text2);
            text3 = new Text("i").setBackgroundColor(ColorConstants.YELLOW).setFontSize(80);
            paragraph2.add(text3);
            text4 = new Text("Wjjj").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(80);
            paragraph2.add(text4);
            text5 = new Text("....").setBackgroundColor(ColorConstants.YELLOW).setFontSize(80);
            paragraph2.add(text5);
            text6 = new Text("Wow!").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(80);
            paragraph2.add(text6);
            paragraph2.add("Hello World");

            div.add(paragraph).add(new AreaBreak()).add(paragraph2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextExpandedFontTest() throws IOException, InterruptedException {
        String fileName = "verticalTextExpandedFont";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div();
            div.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            div.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            PdfFont bioRhyme = PdfFontFactory.createFont(EXPANDED_FONT);
            div.setFont(bioRhyme);

            Paragraph paragraph = new Paragraph()
                    .setHeight(600).setFontSize(40).setBackgroundColor(new DeviceRgb(187, 187, 255));
            Text text1 = new Text("WWWWWWW").setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(text1);
            Text text2 = new Text("aaaaaaaa").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(20);
            paragraph.add(text2);
            Text text3 = new Text("iiiiii").setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(text3);
            Text text4 = new Text("jjjj").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(80);
            paragraph.add(text4);
            Text text5 = new Text("......").setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(text5);
            Text text6 = new Text("Wow!").setBackgroundColor(new DeviceRgb(173, 255, 47));
            paragraph.add(text6);
            paragraph.add("Hello World");

            Paragraph paragraph2 = new Paragraph()
                    .setHeight(600).setFontSize(20).setBackgroundColor(new DeviceRgb(255, 0, 204));
            text1 = new Text("WWWWWWWwwwwWWWWW").setBackgroundColor(ColorConstants.YELLOW);
            paragraph2.add(text1);
            text2 = new Text("Waaaaaaaa").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(20);
            paragraph2.add(text2);
            text3 = new Text("i").setBackgroundColor(ColorConstants.YELLOW).setFontSize(40);
            paragraph2.add(text3);
            text4 = new Text("Wjjj").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(40);
            paragraph2.add(text4);
            text5 = new Text("....").setBackgroundColor(ColorConstants.YELLOW).setFontSize(50);
            paragraph2.add(text5);
            text6 = new Text("Wow!").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(50);
            paragraph2.add(text6);
            paragraph2.add("Hello World");

            div.add(paragraph).add(new AreaBreak()).add(paragraph2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    // TODO DEVSIX-10180 Support text rise in html mode for vertical text
    public void verticalAlignTextRiseTest() throws IOException, InterruptedException {
        String fileName = "verticalAlignTextRise";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div();
            div.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            div.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            PdfFont helvetica = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            div.setFont(helvetica);

            Paragraph paragraph = new Paragraph().setFontSize(30).setBackgroundColor(new DeviceRgb(187, 187, 255));
            Text text1 = new Text("Text").setBackgroundColor(new DeviceRgb(255, 255, 211));
            Text text2 = new Text("rise").setBackgroundColor(new DeviceRgb(229, 235, 253))
                    .setTextRise(20);
            Text text3 = new Text("1").setBackgroundColor(new DeviceRgb(255, 255, 211));
            text3.setProperty(Property.INLINE_VERTICAL_ALIGNMENT,
                    new InlineVerticalAlignment(InlineVerticalAlignmentType.FIXED, -20));
            Text text4 = new Text("2").setBackgroundColor(new DeviceRgb(229, 235, 253));
            text4.setProperty(Property.INLINE_VERTICAL_ALIGNMENT,
                    new InlineVerticalAlignment(InlineVerticalAlignmentType.FIXED, 20));
            Text text5 = new Text("check").setBackgroundColor(new DeviceRgb(255, 255, 211));
            paragraph.add(text1)
                    .add(text2)
                    .add(text3)
                    .add(text4)
                    .add(text5);

            document.add(div.add(paragraph));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void inlineVerticalAlignmentTest() throws IOException, InterruptedException {
        String fileName = "inlineVerticalAlignment";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div();
            div.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            div.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            PdfFont helvetica = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            div.setFont(helvetica);

            Paragraph paragraph = new Paragraph().setFontSize(60).setBackgroundColor(new DeviceRgb(187, 187, 255));
            Text text = new Text("Text").setBackgroundColor(new DeviceRgb(255, 255, 211));

            paragraph.add(text);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.BASELINE);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.TEXT_TOP);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.TEXT_BOTTOM);
            paragraph.add("\n");

            paragraph.add(text);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.SUB);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.SUPER);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.FIXED);
            paragraph.add("\n");

            paragraph.add(text);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.FRACTION);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.MIDDLE);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.TOP);
            addAlignedElement(paragraph, InlineVerticalAlignmentType.BOTTOM);
            paragraph.add("\n");

            // Property.LEADING is not supported for vertical text for now.
            paragraph.setProperty(Property.LINE_HEIGHT, LineHeight.createMultipliedValue(2));

            document.add(div.add(paragraph));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void occupiedAreaDivTest() throws IOException, InterruptedException {
        String fileName = "occupiedAreaDiv";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div().setFontSize(50)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 1));
            PdfFont bioRhyme = PdfFontFactory.createFont(EXPANDED_FONT);
            div.setFont(bioRhyme);
            div.setProperty(Property.LINE_HEIGHT, LineHeight.createMultipliedValue(2));

            Div div1 = new Div()
                    .setBackgroundColor(new DeviceRgb(210, 250, 179))
                    .setBorder(new SolidBorder(new DeviceRgb(0, 128, 0), 1));
            div1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            div1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Div div2 = new Div()
                    .setBackgroundColor(new DeviceRgb(210, 250, 179))
                    .setBorder(new SolidBorder(new DeviceRgb(0, 128, 0), 1));
            div2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);

            Text text1 = new Text("W").setBackgroundColor(new DeviceRgb(255, 255, 211));
            Text text2 = new Text("j").setBackgroundColor(new DeviceRgb(229, 235, 253))
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 1));
            Text text3 = new Text("50").setBackgroundColor(new DeviceRgb(255, 255, 211));
            Text text4 = new Text("10").setBackgroundColor(new DeviceRgb(229, 235, 253)).setFontSize(10);
            Text text5 = new Text("30").setBackgroundColor(new DeviceRgb(255, 255, 211))
                    .setBorder(new SolidBorder(ColorConstants.RED, 1)).setFontSize(30);
            div1.add(new Paragraph().add(text1)
                    .add(text2)
                    .add(text3)
                    .add(text4)
                    .add(text5));
            div2.add(new Paragraph().add(text1)
                    .add(text2)
                    .add(text3)
                    .add(text4)
                    .add(text5));
            div.add(div1).add(div2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void occupiedAreaParagraphTest() throws IOException, InterruptedException {
        String fileName = "occupiedAreaParagraph";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div().setFontSize(50)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.DARK_GRAY, 1));
            PdfFont bioRhyme = PdfFontFactory.createFont(EXPANDED_FONT);
            div.setFont(bioRhyme);
            div.setProperty(Property.LINE_HEIGHT, LineHeight.createMultipliedValue(2));

            Paragraph paragraph1 = new Paragraph()
                    .setBackgroundColor(new DeviceRgb(210, 250, 179))
                    .setBorder(new SolidBorder(new DeviceRgb(0, 128, 0), 1));
            paragraph1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Paragraph paragraph2 = new Paragraph()
                    .setBackgroundColor(new DeviceRgb(210, 250, 179))
                    .setBorder(new SolidBorder(new DeviceRgb(0, 128, 0), 1));
            paragraph2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);

            Text text1 = new Text("W").setBackgroundColor(new DeviceRgb(255, 255, 211));
            Text text2 = new Text("j").setBackgroundColor(new DeviceRgb(229, 235, 253))
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 1));
            Text text3 = new Text("50").setBackgroundColor(new DeviceRgb(255, 255, 211));
            Text text4 = new Text("10").setBackgroundColor(new DeviceRgb(229, 235, 253)).setFontSize(10);
            Text text5 = new Text("30").setBackgroundColor(new DeviceRgb(255, 255, 211))
                    .setBorder(new SolidBorder(ColorConstants.RED, 1)).setFontSize(30);
            paragraph1.add(text1)
                    .add(text2)
                    .add(text3)
                    .add(text4)
                    .add(text5);
            paragraph2.add(text1)
                    .add(text2)
                    .add(text3)
                    .add(text4)
                    .add(text5);
            div.add(paragraph1).add(paragraph2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    private void addAlignedElement(Paragraph p, InlineVerticalAlignmentType verticalAlignment) {
        Text text1 = new Text(" " + verticalAlignment + " ");
        text1.setFontSize(12).setBackgroundColor(new DeviceRgb(229, 235, 253));
        if (verticalAlignment == InlineVerticalAlignmentType.FIXED) {
            text1.setProperty(Property.INLINE_VERTICAL_ALIGNMENT, new InlineVerticalAlignment(verticalAlignment, 25));
        } else if (verticalAlignment == InlineVerticalAlignmentType.FRACTION) {
            text1.setProperty(Property.INLINE_VERTICAL_ALIGNMENT,
                    new InlineVerticalAlignment(verticalAlignment, 0.20F));
        } else {
            text1.setProperty(Property.INLINE_VERTICAL_ALIGNMENT, new InlineVerticalAlignment(verticalAlignment));
        }
        p.add(text1);
    }
}
