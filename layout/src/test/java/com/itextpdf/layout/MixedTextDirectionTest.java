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
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@Tag("IntegrationTest")
public class MixedTextDirectionTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/MixedTextDirectionTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/MixedTextDirectionTest/";

    public static Collection<WritingMode> mixedVertical() {
        return Arrays.asList(WritingMode.HORIZONTAL_TB, WritingMode.VERTICAL_LR, WritingMode.VERTICAL_RL);
    }

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void paragraphMixedTextTest() throws IOException, InterruptedException {
        String fileName = "paragraphMixedTextTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setHeight(200);

            Text text1 = new Text("vertical text chunk");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("horizontal text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("second vertical text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            paragraph.add(text2);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @ParameterizedTest
    @MethodSource("mixedVertical")
    // TODO DEVSIX-10200 Consider text elements with different writing-mode as inline-blocks,
    //  after that vertical RTL text chunks in vertical LTR paragraphs and vice versa will be fixed.
    public void paragraphMixedVerticalTextTest(WritingMode paragraphWritingMode)
            throws IOException, InterruptedException {
        String fileName = "paragraphMixedVerticalText_" + paragraphWritingMode.name();
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setHeight(200);
            paragraph.setProperty(Property.WRITING_MODE, paragraphWritingMode);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            paragraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

            Text text1 = new Text("vertical text chunk left-to-right ");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("vertical text chunk right-to-left ");
            text2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_RL);
            text2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("one more vertical text chunk left-to-right ");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);
            Text text4 = new Text("horizontal text ");
            text4.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text4.setBackgroundColor(ColorConstants.YELLOW);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            paragraph.add(text4);
            paragraph.add(text2);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @ParameterizedTest
    @MethodSource("mixedVertical")
    // TODO DEVSIX-10200 Consider text elements with different writing-mode as inline-blocks,
    //  after that vertical RTL text chunks in vertical LTR paragraphs and vice versa should be fixed.
    // No line breaks in vertical text with different writing-mode looks like workaround for horizontal text.
    public void paragraphMixedVerticalTextNoHeightTest(WritingMode paragraphWritingMode)
            throws IOException, InterruptedException {
        String fileName = "paragraphMixedVerticalTextNoHeight_" + paragraphWritingMode.name();
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setProperty(Property.WRITING_MODE, paragraphWritingMode);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            paragraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

            Text text1 = new Text("vertical text chunk\nleft-to-right ");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("vertical\ntext chunk\nright-to-left ");
            text2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_RL);
            text2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("one more\nvertical text chunk\nleft-to-right ");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);
            Text text4 = new Text("horizontal\ntext ");
            text4.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text4.setBackgroundColor(ColorConstants.YELLOW);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            paragraph.add(text4);
            paragraph.add(text2);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void paragraphMixedTextWithLineBreaksTest() throws IOException, InterruptedException {
        String fileName = "paragraphMixedTextWithLineBreaksTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);

            Text text1 = new Text("vertical text chunk\n");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("horizontal text chunk\n");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("second vertical text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            paragraph.add(text2);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void paragraphMixedTextNoEnoughHorizontalSpaceTest() throws IOException, InterruptedException {
        String fileName = "paragraphMixedTextNoEnoughHorizontalSpaceTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setWidth(300);

            Text text1 = new Text("vertical text chunk");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("horizontal text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("second vertical text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            for (int i = 0; i < 4; ++i) {
                paragraph.add(text1);
                paragraph.add(text2);
                paragraph.add(text3);
            }
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void paragraphMixedTextWithPageBreakTest() throws IOException, InterruptedException {
        String fileName = "paragraphMixedTextWithPageBreakTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setWidth(500);
            paragraph.setFontSize(20);

            Text text1 = new Text("vertical text chunk");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("horizontal text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("second vertical text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            for (int i = 0; i < 5; ++i) {
                paragraph.add(text1);
                paragraph.add(text2);
                paragraph.add(text3);
            }
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalParagraphMixedTest() throws IOException, InterruptedException {
        String fileName = "verticalParagraphMixedTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setHeight(200);
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Text text1 = new Text("vertical text chunk");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("horizontal text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("second vertical text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalParagraphMixedWithLineBreaksTest() throws IOException, InterruptedException {
        String fileName = "verticalParagraphMixedWithLineBreaksTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setHeight(200);
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Text text1 = new Text("vertical text chunk\n");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("horizontal text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("second vertical text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void mixedTextWithAlignmentTest() throws IOException, InterruptedException {
        String fileName = "mixedTextWithAlignmentTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED_ALL);

            Text text1 = new Text("vertical text chunk");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("horizontal text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("second vertical text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalMixedTextWithAlignmentTest() throws IOException, InterruptedException {
        String fileName = "verticalMixedTextWithAlignmentTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED_ALL);
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph.setHeight(700);

            Text text1 = new Text("vertical text");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("horizontal text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("second vertical text");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalWritingAtTextLevelTest() throws IOException, InterruptedException {
        String fileName = "verticalWritingAtTextLevelTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Text text1 = new Text("first text chunk ");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("second text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text(" third text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalWritingAtTextLevelTwoLinesTest() throws IOException, InterruptedException {
        String fileName = "verticalWritingAtTextLevelTwoLinesTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setFontSize(20);
            Text text1 = new Text("text chunk 1");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("text chunk 2");
            text2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("text chunk 3");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            for (int i = 0; i < 10; ++i) {
                paragraph.add(text1);
                paragraph.add(text2);
                paragraph.add(text3);
            }
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalWritingAtTextLevelPageBreakTest() throws IOException, InterruptedException {
        String fileName = "verticalWritingAtTextLevelPageBreakTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setFontSize(20);
            Text text1 = new Text("text chunk 1");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("text chunk 2");
            text2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text("text chunk 3");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            for (int i = 0; i < 30; ++i) {
                paragraph.add(text1);
                paragraph.add(text2);
                paragraph.add(text3);
            }
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalWritingAtTextLevelLongTextTest() throws IOException, InterruptedException {
        String fileName = "verticalWritingAtTextLevelLongTextTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setHeight(200);
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            Text text1 = new Text("some very long text\nchunk with\nvertical writing");
            text1.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text1.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("second text chunk ");
            text2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text(" small chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            text3.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalParagraphWithHorizontalTextTest() throws IOException, InterruptedException {
        String fileName = "verticalParagraphWithHorizontalTextTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

            Text text1 = new Text("first text chunk ");
            text1.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text1.setBackgroundColor(ColorConstants.MAGENTA);
            Text text2 = new Text("second text chunk");
            text2.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text2.setBackgroundColor(ColorConstants.CYAN);
            Text text3 = new Text(" third text chunk");
            text3.setProperty(Property.WRITING_MODE, WritingMode.HORIZONTAL_TB);
            text3.setBackgroundColor(ColorConstants.ORANGE);

            paragraph.add(text1);
            paragraph.add(text2);
            paragraph.add(text3);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }
}
