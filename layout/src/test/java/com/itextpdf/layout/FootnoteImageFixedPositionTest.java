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
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.layout.properties.margins.Footnote;
import com.itextpdf.layout.properties.margins.FootnoteAnchor;
import com.itextpdf.layout.testutil.TestResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Tag("IntegrationTest")
public class FootnoteImageFixedPositionTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/layout/FootnoteImageFixedPositionTest/";
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/layout/FootnoteImageFixedPositionTest/";

    private static final float A4_HEIGHT = PageSize.A4.getHeight();
    private static final float A4_WIDTH = PageSize.A4.getWidth();

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void fixedPositionOnTextFootnoteRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnTextFootnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));
            footnote.setFixedPosition(100, A4_HEIGHT / 2f, 300);

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnTextFootnoteOutsidePageRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnTextFootnoteOutsidePage";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));
            footnote.setFixedPosition(A4_WIDTH + 50f, A4_HEIGHT + 50f, 200);

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Paragraph p = new Paragraph("Anchor with footnote fixed outside page.").add(anchor);
            document.add(p);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void fixedPositionOnTextFootnoteHugeContentTest() throws IOException, InterruptedException {
        String fileName = "fixedPositionOnTextFootnoteHugeContent";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {
            Footnote footnote = new Footnote(TestResourceUtil.repeatString(TestResourceUtil.getByronStanza(), 6));
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));
            footnote.setFixedPosition(36, 100, A4_WIDTH - 72f);

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor);

            Div div = new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2));
            document.add(div);
        }
        Assertions.assertNull(
                new CompareTool().compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnImageFootnoteRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnImageFootnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            image.setWidth(80);

            Footnote footnote = new Footnote(new Paragraph().add(image));
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));
            footnote.setFixedPosition(100, 150, 200);

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnLargeImageFootnoteRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnLargeImageFootnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            image.setWidth(A4_WIDTH * 0.70f);
            image.setHeight(A4_HEIGHT * 0.60f);

            Footnote footnote = new Footnote(new Paragraph().add(image));
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));
            footnote.setFixedPosition(50, 80, A4_WIDTH * 0.70f);

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor);
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnTextFootnoteAnchorRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnTextFootnoteAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            anchor.setFixedPosition(200, A4_HEIGHT * 0.60f, 150);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnTextFootnoteAnchorOutsidePageRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnTextFootnoteAnchorOutsidePage";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            anchor.setFixedPosition(A4_WIDTH + 100f, A4_HEIGHT + 100f, 100);

            Paragraph p = new Paragraph("Paragraph with anchor fixed outside page.").add(anchor);
            document.add(p);
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnTextFootnoteAnchorAndFootnoteRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnBothAnchorAndFootnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));
            footnote.setFixedPosition(50, 200, 250);

            FootnoteAnchor anchor = new FootnoteAnchor("[1]", footnote);
            anchor.setFixedPosition(300, A4_HEIGHT * 0.70f, 100);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnImageFootnoteAnchorRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnImageFootnoteAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            image.setWidth(15);

            FootnoteAnchor anchor = new FootnoteAnchor(image, footnote);
            anchor.setFixedPosition(200, A4_HEIGHT * 0.55f, 80);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 3)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnLargeImageFootnoteAnchorRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnLargeImageFootnoteAnchor";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Footnote footnote = new Footnote(TestResourceUtil.getByronStanza());
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));

            Image largeImage = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            largeImage.setWidth(A4_WIDTH * 0.70f);
            largeImage.setHeight(A4_HEIGHT * 0.60f);

            FootnoteAnchor anchor = new FootnoteAnchor(largeImage, footnote);
            anchor.setFixedPosition(36, 200, A4_WIDTH * 0.70f);

            Paragraph p = new Paragraph().add(anchor);
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
            document.add(new Paragraph("Content after large image anchor with fixed position."));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }

    @Test
    public void fixedPositionOnImageFootnoteAnchorAndImageFootnoteRenderTest()
            throws IOException, InterruptedException {
        String fileName = "fixedPositionOnImageAnchorAndImageFootnote";
        String outFileName = DESTINATION_FOLDER + fileName + ".pdf";
        String cmpFileName = SOURCE_FOLDER + "cmp_" + fileName + ".pdf";

        try (PdfDocument pdfDoc = new PdfDocument(CompareTool.createTestPdfWriter(outFileName));
             Document document = new Document(pdfDoc)) {

            Image footnoteImage = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            footnoteImage.setWidth(120);

            Footnote footnote = new Footnote(new Paragraph().add(footnoteImage));
            footnote.setBorder(new DashedBorder(ColorConstants.YELLOW, 3));
            footnote.setFixedPosition(50, 150, 200);

            Image anchorImage = new Image(ImageDataFactory.create(SOURCE_FOLDER + "bee.png"));
            anchorImage.setWidth(20);

            FootnoteAnchor anchor = new FootnoteAnchor(anchorImage, footnote);
            anchor.setFixedPosition(300, A4_HEIGHT * 0.65f, 100);

            Paragraph p = new Paragraph(TestResourceUtil.getByronStanza()).add(anchor)
                    .add(TestResourceUtil.getByronStanza());
            document.add(new Div().add(p).setBorder(new SolidBorder(ColorConstants.GREEN, 2)));
        }

        Assertions.assertNull(new CompareTool()
                .compareByContent(outFileName, cmpFileName, DESTINATION_FOLDER, "diff_" + fileName));
    }
}
