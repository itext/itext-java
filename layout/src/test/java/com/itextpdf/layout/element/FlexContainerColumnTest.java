/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.layout.element;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.*;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@Category(IntegrationTest.class)
public class FlexContainerColumnTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/FlexContainerColumnTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/FlexContainerColumnTest/";

    private AlignmentPropertyValue alignItemsValue;
    private JustifyContent justifyContentValue;
    private FlexWrapPropertyValue wrapValue;
    private FlexDirectionPropertyValue directionValue;
    private Integer comparisonPdfId;

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    public FlexContainerColumnTest(Object alignItemsValue, Object justifyContentValue, Object wrapValue,
                             Object directionValue, Object comparisonPdfId) {
        this.alignItemsValue = (AlignmentPropertyValue) alignItemsValue;
        this.justifyContentValue = (JustifyContent) justifyContentValue;
        this.wrapValue = (FlexWrapPropertyValue) wrapValue;
        this.directionValue = (FlexDirectionPropertyValue) directionValue;
        this.comparisonPdfId = (Integer) comparisonPdfId;
    }

    @Parameterized.Parameters(name = "{index}: align-items: {0}; justify-content: {1}; flex-wrap: {2}; flex-direction: {3}")
    public static Iterable<Object[]> alignItemsAndJustifyContentProperties() {
        return Arrays.asList(new Object[][]{
                {AlignmentPropertyValue.FLEX_END, JustifyContent.FLEX_END, FlexWrapPropertyValue.NOWRAP,
                        FlexDirectionPropertyValue.COLUMN, 1},
                {AlignmentPropertyValue.CENTER, JustifyContent.CENTER, FlexWrapPropertyValue.NOWRAP,
                        FlexDirectionPropertyValue.COLUMN_REVERSE, 2},
                {AlignmentPropertyValue.STRETCH, JustifyContent.CENTER, FlexWrapPropertyValue.NOWRAP,
                        FlexDirectionPropertyValue.COLUMN, 3},
                {AlignmentPropertyValue.FLEX_START, JustifyContent.FLEX_START, FlexWrapPropertyValue.WRAP,
                        FlexDirectionPropertyValue.COLUMN_REVERSE, 4},
                {AlignmentPropertyValue.CENTER, JustifyContent.CENTER, FlexWrapPropertyValue.WRAP,
                        FlexDirectionPropertyValue.COLUMN, 5},
                {AlignmentPropertyValue.FLEX_END, JustifyContent.FLEX_END, FlexWrapPropertyValue.WRAP_REVERSE,
                        FlexDirectionPropertyValue.COLUMN, 6},
                {AlignmentPropertyValue.CENTER, JustifyContent.CENTER, FlexWrapPropertyValue.WRAP_REVERSE,
                        FlexDirectionPropertyValue.COLUMN_REVERSE, 7}
        });
    }

    @Test
    public void defaultFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "defaultFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_defaultFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(50));
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(40));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerDiv = new Div();
        innerDiv.add(createNewDiv()).add(createNewDiv());
        innerDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        Div blueDiv = createNewDiv();
        blueDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.BLUE));
        flexContainer.add(blueDiv).add(createNewDiv()).add(innerDiv).add(createNewDiv()).add(createNewDiv());

        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexContainerFixedHeightWidthTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerFixedHeightWidthTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerFixedHeightWidthTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(50));
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(40));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.WIDTH, UnitValue.createPointValue(450));
        flexContainer.setProperty(Property.HEIGHT, UnitValue.createPointValue(500));

        Div innerDiv = new Div();
        innerDiv.add(createNewDiv().setMarginLeft(20)).add(createNewDiv());
        innerDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        Div blueDiv = createNewDiv();
        blueDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.BLUE));
        flexContainer.add(blueDiv.setMarginLeft(100)).add(createNewDiv()).add(innerDiv).add(createNewDiv()).add(createNewDiv());

        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerDifferentChildrenTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerDifferentChildrenTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerDiv = new Div();
        innerDiv.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        innerDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));

        Table table = new Table(UnitValue.createPercentArray(new float[] {10, 10, 10}));
        for (int i = 0; i < 3; i++) {
            table.addCell("Hello");
        }

        List romanList = new List(ListNumberingType.ROMAN_LOWER).setSymbolIndent(20).
                setMarginLeft(25).
                add("One").add("Two").add("Three");
        romanList.setProperty(Property.BACKGROUND, new Background(ColorConstants.MAGENTA));

        flexContainer.add(table).add(new Paragraph("Test")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(SOURCE_FOLDER + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA), ignore = true)
    public void flexContainerDifferentChildrenDontFitVerticallyTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerDifferentChildrenDontFitHorizontallyTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerDifferentChildrenDontFitHorizontallyTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.HEIGHT, UnitValue.createPointValue(300));

        Div innerDiv = new Div();
        innerDiv.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        innerDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));

        Table table = new Table(UnitValue.createPercentArray(new float[] {10, 10, 10}));
        for (int i = 0; i < 3; i++) {
            table.addCell("Hello");
        }

        List romanList = new List(ListNumberingType.ROMAN_LOWER).setSymbolIndent(20).
                setMarginLeft(25).
                add("One").add("Two").add("Three");

        flexContainer.add(table).add(new Paragraph("Test")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(SOURCE_FOLDER + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenWithGrowTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerDifferentChildrenWithGrowTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerDifferentChildrenWithGrowTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerDiv = new Div();
        innerDiv.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        innerDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerDiv.setProperty(Property.FLEX_GROW, 1f);

        Table table = new Table(UnitValue.createPercentArray(new float[] {50, 50}));
        for (int i = 0; i < 2; i++) {
            table.addCell("Hello");
        }
        table.setProperty(Property.FLEX_GROW, 1f);

        List romanList = new List(ListNumberingType.ROMAN_LOWER).setSymbolIndent(20).
                setMarginLeft(25).
                add("One").add("Two").add("Three");
        romanList.setProperty(Property.FLEX_GROW, 1f);
        romanList.setProperty(Property.BACKGROUND, new Background(ColorConstants.MAGENTA));

        Image img = new Image(ImageDataFactory.create(SOURCE_FOLDER + "img.jpg"));
        img.setProperty(Property.FLEX_GROW, 1f);

        flexContainer.add(table).add(innerDiv).add(romanList).add(img);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenWithFlexBasisTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerDifferentChildrenWithFlexBasisTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerDifferentChildrenWithFlexBasisTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Table table = new Table(UnitValue.createPercentArray(new float[] {50, 50}));
        for (int i = 0; i < 2; i++) {
            table.addCell("Hello");
        }
        table.setProperty(Property.FLEX_BASIS, UnitValue.createPointValue(150));

        List romanList = new List(ListNumberingType.ROMAN_LOWER).setSymbolIndent(20).
                setMarginLeft(25).
                add("One").add("Two").add("Three");
        romanList.setProperty(Property.BACKGROUND, new Background(ColorConstants.MAGENTA));
        romanList.setProperty(Property.FLEX_BASIS, UnitValue.createPointValue(100));

        Image img = new Image(ImageDataFactory.create(SOURCE_FOLDER + "img.jpg"));
        img.setProperty(Property.FLEX_BASIS, UnitValue.createPointValue(150));

        flexContainer.add(table).add(romanList).add(img);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenWithFlexShrinkTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerDifferentChildrenWithFlexShrinkTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerDifferentChildrenWithFlexShrinkTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setHeight(450);
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Table table = new Table(UnitValue.createPercentArray(new float[] {50, 50}));
        for (int i = 0; i < 2; i++) {
            table.addCell("Hello");
        }
        table.setProperty(Property.FLEX_BASIS, UnitValue.createPointValue(200));
        table.setProperty(Property.FLEX_SHRINK, 0f);

        List romanList = new List(ListNumberingType.ROMAN_LOWER).setSymbolIndent(20).
                setMarginLeft(25).
                add("One").add("Two").add("Three");
        romanList.setProperty(Property.BACKGROUND, new Background(ColorConstants.MAGENTA));
        romanList.setProperty(Property.FLEX_BASIS, UnitValue.createPointValue(200));
        romanList.setProperty(Property.FLEX_SHRINK, 0f);

        Div div = new Div().add(new Paragraph("Test"));
        div.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        div.setProperty(Property.FLEX_BASIS, UnitValue.createPointValue(200));

        flexContainer.add(table).add(romanList).add(div);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexContainerInsideFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex = new FlexContainer();
        innerFlex.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        innerFlex.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.2f);
        }
        innerFlex.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerFlex.setProperty(Property.FLEX_GROW, 0.7f);

        flexContainer.add(innerFlex).add(createNewDiv());
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void multipleFlexContainersInsideFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "multipleFlexContainersInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_multipleFlexContainersInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex1 = new FlexContainer();
        innerFlex1.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        innerFlex1.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex1.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.2f);
        }
        innerFlex1.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerFlex1.setProperty(Property.FLEX_GROW, 1f);

        Div innerFlex2 = new FlexContainer();
        innerFlex2.setProperty(Property.FLEX_DIRECTION, FlexDirectionPropertyValue.COLUMN);
        innerFlex2.add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex2.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.3f);
        }
        innerFlex2.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        innerFlex2.setProperty(Property.FLEX_GROW, 2f);

        flexContainer.add(innerFlex1).add(innerFlex2);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexContainerRotationAngleTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerRotationAngleTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerRotationAngleTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.ROTATION_ANGLE, 20f);

        Table table = new Table(UnitValue.createPercentArray(new float[] {10, 10, 10}));
        for (int i = 0; i < 3; i++) {
            table.addCell("Hello");
        }

        List romanList = new List(ListNumberingType.ROMAN_LOWER).setSymbolIndent(20).
                setMarginLeft(25).
                add("One").add("Two").add("Three");
        romanList.setProperty(Property.BACKGROUND, new Background(ColorConstants.MAGENTA));

        flexContainer.add(table).add(new Paragraph("Test")).add(romanList).add(new Image(ImageDataFactory.create(SOURCE_FOLDER + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexItemBoxSizingTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexItemBoxSizingTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexItemBoxSizingTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLUE, 30));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setWidth(250);
        flexContainer.setHeight(400);

        Div innerDiv = new Div();
        innerDiv.setWidth(120);
        innerDiv.setHeight(120);
        innerDiv.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        innerDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerDiv.setBorder(new SolidBorder(ColorConstants.RED, 20));
        innerDiv.setProperty(Property.FLEX_GROW, 0.3F);

        Div innerDiv2 = new Div();
        innerDiv2.setProperty(Property.FLEX_BASIS, UnitValue.createPointValue(120));
        innerDiv2.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        innerDiv2.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerDiv2.setBorder(new SolidBorder(ColorConstants.RED, 20));
        innerDiv2.setProperty(Property.FLEX_GROW, 0.3F);

        Div innerDiv3 = new Div();
        innerDiv3.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        innerDiv3.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerDiv3.setBorder(new SolidBorder(ColorConstants.RED, 20));

        Div innerDivChild =
                new Div().setBorder(new SolidBorder(ColorConstants.ORANGE, 10)).setBackgroundColor(ColorConstants.PINK).setWidth(50).setHeight(50);
        innerDivChild.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);
        innerDiv.add(innerDivChild);
        innerDiv2.add(innerDivChild);
        innerDiv3.add(innerDivChild);

        flexContainer.add(innerDiv).add(innerDiv2).add(innerDiv3);
        document.add(flexContainer);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void flexContainerBoxSizingTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "flexContainerBoxSizingTest" + comparisonPdfId + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_flexContainerBoxSizingTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLUE, 30));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setWidth(450);
        flexContainer.setProperty(Property.BOX_SIZING, BoxSizingPropertyValue.BORDER_BOX);

        Div innerDiv = new Div();
        innerDiv.setWidth(120);
        Div innerDivChild =
                new Div().setBorder(new SolidBorder(ColorConstants.ORANGE, 10)).setBackgroundColor(ColorConstants.PINK).setWidth(100).setHeight(100);
        innerDiv.add(innerDivChild);
        innerDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerDiv.setBorder(new SolidBorder(ColorConstants.RED, 20));

        flexContainer.add(innerDiv).add(createNewDiv());
        document.add(flexContainer);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    private FlexContainer createFlexContainer() {
        FlexContainer flexContainer = new FlexContainer();
        flexContainer.setProperty(Property.ALIGN_ITEMS, alignItemsValue);
        flexContainer.setProperty(Property.JUSTIFY_CONTENT, justifyContentValue);
        flexContainer.setProperty(Property.FLEX_WRAP, wrapValue);
        flexContainer.setProperty(Property.FLEX_DIRECTION, directionValue);
        if (FlexWrapPropertyValue.NOWRAP != wrapValue) {
            flexContainer.setHeight(300);
        }
        return flexContainer;
    }

    private static Div createNewDiv() {
        Div newDiv = new Div();
        newDiv.setProperty(Property.BORDER, new SolidBorder(1));
        newDiv.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
        newDiv.setProperty(Property.HEIGHT, UnitValue.createPointValue(100));
        return newDiv;
    }
}
