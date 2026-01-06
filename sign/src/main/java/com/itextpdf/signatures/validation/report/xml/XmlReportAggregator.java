/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.validation.events.IValidationEvent;
import com.itextpdf.signatures.validation.events.ProofOfExistenceFoundEvent;
import com.itextpdf.signatures.validation.events.SignatureValidationFailureEvent;
import com.itextpdf.signatures.validation.events.StartSignatureValidationEvent;
import com.itextpdf.signatures.validation.report.xml.SignatureValidationStatus.MainIndication;
import com.itextpdf.signatures.validation.report.xml.SignatureValidationStatus.MessageType;

import java.util.Date;
import java.util.Stack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use this implementation when an xml report has to be created.
 */
public class XmlReportAggregator implements IEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlReportAggregator.class);

    private final ValidationObjects validationObjects = new ValidationObjects();
    private final PadesValidationReport report = new PadesValidationReport(validationObjects);
    private final Stack<SubValidationReport> validationReportStack = new Stack<>();

    /**
     * Instantiates a new AdESReportEventListener instance.
     */
    public XmlReportAggregator() {
        // Declaring default constructor explicitly to avoid removing it unintentionally
    }

    /**
     * Returns the generated PadesValidationReport.
     *
     * @return the generated PadesValidationReport
     */
    public PadesValidationReport getReport() {
        return report;
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
                        ProofOfExistenceFoundEvent poe = (ProofOfExistenceFoundEvent) event;
                        this.proofOfExistenceFound(poe.getTimeStampSignature(),
                                poe.isDocumentTimestamp());
                        break;
                    case SIGNATURE_VALIDATION_STARTED:
                        StartSignatureValidationEvent start = (StartSignatureValidationEvent) event;
                        this.startSignatureValidation(
                                start.getPdfSignature().getContents().getValueBytes(),
                                start.getSignatureName(),
                                start.getSigningDate());
                        break;
                    case SIGNATURE_VALIDATION_SUCCESS:
                        this.reportSignatureValidationSuccess();
                        break;
                    case SIGNATURE_VALIDATION_FAILURE:
                        SignatureValidationFailureEvent failure =
                                (SignatureValidationFailureEvent) event;
                        this.reportSignatureValidationFailure(failure.isInconclusive(),
                                failure.getReason());
                        break;
                    case CERTIFICATE_CHAIN_SUCCESS:
                        break;
                    case CERTIFICATE_CHAIN_FAILURE:
                        break;
                    case CRL_VALIDATION_SUCCESS:
                        break;
                    case CRL_VALIDATION_FAILURE:
                        break;
                    case OCSP_VALIDATION_SUCCESS:
                        break;
                    case OCSP_VALIDATION_FAILURE:
                        break;
                }
            }
        }
    }

    private void startSignatureValidation(byte[] signature, String name, Date signingDate) {
        try {
            SignatureValidationReport currentSignatureValidationReport = new SignatureValidationReport(
                    validationObjects, new CMSContainer(signature), name, signingDate);
            validationReportStack.push(currentSignatureValidationReport);
            report.addSignatureValidationReport(currentSignatureValidationReport);
        } catch (Exception e) { // catching generic Exception here for portability
            LOGGER.error("Unable to parse signature container.", e);
            throw new IllegalArgumentException("Signature is not parsable", e);
        }
    }

    private void proofOfExistenceFound(byte[] timeStampSignature, boolean document) {
        try {
            POEValidationReport currentValidationReport = new POEValidationReport(
                    validationObjects, new CMSContainer(timeStampSignature), document);
            validationReportStack.push(currentValidationReport);
            validationObjects.addObject(currentValidationReport);
        } catch (Exception e) { // catching generic Exception here for portability
            LOGGER.error("Unable to parse timestamp signature container.", e);
            throw new IllegalArgumentException("Timestamp signature is not parsable", e);
        }
    }

    private void reportSignatureValidationSuccess() {
        SignatureValidationStatus status = new SignatureValidationStatus();
        status.setMainIndication(MainIndication.TOTAL_PASSED);
        SubValidationReport currentValidationReport = validationReportStack.pop();
        currentValidationReport.setSignatureValidationStatus(status);
    }

    private void reportSignatureValidationFailure(boolean isInconclusive, String reason) {
        SignatureValidationStatus status = new SignatureValidationStatus();
        if (isInconclusive) {
            status.setMainIndication(MainIndication.INDETERMINATE);
        } else {
            status.setMainIndication(MainIndication.TOTAL_FAILED);
        }
        status.addMessage(reason, MessageType.ERROR);
        SubValidationReport currentValidationReport = validationReportStack.pop();
        currentValidationReport.setSignatureValidationStatus(status);

    }
}
