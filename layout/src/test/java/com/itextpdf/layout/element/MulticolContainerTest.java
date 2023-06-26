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

import com.itextpdf.commons.utils.PlaceHolderTextUtil;
import com.itextpdf.commons.utils.PlaceHolderTextUtil.PlaceHolderTextBy;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
        });
    }

    @Test
    public void continuousColumContainerParagraphPaddingTopBottom() throws IOException, InterruptedException {
        executeTest("continuousColumContainerPaddingTopBottom", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setPaddingBottom(DEFAULT_PADDING * 2F);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
        });
    }

    @Test
    public void continuousColumContainerParagraphBorder() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphBorder", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
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
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 300)));
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
            Paragraph paragraph = new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 300));
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
            Paragraph paragraph = new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 300));
            paragraph.setBorder(new SolidBorder(ColorConstants.RED, 2));
            paragraph.setMarginBottom(200);
            paragraph.setPaddingBottom(40);
            paragraph.setBackgroundColor(ColorConstants.PINK);
            ctx.add(paragraph);
        });
    }

    //TODO: DEVSIX-7626
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
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 8000)));
        });
    }

    //TODO: DEVSIX-7626
    @Test
    public void extraLargeColumnParagraphTest() throws IOException, InterruptedException {
        executeTest("extraLargeColumnParagraphTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 15000)));
        });
    }

    //TODO: DEVSIX-7626
    @Test
    public void largeColumnParagraphWithMarginTest() throws IOException, InterruptedException {
        executeTest("largeColumnParagraphWithMarginTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 8000)));
        });
    }

    //TODO: DEVSIX-7626
    @Test
    public void largeColumnParagraphWithPaddingTest() throws IOException, InterruptedException {
        executeTest("largeColumnParagraphWithPaddingTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 8000)));
        });
    }

    //TODO: DEVSIX-7626
    @Test
    public void largeColumnParagraphWithBorderTest() throws IOException, InterruptedException {
        executeTest("largeColumnParagraphWithBorderTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(new SolidBorder(ColorConstants.GREEN, 50));
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 8000)));
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
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400))
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

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, logLevel =
                    LogLevelConstants.WARN)})
    public void multicolElementWithKeepTogetherTest() throws IOException, InterruptedException {
        executeTest("multicolElementWithKeepTogether", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.setProperty(Property.KEEP_TOGETHER, true);
            ctx.add(pseudoContainer);
        });
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, logLevel =
                    LogLevelConstants.WARN)})
    public void allChildrenOfMulticolElementWithKeepTogetherTest() throws IOException, InterruptedException {
        executeTest("allChildrenOfMulticolElementWithKeepTogether", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            pseudoContainer.setProperty(Property.KEEP_TOGETHER, true);
            ctx.add(pseudoContainer);
        });
    }

    @Test
    public void childOfMulticolElementWithKeepTogetherTest() throws IOException, InterruptedException {
        executeTest("childOfMulticolElementWithKeepTogether", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 7; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            Div temp = new Div();
            temp.add(new Paragraph("7 keep"));
            temp.add(new Paragraph("8 keep"));
            temp.add(new Paragraph("9 keep"));
            temp.add(new Paragraph("10 keep"));
            temp.add(new Paragraph("11 keep"));
            temp.setProperty(Property.KEEP_TOGETHER, true);
            pseudoContainer.add(temp);

            for (int i = 12; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.add(pseudoContainer);
        });
    }

    @Test
    public void childrenOfMulticolElementWithKeepTogetherTest() throws IOException, InterruptedException {
        executeTest("childrenOfMulticolElementWithKeepTogether", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 7; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            Div temp = new Div();
            temp.add(new Paragraph("7 keep"));
            temp.add(new Paragraph("8 keep"));
            temp.add(new Paragraph("9 keep"));
            temp.add(new Paragraph("10 keep"));
            temp.add(new Paragraph("11 keep"));
            temp.setProperty(Property.KEEP_TOGETHER, Boolean.TRUE);
            pseudoContainer.add(temp);

            for (int i = 12; i < 19; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            temp = new Div();
            temp.add(new Paragraph("19 keep"));
            temp.add(new Paragraph("20 keep"));
            temp.add(new Paragraph("21 keep"));
            temp.add(new Paragraph("22 keep"));
            temp.add(new Paragraph("23 keep"));
            temp.add(new Paragraph("24 keep"));
            temp.add(new Paragraph("25 keep"));
            temp.setProperty(Property.KEEP_TOGETHER, Boolean.TRUE);
            pseudoContainer.add(temp);

            for (int i = 26; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.add(pseudoContainer);
        });
    }

    @Test
    public void singleParagraphMultiPageTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "singleParagraphMultiPageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_singleParagraphMultiPageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            document.add(createFirstPageFiller());
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
    public void singleParagraphWithBorderMultiPageTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "singleParagraphWithBorderMultiPageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_singleParagraphWithBorderMultiPageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            document.add(createFirstPageFiller());
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                    "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                    "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                    "id est laborum.");
            paragraph.setBorder(new SolidBorder(2));

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            columnContainer.add(paragraph);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void paragraphWithImagesMultiPageTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithImagesMultiPageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithImagesMultiPageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            document.add(createFirstPageFiller());
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            PdfImageXObject xObject = new PdfImageXObject(
                    ImageDataFactory.createPng(UrlUtil.toURL(SOURCE_FOLDER + "placeholder_100x100.png")));
            Image image1 = new Image(xObject, 20);
            Image image2 = new Image(xObject, 150);
            Image image3 = new Image(xObject, 100).setHorizontalAlignment(HorizontalAlignment.RIGHT);

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(paragraph);
            div.add(image1);
            div.add(image2);
            div.add(image3);
            columnContainer.add(div);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    //TODO: DEVSIX-7621
    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void paragraphWithOverflowingImageTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithOverflowingImageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithOverflowingImageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            document.add(createFirstPageFiller());
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            PdfImageXObject xObject = new PdfImageXObject(
                    ImageDataFactory.createPng(UrlUtil.toURL(SOURCE_FOLDER + "placeholder_100x100.png")));
            Image image = new Image(xObject, 200);

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(paragraph);
            div.add(image);
            columnContainer.add(div);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    //TODO: DEVSIX-7621
    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void overflowingImageWithParagraphTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "overflowingImageWithParagraphMultipageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_overflowingImageWithParagraphMultipageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            PdfImageXObject xObject = new PdfImageXObject(
                    ImageDataFactory.createPng(UrlUtil.toURL(SOURCE_FOLDER + "placeholder_100x100.png")));
            Image image = new Image(xObject, 200);

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(image);
            div.add(paragraph);
            columnContainer.add(div);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void imageBiggerThanPageTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "imageBiggerThanPageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_imageBiggerThanPageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            PdfImageXObject xObject = new PdfImageXObject(
                    ImageDataFactory.createPng(UrlUtil.toURL(SOURCE_FOLDER + "placeholder_100x100.png")));
            Image image = new Image(xObject, 800);

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(image);
            div.add(paragraph);
            columnContainer.add(div);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void overflowingDivWithParagraphMultipageTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "overflowingDivWithParagraphMultipageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_overflowingDivWithParagraphMultipageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            document.add(createFirstPageFiller());
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            Div columnDiv = new Div();
            columnDiv.setProperty(Property.BORDER, new SolidBorder(1));
            columnDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.BLUE));
            columnDiv.setProperty(Property.KEEP_TOGETHER, Boolean.TRUE);
            columnDiv.setProperty(Property.WIDTH, UnitValue.createPointValue(50));
            columnDiv.setProperty(Property.HEIGHT, UnitValue.createPointValue(150));

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(paragraph);
            div.add(columnDiv);
            columnContainer.add(div);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void marginCantFitCurrentPageTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "marginCantFitCurrentPageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_marginCantFitCurrentPageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            document.add(createFirstPageFiller());
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            Div columnDiv = new Div();
            columnDiv.setProperty(Property.BORDER, new SolidBorder(1));
            columnDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.BLUE));
            columnDiv.setProperty(Property.KEEP_TOGETHER, Boolean.TRUE);
            columnDiv.setProperty(Property.MARGIN_BOTTOM, UnitValue.createPointValue(40));
            columnDiv.setProperty(Property.WIDTH, UnitValue.createPointValue(60));
            columnDiv.setProperty(Property.HEIGHT, UnitValue.createPointValue(60));

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(columnDiv);
            div.add(paragraph);
            columnContainer.add(div);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void paddingCantFitCurrentPageTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paddingCantFitCurrentPageTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paddingCantFitCurrentPageTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            document.add(createFirstPageFiller());
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            Div columnDiv = new Div();
            columnDiv.setProperty(Property.BORDER, new SolidBorder(1));
            columnDiv.setProperty(Property.BACKGROUND, new Background(ColorConstants.BLUE));
            columnDiv.setProperty(Property.KEEP_TOGETHER, Boolean.TRUE);
            columnDiv.setProperty(Property.PADDING_BOTTOM, UnitValue.createPointValue(40));
            columnDiv.setProperty(Property.WIDTH, UnitValue.createPointValue(60));
            columnDiv.setProperty(Property.HEIGHT, UnitValue.createPointValue(60));

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(columnDiv);
            div.add(paragraph);
            columnContainer.add(div);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void keepTogetherBlockingLayoutTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "keepTogetherBlockingLayoutTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_keepTogetherBlockingLayoutTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            document.add(createFirstPageFiller());
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                    "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                    "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                    "id est laborum.");
            paragraph.setBorder(new SolidBorder(2));
            paragraph.setFontSize(20);
            paragraph.setProperty(Property.KEEP_TOGETHER, Boolean.TRUE);

            columnContainer.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(paragraph);
            columnContainer.add(div);

            document.add(columnContainer);
        }
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void continuousColumContainerSetWidth() throws IOException, InterruptedException {
        executeTest("continuousColumContainerSetWidth", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setWidth(300);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            Div pseudoContainer = new Div();
            for (int i = 0; i < 30; i++) {
                pseudoContainer.add(new Paragraph("" + i));
            }
            ctx.setBorder(DEFAULT_BORDER);
            ctx.add(pseudoContainer);
        });
    }

    @Test
    public void continuousColumContainerSetHeightBigger() throws IOException, InterruptedException {
        executeTest("continuousColumContainerSetHeightBigger", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setHeight(600);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
            ctx.setBorder(DEFAULT_BORDER);
        });
    }

    @Test
    public void widthBorderTest() throws IOException, InterruptedException {
        executeTest("widthBorderTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(new SolidBorder(ColorConstants.RED, 20));
            ctx.setWidth(300);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 100)));
        });
    }

    @Test
    public void heightBorderTest() throws IOException, InterruptedException {
        executeTest("heightBorderTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            //content should be clipped
            ctx.setHeight(150);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
            ctx.setBorder(new SolidBorder(ColorConstants.RED, 20));
        });
    }

    @Test
    public void widthPaddingTest() throws IOException, InterruptedException {
        executeTest("widthPaddingTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPadding(DEFAULT_PADDING);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setWidth(400);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 100)));
        });
    }

    @Test
    public void heightPaddingTest() throws IOException, InterruptedException {
        executeTest("heightPaddingTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            //content should be clipped
            ctx.setHeight(200);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
            ctx.setPadding(DEFAULT_PADDING);
            ctx.setBorder(DEFAULT_BORDER);
        });
    }


    @Test
    public void heightMarginTest() throws IOException, InterruptedException {
        executeTest("heightMarginTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            //content should be clipped
            ctx.setHeight(200);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
            ctx.setMargin(40);
            ctx.setBorder(DEFAULT_BORDER);
        });
    }


    @Test
    public void widthMarginTest() throws IOException, InterruptedException {
        executeTest("widthMarginTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMargin(40);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setWidth(400);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 100)));
        });
    }


    @Test
    public void widthHeightMarginTest() throws IOException, InterruptedException {
        executeTest("widthHeightMarginTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMargin(60);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setWidth(400);
            ctx.setHeight(400);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 100)));
        });
    }

    @Test
    public void minHeightTest() throws IOException, InterruptedException {
        executeTest("minHeightTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMargin(60);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMinHeight(200);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 10)));
        });
    }


    @Test
    public void maxHeightTest() throws IOException, InterruptedException {
        executeTest("maxHeightTest", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMargin(60);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMaxHeight(200);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 10)));
        });
    }

    @Test
    public void minWidth() throws IOException, InterruptedException {
        executeTest("minWidth", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMinWidth(200);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 200)));
        });
    }

    @Test
    public void minWidthBiggerThenPage() throws IOException, InterruptedException {
        executeTest("minWidthBiggerThenPage", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMinWidth(2000);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 200)));
        });
    }

    @Test
    public void maxWidth() throws IOException, InterruptedException {
        executeTest("maxWidth", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMaxWidth(200);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 200)));
        });
    }


    @Test
    public void widthMultiPage() throws IOException, InterruptedException {
        String testName = "widthMultiPage";
        String filename = DESTINATION_FOLDER + testName + ".pdf";
        String cmpName = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new com.itextpdf.kernel.pdf.PdfWriter(filename))) {
            Document doc = new Document(pdfDoc);

            MulticolContainer container = new MulticolContainer();
            container.setProperty(Property.COLUMN_COUNT, 3);
            container.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            container.setBorder(DEFAULT_BORDER);
            container.setWidth(400);
            container.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 150)));
            doc.add(new Paragraph("ELEMENT ABOVE").setHeight(600).setBackgroundColor(ColorConstants.YELLOW));
            doc.add(container);
            doc.add(new Paragraph("ELEMENT BELOW").setBackgroundColor(ColorConstants.YELLOW));
        }
        CompareTool compareTool = new CompareTool();
        Assert.assertNull(compareTool.compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Ignore("DEVSIX-7630")
    public void heightMultiPage() throws IOException, InterruptedException {
        String testName = "heightMultiPage";
        String filename = DESTINATION_FOLDER + testName + ".pdf";
        String cmpName = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new com.itextpdf.kernel.pdf.PdfWriter(filename))) {
            Document doc = new Document(pdfDoc);

            MulticolContainer container = new MulticolContainer();
            container.setProperty(Property.COLUMN_COUNT, 3);
            container.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            container.setBorder(DEFAULT_BORDER);
            container.setHeight(600);
            container.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 150)));
            doc.add(new Paragraph("ELEMENT ABOVE").setHeight(600).setBackgroundColor(ColorConstants.YELLOW));
            doc.add(container);
            doc.add(new Paragraph("ELEMENT BELOW").setBackgroundColor(ColorConstants.YELLOW));
        }
        CompareTool compareTool = new CompareTool();
        Assert.assertNull(compareTool.compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void continuousColumContainerSetHeightSmaller() throws IOException, InterruptedException {
        executeTest("continuousColumContainerSetHeightSmaller", new MulticolContainer(), ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            //content should be clipped
            ctx.setHeight(50);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
            ctx.setBorder(DEFAULT_BORDER);
        });
    }


    private <T extends IBlockElement> void executeTest(String testName, T container, Consumer<T> executor)
            throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + testName + ".pdf";
        String cmpName = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new com.itextpdf.kernel.pdf.PdfWriter(filename))) {
            Document doc = new Document(pdfDoc);

            executor.accept(container);

            doc.add(new Paragraph("ELEMENT ABOVE").setBackgroundColor(ColorConstants.YELLOW));
            doc.add(container);
            doc.add(new Paragraph("ELEMENT BELOW").setBackgroundColor(ColorConstants.YELLOW));
        }
        CompareTool compareTool = new CompareTool();
        Assert.assertNull(compareTool.compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    private static Div createFirstPageFiller() {
        Div firstPageFiller = new Div();
        firstPageFiller.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(50));
        firstPageFiller.setProperty(Property.BORDER, new SolidBorder(1));
        firstPageFiller.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(20));
        firstPageFiller.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        firstPageFiller.setProperty(Property.WIDTH, UnitValue.createPointValue(450));
        firstPageFiller.setProperty(Property.HEIGHT, UnitValue.createPointValue(650));
        return firstPageFiller;
    }
}
