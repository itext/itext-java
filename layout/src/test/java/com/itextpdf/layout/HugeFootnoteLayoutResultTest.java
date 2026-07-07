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

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.SectionBreak;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.properties.margins.FootnotesProperties;
import com.itextpdf.layout.testutil.LayoutResultTestUtil;
import com.itextpdf.layout.testutil.PageMarginsTestUtil;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Tag("IntegrationTest")
public class HugeFootnoteLayoutResultTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/HugeFootnoteLayoutResultTest/";
    private static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/layout/HugeFootnoteLayoutResultTest/";

    private static final float A4_HEIGHT = PageSize.A4.getHeight();
    private static final float A4_WIDTH = PageSize.A4.getWidth();

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void footnoteHeightExactlyPageHeightRenderTest()
            throws IOException, InterruptedException {
        String fileName = "footnoteHeightExactlyPageHeight";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            document.add(new SectionBreak(PageMarginsTestUtil.getFootnoteMarginBoxes(A4_HEIGHT)));

            Div forced = new Div()
                    .add(new Paragraph("Content — footnote height == page height (FORCED)."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));
            document.add(forced);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void footnoteHeightExactlyPageHeightNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            applyFootnoteMarginBoxes(document, A4_HEIGHT);

            Div element = new Div()
                    .add(new Paragraph(TestResourceUtil.getByronStanza()))
                    .setHeight(60);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 0, A4_HEIGHT, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element should return NOTHING when the footnote height equals the page height");
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void footnoteHeightExceedsPageHeightRenderTest()
            throws IOException, InterruptedException {
        String fileName = "footnoteHeightExceedsPageHeight";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            document.add(new SectionBreak(PageMarginsTestUtil.getFootnoteMarginBoxes(A4_HEIGHT + 50f)));

            Div forced = new Div()
                    .add(new Paragraph("Content — footnote height > page height (FORCED)."))
                    .setBackgroundColor(new DeviceRgb(255, 100, 100));
            document.add(forced);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void footnoteHeightExceedsPageHeightNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            applyFootnoteMarginBoxes(document, A4_HEIGHT + 50f);

            Div element = new Div()
                    .add(new Paragraph(TestResourceUtil.getByronStanza()))
                    .setHeight(60);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 0, A4_HEIGHT + 50f, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element should return NOTHING when the footnote height exceeds the page height");
        }
    }

    @Test
    public void footnoteDoublePageHeightNothingTest() {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
             Document document = new Document(pdfDoc)) {

            applyFootnoteMarginBoxes(document, A4_HEIGHT * 2f);

            Div element = new Div()
                    .add(new Paragraph(TestResourceUtil.getByronStanza()))
                    .setHeight(60);

            int status = LayoutResultTestUtil.getLayoutStatus(element, document,
                    TestResourceUtil.getAvailableRect(A4_HEIGHT, A4_WIDTH, 36f, 0, A4_HEIGHT * 2f, 0, 0));

            Assertions.assertEquals(LayoutResult.NOTHING, status,
                    "Element should return NOTHING when the footnote height is 2× the page height");
        }
    }

    @Test
    @LogMessages(
            messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 3))
    public void footnoteExceedsPageHeightMultiplePagesRenderTest()
            throws IOException, InterruptedException {
        String fileName = "footnoteExceedsPageHeightMultiPage";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            applyFootnoteMarginBoxes(document, A4_HEIGHT + 50f);

            for (int i = 1; i <= 3; i++) {
                Div forced = new Div()
                        .add(new Paragraph("PAGE " + i + " — footnote > page height (FORCED)."))
                        .setBackgroundColor(cellColor(i - 1));
                document.add(forced);
                if (i < 3) {
                    document.add(new SectionBreak(PageMarginsTestUtil.getFootnoteMarginBoxes(A4_HEIGHT + 50f)));
                }
            }
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void hugeParagraphWithFootnoteAnchorInDivTest() throws IOException, InterruptedException {
        String fileName = "hugeParagraphFontWithFootnoteAnchorInDiv";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            pdfDoc.setTagged();

            document.setFootnotesProperties(new FootnotesProperties()
                    .setFootnotesContainerStyle(new Style().setFontSize(27f)));
            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Paragraph p = new Paragraph()
                    .add("Large paragraph text.")
                    .setFontSize(155f)
                    .add(new FootnoteAnchor("[1]", footnote));

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            document.add(div);
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void hugeParagraphWithFootnoteAnchorTest() throws IOException, InterruptedException {
        String fileName = "hugeParagraphWithFootnoteAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));
            footnote.setProperty(Property.FONT_SIZE, UnitValue.createPointValue(105));

            Paragraph p = new Paragraph()
                    .add("Large paragraph text.")
                    .setFontSize(105f)
                    .add(new FootnoteAnchor(new Text("[1]").setFontSize(20).setTextRise(100), footnote));

            document.add(p);
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void hugeFontAnchorFootnoteTest() throws IOException, InterruptedException {
        String fileName = "hugeFontAnchorFootnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Paragraph p = new Paragraph()
                    .add("Paragraph.").setFontSize(30f)
                    .add(new FootnoteAnchor(new Text("Large anchor text.").setFontSize(80f), footnote));

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            document.add(div);
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void hugeFontAnchorWithMultipleFootnotesInDivTest() throws IOException, InterruptedException {
        String fileName = "hugeFontAnchorWithMultipleFootnotesInDiv";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            pdfDoc.setTagged();
            for (int i = 1; i <= 2; i++) {
                Footnote footnote = new Footnote("Footnote " + i + ": " + TestResourceUtil.getByronStanza());
                footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 2));

                FootnoteAnchor footnoteAnchor = new FootnoteAnchor(new Text("Anchor " + i + " with huge font.")
                        .setFontSize(47f), footnote);
                footnoteAnchor.setProperty(Property.FORCED_PLACEMENT, true);
                footnoteAnchor.setProperty(Property.KEEP_TOGETHER, true);
                Paragraph p = new Paragraph()
                        .add("Paragraph " + i)
                        .add(footnoteAnchor);

                Div div = new Div().add(p).setBorder(new SolidBorder(cellColor(i - 1), 2));

                document.add(div);
            }
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void hugeFontAnchorWithMultipleFootnotesInDiv2Test() throws IOException, InterruptedException {
        String fileName = "hugeFontAnchorWithMultipleFootnotesInDiv2";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            pdfDoc.setTagged();
            for (int i = 1; i <= 4; i++) {
                Footnote footnote = new Footnote("Footnote " + i + ": " + TestResourceUtil.getByronStanza());
                footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 2));

                FootnoteAnchor footnoteAnchor = new FootnoteAnchor(new Text("Anchor " + i + " with huge font.")
                        .setFontSize(48f), footnote);
                footnoteAnchor.setProperty(Property.FORCED_PLACEMENT, true);
                footnoteAnchor.setProperty(Property.KEEP_TOGETHER, true);
                Paragraph p = new Paragraph()
                        .add("Paragraph " + i)
                        .add(footnoteAnchor);

                Div div = new Div().add(p).setBorder(new SolidBorder(cellColor(i - 1), 2));

                document.add(div);
            }
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void hugeFontAnchorWithMultipleFootnotesTest() throws IOException, InterruptedException {
        String fileName = "hugeFontAnchorWithMultipleFootnotes";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            for (int i = 1; i <= 4; i++) {
                Footnote footnote = new Footnote("Footnote " + i + ": " + TestResourceUtil.getByronStanza());
                footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 2));

                FootnoteAnchor footnoteAnchor = new FootnoteAnchor(new Text("Anchor " + i + " with huge font.")
                        .setFontSize(58f), footnote);
                footnoteAnchor.setProperty(Property.FORCED_PLACEMENT, true);
                footnoteAnchor.setProperty(Property.KEEP_TOGETHER, true);
                Paragraph p = new Paragraph()
                        .add("Paragraph " + i)
                        .add(footnoteAnchor);

                document.add(p);
            }
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void largeImageAnchorFootnoteRenderTest() throws IOException, InterruptedException {
        String fileName = "largeImageAnchorFootnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 4));
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Image largeImage = new Image(
                    ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            largeImage.setWidth(A4_WIDTH * 0.80f);
            largeImage.setHeight(A4_HEIGHT * 0.70f);

            FootnoteAnchor anchor = new FootnoteAnchor(largeImage, footnote);
            document.add(new Div()
                    .add(new Paragraph().add(anchor))
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 3)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void largeImageAnchorWithNormalContentRenderTest()
            throws IOException, InterruptedException {
        String fileName = "largeImageAnchorNormalContent";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote("Footnote for the large image anchor.\n" + TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Image largeImage = new Image(
                    ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));

            largeImage.setWidth(A4_WIDTH * 0.80f);
            largeImage.setHeight(A4_HEIGHT * 0.70f);

            FootnoteAnchor anchor = new FootnoteAnchor(largeImage, footnote);
            document.add(new Div()
                    .add(new Paragraph().add(anchor))
                    .setBorder(new SolidBorder(ColorConstants.GREEN, 2)));

            document.add(new Paragraph("Normal content after the large image anchor."));
            document.add(new Paragraph(TestResourceUtil.getByronStanza()));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA))
    public void smallImageAnchorWithHugeTextFootnoteRenderTest()
            throws IOException, InterruptedException {
        String fileName = "smallImageAnchorHugeTextFootnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 8));
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            image.setWidth(15);
            FootnoteAnchor anchor = new FootnoteAnchor(image, footnote);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 3)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    private static void applyFootnoteMarginBoxes(Document document, float height) {
        document.setPageMargins(pageNum -> true, PageMarginsTestUtil.getFootnoteMarginBoxes(height));
    }

    private static DeviceRgb cellColor(int index) {
        DeviceRgb[] palette = {
                new DeviceRgb(65, 151, 29),
                new DeviceRgb(209, 247, 29),
                new DeviceRgb(78, 151, 205),
                new DeviceRgb(255, 165, 0)
        };
        return palette[index % palette.length];
    }
}
