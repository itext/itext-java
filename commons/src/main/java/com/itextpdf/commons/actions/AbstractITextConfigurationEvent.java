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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.processors.DefaultITextProductEventProcessor;
import com.itextpdf.commons.actions.processors.ITextProductEventProcessor;
import com.itextpdf.commons.actions.sequence.SequenceId;

import java.util.List;
import java.util.Map;

/**
 * Abstract class which represents system configuration events. Only for internal usage.
 */
public abstract class AbstractITextConfigurationEvent extends AbstractITextEvent {
    /**
     * Adds a new {@link ITextProductEventProcessor} for a product.
     *
     * @param processor is a new processor
     * 
     * @return a replaced processor for the product
     */
    protected ITextProductEventProcessor addProcessor(ITextProductEventProcessor processor) {
        return ProductEventHandler.INSTANCE.addProcessor(processor);
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
     * <p>
     * If processor isn't registered and product supports AGPL mode
     * {@link DefaultITextProductEventProcessor} will be obtained otherwise null will be returned.
     *
     * @param productName is a product for which processor is obtained
     *
     * @return processor for the product
     */
    protected ITextProductEventProcessor getActiveProcessor(String productName) {
        return ProductEventHandler.INSTANCE.getActiveProcessor(productName);
    }

    /**
     * Gets an unmodifiable map of registered processors.
     *
     * @return all processors
     */
    protected Map<String, ITextProductEventProcessor> getProcessors() {
        return ProductEventHandler.INSTANCE.getProcessors();
    }

    /**
     * Gets events registered for provided identifier.
     *
     * @param id is the identifier
     *
     * @return the list of event for identifier
     */
    protected List<AbstractProductProcessITextEvent> getEvents(SequenceId id) {
        return ProductEventHandler.INSTANCE.getEvents(id);
    }

    /**
     * Registers a new event for provided identifier.
     *
     * @param id is the identifier
     * @param event is the event to register
     */
    protected void addEvent(SequenceId id, AbstractProductProcessITextEvent event) {
        ProductEventHandler.INSTANCE.addEvent(id, event);
    }

    /**
     * Registers internal namespace.
     *
     * @param namespace is the namespace to register
     */
    protected void registerInternalNamespace(String namespace) {
        AbstractITextEvent.registerNamespace(namespace);
    }

    /**
     * Method defines the logic of action processing.
     */
    protected abstract void doAction();
}
