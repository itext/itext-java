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
import com.itextpdf.kernel.actions.events.ITextProductEventWrapper;
import com.itextpdf.kernel.actions.processors.DefaultITextProductEventProcessor;
import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;
import com.itextpdf.kernel.actions.sequence.SequenceId;
import com.itextpdf.kernel.counter.context.UnknownContext;
import com.itextpdf.kernel.actions.events.AbstractITextProductEvent;
import com.itextpdf.kernel.actions.exceptions.UnknownProductException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles events based oh their origin.
 */
final class ProductEventHandler extends AbstractContextBasedEventHandler {
    static final ProductEventHandler INSTANCE = new ProductEventHandler();

    private static final Set<String> PRODUCTS_NAMESPACES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    ProductNameConstant.ITEXT_CORE,
                    ProductNameConstant.PDF_HTML,
                    ProductNameConstant.PDF_SWEEP,
                    ProductNameConstant.PDF_OCR,
                    ProductNameConstant.PDF_OCR_TESSERACT4
            )));

    private final ConcurrentHashMap<String, ITextProductEventProcessor> processors = new ConcurrentHashMap<>();
    private final WeakHashMap<SequenceId, List<ITextProductEventWrapper>> events = new WeakHashMap<>();

    private ProductEventHandler() {
        super(UnknownContext.PERMISSIVE);
    }

    /**
     * Pass the event to the appropriate {@link ITextProductEventProcessor}.
     *
     * @param event to handle
     */
    @Override
    protected void onAcceptedEvent(ITextEvent event) {
        if (event instanceof AbstractITextProductEvent) {
            final AbstractITextProductEvent iTextEvent = (AbstractITextProductEvent) event;
            final ITextProductEventProcessor productEventProcessor =
                    findProcessorForProduct(iTextEvent.getProductName());

            if (iTextEvent.getSequenceId() != null) {
                synchronized (events) {
                    final SequenceId id = iTextEvent.getSequenceId();

                    if (!events.containsKey(id)) {
                        events.put(id, new ArrayList<>());
                    }

                    // TODO DEVSIX-5053 if event reporting will be done on document closing then
                    //  event wrapping should be done there
                    events.get(id).add(new ITextProductEventWrapper(iTextEvent,
                            productEventProcessor.getUsageType(),
                            productEventProcessor.getProducer()));
                }
            }

            productEventProcessor.onEvent(iTextEvent);
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

    List<ITextProductEventWrapper> getEvents(SequenceId id) {
        synchronized (events) {
            final List<ITextProductEventWrapper> listOfEvents = events.get(id);
            if (listOfEvents == null) {
                return Collections.<ITextProductEventWrapper>emptyList();
            }
            return Collections.<ITextProductEventWrapper>unmodifiableList(new ArrayList<>(listOfEvents));
        }
    }

    void addEvent(SequenceId id, ITextProductEventWrapper event) {
        synchronized (events) {
            List<ITextProductEventWrapper> listOfEvents = events.get(id);

            if (listOfEvents == null) {
                listOfEvents = new ArrayList<>();
                events.put(id, listOfEvents);
            }

            listOfEvents.add(event);
        }
    }

    private ITextProductEventProcessor findProcessorForProduct(String productName) {
        ITextProductEventProcessor processor = processors.get(productName);

        if (processor != null) {
            return processor;
        }

        if (PRODUCTS_NAMESPACES.contains(productName)) {
            processor = new DefaultITextProductEventProcessor(productName);
            processors.put(productName, processor);
            return processor;
        } else {
            throw new UnknownProductException(
                    MessageFormatUtil.format(UnknownProductException.UNKNOWN_PRODUCT, productName));
        }
    }
}
