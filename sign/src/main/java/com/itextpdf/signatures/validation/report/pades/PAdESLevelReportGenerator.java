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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.validation.events.AbstractCertificateChainEvent;
import com.itextpdf.signatures.validation.events.AlgorithmUsageEvent;
import com.itextpdf.signatures.validation.events.IValidationEvent;
import com.itextpdf.signatures.validation.events.ProofOfExistenceFoundEvent;
import com.itextpdf.signatures.validation.events.StartSignatureValidationEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class generates a PAdES level report for a document based upon the
 * IValidation events triggered during validation.
 */
public class PAdESLevelReportGenerator implements IEventHandler {

    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final Map<String, AbstractPadesLevelRequirements> signatureInfos =
            new HashMap<String, AbstractPadesLevelRequirements>();

    private int timestampCount = 0;
    private boolean PoeFound;
    private boolean DssFound;
    private boolean DssHasPoe;
    private String currentSignature;

    private final List<PAdESLevelReport> timestampReports = new ArrayList<PAdESLevelReport>();

    /**
     * Creates a new instance.
     */
    public PAdESLevelReportGenerator() {
        // Declaring default constructor explicitly to avoid removing it unintentionally
    }

    /**
     * Executes the rules and created the individual signature reports.
     *
     * @return the DocumentPAdESLevelReport
     */
    public DocumentPAdESLevelReport getReport() {
        DocumentPAdESLevelReport result = new DocumentPAdESLevelReport();
        for (Entry<String, AbstractPadesLevelRequirements> entry : signatureInfos.entrySet()) {
            if (entry.getValue() instanceof SignatureRequirements) {
                PAdESLevelReport report = new PAdESLevelReport(entry.getKey(), entry.getValue(), timestampReports);
                result.addPAdESReport(report);
            } else {
                timestampReports.add(new PAdESLevelReport(entry.getKey(), entry.getValue(), timestampReports));
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(IEvent rawEvent) {
        if (rawEvent instanceof IValidationEvent) {
            IValidationEvent event = (IValidationEvent) rawEvent;
            switch (event.getEventType()) {
                case PROOF_OF_EXISTENCE_FOUND:
                    processPoE((ProofOfExistenceFoundEvent) event);
                    break;
                case SIGNATURE_VALIDATION_STARTED:
                    processSignature((StartSignatureValidationEvent) event);
                    break;
                case SIGNATURE_VALIDATION_FAILURE:
                    if (currentSignature != null) {
                        signatureInfos.get(currentSignature).setValidationSucceeded(false);
                    }
                    break;
                case SIGNATURE_VALIDATION_SUCCESS:
                    processSignatureSuccess();
                    break;
                case CERTIFICATE_ISSUER_EXTERNAL_RETRIEVAL:
                case CERTIFICATE_ISSUER_OTHER_INTERNAL_SOURCE_USED:
                    processIssuerRetrieval((AbstractCertificateChainEvent) event);
                    break;
                case CRL_OTHER_INTERNAL_SOURCE_USED:
                case OCSP_OTHER_INTERNAL_SOURCE_USED:
                case CRL_REQUEST:
                case OCSP_REQUEST:
                    processsRevocationRetreival((AbstractCertificateChainEvent) event);
                    break;
                case DSS_ENTRY_PROCESSED:
                    DssFound = true;
                    DssHasPoe = PoeFound;
                    break;
                case ALGORITHM_USAGE:
                    AlgorithmUsageEvent ae = (AlgorithmUsageEvent) rawEvent;
                    if (!ae.isAllowedAccordingToEtsiTs119_312()) {
                        this.signatureInfos.get(currentSignature).addAlgorithmUsage(ae.toString());
                    }
                    break;
            }
        }
    }

    private void processsRevocationRetreival(AbstractCertificateChainEvent event) {
        if (currentSignature != null) {
            AbstractCertificateChainEvent retrieval =
                    event;
            signatureInfos.get(currentSignature).addRevocationDataNotInDSS(
                    retrieval.getCertificate());
        }
    }

    private void processIssuerRetrieval(AbstractCertificateChainEvent event) {
        if (currentSignature != null) {
            AbstractCertificateChainEvent retrieval =
                    event;
            signatureInfos.get(currentSignature).addCertificateIssuerNotInDSS(
                    retrieval.getCertificate());
        }
    }

    private void processSignatureSuccess() {
        if (currentSignature != null) {
            if (this.signatureInfos.get(currentSignature) instanceof DocumentTimestampRequirements) {
                PoeFound = true;
            }
            signatureInfos.get(currentSignature).setValidationSucceeded(true);
        }
        currentSignature = null;
    }

    private void processSignature(StartSignatureValidationEvent event) {
        StartSignatureValidationEvent start = event;
        SignatureRequirements sReqs = new SignatureRequirements();

        getDictionaryInfo(start.getPdfSignature(), sReqs);
        getCmsInfo(start.getPdfSignature(), sReqs);
        signatureInfos.put(start.getSignatureName(), sReqs);
        currentSignature = start.getSignatureName();
    }

    private void processPoE(ProofOfExistenceFoundEvent event) {
        timestampCount++;
        ProofOfExistenceFoundEvent poe = event;
        if (poe.isDocumentTimestamp()) {
            DocumentTimestampRequirements tsReqs = new DocumentTimestampRequirements(timestampCount == 0);
            getDictionaryInfo(poe.getPdfSignature(), tsReqs);
            getCmsInfo(poe.getPdfSignature(), tsReqs);
            currentSignature = "timestamp" + timestampCount;
            signatureInfos.put(currentSignature, tsReqs);
        }
    }

    private void getDictionaryInfo(PdfSignature sig, AbstractPadesLevelRequirements reqs) {
        PdfDictionary sigObj = sig.getPdfObject();
        //Table 1 row 12
        if (sigObj.containsKey(PdfName.M)) {
            reqs.setDictionaryEntryMPresent(true);
            //Table 1 row 12 remark g
            PdfString dateAsString = sigObj.getAsString(PdfName.M);
            reqs.setDictionaryEntryMHasCorrectFormat(
                    PdfDate.decode(dateAsString.getValue()) != null);
        }
        //Table 1 row 14
        reqs.setDictionaryEntryContentsPresent(sigObj.containsKey(PdfName.Contents));
        //Table 1 row 15
        reqs.setDictionaryEntryFilterPresent(sigObj.containsKey(PdfName.Filter));
        //Table 1 row 15 notes h and i are covered by validation
        //Table 1 row 16
        reqs.setDictionaryEntryByteRangePresent(sigObj.containsKey(PdfName.ByteRange));
        //Table 1 row 16 note k is covered by validation
        //Table 1 row 17
        reqs.setDictionaryEntrySubFilterPresent(sigObj.containsKey(PdfName.SubFilter));
        //Table 1 row 17 note l
        reqs.setSignatureDictionaryEntrySubFilterValueIsETSICadesDetached(sigObj.containsKey(PdfName.SubFilter) &&
                sigObj.getAsName(PdfName.SubFilter).equals(PdfName.ETSI_CAdES_DETACHED));
        //Table 1 row 19
        reqs.setDictionaryEntryReasonPresent(sigObj.containsKey(PdfName.Reason));
        //Table 1 row 22
        reqs.setDictionaryEntryCertPresent(sigObj.containsKey(PdfName.Cert));

        reqs.setTimestampDictionaryEntrySubFilterValueEtsiRfc3161(sigObj.containsKey(PdfName.SubFilter) &&
                sigObj.getAsName(PdfName.SubFilter).equals(PdfName.ETSI_RFC3161));

        reqs.setDocumentTimestampPresent(PoeFound);
    }

    private void getCmsInfo(PdfSignature sig, AbstractPadesLevelRequirements reqs) {
        try {
            CMSContainer cms = new CMSContainer(sig.getContents().getValueBytes());

            //Table 1 row 1
            reqs.setSignedDataCertificatesPresent(!cms.getCertificates().isEmpty());
            //Table 1 row 1 note a
            reqs.setSignatureCertificatesContainsSigningCertificate(
                    cms.getCertificates().stream().anyMatch(c -> c.equals(
                            cms.getSignerInfo().getSigningCertificate())));
            //Table 1 row 1 note b is covered by the issuer retrieval events
            //Table 1 row 2 note c availability is covered by validation
            reqs.setContentTypeValueIsIdData(
                    cms.getSignerInfo().getSignedAttributes().stream().anyMatch(
                            a -> OID.CONTENT_TYPE.equals(a.getType())
                                    && OID.ID_DATA.equals(BC_FACTORY.createASN1Set(a.getValue())
                                    .getObjectAt(0).toString())));
            //Table 1 row 3
            reqs.setMessageDigestPresent(
                    cms.getSignerInfo().getSignedAttributes().stream().anyMatch(
                            a -> OID.MESSAGE_DIGEST.equals(a.getType())));
            //Table 1 row 7
            reqs.setCommitmentTypeIndicationPresent(cms.getSignerInfo().getSignedAttributes()
                    .stream().anyMatch(
                            a -> OID.AA_ETS_COMMITMENTTYPE.equals(a.getType())));

            //Table 1 row 9
            reqs.setEssSigningCertificateV1Present(
                    cms.getSignerInfo().getSignedAttributes().stream().anyMatch(
                            a -> OID.AA_SIGNING_CERTIFICATE_V1.equals(a.getType())
                    ));

            //Table 1 row 10
            reqs.setEssSigningCertificateV2Present(
                    cms.getSignerInfo().getSignedAttributes().stream().anyMatch(
                            a -> OID.AA_SIGNING_CERTIFICATE_V2.equals(a.getType())
                    ));
            //Table 1 row 13
            reqs.setCmsSigningTimeAttributePresent(
                    cms.getSignerInfo().getSignedAttributes().stream().anyMatch(
                            a -> OID.SIGNING_TIME.equals(a.getType())));
            //Table 1 row 24
            reqs.setPoeSignaturePresent(
                    PoeFound ||
                            cms.getSignerInfo().getUnSignedAttributes().stream().anyMatch(
                                    a -> OID.AA_TIME_STAMP_TOKEN.equals(a.getType())));

            reqs.setDSSPresent(DssFound);
            reqs.setPoeDssPresent(DssHasPoe);

        } catch (Exception e) {
            // do nothing
            // cms entries cannot be read but that is no reason to stop here
        }
    }
}