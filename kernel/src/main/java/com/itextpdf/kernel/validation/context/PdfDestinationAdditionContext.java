package com.itextpdf.kernel.validation.context;

import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.validation.IValidationContext;
import com.itextpdf.kernel.validation.ValidationType;

/**
 * Class which contains context in which destination was added.
 */
public class PdfDestinationAdditionContext implements IValidationContext {
    private final PdfDestination destination;
    private final PdfAction action;

    /**
     * Creates {@link PdfDestinationAdditionContext} instance.
     *
     * @param destination {@link PdfDestination} instance which was added
     */
    public PdfDestinationAdditionContext(PdfDestination destination) {
        this.destination = destination;
        this.action = null;
    }

    /**
     * Creates {@link PdfDestinationAdditionContext} instance.
     *
     * @param destinationObject {@link PdfObject} which represents destination
     */
    public PdfDestinationAdditionContext(PdfObject destinationObject) {
        // Second check is needed in case of destination page being partially flushed.
        if (destinationObject != null && !destinationObject.isFlushed() &&
                (!(destinationObject instanceof PdfArray) || !((PdfArray) destinationObject).get(0).isFlushed())) {
            this.destination = PdfDestination.makeDestination(destinationObject, false);
        } else {
            this.destination = null;
        }
        this.action = null;
    }

    public PdfDestinationAdditionContext(PdfAction action) {
        this.destination = null;
        this.action = action;
    }

    @Override
    public ValidationType getType() {
        return ValidationType.DESTINATION_ADDITION;
    }

    /**
     * Gets {@link PdfDestination} instance.
     *
     * @return {@link PdfDestination} instance
     */
    public PdfDestination getDestination() {
        return destination;
    }

    public PdfAction getAction() {
        return action;
    }
}
