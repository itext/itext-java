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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.KernelLogMessageConstant;
import com.itextpdf.kernel.actions.events.ConfirmEvent;
import com.itextpdf.kernel.actions.events.ConfirmedEventWrapper;
import com.itextpdf.kernel.actions.processors.DefaultITextProductEventProcessor;
import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.counter.context.UnknownContext;
import com.itextpdf.kernel.actions.exceptions.UnknownProductException;

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
        if (! (event instanceof AbstractProductProcessITextEvent)) {
            return;
        }
        final AbstractProductProcessITextEvent productEvent = (AbstractProductProcessITextEvent) event;
        final ITextProductEventProcessor productEventProcessor = findProcessorForProduct(productEvent.getProductName());
        productEventProcessor.onEvent(productEvent);
        if (productEvent.getSequenceId() != null) {
            if (productEvent instanceof ConfirmEvent) {
                wrapConfirmedEvent((ConfirmEvent) productEvent, productEventProcessor);
            } else {
                addEvent(productEvent.getSequenceId(), productEvent);
            }
        }
    }

    ITextProductEventProcessor addProcessor(ITextProductEventProcessor processor) {
        return processors.put(processor.getProductName(), processor);
    }

    ITextProductEventProcessor removeProcessor(String productName) {
        return processors.remove(productName);
    }

    ITextProductEventProcessor getProcessor(String productName) {
        return processors.get(productName);
    }

    Map<String, ITextProductEventProcessor> getProcessors() {
        return Collections.unmodifiableMap(new HashMap<>(processors));
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
                LOGGER.warn(MessageFormatUtil.format(KernelLogMessageConstant.UNREPORTED_EVENT,
                        confirmedEvent.getProductName(), confirmedEvent.getEventType()));
            }
        }
    }

    private ITextProductEventProcessor findProcessorForProduct(String productName) {
        ITextProductEventProcessor processor = processors.get(productName);

        if (processor != null) {
            return processor;
        }

        if (ProductNameConstant.PRODUCT_NAMES.contains(productName)) {
            processor = new DefaultITextProductEventProcessor(productName);
            processors.put(productName, processor);
            return processor;
        } else {
            throw new UnknownProductException(
                    MessageFormatUtil.format(UnknownProductException.UNKNOWN_PRODUCT, productName));
        }
    }
}
