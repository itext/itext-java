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

import com.itextpdf.commons.actions.confirmations.ConfirmEvent;
import com.itextpdf.commons.actions.confirmations.ConfirmedEventWrapper;
import com.itextpdf.commons.actions.contexts.UnknownContext;
import com.itextpdf.commons.actions.processors.ITextProductEventProcessor;
import com.itextpdf.commons.actions.sequence.SequenceId;
import com.itextpdf.commons.exceptions.ProductEventHandlerRepeatException;
import com.itextpdf.commons.exceptions.UnknownProductException;
import com.itextpdf.commons.logs.CommonsLogMessageConstant;
import com.itextpdf.commons.utils.MessageFormatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles events based oh their origin.
 */
final class ProductEventHandler extends AbstractContextBasedEventHandler {
    static final ProductEventHandler INSTANCE = new ProductEventHandler();

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventHandler.class);
    // The constant has the following value for two reasons. First, to avoid the infinite loop.
    // Second, to retry event processing several times for technical reasons.
    private static final int MAX_EVENT_RETRY_COUNT = 4;

    private final ConcurrentHashMap<String, ITextProductEventProcessor> processors = new ConcurrentHashMap<>();
    private final WeakHashMap<SequenceId, List<AbstractProductProcessITextEvent>> events = new WeakHashMap<>();

    private ProductEventHandler() {
        super(UnknownContext.PERMISSIVE);
    }

    /**
     * Pass the event to the appropriate {@link ITextProductEventProcessor}.
     *
     * @param event to handle
     */
    @Override
    protected void onAcceptedEvent(AbstractContextBasedITextEvent event) {
        for (int i = 0; i < MAX_EVENT_RETRY_COUNT; i++) {
            try {
                tryProcessEvent(event);
                // process succeeded
                return;
            } catch (ProductEventHandlerRepeatException repeatException) {
                // ignore this exception to retry the processing
            }
        }
        // the final processing retry
        tryProcessEvent(event);
    }

    ITextProductEventProcessor addProcessor(ITextProductEventProcessor processor) {
        return processors.put(processor.getProductName(), processor);
    }

    ITextProductEventProcessor removeProcessor(String productName) {
        return processors.remove(productName);
    }

    ITextProductEventProcessor getActiveProcessor(String productName) {
        ITextProductEventProcessor processor = processors.get(productName);

        if (processor != null) {
            return processor;
        }

        if (ProductNameConstant.PRODUCT_NAMES.contains(productName)) {
            processor = ProductProcessorFactoryKeeper.getProductProcessorFactory().createProcessor(productName);
            processors.put(productName, processor);
            return processor;
        } else {
            return null;
        }
    }

    Map<String, ITextProductEventProcessor> getProcessors() {
        return Collections.unmodifiableMap(new HashMap<>(processors));
    }

    void clearProcessors() {
        processors.clear();
    }

    List<AbstractProductProcessITextEvent> getEvents(SequenceId id) {
        synchronized (events) {
            final List<AbstractProductProcessITextEvent> listOfEvents = events.get(id);
            if (listOfEvents == null) {
                return Collections.<AbstractProductProcessITextEvent>emptyList();
            }
            return Collections.<AbstractProductProcessITextEvent>unmodifiableList(new ArrayList<>(listOfEvents));
        }
    }

    void addEvent(SequenceId id, AbstractProductProcessITextEvent event) {
        synchronized (events) {
            List<AbstractProductProcessITextEvent> listOfEvents = events.get(id);

            if (listOfEvents == null) {
                listOfEvents = new ArrayList<>();
                events.put(id, listOfEvents);
            }

            listOfEvents.add(event);
        }
    }

    private void tryProcessEvent(AbstractContextBasedITextEvent event) {
        if (! (event instanceof AbstractProductProcessITextEvent)) {
            return;
        }
        final AbstractProductProcessITextEvent productEvent = (AbstractProductProcessITextEvent) event;
        final String productName = productEvent.getProductName();
        final ITextProductEventProcessor productEventProcessor = getActiveProcessor(productName);
        if (productEventProcessor == null) {
            throw new UnknownProductException(
                    MessageFormatUtil.format(UnknownProductException.UNKNOWN_PRODUCT, productName));
        }

        productEventProcessor.onEvent(productEvent);

        if (productEvent.getSequenceId() != null) {
            if (productEvent instanceof ConfirmEvent) {
                wrapConfirmedEvent((ConfirmEvent) productEvent, productEventProcessor);
            } else {
                addEvent(productEvent.getSequenceId(), productEvent);
            }
        }
    }

    private void wrapConfirmedEvent(ConfirmEvent event, ITextProductEventProcessor productEventProcessor) {
        synchronized (events) {
            final List<AbstractProductProcessITextEvent> eventsList = events.get(event.getSequenceId());

            final AbstractProductProcessITextEvent confirmedEvent = event.getConfirmedEvent();
            final int indexOfReportedEvent = eventsList.indexOf(confirmedEvent);
            if (indexOfReportedEvent >= 0) {
                eventsList.set(indexOfReportedEvent, new ConfirmedEventWrapper(confirmedEvent,
                        productEventProcessor.getUsageType(),
                        productEventProcessor.getProducer()));
            } else {
                LOGGER.warn(MessageFormatUtil.format(CommonsLogMessageConstant.UNREPORTED_EVENT,
                        confirmedEvent.getProductName(), confirmedEvent.getEventType()));
            }
        }
    }
}
