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
import java.util.Collections;
import java.util.List;

/**
 * This class generates a PAdES level report for a document based upon the
 * IValidation events triggered during validation.
 */
public class PAdESLevelReportGenerator implements IEventHandler {
    private static final IBouncyCastleFactory BC_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final List<AbstractPadesLevelRequirements> signatureInfos = new ArrayList<>();

    private int timestampCount = 0;
    private boolean poeFound;
    private boolean dssFound;
    private boolean dssHasPoe;
    private AbstractPadesLevelRequirements currentSignature;
    private final List<PAdESLevelReport> timestampReports = new ArrayList<>();

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
        for (AbstractPadesLevelRequirements entry : signatureInfos) {
            if (entry instanceof SignatureRequirements) {
                PAdESLevelReport report = new PAdESLevelReport(entry, timestampReports);
                result.addPAdESReport(report);
            } else {
                timestampReports.add(new PAdESLevelReport(entry, Collections.<PAdESLevelReport>emptyList()));
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
            if (event.getEventType() != null) {
                switch (event.getEventType()) {
                    case PROOF_OF_EXISTENCE_FOUND:
                        processPoE((ProofOfExistenceFoundEvent) event);
                        break;
                    case SIGNATURE_VALIDATION_STARTED:
                        processSignature((StartSignatureValidationEvent) event);
                        break;
                    case SIGNATURE_VALIDATION_FAILURE:
                        if (currentSignature != null) {
                            currentSignature.setValidationSucceeded(false);
                        }
                        break;
                    case SIGNATURE_VALIDATION_SUCCESS:
                        processSignatureSuccess();
                        break;
                    case CERTIFICATE_ISSUER_NOT_FROM_DOCUMENT:
                        processIssuerCertMissing((AbstractCertificateChainEvent) event);
                        break;
                    case CERTIFICATE_ISSUER_NOT_FROM_DSS:
                        processIssuerNotInDss((AbstractCertificateChainEvent) event);
                        break;
                    case REVOCATION_NOT_FROM_DSS:
                        processRevocationNotInDss((AbstractCertificateChainEvent) event);
                        break;
                    case DSS_ENTRY_PROCESSED:
                        dssFound = true;
                        dssHasPoe = poeFound;
                        break;
                    case DSS_NOT_TIMESTAMPED:
                            processNotTimestampedRevocation((AbstractCertificateChainEvent) event);
                        break;
                    case ALGORITHM_USAGE:
                        AlgorithmUsageEvent ae = (AlgorithmUsageEvent) rawEvent;
                        if (currentSignature != null && !ae.isAllowedAccordingToEtsiTs119_312()) {
                            if (!ae.isAllowedAccordingToAdES()) {
                                currentSignature.addForbiddenAlgorithmUsage(ae.toString());
                            } else if (!ae.isAllowedAccordingToEtsiTs119_312()) {
                                currentSignature.addDiscouragedAlgorithmUsage(ae.toString());
                            }
                        }
                        break;
                }
            }
        }
    }

    private void processRevocationNotInDss(AbstractCertificateChainEvent event) {
        if (currentSignature != null) {
            currentSignature.addRevocationDataNotInDSS(event.getCertificate());
        }
    }

    private void processNotTimestampedRevocation(AbstractCertificateChainEvent event) {
        if (currentSignature != null) {
            currentSignature.addRevocationDataNotTimestamped(event.getCertificate());
        }
    }

    private void processIssuerNotInDss(AbstractCertificateChainEvent event) {
        if (currentSignature != null) {
            currentSignature.addCertificateIssuerNotInDSS(
                    event.getCertificate());
        }
    }

    private void processIssuerCertMissing(AbstractCertificateChainEvent event) {
        if (currentSignature != null) {
            currentSignature.addCertificateIssuerMissing(
                    event.getCertificate());
        }
    }

    private void processSignatureSuccess() {
        if (currentSignature != null) {
            if (currentSignature instanceof DocumentTimestampRequirements) {
                poeFound = true;
            }
            currentSignature.setValidationSucceeded(true);
        }
        currentSignature = null;
    }

    private void processSignature(StartSignatureValidationEvent event) {
        SignatureRequirements sReqs = new SignatureRequirements(event.getSignatureName());

        getDictionaryInfo(event.getPdfSignature(), sReqs);
        getCmsInfo(event.getPdfSignature(), sReqs);
        signatureInfos.add(sReqs);
        currentSignature = sReqs;
    }

    private void processPoE(ProofOfExistenceFoundEvent poe) {
        timestampCount++;
        if (poe.isDocumentTimestamp()) {
            DocumentTimestampRequirements tsReqs = new DocumentTimestampRequirements(
                    "timestamp" + timestampCount, this.dssFound);
            getDictionaryInfo(poe.getPdfSignature(), tsReqs);
            getCmsInfo(poe.getPdfSignature(), tsReqs);
            currentSignature = tsReqs;
            signatureInfos.add(tsReqs);
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

        reqs.setDocumentTimestampPresent(poeFound);
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
                    poeFound ||
                            cms.getSignerInfo().getUnSignedAttributes().stream().anyMatch(
                                    a -> OID.AA_TIME_STAMP_TOKEN.equals(a.getType())));

            reqs.setDSSPresent(dssFound);
            reqs.setPoeDssPresent(dssHasPoe);

        } catch (Exception e) {
            // do nothing
            // cms entries cannot be read but that is no reason to stop here
        }
    }
}