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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.KernelLogMessageConstant;
import com.itextpdf.kernel.Toggle;
import com.itextpdf.kernel.actions.AbstractITextConfigurationEvent;
import com.itextpdf.kernel.actions.ProductNameConstant;
import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;
import com.itextpdf.kernel.actions.producer.ProducerBuilder;
import com.itextpdf.kernel.actions.session.ClosingSession;
import com.itextpdf.kernel.pdf.PdfDocument;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class represents events notifying that {@link PdfDocument} was flushed.
 */
public final class FlushPdfDocumentEvent extends AbstractITextConfigurationEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlushPdfDocumentEvent.class);

    private static final String FLUSH_DOCUMENT_TYPE = "flush-document-event";
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
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getProductName() {
        return ProductNameConstant.ITEXT_CORE;
    }

    /**
     * Returns a type of flushing event.
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return FLUSH_DOCUMENT_TYPE;
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
        final List<ITextProductEventWrapper> events = getEvents(pdfDocument.getDocumentIdWrapper());
        final Set<String> products = new HashSet<>();

        if (events == null || events.isEmpty()) {
            return;
        }

        for (final ITextProductEventWrapper event : events) {
            products.add(event.getEvent().getProductName());
        }

        final Map<String, ITextProductEventProcessor> knownProducts = new HashMap<>();
        for (final String product: products) {
            final ITextProductEventProcessor processor = getProcessor(product);
            if (processor == null) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn(MessageFormatUtil.format(KernelLogMessageConstant.UNKNOWN_PRODUCT_INVOLVED, product));
                }
            } else {
                knownProducts.put(product, processor);
            }
        }

        //TODO DEVSIX-5323 remove the toggle when all tests are ready
        if (Toggle.NEW_PRODUCER_LINE) {
            final String oldProducer = pdfDocument.getDocumentInfo().getProducer();
            final String newProducer = ProducerBuilder.modifyProducer(events, oldProducer);
            pdfDocument.getDocumentInfo().setProducer(newProducer);
        }

        final ClosingSession session = new ClosingSession((PdfDocument) document.get());
        for (final Map.Entry<String, ITextProductEventProcessor> product: knownProducts.entrySet()) {
            product.getValue().aggregationOnClose(session);
        }

        // do not join these loops into one as order of processing is important!

        for (final Map.Entry<String, ITextProductEventProcessor> product: knownProducts.entrySet()) {
            product.getValue().completionOnClose(session);
        }
    }
}
