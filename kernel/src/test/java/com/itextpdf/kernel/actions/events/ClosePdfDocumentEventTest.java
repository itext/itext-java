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

import com.itextpdf.kernel.KernelLogMessageConstant;
import com.itextpdf.kernel.actions.ProductEventHandlerAccess;
import com.itextpdf.kernel.actions.ProductNameConstant;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;
import com.itextpdf.kernel.actions.session.ClosingSession;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ClosePdfDocumentEventTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/actions/";

    @Test
    public void fieldsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {
            ClosePdfDocumentEvent event = new ClosePdfDocumentEvent(document);
            Assert.assertEquals("close-document-event", event.getEventType());
            Assert.assertEquals(ProductNameConstant.ITEXT_CORE, event.getProductName());
        }
    }

    @Test
    public void doActionTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {
            List<String> forMessages = new ArrayList<>();

            access.addProcessor("test-product-1", new TestProductEventProcessor("test-product-1", forMessages));
            access.addProcessor("test-product-2", new TestProductEventProcessor("test-product-2", forMessages));

            access.addEvent(document.getDocumentIdWrapper(), new ITextTestEvent(document, null, "testing", "test-product-1"));
            access.addEvent(document.getDocumentIdWrapper(), new ITextTestEvent(document, null, "testing", "test-product-1"));
            access.addEvent(document.getDocumentIdWrapper(), new ITextTestEvent(document, null, "testing", "test-product-2"));
            access.addEvent(document.getDocumentIdWrapper(), new ITextTestEvent(document, null, "testing", "test-product-2"));

            new ClosePdfDocumentEvent(document).doAction();

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
    @LogMessages(
            messages = {
                    @LogMessage(messageTemplate = KernelLogMessageConstant.UNKNOWN_PRODUCT_INVOLVED, count = 2)
            }
    )
    public void unknownProductTest() throws IOException {
            try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                    PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            access.addEvent(document.getDocumentIdWrapper(), new ITextTestEvent(document, null, "testing", "unknown product"));

            AssertUtil.doesNotThrow(() ->new ClosePdfDocumentEvent(document).doAction());
        }
    }

    @Test
    public void doActionNullDocumentTest() {
        ClosePdfDocumentEvent closeEvent = new ClosePdfDocumentEvent(null);
        AssertUtil.doesNotThrow(() -> closeEvent.doAction());
    }

    private static class TestProductEventProcessor implements ITextProductEventProcessor {
        public final List<String> aggregatedMessages;
        private final String processorId;

        public TestProductEventProcessor(String processorId, List<String> aggregatedMessages) {
            this.processorId = processorId;
            this.aggregatedMessages = aggregatedMessages;
        }

        @Override
        public void onEvent(AbstractITextProductEvent event) {
            // do nothing here
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
}
