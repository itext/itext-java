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

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class MulticolContainerTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/MulticolContainerTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/MulticolContainerTest/";

    private static final float DEFAULT_PADDING = 40F;
    private static final float DEFAULT_MARGIN = 100F;
    private static final Color DEFAULT_BACKGROUND_COLOR = ColorConstants.CYAN;
    private static final Border DEFAULT_BORDER = new SolidBorder(ColorConstants.RED, 5F);

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void paragraphColumnContainerTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphColumnContainerTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphColumnContainerTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
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
            Div columnContainer = new MulticolContainer();
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

    @Test
    public void columnedDivInsideTableTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "columnedDivInsideTableTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_columnedDivInsideTableTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Table table = new Table(2);
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);
            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                    "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                    "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                    "id est laborum.");
            columnContainer.add(paragraph);
            table.addCell(columnContainer);
            table.addCell(new Cell());
            document.add(table);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void continuousColumContainerParagraphMarginTopBottom() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphMarginTopBottom", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 2);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMarginTop(DEFAULT_MARGIN * 1.25F);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.add(new Paragraph(generateLongString(400)));
        });
    }

    @Test
    public void continuousColumContainerParagraphPaddingTopBottom() throws IOException, InterruptedException {
        executeTest("continuousColumContainerPaddingTopBottom", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setPaddingBottom(DEFAULT_PADDING * 2F);
            ctx.add(new Paragraph(generateLongString(400)));
        });
    }

    @Test
    public void continuousColumContainerParagraphBorder() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphBorder", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.add(new Paragraph(generateLongString(400)));
        });
    }


    @Test
    public void continuousColumContainerParagraphAll() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphAll", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            ctx.add(new Paragraph(generateLongString(300)));
        });
    }

    @Test
    public void continuousColumContainerParagraphAllChildStart() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphAllChildStart", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            Paragraph paragraph = new Paragraph(generateLongString(300));
            paragraph.setBorder(new SolidBorder(ColorConstants.RED, 2));
            paragraph.setMarginTop(200);
            paragraph.setPaddingTop(40);
            paragraph.setBackgroundColor(ColorConstants.PINK);
            ctx.add(paragraph);
        });
    }

    @Test
    public void continuousColumContainerParagraphAllChildEnd() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphAllChildEnd", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            Paragraph paragraph = new Paragraph(generateLongString(300));
            paragraph.setBorder(new SolidBorder(ColorConstants.RED, 2));
            paragraph.setMarginBottom(200);
            paragraph.setPaddingBottom(40);
            paragraph.setBackgroundColor(ColorConstants.PINK);
            ctx.add(paragraph);
        });
    }


    @Test
    public void continuousColumContainerParagraphOverflowShouldShow() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphOverflowShouldShow", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            ctx.add(new Paragraph(generateLongString(8000)));
        });
    }

    @Test
    public void continuousColumContainerMultipleElementsMarginTop() throws IOException, InterruptedException {
        executeTest("continuousColumContainerMultipleElementsMarginTop", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.add(pseudoContainer);
        });
    }

    @Test
    public void continuousColumContainerMultipleElementsMarginBottom() throws IOException, InterruptedException {
        executeTest("continuousColumContainerMultipleElementsMarginBottom", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            pseudoContainer.setBackgroundColor(ColorConstants.YELLOW);
            for (int i = 0; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.setMarginBottom(30);
            ctx.add(pseudoContainer);
        });
    }

    @Test
    public void continuousColumContainerInnerBackgroundColorAndBorder() throws IOException, InterruptedException {
        executeTest("continuousColumContainerInnerBackgroundColor", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.add(new Paragraph(generateLongString(400))
                    .setBackgroundColor(ColorConstants.YELLOW)
                    .setMarginTop(DEFAULT_MARGIN)
                    .setBorder(new SolidBorder(ColorConstants.RED, 2))
            );
            ctx.setMarginBottom(DEFAULT_MARGIN);
        });
    }


    @Test
    public void continuousColumContainerMultipleElementsPaddingTop() throws IOException, InterruptedException {
        executeTest("continuousColumContainerMultipleElementsPaddingTop", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.add(pseudoContainer);
        });
    }

    @Test
    public void continuousColumContainerMultipleElementsPaddingBottom() throws IOException, InterruptedException {
        executeTest("continuousColumContainerMultipleElementsPaddingBottom", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.setPaddingBottom(DEFAULT_PADDING);
            ctx.add(pseudoContainer);
        });
    }

    @Test
    public void continuousColumContainerMultipleElementsBorder() throws IOException, InterruptedException {
        executeTest("continuousColumContainerMultipleElementsBorder", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.setBorder(DEFAULT_BORDER);
            ctx.add(pseudoContainer);
        });
    }

    private <T extends IBlockElement> void executeTest(String testName, T container, Consumer<T> executor)
            throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + testName + ".pdf";
        String cmpName = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new com.itextpdf.kernel.pdf.PdfWriter(filename))) {
            Document doc = new Document(pdfDoc);

            container.setProperty(Property.TREAT_AS_CONTINUOUS_CONTAINER, true);
            executor.accept(container);

            doc.add(new Paragraph("ELEMENT ABOVE").setBackgroundColor(ColorConstants.YELLOW));
            doc.add(container);
            doc.add(new Paragraph("ELEMENT BELOW").setBackgroundColor(ColorConstants.YELLOW));
        }
        CompareTool compareTool = new CompareTool();
        Assert.assertNull(compareTool.compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    private static String generateLongString(int amountOfWords) {
        StringBuilder sb = new StringBuilder();
        int random = 1;
        for (int i = 0; i < amountOfWords; i++) {
            random = getPseudoRandomInt(i + random);
            for (int j = 1; j <= random; j++) {
                sb.append('a');
            }
            sb.append(' ');
        }
        return sb.toString();
    }

    private static int getPseudoRandomInt(int prev) {
        final int first = 93840;
        final int second = 1929;
        final int max = 7;
        return (prev * first + second) % max;
    }
}
