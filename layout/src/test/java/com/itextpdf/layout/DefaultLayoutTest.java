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

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.logs.LayoutLogMessageConstant;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class DefaultLayoutTest extends ExtendedITextTest {

    public static float EPS = 0.001f;

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/DefaultLayoutTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/DefaultLayoutTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void multipleAdditionsOfSameModelElementTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleAdditionsOfSameModelElementTest1.pdf";
        String cmpFileName = sourceFolder + "cmp_multipleAdditionsOfSameModelElementTest1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph("Hello. I am a paragraph. I want you to process me correctly");
        document.add(p).add(p).add(new AreaBreak(PageSize.DEFAULT)).add(p);

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rendererTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rendererTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_rendererTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        document.add(new Paragraph(new Text(str).setBackgroundColor(ColorConstants.RED)).setBackgroundColor(ColorConstants.GREEN)).
                add(new Paragraph(str)).
                add(new AreaBreak(PageSize.DEFAULT)).
                add(new Paragraph(str));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void emptyParagraphsTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "emptyParagraphsTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_emptyParagraphsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph());
        // this line should not cause any effect
        document.add(new Paragraph().setBackgroundColor(ColorConstants.GREEN));
        document.add(new Paragraph().setBorder(new SolidBorder(ColorConstants.BLUE, 3)));

        document.add(new Paragraph("Hello! I'm the first paragraph added to the document. Am i right?").setBackgroundColor(ColorConstants.RED).setBorder(new SolidBorder(1)));
        document.add(new Paragraph().setHeight(50));
        document.add(new Paragraph("Hello! I'm the second paragraph added to the document. Am i right?"));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void emptyParagraphsTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "emptyParagraphsTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_emptyParagraphsTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Hello, i'm the text of the first paragraph on the first line. Let's break me and meet on the next line!\nSee? I'm on the second line. Now let's create some empty lines,\n for example one\n\nor two\n\n\nor three\n\n\n\nNow let's do something else"));
        document.add(new Paragraph("\n\n\nLook, i'm the the text of the second paragraph. But before me and the first one there are three empty lines!"));

        document.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void textWithWhitespacesTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textWithWhitespacesTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textWithWhitespacesTest01.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDoc);
        doc.add(new Paragraph("Test non-breaking spaces"));
        doc.add(new Paragraph("\u00a0\u00a0\u00a0\u00a0test test"));
        doc.add(new Paragraph("test test\u00a0\u00a0\u00a0\u00a0test test"));
        doc.add(new Paragraph("Test usual spaces"));
        doc.add(new Paragraph("\u0020\u0020\u0020\u0020test test"));
        doc.add(new Paragraph("test test\u0020\u0020\u0020\u0020test test"));

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(count = 1, messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void addParagraphOnShortPage1() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "addParagraphOnShortPage1.pdf";
        String cmpFileName = sourceFolder + "cmp_addParagraphOnShortPage1.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(500, 70));

        Paragraph p = new Paragraph();
        p.add("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        p.add(new Text("BBB").setFontSize(30));
        p.add("CCC");
        p.add("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD");
        p.add("EEE");

        doc.add(p);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void addParagraphOnShortPage2() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "addParagraphOnShortPage2.pdf";
        String cmpFileName = sourceFolder + "cmp_addParagraphOnShortPage2.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(300, 50));

        Paragraph p = new Paragraph();
        p.add("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        doc.add(p);


        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LayoutLogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    public void addWordOnShortPageTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "addWordOnShortPageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_addWordOnShortPageTest01.pdf";

        // Default font size
        float defaultFontSize = 12;
        // Use the default font to get the width which will be occupied by two letters
        float contentWidth = PdfFontFactory.createFont().getWidth("he", defaultFontSize);
        // Not enough height to place letters without FORCED_PLACEMENT
        float shortHeight = 15;
        // The sum of either top and bottom page margins, or left and right page margins
        float margins = 36 + 36;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(margins + contentWidth + EPS, margins + shortHeight));

        Paragraph p = new Paragraph("hello");

        // The area's height is not enough to place the paragraph.
        // The area's width is enough to place 2 characters.
        doc.add(p);

        doc.close();

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void closeEmptyDocumentTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "closeEmptyDocumentTest.pdf";
        String cmpFileName = sourceFolder + "cmp_closeEmptyDocumentTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdfDocument);
        AssertUtil.doesNotThrow(() -> document.close());

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void closeEmptyDocumentWithEventOnAddingPageTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "closeEmptyDocumentWithEventTest.pdf";
        String cmpFileName = sourceFolder + "cmp_closeEmptyDocumentWithEventTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        new PdfLayer("Some layer", pdfDocument);

        ParagraphAdderHandler handler = new ParagraphAdderHandler();
        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
        AssertUtil.doesNotThrow(() -> pdfDocument.close());

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void checkPageSizeOfClosedEmptyDocumentTest() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        AssertUtil.doesNotThrow(() -> pdfDocument.close());
        byte[] bytes = baos.toByteArray();
        baos.close();

        PdfDocument newDoc = new PdfDocument(new PdfReader(new ByteArrayInputStream(bytes)));
        Assertions.assertTrue(PageSize.DEFAULT.equalsWithEpsilon(newDoc.getPage(1).getPageSize()));
        newDoc.close();
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.ATTEMPT_TO_GENERATE_PDF_PAGES_TREE_WITHOUT_ANY_PAGES, logLevel = LogLevelConstants.INFO)
    })
    public void closeEmptyDocumentWithRemovingPageEventOnAddingPageTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "closeEmptyDocumentWithRemovingEventTest.pdf";
        String cmpFileName = sourceFolder + "cmp_closeEmptyDocumentWithRemovingEventTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        PageRemoverHandler handler = new PageRemoverHandler();
        pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
        AssertUtil.doesNotThrow(() -> pdfDocument.close());

        Assertions.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private static class ParagraphAdderHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            PdfDocument pdfDoc = ((PdfDocumentEvent) event).getDocument();
            List<PdfLayer> group = new ArrayList<>();
            group.add(new PdfLayer("Some second layer", pdfDoc));
            // If page will be added in PdfPagesTree#generateTree method, after flushing PdfOCProperties,
            // exception will be thrown, but page will be added before anu flushing, and there is no exception
            pdfDoc.getCatalog().getOCProperties(false).addOCGRadioGroup(group);
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);

            new Canvas(canvas, new Rectangle(0, 0, 600, 600))
                    .add(new Paragraph("Some text").setFixedPosition(100, 100, 100));
        }
    }

    private static class PageRemoverHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            PdfDocument pdfDoc = ((PdfDocumentEvent) event).getDocument();
            pdfDoc.removePage(1);
        }
    }
}
