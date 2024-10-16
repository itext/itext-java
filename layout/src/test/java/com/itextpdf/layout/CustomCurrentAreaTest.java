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
package com.itextpdf.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class CustomCurrentAreaTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/CustomCurrentAreaTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/CustomCurrentAreaTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void longListItemTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "longListItemTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_longListItemTest.pdf";
        Rectangle customArea = new Rectangle(0, 15, 586, 723);
        try(PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));Document document = new Document(pdf)) {
            ClauseRenderer renderer = new ClauseRenderer(document, customArea);
            document.setRenderer(renderer);

            com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
            list.setListSymbol("1.");
            list.add(new ListItem(
                    "It is a long established fact that a reader will be distracted by the readable content of a page"
                            + " when looking at its layout."));
            document.add(new Table(1).addCell(new Cell().add(list)));
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    private static class ClauseRenderer extends DocumentRenderer {
        protected Rectangle column;

        public ClauseRenderer(Document document, Rectangle rect) {
            super(document, false);
            this.column = rect;
        }

        @Override
        protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
            super.updateCurrentArea(overflowResult);
            return (currentArea = new RootLayoutArea(currentArea.getPageNumber(), column.clone()));
        }
    }
}
