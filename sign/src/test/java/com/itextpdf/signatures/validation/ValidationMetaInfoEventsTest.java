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
package com.itextpdf.signatures.validation;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.actions.AbstractContextBasedEventHandler;
import com.itextpdf.commons.actions.AbstractContextBasedITextEvent;
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.contexts.IContext;
import com.itextpdf.commons.actions.contexts.UnknownContext;
import com.itextpdf.kernel.actions.events.ITextCoreProductEvent;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class ValidationMetaInfoEventsTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/ValidationMetaInfoEventsTest/";

    private static StoreEventsHandler handler;
    private final ValidatorChainBuilder builder = new ValidatorChainBuilder();
    private final ValidationContext validationContext = new ValidationContext(
            ValidatorContext.DOCUMENT_REVISIONS_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);

    @BeforeAll
    public static void before() {
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
    }

    @BeforeEach
    public void setUpHandler() {
        handler = new StoreEventsHandler(UnknownContext.PERMISSIVE);
        EventManager.getInstance().register(handler);
    }

    @AfterEach
    public void resetHandler() {
        EventManager.getInstance().unregister(handler);
    }

    @Test
    public void documentRevisionsValidatorSingleEventTest() throws Exception {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.validateAllDocumentRevisions(validationContext, document);
        }

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
        ITextCoreProductEvent iTextCoreProductEvent = (ITextCoreProductEvent) events.get(0);
        Assertions.assertEquals(ITextCoreProductEvent.PROCESS_PDF, iTextCoreProductEvent.getEventType());
        // Only first iTextCoreProductEvent is confirmed.
        Assertions.assertTrue(events.get(1) instanceof ConfirmEvent);
        ConfirmEvent confirmEvent = (ConfirmEvent) events.get(1);
        Assertions.assertEquals(iTextCoreProductEvent, confirmEvent.getConfirmedEvent());
    }

    @Test
    public void documentRevisionsValidatorZeroEventsTest() throws Exception {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument.pdf"),
                new DocumentProperties().setEventCountingMetaInfo(new ValidationMetaInfo()))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setEventCountingMetaInfo(new ValidationMetaInfo());
            validator.validateAllDocumentRevisions(validationContext, document);
        }

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assertions.assertEquals(0, events.size());
    }

    @Test
    public void signatureValidatorSingleEventTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument.pdf"))) {
            SignatureValidator validator = builder.buildSignatureValidator(document);
            validator.validateSignatures();
        }

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assertions.assertEquals(2, events.size());
        Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
        ITextCoreProductEvent iTextCoreProductEvent = (ITextCoreProductEvent) events.get(0);
        Assertions.assertEquals(ITextCoreProductEvent.PROCESS_PDF, iTextCoreProductEvent.getEventType());
        // Only first iTextCoreProductEvent is confirmed.
        Assertions.assertTrue(events.get(1) instanceof ConfirmEvent);
        ConfirmEvent confirmEvent = (ConfirmEvent) events.get(1);
        Assertions.assertEquals(iTextCoreProductEvent, confirmEvent.getConfirmedEvent());
    }

    @Test
    public void signatureValidatorZeroEventsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument.pdf"),
                new DocumentProperties().setEventCountingMetaInfo(new ValidationMetaInfo()))) {
            SignatureValidator validator = builder.buildSignatureValidator(document);
            validator.setEventCountingMetaInfo(new ValidationMetaInfo());
            validator.validateSignatures();
        }

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assertions.assertEquals(0, events.size());
    }

    private static class StoreEventsHandler extends AbstractContextBasedEventHandler {
        private final List<AbstractContextBasedITextEvent> events = new ArrayList<>();

        protected StoreEventsHandler(IContext onUnknownContext) {
            super(onUnknownContext);
        }

        public List<AbstractContextBasedITextEvent> getEvents() {
            return events;
        }

        @Override
        protected void onAcceptedEvent(AbstractContextBasedITextEvent event) {
            events.add(event);
        }
    }
}
