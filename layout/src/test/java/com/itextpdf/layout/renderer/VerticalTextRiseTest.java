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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.InlineVerticalAlignment;
import com.itextpdf.layout.properties.InlineVerticalAlignmentType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;


@Tag("IntegrationTest")
public class VerticalTextRiseTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/renderer/VerticalTextRiseTest/";
    private static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/layout/renderer/VerticalTextRiseTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void inheritedZeroOverrideTextRiseTest() throws IOException, InterruptedException {
        Div div = new Div();
        div.setProperty(Property.TEXT_RISE, 12f);
        Paragraph inherited = createParagraph()
                .add(decoratedText("Zero ").setTextRise(0))
                .add(decoratedText("Neg ").setTextRise(-12))
                .add(decoratedText("Inherited"));
        Paragraph overridden = createParagraph()
                .add(decoratedText("Zero ").setTextRise(0))
                .add(decoratedText("Pos ").setTextRise(8))
                .add(decoratedText("paragraph"));
        overridden.setProperty(Property.TEXT_RISE, -8f);
        div.add(inherited).add(overridden);
        createPdfAndCompare("inheritedZeroOverrideTextRise", div);
    }

    @Test
    public void textRiseFixedAlignmentTest() throws IOException, InterruptedException {
        Paragraph paragraph = createParagraph().add("Base line");
        // Equal opposite offsets must cancel; equal signs must add.
        for (float[] offsets : new float[][]{{20, -20}, {-20, 20}, {8, 12}, {-8, -12}, {0, 12}}) {
            Text text = decoratedText("text").setTextRise(offsets[0]);
            text.setProperty(Property.INLINE_VERTICAL_ALIGNMENT,
                    new InlineVerticalAlignment(InlineVerticalAlignmentType.FIXED, offsets[1]));
            paragraph.add(text);
        }
        createPdfAndCompare("textRiseFixedAlignment", new PageSize(200, 800), paragraph);
    }

    @Test
    public void textRiseWithBorderAndPaddingTest() throws IOException, InterruptedException {
        Div div = new Div();
        Paragraph paragraph = createParagraph();
        for (float[] style : new float[][]{{-15, 10, 0}, {15, 10, 0}, {-15, 0, 30}, {15, 0, 30},
                {-15, 10, 30}, {15, 10, 30}}) {
            Text text = new Text("Text rise " + (int) style[0]).setTextRise(style[0])
                    .setBackgroundColor(ColorConstants.YELLOW);
            if (style[1] > 0) {
                text.setBorder(new SolidBorder(style[1]));
            }
            text.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(style[2]));
            text.setProperty(Property.PADDING_RIGHT, UnitValue.createPointValue(style[2]));
            paragraph.add(new Text("Before ").setBackgroundColor(ColorConstants.ORANGE))
                    .add(text)
                    .add(new Text(" After").setBackgroundColor(ColorConstants.ORANGE))
                    .add("\n");
        }
        div.add(paragraph);
        createPdfAndCompare("textRiseWithBorderAndPadding", div);
    }

    @Test
    public void textRiseEmptyTextAndNewlinesTest() throws IOException, InterruptedException {
        Paragraph paragraph = createParagraph()
                .add(new Text("base line").setBackgroundColor(ColorConstants.ORANGE))
                .add(new Text("").setTextRise(40).setBackgroundColor(ColorConstants.BLUE))
                .add(decoratedText("First ").setTextRise(12))
                .add(new Text("   ").setTextRise(-12).setBackgroundColor(ColorConstants.RED))
                .add(new Text("\n\n").setTextRise(24))
                .add(new Text("").setTextRise(-40).setBackgroundColor(ColorConstants.ORANGE))
                .add(new Text("base line").setBackgroundColor(ColorConstants.ORANGE))
                .add(decoratedText("Second\r\nThird ").setTextRise(-12))
                .add(new Text("base line").setBackgroundColor(ColorConstants.ORANGE))
                .add(decoratedText("Zero").setTextRise(0))
                .add(new Text("\n").setTextRise(-24));
        createPdfAndCompare("textRiseWEmptyTextAndNewlines", paragraph);
    }

    @Test
    public void textRiseWithWrappingAndPageBreakTest() throws IOException, InterruptedException {
        String text = "some text rise";
        Paragraph paragraph2 = createParagraph()
                .add(new Text("base").setBackgroundColor(ColorConstants.ORANGE))
                .add(decoratedText(text).setTextRise(24))
                .add(new Text("base").setBackgroundColor(ColorConstants.ORANGE));
        createPdfAndCompare("textRiseWithWrappingAndPageBreak", new PageSize(100, 240), paragraph2);
    }

    private static Paragraph createParagraph() {
        Paragraph paragraph = new Paragraph().setFontSize(16);
        paragraph.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        paragraph.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        return paragraph;
    }

    private static Text decoratedText(String text) {
        return new Text(text).setBackgroundColor(ColorConstants.YELLOW).setBorder(new SolidBorder(0.5f))
                .setUnderline().setLineThrough();
    }

    private static void createPdfAndCompare(String name, IBlockElement... elements) throws IOException, InterruptedException {
        createPdfAndCompare(name, PageSize.A4, elements);
    }

    private static void createPdfAndCompare(String name, PageSize pageSize, IBlockElement... elements)
            throws IOException, InterruptedException {
        String outFile = DESTINATION_FOLDER + name + ".pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_" + name + ".pdf";
        try (Document document = new Document(new PdfDocument(new PdfWriter(outFile)), pageSize)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);
            for (IBlockElement element : elements) {
                document.add(element);
            }
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFile, cmpFile, DESTINATION_FOLDER));
    }
}
