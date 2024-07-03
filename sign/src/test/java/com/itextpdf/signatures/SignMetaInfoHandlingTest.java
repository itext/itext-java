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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.actions.AbstractContextBasedEventHandler;
import com.itextpdf.commons.actions.AbstractContextBasedITextEvent;
import com.itextpdf.commons.actions.AbstractITextConfigurationEvent;
import com.itextpdf.commons.actions.AbstractProductProcessITextEvent;
import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.contexts.IContext;
import com.itextpdf.commons.actions.contexts.UnknownContext;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.actions.events.ITextCoreProductEvent;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.client.TestCrlClient;
import com.itextpdf.signatures.testutils.client.TestOcspClient;
import com.itextpdf.signatures.testutils.client.TestTsaClient;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag("IntegrationTest")
public class SignMetaInfoHandlingTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/SignMetaInfoHandlingTest/";
    public static final String CERTS = "./src/test/resources/com/itextpdf/signatures/certs/";
    public static final char[] PASSWORD = "testpassphrase".toCharArray();

    private static final TestConfigurationEvent CONFIGURATION_ACCESS = new TestConfigurationEvent();
    private static StoreEventsHandler handler;

    private static final String srcFile = SOURCE_FOLDER + "helloWorldDoc.pdf";
    private static final String signCertFileName = CERTS + "signCertRsa01.pem";
    private static final String tsaCertFileName = CERTS + "tsCertRsa.pem";
    private static final String caCertFileName = CERTS + "rootRsa.pem";

    private static Certificate[] signRsaChain;
    private static PrivateKey signRsaPrivateKey;
    private static Certificate[] tsaChain;
    private static PrivateKey tsaPrivateKey;
    private static X509Certificate caCert;
    private static PrivateKey caPrivateKey;

    static {
        try {
            signRsaChain = PemFileHelper.readFirstChain(signCertFileName);
            signRsaPrivateKey = PemFileHelper.readFirstKey(signCertFileName, PASSWORD);
            tsaChain = PemFileHelper.readFirstChain(tsaCertFileName);
            tsaPrivateKey = PemFileHelper.readFirstKey(tsaCertFileName, PASSWORD);
            caCert = (X509Certificate) PemFileHelper.readFirstChain(caCertFileName)[0];
            caPrivateKey = PemFileHelper.readFirstKey(caCertFileName, PASSWORD);
        } catch (Exception ignored) {
            // Ignore exception.
        }
    }

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
    public void createDocumentWithSignMetaInfoTest() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SequenceId docSequenceId;
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(out), new DocumentProperties()
                .setEventCountingMetaInfo(new SignMetaInfo()))) {
            docSequenceId = pdfDocument.getDocumentIdWrapper();
        }

        List<AbstractProductProcessITextEvent> confirmedEvents = CONFIGURATION_ACCESS.getPublicEvents(docSequenceId);
        // No confirmed events.
        Assertions.assertEquals(0, confirmedEvents.size());

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assertions.assertEquals(0, events.size());
    }

    @Test
    public void signWithBaselineLTProfileEventHandlingTest() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SignerProperties signerProperties = new SignerProperties();
        PdfPadesSigner padesSigner = new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFile)), out);
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);
        padesSigner.setStampingProperties(new StampingProperties().useAppendMode());
        padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);

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
    public void signWithBaselineLTAProfileEventHandlingTest() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SignerProperties signerProperties = new SignerProperties();
        PdfPadesSigner padesSigner = new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFile)), out);
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient).setTimestampSignatureName("timestampSig1");
        padesSigner.signWithBaselineLTAProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);

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
    public void signCMSContainerWithBaselineLTProfileEventHandlingTest() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PadesTwoPhaseSigningHelper twoPhaseSigningHelper = new PadesTwoPhaseSigningHelper();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        crlClient.addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        ocspClient.addBuilderForCertIssuer(caCert, caPrivateKey);
        twoPhaseSigningHelper.setCrlClient(crlClient).setOcspClient(ocspClient).setTSAClient(testTsa);
        twoPhaseSigningHelper.setStampingProperties(new StampingProperties().useAppendMode());

        try (java.io.ByteArrayOutputStream preparedDoc = new java.io.ByteArrayOutputStream()) {
            CMSContainer container = twoPhaseSigningHelper.createCMSContainerWithoutSignature(signRsaChain,
                    DigestAlgorithms.SHA512, new PdfReader(srcFile), preparedDoc, new SignerProperties());

            IExternalSignature externalSignature = new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA512,
                    BouncyCastleFactoryCreator.getFactory().getProviderName());
            twoPhaseSigningHelper.signCMSContainerWithBaselineLTProfile(externalSignature,
                    new PdfReader(new ByteArrayInputStream(preparedDoc.toByteArray())), out, "Signature1", container);
        }

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assertions.assertEquals(4, events.size());
        Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
        ITextCoreProductEvent iTextCoreProductEvent = (ITextCoreProductEvent) events.get(0);
        Assertions.assertEquals(ITextCoreProductEvent.PROCESS_PDF, iTextCoreProductEvent.getEventType());
        // First iTextCoreProductEvent is confirmed (coming from createCMSContainerWithoutSignature).
        Assertions.assertTrue(events.get(1) instanceof ConfirmEvent);
        ConfirmEvent confirmEvent = (ConfirmEvent) events.get(1);
        Assertions.assertEquals(iTextCoreProductEvent, confirmEvent.getConfirmedEvent());
        Assertions.assertTrue(events.get(2) instanceof ITextCoreProductEvent);
        iTextCoreProductEvent = (ITextCoreProductEvent) events.get(2);
        Assertions.assertEquals(ITextCoreProductEvent.PROCESS_PDF, iTextCoreProductEvent.getEventType());
        // Second iTextCoreProductEvent is confirmed (coming from signCMSContainerWithBaselineLTProfile).
        Assertions.assertTrue(events.get(3) instanceof ConfirmEvent);
        confirmEvent = (ConfirmEvent) events.get(3);
        Assertions.assertEquals(iTextCoreProductEvent, confirmEvent.getConfirmedEvent());
    }

    @Test
    public void signCMSContainerWithBaselineLTAProfileEventHandlingTest() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PadesTwoPhaseSigningHelper twoPhaseSigningHelper = new PadesTwoPhaseSigningHelper();
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        TestCrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        crlClient.addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        ocspClient.addBuilderForCertIssuer(caCert, caPrivateKey);
        twoPhaseSigningHelper.setCrlClient(crlClient).setOcspClient(ocspClient).setTSAClient(testTsa)
                .setTimestampSignatureName("timestampSig1");

        try (java.io.ByteArrayOutputStream preparedDoc = new java.io.ByteArrayOutputStream()) {
            CMSContainer container = twoPhaseSigningHelper.createCMSContainerWithoutSignature(signRsaChain,
                    DigestAlgorithms.SHA512, new PdfReader(srcFile), preparedDoc, new SignerProperties());

            IExternalSignature externalSignature = new PrivateKeySignature(signRsaPrivateKey, DigestAlgorithms.SHA512,
                    BouncyCastleFactoryCreator.getFactory().getProviderName());
            twoPhaseSigningHelper.signCMSContainerWithBaselineLTAProfile(externalSignature,
                    new PdfReader(new ByteArrayInputStream(preparedDoc.toByteArray())), out, "Signature1", container);
        }

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assertions.assertEquals(4, events.size());
        Assertions.assertTrue(events.get(0) instanceof ITextCoreProductEvent);
        ITextCoreProductEvent iTextCoreProductEvent = (ITextCoreProductEvent) events.get(0);
        Assertions.assertEquals(ITextCoreProductEvent.PROCESS_PDF, iTextCoreProductEvent.getEventType());
        // First iTextCoreProductEvent is confirmed (coming from createCMSContainerWithoutSignature).
        Assertions.assertTrue(events.get(1) instanceof ConfirmEvent);
        ConfirmEvent confirmEvent = (ConfirmEvent) events.get(1);
        Assertions.assertEquals(iTextCoreProductEvent, confirmEvent.getConfirmedEvent());
        Assertions.assertTrue(events.get(2) instanceof ITextCoreProductEvent);
        iTextCoreProductEvent = (ITextCoreProductEvent) events.get(2);
        Assertions.assertEquals(ITextCoreProductEvent.PROCESS_PDF, iTextCoreProductEvent.getEventType());
        // Second iTextCoreProductEvent is confirmed (coming from signCMSContainerWithBaselineLTAProfile).
        Assertions.assertTrue(events.get(3) instanceof ConfirmEvent);
        confirmEvent = (ConfirmEvent) events.get(3);
        Assertions.assertEquals(iTextCoreProductEvent, confirmEvent.getConfirmedEvent());
    }

    @Test
    public void passSignMetaInfoThroughStampingPropertiesTest() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        SignerProperties signerProperties = new SignerProperties();
        PdfPadesSigner padesSigner = new PdfPadesSigner(new PdfReader(FileUtil.getInputStreamForFile(srcFile)), out);
        TestTsaClient testTsa = new TestTsaClient(Arrays.asList(tsaChain), tsaPrivateKey);
        ICrlClient crlClient = new TestCrlClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        TestOcspClient ocspClient = new TestOcspClient().addBuilderForCertIssuer(caCert, caPrivateKey);
        padesSigner.setOcspClient(ocspClient).setCrlClient(crlClient);
        padesSigner.setStampingProperties((StampingProperties) new StampingProperties().useAppendMode()
                .setEventCountingMetaInfo(new SignMetaInfo()));
        padesSigner.signWithBaselineLTProfile(signerProperties, signRsaChain, signRsaPrivateKey, testTsa);

        List<AbstractContextBasedITextEvent> events = handler.getEvents();
        Assertions.assertEquals(0, events.size());
    }

    private static class TestConfigurationEvent extends AbstractITextConfigurationEvent {
        @Override
        protected void doAction() {
            throw new IllegalStateException();
        }

        public List<AbstractProductProcessITextEvent> getPublicEvents(SequenceId sequenceId) {
            return super.getEvents(sequenceId);
        }
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
