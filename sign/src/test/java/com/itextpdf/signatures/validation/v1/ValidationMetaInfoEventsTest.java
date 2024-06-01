package com.itextpdf.signatures.validation.v1;

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
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class ValidationMetaInfoEventsTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/ValidationMetaInfoEventsTest/";

    private static StoreEventsHandler handler;
    private final ValidatorChainBuilder builder = new ValidatorChainBuilder();
    private final ValidationContext validationContext = new ValidationContext(
            ValidatorContext.DOCUMENT_REVISIONS_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);

    @BeforeClass
    public static void before() {
        Security.addProvider(BouncyCastleFactoryCreator.getFactory().getProvider());
    }

    @Before
    public void setUpHandler() {
        handler = new StoreEventsHandler(UnknownContext.PERMISSIVE);
        EventManager.getInstance().register(handler);
    }

    @After
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
        Assert.assertEquals(2, events.size());
        Assert.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
        ITextCoreProductEvent iTextCoreProductEvent = (ITextCoreProductEvent) events.get(0);
        Assert.assertEquals(ITextCoreProductEvent.PROCESS_PDF, iTextCoreProductEvent.getEventType());
        // Only first iTextCoreProductEvent is confirmed.
        Assert.assertTrue(events.get(1) instanceof ConfirmEvent);
        ConfirmEvent confirmEvent = (ConfirmEvent) events.get(1);
        Assert.assertEquals(iTextCoreProductEvent, confirmEvent.getConfirmedEvent());
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
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void signatureValidatorSingleEventTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument.pdf"))) {
            SignatureValidator validator = builder.buildSignatureValidator();
            validator.validateSignatures(document);
        }

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assert.assertEquals(2, events.size());
        Assert.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
        ITextCoreProductEvent iTextCoreProductEvent = (ITextCoreProductEvent) events.get(0);
        Assert.assertEquals(ITextCoreProductEvent.PROCESS_PDF, iTextCoreProductEvent.getEventType());
        // Only first iTextCoreProductEvent is confirmed.
        Assert.assertTrue(events.get(1) instanceof ConfirmEvent);
        ConfirmEvent confirmEvent = (ConfirmEvent) events.get(1);
        Assert.assertEquals(iTextCoreProductEvent, confirmEvent.getConfirmedEvent());
    }

    @Test
    public void signatureValidatorZeroEventsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument.pdf"),
                new DocumentProperties().setEventCountingMetaInfo(new ValidationMetaInfo()))) {
            SignatureValidator validator = builder.buildSignatureValidator();
            validator.setEventCountingMetaInfo(new ValidationMetaInfo());
            validator.validateSignatures(document);
        }

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assert.assertEquals(0, events.size());
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
