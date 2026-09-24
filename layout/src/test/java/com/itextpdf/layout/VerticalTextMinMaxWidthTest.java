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
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.RenderingMode;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalTextOrientation;
import com.itextpdf.layout.properties.WritingMode;
import com.itextpdf.layout.renderer.FlexContainerRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Tag("IntegrationTest")
public class VerticalTextMinMaxWidthTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/VerticalTextMinMaxWidthTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/VerticalTextMinMaxWidthTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void flexBasis0Test() throws IOException, InterruptedException {
        String fileName = "flexBasis0Test";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div flexContainer = createFlexContainer();
            flexContainer.setHeight(200);
            flexContainer.setWidth(400);

            Paragraph paragraph1 = new Paragraph("first flex child with flex-basis: 0");
            // flex-basis: 0 is needed to test min-width calculations.
            paragraph1.setProperty(Property.FLEX_BASIS, new UnitValue(UnitValue.POINT, 0));
            paragraph1.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph1.setMargin(10);
            flexContainer.add(paragraph1);

            Paragraph paragraph2 = new Paragraph("second flex child with flex-basis: 0");
            paragraph2.setProperty(Property.FLEX_BASIS, new UnitValue(UnitValue.POINT, 0));
            paragraph2.setBackgroundColor(ColorConstants.GRAY);
            paragraph2.setMargin(10);
            flexContainer.add(paragraph2);

            document.add(flexContainer);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void flexWithHeightTest() throws IOException, InterruptedException {
        String fileName = "flexWithHeightTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div flexContainer = createFlexContainer();
            flexContainer.setWidth(400);
            flexContainer.setHeight(200);

            Paragraph paragraph1 = new Paragraph("first flex child which wraps");
            paragraph1.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph1.setMargin(10);
            flexContainer.add(paragraph1);

            Paragraph paragraph2 = new Paragraph("second flex child which wraps");
            paragraph2.setBackgroundColor(ColorConstants.GRAY);
            paragraph2.setMargin(10);
            flexContainer.add(paragraph2);

            document.add(flexContainer);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void flexWithoutHeightTest() throws IOException, InterruptedException {
        String fileName = "flexWithoutHeightTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div flexContainer = createFlexContainer();
            flexContainer.setWidth(400);

            Paragraph paragraph1 = new Paragraph("first flex child which wraps");
            paragraph1.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph1.setMargin(10);
            flexContainer.add(paragraph1);

            Paragraph paragraph2 = new Paragraph("second flex child which wraps");
            paragraph2.setBackgroundColor(ColorConstants.GRAY);
            paragraph2.setMargin(10);
            flexContainer.add(paragraph2);

            document.add(flexContainer);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void flexPercentageHeightTest() throws IOException, InterruptedException {
        String fileName = "flexPercentageHeightTest";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";
        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            document.setProperty(Property.RENDERING_MODE, RenderingMode.HTML_MODE);

            Div flexContainer = createFlexContainer();
            flexContainer.setHeight(UnitValue.createPercentValue(50));
            flexContainer.setWidth(400);

            Paragraph paragraph1 = new Paragraph("first flex child which wraps");
            paragraph1.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            paragraph1.setMargin(10);
            flexContainer.add(paragraph1);

            Paragraph paragraph2 = new Paragraph("second flex child which wraps");
            paragraph2.setBackgroundColor(ColorConstants.GRAY);
            paragraph2.setMargin(10);
            flexContainer.add(paragraph2);

            Div flexParent = new Div();
            flexParent.setHeight(400);
            flexParent.add(flexContainer);

            document.add(flexParent);
        }

        // Since we skip percentage height, the one we'll get is 400, which is enough to fix entire line.
        // But in reality the height is 200, which forces the line to wrap. In result, children occupied area is not wide enough.
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    private static Div createFlexContainer() {
        Div flexContainer = new Div();
        flexContainer.setNextRenderer(new FlexContainerRenderer(flexContainer));
        flexContainer.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        flexContainer.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        flexContainer.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

        return flexContainer;
    }
}
