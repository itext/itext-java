/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.layout.element.gridcontainer;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.GridContainer;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.BackgroundImage;
import com.itextpdf.layout.properties.BackgroundRepeat;
import com.itextpdf.layout.properties.BackgroundRepeat.BackgroundRepeatValue;
import com.itextpdf.layout.properties.BoxSizingPropertyValue;
import com.itextpdf.layout.properties.GridValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GridContainerLayoutTest extends ExtendedITextTest {

    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/GridContainerTest/";

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/GridContainerTest/";

    @Before
    public void setup() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void simpleBorderBoxSizingTestTest() throws IOException, InterruptedException {
        String fileName = DESTINATION_FOLDER + "border.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fileName));
        Document document = new Document(pdfDocument);

        GridContainer gridcontainer0 = createGridBoxWithText();
        document.add(new Paragraph("BOX_SIZING: BORDER_BOX"));
        gridcontainer0.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        gridcontainer0.setBorder(new SolidBorder(ColorConstants.BLACK, 20));
        document.add(gridcontainer0);

        document.add(new Paragraph("BOX_SIZING: CONTENT_BOX"));
        GridContainer gridcontainer1 = createGridBoxWithText();
        gridcontainer1.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.CONTENT_BOX);
        gridcontainer1.setBorder(new SolidBorder(ColorConstants.BLACK, 20));

        document.add(gridcontainer1);

        document.close();
        Assert.assertNull(new CompareTool().compareByContent(fileName, SOURCE_FOLDER + "cmp_border.pdf",
                DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void simpleMarginTest() throws IOException, InterruptedException {
        String fileName = DESTINATION_FOLDER + "margin.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fileName));
        Document document = new Document(pdfDocument);
        document.add(new Paragraph("Margin "));
        GridContainer gridcontainer0 = createGridBoxWithText();
        gridcontainer0.setMarginTop(50);
        gridcontainer0.setMarginBottom(100);
        gridcontainer0.setMarginLeft(10);
        gridcontainer0.setMarginRight(10);
        document.add(gridcontainer0);
        document.add(new Paragraph("Margin "));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, SOURCE_FOLDER + "cmp_margin.pdf",
                DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void simplePaddingTest() throws IOException, InterruptedException {
        String fileName = DESTINATION_FOLDER + "padding.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fileName));
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Padding "));
        GridContainer gridcontainer0 = createGridBoxWithText();
        gridcontainer0.setPaddingTop(50);
        gridcontainer0.setPaddingBottom(100);
        gridcontainer0.setPaddingLeft(10);
        gridcontainer0.setPaddingRight(10);
        document.add(gridcontainer0);
        document.add(new Paragraph("Padding "));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, SOURCE_FOLDER + "cmp_padding.pdf",
                DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void simpleBackGroundTest() throws IOException, InterruptedException {
        String fileName = DESTINATION_FOLDER + "background.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fileName));
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Background "));
        GridContainer gridcontainer0 = createGridBoxWithText();
        gridcontainer0.setBackgroundColor(ColorConstants.RED);
        document.add(gridcontainer0);
        document.add(new Paragraph("Background "));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, SOURCE_FOLDER + "cmp_background.pdf",
                DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void backgroundWithImageTest() throws IOException, InterruptedException {
        String fileName = DESTINATION_FOLDER + "backgroundWithImage.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fileName));
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Background with image "));
        GridContainer gridcontainer0 = createGridBoxWithText();

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.create(SOURCE_FOLDER + "rock_texture.jpg"))
                .put(PdfName.BBox, new PdfArray(new Rectangle(70, -15, 500, 750)));
        BackgroundImage image = new BackgroundImage.Builder().setImage(xObject)
                .setBackgroundRepeat(new BackgroundRepeat(
                        BackgroundRepeatValue.REPEAT)).build();

        gridcontainer0.setBackgroundImage(image);
        document.add(gridcontainer0);
        document.add(new Paragraph("Background with image "));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, SOURCE_FOLDER + "cmp_backgroundWithImage.pdf",
                DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void emptyGridContainerTest() throws IOException, InterruptedException {
        String fileName = DESTINATION_FOLDER + "emptyGridContainer.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(fileName));
        Document document = new Document(pdfDocument);

        GridContainer gridcontainer0 = new GridContainer();

        gridcontainer0.setProperty(Property.COLUMN_GAP_BORDER, null);
        gridcontainer0.setBackgroundColor(ColorConstants.RED);
        gridcontainer0.setProperty(Property.GRID_TEMPLATE_COLUMNS,
                Arrays.asList(
                        GridValue.createUnitValue(new UnitValue(1, 150.0f)),
                        GridValue.createUnitValue(new UnitValue(1, 150.0f)),
                        GridValue.createUnitValue(new UnitValue(1, 150.0f))));
        gridcontainer0.setProperty(Property.COLUMN_GAP, 12.0f);
        document.add(gridcontainer0);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(fileName, SOURCE_FOLDER + "cmp_emptyGridContainer.pdf",
                DESTINATION_FOLDER, "diff"));
    }


    private GridContainer createGridBoxWithSizedDiv() {
        GridContainer gridcontainer0 = new GridContainer();

        gridcontainer0.setProperty(Property.COLUMN_GAP_BORDER, null);
        gridcontainer0.setProperty(Property.GRID_TEMPLATE_COLUMNS,
                Arrays.asList(new UnitValue(1, 150.0f), new UnitValue(1, 150.0f), new UnitValue(1, 150.0f)));
        gridcontainer0.setProperty(Property.COLUMN_GAP, 12.0f);
        gridcontainer0.setBackgroundColor(ColorConstants.RED);
        for (int i = 0; i < 4; i++) {
            Div div1 = new Div();
            div1.setBackgroundColor(ColorConstants.YELLOW);
            div1.setHeight(20);
            div1.setWidth(30);
            div1.setProperty(Property.COLUMN_GAP_BORDER, null);
            div1.setProperty(Property.COLUMN_GAP, 12.0f);
            gridcontainer0.add(div1);
        }
        return gridcontainer0;

    }

    private GridContainer createGridBoxWithText() {
        GridContainer gridcontainer0 = new GridContainer();

        gridcontainer0.setProperty(Property.COLUMN_GAP_BORDER, null);
        gridcontainer0.setProperty(Property.GRID_TEMPLATE_COLUMNS,
                Arrays.asList(GridValue.createUnitValue(new UnitValue(1, 150.0f)),
                        GridValue.createUnitValue(new UnitValue(1, 150.0f)),
                        GridValue.createUnitValue(new UnitValue(1, 150.0f))));
        gridcontainer0.setProperty(Property.COLUMN_GAP, 12.0f);
        Div div1 = new Div();
        div1.setBackgroundColor(ColorConstants.YELLOW);
        div1.setProperty(Property.COLUMN_GAP_BORDER, null);
        div1.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph2 = new Paragraph();
        Text text3 = new Text("One");
        paragraph2.add(text3);

        div1.add(paragraph2);

        gridcontainer0.add(div1);

        Div div4 = new Div();
        div4.setProperty(Property.COLUMN_GAP_BORDER, null);
        div4.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph5 = new Paragraph();
        Text text6 = new Text("Two");
        paragraph5.add(text6);

        div4.add(paragraph5);

        gridcontainer0.add(div4);

        Div div7 = new Div();
        div7.setBackgroundColor(ColorConstants.GREEN);
        div7.setProperty(Property.COLUMN_GAP_BORDER, null);
        div7.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph8 = new Paragraph();
        Text text9 = new Text("Three");
        paragraph8.add(text9);

        div7.add(paragraph8);

        gridcontainer0.add(div7);

        Div div10 = new Div();
        div10.setBackgroundColor(ColorConstants.CYAN);
        div10.setProperty(Property.COLUMN_GAP_BORDER, null);
        div10.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph11 = new Paragraph();
        Text text12 = new Text("Four");
        paragraph11.add(text12);

        div10.add(paragraph11);

        gridcontainer0.add(div10);

        Div div13 = new Div();

        div13.setProperty(Property.COLUMN_GAP_BORDER, null);
        div13.setProperty(Property.COLUMN_GAP, 12.0f);
        Paragraph paragraph14 = new Paragraph();
        Text text15 = new Text("Five");
        paragraph14.add(text15);

        div13.add(paragraph14);

        gridcontainer0.add(div13);
        return gridcontainer0;
    }


}
