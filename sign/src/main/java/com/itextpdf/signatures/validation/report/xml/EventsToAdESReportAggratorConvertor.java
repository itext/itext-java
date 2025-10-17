package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.commons.actions.IEvent;
import com.itextpdf.commons.actions.IEventHandler;
import com.itextpdf.signatures.validation.events.IValidationEvent;
import com.itextpdf.signatures.validation.events.ProofOfExistenceFoundEvent;
import com.itextpdf.signatures.validation.events.SignatureValidationFailureEvent;
import com.itextpdf.signatures.validation.events.StartSignatureValidationEvent;

/**
 * This class is for internal usage.
 *
 * It bridges the gap between the new event driven system of collecting validation meta info
 * and the previous interface driven system.
 */
public class EventsToAdESReportAggratorConvertor implements IEventHandler {

    private final AdESReportAggregator target;

    /**
     * Creates a new instance of the convertor, wrapping an AdESReportAggregator implementation.
     *
     * @param target an AdESReportAggregator implementation to be wrapped
     */
    public EventsToAdESReportAggratorConvertor(AdESReportAggregator target) {
        this.target = target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(IEvent rawEvent) {
        if (rawEvent instanceof IValidationEvent) {
            IValidationEvent event = (IValidationEvent) rawEvent;
            switch ( (event.getEventType())) {
                case SIGNATURE_VALIDATION_STARTED:
                    StartSignatureValidationEvent startEvent = (StartSignatureValidationEvent) event;
                    target.startSignatureValidation(
                            startEvent.getPdfSignature().getContents().getValueBytes(),
                            startEvent.getSignatureName(),
                            startEvent.getSigningDate());
                    break;
                case PROOF_OF_EXISTENCE_FOUND:
                    ProofOfExistenceFoundEvent peoEvent = (ProofOfExistenceFoundEvent) event;
                    target.proofOfExistenceFound(peoEvent.getTimeStampSignature(),
                            peoEvent.isDocumentTimestamp());
                    break;
                case SIGNATURE_VALIDATION_SUCCESS:
                    target.reportSignatureValidationSuccess();
                    break;
                case SIGNATURE_VALIDATION_FAILURE:
                    SignatureValidationFailureEvent failureEvent =
                            (SignatureValidationFailureEvent) event;
                    target.reportSignatureValidationFailure(failureEvent.isInconclusive(),
                            failureEvent.getReason());
                    break;
            }
        }
    }
}
