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

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class ColumnContainerTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/ColumnContainerTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/ColumnContainerTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void paragraphColumnContainerTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphColumnContainerTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphColumnContainerTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new ColumnContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);
            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                    "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                    "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                    "id est laborum.");
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void divColumnContainerTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "divColumnContainerTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_divColumnContainerTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new ColumnContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 2);
            Div div = new Div();
            div.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(50));
            div.setProperty(Property.BORDER, new SolidBorder(2));
            div.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(40));
            div.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
            div.setProperty(Property.WIDTH, UnitValue.createPointValue(450));
            div.setProperty(Property.HEIGHT, UnitValue.createPointValue(500));
            columnContainer.add(div);
            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }
}
