/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.pdfua;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.GridContainer;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.grid.PointValue;
import com.itextpdf.layout.properties.grid.TemplateValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("IntegrationTest")
public class PdfUATaggedGridContainerTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUATaggedGridContainerTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";

    private UaValidationTestFramework framework;

    @BeforeAll
    public static void setup() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void initializeFramework() {
        framework = new UaValidationTestFramework(DESTINATION_FOLDER);
    }

    public static List<PdfUAConformance> data() {
        return UaValidationTestFramework.getConformanceList();
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simpleBorderBoxSizingTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font = loadFont();

            document.setFont(font);

            GridContainer gridContainer0 = createGridBoxWithText();
            document.add(new Paragraph("BOX_SIZING: BORDER_BOX"));
            gridContainer0.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
            gridContainer0.setBorder(new SolidBorder(ColorConstants.BLACK, 20));
            document.add(gridContainer0);

            document.add(new Paragraph("BOX_SIZING: CONTENT_BOX"));
            GridContainer gridContainer1 = createGridBoxWithText();
            gridContainer1.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
            gridContainer1.setBorder(new SolidBorder(ColorConstants.BLACK, 20));

            document.add(gridContainer1);
        });
        framework.assertBothValid("border", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simpleMarginTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font = loadFont();
            document.setFont(font);

            document.add(new Paragraph("Validate Grid Container with Margin "));
            GridContainer gridContainer0 = createGridBoxWithText();
            gridContainer0.setMarginTop(50);
            gridContainer0.setMarginBottom(100);
            gridContainer0.setMarginLeft(10);
            gridContainer0.setMarginRight(10);
            document.add(gridContainer0);
        });
        framework.assertBothValid("margin", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simplePaddingTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font = loadFont();
            document.setFont(font);

            document.add(new Paragraph("Validate Grid Container with Padding"));
            GridContainer gridContainer0 = createGridBoxWithText();
            gridContainer0.setPaddingTop(50);
            gridContainer0.setPaddingBottom(100);
            gridContainer0.setPaddingLeft(10);
            gridContainer0.setPaddingRight(10);
            document.add(gridContainer0);
        });
        framework.assertBothValid("padding", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void simpleBackgroundTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            PdfFont font = loadFont();
            document.setFont(font);

            document.add(new Paragraph("Validate Grid Container with Background"));
            GridContainer gridContainer0 = createGridBoxWithText();
            gridContainer0.setBackgroundColor(ColorConstants.RED);
            document.add(gridContainer0);
        });
        framework.assertBothValid("background", pdfUAConformance);
    }

    @ParameterizedTest
    @MethodSource("data")
    public void emptyGridContainerTest(PdfUAConformance pdfUAConformance) throws IOException {
        framework.addBeforeGenerationHook(pdfDoc -> {
            Document document = new Document(pdfDoc);
            GridContainer gridContainer0 = new GridContainer();

            gridContainer0.setProperty(Property.COLUMN_GAP_BORDER, null);
            gridContainer0.setBackgroundColor(ColorConstants.RED);
            gridContainer0.setProperty(Property.GRID_TEMPLATE_COLUMNS,
                    Arrays.asList(
                            (TemplateValue) new PointValue(150.0f),
                            (TemplateValue) new PointValue(150.0f),
                            (TemplateValue) new PointValue(150.0f)));
            gridContainer0.setProperty(Property.COLUMN_GAP, 12.0f);
            document.add(gridContainer0);
        });
        framework.assertBothValid("emptyGridContainer", pdfUAConformance);
    }


    private GridContainer createGridBoxWithText() {
        GridContainer gridContainer0 = new GridContainer();

        gridContainer0.setProperty(Property.COLUMN_GAP_BORDER, null);
        gridContainer0.setProperty(Property.GRID_TEMPLATE_COLUMNS,
                Arrays.asList(
                        (TemplateValue) new PointValue(150.0f),
                        (TemplateValue) new PointValue(150.0f),
                        (TemplateValue) new PointValue(150.0f)));
        gridContainer0.setProperty(Property.COLUMN_GAP, 12.0f);
        Div div1 = new Div();
        div1.setBackgroundColor(ColorConstants.YELLOW);
        div1.setProperty(Property.COLUMN_GAP_BORDER, null);
        div1.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph2 = new Paragraph();
        Text text3 = new Text("One");
        paragraph2.add(text3);

        div1.add(paragraph2);

        gridContainer0.add(div1);

        Div div4 = new Div();
        div4.setProperty(Property.COLUMN_GAP_BORDER, null);
        div4.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph5 = new Paragraph();
        Text text6 = new Text("Two");
        paragraph5.add(text6);

        div4.add(paragraph5);

        gridContainer0.add(div4);

        Div div7 = new Div();
        div7.setBackgroundColor(ColorConstants.GREEN);
        div7.setProperty(Property.COLUMN_GAP_BORDER, null);
        div7.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph8 = new Paragraph();
        Text text9 = new Text("Three");
        paragraph8.add(text9);

        div7.add(paragraph8);

        gridContainer0.add(div7);

        Div div10 = new Div();
        div10.setBackgroundColor(ColorConstants.CYAN);
        div10.setProperty(Property.COLUMN_GAP_BORDER, null);
        div10.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph11 = new Paragraph();
        Text text12 = new Text("Four");
        paragraph11.add(text12);

        div10.add(paragraph11);

        gridContainer0.add(div10);

        Div div13 = new Div();

        div13.setProperty(Property.COLUMN_GAP_BORDER, null);
        div13.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph14 = new Paragraph();
        Text text15 = new Text("Five");
        paragraph14.add(text15);

        div13.add(paragraph14);

        gridContainer0.add(div13);
        return gridContainer0;
    }

    private static PdfFont loadFont(){
        try {
            return PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
