/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.layout.testutil;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.ColumnDocumentRenderer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.layout.property.OverflowPropertyValue;
import com.itextpdf.layout.property.ParagraphOrphansControl;
import com.itextpdf.layout.property.ParagraphWidowsControl;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.ParagraphRenderer;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class OrphansWidowsTestUtil {

    public static String PARA_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
            + "tempor incididunt ut labore et dolore magna aliqua. Nulla at volutpat diam ut "
            + "venenatis tellus in. Orci porta non pulvinar neque laoreet suspendisse interdum "
            + "consectetur. Ipsum dolor sit amet consectetur adipiscing. Id porta nibh venenatis"
            + " cras sed felis eget velit. Sapien nec sagittis aliquam malesuada. Cras sed felis"
            + " eget velit aliquet sagittis. Leo a diam sollicitudin tempor id eu nisl nunc."
            + " Faucibus a pellentesque sit amet porttitor eget dolor morbi. Nisl vel pretium"
            + " lectus quam id leo in vitae. Vehicula ipsum a arcu cursus vitae. Tincidunt praesent"
            + " semper feugiat nibh sed pulvinar proin gravida hendrerit. Nisl vel pretium lectus"
            + " quam id leo in vitae turpis. Quis hendrerit dolor magna eget est lorem. Diam sit"
            + " amet nisl suscipit adipiscing bibendum est ultricies. Ultricies mi eget mauris pharetra."
            + " Etiam dignissim diam quis enim. Felis bibendum ut tristique et egestas quis.";

    public static final float LINES_SPACE_EPS = 5;

    public static void produceOrphansWidowsTestCase(String outPdf, int linesLeft, boolean orphans, Paragraph testPara,
            boolean applyMarginsOnTestPara) throws FileNotFoundException {
        PageSize pageSize = new PageSize(PageSize.A4.getWidth(), PageSize.A5.getHeight());
        Document doc = new Document(new PdfDocument(new PdfWriter(outPdf)), pageSize);

        Rectangle[] columns = initUniformColumns(pageSize, 2);
        doc.setRenderer(new ColumnDocumentRenderer(doc, columns));

        setParagraphStylingProperties(testPara, applyMarginsOnTestPara);
        testPara.add(PARA_TEXT);

        float linesHeight = calculateHeightForLinesNum(doc, testPara, columns[0].getWidth(), linesLeft, orphans);

        String descriptionIntro = "Test " + (orphans ? "orphans" : "widows") + ". ";
        String descriptionBeg = "This block height is adjusted in such way as to leave ";
        String descriptionEnd = " line(s) on area break. Reference example without orphans/widows control can be found on the next page.";
        float adjustmentHeight = columns[0].getHeight() - linesHeight - LINES_SPACE_EPS;
        doc.add(new Paragraph()
                .add(new Text(descriptionIntro).setFontColor(ColorConstants.RED))
                .add(new Text(descriptionBeg).setFontSize(8))
                .add(new Text(String.valueOf(linesLeft)).setFontColor(ColorConstants.RED))
                .add(new Text(descriptionEnd).setFontSize(8))
                .setMargin(0)
                .setBorder(new SolidBorder(1))
                .setHeight(adjustmentHeight));
        if (!applyMarginsOnTestPara) {
            doc.add(testPara);
        } else {
            Div div = new Div().add(testPara).setMarginTop(15);
            div.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            doc.add(div);
        }

        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        doc.add(new Paragraph("Reference example without orphans/widows control.")
                .setMargin(0)
                .setBorder(new SolidBorder(1))
                .setHeight(adjustmentHeight));

        if (!applyMarginsOnTestPara) {
            doc.add(setParagraphStylingProperties(new Paragraph(PARA_TEXT), false));
        } else {
            Div div = new Div().add(setParagraphStylingProperties(new Paragraph(PARA_TEXT), true)).setMarginTop(15);
            div.setProperty(Property.COLLAPSING_MARGINS, Boolean.TRUE);
            doc.add(div);
        }
        doc.close();
    }

    public static void produceOrphansWidowsAndMaxHeightLimitTestCase(String outPdf, boolean orphans)
            throws FileNotFoundException {
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf)));
        singleMaxHeightCase(document, orphans, false);
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        singleMaxHeightCase(document, orphans, true);
        document.close();
    }

    public static void produceOrphansWidowsOnCanvasOfLimitedSizeTestCase(String outPdf, boolean orphans)
            throws FileNotFoundException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outPdf));
        Document document = new Document(pdfDocument);
        String orphansOrWidows = orphans ? "orphans" : "widows";

        Paragraph paraOnCanvas = setParagraphStylingProperties(new Paragraph(PARA_TEXT), false);
        Rectangle effectiveArea = document.getPageEffectiveArea(pdfDocument.getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight = calculateHeightForLinesNum(document, paraOnCanvas, effectiveArea.getWidth(),
                minOrphansOrWidows - 1, orphans);
        if (orphans) {
            paraOnCanvas.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            paraOnCanvas.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, false));
        }

        String description = "The paragraph beneath has property " + orphansOrWidows.toUpperCase() + "_CONTROL,"
                + " limiting the number of allowed " + orphansOrWidows + " to 3. "
                + "The size of canvas is limited so that the lines that" + (orphans ? " " : " don't ")
                + "fit in the canvas cause " + orphansOrWidows + " violation. "
                + "The entire canvas area is filled in magenta.";
        singleLimitedCanvasSizeCase(document, paraOnCanvas, description, linesHeight, 1);
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        paraOnCanvas.deleteOwnProperty(orphans ? Property.ORPHANS_CONTROL : Property.WIDOWS_CONTROL);
        description = "The paragraph beneath has no " + orphansOrWidows.toUpperCase() + "_CONTROL, property.";
        singleLimitedCanvasSizeCase(document, paraOnCanvas, description, linesHeight, 2);
        document.close();
    }

    public static void produceOrphansWidowsWithinDivOfLimitedSizeTestCase(String outPdf, boolean orphans)
            throws FileNotFoundException {
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf)));
        String orphansOrWidows = orphans ? "orphans" : "widows";
        Paragraph testDescription = new Paragraph().setBorder(new SolidBorder(ColorConstants.RED, 1));
        testDescription.add("The paragraph beneath has property " + orphansOrWidows.toUpperCase() + "_CONTROL,"
                + " limiting the number of allowed " + orphansOrWidows + " to 3. "
                + "The size of div-wrapper of the paragraph is limited so that the lines that"
                + (orphans ? " " : " don't ") + "fit in the canvas cause " + orphansOrWidows + " violation. ");
        document.add(testDescription);

        Paragraph paragraph = setParagraphStylingProperties(new Paragraph(PARA_TEXT), false);
        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight = calculateHeightForLinesNum(document, paragraph, effectiveArea.getWidth(),
                minOrphansOrWidows - 1, orphans);
        if (orphans) {
            paragraph.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            paragraph.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, false));
        }
        Div divOfLimitedSize = new Div().add(paragraph);
        divOfLimitedSize.setHeight(linesHeight + LINES_SPACE_EPS).setBackgroundColor(ColorConstants.MAGENTA);
        document.add(divOfLimitedSize);
        document.close();
    }

    public static void produceOrphansWidowsKeepTogetherTestCase(String outPdf, boolean orphans, boolean large)
            throws FileNotFoundException {
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf)));
        Paragraph paragraph = new Paragraph(PARA_TEXT).setMargin(0).setBackgroundColor(new DeviceRgb(232, 232, 232));
        if (large) {
            paragraph.add(PARA_TEXT).add(PARA_TEXT).add(PARA_TEXT).add(PARA_TEXT);
        }
        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight;
        if (!large || orphans) {
            linesHeight = calculateHeightForLinesNum(document, paragraph, effectiveArea.getWidth(),
                    minOrphansOrWidows - 1, orphans);
        } else {
            linesHeight = calculateHeightForLinesNumKeepTogetherCaseSpecific(document, paragraph,
                    effectiveArea.getWidth(), effectiveArea.getHeight(), minOrphansOrWidows - 1);
        }
        if (orphans) {
            paragraph.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            paragraph.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, true));
        }
        paragraph.setKeepTogether(true);

        String orphansOrWidows = orphans ? "orphans" : "widows";
        Paragraph testDescription = new Paragraph().setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.RED, 1))
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS);
        testDescription.add("The paragraph beneath has property " + orphansOrWidows.toUpperCase() + "_CONTROL,"
                + " limiting the number of allowed " + orphansOrWidows + " to 3. "
                + "The paragraph has also KEEP_TOGETHER property. The size of this description-paragraph is defined so"
                + " that " + orphansOrWidows + " violation " + (large ? "occurs."
                : "should have occurred if not for KEEP_TOGETHER."));
        document.add(testDescription);
        document.add(paragraph);
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        String referencePagesDescription;
        if (large) {
            referencePagesDescription = "The paragraph beneath has KEEP_TOGETHER property "
                    + "and no " + orphansOrWidows.toUpperCase() + "_CONTROL property.";
        } else {
            referencePagesDescription = "The paragraph beneath has neither KEEP_TOGETHER property nor "
                    + orphansOrWidows.toUpperCase() + "_CONTROL property.";
        }
        document.add(new Paragraph(referencePagesDescription)
                .setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.RED, 1))
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS));
        paragraph.deleteOwnProperty(orphans ? Property.ORPHANS_CONTROL : Property.WIDOWS_CONTROL);
        if (!large) {
            paragraph.deleteOwnProperty(Property.KEEP_TOGETHER);
        }
        document.add(paragraph);
        document.close();
    }

    public static void produceOrphansWidowsInlineImageTestCase(String outPdf, String imagePath, boolean orphans)
            throws FileNotFoundException, MalformedURLException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outPdf));
        Document document = new Document(pdfDocument);
        Image img = new Image(ImageDataFactory.create(imagePath));
        singleInlineImageCase(document, img, orphans, true);
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        singleInlineImageCase(document, img, orphans, false);
        document.close();
    }

    public static void produceOrphansWidowsHugeInlineImageTestCase(String outPdf, String imagePath, boolean orphans)
            throws FileNotFoundException, MalformedURLException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outPdf));
        Document document = new Document(pdfDocument);
        Image img = new Image(ImageDataFactory.create(imagePath));
        String text = "Just two lines\nJust two lines\n";
        Paragraph paragraph = new Paragraph().setMargin(0).setBackgroundColor(new DeviceRgb(232, 232, 232));
        if (orphans) {
            paragraph.add(text).add(img);
        } else {
            paragraph.add(img).add(text);
        }

        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        if (orphans) {
            paragraph.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            paragraph.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, false));
        }

        String orphansOrWidows = orphans ? "orphans" : "widows";
        Paragraph testDescription = new Paragraph("The paragraph beneath has " + orphansOrWidows.toUpperCase()
                + "_CONTROL property, limiting the number of allowed " + orphansOrWidows
                + " to 3. Huge image is part of the paragraph.")
                .setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.RED, 1))
                .setHeight(effectiveArea.getHeight() / 2);
        document.add(testDescription);
        document.add(paragraph);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        testDescription = new Paragraph("The paragraph beneath has no " + orphansOrWidows.toUpperCase()
                + "_CONTROL property. Huge image is part of the paragraph.")
                .setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.RED, 1))
                .setHeight(effectiveArea.getHeight() / 2);
        document.add(testDescription);
        paragraph.deleteOwnProperty(orphans ? Property.ORPHANS_CONTROL : Property.WIDOWS_CONTROL);
        document.add(paragraph);

        document.close();
    }

    public static void produceOrphansWidowsInlineBlockTestCase(String outPdf, boolean orphans)
            throws FileNotFoundException {
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf)));

        Paragraph inlineBlockParagraph = setParagraphStylingProperties(new Paragraph(OrphansWidowsTestUtil.PARA_TEXT),
                false);
        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight = calculateHeightForLinesNum(document, inlineBlockParagraph,
                effectiveArea.getWidth(), minOrphansOrWidows - 1, orphans);
        String orphansOrWidows = orphans ? "orphans" : "widows";
        document.add(new Paragraph(
                "The paragraph beneath has property " + orphansOrWidows + "_CONTROL, limiting the number of allowed "
                        + orphansOrWidows + " to 3.")
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));
        Paragraph outerParagraph = new Paragraph().setMargin(0).setBorder(new SolidBorder(ColorConstants.RED, 1));
        outerParagraph.add(inlineBlockParagraph);
        if (orphans) {
            inlineBlockParagraph.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            inlineBlockParagraph.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, false));
        }
        document.add(outerParagraph);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        document.add(new Paragraph(
                "The paragraph beneath has no " + orphansOrWidows + "_CONTROL property, limiting the number of allowed "
                        + orphansOrWidows + " to 3.")
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));
        inlineBlockParagraph.deleteOwnProperty(orphans ? Property.ORPHANS_CONTROL : Property.WIDOWS_CONTROL);
        document.add(outerParagraph);

        document.close();
    }

    public static void produceOrphansWidowsInlineFloatTestCase(String outPdf, boolean orphans)
            throws FileNotFoundException {
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf)));

        Paragraph inlineFloatParagraph = setParagraphStylingProperties(new Paragraph(OrphansWidowsTestUtil.PARA_TEXT),
                false);
        float floatingParaWidth = 200;

        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight = calculateHeightForLinesNum(document, inlineFloatParagraph, floatingParaWidth,
                minOrphansOrWidows - 1, orphans);
        String orphansOrWidows = orphans ? "orphans" : "widows";
        document.add(new Paragraph("The paragraph on the right has property " + orphansOrWidows + "_CONTROL, "
                + "limiting the number of allowed " + orphansOrWidows + " to 3. "
                + "It's also floating to the right and has fixed width.")
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));
        if (orphans) {
            inlineFloatParagraph.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            inlineFloatParagraph.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, false));
        }
        inlineFloatParagraph.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);
        inlineFloatParagraph.setWidth(floatingParaWidth - 2);
        Paragraph placeholder = new Paragraph("This is just a placeholder")
                .setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.RED, 1));
        document.add(inlineFloatParagraph);
        document.add(placeholder);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        document.add(new Paragraph("The paragraph on the right has no " + orphansOrWidows + "_CONTROL,  property "
                + "limiting the number of allowed " + orphansOrWidows + " to 3. "
                + "It's also floating to the right and has fixed width.")
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));
        inlineFloatParagraph.deleteOwnProperty(orphans ? Property.ORPHANS_CONTROL : Property.WIDOWS_CONTROL);
        document.add(inlineFloatParagraph);
        document.add(placeholder);

        document.close();
    }

    public static void produceOrphansWidowsFloatingDivTestCase(String outPdf, boolean orphans)
            throws FileNotFoundException {
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf)));

        Paragraph paraInFloatingDiv = setParagraphStylingProperties(new Paragraph(OrphansWidowsTestUtil.PARA_TEXT),
                false);
        float floatingDivWidth = 200;

        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight = calculateHeightForLinesNum(document, paraInFloatingDiv, floatingDivWidth,
                minOrphansOrWidows - 1, orphans);
        String orphansOrWidows = orphans ? "orphans" : "widows";
        document.add(new Paragraph("The div on the right has a paragraph child, that has property "
                + orphansOrWidows + "_CONTROL, limiting the number of allowed " + orphansOrWidows + " to 3. "
                + "The div is floating to the right and has fixed width.")
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));
        if (orphans) {
            paraInFloatingDiv.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            paraInFloatingDiv.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, false));
        }
        Div floatingDiv = new Div().add(paraInFloatingDiv)
                .setMargin(0)
                .setWidth(floatingDivWidth - 2);
        floatingDiv.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        Paragraph placeholder = new Paragraph("This is just a placeholder")
                .setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.RED, 1));
        document.add(floatingDiv);
        document.add(placeholder);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        document.add(new Paragraph("The div on the right has a paragraph child, that has property, that has no "
                + orphansOrWidows + "_CONTROL, limiting the number of allowed " + orphansOrWidows + " to 3."
                + "The div is floating to the right and has fixed width.")
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));
        paraInFloatingDiv.deleteOwnProperty(orphans ? Property.ORPHANS_CONTROL : Property.WIDOWS_CONTROL);
        document.add(floatingDiv);
        document.add(placeholder);

        document.close();
    }

    public static void produceOrphansWidowsBiggerThanLinesCountTestCase(String outPdf, boolean orphans,
            boolean singleLine) throws FileNotFoundException {
        Document document = new Document(new PdfDocument(new PdfWriter(outPdf)));

        Paragraph smallParagraph = setParagraphStylingProperties(new Paragraph(), false);
        if (singleLine) {
            smallParagraph.add("Single line!");
        } else {
            smallParagraph.add("First line!\nSecond line!");
        }

        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight = calculateHeightForLinesNum(document, smallParagraph, effectiveArea.getWidth(),
                minOrphansOrWidows - 2, orphans);
        String orphansOrWidows = orphans ? "orphans" : "widows";
        document.add(new Paragraph("The paragraph beneath has only " + (singleLine ? "one line" : "two lines")
                + " and property " + orphansOrWidows + "_CONTROL,"
                + " limiting the number of allowed " + orphansOrWidows + " to 3.")
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));
        if (orphans) {
            smallParagraph.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            smallParagraph.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 2, false));
        }
        document.add(smallParagraph);
        document.close();
    }

    public static void produceOrphansWidowsUnexpectedWidthOfNextAreaTestCase(String outPdf, boolean widerNextPage)
            throws FileNotFoundException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outPdf));
        Document document = new Document(pdfDocument);
        pdfDocument.addNewPage();
        pdfDocument.addNewPage(widerNextPage ? PageSize.A2 : PageSize.A6);
        pdfDocument.addNewPage();

        Paragraph smallParagraph = setParagraphStylingProperties(new Paragraph(PARA_TEXT), false);

        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        float linesHeight = calculateHeightForLinesNum(document, smallParagraph, effectiveArea.getWidth(),
                3, false);
        String descriptionPara = "The paragraph beneath has property WIDOWS_CONTROL set to (6, 3, false)."
                + " The widows are resolved as if the next page had the same effective area as this one."
                + (widerNextPage ? " In fact the following page is wider, which is why the widows aren't fixed,"
                + " some lines are moved for no reason and no violation report is logged." : " In fact"
                + " the following page is narrower, which is why the widows shouldn't be fixed in the first place. "
                + "As a result some lines are moved for no reason.");
        document.add(new Paragraph(descriptionPara)
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));

        smallParagraph.setWidowsControl(new ParagraphWidowsControl(6, 3, false));
        document.add(smallParagraph);

        document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        String referencePagePara = "This is a reference page illustrating how" + (widerNextPage ? " widows are"
                + " resolved when the page's effective area remains the same" : " it would look like"
                + " if widows \"violation\" hadn't been resolved: there's actually no violation.");
        document.add(new Paragraph(referencePagePara)
                .setMargin(0)
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS)
                .setBorder(new SolidBorder(1)));

        if (!widerNextPage) {
            smallParagraph.deleteOwnProperty(Property.WIDOWS_CONTROL);
            pdfDocument.addNewPage(PageSize.A6);
        }
        document.add(smallParagraph);

        document.close();
    }

    public static void produceOrphansOrWidowsTestCase(String outPdf, int linesLeft, boolean orphans,
            Paragraph testPara) throws FileNotFoundException {
        Document doc = new Document(new PdfDocument(new PdfWriter(outPdf)));

        PageSize pageSize = new PageSize(PageSize.A4.getWidth(), PageSize.A5.getHeight());
        doc.getPdfDocument().setDefaultPageSize(pageSize);

        testPara.setMargin(0).setBackgroundColor(new DeviceRgb(232, 232, 232));
        testPara.add(PARA_TEXT);

        String orphansOrWidows = orphans ? "orphans" : "widows";
        String description = "Test " + orphansOrWidows + ".\n" + " This block height is adjusted in"
                + " such way as to leave " + (String.valueOf(linesLeft)) + " line(s) on area break.\n"
                + " Configuration is identified by the file name.\n Reference example"
                + " without " + orphansOrWidows + " control can be found on the next page.";

        float effectiveWidth;
        float effectiveHeight;

        doc.setRenderer(new DocumentRenderer(doc));

        Rectangle effectiveArea = doc.getPageEffectiveArea(pageSize);
        effectiveWidth = effectiveArea.getWidth();
        effectiveHeight = effectiveArea.getHeight();

        float linesHeight = calculateHeightForLinesNum(doc, testPara, effectiveWidth, linesLeft, orphans);
        float adjustmentHeight = effectiveHeight - linesHeight - LINES_SPACE_EPS;

        doc.add(new Paragraph()
                .add(new Text(description))
                .setMargin(0)
                .setBorder(new SolidBorder(1))
                .setHeight(adjustmentHeight));

        doc.add(testPara);
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        doc.add(new Paragraph("Reference example without orphans/widows control.")
                .setMargin(0)
                .setBorder(new SolidBorder(1))
                .setHeight(adjustmentHeight));

        doc.add(new Paragraph(PARA_TEXT).setMargin(0).setBackgroundColor(new DeviceRgb(232, 232, 232)));

        doc.close();
    }

    public static void produceOrphansAndWidowsTestCase(String outPdf, Paragraph testPara) throws FileNotFoundException {
        Document doc = new Document(new PdfDocument(new PdfWriter(outPdf)));

        PageSize pageSize = new PageSize(PageSize.A4.getWidth(), PageSize.A5.getHeight());
        doc.getPdfDocument().setDefaultPageSize(pageSize);

        Rectangle[] columns = initUniformColumns(pageSize, 2);
        doc.setRenderer(new ColumnDocumentRenderer(doc, columns));

        String paraText = "A one line string\n";

        testPara.setMargin(0).setBackgroundColor(new DeviceRgb(232, 232, 232));
        testPara.add(paraText);

        float linesHeight = calculateHeightForLinesNum(doc, testPara, columns[1].getWidth(), 1, true);
        float adjustmentHeight = columns[0].getHeight() - linesHeight - LINES_SPACE_EPS;

        String description = "Test orphans and widows case at once. This block height"
                + " is adjusted in such way that both orphans and widows cases occur.\n "
                + "The following paragraph contains as many fitting in one line text strings as needed"
                + " to reproduce the case with both orphans and widows\n"
                + "Reference example without orphans and widows"
                + " control can be found on the next page";

        doc.add(new Paragraph(description)
                .setMargin(0)
                .setBorder(new SolidBorder(1))
                .setHeight(adjustmentHeight));

        Paragraph tempPara = new Paragraph().setMargin(0);
        for (int i = 0; i < 50; i++) {
            tempPara.add(paraText);
        }

        ParagraphRenderer renderer = (ParagraphRenderer) tempPara.createRendererSubTree().setParent(doc.getRenderer());
        LayoutResult layoutRes = renderer.layout(new LayoutContext(new LayoutArea
                (1, new Rectangle( columns[1].getWidth(), columns[1].getHeight()))));
        int numberOfLines = ((ParagraphRenderer) layoutRes.getSplitRenderer()).getLines().size();
        for (int i = 0; i <= numberOfLines; i++) {
            testPara.add(paraText);
        }
        doc.add(testPara);
        doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

        doc.add(new Paragraph("Reference example without orphans and widows control.")
                .setMargin(0)
                .setBorder(new SolidBorder(1))
                .setHeight(adjustmentHeight));

        Paragraph paragraph = new Paragraph();
        for (int i = 0; i <= numberOfLines + 1; i++) {
            paragraph.add(paraText);
        }
        paragraph.setMargin(0).setBackgroundColor(new DeviceRgb(232, 232, 232));
        doc.add(paragraph);

        doc.add(new Paragraph(paraText).setMargin(0).setBackgroundColor(new DeviceRgb(232, 232, 232)));

        doc.close();
    }

    public static float calculateHeightForLinesNum(Document doc, Paragraph p, float width, float linesNum,
            boolean orphans) {
        ParagraphRenderer renderer = (ParagraphRenderer) p.createRendererSubTree().setParent(doc.getRenderer());
        LayoutResult layoutRes = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(width, 100000))));
        float lineHeight = layoutRes.getOccupiedArea().getBBox().getHeight() / renderer.getLines().size();
        float height = lineHeight * linesNum;
        if (orphans) {
            return height;
        } else {
            return layoutRes.getOccupiedArea().getBBox().getHeight() - height;
        }
    }

    public static float calculateHeightForLinesNumKeepTogetherCaseSpecific(Document doc, Paragraph p, float width,
            float height, float linesNum) {
        ParagraphRenderer renderer = (ParagraphRenderer) p.createRendererSubTree().setParent(doc.getRenderer());
        LayoutResult layoutRes = renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(width, 10000))));
        int allLinesCount = renderer.getLines().size();
        float lineHeight = layoutRes.getOccupiedArea().getBBox().getHeight() / allLinesCount;
        renderer.layout(new LayoutContext(new LayoutArea(1, new Rectangle(width, height))));
        int linesWithinOnePageCount = renderer.getLines().size();
        return (allLinesCount - linesWithinOnePageCount - linesNum) * lineHeight;
    }

    private static void singleLimitedCanvasSizeCase(Document document, Paragraph paraOnCanvas, String description,
            float canvasHeight, int pageNum) {
        PdfDocument pdfDocument = document.getPdfDocument();
        document.add(new Paragraph(description).setBorder(new SolidBorder(ColorConstants.RED, 1)));
        PdfCanvas pdfCanvas = new PdfCanvas(pdfDocument.getPage(pageNum));
        Rectangle effectiveArea = document.getPageEffectiveArea(pdfDocument.getDefaultPageSize());
        Rectangle rectangle = new Rectangle(36, 550, effectiveArea.getWidth(), canvasHeight + LINES_SPACE_EPS);
        pdfCanvas.saveState()
                .setFillColor(ColorConstants.MAGENTA)
                .rectangle(rectangle)
                .fill()
                .restoreState();
        Canvas canvas = new Canvas(pdfCanvas, pdfDocument, rectangle);
        canvas.add(paraOnCanvas);
        canvas.close();
    }

    private static void singleMaxHeightCase(Document document, boolean orphans, boolean overflowVisible) {
        String orphansOrWidows = orphans ? "orphans" : "widows";
        Paragraph testDescription = new Paragraph().setBorder(new SolidBorder(ColorConstants.RED, 1));
        testDescription.add("The paragraph beneath has property " + orphansOrWidows.toUpperCase()
                + "_CONTROL, limiting the number of allowed " + orphansOrWidows + " to 3. "
                + "The paragraph also has property MAX_HEIGHT, whose value is defined so that the lines that" + (
                orphans ? " " : " don't ") + "fit in the area limited by MAX_HEIGHT value cause " + orphansOrWidows
                + " violation.\n");
        if (overflowVisible) {
            testDescription.add("On this page the paragraph has also Property.OVERFLOW_Y set to VISIBLE "
                    + "in order to visualize the " + orphansOrWidows + " violation.");
        }
        document.add(testDescription);

        Paragraph paragraph = setParagraphStylingProperties(new Paragraph(PARA_TEXT), false);
        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight = calculateHeightForLinesNum(document, paragraph,
                effectiveArea.getWidth(), minOrphansOrWidows - 1, orphans);
        if (orphans) {
            paragraph.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
        } else {
            paragraph.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, false));
        }
        paragraph.setProperty(Property.MAX_HEIGHT, new UnitValue(UnitValue.POINT, linesHeight + LINES_SPACE_EPS));
        if (overflowVisible) {
            paragraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
        }
        document.add(paragraph);

    }

    private static void singleInlineImageCase(Document document, Image img, boolean orphans,
            boolean orphansWidowsEnabled) {
        Paragraph paragraph = setParagraphStylingProperties(new Paragraph(), false);
        for (int i = 0; i < 100; i++) {
            paragraph.add(img).add("inline image");
        }
        Rectangle effectiveArea = document.getPageEffectiveArea(document.getPdfDocument().getDefaultPageSize());
        int minOrphansOrWidows = 3;
        float linesHeight = calculateHeightForLinesNum(document, paragraph, effectiveArea.getWidth(),
                minOrphansOrWidows - 1, orphans);
        if (orphansWidowsEnabled) {
            if (orphans) {
                paragraph.setOrphansControl(new ParagraphOrphansControl(minOrphansOrWidows));
            } else {
                paragraph.setWidowsControl(new ParagraphWidowsControl(minOrphansOrWidows, 1, false));
            }
        }
        String orphansOrWidows = orphans ? "orphans" : "widows";
        Paragraph testDescription = new Paragraph().setMargin(0)
                .setBorder(new SolidBorder(ColorConstants.RED, 1))
                .setHeight(effectiveArea.getHeight() - linesHeight - LINES_SPACE_EPS);
        testDescription.add("The paragraph beneath has" + (orphansWidowsEnabled ? " " : " no ")
                + orphansOrWidows.toUpperCase() + "_CONTROL property"
                + (orphansWidowsEnabled ? ", limiting the number of allowed " + orphansOrWidows + " to 3. " : ".")
                + "The size of this description-paragraph is defined so that " + orphansOrWidows
                + " violation occurs.");
        document.add(testDescription);
        document.add(paragraph);
    }

    private static Paragraph setParagraphStylingProperties(Paragraph paragraph, boolean increasedTopMargin) {
        return paragraph.setMargins(increasedTopMargin ? 30 : 0, 0, 0, 0)
                .setBackgroundColor(new DeviceRgb(232, 232, 232))
                .setBorder(new SolidBorder(1));
    }

    private static Rectangle[] initUniformColumns(PageSize pageSize, int columnsNum) {
        Rectangle[] columns = new Rectangle[columnsNum];
        float columnWidth = (pageSize.getWidth() - 72) / columnsNum;
        for (int i = 0; i < columnsNum; ++i) {
            columns[i] = new Rectangle(36 + i * columnWidth, 36, columnWidth - 36, pageSize.getHeight() - 72);
        }
        return columns;
    }
}
