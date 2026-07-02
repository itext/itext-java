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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.data.IEventData;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class TableMultiPageRenderingTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/TableMultiPageRenderingTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/TableMultiPageRenderingTest/";
    private static final float MARGIN = 36f;
    private static final PageSize SHORT_PAGE = new PageSize(PageSize.A4.getWidth(), 180);

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void tableStartsNearBottomTest() throws Exception {
        String fileName = "tableBottomPage.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createA4Document(destPath);
        addFiller(document, 88);

        Table table = new Table(new float[] {1, 3});
        table.setWidth(PageSize.A4.getWidth() - 2 * MARGIN);

        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Description")));

        for (int i = 1; i <= 20; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
            table.addCell(new Cell().add(
                    new Paragraph("Row " + i + " - some longer content to make the table taller.")
            ));
        }

        document.add(table);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    @Test
    public void tableWithIncompleteRowStartsNearBottomTest() throws Exception {
        String fileName = "tableLong2ndCellBottomPage.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createA4Document(destPath);
        addFiller(document, 88);

        Table table = new Table(new float[] {1, 3});
        table.setWidth(PageSize.A4.getWidth() - 2 * MARGIN);

        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Description")));

        table.addCell(new Cell().add(new Paragraph(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam tincidunt urna "
                        + "vel massa iaculis, ultrices posuere ex iaculis. Ut dignissim imperdiet "
                        + "libero sit amet eleifend. Nulla congue porta mi, et cursus lorem iaculis "
                        + "eget. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices "
                        + "posuere cubilia curae; Fusce commodo elementum massa eu euismod."
        )));
        table.addCell(new Cell().add(new Paragraph("0")));


        for (int i = 1; i <= 20; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
            table.addCell(new Cell().add(
                    new Paragraph("Row " + i + " - some longer content to make the table taller.")
            ));
        }

        document.add(table);
        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    @Test
    public void imageRowFlowsToSecondPageTest() throws Exception {
        String fileName = "tableWithImageStartsBottomPage.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createA4Document(destPath);
        addFiller(document, 85);

        document.add(createBaseImageTable(false));
        document.close();

        Assertions.assertTrue(pageContainsImage(destPath, 2));
        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    //TODO DEVSIX-7410: Fix test after fix
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageDoesNotFitOnShortFirstPageTest() throws Exception {
        String fileName = "tableImageShortPage.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createShortFirstPageDocument(destPath);

        document.add(createBaseImageTable(false));
        document.getPdfDocument().setDefaultPageSize(PageSize.A4);
        document.close();

        Assertions.assertTrue(pageContainsImage(destPath, 1));
        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    //TODO DEVSIX-7410: Fix cmp after fix
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void imageDoesNotFitOnShortFirstPageTest2() throws Exception {
        String fileName = "tableImageShortPage2.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createShortFirstPageDocument(destPath, true);

        Table table = createBaseImageTable(false);
        table.addCell(new Cell().add(new Paragraph("Testing.")));

        document.add(table);
        document.close();

        Assertions.assertTrue(pageContainsImage(destPath, 1));
        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    //TODO DEVSIX-7410: Fix cmp after fix
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void thirdRowImageFlowsToSecondPageTest() throws Exception {
        String fileName = "extraRowImageToSecondPageTest.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createShortFirstPageDocument(destPath);

        Table table = new Table(1);
        table.setWidth(PageSize.A4.getWidth() - 2 * MARGIN);
        table.addHeaderCell(createHeaderCell());
        table.addCell(new Cell().add(new Paragraph("Extra cell first.")));
        table.addCell(new Cell().add(new Image(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"))));

        document.add(table);
        document.close();

        Assertions.assertTrue(pageContainsImage(destPath, 2));
        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    //TODO DEVSIX-7410: Fix cmp after rix
    @Test
    public void thirdRowImageFlowsToSecondPageA4Test() throws Exception {
        String fileName = "extraRowImageToSecondA4PageTest.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createShortFirstPageDocument(destPath, true);

        Table table = new Table(1);
        table.setWidth(PageSize.A4.getWidth() - 2 * MARGIN);
        table.addHeaderCell(createHeaderCell());
        table.addCell(new Cell().add(new Paragraph("Extra cell first.")));
        table.addCell(new Cell().add(new Image(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"))));

        document.add(table);
        document.close();

        Assertions.assertTrue(pageContainsImage(destPath, 2));
        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    //TODO DEVSIX-7410: Fix test after fix
    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 6)
    })
    public void multipleImageRowsStartOnShortFirstPageTest() throws Exception {
        String fileName = "tableMultiImagesShortPage.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createShortFirstPageDocument(destPath);

        Table table = createBaseImageTable(false);

        for (int i = 0; i < 5; i++) {
            table.addCell(new Cell().add(new Image(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"))));
        }

        document.add(table);
        document.getPdfDocument().setDefaultPageSize(PageSize.A4);
        document.close();

        Assertions.assertTrue(pageContainsImage(destPath, 1));
        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    @Test
    public void autoScaledImageFitsOnShortFirstPageTest() throws Exception {
        String fileName = "tableImageShortPageScaled.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        Document document = createShortFirstPageDocument(destPath);

        document.add(createBaseImageTable(true));
        document.close();

        Assertions.assertTrue(pageContainsImage(destPath, 1));
        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.TABLE_WIDTH_IS_MORE_THAN_EXPECTED_DUE_TO_MIN_WIDTH)
    })
    public void imageOnVeryThinPageTest() throws Exception {
        String fileName = "tableImageThinPage.pdf";
        String destPath = DESTINATION_FOLDER + fileName;

        PdfWriter writer = new PdfWriter(destPath);
        PdfDocument pdf = new PdfDocument(writer);

        PageSize thinPage = new PageSize(180, PageSize.A4.getHeight());
        pdf.setDefaultPageSize(thinPage);

        Document document = new Document(pdf);
        document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

        Table table = new Table(1);
        table.setWidth(thinPage.getWidth() - 2 * MARGIN);
        table.addHeaderCell(createHeaderCell());

        Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));

        table.addCell(new Cell().add(image));

        document.add(table);
        document.close();

        Assertions.assertTrue(pageContainsImage(destPath, 1));
        Assertions.assertNull(new CompareTool().compareByContent(
                destPath,
                SOURCE_FOLDER + "cmp_" + fileName,
                DESTINATION_FOLDER
        ));
    }

    private Document createA4Document(String fileName) throws Exception {
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

        return document;
    }

    private Document createShortFirstPageDocument(String fileName) throws Exception {
        return createShortFirstPageDocument(fileName, false);
    }

    private Document createShortFirstPageDocument(String fileName, boolean addSecondA4Page) throws Exception {
        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);

        pdf.setDefaultPageSize(SHORT_PAGE);

        if (addSecondA4Page) {
            pdf.addNewPage();
            pdf.setDefaultPageSize(PageSize.A4);
        }

        Document document = new Document(pdf);
        document.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);

        return document;
    }

    private Table createBaseImageTable(boolean autoScale) throws Exception {
        Table table = new Table(1);
        table.setWidth(PageSize.A4.getWidth() - 2 * MARGIN);

        table.addHeaderCell(createHeaderCell());

        Image image = new Image(ImageDataFactory.create(SOURCE_FOLDER + "itis.jpg"));
        image.setAutoScale(autoScale);

        table.addCell(new Cell().add(image));
        return table;
    }

    private Cell createHeaderCell() {
        return new Cell().add(new Paragraph("Header"));
    }

    private void addFiller(Document document, int lines) {
        for (int i = 0; i < lines; i++) {
            document.add(new Paragraph(" "));
        }
    }

    private static boolean pageContainsImage(String pdfPath, int pageNumber) throws IOException {
        final boolean[] found = {false};

        try(PdfDocument pdfDocument = new PdfDocument(new PdfReader(pdfPath))) {
            PdfPage page = pdfDocument.getPage(pageNumber);

            IEventListener listener = new IEventListener() {
                @Override
                public void eventOccurred(IEventData data, EventType type) {
                    if (type == EventType.RENDER_IMAGE) {
                        found[0] = true;
                    }
                }

                @Override
                public java.util.Set<EventType> getSupportedEvents() {
                    java.util.Set<EventType> types = new java.util.HashSet<>();
                    types.add(EventType.RENDER_IMAGE);
                    return types;
                }
            };

            PdfCanvasProcessor processor = new PdfCanvasProcessor(listener);
            processor.processPageContent(page);
        }

        return found[0];
    }
}
