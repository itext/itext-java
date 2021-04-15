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
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class LinkDocumentIdEventTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/actions/";

    @Test
    public void propertiesTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            SequenceId sequenceId = new SequenceId();
            LinkDocumentIdEvent event = new LinkDocumentIdEvent(document, sequenceId, ProductNameConstant.ITEXT_CORE);

            Assert.assertEquals("link-document-id-event", event.getEventType());
            Assert.assertEquals(ProductNameConstant.ITEXT_CORE, event.getProductName());
        }
    }

    @Test
    public void doActionLinkModifiedDocumentTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            SequenceId sequenceId = new SequenceId();
            access.addEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-0")));
            access.addEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-1")));
            access.addEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-2")));

            access.addEvent(document.getDocumentIdWrapper(), wrapEvent(new ITextTestEvent(document.getDocumentIdWrapper(), null, "document-testing",
                    "test-product-3")));
            access.addEvent(document.getDocumentIdWrapper(), wrapEvent(new ITextTestEvent(document.getDocumentIdWrapper(), null, "document-testing",
                    "test-product-4")));


            int initialSequenceEventsNumber = access.getEvents(sequenceId).size();
            int initialDocumentEventsNumber = access.getEvents(document.getDocumentIdWrapper()).size();

            new LinkDocumentIdEvent(document, sequenceId, ProductNameConstant.ITEXT_CORE).doAction();

            Assert.assertEquals(initialSequenceEventsNumber, access.getEvents(sequenceId).size());

            List<ITextProductEventWrapper> actualDocumentEvents = access.getEvents(document.getDocumentIdWrapper());
            Assert.assertEquals(initialDocumentEventsNumber + 3, actualDocumentEvents.size());

            for (int i = initialDocumentEventsNumber; i < initialDocumentEventsNumber + 3; i++) {
                AbstractITextProductEvent sequenceEvent = actualDocumentEvents.get(i).getEvent();
                Assert.assertEquals("sequenceId-testing", sequenceEvent.getEventType());
                Assert.assertEquals("test-product-" + (i - initialDocumentEventsNumber), sequenceEvent.getProductName());
                Assert.assertNull(sequenceEvent.getMetaInfo());
                Assert.assertEquals(sequenceId, sequenceEvent.getSequenceId());
            }
        }
    }

    @Test
    public void nullValuesAreAcceptableTest() throws IOException {
        AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(null, null, ProductNameConstant.ITEXT_CORE));
        AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(null, new SequenceId(), ProductNameConstant.ITEXT_CORE));
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {
            AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(document, null, ProductNameConstant.ITEXT_CORE));
        }
    }

    private static ITextProductEventWrapper wrapEvent(AbstractITextProductEvent event) {
        return new ITextProductEventWrapper(event, "AGPL Version", "iText");
    }
}
