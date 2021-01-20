/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.actions;

import com.itextpdf.kernel.actions.events.AbstractITextProductEvent;
import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;
import com.itextpdf.kernel.actions.sequence.SequenceId;

import java.util.List;

/**
 * Class is recommended for internal usage. Represents system configuration events.
 */
public abstract class AbstractITextConfigurationEvent implements ITextEvent {

    private static final String INTERNAL_PACKAGE = "com.itextpdf.";
    private static final String ONLY_FOR_INTERNAL_USE = "AbstractTextConfigurationEvent is only for internal usage.";

    /**
     * Creates an instance of configuration event.
     */
    public AbstractITextConfigurationEvent() {
        super();
        // TODO: DEVSIX-4958 if needed we can create some wrapper mechanism to allow creation
        // of ITextConfigurationEvent in Pdf2Data
        if (! this.getClass().getName().startsWith(INTERNAL_PACKAGE)) {
            throw new UnsupportedOperationException(ONLY_FOR_INTERNAL_USE);
        }
    }

    /**
     * Add a new {@link ITextProductEventProcessor} for a product.
     * 
     * @param productName is a name of the product for which the new processor is defined
     * @param processor is a new processor
     * 
     * @return a replaced processor for the product
     */
    protected ITextProductEventProcessor addProcessor(String productName, ITextProductEventProcessor processor) {
        return ProductEventHandler.INSTANCE.addProcessor(productName, processor);
    }

    /**
     * Removes a processor registered for a product.
     * 
     * @param productName is a product for which processor is removed
     * 
     * @return removed processor
     */
    protected ITextProductEventProcessor removeProcessor(String productName) {
        return ProductEventHandler.INSTANCE.removeProcessor(productName);
    }

    /**
     * Gets a processor registered for a product.
     *
     * @param productName is a product for which processor is obtained
     *
     * @return processor for the product
     */
    protected ITextProductEventProcessor getProcessor(String productName) {
        return ProductEventHandler.INSTANCE.getProcessor(productName);
    }

    /**
     * Gets events registered for provided identifier.
     *
     * @param id is the identifier
     *
     * @return the list of event for identifier
     */
    protected List<AbstractITextProductEvent> getEvents(SequenceId id) {
        return ProductEventHandler.INSTANCE.getEvents(id);
    }

    /**
     * Registers a new event for provided identifier.
     *
     * @param id is the identifier
     * @param event is the event to register
     */
    protected void addEvent(SequenceId id, AbstractITextProductEvent event) {
        ProductEventHandler.INSTANCE.addEvent(id, event);
    }
    /**
     * Method defines the logic of action processing.
     */
    protected abstract void doAction();
}
