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
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.OverflowPropertyValue;
import com.itextpdf.layout.properties.OverflowWrapPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
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
public class VerticalTextOverflowTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/VerticalTextOverflowTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/VerticalTextOverflowTest/";
    private static final String EXPANDED_FONT =
            "./src/test/resources/com/itextpdf/layout/fonts/BioRhymeExpanded-Regular.ttf";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @ParameterizedTest
    @MethodSource("overflowValues")
    public void overflowTest(OverflowPropertyValue overflowX, OverflowPropertyValue overflowY)
            throws IOException, InterruptedException {
        String fileName = "overflow_" + overflowX + "_" + overflowY;
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div();
            PdfFont bioRhyme = PdfFontFactory.createFont(EXPANDED_FONT);
            div.setFont(bioRhyme).setFontSize(40);
            div.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.NORMAL);

            Paragraph paragraph1 = new Paragraph()
                    .setHeight(400).setWidth(200).setBackgroundColor(new DeviceRgb(187, 187, 255));
            paragraph1.setProperty(Property.OVERFLOW_X, overflowX);
            paragraph1.setProperty(Property.OVERFLOW_Y, overflowY);

            Paragraph paragraph2 = new Paragraph()
                    .setHeight(400).setWidth(200).setBackgroundColor(new DeviceRgb(255, 187, 187));
            paragraph2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph2.setProperty(Property.OVERFLOW_X, overflowX);
            paragraph2.setProperty(Property.OVERFLOW_Y, overflowY);

            Text text1 = new Text("WWWWWW").setBackgroundColor(ColorConstants.YELLOW);
            Text text2 = new Text("aaaaaaa").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(20);
            Text text3 = new Text("iiiii").setBackgroundColor(ColorConstants.YELLOW);
            String text4 = "\noverflow-x: " + overflowX + "; overflow-y: " + overflowY + ";";

            paragraph1.add(text1).add(text2).add(text3).add(text4);
            paragraph2.add(text1).add(text2).add(text3).add(text4);

            div.add(paragraph1).add(new AreaBreak()).add(paragraph2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @ParameterizedTest
    @MethodSource("overflowWrapValues")
    public void overflowWrapTest(OverflowWrapPropertyValue overflowWrap) throws IOException, InterruptedException {
        String fileName = "overflowWrap_" + overflowWrap;
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div();
            PdfFont bioRhyme = PdfFontFactory.createFont(EXPANDED_FONT);
            div.setFont(bioRhyme).setFontSize(40);
            div.setProperty(Property.OVERFLOW_WRAP, overflowWrap);

            Paragraph paragraph1 = new Paragraph()
                    .setHeight(400).setWidth(200).setBackgroundColor(new DeviceRgb(187, 187, 255));
            paragraph1.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            paragraph1.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

            Paragraph paragraph2 = new Paragraph()
                    .setHeight(400).setWidth(200).setBackgroundColor(new DeviceRgb(255, 187, 187));
            paragraph2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph2.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            paragraph2.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);

            Text text1 = new Text("WWWWWW").setBackgroundColor(ColorConstants.YELLOW);
            Text text2 = new Text("aaaaaaa").setBackgroundColor(new DeviceRgb(173, 255, 47)).setFontSize(20);
            Text text3 = new Text("iiiii").setBackgroundColor(ColorConstants.YELLOW);
            String text4 = "\noverflow-wrap: " + overflowWrap;

            paragraph1.add(text1).add(text2).add(text3).add(text4);
            paragraph2.add(text1).add(text2).add(text3).add(text4);

            div.add(paragraph1).add(new AreaBreak()).add(paragraph2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void noWrapTest() throws IOException, InterruptedException {
        String fileName = "noWrap";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div div = new Div();
            div.setFontSize(20);
            div.setProperty(Property.OVERFLOW_WRAP, OverflowWrapPropertyValue.NORMAL);

            Paragraph paragraph1 = new Paragraph()
                    .setHeight(50).setWidth(100).setBackgroundColor(new DeviceRgb(187, 187, 255));
            paragraph1.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            paragraph1.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
            paragraph1.setProperty(Property.NO_SOFT_WRAP_INLINE, Boolean.TRUE);

            Paragraph paragraph2 = new Paragraph()
                    .setHeight(50).setWidth(100).setBackgroundColor(new DeviceRgb(255, 187, 187));
            paragraph2.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
            paragraph2.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
            paragraph2.setProperty(Property.OVERFLOW_X, OverflowPropertyValue.VISIBLE);
            paragraph2.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
            paragraph2.setProperty(Property.NO_SOFT_WRAP_INLINE, Boolean.TRUE);

            String text = "no soft wrap inline";

            paragraph1.add("horizontal " + text);
            paragraph2.add("vertical " + text);

            div.add(paragraph1).add(paragraph2);
            document.add(div);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    public static Iterable<Object[]> overflowValues() {
        return Arrays.asList(new Object[][]{
                {OverflowPropertyValue.VISIBLE, OverflowPropertyValue.VISIBLE},
                {OverflowPropertyValue.VISIBLE, OverflowPropertyValue.HIDDEN},
                {OverflowPropertyValue.HIDDEN, OverflowPropertyValue.VISIBLE},
                {OverflowPropertyValue.HIDDEN, OverflowPropertyValue.HIDDEN}});
    }

    public static Collection<OverflowWrapPropertyValue> overflowWrapValues() {
        return Arrays.asList(OverflowWrapPropertyValue.NORMAL, OverflowWrapPropertyValue.ANYWHERE,
                OverflowWrapPropertyValue.BREAK_WORD);
    }
}
