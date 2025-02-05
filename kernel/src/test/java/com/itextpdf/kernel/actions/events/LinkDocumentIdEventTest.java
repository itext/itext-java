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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.sequence.AbstractIdentifiableElement;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.actions.sequence.SequenceIdManager;
import com.itextpdf.kernel.actions.ProductEventHandlerAccess;
import com.itextpdf.kernel.actions.ecosystem.ITextTestEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class LinkDocumentIdEventTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/actions/";

    @Test
    public void doActionLinkModifiedDocumentBySequenceIdTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            SequenceId sequenceId = new SequenceId();
            access.publicAddEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-0")));
            access.publicAddEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-1")));
            access.publicAddEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-2")));

            access.publicAddEvent(document.getDocumentIdWrapper(), wrapEvent(new ITextTestEvent(document.getDocumentIdWrapper(), null, "document-testing",
                    "test-product-3")));
            access.publicAddEvent(document.getDocumentIdWrapper(), wrapEvent(new ITextTestEvent(document.getDocumentIdWrapper(), null, "document-testing",
                    "test-product-4")));


            int initialSequenceEventsNumber = access.publicGetEvents(sequenceId).size();
            int initialDocumentEventsNumber = access.publicGetEvents(document.getDocumentIdWrapper()).size();

            new LinkDocumentIdEvent(document, sequenceId).doAction();

            Assertions.assertEquals(initialSequenceEventsNumber, access.publicGetEvents(sequenceId).size());

            List<AbstractProductProcessITextEvent> actualDocumentEvents = access.publicGetEvents(document.getDocumentIdWrapper());
            Assertions.assertEquals(initialDocumentEventsNumber + 3, actualDocumentEvents.size());

            for (int i = initialDocumentEventsNumber; i < initialDocumentEventsNumber + 3; i++) {
                AbstractProductProcessITextEvent sequenceEvent = actualDocumentEvents.get(i);
                Assertions.assertEquals("sequenceId-testing", sequenceEvent.getEventType());
                Assertions.assertEquals("test-product-" + (i - initialDocumentEventsNumber), sequenceEvent.getProductName());
                Assertions.assertEquals(sequenceId, sequenceEvent.getSequenceId());
            }
        }
    }

    @Test
    public void doActionLinkModifiedDocumentByIdentifiableElemTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            SequenceId sequenceId = new SequenceId();
            access.publicAddEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-0")));
            access.publicAddEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-1")));
            access.publicAddEvent(sequenceId, wrapEvent(new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-2")));

            access.publicAddEvent(document.getDocumentIdWrapper(), wrapEvent(new ITextTestEvent(document.getDocumentIdWrapper(), null, "document-testing",
                    "test-product-3")));
            access.publicAddEvent(document.getDocumentIdWrapper(), wrapEvent(new ITextTestEvent(document.getDocumentIdWrapper(), null, "document-testing",
                    "test-product-4")));


            int initialSequenceEventsNumber = access.publicGetEvents(sequenceId).size();
            int initialDocumentEventsNumber = access.publicGetEvents(document.getDocumentIdWrapper()).size();

            IdentifiableElement identifiableElement = new IdentifiableElement();
            SequenceIdManager.setSequenceId(identifiableElement, sequenceId);
            new LinkDocumentIdEvent(document, identifiableElement).doAction();

            Assertions.assertEquals(initialSequenceEventsNumber, access.publicGetEvents(sequenceId).size());

            List<AbstractProductProcessITextEvent> actualDocumentEvents = access.publicGetEvents(document.getDocumentIdWrapper());
            Assertions.assertEquals(initialDocumentEventsNumber + 3, actualDocumentEvents.size());

            for (int i = initialDocumentEventsNumber; i < initialDocumentEventsNumber + 3; i++) {
                AbstractProductProcessITextEvent sequenceEvent = actualDocumentEvents.get(i);
                Assertions.assertEquals("sequenceId-testing", sequenceEvent.getEventType());
                Assertions.assertEquals("test-product-" + (i - initialDocumentEventsNumber), sequenceEvent.getProductName());
                Assertions.assertEquals(sequenceId, sequenceEvent.getSequenceId());
            }
        }
    }

    @Test
    public void linkSimilarEventsButDifferentInstanceTest() throws IOException {
        try (ProductEventHandlerAccess access = new ProductEventHandlerAccess();
                PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {

            SequenceId sequenceId = new SequenceId();
            access.publicAddEvent(sequenceId, new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-1"));

            access.publicAddEvent(document.getDocumentIdWrapper(), new ITextTestEvent(sequenceId, null, "sequenceId-testing",
                    "test-product-1"));

            new LinkDocumentIdEvent(document, sequenceId).doAction();

            // Check that first event will be linked to document but it was the
            // similar to stored second event, but they have different instance
            Assertions.assertEquals(3, access.publicGetEvents(document.getDocumentIdWrapper()).size());

        }
    }

    @Test
    public void nullValuesAreAcceptableTest() throws IOException {
        AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(null, (SequenceId) null));
        AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(null, (AbstractIdentifiableElement) null));

        AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(null, new SequenceId()));
        AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(null, new IdentifiableElement()));

        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hello.pdf"))) {
            AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(document, (SequenceId) null));
            AssertUtil.doesNotThrow(() -> new LinkDocumentIdEvent(document, (AbstractIdentifiableElement) null));
        }
    }

    private static ConfirmedEventWrapper wrapEvent(AbstractProductProcessITextEvent event) {
        return new ConfirmedEventWrapper(event, "AGPL Version", "iText");
    }

    private static class IdentifiableElement extends AbstractIdentifiableElement {

    }
}
