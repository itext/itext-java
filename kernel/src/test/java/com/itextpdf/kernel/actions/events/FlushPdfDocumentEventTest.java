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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.confirmations.EventConfirmationType;
import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.commons.actions.processors.ITextProductEventProcessor;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.actions.ProductEventHandlerAccess;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class FlushPdfDocumentEventTest extends ExtendedITextTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/actions/";

    @Test
    public void onCloseReportingTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            ITextTestEvent event = new ITextTestEvent(document.getDocumentIdWrapper(), ITextCoreProductData.getInstance(), null, "test-event", EventConfirmationType.ON_CLOSE);
            int initialLength = access.publicGetEvents(document.getDocumentIdWrapper()).size();

            EventManager.getInstance().onEvent(event);
            new FlushPdfDocumentEvent(document).doAction();

            AbstractProductProcessITextEvent reportedEvent = access.publicGetEvents(document.getDocumentIdWrapper()).get(initialLength);
            Assert.assertTrue(reportedEvent instanceof ConfirmedEventWrapper);
            ConfirmedEventWrapper wrappedEvent = (ConfirmedEventWrapper) reportedEvent;
            Assert.assertEquals(event, wrappedEvent.getEvent());
        }
    }

    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = KernelLogMessageConstant.UNCONFIRMED_EVENT)
            }
    )
    public void onDemandReportingIgnoredTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            ITextTestEvent event = new ITextTestEvent(document.getDocumentIdWrapper(),
                    ITextCoreProductData.getInstance(), null, "test-event", EventConfirmationType.ON_DEMAND);
            int initialLength = access.publicGetEvents(document.getDocumentIdWrapper()).size();

            EventManager.getInstance().onEvent(event);
            new FlushPdfDocumentEvent(document).doAction();

            AbstractProductProcessITextEvent reportedEvent = access.publicGetEvents(document.getDocumentIdWrapper()).get(initialLength);
            Assert.assertFalse(reportedEvent instanceof ConfirmedEventWrapper);
        }
    }

    @Test
    public void onDemandReportingConfirmedTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            ITextTestEvent event = new ITextTestEvent(document.getDocumentIdWrapper(), ITextCoreProductData.getInstance(), null, "test-event", EventConfirmationType.ON_DEMAND);
            int initialLength = access.publicGetEvents(document.getDocumentIdWrapper()).size();

            EventManager.getInstance().onEvent(event);

            AbstractProductProcessITextEvent reportedEvent = access.publicGetEvents(document.getDocumentIdWrapper()).get(initialLength);
            Assert.assertFalse(reportedEvent instanceof ConfirmedEventWrapper);
            Assert.assertEquals(event, reportedEvent);

            EventManager.getInstance().onEvent(new ConfirmEvent(document.getDocumentIdWrapper(), event));
            new FlushPdfDocumentEvent(document).doAction();

            AbstractProductProcessITextEvent confirmedEvent = access.publicGetEvents(document.getDocumentIdWrapper()).get(initialLength);
            Assert.assertTrue(confirmedEvent instanceof ConfirmedEventWrapper);
            ConfirmedEventWrapper wrappedEvent = (ConfirmedEventWrapper) confirmedEvent;
            Assert.assertEquals(event, wrappedEvent.getEvent());
        }
    }

    @Test
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = KernelLogMessageConstant.UNKNOWN_PRODUCT_INVOLVED)
            }
    )
    public void unknownProductTest() throws IOException {
            try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                    PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            access.publicAddEvent(document.getDocumentIdWrapper(), getEvent("unknown product", document.getDocumentIdWrapper()));

            AssertUtil.doesNotThrow(() -> new FlushPdfDocumentEvent(document).doAction());
        }
    }

    @Test
    public void doActionNullDocumentTest() {
        FlushPdfDocumentEvent closeEvent = new FlushPdfDocumentEvent(null);
        AssertUtil.doesNotThrow(() -> closeEvent.doAction());
    }

    @Test
    public void doActionNullEventMapTest() throws IOException {
        try (PdfDocument document = new DummyPdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {
            AssertUtil.doesNotThrow(() -> new FlushPdfDocumentEvent(document).doAction());
            Assert.assertTrue(document.getDocumentInfo().getProducer()
                    .contains("Apryse Group NV (no registered products)"));
        }
    }

    @Test
    public void flushEventAfterEachEventTest() throws IOException {
        String resourceInit = SOURCE_FOLDER + "hello.pdf";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdf = new PdfDocument(new PdfReader(resourceInit), new PdfWriter(baos))) {
            pdf.addNewPage();
            EventManager.getInstance().onEvent(new FlushPdfDocumentEvent(pdf));
        }

        try (PdfDocument pdf = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())))) {
            String producerLine = pdf.getDocumentInfo().getProducer();
            String modifiedByItext = "modified using iText\u00ae Core";
            Assert.assertEquals(producerLine.indexOf(modifiedByItext), producerLine.lastIndexOf(modifiedByItext));
        }
    }

    private static class DummyPdfDocument extends PdfDocument {

        public DummyPdfDocument(PdfReader reader) {
            super(reader);
        }

        public SequenceId getDocumentIdWrapper() {
            return null;
        }
    }

    private static class TestProductEventProcessor implements ITextProductEventProcessor {
        private final String processorId;

        public TestProductEventProcessor(String processorId) {
            this.processorId = processorId;
        }

        @Override
        public void onEvent(AbstractProductProcessITextEvent event) {
            // do nothing here
        }

        @Override
        public String getProductName() {
            return processorId;
        }

        @Override
        public String getUsageType() {
            return "AGPL Version";
        }

        @Override
        public String getProducer() {
            return "iText";
        }
    }

    private static ConfirmedEventWrapper getEvent(String productName, SequenceId sequenceId) {
        ProductData productData = new ProductData(productName, productName, "2.0", 1999, 2020);
        return new ConfirmedEventWrapper(new ITextTestEvent(sequenceId, productData, null, "testing"), "AGPL Version", "iText");
    }
}
