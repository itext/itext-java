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
package com.itextpdf.kernel.actions;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.actions.events.AbstractITextProductEvent;
import com.itextpdf.kernel.actions.events.ITextProductEventWrapper;
import com.itextpdf.kernel.actions.exceptions.UnknownProductException;
import com.itextpdf.kernel.actions.processors.DefaultITextProductEventProcessor;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class ProductEventHandlerTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/actions/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void unknownProductTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        junitExpectedException.expect(UnknownProductException.class);
        junitExpectedException.expectMessage(
                MessageFormatUtil.format(UnknownProductException.UNKNOWN_PRODUCT, "Unknown Product"));
        
        handler.onAcceptedEvent(new ITextTestEvent(new SequenceId(), null, "test-event",
                "Unknown Product"));
    }

    @Test
    public void sequenceIdBasedEventTest() {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        SequenceId sequenceId = new SequenceId();

        Assert.assertTrue(handler.getEvents(sequenceId).isEmpty());

        handler.onAcceptedEvent(new ITextTestEvent(sequenceId, null, "test-event",
                ProductNameConstant.ITEXT_CORE));

        Assert.assertEquals(1, handler.getEvents(sequenceId).size());

        ITextProductEventWrapper wrapper = handler.getEvents(sequenceId).get(0);
        DefaultITextProductEventProcessor processor = new DefaultITextProductEventProcessor(ProductNameConstant.ITEXT_CORE);
        Assert.assertEquals(processor.getUsageType(), wrapper.getProductUsageType());
        Assert.assertEquals(processor.getProducer(), wrapper.getProducerLine());

        AbstractITextProductEvent event = handler.getEvents(sequenceId).get(0).getEvent();
        Assert.assertEquals(sequenceId.getId(), event.getSequenceId().getId());
        Assert.assertNull(event.getMetaInfo());
        Assert.assertEquals("test-event", event.getEventType());
        Assert.assertEquals(ProductNameConstant.ITEXT_CORE, event.getProductName());
    }

    @Test
    public void documentIdBasedEventTest() throws IOException {
        ProductEventHandler handler = ProductEventHandler.INSTANCE;

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            int alreadyRegisteredEvents = handler.getEvents(document.getDocumentIdWrapper()).size();
            handler.onAcceptedEvent(new ITextTestEvent(document.getDocumentIdWrapper(), null, "test-event",
                    ProductNameConstant.ITEXT_CORE));

            Assert.assertEquals(alreadyRegisteredEvents + 1, handler.getEvents(document.getDocumentIdWrapper()).size());

            DefaultITextProductEventProcessor processor = new DefaultITextProductEventProcessor(ProductNameConstant.ITEXT_CORE);

            ITextProductEventWrapper wrapper = handler.getEvents(document.getDocumentIdWrapper()).get(alreadyRegisteredEvents);
            Assert.assertEquals(processor.getProducer(), wrapper.getProducerLine());
            Assert.assertEquals(processor.getUsageType(), wrapper.getProductUsageType());

            AbstractITextProductEvent event = wrapper.getEvent();
            Assert.assertEquals(document.getDocumentIdWrapper(), event.getSequenceId());
            Assert.assertNull(event.getMetaInfo());
            Assert.assertEquals("test-event", event.getEventType());
            Assert.assertEquals(ProductNameConstant.ITEXT_CORE, event.getProductName());
            Assert.assertNull(event.getProductData());
        }
    }
}
