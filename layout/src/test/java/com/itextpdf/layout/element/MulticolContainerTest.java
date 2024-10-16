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

import com.itextpdf.commons.utils.PlaceHolderTextUtil;
import com.itextpdf.commons.utils.PlaceHolderTextUtil.PlaceHolderTextBy;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
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
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Background;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class MulticolContainerTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/MulticolContainerTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/MulticolContainerTest/";

    private static final float DEFAULT_PADDING = 40F;
    private static final float DEFAULT_MARGIN = 100F;
    private static final Color DEFAULT_BACKGROUND_COLOR = ColorConstants.CYAN;
    private static final Border DEFAULT_BORDER = new SolidBorder(ColorConstants.RED, 5F);

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void paragraphColumnContainerTest() throws IOException, InterruptedException {
        executeTest("paragraphColumnContainerTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                    "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                    "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                    "id est laborum.");
            ctx.add(paragraph);
        }, false);
    }

    @Test
    public void divColumnContainerTest() throws IOException, InterruptedException {
        executeTest("divColumnContainerTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 2);
            Div div = new Div();
            div.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(50));
            div.setBorder(new SolidBorder(2));
            div.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(40));
            div.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
            div.setProperty(Property.WIDTH, UnitValue.createPointValue(450));
            div.setProperty(Property.HEIGHT, UnitValue.createPointValue(500));
            ctx.add(div);
        }, false);
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
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void continuousColumContainerParagraphMarginTopBottom() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphMarginTopBottom", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 2);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMarginTop(DEFAULT_MARGIN * 1.25F);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
        });
    }

    @Test
    public void continuousColumContainerParagraphPaddingTopBottom() throws IOException, InterruptedException {
        executeTest("continuousColumContainerPaddingTopBottom", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setPaddingBottom(DEFAULT_PADDING * 2F);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
        });
    }

    @Test
    public void continuousColumContainerParagraphBorder() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphBorder", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
        });
    }


    @Test
    public void continuousColumContainerParagraphAll() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphAll", ctx -> {
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
        executeTest("continuousColumContainerParagraphAllChildStart", ctx -> {
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
        executeTest("continuousColumContainerParagraphAllChildEnd", ctx -> {
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

    @Test
    public void continuousColumContainerParagraphOverflowShouldShow() throws IOException, InterruptedException {
        executeTest("continuousColumContainerParagraphOverflowShouldShow", ctx -> {
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

    @Test
    public void extraLargeColumnParagraphTest() throws IOException, InterruptedException {
        executeTest("extraLargeColumnParagraphTest", ctx -> {
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

    @Test
    public void largeColumnParagraphWithMarginTest() throws IOException, InterruptedException {
        executeTest("largeColumnParagraphWithMarginTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setMarginTop(DEFAULT_MARGIN);
            ctx.setMarginBottom(DEFAULT_MARGIN);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 8000)));
        });
    }

    @Test
    public void largeColumnParagraphWithPaddingTest() throws IOException, InterruptedException {
        executeTest("largeColumnParagraphWithPaddingTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setPaddingTop(DEFAULT_PADDING);
            ctx.setPaddingBottom(DEFAULT_PADDING);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 8000)));
        });
    }

    @Test
    public void largeColumnParagraphWithBorderTest() throws IOException, InterruptedException {
        executeTest("largeColumnParagraphWithBorderTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(new SolidBorder(ColorConstants.GREEN, 50));
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 8000)));
        });
    }

    @Test
    public void continuousColumContainerMultipleElementsMarginTop() throws IOException, InterruptedException {
        executeTest("continuousColumContainerMultipleElementsMarginTop", ctx -> {
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
        executeTest("continuousColumContainerMultipleElementsMarginBottom", ctx -> {
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
        executeTest("continuousColumContainerInnerBackgroundColor", ctx -> {
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
        executeTest("continuousColumContainerMultipleElementsPaddingTop", ctx -> {
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
        executeTest("continuousColumContainerMultipleElementsPaddingBottom", ctx -> {
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
        executeTest("continuousColumContainerMultipleElementsBorder", ctx -> {
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
        executeTest("multicolElementWithKeepTogether", ctx -> {
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
        executeTest("allChildrenOfMulticolElementWithKeepTogether", ctx -> {
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
        executeTest("childOfMulticolElementWithKeepTogether", ctx -> {
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
        executeTest("childrenOfMulticolElementWithKeepTogether", ctx -> {
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
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
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
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
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
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void overflowImageBetweenParagraphsTest() throws IOException, InterruptedException {
        executeTest("overflowImageBetweenParagraphsTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            Image image = createImage(SOURCE_FOLDER + "placeholder_100x100.png", 200);
            Paragraph paragraph2 = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph2.setBorder(new SolidBorder(ColorConstants.BLUE, 2));

            ctx.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(paragraph);
            div.add(image);
            div.add(paragraph2);
            ctx.add(div);
        }, false);
    }

    @Test
    public void overflowingImageWithParagraphTest() throws IOException, InterruptedException {
        executeTest("overflowingImageWithParagraphMultipageTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            Image image = createImage(SOURCE_FOLDER + "placeholder_100x100.png", 200);

            ctx.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(image);
            div.add(paragraph);
            ctx.add(div);
        }, false);
    }

    @Test
    public void overflowImageWithForcedPlacementTest() throws IOException, InterruptedException {
        executeTest("overflowImageWithForcedPlacementTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            Image image = createImage(SOURCE_FOLDER + "placeholder_100x100.png", 200);
            image.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
            Paragraph paragraph2 = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph2.setBorder(new SolidBorder(ColorConstants.BLUE, 2));

            ctx.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(paragraph);
            div.add(image);
            div.add(paragraph2);
            ctx.add(div);
        }, false);
    }

    @Test
    public void imageForcedPlacementAndKeepTogetherTest() throws IOException, InterruptedException {
        executeTest("imageForcedPlacementAndKeepTogetherTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(ColorConstants.GREEN, 2));

            Image image = createImage(SOURCE_FOLDER + "placeholder_100x100.png", 200);
            image.setProperty(Property.FORCED_PLACEMENT, Boolean.TRUE);
            Paragraph paragraph2 = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph2.setBorder(new SolidBorder(ColorConstants.BLUE, 2));
            Div child = new Div();
            child.setBorder(new SolidBorder(ColorConstants.BLACK, 2));
            child.setKeepTogether(true);
            child.add(image);
            child.add(paragraph2);

            ctx.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(paragraph);
            div.add(child);
            ctx.add(div);
        }, false);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void imageBiggerThanPageTest() throws IOException, InterruptedException {
        executeTest("imageBiggerThanPageTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);

            Paragraph paragraph = new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, ");
            paragraph.setBorder(new SolidBorder(2));
            Image image = createImage(SOURCE_FOLDER + "placeholder_100x100.png", 800);

            ctx.setBorder(new SolidBorder(ColorConstants.RED, 3));
            Div div = new Div();
            div.add(image);
            div.add(paragraph);
            ctx.add(div);
        }, false);
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
            columnDiv.setBorder(new SolidBorder(1));
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
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
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
            columnDiv.setBorder(new SolidBorder(1));
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
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
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
            columnDiv.setBorder(new SolidBorder(1));
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
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
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
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void continuousColumContainerSetWidth() throws IOException, InterruptedException {
        executeTest("continuousColumContainerSetWidth", ctx -> {
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
        executeTest("continuousColumContainerSetHeightBigger", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setHeight(600);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
            ctx.setBorder(DEFAULT_BORDER);
        });
    }

    @Test
    public void widthBorderTest() throws IOException, InterruptedException {
        executeTest("widthBorderTest", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(new SolidBorder(ColorConstants.RED, 20));
            ctx.setWidth(300);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 100)));
        });
    }

    @Test
    public void heightBorderTest() throws IOException, InterruptedException {
        executeTest("heightBorderTest", ctx -> {
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
        executeTest("widthPaddingTest", ctx -> {
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
        executeTest("heightPaddingTest", ctx -> {
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
        executeTest("heightMarginTest", ctx -> {
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
        executeTest("widthMarginTest", ctx -> {
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
        executeTest("widthHeightMarginTest", ctx -> {
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
        executeTest("minHeightTest", ctx -> {
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
        executeTest("maxHeightTest", ctx -> {
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
        executeTest("minWidth", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMinWidth(200);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 200)));
        });
    }

    @Test
    public void minWidthBiggerThenPage() throws IOException, InterruptedException {
        executeTest("minWidthBiggerThenPage", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.setBorder(DEFAULT_BORDER);
            ctx.setMinWidth(2000);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 200)));
        });
    }

    @Test
    public void maxWidth() throws IOException, InterruptedException {
        executeTest("maxWidth", ctx -> {
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
        Assertions.assertNull(compareTool.compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    @Disabled("DEVSIX-7630")
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
        Assertions.assertNull(compareTool.compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void continuousColumContainerSetHeightSmaller() throws IOException, InterruptedException {
        executeTest("continuousColumContainerSetHeightSmaller", ctx -> {
            ctx.setProperty(Property.COLUMN_COUNT, 3);
            //content should be clipped
            ctx.setHeight(50);
            ctx.setBackgroundColor(DEFAULT_BACKGROUND_COLOR);
            ctx.add(new Paragraph(PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, 400)));
            ctx.setBorder(DEFAULT_BORDER);
        });
    }

    @Test
    public void paragraphWithColumnWidthTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithColumnWidthTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithColumnWidthTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_WIDTH, 200.0f);
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        //expecting 2 columns with ~260px width each
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void paragraphWithColumnWidthAndColumnCountTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithColumnWidthAndColumnCountTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithColumnWidthAndColumnCountTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            //column width is ignored in this case, because column-count requires higher width
            columnContainer.setProperty(Property.COLUMN_WIDTH, 100.0f);
            columnContainer.setProperty(Property.COLUMN_COUNT, 2);
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void paragraphWithInvalidColumnValuesTest() {

        try (Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            Div columnContainer = new MulticolContainer();
            //column width is ignored in this case, because column-count requires higher width
            columnContainer.setProperty(Property.COLUMN_WIDTH, -30.0f);
            columnContainer.setProperty(Property.COLUMN_COUNT, -2);
            columnContainer.setProperty(Property.COLUMN_GAP, -20.0f);
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            Throwable exception = Assertions.assertThrows(IllegalStateException.class, () -> document.add(columnContainer));
            Assertions.assertEquals(LayoutExceptionMessageConstant.INVALID_COLUMN_PROPERTIES, exception.getMessage());
        }
    }

    @Test
    public void paragraphWithColumnWidthAndGapTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithColumnWidthAndGapTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithColumnWidthAndGapTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_WIDTH, 100.0f);
            columnContainer.setProperty(Property.COLUMN_GAP, 100.0f);
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void paragraphWithColumnCountAndGapTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithColumnCountAndGapTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithColumnCountAndGapTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 5);
            columnContainer.setProperty(Property.COLUMN_GAP, 50.0f);
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void paragraphWithSimpleSolidColumnGapTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithSimpleStyledColumnGapTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithSimpleStyledColumnGapTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 5);
            columnContainer.setProperty(Property.COLUMN_GAP, 50.0f);
            columnContainer.setProperty(Property.COLUMN_GAP_BORDER, new SolidBorder(50));
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }


    @Test
    public void divWithSimpleSolidColumnGapTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "divWithSimpleStyledColumnGapTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_divWithSimpleStyledColumnGapTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 5);
            columnContainer.setProperty(Property.COLUMN_GAP, 50.0f);
            columnContainer.setProperty(Property.COLUMN_GAP_BORDER, new SolidBorder(50));
            Div div = new Div();
            for (int i = 0; i < 20; i++) {
                Paragraph paragraph = new Paragraph("Hello world! " + i);
                div.add(paragraph);
            }
            columnContainer.add(div);
            document.add(columnContainer);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void paragraphWithNegativeValueSolidColumnGapTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithNegativeValueSolidColumnGapTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithNegativeValueSolidColumnGapTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 5);
            columnContainer.setProperty(Property.COLUMN_GAP_BORDER, new SolidBorder(0));
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }

    @Test
    public void paragraphWithBiggerValueSolidColumnGapTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithBiggerValueSolidColumnGapTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithBiggerValueSolidColumnGapTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 5);
            columnContainer.setProperty(Property.COLUMN_GAP_BORDER, new SolidBorder(600));
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }


    @Test
    public void paragraphWithNullValueSolidColumnGapTest() throws IOException, InterruptedException {
        String outFileName = DESTINATION_FOLDER + "paragraphWithNullValueSolidColumnGapTest.pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_paragraphWithNullValueSolidColumnGapTest.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outFileName)))) {
            Div columnContainer = new MulticolContainer();
            columnContainer.setProperty(Property.COLUMN_COUNT, 5);
            columnContainer.setProperty(Property.COLUMN_GAP_BORDER, null);
            Paragraph paragraph = createDummyParagraph();
            columnContainer.add(paragraph);
            document.add(columnContainer);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff"));
    }
    private void executeTest(String testName, Consumer<MulticolContainer> executor, boolean wrapByP)
            throws IOException, InterruptedException {
        String filename = DESTINATION_FOLDER + testName + ".pdf";
        String cmpName = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        try (PdfDocument pdfDoc = new PdfDocument(new com.itextpdf.kernel.pdf.PdfWriter(filename))) {
            Document doc = new Document(pdfDoc);

            MulticolContainer container = new MulticolContainer();
            executor.accept(container);

            if (wrapByP) {
                doc.add(new Paragraph("ELEMENT ABOVE").setBackgroundColor(ColorConstants.YELLOW));
            }
            doc.add(container);
            if (wrapByP) {
                doc.add(new Paragraph("ELEMENT BELOW").setBackgroundColor(ColorConstants.YELLOW));
            }
        }
        CompareTool compareTool = new CompareTool();
        Assertions.assertNull(compareTool.compareByContent(filename, cmpName, DESTINATION_FOLDER, "diff_"));
    }

    private void executeTest(String testName, Consumer<MulticolContainer> executor) throws IOException, InterruptedException {
       executeTest(testName, executor, true);
    }

    private Image createImage(String path, float width) {
        PdfImageXObject xObject = null;
        try {
            xObject = new PdfImageXObject(ImageDataFactory.create(path));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return new Image(xObject, width);
    }

    private static Paragraph createDummyParagraph() {
        return new Paragraph("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute " +
                "irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim " +
                "id est laborum.");
    }

    private static Div createFirstPageFiller() {
        Div firstPageFiller = new Div();
        firstPageFiller.setProperty(Property.MARGIN_TOP, UnitValue.createPointValue(50));
        firstPageFiller.setBorder(new SolidBorder(1));
        firstPageFiller.setProperty(Property.PADDING_LEFT, UnitValue.createPointValue(20));
        firstPageFiller.setProperty(Property.BACKGROUND, new Background(ColorConstants.LIGHT_GRAY));
        firstPageFiller.setProperty(Property.WIDTH, UnitValue.createPointValue(450));
        firstPageFiller.setProperty(Property.HEIGHT, UnitValue.createPointValue(650));
        return firstPageFiller;
    }
}
