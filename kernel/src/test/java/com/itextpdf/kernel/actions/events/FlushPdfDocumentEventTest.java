/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.events.data.ProductData;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.actions.AbstractProductProcessITextEvent;
import com.itextpdf.kernel.actions.EventManager;
import com.itextpdf.kernel.actions.ProductEventHandlerAccess;
import com.itextpdf.kernel.actions.data.ITextCoreProductData;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.actions.session.ClosingSession;
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
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class FlushPdfDocumentEventTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/actions/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void doActionTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {
            List<String> forMessages = new ArrayList<>();

            access.addProcessor(new TestProductEventProcessor("test-product-1", forMessages));
            access.addProcessor(new TestProductEventProcessor("test-product-2", forMessages));

            access.addEvent(document.getDocumentIdWrapper(), getEvent("test-product-1", document.getDocumentIdWrapper()));
            access.addEvent(document.getDocumentIdWrapper(), getEvent("test-product-1", document.getDocumentIdWrapper()));
            access.addEvent(document.getDocumentIdWrapper(), getEvent("test-product-2", document.getDocumentIdWrapper()));
            access.addEvent(document.getDocumentIdWrapper(), getEvent("test-product-2", document.getDocumentIdWrapper()));

            new FlushPdfDocumentEvent(document).doAction();

            Assert.assertEquals(4, forMessages.size());
            Assert.assertTrue(forMessages.contains("aggregation message from test-product-1"));
            Assert.assertTrue(forMessages.contains("aggregation message from test-product-2"));
            Assert.assertTrue(forMessages.contains("completion message from test-product-1"));
            Assert.assertTrue(forMessages.contains("completion message from test-product-2"));

            // check order
            Assert.assertTrue(forMessages.get(0).startsWith("aggregation"));
            Assert.assertTrue(forMessages.get(1).startsWith("aggregation"));
            Assert.assertTrue(forMessages.get(2).startsWith("completion"));
            Assert.assertTrue(forMessages.get(3).startsWith("completion"));
        }
    }

    @Test
    public void onCloseReportingTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            ITextTestEvent event = new ITextTestEvent(document.getDocumentIdWrapper(), ITextCoreProductData.getInstance(), null, "test-event", EventConfirmationType.ON_CLOSE);
            int initialLength = access.getEvents(document.getDocumentIdWrapper()).size();

            EventManager.getInstance().onEvent(event);
            new FlushPdfDocumentEvent(document).doAction();

            AbstractProductProcessITextEvent reportedEvent = access.getEvents(document.getDocumentIdWrapper()).get(initialLength);
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
            int initialLength = access.getEvents(document.getDocumentIdWrapper()).size();

            EventManager.getInstance().onEvent(event);
            new FlushPdfDocumentEvent(document).doAction();

            AbstractProductProcessITextEvent reportedEvent = access.getEvents(document.getDocumentIdWrapper()).get(initialLength);
            Assert.assertFalse(reportedEvent instanceof ConfirmedEventWrapper);
        }
    }

    @Test
    public void onDemandReportingConfirmedTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            ITextTestEvent event = new ITextTestEvent(document.getDocumentIdWrapper(), ITextCoreProductData.getInstance(), null, "test-event", EventConfirmationType.ON_DEMAND);
            int initialLength = access.getEvents(document.getDocumentIdWrapper()).size();

            EventManager.getInstance().onEvent(event);

            AbstractProductProcessITextEvent reportedEvent = access.getEvents(document.getDocumentIdWrapper()).get(initialLength);
            Assert.assertFalse(reportedEvent instanceof ConfirmedEventWrapper);
            Assert.assertEquals(event, reportedEvent);

            EventManager.getInstance().onEvent(new ConfirmEvent(document.getDocumentIdWrapper(), event));
            new FlushPdfDocumentEvent(document).doAction();

            AbstractProductProcessITextEvent confirmedEvent = access.getEvents(document.getDocumentIdWrapper()).get(initialLength);
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

            access.addEvent(document.getDocumentIdWrapper(), getEvent("unknown product", document.getDocumentIdWrapper()));

            AssertUtil.doesNotThrow(() -> new FlushPdfDocumentEvent(document).doAction());
        }
    }

    @Test
    public void doActionNullDocumentTest() {
        FlushPdfDocumentEvent closeEvent = new FlushPdfDocumentEvent(null);
        AssertUtil.doesNotThrow(() -> closeEvent.doAction());
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
            Assert.assertNotEquals(producerLine.indexOf(modifiedByItext), producerLine.lastIndexOf(modifiedByItext));
        }
    }

    private static class TestProductEventProcessor implements ITextProductEventProcessor {
        public final List<String> aggregatedMessages;
        private final String processorId;

        public TestProductEventProcessor(String processorId, List<String> aggregatedMessages) {
            this.processorId = processorId;
            this.aggregatedMessages = aggregatedMessages;
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

        @Override
        public void aggregationOnClose(ClosingSession session) {
            aggregatedMessages.add("aggregation message from " + processorId);
        }

        @Override
        public void completionOnClose(ClosingSession session) {
            aggregatedMessages.add("completion message from " + processorId);
        }
    }

    private static ConfirmedEventWrapper getEvent(String productName, SequenceId sequenceId) {
        ProductData productData = new ProductData(productName, productName, "2.0", 1999, 2020);
        return new ConfirmedEventWrapper(new ITextTestEvent(sequenceId, productData, null, "testing"), "AGPL Version", "iText");
    }

}
