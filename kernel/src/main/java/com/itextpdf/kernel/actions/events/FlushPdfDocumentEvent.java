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
package com.itextpdf.kernel.actions.events;

import com.itextpdf.events.confirmations.ConfirmEvent;
import com.itextpdf.events.confirmations.ConfirmedEventWrapper;
import com.itextpdf.events.confirmations.EventConfirmationType;
import com.itextpdf.events.sequence.SequenceId;
import com.itextpdf.events.utils.MessageFormatUtil;
import com.itextpdf.events.AbstractITextConfigurationEvent;
import com.itextpdf.events.AbstractProductProcessITextEvent;
import com.itextpdf.events.EventManager;
import com.itextpdf.events.processors.ITextProductEventProcessor;
import com.itextpdf.events.producer.ProducerBuilder;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class represents events notifying that {@link PdfDocument} was flushed.
 */
public final class FlushPdfDocumentEvent extends AbstractITextConfigurationEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlushPdfDocumentEvent.class);

    private final WeakReference<PdfDocument> document;

    /**
     * Creates a new instance of the flushing event.
     *
     * @param document is a document to be flushed
     */
    public FlushPdfDocumentEvent(PdfDocument document) {
        super();
        this.document = new WeakReference<>(document);
    }

    /**
     * Prepares document for flushing.
     */
    @Override
    protected void doAction() {
        final PdfDocument pdfDocument = (PdfDocument) document.get();
        if (pdfDocument == null) {
            return;
        }
        List<AbstractProductProcessITextEvent> events = getEvents(pdfDocument.getDocumentIdWrapper());
        final Set<String> products = new HashSet<>();

        if (events == null || events.isEmpty()) {
            return;
        }

        for (final AbstractProductProcessITextEvent event : events) {
            if (event.getConfirmationType() == EventConfirmationType.ON_CLOSE) {
                EventManager.getInstance().onEvent(new ConfirmEvent(pdfDocument.getDocumentIdWrapper(), event));
            }
            products.add(event.getProductName());
        }

        for (final String product: products) {
            final ITextProductEventProcessor processor = getActiveProcessor(product);
            if (processor == null && LOGGER.isWarnEnabled()) {
                LOGGER.warn(MessageFormatUtil.format(KernelLogMessageConstant.UNKNOWN_PRODUCT_INVOLVED, product));
            }
        }

        final String oldProducer = pdfDocument.getDocumentInfo().getProducer();
        final String newProducer = ProducerBuilder.modifyProducer(getConfirmedEvents(pdfDocument.getDocumentIdWrapper()), oldProducer);
        pdfDocument.getDocumentInfo().setProducer(newProducer);
    }

    private List<ConfirmedEventWrapper> getConfirmedEvents(SequenceId sequenceId) {
        final List<AbstractProductProcessITextEvent> events = getEvents(sequenceId);
        final List<ConfirmedEventWrapper> confirmedEvents = new ArrayList<>();
        for (AbstractProductProcessITextEvent event : events) {
            if (event instanceof ConfirmedEventWrapper) {
                confirmedEvents.add((ConfirmedEventWrapper) event);
            } else {
                LOGGER.warn(MessageFormatUtil.format(KernelLogMessageConstant.UNCONFIRMED_EVENT,
                        event.getProductName(), event.getEventType()));
            }
        }
        return confirmedEvents;
    }
}
