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
package com.itextpdf.layout.properties;

import com.itextpdf.commons.utils.PlaceHolderTextUtil;
import com.itextpdf.commons.utils.PlaceHolderTextUtil.PlaceHolderTextBy;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ContinuousContainerTest extends ExtendedITextTest {


    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/ContinuousContainerTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/ContinuousContainerTest/";

    private static final float DEFAULT_PADDING = 40F;
    private static final float DEFAULT_MARGIN = 100F;
    private static final Color DEFAULT_BACKGROUND_COLOR = ColorConstants.CYAN;
    private static final Border DEFAULT_BORDER = new SolidBorder(ColorConstants.RED, 5F);

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void blockRendererMarginTop() throws IOException, InterruptedException {
        executeTest("blockRendererMarginTop", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMarginTop(DEFAULT_MARGIN);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererMarginBottom() throws IOException, InterruptedException {
        executeTest("blockRendererMarginBottom", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererMarginAll() throws IOException, InterruptedException {
        executeTest("blockRendererMarginAll", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMargin(100);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererPaddingTop() throws IOException, InterruptedException {
        executeTest("blockRendererPaddingTop", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingTop(DEFAULT_PADDING);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererPaddingBottom() throws IOException, InterruptedException {
        executeTest("blockRendererPaddingBottom", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererPaddingAll() throws IOException, InterruptedException {
        executeTest("blockRendererPaddingAll", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPadding(DEFAULT_PADDING);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererBorderTop() throws IOException, InterruptedException {
        executeTest("blockRendererBorderTop", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorderTop(DEFAULT_BORDER);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererBorderBottom() throws IOException, InterruptedException {
        executeTest("blockRendererBorderBottom", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorderBottom(DEFAULT_BORDER);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererBorderAll() throws IOException, InterruptedException {
        executeTest("blockRendererBorderAll", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }

    @Test
    public void blockRendererBorderWideAll() throws IOException, InterruptedException {
        executeTest("blockRendererBorderWideAll", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(new SolidBorder(ColorConstants.GREEN, 50F));
            for (int i = 0; i < 30; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 5)));
            }
        });
    }


    @Test
    public void blockRendererMultiPageBorderPaddingMargin() throws IOException, InterruptedException {
        executeTest("blockRendererMultiPageBorderPaddingMargin", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            for (int i = 0; i < 100; i++) {
                ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 10)));
            }
        });
    }

    @Test
    public void blockRendererWithComplexInnerElements() throws IOException, InterruptedException {
        executeTest("blockRendererWithComplexInnerElements", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMarginTop(40);
            ctx.setMarginBottom(80);
            ctx.setPaddingTop(40);
            ctx.setPaddingBottom(20);
            Table table = new Table(3);
            for (int i = 0; i < 99; i++) {
                table.addCell(new Paragraph("Some text"));
            }
            ctx.add(table);
            ctx.add(new Paragraph("Before area break"));
            ctx.add(new AreaBreak());
            ctx.add(new AreaBreak());
            ctx.add(new Paragraph("after area break"));
            List list = new List();
            for (int i = 0; i < 150; i++) {
                list.add(new ListItem("Bing"));
            }
            ctx.add(list);

        });
    }

    @Test
    public void blockRendererception() throws IOException, InterruptedException {
        executeTest("blockRenderception", new Div(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMarginTop(DEFAULT_MARGIN / 2);
            ctx.setMarginBottom(DEFAULT_MARGIN / 2);
            ctx.setPaddingTop(DEFAULT_PADDING / 2);
            ctx.setPaddingBottom(DEFAULT_PADDING / 2);
            ctx.setPaddingLeft(15);
            ctx.setPaddingRight(15);

            Div div1 = new Div();
            div1.setBackgroundColor(ColorConstants.PINK);
            div1.setBorder(new SolidBorder(ColorConstants.BLUE, 3));
            div1.setMargin(DEFAULT_MARGIN / 2);
            div1.setPadding(DEFAULT_PADDING / 2);

            Div div2 = new Div();
            div2.setBackgroundColor(ColorConstants.GREEN);
            div2.setBorder(new SolidBorder(ColorConstants.RED, 3));
            div2.setMargin(DEFAULT_MARGIN / 2);
            div2.setPadding(DEFAULT_PADDING / 2);

            for (int i = 0; i < 60; i++) {
                div2.add(new Paragraph("Bing bong"));
            }

            div1.add(div2);
            ctx.add(div1);

        });
    }

    @Test
    public void paragraphRendererMarginTop() throws IOException, InterruptedException {
        executeTest("paragraphRendererMarginTop", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 1500));
        });
    }

    @Test
    public void paragraphRendererMarginBottom() throws IOException, InterruptedException {
        executeTest("paragraphRendererMarginBottom", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererMarginAll() throws IOException, InterruptedException {
        executeTest("paragraphRendererMarginAll", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMargin(DEFAULT_MARGIN);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererFitsWithoutMarginButWeTriggerOverflow()
            throws IOException, InterruptedException {
        executeTest("paragraphRendererFitsWithoutMarginButWeTriggerOverflow", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            //just the right amount of words to fit the paragraph on the page
            final int amountOfWords = 900;
            //trigger overflow
            ctx.setMarginTop(20);
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }


    @Test
    public void paragraphRendererFitsWithoutPaddingButWeTriggerOverflow()
            throws IOException, InterruptedException {
        executeTest("paragraphRendererFitsWithoutPaddingButWeTriggerOverflow", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            //just the right amount of words to fit the paragraph on the page
            final int amountOfWords = 900;
            //trigger overflow with small padding
            ctx.setPaddingTop(20);
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }


    @Test
    public void paragraphRendererFitsWithoutBorderButWeTriggerOverflow()
            throws IOException, InterruptedException {
        executeTest("paragraphRendererFitsWithoutBorderButWeTriggerOverflow", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            //just the right amount of words to fit the paragraph on the page
            final int amountOfWords = 900;
            //trigger overflow
            ctx.setBorder(new SolidBorder(ColorConstants.RED, 8));
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererPaddingTop() throws IOException, InterruptedException {
        executeTest("paragraphRendererPaddingTop", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingTop(DEFAULT_PADDING);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererPaddingBottom() throws IOException, InterruptedException {
        executeTest("paragraphRendererPaddingBottom", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererPaddingAll() throws IOException, InterruptedException {
        executeTest("paragraphRendererPaddingAll", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPadding(DEFAULT_PADDING);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererBorderTop() throws IOException, InterruptedException {
        executeTest("paragraphRendererBorderTop", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorderTop(DEFAULT_BORDER);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererBorderBottom() throws IOException, InterruptedException {
        executeTest("paragraphRendererBorderBottom", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorderBottom(DEFAULT_BORDER);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererBorderAll() throws IOException, InterruptedException {
        executeTest("paragraphRendererBorderAll", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }


    @Test
    public void paragraphRendererWideBorderAll() throws IOException, InterruptedException {
        executeTest("paragraphRendererWideBorderAll", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(new SolidBorder(ColorConstants.GREEN, 25));
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
        });
    }

    @Test
    public void paragraphRendererBorderMarginPadding() throws IOException, InterruptedException {
        executeTest("paragraphRendererBorderMarginPadding", new Paragraph(), ctx -> {
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPadding(DEFAULT_PADDING);
            ctx.setMargin(DEFAULT_MARGIN);
            ctx.setBorder(DEFAULT_BORDER);
            final int amountOfWords = 1000;
            ctx.add(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords));
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
        Assertions.assertNull(compareTool.compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

}
