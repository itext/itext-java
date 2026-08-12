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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.layout.properties.Underline;
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

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicVerticalTextTest() throws IOException, InterruptedException {
        String fileName = "basicVerticalText";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.add(new Text("some long vertical text to trigger multiple line breaks. Font size will be also increased to make it easier."));
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.add(new Text("some long vertical text\nto trigger multiple line breaks.\nFont size will be also increased\nto make it easier."));
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.add(new Text("some long vertical\ntext to trigger multiple line breaks.\nFont size will be also increased to make it easier.\n"));
            paragraph.add(new Text("Additional chunk of text,\n to trigger page break.\nFont size increased even further."));
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            Text text = new Text("some text");
            text.setProperty(Property.ITALIC_SIMULATION, true);
            text.setProperty(Property.BOLD_SIMULATION, true);
            text.setProperty(Property.UNDERLINE, Collections.singletonList(new Underline(ColorConstants.RED, 1, .75F, 0, 0,
                    1 / 4F,
                    PdfCanvasConstants.LineCapStyle.BUTT)));
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

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
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            Text normalText = new Text("Normal\ntext").setBackgroundColor(ColorConstants.CYAN);
            // TODO DEVSIX-10137 double line break is ignored,
            //  although it's suppose to create a separate line without any content.
            Text whitespacesRiddenText = new Text(
                    "     Hello     \n \n World    \n        \n  "
            ).setBackgroundColor(ColorConstants.LIGHT_GRAY);
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
            hParagraph.setWidth((float) (12 * 3 + 12/2.4 * 2));
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
    public void lineTroughWithTextRiseTest() throws IOException, InterruptedException {
        //TODO DEVSIX-10137 :Support baselining and line width for vertical drawing
        String outFileName = DESTINATION_FOLDER + "lineTroughWithTextRise.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_lineTroughWithTextRise.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        Text textUp = new Text("textRise10f_with_lineThrough");
        textUp.setTextRise(-10f);
        textUp.setLineThrough();
        textUp.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        textUp.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

        Text textDown = new Text("textRise-10f_with_lineThrough");
        textDown.setTextRise(-10f);
        textDown.setLineThrough();
        textDown.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        textDown.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

        Paragraph n = new Paragraph("baseline");
        n.add(textUp).add(textDown);
        n.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        n.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

        document.add(n);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void underlineTest() throws IOException, InterruptedException {
        //TODO DEVSIX-10137 :Support line width for vertical drawing should
        // fix issue for the last part of pages 2 and 3.
        String outFileName = DESTINATION_FOLDER + "underline.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_underline.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
                Document document = new Document(pdfDocument)) {

            Paragraph p = new Paragraph("Yellow text with pink stroked dashed underline.")
                    .setFontSize(45).setFontColor(ColorConstants.YELLOW);
            Underline underline = new Underline(null, 0, 0.1f, 0, -0.1f, PdfCanvasConstants.LineCapStyle.BUTT)
                    .setStrokeWidth(2).setStrokeColor(new TransparentColor(ColorConstants.PINK, 0.5f))
                    .setDashPattern(new float[]{5, 5, 10, 5}, 5);
            p.setUnderline(underline);
            p.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            p.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            TransparentColor strokeColor = new TransparentColor(ColorConstants.GREEN, 0.5f);
            Paragraph p2 = new Paragraph("Text with line-through and default underline.")
                    .setFontSize(50).setStrokeWidth(1).setFontColor(ColorConstants.DARK_GRAY)
                    .setStrokeColor(strokeColor);
            Underline underline2 = new Underline(ColorConstants.DARK_GRAY, 0, 0.1f, 0, 0.3f,
                    PdfCanvasConstants.LineCapStyle.BUTT)
                    .setStrokeWidth(1).setStrokeColor(strokeColor);
            p2.setUnderline(underline2);
            p2.setUnderline();
            p2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            p2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Underline underline3 = new Underline(null, 0, 0.1f, 0, 0.9f, PdfCanvasConstants.LineCapStyle.BUTT);
            Paragraph p3 = new Paragraph("Text with null font color and default overline.").setFontSize(50)
                    .setFontColor((TransparentColor) null);
            p3.setUnderline(underline3);
            p3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            p3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            //This line should be around the middle of the text compared to horizontal text.
            Underline underline4 = new Underline(null, 0, 0.1f, 15, 0f, PdfCanvasConstants.LineCapStyle.BUTT);
            Paragraph p4 = new Paragraph("Text with custom yPosition (15).").setFontSize(50);

            p4.setUnderline(underline4);

            p4.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            p4.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            document.add(p);
            document.add(p2);
            document.add(p3);
            document.add(p4);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void fontStyleSimulationTest01() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "fontStyleSimulationTest01.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_fontStyleSimulationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph("I'm underlined").setUnderline();
        p.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        p.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

        document.add(p);
        p = new Paragraph("I'm strikethrough").setLineThrough();
        p.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        p.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        document.add(p);
        p = new Paragraph(new Text("I'm a bold simulation font").setBackgroundColor(ColorConstants.GREEN)).simulateBold();
        p.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        p.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        document.add(p);
        p = new Paragraph(new Text("I'm an italic simulation font").setBackgroundColor(ColorConstants.GREEN)).simulateItalic();
        p.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        p.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        document.add(p);
        p = new Paragraph(new Text("I'm a super bold italic underlined linethrough piece of text and no one can be better than me, even if " +
                "such a long description will cause me to occupy two lines").setBackgroundColor(ColorConstants.GREEN))
                .simulateItalic().simulateBold().setUnderline().setLineThrough();
        p.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        p.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        document.add(p);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }
}
