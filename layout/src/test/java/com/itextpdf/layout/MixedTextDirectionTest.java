package com.itextpdf.layout;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
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

import java.io.IOException;

@Tag("IntegrationTest")
public class MixedTextDirectionTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/MixedTextDirectionTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/MixedTextDirectionTest/";

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
