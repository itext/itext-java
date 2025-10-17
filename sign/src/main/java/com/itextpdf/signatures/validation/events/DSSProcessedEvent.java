package com.itextpdf.signatures.validation.events;

import com.itextpdf.kernel.pdf.PdfDictionary;

/**
 * This event is triggered after the most recent DSS is being read.
 */
public class DSSProcessedEvent implements IValidationEvent{

    private final PdfDictionary dss;

    /**
     * Creates a new instance.
     *
     * @param dss the dss that was read
     */
    public DSSProcessedEvent(PdfDictionary dss) {
        this.dss = dss;
    }

    @Override
    public EventType getEventType() {
        return EventType.DSS_ENTRY_PROCESSED;
    }

    /**
     * Returns the DSS.
     *
     * @return the DSS
     */
    public PdfDictionary getDss() {
        return dss;
    }
}
