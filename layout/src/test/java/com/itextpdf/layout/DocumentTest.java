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

import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.actions.sequence.AbstractIdentifiableElement;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.actions.sequence.SequenceIdManager;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.actions.events.ITextCoreProductEvent;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEvent;
import com.itextpdf.kernel.pdf.event.AbstractPdfDocumentEventHandler;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.exceptions.LayoutExceptionMessageConstant;
import com.itextpdf.layout.properties.margins.MarginBoxName;
import com.itextpdf.layout.properties.margins.PageMarginBoxes;
import com.itextpdf.layout.properties.margins.PageMarginContent;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.testutil.TestConfigurationEvent;
import com.itextpdf.layout.testutil.TestProductEvent;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class DocumentTest extends ExtendedITextTest {

    private static final TestConfigurationEvent CONFIGURATION_ACCESS = new TestConfigurationEvent();

    @Test
    public void executeActionInClosedDocTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDoc);
        Paragraph paragraph = new Paragraph("test");
        document.add(paragraph);
        document.close();
        Exception exception = Assertions.assertThrows(PdfException.class,
                () -> document.checkClosingStatus());
        Assertions.assertEquals(LayoutExceptionMessageConstant.DOCUMENT_CLOSED_IT_IS_IMPOSSIBLE_TO_EXECUTE_ACTION,
                exception.getMessage());
    }

    @Test
    public void addBlockElemMethodLinkingTest() {
        try (Document doc = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            SequenceId sequenceId = new SequenceId();
            EventManager.getInstance().onEvent(new TestProductEvent(sequenceId));

            IBlockElement blockElement = new Paragraph("some text");
            SequenceIdManager.setSequenceId((AbstractIdentifiableElement) blockElement, sequenceId);
            doc.add(blockElement);

            List<AbstractProductProcessITextEvent> events = CONFIGURATION_ACCESS.getPublicEvents(
                    doc.getPdfDocument().getDocumentIdWrapper());
            // Second event was linked by adding block element method
            Assertions.assertEquals(2, events.size());

            Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
            Assertions.assertTrue(events.get(1) instanceof TestProductEvent);
        }
    }

    @Test
    public void addAreaBreakElemMethodLinkingTest() {
        try (Document doc = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            SequenceId sequenceId = new SequenceId();
            EventManager.getInstance().onEvent(new TestProductEvent(sequenceId));

            AreaBreak areaBreak = new AreaBreak();
            SequenceIdManager.setSequenceId(areaBreak, sequenceId);
            doc.add(areaBreak);

            List<AbstractProductProcessITextEvent> events = CONFIGURATION_ACCESS.getPublicEvents(
                    doc.getPdfDocument().getDocumentIdWrapper());
            Assertions.assertEquals(1, events.size());

            Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
        }
    }

    @Test
    public void addImageElemMethodLinkingTest() {
        try (Document doc = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            SequenceId sequenceId = new SequenceId();
            EventManager.getInstance().onEvent(new TestProductEvent(sequenceId));

            Image image = new Image(new PdfFormXObject(new Rectangle(10, 10)));
            SequenceIdManager.setSequenceId(image, sequenceId);
            doc.add(image);

            List<AbstractProductProcessITextEvent> events = CONFIGURATION_ACCESS.getPublicEvents(
                    doc.getPdfDocument().getDocumentIdWrapper());
            // Second event was linked by adding block element
            Assertions.assertEquals(2, events.size());

            Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
            Assertions.assertTrue(events.get(1) instanceof TestProductEvent);
        }
    }

    @Test
    public void relayoutWithImmediateFlushTest() {
        try (Document document = new Document(new PdfDocument(new PdfWriter(new ByteArrayOutputStream())))) {
            IllegalStateException exception = (IllegalStateException) Assertions.assertThrows(
                    IllegalStateException.class, () -> document.relayout());
            Assertions.assertEquals("Operation not supported with immediate flush", exception.getMessage());
        }
    }

    @Test
    public void relayoutWithInvalidNextRendererTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
        NullNextRendererDocumentRenderer customRenderer = new NullNextRendererDocumentRenderer(document);
        document.setRenderer(customRenderer);
        try {
            document.add(new Paragraph("fallback renderer paragraph"));

            document.relayout();

            Assertions.assertTrue(customRenderer.isRemoveMarginBoxesEventHandlerCalled());
            Assertions.assertNotSame(customRenderer, document.getRenderer());
            Assertions.assertEquals(DocumentRenderer.class, document.getRenderer().getClass());
            Assertions.assertDoesNotThrow(() -> document.close());
        } finally {
            if (!pdfDocument.isClosed()) {
                document.close();
            }
        }
    }

    @Test
    public void relayoutWithSameNextRendererTest() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
        SameNextRendererDocumentRenderer customRenderer = new SameNextRendererDocumentRenderer(document);
        document.setRenderer(customRenderer);
        try {
            document.add(new Paragraph("same renderer paragraph"));

            document.relayout();

            Assertions.assertFalse(customRenderer.isRemoveMarginBoxesEventHandlerCalled());
            Assertions.assertSame(customRenderer, document.getRenderer());
            Assertions.assertDoesNotThrow(() -> document.close());
        } finally {
            if (!pdfDocument.isClosed()) {
                document.close();
            }
        }
    }

    @Test
    public void relayoutDoesNotKeepWrongEventHandlersDocumentRendererTest() {
        ThrowOnTooManyGetPagePdfDocument pdfDocument =
                new ThrowOnTooManyGetPagePdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Document document = new Document(pdfDocument, pdfDocument.getDefaultPageSize(), false);
        try {
            pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new GetPageProbeOnEndPageEventHandler());
            document.setPageMargins(1, new PageMarginBoxes(Collections.singletonList(
                    new PageMarginContent(MarginBoxName.TOP, 24f))));
            document.add(new Paragraph("test paragraph"));
            document.relayout();

            pdfDocument.resetGetPageCalls();
            pdfDocument.setMaxGetPageCalls(5);
            Assertions.assertDoesNotThrow(() -> document.close());
            Assertions.assertEquals(5, pdfDocument.getPageCalls());
        } finally {
            if (!pdfDocument.isClosed()) {
                document.close();
            }
        }
    }

    @Test
    public void processedPagesKeepMarginsAfterPredicateTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String marker = "LATE_MARGIN_MARKER";

        try (Document document = new Document(new PdfDocument(new PdfWriter(baos)))) {
            for (int i = 0; i < 80; i++) {
                document.add(new Paragraph("Paragraph " + i + " ").setMarginBottom(12));
            }

            document.flush();
            Assertions.assertTrue(document.getPdfDocument().getNumberOfPages() >= 2);

            PageMarginBoxes pageMargins = new PageMarginBoxes(Collections.singletonList(
                    new PageMarginContent(MarginBoxName.TOP,
                            new Div().add(new Paragraph(marker)).setHeight(40))));
            document.setPageMargins(pageNum -> pageNum % 2 == 0, pageMargins);
        }

        try (PdfDocument result = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())))) {
            String secondPageText = PdfTextExtractor.getTextFromPage(result.getPage(2));
            Assertions.assertFalse(secondPageText.contains(marker));
        }
    }

    @Test
    public void marginsAreDrawnOnPagesAfterPredicateTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String marker = "FUTURE_PAGE_MARGIN_MARKER";

        try (Document document = new Document(new PdfDocument(new PdfWriter(baos)))) {
            document.add(new Paragraph("Page 1 content"));
            document.flush();

            PageMarginBoxes pageMargins = new PageMarginBoxes(Collections.singletonList(
                    new PageMarginContent(MarginBoxName.TOP,
                            new Div().add(new Paragraph(marker)).setHeight(30))));
            document.setPageMargins(pageNum -> pageNum < 5, pageMargins);

            document.add(new AreaBreak());
            document.add(new Paragraph("Page 2 content"));
        }

        try (PdfDocument result = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())))) {
            String firstPageText = PdfTextExtractor.getTextFromPage(result.getPage(1));
            String secondPageText = PdfTextExtractor.getTextFromPage(result.getPage(2));

            Assertions.assertFalse(firstPageText.contains(marker));
            Assertions.assertTrue(secondPageText.contains(marker));
        }
    }

    @Test
    public void noMarginsOnOldPagesButDrawnOnNewPagesTest() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String marker = "EVEN_LATER_PAGE_MARGIN_MARKER";

        try (Document document = new Document(new PdfDocument(new PdfWriter(baos)))) {
            document.add(new Paragraph("Page 1 content"));
            document.add(new AreaBreak());
            document.add(new Paragraph("Page 2 content"));
            document.flush();
            Assertions.assertTrue(document.getPdfDocument().getNumberOfPages() >= 2);

            PageMarginBoxes pageMargins = new PageMarginBoxes(Collections.singletonList(
                    new PageMarginContent(MarginBoxName.TOP,
                            new Div().add(new Paragraph(marker)).setHeight(30))));
            document.setPageMargins(pageNum -> pageNum % 2 == 0, pageMargins);

            document.add(new AreaBreak());
            document.add(new Paragraph("Page 3 content"));
            document.add(new AreaBreak());
            document.add(new Paragraph("Page 4 content"));
        }

        try (PdfDocument result = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())))) {
            String page2Text = PdfTextExtractor.getTextFromPage(result.getPage(2));
            String page4Text = PdfTextExtractor.getTextFromPage(result.getPage(4));

            Assertions.assertFalse(page2Text.contains(marker));
            Assertions.assertTrue(page4Text.contains(marker));
        }
    }

    private static final class ThrowOnTooManyGetPagePdfDocument extends PdfDocument {
        private int pageCalls = 0;
        private int maxGetPageCalls = Integer.MAX_VALUE;

        public ThrowOnTooManyGetPagePdfDocument(PdfWriter writer) {
            super(writer);
        }

        @Override
        public PdfPage getPage(int pageNum) {
            ++pageCalls;
            if (pageCalls > maxGetPageCalls) {
                throw new IllegalStateException("getPage(int) called too many times: " + pageCalls
                        + " (max " + maxGetPageCalls + ")");
            }
            return super.getPage(pageNum);
        }

        public void resetGetPageCalls() {
            pageCalls = 0;
        }

        public void setMaxGetPageCalls(int maxGetPageCalls) {
            this.maxGetPageCalls = maxGetPageCalls;
        }

        public int getPageCalls() {
            return pageCalls;
        }
    }

    private static final class GetPageProbeOnEndPageEventHandler extends AbstractPdfDocumentEventHandler {
        @Override
        public void onAcceptedEvent(AbstractPdfDocumentEvent event) {
            if (event instanceof PdfDocumentEvent) {
                PdfDocumentEvent pageEvent = (PdfDocumentEvent) event;
                int pageNumber = event.getDocument().getPageNumber(pageEvent.getPage());
                event.getDocument().getPage(pageNumber);
            }
        }
    }

    private static final class NullNextRendererDocumentRenderer extends DocumentRenderer {
        private boolean removeMarginBoxesEventHandlerCalled;

        public NullNextRendererDocumentRenderer(Document document) {
            super(document, false);
        }

        @Override
        public IRenderer getNextRenderer() {
            return null;
        }

        @Override
        public void removeEventHandlersForRelayout() {
            removeMarginBoxesEventHandlerCalled = true;
            super.removeEventHandlersForRelayout();
        }

        public boolean isRemoveMarginBoxesEventHandlerCalled() {
            return removeMarginBoxesEventHandlerCalled;
        }
    }

    private static final class SameNextRendererDocumentRenderer extends DocumentRenderer {
        private boolean removeMarginBoxesEventHandlerCalled;

        public SameNextRendererDocumentRenderer(Document document) {
            super(document, false);
        }

        @Override
        public IRenderer getNextRenderer() {
            return this;
        }

        @Override
        public void removeEventHandlersForRelayout() {
            removeMarginBoxesEventHandlerCalled = true;
            super.removeEventHandlersForRelayout();
        }

        public boolean isRemoveMarginBoxesEventHandlerCalled() {
            return removeMarginBoxesEventHandlerCalled;
        }
    }
}
