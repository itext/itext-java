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

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.VerticalParagraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
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
import java.util.List;

@Tag("IntegrationTest")
public class VerticalTextTableTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/VerticalTextTableTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/VerticalTextTableTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    public static List<Boolean> isRightToLeftProgression() {
        return Arrays.asList(Boolean.FALSE, Boolean.TRUE);
    }

    @ParameterizedTest
    @MethodSource("isRightToLeftProgression")
    public void verticalTextInTableVerticalAlignmentTest(boolean rightToLeftProgression)
            throws IOException, InterruptedException {
        String fileName = "verticalTextInTableVerticalAlignment" + (rightToLeftProgression ? "_rtl" : "_ltr");
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Table table = new Table(UnitValue.createPointArray(new float[]{150, 150, 150}));

            table.addCell(createCell("top\nseveral\nlines", VerticalAlignment.TOP, new DeviceRgb(222, 243, 222),
                    TextAlignment.LEFT, false, rightToLeftProgression));
            table.addCell(createCell("middle\nseveral\nlines", VerticalAlignment.MIDDLE, new DeviceRgb(222, 243, 222),
                    TextAlignment.LEFT, false, rightToLeftProgression));
            table.addCell(createCell("bottom\nseveral\nlines", VerticalAlignment.BOTTOM, new DeviceRgb(222, 243, 222),
                    TextAlignment.LEFT, false, rightToLeftProgression));

            table.addCell(createCell("top\nseveral\nlines", VerticalAlignment.TOP, new DeviceRgb(222, 243, 222),
                    TextAlignment.LEFT, true, rightToLeftProgression));
            table.addCell(createCell("middle\nseveral\nlines", VerticalAlignment.MIDDLE, new DeviceRgb(222, 243, 222),
                    TextAlignment.LEFT, true, rightToLeftProgression));
            table.addCell(createCell("bottom\nseveral\nlines", VerticalAlignment.BOTTOM, new DeviceRgb(222, 243, 222),
                    TextAlignment.LEFT, true, rightToLeftProgression));

            table.addCell(createCell("top\nseveral\nlines\ntop\nseveral\nlines\ntop\nseveral\nlines\ntop",
                    VerticalAlignment.TOP, new DeviceRgb(255, 249, 220), TextAlignment.LEFT, true,
                    rightToLeftProgression
            ));
            table.addCell(createCell("middle\nseveral\nlines\nmiddle\nseveral\nlines\nmiddle\nseveral\nlines\nmiddle",
                    VerticalAlignment.MIDDLE, new DeviceRgb(222, 243, 222), TextAlignment.LEFT, true,
                    rightToLeftProgression
            ));
            table.addCell(createCell("bottom\nseveral\nlines\nbottom\nseveral\nlines\nbottom\nseveral\nlines\nbottom",
                    VerticalAlignment.BOTTOM, new DeviceRgb(254, 243, 255), TextAlignment.LEFT, true,
                    rightToLeftProgression
            ));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextInTableTextAlignmentTest() throws IOException, InterruptedException {
        String fileName = "verticalTextInTableTextAlignment";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Table table = new Table(UnitValue.createPointArray(new float[]{150, 150, 150}));

            table.addCell(createCell("top\nseveral\nlines", VerticalAlignment.TOP, new DeviceRgb(222, 243, 222),
                    TextAlignment.LEFT, false, false));
            table.addCell(createCell("middle\nseveral\nlines", VerticalAlignment.TOP, new DeviceRgb(222, 243, 222),
                    TextAlignment.CENTER, false, false));
            table.addCell(createCell("bottom\nseveral\nlines", VerticalAlignment.TOP, new DeviceRgb(222, 243, 222),
                    TextAlignment.RIGHT, false, false));

            table.addCell(createCell("top\nseveral\nlines", VerticalAlignment.TOP, new DeviceRgb(222, 243, 222),
                    TextAlignment.LEFT, true, false));
            table.addCell(createCell("middle\nseveral\nlines", VerticalAlignment.TOP, new DeviceRgb(222, 243, 222),
                    TextAlignment.CENTER, true, false));
            table.addCell(createCell("bottom\nseveral\nlines", VerticalAlignment.TOP, new DeviceRgb(222, 243, 222),
                    TextAlignment.RIGHT, true, false));

            table.addCell(createCell("top\nseveral\nlines\ntop\nseveral\nlines\ntop\nseveral\nlines\ntop",
                    VerticalAlignment.TOP, new DeviceRgb(255, 249, 220), TextAlignment.LEFT, true, false));
            table.addCell(createCell("middle\nseveral\nlines\nmiddle\nseveral\nlines\nmiddle\nseveral\nlines\nmiddle",
                    VerticalAlignment.TOP, new DeviceRgb(222, 243, 222), TextAlignment.CENTER, true, false));
            table.addCell(createCell("bottom\nseveral\nlines\nbottom\nseveral\nlines\nbottom\nseveral\nlines\nbottom",
                    VerticalAlignment.TOP, new DeviceRgb(254, 243, 255), TextAlignment.RIGHT, true, false));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextTableCellAlignWithHeightTest() throws IOException, InterruptedException {
        String fileName = "verticalTextTableCellAlignWithHeight";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Table table = new Table(UnitValue.createPointArray(new float[]{120, 120, 120, 120}));

            table.addHeaderCell(createVerticalParagraphCell("Left", TextAlignment.LEFT, VerticalAlignment.TOP,
                    null, 50, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalParagraphCell("Center", TextAlignment.CENTER, VerticalAlignment.MIDDLE,
                    null, 50, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalParagraphCell("Right", TextAlignment.RIGHT, VerticalAlignment.BOTTOM,
                    null, 50, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalTextCell("Middle vertical", TextAlignment.CENTER,
                    VerticalAlignment.MIDDLE));

            table.addCell(createHorizontalCell("Row horizontal value A"));
            table.addCell(createHorizontalCell("Row horizontal value B"));
            table.addCell(createHorizontalCell("Row horizontal value C"));
            table.addCell(createHorizontalCell("Row horizontal value D"));

            table.addHeaderCell(createVerticalParagraphCell("Right", TextAlignment.RIGHT, VerticalAlignment.TOP,
                    200f, null, new DeviceRgb(255, 222, 239)).setBackgroundColor(new DeviceRgb(253, 253, 244)));
            table.addHeaderCell(createVerticalParagraphCell("Center", TextAlignment.CENTER, VerticalAlignment.MIDDLE,
                    null, 400, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalParagraphCell("Right", TextAlignment.RIGHT, VerticalAlignment.BOTTOM,
                    null, 200, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalTextCell("Middle vertical", TextAlignment.CENTER,
                    VerticalAlignment.MIDDLE));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextTableCellHorizontalAlignTest() throws IOException, InterruptedException {
        String fileName = "verticalTextTableCellHorizontalAlign";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Table table = new Table(UnitValue.createPointArray(new float[]{105, 105, 105, 105, 105}));

            table.addHeaderCell(createVerticalParagraphCell("Left\nseveral\nlines", TextAlignment.LEFT,
                    VerticalAlignment.MIDDLE, null, null, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalParagraphCell("Center\nseveral\nlines", TextAlignment.CENTER,
                    VerticalAlignment.MIDDLE, null, null, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalParagraphCell("Right\nseveral\nlines", TextAlignment.RIGHT,
                    VerticalAlignment.MIDDLE, null, null, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalTextCell("Middle vertical", TextAlignment.CENTER,
                    VerticalAlignment.MIDDLE));
            table.addHeaderCell(createHorizontalCell("Horizontal")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            table.addCell(createHorizontalCell("Row horizontal value A"));
            table.addCell(createHorizontalCell("Row horizontal value B"));
            table.addCell(createHorizontalCell("Row horizontal value C"));
            table.addCell(createHorizontalCell("Row horizontal value D"));
            table.addCell(createHorizontalCell("Row horizontal value E"));

            table.addFooterCell(createVerticalTextCell("Left", TextAlignment.LEFT, VerticalAlignment.MIDDLE));
            table.addFooterCell(createVerticalTextCell("Center", TextAlignment.CENTER, VerticalAlignment.MIDDLE));
            table.addFooterCell(createVerticalTextCell("Right", TextAlignment.RIGHT, VerticalAlignment.MIDDLE));
            table.addFooterCell(createVerticalTextCell("Middle vertical", TextAlignment.LEFT,
                    VerticalAlignment.MIDDLE));
            table.addFooterCell(createHorizontalCell("Horizontal")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    @Test
    public void verticalTextTableCellVerticalAlignTest() throws IOException, InterruptedException {
        String fileName = "verticalTextTableCellVerticalAlign";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDocument = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDocument)) {
            Table table = new Table(UnitValue.createPointArray(new float[]{105, 105, 105, 105, 105}));

            table.addHeaderCell(createVerticalParagraphCell("Top", TextAlignment.CENTER, VerticalAlignment.TOP,
                    null, null, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalParagraphCell("Middle", TextAlignment.CENTER, VerticalAlignment.MIDDLE,
                    null, null, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalParagraphCell("Bottom", TextAlignment.CENTER, VerticalAlignment.BOTTOM,
                    null, null, new DeviceRgb(255, 222, 239)));
            table.addHeaderCell(createVerticalTextCell("Middle vertical", TextAlignment.CENTER,
                    VerticalAlignment.MIDDLE));
            table.addHeaderCell(createHorizontalCell("Horizontal")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            table.addCell(createHorizontalCell("Row horizontal value A"));
            table.addCell(createHorizontalCell("Row horizontal value B"));
            table.addCell(createHorizontalCell("Row horizontal value C"));
            table.addCell(createHorizontalCell("Row horizontal value D"));
            table.addCell(createHorizontalCell("Row horizontal value E"));

            table.addFooterCell(createVerticalTextCell("Top", TextAlignment.LEFT, VerticalAlignment.TOP));
            table.addFooterCell(createVerticalTextCell("Middle", TextAlignment.LEFT, VerticalAlignment.MIDDLE));
            table.addFooterCell(createVerticalTextCell("Bottom", TextAlignment.LEFT, VerticalAlignment.BOTTOM));
            table.addFooterCell(createVerticalTextCell("Middle vertical", TextAlignment.LEFT,
                    VerticalAlignment.MIDDLE));
            table.addFooterCell(createHorizontalCell("Horizontal").setTextAlignment(TextAlignment.LEFT)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE));

            document.add(table);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER));
    }

    private static Cell createCell(String text, VerticalAlignment verticalAlignment, DeviceRgb borderColor,
                                   TextAlignment textAlignment, boolean verticalCell, boolean rightToLeftProgression) {
        Cell cell = new Cell()
                .setHeight(200)
                .setVerticalAlignment(verticalAlignment)
                .setBorder(new SolidBorder(borderColor, 3))
                .setBackgroundColor(new DeviceRgb(255, 252, 254))
                .setPadding(0);

        if (verticalCell) {
            cell.setProperty(Property.WRITING_MODE,
                    rightToLeftProgression ? WritingMode.VERTICAL_RL : WritingMode.VERTICAL_LR);
            cell.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        }

        VerticalParagraph paragraph = new VerticalParagraph(text, rightToLeftProgression)
                .setMargin(0)
                .setBackgroundColor(new DeviceRgb(222, 243, 222))
                .setBorder(new SolidBorder(new DeviceRgb(153, 153, 153), 1))
                .setTextAlignment(textAlignment);

        cell.add(paragraph);
        return cell;
    }

    private static Cell createHorizontalCell(String text) {
        return new Cell().add(new Paragraph(text).setMargin(0));
    }

    private static Cell createVerticalTextCell(String text, TextAlignment textAlignment,
                                               VerticalAlignment verticalAlignment) {
        Cell cell = new Cell()
                .setVerticalAlignment(verticalAlignment)
                .setPadding(8)
                .setBorder(new SolidBorder(new DeviceRgb(153, 153, 153), 1));
        cell.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        cell.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);
        Paragraph paragraph = new Paragraph(text).setMargin(0).setTextAlignment(textAlignment);
        cell.add(paragraph);
        return cell;
    }

    private static Cell createVerticalParagraphCell(String text, TextAlignment textAlignment,
                                                    VerticalAlignment verticalAlignment, Float cellHeight,
                                                    Integer paragraphHeight, DeviceRgb paragraphBackground) {
        Cell cell = new Cell()
                .setVerticalAlignment(verticalAlignment)
                .setPadding(8)
                .setBorder(new SolidBorder(new DeviceRgb(153, 153, 153), 1));
        cell.setProperty(Property.WRITING_MODE, WritingMode.VERTICAL_LR);
        cell.setProperty(Property.TEXT_ORIENTATION, VerticalTextOrientation.UPRIGHT);

        if (cellHeight != null) {
            cell.setHeight((float) cellHeight);
        }

        VerticalParagraph paragraph = new VerticalParagraph(text, false)
                .setMargin(0)
                .setTextAlignment(textAlignment);

        if (paragraphHeight != null) {
            paragraph.setHeight((float) paragraphHeight);
        }
        if (paragraphBackground != null) {
            paragraph.setBackgroundColor(paragraphBackground);
        }

        cell.add(paragraph);
        return cell;
    }
}
