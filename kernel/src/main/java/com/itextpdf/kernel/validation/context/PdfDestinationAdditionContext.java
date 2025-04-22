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

    /**
     * Gets {@link PdfAction} instance.
     *
     * @return {@link PdfAction} instance
     */
    public PdfAction getAction() {
        return action;
    }
}
