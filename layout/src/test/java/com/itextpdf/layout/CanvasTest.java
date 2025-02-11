/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.sequence.AbstractIdentifiableElement;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.actions.sequence.SequenceIdManager;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.actions.events.ITextCoreProductEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Link;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.ListNumberingType;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.testutil.TestConfigurationEvent;
import com.itextpdf.layout.testutil.TestProductEvent;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class CanvasTest extends ExtendedITextTest {
    private static final TestConfigurationEvent CONFIGURATION_ACCESS = new TestConfigurationEvent();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/CanvasTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/layout/CanvasTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN))
    public void canvasNoPageLinkTest() throws IOException, InterruptedException {
        String testName = "canvasNoPageLinkTest";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(pdfCanvas, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    public void canvasWithPageLinkTest() throws IOException, InterruptedException {
        String testName = "canvasWithPageLinkTest";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    public void listItemWithoutMarginsInCanvasTest() throws IOException, InterruptedException {
        String testName = "listItemWithoutMarginsInCanvasTest";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();

        Canvas canvas = new Canvas(page, pageSize);
        com.itextpdf.layout.element.List list = new com.itextpdf.layout.element.List();
        list.setListSymbol(ListNumberingType.DECIMAL);
        list.add(new ListItem("list item 1"));
        list.add(new ListItem("list item 2"));
        canvas.add(list);
        canvas.close();
        pdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    public void notApplyingMarginsInCanvasTest() throws IOException, InterruptedException {
        String testName = "notApplyingMarginsInCanvasTest";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();

        Canvas canvas = new Canvas(page, pageSize);
        canvas.setProperty(Property.MARGIN_LEFT, 36);
        canvas.add(new Paragraph("Hello"));
        canvas.close();
        pdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    public void nullableMarginsInCanvasRendererTest() throws IOException, InterruptedException {
        String testName = "nullableMarginsInCanvasRenderer";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();

        Canvas canvas = new Canvas(page, pageSize);
        canvas.setProperty(Property.MARGIN_LEFT, null);
        canvas.add(new Paragraph("Hello"));
        canvas.close();
        pdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    public void canvasWithPageEnableTaggingTest01() throws IOException, InterruptedException {
        String testName = "canvasWithPageEnableTaggingTest01";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));

        pdf.setTagged();

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);
        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.UNABLE_TO_APPLY_PAGE_DEPENDENT_PROP_UNKNOWN_PAGE_ON_WHICH_ELEMENT_IS_DRAWN),
            @LogMessage(messageTemplate = IoLogMessageConstant.PASSED_PAGE_SHALL_BE_ON_WHICH_CANVAS_WILL_BE_RENDERED)})
    public void canvasWithPageEnableTaggingTest02() throws IOException, InterruptedException {
        String testName = "canvasWithPageEnableTaggingTest02";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));

        pdf.setTagged();

        PdfPage page = pdf.addNewPage();
        Rectangle pageSize = page.getPageSize();
        Rectangle rectangle = new Rectangle(
                pageSize.getX() + 36,
                pageSize.getTop() - 80,
                pageSize.getWidth() - 72,
                50);

        Canvas canvas = new Canvas(page, rectangle);

        // This will disable tagging and also prevent annotations addition. Created tagged document is invalid. Expected log message.
        canvas.enableAutoTagging(null);

        canvas.add(
                new Paragraph(
                        new Link("Google link!", PdfAction.createURI("https://www.google.com"))
                                .setUnderline()
                                .setFontColor(ColorConstants.BLUE)));
        canvas.close();
        pdf.close();

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    public void elementWithAbsolutePositioningInCanvasTest() throws IOException, InterruptedException {
        String testName = "elementWithAbsolutePositioningInCanvas";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out))) {
            pdf.addNewPage();
            Canvas canvas = new Canvas(new PdfCanvas(pdf.getFirstPage()),
                    new Rectangle(120, 650, 60, 80));

            Div notFittingDiv = new Div().setWidth(100)
                    .add(new Paragraph("Paragraph in Div with Not set position"));
            canvas.add(notFittingDiv);

            Div divWithPosition = new Div().setFixedPosition(120, 300, 80);
            divWithPosition.add(new Paragraph("Paragraph in Div with set position"));
            canvas.add(divWithPosition);

            canvas.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    //TODO: DEVSIX-4820 (discuss the displaying of element with absolute position)
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.CANVAS_ALREADY_FULL_ELEMENT_WILL_BE_SKIPPED)})
    public void parentElemWithAbsolPositionKidNotSuitCanvasTest() throws IOException, InterruptedException {
        String testName = "parentElemWithAbsolPositionKidNotSuitCanvas";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out))) {
            pdf.addNewPage();

            Canvas canvas = new Canvas(new PdfCanvas(pdf.getFirstPage()),
                    new Rectangle(120, 650, 55, 80));

            Div notFittingDiv = new Div().setWidth(100).add(new Paragraph("Paragraph in Div with Not set position"));
            canvas.add(notFittingDiv);

            Div divWithPosition = new Div().setFixedPosition(120, 300, 80);
            divWithPosition.add(new Paragraph("Paragraph in Div with set position"));
            canvas.add(divWithPosition);

            canvas.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    //TODO: DEVSIX-4820 (NullPointerException on processing absolutely positioned elements in small canvas area)
    public void nestedElementWithAbsolutePositioningInCanvasTest() throws IOException, InterruptedException {
        String testName = "nestedElementWithAbsolutePositioningInCanvas";
        String out = DESTINATION_FOLDER + testName + ".pdf";
        String cmp = SOURCE_FOLDER + "cmp_" + testName + ".pdf";

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(out))) {
            pdf.addNewPage();

            Canvas canvas = new Canvas(new PdfCanvas(pdf.getFirstPage()),
                    new Rectangle(120, 650, 55, 80));

            Div notFittingDiv = new Div().setWidth(100).add(new Paragraph("Paragraph in Div with Not set position"));

            Div divWithPosition = new Div().setFixedPosition(50, 20, 80);
            divWithPosition.add(new Paragraph("Paragraph in Div with set position"));

            notFittingDiv.add(divWithPosition);

            Assertions.assertThrows(NullPointerException.class, () -> canvas.add(notFittingDiv));
            canvas.close();
        }

        Assertions.assertNull(new CompareTool().compareByContent(out, cmp, DESTINATION_FOLDER));
    }

    @Test
    public void addBlockElemMethodLinkingTest() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();

            SequenceId sequenceId = new SequenceId();
            EventManager.getInstance().onEvent(new TestProductEvent(sequenceId));

            IBlockElement blockElement = new Paragraph("some text");
            SequenceIdManager.setSequenceId((AbstractIdentifiableElement) blockElement, sequenceId);
            List<AbstractProductProcessITextEvent> events;
            try (Canvas canvas = new Canvas(pdfDocument.getPage(1), new Rectangle(0, 0, 200, 200))) {
                canvas.add(blockElement);
                events = CONFIGURATION_ACCESS.getPublicEvents(
                        canvas.getPdfDocument().getDocumentIdWrapper());
            }

            // Second event was linked by adding block element method
            Assertions.assertEquals(2, events.size());

            Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
            Assertions.assertTrue(events.get(1) instanceof TestProductEvent);
        }
    }

    @Test
    public void addImageElemMethodLinkingTest() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            pdfDocument.addNewPage();

            SequenceId sequenceId = new SequenceId();
            EventManager.getInstance().onEvent(new TestProductEvent(sequenceId));

            Image image = new Image(new PdfFormXObject(new Rectangle(10, 10)));
            SequenceIdManager.setSequenceId(image, sequenceId);
            List<AbstractProductProcessITextEvent> events;
            try (Canvas canvas = new Canvas(pdfDocument.getPage(1), new Rectangle(0, 0, 200, 200))) {
                canvas.add(image);
                events = CONFIGURATION_ACCESS.getPublicEvents(
                        canvas.getPdfDocument().getDocumentIdWrapper());
            }

            // Second event was linked by adding block element method
            Assertions.assertEquals(2, events.size());

            Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
            Assertions.assertTrue(events.get(1) instanceof TestProductEvent);
        }
    }

    @Test
    public void drawingOnPageReuseCanvas() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            ExposedPdfCanvas canvas = new ExposedPdfCanvas(pdfDocument.addNewPage());
            Assertions.assertTrue(canvas.getDrawingOnPage());
            try (Canvas canvas1 = new Canvas(canvas, new Rectangle(200, 200, 200, 200))) {
                Assertions.assertTrue(((ExposedPdfCanvas) canvas1.pdfCanvas).getDrawingOnPage());
            }
        }
    }

    @Test
    public void notDrawingOnPageReuseCanvas() {
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfStream stream = new PdfStream();
            ExposedPdfCanvas canvas = new ExposedPdfCanvas(stream, new PdfResources(), pdfDocument);
            Assertions.assertFalse(canvas.getDrawingOnPage());
            try (Canvas canvas1 = new Canvas(canvas, new Rectangle(200, 200, 200, 200))) {
                Assertions.assertFalse(((ExposedPdfCanvas) canvas1.pdfCanvas).getDrawingOnPage());
            }
        }
    }

    static class ExposedPdfCanvas extends PdfCanvas{

        public ExposedPdfCanvas(PdfStream contentStream, PdfResources resources, PdfDocument document) {
            super(contentStream, resources, document);
        }

        public ExposedPdfCanvas(PdfPage page) {
            super(page);
        }


        public boolean getDrawingOnPage(){
            return this.drawingOnPage;
        }
    }
}
