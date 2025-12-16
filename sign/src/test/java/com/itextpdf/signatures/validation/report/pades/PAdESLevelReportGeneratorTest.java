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
package com.itextpdf.signatures.validation.report.pades;

import com.itextpdf.commons.actions.EventManager;
import com.itextpdf.commons.utils.EncodingUtil;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.cms.CMSTestHelper;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.testutils.X509MockCertificate;
import com.itextpdf.signatures.validation.SignatureValidator;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.events.AlgorithmUsageEvent;
import com.itextpdf.signatures.validation.events.CertificateIssuerExternalRetrievalEvent;
import com.itextpdf.signatures.validation.events.CertificateIssuerRetrievedOutsideDSSEvent;
import com.itextpdf.signatures.validation.events.DSSProcessedEvent;
import com.itextpdf.signatures.validation.events.DssNotTimestampedEvent;
import com.itextpdf.signatures.validation.events.IValidationEvent;
import com.itextpdf.signatures.validation.events.ProofOfExistenceFoundEvent;
import com.itextpdf.signatures.validation.events.RevocationNotFromDssEvent;
import com.itextpdf.signatures.validation.events.SignatureValidationFailureEvent;
import com.itextpdf.signatures.validation.events.SignatureValidationSuccessEvent;
import com.itextpdf.signatures.validation.events.StartSignatureValidationEvent;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class PAdESLevelReportGeneratorTest extends ExtendedITextTest {
    private static final String certsSrc = "./src/test/resources/com/itextpdf/signatures/certs/";

    private PAdESLevelReportGenerator sut;
    private EventManager eventManager;

    @BeforeEach
    public void setUp() {
        ValidatorChainBuilder builder = new ValidatorChainBuilder();
        sut = new PAdESLevelReportGenerator();

        builder.withPAdESLevelReportGenerator(sut);
        eventManager = builder.getEventManager();
    }

    @Test
    public void testB_BHappyPath() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_B_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_B, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_B, report.getDocumentLevel());
    }

    @Test
    public void testB_THappyPath() throws CertificateException, IOException {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_T_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        Certificate[] chain = PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem");
        event = new RevocationNotFromDssEvent((X509Certificate) chain[0]);
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_T, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
    }

    @Test
    public void testB_LTHappyPath() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.L_T_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LT, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LT, report.getDocumentLevel());
    }

    @Test
    public void testB_LTAHappyPath() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void testB_LTADSSMissingPath() throws CertificateException, IOException {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        Certificate[] chain = PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem");
        event = new RevocationNotFromDssEvent((X509Certificate) chain[0]);
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_T, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_LT).stream()
                .anyMatch(nc -> nc.contains(
                        AbstractPadesLevelRequirements.REVOCATION_DATA_FOR_THESE_CERTIFICATES_IS_MISSING)));
    }

    @Test
    public void testCMSContainsSigningDate() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.WITH_CMS_SIGNING_TIME_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties()
                .get(PAdESLevel.B_B).stream()
                .anyMatch(
                        nc ->
                                AbstractPadesLevelRequirements.CLAIMED_TIME_OF_SIGNING_SHALL_NOT_BE_INCLUDED_IN_THE_CMS
                                        .equals(nc)));
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
    }

    @Test
    public void testSingleSignatureMissingCMSCerts() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(CMSTestHelper.SERIALIZED_B64_MISSING_CERTIFICATES));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(nc -> AbstractPadesLevelRequirements.SIGNED_DATA_CERTIFICATES_MUST_BE_INCLUDED
                        .equals(nc)));
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
    }

    @Test
    public void testCMSContainsMissingContentType() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.CMS_MISSING_CONTENT_TYPE_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(nc -> AbstractPadesLevelRequirements.CMS_CONTENT_TYPE_MUST_BE_ID_DATA.equals(nc)));
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
    }

    @Test
    public void testCMSContainsWrongContentType() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.WRONG_CONTENT_TYPE));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(nc -> AbstractPadesLevelRequirements.CMS_CONTENT_TYPE_MUST_BE_ID_DATA.equals(nc)));
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
    }

    @Test
    public void testCMSMissingMessageDigest() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.MISSING_MESSAGE_DIGEST));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(nc -> AbstractPadesLevelRequirements.CMS_MESSAGE_DIGEST_IS_MISSING.equals(nc)));
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
    }

    @Test
    public void testCMSCommitmentTypeAndDictReasonArePresent() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.CONTAINING_COMMITMENT_INDICATION));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        signatureDict.put(PdfName.Reason, new PdfString("Reason"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(
                        nc -> AbstractPadesLevelRequirements.COMMITMENT_TYPE_AND_REASON_SHALL_NOT_BE_USED_TOGETHER
                                .equals(nc)));
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
    }

    @Test
    public void testOnlyDictReasonIsPresent() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        signatureDict.put(PdfName.Reason, new PdfString("Reason"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void testCMSCommitmentTypeIsPresent() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.CONTAINING_COMMITMENT_INDICATION));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void testCMSMissingSigningCertificate() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.MISSING_SIGNER_CERT_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(
                        nc ->
                                AbstractPadesLevelRequirements.SIGNED_DATA_CERTIFICATES_MUST_INCLUDE_SIGNING_CERTIFICATE
                                .equals(nc)));
    }

    @Test
    public void testMissingClaimedTimeOfSinging() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(nc -> AbstractPadesLevelRequirements.DICTIONARY_ENTRY_M_IS_MISSING.equals(nc)));
    }

    @Test
    public void testCMSContainsSigningTime() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.WITH_SIGNING_TIME_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));

        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(
                        nc ->
                                AbstractPadesLevelRequirements.CLAIMED_TIME_OF_SIGNING_SHALL_NOT_BE_INCLUDED_IN_THE_CMS
                                        .equals(nc)));
    }

    @Test
    public void testDictionaryContainsSignerCert() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));

        signatureDict.put(PdfName.Cert, new PdfString(""));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B)
                .stream()
                .anyMatch(nc -> AbstractPadesLevelRequirements.CERT_ENTRY_IS_ADDED_TO_THE_SIGNATURE_DICTIONARY
                        .equals(nc)));
    }

    @Test
    public void testB_TTrustedTimeIsMissing() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.NO_TIMESTAMP_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));

        PdfSignature sig = new PdfSignature(signatureDict);

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_B, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_T)
                .stream()
                .anyMatch(
                        nc -> AbstractPadesLevelRequirements.THERE_MUST_BE_A_SIGNATURE_OR_DOCUMENT_TIMESTAMP_AVAILABLE
                                .equals(nc)));

    }

    @Test
    public void testCMSB_TPoEFromSignature() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));

        PdfSignature sig = new PdfSignature(signatureDict);

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LT, report.getDocumentLevel());
    }

    @Test
    public void testCMSPoEFromDocTimeStamp() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.NO_TIMESTAMP_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));

        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void testInvalidSignature() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_B_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationFailureEvent(true, "test");
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.INDETERMINATE, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.INDETERMINATE, report.getDocumentLevel());
    }

    @Test
    public void testB_LTA_DSSMissing_Certs() throws CertificateException, IOException {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        Certificate[] chain = PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem");
        event = new CertificateIssuerExternalRetrievalEvent((X509Certificate) chain[0]);
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_T, report.getSignatureReport("test").getLevel());
        Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_LT).stream()
                .anyMatch(nc -> nc.contains(AbstractPadesLevelRequirements.ISSUER_FOR_THESE_CERTIFICATES_IS_MISSING)));
    }

    @Test
    public void testB_LTA_DSSMissing_CertsTest2() throws CertificateException, IOException {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        Certificate[] chain = PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem");
        event = new CertificateIssuerRetrievedOutsideDSSEvent((X509Certificate) chain[0]);
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getWarnings().get(PAdESLevel.B_LT)
                .stream()
                .anyMatch(nc ->
                        nc.contains(AbstractPadesLevelRequirements.ISSUER_FOR_THESE_CERTIFICATES_IS_NOT_IN_DSS)));
    }

    @Test
    public void testB_DSSMissingRevData() throws CertificateException, IOException {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        Certificate[] chain = PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem");
        event = new RevocationNotFromDssEvent((X509Certificate) chain[0]);
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_T, report.getSignatureReport("test").getLevel());
        Assertions.assertEquals(PAdESLevel.B_T, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_LT).stream()
                .anyMatch(nc -> nc.contains(
                        AbstractPadesLevelRequirements.REVOCATION_DATA_FOR_THESE_CERTIFICATES_IS_MISSING)));
    }

    @Test
    public void testB_DSSMissingTimestampedRevData() throws CertificateException, IOException {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        Certificate[] chain = PemFileHelper.readFirstChain(certsSrc + "signCertRsa01.pem");
        event = new DssNotTimestampedEvent((X509Certificate) chain[0]);
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LT, report.getSignatureReport("test").getLevel());
        Assertions.assertEquals(PAdESLevel.B_LT, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_LTA).stream()
                .anyMatch(nc -> nc.contains(
                        AbstractPadesLevelRequirements.REVOCATION_DATA_FOR_THESE_CERTIFICATES_NOT_TIMESTAMPED)));
    }

    @Test
    public void testInvalidAlgorithmUsed() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_B_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        eventManager.onEvent(new AlgorithmUsageEvent("MD5", "1.2.840.113549.2.5", "HASH x"));
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.NONE, report.getSignatureReport("test").getLevel());
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B).stream()
                .anyMatch(nc -> nc.contains(
                        AbstractPadesLevelRequirements.A_FORBIDDEN_HASH_OR_SIGNING_ALGORITHM_WAS_USED)
                        && nc.contains("1.2.840.113549.2.5")));
    }

    @Test
    public void testTimestampWrongSubFilter() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        timestampDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LT, report.getSignatureReport("test").getLevel());
        Assertions.assertEquals(PAdESLevel.B_LT, report.getDocumentLevel());

        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_LTA).stream()
                .anyMatch(nc -> nc.contains(DocumentTimestampRequirements.SUBFILTER_NOT_ETSI_RFC3161)));

    }

    @Test
    public void testAlgorithmReportingPositive() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);

        eventManager.onEvent(new AlgorithmUsageEvent("SHA-512", OID.SHA_512,
                SignatureValidator.VALIDATING_SIGNATURE_NAME));

        eventManager.onEvent(new AlgorithmUsageEvent("SHA-256", OID.SHA_256,
                SignatureValidator.VALIDATING_SIGNATURE_NAME));

        eventManager.onEvent(new AlgorithmUsageEvent("RSA", OID.RSA,
                SignatureValidator.VALIDATING_SIGNATURE_NAME));

        eventManager.onEvent(new AlgorithmUsageEvent("ECDSA", OID.ECDSA,
                SignatureValidator.VALIDATING_SIGNATURE_NAME));

        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
        Assertions.assertFalse(report.getSignatureReport("test").getWarnings().get(PAdESLevel.B_B).
                stream().anyMatch(m ->
                        m.contains(AbstractPadesLevelRequirements.A_DISCOURAGED_HASH_OR_SIGNING_ALGORITHM_WAS_USED)));
    }

    @Test
    public void testAlgorithmReportingDiscouraged() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);

        eventManager.onEvent(new
                AlgorithmUsageEvent("SHA-1", "1.3.14.3.2.26", SignatureValidator.VALIDATING_SIGNATURE_NAME));

        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
        Assertions.assertTrue(report.getSignatureReport("test").getWarnings().get(PAdESLevel.B_B).
                stream().anyMatch(m ->
                        m.contains(AbstractPadesLevelRequirements.A_DISCOURAGED_HASH_OR_SIGNING_ALGORITHM_WAS_USED)
                                && m.contains("SHA-1")));
    }

    @Test
    public void testAlgorithmReportingForbidden() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        eventManager.onEvent(new AlgorithmUsageEvent("MD5", OID.MD5, SignatureValidator.VALIDATING_SIGNATURE_NAME));
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.NONE, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.NONE, report.getDocumentLevel());

        Assertions.assertTrue(report.getSignatureReport("test").getNonConformaties().get(PAdESLevel.B_B).
                stream().anyMatch(m ->
                        m.contains(AbstractPadesLevelRequirements.A_FORBIDDEN_HASH_OR_SIGNING_ALGORITHM_WAS_USED)
                        && m.contains(OID.MD5)));
    }

    @Test
    public void signatureValidationSuccessEventMisfiresTest() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));

        eventManager.onEvent(new SignatureValidationSuccessEvent());
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void signatureValidationFailureEventMisfiresTest() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));

        eventManager.onEvent(new SignatureValidationFailureEvent(true, "test"));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void certificateIssuerRetrievedOutsideDSSEventMisfiresTest() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));

        eventManager.onEvent(new CertificateIssuerRetrievedOutsideDSSEvent(new X509MockCertificate()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void certificateIssuerExternalRetrievalEventMisfiresTest() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));

        eventManager.onEvent(new CertificateIssuerExternalRetrievalEvent(new X509MockCertificate()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void revocationNotFromDssEventMisfiresTest() {
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_LTA_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);

        contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.LTA_1_TS_B64));
        PdfSignature timestampDict = getTimestampPdfDictionary(contents);
        eventManager.onEvent(new ProofOfExistenceFoundEvent(timestampDict, "timestampSig1"));
        eventManager.onEvent(new SignatureValidationSuccessEvent());
        eventManager.onEvent(new DSSProcessedEvent(new PdfDictionary()));

        eventManager.onEvent(new RevocationNotFromDssEvent(new X509MockCertificate()));
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);

        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_LTA, report.getDocumentLevel());
    }

    @Test
    public void algorithmUsageEventMisfiresTest() {
        eventManager.onEvent(new AlgorithmUsageEvent("MD5", OID.MD5, "Test"));
        PdfDictionary signatureDict = new PdfDictionary();
        PdfString contents = new PdfString(EncodingUtil.fromBase64(PAdESLevelHelper.B_B_1_B64));
        contents.setHexWriting(true);
        signatureDict.put(PdfName.Contents, contents);
        signatureDict.put(PdfName.Filter, PdfName.Sig);
        signatureDict.put(PdfName.SubFilter, PdfName.ETSI_CAdES_DETACHED);
        signatureDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        signatureDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        PdfSignature sig = new PdfSignature(signatureDict);
        IValidationEvent event = new StartSignatureValidationEvent(sig, "test", new Date());
        eventManager.onEvent(event);
        event = new SignatureValidationSuccessEvent();
        eventManager.onEvent(event);
        DocumentPAdESLevelReport report = sut.getReport();
        System.out.println(report);
        Assertions.assertEquals(PAdESLevel.B_B, report.getSignatureReport("test")
                .getLevel());
        Assertions.assertEquals(PAdESLevel.B_B, report.getDocumentLevel());
    }
    
    private static PdfSignature getTimestampPdfDictionary(PdfString contents) {
        PdfDictionary timestampDict = new PdfDictionary();
        timestampDict.put(PdfName.Contents, contents);
        timestampDict.put(PdfName.Filter, PdfName.Sig);
        timestampDict.put(PdfName.SubFilter, PdfName.ETSI_RFC3161);
        timestampDict.put(PdfName.ByteRange, new PdfString("1 2 3 4"));
        timestampDict.put(PdfName.M, new PdfString("D:20231204144752+01'00'"));
        return new PdfSignature(timestampDict);
    }
}