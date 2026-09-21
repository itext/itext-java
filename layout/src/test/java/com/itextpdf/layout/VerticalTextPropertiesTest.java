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
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.VerticalParagraph;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.FloatPropertyValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.ParagraphOrphansControl;
import com.itextpdf.layout.properties.ParagraphWidowsControl;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.TextAnchor;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.layout.splitting.BreakAllSplitCharacters;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Tag("IntegrationTest")
public class VerticalTextPropertiesTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/VerticalTextPropertiesTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/VerticalTextPropertiesTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void hyphenationTest() throws IOException, InterruptedException {
        String fileName = "hyphenation";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            VerticalParagraph paragraph = new VerticalParagraph(false);

            paragraph.setHyphenation(new HyphenationConfig("en", "EN", 2, 2));

            Text text = new Text("Hyphen hyphen hyphen hyphen hyphen hyphen hyphen ");
            paragraph.add(text).add("non\u2011breaking").add("\n").add(text).add("non\u2010breaking");

            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void horizontalAlignmentTest() throws IOException, InterruptedException {
        String fileName = "horizontalAlignment";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("first line\nsecond line\nthird line"));

            paragraph.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(paragraph);

            paragraph.setHorizontalAlignment(HorizontalAlignment.RIGHT);
            document.add(paragraph);

            paragraph.setHorizontalAlignment(HorizontalAlignment.LEFT);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void textAlignmentTest() throws IOException, InterruptedException {
        String fileName = "textAlignment";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("first line\nsecond line\nthird line"));
            paragraph.setHeight(200).setBorder(new SolidBorder(1));

            paragraph.setTextAlignment(TextAlignment.CENTER);
            document.add(paragraph);

            paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
            document.add(paragraph);

            paragraph.setTextAlignment(TextAlignment.RIGHT);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void textRenderingModeTest() throws IOException, InterruptedException {
        String fileName = "textRenderingMode";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("first\nsecond\nthird"));
            paragraph.setBorder(new SolidBorder(1)).setFontSize(20).setStrokeColor(ColorConstants.CYAN);

            paragraph.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
            document.add(paragraph);

            paragraph.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
            document.add(paragraph);

            paragraph.setBackgroundColor(ColorConstants.PINK);
            paragraph.setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE_CLIP);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void rotationAngleTest() throws IOException, InterruptedException {
        String fileName = "rotationAngle";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("first line\nsecond line\nthird line"));
            paragraph.setHeight(200).setBorder(new SolidBorder(1));

            paragraph.setRotationAngle(Math.PI / 2);
            document.add(paragraph);

            paragraph.setRotationAngle(-Math.PI / 2);
            document.add(paragraph);

            paragraph.setRotationAngle(Math.PI / 4);
            document.add(paragraph);

            paragraph.setRotationAngle(-Math.PI / 4);
            document.add(paragraph);

            paragraph.setRotationAngle(Math.PI);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void orphansControlTest() throws IOException, InterruptedException {
        String fileName = "orphansControl";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            // Add spacing to force page break
            document.add(new Div().setHeight(600));

            // Paragraph with enough lines to demonstrate orphans behavior
            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\nLine 6\nLine 7\nLine 8\nLine 9\nLine 10"));
            paragraph.setHeight(250).setWidth(100).setBorder(new SolidBorder(1));

            // Control orphans (minimum 8 lines at the start if split)
            paragraph.setOrphansControl(new ParagraphOrphansControl(8));

            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void widowsControlTest() throws IOException, InterruptedException {
        String fileName = "widowsControl";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            // Add spacing to force page break
            document.add(new Div().setHeight(600));

            // Paragraph with enough lines to demonstrate widows behavior
            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\nLine 6\nLine 7\nLine 8\nLine 9\nLine 10"));
            paragraph.setHeight(250).setWidth(100).setBorder(new SolidBorder(1));

            // Control widows (minimum 4 lines at the end if split)
            paragraph.setWidowsControl(new ParagraphWidowsControl(5, 1, true));

            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.UNSUPPORTED_PROPERTY, count = 2))
    public void floatWithVerticalTextTest() throws IOException, InterruptedException {
        String fileName = "floatWithVerticalText";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            // Create a floated DIV
            Div floatedDiv = new Div()
                    .setWidth(50)
                    .setHeight(200)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                    .add(new Paragraph("Floated\nElement"));
            floatedDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
            document.add(floatedDiv);

            // Create a floated VerticalParagraph
            VerticalParagraph verticalParagraph = new VerticalParagraph(false);
            verticalParagraph.add(new Text("Line 1\nLine 2\nLine 3\nLine 4\nLine 5\nLine 6\nLine 7\nLine 8"));
            verticalParagraph.setHeight(200).setWidth(150).setBorder(new SolidBorder(1));
            verticalParagraph.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
            document.add(verticalParagraph);

            // Add paragraph between the floated elements
            Paragraph normalParagraph = new Paragraph("Normal text added after right floated vertical paragraph " +
                    "and div, but before the next left floated elements.");
            document.add(normalParagraph);

            floatedDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
            document.add(floatedDiv);

            verticalParagraph.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
            document.add(verticalParagraph);

            // Add another paragraph after the floated elements.
            normalParagraph = new Paragraph("Normal text added after vertical paragraphs and divs.");
            document.add(normalParagraph);

            document.add(new VerticalParagraph("This is a vertical paragraph with a lot of text to demonstrate " +
                    "how it interacts with floated elements. It should wrap around the floated elements " +
                    "and continue on the next line if necessary. " +
                    "The quick brown fox jumps over the lazy dog. 1234567890 ABCDEFG abcdefg.", false)
                    .setHeight(300));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void horizontalScalingTest() throws IOException, InterruptedException {
        String fileName = "horizontalScaling";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            Text text = new Text("Scaled Text Example");
            paragraph.add(text);
            paragraph.setHeight(200).setBorder(new SolidBorder(1));

            // Test different horizontal scaling values
            text.setProperty(Property.HORIZONTAL_SCALING, 0.75f);
            document.add(paragraph);

            text.setProperty(Property.HORIZONTAL_SCALING, 1.0f);
            document.add(paragraph);

            text.setProperty(Property.HORIZONTAL_SCALING, 1.25f);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void characterSpacingTest() throws IOException, InterruptedException {
        String fileName = "characterSpacing";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("Character\nSpacing\nTest"));
            paragraph.setHeight(200).setWidth(100).setBorder(new SolidBorder(1));

            // Test different character spacing values
            paragraph.setCharacterSpacing(-5);
            document.add(paragraph);

            paragraph.setCharacterSpacing(2);
            document.add(paragraph);

            paragraph.setCharacterSpacing(5);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void wordSpacingTest() throws IOException, InterruptedException {
        String fileName = "wordSpacing";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("Word spacing test with multiple words here"));
            paragraph.setHeight(200).setWidth(100).setBorder(new SolidBorder(1));

            // Test different word spacing values
            paragraph.setWordSpacing(-20);
            document.add(paragraph);

            paragraph.setWordSpacing(0);
            document.add(paragraph);

            paragraph.setWordSpacing(20);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void spacingRatioTest() throws IOException, InterruptedException {
        String fileName = "spacingRatio";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("Spacing Ratio Test Text"));
            paragraph.setHeight(200).setWidth(100).setBorder(new SolidBorder(1));
            paragraph.setTextAlignment(TextAlignment.JUSTIFIED_ALL);

            // Test different spacing ratios
            paragraph.setProperty(Property.SPACING_RATIO, 0.8f);
            document.add(paragraph);

            paragraph.setProperty(Property.SPACING_RATIO, 1.0f);
            document.add(paragraph);

            paragraph.setProperty(Property.SPACING_RATIO, 1.5f);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.UNSUPPORTED_PROPERTY, count = 1))
    public void tabStopsTest() throws IOException, InterruptedException {
        String fileName = "tabStops";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document doc = new Document(pdfDocument)) {
            doc.add(new Paragraph("x-coordinate = 100").setFontColor(ColorConstants.RED)
                    .setFirstLineIndent(100).setFontSize(8));
            doc.add(new Paragraph("x-coordinate = 200").setFontColor(ColorConstants.GREEN)
                    .setFirstLineIndent(200).setFontSize(8));
            doc.add(new Paragraph("x-coordinate = 300").setFontColor(ColorConstants.BLUE)
                    .setFirstLineIndent(300).setFontSize(8));

            Paragraph p = new Paragraph()
                    .add("Hello, iText!\n").add(new Tab()).addTabStops(new TabStop(100, TabAlignment.CENTER,
                            new DashedLine(.5f)))
                    .add("Hi, iText!\n").add(new Tab()).addTabStops(new TabStop(200, TabAlignment.RIGHT,
                            new DashedLine(.5f)))
                    .add("Hello, iText!\n").add(new Tab()).addTabStops(new TabStop(300, TabAlignment.LEFT,
                            new DashedLine(.5f)))
                    .add("Hello, iText!\n");

            p.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            p.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            p.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            doc.add(p);

            float[] positions = {100, 200, 300};

            drawTabStopsPositions(positions, doc, 1, 0, 120);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.UNSUPPORTED_PROPERTY, count = 1))
    public void textAnchorTest() throws IOException, InterruptedException {
        String fileName = "textAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("Anchor Start\nAnchor Middle\nAnchor End"));
            paragraph.setHeight(200).setWidth(100).setBorder(new SolidBorder(1));
            paragraph.setProperty(Property.TEXT_ANCHOR, TextAnchor.END);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void textRiseTest() throws IOException, InterruptedException {
        String fileName = "textRise";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            Text text1 = new Text("Normal");
            Text text2 = new Text("Raised").setTextRise(5);
            Text text3 = new Text("Lowered").setTextRise(-5);
            paragraph.add(text1).add(" ").add(text2).add(" ").add(text3);
            paragraph.setHeight(200).setWidth(100).setBorder(new SolidBorder(1));

            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void splitCharactersTest() throws IOException, InterruptedException {
        String fileName = "splitCharacters";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            paragraph.add(new Text("Verylongwordthatmaysplitandcontinueontheline"));
            paragraph.setHeight(200).setWidth(80).setBorder(new SolidBorder(1));
            paragraph.setProperty(Property.SPLIT_CHARACTERS, new BreakAllSplitCharacters());
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void skewTest() throws IOException, InterruptedException {
        String fileName = "skew";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            Text text = new Text("Skewed Text Example");
            paragraph.add(text);
            paragraph.setHeight(200).setWidth(100).setBorder(new SolidBorder(1));

            // Test different skew angles
            text.setSkew(0, 10);
            document.add(paragraph);

            text.setSkew(10, 0);
            document.add(paragraph);

            text.setSkew(15, 15);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalScalingTest() throws IOException, InterruptedException {
        String fileName = "verticalScaling";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            VerticalParagraph paragraph = new VerticalParagraph(false);
            Text text = new Text("Scaled\nVertically");
            paragraph.add(text);
            paragraph.setHeight(200).setWidth(100).setBorder(new SolidBorder(1));

            // Test different vertical scaling values
            text.setProperty(Property.VERTICAL_SCALING, 0.75f);
            document.add(paragraph);

            text.setProperty(Property.VERTICAL_SCALING, 1.0f);
            document.add(paragraph);

            text.setProperty(Property.VERTICAL_SCALING, 1.5f);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    private void drawTabStopsPositions(float[] positions, Document doc, int pageNum, int yStart, int dy) {
        PdfCanvas canvas = new PdfCanvas(doc.getPdfDocument().getPage(pageNum));
        float left = doc.getLeftMargin();
        float h = doc.getPdfDocument().getPage(pageNum).getCropBox().getHeight() - yStart;

        canvas.saveState();
        canvas.setLineDash(4, 2);
        canvas.setLineWidth(0.5f);
        canvas.setLineDash(4, 2);
        for (float f : positions) {
            canvas.moveTo(left + f, h);
            canvas.lineTo(left + f, h - dy);
        }

        canvas.stroke();
        canvas.restoreState();
        canvas.release();
    }
}
