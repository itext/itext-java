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
package com.itextpdf.layout.element;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.properties.GridValue;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class GridContainerTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/GridContainerTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/GridContainerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicThreeColumnsTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicThreeColumnsTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicThreeColumnsTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 150.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 150.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 150.0f)));
        SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            for (int i = 0; i < 5; ++i) {
                grid.add(new Paragraph("Test" + i).setBorder(border));
            }
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void basicAutoColumnsTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicAutoColumnsTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicAutoColumnsTest.pdf";
        SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_AUTO_COLUMNS, GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 150.0f)));
            for (int i = 0; i < 5; ++i) {
                grid.add(new Paragraph("Test" + i).setBorder(border));
            }
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void basicAutoRowsTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicAutoRowsTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicAutoRowsTest.pdf";
        SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_AUTO_ROWS, GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
            grid.add(new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                    "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                    "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                    "id est laborum.").setBorder(border));
            grid.add(new Paragraph("test").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void basicThreeColumnsWithCustomColumnIndexesTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicThreeColumnsWithCustomColumnIndexesTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicThreeColumnsWithCustomColumnIndexesTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One").setBorder(border);
            paragraph1.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph1.setProperty(Property.GRID_COLUMN_END, 3);
            grid.add(paragraph1);
            grid.add(new Paragraph("Two").setBorder(border));
            Paragraph paragraph3 = new Paragraph("Three").setBorder(border);
            paragraph3.setProperty(Property.GRID_COLUMN_START, 2);
            paragraph3.setProperty(Property.GRID_COLUMN_END, 4);
            grid.add(paragraph3);
            grid.add(new Paragraph("Four").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void threeColumnsWithAdjacentWideCellsTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "threeColumnsWithAdjacentWideCellsTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_threeColumnsWithAdjacentWideCellsTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One");
            paragraph1.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph1.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph1.setBorder(border);
            grid.add(paragraph1);
            Paragraph paragraph2 = new Paragraph("Two");
            paragraph2.setProperty(Property.GRID_COLUMN_START, 3);
            paragraph2.setProperty(Property.GRID_COLUMN_END, 5);
            paragraph2.setBorder(border);
            grid.add(paragraph2);
            Paragraph paragraph3 = new Paragraph("Three");
            paragraph3.setProperty(Property.GRID_COLUMN_START, 2);
            paragraph3.setProperty(Property.GRID_COLUMN_END, 4);
            paragraph3.setBorder(border);
            grid.add(paragraph3);
            grid.add(new Paragraph("Four").setBorder(border));
            grid.add(new Paragraph("Five").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void basicThreeColumnsWithCustomRowIndexesTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicThreeColumnsWithCustomRowIndexesTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicThreeColumnsWithCustomRowIndexesTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One").setBorder(border);
            paragraph1.setProperty(Property.GRID_ROW_START, 1);
            paragraph1.setProperty(Property.GRID_ROW_END, 3);
            grid.add(paragraph1);
            grid.add(new Paragraph("Two").setBorder(border));
            Paragraph paragraph3 = new Paragraph("Three").setBorder(border);
            paragraph3.setProperty(Property.GRID_ROW_START, 3);
            paragraph3.setProperty(Property.GRID_ROW_END, 4);
            grid.add(paragraph3);
            grid.add(new Paragraph("Four").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void basicThreeColumnsWithCustomColumnAndRowIndexesTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicThreeColumnsWithCustomColumnAndRowIndexesTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicThreeColumnsWithCustomColumnAndRowIndexesTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One");
            paragraph1.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph1.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph1.setProperty(Property.GRID_ROW_START, 1);
            paragraph1.setProperty(Property.GRID_ROW_END, 3);
            paragraph1.setBorder(border);
            grid.add(paragraph1);
            grid.add(new Paragraph("Two").setBorder(border));
            grid.add(new Paragraph("Three").setBorder(border));
            grid.add(new Paragraph("Four").setBorder(border));
            grid.add(new Paragraph("Five").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void basicThreeColumnsWithReversedIndexesTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicThreeColumnsWithReversedIndexesTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicThreeColumnsWithReversedIndexesTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One");
            paragraph1.setProperty(Property.GRID_COLUMN_START, 3);
            paragraph1.setProperty(Property.GRID_COLUMN_END, 1);
            paragraph1.setProperty(Property.GRID_ROW_START, 3);
            paragraph1.setProperty(Property.GRID_ROW_END, 1);
            paragraph1.setBorder(border);
            grid.add(paragraph1);
            Paragraph paragraph2 = new Paragraph("Two");
            paragraph2.setProperty(Property.GRID_ROW_START, 3);
            paragraph2.setProperty(Property.GRID_ROW_END, 1);
            paragraph2.setBorder(border);
            grid.add(paragraph2);
            Paragraph paragraph3 = new Paragraph("Three");
            paragraph3.setProperty(Property.GRID_COLUMN_START, 3);
            paragraph3.setProperty(Property.GRID_COLUMN_END, 1);
            paragraph3.setBorder(border);
            grid.add(paragraph3);
            grid.add(new Paragraph("Four").setBorder(border));
            grid.add(new Paragraph("Five").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void basicThreeColumnsWithoutColumnEndTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicThreeColumnsWithoutColumnEndTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicThreeColumnsWithoutColumnEndTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One");
            paragraph1.setProperty(Property.GRID_ROW_END, 2);
            paragraph1.setBorder(border);
            grid.add(paragraph1);
            Paragraph paragraph2 = new Paragraph("Two");
            paragraph2.setProperty(Property.GRID_ROW_START, 2);
            paragraph2.setBorder(border);
            grid.add(paragraph2);
            Paragraph paragraph3 = new Paragraph("Three");
            paragraph3.setProperty(Property.GRID_COLUMN_START, 3);
            paragraph3.setBorder(border);
            grid.add(paragraph3);
            Paragraph paragraph4 = new Paragraph("Four");
            paragraph4.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph4.setBorder(border);
            grid.add(paragraph4);
            grid.add(new Paragraph("Five").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void fixedColumnRowGoesFirstTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "fixedColumnRowGoesFirstTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_fixedColumnRowGoesFirstTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One");
            paragraph1.setProperty(Property.GRID_ROW_END, 3);
            paragraph1.setBorder(border);
            grid.add(paragraph1);
            Paragraph paragraph2 = new Paragraph("Two\nTwo");
            paragraph2.setProperty(Property.GRID_ROW_START, 1);
            paragraph2.setProperty(Property.GRID_ROW_END, 3);
            paragraph2.setBorder(border);
            grid.add(paragraph2);
            grid.add(new Paragraph("Three").setBorder(border));
            grid.add(new Paragraph("Four").setBorder(border));
            grid.add(new Paragraph("Five").setBorder(border));

            Paragraph paragraph6 = new Paragraph("Six");
            paragraph6.setProperty(Property.GRID_COLUMN_START, 3);
            paragraph6.setBorder(border);
            grid.add(paragraph6);
            Paragraph paragraph7 = new Paragraph("Seven");
            paragraph7.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph7.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph7.setBorder(border);
            grid.add(paragraph7);
            Paragraph paragraph8 = new Paragraph("Eight\nEight");
            paragraph8.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph8.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph8.setProperty(Property.GRID_ROW_START, 4);
            paragraph8.setProperty(Property.GRID_ROW_END, 6);
            paragraph8.setBorder(border);
            grid.add(paragraph8);

            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }
    
    @Test
    public void overlapWithExistingColumnTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "overlapWithExistingColumnTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("Two");
            paragraph1.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph1.setProperty(Property.GRID_COLUMN_END, 2);
            paragraph1.setProperty(Property.GRID_ROW_START, 1);
            paragraph1.setProperty(Property.GRID_ROW_END, 2);
            paragraph1.setBorder(border);
            grid.add(paragraph1);
            grid.add(new Paragraph("Three").setBorder(border));
            grid.add(new Paragraph("Four").setBorder(border));
            grid.add(new Paragraph("Five").setBorder(border));
            Paragraph paragraph2 = new Paragraph("One");
            paragraph2.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph2.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph2.setProperty(Property.GRID_ROW_START, 1);
            paragraph2.setProperty(Property.GRID_ROW_END, 3);
            paragraph2.setBorder(border);
            grid.add(paragraph2);
            Exception e = Assert.assertThrows(IllegalArgumentException.class, () ->
                    document.add(grid));
            Assert.assertEquals(LayoutExceptionMessageConstant.INVALID_CELL_INDEXES, e.getMessage());
        }
    }

    @Test
    @Ignore("TODO DEVSIX-8324")
    public void basicThreeColumnsWithPtAndPercentTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "basicThreeColumnsWithPtAndPercentTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_basicThreeColumnsWithPtAndPercentTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.PERCENT, 15.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.PERCENT, 50.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            for (int i = 0; i < 5; ++i) {
                grid.add(new Paragraph("Test" + i).setBorder(border));
            }
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void thirdColumnNotLayoutedTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "thirdColumnNotLayoutedTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_thirdColumnNotLayoutedTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 200.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 200.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 200.0f)));
        SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            for (int i = 0; i < 5; ++i) {
                grid.add(new Paragraph("Test" + i).setBorder(border));
            }
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void threeColumnsWithSquareAndVerticalCellsTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "threeColumnsWithSquareAndVerticalCellsTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_threeColumnsWithSquareAndVerticalCellsTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One");
            paragraph1.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph1.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph1.setProperty(Property.GRID_ROW_START, 1);
            paragraph1.setProperty(Property.GRID_ROW_END, 3);
            paragraph1.setBorder(border);
            grid.add(paragraph1);
            grid.add(new Paragraph("Two").setBorder(border));
            Paragraph paragraph3 = new Paragraph("Three");
            paragraph3.setProperty(Property.GRID_ROW_START, 2);
            paragraph3.setProperty(Property.GRID_ROW_END, 4);
            paragraph3.setBorder(border);
            grid.add(paragraph3);
            grid.add(new Paragraph("Four").setBorder(border));
            grid.add(new Paragraph("Five").setBorder(border));
            grid.add(new Paragraph("Six").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void threeColumnsWithSquareCellAndCellWithExplicitHeightTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "threeColumnsWithSquareCellAndCellWithExplicitHeightTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_threeColumnsWithSquareCellAndCellWithExplicitHeightTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One");
            paragraph1.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph1.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph1.setProperty(Property.GRID_ROW_START, 1);
            paragraph1.setProperty(Property.GRID_ROW_END, 3);
            paragraph1.setBorder(border).setBackgroundColor(ColorConstants.RED);
            grid.add(paragraph1);
            grid.add(new Paragraph("Two").setBorder(border).setBackgroundColor(ColorConstants.RED));
            grid.add(new Paragraph("Three").setBorder(border).setBackgroundColor(ColorConstants.RED).setHeight(100.0f));
            grid.add(new Paragraph("Four").setBorder(border).setBackgroundColor(ColorConstants.RED));
            grid.add(new Paragraph("Five").setBorder(border).setBackgroundColor(ColorConstants.RED));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void threeColumnsWithVerticalCellWithSeveralNeighboursToTheLeftTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "threeColumnsWithVerticalCellWithSeveralNeighboursToTheLeftTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_threeColumnsWithVerticalCellWithSeveralNeighboursToTheLeftTest.pdf";

        java.util.List<GridValue> templateColumns = new ArrayList<>();
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));
        templateColumns.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 100.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, templateColumns);
            Paragraph paragraph1 = new Paragraph("One");
            paragraph1.setProperty(Property.GRID_COLUMN_START, 1);
            paragraph1.setProperty(Property.GRID_COLUMN_END, 3);
            paragraph1.setProperty(Property.GRID_ROW_START, 1);
            paragraph1.setProperty(Property.GRID_ROW_END, 3);
            paragraph1.setBorder(border).setBackgroundColor(ColorConstants.RED);
            grid.add(paragraph1);
            grid.add(new Paragraph("Two").setBorder(border).setBackgroundColor(ColorConstants.RED));
            Paragraph paragraph3 = new Paragraph("Three");
            paragraph3.setProperty(Property.GRID_ROW_START, 2);
            paragraph3.setProperty(Property.GRID_ROW_END, 4);
            paragraph3.setBorder(border).setBackgroundColor(ColorConstants.RED);
            grid.add(paragraph3);
            grid.add(new Paragraph("Four").setBorder(border).setBackgroundColor(ColorConstants.RED));
            grid.add(new Paragraph("Five").setBorder(border).setBackgroundColor(ColorConstants.RED));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void bigCellMinContentTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "bigCellMinContentTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_bigCellMinContentTest.pdf";
        SolidBorder border = new SolidBorder(ColorConstants.BLUE, 1);

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.add(new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore").setBorder(border));
            grid.add(new Paragraph("test").setBorder(border));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void columnRowGapTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "columnRowGapTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_columnRowGapTest.pdf";
        java.util.List<GridValue> template = new ArrayList<>();
        template.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 50.0f)));
        template.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 50.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, template);
            grid.setProperty(Property.GRID_TEMPLATE_ROWS, template);
            grid.setProperty(Property.GRID_AUTO_ROWS, GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 70.0f)));
            grid.setProperty(Property.COLUMN_GAP, 20.0f);
            grid.setProperty(Property.ROW_GAP, 20.0f);
            grid.add(new Paragraph("One").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Two").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Tree").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Four").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Five").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Six").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Seven").setBackgroundColor(ColorConstants.CYAN));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void fewBigCellsWithGapTest() throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + "fewBigCellsWithGapTest.pdf";
        String cmpName = SOURCE_FOLDER + "cmp_fewBigCellsWithGapTest.pdf";
        java.util.List<GridValue> template = new ArrayList<>();
        template.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 50.0f)));
        template.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 50.0f)));
        template.add(GridValue.createUnitValue(new UnitValue(UnitValue.POINT, 50.0f)));

        try (Document document = new Document(new PdfDocument(new PdfWriter(filename)))) {
            GridContainer grid = new GridContainer();
            grid.setProperty(Property.GRID_TEMPLATE_COLUMNS, template);
            grid.setProperty(Property.GRID_TEMPLATE_ROWS, template);
            grid.setProperty(Property.COLUMN_GAP, 10.0f);
            grid.setProperty(Property.ROW_GAP, 10.0f);
            final Paragraph one = new Paragraph("One").setBackgroundColor(ColorConstants.CYAN);
            one.setProperty(Property.GRID_COLUMN_START, 1);
            one.setProperty(Property.GRID_COLUMN_END, 3);
            one.setProperty(Property.GRID_ROW_START, 1);
            one.setProperty(Property.GRID_ROW_END, 3);
            grid.add(one);
            grid.add(new Paragraph("Two").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Tree").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Four").setBackgroundColor(ColorConstants.CYAN));
            final Paragraph five = new Paragraph("Five").setBackgroundColor(ColorConstants.CYAN);
            five.setProperty(Property.GRID_COLUMN_START, 1);
            five.setProperty(Property.GRID_COLUMN_END, 4);
            five.setProperty(Property.GRID_ROW_START, 3);
            five.setProperty(Property.GRID_ROW_END, 5);
            grid.add(five);
            grid.add(new Paragraph("Six").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Seven").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Eight").setBackgroundColor(ColorConstants.CYAN));
            grid.add(new Paragraph("Nine").setBackgroundColor(ColorConstants.CYAN));
            document.add(grid);
        }
        Assert.assertNull(new CompareTool().compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }
}
