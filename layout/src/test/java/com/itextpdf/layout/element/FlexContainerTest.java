/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.property.AlignmentPropertyValue;
import com.itextpdf.layout.property.Background;
import com.itextpdf.layout.property.BoxSizingPropertyValue;
import com.itextpdf.layout.property.JustifyContent;
import com.itextpdf.layout.property.ListNumberingType;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
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
import java.io.IOException;
import java.util.Arrays;

@RunWith(Parameterized.class)
@Category(IntegrationTest.class)
public class FlexContainerTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FlexContainerTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FlexContainerTest/";

    private AlignmentPropertyValue alignItemsValue;
    private JustifyContent justifyContentValue;
    private Integer comparisonPdfId;

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    public FlexContainerTest(Object alignItemsValue, Object justifyContentValue, Object comparisonPdfId) {
        this.alignItemsValue = (AlignmentPropertyValue) alignItemsValue;
        this.justifyContentValue = (JustifyContent) justifyContentValue;
        this.comparisonPdfId = (Integer) comparisonPdfId;
    }

    @Parameterized.Parameters(name = "{index}: align-items: {0}; justify-content: {1}")
    public static Iterable<Object[]> alignItemsAndJustifyContentProperties() {
        return Arrays.asList(new Object[][]{
                {AlignmentPropertyValue.FLEX_START, JustifyContent.FLEX_START, 1},
                {AlignmentPropertyValue.FLEX_END, JustifyContent.FLEX_END, 2},
                {AlignmentPropertyValue.CENTER, JustifyContent.CENTER, 3},
                {AlignmentPropertyValue.STRETCH, JustifyContent.CENTER, 4}
        });
    }

    @Test
    public void defaultFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "defaultFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_defaultFlexContainerTest" + comparisonPdfId + ".pdf";
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
        flexContainer.add(createNewDiv()).add(createNewDiv()).add(innerDiv).add(createNewDiv()).add(createNewDiv());

        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerFixedHeightWidthTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerFixedHeightWidthTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerFixedHeightWidthTest" + comparisonPdfId + ".pdf";
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
        flexContainer.add(createNewDiv().setMarginLeft(100)).add(createNewDiv()).add(innerDiv).add(createNewDiv()).add(createNewDiv());

        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerDifferentChildrenTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerDifferentChildrenTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.ALIGN_ITEMS, alignItemsValue);
        flexContainer.setProperty(Property.JUSTIFY_CONTENT, justifyContentValue);
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.WIDTH, UnitValue.createPointValue(500));

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

        flexContainer.add(table).add(new Paragraph("Test")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(sourceFolder + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerHeightClippedTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerHeightClippedTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerHeightClippedTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.WIDTH, UnitValue.createPointValue(500));
        flexContainer.setHeight(250);

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

        flexContainer.add(table).add(new Paragraph("Test")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(sourceFolder + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA), ignore = true)
    // TODO DEVSIX-5042 HEIGHT property is ignored when FORCED_PLACEMENT is true
    public void flexContainerDifferentChildrenDontFitHorizontallyTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerDifferentChildrenDontFitHorizontallyTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerDifferentChildrenDontFitHorizontallyTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.WIDTH, UnitValue.createPointValue(300));

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

        flexContainer.add(table).add(new Paragraph("Test")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(sourceFolder + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    // TODO DEVSIX-5042 HEIGHT property is ignored when FORCED_PLACEMENT is true
    public void flexContainerDifferentChildrenDontFitHorizontallyForcedPlacementTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerDifferentChildrenDontFitHorizontallyForcedPlacementTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerDifferentChildrenDontFitHorizontallyForcedPlacementTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.FORCED_PLACEMENT, true);

        Div innerDiv = new Div();
        innerDiv.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        innerDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));

        Table table = new Table(UnitValue.createPercentArray(new float[] {25, 25, 25, 25}));
        for (int i = 0; i < 4; i++) {
            table.addCell("Hello");
        }

        List romanList = new List(ListNumberingType.ROMAN_LOWER).setSymbolIndent(20).
                setMarginLeft(25).
                add("MuchMoreText").add("MuchMoreText").add("MuchMoreText");

        flexContainer.add(table).add(new Paragraph("MuchMoreText")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(sourceFolder + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenDontFitVerticallyTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerDifferentChildrenDontFitVerticallyTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerDifferentChildrenDontFitVerticallyTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.WIDTH, UnitValue.createPointValue(500));
        flexContainer.setHeight(400);

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

        flexContainer.add(table).add(new Paragraph("Test")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(sourceFolder + "img.jpg")));
        Div prevDiv = new Div();
        prevDiv.setHeight(480);
        prevDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        document.add(prevDiv);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenFitContainerDoesNotFitVerticallyTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerDifferentChildrenFitContainerDoesNotFitVerticallyTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerDifferentChildrenFitContainerDoesNotFitVerticallyTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setHeight(600);

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

        flexContainer.add(table).add(new Paragraph("Test")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(sourceFolder + "img.jpg")));
        Div prevDiv = new Div();
        prevDiv.setHeight(400);
        prevDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        document.add(prevDiv);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenWithGrowTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerDifferentChildrenWithGrowTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerDifferentChildrenWithGrowTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setWidth(500);

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

        Image img = new Image(ImageDataFactory.create(sourceFolder + "img.jpg"));
        img.setProperty(Property.FLEX_GROW, 1f);

        flexContainer.add(table).add(innerDiv).add(romanList).add(img);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenWithFlexBasisTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerDifferentChildrenWithFlexBasisTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerDifferentChildrenWithFlexBasisTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setWidth(500);

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

        Image img = new Image(ImageDataFactory.create(sourceFolder + "img.jpg"));
        img.setProperty(Property.FLEX_BASIS, UnitValue.createPointValue(150));

        flexContainer.add(table).add(romanList).add(img);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerDifferentChildrenWithFlexShrinkTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerDifferentChildrenWithFlexShrinkTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerDifferentChildrenWithFlexShrinkTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setWidth(500);

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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerInsideFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex = new FlexContainer();
        innerFlex.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.2f);
        }
        innerFlex.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerFlex.setProperty(Property.FLEX_GROW, 0.7f);

        flexContainer.add(innerFlex).add(createNewDiv());
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerInsideFlexContainerWithHugeBordersTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerInsideFlexContainerWithHugeBordersTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerInsideFlexContainerWithHugeBordersTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLUE,20));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex = new FlexContainer();
        innerFlex.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 1f);
            children.setProperty(Property.BORDER, new SolidBorder(ColorConstants.YELLOW,10));
        }
        innerFlex.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        innerFlex.setProperty(Property.FLEX_GROW, 1f);
        innerFlex.setProperty(Property.BORDER, new SolidBorder(ColorConstants.RED,15));

        flexContainer.add(innerFlex).add(createNewDiv().setBorder(new SolidBorder(ColorConstants.GREEN,10)));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void multipleFlexContainersInsideFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleFlexContainersInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_multipleFlexContainersInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex1 = new FlexContainer();
        innerFlex1.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex1.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.2f);
        }
        innerFlex1.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerFlex1.setProperty(Property.FLEX_GROW, 1f);

        Div innerFlex2 = new FlexContainer();
        innerFlex2.add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex2.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.3f);
        }
        innerFlex2.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        innerFlex2.setProperty(Property.FLEX_GROW, 2f);

        flexContainer.add(innerFlex1).add(innerFlex2);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void multipleFlexContainersWithPredefinedPointWidthsInsideFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleFlexContainersWithPredefinedPointWidthsInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_multipleFlexContainersWithPredefinedPointWidthsInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex1 = new FlexContainer();
        innerFlex1.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex1.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.2f);
        }
        innerFlex1.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerFlex1.setProperty(Property.FLEX_GROW, 1f);
        innerFlex1.setWidth(380);

        Div innerFlex2 = new FlexContainer();
        innerFlex2.add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex2.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.3f);
        }
        innerFlex2.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        innerFlex2.setProperty(Property.FLEX_GROW, 2f);
        innerFlex2.setWidth(200);

        flexContainer.add(innerFlex1).add(innerFlex2);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void multipleFlexContainersWithPredefinedPercentWidthsInsideFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleFlexContainersWithPredefinedPercentWidthsInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_multipleFlexContainersWithPredefinedPercentWidthsInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex1 = new FlexContainer();
        innerFlex1.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex1.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.2f);
        }
        innerFlex1.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerFlex1.setProperty(Property.FLEX_GROW, 1f);
        innerFlex1.setWidth(UnitValue.createPercentValue(40));

        Div innerFlex2 = new FlexContainer();
        innerFlex2.add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex2.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.3f);
        }
        innerFlex2.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        innerFlex2.setProperty(Property.FLEX_GROW, 2f);
        innerFlex2.setWidth(UnitValue.createPercentValue(40));

        flexContainer.add(innerFlex1).add(innerFlex2);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    // TODO DEVSIX-5087 Content should not overflow container by default
    public void multipleFlexContainersWithPredefinedMinWidthsInsideFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleFlexContainersWithPredefinedMinWidthsInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_multipleFlexContainersWithPredefinedMinWidthsInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex1 = new FlexContainer();
        innerFlex1.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex1.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.2f);
        }
        innerFlex1.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerFlex1.setProperty(Property.FLEX_GROW, 1f);
        innerFlex1.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(380));

        Div innerFlex2 = new FlexContainer();
        innerFlex2.add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex2.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.3f);
        }
        innerFlex2.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        innerFlex2.setProperty(Property.FLEX_GROW, 2f);
        innerFlex2.setProperty(Property.MIN_WIDTH, UnitValue.createPointValue(200));

        flexContainer.add(innerFlex1).add(innerFlex2);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void multipleFlexContainersWithPredefinedMaxWidthsInsideFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleFlexContainersWithPredefinedMaxWidthsInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_multipleFlexContainersWithPredefinedMaxWidthsInsideFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div innerFlex1 = new FlexContainer();
        innerFlex1.add(createNewDiv()).add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex1.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.2f);
        }
        innerFlex1.setProperty(Property.BACKGROUND, new Background(ColorConstants.GREEN));
        innerFlex1.setProperty(Property.FLEX_GROW, 1f);
        innerFlex1.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(200));

        Div innerFlex2 = new FlexContainer();
        innerFlex2.add(createNewDiv()).add(createNewDiv());
        for (IElement children : innerFlex2.getChildren()) {
            children.setProperty(Property.FLEX_GROW, 0.3f);
        }
        innerFlex2.setProperty(Property.BACKGROUND, new Background(ColorConstants.RED));
        innerFlex2.setProperty(Property.FLEX_GROW, 2f);
        innerFlex2.setProperty(Property.MAX_WIDTH, UnitValue.createPointValue(200));

        flexContainer.add(innerFlex1).add(innerFlex2);
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerFillAvailableAreaTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerFillAvailableAreaTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerFillAvailableAreaTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.FILL_AVAILABLE_AREA, true);

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

        flexContainer.add(table).add(new Paragraph("Test")).add(innerDiv).add(romanList).add(new Image(ImageDataFactory.create(sourceFolder + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerRotationAngleTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerRotationAngleTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerRotationAngleTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setProperty(Property.WIDTH, UnitValue.createPointValue(400));
        flexContainer.setProperty(Property.ROTATION_ANGLE, 20f);

        Table table = new Table(UnitValue.createPercentArray(new float[] {10, 10, 10}));
        for (int i = 0; i < 3; i++) {
            table.addCell("Hello");
        }

        List romanList = new List(ListNumberingType.ROMAN_LOWER).setSymbolIndent(20).
                setMarginLeft(25).
                add("One").add("Two").add("Three");
        romanList.setProperty(Property.BACKGROUND, new Background(ColorConstants.MAGENTA));

        flexContainer.add(table).add(new Paragraph("Test")).add(romanList).add(new Image(ImageDataFactory.create(sourceFolder + "img.jpg")));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    // TODO DEVSIX-5174 content should overflow bottom
    public void respectFlexContainersHeightTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "respectFlexContainersHeightTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_respectFlexContainersHeightTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        Style containerStyle = new Style()
                .setWidth(60)
                .setHeight(50);

        Div flexContainer = getFlexContainer(null, containerStyle);
        Div flexItem = new Div()
                .setBackgroundColor(ColorConstants.BLUE)
                .add(new Paragraph("h"))
                .add(new Paragraph("e"))
                .add(new Paragraph("l"))
                .add(new Paragraph("l"))
                .add(new Paragraph("o"))
                .add(new Paragraph("w"))
                .add(new Paragraph("o"))
                .add(new Paragraph("r"))
                .add(new Paragraph("l"))
                .add(new Paragraph("d"));
        flexContainer.add(flexItem);
        flexContainer.add(new Div().setBackgroundColor(ColorConstants.YELLOW).setWidth(10).setHeight(200));

        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void respectFlexContainersWidthTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "respectFlexContainersWidthTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_respectFlexContainersWidthTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        // default (overflow fit)
        OverflowPropertyValue overflowX = null;
        Style containerStyle = new Style()
                .setWidth(60)
                .setHeight(200);

        Style itemStyle = new Style()
                .setWidth(60f)
                .setHeight(100f);

        Div flexContainer = getFlexContainer(overflowX, containerStyle);
        flexContainer
                .add(getFlexItem(overflowX, itemStyle))
                .add(getFlexItem(overflowX, itemStyle));
        document.add(flexContainer);

        document.add(new AreaBreak());

        // default (overflow visible)
        overflowX = OverflowPropertyValue.VISIBLE;
        flexContainer = getFlexContainer(overflowX, containerStyle);
        flexContainer
                .add(getFlexItem(overflowX, itemStyle))
                .add(getFlexItem(overflowX, itemStyle));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexItemsMinHeightShouldBeOverriddenTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexItemsMinHeightShouldBeOverriddenTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexItemsMinHeightShouldBeOverriddenTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();

        flexContainer.add(new Div().setWidth(100).setBackgroundColor(ColorConstants.BLUE).setHeight(100));
        flexContainer.add(new Div().setWidth(100).setBackgroundColor(ColorConstants.YELLOW).setMinHeight(20));
        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void linesMinHeightShouldBeRespectedTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linesMinHeightShouldBeRespectedTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_linesMinHeightShouldBeRespectedTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setMinHeight(100);

        Div child = new Div().setWidth(100).setBackgroundColor(ColorConstants.BLUE);
        child.add(new Paragraph().setWidth(100).setBackgroundColor(ColorConstants.YELLOW));
        flexContainer.add(child);

        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void linesMaxHeightShouldBeRespectedTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "linesMaxHeightShouldBeRespectedTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_linesMaxHeightShouldBeRespectedTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setMaxHeight(100);

        Div child = new Div().setWidth(100).setBackgroundColor(ColorConstants.BLUE).setHeight(150);
        child.add(new Paragraph().setWidth(100).setBackgroundColor(ColorConstants.YELLOW));
        flexContainer.add(child);

        document.add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void collapsingMarginsFlexContainerTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsFlexContainerTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsFlexContainerTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.setProperty(Property.COLLAPSING_MARGINS, true);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(50));
        flexContainer.setProperty(Property.BORDER, new SolidBorder(2));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));

        Div child1 = createNewDiv();
        child1.setBackgroundColor(ColorConstants.CYAN);
        child1.setMargin(50);

        Div child2 = createNewDiv();
        child2.setBackgroundColor(ColorConstants.CYAN);
        child2.setMargin(50);

        flexContainer.add(child1).add(child2);

        Div flexContainersSibling = createNewDiv();
        flexContainersSibling.setMarginBottom(40);

        document.add(flexContainersSibling).add(flexContainer);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexItemBoxSizingTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexItemBoxSizingTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexItemBoxSizingTest" + comparisonPdfId + ".pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Div flexContainer = createFlexContainer();
        flexContainer.setProperty(Property.BORDER, new SolidBorder(ColorConstants.BLUE, 30));
        flexContainer.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        flexContainer.setWidth(450);
        flexContainer.setHeight(200);

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

        Div divToCompare = new Div().setWidth(450).setHeight(100).setBackgroundColor(ColorConstants.MAGENTA).setMarginTop(50);

        flexContainer.add(innerDiv).add(innerDiv2).add(innerDiv3);
        document.add(flexContainer).add(divToCompare);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void flexContainerBoxSizingTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "flexContainerBoxSizingTest" + comparisonPdfId + ".pdf";
        String cmpFileName = sourceFolder + "cmp_flexContainerBoxSizingTest" + comparisonPdfId + ".pdf";
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

        Div divToCompare = new Div().setWidth(450).setHeight(100).setBackgroundColor(ColorConstants.MAGENTA).setMarginTop(50);

        flexContainer.add(innerDiv).add(createNewDiv());
        document.add(flexContainer).add(divToCompare);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private Div getFlexContainer(OverflowPropertyValue overflowX, Style style) {
        FlexContainer flexContainer = createFlexContainer();
        flexContainer
                .setBackgroundColor(ColorConstants.GREEN)
                .setBorderRight(new SolidBorder(60));
        if (null != style) {
            flexContainer.addStyle(style);
        }
        if (null != overflowX) {
            flexContainer.setProperty(Property.OVERFLOW_X, overflowX);
        }
        return flexContainer;
    }

    private static Div getFlexItem(OverflowPropertyValue overflowX, Style style) {
        Div flexItem = new Div();
        flexItem.setProperty(Property.FLEX_GROW, 0f);
        flexItem.setProperty(Property.FLEX_SHRINK, 0f);
        if (null != style) {
            flexItem.addStyle(style);
        }
        flexItem.setBackgroundColor(ColorConstants.BLUE);
        if (null != overflowX) {
            flexItem.setProperty(Property.OVERFLOW_X, overflowX);
        }
        return flexItem;
    }

    private FlexContainer createFlexContainer() {
        FlexContainer flexContainer = new FlexContainer();
        flexContainer.setProperty(Property.ALIGN_ITEMS, alignItemsValue);
        flexContainer.setProperty(Property.JUSTIFY_CONTENT, justifyContentValue);
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
