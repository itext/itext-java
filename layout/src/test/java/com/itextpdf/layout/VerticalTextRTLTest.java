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
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.VerticalParagraph;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.FloatPropertyValue;
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
import java.util.Collections;
import java.util.Map;

@Tag("IntegrationTest")
public class VerticalTextRTLTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/VerticalTextRTLTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/VerticalTextRTLTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static Collection<Float> widthValues() {
        return Arrays.asList(0F, 400F, 60F);
    }

    @ParameterizedTest
    @MethodSource("widthValues")
    public void basicVerticalRtlTest(Float width) throws IOException, InterruptedException {
        String fileName = "basicVerticalRtl" + (width == 0F ? "" : ("_" + width.intValue()));
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            VerticalParagraph paragraph = new VerticalParagraph(true);
            paragraph.setHeight(300).setFontSize(16).setBorder(new SolidBorder(1));
            if (width != 0F) {
                paragraph.setWidth((float) width);
            }
            paragraph.add(new Text("The quick brown fox jumps over the lazy dog. 1234567890 ABCDEFG abcdefg."));
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void pageSplitTest() throws IOException, InterruptedException {
        String fileName = "pageSplit";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.add(new Div().setHeight(600));

            VerticalParagraph paragraph = new VerticalParagraph(true);
            paragraph.setHeight(300).setWidth(80).setFontSize(16)
                    .setBorder(new SolidBorder(1))
                    .setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(new Text("The quick brown fox jumps over the lazy dog. 1234567890 ABCDEFG abcdefg."));
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void directionRtlTest() throws IOException, InterruptedException {
        String fileName = "directionRtl";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            VerticalParagraph paragraph = new VerticalParagraph(true);
            paragraph.setProperty(Property.BASE_DIRECTION, BaseDirection.RIGHT_TO_LEFT);
            paragraph.setProperty(Property.TEXT_ALIGNMENT, TextAlignment.RIGHT);
            paragraph.setHeight(300).setFontSize(16)
                    .setBorder(new SolidBorder(1))
                    .setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(new Text("The quick brown fox jumps over the lazy dog. 1234567890 ABCDEFG abcdefg."));
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void innerTextVerticalRlTest() throws IOException, InterruptedException {
        String fileName = "innerTextVerticalRl";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            paragraph.setHeight(300).setFontSize(16).setBorder(new SolidBorder(1));
            Text text = new Text("The quick brown fox jumps over the lazy dog. 1234567890 ABCDEFG abcdefg.");
            text.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_RL);
            text.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text.setBackgroundColor(ColorConstants.YELLOW);
            paragraph.add(text);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void severalInnerTextVerticalRlTest() throws IOException, InterruptedException {
        String fileName = "severalInnerTextVerticalRl";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Paragraph paragraph = new Paragraph();
            paragraph.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            paragraph.setHeight(300).setFontSize(16).setBorder(new SolidBorder(1));
            Text text = new Text("The quick brown fox jumps over the lazy dog. 1234567890 ABCDEFG abcdefg.");
            text.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_RL);
            text.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text.setBackgroundColor(ColorConstants.YELLOW);
            Text text2 = new Text("One more\nvertical\nRTL text\nwith line breaks.");
            text2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_RL);
            text2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            text2.setBackgroundColor(ColorConstants.PINK);
            paragraph.add(text).add(text2);
            document.add(paragraph);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    // Float + vertical-rl writing mode is not supported.
    public void floatWithVerticalRlTextTest() throws IOException, InterruptedException {
        String fileName = "floatWithVerticalRlText";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {

            // Create a floated DIV
            Div floatedDiv = new Div()
                    .setWidth(200)
                    .setHeight(200)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBorder(new SolidBorder(ColorConstants.BLACK, 1))
                    .add(new Paragraph("Floated\nElement"));
            floatedDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
            document.add(floatedDiv);

            // Add paragraph between the floated elements
            Paragraph normalParagraph = new Paragraph("Normal text added after right floated div, " +
                    "but before the next left floated div.");
            document.add(normalParagraph);

            floatedDiv.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
            document.add(floatedDiv);

            // Add another paragraph after the floated elements.
            normalParagraph = new Paragraph("Normal text added after vertical paragraphs and divs.");
            document.add(normalParagraph);

            document.add(new CustomVerticalParagraph("This is a vertical paragraph with a lot of text to " +
                    "demonstrate how it interacts with floated elements. It should wrap around the floated elements " +
                    "and continue on the next line if necessary. " +
                    "The quick brown fox jumps over the lazy dog. 1234567890 ABCDEFG abcdefg.", true)
                    .setHeight(300));
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    private static class CustomVerticalParagraph extends VerticalParagraph {

        public CustomVerticalParagraph(String text, boolean rightToLeftProgression) {
            super(text, rightToLeftProgression);
        }

        @Override
        public Map<Integer, String> getUnsupportedProperties() {
            return Collections.<Integer, String>emptyMap();
        }
    }
}
