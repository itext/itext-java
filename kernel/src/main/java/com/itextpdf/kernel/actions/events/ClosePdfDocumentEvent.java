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
import com.itextpdf.kernel.actions.AbstractITextConfigurationEvent;
import com.itextpdf.kernel.actions.ProductNameConstant;
import com.itextpdf.kernel.actions.processors.ITextProductEventProcessor;
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
 * Class represents events notifying that {@link PdfDocument} was closed.
 */
public final class ClosePdfDocumentEvent extends AbstractITextConfigurationEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClosePdfDocumentEvent.class);

    private static final String CLOSE_TYPE = "close-document-event";
    private final WeakReference<PdfDocument> document;

    /**
     * Creates a new instance of the closing event.
     *
     * @param document is a document to be close
     */
    public ClosePdfDocumentEvent(PdfDocument document) {
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
     * Returns a type of closing event.
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return CLOSE_TYPE;
    }

    /**
     * Prepares document for closing.
     */
    @Override
    protected void doAction() {
        final PdfDocument pdfDocument = (PdfDocument) document.get();
        if (pdfDocument == null) {
            return;
        }
        final List<AbstractITextProductEvent> events = getEvents(pdfDocument.getDocumentIdWrapper());
        final Set<String> products = new HashSet<>();

        if (events != null) {
            for (final AbstractITextProductEvent event : events) {
                products.add(event.getProductName());
            }
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
